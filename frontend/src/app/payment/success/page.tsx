"use client";

import { useEffect, Suspense } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { toast } from "sonner";
import { paymentApi } from "@/lib/payment-api";

function PaymentSuccessContent() {
  const router = useRouter();
  const searchParams = useSearchParams();

  useEffect(() => {
    const paymentKey = searchParams.get("paymentKey");
    const orderId = searchParams.get("orderId"); // "ORDER-{id}-{uuid}" 형식
    const amount = Number(searchParams.get("amount"));

    if (!paymentKey || !orderId || !amount) {
      router.push("/orders");
      return;
    }

    paymentApi
      .confirmToss(paymentKey, orderId, amount)
      .then((res) => {
        toast.success("결제가 완료되었습니다.");
        const numericOrderId = orderId.replace("ORDER-", "").split("-")[0];
        router.push(`/orders/${numericOrderId}`);
      })
      .catch((err) => {
        toast.error(err instanceof Error ? err.message : "결제 승인에 실패했습니다.");
        router.push("/orders");
      });
  }, [searchParams, router]);

  return (
    <div className="flex min-h-screen items-center justify-center text-gray-400">
      결제 승인 처리 중...
    </div>
  );
}

export default function PaymentSuccessPage() {
  return (
    <Suspense fallback={<div className="flex min-h-screen items-center justify-center text-gray-400">처리 중...</div>}>
      <PaymentSuccessContent />
    </Suspense>
  );
}
