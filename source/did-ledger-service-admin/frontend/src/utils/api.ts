import { tokenStore } from './authTokens';

type ApiResponse = { url: string; data: any };
type ApiErrorResponse = { url: string; status: number; code: string; message: string };


async function refreshAccessToken(baseUrl: string): Promise<string> {
  const refreshToken = tokenStore.getRefresh();
  if (!refreshToken) throw new Error('NO_REFRESH_TOKEN');

  const res = await fetch(`${baseUrl}/refresh-token`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken }),
  });

  if (!res.ok) throw new Error(`REFRESH_FAILED_${res.status}`);
  const { accessToken } = await res.json();
  if (!accessToken) throw new Error('REFRESH_NO_ACCESS');
  tokenStore.writeAccess(accessToken);
  return accessToken;
}

export const requestApi = async (
    baseUrl: string,
    endpoint: string,
    method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH',
    body?: any,
    _retry = false,
): Promise<ApiResponse> => {
  const fullUrl = `${baseUrl}/${endpoint}`;
  const isFormData = body instanceof FormData;
  const accessToken = tokenStore.getAccess();

  const headers: HeadersInit | undefined = isFormData
      ? undefined
      : {
        'Content-Type': 'application/json',
        ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
      };

  const res = await fetch(fullUrl, {
    method,
    headers,
    body: isFormData ? body : body ? JSON.stringify(body) : undefined,
  });

  const text = await res.text();
  let data: any = {};
  if (text) {
    try { data = JSON.parse(text); } catch (err){
      console.log(err);
    }
  }

  if (res.status === 403) {
    // activeSession 마커도 클리어
    sessionStorage.removeItem('activeSession');
    window.location.replace('/sign-in');
  }

  if (res.status === 401 && !_retry) {
    try {
      await refreshAccessToken(baseUrl);
      return requestApi(baseUrl, endpoint, method, body, true);
    } catch {
      tokenStore.clear();
      // activeSession 마커도 클리어
      sessionStorage.removeItem('activeSession');
      window.location.replace('/sign-in');
    }
  }

  if (!res.ok) {
    throw {
      url: fullUrl,
      status: res.status,
      code: typeof data?.code === 'string' ? data.code : 'UNKNOWN_ERROR',
      message:
          typeof data?.description === 'string'
              ? data.description
              : `HTTP error! Status: ${res.status}`,
    } as ApiErrorResponse;
  }

  return { url: fullUrl, data };
};

export const getData = (baseUrl: string, endpoint: string): Promise<ApiResponse> =>
    requestApi(baseUrl, endpoint, "GET");

export const postData = (baseUrl: string, endpoint: string, body: any): Promise<ApiResponse> =>
    requestApi(baseUrl, endpoint, "POST", body);

export const putData = (baseUrl: string, endpoint: string, body: any): Promise<ApiResponse> =>
    requestApi(baseUrl, endpoint, "PUT", body);

export const deleteData = (baseUrl: string, endpoint: string): Promise<ApiResponse> =>
    requestApi(baseUrl, endpoint, "DELETE");

export const uploadData = (baseUrl: string, endpoint: string, formData: FormData): Promise<ApiResponse> =>
    requestApi(baseUrl, endpoint, "POST", formData);