/**
 * Analytics Dashboard Component
 *
 * This comprehensive analytics page was implemented by 'GitHub Copilot'.
 * It provides detailed analytics for shortened URLs including click tracking,
 * data visualizations, and paginated click history.
 */

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

// UI components for input and table
import { Input } from '../components/ui/input';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../components/ui/table';
import { Pagination, PaginationContent, PaginationItem, PaginationLink, PaginationNext, PaginationPrevious } from '../components/ui/pagination';

// Recharts components for data visualization
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend } from 'recharts';

// Icon components from the 'lucide-react' library
import {
    ArrowLeft,
    Calendar,
    MousePointerClick,
    TrendingUp,
    Copy,
    Clock,
    Monitor
} from 'lucide-react';

// Toast notification library
import { toast } from "sonner";

// API client for making HTTP requests
import apiClient from '../api/api-client';



// Date formatting utilities
import { format } from 'date-fns';

// #region Type Definitions

interface UrlData {
    originalUrl: string;
    shortCode: string;
}

interface ClickData {
    id: number;
    shortCode: string;
    originalUrl: string;
    clickedAt: string;
    userAgent: string;
}

interface PaginatedClickResponse {
    content: ClickData[];
    pageable: {
        pageNumber: number;
        pageSize: number;
        sort: { sorted: boolean };
        offset: number;
        paged: boolean;
    };
    last: boolean;
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    first: boolean;
    numberOfElements: number;
    empty: boolean;
}

interface AnalyticsData {
    totalClicks: number;
}

interface ChartData {
    name: string;
    value: number;
}

interface TimeSeriesData {
    time: string;
    clicks: number;
}

// #endregion


const Analytics = () => {
    // Get shortCode from URL parameters
    const { shortCode } = useParams<{ shortCode: string }>();
    const navigate = useNavigate();

    // State management
    const [urlData, setUrlData] = useState<UrlData | null>(null);
    const [analyticsData, setAnalyticsData] = useState<AnalyticsData | null>(null);
    const [clicksData, setClicksData] = useState<PaginatedClickResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [currentPage, setCurrentPage] = useState(0);



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
     * Parse user agent to extract browser and OS information
     */
    const parseUserAgent = (userAgent: string) => {
        let browser = 'Unknown';
        let os = 'Unknown';

        // Browser detection
        if (userAgent.includes('Chrome')) browser = 'Chrome';
        else if (userAgent.includes('Firefox')) browser = 'Firefox';
        else if (userAgent.includes('Safari') && !userAgent.includes('Chrome')) browser = 'Safari';
        else if (userAgent.includes('Edge')) browser = 'Edge';

        // OS detection
        if (userAgent.includes('Mac OS X') || userAgent.includes('Macintosh')) os = 'Mac OS';
        else if (userAgent.includes('Windows')) os = 'Windows';
        else if (userAgent.includes('Linux')) os = 'Linux';
        else if (userAgent.includes('Android')) os = 'Android';
        else if (userAgent.includes('iOS') || userAgent.includes('iPhone') || userAgent.includes('iPad')) os = 'iOS';

        return { browser, os };
    };

    /**
     * Process clicks data for charts
     */
    const processAnalyticsData = (clicks: ClickData[]) => {
        // Time series data - group by hour
        const timeGroups: { [key: string]: number } = {};
        const browserGroups: { [key: string]: number } = {};
        const osGroups: { [key: string]: number } = {};

        clicks.forEach(click => {
            // Process time data
            const date = new Date(click.clickedAt);
            const hourKey = format(date, 'HH:mm');
            timeGroups[hourKey] = (timeGroups[hourKey] || 0) + 1;

            // Process browser and OS data
            const { browser, os } = parseUserAgent(click.userAgent);
            browserGroups[browser] = (browserGroups[browser] || 0) + 1;
            osGroups[os] = (osGroups[os] || 0) + 1;
        });

        const timeSeriesData: TimeSeriesData[] = Object.entries(timeGroups).map(([time, clicks]) => ({
            time,
            clicks
        })).sort((a, b) => a.time.localeCompare(b.time));

        const browserData: ChartData[] = Object.entries(browserGroups).map(([name, value]) => ({
            name,
            value
        }));

        const osData: ChartData[] = Object.entries(osGroups).map(([name, value]) => ({
            name,
            value
        }));

        return { timeSeriesData, browserData, osData };
    };

    /**
     * Fetch URL data and analytics information
     */
    const fetchAnalyticsData = async (pageNumber = 0) => {
        if (!shortCode) {
            setError('Invalid short code');
            setLoading(false);
            return;
        }

        try {
            setLoading(true);
            setError(null);

            // Fetch URL data, analytics count, and paginated clicks in parallel
            const [urlResponse, analyticsResponse, clicksResponse] = await Promise.all([
                apiClient.get<UrlData[]>('/api/urls'),
                apiClient.get<number>(`/api/analytics/${shortCode}/count`),
                apiClient.get<PaginatedClickResponse>(`/api/analytics/${shortCode}/clicks?pageNumber=${pageNumber}&pageSize=20&sortOrder=desc`)
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
            setClicksData(clicksResponse.data);

        } catch (error) {
            console.error('Error fetching analytics data:', error);
            setError(error instanceof Error ? error.message : 'Failed to load analytics data');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchAnalyticsData(currentPage);
    }, [shortCode, currentPage]);

    const handlePageChange = (page: number) => {
        setCurrentPage(page);
    };

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
    if (error || !urlData || !analyticsData || !clicksData) {
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

    const { timeSeriesData, browserData } = processAnalyticsData(clicksData.content);
    const CHART_COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'];

    // Main analytics layout
    return (
        <div className="container mx-auto py-8 px-4 max-w-7xl">
            <div className="space-y-8">

                {/* Header Section */}
                <div className="space-y-4">
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

                    <div className="space-y-2">
                        <h1 className="text-3xl font-bold">Analytics for: {shortCode}</h1>

                        {/* Original URL with copy functionality */}
                        <div className="flex items-center gap-2 max-w-4xl">
                            <Input
                                value={urlData.originalUrl}
                                readOnly
                                className="flex-1 font-mono text-sm bg-muted"
                            />
                            <Button
                                variant="outline"
                                size="icon"
                                onClick={() => copyToClipboard(urlData.originalUrl)}
                                className="shrink-0">
                                <Copy className="h-4 w-4" />
                            </Button>
                        </div>
                    </div>
                </div>

                {/* Key Metrics Cards */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    {/* Total Clicks */}
                    <Card>
                        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                            <CardTitle className="text-sm font-medium">Total Clicks</CardTitle>
                            <MousePointerClick className="h-4 w-4 text-muted-foreground" />
                        </CardHeader>
                        <CardContent>
                            <div className="text-2xl font-bold">{analyticsData.totalClicks.toLocaleString()}</div>
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

                    {/* Status */}
                    <Card>
                        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                            <CardTitle className="text-sm font-medium">Status</CardTitle>
                            <TrendingUp className="h-4 w-4 text-muted-foreground" />
                        </CardHeader>
                        <CardContent>
                            <Badge variant="outline" className="text-green-600 border-green-600">
                                Active
                            </Badge>
                        </CardContent>
                    </Card>
                </div>

                {/* Charts Section */}
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    {/* Clicks Over Time */}
                    <Card>
                        <CardHeader>
                            <CardTitle className="flex items-center gap-2">
                                <Clock className="h-5 w-5" />
                                Clicks Over Time
                            </CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="h-[300px]">
                                <ResponsiveContainer width="100%" height="100%">
                                    <BarChart data={timeSeriesData}>
                                        <CartesianGrid strokeDasharray="3 3" />
                                        <XAxis dataKey="time" />
                                        <YAxis />
                                        <Tooltip />
                                        <Bar dataKey="clicks" fill="#0088FE" />
                                    </BarChart>
                                </ResponsiveContainer>
                            </div>
                        </CardContent>
                    </Card>

                    {/* Browser Breakdown */}
                    <Card>
                        <CardHeader>
                            <CardTitle className="flex items-center gap-2">
                                <Monitor className="h-5 w-5" />
                                Browser Breakdown
                            </CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="h-[300px]">
                                <ResponsiveContainer width="100%" height="100%">
                                    <PieChart>
                                        <Pie
                                            data={browserData}
                                            cx="50%"
                                            cy="50%"
                                            outerRadius={80}
                                            fill="#8884d8"
                                            dataKey="value"
                                            label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                                        >
                                            {browserData.map((_, index) => (
                                                <Cell key={`cell-${index}`} fill={CHART_COLORS[index % CHART_COLORS.length]} />
                                            ))}
                                        </Pie>
                                        <Tooltip />
                                        <Legend />
                                    </PieChart>
                                </ResponsiveContainer>
                            </div>
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
                                        <TableHead>Browser</TableHead>
                                        <TableHead>OS</TableHead>
                                        <TableHead className="hidden md:table-cell">User Agent</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {clicksData.content.map((click) => {
                                        const { browser, os } = parseUserAgent(click.userAgent);
                                        return (
                                            <TableRow key={click.id}>
                                                <TableCell className="font-medium">
                                                    {format(new Date(click.clickedAt), 'MMM dd, yyyy, h:mm a')}
                                                </TableCell>
                                                <TableCell>
                                                    <Badge variant="secondary">{browser}</Badge>
                                                </TableCell>
                                                <TableCell>
                                                    <Badge variant="outline">{os}</Badge>
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