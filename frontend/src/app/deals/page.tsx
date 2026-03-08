"use client";

import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Zap, Clock } from "lucide-react";
import Link from "next/link";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import Header from "@/components/layout/Header";
import { dealApi } from "@/lib/deal-api";
import { DealResponse } from "@/lib/types";

function getStatusBadge(status: string) {
  if (status === "ACTIVE") return <Badge className="bg-green-500 hover:bg-green-600">진행중</Badge>;
  if (status === "SCHEDULED") return <Badge variant="secondary">오픈 예정</Badge>;
  return <Badge variant="outline" className="text-gray-400">종료</Badge>;
}

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
    <span className="flex items-center gap-1 text-xs text-orange-600 font-medium">
      <Clock size={12} />
      {remaining}
    </span>
  );
}

export default function DealsPage() {
  const [deals, setDeals] = useState<DealResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    setLoading(true);
    dealApi
      .getDeals(page)
      .then((res) => {
        setDeals(res.data.content);
        setTotalPages(res.data.totalPages);
      })
      .catch(() => toast.error("딜 목록을 불러오지 못했습니다."))
      .finally(() => setLoading(false));
  }, [page]);

  const activeDeals = deals.filter((d) => d.status === "ACTIVE");
  const otherDeals = deals.filter((d) => d.status !== "ACTIVE");

  return (
    <div>
      <Header />
      <div className="max-w-5xl mx-auto px-4 py-8">
        <div className="flex items-center gap-2 mb-6">
          <Zap size={24} className="text-yellow-500 fill-yellow-400" />
          <h1 className="text-2xl font-bold">선착순 플래시 딜</h1>
        </div>

        {loading ? (
          <div className="text-center py-20 text-gray-400">불러오는 중...</div>
        ) : deals.length === 0 ? (
          <div className="text-center py-20 text-gray-400">현재 등록된 딜이 없습니다.</div>
        ) : (
          <>
            {activeDeals.length > 0 && (
              <section className="mb-10">
                <h2 className="text-base font-semibold text-green-600 mb-3">⚡ 지금 진행중</h2>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                  {activeDeals.map((deal) => (
                    <DealCard key={deal.dealId} deal={deal} />
                  ))}
                </div>
              </section>
            )}
            {otherDeals.length > 0 && (
              <section>
                <h2 className="text-base font-semibold text-gray-500 mb-3">기타 딜</h2>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                  {otherDeals.map((deal) => (
                    <DealCard key={deal.dealId} deal={deal} />
                  ))}
                </div>
              </section>
            )}
          </>
        )}

        {totalPages > 1 && (
          <div className="flex justify-center gap-2 mt-10">
            <Button variant="outline" size="sm" disabled={page === 0} onClick={() => setPage((p) => p - 1)}>
              이전
            </Button>
            <span className="flex items-center px-3 text-sm text-gray-600">
              {page + 1} / {totalPages}
            </span>
            <Button variant="outline" size="sm" disabled={page >= totalPages - 1} onClick={() => setPage((p) => p + 1)}>
              다음
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}

function DealCard({ deal }: { deal: DealResponse }) {
  const discountRate = getDiscountRate(deal.originalPrice, deal.discountPrice);
  const isActive = deal.status === "ACTIVE";
  const isEnded = deal.status === "ENDED";

  return (
    <Link href={`/deals/${deal.dealId}`}>
      <div className={`border rounded-xl p-5 hover:shadow-md transition-shadow cursor-pointer ${isEnded ? "opacity-60" : "bg-white"}`}>
        <div className="flex items-start justify-between mb-3">
          {getStatusBadge(deal.status)}
          {discountRate > 0 && (
            <span className="text-sm font-bold text-red-500">{discountRate}% OFF</span>
          )}
        </div>
        <p className="font-semibold text-gray-800 mb-1 line-clamp-2">{deal.title}</p>
        <p className="text-xs text-gray-500 mb-3">{deal.productName}</p>
        <div className="flex items-baseline gap-2 mb-2">
          <span className="text-xl font-bold text-blue-600">
            {deal.discountPrice.toLocaleString("ko-KR")}원
          </span>
          <span className="text-sm text-gray-400 line-through">
            {deal.originalPrice.toLocaleString("ko-KR")}원
          </span>
        </div>
        <div className="flex items-center justify-between text-xs text-gray-500">
          <span>잔여 재고 {deal.remainingStock}개</span>
          {isActive && <Countdown endAt={deal.endAt} />}
          {deal.status === "SCHEDULED" && (
            <span>{new Date(deal.startAt).toLocaleString("ko-KR")} 오픈</span>
          )}
        </div>
      </div>
    </Link>
  );
}
