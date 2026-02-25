"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const TABS = [
  { label: "상품", href: "/products" },
];

export default function CategoryTab() {
  const pathname = usePathname();

  return (
    <div className="border-b bg-white">
      <div className="max-w-7xl mx-auto px-4 flex gap-0">
        {TABS.map((tab) => {
          const isActive = pathname.startsWith(tab.href);
          return (
            <Link
              key={tab.href}
              href={tab.href}
              className={`px-6 py-3 text-sm font-medium border-b-2 transition-colors ${
                isActive
                  ? "border-black text-black"
                  : "border-transparent text-gray-500 hover:text-black"
              }`}
            >
              {tab.label}
            </Link>
          );
        })}
      </div>
    </div>
  );
}
