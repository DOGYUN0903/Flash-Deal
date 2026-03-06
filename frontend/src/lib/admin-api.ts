import { api } from "./api";
import { ApiResponse, DealResponse, PageResponse } from "./types";

export interface AdminProductSummary {
  productId: number;
  name: string;
  price: number;
  stockQuantity: number;
  status: string;
  imageUrl: string | null;
}

export interface AdminProductDetail {
  productId: number;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  status: string;
  category: string;
  imageUrl: string | null;
}

export interface ProductCreateBody {
  name: string;
  description: string;
  price: number;
  stock: number;
  category: string;
}

export interface ProductUpdateBody {
  name?: string;
  description?: string;
  price?: number;
  stock?: number;
  category?: string;
}

export interface DealCreateBody {
  productId: number;
  title: string;
  discountPrice: number;
  startAt: string;
  endAt: string;
}

export interface AdminOrderSummary {
  orderId: number;
  memberId: number;
  status: string;
  totalPrice: number;
  itemCount: number;
  createdAt: string;
}

async function multipartRequest<T>(
  url: string,
  method: "POST" | "PATCH",
  data: object,
  image?: File | null
): Promise<T> {
  const formData = new FormData();
  Object.entries(data).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      formData.append(key, String(value));
    }
  });
  if (image) formData.append("image", image);

  const res = await fetch(url, { method, credentials: "include", body: formData });

  if (res.status === 401) {
    window.location.href = "/login";
    throw new Error("로그인이 필요합니다.");
  }

  const json = await res.json();
  if (!res.ok) throw new Error(json?.message ?? "요청에 실패했습니다.");
  return json;
}

export const adminApi = {
  // 상품
  getProducts: (page = 0, size = 10) =>
    api.get<ApiResponse<PageResponse<AdminProductSummary>>>(
      `/api/admin/products?page=${page}&size=${size}`
    ),

  getProduct: (productId: number) =>
    api.get<ApiResponse<AdminProductDetail>>(`/api/admin/products/${productId}`),

  createProduct: (body: ProductCreateBody, image?: File | null) =>
    multipartRequest<ApiResponse<AdminProductDetail>>(
      "/api/admin/products", "POST", body, image
    ),

  updateProduct: (productId: number, body: ProductUpdateBody, image?: File | null) =>
    multipartRequest<ApiResponse<AdminProductDetail>>(
      `/api/admin/products/${productId}`, "PATCH", body, image
    ),

  deleteProduct: (productId: number) =>
    api.delete<ApiResponse<void>>(`/api/admin/products/${productId}`),

  // 딜
  createDeal: (body: DealCreateBody) =>
    api.post<ApiResponse<DealResponse>>("/api/admin/deals", body),

  // 주문
  getOrders: (page = 0, size = 10) =>
    api.get<ApiResponse<PageResponse<AdminOrderSummary>>>(
      `/api/admin/orders?page=${page}&size=${size}`
    ),
};
