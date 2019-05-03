package com.hong.flowable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j2
public class FlowableApplicationTest {

	private static final String EMAIL_EMAIL_COM = "email@email.com";

	@Autowired
	RuntimeService runtimeService;

	@Autowired
	TaskService taskservice;
	
	@Autowired
	EmailService emailService;

	private static final String EMAIL2 = "email";
	private static final String CUSTOMER_ID = "customerId";

	@Test
	public void contextLoads() throws Exception {
		String customerId = "1";
		String processInstanceId = this.beginCustomerEnrollmentProcess(customerId, EMAIL_EMAIL_COM);
		log.info("process instance ID: " + processInstanceId);

		Assert.assertNotNull(processInstanceId, "The process instance ID should not be null");

		// get outstanding tasks
		List<Task> tasks = this.taskservice
			.createTaskQuery()
			.taskName("confirm-email-task")
			.includeProcessVariables()
			.processVariableValueEquals(CUSTOMER_ID, customerId)			
			.list();
		
		Assert.assertTrue("There should be one outstanding", tasks.size() >= 1);
		// async
		// this.confirmEmail(customerId);
		
		// complete outstanding tasks
		tasks.forEach(task -> {
			this.taskservice.claim(task.getId(), "hong");
			this.taskservice.complete(task.getId());
		});

		// confirm that email has been sent
		Assert.assertEquals(this.emailService.sends.get(EMAIL_EMAIL_COM), 1);
	}

	String beginCustomerEnrollmentProcess(String customerId, String email) {
		Map<String, Object> vars = new HashMap<>();
		vars.put(CUSTOMER_ID, customerId);
		vars.put(EMAIL2, email);

		ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey("signup-process", vars);

		return processInstance.getId();
	}

	void confirmEmail(String customerId) {
		// TODO Auto-generated method stub

	}

}
