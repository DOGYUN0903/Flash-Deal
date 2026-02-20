"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { toast } from "sonner";
import { PackageSearch } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import Header from "@/components/layout/Header";
import { orderApi } from "@/lib/order-api";
import { OrderSummary } from "@/lib/types";

const STATUS_MAP: Record<string, { label: string; variant: "default" | "secondary" | "destructive" }> = {
  PENDING:   { label: "결제 대기",  variant: "secondary" },
  PAID:      { label: "결제 완료",  variant: "default" },
  SHIPPED:   { label: "배송 중",   variant: "default" },
  DELIVERED: { label: "배송 완료", variant: "default" },
  CANCELED:  { label: "취소됨",   variant: "destructive" },
};

export default function OrdersPage() {
  const router = useRouter();
  const [orders, setOrders] = useState<OrderSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    setLoading(true);
    orderApi
      .getOrders(page)
      .then((res) => {
        setOrders(res.data.data);
        setTotalPages(res.data.totalPages);
      })
      .catch(() => {
        toast.error("로그인이 필요합니다.");
        router.push("/login");
      })
      .finally(() => setLoading(false));
  }, [page, router]);

  if (loading) {
    return (
      <div>
        <Header />
        <div className="flex justify-center items-center py-40 text-gray-400">
          불러오는 중...
        </div>
      </div>
    );
  }

  return (
    <div>
      <Header />
      <div className="max-w-3xl mx-auto px-4 py-8">
        <h2 className="text-2xl font-bold mb-6">주문 목록</h2>

        {orders.length === 0 ? (
          <div className="text-center py-20 text-gray-400">
            <PackageSearch size={48} className="mx-auto mb-4 opacity-30" />
            <p>주문 내역이 없습니다.</p>
            <Button className="mt-4" variant="outline" onClick={() => router.push("/products")}>
              상품 보러가기
            </Button>
          </div>
        ) : (
          <>
            <div className="space-y-3">
              {orders.map((order) => {
                const status = STATUS_MAP[order.status] ?? { label: order.status, variant: "secondary" as const };
                return (
                  <Link key={order.orderId} href={`/orders/${order.orderId}`}>
                    <Card className="hover:bg-gray-50 transition-colors cursor-pointer">
                      <CardContent className="flex items-center justify-between py-4">
                        <div className="space-y-1">
                          <p className="font-medium">주문 #{order.orderId}</p>
                          <p className="text-sm text-gray-500">
                            상품 {order.itemCount}종 ·{" "}
                            {new Date(order.createdAt + "Z").toLocaleDateString("ko-KR")}
                          </p>
                        </div>
                        <div className="flex items-center gap-4">
                          <p className="font-bold text-blue-600">
                            {order.totalPrice.toLocaleString("ko-KR")}원
                          </p>
                          <Badge variant={status.variant}>{status.label}</Badge>
                        </div>
                      </CardContent>
                    </Card>
                  </Link>
                );
              })}
            </div>

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
