"use client";

import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import { toast } from "sonner";
import { ArrowLeft, ShoppingCart, Zap, Star } from "lucide-react";
import Image from "next/image";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { Textarea } from "@/components/ui/textarea";
import Header from "@/components/layout/Header";
import CategoryTab from "@/components/layout/CategoryTab";
import { productApi } from "@/lib/product-api";
import { cartApi } from "@/lib/cart-api";
import { orderApi } from "@/lib/order-api";
import { ProductDetail, ReviewResponse } from "@/lib/types";

const STATUS_LABEL: Record<string, { label: string; variant: "default" | "secondary" | "destructive" }> = {
  ON_SALE:   { label: "판매중",     variant: "default" },
  PREPARING: { label: "판매 준비중", variant: "secondary" },
  SOLD_OUT:  { label: "품절",       variant: "destructive" },
};

function StarRating({ value, onChange }: { value: number; onChange?: (v: number) => void }) {
  const [hovered, setHovered] = useState(0);
  return (
    <div className="flex gap-1">
      {[1, 2, 3, 4, 5].map((star) => (
        <Star
          key={star}
          size={20}
          className={`cursor-pointer transition-colors ${
            star <= (hovered || value)
              ? "fill-yellow-400 text-yellow-400"
              : "text-gray-300"
          }`}
          onClick={() => onChange?.(star)}
          onMouseEnter={() => onChange && setHovered(star)}
          onMouseLeave={() => onChange && setHovered(0)}
        />
      ))}
    </div>
  );
}

export default function ProductDetailPage() {
  const router = useRouter();
  const { id } = useParams();
  const productId = Number(id);

  const [product, setProduct] = useState<ProductDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [adding, setAdding] = useState(false);
  const [buying, setBuying] = useState(false);

  // 리뷰
  const [reviews, setReviews] = useState<ReviewResponse[]>([]);
  const [reviewPage, setReviewPage] = useState(0);
  const [reviewTotalPages, setReviewTotalPages] = useState(0);
  const [rating, setRating] = useState(0);
  const [content, setContent] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    productApi
      .getProduct(productId)
      .then((res) => setProduct(res.data))
      .catch(() => {
        toast.error("상품 정보를 불러오지 못했습니다.");
        router.push("/products");
      })
      .finally(() => setLoading(false));
  }, [productId, router]);

  useEffect(() => {
    productApi
      .getReviews(productId, reviewPage)
      .then((res) => {
        setReviews(res.data.data);
        setReviewTotalPages(res.data.totalPages);
      })
      .catch(() => {});
  }, [productId, reviewPage]);

  const handleReviewSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (rating === 0) {
      toast.error("별점을 선택해주세요.");
      return;
    }
    setSubmitting(true);
    try {
      await productApi.createReview(productId, rating, content);
      toast.success("리뷰가 등록되었습니다.");
      setRating(0);
      setContent("");
      // 리뷰 목록 새로고침
      const res = await productApi.getReviews(productId, 0);
      setReviews(res.data.data);
      setReviewTotalPages(res.data.totalPages);
      setReviewPage(0);
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "리뷰 등록에 실패했습니다.");
    } finally {
      setSubmitting(false);
    }
  };

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
          {/* 좌측: 이미지 */}
          <div className="bg-gray-100 rounded-xl aspect-square overflow-hidden relative">
            {product.imageUrl ? (
              <Image src={product.imageUrl} alt={product.name} fill className="object-cover" />
            ) : (
              <div className="w-full h-full flex items-center justify-center text-gray-400 text-sm">
                상품 이미지
              </div>
            )}
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
                    router.push(`/payment?orderId=${res.data.orderId}`);
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

        {/* 리뷰 섹션 */}
        <div className="mt-16">
          <h2 className="text-xl font-bold mb-6">상품 리뷰</h2>

          {/* 리뷰 작성 폼 */}
          <Card className="mb-8">
            <CardContent className="pt-6">
              <form onSubmit={handleReviewSubmit} className="space-y-4">
                <div>
                  <p className="text-sm font-medium text-gray-700 mb-2">별점</p>
                  <StarRating value={rating} onChange={setRating} />
                </div>
                <div>
                  <Textarea
                    placeholder="리뷰를 작성해주세요. (선택)"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    rows={3}
                    maxLength={500}
                  />
                </div>
                <Button type="submit" disabled={submitting}>
                  {submitting ? "등록 중..." : "리뷰 등록"}
                </Button>
              </form>
            </CardContent>
          </Card>

          {/* 리뷰 목록 */}
          {reviews.length === 0 ? (
            <div className="text-center py-10 text-gray-400">아직 리뷰가 없습니다.</div>
          ) : (
            <div className="space-y-4">
              {reviews.map((review) => (
                <Card key={review.reviewId}>
                  <CardContent className="pt-4">
                    <div className="flex items-center justify-between mb-2">
                      <div className="flex items-center gap-2">
                        <span className="text-sm font-medium">{review.memberName}</span>
                        <StarRating value={review.rating} />
                      </div>
                      <span className="text-xs text-gray-400">
                        {new Date(review.createdAt).toLocaleDateString("ko-KR")}
                      </span>
                    </div>
                    {review.content && (
                      <p className="text-sm text-gray-600">{review.content}</p>
                    )}
                  </CardContent>
                </Card>
              ))}

              {/* 리뷰 페이지네이션 */}
              {reviewTotalPages > 1 && (
                <div className="flex justify-center gap-2 mt-4">
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={reviewPage === 0}
                    onClick={() => setReviewPage((p) => p - 1)}
                  >
                    이전
                  </Button>
                  <span className="flex items-center px-3 text-sm text-gray-600">
                    {reviewPage + 1} / {reviewTotalPages}
                  </span>
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={reviewPage >= reviewTotalPages - 1}
                    onClick={() => setReviewPage((p) => p + 1)}
                  >
                    다음
                  </Button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
