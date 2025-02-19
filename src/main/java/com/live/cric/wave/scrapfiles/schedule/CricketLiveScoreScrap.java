package com.live.cric.wave.scrapfiles.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class CricketLiveScoreScrap {

    @Value("${base.url}")
    private String baseUrl;
    @Value("${scrape.base.url}")
    private String scrapebaseurl;public String getLiveMatches() throws IOException {
        List<Map<String, Object>> liveMatches = new ArrayList<>();

        String url = scrapebaseurl + "/cricket-match/live-scores";
        Document doc = Jsoup.connect(url).get();

        // Select each match section
        Elements matchSections = doc.select("div.cb-col.cb-col-100.cb-plyr-tbody");

        for (Element section : matchSections) {
            // Extract the tournament title
            String matchTitle = section.select("h2.cb-lv-grn-strip a").text();

            // Iterate through each match in the section
            Elements matches = section.select("div.cb-mtch-lst.cb-tms-itm");
            for (Element match : matches) {
                Map<String, Object> matchInfo = new HashMap<>();

                // Extract teams and match URL from the header
                Element headerLink = match.selectFirst("h3.cb-lv-scr-mtch-hdr a");
                String teams = headerLink.text();
                String matchUrl = baseUrl + headerLink.attr("href");

                // Extract team scores
                Element scoreWell = match.selectFirst("a.cb-lv-scrs-well");
                String team1Score = scoreWell.select("div.cb-hmscg-bwl-txt").text(); // TN
                String team2Score = scoreWell.select("div.cb-hmscg-bat-txt").text(); // VID

                // Extract status
                String status = match.select("div.cb-text-live").text();

                // Extract date, time, and venue
                String venue = match.select("div.text-gray").text(); // Extract date (e.g., Feb 7)

//                // Populate match info
                matchInfo.put("matchTitle", matchTitle);
                matchInfo.put("teams", teams.replace(",",""));
                matchInfo.put("matchUrl", matchUrl);
                matchInfo.put("status", status);
                matchInfo.put("team1", team1Score);
                matchInfo.put("team2", team2Score);
                matchInfo.put("venue", venue);  // Add extracted date
//
                liveMatches.add(matchInfo);
            }
        }

        // Convert to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(liveMatches);
    }

    private Map<String, String> parseScore(String score) {
        Map<String, String> teamScores = new HashMap<>();

        // Split the score into parts
        String[] scoreParts = score.split("\\s+"); // Split by whitespace

        // Team 1 details
        String team1Name = scoreParts[0]; // First part is team 1 name
        String team1Score = scoreParts[1]; // Second part is team 1 score

        // Team 2 details
        String team2Name = "";
        String team2Score = "";

        // Handle cases where the score string has additional information (e.g., "d", "(20 Ovs)")
        if (scoreParts.length >= 4) {
            team2Name = scoreParts[2]; // Third part is team 2 name
            team2Score = scoreParts[3]; // Fourth part is team 2 score
        } else if (scoreParts.length == 3) {
            // Handle cases like "AUS 654-6 d SL"
            team2Name = scoreParts[2]; // Third part is team 2 name
            team2Score = ""; // No score available for team 2
        }

        // Add to the map
        teamScores.put("team1", team1Name + " " + team1Score);
        teamScores.put("team2", team2Name + " " + team2Score);

        return teamScores;
    }
    public String getLiveScore(String matchId, String matchName) throws IOException {
        List<Map<String, Object>> liveScores = new ArrayList<>();
        String url = "https://m.cricbuzz.com/live-cricket-scores/" + matchId + "/" + matchName;
        Document doc = Jsoup.connect(url).get();
        Map<String, Object> matchDetails = new HashMap<>();




        Element team1Element = doc.selectFirst("div.flex.flex-row.font-light.text-base .mr-2");

        if (team1Element != null) {
            matchDetails.put("team1", team1Element.text());
        } else {
            Element alternativeElement = doc.select("div.flex.flex-row").select("div.mr-2").last(); // New selector
            if (alternativeElement != null) {
                matchDetails.put("team1", alternativeElement.text());
            } else {
                matchDetails.put("team1", "N/A");
            }
        }
        Element team1ScoreElement = doc.selectFirst("div.flex.flex-row.font-light.text-base .flex");

        if (team1ScoreElement != null) {
            matchDetails.put("team1Score", team1ScoreElement.text());
        } else {
            Element alternativeElement = doc.select("div.flex.flex-row").select("div").last(); // New selector

            if (alternativeElement != null) {
                matchDetails.put("team1Score", alternativeElement.text());
            } else {
                matchDetails.put("team1Score", "N/A");
            }
        }

        // Extract Team 2 details
        Element team2Element = doc.selectFirst("div.flex.flex-row.font-bold.text-xl .mr-2");

        if (team2Element != null) {
            matchDetails.put("team2", team2Element.text());
        } else {
            Element alternativeElement = doc.selectFirst("div.flex.flex-row.text-cbTxtSec div.mr-2"); // New selector
            if (alternativeElement != null) {
                matchDetails.put("team2", alternativeElement.text());
            } else {
                matchDetails.put("team2", "N/A");
            }
        }   // Extract Team 2 details
        Element team2ScoreElement = doc.selectFirst("div.flex.flex-row.font-bold.text-xl.flex");

        if (team2ScoreElement != null) {
            matchDetails.put("team2Score", team2ScoreElement.text());
        } else {
            Element alternativeElement = doc.selectFirst("div.flex.flex-row.text-cbTxtSec > div:nth-child(2)"); // New selector
            if (alternativeElement != null) {
                matchDetails.put("team2Score", alternativeElement.text());
            } else {
                matchDetails.put("team2Score", "N/A");
            }
        }


        // Extract Player of the Match
        Element playerOfTheMatchElement = doc.selectFirst("div.text-xs:contains(PLAYER OF THE MATCH) + div a");
        if (playerOfTheMatchElement != null) {
            Map<String, String> playerOfTheMatch = new HashMap<>();
            playerOfTheMatch.put("playerName", playerOfTheMatchElement.text());
            playerOfTheMatch.put("playerUrl", playerOfTheMatchElement.attr("href"));
            playerOfTheMatch.put("imageUrl", playerOfTheMatchElement.select("img").attr("src"));

            matchDetails.put("playerOfTheMatch", playerOfTheMatch);
        } else {
            matchDetails.put("playerOfTheMatch", "N/A");
        }

        // Extract Player of the Series
        Element playerOfTheSeriesElement = doc.selectFirst("div.text-xs:contains(PLAYER OF THE SERIES) + div a");
        if (playerOfTheSeriesElement != null) {
            Map<String, String> playerOfTheSeries = new HashMap<>();
            playerOfTheSeries.put("playerName", playerOfTheSeriesElement.text());
            playerOfTheSeries.put("playerUrl", playerOfTheSeriesElement.attr("href"));
            playerOfTheSeries.put("imageUrl", playerOfTheSeriesElement.select("img").attr("src"));

            matchDetails.put("playerOfTheSeries", playerOfTheSeries);
        } else {
            matchDetails.put("playerOfTheSeries", "N/A");
        }


        // Extract Series, Venue, Date & Time
        Element seriesElement = doc.selectFirst("div.text-xs .font-bold:contains(Series:) + a");
        String series = seriesElement != null ? seriesElement.text() : "N/A";
        matchDetails.put("series", series);

        Element venueElement = doc.selectFirst("div.text-xs .font-bold:contains(Venue:) + a");
        String venue = venueElement != null ? venueElement.text() : "N/A";
        matchDetails.put("venue", venue);

        Element dateTimeElement = doc.selectFirst("div.text-xs .font-bold:contains(Date & Time:)");
        String dateTime = dateTimeElement != null ? dateTimeElement.parent().text().replace("Date & Time: ", "").trim() : "N/A";
        matchDetails.put("dateTime", dateTime);


        // Extract other details safely
        matchDetails.put("crr", Optional.ofNullable(doc.selectFirst("div.text-cbTxtSec span:contains(CRR) + span"))
                .map(Element::text).orElse("N/A"));
        matchDetails.put("pship", Optional.ofNullable(doc.select("div.text-cbTxtSec div.mb-3 span.font-bold:contains(Partnership)").first())
                .map(e -> e.parent().ownText()).orElse("N/A"));
        matchDetails.put("last10", Optional.ofNullable(doc.select("div.text-cbTxtSec div.mb-3 span.font-bold:contains(Last 10 overs)").first())
                .map(e -> e.parent().ownText()).orElse("N/A"));
        matchDetails.put("toss", Optional.ofNullable(doc.select("div.text-cbTxtSec span.font-bold:contains(Toss)").first())
                .map(e -> e.parent().ownText().replace("Toss: ", "").trim()).orElse("N/A"));
        matchDetails.put("liveUpdate", Optional.ofNullable(doc.selectFirst("div.text-cbTxtLive"))
                .map(Element::text).orElse("N/A"));
        Element liveUpdatesElement = doc.selectFirst("div.text-cbTxtLive");

        if (liveUpdatesElement != null) {
            matchDetails.put("liveUpdate", liveUpdatesElement.text());
        } else {
            Element alternativeElement = doc.selectFirst("div.text-cbTextLink"); // New selector
            if (alternativeElement != null) {
                matchDetails.put("liveUpdate", alternativeElement.text());
            } else {
                matchDetails.put("liveUpdate", "N/A");
            }
        }
        // Extract Batter Details
        List<Map<String, String>> batterDetailsList = new ArrayList<>();
        Elements batters = doc.select("div.text-sm.mb-2 .grid.scorecard-bat-grid");
        for (Element batter : batters) {
            Elements details = batter.select(".flex");
            if (details.size() >= 6) {
                Map<String, String> batterDetails = new HashMap<>();
                batterDetails.put("playerName", details.get(0).text());
                batterDetails.put("playerUrl", details.get(0).select("a").attr("href"));
                batterDetails.put("R", details.get(1).text());
                batterDetails.put("B", details.get(2).text());
                batterDetails.put("fours", details.get(3).text());
                batterDetails.put("sixs", details.get(4).text());
                batterDetails.put("SR", details.get(5).text());
                batterDetailsList.add(batterDetails);
            }
        }
        matchDetails.put("batterDetails", batterDetailsList);

        // Extract Bowler Details
        Map<String, String> bowlerDetails = new HashMap<>();
        Elements bowlers = doc.select(".grid.scorecard-bat-grid");
        if (!bowlers.isEmpty()) {
            Elements details = bowlers.get(Math.min(batters.size(), bowlers.size() - 1)).select(".flex");
            if (details.size() >= 6) {
                bowlerDetails.put("playerName", details.get(0).text());
                bowlerDetails.put("playerUrl", details.get(0).select("a").attr("href"));
                bowlerDetails.put("O", details.get(1).text());
                bowlerDetails.put("M", details.get(2).text());
                bowlerDetails.put("R", details.get(3).text());
                bowlerDetails.put("W", details.get(4).text());
                bowlerDetails.put("ECO", details.get(5).text());
            }
        }
        matchDetails.put("bowlerDetails", bowlerDetails);

        // Extract Commentary
        List<Map<String, String>> commentaryList = new ArrayList<>();
        Elements commentaryDivs = doc.select("div.mb-2 div.flex.gap-4");
        for (Element commentary : commentaryDivs) {
            String overNumber = commentary.select("div.font-bold.text-center").text();
            String commentaryText = commentary.select("div:not(.font-bold)").text();
            if (!overNumber.isEmpty() && !commentaryText.isEmpty()) {
                Map<String, String> commentaryDetails = new HashMap<>();
                commentaryDetails.put("over", overNumber);
                commentaryDetails.put("commentary", commentaryText);
                commentaryList.add(commentaryDetails);
            }
        }
        matchDetails.put("commentary", commentaryList);

        liveScores.add(matchDetails);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(liveScores);
    }

}
