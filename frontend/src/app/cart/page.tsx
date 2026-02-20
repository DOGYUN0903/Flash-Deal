"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import { Minus, Plus, Trash2, ShoppingCart } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import Header from "@/components/layout/Header";
import { cartApi } from "@/lib/cart-api";
import { orderApi } from "@/lib/order-api";
import { CartItem } from "@/lib/types";

export default function CartPage() {
  const router = useRouter();
  const [items, setItems] = useState<CartItem[]>([]);
  const [totalPrice, setTotalPrice] = useState(0);
  const [loading, setLoading] = useState(true);
  const [ordering, setOrdering] = useState(false);

  useEffect(() => {
    cartApi
      .getCart()
      .then((res) => {
        setItems(res.data.items);
        setTotalPrice(res.data.totalPrice);
      })
      .catch(() => {
        toast.error("로그인이 필요합니다.");
        router.push("/login");
      })
      .finally(() => setLoading(false));
  }, [router]);

  const handleUpdateQuantity = async (cartItemId: number, newQuantity: number) => {
    if (newQuantity < 1) return;
    try {
      const res = await cartApi.updateItem(cartItemId, newQuantity);
      setItems((prev) =>
        prev.map((item) =>
          item.cartItemId === cartItemId
            ? { ...item, quantity: res.data.quantity, totalPrice: res.data.totalPrice }
            : item
        )
      );
      setTotalPrice((prev) => prev - items.find((i) => i.cartItemId === cartItemId)!.totalPrice + res.data.totalPrice);
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "수량 변경에 실패했습니다.");
    }
  };

  const handleRemoveItem = async (cartItemId: number) => {
    try {
      await cartApi.removeItem(cartItemId);
      const removed = items.find((i) => i.cartItemId === cartItemId);
      setItems((prev) => prev.filter((item) => item.cartItemId !== cartItemId));
      if (removed) setTotalPrice((prev) => prev - removed.totalPrice);
      toast.success("상품이 삭제되었습니다.");
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "삭제에 실패했습니다.");
    }
  };

  const handleClearCart = async () => {
    try {
      await cartApi.clearCart();
      setItems([]);
      setTotalPrice(0);
      toast.success("장바구니를 비웠습니다.");
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "장바구니 비우기에 실패했습니다.");
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

  return (
    <div>
      <Header />
      <div className="max-w-3xl mx-auto px-4 py-8">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold flex items-center gap-2">
            <ShoppingCart size={24} />
            장바구니
          </h2>
          {items.length > 0 && (
            <Button variant="outline" size="sm" onClick={handleClearCart}>
              전체 삭제
            </Button>
          )}
        </div>

        {items.length === 0 ? (
          <div className="text-center py-20 text-gray-400">
            <ShoppingCart size={48} className="mx-auto mb-4 opacity-30" />
            <p>장바구니가 비어있습니다.</p>
            <Button className="mt-4" variant="outline" onClick={() => router.push("/products")}>
              상품 보러가기
            </Button>
          </div>
        ) : (
          <>
            <div className="space-y-3">
              {items.map((item) => (
                <Card key={item.cartItemId}>
                  <CardContent className="flex items-center gap-4 py-4">
                    {/* 상품 정보 */}
                    <div className="flex-1 min-w-0">
                      <p className="font-medium truncate">{item.productName}</p>
                      <p className="text-sm text-gray-500">
                        {item.price.toLocaleString("ko-KR")}원 / 개
                      </p>
                    </div>

                    {/* 수량 조절 */}
                    <div className="flex items-center gap-2">
                      <Button
                        variant="outline"
                        size="icon"
                        className="h-8 w-8"
                        onClick={() => handleUpdateQuantity(item.cartItemId, item.quantity - 1)}
                        disabled={item.quantity <= 1}
                      >
                        <Minus size={14} />
                      </Button>
                      <span className="w-8 text-center text-sm font-medium">{item.quantity}</span>
                      <Button
                        variant="outline"
                        size="icon"
                        className="h-8 w-8"
                        onClick={() => handleUpdateQuantity(item.cartItemId, item.quantity + 1)}
                      >
                        <Plus size={14} />
                      </Button>
                    </div>

                    {/* 소계 */}
                    <p className="w-24 text-right font-bold text-blue-600">
                      {item.totalPrice.toLocaleString("ko-KR")}원
                    </p>

                    {/* 삭제 */}
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-8 w-8 text-gray-400 hover:text-red-500"
                      onClick={() => handleRemoveItem(item.cartItemId)}
                    >
                      <Trash2 size={16} />
                    </Button>
                  </CardContent>
                </Card>
              ))}
            </div>

            {/* 합계 & 주문 */}
            <Card className="mt-6">
              <CardHeader>
                <CardTitle className="text-base">주문 요약</CardTitle>
              </CardHeader>
              <CardContent className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">상품 수</span>
                  <span>{items.reduce((acc, i) => acc + i.quantity, 0)}개</span>
                </div>
                <div className="flex justify-between font-bold text-lg">
                  <span>총 결제금액</span>
                  <span className="text-blue-600">{totalPrice.toLocaleString("ko-KR")}원</span>
                </div>
              </CardContent>
              <CardFooter>
                <Button
                  className="w-full"
                  size="lg"
                  disabled={ordering}
                  onClick={async () => {
                    setOrdering(true);
                    try {
                      const res = await orderApi.fromCartOrder();
                      toast.success("주문이 완료되었습니다.");
                      router.push(`/orders/${res.data.orderId}`);
                    } catch (err) {
                      toast.error(err instanceof Error ? err.message : "주문에 실패했습니다.");
                    } finally {
                      setOrdering(false);
                    }
                  }}
                >
                  {ordering ? "처리 중..." : "주문하기"}
                </Button>
              </CardFooter>
            </Card>
          </>
        )}
      </div>
    </div>
  );
}
