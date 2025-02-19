"use client";

import { Button } from "@/components/ui/button";
import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Textarea } from "@/components/ui/textarea";
import { useToast } from "@/hooks/use-toast";
import client from "@/lib/backend/client";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { Editor } from "@tinymce/tinymce-react";
import { Input } from "@/components/ui/input";
import { getUplodableInputAccept } from "@/utils/uplodableInputAccept";
import React from "react";

const answerWriteFormSchema = z.object({
  content: z
    .string()
    .min(1, "내용을 입력해주세요.")
    .min(4, "내용은 4자 이상이여야 합니다."),
  attachment_0: z.array(z.instanceof(File)).optional(),
});

type AnswerWriteFormInputs = z.infer<typeof answerWriteFormSchema>;

interface EnhancedFile extends File {
  uploadedUrl?: string;
}

export default function ClientPage({ params }: { params: { id: string } }) {
  const [uploadedImages, setUploadedImages] = React.useState<EnhancedFile[]>(
    []
  );

  const { id } = params;
  const router = useRouter();
  const { toast } = useToast();
  const form = useForm<AnswerWriteFormInputs>({
    resolver: zodResolver(answerWriteFormSchema),
    defaultValues: {
      content: "",
    },
  });

  const MyEditor = React.useMemo(() => {
    return (
      <Editor
        apiKey={process.env.NEXT_PUBLIC_TINYMCE_API_KEY}
        initialValue={form.getValues("content")}
        onEditorChange={(content, editor) => {
          form.setValue("content", content);

          // 현재 에디터에 있는 이미지들의 src 목록을 가져옴
          const currentImages = editor.dom.select("img").map((img) => img.src);

          // uploadedImages에서 현재 에디터에 없는 이미지들 제거
          setUploadedImages((prev) =>
            prev.filter((file) => {
              const objectUrl = URL.createObjectURL(file);
              const isInEditor = currentImages.some((src) => src === objectUrl);
              URL.revokeObjectURL(objectUrl);
              return isInEditor;
            })
          );
        }}
        init={{
          language: "ko_KR",
          height: 500,
          menubar: false,
          plugins: [
            "advlist",
            "autolink",
            "codesample",
            "emoticons",
            "lists",
            "link",
            "image",
            "charmap",
            "preview",
            "anchor",
            "searchreplace",
            "wordcount",
            "media",
            "table",
          ],
          toolbar:
            "undo redo | blocks | " +
            "bold italic underline strikethrough subscript superscript | " +
            "forecolor backcolor | " +
            "alignleft aligncenter alignright | " +
            "bullist numlist outdent indent | " +
            "codesample emoticons | link image media | table",
          file_picker_types: "file media",
          link_picker_callback: false,
          link_quicklink: true,
          media_alt_source: false,
          media_poster: false,
          images_upload_handler: async function (blobInfo, progress) {
            try {
              const imageFile = new File(
                [blobInfo.blob()],
                blobInfo.filename(),
                { type: blobInfo.blob().type }
              );
              setUploadedImages((prev) => [...prev, imageFile]);
              return URL.createObjectURL(imageFile);
            } catch (error) {
              console.error("Image upload failed:", error);
              toast({
                title: "이미지 처리 실패",
                description: "이미지 처리 중 오류가 발생했습니다.",
                variant: "destructive",
              });
              throw error;
            }
          },
        }}
      />
    );
  }, []);

  const onSubmit = async (data: AnswerWriteFormInputs) => {
    try {
      const response = await client.POST(
        "/api/questions/{questionId}/answers",
        {
          body: {
            content: data.content,
          },
          params: {
            path: {
              questionId: Number(id),
            },
          },
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

      // 파일 업로드 처리
      if (data.attachment_0) {
        const formData = new FormData();
        for (const file of [...data.attachment_0].reverse())
          formData.append("files", file);

        const uploadResponse = await client.POST(
          "/api/answers/{answerId}/genFiles/{typeCode}",
          {
            params: {
              path: {
                answerId: response.data.data.id,
                typeCode: "attachment",
              },
            },
            body: formData as any,
          }
        );

        if (uploadResponse.error) {
          toast({
            title: uploadResponse.error.msg,
            variant: "destructive",
          });

          return;
        }
      }

      toast({
        title: response.data.msg,
      });
      router.replace(`/question/${id}`);
    } catch (error) {
      console.error(error);
      toast({
        title: "Error",
        description: "답변 등록 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  };

  return (
    <div className="container mx-auto px-4 mt-20">
      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="flex flex-col gap-4 w-full"
        >
          <Card>
            <CardHeader>
              <CardTitle>
                <h2 className="text-2xl font-bold">답변 등록</h2>
              </CardTitle>
            </CardHeader>
            <CardContent>
              <FormField
                control={form.control}
                name="content"
                render={({ field }) => (
                  <FormItem>
                    <FormControl>{MyEditor}</FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="attachment_0"
                render={({ field: { onChange, ...field } }) => (
                  <FormItem className="mt-5">
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
            </CardContent>
            <CardFooter className="flex justify-end gap-2 items-center">
              <Button
                variant="outline"
                type="button"
                onClick={() => router.back()}
              >
                취소
              </Button>
              <Button type="submit" disabled={form.formState.isSubmitting}>
                {form.formState.isSubmitting ? "저장 중..." : "답변 등록"}
              </Button>
            </CardFooter>
          </Card>
        </form>
      </Form>
    </div>
  );
}
