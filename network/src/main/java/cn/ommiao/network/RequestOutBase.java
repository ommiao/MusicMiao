package cn.ommiao.network;

import cn.ommiao.bean.JavaBean;

public class RequestOutBase<T extends JavaBean> extends JavaBean {

    private int code;
    private String message;
    private T data;

    public RequestOutBase() {

    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData(){
        return data;
    }

    public boolean isDataValid(){
        return data != null;
    }
}
