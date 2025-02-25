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
import { useToast } from "@/hooks/use-toast";
import client from "@/lib/backend/client";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { Input } from "@/components/ui/input";
import { getUplodableInputAccept } from "@/utils/uplodableInputAccept";
import React from "react";
import MyEditor from "@/lib/business/components/MyEditor";
import { useFileUploader } from "@/lib/business/components/FileUploader";

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

export default function ClientPage({ params }: { params: { id: string } }) {
  const { id } = params;
  const router = useRouter();
  const { toast } = useToast();
  const form = useForm<AnswerWriteFormInputs>({
    resolver: zodResolver(answerWriteFormSchema),
    defaultValues: {
      content: "",
    },
  });

  const [uploadedImages, setUploadedImages] = React.useState<EnhancedFile[]>(
    []
  );

  const { uploadFiles } = useFileUploader({ entityType: "answers" });

  const onSubmit = async (data: AnswerWriteFormInputs) => {
    try {
      const response = await client.POST(
        "/api/questions/{questionId}/answers",
        {
          body: { content: data.content },
          params: { path: { questionId: Number(id) } },
        }
      );

      if (response.error) {
        toast({
          title: "Error",
          description: response.error.msg,
          variant: "destructive",
        });
        return;
      }

      const answerId = response.data.data.id;

      // 에디터 이미지와 첨부파일 각각 업로드
      if (uploadedImages && uploadedImages.length > 0) {
        await uploadFiles(uploadedImages, answerId, "body");
      }

      if (data.attachment_0 && data.attachment_0.length > 0) {
        await uploadFiles(data.attachment_0, answerId, "attachment");
      }

      toast({ title: response.data.msg });

      router.replace(`/question/${id}`);
    } catch (error) {
      console.error("Submit error:", error);

      toast({
        title: "Error",
        description: "답변 등록 중 오류가 발생했습니다.",
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

          <FormField
            control={form.control}
            name="attachment_0"
            render={({ field: { onChange, ...field } }) => (
              <FormItem className="my-4">
                <FormLabel>
                  첨부파일 추가 (드래그 앤 드롭 가능, 최대 5개)
                </FormLabel>
                <FormControl>
                  <Input
                    type="file"
                    multiple
                    accept={getUplodableInputAccept()}
                    onChange={(e) => {
                      const files = Array.from(e.target.files || []);
                      onChange(files);
                    }}
                    {...field}
                    value={undefined}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <div className="mt-6 flex justify-start gap-2">
            <Button type="submit" disabled={form.formState.isSubmitting}>
              {form.formState.isSubmitting ? "저장 중..." : "답변 등록"}
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
