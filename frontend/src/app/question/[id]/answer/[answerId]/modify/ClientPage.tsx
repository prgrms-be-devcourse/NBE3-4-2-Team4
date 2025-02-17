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
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { z } from "zod";

const answerWriteFormSchema = z.object({
  content: z
    .string()
    .min(1, "내용을 입력해주세요.")
    .min(4, "내용은 4자 이상이여야 합니다."),
  attachment_0: z.instanceof(File).optional(),
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
        formData.append("files", data.attachment_0);

        const uploadResponse = await client.POST(
          "/api/answers/{answerId}/genFiles/{typeCode}",
          {
            params: {
              path: {
                answerId: answer.id,
                typeCode: "attachment",
              },
            },
            body: formData,
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

              <FormField
                control={form.control}
                name="attachment_0"
                render={({ field: { onChange, ...field } }) => (
                  <FormItem>
                    <FormLabel>첨부파일</FormLabel>
                    <FormControl>
                      <Input
                        type="file"
                        onChange={(e) => {
                          const file = e.target.files?.[0];
                          onChange(file);
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
                {form.formState.isSubmitting ? "저장 중..." : "답변 수정"}
              </Button>
            </CardFooter>
          </Card>
        </form>
      </Form>
    </div>
  );
}
