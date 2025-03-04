"use client";
import { useState, useEffect } from "react";
import type { components } from "@/lib/backend/apiV1/schema";
import { useToast } from "@/hooks/use-toast";
import client from "@/lib/backend/client";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Input } from "@/components/ui/input";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import MyEditor from "@/lib/business/components/MyEditor";
import { getUplodableInputAccept } from "@/utils/uplodableInputAccept";
import React from "react";
import { useFileUploader } from "@/lib/business/components/FileUploader";
import { FileUploadField } from "@/lib/business/components/FileUploadField";

type CategoryDto = components["schemas"]["QuestionCategoryDto"];

interface Props {
  categories: CategoryDto[];
}

interface EnhancedFile extends File {
  uploadedUrl?: string;
  blobId?: string;
}

const questionFormSchema = z.object({
  title: z
    .string()
    .min(1, "제목을 입력해주세요.")
    .min(2, "제목은 2자 이상이여야 합니다."),
  content: z
    .string()
    .min(1, "내용을 입력해주세요.")
    .min(4, "내용은 4자 이상이여야 합니다."),
  amount: z.number().min(1, "포인트/캐시는 1 이상이여야 합니다."),
  assetType: z.enum(["포인트", "캐시"]),
  attachment_0: z.array(z.instanceof(File)).optional(),
});

type QuestionFormInputs = z.infer<typeof questionFormSchema>;

export default function ClientPage({ categories }: Props) {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState(categories[0].name);

  const [points, setPoints] = useState<number>(0);
  const [categoryId, setCategoryId] = useState<number | null>(null);

  const { toast } = useToast();
  const router = useRouter();

  const [uploadedImages, setUploadedImages] = React.useState<EnhancedFile[]>(
    []
  );

  const { uploadFiles } = useFileUploader({ entityType: "questions" });

  useEffect(() => {
    if (categories.length > 0 && categoryId === 0) {
      setCategoryId(categories[0].id!!); // 첫 번째 카테고리를 기본값으로 설정
      setSelectedOption(categories[0].name);
    }
  }, [categories, categoryId]);

  const toggleDropdown = () => {
    // 카테고리 드롭다운
    setIsOpen(!isOpen);
  };

  const selectOption = (option: string) => {
    setSelectedOption(option);
    setIsOpen(false);
  };

  const form = useForm<QuestionFormInputs>({
    resolver: zodResolver(questionFormSchema),
    defaultValues: {
      title: "",
      content: "",
      amount: 0,
      assetType: "포인트",
    },
  });

  const onSubmit = async (data: QuestionFormInputs) => {
    try {
      const response = await client.POST("/api/questions", {
        headers: {
          "Content-Type": "application/json",
        },
        body: {
          title: data.title,
          content: data.content,
          categoryId: categoryId!!,
          amount: data.amount,
          assetType: data.assetType,
        },
      });

      if (response.error) {
        toast({
          title: response.error.msg, // 서버에서 전달한 msg를 사용
          variant: "destructive",
        });
        return;
      }

      const questionId = response.data.data.item?.id!!;

      // 에디터 이미지와 첨부파일 각각 업로드
      if (uploadedImages && uploadedImages.length > 0) {
        await uploadFiles(uploadedImages, questionId, "body");
      }

      if (data.attachment_0 && data.attachment_0.length > 0) {
        await uploadFiles(data.attachment_0, questionId, "attachment");
      }

      toast({
        title: response.data.msg,
      });

      router.replace(`/question/list`);
    } catch (error) {
      toast({
        title: "질문 등록 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  };

  return (
    <div className="container mx-auto px-4">
      <div className="mt-20 mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          질문 등록
        </h2>
      </div>

      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)}>
          {/* 제목 입력 */}
          <FormField
            control={form.control}
            name="title"
            render={({ field }) => (
              <FormItem>
                <Label>제목</Label>
                <FormControl>
                  <Input
                    {...field}
                    placeholder="제목을 입력하세요"
                    autoComplete="off"
                    autoFocus
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          {/* 내용 입력 */}
          <div className="my-4">
            <FormField
              control={form.control}
              name="content"
              render={({ field }) => (
                <FormItem>
                  <Label>내용</Label>
                  <FormControl>
                    <MyEditor
                      form={form}
                      uploadedImages={uploadedImages}
                      onUploadedImagesChange={setUploadedImages}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          <div className="flex gap-2">
            {/* 카테고리 설정 */}
            <div>
              <Label>카테고리</Label>
              <Select
                value={
                  categoryId !== 0
                    ? categoryId?.toString()
                    : categories[0].id?.toString()
                }
                onValueChange={(value) => {
                  setSelectedOption(value);
                  setCategoryId(Number(value)); // value를 숫자로 변환하여 categoryId 설정
                }}
              >
                <SelectTrigger
                  className="md:w-[180px] w-[100px] mt-2"
                  id="category"
                >
                  <SelectValue placeholder="카테고리 선택" />
                </SelectTrigger>
                <SelectContent>
                  {categories.map((category, index) => (
                    <SelectItem key={index} value={category.id!!.toString()}>
                      {category.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* 포인트/캐시 설정 */}
            <div className="flex flex-col gap-2">
              <FormField
                control={form.control}
                name="assetType"
                render={({ field }) => (
                  <FormItem>
                    <div className="flex gap-3">
                      <div className="flex items-center gap-2 mt-2">
                        <input
                          type="radio"
                          {...field}
                          value="포인트"
                          checked={field.value === "포인트"}
                          onChange={() => field.onChange("포인트")}
                        />
                        <Label>포인트</Label>
                      </div>
                      <div className="flex items-center gap-2 mt-2">
                        <input
                          type="radio"
                          {...field}
                          value="캐시"
                          checked={field.value === "캐시"}
                          onChange={() => field.onChange("캐시")}
                        />
                        <Label>캐시</Label>
                      </div>
                    </div>
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="amount"
                render={({ field }) => (
                  <FormItem>
                    <FormControl>
                      <Input
                        {...field}
                        type="number"
                        onChange={(e) => field.onChange(Number(e.target.value))}
                        autoComplete="off"
                        className="w-[120px]"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
          </div>

          <FileUploadField control={form.control} name="attachment_0" />

          {/* 작성 버튼 */}
          <Button type="submit" className="mt-6">
            작성하기
          </Button>
        </form>
      </Form>
    </div>
  );
}
