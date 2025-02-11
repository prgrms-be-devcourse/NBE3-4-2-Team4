"use client";

import { use } from "react";

import { useRouter } from "next/navigation";

import client from "@/lib/backend/client";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

import { useToast } from "@/hooks/use-toast";

export default function Page({ params }: { params: Promise<{ id: string }> }) {
  const router = useRouter();
  const { toast } = useToast();

  const { id } = use(params);

  const handleDelete = async () => {
    //if (!window.confirm("정말 삭제하시겠습니까?")) return;

    try {
      const response = await client.DELETE("/api/questions/{id}", {
        params: { path: { id: Number(id) } },
      });

      if (response.error) {
        toast({
          title: response.error.msg,
          variant: "destructive",
        });
        return;
      }

      toast({
        title: response.data.msg,
      });

      // toast({
      //   title: "삭제되었습니다.",
      // }); // 삭제 완료 후 알림

      router.back();
      setTimeout(() => {
        router.replace(`/question/list`);
      }, 100); // 이전 페이지로 이동
    } catch (error) {
      toast({
        title: "질문 삭제 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  };

  return (
    <Dialog
      open
      onOpenChange={() => {
        router.back();
      }}
    >
      <DialogContent>
        <DialogHeader>
          <DialogTitle>질문 삭제</DialogTitle>
          <DialogDescription>
            정말 이 질문을 삭제하시겠습니까?
          </DialogDescription>
        </DialogHeader>
        <DialogFooter className="gap-2">
          <Button
            variant="outline"
            onClick={() => {
              router.back();
            }}
          >
            취소
          </Button>
          <Button variant="destructive" onClick={handleDelete}>
            삭제
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
