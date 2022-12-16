package com.camunda.consulting;

import org.camunda.connect.Connectors;
import org.camunda.connect.httpclient.HttpConnector;
import org.springframework.stereotype.Component;

@Component
public class WeatherService {

  public String getWeather(String location) {
    HttpConnector http = Connectors.getConnector(HttpConnector.ID);
    return http.createRequest()
        .get()
        .url("https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=2662e1423eb727621234567890987654" )
        .execute()
        .getResponse();
  }
}
