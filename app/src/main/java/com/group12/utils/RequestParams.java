package com.group12.utils;

import java.util.HashMap;

public class RequestParams extends HashMap{

    @Override
    public String toString(){
        StringBuilder res = new StringBuilder();
        for (Object key: keySet()){
            res.append(key.toString());
            res.append("=");
            res.append(get(key));
            res.append("&");
        }
        res.deleteCharAt(res.length()-1);
        return res.toString();
    }
}

