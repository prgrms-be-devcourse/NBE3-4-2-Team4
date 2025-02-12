"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import Link from "next/link";
import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { components } from "@/lib/backend/apiV1/schema";
import client from "@/lib/backend/client";
import PaginationType1Responsive from "@/lib/business/components/PaginationType1Responsive";
import { useRouter } from "next/navigation";
import { useToast } from "@/hooks/use-toast";

export default function ClientPage({
  page,
  pageSize,
  itemPage,
}: {
  page: number;
  pageSize: number;
  itemPage: components["schemas"]["PageDtoGetItem"];
}) {
  const [isDeleting, setIsDeleting] = useState(false);
  const [selectedProductId, setSelectedProductId] = useState<number | null>(
    null
  );

  const router = useRouter();
  const { toast } = useToast();

  const normalizedItemPage: components["schemas"]["PageDtoGetItem"] = {
    currentPageNumber: itemPage.data.current_page_number,
    pageSize: itemPage.data.page_size,
    totalPages: itemPage.data.total_pages,
    totalItems: itemPage.data.total_items,
    hasMore: itemPage.data.has_more,
    items: itemPage.data.items,
  };

  // 삭제 기능 구현
  const handleDelete = async () => {
    if (!selectedProductId) return;

    setIsDeleting(true);

    try {
      const response = await client.DELETE(
        `/api/products/${selectedProductId}`
      );

      if (response.error) {
        throw new Error("상품 삭제 실패");
      }

      toast({
        title: "상품이 성공적으로 삭제되었습니다!",
      });

      router.refresh();

      //   alert("상품이 성공적으로 삭제되었습니다!");
      //   window.location.reload(); // 삭제 후 목록 새로고침
    } catch (error) {
      //   console.error("상품 삭제 중 오류 발생:", error);
      //   alert("상품 삭제 중 오류가 발생했습니다.");
      toast({
        title: "상품 삭제 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    } finally {
      setIsDeleting(false);
      setSelectedProductId(null);
    }
  };

  return (
    <div className="container mx-auto px-4">
      <div className="mt-20 mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          상품 목록
        </h2>
      </div>
      <div className="flex justify-end items-center mb-2">
        <Button asChild>
          <Link href="/adm/products/new">상품 등록</Link>
        </Button>
      </div>

      <div className="border border-gray-200 rounded-lg px-8 py-2 overflow-x-auto shadow-[0_0_10px_0_rgba(0,0,0,0.1)] dark:shadow-[0_0_10px_0_rgba(255,255,255,0.3)] dark:border-gray-700">
        <Table className="min-w-[1000px] w-full">
          <TableHeader>
            <TableRow>
              <TableHead className="w-[70px] text-center">ID</TableHead>
              <TableHead className="w-[250px] text-center">상품명</TableHead>
              <TableHead className="w-[120px] text-center">가격</TableHead>
              <TableHead className="text-center">설명</TableHead>
              <TableHead className="w-[120px] text-center">카테고리</TableHead>
              <TableHead className="w-[100px] text-center">판매 상태</TableHead>
              <TableHead className="w-[130px] text-center">관리</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {normalizedItemPage.items?.map((item) => (
              <TableRow key={item.product_id}>
                <TableCell className="text-center">{item.product_id}</TableCell>
                <TableCell className="text-center">
                  {item.product_name}
                </TableCell>
                <TableCell className="text-center">
                  {item.product_price.toLocaleString()}원
                </TableCell>
                <TableCell>
                  <div className="line-clamp-5">{item.product_description}</div>
                </TableCell>
                <TableCell className="text-center">
                  {item.product_category}
                </TableCell>
                <TableCell className="text-center">
                  {item.product_sale_state}
                </TableCell>
                <TableCell className="text-center">
                  <div className="flex gap-2">
                    <Button variant="outline" asChild>
                      <Link href={`/adm/products/edit/${item.product_id}`}>
                        수정
                      </Link>
                    </Button>
                    {/* 삭제 모달 버튼 */}
                    <Dialog>
                      <DialogTrigger asChild>
                        <Button
                          variant="destructive"
                          onClick={() => setSelectedProductId(item.product_id)}
                        >
                          삭제
                        </Button>
                      </DialogTrigger>
                      <DialogContent>
                        <DialogTitle>정말 삭제하시겠습니까?</DialogTitle>
                        <DialogDescription>
                          해당 상품을 삭제하면 되돌릴 수 없습니다. 정말
                          삭제하시겠습니까?
                        </DialogDescription>
                        <div className="flex justify-end gap-4 mt-4">
                          <Button
                            variant="outline"
                            onClick={() => setSelectedProductId(null)}
                          >
                            취소
                          </Button>
                          <Button
                            variant="destructive"
                            onClick={handleDelete}
                            disabled={isDeleting}
                          >
                            {isDeleting ? "삭제 중..." : "삭제"}
                          </Button>
                        </div>
                      </DialogContent>
                    </Dialog>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      <PaginationType1Responsive
        className="my-6"
        baseQueryString={`pageSize=${pageSize}`}
        totalPages={normalizedItemPage.totalPages}
        currentPageNumber={normalizedItemPage.currentPageNumber}
      />
    </div>
  );
}
