"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import type { components } from "@/lib/backend/apiV1/schema";
import ProductList from "@/app/shop/list/ProductList";
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "@/components/ui/select";
import client from '@/lib/backend/client';
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";

export default function ClientPage({
                                     page,
                                     pageSize,
                                     itemPage,
                                     categories,
                                   }: {
  page: number;
  pageSize: number;
  itemPage: components["schemas"]["PageDtoGetItem"];
  categories: string[];
}) {
  const router = useRouter();
  const searchParams = useSearchParams();

  // 상품 목록 상태 (카테고리 변경 시 업데이트)
  const [filteredItemPage, setFilteredItemPage] = useState(itemPage);
  const [categoryValue, setCategoryValue] = useState("전체");

  // 검색 상태
  const [keywordType, setKeywordType] = useState(
      searchParams.get("keyword_type") || "ALL"
  );
  const [keyword, setKeyword] = useState(
      searchParams.get("keyword") || ""
  );

  // 검색 키워드 옵션
  const options = ["전체", "상품명", "카테고리"];
  const [selectedOption, setSelectedOption] = useState(options[0]);

  const optionMapping: { [key: string]: string } = {
    전체: "ALL",
    상품명: "NAME",
    카테고리: "CATEGORY",
  };

  // 상품 검색 실행 함수
  const handleSearch = () => {
    const queryParams = new URLSearchParams();
    queryParams.set("page", "1");
    queryParams.set("pageSize", "12");

    if (keywordType) queryParams.set("keyword_type", keywordType);
    if (keyword) queryParams.set("keyword", keyword);

    router.push(`?${queryParams.toString()}`);
  }

  // 상품 검색 API 호출
  useEffect(() => {
    const fetchProductsByKeyword = async () => {
      try {
        const response = await client.GET(`/api/products`, {
          params: {
            query: Object.fromEntries(searchParams.entries()), // 🔹 URL 쿼리 스트링을 그대로 API 요청에 반영
          },
          headers: {
            "Content-Type": "application/json",
          },
        });

        if (!response.data) {
          throw new Error("검색 결과를 불러오는 데 실패했습니다.");
        }

        setFilteredItemPage(response.data!);
      } catch (error) {
        console.error("검색 중 오류 발생:", error);
      }
    };

    fetchProductsByKeyword();
  }, [searchParams]); // searchParams 변경 감지 → 검색 즉시 반영


  // 카테고리 선택 시 API 호출
  useEffect(() => {
    const fetchProductsByCategory = async () => {
      try {
        const categoryParam = categoryValue === "전체" ? "" : `${categoryValue}`;

        // client.GET을 이용한 API 호출
        const response = await client.GET(`/api/products/categories`, {
          params: {
            query: {
              category_keyword: categoryParam,
              page: page,
              pageSize: pageSize,
            }
          },
          headers: {
            "Content-Type": "application/json",
          },
        });

        if (!response.data) {
          throw new Error("상품을 가져오는 데 실패했습니다.");
        }

        setFilteredItemPage(response.data!); // API 응답을 새로운 상품 목록으로 설정
      } catch (error) {
        console.error("카테고리별 상품을 불러오는 중 오류 발생:", error);
      }
    };

    if (categoryValue !== "전체") {
      fetchProductsByCategory();
    } else {
      setFilteredItemPage(itemPage); // "전체" 선택 시 기존 상품 목록 유지
    }
  }, [categoryValue, page, pageSize]); // categoryValue가 변경될 때 실행

  return (
      <>
        <div className="container mx-auto px-4">
          <div className="mt-20 mb-10 text-center">
            <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
              포인트 쇼핑
            </h2>
            <p className="text-md text-gray-400 mt-3">
              적립한 포인트로 특별한 혜택을 누리세요
            </p>
          </div>

          {/* 카테고리 선택 창 */}
          <div className="flex mb-4 md:flex-row flex-col gap-2 md:items-center items-start justify-between">
            <div className="flex gap-2">
              <Select onValueChange={(value) => setCategoryValue(value)} value={categoryValue}>
                <SelectTrigger className="md:w-[180px] w-[100px]" id="category">
                  <SelectValue placeholder="카테고리로 검색" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="전체">전체</SelectItem>
                  {categories.map((category, index) => (
                      <SelectItem key={index} value={category}>
                        {category}
                      </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* 검색 입력창 */}
            <div className="flex gap-2">
              <div>
                <Select
                    value={selectedOption}
                    onValueChange={(value) => {
                      setSelectedOption(value);
                      setKeywordType(optionMapping[value]);
                    }}
                >
                  <SelectTrigger className="md:w-[180px] w-[100px]" id="category">
                    <SelectValue placeholder="전체" />
                  </SelectTrigger>
                  <SelectContent>
                    {options.map((option, index) => (
                        <SelectItem key={index} value={option}>
                          {option}
                        </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <Input
                  type="text"
                  placeholder="검색어을 입력하세요"
                  value={keyword}
                  onChange={(e) => setKeyword(e.target.value)}
              />
              <Button onClick={handleSearch}>검색</Button>
            </div>
          </div>

          {/* 상품 리스트 (필터링된 데이터 적용) */}
          <ProductList page={page} pageSize={pageSize} itemPage={filteredItemPage} />
        </div>
      </>
  );
}