package com.group12.utils;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class RequestParamsTest {

    private RequestParams requestParams;

    @Test
    public void testToString(){
        String expected = "test=string&hello=world";
        requestParams = new RequestParams();
        requestParams.put("hello","world");
        requestParams.put("test","string");
        assertEquals(expected,requestParams.toString());
    }
}
