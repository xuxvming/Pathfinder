package com.group12.pathfinder;

import android.os.AsyncTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutionException;


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

    public AbstractDirectionsObject makeRequest(){
        RequestMaker requestMaker = new RequestMaker();
        AbstractDirectionsObject abstractDirectionsObject = new AbstractDirectionsObject();
        try {
            String res = requestMaker.execute(createURl()).get();
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(res);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonArray routes = jsonObject.getAsJsonArray("routes");
            String polyline = routes.get(0).getAsJsonObject().get("overview_polyline").getAsJsonObject().get("points").getAsString();
            JsonObject starLocation = (JsonObject) routes.get(0).getAsJsonObject().get("legs").getAsJsonArray().get(0).getAsJsonObject().get("start_location");
            JsonObject endLocation = (JsonObject) routes.get(0).getAsJsonObject().get("legs").getAsJsonArray().get(0).getAsJsonObject().get("end_location");
            abstractDirectionsObject.setOverviewPolyline(polyline);
            abstractDirectionsObject.setOriginLat(starLocation.get("lat").getAsDouble());
            abstractDirectionsObject.setOriginLng(starLocation.get("lng").getAsDouble());
            abstractDirectionsObject.setDestinationLat(endLocation.get("lat").getAsDouble());
            abstractDirectionsObject.setDestinationLng(endLocation.get("lng").getAsDouble());
        } catch (ExecutionException e ) {
            LOGGER.error("Error getting request",e);
        } catch (InterruptedException e) {
            LOGGER.error("Request Interrupted",e);
        }
        return abstractDirectionsObject;
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
          //handle the invalid response
      }

  }
}


