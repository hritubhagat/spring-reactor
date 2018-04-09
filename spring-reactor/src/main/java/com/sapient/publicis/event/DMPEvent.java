package com.sapient.publicis.event;

import com.mgmresorts.aurora.messages.GetCustomerBalancesFullRequest;
import com.mgmresorts.aurora.messages.GetCustomerBalancesFullResponse;

public class DMPEvent {

    private String eventName = "";
    
    private GetCustomerBalancesFullRequest balancesRequest;
    private GetCustomerBalancesFullResponse balancesResponse;

    public DMPEvent() {
    }
    
    public DMPEvent(String eventName) {
        this.eventName = eventName;
    }
    
    public String getEventName() {
        return eventName;
    }


    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    
    public GetCustomerBalancesFullRequest getBalancesRequest() {
        return balancesRequest;
    }

    public void setBalancesRequest(GetCustomerBalancesFullRequest balancesRequest) {
        this.balancesRequest = balancesRequest;
    }

    public void filterInterest1(DMPEvent dmpEvent) {
        
    }

    public GetCustomerBalancesFullResponse getBalancesResponse() {
        return balancesResponse;
    }

    public void setBalancesResponse(GetCustomerBalancesFullResponse balancesResponse) {
        this.balancesResponse = balancesResponse;
    }
    
    
}
