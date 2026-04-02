import DashboardIcon from '@mui/icons-material/Dashboard';
import { type Navigation } from '@toolpad/core/AppProvider';

// 단순한 고정 네비게이션 - dashboard만 포함
export const NAVIGATION: Navigation = [
  {
    kind: 'divider',
  },
  { 
    segment: 'dashboard', 
    title: 'Dashboard',
    icon: <DashboardIcon />,
  },
  {
    kind: 'divider',
  },
];

// 더 이상 서버 상태에 따른 네비게이션 변경 없음
export const getNavigationByStatus = (): Navigation => {
  return NAVIGATION;
};
