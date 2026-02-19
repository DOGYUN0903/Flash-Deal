import { api } from "./api";
import { ApiResponse, Deal, DealPurchaseResponse } from "./types";

export const dealApi = {
  getDeals: () => api.get<ApiResponse<Deal[]>>("/api/deals"),

  getDeal: (dealId: number) => api.get<ApiResponse<Deal>>(`/api/deals/${dealId}`),

  purchase: (dealId: number) =>
    api.post<ApiResponse<DealPurchaseResponse>>(`/api/deals/${dealId}/purchase`, {}),
};
