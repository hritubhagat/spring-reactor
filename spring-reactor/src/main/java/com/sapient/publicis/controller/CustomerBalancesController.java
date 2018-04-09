package com.sapient.publicis.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mgmresorts.aurora.messages.GetCustomerBalancesFullResponse;
import com.sapient.publicis.service.CustomerBalancesSerivce;

@Controller
@RequestMapping(
        value = "/customerBalancesCtl")
public class CustomerBalancesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerBalancesController.class);
    
    @Autowired
    private CustomerBalancesSerivce balancesService;
    
    @ResponseBody
    @ResponseStatus(
            value = HttpStatus.OK)
    @RequestMapping(
            value = "/getBalances.sjson", produces = { MediaType.APPLICATION_JSON_VALUE })
    public GetCustomerBalancesFullResponse getBalancesFull(HttpServletRequest request) {
        LOGGER.info("customer balances controller start");
        GetCustomerBalancesFullResponse balancesResponse = balancesService
                .getCustomerBalances(request.getParameter("customerId"));
        LOGGER.info("customer balances end");
        return balancesResponse;
    }
}
