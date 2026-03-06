"use client";

import Link from "next/link";
import { Zap } from "lucide-react";
import { useState, useEffect } from "react";

const MESSAGES = [
  "⚡ 지금 선착순 플래시 딜 진행 중!",
  "🔥 한정 수량 특가 — 놓치면 끝",
  "⏰ 실시간 재고 소진 중 — 지금 바로 확인하세요",
];

export default function AnnouncementBar() {
  const [index, setIndex] = useState(0);
  const [visible, setVisible] = useState(true);

  useEffect(() => {
    const interval = setInterval(() => {
      setVisible(false);
      setTimeout(() => {
        setIndex((i) => (i + 1) % MESSAGES.length);
        setVisible(true);
      }, 300);
    }, 4000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="bg-black text-white text-xs py-2 px-4 text-center">
      <Link href="/deals" className="flex items-center justify-center gap-2 hover:opacity-80 transition-opacity">
        <Zap size={12} className="fill-yellow-400 text-yellow-400 shrink-0" />
        <span
          className="transition-opacity duration-300"
          style={{ opacity: visible ? 1 : 0 }}
        >
          {MESSAGES[index]}
        </span>
        <span className="underline underline-offset-2 shrink-0">딜 보러가기 →</span>
      </Link>
    </div>
  );
}
