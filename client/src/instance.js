import axios from "axios";

/**
 * @description Axios 인스턴스를 생성
 * 목적 : axios 요청 시 header에 token을 보내기 위함
 */

const instance = axios.create({
  baseURL: process.env.REACT_APP_BASE_URL, // 서버 포트 ex) http://localhost:8080/
});

instance.interceptors.request.use(
  (config) => {
    const accessToken = localStorage.getItem("authorization");
    try {
      if (accessToken) {
        config.headers["Authorization"] = `${accessToken}`;
      }
      return config;
    } catch (err) {
      console.error("[_axios.interceptors.request] config : " + err.message);
    }
    return config;
  },
  (error) => {
    // 요청 에러 직전 호출됩니다.
    return Promise.reject(error);
  }
);

instance.interceptors.response.use(
  (response) => {
    if (response.status === 404) {
      console.log('404 페이지로 넘어가야 함!');
    }

    return response;
  },
  async (error) => {
    if (error.response?.status === 401) {
      /* if (isTokenExpired()) await tokenRefresh();

      const accessToken = getToken();

      error.config.headers = {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      };

      const response = await axios.request(error.config);
      return response; */
    }
    return Promise.reject(error);
  }
);

export default instance;