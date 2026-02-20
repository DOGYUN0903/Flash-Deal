"use client";

import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import { toast } from "sonner";
import { ArrowLeft, ShoppingCart, Zap } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import Header from "@/components/layout/Header";
import CategoryTab from "@/components/layout/CategoryTab";
import { productApi } from "@/lib/product-api";
import { cartApi } from "@/lib/cart-api";
import { orderApi } from "@/lib/order-api";
import { ProductDetail } from "@/lib/types";

const STATUS_LABEL: Record<string, { label: string; variant: "default" | "secondary" | "destructive" }> = {
  ON_SALE:   { label: "판매중",     variant: "default" },
  PREPARING: { label: "판매 준비중", variant: "secondary" },
  SOLD_OUT:  { label: "품절",       variant: "destructive" },
};

export default function ProductDetailPage() {
  const router = useRouter();
  const { id } = useParams();
  const [product, setProduct] = useState<ProductDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [adding, setAdding] = useState(false);
  const [buying, setBuying] = useState(false);

  useEffect(() => {
    productApi
      .getProduct(Number(id))
      .then((res) => setProduct(res.data))
      .catch(() => {
        toast.error("상품 정보를 불러오지 못했습니다.");
        router.push("/products");
      })
      .finally(() => setLoading(false));
  }, [id, router]);

  if (loading) {
    return (
      <div>
        <Header />
        <CategoryTab />
        <div className="flex justify-center items-center py-40 text-gray-400">
          불러오는 중...
        </div>
      </div>
    );
  }

  if (!product) return null;

  const status = STATUS_LABEL[product.status] ?? { label: product.status, variant: "secondary" as const };

  return (
    <div>
      <Header />
      <CategoryTab />
      <div className="max-w-6xl mx-auto px-4 py-8">
        {/* 뒤로가기 */}
        <button
          onClick={() => router.back()}
          className="flex items-center gap-1 text-sm text-gray-500 hover:text-gray-800 mb-6"
        >
          <ArrowLeft size={16} />
          상품 목록으로
        </button>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-10">
          {/* 좌측: 상품 이미지 영역 (placeholder) */}
          <div className="bg-gray-100 rounded-xl aspect-square flex items-center justify-center text-gray-400 text-sm">
            상품 이미지
          </div>

          {/* 우측: 상품 정보 */}
          <div className="flex flex-col gap-4">
            <div className="flex items-start justify-between gap-3">
              <h1 className="text-2xl font-bold">{product.name}</h1>
              <Badge variant={status.variant}>{status.label}</Badge>
            </div>

            <p className="text-3xl font-bold text-blue-600">
              {product.price.toLocaleString("ko-KR")}원
            </p>

            {product.description && (
              <Card>
                <CardContent className="pt-4">
                  <p className="text-sm text-gray-600 leading-relaxed whitespace-pre-line">
                    {product.description}
                  </p>
                </CardContent>
              </Card>
            )}

            <div className="flex flex-col gap-3 mt-4">
              <Button
                size="lg"
                className="gap-2"
                disabled={product.status !== "ON_SALE" || buying}
                onClick={async () => {
                  setBuying(true);
                  try {
                    const res = await orderApi.directOrder(product.productId, 1);
                    toast.success("주문이 완료되었습니다.");
                    router.push(`/orders/${res.data.orderId}`);
                  } catch (err) {
                    toast.error(err instanceof Error ? err.message : "즉시 구매에 실패했습니다.");
                  } finally {
                    setBuying(false);
                  }
                }}
              >
                <Zap size={18} />
                {buying ? "처리 중..." : "즉시 구매"}
              </Button>
              <Button
                size="lg"
                variant="outline"
                className="gap-2"
                disabled={product.status !== "ON_SALE" || adding}
                onClick={async () => {
                  setAdding(true);
                  try {
                    await cartApi.addItem(product.productId, 1);
                    toast.success("장바구니에 담았습니다.");
                  } catch (err) {
                    toast.error(err instanceof Error ? err.message : "장바구니 담기에 실패했습니다.");
                  } finally {
                    setAdding(false);
                  }
                }}
              >
                <ShoppingCart size={18} />
                {adding ? "담는 중..." : "장바구니 담기"}
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
