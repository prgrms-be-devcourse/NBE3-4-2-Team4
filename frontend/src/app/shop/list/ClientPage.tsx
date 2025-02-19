"use client";

import { useState, useEffect, useCallback } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import type { components } from "@/lib/backend/apiV1/schema";
import ProductList from "@/app/shop/list/ProductList";
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "@/components/ui/select";
import client from "@/lib/backend/client";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

// 상수: 옵션 목록과 매핑
const SEARCH_OPTIONS = ["전체", "상품명", "카테고리"] as const;
const OPTION_MAPPING: Record<string, string> = {
  전체: "ALL",
  상품명: "NAME",
  카테고리: "CATEGORY",
};

export default function ClientPage({
                                     itemPage: initialItemPage,
                                     categories,
                                   }: {
  itemPage: components["schemas"]["PageDtoGetItem"];
  categories: string[];
}) {
  const router = useRouter();
  const searchParams = useSearchParams();

  // 상태 관리
  const [itemPage, setItemPage] = useState(initialItemPage);
  const [categoryValue, setCategoryValue] = useState("전체");
  const [searchKeywordType, setSearchKeywordType] = useState(
      searchParams.get("search_keyword_type") || "ALL"
  );
  const [searchKeyword, setSearchKeyword] = useState(
      searchParams.get("search_keyword") || ""
  );
  const [selectedOption, setSelectedOption] = useState("전체");

  // 제품 목록 재조회
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const queryParams = new URLSearchParams(searchParams.toString());

        // 카테고리 필터
        if (queryParams.get("category_keyword") || categoryValue !== "전체") {
          // URL에 이미 category_keyword가 있거나, 상태값이 변경된 경우
          if (categoryValue !== "전체") {
            queryParams.set("category_keyword", categoryValue);
          } else {
            queryParams.delete("category_keyword");
          }
        }

        // 검색 필터
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

        const response = await client.GET("/api/products", {
          params: { query: Object.fromEntries(queryParams.entries()) },
        });

        if (!response.data) throw new Error("상품을 가져오는 데 실패했습니다.");
        setItemPage(response.data);
      } catch (error) {
        console.error("상품 목록 불러오는 중 오류 발생:", error);
      }
    };

    // URL(query string)이 변경될 때마다 재조회
    fetchProducts();
  }, [searchParams.toString()]);

  // 검색 실행 함수 (검색어 입력 후 Enter 또는 버튼 클릭 시)
  const handleSearch = useCallback(() => {
    const params = new URLSearchParams(searchParams.toString());
    // 검색 시 페이지를 초기화
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

  // 카테고리 선택 변경 시 URL 업데이트 (즉시 페이지 1로 재설정)
  const handleCategoryChange = useCallback(
      (value: string) => {
        setCategoryValue(value);
        const params = new URLSearchParams(searchParams.toString());
        params.set("page", "1");
        params.set("page_size", "12");
        if (value !== "전체") {
          params.set("category_keyword", value);
        } else {
          params.delete("category_keyword");
        }
        router.push(`?${params.toString()}`);
      },
      [searchParams, router]
  );

  return (
      <div className="container mx-auto px-4">
        <div className="mt-20 mb-10 text-center">
          <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
            포인트 쇼핑
          </h2>
          <p className="text-md text-gray-400 mt-3">
            적립한 포인트로 특별한 혜택을 누리세요
          </p>
        </div>

        {/* 카테고리 및 검색 입력 영역 */}
        <div className="flex mb-4 md:flex-row flex-col gap-2 md:items-center items-start justify-between">
          <div className="flex gap-2">
            <Select onValueChange={handleCategoryChange} value={categoryValue}>
              <SelectTrigger className="md:w-[180px] w-[100px]" id="category">
                <SelectValue placeholder="카테고리로 검색" />
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

          <div className="flex gap-2">
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

        {/* 상품 리스트 렌더링 */}
        <ProductList itemPage={itemPage} />
      </div>
  );
}