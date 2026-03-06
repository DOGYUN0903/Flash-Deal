"use client";

import { useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { adminApi, DealCreateBody } from "@/lib/admin-api";
import { DealResponse } from "@/lib/types";

const EMPTY_FORM: DealCreateBody = {
  productId: 0,
  title: "",
  discountPrice: 0,
  startAt: "",
  endAt: "",
};

export default function AdminDealsPage() {
  const [form, setForm] = useState({
    productId: "",
    title: "",
    discountPrice: "",
    startAt: "",
    endAt: "",
  });
  const [submitting, setSubmitting] = useState(false);
  const [lastCreated, setLastCreated] = useState<DealResponse | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const res = await adminApi.createDeal({
        productId: Number(form.productId),
        title: form.title,
        discountPrice: Number(form.discountPrice),
        startAt: new Date(form.startAt).toISOString().slice(0, 19),
        endAt: new Date(form.endAt).toISOString().slice(0, 19),
      });
      toast.success("딜이 등록되었습니다.");
      setLastCreated(res.data);
      setForm({ productId: "", title: "", discountPrice: "", startAt: "", endAt: "" });
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "딜 등록에 실패했습니다.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="p-8 max-w-xl">
      <h1 className="text-2xl font-bold mb-6">딜 관리</h1>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">선착순 딜 등록</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label>상품 ID</Label>
              <Input
                type="number"
                min={1}
                placeholder="등록할 상품의 ID"
                value={form.productId}
                onChange={(e) => setForm({ ...form, productId: e.target.value })}
                required
              />
            </div>
            <div className="space-y-2">
              <Label>딜 제목</Label>
              <Input
                type="text"
                placeholder="예: 나이키 에어맥스 90 한정 할인!"
                value={form.title}
                onChange={(e) => setForm({ ...form, title: e.target.value })}
                required
              />
            </div>
            <div className="space-y-2">
              <Label>딜 가격 (원)</Label>
              <Input
                type="number"
                min={0}
                value={form.discountPrice}
                onChange={(e) => setForm({ ...form, discountPrice: e.target.value })}
                required
              />
            </div>
            <div className="space-y-2">
              <Label>오픈 시각</Label>
              <Input
                type="datetime-local"
                value={form.startAt}
                onChange={(e) => setForm({ ...form, startAt: e.target.value })}
                required
              />
            </div>
            <div className="space-y-2">
              <Label>종료 시각</Label>
              <Input
                type="datetime-local"
                value={form.endAt}
                onChange={(e) => setForm({ ...form, endAt: e.target.value })}
                required
              />
            </div>
            <Button type="submit" className="w-full" disabled={submitting}>
              {submitting ? "등록 중..." : "딜 등록"}
            </Button>
          </form>
        </CardContent>
      </Card>

      {lastCreated && (
        <Card className="mt-6 border-green-200 bg-green-50">
          <CardHeader>
            <CardTitle className="text-base text-green-700">등록 완료</CardTitle>
          </CardHeader>
          <CardContent className="text-sm space-y-1 text-green-800">
            <p><span className="font-medium">딜 제목:</span> {lastCreated.title}</p>
            <p><span className="font-medium">상품명:</span> {lastCreated.productName}</p>
            <p><span className="font-medium">원가:</span> {lastCreated.originalPrice.toLocaleString("ko-KR")}원</p>
            <p><span className="font-medium">딜 가격:</span> {lastCreated.discountPrice.toLocaleString("ko-KR")}원</p>
            <p><span className="font-medium">잔여 재고:</span> {lastCreated.remainingStock}개</p>
            <p><span className="font-medium">오픈:</span> {new Date(lastCreated.startAt).toLocaleString("ko-KR")}</p>
            <p><span className="font-medium">종료:</span> {new Date(lastCreated.endAt).toLocaleString("ko-KR")}</p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
