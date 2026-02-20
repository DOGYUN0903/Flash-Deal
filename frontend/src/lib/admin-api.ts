import { api } from "./api";
import { ApiResponse, PageResponse } from "./types";

export interface AdminProductSummary {
  productId: number;
  name: string;
  price: number;
  stockQuantity: number;
  status: string;
}

export interface AdminProductDetail {
  productId: number;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  status: string;
}

export interface ProductCreateBody {
  name: string;
  description: string;
  price: number;
  stock: number;
}

export interface ProductUpdateBody {
  name?: string;
  description?: string;
  price?: number;
  stock?: number;
}

export interface DealCreateBody {
  productId: number;
  dealPrice: number;
  stock: number;
  openTime: string;
  endTime: string;
}

export interface AdminDealDetail {
  id: number;
  productName: string;
  productDescription: string;
  originalPrice: number;
  dealPrice: number;
  stock: number;
  openTime: string;
  endTime: string;
}

export interface AdminOrderSummary {
  orderId: number;
  memberId: number;
  status: string;
  totalPrice: number;
  itemCount: number;
  createdAt: string;
}

export const adminApi = {
  // 상품
  getProducts: (page = 0, size = 10) =>
    api.get<ApiResponse<PageResponse<AdminProductSummary>>>(
      `/api/admin/products?page=${page}&size=${size}`
    ),

  getProduct: (productId: number) =>
    api.get<ApiResponse<AdminProductDetail>>(`/api/admin/products/${productId}`),

  createProduct: (body: ProductCreateBody) =>
    api.post<ApiResponse<AdminProductDetail>>("/api/admin/products", body),

  updateProduct: (productId: number, body: ProductUpdateBody) =>
    api.patch<ApiResponse<AdminProductDetail>>(`/api/admin/products/${productId}`, body),

  deleteProduct: (productId: number) =>
    api.delete<ApiResponse<void>>(`/api/admin/products/${productId}`),

  // 딜
  createDeal: (body: DealCreateBody) =>
    api.post<ApiResponse<AdminDealDetail>>("/api/admin/deals", body),

  // 주문
  getOrders: (page = 0, size = 10) =>
    api.get<ApiResponse<PageResponse<AdminOrderSummary>>>(
      `/api/admin/orders?page=${page}&size=${size}`
    ),
};
