package com.live.cric.wave.scrapfiles.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.live.cric.wave.utils.PageScrollLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CricketNewsScrap {

    @Value("${base.url}")
    private String baseUrl;

    public String getAllTopics() throws Exception {
        // URL of the Cricbuzz page
        String url = "https://www.cricbuzz.com/cricket-news/info/";

        // Fetch and parse the HTML content
        Document document = Jsoup.connect(url).get();

        // List to store news details
        List<Map<String, String>> newsList = new ArrayList<>();

        // Select all news blocks
        Elements newsBlocks = document.select(".cb-brdr-thin-btm.cb-lst-itm-sm");

        // Iterate through each news block
        for (Element newsBlock : newsBlocks) {
            Map<String, String> newsDetails = new HashMap<>();

            // Extract the title
            Element titleElement = newsBlock.selectFirst(".cb-nws-hdln-ancr");
            String title = titleElement != null ? titleElement.text() : "No title";

            // Extract the link
            String link = titleElement != null ? baseUrl + titleElement.attr("href") : "No link";

            // Extract the description
            Element descriptionElement = newsBlock.selectFirst(".cb-nws-intr");
            String description = descriptionElement != null ? descriptionElement.text() : "No description";

            // Add details to the map
            newsDetails.put("title", title);
            newsDetails.put("link", link);
            newsDetails.put("description", description);

            // Add the map to the list
            newsList.add(newsDetails);
        }

        // Convert the list to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(newsList);
    }
    public String getAllNews(String newsId,String newsName) throws Exception {
        String url = "https://www.cricbuzz.com/cricket-news";

        if (newsId != null && !newsId.isEmpty()) {
            url += "/" + newsId;
        }

        if (newsName != null && !newsName.isEmpty()) {
            url += "/" + newsName;
        }

        System.out.println("url="+url);
//        String url = "https://www.cricbuzz.com/cricket-news/editorial/cb-plus";
//        String url = "https://www.cricbuzz.com/cricket-news/latest-news";
//        String url = "https://www.cricbuzz.com/cricket-news/editorial/spotlight";
//        String url = "https://www.cricbuzz.com/cricket-news/editorial/editorial-list";
//        String url = "https://www.cricbuzz.com/cricket-news/editorial/specials";
//        String url = "https://www.cricbuzz.com/cricket-news/editorial/stats-analysis";
//        String url = "https://www.cricbuzz.com/cricket-news/editorial/interviews";
//        String url = "https://www.cricbuzz.com/cricket-news/editorial/live-blogs";
//        String url = "https://www.cricbuzz.com/cricket-news/info/";
//        int maxScrolls = 1;
//        int waitTimeMillis = 2000;
//        String pageSource = PageScrollLoader.loadPageSource(url, maxScrolls, waitTimeMillis);

        Document document = Jsoup.connect(url).get();

        List<Map<String, String>> newsList = new ArrayList<>();

        // Select all news blocks
        Elements newsBlocks = document.select("#news-list > .cb-col.cb-col-100.cb-lst-itm.cb-pos-rel.cb-lst-itm-lg");

        for (Element news : newsBlocks) {
            Map<String, String> newsDetails = new HashMap<>();

            // Extract image URL
            Element imageElement = news.selectFirst(".cb-col.cb-col-33 img");
            String imageUrl = imageElement.select("img").hasAttr("src")
                    ? imageElement.select("img").attr("src")
                    : imageElement.select("img").hasAttr("source")
                    ? imageElement.select("img").attr("source")
                    : null;

            // Extract news title
            Element titleElement = news.selectFirst("h2.cb-nws-hdln a");
            String title = titleElement != null ? titleElement.text() : "";

            // Extract news link
            String newsLink = titleElement != null ? titleElement.attr("href") : "";

            // Extract news description
            Element descriptionElement = news.selectFirst(".cb-nws-intr");
            String description = descriptionElement != null ? descriptionElement.text() : "";

            // Extract category (e.g., NEWS)
            Element categoryElement = news.selectFirst(".cb-nws-time");
            String category = categoryElement != null ? categoryElement.text() : "";

            // Extract timestamp
            Element timestampElement = news.selectFirst("span.cb-nws-time");
            String timestamp = timestampElement != null ? timestampElement.text() : "";

            // Populate the map
            newsDetails.put("imageUrl", imageUrl);
            newsDetails.put("title", title);
            newsDetails.put("link",  baseUrl+newsLink); // Add base URL to relative link
            newsDetails.put("description", description);
            newsDetails.put("category", category);
            newsDetails.put("timestamp", timestamp);

            // Add to list
            newsList.add(newsDetails);
        }

        // Convert the list to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(newsList);
    }
}
