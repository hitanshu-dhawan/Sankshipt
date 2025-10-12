import { BrowserRouter, Routes, Route, Outlet, Navigate } from 'react-router-dom';

import Login from './pages/Login';
import Signup from './pages/Signup';
import AuthCallback from './pages/AuthCallback';
import Dashboard from './pages/Dashboard';

import { Toaster } from "@/components/ui/sonner"


// Component that protects routes by checking for an access token in localStorage
// If token exists, renders child routes via Outlet; otherwise redirects to login page
const ProtectedRoute = () => {
  const token = localStorage.getItem('accessToken');
  return token ? <Outlet /> : <Navigate to="/login" />;
};


const App = () => {
  return (
    <BrowserRouter>

      <Routes>

        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/auth/callback" element={<AuthCallback />} />

        {/* Protected Routes */}
        <Route element={<ProtectedRoute />}>
          <Route path="/dashboard" element={<Dashboard />} />
        </Route>

        <Route path="*" element={<Login />} />

      </Routes>

      <Toaster />

    </BrowserRouter>
  );
};

export default App;
