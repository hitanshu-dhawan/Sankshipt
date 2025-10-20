import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';

import { useNavigate } from 'react-router-dom';

import { AUTH_SERVER_URL } from '../config';


const Login = () => {

    const navigate = useNavigate();

    const handleLogin = () => {
        const params = new URLSearchParams({
            response_type: 'code',
            client_id: 'sankshipt-client',
            scope: 'api.read api.write api.delete',
            redirect_uri: `${window.location.origin}/auth/callback`,
        });
        window.location.href = `${AUTH_SERVER_URL}/oauth2/authorize?${params.toString()}`;
    };

    return (
        <div className="flex items-center justify-center min-h-screen">
            <Card className="w-full max-w-md">

                {/* Card Header */}
                <CardHeader className="text-center">
                    <CardTitle className="text-2xl font-bold">Welcome to Sankshipt</CardTitle>
                    <p className="text-muted-foreground">Login to continue</p>
                </CardHeader>

                {/* Card Content */}
                <CardContent className="pt-4">

                    {/* OAuth 2.0 Login Button */}
                    <Button onClick={handleLogin} className="w-full" size="lg">
                        Login with OAuth 2.0
                    </Button>

                    {/* Signup Link */}
                    <div className="text-center mt-4">
                        <p className="text-sm text-muted-foreground">
                            Don't have an account?{' '}
                            <Button
                                variant="link"
                                className="p-0 h-auto font-normal"
                                onClick={() => navigate('/signup')} >
                                Sign up
                            </Button>
                        </p>
                    </div>

                </CardContent>

            </Card>
        </div>
    );
};

export default Login;
