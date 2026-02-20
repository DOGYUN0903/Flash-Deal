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

export interface PageResponse<T> {
  data: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
}

export interface ProductSummary {
  productId: number;
  name: string;
  price: number;
  status: string;
}

export interface ProductDetail {
  productId: number;
  name: string;
  description: string;
  price: number;
  status: string;
}

export interface Deal {
  id: number;
  productName: string;
  dealPrice: number;
  stock: number;
  openTime: string;
  endTime: string;
}

export interface CartItem {
  cartItemId: number;
  productId: number;
  productName: string;
  price: number;
  quantity: number;
  totalPrice: number;
}

export interface CartResponse {
  memberId: number;
  items: CartItem[];
  totalPrice: number;
  totalQuantity: number;
}

export interface DealDetail {
  id: number;
  productName: string;
  productDescription: string;
  originalPrice: number;
  dealPrice: number;
  stock: number;
  openTime: string;
  endTime: string;
}

export interface OrderItem {
  orderItemId: number;
  productId: number;
  productName: string;
  price: number;
  quantity: number;
  orderPrice: number;
}

export interface OrderSummary {
  orderId: number;
  status: string;
  totalPrice: number;
  itemCount: number;
  createdAt: string;
}

export interface OrderDetail {
  orderId: number;
  memberId: number;
  status: string;
  orderItems: OrderItem[];
  totalPrice: number;
  createdAt: string;
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
