package cn.ommiao.network;


import java.util.HashMap;

import cn.ommiao.bean.JavaBean;

public class RequestInBase extends JavaBean {

    public RequestInBase() {

    }

    protected HashMap<String, String> body(){
        return new HashMap<>();
    }

}
