"use client";
import { useState, useEffect } from "react";
import type { components } from "@/lib/backend/apiV1/schema";
import { useToast } from "@/hooks/use-toast";
import client from "@/lib/backend/client";
import { z } from "zod";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import {
  DialogHeader,
  DialogFooter,
  DialogTrigger,
  Dialog,
  DialogContent,
  DialogTitle,
  DialogDescription,
  DialogClose,
} from "@/components/ui/dialog";
import React from "react";
import { useFileUploader } from "@/lib/business/components/FileUploader";
import MyEditor from "@/lib/business/components/MyEditor";
import { getUplodableInputAccept } from "@/utils/uplodableInputAccept";
import Link from "next/link";
import { FileUploadField } from "@/lib/business/components/FileUploadField";

type CategoryDto = components["schemas"]["QuestionCategoryDto"];
type QuestionDto = components["schemas"]["QuestionDto"];

interface Props {
  categories: CategoryDto[];
  questionData: QuestionDto;
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

export default function ClientPage({ categories, questionData }: Props) {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState(
    questionData.categoryName
  );

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [amounts, setAmounts] = useState<number>(0);
  const [categoryId, setCategoryId] = useState<number>(0);
  const [uploadedImages, setUploadedImages] = React.useState<EnhancedFile[]>(
    []
  );
  const { uploadFiles } = useFileUploader({ entityType: "questions" });

  const { toast } = useToast();
  const router = useRouter();
  const categoryOrder = [
    "전체",
    "건강",
    "경제",
    "교육",
    "스포츠",
    "여행",
    "음식",
    "취업",
    "IT",
    "기타",
  ];

  useEffect(() => {
    if (!questionData || !categories || categories.length === 0) return;

    // categoryName으로 categories 배열에서 해당 카테고리 찾기
    const category = categories.find(
      (c) => c.name === questionData.categoryName
    );
    if (category) {
      setCategoryId(category.id!!);
      setSelectedOption(questionData.categoryName);
    }

    setTitle(questionData.title);
    setContent(questionData.content);
    setAmounts(questionData.amount);
  }, []);

  const form = useForm<QuestionFormInputs>({
    resolver: zodResolver(questionFormSchema),
    defaultValues: {
      title: questionData.title,
      content: questionData.content,
      amount: questionData.amount,
      assetType:
        questionData.assetType === "전체" ? "포인트" : questionData.assetType,
    },
  });

  const onSubmit = async (data: QuestionFormInputs) => {
    try {
      const response = await client.PUT("/api/questions/{id}", {
        params: { path: { id: Number(questionData.id) } },
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

      // 에디터 이미지와 첨부파일 각각 업로드
      if (uploadedImages && uploadedImages.length > 0) {
        await uploadFiles(uploadedImages, Number(questionData.id), "body");
      }

      if (data.attachment_0 && data.attachment_0.length > 0) {
        await uploadFiles(
          data.attachment_0,
          Number(questionData.id),
          "attachment"
        );
      }

      toast({
        title: response.data.msg,
      });
      router.replace(`/question/${questionData.id}`);
    } catch (error) {
      toast({
        title: "질문 수정 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  };

  return (
    <div className="container mx-auto px-4">
      <div className="mt-20 mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          질문 수정
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
                value={categoryId?.toString()}
                onValueChange={(value) => {
                  const selectedCategory = categories.find(
                    (c) => c.id === Number(value)
                  );
                  if (selectedCategory) {
                    setSelectedOption(selectedCategory.name!!);
                    setCategoryId(Number(value));
                  }
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

            {/* 포인트 설정 */}
            <FormField
              control={form.control}
              name="amount"
              render={({ field }) => (
                <FormItem>
                  <Label>{questionData.assetType}</Label>
                  <FormControl>
                    <Input
                      {...field}
                      type="number"
                      onChange={(e) => field.onChange(Number(e.target.value))}
                      autoComplete="off"
                      className="w-[100px]"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          <div className="flex gap-2 items-end my-4">
            <div className="flex-1">
              <FileUploadField control={form.control} name="attachment_0" />
            </div>
            <Button variant="outline" asChild>
              <Link href={`/question/${questionData.id}/genFile/listForEdit`}>
                기존 첨부파일 변경/삭제
              </Link>
            </Button>
          </div>

          {/* 작성 버튼 */}
          <Dialog>
            <DialogTrigger asChild>
              <Button type="button" className="mt-6">
                수정하기
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>답변 수정</DialogTitle>
                <DialogDescription>
                  정말 이 답변을 수정하시겠습니까?
                </DialogDescription>
              </DialogHeader>
              <DialogFooter className="gap-2">
                <DialogClose asChild>
                  <Button variant="outline" type="button">
                    취소
                  </Button>
                </DialogClose>
                <Button
                  variant="default"
                  onClick={form.handleSubmit(onSubmit)}
                  type="button"
                >
                  수정
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </form>
      </Form>
    </div>
  );
}
