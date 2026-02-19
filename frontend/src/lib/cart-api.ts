import { api } from "./api";
import { ApiResponse, CartItem, CartResponse } from "./types";

export const cartApi = {
  getCart: () =>
    api.get<ApiResponse<CartResponse>>("/api/carts"),

  addItem: (productId: number, quantity: number) =>
    api.post<ApiResponse<CartItem>>("/api/carts", { productId, quantity }),

  updateItem: (cartItemId: number, quantity: number) =>
    api.patch<ApiResponse<CartItem>>(`/api/carts/${cartItemId}`, { quantity }),

  removeItem: (cartItemId: number) =>
    api.delete<ApiResponse<void>>(`/api/carts/${cartItemId}`),

  clearCart: () =>
    api.delete<ApiResponse<void>>("/api/carts"),
};
