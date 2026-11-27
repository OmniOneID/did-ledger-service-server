// The server stores "No Limit" API keys with this expiry and lists them as NO_LIMIT_TEXT.
const NO_LIMIT_EPOCH_MS = Date.UTC(9999, 11, 31, 23, 59, 59);
const NO_LIMIT_TEXT = '9999-12-31 23:59:59';

const pad = (value: number) => String(value).padStart(2, '0');

/**
 * Formats a date as `yyyy-MM-dd HH:mm:ss` in local time, the format the admin lists use.
 * Values at or beyond the "No Limit" expiry are shown as `9999-12-31 23:59:59`.
 */
export const formatDateTime = (value: Date | string | null | undefined): string => {
    if (value === null || value === undefined || value === '') return '';
    const date = value instanceof Date ? value : new Date(value);
    if (Number.isNaN(date.getTime())) return String(value);
    if (date.getTime() >= NO_LIMIT_EPOCH_MS || date.getFullYear() > 9999) return NO_LIMIT_TEXT;
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} `
        + `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
};
