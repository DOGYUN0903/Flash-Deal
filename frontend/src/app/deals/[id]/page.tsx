"use client";

import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import { toast } from "sonner";
import { ArrowLeft, Zap } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import Header from "@/components/layout/Header";
import CategoryTab from "@/components/layout/CategoryTab";
import { dealApi } from "@/lib/deal-api";
import { DealDetail } from "@/lib/types";

function getDealStatus(deal: DealDetail) {
  const now = new Date();
  const open = new Date(deal.openTime);
  const end = new Date(deal.endTime);
  if (deal.stock === 0) return { label: "품절", variant: "destructive" as const, isOpen: false };
  if (now < open) return { label: "오픈 예정", variant: "secondary" as const, isOpen: false };
  if (now > end) return { label: "종료", variant: "secondary" as const, isOpen: false };
  return { label: "진행중", variant: "default" as const, isOpen: true };
}

export default function DealDetailPage() {
  const router = useRouter();
  const { id } = useParams();
  const [deal, setDeal] = useState<DealDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [buying, setBuying] = useState(false);

  useEffect(() => {
    dealApi
      .getDeal(Number(id))
      .then((res) => setDeal(res.data))
      .catch(() => {
        toast.error("딜 정보를 불러오지 못했습니다.");
        router.push("/deals");
      })
      .finally(() => setLoading(false));
  }, [id, router]);

  const handleBuy = async () => {
    setBuying(true);
    try {
      const res = await dealApi.purchase(Number(id));
      router.push(`/payment?orderId=${res.data.orderId}`);
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "구매에 실패했습니다.");
      setBuying(false);
    }
  };

  if (loading) {
    return (
      <div>
        <Header />
        <CategoryTab />
        <div className="flex justify-center items-center py-40 text-gray-400">
          불러오는 중...
        </div>
      </div>
    );
  }

  if (!deal) return null;

  const status = getDealStatus(deal);
  const discountRate = Math.round((1 - deal.dealPrice / deal.originalPrice) * 100);

  return (
    <div>
      <Header />
      <CategoryTab />
      <div className="max-w-6xl mx-auto px-4 py-8">
        {/* 뒤로가기 */}
        <button
          onClick={() => router.back()}
          className="flex items-center gap-1 text-sm text-gray-500 hover:text-gray-800 mb-6"
        >
          <ArrowLeft size={16} />
          딜 목록으로
        </button>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-10">
          {/* 좌측: 이미지 placeholder */}
          <div className="bg-gray-100 rounded-xl aspect-square flex items-center justify-center text-gray-400 text-sm">
            상품 이미지
          </div>

          {/* 우측: 딜 정보 */}
          <div className="flex flex-col gap-4">
            <div className="flex items-start justify-between gap-3">
              <h1 className="text-2xl font-bold">{deal.productName}</h1>
              <Badge variant={status.variant}>{status.label}</Badge>
            </div>

            {/* 가격 */}
            <div className="flex flex-col gap-1">
              <p className="text-sm text-gray-400 line-through">
                정가 {deal.originalPrice.toLocaleString("ko-KR")}원
              </p>
              <div className="flex items-center gap-2">
                <span className="text-red-500 font-bold text-xl">{discountRate}%</span>
                <span className="text-3xl font-bold text-blue-600">
                  {deal.dealPrice.toLocaleString("ko-KR")}원
                </span>
              </div>
            </div>

            {/* 재고 & 기간 */}
            <Card>
              <CardContent className="pt-4 space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">남은 수량</span>
                  <span className="font-medium">{deal.stock}개</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">딜 기간</span>
                  <span className="font-medium text-right">
                    {new Date(deal.openTime).toLocaleString("ko-KR")}
                    <br />~ {new Date(deal.endTime).toLocaleString("ko-KR")}
                  </span>
                </div>
              </CardContent>
            </Card>

            {/* 상품 설명 */}
            {deal.productDescription && (
              <Card>
                <CardContent className="pt-4">
                  <p className="text-sm text-gray-600 leading-relaxed whitespace-pre-line">
                    {deal.productDescription}
                  </p>
                </CardContent>
              </Card>
            )}

            {/* 구매 버튼 */}
            <div className="mt-4">
              <Button
                size="lg"
                className="w-full gap-2"
                disabled={!status.isOpen || buying}
                onClick={handleBuy}
              >
                <Zap size={18} />
                {buying ? "구매 중..." : deal.stock === 0 ? "품절" : !status.isOpen ? "구매 불가" : "지금 구매하기"}
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
