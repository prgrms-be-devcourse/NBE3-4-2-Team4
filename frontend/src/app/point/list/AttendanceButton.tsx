import createClient from "openapi-fetch";
import { useRouter, usePathname } from "next/navigation";
import { Button } from "@/components/ui/button";
import { CheckCircle } from "lucide-react";
import { useToast } from "@/hooks/use-toast";

const client = createClient<paths>({
  baseUrl: "http://localhost:8080",
});

export default function AttendanceButton() {
  const router = useRouter();
  const pathname = usePathname();
  const { toast } = useToast();

  const handleAttend = async (e) => {
    const data = await client.PUT("/api/points/attendance", {
      credentials: "include",
    });

    if (!data.response.ok) {
      console.log(data);
      //alert(data.error.msg);

      toast({
        title: "출석 체크 실패",
        description: data.error.msg,
        variant: "destructive",
      });

      return;
    }
    //alert("출석 성공!");

    toast({
      title: "출석 체크 성공",
      description: "출석 체크를 완료했습니다.",
    });

    router.replace(pathname);
  };

  return (
    <Button
      variant="default"
      onClick={handleAttend}
      className="flex items-center gap-2"
    >
      <CheckCircle />
      출석 체크 하기
    </Button>
  );
}
