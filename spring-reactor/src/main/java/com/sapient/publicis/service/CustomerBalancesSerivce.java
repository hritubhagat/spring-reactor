package com.sapient.publicis.service;

import com.mgmresorts.aurora.messages.GetCustomerBalancesFullResponse;

public interface CustomerBalancesSerivce {

    public GetCustomerBalancesFullResponse getCustomerBalances(String customerId);
}
