package com.group12.utils;

import android.os.AsyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestMaker extends AsyncTask<String, Void, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMaker.class);

    private String requestMethod = "GET";

    @Override
    protected String doInBackground(String... params) {
        try{
            String link = params[0];
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestMethod);
            InputStream inputStream = connection.getInputStream();
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) !=null){
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            LOGGER.error("Error making request" ,e );
        }
        return null;
    }

    @Override
    protected void onPostExecute(String res){
        //TODO handle the invalid response, add a callback listener
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }



}
