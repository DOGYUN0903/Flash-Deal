"use client";

import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Star, X } from "lucide-react";
import Image from "next/image";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import Link from "next/link";
import Header from "@/components/layout/Header";
import CategoryTab from "@/components/layout/CategoryTab";
import { productApi } from "@/lib/product-api";
import { ProductSummary } from "@/lib/types";

export default function ProductsPage() {
  const [products, setProducts] = useState<ProductSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);

  const [category, setCategory] = useState<string | null>(null);

  const [draftName, setDraftName] = useState("");
  const [draftMinPrice, setDraftMinPrice] = useState("");
  const [draftMaxPrice, setDraftMaxPrice] = useState("");

  const [appliedName, setAppliedName] = useState("");
  const [appliedMinPrice, setAppliedMinPrice] = useState("");
  const [appliedMaxPrice, setAppliedMaxPrice] = useState("");
  const [excludeSoldOut, setExcludeSoldOut] = useState(false);

  const hasActiveFilter = appliedName || appliedMinPrice || appliedMaxPrice || excludeSoldOut;

  useEffect(() => {
    setLoading(true);
    productApi
      .getProducts({
        productName: appliedName || undefined,
        minPrice: appliedMinPrice ? Number(appliedMinPrice) : undefined,
        maxPrice: appliedMaxPrice ? Number(appliedMaxPrice) : undefined,
        category: category ?? undefined,
        excludeSoldOut: excludeSoldOut || undefined,
        page,
        size: 15,
      })
      .then((res) => {
        setProducts(res.data.data);
        setTotalPages(res.data.totalPages);
      })
      .catch(() => toast.error("상품 목록을 불러오지 못했습니다."))
      .finally(() => setLoading(false));
  }, [appliedName, appliedMinPrice, appliedMaxPrice, excludeSoldOut, category, page]);

  const handleCategoryChange = (cat: string | null) => {
    setCategory(cat);
    setPage(0);
  };

  const handleApplyFilter = () => {
    setAppliedName(draftName);
    setAppliedMinPrice(draftMinPrice);
    setAppliedMaxPrice(draftMaxPrice);
    setPage(0);
  };

  const handleResetFilter = () => {
    setDraftName("");
    setDraftMinPrice("");
    setDraftMaxPrice("");
    setAppliedName("");
    setAppliedMinPrice("");
    setAppliedMaxPrice("");
    setExcludeSoldOut(false);
    setPage(0);
  };

  return (
    <div>
      <Header />
      <CategoryTab activeCategory={category} onCategoryChange={handleCategoryChange} />

      <div className="max-w-7xl mx-auto px-4 py-6">
        <div className="flex gap-6">
          {/* 사이드바 필터 */}
          <aside className="w-52 shrink-0">
            <div className="border rounded-xl p-4 bg-white sticky top-4">
              <div className="flex items-center justify-between mb-4">
                <span className="text-sm font-semibold">필터</span>
                {hasActiveFilter && (
                  <button
                    onClick={handleResetFilter}
                    className="flex items-center gap-1 text-xs text-gray-400 hover:text-black transition-colors"
                  >
                    <X size={12} />
                    초기화
                  </button>
                )}
              </div>

              <div className="flex flex-col gap-5">
                {/* 상품명 */}
                <div className="flex flex-col gap-1.5">
                  <label className="text-xs font-medium text-gray-600">상품명</label>
                  <Input
                    placeholder="상품명 검색"
                    value={draftName}
                    onChange={(e) => setDraftName(e.target.value)}
                    onKeyDown={(e) => e.key === "Enter" && handleApplyFilter()}
                    className="h-8 text-sm"
                  />
                </div>

                {/* 품절 제외 */}
                <div className="flex items-center gap-2">
                  <input
                    type="checkbox"
                    id="excludeSoldOut"
                    checked={excludeSoldOut}
                    onChange={(e) => {
                      setExcludeSoldOut(e.target.checked);
                      setPage(0);
                    }}
                    className="w-4 h-4 cursor-pointer accent-black"
                  />
                  <label htmlFor="excludeSoldOut" className="text-xs font-medium text-gray-600 cursor-pointer">
                    품절 제외
                  </label>
                </div>

                {/* 가격 범위 */}
                <div className="flex flex-col gap-1.5">
                  <label className="text-xs font-medium text-gray-600">가격 범위</label>
                  <Input
                    type="number"
                    placeholder="최소 금액"
                    min={0}
                    value={draftMinPrice}
                    onChange={(e) => setDraftMinPrice(e.target.value)}
                    className="h-8 text-sm"
                  />
                  <Input
                    type="number"
                    placeholder="최대 금액"
                    min={0}
                    value={draftMaxPrice}
                    onChange={(e) => setDraftMaxPrice(e.target.value)}
                    className="h-8 text-sm"
                  />
                </div>
              </div>

              <Button size="sm" className="w-full mt-5" onClick={handleApplyFilter}>
                적용
              </Button>
            </div>
          </aside>

          {/* 상품 목록 */}
          <div className="flex-1 min-w-0">
            <p className="text-sm text-gray-500 mb-4">
              {category
                ? `카테고리: ${{
                    ELECTRONICS: "전자기기",
                    FASHION: "패션",
                    FOOD: "식품",
                    SPORTS: "스포츠",
                    BEAUTY: "뷰티",
                    FURNITURE: "가구",
                    BOOKS: "도서",
                  }[category] ?? category}`
                : "전체 상품"}
            </p>

            {loading ? (
              <div className="text-center py-20 text-gray-400">불러오는 중...</div>
            ) : products.length === 0 ? (
              <div className="text-center py-20 text-gray-400">상품이 없습니다.</div>
            ) : (
              <>
                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
                  {products.map((product) => (
                    <Link href={`/products/${product.productId}`} key={product.productId}>
                      <div className="group cursor-pointer">
                        <div className="aspect-square bg-gray-100 rounded-lg mb-3 overflow-hidden group-hover:opacity-90 transition-opacity relative">
                          {product.imageUrl ? (
                            <Image src={product.imageUrl} alt={product.name} fill className="object-cover" />
                          ) : (
                            <div className="w-full h-full flex items-center justify-center text-gray-300 text-sm">
                              이미지 없음
                            </div>
                          )}
                        </div>
                        <div className="space-y-1">
                          {product.status === "SOLD_OUT" && (
                            <Badge variant="destructive" className="text-xs">품절</Badge>
                          )}
                          <p className="text-sm font-medium text-gray-800 leading-snug line-clamp-2">
                            {product.name}
                          </p>
                          <p className="text-base font-bold">
                            {product.price.toLocaleString("ko-KR")}원
                          </p>
                          <div className="flex items-center gap-1">
                            <Star size={12} className="fill-yellow-400 text-yellow-400" />
                            <span className="text-xs text-gray-600">
                              {product.averageRating != null
                                ? product.averageRating.toFixed(1)
                                : "0.0"}
                            </span>
                            <span className="text-xs text-gray-400">
                              ({product.reviewCount.toLocaleString()})
                            </span>
                          </div>
                        </div>
                      </div>
                    </Link>
                  ))}
                </div>

                {totalPages > 1 && (
                  <div className="flex justify-center gap-2 mt-10">
                    <Button
                      variant="outline"
                      size="sm"
                      disabled={page === 0}
                      onClick={() => setPage((p) => p - 1)}
                    >
                      이전
                    </Button>
                    <span className="flex items-center px-3 text-sm text-gray-600">
                      {page + 1} / {totalPages}
                    </span>
                    <Button
                      variant="outline"
                      size="sm"
                      disabled={page >= totalPages - 1}
                      onClick={() => setPage((p) => p + 1)}
                    >
                      다음
                    </Button>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
