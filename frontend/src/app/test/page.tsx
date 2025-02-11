"use client";

import { useId } from "@/context/IdContext.tsx";
import { useRole } from "@/context/RoleContext.tsx";

export default function MyPage() {
    const { id } = useId();
    const { role } = useRole();
    return (
        <div>
            <h1>My Page</h1>
            {role ? (
                <p>사용자 role: {role}</p>
            ) : (
                <p>사용자 정보가 없습니다. 로그인해주세요.</p>
            )}
            {id ? (
                <p>사용자 ID: {id}</p>
            ) : (
                <p>사용자 정보가 없습니다. 로그인해주세요.</p>
            )}
        </div>
    );
}