import { api } from "./api";
import { ApiResponse, LoginRequest, LoginResponse, SignupRequest } from "./types";

export const authApi = {
  login: (body: LoginRequest) =>
    api.post<ApiResponse<LoginResponse>>("/api/auth/login", body),

  signup: (body: SignupRequest) =>
    api.post<ApiResponse<LoginResponse>>("/api/auth/signup", body),

  logout: () => api.post<ApiResponse<null>>("/api/auth/logout", {}),
};
