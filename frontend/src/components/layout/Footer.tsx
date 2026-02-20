import Image from "next/image";
import Link from "next/link";

export default function Footer() {
  return (
    <footer className="border-t bg-white mt-auto">
      <div className="max-w-6xl mx-auto px-4 py-10">
        {/* 메인 영역: 로고(좌) + 회사정보(우) */}
        <div className="flex flex-col md:flex-row gap-10">
          {/* 좌측: 로고 */}
          <div className="flex-shrink-0 flex items-start gap-2">
            <Image
              src="/images/flashdeal.png"
              alt="Flash Deal 로고"
              width={40}
              height={40}
              className="rounded-md"
            />
            <span className="text-lg font-bold">Flash Deal</span>
          </div>

          {/* 우측: 회사 정보 */}
          <div className="text-sm text-gray-500 space-y-1.5">
            <p>
              <span className="text-gray-700 font-medium">상호명</span> Flash Deal &nbsp;|&nbsp;
              <span className="text-gray-700 font-medium">대표자</span> 홍길동 &nbsp;|&nbsp;
              <span className="text-gray-700 font-medium">사업자등록번호</span> 000-00-00000
            </p>
            <p>
              <span className="text-gray-700 font-medium">통신판매업신고번호</span> 제2025-서울강남-0000호
            </p>
            <p>
              <span className="text-gray-700 font-medium">주소</span> 서울특별시 강남구 테헤란로 000, 00층
            </p>
            <p>
              <span className="text-gray-700 font-medium">고객센터</span> 1588-0000 &nbsp;|&nbsp;
              <span className="text-gray-700 font-medium">이메일</span> contact@flashdeal.com
            </p>
            <p>
              <span className="text-gray-700 font-medium">운영시간</span> 평일 09:00 - 18:00 (주말 및 공휴일 휴무)
            </p>
          </div>
        </div>

        {/* 하단: 저작권 + 링크 */}
        <div className="mt-8 pt-6 border-t flex flex-col md:flex-row items-start md:items-center justify-between gap-3">
          <p className="text-xs text-gray-400">© 2025 Flash Deal. All rights reserved.</p>
          <div className="flex gap-4 text-xs text-gray-400">
            <Link href="#" className="hover:text-gray-600">이용약관</Link>
            <Link href="#" className="hover:text-gray-600">개인정보처리방침</Link>
            <Link href="#" className="hover:text-gray-600">고객센터</Link>
          </div>
        </div>
      </div>
    </footer>
  );
}
