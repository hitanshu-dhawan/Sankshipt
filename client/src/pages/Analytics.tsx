// React hooks for state and lifecycle management
import { useState, useEffect } from 'react';

// React Router for navigation and URL parameters
import { useParams, useNavigate } from 'react-router-dom';

// UI components for displaying content in a card container
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';

// UI component for interactive buttons
import { Button } from '../components/ui/button';

// UI component for displaying badges or tags
import { Badge } from '../components/ui/badge';

// UI component for indicating loading (spinner)
import { Spinner } from '../components/ui/spinner';

// Icon components from the 'lucide-react' library
import {
    ArrowLeft,
    ExternalLink,
    Calendar,
    MousePointer,
    TrendingUp,
    Copy
} from 'lucide-react';

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

interface AnalyticsData {
    totalClicks: number;
    // Add more analytics data fields as per your API
}

// #endregion


const Analytics = () => {
    // Get shortCode from URL parameters
    const { shortCode } = useParams<{ shortCode: string }>();
    const navigate = useNavigate();

    // State management
    const [urlData, setUrlData] = useState<UrlData | null>(null);
    const [analyticsData, setAnalyticsData] = useState<AnalyticsData | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // Derived values
    const shortUrl = shortCode ? `${API_SERVER_URL}/${shortCode}` : '';

    /**
     * Copy text to clipboard with user feedback
     */
    const copyToClipboard = async (text: string) => {
        try {
            await navigator.clipboard.writeText(text);
            toast.success('Copied to clipboard!');
        } catch (err) {
            console.error('Failed to copy: ', err);
            toast.error('Failed to copy to clipboard.');
        }
    };

    /**
     * Fetch URL data and analytics information
     */
    useEffect(() => {
        const fetchAnalyticsData = async () => {
            if (!shortCode) {
                setError('Invalid short code');
                setLoading(false);
                return;
            }

            try {
                setLoading(true);
                setError(null);

                // Fetch URL data and analytics in parallel
                const [urlResponse, analyticsResponse] = await Promise.all([
                    // Fetch URL details (you might need to adjust this endpoint)
                    apiClient.get<UrlData[]>('/api/urls'),
                    // Fetch analytics data
                    apiClient.get<number>(`/api/analytics/${shortCode}/count`)
                ]);

                // Find the specific URL data for this shortCode
                const urls = urlResponse.data;
                const foundUrl = urls.find(url => url.shortCode === shortCode);

                if (!foundUrl) {
                    throw new Error('URL not found');
                }

                setUrlData(foundUrl);
                setAnalyticsData({
                    totalClicks: typeof analyticsResponse.data === 'number' ? analyticsResponse.data : 0
                });

            } catch (error) {
                console.error('Error fetching analytics data:', error);
                setError(error instanceof Error ? error.message : 'Failed to load analytics data');
            } finally {
                setLoading(false);
            }
        };

        fetchAnalyticsData();
    }, [shortCode]);

    // Loading state
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

    // Error state
    if (error || !urlData || !analyticsData) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <Card className="w-full max-w-md">
                    <CardHeader className="text-center">
                        <CardTitle className="text-destructive">Something went wrong!</CardTitle>
                        <p className="text-muted-foreground text-sm mt-2">
                            {error || 'Failed to load analytics data'}
                        </p>
                    </CardHeader>
                    <CardContent>
                        <Button onClick={() => navigate('/dashboard')} className="w-full">
                            <ArrowLeft className="h-4 w-4 mr-2" />
                            Back to Dashboard
                        </Button>
                    </CardContent>
                </Card>
            </div>
        );
    }

    // Main analytics layout
    return (
        <div className="container mx-auto py-8 px-4">
            <div className="space-y-6">

                {/* Header with back button */}
                <div className="flex items-center gap-4">
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={() => navigate('/dashboard')}
                        className="flex items-center gap-2">
                        <ArrowLeft className="h-4 w-4" />
                        Back to Dashboard
                    </Button>
                </div>

                {/* URL Information Card */}
                <Card>
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2">
                            <TrendingUp className="h-5 w-5" />
                            URL Analytics
                        </CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">

                        {/* Short URL */}
                        <div className="space-y-2">
                            <div className="text-sm font-medium">Short URL:</div>
                            <div className="flex items-center gap-2">
                                <div className="text-sm text-muted-foreground font-mono bg-muted p-2 rounded flex-1">
                                    {shortUrl}
                                </div>
                                <Button
                                    variant="outline"
                                    size="sm"
                                    onClick={() => copyToClipboard(shortUrl)}>
                                    <Copy className="h-4 w-4" />
                                </Button>
                                <Button
                                    variant="outline"
                                    size="sm"
                                    onClick={() => window.open(shortUrl, '_blank')}>
                                    <ExternalLink className="h-4 w-4" />
                                </Button>
                            </div>
                        </div>

                        {/* Original URL */}
                        <div className="space-y-2">
                            <div className="text-sm font-medium">Original URL:</div>
                            <div className="flex items-center gap-2">
                                <div className="text-sm text-muted-foreground bg-muted p-2 rounded flex-1 break-all">
                                    {urlData.originalUrl}
                                </div>
                                <Button
                                    variant="outline"
                                    size="sm"
                                    onClick={() => copyToClipboard(urlData.originalUrl)}>
                                    <Copy className="h-4 w-4" />
                                </Button>
                                <Button
                                    variant="outline"
                                    size="sm"
                                    onClick={() => window.open(urlData.originalUrl, '_blank')}>
                                    <ExternalLink className="h-4 w-4" />
                                </Button>
                            </div>
                        </div>

                    </CardContent>
                </Card>

                {/* Analytics Stats Cards */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">

                    {/* Total Clicks */}
                    <Card>
                        <CardContent className="p-6">
                            <div className="flex items-center space-x-2">
                                <MousePointer className="h-4 w-4 text-muted-foreground" />
                                <div className="text-sm font-medium text-muted-foreground">
                                    Total Clicks
                                </div>
                            </div>
                            <div className="text-2xl font-bold mt-2">
                                {analyticsData.totalClicks.toLocaleString()}
                            </div>
                        </CardContent>
                    </Card>

                    {/* Short Code */}
                    <Card>
                        <CardContent className="p-6">
                            <div className="flex items-center space-x-2">
                                <Calendar className="h-4 w-4 text-muted-foreground" />
                                <div className="text-sm font-medium text-muted-foreground">
                                    Short Code
                                </div>
                            </div>
                            <div className="text-2xl font-bold mt-2 font-mono">
                                {shortCode}
                            </div>
                        </CardContent>
                    </Card>

                    {/* Status */}
                    <Card>
                        <CardContent className="p-6">
                            <div className="flex items-center space-x-2">
                                <TrendingUp className="h-4 w-4 text-muted-foreground" />
                                <div className="text-sm font-medium text-muted-foreground">
                                    Status
                                </div>
                            </div>
                            <div className="mt-2">
                                <Badge variant="outline" className="text-green-600 border-green-600">
                                    Active
                                </Badge>
                            </div>
                        </CardContent>
                    </Card>

                </div>

                {/* Placeholder for future analytics features */}
                <Card>
                    <CardHeader>
                        <CardTitle>Detailed Analytics</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-center py-8">
                            <p className="text-muted-foreground">
                                More detailed analytics features like click history, geographic data,
                                and referrer information will be available soon.
                            </p>
                        </div>
                    </CardContent>
                </Card>

            </div>
        </div>
    );
};

export default Analytics;