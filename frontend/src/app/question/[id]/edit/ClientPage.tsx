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

type CategoryDto = components["schemas"]["QuestionCategoryDto"];
type QuestionDto = components["schemas"]["QuestionDto"];

interface Props {
  categories: CategoryDto[];
  questionData: QuestionDto;
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

export default function ClientPage({ categories, questionData }: Props) {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState(
    questionData.categoryName
  );

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [points, setPoints] = useState<number>(0);
  const [categoryId, setCategoryId] = useState<number>(0);

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

  //   useEffect(() => {
  //     const categoryIndex = categoryOrder.indexOf(questionData.categoryName);
  //     if (categories.length > 0) {
  //       if (categoryIndex !== -1) {
  //         setCategoryId(categories[categoryIndex]?.id || null);
  //         setSelectedOption(categoryOrder[categoryIndex]);
  //       }
  //       setTitle(questionData.title);
  //       setContent(questionData.content);
  //       setPoints(questionData.point);
  //     }
  //   }, [categories]);

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
    setPoints(questionData.point);
  }, []);

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
      title: questionData.title,
      content: questionData.content,
      point: questionData.point,
    },
  });

  const onSubmit = async (data: QuestionFormInputs) => {
    // e.preventDefault();

    // const submitData = {
    //   title: title,
    //   content: content,
    //   categoryId: categoryId!!,
    //   point: points,
    // };

    try {
      //if (!window.confirm(`수정하시겠습니까?`)) return;

      const response = await client.PUT("/api/questions/{id}", {
        params: { path: { id: Number(questionData.id) } },
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
            {/* <div>
              <label className="block text-lg font-semibold mb-2">
                카테고리
              </label>
              <div className="relative">
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
              </div>
            </div> */}

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

          {/* <button
            type="submit"
            className="p-3 bg-teal-500 text-white font-bold py-2 rounded-md hover:bg-teal-600 mt-6"
          >
            수정하기
          </button> */}
        </form>
      </Form>
    </div>
  );
}
