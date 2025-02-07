package com.live.cric.wave.scrapfiles.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.live.cric.wave.utils.PageScrollLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

    @Service
    public class CricketScheduleScrap {
        private static final Logger logger = LoggerFactory.getLogger(CricketScheduleScrap.class);

        @Value("${base.url}")
        private String baseUrl;

        public String getAllSchedule() throws Exception {
            String url = "https://www.cricbuzz.com/cricket-schedule/upcoming-series/all";
            int maxScrolls = 1;
            int waitTimeMillis = 2000;
            String pageSource = PageScrollLoader.loadPageSource(url, maxScrolls, waitTimeMillis);

            Document document = Jsoup.parse(pageSource);
            List<Map<String, String>> scheduleList = new ArrayList<>();

            // Select all match blocks
//            Elements matchBlocks = document.select("#domestic-list > .cb-col-100.cb-col");
//            Elements matchBlocks = document.select("#league-list > .cb-col-100.cb-col");
//            Elements matchBlocks = document.select("#women-list > .cb-col-100.cb-col");
            Elements matchBlocks = document.select("#all-list > .cb-col-100.cb-col");
            int tt=0;
            for (Element block : matchBlocks) {
                // Extract the date for this block
                Element dateElement = block.selectFirst(".cb-lv-grn-strip");
                String date = dateElement != null ? dateElement.text() : "Unknown Date";

                // Extract all series blocks within this date block
                Elements seriesBlocks = block.select(".cb-col-67.cb-col");
                tt=0;
                for (Element seriesBlock : seriesBlocks) {
                    // Extract the series title
                    Elements seriesTitleElement = block.select(".cb-col-33.cb-col.cb-mtchs-dy.text-bold");
                    String seriesTitle = seriesTitleElement != null ? seriesTitleElement.get(tt).text() : "Unknown Series";

                    Elements seriesLinkElement = block.select("a");
                    String seriesLink = seriesLinkElement != null ? seriesTitleElement.get(tt).attr("href") :  "Unknown Link";


                    // Extract matches within this series block
                    Elements matches = seriesBlock.select(".cb-ovr-flo.cb-col-60.cb-col.cb-mtchs-dy-vnu");
                    Elements timings = seriesBlock.select(".cb-col-40.cb-col.cb-mtchs-dy-tm");
                    Elements timingFormats = seriesBlock.select(".cb-col-40.cb-col.cb-mtchs-dy-tm > div.cb-font-12.text-gray");

                    for (int i = 0; i < matches.size(); i++) {
                        Map<String, String> matchDetails = new HashMap<>();
                        Element match = matches.get(i);
                        Element timing = timings.get(i);
                        Element timingFormat = timingFormats.size() > i ? timingFormats.get(i) : null;

                        matchDetails.put("date", date);
                        matchDetails.put("seriesTitle", seriesTitle+""); // Correctly associates series title with match
                        matchDetails.put("seriesLink", seriesLink+""); // Correctly associates series title with match
                        matchDetails.put("matchTitle", match.selectFirst("a").text());
                        matchDetails.put("matchLink", baseUrl+match.selectFirst("a").attr("href"));
                        matchDetails.put("matchVenue", match.selectFirst("[itemprop='location']").text());
                        matchDetails.put("matchFormat", timingFormat != null ? timingFormat.text() : "Unknown Format");
                        matchDetails.put("matchTime", timing.text());

                        scheduleList.add(matchDetails);
                    }
                    tt++;
                }
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(scheduleList);
        }

    }


