import { api } from "./api";
import { ApiResponse, OrderDetail, OrderSummary, PageResponse } from "./types";

export const orderApi = {
  getOrders: (page = 0, size = 10) =>
    api.get<ApiResponse<PageResponse<OrderSummary>>>(`/api/orders?page=${page}&size=${size}`),

  getOrder: (orderId: number) =>
    api.get<ApiResponse<OrderDetail>>(`/api/orders/${orderId}`),

  directOrder: (productId: number, quantity: number) =>
    api.post<ApiResponse<OrderDetail>>("/api/orders/direct", { productId, quantity }),

  cancelOrder: (orderId: number) =>
    api.delete<ApiResponse<void>>(`/api/orders/${orderId}`),
};
