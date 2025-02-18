"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";

import { useRouter } from "next/navigation";
import Image from "next/image";

import client from "@/lib/backend/client";

import { components } from "@/lib/backend/apiV1/schema";

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

import imageLoader from "@/utils/imageLoader";
import { getUplodableInputAccept } from "@/utils/uplodableInputAccept";

const editFormSchema = z.object({
  file: z.instanceof(File, { message: "파일을 선택해주세요." }),
});

type EditFormInputs = z.infer<typeof editFormSchema>;

export default function ClientPage({
  id,
  answerId,
  genFile,
}: {
  id: string;
  answerId: string;
  genFile: components["schemas"]["AnswerGenFileDto"];
}) {
  const router = useRouter();
  const { toast } = useToast();

  const form = useForm<EditFormInputs>({
    resolver: zodResolver(editFormSchema),
  });

  const onSubmit = async (data: EditFormInputs) => {
    const formData = new FormData();
    formData.append("file", data.file);

    const response = await client.PUT("/api/answers/{answerId}/genFiles/{id}", {
      params: {
        path: {
          answerId: parseInt(answerId),
          id: genFile.id,
        },
      },
      body: formData as any,
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
      <DialogContent className="max-w-[100dvh] max-h-[100dvh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>파일 수정</DialogTitle>
          <DialogDescription>{genFile.originalFileName}</DialogDescription>
        </DialogHeader>

        <div className="flex justify-center">
          {genFile.fileExtTypeCode == "img" && (
            <Image
              loader={imageLoader}
              src={genFile.publicUrl}
              alt={genFile.originalFileName}
              width={100}
              height={100}
              className="max-w-[80dvh] max-h-[70dvh] w-full"
            />
          )}
          {genFile.fileExtTypeCode == "audio" && (
            <audio
              src={genFile.publicUrl}
              controls
              className="max-w-[80dvh] max-h-[70dvh] w-full"
            />
          )}
          {genFile.fileExtTypeCode == "video" && (
            <video
              src={genFile.publicUrl}
              controls
              className="max-w-[80dvh] max-h-[70dvh] w-full"
            />
          )}
        </div>

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
