/*package com.sapient.publicis.controller;


import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sapient.publicis.service.SearchContentService;


@RestController
@RequestMapping(value = "/searchCtl")
public class SearchController {
    @Autowired
    private SearchContentService searchService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/search.sjson", produces = { MediaType.APPLICATION_JSON_VALUE})
    public Mono<String> searchContent(HttpServletRequest request){
        LOGGER.info("search controller start");
        Mono<String> mono = searchService.searchContent();
        LOGGER.info("search controller end, changed just now");
        return mono;
    }
}
*/