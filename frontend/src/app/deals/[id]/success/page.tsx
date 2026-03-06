"use client";

import { useEffect, Suspense } from "react";
import { useParams, useRouter, useSearchParams } from "next/navigation";
import { toast } from "sonner";
import { dealApi } from "@/lib/deal-api";

function DealSuccessContent() {
  const params = useParams();
  const router = useRouter();
  const searchParams = useSearchParams();
  const dealId = Number(params.id);

  useEffect(() => {
    const paymentKey = searchParams.get("paymentKey");
    const orderId = searchParams.get("orderId");
    const amount = Number(searchParams.get("amount"));
    const quantity = Number(sessionStorage.getItem("deal_quantity") ?? "1");

    if (!paymentKey || !orderId || !amount) {
      router.push(`/deals/${dealId}`);
      return;
    }

    sessionStorage.removeItem("deal_quantity");

    dealApi
      .createDealOrder(dealId, { paymentKey, orderId, amount, quantity })
      .then((res) => {
        toast.success("딜 주문이 완료되었습니다!");
        router.push(`/orders/${res.data.orderId}`);
      })
      .catch((err) => {
        toast.error(err instanceof Error ? err.message : "딜 주문 처리에 실패했습니다.");
        router.push(`/deals/${dealId}`);
      });
  }, [dealId, searchParams, router]);

  return (
    <div className="flex min-h-screen items-center justify-center text-gray-400">
      딜 주문 처리 중...
    </div>
  );
}

export default function DealSuccessPage() {
  return (
    <Suspense fallback={<div className="flex min-h-screen items-center justify-center text-gray-400">처리 중...</div>}>
      <DealSuccessContent />
    </Suspense>
  );
}
