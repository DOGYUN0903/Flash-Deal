"use client";

import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import { toast } from "sonner";
import { ArrowLeft } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import Header from "@/components/layout/Header";
import { orderApi } from "@/lib/order-api";
import { OrderDetail } from "@/lib/types";

const STATUS_MAP: Record<string, { label: string; variant: "default" | "secondary" | "destructive" }> = {
  PENDING:   { label: "결제 대기",  variant: "secondary" },
  PAID:      { label: "결제 완료",  variant: "default" },
  SHIPPED:   { label: "배송 중",   variant: "default" },
  DELIVERED: { label: "배송 완료", variant: "default" },
  CANCELED:  { label: "취소됨",   variant: "destructive" },
};

export default function OrderDetailPage() {
  const router = useRouter();
  const { id } = useParams();
  const [order, setOrder] = useState<OrderDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [canceling, setCanceling] = useState(false);

  useEffect(() => {
    orderApi
      .getOrder(Number(id))
      .then((res) => setOrder(res.data))
      .catch(() => {
        toast.error("주문 정보를 불러오지 못했습니다.");
        router.push("/orders");
      })
      .finally(() => setLoading(false));
  }, [id, router]);

  const handleCancel = async () => {
    if (!confirm("정말 주문을 취소하시겠습니까?")) return;
    setCanceling(true);
    try {
      await orderApi.cancelOrder(Number(id));
      toast.success("주문이 취소되었습니다.");
      setOrder((prev) => prev ? { ...prev, status: "CANCELED" } : prev);
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "주문 취소에 실패했습니다.");
    } finally {
      setCanceling(false);
    }
  };

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

  if (!order) return null;

  const status = STATUS_MAP[order.status] ?? { label: order.status, variant: "secondary" as const };
  const isCancelable = order.status === "PENDING" || order.status === "PAID";

  return (
    <div>
      <Header />
      <div className="max-w-3xl mx-auto px-4 py-8">
        <button
          onClick={() => router.back()}
          className="flex items-center gap-1 text-sm text-gray-500 hover:text-gray-800 mb-6"
        >
          <ArrowLeft size={16} />
          주문 목록으로
        </button>

        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold">주문 #{order.orderId}</h2>
          <Badge variant={status.variant}>{status.label}</Badge>
        </div>

        {/* 주문 상품 목록 */}
        <Card className="mb-4">
          <CardHeader>
            <CardTitle className="text-base">주문 상품</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {order.orderItems.map((item) => (
              <div key={item.orderItemId} className="flex justify-between items-center text-sm">
                <div>
                  <p className="font-medium">{item.productName}</p>
                  <p className="text-gray-500">
                    {item.price.toLocaleString("ko-KR")}원 × {item.quantity}개
                  </p>
                </div>
                <p className="font-bold">{item.orderPrice.toLocaleString("ko-KR")}원</p>
              </div>
            ))}
          </CardContent>
        </Card>

        {/* 주문 요약 */}
        <Card className="mb-6">
          <CardContent className="pt-4 space-y-2">
            <div className="flex justify-between text-sm">
              <span className="text-gray-500">주문 일시</span>
              <span>{new Date(order.createdAt).toLocaleString("ko-KR")}</span>
            </div>
            <div className="flex justify-between font-bold text-lg">
              <span>총 결제금액</span>
              <span className="text-blue-600">{order.totalPrice.toLocaleString("ko-KR")}원</span>
            </div>
          </CardContent>
        </Card>

        {/* 취소 버튼 */}
        {isCancelable && (
          <Button
            variant="destructive"
            className="w-full"
            onClick={handleCancel}
            disabled={canceling}
          >
            {canceling ? "취소 중..." : "주문 취소"}
          </Button>
        )}
      </div>
    </div>
  );
}
