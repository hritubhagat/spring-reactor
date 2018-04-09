/*package com.sapient.publicis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sapient.publicis.dao.SearchContentDao;

import reactor.core.publisher.Mono;

@Service
public class SearchContentServiceImpl implements SearchContentService{
    
    @Autowired
    private SearchContentDao searchDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchContentServiceImpl.class);
    public Mono<String> searchContent() {
        LOGGER.info("Search content service start");
        Mono<String> mono= searchDao.searchContent();
        LOGGER.info("Search content service end");
        return mono;
    }
}
*/