import { api } from "./api";
import { ApiResponse, MemberProfile, MemberUpdateRequest, PasswordChangeRequest } from "./types";

export const memberApi = {
  getMyProfile: () =>
    api.get<ApiResponse<MemberProfile>>("/api/members/me"),

  verifyPassword: (password: string) =>
    api.post<ApiResponse<null>>("/api/members/me/verify-password", { password }),

  updateMyProfile: (body: MemberUpdateRequest) =>
    api.patch<ApiResponse<MemberProfile>>("/api/members/me", body),

  changePassword: (body: PasswordChangeRequest) =>
    api.patch<ApiResponse<null>>("/api/members/me/password", body),
};
