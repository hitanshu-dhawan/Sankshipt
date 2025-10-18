export interface UrlData {
    originalUrl: string;
    shortCode: string;
}

export interface UrlDataWithClicks extends UrlData {
    clicks: number;
}