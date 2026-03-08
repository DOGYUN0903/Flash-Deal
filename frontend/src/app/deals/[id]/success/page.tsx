"use client";

import { useEffect } from "react";
import { useParams, useRouter } from "next/navigation";

export default function DealSuccessPage() {
  const params = useParams();
  const router = useRouter();

  useEffect(() => {
    router.push(`/deals/${params.id}`);
  }, [params.id, router]);

  return (
    <div className="flex min-h-screen items-center justify-center text-gray-400">
      리다이렉트 중...
    </div>
  );
}
