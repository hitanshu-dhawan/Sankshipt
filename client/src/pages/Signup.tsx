// Core React hooks and utilities
import { z } from 'zod';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

// Form handling libraries
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';

// External libraries
import axios from 'axios';
import { toast } from 'sonner';

// UI Components (in order of usage)
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { AlertCircleIcon } from 'lucide-react';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Button } from '@/components/ui/button';

// Configuration
import { AUTH_SERVER_URL } from '../config';


// Validation schema
const signupSchema = z.object({
    firstName: z.string().min(1, 'First name is required'),
    lastName: z.string().min(1, 'Last name is required'),
    email: z.email('Please enter a valid email address'),
    password: z.string().min(4, 'Password must be at least 4 characters'),
    roles: z.array(z.enum(['ADMIN', 'USER'])).min(1, 'Please select a role'),
});

type SignupFormData = z.infer<typeof signupSchema>;

const Signup = () => {

    // Store the currently selected user role for the signup form
    const [selectedRole, setSelectedRole] = useState<'ADMIN' | 'USER' | null>(null);
    // Track form submission loading state
    const [isLoading, setIsLoading] = useState(false);
    // Store any error messages to display to the user
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    const navigate = useNavigate();

    // Initialize react-hook-form with Zod validation schema
    const {
        // Function to register form inputs and apply validation rules
        register,
        // Function that handles form submission with validation
        handleSubmit,
        // Object containing form state, destructured to get validation errors
        formState: { errors },
        // Function to programmatically set values for specific form fields
        setValue,
    } = useForm<SignupFormData>({
        // Use Zod schema for form validation with zodResolver
        resolver: zodResolver(signupSchema),
        // Set initial/default values for form fields
        defaultValues: {
            // Initialize roles as empty array (will be populated when user selects a role)
            roles: [],
        },
    });

    const onSubmit = async (data: SignupFormData) => {

        setIsLoading(true);
        setErrorMessage(null);

        try {
            const requestData = {
                firstName: data.firstName,
                lastName: data.lastName,
                email: data.email,
                password: data.password,
                roles: data.roles,
            };

            await axios.post(
                `${AUTH_SERVER_URL}/api/users/signup`,
                requestData,
                {
                    headers: {
                        'Content-Type': 'application/json',
                    },
                }
            );

            // Show success message
            toast.success('Account created successfully! Redirecting to login...');

            // Wait a moment for the user to see the success message, then redirect
            setTimeout(() => {
                navigate('/login');
            }, 2000);

        } catch (error: any) {
            console.error('Signup error:', error);

            if (error.response?.data?.message) {
                setErrorMessage(error.response.data.message);
            } else {
                setErrorMessage('An error occurred during signup. Please try again.');
            }
        } finally {
            setIsLoading(false);
        }
    };

    const handleRoleChange = (value: 'ADMIN' | 'USER') => {
        setSelectedRole(value);
        setValue('roles', [value]);
    };

    return (
        <div className="flex items-center justify-center min-h-screen">
            <Card className="w-full max-w-md">

                {/* Card Header */}
                <CardHeader className="text-center">
                    <CardTitle className="text-2xl font-bold">Create Account</CardTitle>
                    <p className="text-muted-foreground">Sign up for Sankshipt</p>
                </CardHeader>

                {/* Card Content */}
                <CardContent className="pt-4">
                    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">

                        {/* Error Alert */}
                        {errorMessage && (
                            <Alert variant="destructive">
                                <AlertCircleIcon />
                                <AlertDescription>{errorMessage}</AlertDescription>
                            </Alert>
                        )}

                        {/* First Name */}
                        <div className="space-y-2">
                            <Label htmlFor="firstName">First Name</Label>
                            <Input
                                id="firstName"
                                {...register('firstName')}
                                placeholder="Enter your first name"
                                disabled={isLoading}
                            />
                            {errors.firstName && (
                                <p className="text-sm text-destructive">{errors.firstName.message}</p>
                            )}
                        </div>

                        {/* Last Name */}
                        <div className="space-y-2">
                            <Label htmlFor="lastName">Last Name</Label>
                            <Input
                                id="lastName"
                                {...register('lastName')}
                                placeholder="Enter your last name"
                                disabled={isLoading}
                            />
                            {errors.lastName && (
                                <p className="text-sm text-destructive">{errors.lastName.message}</p>
                            )}
                        </div>

                        {/* Email */}
                        <div className="space-y-2">
                            <Label htmlFor="email">Email</Label>
                            <Input
                                id="email"
                                type="email"
                                {...register('email')}
                                placeholder="Enter your email"
                                disabled={isLoading}
                            />
                            {errors.email && (
                                <p className="text-sm text-destructive">{errors.email.message}</p>
                            )}
                        </div>

                        {/* Password */}
                        <div className="space-y-2">
                            <Label htmlFor="password">Password</Label>
                            <Input
                                id="password"
                                type="password"
                                {...register('password')}
                                placeholder="Enter your password"
                                disabled={isLoading}
                            />
                            {errors.password && (
                                <p className="text-sm text-destructive">{errors.password.message}</p>
                            )}
                        </div>

                        {/* Role */}
                        <div className="space-y-2">
                            <Label htmlFor="role">Role</Label>
                            <Select
                                value={selectedRole || ''}
                                onValueChange={handleRoleChange}
                                disabled={isLoading} >
                                <SelectTrigger>
                                    <SelectValue placeholder="Select your role" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="USER">User</SelectItem>
                                    <SelectItem value="ADMIN">Admin</SelectItem>
                                </SelectContent>
                            </Select>
                            {errors.roles && (
                                <p className="text-sm text-destructive">{errors.roles.message}</p>
                            )}
                        </div>

                        {/* Submit Button */}
                        <Button
                            type="submit"
                            className="w-full"
                            size="lg"
                            disabled={isLoading} >
                            {isLoading ? 'Creating Account...' : 'Sign Up'}
                        </Button>

                        {/* Login Link */}
                        <div className="text-center">
                            <p className="text-sm text-muted-foreground">
                                Already have an account?{' '}
                                <Button
                                    variant="link"
                                    className="p-0 h-auto font-normal"
                                    onClick={() => navigate('/login')}
                                    disabled={isLoading}>
                                    Sign in
                                </Button>
                            </p>
                        </div>

                    </form>
                </CardContent>

            </Card>
        </div>
    );
};

export default Signup;
