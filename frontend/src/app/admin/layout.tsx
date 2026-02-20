"use client";

import { useEffect, useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import Link from "next/link";
import { toast } from "sonner";
import { LayoutDashboard, Package, Zap, ShoppingBag, LogOut } from "lucide-react";
import { authApi } from "@/lib/auth-api";
import { memberApi } from "@/lib/member-api";

const NAV_ITEMS = [
  { href: "/admin", label: "대시보드", icon: LayoutDashboard, exact: true },
  { href: "/admin/products", label: "상품 관리", icon: Package, exact: false },
  { href: "/admin/deals", label: "딜 관리", icon: Zap, exact: false },
  { href: "/admin/orders", label: "주문 관리", icon: ShoppingBag, exact: false },
];

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const pathname = usePathname();
  const [checking, setChecking] = useState(true);

  useEffect(() => {
    memberApi.getMyProfile()
      .then(() => setChecking(false))
      .catch(() => {
        toast.error("로그인이 필요합니다.");
        router.push("/login");
      });
  }, [router]);

  const handleLogout = async () => {
    await authApi.logout();
    router.push("/login");
  };

  if (checking) {
    return (
      <div className="flex min-h-screen items-center justify-center text-gray-400">
        확인 중...
      </div>
    );
  }

  return (
    <div className="flex min-h-screen">
      {/* 사이드바 */}
      <aside className="w-56 bg-gray-900 text-white flex flex-col">
        <div className="h-16 flex items-center px-6 border-b border-gray-700">
          <span className="font-bold text-lg">⚡ 관리자</span>
        </div>
        <nav className="flex-1 py-4 space-y-1 px-3">
          {NAV_ITEMS.map(({ href, label, icon: Icon, exact }) => {
            const active = exact ? pathname === href : pathname.startsWith(href);
            return (
              <Link
                key={href}
                href={href}
                className={`flex items-center gap-3 px-3 py-2 rounded-md text-sm transition-colors ${
                  active
                    ? "bg-gray-700 text-white"
                    : "text-gray-400 hover:bg-gray-800 hover:text-white"
                }`}
              >
                <Icon size={16} />
                {label}
              </Link>
            );
          })}
        </nav>
        <div className="p-3 border-t border-gray-700">
          <button
            onClick={handleLogout}
            className="flex items-center gap-3 px-3 py-2 rounded-md text-sm text-gray-400 hover:bg-gray-800 hover:text-white transition-colors w-full"
          >
            <LogOut size={16} />
            로그아웃
          </button>
        </div>
      </aside>

      {/* 메인 콘텐츠 */}
      <main className="flex-1 bg-gray-50 overflow-auto">
        {children}
      </main>
    </div>
  );
}
