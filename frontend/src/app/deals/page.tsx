"use client";

import { useEffect, useState } from "react";
import { toast } from "sonner";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import Header from "@/components/layout/Header";
import CategoryTab from "@/components/layout/CategoryTab";
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
      <Link href={`/deals/${deal.id}`}>
        <CardHeader className="hover:bg-gray-50 transition-colors rounded-t-xl cursor-pointer">
          <div className="flex items-start justify-between gap-2">
            <CardTitle className="text-lg">{deal.productName}</CardTitle>
            <Badge variant={isOpen ? "default" : "secondary"}>
              {deal.stock === 0 ? "품절" : isOpen ? "진행중" : "종료"}
            </Badge>
          </div>
        </CardHeader>
        <CardContent className="flex-1 space-y-2 hover:bg-gray-50 transition-colors cursor-pointer">
          <p className="text-2xl font-bold text-blue-600">{formatPrice(deal.dealPrice)}</p>
          <p className="text-sm text-gray-500">남은 수량: {deal.stock}개</p>
          <p className="text-xs text-gray-400">
            {open.toLocaleString("ko-KR")} ~ {end.toLocaleString("ko-KR")}
          </p>
        </CardContent>
      </Link>
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
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    setLoading(true);
    dealApi
      .getDeals(page)
      .then((res) => {
        setDeals(res.data.data);
        setTotalPages(res.data.totalPages);
      })
      .catch(() => {
        toast.error("딜 목록을 불러오지 못했습니다.");
      })
      .finally(() => setLoading(false));
  }, [page]);

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
      <CategoryTab />
      <div className="max-w-6xl mx-auto px-4 py-8">
        <h2 className="text-2xl font-bold mb-6">⚡ 플래시 딜</h2>
      {/* 딜 목록 */}
      {loading ? (
        <div className="text-center py-20 text-gray-400">불러오는 중...</div>
      ) : deals.length === 0 ? (
        <div className="text-center py-20 text-gray-400">진행 중인 딜이 없습니다.</div>
      ) : (
        <>
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

          {/* 페이지네이션 */}
          {totalPages > 1 && (
            <div className="flex justify-center gap-2 mt-8">
              <Button
                variant="outline"
                size="sm"
                disabled={page === 0}
                onClick={() => setPage((p) => p - 1)}
              >
                이전
              </Button>
              <span className="flex items-center px-3 text-sm text-gray-600">
                {page + 1} / {totalPages}
              </span>
              <Button
                variant="outline"
                size="sm"
                disabled={page >= totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
              >
                다음
              </Button>
            </div>
          )}
        </>
      )}
      </div>
    </div>
  );
}
