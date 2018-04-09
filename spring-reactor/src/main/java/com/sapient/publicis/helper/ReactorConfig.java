package com.sapient.publicis.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mgmresorts.aurora.messages.GetCustomerBalancesFullRequest;
import com.mgmresorts.aurora.messages.GetCustomerBalancesFullResponse;
import com.sapient.publicis.event.DMPEvent;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

@Configuration
public class ReactorConfig {
    private static final int NUMBER_OF_EMP = 0;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactorConfig.class);

    @Autowired
    CountDownLatch latch;

    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${http.proxy.host:}")
    private String proxyHost;

    @Value("${http.proxy.port:}")
    private String proxyPort;

    @Value("${http.proxy.user:}")
    private String proxyUser;

    @Value("${http.proxy.pass:}")
    private String proxyPass;

    @Bean
    ReplayProcessor<DMPEvent> createReplayProcessor() {

        ReplayProcessor<DMPEvent> rp = ReplayProcessor.create();

        // filter() method below evaluates each source value(event is this case)
        // against the given Predicate. If the
        // predicate test succeeds, the value is emitted. If the predicate test
        // fails, the value is ignored and a request of 1 is made upstream.

        Flux<DMPEvent> interest1 = rp.filter(event -> filterInterest1(event));

        // Flux containing only values(events in this case) that pass the
        // predicate test

        Flux<DMPEvent> interest2 = rp.filter(event -> filterInterest2(event));

        interest1.subscribe(new BaseSubscriber<DMPEvent>() {

            /**
             * Hook for further processing of onSubscribe's Subscription.
             * Implement this method to call request(long) as an initial
             * request. Values other than the unbounded Long.MAX_VALUE imply
             * that you'll also call request in hookOnNext(Object).
             */

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                // Defaults to request unbounded Long.MAX_VALUE
                requestUnbounded();
            }

            /**
             * Hook for processing of onNext values. You can call request(long)
             * here to further request data from the source Publisher if the
             * initial request wasn't unbounded.Defaults to doing nothing. value
             * - the emitted value to process
             */

            @Override
            protected void hookOnNext(DMPEvent value) {

                GetCustomerBalancesFullRequest requestData = value.getBalancesRequest();
                LOGGER.info("inside hookOnNext::" + requestData.toJsonString());

                String uri = "https://10.119.221.156/tpws/customer/balancesFull?request={requestParam}";
                String requestParam = requestData.toJsonString();
                Map<String, String> params = new HashMap<String, String>();
                params.put("requestParam", requestParam);
                GetCustomerBalancesFullResponse response = get(uri,
                        GetCustomerBalancesFullResponse.class, params);

                LOGGER.info("event 1 handler -> event name:" + value.getEventName());
                LOGGER.info(response.toJsonString());
                // System.out.println(response.toJsonString());

                try {
                    // LOGGER.info("Waiting for response.");
                    latch.await();

                } catch (InterruptedException e) {
                    LOGGER.info("Thread interrupted::{}", e);
                }
                latch.countDown();
                LOGGER.info("Waiting for response.");
            }

        });

        interest2.subscribe(new BaseSubscriber<DMPEvent>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                requestUnbounded();
            }

            @Override
            protected void hookOnNext(DMPEvent value) {
                // todo: call service method
                System.out.println("event2 handler -> event name:" + value.getEventName());
            }
        });

        return rp;
    }

    @Bean
    public CountDownLatch latch() {
        return new CountDownLatch(NUMBER_OF_EMP);
    }

    public boolean filterInterest1(DMPEvent dmpEvent) {
        if (dmpEvent != null && dmpEvent.getEventName() != null
                && dmpEvent.getEventName().equalsIgnoreCase("CustomerBalEvent")) {
            return true;
        }
        return false;
    }

    public boolean filterInterest2(DMPEvent dmpEvent) {
        if (dmpEvent != null && dmpEvent.getEventName() != null && dmpEvent.getEventName().equalsIgnoreCase("event2")) {
            return true;
        }
        return false;
    }

    @PostConstruct
    private void initRestTemplate() {

        LOGGER.info("Auro Rest Template initialization started");
        HttpComponentsClientHttpRequestFactory auroraClientHttpFactory = null;
        ClientHttpRequestFactory clientHttpFactory = restTemplate.getRequestFactory();

        if (null != clientHttpFactory && clientHttpFactory instanceof HttpComponentsClientHttpRequestFactory
                && StringUtils.isNotEmpty(proxyHost) && NumberUtils.isNumber(proxyPort)) {
            auroraClientHttpFactory = (HttpComponentsClientHttpRequestFactory) clientHttpFactory;
            HttpHost proxy = new HttpHost(proxyHost, NumberUtils.toInt(proxyPort));
            HttpClient client;
            CredentialsProvider credsProvider;
            if (StringUtils.isNotEmpty(proxyUser) && StringUtils.isNotEmpty(proxyPass)) {
                credsProvider = new BasicCredentialsProvider();
                Credentials credentials = new UsernamePasswordCredentials(proxyUser, proxyPass);
                credsProvider.setCredentials(new AuthScope(null, -1), credentials);
                client = HttpClientBuilder.create().setProxy(proxy).setDefaultCredentialsProvider(credsProvider)
                        .build();
                auroraClientHttpFactory.setHttpClient(client);
                return;
            }
            client = HttpClientBuilder.create().setProxy(proxy).build();
            auroraClientHttpFactory.setHttpClient(client);
        }

        // Add headers
        final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        final HeaderInterceptor headerInterceptor = new HeaderInterceptor();
        interceptors.add(headerInterceptor);

        restTemplate
                .setRequestFactory(new InterceptingClientHttpRequestFactory(clientHttpFactory, interceptors));

        // Add custom JSON parser
        final List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

        messageConverters.add(new StringHttpMessageConverter());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new SimpleModule().addSerializer(DateTime.class, new CustomDateTimeSerializer()));
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        final MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        final List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        acceptableMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        acceptableMediaTypes.add(MediaType.parseMediaType("text/javascript"));
        mappingJacksonHttpMessageConverter.setSupportedMediaTypes(acceptableMediaTypes);
        mappingJacksonHttpMessageConverter.setObjectMapper(mapper);
        messageConverters.add(mappingJacksonHttpMessageConverter);

        restTemplate.setMessageConverters(messageConverters);
    }

    private class CustomDateTimeSerializer extends JsonSerializer<DateTime> {
        @Override
        public void serialize(DateTime value, JsonGenerator jsonGenerator, SerializerProvider provider)
                throws IOException {
            jsonGenerator.writeString(ISODateTimeFormat.dateTime().print(value));
        }
    }

    private class HeaderInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            final HttpRequest wrapper = new HttpRequestWrapper(request);
            final HttpHeaders headers = wrapper.getHeaders();
            if (headers.getAccept() == null || headers.getAccept().isEmpty()) {
                headers.setAccept(MediaType.parseMediaTypes(MediaType.APPLICATION_JSON_VALUE));
            }
            headers.setContentType(MediaType.APPLICATION_JSON);
            return execution.execute(wrapper, body);
        }

    }

    private <I, O> O post(String url, I request, Class<O> clazz) {
        return restTemplate.postForObject(url, request, clazz);
    }

    private <I, O> O get(String url, Class<O> clazz, I request) {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {

            public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
                return hostname.equals("10.119.221.156");
            }
        });
        
        return restTemplate.getForObject(url, clazz, request);
    }

    /**
     * Utility method to convert object to JSON using jackson mapper.
     *
     * @param object
     *            - Any Object
     * @return JSON String
     */
    public static String convertToJson(Object object) {
        final ObjectMapper mapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(object);
        } catch (IOException e) {
            LOGGER.error("Error extracting json from path: {}", e);
        }
        return jsonString;
    }
}