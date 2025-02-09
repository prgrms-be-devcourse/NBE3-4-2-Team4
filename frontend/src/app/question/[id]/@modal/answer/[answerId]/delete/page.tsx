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

export default function Page({
  params,
}: {
  params: Promise<{ id: string; answerId: string }>;
}) {
  const router = useRouter();
  const { toast } = useToast();

  const { id, answerId } = use(params);

  const onSelectAnswer = async () => {
    const response = await client.DELETE("/api/answers/{id}", {
      params: {
        path: {
          id: parseInt(answerId),
        },
      },
    });

    if (response.error) {
      toast({
        title: response.error.msg,
        description: response.error.data.toString(),
        variant: "destructive",
      });
      return;
    }

    toast({
      title: response.data.msg,
    });

    router.replace(`/question/${id}`);
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
          <DialogTitle>답변 삭제</DialogTitle>
          <DialogDescription>
            정말 이 답변을 삭제하시겠습니까?
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
          <Button variant="destructive" onClick={onSelectAnswer}>
            삭제
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
