"use client";

import { useEffect, useState, Suspense } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { toast } from "sonner";
import { Clock } from "lucide-react";
import { loadTossPayments, ANONYMOUS } from "@tosspayments/tosspayments-sdk";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import Header from "@/components/layout/Header";
import { orderApi } from "@/lib/order-api";
import { OrderDetail } from "@/lib/types";

const CLIENT_KEY = process.env.NEXT_PUBLIC_TOSS_CLIENT_KEY!;
const PAYMENT_TIMEOUT_SECONDS = 10 * 60; // 10분

function formatTime(seconds: number) {
  const m = Math.floor(seconds / 60).toString().padStart(2, "0");
  const s = (seconds % 60).toString().padStart(2, "0");
  return `${m}:${s}`;
}

function PaymentContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const orderId = Number(searchParams.get("orderId"));

  const [order, setOrder] = useState<OrderDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [paying, setPaying] = useState(false);
  const [timeLeft, setTimeLeft] = useState(PAYMENT_TIMEOUT_SECONDS);
  const [expired, setExpired] = useState(false);

  const [tossOrderId] = useState(() =>
    `ORDER-${orderId}-${crypto.randomUUID().slice(0, 8)}`
  );

  useEffect(() => {
    if (!orderId) {
      router.push("/orders");
      return;
    }
    orderApi
      .getOrder(orderId)
      .then((res) => setOrder(res.data))
      .catch(() => {
        toast.error("주문 정보를 불러오지 못했습니다.");
        router.push("/orders");
      })
      .finally(() => setLoading(false));
  }, [orderId, router]);

  // 카운트다운 타이머
  useEffect(() => {
    if (loading || expired) return;

    const interval = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          clearInterval(interval);
          setExpired(true);
          toast.error("결제 시간이 만료되었습니다. 다시 시도해주세요.");
          router.push("/orders");
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(interval);
  }, [loading, expired, router]);

  const handlePayment = async () => {
    if (!order || expired) return;
    setPaying(true);
    try {
      const tossPayments = await loadTossPayments(CLIENT_KEY);
      const payment = tossPayments.payment({ customerKey: ANONYMOUS });

      const orderName =
        order.orderItems.length === 1
          ? order.orderItems[0].productName
          : `${order.orderItems[0].productName} 외 ${order.orderItems.length - 1}개`;

      await payment.requestPayment({
        method: "CARD",
        amount: { currency: "KRW", value: order.totalPrice },
        orderId: tossOrderId,
        orderName,
        successUrl: `${window.location.origin}/payment/success`,
        failUrl: `${window.location.origin}/payment/fail`,
      });
    } catch (err: unknown) {
      if (err && typeof err === "object" && "code" in err && (err as { code: string }).code !== "USER_CANCEL") {
        toast.error("결제 중 오류가 발생했습니다.");
      }
      setPaying(false);
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

  if (!order) return null;

  const isUrgent = timeLeft <= 60;

  return (
    <div>
      <Header />
      <div className="max-w-xl mx-auto px-4 py-8">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-bold">결제하기</h1>
          {/* 카운트다운 타이머 */}
          <div className={`flex items-center gap-2 px-4 py-2 rounded-lg font-mono font-bold text-lg border-2 transition-colors ${
            isUrgent
              ? "border-red-400 bg-red-50 text-red-600 animate-pulse"
              : "border-orange-300 bg-orange-50 text-orange-600"
          }`}>
            <Clock size={18} />
            {formatTime(timeLeft)}
          </div>
        </div>

        {isUrgent && (
          <p className="text-sm text-red-500 text-right -mt-4 mb-4">
            곧 결제 시간이 만료됩니다!
          </p>
        )}

        <Card className="mb-4">
          <CardHeader>
            <CardTitle className="text-base">주문 상품</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2">
            {order.orderItems.map((item) => (
              <div key={item.orderItemId} className="flex justify-between text-sm">
                <span>{item.productName} × {item.quantity}</span>
                <span className="font-medium">{item.orderPrice.toLocaleString("ko-KR")}원</span>
              </div>
            ))}
          </CardContent>
        </Card>

        <Card className="mb-6">
          <CardContent className="pt-4">
            <div className="flex justify-between font-bold text-lg">
              <span>총 결제금액</span>
              <span className="text-blue-600">{order.totalPrice.toLocaleString("ko-KR")}원</span>
            </div>
          </CardContent>
        </Card>

        <Button className="w-full" size="lg" onClick={handlePayment} disabled={paying || expired}>
          {paying ? "결제창 이동 중..." : `${order.totalPrice.toLocaleString("ko-KR")}원 결제하기`}
        </Button>
      </div>
    </div>
  );
}

export default function PaymentPage() {
  return (
    <Suspense fallback={<div className="flex justify-center items-center py-40 text-gray-400">불러오는 중...</div>}>
      <PaymentContent />
    </Suspense>
  );
}
