"use client";

import { useState, useEffect, useCallback } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import type { components } from "@/lib/backend/apiV1/schema";
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "@/components/ui/select";
import { Input } from "@/components/ui/input";
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
import PaginationType1Responsive from "@/lib/business/components/PaginationType1Responsive";
import client from "@/lib/backend/client";
import { useToast } from "@/hooks/use-toast";

// 검색 옵션과 매핑 (검색 시 사용하는 select)
const SEARCH_OPTIONS = ["전체", "상품명", "카테고리"] as const;
const OPTION_MAPPING: Record<string, string> = {
  전체: "ALL",
  상품명: "NAME",
  카테고리: "CATEGORY",
};

export default function ClientPage({
                                     itemPage: initialItemPage,
                                     categories,
                                     saleStates,
                                   }: {
  itemPage: components["schemas"]["PageDtoGetItem"];
  categories: string[];
  saleStates: string[];
}) {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { toast } = useToast();

  // 서버에서 받아온 초기 상품 데이터 상태
  const [itemPage, setItemPage] = useState(initialItemPage);

  // 필터 및 검색 상태
  const [categoryFilter, setCategoryFilter] = useState("전체");
  const [saleStateFilter, setSaleStateFilter] = useState("ALL");
  const [selectedOption, setSelectedOption] = useState("전체");
  const [searchKeywordType, setSearchKeywordType] = useState(
      searchParams.get("search_keyword_type") || "ALL"
  );
  const [searchKeyword, setSearchKeyword] = useState(
      searchParams.get("search_keyword") || ""
  );

  // 삭제 관련 상태
  const [isDeleting, setIsDeleting] = useState(false);
  const [selectedProductId, setSelectedProductId] = useState<number | null>(null);

  // 필터(카테고리, 판매 상태)가 변경되면 URL의 page 파라미터를 1로 초기화 --
  useEffect(() => {
    const params = new URLSearchParams(searchParams.toString());
    params.set("page", "1");
    router.replace(`?${params.toString()}`);
  }, [categoryFilter, saleStateFilter]);

  // URL(query string) 또는 필터/검색 값이 변경되면 상품 목록 재조회 --
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const queryParams = new URLSearchParams(searchParams.toString());

        // 판매 상태 필터 적용: saleStateFilter가 ALL이 아니면 해당 값 설정
        if (saleStateFilter !== "ALL") {
          queryParams.set("sale_state_keyword", saleStateFilter);
        } else {
          queryParams.delete("sale_state_keyword");
        }

        // 카테고리 필터 적용: "전체"가 아니라면 URL에 반영
        if (categoryFilter !== "전체") {
          queryParams.set("category_keyword", categoryFilter);
        } else {
          queryParams.delete("category_keyword");
        }

        // 검색 필터 적용
        if (searchKeywordType !== "ALL") {
          queryParams.set("search_keyword_type", searchKeywordType);
        } else {
          queryParams.delete("search_keyword_type");
        }
        if (searchKeyword.trim()) {
          queryParams.set("search_keyword", searchKeyword);
        } else {
          queryParams.delete("search_keyword");
        }

        // 기본 페이지 및 페이지 사이즈 설정
        if (!queryParams.get("page")) {
          queryParams.set("page", "1");
        }
        if (!queryParams.get("page_size")) {
          queryParams.set("page_size", "12");
        }

        const response = await client.GET("/api/products", {
          params: { query: Object.fromEntries(queryParams.entries()) },
        });
        if (!response.data) throw new Error("상품을 가져오는 데 실패했습니다.");
        setItemPage(response.data);
      } catch (error) {
        console.error("상품 목록 불러오는 중 오류 발생:", error);
      }
    };

    fetchProducts();
  }, [
    searchParams.toString(),
    saleStateFilter,
    categoryFilter,
    searchKeyword,
    searchKeywordType,
  ]);

  // 검색 실행 함수 (검색어 입력 후 Enter 또는 버튼 클릭 시)
  const handleSearch = useCallback(() => {
    const params = new URLSearchParams(searchParams.toString());
    params.set("page", "1");
    params.set("page_size", "12");

    if (searchKeywordType && searchKeywordType !== "ALL") {
      params.set("search_keyword_type", searchKeywordType);
    } else {
      params.delete("search_keyword_type");
    }
    if (searchKeyword.trim()) {
      params.set("search_keyword", searchKeyword);
    } else {
      params.delete("search_keyword");
    }
    router.push(`?${params.toString()}`);
  }, [searchParams, router, searchKeyword, searchKeywordType]);

  // 카테고리 선택 변경 시 URL 업데이트 및 상태 변경
  const handleCategoryChange = useCallback(
      (value: string) => {
        setCategoryFilter(value);
        const params = new URLSearchParams(searchParams.toString());
        params.set("page", "1");
        if (value !== "전체") {
          params.set("category_keyword", value);
        } else {
          params.delete("category_keyword");
        }
        router.push(`?${params.toString()}`);
      },
      [searchParams, router]
  );

  // 판매 상태 선택 변경 시 URL 업데이트 및 상태 변경
  const handleSaleStateChange = useCallback(
      (value: string) => {
        setSaleStateFilter(value);
        const params = new URLSearchParams(searchParams.toString());
        params.set("page", "1");
        if (value !== "ALL") {
          params.set("sale_state_keyword", value);
        } else {
          params.delete("sale_state_keyword");
        }
        router.push(`?${params.toString()}`);
      },
      [searchParams, router]
  );

  // 삭제 기능
  const handleDelete = async () => {
    if (!selectedProductId) return;
    setIsDeleting(true);
    try {
      const response = await client.DELETE(`/api/products/${selectedProductId}`);
      if (response.error) throw new Error("상품 삭제 실패");
      toast({ title: "상품이 성공적으로 삭제되었습니다!" });
      router.refresh();
    } catch (error) {
      toast({ title: "상품 삭제 중 오류가 발생했습니다.", variant: "destructive" });
    } finally {
      setIsDeleting(false);
      setSelectedProductId(null);
    }
  };

  // 서버에서 받아온 데이터를 그대로 사용 (이미 서버측 필터링 적용됨)
  const normalizedItemPage: components["schemas"]["PageDtoGetItem"] = {
    currentPageNumber: itemPage.data.current_page_number,
    pageSize: itemPage.data.page_size,
    totalPages: itemPage.data.total_pages,
    totalItems: itemPage.data.total_items,
    hasMore: itemPage.data.has_more,
    items: itemPage.data.items,
  };

  // URL에서 page 파라미터를 제거한 문자열 (Pagination 컴포넌트에 전달)
  const filteredSearchParams = () => {
    const queryParams = new URLSearchParams(searchParams.toString());
    queryParams.delete("page");
    return queryParams.toString();
  };

  return (
      <div className="container mx-auto px-4">
        {/* 헤더 */}
        <div className="mt-20 mb-10 text-center">
          <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
            상품 목록
          </h2>
        </div>

        {/* 상품 등록 버튼 */}
        <div className="flex flex-wrap items-center gap-4 mb-4">
          <div className="flex items-center ml-auto">
            <Button asChild>
              <Link href="/adm/products/new">상품 등록</Link>
            </Button>
          </div>
        </div>

        {/* 검색 및 필터링 영역 */}
        <div className="flex flex-wrap items-center gap-4 mb-4">
          {/* 카테고리 선택 */}
          <div className="flex items-center">
            <label htmlFor="category" className="mr-2 font-medium">
              카테고리:
            </label>
            <Select onValueChange={handleCategoryChange} value={categoryFilter}>
              <SelectTrigger className="md:w-[180px] w-[100px]" id="category">
                <SelectValue placeholder="카테고리 선택" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="전체">전체</SelectItem>
                {categories.map((cat, index) => (
                    <SelectItem key={index} value={cat}>
                      {cat}
                    </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          {/* 판매 상태 선택 */}
          <div className="flex items-center">
            <label htmlFor="saleState" className="mr-2 font-medium">
              판매 상태:
            </label>
            <Select onValueChange={handleSaleStateChange} value={saleStateFilter}>
              <SelectTrigger className="md:w-[180px] w-[100px]" id="saleState">
                <SelectValue placeholder="판매 상태 선택" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ALL">ALL</SelectItem>
                {saleStates.map((state, index) => (
                    <SelectItem key={index} value={state}>
                      {state}
                    </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          {/* 검색 타입 및 검색어 */}
          <div className="flex gap-2 ml-auto">
            <Select
                value={selectedOption}
                onValueChange={(value) => {
                  setSelectedOption(value);
                  setSearchKeywordType(OPTION_MAPPING[value]);
                }}
            >
              <SelectTrigger className="md:w-[180px] w-[100px]" id="searchType">
                <SelectValue placeholder="전체" />
              </SelectTrigger>
              <SelectContent>
                {SEARCH_OPTIONS.map((option, index) => (
                    <SelectItem key={index} value={option}>
                      {option}
                    </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <Input
                type="text"
                placeholder="검색어를 입력하세요"
                value={searchKeyword}
                onChange={(e) => setSearchKeyword(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && handleSearch()}
            />
            <Button onClick={handleSearch}>검색</Button>
          </div>
        </div>

        {/* 상품 테이블 영역 */}
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
              {normalizedItemPage.items && normalizedItemPage.items.length > 0 ? (
                  normalizedItemPage.items.map((item) => (
                      <TableRow key={item.product_id}>
                        <TableCell className="text-center">{item.product_id}</TableCell>
                        <TableCell className="text-center">{item.product_name}</TableCell>
                        <TableCell className="text-center">
                          {item.product_price.toLocaleString()}원
                        </TableCell>
                        <TableCell>
                          <div className="line-clamp-5">{item.product_description}</div>
                        </TableCell>
                        <TableCell className="text-center">{item.product_category}</TableCell>
                        <TableCell className="text-center">{item.product_sale_state}</TableCell>
                        <TableCell className="text-center">
                          <div className="flex gap-2">
                            <Button variant="outline" asChild>
                              <Link href={`/adm/products/edit/${item.product_id}`}>수정</Link>
                            </Button>
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
                                  해당 상품을 삭제하면 되돌릴 수 없습니다. 정말 삭제하시겠습니까?
                                </DialogDescription>
                                <div className="flex justify-end gap-4 mt-4">
                                  <Button variant="outline" onClick={() => setSelectedProductId(null)}>
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
                  ))
              ) : (
                  <TableRow>
                    <TableCell colSpan={7} className="text-center py-4">
                      상품이 존재하지 않습니다.
                    </TableCell>
                  </TableRow>
              )}
            </TableBody>
          </Table>
        </div>
        <PaginationType1Responsive
            className="my-6"
            baseQueryString={filteredSearchParams()}
            totalPages={normalizedItemPage.totalPages}
            currentPageNumber={normalizedItemPage.currentPageNumber}
        />
      </div>
  );
}