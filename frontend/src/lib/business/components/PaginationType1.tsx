import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationLink,
} from "@/components/ui/pagination";

export interface PaginationProps {
    className?: string;
    baseQueryString: string;
    totalPages: number;
    currentPageNumber: number;
    paginationArmSize?: number;
}

interface UsePaginationProps {
    baseQueryString: string;
    totalPages: number;
    currentPageNumber: number;
    paginationArmSize: number;
}

interface PaginationResult {
    pageButtonUrl: (pageNumber: number) => string;
    prevEllipsisButtonPageNumber: number | undefined;
    nextEllipsisButtonPageNumber: number | undefined;
    middlePages: number[];
}

function createPageButtonUrl(baseQueryString: string) {
    return (pageNumber: number) => `?page=${pageNumber}&${baseQueryString}`;
}

function calculatePrevEllipsisNumber(
    currentPageNumber: number,
    paginationArmSize: number,
) {
    return currentPageNumber - paginationArmSize - 1 > 2
        ? currentPageNumber - paginationArmSize - 1
        : undefined;
}

function calculateNextEllipsisNumber(
    currentPageNumber: number,
    paginationArmSize: number,
    totalPages: number,
) {
    return currentPageNumber + paginationArmSize + 1 < totalPages - 1
        ? currentPageNumber + paginationArmSize + 1
        : undefined;
}

function generateMiddlePages(
    totalPages: number,
    currentPageNumber: number,
    paginationArmSize: number,
    prevEllipsisButtonPageNumber: number | undefined,
    nextEllipsisButtonPageNumber: number | undefined,
): number[] {
    const isInCurrentRange = (pageNum: number) =>
        pageNum >= currentPageNumber - paginationArmSize &&
        pageNum <= currentPageNumber + paginationArmSize;

    const isInStartRange = (pageNum: number) =>
        !prevEllipsisButtonPageNumber && pageNum <= 2;

    const isInEndRange = (pageNum: number) =>
        !nextEllipsisButtonPageNumber && pageNum >= totalPages - 1;

    return Array.from({ length: totalPages }, (_, i) => i + 1).filter(
        (pageNum) =>
            pageNum > 1 &&
            pageNum < totalPages &&
            (isInCurrentRange(pageNum) ||
                isInStartRange(pageNum) ||
                isInEndRange(pageNum)),
    );
}

function usePagination({
                           baseQueryString,
                           totalPages,
                           currentPageNumber,
                           paginationArmSize,
                       }: UsePaginationProps): PaginationResult {
    const pageButtonUrl = createPageButtonUrl(baseQueryString);

    const prevEllipsisButtonPageNumber = calculatePrevEllipsisNumber(
        currentPageNumber,
        paginationArmSize,
    );

    const nextEllipsisButtonPageNumber = calculateNextEllipsisNumber(
        currentPageNumber,
        paginationArmSize,
        totalPages,
    );

    const middlePages = generateMiddlePages(
        totalPages,
        currentPageNumber,
        paginationArmSize,
        prevEllipsisButtonPageNumber,
        nextEllipsisButtonPageNumber,
    );

    return {
        pageButtonUrl,
        prevEllipsisButtonPageNumber,
        nextEllipsisButtonPageNumber,
        middlePages,
    };
}

function PaginationNumber({
                              pageNumber,
                              currentPageNumber,
                              pageButtonUrl,
                          }: {
    pageNumber: number;
    currentPageNumber: number;
    pageButtonUrl: (page: number) => string;
}) {
    return (
        <PaginationItem>
            <PaginationLink
                href={pageButtonUrl(pageNumber)}
                isActive={pageNumber === currentPageNumber}
            >
                {pageNumber}
            </PaginationLink>
        </PaginationItem>
    );
}

function EllipsisButton({
                            pageNumber,
                            pageButtonUrl,
                        }: {
    pageNumber: number;
    pageButtonUrl: (page: number) => string;
}) {
    return (
        <PaginationLink href={pageButtonUrl(pageNumber)}>
            <PaginationEllipsis />
        </PaginationLink>
    );
}

export default function PaginationType1({
                                            className,
                                            baseQueryString,
                                            totalPages,
                                            currentPageNumber,
                                            paginationArmSize = 1,
                                        }: PaginationProps) {
    const {
        pageButtonUrl,
        prevEllipsisButtonPageNumber,
        nextEllipsisButtonPageNumber,
        middlePages,
    } = usePagination({
        baseQueryString,
        totalPages,
        currentPageNumber,
        paginationArmSize,
    });

    if (totalPages <= 1) return null;

    return (
        <Pagination className={className}>
            <PaginationContent>
                <PaginationNumber
                    pageNumber={1}
                    currentPageNumber={currentPageNumber}
                    pageButtonUrl={pageButtonUrl}
                />

                {prevEllipsisButtonPageNumber && (
                    <EllipsisButton
                        pageNumber={prevEllipsisButtonPageNumber}
                        pageButtonUrl={pageButtonUrl}
                    />
                )}

                {middlePages.map((pageNum) => (
                    <PaginationNumber
                        key={pageNum}
                        pageNumber={pageNum}
                        currentPageNumber={currentPageNumber}
                        pageButtonUrl={pageButtonUrl}
                    />
                ))}

                {nextEllipsisButtonPageNumber && (
                    <EllipsisButton
                        pageNumber={nextEllipsisButtonPageNumber}
                        pageButtonUrl={pageButtonUrl}
                    />
                )}

                <PaginationNumber
                    pageNumber={totalPages}
                    currentPageNumber={currentPageNumber}
                    pageButtonUrl={pageButtonUrl}
                />
            </PaginationContent>
        </Pagination>
    );
}