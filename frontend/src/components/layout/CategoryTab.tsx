"use client";

import { cn } from "@/lib/utils";

export const PRODUCT_CATEGORIES = [
  { label: "전체", value: null },
  { label: "전자기기", value: "ELECTRONICS" },
  { label: "패션", value: "FASHION" },
  { label: "식품", value: "FOOD" },
  { label: "스포츠", value: "SPORTS" },
  { label: "뷰티", value: "BEAUTY" },
  { label: "가구", value: "FURNITURE" },
  { label: "도서", value: "BOOKS" },
];

interface CategoryTabProps {
  activeCategory?: string | null;
  onCategoryChange?: (category: string | null) => void;
}

export default function CategoryTab({ activeCategory, onCategoryChange }: CategoryTabProps) {
  return (
    <div className="border-b bg-white">
      <div className="max-w-7xl mx-auto px-4 overflow-x-auto">
        <div className="flex gap-0 w-max sm:w-auto">
          {PRODUCT_CATEGORIES.map((cat) => {
            const isActive =
              cat.value === null
                ? activeCategory == null || activeCategory === ""
                : activeCategory === cat.value;
            return (
              <button
                key={cat.label}
                onClick={() => onCategoryChange?.(cat.value)}
                className={cn(
                  "px-5 py-3 text-sm font-medium border-b-2 transition-colors whitespace-nowrap",
                  isActive
                    ? "border-black text-black"
                    : "border-transparent text-gray-500 hover:text-black"
                )}
              >
                {cat.label}
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
}
