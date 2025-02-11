"use client";

import { useId } from "@/context/IdContext.tsx";

export default function MyPage() {
    const { id } = useId();

    return (
        <div>
            <h1>My Page</h1>
            {id ? (
                <p>사용자 ID: {id}</p>
            ) : (
                <p>사용자 정보가 없습니다. 로그인해주세요.</p>
            )}
        </div>
    );
}