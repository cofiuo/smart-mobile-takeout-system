package com.go.common;
import lombok.Data;

@Data
public class Result {
    private Integer code;
    private String message;
    private Object resultData;

    //成功：  重载
    public static Result success(int code,String message,Object data){
        Result r = new Result();
        r.setCode(code);
        r.setMessage(message);
        r.setResultData(data);
        return r;
    }

    public static Result success(Object data){
        return success(20000,"操作成功",data);
    }

    //失败:
    public static Result fail(int code,String message,Object data){
        Result r = new Result();
        r.setCode(code);
        r.setMessage(message);
        r.setResultData(data);
        return r;
    }
    public static Result fail(String message){
        return fail(400,message,null);
    }
}
