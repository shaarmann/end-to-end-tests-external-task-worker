package com.camunda.consulting;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ExternalTaskSubscription("requestWeatherInfo")
public class WeatherWorker implements ExternalTaskHandler {

  WeatherService weatherService;

  public WeatherWorker(WeatherService weatherService) {
    this.weatherService = weatherService;
  }

  @Override
  public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
    String location = externalTask.getVariable("location");
    String weather = weatherService.getWeather(location);
    Map<String, Object> vars = new HashMap<>();
    vars.put("weather", weather);
    externalTaskService.complete(externalTask, vars);
  }
}
