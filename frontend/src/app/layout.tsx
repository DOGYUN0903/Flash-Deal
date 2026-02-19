import type { Metadata } from "next";
import { Geist } from "next/font/google";
import { Toaster } from "@/components/ui/sonner";
import "./globals.css";

const geist = Geist({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Flash Deal",
  description: "한정 수량 특가 구매 플랫폼",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <body className={`${geist.className} antialiased bg-gray-50 min-h-screen`}>
        {children}
        <Toaster richColors position="top-center" />
      </body>
    </html>
  );
}
