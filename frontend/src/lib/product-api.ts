import { api } from "./api";
import { ApiResponse, PageResponse, ProductDetail, ProductSummary } from "./types";

export interface ProductSearchParams {
  productName?: string;
  minPrice?: number;
  maxPrice?: number;
  page?: number;
  size?: number;
}

export const productApi = {
  getProducts: (params: ProductSearchParams = {}) => {
    const query = new URLSearchParams();
    if (params.productName) query.set("productName", params.productName);
    if (params.minPrice !== undefined) query.set("minPrice", String(params.minPrice));
    if (params.maxPrice !== undefined) query.set("maxPrice", String(params.maxPrice));
    if (params.page !== undefined) query.set("page", String(params.page));
    if (params.size !== undefined) query.set("size", String(params.size));

    const qs = query.toString();
    return api.get<ApiResponse<PageResponse<ProductSummary>>>(
      `/api/products${qs ? `?${qs}` : ""}`
    );
  },

  getProduct: (productId: number) =>
    api.get<ApiResponse<ProductDetail>>(`/api/products/${productId}`),
};
