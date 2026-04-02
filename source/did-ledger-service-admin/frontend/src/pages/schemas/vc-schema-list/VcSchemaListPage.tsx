import { Box, Link, styled, Typography, Tooltip } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core';
import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { fetchVcSchemaList } from '../../../apis/vc-schema-api';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}

type VcSchemaRow = {
  id: string | number;
  schemaId: string;
  title: string;
  version: string;
  description: string;
  did: string;
  createdAt: string;
  updatedAt: string;
};

const VcSchemaListPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [loading, setLoading] = useState<boolean>(false);
    const [totalRows, setTotalRows] = useState<number>(0);
    const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
    const [rows, setRows] = useState<VcSchemaRow[]>([]);
    const [searchText, setSearchText] = useState<string>('');
    const [selectedSearch, setSelectedSearch] = useState<string>('schemaId');

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
            const response = await fetchVcSchemaList(
                paginationModel.page,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
        } catch (err) {
            console.error("Failed to fetch VC Schema List ", err);
            navigate('/error', { state: { message: formatErrorMessage(err, "Failed to retrieve VC Schema List") } });
        } finally {
            setLoading(false);
        }
    }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, navigate]);

    const getData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetchVcSchemaList(
                0,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
            setPaginationModel((prev) => ({ ...prev, page: 0 }));
        } catch (err) {
            console.error("Failed to fetch VC Schema List ", err);
            setLoading(false);
            await dialogs.open(CustomDialog, {
                title: 'Notification',
                message: formatErrorMessage(err, 'Failed to retrieve VC Schema List'),
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

    // Truncate long strings for display with tooltip
    const truncateString = (str: string, maxLength: number) => {
        if (str.length <= maxLength) return str;
        return str.substring(0, maxLength) + '...';
    };

    // Render cell with conditional tooltip
    const renderCellWithTooltip = (value: string, maxLength: number, isLink: boolean = false, onClick?: () => void) => {
        if (!value) return null;
        
        const isTruncated = value.length > maxLength;
        const displayValue = isTruncated ? truncateString(value, maxLength) : value;
        
        const content = isLink ? (
            <Link 
                component="button"
                variant='body2'
                onClick={onClick}
                sx={{ 
                    cursor: 'pointer', 
                    color: 'primary.main', 
                    textAlign: 'left',
                    display: 'inline-block',
                    maxWidth: '100%'
                }}
            >
                {displayValue}
            </Link>
        ) : (
            <span style={{ 
                display: 'inline-block',
                maxWidth: '100%'
            }}>
                {displayValue}
            </span>
        );
        
        return isTruncated ? (
            <Tooltip title={value} arrow placement="top">
                {content}
            </Tooltip>
        ) : content;
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
    
    return (
      <>
        <FullscreenLoader open={loading} />
        <StyledContainer>
          <StyledSubTitle>VC Schema List</StyledSubTitle>
          <CustomDataGrid 
              rows={rows} 
              columns={[
                  { 
                      field: 'schemaId', 
                      headerName: "Schema ID", 
                      width: 350,
                      renderCell: (params) => renderCellWithTooltip(
                          params.value, 
                          50,
                          true, 
                          () => navigate(`/schemas/vc-schema-list/${params.row.id}`)
                      ),
                  },
                  { 
                      field: 'title', 
                      headerName: "Title", 
                      width: 200,
                      renderCell: (params) => renderCellWithTooltip(params.value, 25)
                  },
                  { 
                      field: 'version', 
                      headerName: "Version", 
                      width: 100,
                      align: 'center',
                      headerAlign: 'center'
                  },
                  { 
                      field: 'did', 
                      headerName: "Publisher DID", 
                      width: 250,
                      renderCell: (params) => renderCellWithTooltip(params.value, 35)
                  },
                  { 
                      field: 'createdAt', 
                      headerName: "Created At", 
                      width: 150,
                      align: 'center',
                      headerAlign: 'center'
                  },
              ]} 
              selectedRow={selectedRow} 
              setSelectedRow={setSelectedRow}
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
                  { value: 'schemaId', label: 'Schema ID' },
                  { value: 'title', label: 'Title' },
                  { value: 'version', label: 'Version' },
                  { value: 'did', label: 'Publisher DID' },
              ]}
              onSearch={handleSearch}
              onRefresh={getData}
          />
        </StyledContainer>
      </>
    )
}

export default VcSchemaListPage
