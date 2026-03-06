import { api } from "./api";
import { ApiResponse, DealResponse, OrderDetail, PageResponse } from "./types";

export interface DealOrderBody {
  paymentKey: string;
  orderId: string;
  amount: number;
  quantity: number;
}

export const dealApi = {
  getDeals: (page = 0, size = 10) =>
    api.get<ApiResponse<PageResponse<DealResponse>>>(`/api/deals?page=${page}&size=${size}`),

  getDeal: (dealId: number) =>
    api.get<ApiResponse<DealResponse>>(`/api/deals/${dealId}`),

  createDealOrder: (dealId: number, body: DealOrderBody) =>
    api.post<ApiResponse<OrderDetail>>(`/api/deals/${dealId}/order`, body),
};
