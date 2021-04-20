package org.netcracker.learningcenter.confluence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.buf.StringUtils;
import org.netcracker.learningcenter.utils.ConfluenceUtils;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.netcracker.learningcenter.utils.ConfluenceUtils.*;

public class ConfluenceClient {
    public static final String CQL_SEARCH = "/rest/api/content/search";
    public static final String BODY = "body.storage";
    public static final String CREATED_DATE = "history.createdDate";
    public static final String COMMENTS = "children.comment.body.storage";
    private static final Logger LOG = LogManager.getLogger(ConfluenceClient.class);
    private final WebClient confluenceClient;
    private final ObjectMapper mapper;
    private final String url;
    private String token;

    public ConfluenceClient(String baseURL, String token, ObjectMapper mapper) {
        this.mapper = mapper;
        confluenceClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create(ConnectionProvider.newConnection())))
                .baseUrl(baseURL)
                .defaultHeaders(httpHeaders -> httpHeaders.setBasicAuth(token))
                .build();
        this.url = baseURL;
        this.token = token;
    }

    public List<ConfluencePageModel> findByCQL(String cql) throws JsonProcessingException {
        LOG.info("Search in {} using cql {}", url, cql);
        return ConfluenceUtils.jsonToPageModelList(executeSearchUsingCQL(cql), url);
    }

    public List<ConfluencePageModel> findByKeywords(List<String> keywords, String startDate) throws JsonProcessingException {
        List<ConfluencePageModel> models = new ArrayList<>();
        String cqlFilter = "";
        if (!startDate.isEmpty()) {
            cqlFilter += "and created>=" + startDate;
        }
        for (String word : keywords) {
            String cql = "text~\"" + word + "\"" + cqlFilter;
            LOG.info("Search in {} using keyword {} and filter {}", url, word, startDate);
            models.addAll(ConfluenceUtils.jsonToPageModelList(executeSearchUsingCQL(cql), url));
        }
        return models;
    }

    private JsonNode executeSearchUsingCQL(String cql) throws JsonProcessingException {
        return mapper.readTree(confluenceClient.get()
                .uri(uriBuilder -> uriBuilder.path(CQL_SEARCH)
                        .queryParam(CQL, cql)
                        .queryParam(EXPAND, buildExpandParam(BODY, CREATED_DATE, COMMENTS))
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block());
    }

    private String buildExpandParam(String... params) {
        return StringUtils.join(Arrays.asList(params), ',');
    }

}
