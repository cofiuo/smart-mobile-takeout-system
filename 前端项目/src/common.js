//显示当前时间 yyyy-MM-dd
//2025-06-01
export function getCurDate(){
    let d = new Date();
    let year = d.getFullYear();   //得到年份
    let month = d.getMonth()+1;   //枚举方式
    let day = d.getDate();
    
    month = month<10?'0'+month:month;
    day = day<10?'0'+day:day;

    return `${year}-${month}-${day}`;
}

//SessionStorage存  key-value
export function setSessionStorage(key,value){
    //传入value可以是任何类型，传入value可以json对象。
    //sessionStorage 存储只能是存字符串数据
    sessionStorage.setItem(key,JSON.stringify(value));
}

//获取SessionStorage的数据
export function getSessionStorage(key){
    let str = sessionStorage.getItem(key);
    if(str==''||str==null||str=='null'||str==undefined){
        return null;
    }else{
        return JSON.parse(str);  //将字符串转换json对象
    }
}

//删除SessionStorage的方法
export function removeSessionStorage(key){
    sessionStorage.removeItem(key);
}


//localStorage存  key-value
export function setLocalStorage(key,value){
    localStorage.setItem(key,JSON.stringify(value));
}

//获取localStorage的数据
export function getLocalStorage(key){
    let str = localStorage.getItem(key);
    if(str==''||str==null||str=='null'||str==undefined){
        return null;
    }else{
        return JSON.parse(str);  //将字符串转换json对象
    }
}

//删除localStorage的方法
export function removeLocalStorage(key){
    localStorage.removeItem(key);
}