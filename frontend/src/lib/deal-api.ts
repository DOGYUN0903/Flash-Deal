import { api } from "./api";
import { ApiResponse, DealResponse, OrderDetail } from "./types";

export interface DealOrderBody {
  paymentKey: string;
  orderId: string;
  amount: number;
  quantity: number;
}

export const dealApi = {
  getDeals: () =>
    api.get<ApiResponse<DealResponse[]>>("/api/deals"),

  getDeal: (dealId: number) =>
    api.get<ApiResponse<DealResponse>>(`/api/deals/${dealId}`),

  createDealOrder: (dealId: number, body: DealOrderBody) =>
    api.post<ApiResponse<OrderDetail>>(`/api/deals/${dealId}/order`, body),
};
