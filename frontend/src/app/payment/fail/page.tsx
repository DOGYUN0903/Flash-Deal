"use client";

import { Suspense } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { XCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import Header from "@/components/layout/Header";

function PaymentFailContent() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const message = searchParams.get("message") ?? "결제에 실패했습니다.";
  const code = searchParams.get("code");

  return (
    <div>
      <Header />
      <div className="max-w-md mx-auto px-4 py-20 text-center">
        <XCircle size={56} className="mx-auto mb-4 text-red-400" />
        <h1 className="text-xl font-bold mb-2">결제에 실패했습니다</h1>
        <p className="text-gray-500 text-sm mb-1">{message}</p>
        {code && <p className="text-gray-400 text-xs mb-6">오류 코드: {code}</p>}
        <div className="flex gap-3 justify-center">
          <Button variant="outline" onClick={() => router.push("/orders")}>주문 목록</Button>
          <Button onClick={() => router.back()}>다시 시도</Button>
        </div>
      </div>
    </div>
  );
}

export default function PaymentFailPage() {
  return (
    <Suspense fallback={null}>
      <PaymentFailContent />
    </Suspense>
  );
}
