import { useState, useEffect } from 'react';
import apiClient from '../api/api-client';

const Dashboard = () => {
    const [urlsData, setUrlsData] = useState('');

    useEffect(() => {
        const fetchUrls = async () => {
            try {
                const response = await apiClient.get('/api/urls');
                setUrlsData(JSON.stringify(response.data, null, 2));
            } catch (error) {
                setUrlsData(`Error: ${error}`);
            }
        };

        fetchUrls();
    }, []);

    return (
        <div className="flex items-center justify-center min-h-screen p-4">
            <h3 className="text-lg font-mono whitespace-pre-wrap overflow-auto">
                {urlsData || 'Loading...'}
            </h3>
        </div>
    );
};

export default Dashboard;
