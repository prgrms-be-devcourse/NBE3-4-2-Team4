"use client"

import { useState } from "react";
import { components } from "@/lib/backend/apiV1/schema";
import { useNickname } from "@/context/NicknameContext";
import {useRouter} from "next/navigation";

type NicknameUpdateRequestDto = components["schemas"]["NicknameUpdateRequestDto"];

export default function ClientPage() {
    const [formData, setFormData] = useState<NicknameUpdateRequestDto>({
        newNickname: ""
    });
    const { setNickname } = useNickname();
    const router = useRouter();
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        try {
            const response = await fetch("http://localhost:8080/api/members/nickname", {
                method: "PATCH",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(formData),
            });

            if (!response.ok) {
                const errorData = await response.json();
                console.error("닉네임 수정 실패:", errorData);
            }else{
                setNickname(formData.newNickname);
            }
            router.push("/mypage")
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <div className="container mx-auto p-4 flex flex-col justify-center items-center h-screen">
            <h1 className="text-xl font-bold mb-4">닉네임 수정</h1>

            <form onSubmit={handleSubmit} className="w-full max-w-lg">
                <div className="mb-4">
                    <label htmlFor="newNickname" className="block text-sm font-medium text-gray-700">새로운 닉네임</label>
                    <input
                        type="text"
                        name="newNickname"
                        placeholder="변경할 닉네임"
                        value={formData.newNickname}  // value 속성 추가
                        onChange={handleChange}
                        className="mt-2 p-2 w-full border border-gray-300 rounded-md"
                        required
                    />
                </div>
                <button
                    type="submit"
                    className="mt-4 w-full py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                >
                    {"닉네임 수정"}
                </button>
            </form>
        </div>
    );
}