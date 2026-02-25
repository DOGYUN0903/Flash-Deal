import { api } from "./api";
import { ApiResponse, PageResponse, ProductDetail, ProductSummary, ReviewResponse } from "./types";

export interface ProductSearchParams {
  productName?: string;
  minPrice?: number;
  maxPrice?: number;
  category?: string;
  page?: number;
  size?: number;
}

export const productApi = {
  getProducts: (params: ProductSearchParams = {}) => {
    const query = new URLSearchParams();
    if (params.productName) query.set("productName", params.productName);
    if (params.minPrice !== undefined) query.set("minPrice", String(params.minPrice));
    if (params.maxPrice !== undefined) query.set("maxPrice", String(params.maxPrice));
    if (params.category) query.set("category", params.category);
    if (params.page !== undefined) query.set("page", String(params.page));
    if (params.size !== undefined) query.set("size", String(params.size));

    const qs = query.toString();
    return api.get<ApiResponse<PageResponse<ProductSummary>>>(
      `/api/products${qs ? `?${qs}` : ""}`
    );
  },

  getProduct: (productId: number) =>
    api.get<ApiResponse<ProductDetail>>(`/api/products/${productId}`),

  getReviews: (productId: number, page = 0) =>
    api.get<ApiResponse<PageResponse<ReviewResponse>>>(
      `/api/products/${productId}/reviews?page=${page}&size=10`
    ),

  createReview: (productId: number, rating: number, content: string) =>
    api.post<ApiResponse<ReviewResponse>>(`/api/products/${productId}/reviews`, {
      rating,
      content,
    }),
};
