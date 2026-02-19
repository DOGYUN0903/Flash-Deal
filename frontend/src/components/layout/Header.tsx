"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { ShoppingCart, User, LogOut } from "lucide-react";
import { Button } from "@/components/ui/button";
import { authApi } from "@/lib/auth-api";

export default function Header() {
  const router = useRouter();

  const handleLogout = async () => {
    await authApi.logout();
    router.push("/login");
  };

  return (
    <header className="border-b bg-white sticky top-0 z-10">
      <div className="max-w-5xl mx-auto px-4 h-16 flex items-center justify-between">
        <Link href="/deals" className="text-xl font-bold">
          ⚡ Flash Deal
        </Link>
        <nav className="flex items-center gap-1">
          <Link href="/mypage">
            <Button variant="ghost" size="sm" className="gap-2">
              <User size={18} />
              마이페이지
            </Button>
          </Link>
          <Link href="/cart">
            <Button variant="ghost" size="sm" className="gap-2">
              <ShoppingCart size={18} />
              장바구니
            </Button>
          </Link>
          <Button variant="ghost" size="sm" className="gap-2 text-red-500 hover:text-red-600 hover:bg-red-50" onClick={handleLogout}>
            <LogOut size={18} />
            로그아웃
          </Button>
        </nav>
      </div>
    </header>
  );
}
