import { useLocation, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import axios from 'axios';

import { Spinner } from '@/components/ui/spinner';

import { AUTH_SERVER_URL, OAUTH2_CLIENT_ID, OAUTH2_CLIENT_SECRET } from '../config';


const AuthCallback = () => {

    // Get current URL location and search params
    const location = useLocation();
    // Get navigation function to redirect users
    const navigate = useNavigate();

    // Effect hook runs when component mounts and when location changes
    useEffect(() => {

        // Function to exchange authorization code for access token
        const exchangeCodeForToken = async (code: string) => {
            try {

                // Make POST request to OAuth2 token endpoint to exchange code for token
                const response = await axios.post(`${AUTH_SERVER_URL}/oauth2/token`, {
                    grant_type: 'authorization_code',
                    code: code,
                    redirect_uri: `${window.location.origin}/auth/callback`,
                    client_id: OAUTH2_CLIENT_ID,
                    client_secret: OAUTH2_CLIENT_SECRET,
                }, {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        // Basic authentication header with client credentials
                        'Authorization': 'Basic ' + btoa(`${OAUTH2_CLIENT_ID}:${OAUTH2_CLIENT_SECRET}`),
                    }
                });

                console.log("Response from token exchange: ", response.data);

                const { access_token } = response.data;

                // Store the access token in browser's local storage
                localStorage.setItem('accessToken', access_token);

                // Redirect user to dashboard after successful token exchange
                navigate('/dashboard');
            } catch (error) {
                // Log error and redirect to login page if token exchange fails
                console.error('Token exchange failed:', error);
                navigate('/login');
            }
        };

        console.log("Location search params: ", location.search);

        // Parse URL search parameters to extract authorization code
        const params = new URLSearchParams(location.search);
        const code = params.get('code');

        // If authorization code exists, exchange it for token, otherwise redirect to login
        if (code) {
            exchangeCodeForToken(code);
        } else {
            navigate('/login');
        }
    }, [location, navigate]);

    // Render loading spinner while processing OAuth callback
    return (
        <div className="flex items-center justify-center min-h-screen">
            <Spinner className="size-8" />
        </div>
    );
};

export default AuthCallback;
