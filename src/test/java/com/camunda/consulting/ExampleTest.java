package com.camunda.consulting;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.findId;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.mockito.BDDMockito.given;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
    "camunda.bpm.client.base-url=http://localhost:8080/engine-rest",
    "lock-duration=120000",
    "server.port=8080"
    })
public class ExampleTest {

  @Autowired
  RuntimeService runtimeService;

  @Mock
  WeatherService weatherService;


  @Test
  public void testProcessEndToEnd() throws InterruptedException {
    given(weatherService.getWeather("Berlin")).willReturn("sunshine");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ExampleProcess");
    assertThat(processInstance).isWaitingAt(findId("request weather"));
    Thread.sleep(10000);
    assertThat(processInstance).isWaitingAt(findId("show weather")).task();
    complete(task());
    assertThat(processInstance).isEnded();
  }
}
