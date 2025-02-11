"use client";
import { useState, useEffect } from "react";
import type { components } from "@/lib/backend/apiV1/schema";
import client from "@/utils/apiClient";
import { useToast } from "@/hooks/use-toast";

type CategoryDto = components["schemas"]["QuestionCategoryDto"];

interface Props {
    categories: CategoryDto[];
}

export default function ClientPage({ categories }: Props) {
    const [isOpen, setIsOpen] = useState(false);
    const [selectedOption, setSelectedOption] = useState(categories[0].name);

    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [points, setPoints] = useState<number>(0);
    const [categoryId, setCategoryId] = useState<number | null>(null);

    const { toast } = useToast();

    useEffect(() => {
        if (categories.length > 0) {
          setCategoryId(categories[0].id!!); // 첫 번째 카테고리를 기본값으로 설정
          setSelectedOption(categories[0].name);
        }
      }, [categories]);

    const toggleDropdown = () => { // 카테고리 드롭다운
        setIsOpen(!isOpen);
    };

    const selectOption = (option: string) => {
        setSelectedOption(option);
        setIsOpen(false);
    };

    const handleFormSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
    
        const submitData = {
            title: title,
            content: content,
            categoryId: categoryId!!,
            point: points,
        };
    
        try {
            const response = await client.POST("/api/questions", {
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                },
                body: submitData,
            });

            if (response.error) {
                toast({
                    title: response.error.msg,  // 서버에서 전달한 msg를 사용
                    variant: "destructive",
                });
                return;
            }
            toast({
                title: response.data.msg,
            });
            window.location.href = "/question/list";
        } catch (error) {
            toast({
                title: "질문 등록 중 오류가 발생했습니다.",
                variant: "destructive",
            });
        }
    };

    return (
        <div className="container mx-auto px-4">
            <h2 className="text-2xl font-bold mb-2">글쓰기</h2>
            <hr className="mb-6" />

            <form onSubmit={handleFormSubmit}>
                {/* 제목 입력 */}
                <label className="block text-lg font-semibold mb-2">제목</label>
                <input
                    type="text"
                    className="w-full p-2 border rounded-md mb-4 dark:bg-gray-800 dark:border-gray-700 dark:text-white"
                    placeholder="제목을 입력하세요"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                />

                {/* 내용 입력 */}
                <label className="block text-lg font-semibold mb-2">내용</label>
                <textarea
                    className="w-full p-2 border rounded-md h-40 resize-none mb-4 dark:bg-gray-800 dark:border-gray-700 dark:text-white"
                    placeholder="내용을 입력하세요"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                />

                <div className="flex gap-10">
                    {/* 카테고리 설정 */}
                    <div>
                        <label className="block text-lg font-semibold mb-2">카테고리</label>
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
                                <li key={index}
                                    className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
                                    onClick={() => {
                                        selectOption(category.name!!);
                                        setCategoryId(category.id!!);
                                    }}>
                                {category.name}
                                </li>
                                ))}
                            </ul>
                            )}
                        </div>
                    </div>

                    {/* 포인트 설정 */}
                    <div>
                        <label className="block text-lg font-semibold mb-2">포인트</label>
                        <input type="number" min={0} step={10} className="border rounded px-4 py-2 w-40" 
                        placeholder="포인트 입력"
                        value={points}
                        onChange={(e) => setPoints(Number(e.target.value))}
                        />
                    </div>
                </div>

                {/* 작성 버튼 */}
                <button
                    type="submit"
                    className="p-3 bg-teal-500 text-white font-bold py-2 rounded-md hover:bg-teal-600 mt-6">
                    작성하기
                </button>
            </form>
        </div>
    );
}