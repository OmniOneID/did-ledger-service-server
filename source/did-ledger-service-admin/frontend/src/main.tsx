import * as React from 'react';
import * as ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router';
import App from './App';
import Layout from './layout/Layout';
import SignInPage from './pages/auth/SignIn';
import ErrorPage from './pages/ErrorPage';
import DashboardPage from './pages/dashboard/DashboardPage';
import AdminRegisterPage from './pages/admins/admin/AdminRegisterPage';
import AdminDetailPage from "./pages/admins/admin/AdminDetailPage";
import AdminManagementPage from "./pages/admins/admin/AdminManagementPage";
import PasswordPolicyManagementPage from "./pages/admins/password-policy/PasswordPolicyManagementPage";
import ApiKeyManagementPage from "./pages/api-keys/ApiKeyManagementPage";
import ServerConfigurationPage from "./pages/server-configuration/ServerConfigurationPage";
import DidListPage from "./pages/dids/did-list/DidListPage";
import DidChangeHistoryPage from "./pages/dids/did-change-history/DidChangeHistoryPage";
import DidDetailPage from "./pages/dids/did-list/DidDetailPage";
import VcMetadataListPage from "./pages/vc-metadatas/vc-metadata-list/VcMetadataListPage";
import VcChangeHistoryPage from "./pages/vc-metadatas/vc-change-history/VcChangeHistoryPage";
import VcMetadataDetailPage from "./pages/vc-metadatas/vc-metadata-list/VcMetadataDetailPage";
import VcSchemaListPage from "./pages/schemas/vc-schema-list/VcSchemaListPage";
import VcSchemaDetailPage from "./pages/schemas/vc-schema-list/VcSchemaDetailPage";
import ApiLogManagementPage from './pages/log/ApiLogManagementPage';
import AuditApiLogManagementPage from './pages/log/AuditApiLogManagementPage';

const router = createBrowserRouter([
  {
    Component: App,
    children: [
      {
        path: '/',
        Component: Layout,
        children: [
          {
            path: '/',
            Component: DidListPage
          },
          {
            path: '/dashboard',
            Component: DidListPage
          },
          {
            path: 'dids',
            Component: DidListPage,
          },
          {
            path: 'dids/did-list',
            Component: DidListPage,
          },
          {
            path: 'dids/did-list/:id',
            Component: DidDetailPage,
          },
          {
            path: 'dids/did-change-history',
            Component: DidChangeHistoryPage,
          },
          {
            path: 'vc-metadatas',
            Component: VcMetadataListPage,
          },
          {
            path: 'vc-metadatas/vc-metadata-list',
            Component: VcMetadataListPage,
          },
          {
            path: 'vc-metadatas/vc-metadata-list/:id',
            Component: VcMetadataDetailPage,
          },
          {
            path: 'vc-metadatas/vc-change-history',
            Component: VcChangeHistoryPage,
          },
          {
            path: 'schemas/vc-schema-list',
            Component: VcSchemaListPage,
          },
          {
            path: 'schemas/vc-schema-list/:id',
            Component: VcSchemaDetailPage,
          },
          {
            path: 'schemas',
            Component: VcSchemaListPage,
          },
          {
            path: 'admins/admin-management/admin-registration',
            Component: AdminRegisterPage,
          },
          {
            path: 'admins/admin-management/:id',
            Component: AdminDetailPage,
          },
          {
            path: 'admins/admin-management',
            Component: AdminManagementPage,
          },
          {
            path: 'admins/password-policy',
            Component: PasswordPolicyManagementPage,
          },
          {
            path: 'admins',
            Component: AdminManagementPage,
          },
          {
            path: 'apikey-management',
            Component: ApiKeyManagementPage,
          },
          {
            path: 'server-configuration',
            Component: ServerConfigurationPage,
          },
          {
            path: '/logs/api',
            Component: ApiLogManagementPage,
          },
          {
            path: '/logs/audit',
            Component: AuditApiLogManagementPage,
          },
        ],
      },
      {
        path: '/sign-in',
        Component: SignInPage,
      },
      {
        path: '/error',
        Component: ErrorPage,
      },
    ],
  },
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>,
);
