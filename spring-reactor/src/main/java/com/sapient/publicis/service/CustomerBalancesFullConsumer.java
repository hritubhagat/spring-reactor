/*package com.sapient.publicis.service;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.mgmresorts.aurora.messages.GetCustomerBalancesFullRequest;
import com.mgmresorts.aurora.messages.GetCustomerBalancesFullResponse;

import reactor.bus.Event;

public class CustomerBalancesFullConsumer implements Consumer<Event<GetCustomerBalancesFullRequest>> {

    @Autowired
    CountDownLatch latch;

    public void accept(Event<GetCustomerBalancesFullRequest> balancesDataEvent) {
        RestTemplate restTemplate = new RestTemplate();
        GetCustomerBalancesFullRequest requestData = balancesDataEvent.getData();
        String requestParam = requestData.toJsonString();
        restTemplate.getForObject("http://localhost:9998/tpws/customer/balancesFull?request={requestParam}", GetCustomerBalancesFullResponse.class,requestParam);
        latch.countDown();
    }

}
*/