import { useState, useEffect, useCallback } from 'react';
import apiClient from '../api/api-client';
import { DataTable } from '../components/ui/data-table';
import { columns } from '../components/columns';
import { type UrlData, type UrlDataWithClicks } from '../types/url';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Spinner } from '../components/ui/spinner';
import { Button } from '../components/ui/button';
import { RefreshCw, Plus } from 'lucide-react';

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
