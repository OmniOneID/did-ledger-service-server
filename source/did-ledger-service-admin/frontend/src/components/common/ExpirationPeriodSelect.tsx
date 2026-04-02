import React from 'react';
import {
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Typography,
    SelectChangeEvent,
} from '@mui/material';

interface ExpirationPeriodSelectProps {
    value: number;
    onChange: (days: number) => void;
    error?: string;
    required?: boolean;
    disabled?: boolean;
    label?: string;
    fullWidth?: boolean;
}

const EXPIRATION_OPTIONS = [
    { value: 7, label: '1 week (7 days)' },
    { value: 14, label: '2 weeks (14 days)' },
    { value: 30, label: '1 month (30 days)' },
    { value: 60, label: '2 months (60 days)' },
    { value: 90, label: '3 months (90 days)' },
    { value: 180, label: '6 months (180 days)' },
    { value: 365, label: '1 year (365 days)' },
];

const ExpirationPeriodSelect: React.FC<ExpirationPeriodSelectProps> = ({
    value,
    onChange,
    error,
    required = false,
    disabled = false,
    label = 'Expiration Period',
    fullWidth = true,
}) => {
    const handleChange = (event: SelectChangeEvent<string>) => {
        const days = parseInt(event.target.value);
        onChange(days);
    };

    const displayLabel = required ? `${label} *` : label;

    return (
        <FormControl fullWidth={fullWidth} error={!!error}>
            <InputLabel>{displayLabel}</InputLabel>
            <Select
                value={value.toString()}
                onChange={handleChange}
                label={displayLabel}
                disabled={disabled}
            >
                {EXPIRATION_OPTIONS.map((option) => (
                    <MenuItem key={option.value} value={option.value.toString()}>
                        {option.label}
                    </MenuItem>
                ))}
            </Select>
            {error && (
                <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 1.5 }}>
                    {error}
                </Typography>
            )}
        </FormControl>
    );
};

export default ExpirationPeriodSelect;
export { EXPIRATION_OPTIONS };