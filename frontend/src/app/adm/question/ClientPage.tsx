"use client";
import type { components } from "@/lib/backend/apiV1/schema";
import client from "@/lib/backend/client";
import { useToast } from "@/hooks/use-toast";
import { useState } from "react";

type CategoryDto = components["schemas"]["QuestionCategoryDto"];

interface Props {
  categories: CategoryDto[];
}

export default function ClientPage({ categories }: Props) {
  const { toast } = useToast();
  const [categoryName, setCategoryName] = useState("");

  const onSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const response = await client.POST("/api/questions/categories", {
        headers: {
          "Content-Type": "application/json",
        },
        body: {
          name: categoryName,
        },
      });

      setCategoryName("");
    } catch (error) {
      toast({
        title: "카테고리 등록 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  }

  const onDelete = async (category: CategoryDto) => {
    try {
      const response = await client.DELETE(`/api/questions/categories/{id}`, {
        params: { path: { id: Number(category.id) } },
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (response.error) {
        toast({
          title: response.error.msg,
          variant: "destructive",
        });
        return;
      }

      toast({
        title: response.data.msg,
        variant: "default",
      });
    } catch (error) {
      toast({
        title: "카테고리 삭제 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  }

  return (
    <div className="flex flex-col gap-6">
      <h1 className="flex justify-center">카테고리 설정</h1>
      <form onSubmit={onSubmit} className="flex items-center space-x-4">
        <label className="text-gray-700 font-medium">카테고리 이름</label>
        <input
          type="text"
          placeholder="카테고리 입력"
          value={categoryName}
          onChange={(e) => setCategoryName(e.target.value)}
          className="flex-1 p-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <button
          type="submit"
          className="bg-gray-500 text-white font-semibold px-4 py-2 rounded-lg hover:bg-gray-600 transition"
        >
          추가
        </button>
      </form>

      <h1>카테고리 목록</h1>
      <ul className="space-y-2 mb-5">
        {categories.map((category, index) => (
          <li key={index} className="flex justify-between items-center p-2 border-b">
            <span className="text-gray-700 font-medium">{category.name}</span>
            <form onSubmit={() => onDelete(category)}>
              <button type="submit" className="text-red-500 hover:text-red-600 transition">
                삭제
              </button>
            </form>
          </li>
        ))}
      </ul>
    </div>
  );
}