export const REACT_APP_API_BASE_URL = process.env.REACT_APP_API_BASE_URL ?? "http://localhost:8888";

export const URLS = {
    LOGIN: `${REACT_APP_API_BASE_URL}/auth/login`,
    REGISTER: `${REACT_APP_API_BASE_URL}/auth/register`
}

