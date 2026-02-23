"use client";

import Link from "next/link";
import Image from "next/image";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { ShoppingCart, User, LogOut, Package, LogIn } from "lucide-react";
import { Button } from "@/components/ui/button";
import { authApi } from "@/lib/auth-api";

export default function Header() {
  const router = useRouter();
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    authApi.checkAuth().then(setIsLoggedIn);
  }, []);

  const handleLogout = async () => {
    await authApi.logout();
    setIsLoggedIn(false);
    router.push("/");
  };

  return (
    <header className="border-b bg-white sticky top-0 z-10">
      <div className="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
        <Link href="/products" className="flex items-center gap-2">
          <Image
            src="/images/flashdeal.png"
            alt="Flash Deal 로고"
            width={36}
            height={36}
            className="rounded-md"
          />
          <span className="text-xl font-bold">Flash Deal</span>
        </Link>
        <nav className="flex items-center gap-1">
          {isLoggedIn ? (
            <>
              <Link href="/mypage">
                <Button variant="ghost" size="sm" className="gap-2">
                  <User size={18} />
                  마이페이지
                </Button>
              </Link>
              <Link href="/orders">
                <Button variant="ghost" size="sm" className="gap-2">
                  <Package size={18} />
                  주문내역
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
            </>
          ) : (
            <>
              <Link href="/login">
                <Button variant="ghost" size="sm" className="gap-2">
                  <LogIn size={18} />
                  로그인
                </Button>
              </Link>
              <Link href="/signup">
                <Button size="sm">
                  회원가입
                </Button>
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
