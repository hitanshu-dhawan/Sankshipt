
// Core utilities and types for building data tables (TanStack Table)
import {
    type ColumnDef,
    type ColumnFiltersState,
    type SortingState,
    type VisibilityState,
    flexRender,
    getCoreRowModel,
    getFilteredRowModel,
    getPaginationRowModel,
    getSortedRowModel,
    useReactTable,
} from "@tanstack/react-table";

// Icon components from the 'lucide-react' library
import {
    Plus,
    ArrowDownUp,
    EllipsisVertical,
    Copy,
    ChartNoAxesCombined,
    Trash2
} from 'lucide-react';

// UI component for displaying badges or tags
import { Badge } from '../components/ui/badge';

// UI component for interactive buttons
import { Button } from '../components/ui/button';

// UI components for displaying content in a card container
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';

// UI components for building dropdown menus
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from '../components/ui/dropdown-menu';

// UI component for indicating loading (spinner)
import { Spinner } from '../components/ui/spinner';

// UI components for structuring data tables
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from '../components/ui/table';

// React hooks for state and lifecycle management
import { useState, useEffect, useCallback } from 'react';

// Toast notification library
import { toast } from "sonner";

// API client for making HTTP requests
import apiClient from '../api/api-client';

// Configuration constants
import { API_SERVER_URL } from '../config';


// #region Type Definitions

interface UrlData {
    originalUrl: string;
    shortCode: string;
}

interface UrlDataWithClicks extends UrlData {
    clicks: number;
}

// #endregion


// #region DataTable Component

interface DataTableProps<TData, TValue> {
    columns: ColumnDef<TData, TValue>[]
    data: TData[]
}

function DataTable<TData, TValue>({
    columns,
    data,
}: DataTableProps<TData, TValue>) {

    const [sorting, setSorting] = useState<SortingState>([])
    const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([])
    const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({})
    const [rowSelection, setRowSelection] = useState({})

    const table = useReactTable({
        data,
        columns,
        onSortingChange: setSorting,
        onColumnFiltersChange: setColumnFilters,
        getCoreRowModel: getCoreRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
        getSortedRowModel: getSortedRowModel(),
        getFilteredRowModel: getFilteredRowModel(),
        onColumnVisibilityChange: setColumnVisibility,
        onRowSelectionChange: setRowSelection,
        state: {
            sorting,
            columnFilters,
            columnVisibility,
            rowSelection,
        },
    })

    return (
        <div className="overflow-hidden rounded-md border">
            <Table>
                <TableHeader>
                    {table.getHeaderGroups().map((headerGroup) => (
                        <TableRow key={headerGroup.id}>
                            {headerGroup.headers.map((header) => {
                                return (
                                    <TableHead key={header.id}>
                                        {header.isPlaceholder
                                            ? null
                                            : flexRender(
                                                header.column.columnDef.header,
                                                header.getContext()
                                            )}
                                    </TableHead>
                                )
                            })}
                        </TableRow>
                    ))}
                </TableHeader>
                <TableBody>
                    {table.getRowModel().rows?.length ? (
                        table.getRowModel().rows.map((row) => (
                            <TableRow
                                key={row.id}
                                data-state={row.getIsSelected() && "selected"}
                            >
                                {row.getVisibleCells().map((cell) => (
                                    <TableCell key={cell.id}>
                                        {flexRender(cell.column.columnDef.cell, cell.getContext())}
                                    </TableCell>
                                ))}
                            </TableRow>
                        ))
                    ) : (
                        <TableRow>
                            <TableCell colSpan={columns.length} className="h-24 text-center">
                                No results.
                            </TableCell>
                        </TableRow>
                    )}
                </TableBody>
            </Table>
        </div>
    )
}

// #endregion


// #region Table Columns Definition

const columns: ColumnDef<UrlDataWithClicks>[] = [
    {
        accessorKey: "shortCode",
        header: () => {
            return (
                <Button variant="ghost">
                    Short Code
                </Button>
            )
        },
        cell: ({ row }) => {
            const shortCode = row.getValue("shortCode") as string
            const shortUrl = `${API_SERVER_URL}/${shortCode}`

            return (
                <div className="space-y-1">
                    <div className="font-mono">
                        {shortCode}
                    </div>
                    <div
                        className="text-xs text-muted-foreground cursor-pointer hover:text-primary hover:underline"
                        title={shortUrl}
                        onClick={() => window.open(shortUrl, '_blank')}>
                        {shortUrl}
                    </div>
                </div>
            )
        },
    },
    {
        accessorKey: "originalUrl",
        header: ({ column }) => {
            return (
                <Button
                    variant="ghost"
                    onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}>
                    Original URL
                    <ArrowDownUp className="h-4 w-4" />
                </Button>
            )
        },
        cell: ({ row }) => {
            const url = row.getValue("originalUrl") as string

            return (
                <div className="space-y-1">
                    <div
                        className="cursor-pointer hover:text-primary hover:underline"
                        title={url}
                        onClick={() => window.open(url, '_blank')}>
                        {url.length > 60 ? `${url.substring(0, 60)}...` : url}
                    </div>
                    <div className="text-xs text-muted-foreground">
                        {URL.canParse?.(url) ? new URL(url).hostname : url.split('/')[0] || url}
                    </div>
                </div>
            )
        },
    },
    {
        accessorKey: "clicks",
        header: ({ column }) => {
            return (
                <div className="flex justify-center">
                    <Button
                        variant="ghost"
                        onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}>
                        Clicks
                        <ArrowDownUp className="h-4 w-4" />
                    </Button>
                </div>
            )
        },
        cell: ({ row }) => {
            const clicks = row.getValue("clicks") as number

            return (
                <div className="flex justify-center">
                    <Badge variant="outline">
                        {clicks}
                    </Badge>
                </div>
            )
        },
    },
    {
        id: "actions",
        cell: ({ row }) => {

            const urlData = row.original
            const shortCode = urlData.shortCode
            const shortUrl = `${API_SERVER_URL}/${shortCode}`
            const originalUrl = urlData.originalUrl

            const copyToClipboard = async (text: string) => {
                try {
                    await navigator.clipboard.writeText(text)
                    toast.success('Copied to clipboard!')
                } catch (err) {
                    console.error('Failed to copy: ', err)
                    toast.error('Failed to copy to clipboard.')
                }
            }

            const viewAnalytics = () => {
                // TODO: Navigate to detailed analytics page
                toast(`Analytics for ${shortCode}:\nClicks: ${urlData.clicks}\n\nDetailed analytics coming soon!`);
            };

            const deleteUrl = async () => {
                // TODO: Implement URL deletion logic
                toast.error('URL deletion not implemented yet.');
            };

            return (
                <DropdownMenu>

                    <DropdownMenuTrigger asChild>
                        <Button variant="ghost" className="h-8 w-8">
                            <EllipsisVertical className="h-4 w-4" />
                        </Button>
                    </DropdownMenuTrigger>

                    <DropdownMenuContent align="end">

                        {/* Copy short URL */}
                        <DropdownMenuItem onClick={() => copyToClipboard(shortUrl)}>
                            <Copy className="mr-2 h-4 w-4" />
                            Copy short URL
                        </DropdownMenuItem>

                        {/* Copy original URL */}
                        <DropdownMenuItem onClick={() => copyToClipboard(originalUrl)}>
                            <Copy className="mr-2 h-4 w-4" />
                            Copy original URL
                        </DropdownMenuItem>

                        <DropdownMenuSeparator />

                        {/* View analytics */}
                        <DropdownMenuItem onClick={viewAnalytics}>
                            <ChartNoAxesCombined className="mr-2 h-4 w-4" />
                            View analytics
                        </DropdownMenuItem>

                        <DropdownMenuSeparator />

                        {/* Delete URL */}
                        <DropdownMenuItem
                            onClick={deleteUrl}
                            variant="destructive">
                            <Trash2 className="mr-2 h-4 w-4" />
                            Delete
                        </DropdownMenuItem>

                    </DropdownMenuContent>

                </DropdownMenu>
            )
        },
    },
];

// #endregion


// #region Dashboard Component

const Dashboard = () => {

    const [urlsData, setUrlsData] = useState<UrlDataWithClicks[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const fetchUrlsWithClicks = useCallback(async (isRefresh = false) => {
        try {

            setLoading(true);

            setError(null);

            // First, fetch all URLs
            const urlsResponse = await apiClient.get<UrlData[]>('/api/urls');
            const urls = urlsResponse.data;

            if (!Array.isArray(urls)) {
                throw new Error('Invalid response format');
            }

            // Then, fetch click counts for each URL with proper error handling
            const urlsWithClicks: UrlDataWithClicks[] = await Promise.all(
                urls.map(async (url) => {
                    try {
                        const analyticsResponse = await apiClient.get<number>(
                            `/api/analytics/${url.shortCode}/count`
                        );
                        const clicks = typeof analyticsResponse.data === 'number'
                            ? analyticsResponse.data
                            : 0;

                        return {
                            ...url,
                            clicks
                        };
                    } catch (analyticsError) {
                        console.warn(`Failed to fetch analytics for ${url.shortCode}:`, analyticsError);
                        return {
                            ...url,
                            clicks: 0
                        };
                    }
                })
            );

            setUrlsData(urlsWithClicks);
        } catch (error) {
            console.error('Error fetching URLs:', error);
            setError(error instanceof Error ? error.message : 'Failed to load URLs data');
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchUrlsWithClicks();
    }, [fetchUrlsWithClicks]);

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="flex flex-col items-center gap-4">
                    <Spinner className="h-8 w-8" />
                    <p className="text-muted-foreground">Loading dashboard...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex items-center justify-center min-h-screen p-4">
                <Card className="w-full max-w-md">
                    <CardHeader>
                        <CardTitle className="text-destructive">Error</CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <p>{error}</p>
                        <Button onClick={() => fetchUrlsWithClicks()} className="w-full">
                            Try Again
                        </Button>
                    </CardContent>
                </Card>
            </div>
        );
    }

    return (
        <div className="container mx-auto py-10 px-4">
            <div className="space-y-6">
                <div className="flex items-center justify-between">
                    <div>
                        <h1 className="text-3xl font-bold">URL Dashboard</h1>
                        <p className="text-muted-foreground">
                            Manage and monitor your shortened URLs ({urlsData.length} total)
                        </p>
                    </div>
                    <div className="flex gap-2">
                        <Button>
                            <Plus className="mr-2 h-4 w-4" />
                            Create URL
                        </Button>
                    </div>
                </div>

                <Card>
                    <CardHeader>
                        <CardTitle>Your URLs</CardTitle>
                    </CardHeader>
                    <CardContent>
                        {urlsData.length === 0 ? (
                            <div className="flex flex-col items-center justify-center py-12 text-center">
                                <div className="text-muted-foreground space-y-2">
                                    <p className="text-lg">No URLs found</p>
                                    <p className="text-sm">Create your first shortened URL to get started</p>
                                </div>
                                <Button className="mt-4">
                                    <Plus className="mr-2 h-4 w-4" />
                                    Create Your First URL
                                </Button>
                            </div>
                        ) : (
                            <DataTable
                                columns={columns}
                                data={urlsData}
                            />
                        )}
                    </CardContent>
                </Card>
            </div>
        </div>
    );
};

// #endregion


export default Dashboard;
