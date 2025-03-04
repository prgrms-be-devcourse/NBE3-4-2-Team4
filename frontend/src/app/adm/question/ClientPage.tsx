"use client";
import type { components } from "@/lib/backend/apiV1/schema";
import client from "@/lib/backend/client";
import { useToast } from "@/hooks/use-toast";
import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { useRouter } from "next/navigation";

type CategoryDto = components["schemas"]["QuestionCategoryDto"];

interface Props {
  categories: CategoryDto[];
}

export default function ClientPage({ categories }: Props) {
  const { toast } = useToast();
  const [categoryName, setCategoryName] = useState("");
  const router = useRouter();

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

      router.refresh();
    } catch (error) {
      toast({
        title: "카테고리 등록 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  };

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
  };

  return (
    <div className="flex flex-col gap-8">
      <div>
        <Label className="text-gray-700 font-semibold text-lg mb-3 block">
          카테고리 설정
        </Label>
        <form
          onSubmit={onSubmit}
          className="flex items-center gap-2 border-b border-dashed pb-8"
        >
          <Label className="text-gray-700 font-medium w-[120px]">
            카테고리 이름
          </Label>
          <Input
            type="text"
            placeholder="카테고리 입력"
            value={categoryName}
            onChange={(e) => setCategoryName(e.target.value)}
          />
          <Button type="submit" variant="default">
            추가
          </Button>
        </form>
      </div>

      <div>
        <Label className="text-gray-700 font-semibold text-lg mb-3 block">
          카테고리 목록
        </Label>
        <ul className="mb-5">
          {categories.map((category, index) => (
            <li
              key={index}
              className="flex justify-between items-center py-2 border-b last:border-b-0"
            >
              <span className="text-gray-700 font-medium">{category.name}</span>
              <form onSubmit={() => onDelete(category)}>
                <Button type="submit" variant="destructive">
                  삭제
                </Button>
              </form>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
