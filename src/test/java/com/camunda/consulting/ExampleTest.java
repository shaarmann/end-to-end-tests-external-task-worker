package com.camunda.consulting;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.concurrent.CountDownLatch;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.findId;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

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

  @MockBean
  WeatherService weatherService;

  @SpyBean
  WeatherWorker weatherWorker;


  @Test
  public void testProcessEndToEnd() throws InterruptedException {
    given(weatherService.getWeather("Berlin")).willReturn("sunshine");
    final CountDownLatch latch = new CountDownLatch(1);
    doAnswer(invocation -> {
      invocation.callRealMethod();
      latch.countDown();
      return null;
    }).when(weatherWorker).execute(any(), any());
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ExampleProcess");
    assertThat(processInstance).isWaitingAt(findId("request weather"));
    latch.await();
    assertThat(processInstance).isWaitingAt(findId("show weather")).task();
    complete(task());
    assertThat(processInstance).isEnded();
  }
}
