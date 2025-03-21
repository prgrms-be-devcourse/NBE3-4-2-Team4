import { Button } from "@/components/ui/button";
import { useRouter, useSearchParams } from "next/navigation";

export interface PaginationProps {
  totalPages: number;
}

export default function Pagination2({ totalPages }: PaginationProps) {
  if (totalPages <= 1) return null;

  const router = useRouter();
  const searchParams = useSearchParams();

  const currentPage = Number(searchParams.get("page")) || 1;

  // 페이지 이동 함수
  const changePage = (newPage: number) => {
    const queryParams = new URLSearchParams();
    queryParams.set("page", newPage.toString());

    router.push(`?${queryParams.toString()}`);
  };

  return (
    <div className="flex justify-center gap-2 my-10">
      <Button
        onClick={() => changePage(currentPage - 1)}
        disabled={currentPage === 1}
        variant={currentPage === 1 ? "secondary" : "default"}
        className={`${currentPage === 1 ? "cursor-not-allowed" : ""}`}
      >
        이전
      </Button>
      <Button
        onClick={() => changePage(currentPage + 1)}
        disabled={currentPage === totalPages}
        variant={currentPage === totalPages ? "secondary" : "default"}
        className={`${currentPage === totalPages ? "cursor-not-allowed" : ""}`}
      >
        다음
      </Button>
    </div>
  );
}
