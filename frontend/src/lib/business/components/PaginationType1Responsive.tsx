import PaginationType1, { PaginationProps } from "./PaginationType1";

export default function PaginationType1Responsive({
                                                      className,
                                                      ...props
                                                  }: PaginationProps) {
    return (
        <>
            <PaginationType1 className={`${className} sm:hidden`} {...props} />
            <PaginationType1
                className={`${className} hidden sm:flex`}
                {...props}
                paginationArmSize={3}
            />
        </>
    );
}