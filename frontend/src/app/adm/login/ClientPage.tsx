"use client";
import { useState } from "react";
import { useRouter} from "next/navigation";
import type { components } from "@/lib/backend/apiV1/schema";

type AdminLoginRequestDto = components["schemas"]["AdminLoginRequestDto"];

export default function ClientPage() {
    const router = useRouter();
    const [formData, setFormData] = useState<AdminLoginRequestDto>({
        adminUsername: "",
        password: "",
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        console.log("전송 데이터:", formData); // 확인용 로그

        try {
            const response = await fetch("http://localhost:8080/api/admin/login", {
                method: "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(formData),
            });

            if (!response.ok) {
                const errorData = await response.json();
                console.error("로그인 실패:", errorData);
                throw new Error("로그인 실패");
            }

            const data = await response.json();
            console.log("로그인 성공:", data);

            router.push("/adm");
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <div className="flex-1 flex flex-col justify-center items-center p-4">
            <h2 className="text-xl mb-4">관리자 로그인</h2>
            <form onSubmit={handleSubmit} className="flex flex-col gap-4 w-80">
                <input
                    type="text"
                    name="adminUsername"
                    placeholder="관리자 아이디"
                    value={formData.adminUsername}
                    onChange={handleChange}
                    className="p-2 border rounded"
                />
                <input
                    type="password"
                    name="password"
                    placeholder="비밀번호"
                    value={formData.password}
                    onChange={handleChange}
                    className="p-2 border rounded"
                />
                <button type="submit" className="p-2 bg-blue-500 text-white rounded">
                    로그인
                </button>
            </form>
        </div>
    );
}