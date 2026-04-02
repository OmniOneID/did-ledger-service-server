import { Box, Link, styled, Typography, Chip } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core';
import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import CustomDataGrid from '../../components/data-grid/CustomDataGrid';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../components/dialog/CustomDialog';
import { fetchApiKeyList, getApiKeyInfo } from '../../apis/apikey-api';
import { useSession } from '../../context/SessionContext';
import { formatErrorMessage } from '../../utils/error-handler';
import CreateApiKeyDialog from './CreateApiKeyDialog';
import ApiKeyDetailDialog from './ApiKeyDetailDialog';

type Props = {}

type ApiKeyRow = {
  id: string | number;
  apiKey: string;
  maskedApiKey: string;
  name: string;
  description: string;
  role: string;
  isActive: boolean;
  lastUsedAt: string;
  expiresAt: string;
  createdAt: string;
  updatedAt: string;
};

const ApiKeyManagementPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [loading, setLoading] = useState<boolean>(false);
    const [totalRows, setTotalRows] = useState<number>(0);
    const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
    const [rows, setRows] = useState<ApiKeyRow[]>([]);
    const { session } = useSession(); 
    const [searchText, setSearchText] = useState<string>('');
    const [selectedSearch, setSelectedSearch] = useState<string>('name');
    const [createDialogOpen, setCreateDialogOpen] = useState<boolean>(false);
    const [detailDialogOpen, setDetailDialogOpen] = useState<boolean>(false);

    const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
        page: 0,
        pageSize: 10,
    });

    const selectedRowData = useMemo(() => {
        return rows.find(row => row.id === selectedRow) || null;
    }, [rows, selectedRow]);

    const fetchData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetchApiKeyList(
                paginationModel.page,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
        } catch (err) {
            console.error("Failed to fetch API Key List ", err);
            navigate('/error', { state: { message: formatErrorMessage(err, "Failed to retrieve API Key List") } });
        } finally {
            setLoading(false);
        }
    }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, navigate]);

    const getData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetchApiKeyList(
                0,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
            setPaginationModel((prev) => ({ ...prev, page: 0 }));
        } catch (err) {
            console.error("Failed to fetch API Key List ", err);
            setLoading(false);
            await dialogs.open(CustomDialog, {
                title: 'Notification',
                message: formatErrorMessage(err, 'Failed to retrieve API Key List'),
                isModal: true,
            });
        } finally {
            setLoading(false);
        }
    }, [paginationModel.pageSize, selectedSearch, searchText, navigate]);

    const handleSearch = useCallback(
        async (field: string, text: string) => {
            const trimmed = text.trim();
            console.log(trimmed);
            if (!trimmed) return;

            setSelectedSearch(field);
            setSearchText(trimmed);
            setPaginationModel((prev) => ({ ...prev, page: 0 }));
        }, []
    );

    const handleRowDoubleClick = (id: string | number) => {
        setSelectedRow(id);
        setDetailDialogOpen(true);
    };

    const handleDeactivateSuccess = () => {
        setDetailDialogOpen(false);
        fetchData(); // 비활성화 후 목록 갱신
    };

    const handleActivateSuccess = () => {
        setDetailDialogOpen(false);
        fetchData(); // 활성화 후 목록 갱신
    };

    const handleRenewSuccess = () => {
        setDetailDialogOpen(false);
        fetchData(); // 갱신 후 목록 갱신
    };

    const handleCreateApiKey = () => {
        setCreateDialogOpen(true);
    };

    const handleCreateSuccess = () => {
        setCreateDialogOpen(false);
        fetchData(); // Refresh the list with newly created API key
    };

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
      margin: 'auto',
      marginTop: theme.spacing(1),
      padding: theme.spacing(3),
      border: 'none',
      borderRadius: theme.shape.borderRadius,
      backgroundColor: '#ffffff',
      boxShadow: '0px 4px 8px 0px #0000001A',
    })), []);

    const StyledSubTitle = useMemo(() => styled(Typography)({
        textAlign: 'left',
        fontSize: '24px',
        fontWeight: 700,
    }), []);

    const getStatusChip = (isActive: boolean) => {
        return (
            <Chip
                label={isActive ? 'Active' : 'Inactive'}
                color={isActive ? 'success' : 'error'}
                size="small"
                variant="filled"
            />
        );
    };

    const getRoleChip = (role: string) => {
        const roleColors: Record<string, 'primary' | 'secondary' | 'warning'> = {
            'TAS': 'primary',      // 파란색
            'ISSUER': 'secondary', // 회색
            'READ': 'warning'      // 주황색
        };

        return (
            <Chip
                label={role}
                color={roleColors[role] || 'default'}
                size="small"
                variant="outlined"
            />
        );
    };
    
    return (
      <>
        <FullscreenLoader open={loading} />
        <StyledContainer>
          <StyledSubTitle>API Key Management</StyledSubTitle>
          <CustomDataGrid 
              rows={rows} 
              columns={[
                  { 
                      field: 'name', 
                      headerName: "Name", 
                      width: 150,
                      renderCell: (params) => (
                          <Typography
                              variant="body2"
                              sx={{
                                  fontWeight: 500,
                                  cursor: 'pointer',
                                  color: '#1976d2',
                                  '&:hover': {
                                      textDecoration: 'underline',
                                      backgroundColor: 'rgba(25, 118, 210, 0.04)',
                                  },
                                  padding: '4px 8px',
                                  borderRadius: '4px',
                                  transition: 'all 0.2s ease-in-out',
                              }}
                              onClick={(e) => {
                                  e.stopPropagation(); // 행 선택 방지
                                  handleRowDoubleClick(params.row.id);
                              }}
                              title="Click to view details"
                          >
                              {params.value}
                          </Typography>
                      ),
                  },
                  { 
                      field: 'maskedApiKey', 
                      headerName: "API Key", 
                      width: 250,
                      renderCell: (params) => (
                          <Typography
                              variant="body2"
                              sx={{
                                  fontFamily: 'monospace',
                                  fontSize: '12px',
                              }}
                          >
                              {params.value || 'N/A'}
                          </Typography>
                      ),
                  },
                  { 
                      field: 'isActive', 
                      headerName: "Status", 
                      width: 80,
                      renderCell: (params) => getStatusChip(params.value)
                  },
                  { 
                      field: 'role', 
                      headerName: "Role", 
                      width: 100,
                      renderCell: (params) => getRoleChip(params.value)
                  },
                  { field: 'expiresAt', headerName: "Expires At", width: 180},
                  { field: 'updatedAt', headerName: "Updated At", width: 180},
                  { field: 'lastUsedAt', headerName: "Last Used At", width: 180},
              ]} 
              selectedRow={selectedRow} 
              setSelectedRow={setSelectedRow}
              onRegister={handleCreateApiKey}
              paginationMode="server" 
              totalRows={totalRows} 
              paginationModel={paginationModel} 
              setPaginationModel={setPaginationModel}
              enableSearch={true}
              searchText={searchText}
              setSearchText={setSearchText}
              selectedSearch={selectedSearch}
              setSelectedSearch={setSelectedSearch}
              searchOptions={[
                  { value: 'name', label: 'Name' },
                  { value: 'role', label: 'Role' },
                  { value: 'isActive', label: 'Status' },
              ]}
              selectableFields={[
                  {
                      field: 'role',
                      options: [
                          { value: 'TAS', label: 'TAS' },
                          { value: 'ISSUER', label: 'ISSUER' },
                          { value: 'READ', label: 'READ' },
                      ]
                  },
                  {
                      field: 'isActive',
                      options: [
                          { value: 'true', label: 'Active' },
                          { value: 'false', label: 'Inactive' },
                      ]
                  },
              ]}
              onSearch={handleSearch}
              onRefresh={getData}
          />
        </StyledContainer>

        {/* Create API Key Dialog */}
        <CreateApiKeyDialog 
          open={createDialogOpen}
          onClose={() => setCreateDialogOpen(false)}
          onSuccess={handleCreateSuccess}
        />

        {/* API Key Detail Dialog */}
        {selectedRowData && (
          <ApiKeyDetailDialog 
            open={detailDialogOpen}
            onClose={() => setDetailDialogOpen(false)}
            apiKeyData={selectedRowData}
            onDeactivateSuccess={handleDeactivateSuccess}
            onActivateSuccess={handleActivateSuccess}
            onRenewSuccess={handleRenewSuccess}
          />
        )}
      </>
      
  )
}

export default ApiKeyManagementPage