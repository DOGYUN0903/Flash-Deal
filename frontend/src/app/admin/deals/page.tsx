"use client";

import { useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { adminApi, AdminDealDetail } from "@/lib/admin-api";

const EMPTY_FORM = {
  productId: "",
  dealPrice: "",
  stock: "",
  openTime: "",
  endTime: "",
};

export default function AdminDealsPage() {
  const [form, setForm] = useState(EMPTY_FORM);
  const [submitting, setSubmitting] = useState(false);
  const [lastCreated, setLastCreated] = useState<AdminDealDetail | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const res = await adminApi.createDeal({
        productId: Number(form.productId),
        dealPrice: Number(form.dealPrice),
        stock: Number(form.stock),
        openTime: new Date(form.openTime).toISOString().slice(0, 19),
        endTime: new Date(form.endTime).toISOString().slice(0, 19),
      });
      toast.success("딜이 등록되었습니다.");
      setLastCreated(res.data);
      setForm(EMPTY_FORM);
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
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-2">
                <Label>딜 가격 (원)</Label>
                <Input
                  type="number"
                  min={0}
                  value={form.dealPrice}
                  onChange={(e) => setForm({ ...form, dealPrice: e.target.value })}
                  required
                />
              </div>
              <div className="space-y-2">
                <Label>딜 재고</Label>
                <Input
                  type="number"
                  min={1}
                  value={form.stock}
                  onChange={(e) => setForm({ ...form, stock: e.target.value })}
                  required
                />
              </div>
            </div>
            <div className="space-y-2">
              <Label>오픈 시각</Label>
              <Input
                type="datetime-local"
                value={form.openTime}
                onChange={(e) => setForm({ ...form, openTime: e.target.value })}
                required
              />
            </div>
            <div className="space-y-2">
              <Label>종료 시각</Label>
              <Input
                type="datetime-local"
                value={form.endTime}
                onChange={(e) => setForm({ ...form, endTime: e.target.value })}
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
            <p><span className="font-medium">상품명:</span> {lastCreated.productName}</p>
            <p><span className="font-medium">딜 가격:</span> {lastCreated.dealPrice.toLocaleString("ko-KR")}원</p>
            <p><span className="font-medium">재고:</span> {lastCreated.stock}개</p>
            <p><span className="font-medium">오픈:</span> {new Date(lastCreated.openTime + "Z").toLocaleString("ko-KR")}</p>
            <p><span className="font-medium">종료:</span> {new Date(lastCreated.endTime + "Z").toLocaleString("ko-KR")}</p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
