import { useTheme } from "next-themes";

import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { MessageSquareText } from "lucide-react";
import client from "@/lib/backend/client";
import { toast } from "@/hooks/use-toast";
import { Badge } from "@/components/ui/badge";

export default function NameToggleButton({
  recipientId,
  name,
  variant = undefined,
  icon: Icon,
}: {
  recipientId: number;
  name: string;
  variant:
    | "default"
    | "destructive"
    | "outline"
    | "secondary"
    | null
    | undefined;
  icon?: React.ElementType;
}) {
  const onClick = async () => {
    try {
      const response = await client.POST("/api/notifications/{recipientId}", {
        params: {
          path: {
            recipientId: recipientId,
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

      toast({
        title: response.data.msg,
      });
    } catch (error) {
      console.error("알림 전송 중 오류 발생:", error);
      alert("알림 전송 중 오류가 발생했습니다.");
    }
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Badge variant={variant} className="cursor-pointer">
          {Icon && <Icon className="mr-2 h-4 w-4" />}
          {name}
        </Badge>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuItem onClick={onClick}>
          <Button variant="link" className="w-full justify-start p-0 h-auto">
            <MessageSquareText /> 채팅하기
          </Button>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
