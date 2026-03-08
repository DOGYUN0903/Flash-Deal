"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { toast } from "sonner";
import { Clock, Zap } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import Header from "@/components/layout/Header";
import { dealApi } from "@/lib/deal-api";
import { DealResponse } from "@/lib/types";

function getDiscountRate(original: number, discount: number) {
  return Math.round(((original - discount) / original) * 100);
}

function Countdown({ endAt }: { endAt: string }) {
  const [remaining, setRemaining] = useState("");

  useEffect(() => {
    const calc = () => {
      const diff = new Date(endAt).getTime() - Date.now();
      if (diff <= 0) { setRemaining("종료됨"); return; }
      const h = Math.floor(diff / 3600000);
      const m = Math.floor((diff % 3600000) / 60000);
      const s = Math.floor((diff % 60000) / 1000);
      setRemaining(`${h > 0 ? `${h}시간 ` : ""}${m}분 ${s}초`);
    };
    calc();
    const id = setInterval(calc, 1000);
    return () => clearInterval(id);
  }, [endAt]);

  return (
    <span className="flex items-center gap-1 font-mono font-bold text-orange-600">
      <Clock size={16} />
      {remaining}
    </span>
  );
}

export default function DealDetailPage() {
  const params = useParams();
  const router = useRouter();
  const dealId = Number(params.id);

  const [deal, setDeal] = useState<DealResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [ordering, setOrdering] = useState(false);
  const [quantity, setQuantity] = useState(1);

  useEffect(() => {
    dealApi
      .getDeal(dealId)
      .then((res) => setDeal(res.data))
      .catch(() => {
        toast.error("딜 정보를 불러오지 못했습니다.");
        router.push("/deals");
      })
      .finally(() => setLoading(false));
  }, [dealId, router]);

  const handleOrder = async () => {
    if (!deal) return;
    setOrdering(true);
    try {
      const amount = deal.discountPrice * quantity;
      const res = await dealApi.createDealOrder(dealId, { amount, quantity });
      toast.success("딜 주문이 완료되었습니다!");
      router.push(`/orders/${res.data.orderId}`);
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "딜 주문에 실패했습니다.");
      setOrdering(false);
    }
  };

  if (loading) {
    return (
      <div>
        <Header />
        <div className="flex justify-center items-center py-40 text-gray-400">불러오는 중...</div>
      </div>
    );
  }

  if (!deal) return null;

  const discountRate = getDiscountRate(deal.originalPrice, deal.discountPrice);
  const isActive = deal.status === "ACTIVE";
  const totalPrice = deal.discountPrice * quantity;

  return (
    <div>
      <Header />
      <div className="max-w-xl mx-auto px-4 py-8">
        <div className="flex items-center gap-2 mb-6">
          <Zap size={20} className="text-yellow-500 fill-yellow-400" />
          <h1 className="text-xl font-bold">플래시 딜</h1>
        </div>

        <Card className="mb-4">
          <CardContent className="pt-6 space-y-4">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-xs text-gray-500 mb-1">{deal.productName}</p>
                <h2 className="text-lg font-bold">{deal.title}</h2>
              </div>
              {deal.status === "ACTIVE" && <Badge className="bg-green-500 hover:bg-green-600 shrink-0">진행중</Badge>}
              {deal.status === "SCHEDULED" && <Badge variant="secondary" className="shrink-0">오픈 예정</Badge>}
              {deal.status === "ENDED" && <Badge variant="outline" className="text-gray-400 shrink-0">종료</Badge>}
            </div>

            <div className="flex items-baseline gap-3">
              <span className="text-3xl font-bold text-blue-600">
                {deal.discountPrice.toLocaleString("ko-KR")}원
              </span>
              <span className="text-base text-gray-400 line-through">
                {deal.originalPrice.toLocaleString("ko-KR")}원
              </span>
              {discountRate > 0 && (
                <span className="text-base font-bold text-red-500">{discountRate}% OFF</span>
              )}
            </div>

            <div className="flex items-center justify-between text-sm text-gray-600">
              <span>잔여 재고 <span className="font-semibold text-gray-800">{deal.remainingStock}개</span></span>
              {isActive && <Countdown endAt={deal.endAt} />}
              {deal.status === "SCHEDULED" && (
                <span>{new Date(deal.startAt).toLocaleString("ko-KR")} 오픈</span>
              )}
            </div>

            {isActive && deal.remainingStock > 0 && (
              <div className="flex items-center gap-3 pt-2 border-t">
                <span className="text-sm font-medium">수량</span>
                <div className="flex items-center gap-2">
                  <button
                    className="w-8 h-8 rounded border flex items-center justify-center text-lg font-bold hover:bg-gray-50 disabled:opacity-40"
                    onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                    disabled={quantity <= 1}
                  >
                    −
                  </button>
                  <span className="w-8 text-center font-semibold">{quantity}</span>
                  <button
                    className="w-8 h-8 rounded border flex items-center justify-center text-lg font-bold hover:bg-gray-50 disabled:opacity-40"
                    onClick={() => setQuantity((q) => Math.min(deal.remainingStock, q + 1))}
                    disabled={quantity >= deal.remainingStock}
                  >
                    +
                  </button>
                </div>
              </div>
            )}
          </CardContent>
        </Card>

        {isActive && deal.remainingStock > 0 ? (
          <Button
            className="w-full"
            size="lg"
            onClick={handleOrder}
            disabled={ordering}
          >
            {ordering ? "주문 처리 중..." : `${totalPrice.toLocaleString("ko-KR")}원 지금 구매`}
          </Button>
        ) : (
          <Button className="w-full" size="lg" disabled>
            {deal.status === "ENDED" || deal.remainingStock === 0 ? "종료된 딜입니다" : "아직 시작 전입니다"}
          </Button>
        )}
      </div>
    </div>
  );
}
