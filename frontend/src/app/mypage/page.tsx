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

export default function MyPage() {
  const router = useRouter();
  const [profile, setProfile] = useState<MemberProfile | null>(null);
  const [loading, setLoading] = useState(true);

  // 회원정보 수정
  const [name, setName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [updating, setUpdating] = useState(false);

  // 비밀번호 변경
  const [currentPassword, setCurrentPassword] = useState("");
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

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setUpdating(true);
    try {
      const res = await memberApi.updateMyProfile({ name, phoneNumber });
      setProfile(res.data);
      toast.success("회원정보가 수정되었습니다.");
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
      await memberApi.changePassword({ currentPassword, newPassword });
      toast.success("비밀번호가 변경되었습니다.");
      setCurrentPassword("");
      setNewPassword("");
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
              <p className="text-sm text-gray-500">이메일</p>
              <p className="font-medium">{profile.email}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">보유 잔액</p>
              <p className="font-medium text-blue-600">
                {profile.balance.toLocaleString("ko-KR")}원
              </p>
            </div>
          </CardContent>
        </Card>

        {/* 회원정보 수정 */}
        <Card>
          <CardHeader>
            <CardTitle>회원정보 수정</CardTitle>
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
              <Button type="submit" disabled={updating}>
                {updating ? "수정 중..." : "수정하기"}
              </Button>
            </form>
          </CardContent>
        </Card>

        {/* 비밀번호 변경 */}
        <Card>
          <CardHeader>
            <CardTitle>비밀번호 변경</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleChangePassword} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="currentPassword">현재 비밀번호</Label>
                <Input
                  id="currentPassword"
                  type="password"
                  value={currentPassword}
                  onChange={(e) => setCurrentPassword(e.target.value)}
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="newPassword">새 비밀번호</Label>
                <Input
                  id="newPassword"
                  type="password"
                  placeholder="대소문자+숫자+특수문자 포함 8자 이상"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  required
                />
              </div>
              <Button type="submit" disabled={changingPw}>
                {changingPw ? "변경 중..." : "비밀번호 변경"}
              </Button>
            </form>
          </CardContent>
        </Card>

      </div>
    </div>
  );
}
