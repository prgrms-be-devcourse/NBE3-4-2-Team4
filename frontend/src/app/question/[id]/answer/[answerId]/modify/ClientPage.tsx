"use client";

import { Badge } from "@/components/ui/badge";
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
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { useToast } from "@/hooks/use-toast";
import { components } from "@/lib/backend/apiV1/schema";
import client from "@/lib/backend/client";
import { getUplodableInputAccept } from "@/utils/uplodableInputAccept";
import { zodResolver } from "@hookform/resolvers/zod";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { z } from "zod";

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
  const form = useForm<AnswerWriteFormInputs>({
    resolver: zodResolver(answerWriteFormSchema),
    defaultValues: {
      content: answer.content,
    },
  });

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
                answerId: answer.id,
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
      toast({
        title: "답변 수정 중 오류가 발생했습니다",
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
              <CardTitle className="flex justify-between items-center">
                <h2 className="text-2xl font-bold">답변 수정</h2>
                <Badge variant="secondary">{answer.authorName}</Badge>
              </CardTitle>
            </CardHeader>
            <CardContent>
              <FormField
                control={form.control}
                name="content"
                render={({ field }) => (
                  <FormItem>
                    <FormControl>
                      <Textarea
                        {...field}
                        placeholder="내용을 입력해주세요"
                        autoComplete="off"
                        rows={20}
                        autoFocus
                      ></Textarea>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <div className="flex gap-2 items-end">
                <div className="flex-1">
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
                </div>
                <Button variant="outline" asChild>
                  <Link
                    href={`/question/${answer.questionId}/answer/${answer.id}/genFile/listForEdit`}
                  >
                    기존 첨부파일 변경/삭제
                  </Link>
                </Button>
              </div>
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
                {form.formState.isSubmitting ? "저장 중..." : "답변 수정"}
              </Button>
            </CardFooter>
          </Card>
        </form>
      </Form>
    </div>
  );
}
