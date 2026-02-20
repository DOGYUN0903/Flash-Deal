import { api } from "./api";
import { ApiResponse } from "./types";

export interface PaymentResponse {
  paymentId: number;
  orderId: number;
  status: string;
  method: string;
  amount: number;
  paidAt: string;
  createdAt: string;
}

export const paymentApi = {
  confirmToss: (paymentKey: string, orderId: string, amount: number) =>
    api.post<ApiResponse<PaymentResponse>>("/api/payments/toss/confirm", {
      paymentKey,
      orderId,
      amount,
    }),
};
