import { Mail } from "lucide-react";
import { RedDotCircle } from "@/components/ui/red-dot-circle";
import Link from "next/link";

interface MessageNumIconProps {
  count: number;
}

const MessageNumIcon: React.FC<MessageNumIconProps> = ({ count }) => {
  return (
    <div className="relative">
      <Link href={"/message"}>
        <Mail className="mr-4 w-5 h-5" />
        {count > 0 && (
          <RedDotCircle className="absolute right-[8px] bottom-2 rounded-full w-4 h-4 flex items-center justify-center">
            {count}
          </RedDotCircle>
        )}
      </Link>
    </div>
  );
};

export default MessageNumIcon;
