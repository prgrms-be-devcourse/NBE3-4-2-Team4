"use client";
import client from "@/lib/backend/client";
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
  FormLabel,
} from "@/components/ui/form";
import { useEffect } from "react";
import { useToast } from "@/hooks/use-toast";
import { Textarea } from "@/components/ui/textarea";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { z } from "zod";

const messageWriteFormSchema = z.object({
  title: z.string()
    .min(1, "제목을 입력해주세요.")
    .min(4, "제목은 4자 이상이여야 합니다."),
  content: z.string()
    .min(1, "내용을 입력해주세요.")
    .min(4, "내용은 4자 이상이여야 합니다."),
});

type MessageWriteFormInputs = z.infer<typeof messageWriteFormSchema>;

interface ClientPageProps {
  user: string | null;
}

export default function ClientPage({ user }: ClientPageProps) {
  const router = useRouter();
  const { toast } = useToast();

  const form = useForm<MessageWriteFormInputs>({
    resolver: zodResolver(messageWriteFormSchema),
    defaultValues: {
      title: "",
      content: "",
    },
  });

  const onSubmit = async (data: MessageWriteFormInputs) => {
    console.log(data);
    const response = await client.POST("/api/messages", {
      body: {
        title: data.title,
        content: data.content,
        receiverName: user!!,
      }
    });

    if (response.error) {
      console.error("쪽지 전송 실패:", response.error);
      return
    } else {
      toast({
        title: "쪽지를 보냈습니다.",
        variant: "default",
      });
      router.back();
    }
  };

  useEffect(() => {
    router.refresh();
  }, []);

  return (
    <div className="container mx-auto px-4 mb-4 my-4">
      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="flex flex-col gap-4 w-full"
        >
          <Card>
            <CardHeader>
              <CardTitle className="flex justify-between items-center">
                <h1 className="text-xl font-bold -mb-2">쪽지 쓰기</h1>
                <h1>{"받는이: " + user}</h1>
              </CardTitle>
            </CardHeader>
            <CardContent>
              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem className="flex gap-4 items-center mb-3">
                    <FormLabel className="w-10 text-md mt-2">제목</FormLabel>
                    <FormControl>
                      <input {...field} 
                      placeholder="제목을 입력하세요" 
                      className="border p-2 w-full rounded-md text-sm" />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              {/* 내용 */}
              <FormField
                control={form.control}
                name="content"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-md">내용</FormLabel>
                    <FormControl>
                      <Textarea
                        {...field}
                        placeholder="내용을 입력해주세요"
                        autoComplete="off"
                        rows={15}
                        autoFocus
                      ></Textarea>
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
                {form.formState.isSubmitting ? "저장 중..." : "쪽지 보내기"}
              </Button>
            </CardFooter>
          </Card>
        </form>
      </Form>
    </div>
  );
}