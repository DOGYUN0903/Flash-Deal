"use client";

import Link from "next/link";
import Image from "next/image";
import { useRouter, usePathname } from "next/navigation";
import { useEffect, useState } from "react";
import { ShoppingCart, User, LogOut, Package, LogIn, Zap } from "lucide-react";
import { Button } from "@/components/ui/button";
import { authApi } from "@/lib/auth-api";
import { cartApi } from "@/lib/cart-api";

export default function Header() {
  const router = useRouter();
  const pathname = usePathname();
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [cartCount, setCartCount] = useState(0);

  useEffect(() => {
    authApi.checkAuth().then((loggedIn) => {
      setIsLoggedIn(loggedIn);
      if (loggedIn) {
        cartApi.getCart().then((res) => setCartCount(res.data.totalQuantity)).catch(() => {});
      } else {
        setCartCount(0);
      }
    });
  }, [pathname]);

  const handleLogout = async () => {
    await authApi.logout();
    setIsLoggedIn(false);
    setCartCount(0);
    router.push("/");
  };

  return (
    <header className="border-b bg-white sticky top-0 z-10">
      <div className="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
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
          <Link href="/deals">
            <Button variant="ghost" size="sm" className="gap-2 text-yellow-600 hover:text-yellow-700 hover:bg-yellow-50">
              <Zap size={18} className="fill-yellow-400" />
              딜
            </Button>
          </Link>
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
              <div className="relative">
                <ShoppingCart size={18} />
                {isLoggedIn && cartCount > 0 && (
                  <span className="absolute -top-2 -right-2 bg-red-500 text-white text-[10px] font-bold rounded-full h-4 w-4 flex items-center justify-center leading-none">
                    {cartCount > 99 ? "99+" : cartCount}
                  </span>
                )}
              </div>
              장바구니
            </Button>
          </Link>
          {isLoggedIn ? (
            <Button variant="ghost" size="sm" className="gap-2 text-red-500 hover:text-red-600 hover:bg-red-50" onClick={handleLogout}>
              <LogOut size={18} />
              로그아웃
            </Button>
          ) : (
            <Link href="/login">
              <Button variant="ghost" size="sm" className="gap-2">
                <LogIn size={18} />
                로그인
              </Button>
            </Link>
          )}
        </nav>
      </div>
    </header>
  );
}
