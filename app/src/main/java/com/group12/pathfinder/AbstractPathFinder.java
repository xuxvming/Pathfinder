package com.group12.pathfinder;

import android.os.AsyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


public abstract class AbstractPathFinder {

    private static final Logger LOGGER =  LoggerFactory.getLogger(AbstractPathFinder.class);

    private final String origin;
    private final String destination;
    private final String url;
    private String departureTime;
    private String mode;

    public AbstractPathFinder(String origin, String destination,String url) {
        this.origin = origin;
        this.destination = destination;
        this.url =  url;
    }

    public void makeRequest(){
        RequestMaker requestMaker = new RequestMaker();
        requestMaker.execute(createURl());
    }

    public abstract String createURl();

    String getUrl() {
        return url;
    }

    String getOrigin() { return origin; }

    String getDestination() { return destination; }

    String getDepartureTime() { return departureTime; }

    String getMode() { return mode; }

    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

  private static class RequestMaker extends AsyncTask<String, Void, String>{

      @Override
      protected String doInBackground(String... params) {
          try{
              String link = params[0];
              URL url = new URL(link);
              InputStream inputStream = url.openConnection().getInputStream();
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
            LOGGER.info(res);
      }
  }
}


