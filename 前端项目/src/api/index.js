import axios from "axios";
import qs from "qs";

//创建axios对象，做一些基础设置
// http://localhost:10001/account/login
const axiosInstance = axios.create({
    baseURL: "http://localhost:10001",
    timeout: 50000,
});


// url：/account/login 



export function get(url) {
    return axiosInstance.get(url);
}


export function post(url,param,isJSON) {
    let hearders={
        'Content-Type':'application/json'
    }
    //isJSON true表示传递参数是json格式,false表示传递参数是 参数形式(qs)
    if(!isJSON){
        param=qs.stringify(param); //将json转化为 参数形式
        hearders['Content-Type']='application/x-www-form-urlencoded;charset=UTF-8';
    }
    return axiosInstance.post(url,param,hearders);

}