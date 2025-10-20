
// Core utilities and types for building data tables (TanStack Table)
import {
    type ColumnDef,
    type SortingState,
    flexRender,
    getCoreRowModel,
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

// UI components for building dialog modals
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from '../components/ui/dialog';

// UI components for building alert dialogs (confirmations)
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger,
} from '../components/ui/alert-dialog';

// UI components for building dropdown menus
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from '../components/ui/dropdown-menu';

// UI components for form inputs
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';

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

// React Router for navigation
import { useNavigate } from 'react-router-dom';

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

    const table = useReactTable({
        data,
        columns,
        getCoreRowModel: getCoreRowModel(),
        getSortedRowModel: getSortedRowModel(),
        onSortingChange: setSorting,
        state: {
            sorting,
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
                            <TableRow key={row.id}>
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

const createColumns = (
    onViewAnalytics: (shortCode: string) => void,
    onUrlDeleted: (shortCode: string) => void
): ColumnDef<UrlDataWithClicks>[] => [
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
                            <DropdownMenuItem onClick={() => onViewAnalytics(shortCode)}>
                                <ChartNoAxesCombined className="mr-2 h-4 w-4" />
                                View analytics
                            </DropdownMenuItem>

                            <DropdownMenuSeparator />

                            {/* Delete URL */}
                            <DeleteUrlDialog urlData={urlData} onUrlDeleted={onUrlDeleted}>
                                <DropdownMenuItem
                                    variant="destructive"
                                    onSelect={(e) => e.preventDefault()}>
                                    <Trash2 className="mr-2 h-4 w-4" />
                                    Delete
                                </DropdownMenuItem>
                            </DeleteUrlDialog>

                        </DropdownMenuContent>

                    </DropdownMenu>
                )
            },
        },
    ];

// #endregion


// #region CreateUrlDialog Component

interface CreateUrlDialogProps {
    /**
     * The trigger element that will open the dialog when clicked.
     * Can be any React element such as Button, Link, Icon, or custom component.
     * This element will be wrapped with DialogTrigger to handle dialog opening.
     */
    children: React.ReactNode;

    /**
     * Callback function called when a new URL is successfully created.
     * Receives the newly created URL data including shortCode, originalUrl, and clicks (set to 0).
     * Used to update the parent component's state and refresh the URL list.
     * 
     * @param newUrl - The newly created URL data with initial click count of 0
     */
    onUrlCreated: (newUrl: UrlDataWithClicks) => void;
}

const CreateUrlDialog = ({ children, onUrlCreated }: CreateUrlDialogProps) => {

    // State for managing dialog open/close
    const [open, setOpen] = useState(false);

    // State for form input
    const [originalUrl, setOriginalUrl] = useState('');

    // State for tracking submission status
    const [isSubmitting, setIsSubmitting] = useState(false);

    /**
     * Handles form submission to create a new shortened URL
     */
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        // Validate input
        if (!originalUrl.trim()) {
            toast.error('Please enter a valid URL');
            return;
        }

        try {
            setIsSubmitting(true);

            // Make API call to create shortened URL
            const response = await apiClient.post<UrlData>('/api/urls', {
                originalUrl: originalUrl.trim()
            });

            // Create new URL data with initial click count of 0
            const newUrlData: UrlDataWithClicks = {
                ...response.data,
                clicks: 0
            };

            // Notify parent component of new URL creation
            onUrlCreated(newUrlData);

            // Reset form and close dialog
            setOriginalUrl('');
            setOpen(false);

            // Show success message
            toast.success('URL shortened successfully!');

        } catch (error) {
            console.error('Error creating URL:', error);
            toast.error('Failed to create shortened URL. Please try again.');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <Dialog open={open} onOpenChange={setOpen}>

            <DialogTrigger asChild>
                {children}
            </DialogTrigger>

            <DialogContent className="sm:max-w-md">

                <DialogHeader>
                    <DialogTitle>Create Short URL</DialogTitle>
                    <DialogDescription>
                        Enter the URL you want to shorten. We'll generate a short code for easy sharing.
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-4">

                    <div className="space-y-2">
                        <Label htmlFor="originalUrl">Original URL</Label>
                        <Input
                            id="originalUrl"
                            type="url"
                            placeholder="https://example.com"
                            value={originalUrl}
                            onChange={(e) => setOriginalUrl(e.target.value)}
                            disabled={isSubmitting}
                            required
                        />
                    </div>

                    <DialogFooter>
                        <Button
                            type="button"
                            variant="outline"
                            onClick={() => setOpen(false)}
                            disabled={isSubmitting}>
                            Cancel
                        </Button>
                        <Button
                            type="submit"
                            disabled={isSubmitting || !originalUrl.trim()}>
                            {isSubmitting ? (
                                <>
                                    <Spinner className="h-4 w-4" />
                                    Creating...
                                </>
                            ) : (
                                'Create Short URL'
                            )}
                        </Button>
                    </DialogFooter>

                </form>

            </DialogContent>

        </Dialog>
    );
};

// #endregion


// #region DeleteUrlDialog Component

interface DeleteUrlDialogProps {
    /**
     * The URL data to be deleted
     */
    urlData: UrlDataWithClicks;

    /**
     * The trigger element that will open the dialog when clicked.
     * Can be any React element such as Button, MenuItem, etc.
     */
    children: React.ReactNode;

    /**
     * Callback function called when a URL is successfully deleted.
     * Receives the short code of the deleted URL.
     * Used to update the parent component's state and refresh the URL list.
     * 
     * @param shortCode - The short code of the deleted URL
     */
    onUrlDeleted: (shortCode: string) => void;
}

const DeleteUrlDialog = ({ urlData, children, onUrlDeleted }: DeleteUrlDialogProps) => {

    // State for managing dialog open/close
    const [open, setOpen] = useState(false);

    // State for tracking deletion status
    const [isDeleting, setIsDeleting] = useState(false);

    const shortUrl = `${API_SERVER_URL}/${urlData.shortCode}`;

    /**
     * Handles the deletion of the URL
     */
    const handleDelete = async () => {
        try {
            setIsDeleting(true);

            // Make API call to delete the URL
            await apiClient.delete('/api/urls', {
                data: {
                    shortCode: urlData.shortCode
                }
            });

            // Notify parent component of URL deletion
            onUrlDeleted(urlData.shortCode);

            // Close dialog
            setOpen(false);

            // Show success message
            toast.success('URL deleted successfully!');

        } catch (error) {
            console.error('Error deleting URL:', error);
            toast.error('Failed to delete URL. Please try again.');
        } finally {
            setIsDeleting(false);
        }
    };

    return (
        <AlertDialog open={open} onOpenChange={setOpen}>

            <AlertDialogTrigger asChild>
                {children}
            </AlertDialogTrigger>

            <AlertDialogContent>

                <AlertDialogHeader>
                    <AlertDialogTitle>Delete URL</AlertDialogTitle>
                    <AlertDialogDescription>
                        Are you sure you want to delete this shortened URL? This action cannot be undone.
                    </AlertDialogDescription>
                </AlertDialogHeader>

                {/* URL Information Display */}
                <div className="space-y-4 py-4">
                    <div className="space-y-2">
                        <div className="text-sm font-medium">Short URL:</div>
                        <div className="text-sm text-muted-foreground font-mono bg-muted p-2 rounded">
                            {shortUrl}
                        </div>
                    </div>
                    <div className="space-y-2">
                        <div className="text-sm font-medium">Original URL:</div>
                        <div className="text-sm text-muted-foreground bg-muted p-2 rounded break-all">
                            {urlData.originalUrl}
                        </div>
                    </div>
                </div>

                <AlertDialogFooter>

                    <AlertDialogCancel disabled={isDeleting}>
                        Cancel
                    </AlertDialogCancel>

                    <AlertDialogAction
                        onClick={handleDelete}
                        disabled={isDeleting}
                        className="bg-destructive text-destructive-foreground hover:bg-destructive/90">
                        {isDeleting ? (
                            <>
                                <Spinner className="h-4 w-4" />
                                Deleting...
                            </>
                        ) : (
                            'Delete URL'
                        )}
                    </AlertDialogAction>

                </AlertDialogFooter>

            </AlertDialogContent>

        </AlertDialog>
    );
};

// #endregion


// #region Dashboard Component

const Dashboard = () => {

    // Stores the array of URLs with their click analytics data
    const [urlsData, setUrlsData] = useState<UrlDataWithClicks[]>([]);

    // Tracks whether data is currently being fetched from the API
    const [loading, setLoading] = useState(true);

    // Holds any error message that occurs during data fetching (null when no error)
    const [error, setError] = useState<string | null>(null);

    // Navigation hook
    const navigate = useNavigate();

    /**
     * Handles adding a newly created URL to the existing data
     * This function is called when a new URL is successfully created via the dialog
     * @param newUrl - The newly created URL data with clicks initialized to 0
     */
    const handleUrlCreated = useCallback((newUrl: UrlDataWithClicks) => {
        // Add the new URL to the beginning of the array (most recent first)
        setUrlsData(prevUrls => [newUrl, ...prevUrls]);
    }, []);

    /**
     * Handles removing a deleted URL from the existing data
     * This function is called when a URL is successfully deleted
     * @param shortCode - The short code of the deleted URL
     */
    const handleUrlDeleted = useCallback((shortCode: string) => {
        // Remove the URL with the matching short code from the array
        setUrlsData(prevUrls => prevUrls.filter(url => url.shortCode !== shortCode));
    }, []);

    /**
     * Handles navigation to analytics page for a specific URL
     * This function is called when user clicks "View Analytics" for a URL
     * @param shortCode - The short code of the URL to view analytics for
     */
    const handleViewAnalytics = useCallback((shortCode: string) => {
        // Navigate to analytics page with the shortCode using React Router
        navigate(`/analytics/${shortCode}`);
    }, [navigate]);

    // Create columns with handlers
    const columns = createColumns(handleViewAnalytics, handleUrlDeleted);

    /**
     * Fetches all URLs and their corresponding click analytics
     * This function combines data from two API endpoints:
     * 1. /api/urls - Gets all shortened URLs for the user
     * 2. /api/analytics/{shortCode}/count - Gets click count for each URL
     */
    const fetchUrlsWithClicks = useCallback(async () => {
        try {
            // Reset states at the start of data fetching
            setLoading(true);
            setError(null);

            // Fetch all URLs from the main API endpoint
            const urlsResponse = await apiClient.get<UrlData[]>('/api/urls');
            const urls = urlsResponse.data;

            // Validate response format to ensure we received an array
            if (!Array.isArray(urls)) {
                throw new Error('Invalid response format - expected array of URLs');
            }

            // Fetch click analytics for each URL in parallel
            // Using Promise.all for better performance than sequential fetching
            const urlsWithClicks: UrlDataWithClicks[] = await Promise.all(
                urls.map(async (url) => {
                    try {
                        // Attempt to fetch click count for this specific URL
                        const analyticsResponse = await apiClient.get<number>(
                            `/api/analytics/${url.shortCode}/count`
                        );

                        // Ensure we have a valid number, default to 0 if not
                        const clicks = typeof analyticsResponse.data === 'number'
                            ? analyticsResponse.data
                            : 0;

                        // Combine original URL data with click analytics
                        return {
                            ...url,
                            clicks
                        };
                    } catch (analyticsError) {
                        // If analytics fetch fails, log warning but continue with 0 clicks
                        // This ensures the dashboard still works even if analytics service is down
                        console.error(`Failed to fetch analytics for ${url.shortCode}:`, analyticsError);
                        return {
                            ...url,
                            clicks: 0
                        };
                    }
                })
            );

            // Update state with the combined data, reversed to show latest URLs first
            setUrlsData(urlsWithClicks.reverse());
        } catch (error) {
            // Handle any errors that occurred during the main URL fetching
            console.error('Error fetching URLs:', error);
            setError(error instanceof Error ? error.message : 'Failed to load URLs data');
        } finally {
            // Always set loading to false when done, regardless of success or failure
            setLoading(false);
        }
    }, []);

    // Fetch data when component mounts
    useEffect(() => {
        fetchUrlsWithClicks();
    }, [fetchUrlsWithClicks]);

    // Loading state - show spinner while data is being fetched
    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="flex flex-col items-center gap-4">
                    <Spinner className="size-8" />
                    <p className="text-muted-foreground">Loading dashboard...</p>
                </div>
            </div>
        );
    }

    // Error state - show error message with retry option
    if (error) {
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
                        <Button onClick={() => fetchUrlsWithClicks()} className="w-full">
                            Try Again
                        </Button>
                    </CardContent>
                </Card>
            </div>
        );
    }

    // Main dashboard layout
    return (
        <div className="container mx-auto py-8 px-4">
            <div className="space-y-6">

                {/* Dashboard header with title and action buttons */}
                <div className="flex items-center justify-between">
                    <div>
                        <h1 className="text-3xl font-bold">Sankshipt Dashboard</h1>
                        <p className="text-muted-foreground mt-2">
                            Manage and monitor your shortened URLs ({urlsData.length} total)
                        </p>
                    </div>
                    <CreateUrlDialog onUrlCreated={handleUrlCreated}>
                        <Button>
                            <Plus className="h-4 w-4" />
                            Create URL
                        </Button>
                    </CreateUrlDialog>
                </div>

                {/* Main content area - either empty state or data table */}
                {urlsData.length === 0 ? (
                    // Empty state when no URLs exist
                    <div className="flex flex-col items-center justify-center py-12 text-center">
                        <p className="text-muted-foreground">No URLs found</p>
                    </div>
                ) : (
                    // Data table showing all URLs when data exists
                    <DataTable
                        columns={columns}
                        data={urlsData}
                    />
                )}

            </div>
        </div>
    );
};

// #endregion


export default Dashboard;
