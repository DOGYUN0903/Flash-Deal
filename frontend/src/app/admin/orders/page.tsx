"use client";

import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { adminApi, AdminOrderSummary } from "@/lib/admin-api";

const STATUS_MAP: Record<string, { label: string; variant: "default" | "secondary" | "destructive" }> = {
  PENDING:   { label: "결제 대기",  variant: "secondary" },
  PAID:      { label: "결제 완료",  variant: "default" },
  SHIPPED:   { label: "배송 중",   variant: "default" },
  DELIVERED: { label: "배송 완료", variant: "default" },
  CANCELED:  { label: "취소됨",   variant: "destructive" },
};

export default function AdminOrdersPage() {
  const [orders, setOrders] = useState<AdminOrderSummary[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    adminApi
      .getOrders(page)
      .then((res) => {
        setOrders(res.data.content);
        setTotalPages(res.data.totalPages);
      })
      .catch(() => toast.error("주문 목록을 불러오지 못했습니다."))
      .finally(() => setLoading(false));
  }, [page]);

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold mb-6">주문 관리</h1>

      {loading ? (
        <p className="text-gray-400 py-20 text-center">불러오는 중...</p>
      ) : (
        <>
          <div className="bg-white rounded-lg border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>주문 ID</TableHead>
                  <TableHead>회원 ID</TableHead>
                  <TableHead className="text-right">상품 수</TableHead>
                  <TableHead className="text-right">결제 금액</TableHead>
                  <TableHead>상태</TableHead>
                  <TableHead>주문 일시</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {orders.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={6} className="text-center text-gray-400 py-10">
                      주문이 없습니다.
                    </TableCell>
                  </TableRow>
                ) : orders.map((order) => {
                  const status = STATUS_MAP[order.status] ?? { label: order.status, variant: "secondary" as const };
                  return (
                    <TableRow key={order.orderId}>
                      <TableCell className="font-medium">#{order.orderId}</TableCell>
                      <TableCell className="text-gray-500">{order.memberId}</TableCell>
                      <TableCell className="text-right">{order.itemCount}종</TableCell>
                      <TableCell className="text-right font-medium">
                        {order.totalPrice.toLocaleString("ko-KR")}원
                      </TableCell>
                      <TableCell>
                        <Badge variant={status.variant}>{status.label}</Badge>
                      </TableCell>
                      <TableCell className="text-gray-500 text-sm">
                        {new Date(order.createdAt + "Z").toLocaleString("ko-KR")}
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </div>

          {totalPages > 1 && (
            <div className="flex justify-center gap-2 mt-6">
              <Button variant="outline" size="sm" disabled={page === 0} onClick={() => setPage((p) => p - 1)}>이전</Button>
              <span className="flex items-center px-3 text-sm text-gray-600">{page + 1} / {totalPages}</span>
              <Button variant="outline" size="sm" disabled={page >= totalPages - 1} onClick={() => setPage((p) => p + 1)}>다음</Button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
