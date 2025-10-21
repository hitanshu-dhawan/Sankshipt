# 🎨 Sankshipt Client - React Web Application

**Sankshipt Client** is a modern, responsive React web application that provides a sleek user interface for the Sankshipt URL shortener service. Built with cutting-edge web technologies, it offers seamless URL management, comprehensive analytics, and secure OAuth2 authentication.

## 🏗️ Tech Stack

### Frontend Framework & Libraries
- **React 19.1.1** - Latest React with concurrent features
- **TypeScript 5.9.3** - Type-safe JavaScript development
- **Vite 7.1.7** - Lightning-fast build tool and dev server
- **React Router DOM 7.9.4** - Client-side routing and navigation

### UI Framework & Design System
- **Tailwind CSS 4.1.14** - Utility-first CSS framework
- **Radix UI** - Accessible, unstyled UI primitives
  - Comprehensive component collection (30+ components)
  - Keyboard navigation & ARIA compliance
  - Focus management and screen reader support
- **Shadcn/ui** - Beautiful, customizable UI components
- **Lucide React** - Modern icon library with 1000+ icons

### State Management & Forms
- **React Hook Form 7.65.0** - Performant form handling
- **Zod 4.1.12** - TypeScript-first schema validation
- **@hookform/resolvers** - Form validation integration

### Data Management & Visualization
- **TanStack Table 8.21.3** - Powerful data tables with sorting
- **Recharts 2.15.4** - Composable charting library
- **Axios 1.12.2** - HTTP client for API communication

### Developer Experience
- **ESLint 9.36.0** - Code linting and quality enforcement
- **TypeScript ESLint** - TypeScript-specific linting rules
- **Vite SWC Plugin** - Fast TypeScript compilation

### UI Enhancements
- **Sonner** - Beautiful toast notifications
- **Date-fns** - Modern date utility library
- **Class Variance Authority** - Type-safe component variants
- **Next Themes** - Theme management system

## 🚀 Getting Started

### Prerequisites
- Node.js 18+ and npm
- Docker (for containerized development)
- Running Sankshipt backend services (API and Auth servers)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/hitanshu-dhawan/Sankshipt.git
   cd Sankshipt/client
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure environment**
   - Ensure backend services are running on:
     - API Server: `http://localhost:8080`
     - Auth Server: `http://localhost:9000`
   - Update `src/config.ts` if needed

4. **Start development server**
   ```bash
   npm run dev
   ```

5. **Access the application**
   - Open http://localhost:3000
   - The app will hot-reload on changes

### Docker Development

1. **Build and run with Docker Compose (from project root)**
   ```bash
   docker-compose up --build
   ```

2. **Access containerized application**
   - Client: http://localhost:3000
   - Automatic service connectivity to backend

### Available Scripts

```bash
# Development server with hot reload
npm run dev

# Type checking and production build
npm run build

# ESLint code analysis
npm run lint

# Preview production build locally
npm run preview
```

## 🎯 Application Features

### 🔐 Authentication & Authorization
- **OAuth2 Authorization Code Flow** - Secure, standard-compliant authentication
- **JWT Token Management** - Automatic token handling and refresh
- **Protected Routes** - Route-level access control
- **Automatic Redirects** - Seamless login/logout experience
- **Scope-based Permissions** - Fine-grained API access control

### 🔗 URL Management
- **Create Short URLs** - Intuitive form with validation
- **URL Listing** - Sortable table with pagination
- **Copy to Clipboard** - One-click URL copying
- **Bulk Operations** - Multiple URL management
- **URL Validation** - Real-time form validation

### 📊 Analytics Dashboard
- **Click Tracking** - Detailed click analytics per URL
- **Visual Charts** - Interactive data visualization
- **Paginated History** - Efficient data loading
- **Time-based Sorting** - Chronological click analysis
- **User Agent Details** - Browser and device information

### 🎨 User Experience
- **Responsive Design** - Mobile-first, adaptive layouts
- **Dark/Light Themes** - System preference detection
- **Loading States** - Skeleton screens and spinners
- **Error Handling** - Graceful error boundaries
- **Toast Notifications** - Real-time user feedback
- **Keyboard Navigation** - Full accessibility support

## 🗂️ Project Structure

```
client/
├── public/                     # Static assets
├── src/
│   ├── api/
│   │   └── api-client.ts      # Axios HTTP client with interceptors
│   ├── components/
│   │   └── ui/                # Reusable Shadcn/ui components (30+)
│   │       ├── button.tsx     # Button component variants
│   │       ├── card.tsx       # Card container components
│   │       ├── dialog.tsx     # Modal dialog components
│   │       ├── table.tsx      # Data table components
│   │       └── ...            # Additional UI components
│   ├── hooks/
│   │   └── use-mobile.ts      # Mobile breakpoint detection
│   ├── lib/
│   │   └── utils.ts           # Utility functions (cn, etc.)
│   ├── pages/
│   │   ├── Analytics.tsx      # URL analytics dashboard
│   │   ├── AuthCallback.tsx   # OAuth2 callback handler
│   │   ├── Dashboard.tsx      # Main URL management dashboard
│   │   ├── Login.tsx          # Authentication login page
│   │   └── Signup.tsx         # User registration page
│   ├── types/                 # TypeScript type definitions
│   ├── App.tsx               # Root component with routing
│   ├── config.ts             # Application configuration
│   ├── index.css             # Global styles and Tailwind
│   └── main.tsx              # Application entry point
├── components.json            # Shadcn/ui configuration
├── Dockerfile                # Container configuration
├── package.json              # Dependencies and scripts
├── tsconfig.json             # TypeScript configuration
├── vite.config.ts            # Vite build configuration
└── tailwind.config.js        # Tailwind CSS configuration
```

## 🔄 Application Flow

### Authentication Flow
1. **Login Page** → User clicks "Login with OAuth2"
2. **OAuth2 Redirect** → Redirects to Auth Server authorization endpoint
3. **User Consent** → User grants permissions (api.read, api.write, api.delete)
4. **Auth Callback** → Receives authorization code
5. **Token Exchange** → Exchanges code for JWT access token
6. **Dashboard Access** → Stores token and redirects to dashboard

### URL Management Flow
1. **Create URL** → User submits original URL via form
2. **API Request** → POST to `/api/urls` with Bearer token
3. **Short Code Generation** → Backend generates unique short code
4. **Table Update** → Refreshes URL list with new entry
5. **Copy & Share** → User can copy short URL

### Analytics Flow
1. **View Analytics** → User clicks analytics icon for specific URL
2. **Fetch Data** → GET requests for click count and paginated history
3. **Data Visualization** → Displays charts and detailed click table
4. **Pagination** → Users can navigate through click history

## 🎨 UI Components Library

### Core Components (30+ Available)
- **Layout**: Card, Separator, Sheet, Sidebar
- **Navigation**: Breadcrumb, Menubar, Navigation Menu, Pagination
- **Input**: Button, Input, Select, Textarea, Checkbox, Radio Group
- **Feedback**: Alert, Badge, Progress, Spinner, Sonner (Toast)
- **Overlay**: Dialog, Alert Dialog, Popover, Tooltip, Hover Card
- **Data Display**: Table, Avatar, Accordion, Collapsible, Tabs

### Component Features
- **Accessibility First** - WCAG 2.1 AA compliance
- **Keyboard Navigation** - Full keyboard support
- **Theme Support** - Dark/light mode compatibility
- **Responsive Design** - Mobile-optimized layouts
- **Type Safety** - Full TypeScript integration

## 🔐 Security Implementation

### Client-Side Security
- **Token Storage** - Secure localStorage management
- **Automatic Logout** - Token expiration handling
- **Route Protection** - Authenticated route guards
- **XSS Prevention** - Input sanitization and validation

### API Security
- **Bearer Token Auth** - JWT tokens in Authorization headers
- **Automatic Interceptors** - Request/response authentication
- **Error Handling** - 401/403 automatic redirects
- **Scope Validation** - Permission-based API access

## 🎯 Advanced Features

### Data Tables
```typescript
// Sortable, paginated tables with TanStack Table
const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    onSortingChange: setSorting,
});
```

### Form Validation
```typescript
// Type-safe validation with Zod + React Hook Form
const signupSchema = z.object({
    firstName: z.string().min(1, 'First name is required'),
    email: z.string().email('Invalid email format'),
    role: z.enum(['USER', 'ADMIN'])
});
```

### HTTP Client
```typescript
// Automatic token injection and error handling
apiClient.interceptors.request.use((config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});
```

## 📱 Responsive Design

### Breakpoint Strategy
- **Mobile First** - min-width media queries
- **Tailwind Breakpoints** - sm (640px), md (768px), lg (1024px), xl (1280px)
- **Dynamic Components** - useIsMobile hook for conditional rendering
- **Flexible Layouts** - CSS Grid and Flexbox for adaptive designs

### Mobile Optimizations
- **Touch Targets** - 44px minimum tap areas
- **Gesture Support** - Swipe and touch interactions
- **Viewport Meta** - Proper mobile scaling
- **Performance** - Lazy loading and code splitting

## 🚀 Deployment

### Production Build
```bash
# Create optimized production build
npm run build

# Preview production build locally
npm run preview
```

### Docker Deployment
```bash
# Build production container
docker build -t sankshipt-client .

# Run container
docker run -p 3000:3000 sankshipt-client
```

### Environment Configuration
Update `src/config.ts` for different environments:
```typescript
export const API_SERVER_URL = process.env.VITE_API_URL || 'http://localhost:8080'
export const AUTH_SERVER_URL = process.env.VITE_AUTH_URL || 'http://localhost:9000'
```

## 🧪 Development Guidelines

### Code Organization
- **Feature-based Structure** - Group related components
- **Reusable Components** - Atomic design principles
- **Type Safety** - Comprehensive TypeScript usage
- **Error Boundaries** - Graceful error handling

### Best Practices
- **Component Composition** - Prefer composition over inheritance
- **Custom Hooks** - Extract reusable logic
- **Performance** - Memo and callback optimization
- **Accessibility** - ARIA labels and semantic HTML

### Styling Guidelines
- **Utility-First** - Tailwind CSS approach
- **Design Tokens** - Consistent spacing and colors
- **Component Variants** - Class Variance Authority
- **Responsive Design** - Mobile-first methodology

## 🔧 Configuration Files

### TypeScript Configuration
- **tsconfig.json** - Base TypeScript settings
- **tsconfig.app.json** - Application-specific config
- **tsconfig.node.json** - Node.js tooling config

### Build Configuration
- **vite.config.ts** - Vite build and dev server settings
- **Path Aliases** - `@` resolves to `src/` directory
- **Plugin Configuration** - React SWC and Tailwind integration

### Code Quality
- **eslint.config.js** - ESLint rules and configurations
- **Prettier** - Code formatting (via editor integration)

## 🤝 Contributing

### Development Workflow
1. **Fork** the repository
2. **Create** feature branch: `git checkout -b feature/amazing-feature`
3. **Install** dependencies: `npm install`
4. **Start** dev server: `npm run dev`
5. **Make** changes and test locally
6. **Lint** code: `npm run lint`
7. **Build** for production: `npm run build`
8. **Commit** changes: `git commit -m 'Add amazing feature'`
9. **Push** to branch: `git push origin feature/amazing-feature`
10. **Open** Pull Request

### Code Standards
- **TypeScript** - All components must be typed
- **Accessibility** - WCAG 2.1 AA compliance required
- **Testing** - Unit tests for complex logic
- **Documentation** - JSDoc comments for public APIs

## 📚 Resources & References

### Documentation
- [React Documentation](https://react.dev/)
- [Vite Guide](https://vitejs.dev/guide/)
- [Tailwind CSS Docs](https://tailwindcss.com/docs)
- [Radix UI Primitives](https://www.radix-ui.com/primitives)
- [TanStack Table](https://tanstack.com/table/latest)

### UI Component System
- [Shadcn/ui Components](https://ui.shadcn.com/docs)
- [Lucide Icons](https://lucide.dev/)
- [Recharts Examples](https://recharts.org/en-US/)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](../LICENSE) file for details.

## 🏷️ Version Information

- **Version**: 0.0.0
- **React**: 19.1.1
- **TypeScript**: 5.9.3
- **Vite**: 7.1.7
- **Node.js**: 18+ required

---

**Built with ❤️ using React, TypeScript, and modern web technologies**

For questions, issues, or feature requests, please open an issue on GitHub.