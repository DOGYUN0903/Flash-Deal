export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface LoginResponse {
  userId: number;
  name: string;
  role: string;
}

export interface SignupRequest {
  email: string;
  password: string;
  name: string;
  phoneNumber: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface MemberProfile {
  id: number;
  email: string;
  name: string;
  phoneNumber: string;
  balance: number;
}

export interface MemberUpdateRequest {
  name: string;
  phoneNumber: string;
}

export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
}

export interface Deal {
  id: number;
  productName: string;
  dealPrice: number;
  stock: number;
  openTime: string;
  endTime: string;
}

export interface DealPurchaseResponse {
  orderId: number;
  dealId: number;
  productName: string;
  dealPrice: number;
  remainingStock: number;
  orderStatus: string;
  createdAt: string;
}
