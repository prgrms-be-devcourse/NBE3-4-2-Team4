import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useId } from "@/context/IdContext";

// id가 있으면 특정 페이지로 리디렉션하는 커스텀 훅
export function useRedirectIfAuthenticated() {
    const { id } = useId();
    const router = useRouter();

    useEffect(() => {
        if (id) {
            router.push("/"); // id가 있으면 메인 페이지로 리디렉션
        }
    }, [id, router]);
}

export function useRedirectIfNotAuthenticated() {
    const { id } = useId();
    const router = useRouter();

    useEffect(() => {
        if (!id) {
            router.push("/"); // id가 있으면 메인 페이지로 리디렉션
        }
    }, [id, router]);
}