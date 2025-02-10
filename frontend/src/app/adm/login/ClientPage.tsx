"use client";
import {useState} from "react";
import type { components } from "@/lib/backend/apiV1/schema";
import { useId } from "@/context/IdContext";
import { useNickname } from "@/context/NicknameContext";
import {useRedirectIfAuthenticated} from "@/lib/hooks/useRedirect";  // 경로는 적절히 수정

type AdminLoginRequestDto = components["schemas"]["AdminLoginRequestDto"];

export default function ClientPage() {
    useRedirectIfAuthenticated();

    const [formData, setFormData] = useState<AdminLoginRequestDto>({
        adminUsername: "",
        password: "",
    });
    const { setNickname } = useNickname();
    const { setId } = useId();
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        try {
            const response = await fetch("http://localhost:8080/api/admin/login", {
                method: "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(formData),
            });

            const data = await response.json();

            if (!response.ok) {
                console.error("로그인 실패:",data);
            }else {
                setId(data.id);
                setNickname(data.nickname);
            }

            window.location.href = "/adm";
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