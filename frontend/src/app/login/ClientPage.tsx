import { Button } from "@/components/ui/button";
import Link from "next/link";
import {useRedirectIfAuthenticated} from "@/lib/hooks/useRedirect";  // 경로는 적절히 수정

export default function ClientPage() {
    useRedirectIfAuthenticated();

    return (
        <div className="flex-1 flex flex-col justify-center items-center p-4">
            <h2 className="text-xl mb-4">로그인</h2>
            <Link href="http://localhost:8080/oauth2/authorization/kakao">
                <Button variant="link">카카오 로그인</Button>
            </Link>
            <Link href="http://localhost:8080/oauth2/authorization/naver">
                <Button variant="link">네이버 로그인</Button>
            </Link>
            <Link href="http://localhost:8080/oauth2/authorization/google">
                <Button variant="link">구글 로그인</Button>
            </Link>
        </div>
    );
}