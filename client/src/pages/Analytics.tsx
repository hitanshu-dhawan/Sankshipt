// React and related libraries
import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

// UI components from Shadcn UI
import { Spinner } from '../components/ui/spinner';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Input } from '../components/ui/input';
import { Button } from '../components/ui/button';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../components/ui/table';
import { Pagination, PaginationContent, PaginationItem, PaginationLink, PaginationNext, PaginationPrevious } from '../components/ui/pagination';

// Icon components from the 'lucide-react' library
import { Copy, MousePointerClick, Calendar } from 'lucide-react';

// Toast notification library
import { toast } from "sonner";

// Date formatting library
import { format } from 'date-fns';

// API client for making HTTP requests
import apiClient from '../api/api-client';


// #region Type Definitions

interface ClicksPaginatedResponse {
    content: ClickContent[];
    pageable: {
        pageNumber: number;
        pageSize: number;
        sort: {
            empty: boolean;
            sorted: boolean;
            unsorted: boolean;
        };
        offset: number;
        paged: boolean;
        unpaged: boolean;
    };
    last: boolean;
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    sort: {
        empty: boolean;
        sorted: boolean;
        unsorted: boolean;
    };
    first: boolean;
    numberOfElements: number;
    empty: boolean;
}

interface ClickContent {
    id: number;
    shortCode: string;
    originalUrl: string;
    clickedAt: string;
    userAgent: string;
}

// #endregion


const Analytics = () => {

    // Get shortCode from URL parameters
    const { shortCode } = useParams<{ shortCode: string }>();

    // Stores paginated click history data from the API
    const [clicksData, setClicksData] = useState<ClicksPaginatedResponse | null>(null);
    // Tracks if data is currently being fetched from the server
    const [loading, setLoading] = useState(true);
    // Stores any error message that occurs during API calls
    const [error, setError] = useState<string | null>(null);
    // Tracks the current page number for pagination (0-indexed)
    const [currentPage, setCurrentPage] = useState(0);


    const copyToClipboard = async (text: string) => {
        try {
            await navigator.clipboard.writeText(text)
            toast.success('Copied to clipboard!')
        } catch (err) {
            console.error('Failed to copy: ', err)
            toast.error('Failed to copy to clipboard.')
        }
    }

    /**
     * Fetch URL data and analytics information for the given shortCode and pageNumber
     */
    const fetchAnalyticsData = async (pageNumber = 0) => {

        if (!shortCode) {
            setError('Invalid short code');
            setLoading(false);
            return;
        }

        try {
            // Reset states at the start of data fetching
            setLoading(true);
            setError(null);

            // Fetch paginated clicks
            const clicksResponse = await apiClient.get<ClicksPaginatedResponse>(`/api/analytics/${shortCode}/clicks?pageNumber=${pageNumber}&pageSize=20&sortOrder=desc`);

            // Check if response data is valid
            if (!clicksResponse.data || !clicksResponse.data.content || clicksResponse.data.content.length === 0) {
                setError('No analytics data found for this URL');
                return;
            }

            setClicksData(clicksResponse.data);
        } catch (error) {
            console.error('Error fetching analytics data:', error);
            setError(error instanceof Error ? error.message : 'Failed to load analytics data');
        } finally {
            setLoading(false);
        }
    };

    // Fetch data when component mounts
    useEffect(() => {
        fetchAnalyticsData(currentPage);
    }, [shortCode, currentPage]);

    const handlePageChange = (page: number) => {
        setCurrentPage(page);
    };


    /** Loading state */
    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="flex flex-col items-center gap-4">
                    <Spinner className="size-8" />
                    <p className="text-muted-foreground">Loading analytics...</p>
                </div>
            </div>
        );
    }

    /** Error state */
    if (error || !clicksData) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <Card className="w-full max-w-md">
                    <CardHeader className="text-center">
                        <CardTitle className="text-destructive">Something went wrong!</CardTitle>
                        <p className="text-muted-foreground text-sm mt-2">
                            {error}
                        </p>
                    </CardHeader>
                    <CardContent>
                        <Button onClick={() => fetchAnalyticsData()} className="w-full">
                            Try Again
                        </Button>
                    </CardContent>
                </Card>
            </div>
        );
    }

    /** Main analytics layout */
    return (
        <div className="container mx-auto py-8 px-4 max-w-7xl">
            <div className="space-y-8">

                {/* Header Section */}
                <div className="space-y-4">
                    <div className="space-y-2">
                        <h1 className="text-3xl font-bold">Analytics for: {shortCode}</h1>

                        {/* Original URL with copy functionality */}
                        <div className="flex items-center gap-2 max-w-4xl">
                            <Input
                                // value={urlData.originalUrl}
                                readOnly
                                className="flex-1 font-mono text-sm bg-muted"
                            />
                            <Button
                                variant="outline"
                                size="icon"
                                // onClick={() => copyToClipboard(urlData.originalUrl)}
                                className="shrink-0">
                                <Copy className="h-4 w-4" />
                            </Button>
                        </div>

                        {/* Short URL with copy functionality */}
                        <div className="flex items-center gap-2 max-w-4xl">
                            <Input
                                value={`${window.location.origin}/${shortCode}`}
                                readOnly
                                className="flex-1 font-mono text-sm bg-muted"
                            />
                            <Button
                                variant="outline"
                                size="icon"
                                onClick={() => copyToClipboard(`${window.location.origin}/${shortCode}`)}
                                className="shrink-0">
                                <Copy className="h-4 w-4" />
                            </Button>
                        </div>
                    </div>
                </div>

                {/* Key Metrics Cards */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {/* Total Clicks */}
                    <Card>
                        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                            <CardTitle className="text-sm font-medium">Total Clicks</CardTitle>
                            <MousePointerClick className="h-4 w-4 text-muted-foreground" />
                        </CardHeader>
                        <CardContent>
                            {/* <div className="text-2xl font-bold">{analyticsData.totalClicks.toLocaleString()}</div> */}
                        </CardContent>
                    </Card>

                    {/* Short Code */}
                    <Card>
                        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                            <CardTitle className="text-sm font-medium">Short Code</CardTitle>
                            <Calendar className="h-4 w-4 text-muted-foreground" />
                        </CardHeader>
                        <CardContent>
                            <div className="text-2xl font-bold font-mono">{shortCode}</div>
                        </CardContent>
                    </Card>


                </div>



                {/* Recent Clicks Table */}
                <Card>
                    <CardHeader>
                        <CardTitle>Recent Clicks</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="rounded-md border">
                            <Table>
                                <TableHeader>
                                    <TableRow>
                                        <TableHead>Clicked At</TableHead>
                                        <TableHead className="hidden md:table-cell">User Agent</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {clicksData.content.map((click) => {
                                        return (
                                            <TableRow key={click.id}>
                                                <TableCell className="font-medium">
                                                    {format(new Date(click.clickedAt), 'MMM dd, yyyy, h:mm a')}
                                                </TableCell>
                                                <TableCell className="hidden md:table-cell text-xs text-muted-foreground max-w-xs truncate">
                                                    {click.userAgent}
                                                </TableCell>
                                            </TableRow>
                                        );
                                    })}
                                </TableBody>
                            </Table>
                        </div>

                        {/* Pagination */}
                        {clicksData.totalPages > 1 && (
                            <div className="flex justify-center mt-4">
                                <Pagination>
                                    <PaginationContent>
                                        <PaginationItem>
                                            <PaginationPrevious
                                                onClick={() => handlePageChange(currentPage - 1)}
                                                className={clicksData.first ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                                            />
                                        </PaginationItem>

                                        {Array.from({ length: clicksData.totalPages }, (_, i) => (
                                            <PaginationItem key={i}>
                                                <PaginationLink
                                                    onClick={() => handlePageChange(i)}
                                                    isActive={i === currentPage}
                                                    className="cursor-pointer"
                                                >
                                                    {i + 1}
                                                </PaginationLink>
                                            </PaginationItem>
                                        ))}

                                        <PaginationItem>
                                            <PaginationNext
                                                onClick={() => handlePageChange(currentPage + 1)}
                                                className={clicksData.last ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                                            />
                                        </PaginationItem>
                                    </PaginationContent>
                                </Pagination>
                            </div>
                        )}
                    </CardContent>
                </Card>

            </div>
        </div>
    );

};

export default Analytics;
