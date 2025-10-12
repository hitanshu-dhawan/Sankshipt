import axios from 'axios';

import { API_SERVER_URL } from '../config';

// Create an Axios instance
const apiClient = axios.create({
    baseURL: API_SERVER_URL
});

// Add a request interceptor
apiClient.interceptors.request.use(
    (config) => {
        // Get the token from localStorage
        const token = localStorage.getItem('accessToken');

        // If the token exists, add it to the Authorization header
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    }
);

// Add a response interceptor for handling auth errors
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        // If we get a 401, redirect to login
        if (error.response?.status === 401) {
            localStorage.removeItem('accessToken');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default apiClient;