import { useState, useEffect, useCallback } from 'react';
import apiClient from '../api/api-client';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Spinner } from '../components/ui/spinner';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Badge } from '../components/ui/badge';
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from '../components/ui/table';
import {
    DropdownMenu,
    DropdownMenuCheckboxItem,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from '../components/ui/dropdown-menu';
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
import {
    RefreshCw,
    Plus,
    ChevronDownIcon,
    ArrowUpDown,
    Copy,
    MoreHorizontal,
    Trash2,
    BarChart3,
    ExternalLink
} from 'lucide-react';

// Type definitions
interface UrlData {
    originalUrl: string;
    shortCode: string;
}

interface UrlDataWithClicks extends UrlData {
    clicks: number;
}

// DataTable component
interface DataTableProps<TData, TValue> {
    columns: ColumnDef<TData, TValue>[]
    data: TData[]
    searchKey?: string
    searchPlaceholder?: string
}

function DataTable<TData, TValue>({
    columns,
    data,
    searchKey,
    searchPlaceholder = "Filter...",
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
        <div className="w-full">
            <div className="flex items-center py-4">
                {searchKey && (
                    <Input
                        placeholder={searchPlaceholder}
                        value={(table.getColumn(searchKey)?.getFilterValue() as string) ?? ""}
                        onChange={(event) =>
                            table.getColumn(searchKey)?.setFilterValue(event.target.value)
                        }
                        className="max-w-sm"
                    />
                )}
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <Button variant="outline" className="ml-auto">
                            Columns <ChevronDownIcon className="ml-2 h-4 w-4" />
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                        {table
                            .getAllColumns()
                            .filter((column) => column.getCanHide())
                            .map((column) => {
                                return (
                                    <DropdownMenuCheckboxItem
                                        key={column.id}
                                        className="capitalize"
                                        checked={column.getIsVisible()}
                                        onCheckedChange={(value) =>
                                            column.toggleVisibility(!!value)
                                        }
                                    >
                                        {column.id}
                                    </DropdownMenuCheckboxItem>
                                )
                            })}
                    </DropdownMenuContent>
                </DropdownMenu>
            </div>
            <div className="rounded-md border">
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
                                            {flexRender(
                                                cell.column.columnDef.cell,
                                                cell.getContext()
                                            )}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell
                                    colSpan={columns.length}
                                    className="h-24 text-center"
                                >
                                    No results.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>
            <div className="flex items-center justify-end space-x-2 py-4">
                <div className="flex-1 text-sm text-muted-foreground">
                    {table.getFilteredSelectedRowModel().rows.length} of{" "}
                    {table.getFilteredRowModel().rows.length} row(s) selected.
                </div>
                <div className="space-x-2">
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={() => table.previousPage()}
                        disabled={!table.getCanPreviousPage()}
                    >
                        Previous
                    </Button>
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={() => table.nextPage()}
                        disabled={!table.getCanNextPage()}
                    >
                        Next
                    </Button>
                </div>
            </div>
        </div>
    )
}

// Table columns definition
const columns: ColumnDef<UrlDataWithClicks>[] = [
    {
        accessorKey: "shortCode",
        header: ({ column }) => {
            return (
                <Button
                    variant="ghost"
                    onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
                >
                    Short Code
                    <ArrowUpDown className="ml-2 h-4 w-4" />
                </Button>
            )
        },
        cell: ({ row }) => {
            const shortCode = row.getValue("shortCode") as string
            const baseUrl = typeof window !== 'undefined' ? window.location.origin : ''
            const shortUrl = `${baseUrl}/${shortCode}`

            return (
                <div className="space-y-1">
                    <div className="font-mono font-medium text-primary">
                        {shortCode}
                    </div>
                    <div className="text-xs text-muted-foreground truncate max-w-[200px]" title={shortUrl}>
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
                    onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
                >
                    Original URL
                    <ArrowUpDown className="ml-2 h-4 w-4" />
                </Button>
            )
        },
        cell: ({ row }) => {
            const url = row.getValue("originalUrl") as string
            const truncatedUrl = url.length > 60 ? `${url.substring(0, 60)}...` : url

            // Extract domain for better display
            let domain = '';
            try {
                domain = new URL(url).hostname;
            } catch {
                domain = url;
            }

            return (
                <div className="max-w-[400px]">
                    <div className="truncate font-medium" title={url}>
                        {truncatedUrl}
                    </div>
                    <div className="text-xs text-muted-foreground truncate">
                        {domain}
                    </div>
                </div>
            )
        },
    },
    {
        accessorKey: "clicks",
        header: ({ column }) => {
            return (
                <Button
                    variant="ghost"
                    onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
                >
                    Clicks
                    <ArrowUpDown className="ml-2 h-4 w-4" />
                </Button>
            )
        },
        cell: ({ row }) => {
            const clicks = row.getValue("clicks") as number
            return (
                <Badge variant={clicks > 0 ? "default" : "secondary"}>
                    {clicks}
                </Badge>
            )
        },
    },
    {
        id: "actions",
        enableHiding: false,
        cell: ({ row }) => {
            const urlData = row.original
            const baseUrl = window.location.origin
            const shortUrl = `${baseUrl}/${urlData.shortCode}`

            const copyToClipboard = async (text: string) => {
                try {
                    await navigator.clipboard.writeText(text)
                    alert('Copied to clipboard!')
                } catch (err) {
                    console.error('Failed to copy: ', err)
                    // Fallback for older browsers
                    const textArea = document.createElement('textarea')
                    textArea.value = text
                    document.body.appendChild(textArea)
                    textArea.select()
                    document.execCommand('copy')
                    document.body.removeChild(textArea)
                    alert('Copied to clipboard!')
                }
            }

            const openOriginalUrl = () => {
                window.open(urlData.originalUrl, '_blank')
            }

            const deleteUrl = async () => {
                const confirmDelete = window.confirm(
                    `Are you sure you want to delete the URL with short code "${urlData.shortCode}"?\n\nThis action cannot be undone.`
                );

                if (confirmDelete) {
                    try {
                        // TODO: Implement actual delete API call
                        // await apiClient.delete(`/api/urls/${urlData.shortCode}`);
                        console.log('Delete URL:', urlData.shortCode);
                        alert('URL deleted successfully! (This is a placeholder - implement actual deletion)');
                        // TODO: Refresh the data after deletion
                    } catch (error) {
                        console.error('Failed to delete URL:', error);
                        alert('Failed to delete URL. Please try again.');
                    }
                }
            };

            const viewAnalytics = () => {
                alert(`Analytics for ${urlData.shortCode}:\nClicks: ${urlData.clicks}\n\nDetailed analytics coming soon!`);
            };

            return (
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <Button variant="ghost" className="h-8 w-8 p-0">
                            <span className="sr-only">Open menu</span>
                            <MoreHorizontal className="h-4 w-4" />
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                        <DropdownMenuLabel>Actions</DropdownMenuLabel>
                        <DropdownMenuItem
                            onClick={() => copyToClipboard(shortUrl)}
                        >
                            <Copy className="mr-2 h-4 w-4" />
                            Copy short URL
                        </DropdownMenuItem>
                        <DropdownMenuItem
                            onClick={() => copyToClipboard(urlData.originalUrl)}
                        >
                            <Copy className="mr-2 h-4 w-4" />
                            Copy original URL
                        </DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem onClick={openOriginalUrl}>
                            <ExternalLink className="mr-2 h-4 w-4" />
                            Open original URL
                        </DropdownMenuItem>
                        <DropdownMenuItem onClick={viewAnalytics}>
                            <BarChart3 className="mr-2 h-4 w-4" />
                            View analytics
                        </DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem
                            onClick={deleteUrl}
                            variant="destructive"
                        >
                            <Trash2 className="mr-2 h-4 w-4" />
                            Delete
                        </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
            )
        },
    },
];

const Dashboard = () => {
    const [urlsData, setUrlsData] = useState<UrlDataWithClicks[]>([]);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchUrlsWithClicks = useCallback(async (isRefresh = false) => {
        try {
            if (isRefresh) {
                setRefreshing(true);
            } else {
                setLoading(true);
            }
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
            setRefreshing(false);
        }
    }, []);

    useEffect(() => {
        fetchUrlsWithClicks();
    }, [fetchUrlsWithClicks]);

    const handleRefresh = () => {
        fetchUrlsWithClicks(true);
    };

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
                        <Button
                            variant="outline"
                            onClick={handleRefresh}
                            disabled={refreshing}
                        >
                            <RefreshCw className={`mr-2 h-4 w-4 ${refreshing ? 'animate-spin' : ''}`} />
                            {refreshing ? 'Refreshing...' : 'Refresh'}
                        </Button>
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
                                searchKey="originalUrl"
                                searchPlaceholder="Search URLs..."
                            />
                        )}
                    </CardContent>
                </Card>
            </div>
        </div>
    );
};

export default Dashboard;
