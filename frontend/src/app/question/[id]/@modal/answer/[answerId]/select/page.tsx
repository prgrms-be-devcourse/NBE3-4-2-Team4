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
    const response = await client.PUT("/api/questions/{id}/select/{answerId}", {
      params: {
        path: {
          id: parseInt(id),
          answerId: parseInt(answerId),
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
          <DialogTitle>정말 이 답변을 채택하시겠습니까?</DialogTitle>
          <DialogDescription>
            채택된 답변자에게 포인트가 지급됩니다.
            <br />
            채택 후 새로운 답변을 다시 채택할 수 없습니다.
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
          <Button variant="default" onClick={onSelectAnswer}>
            채택
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
