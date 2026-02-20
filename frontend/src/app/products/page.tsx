"use client";

import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Search } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import Link from "next/link";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import Header from "@/components/layout/Header";
import CategoryTab from "@/components/layout/CategoryTab";
import { productApi } from "@/lib/product-api";
import { ProductSummary } from "@/lib/types";

const STATUS_LABEL: Record<string, { label: string; variant: "default" | "secondary" | "destructive" }> = {
  ON_SALE:   { label: "판매중",     variant: "default" },
  PREPARING: { label: "판매 준비중", variant: "secondary" },
  SOLD_OUT:  { label: "품절",       variant: "destructive" },
};

export default function ProductsPage() {
  const [products, setProducts] = useState<ProductSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);

  // 검색 조건
  const [productName, setProductName] = useState("");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [searchParams, setSearchParams] = useState({});

  useEffect(() => {
    setLoading(true);
    productApi
      .getProducts({ ...searchParams, page, size: 10 })
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
      <div className="max-w-6xl mx-auto px-4 py-8">
        <h2 className="text-2xl font-bold mb-6">상품 목록</h2>

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
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {products.map((product) => {
                const status = STATUS_LABEL[product.status] ?? { label: product.status, variant: "secondary" as const };
                return (
                  <Link href={`/products/${product.productId}`} key={product.productId}>
                    <Card className="hover:shadow-md transition-shadow cursor-pointer h-full">
                      <CardHeader>
                        <div className="flex items-start justify-between gap-2">
                          <CardTitle className="text-base">{product.name}</CardTitle>
                          <Badge variant={status.variant}>{status.label}</Badge>
                        </div>
                      </CardHeader>
                      <CardContent>
                        <p className="text-xl font-bold text-blue-600">
                          {product.price.toLocaleString("ko-KR")}원
                        </p>
                      </CardContent>
                    </Card>
                  </Link>
                );
              })}
            </div>

            {/* 페이지네이션 */}
            {totalPages > 1 && (
              <div className="flex justify-center gap-2 mt-8">
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
