import { api } from "./api";
import { ApiResponse, Deal, DealDetail, DealPurchaseResponse, PageResponse } from "./types";

export const dealApi = {
  getDeals: (page = 0, size = 10) =>
    api.get<ApiResponse<PageResponse<Deal>>>(`/api/deals?page=${page}&size=${size}`),

  getDeal: (dealId: number) =>
    api.get<ApiResponse<DealDetail>>(`/api/deals/${dealId}`),

  purchase: (dealId: number) =>
    api.post<ApiResponse<DealPurchaseResponse>>(`/api/deals/${dealId}/purchase`, {}),
};
