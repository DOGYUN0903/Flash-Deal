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
  imageUrl: string | null;
  category: string | null;
  reviewCount: number;
  averageRating: number | null;
}

export interface ReviewResponse {
  reviewId: number;
  memberName: string;
  rating: number;
  content: string;
  createdAt: string;
}

export interface ProductDetail {
  productId: number;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  status: string;
  imageUrl: string | null;
  category: string | null;
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


export interface DealResponse {
  dealId: number;
  productId: number;
  productName: string;
  title: string;
  originalPrice: number;
  discountPrice: number;
  remainingStock: number;
  status: string;
  startAt: string;
  endAt: string;
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

