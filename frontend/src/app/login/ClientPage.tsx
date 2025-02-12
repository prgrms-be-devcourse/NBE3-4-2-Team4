import { Button } from "@/components/ui/button";
import Link from "next/link";
import { useRedirectIfAuthenticated } from "@/lib/hooks/useRedirect"; // 경로는 적절히 수정
import { Globe, Mail, MessageCircle, Settings } from "lucide-react";

export default function ClientPage() {
  useRedirectIfAuthenticated();

  return (
    <div className="flex-1 flex flex-col justify-center items-center p-4">
      <Button
        className="mb-2 w-[180px] h-[40px] bg-[#FEE500] hover:bg-[#FDD000] text-black"
        asChild
      >
        <Link href="http://localhost:8080/oauth2/authorization/kakao">
          <MessageCircle size={20} />
          카카오 로그인
        </Link>
      </Button>
      <Button
        className="mb-2 w-[180px] h-[40px] bg-[#03C75A] hover:bg-[#02B150] text-white"
        asChild
      >
        <Link href="http://localhost:8080/oauth2/authorization/naver">
          <Mail size={20} />
          네이버 로그인
        </Link>
      </Button>
      <Button
        className="mb-16 bg-[#4285F4] w-[180px] h-[40px] hover:bg-[#357ABD] text-white"
        asChild
      >
        <Link href="http://localhost:8080/oauth2/authorization/google">
          <Globe size={20} />
          구글 로그인
        </Link>
      </Button>

      <Button
        className="bg-gray-800 w-[180px] h-[40px] hover:bg-gray-700 text-white"
        asChild
      >
        <Link href="http://localhost:3000/adm/login">
          <Settings size={20} />
          관리자 로그인
        </Link>
      </Button>
    </div>
  );
}
