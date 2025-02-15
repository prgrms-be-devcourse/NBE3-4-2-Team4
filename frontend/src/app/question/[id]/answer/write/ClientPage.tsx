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

const answerWriteFormSchema = z.object({
  content: z
    .string()
    .min(1, "내용을 입력해주세요.")
    .min(4, "내용은 4자 이상이여야 합니다."),
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

  const MyEditor = () => {
    return (
      <Editor
        apiKey={process.env.NEXT_PUBLIC_TINYMCE_API_KEY}
        initialValue=""
        onEditorChange={(content) => {
          form.setValue("content", content);
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
              const formData = new FormData();
              formData.append("file", blobInfo.blob());
              formData.append("maxWidth", "1024");
              formData.append("maxHeight", "1024");
              formData.append("quality", "0.8");

              const response = await fetch("/api/upload/image", {
                method: "POST",
                body: formData,
              });

              if (!response.ok) {
                throw new Error("Upload failed");
              }

              const { imageUrl } = await response.json();
              return imageUrl;
            } catch (error) {
              console.error("Image upload failed:", error);
              toast({
                title: "이미지 업로드 실패",
                description: "이미지 업로드 중 오류가 발생했습니다.",
                variant: "destructive",
              });
              throw error;
            }
          },
        }}
      />
    );
  };

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
          title: response.error.msg,
          variant: "destructive",
        });
        return;
      }

      toast({
        title: response.data.msg,
      });
      router.replace(`/question/${id}`);
    } catch (error) {
      toast({
        title: "답변 등록 중 오류가 발생했습니다",
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
                    <FormControl>
                      {/* <Textarea
                        {...field}
                        placeholder="내용을 입력해주세요"
                        autoComplete="off"
                        rows={20}
                        autoFocus
                      /> */}
                      <MyEditor />
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
