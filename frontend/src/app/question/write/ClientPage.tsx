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

type CategoryDto = components["schemas"]["QuestionCategoryDto"];

interface Props {
  categories: CategoryDto[];
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
  point: z.number().min(1, "포인트는 1 이상이여야 합니다."),
});

type QuestionFormInputs = z.infer<typeof questionFormSchema>;

export default function ClientPage({ categories }: Props) {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState(categories[0].name);

  //   const [title, setTitle] = useState("");
  //   const [content, setContent] = useState("");
  const [points, setPoints] = useState<number>(0);
  const [categoryId, setCategoryId] = useState<number | null>(null);

  const { toast } = useToast();
  const router = useRouter();

  useEffect(() => {
    if (categories.length > 0) {
      setCategoryId(categories[0].id!!); // 첫 번째 카테고리를 기본값으로 설정
      setSelectedOption(categories[0].name);
    }
  }, [categories]);

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
      point: 0,
    },
  });

  const onSubmit = async (data: QuestionFormInputs) => {
    //e.preventDefault();

    // const submitData = {
    //   title: title,
    //   content: content,
    //   categoryId: categoryId!!,
    //   point: points,
    // };

    try {
      const response = await client.POST("/api/questions", {
        headers: {
          "Content-Type": "application/json",
        },
        //body: submitData,
        body: {
          title: data.title,
          content: data.content,
          categoryId: categoryId!!,
          point: data.point,
        },
      });

      if (response.error) {
        toast({
          title: response.error.msg, // 서버에서 전달한 msg를 사용
          variant: "destructive",
        });
        return;
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
          {/* <label className="block text-lg font-semibold mb-2">제목</label>
          <input
            type="text"
            className="w-full p-2 border rounded-md mb-4 dark:bg-gray-800 dark:border-gray-700 dark:text-white"
            placeholder="제목을 입력하세요"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          /> */}

          {/* 내용 입력 */}
          <div className="my-4">
            <FormField
              control={form.control}
              name="content"
              render={({ field }) => (
                <FormItem>
                  <Label>내용</Label>
                  <FormControl>
                    <Textarea
                      {...field}
                      placeholder="내용을 입력해주세요"
                      autoComplete="off"
                      rows={20}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>
          {/* <label className="block text-lg font-semibold mb-2">내용</label>
          <textarea
            className="w-full p-2 border rounded-md h-40 resize-none mb-4 dark:bg-gray-800 dark:border-gray-700 dark:text-white"
            placeholder="내용을 입력하세요"
            value={content}
            onChange={(e) => setContent(e.target.value)}
          /> */}

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
              {/* <div className="relative">
                <button
                  type="button"
                  className="px-4 py-2 border rounded-md flex items-center justify-between w-40"
                  onClick={toggleDropdown}
                >
                  {selectedOption ? selectedOption : "선택하세요"}
                  <span className="ml-2">&#9662;</span>
                </button>
                {isOpen && (
                  <ul className="absolute bg-white border rounded shadow w-full mt-2">
                    {categories.map((category, index) => (
                      <li
                        key={index}
                        className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
                        onClick={() => {
                          selectOption(category.name!!);
                          setCategoryId(category.id!!);
                        }}
                      >
                        {category.name}
                      </li>
                    ))}
                  </ul>
                )}
              </div> */}
            </div>

            {/* 포인트 설정 */}
            <FormField
              control={form.control}
              name="point"
              render={({ field }) => (
                <FormItem>
                  <Label>포인트</Label>
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
            {/* <div>
              <label className="block text-lg font-semibold mb-2">포인트</label>
              <input
                type="number"
                min={0}
                step={10}
                className="border rounded px-4 py-2 w-40"
                placeholder="포인트 입력"
                value={points}
                onChange={(e) => setPoints(Number(e.target.value))}
              />
            </div> */}
          </div>

          {/* 작성 버튼 */}
          <Button type="submit" className="mt-6">
            작성하기
          </Button>
        </form>
      </Form>
    </div>
  );
}
