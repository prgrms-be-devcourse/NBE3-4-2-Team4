import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"
import { cn } from "@/lib/utils"

// 쪽지 알림 숫자 스타일 정의
const RedDotCircleVariants = cva(
  "inline-flex items-center justify-center rounded-full bg-red-500 text-white text-[11px] font-semibold w-5 h-5", // 동그란 빨간 배지 스타일
  {
    variants: {
      variant: {
        default: "bg-red-500 text-white",
      },
    },
    defaultVariants: {
      variant: "default",
    },
  }
)

export interface NotificationBadgeProps
  extends React.HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof RedDotCircleVariants> {}

function RedDotCircle({ className, variant, ...props }: NotificationBadgeProps) {
  return (
    <div className={cn(RedDotCircleVariants({ variant }), className)} {...props} />
  )
}

export { RedDotCircle, RedDotCircleVariants }
