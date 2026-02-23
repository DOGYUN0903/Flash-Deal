"use client";

import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Search, Star } from "lucide-react";
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

  const [productName, setProductName] = useState("");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [searchParams, setSearchParams] = useState({});

  useEffect(() => {
    setLoading(true);
    productApi
      .getProducts({ ...searchParams, page, size: 15 })
      .then((res) => {
        setProducts(res.data.data);
        setTotalPages(res.data.totalPages);
      })
      .catch(() => toast.error("상품 목록을 불러오지 못했습니다."))
      .finally(() => setLoading(false));
  }, [searchParams, page]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
    setSearchParams({
      productName: productName || undefined,
      minPrice: minPrice ? Number(minPrice) : undefined,
      maxPrice: maxPrice ? Number(maxPrice) : undefined,
    });
  };

  return (
    <div>
      <Header />
      <CategoryTab />
      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* 검색 */}
        <form onSubmit={handleSearch} className="flex flex-wrap gap-3 mb-8">
          <Input
            placeholder="상품명 검색"
            value={productName}
            onChange={(e) => setProductName(e.target.value)}
            className="w-48"
          />
          <Input
            type="number"
            placeholder="최소 금액"
            value={minPrice}
            onChange={(e) => setMinPrice(e.target.value)}
            className="w-36"
          />
          <Input
            type="number"
            placeholder="최대 금액"
            value={maxPrice}
            onChange={(e) => setMaxPrice(e.target.value)}
            className="w-36"
          />
          <Button type="submit" className="gap-2">
            <Search size={16} />
            검색
          </Button>
        </form>

        {/* 상품 목록 */}
        {loading ? (
          <div className="text-center py-20 text-gray-400">불러오는 중...</div>
        ) : products.length === 0 ? (
          <div className="text-center py-20 text-gray-400">상품이 없습니다.</div>
        ) : (
          <>
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
              {products.map((product) => (
                <Link href={`/products/${product.productId}`} key={product.productId}>
                  <div className="group cursor-pointer">
                    {/* 이미지 */}
                    <div className="aspect-square bg-gray-100 rounded-lg mb-3 overflow-hidden group-hover:opacity-90 transition-opacity relative">
                      {product.imageUrl ? (
                        <Image src={product.imageUrl} alt={product.name} fill className="object-cover" />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center text-gray-300 text-sm">이미지 없음</div>
                      )}
                    </div>
                    {/* 상품 정보 */}
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
                      {/* 별점 + 리뷰수 */}
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

            {/* 페이지네이션 */}
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
  );
}
