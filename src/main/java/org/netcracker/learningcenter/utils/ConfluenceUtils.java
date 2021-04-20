package org.netcracker.learningcenter.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.jsoup.Jsoup;
import org.netcracker.learningcenter.confluence.ConfluencePageModel;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConfluenceUtils {
    public static final String BODY_PATH = "body";
    public static final String STORAGE_PATH = "storage";
    public static final String VALUE_PATH = "value";
    public static final String CHILDREN_PATH = "children";
    public static final String COMMENT_PATH = "comment";
    public static final String RESULT_PATH = "results";
    public static final String LINKS_PATH = "_links";
    public static final String WEBUI_PATH = "webui";
    public static final String TITLE_PATH = "title";
    public static final String EXPAND = "expand";
    public static final String CQL = "cql";
    public static final String HISTORY = "history";
    public static final String CREATED_DATE = "createdDate";

    public static List<ConfluencePageModel> jsonToPageModelList(JsonNode node,String url) {
        List<ConfluencePageModel> pages = new ArrayList<>();
        Iterator<JsonNode> iterator = node.path(RESULT_PATH).elements();
        while (iterator.hasNext()) {
            pages.add(jsonNodeToPageModel(iterator.next(),url));
        }
        return pages;
    }

    public static ConfluencePageModel jsonNodeToPageModel(JsonNode node,String url) {
        String title = node.path(TITLE_PATH).asText();
        String webLink = url + node.path(LINKS_PATH).path(WEBUI_PATH).asText();
        String body = Jsoup.parse(node.path(BODY_PATH).path(STORAGE_PATH).path(VALUE_PATH).asText()).text();
        String date = node.path(HISTORY).path(CREATED_DATE).asText();
        LocalDate localDate = ZonedDateTime.parse(date).toLocalDate();
        List<String> commentsList = new ArrayList<>();
        Iterator<JsonNode> comments = node.path(CHILDREN_PATH).path(COMMENT_PATH).path(RESULT_PATH).elements();
        while (comments.hasNext()) {
            commentsList.add(Jsoup.parse(comments.next().path(BODY_PATH).path(STORAGE_PATH).path(VALUE_PATH).asText()).text());
        }
        return new ConfluencePageModel(title, webLink, body, commentsList,localDate);
    }
}
