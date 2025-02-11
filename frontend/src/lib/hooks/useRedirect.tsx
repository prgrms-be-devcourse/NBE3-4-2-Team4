import {useEffect} from "react";
import {useRouter} from "next/navigation";
import {useId} from "@/context/IdContext";
import {useRole} from "@/context/RoleContext";

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
        if (id === null) {
            router.push("/"); // id가 있으면 메인 페이지로 리디렉션
        }
    }, [id, router]);
}

export function useRedirectIfNotAdmin() {
    const { id } = useId();
    const { role } = useRole();
    const router = useRouter();

    useEffect(() => {
        // id가 없거나 role이 ADMIN이 아니면 리디렉션
        if (!id || role?.toString() !== "ADMIN") {
            router.push("/"); // 메인 페이지로 리디렉션
        }
    }, [id, role, router]); // id와 role이 변경될 때마다 effect가 실행됩니다.
}