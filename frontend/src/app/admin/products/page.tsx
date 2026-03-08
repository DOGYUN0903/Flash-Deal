"use client";

import { useEffect, useRef, useState } from "react";
import { toast } from "sonner";
import { Plus, Pencil, Trash2, ImagePlus } from "lucide-react";
import Image from "next/image";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Textarea } from "@/components/ui/textarea";
import { adminApi, AdminProductSummary, AdminProductDetail } from "@/lib/admin-api";

const STATUS_LABEL: Record<string, { label: string; variant: "default" | "secondary" | "destructive" }> = {
  ON_SALE:   { label: "판매중",     variant: "default" },
  PREPARING: { label: "판매 준비중", variant: "secondary" },
  SOLD_OUT:  { label: "품절",       variant: "destructive" },
};

const CATEGORY_OPTIONS = [
  { value: "ELECTRONICS", label: "전자기기" },
  { value: "FASHION",     label: "패션" },
  { value: "FOOD",        label: "식품" },
  { value: "SPORTS",      label: "스포츠" },
  { value: "BEAUTY",      label: "뷰티" },
  { value: "FURNITURE",   label: "가구" },
  { value: "BOOKS",       label: "도서" },
];

const EMPTY_FORM = { name: "", description: "", price: "", stock: "", category: "" };

function ImagePicker({
  existingUrl,
  onFileChange,
}: {
  existingUrl?: string | null;
  onFileChange: (file: File | null) => void;
}) {
  const inputRef = useRef<HTMLInputElement>(null);
  const [preview, setPreview] = useState<string | null>(existingUrl ?? null);

  const handleFile = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] ?? null;
    onFileChange(file);
    if (file) {
      setPreview(URL.createObjectURL(file));
    }
  };

  return (
    <div className="space-y-2">
      <Label>상품 이미지</Label>
      <div
        className="relative w-full h-40 bg-gray-100 rounded-lg border-2 border-dashed border-gray-300 flex items-center justify-center cursor-pointer hover:border-gray-400 transition-colors overflow-hidden"
        onClick={() => inputRef.current?.click()}
      >
        {preview ? (
          <Image src={preview} alt="상품 이미지" fill className="object-cover rounded-lg" unoptimized />
        ) : (
          <div className="flex flex-col items-center gap-1 text-gray-400">
            <ImagePlus size={28} />
            <span className="text-sm">클릭하여 이미지 선택</span>
          </div>
        )}
      </div>
      <input ref={inputRef} type="file" accept="image/*" className="hidden" onChange={handleFile} />
    </div>
  );
}

export default function AdminProductsPage() {
  const [products, setProducts] = useState<AdminProductSummary[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);

  const [createOpen, setCreateOpen] = useState(false);
  const [createForm, setCreateForm] = useState(EMPTY_FORM);
  const [createImage, setCreateImage] = useState<File | null>(null);
  const [creating, setCreating] = useState(false);

  const [editTarget, setEditTarget] = useState<AdminProductDetail | null>(null);
  const [editForm, setEditForm] = useState(EMPTY_FORM);
  const [editImage, setEditImage] = useState<File | null>(null);
  const [editing, setEditing] = useState(false);

  const fetchProducts = (p = page) => {
    setLoading(true);
    adminApi
      .getProducts(p)
      .then((res) => {
        setProducts(res.data.content);
        setTotalPages(res.data.totalPages);
      })
      .catch(() => toast.error("상품 목록을 불러오지 못했습니다."))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchProducts(page); }, [page]);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setCreating(true);
    try {
      await adminApi.createProduct(
        {
          name: createForm.name,
          description: createForm.description,
          price: Number(createForm.price),
          stock: Number(createForm.stock),
          category: createForm.category,
        },
        createImage
      );
      toast.success("상품이 등록되었습니다.");
      setCreateOpen(false);
      setCreateForm(EMPTY_FORM);
      setCreateImage(null);
      fetchProducts(0);
      setPage(0);
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "등록에 실패했습니다.");
    } finally {
      setCreating(false);
    }
  };

  const openEdit = async (productId: number) => {
    try {
      const res = await adminApi.getProduct(productId);
      setEditTarget(res.data);
      setEditForm({
        name: res.data.name,
        description: res.data.description,
        price: String(res.data.price),
        stock: String(res.data.stockQuantity),
        category: res.data.category ?? "",
      });
      setEditImage(null);
    } catch {
      toast.error("상품 정보를 불러오지 못했습니다.");
    }
  };

  const handleEdit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editTarget) return;
    setEditing(true);
    try {
      await adminApi.updateProduct(
        editTarget.productId,
        {
          name: editForm.name,
          description: editForm.description,
          price: Number(editForm.price),
          stock: Number(editForm.stock),
        },
        editImage
      );
      toast.success("상품이 수정되었습니다.");
      setEditTarget(null);
      setEditImage(null);
      fetchProducts(page);
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "수정에 실패했습니다.");
    } finally {
      setEditing(false);
    }
  };

  const handleDelete = async (productId: number, name: string) => {
    if (!confirm(`"${name}" 상품을 삭제하시겠습니까?`)) return;
    try {
      await adminApi.deleteProduct(productId);
      toast.success("상품이 삭제되었습니다.");
      fetchProducts(page);
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "삭제에 실패했습니다.");
    }
  };

  return (
    <div className="p-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">상품 관리</h1>
        <Button className="gap-2" onClick={() => setCreateOpen(true)}>
          <Plus size={16} />
          상품 등록
        </Button>
      </div>

      {loading ? (
        <p className="text-gray-400 py-20 text-center">불러오는 중...</p>
      ) : (
        <>
          <div className="bg-white rounded-lg border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>이미지</TableHead>
                  <TableHead>상품명</TableHead>
                  <TableHead className="text-right">가격</TableHead>
                  <TableHead className="text-right">재고</TableHead>
                  <TableHead>상태</TableHead>
                  <TableHead className="text-right">관리</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {products.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={7} className="text-center text-gray-400 py-10">
                      등록된 상품이 없습니다.
                    </TableCell>
                  </TableRow>
                ) : products.map((p) => {
                  const status = STATUS_LABEL[p.status] ?? { label: p.status, variant: "secondary" as const };
                  return (
                    <TableRow key={p.productId}>
                      <TableCell className="text-gray-500">{p.productId}</TableCell>
                      <TableCell>
                        {p.imageUrl ? (
                          <div className="relative w-10 h-10 rounded overflow-hidden">
                            <Image src={p.imageUrl} alt={p.name} fill className="object-cover" />
                          </div>
                        ) : (
                          <div className="w-10 h-10 rounded bg-gray-100 flex items-center justify-center text-gray-300 text-xs">없음</div>
                        )}
                      </TableCell>
                      <TableCell className="font-medium">{p.name}</TableCell>
                      <TableCell className="text-right">{p.price.toLocaleString("ko-KR")}원</TableCell>
                      <TableCell className="text-right">{p.stockQuantity.toLocaleString("ko-KR")}</TableCell>
                      <TableCell>
                        <Badge variant={status.variant}>{status.label}</Badge>
                      </TableCell>
                      <TableCell className="text-right">
                        <div className="flex justify-end gap-2">
                          <Button size="sm" variant="outline" className="gap-1" onClick={() => openEdit(p.productId)}>
                            <Pencil size={13} />
                            수정
                          </Button>
                          <Button size="sm" variant="destructive" className="gap-1" onClick={() => handleDelete(p.productId, p.name)}>
                            <Trash2 size={13} />
                            삭제
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </div>

          {totalPages > 1 && (
            <div className="flex justify-center gap-2 mt-6">
              <Button variant="outline" size="sm" disabled={page === 0} onClick={() => setPage((p) => p - 1)}>이전</Button>
              <span className="flex items-center px-3 text-sm text-gray-600">{page + 1} / {totalPages}</span>
              <Button variant="outline" size="sm" disabled={page >= totalPages - 1} onClick={() => setPage((p) => p + 1)}>다음</Button>
            </div>
          )}
        </>
      )}

      {/* 상품 등록 모달 */}
      <Dialog open={createOpen} onOpenChange={(open) => { setCreateOpen(open); if (!open) { setCreateForm(EMPTY_FORM); setCreateImage(null); } }}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>상품 등록</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleCreate} className="space-y-4">
            <ImagePicker onFileChange={setCreateImage} />
            <div className="space-y-2">
              <Label>상품명</Label>
              <Input value={createForm.name} onChange={(e) => setCreateForm({ ...createForm, name: e.target.value })} required />
            </div>
            <div className="space-y-2">
              <Label>설명</Label>
              <Textarea rows={5} value={createForm.description} onChange={(e) => setCreateForm({ ...createForm, description: e.target.value })} />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-2">
                <Label>가격 (원)</Label>
                <Input type="number" min={0} value={createForm.price} onChange={(e) => setCreateForm({ ...createForm, price: e.target.value })} required />
              </div>
              <div className="space-y-2">
                <Label>재고</Label>
                <Input type="number" min={0} value={createForm.stock} onChange={(e) => setCreateForm({ ...createForm, stock: e.target.value })} required />
              </div>
            </div>
            <div className="space-y-2">
              <Label>카테고리</Label>
              <select
                className="w-full border rounded-md px-3 py-2 text-sm"
                value={createForm.category}
                onChange={(e) => setCreateForm({ ...createForm, category: e.target.value })}
                required
              >
                <option value="">카테고리 선택</option>
                {CATEGORY_OPTIONS.map((c) => (
                  <option key={c.value} value={c.value}>{c.label}</option>
                ))}
              </select>
            </div>
            <DialogFooter>
              <Button type="button" variant="ghost" onClick={() => setCreateOpen(false)}>취소</Button>
              <Button type="submit" disabled={creating}>{creating ? "등록 중..." : "등록"}</Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* 상품 수정 모달 */}
      <Dialog open={!!editTarget} onOpenChange={(open) => { if (!open) { setEditTarget(null); setEditImage(null); } }}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>상품 수정</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleEdit} className="space-y-4">
            <ImagePicker existingUrl={editTarget?.imageUrl} onFileChange={setEditImage} />
            <div className="space-y-2">
              <Label>상품명</Label>
              <Input value={editForm.name} onChange={(e) => setEditForm({ ...editForm, name: e.target.value })} required />
            </div>
            <div className="space-y-2">
              <Label>설명</Label>
              <Textarea rows={5} value={editForm.description} onChange={(e) => setEditForm({ ...editForm, description: e.target.value })} />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-2">
                <Label>가격 (원)</Label>
                <Input type="number" min={0} value={editForm.price} onChange={(e) => setEditForm({ ...editForm, price: e.target.value })} required />
              </div>
              <div className="space-y-2">
                <Label>재고</Label>
                <Input type="number" min={0} value={editForm.stock} onChange={(e) => setEditForm({ ...editForm, stock: e.target.value })} required />
              </div>
            </div>
            <div className="space-y-2">
              <Label>카테고리</Label>
              <select
                className="w-full border rounded-md px-3 py-2 text-sm"
                value={editForm.category}
                onChange={(e) => setEditForm({ ...editForm, category: e.target.value })}
                required
              >
                <option value="">카테고리 선택</option>
                {CATEGORY_OPTIONS.map((c) => (
                  <option key={c.value} value={c.value}>{c.label}</option>
                ))}
              </select>
            </div>
            <DialogFooter>
              <Button type="button" variant="ghost" onClick={() => setEditTarget(null)}>취소</Button>
              <Button type="submit" disabled={editing}>{editing ? "수정 중..." : "수정"}</Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
