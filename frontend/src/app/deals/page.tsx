"use client";

import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import Header from "@/components/layout/Header";
import { dealApi } from "@/lib/deal-api";
import { Deal } from "@/lib/types";

function formatPrice(price: number) {
  return price.toLocaleString("ko-KR") + "원";
}

function DealCard({ deal, onBuy }: { deal: Deal; onBuy: (id: number) => void }) {
  const now = new Date();
  const open = new Date(deal.openTime);
  const end = new Date(deal.endTime);
  const isOpen = now >= open && now <= end && deal.stock > 0;

  return (
    <Card className="flex flex-col">
      <CardHeader>
        <div className="flex items-start justify-between gap-2">
          <CardTitle className="text-lg">{deal.productName}</CardTitle>
          <Badge variant={isOpen ? "default" : "secondary"}>
            {deal.stock === 0 ? "품절" : isOpen ? "진행중" : "종료"}
          </Badge>
        </div>
      </CardHeader>
      <CardContent className="flex-1 space-y-2">
        <p className="text-2xl font-bold text-blue-600">{formatPrice(deal.dealPrice)}</p>
        <p className="text-sm text-gray-500">남은 수량: {deal.stock}개</p>
        <p className="text-xs text-gray-400">
          {open.toLocaleString("ko-KR")} ~ {end.toLocaleString("ko-KR")}
        </p>
      </CardContent>
      <CardFooter>
        <Button
          className="w-full"
          disabled={!isOpen}
          onClick={() => onBuy(deal.id)}
        >
          {deal.stock === 0 ? "품절" : isOpen ? "구매하기" : "종료된 딜"}
        </Button>
      </CardFooter>
    </Card>
  );
}

export default function DealsPage() {
  const [deals, setDeals] = useState<Deal[]>([]);
  const [loading, setLoading] = useState(true);
  const [buying, setBuying] = useState<number | null>(null);

  useEffect(() => {
    dealApi
      .getDeals()
      .then((res) => setDeals(res.data))
      .catch(() => {
        toast.error("딜 목록을 불러오지 못했습니다.");
      })
      .finally(() => setLoading(false));
  }, []);

  const handleBuy = async (dealId: number) => {
    setBuying(dealId);
    try {
      const res = await dealApi.purchase(dealId);
      toast.success(`구매 완료! 남은 수량: ${res.data.remainingStock}개`);
      setDeals((prev) =>
        prev.map((d) =>
          d.id === dealId ? { ...d, stock: res.data.remainingStock } : d
        )
      );
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "구매에 실패했습니다.");
    } finally {
      setBuying(null);
    }
  };

  return (
    <div>
      <Header />
      <div className="max-w-5xl mx-auto px-4 py-8">
      {/* 딜 목록 */}
      {loading ? (
        <div className="text-center py-20 text-gray-400">불러오는 중...</div>
      ) : deals.length === 0 ? (
        <div className="text-center py-20 text-gray-400">진행 중인 딜이 없습니다.</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {deals.map((deal) => (
            <DealCard
              key={deal.id}
              deal={deal}
              onBuy={(id) => {
                if (buying === null) handleBuy(id);
              }}
            />
          ))}
        </div>
      )}
      </div>
    </div>
  );
}
