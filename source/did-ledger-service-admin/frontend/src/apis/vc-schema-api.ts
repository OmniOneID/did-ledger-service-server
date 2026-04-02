import { getData } from "../utils/api";

const API_BASE_URL = "/lss/admin/v1";

// Fetch VC Schema list with pagination and search
export const fetchVcSchemaList = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `vc-schema/list?${params.toString()}`);
}

// Get VC Schema by ID
export const getVcSchemaInfo = async (id: number) => {
    return getData(API_BASE_URL, `vc-schema?id=${id}`);
}

// Get VC Schema detailed information
export const getVcSchemaDetail = async (id: number) => {
    return getData(API_BASE_URL, `vc-schema/detail?id=${id}`);
}
