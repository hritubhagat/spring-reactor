package com.sapient.publicis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mgmresorts.aurora.messages.GetCustomerBalancesFullRequest;
import com.mgmresorts.aurora.messages.GetCustomerBalancesFullResponse;
import com.mgmresorts.aurora.messages.MessageFactory;
import com.sapient.publicis.event.DMPEvent;

import reactor.core.publisher.ReplayProcessor;

@Service
public class CustomerBalancesServiceImpl implements CustomerBalancesSerivce{


    @Autowired
    private ReplayProcessor<DMPEvent> rp;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerBalancesServiceImpl.class);
    
    public GetCustomerBalancesFullResponse getCustomerBalances(String customerId) {

        GetCustomerBalancesFullRequest balancesRequest = MessageFactory.createGetCustomerBalancesFullRequest();
//        balancesRequest.setCustomerId(Long.parseLong(customerId));
        balancesRequest.setCustomerId(46979088385L);
        DMPEvent customerBalanceEvent = new DMPEvent();
        customerBalanceEvent.setEventName("CustomerBalEvent");
        customerBalanceEvent.setBalancesRequest(balancesRequest);
        rp.onNext(customerBalanceEvent);
//        rp.onNext(new DMPEvent("event2"));
//        rp.onNext(new DMPEvent("event3"));
        return customerBalanceEvent.getBalancesResponse();
    }

}
