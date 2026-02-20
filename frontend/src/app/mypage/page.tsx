"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import Header from "@/components/layout/Header";
import { memberApi } from "@/lib/member-api";
import { MemberProfile } from "@/lib/types";

type Step = "idle" | "verify-for-profile" | "verify-for-password" | "edit-profile" | "edit-password";

export default function MyPage() {
  const router = useRouter();
  const [profile, setProfile] = useState<MemberProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [step, setStep] = useState<Step>("idle");

  // 비밀번호 확인 단계
  const [verifyPassword, setVerifyPassword] = useState("");
  const [verifiedPassword, setVerifiedPassword] = useState(""); // 검증 완료된 현재 비밀번호
  const [verifying, setVerifying] = useState(false);

  // 회원정보 수정
  const [name, setName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [updating, setUpdating] = useState(false);

  // 비밀번호 변경
  const [newPassword, setNewPassword] = useState("");
  const [changingPw, setChangingPw] = useState(false);

  useEffect(() => {
    memberApi
      .getMyProfile()
      .then((res) => {
        setProfile(res.data);
        setName(res.data.name);
        setPhoneNumber(res.data.phoneNumber);
      })
      .catch(() => {
        toast.error("로그인이 필요합니다.");
        router.push("/login");
      })
      .finally(() => setLoading(false));
  }, [router]);

  const handleVerify = async (e: React.FormEvent) => {
    e.preventDefault();
    setVerifying(true);
    try {
      await memberApi.verifyPassword(verifyPassword);
      setVerifiedPassword(verifyPassword);
      setVerifyPassword("");
      setStep(step === "verify-for-profile" ? "edit-profile" : "edit-password");
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "비밀번호가 일치하지 않습니다.");
    } finally {
      setVerifying(false);
    }
  };

  const handleCancel = () => {
    setVerifyPassword("");
    setVerifiedPassword("");
    setNewPassword("");
    setStep("idle");
  };

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setUpdating(true);
    try {
      const res = await memberApi.updateMyProfile({ name, phoneNumber });
      setProfile(res.data);
      toast.success("회원정보가 수정되었습니다.");
      setStep("idle");
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "수정에 실패했습니다.");
    } finally {
      setUpdating(false);
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setChangingPw(true);
    try {
      await memberApi.changePassword({ currentPassword: verifiedPassword, newPassword });
      toast.success("비밀번호가 변경되었습니다.");
      setNewPassword("");
      setVerifiedPassword("");
      setStep("idle");
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "비밀번호 변경에 실패했습니다.");
    } finally {
      setChangingPw(false);
    }
  };

  if (loading) {
    return <div className="flex min-h-screen items-center justify-center text-gray-400">불러오는 중...</div>;
  }

  if (!profile) return null;

  return (
    <div>
      <Header />
      <div className="max-w-2xl mx-auto px-4 py-8 space-y-6">

        {/* 프로필 요약 */}
        <Card>
          <CardHeader>
            <CardTitle>내 정보</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-2 gap-4">
            <div>
              <p className="text-sm text-gray-500">이름</p>
              <p className="font-medium">{profile.name}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">이메일</p>
              <p className="font-medium">{profile.email}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">연락처</p>
              <p className="font-medium">{profile.phoneNumber}</p>
            </div>
          </CardContent>
        </Card>

        {/* 초기 상태: 카드 두 개 세로 배치 */}
        {step === "idle" && (
          <>
            <Card
              className="cursor-pointer hover:bg-gray-50 active:bg-gray-100 active:scale-[0.99] transition-all"
              onClick={() => setStep("verify-for-profile")}
            >
              <CardContent className="flex items-center justify-between py-1">
                <p className="font-medium">회원정보 수정</p>
                <p className="text-sm text-gray-500">이름, 연락처를 변경합니다.</p>
              </CardContent>
            </Card>

            <Card
              className="cursor-pointer hover:bg-gray-50 active:bg-gray-100 active:scale-[0.99] transition-all"
              onClick={() => setStep("verify-for-password")}
            >
              <CardContent className="flex items-center justify-between py-1">
                <p className="font-medium">비밀번호 변경</p>
                <p className="text-sm text-gray-500">계정 비밀번호를 변경합니다.</p>
              </CardContent>
            </Card>
          </>
        )}

        {/* 비밀번호 확인 단계 */}
        {(step === "verify-for-profile" || step === "verify-for-password") && (
          <Card>
            <CardHeader>
              <CardTitle className="text-base">
                현재 비밀번호 확인
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-sm text-gray-500 mb-4">
                {step === "verify-for-profile" ? "회원정보 수정" : "비밀번호 변경"}을 위해 현재 비밀번호를 입력해주세요.
              </p>
              <form onSubmit={handleVerify} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="verifyPassword">현재 비밀번호</Label>
                  <Input
                    id="verifyPassword"
                    type="password"
                    value={verifyPassword}
                    onChange={(e) => setVerifyPassword(e.target.value)}
                    autoFocus
                    required
                  />
                </div>
                <div className="flex gap-2">
                  <Button type="submit" disabled={verifying}>
                    {verifying ? "확인 중..." : "확인"}
                  </Button>
                  <Button type="button" variant="ghost" onClick={handleCancel}>
                    취소
                  </Button>
                </div>
              </form>
            </CardContent>
          </Card>
        )}

        {/* 회원정보 수정 폼 */}
        {step === "edit-profile" && (
          <Card>
            <CardHeader>
              <CardTitle className="text-base">회원정보 수정</CardTitle>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleUpdateProfile} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="name">이름</Label>
                  <Input
                    id="name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="phoneNumber">연락처</Label>
                  <Input
                    id="phoneNumber"
                    placeholder="010-0000-0000"
                    value={phoneNumber}
                    onChange={(e) => setPhoneNumber(e.target.value)}
                    required
                  />
                </div>
                <div className="flex gap-2">
                  <Button type="submit" disabled={updating}>
                    {updating ? "수정 중..." : "수정하기"}
                  </Button>
                  <Button type="button" variant="ghost" onClick={handleCancel}>
                    취소
                  </Button>
                </div>
              </form>
            </CardContent>
          </Card>
        )}

        {/* 비밀번호 변경 폼 */}
        {step === "edit-password" && (
          <Card>
            <CardHeader>
              <CardTitle className="text-base">비밀번호 변경</CardTitle>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleChangePassword} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="newPassword">새 비밀번호</Label>
                  <Input
                    id="newPassword"
                    type="password"
                    placeholder="대소문자+숫자+특수문자 포함 8자 이상"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    autoFocus
                    required
                  />
                </div>
                <div className="flex gap-2">
                  <Button type="submit" disabled={changingPw}>
                    {changingPw ? "변경 중..." : "비밀번호 변경"}
                  </Button>
                  <Button type="button" variant="ghost" onClick={handleCancel}>
                    취소
                  </Button>
                </div>
              </form>
            </CardContent>
          </Card>
        )}

      </div>
    </div>
  );
}
