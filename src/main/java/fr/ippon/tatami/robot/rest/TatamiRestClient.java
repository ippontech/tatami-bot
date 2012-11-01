package fr.ippon.tatami.robot.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class TatamiRestClient {

    private final Log log = LogFactory.getLog(TatamiRestClient.class);

    private String postStatusUrl;

    private String getStatusesUrl;

    private RestTemplate template;

    @Value("${tatami.server.protocol}")
    private String protocol;

    @Value("${tatami.server.host}")
    private String host;

    @Value("${tatami.server.port}")
    private int port;

    @Value("${tatami.server.context}")
    private String context;

    @Value("${tatami.bot.username}")
    private String username;

    @Value("${tatami.bot.password}")
    private String password;

    @PostConstruct
    public void init() {

        String baseUrl = protocol + "://" + host +
                ":" + port + context;

        this.postStatusUrl = baseUrl + "/rest/statuses/update";

        this.template = new RestTemplate(
                new HttpComponentsClientHttpRequestFactoryBasicAuth
                        (new HttpHost(host,
                                port,
                                protocol)));

        HttpComponentsClientHttpRequestFactory requestFactory =
                (HttpComponentsClientHttpRequestFactory) template.getRequestFactory();
        DefaultHttpClient httpClient =
                (DefaultHttpClient) requestFactory.getHttpClient();
        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(host, port, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(username, password));

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJacksonHttpMessageConverter());
        template.setMessageConverters(messageConverters);
        ResponseErrorHandler responseErrorHandler = new DefaultResponseErrorHandler();
        template.setErrorHandler(responseErrorHandler);
    }

    public void postStatus(String content) {
        Status status = new Status();
        status.setContent(content);
        template.postForObject(postStatusUrl, status, Object.class);
    }
}

