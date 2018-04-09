/*package com.sapient.publicis.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import reactor.core.publisher.Mono;

@Component
public class SearchContentDaoImpl implements SearchContentDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchContentDaoImpl.class);
    public Mono<String> searchContent() {
        
        LOGGER.info("SearchContentDaoImpl.searchContent start");
        RestTemplate restTemplate = new RestTemplate();
        String uri = "http://sapestage-mgmgrand.sapient.com/mgm-web/search/en/v1/66964e2b-2550-4476-84c3-1a4c0c5c067f/search?"
                + "q=ka&source=DMP&staticDomain=&page=/content/mgm-grand/en/search.html&";
        Mono<String> monoData= Mono.just(restTemplate.getForObject(uri, String.class));
        LOGGER.info("SearchContentDaoImpl.searchContent end");
        return monoData;
    }
}
*/