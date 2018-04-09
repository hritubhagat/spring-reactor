/*package com.sapient.publicis.helper;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mgmresorts.aurora.messages.GetCustomerBalancesFullRequest;
import com.mgmresorts.aurora.messages.MessageFactory;
import com.sapient.publicis.controller.CustomerBalancesController;

import reactor.bus.Event;
import reactor.bus.EventBus;

public class EventGenerator {

    @Autowired
    private EventBus eventBus;

    @Autowired
    CountDownLatch latch;
    private static final Logger LOGGER = LoggerFactory.getLogger(EventGenerator.class);

    public void publishEmployee(int numberOfEmp) throws InterruptedException {
        GetCustomerBalancesFullRequest balancesRequest = MessageFactory.createGetCustomerBalancesFullRequest();
        balancesRequest.setCustomerId(Long.parseLong(request.getParameter("customerId")));
        LOGGER.info("getCustomerBalancesFull Request : {}", balancesRequest.toJsonString());
        eventBus.notify("customerBalancesFullConsumer", Event.wrap(balancesRequest));
        latch.await();
    }
}
*/