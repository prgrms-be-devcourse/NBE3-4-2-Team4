"use client";

import { Button } from "@/components/ui/button";
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
import { components } from "@/lib/backend/apiV1/schema";
import client from "@/lib/backend/client";
import { getUplodableInputAccept } from "@/utils/uplodableInputAccept";
import { zodResolver } from "@hookform/resolvers/zod";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { z } from "zod";
import React from "react";
import MyEditor from "@/lib/business/components/MyEditor";
import { useFileUploader } from "@/lib/business/components/FileUploader";
import { FileUploadField } from "@/lib/business/components/FileUploadField";

interface EnhancedFile extends File {
  uploadedUrl?: string;
  blobId?: string;
}

const answerWriteFormSchema = z.object({
  content: z
    .string()
    .min(1, "내용을 입력해주세요.")
    .min(4, "내용은 4자 이상이여야 합니다."),
  attachment_0: z.array(z.instanceof(File)).optional(),
});

type AnswerWriteFormInputs = z.infer<typeof answerWriteFormSchema>;

export default function ClientPage({
  params,
  answer,
}: {
  params: { id: string; answerId: string };
  answer: components["schemas"]["AnswerDto"];
}) {
  const { id, answerId } = params;
  const router = useRouter();
  const { toast } = useToast();
  const [uploadedImages, setUploadedImages] = React.useState<EnhancedFile[]>(
    []
  );
  const form = useForm<AnswerWriteFormInputs>({
    resolver: zodResolver(answerWriteFormSchema),
    defaultValues: {
      content: answer.content,
    },
  });

  const { uploadFiles } = useFileUploader({ entityType: "answers" });

  const onSubmit = async (data: AnswerWriteFormInputs) => {
    try {
      const response = await client.PATCH("/api/answers/{id}", {
        body: {
          content: data.content,
        },
        params: {
          path: {
            id: Number(answerId),
          },
        },
      });

      if (response.error) {
        toast({
          title: response.error.msg,
          variant: "destructive",
        });
        return;
      }

      // 에디터 이미지와 첨부파일 각각 업로드
      if (uploadedImages && uploadedImages.length > 0) {
        await uploadFiles(uploadedImages, Number(answerId), "body");
      }

      if (data.attachment_0 && data.attachment_0.length > 0) {
        await uploadFiles(data.attachment_0, Number(answerId), "attachment");
      }
      toast({
        title: response.data.msg,
      });

      router.replace(`/question/${id}`);
    } catch (error) {
      toast({
        title: "답변 수정 중 오류가 발생했습니다",
        variant: "destructive",
      });
    }
  };

  return (
    <div className="container mx-auto px-4">
      <div className="mt-20 mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          답변 등록
        </h2>
      </div>
      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="flex flex-col gap-4 w-full"
        >
          <FormField
            control={form.control}
            name="content"
            render={({ field }) => (
              <FormItem>
                <FormControl>
                  <MyEditor
                    form={form}
                    uploadedImages={uploadedImages}
                    onUploadedImagesChange={setUploadedImages}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <div className="flex gap-2 items-end my-4">
            <div className="flex-1">
              <FileUploadField control={form.control} name="attachment_0" />
            </div>
            <Button variant="outline" asChild>
              <Link
                href={`/question/${answer.questionId}/answer/${answer.id}/genFile/listForEdit`}
              >
                기존 첨부파일 변경/삭제
              </Link>
            </Button>
          </div>

          <div className="mt-6 flex justify-start gap-2">
            <Button type="submit" disabled={form.formState.isSubmitting}>
              {form.formState.isSubmitting ? "저장 중..." : "답변 수정"}
            </Button>
            <Button
              variant="outline"
              type="button"
              onClick={() => router.back()}
            >
              취소
            </Button>
          </div>
        </form>
      </Form>
    </div>
  );
}
