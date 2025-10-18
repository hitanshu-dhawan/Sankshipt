"use client"

import { type ColumnDef } from "@tanstack/react-table"
import { ArrowUpDown, Copy, MoreHorizontal, Trash2, BarChart3, ExternalLink } from "lucide-react"

import { Button } from "@/components/ui/button"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Badge } from "@/components/ui/badge"
import { type UrlDataWithClicks } from "@/types/url"

export const columns: ColumnDef<UrlDataWithClicks>[] = [
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
                    // TODO: Add toast notification for successful copy
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
                // TODO: Implement analytics view - could open a modal or navigate to analytics page
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
]