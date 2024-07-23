import axios from "axios";

const myAxios = axios.create({
    baseURL: 'http://localhost:8080/api',
});

myAxios.defaults.withCredentials = true; // 配置为true


// Add a request interceptor
myAxios.interceptors.request.use(function (config) {
    console.log("发送请求",config);
    return config;
}, function (error) {
    return Promise.reject(error);
});

// Add a response interceptor
myAxios.interceptors.response.use(function (response) {
    console.log("收到响应response.data",response.data);
    return response.data;
}, function (error) {
    return Promise.reject(error);
});


export default myAxios;