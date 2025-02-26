"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { useRouter } from "next/navigation";
import { components } from "@/lib/backend/apiV1/schema";
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
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { useToast } from "@/hooks/use-toast";
import { getUplodableInputAccept } from "@/utils/uplodableInputAccept";
import { FilePreview } from "@/lib/business/components/FilePreview";

const editFormSchema = z.object({
  file: z.instanceof(File, { message: "파일을 선택해주세요." }),
});

type EditFormInputs = z.infer<typeof editFormSchema>;
type EntityType = "answers" | "questions";

interface GenFileModifyProps {
  genFile: components["schemas"]["GenFileDto"];
  parentId: number;
  entityType: EntityType;
}

export function GenFileModify({
  genFile,
  parentId,
  entityType,
}: GenFileModifyProps) {
  const router = useRouter();
  const { toast } = useToast();

  const form = useForm<EditFormInputs>({
    resolver: zodResolver(editFormSchema),
  });

  const onSubmit = async (data: EditFormInputs) => {
    const formData = new FormData();
    formData.append("file", data.file);

    const response = await client.PUT(
      `/api/${entityType}/{parentId}/genFiles/{id}`,
      {
        params: {
          path: {
            parentId,
            id: genFile.id,
          },
        },
        body: formData as any,
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
      <DialogContent className="max-h-[100dvh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>파일 수정</DialogTitle>
          <DialogDescription>{genFile.originalFileName}</DialogDescription>
        </DialogHeader>

        <FilePreview genFile={genFile} />

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="file"
              render={({ field: { onChange, ...field } }) => (
                <FormItem>
                  <FormLabel>새 파일</FormLabel>
                  <FormControl>
                    <Input
                      type="file"
                      accept={getUplodableInputAccept()}
                      onChange={(e) => {
                        const file = e.target.files?.[0];
                        if (file) onChange(file);
                      }}
                      {...field}
                      value={undefined}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <DialogFooter className="gap-2">
              <Button
                type="button"
                variant="outline"
                onClick={() => router.back()}
              >
                취소
              </Button>
              <Button type="submit" disabled={form.formState.isSubmitting}>
                {form.formState.isSubmitting ? "수정 중..." : "수정"}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
