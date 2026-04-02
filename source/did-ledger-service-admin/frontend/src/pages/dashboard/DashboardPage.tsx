import React from 'react';
import { Box, Typography } from '@mui/material';

const DashboardPage: React.FC = () => {
  return (
    <Box 
      sx={{ 
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '60vh',
        flexDirection: 'column'
      }}
    >
      <Typography variant="h2" sx={{ marginBottom: 2 }}>
        Hello, World!
      </Typography>
      <Typography variant="h6" color="text.secondary">
        OpenDID Ledger Service Admin Console
      </Typography>
    </Box>
  );
};

export default DashboardPage;
