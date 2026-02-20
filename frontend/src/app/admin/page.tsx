"use client";

import Link from "next/link";
import { Package, Zap, ShoppingBag } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";

const MENU_ITEMS = [
  {
    href: "/admin/products",
    icon: Package,
    label: "상품 관리",
    description: "상품 등록, 수정, 삭제",
  },
  {
    href: "/admin/deals",
    icon: Zap,
    label: "딜 관리",
    description: "선착순 딜 등록",
  },
  {
    href: "/admin/orders",
    icon: ShoppingBag,
    label: "주문 관리",
    description: "전체 주문 목록 조회",
  },
];

export default function AdminDashboard() {
  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold mb-2">대시보드</h1>
      <p className="text-gray-500 mb-8">Flash Deal 관리자 페이지입니다.</p>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {MENU_ITEMS.map(({ href, icon: Icon, label, description }) => (
          <Link key={href} href={href}>
            <Card className="hover:bg-white hover:shadow-md active:scale-[0.99] transition-all cursor-pointer">
              <CardContent className="flex items-center gap-4 py-6 px-5">
                <div className="p-3 bg-gray-100 rounded-lg">
                  <Icon size={22} className="text-gray-700" />
                </div>
                <div>
                  <p className="font-semibold">{label}</p>
                  <p className="text-sm text-gray-500">{description}</p>
                </div>
              </CardContent>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  );
}
