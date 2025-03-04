"use client";

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

type EntityType = "answers" | "questions";

interface GenFileDeleteProps {
  parentId: number;
  genFileId: number;
  entityType: EntityType;
}

export function GenFileDelete({
  parentId,
  genFileId,
  entityType,
}: GenFileDeleteProps) {
  const router = useRouter();
  const { toast } = useToast();

  const onDelete = async () => {
    const response = await client.DELETE(
      `/api/${entityType}/{parentId}/genFiles/{id}`,
      {
        params: {
          path: {
            parentId: parentId,
            id: genFileId,
          },
        },
      }
    );

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

    sessionStorage.setItem("needToRefresh", "true");
    router.back();
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
          <DialogTitle>파일 삭제</DialogTitle>
          <DialogDescription>
            이 파일을 정말 삭제하시겠습니까?
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
          <Button variant="destructive" onClick={onDelete}>
            삭제
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
