package com.live.cric.wave.scrapfiles.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CricketLiveScoreScrap {

    @Value("${base.url}")
    private String baseUrl;
    @Value("${scrape.base.url}")
    private String scrapebaseurl;

    public String getLiveMatches() throws IOException {
        List<Map<String, Object>> liveMatches = new ArrayList<>();

        // URL of the live match scores
        String url = scrapebaseurl+"/cricket-match/live-scores";

        // Fetch the HTML content from the URL
        Document doc = Jsoup.connect(url).get();

        // Select the elements containing the live scores
        Elements liveScoreElements = doc.select("div.cb-col.cb-col-100.cb-plyr-tbody.cb-rank-hdr.cb-lv-main");

        // Iterate through each live score element and extract the data
        for (Element liveScoreElement : liveScoreElements) {
            // Extract the match title
            String matchTitle = liveScoreElement.select("h2.cb-lv-grn-strip.text-bold.cb-lv-scr-mtch-hdr a").text();

            // Extract the match details
            Elements matchDetails = liveScoreElement.select("div.cb-mtch-lst.cb-col.cb-col-100.cb-tms-itm");
            for (Element matchDetail : matchDetails) {
                Map<String, Object> matchInfo = new HashMap<>();

                // Extract team names
                String teams = matchDetail.select("h3.cb-lv-scr-mtch-hdr a").text();

                // Extract the URL from the <a> tag
                String matchUrl = matchDetail.select("h3.cb-lv-scr-mtch-hdr a").attr("href");
                matchUrl =  baseUrl+ matchUrl; // Construct the full URL

                // Extract match status
                String status = matchDetail.select("div.cb-text-live").text();

                // Extract team 1 name and score
                String team1Element = matchDetail.select("div.cb-hmscg-bwl-txt").text();
//                String team1Name = team1Element.select("div.cb-hmscg-tm-nm").text();
//                String team1Score = team1Element.select("div.cb-ovr-flo").get(0).text();

                // Extract team 2 name and score
                String team2Element = matchDetail.select("div.cb-hmscg-bat-txt").text();
//                String team2Name = team2Element.select("div.cb-hmscg-tm-nm").text();
//                String team2Score = team2Element.select("div.cb-ovr-flo").get(0).text();

                // Add data to the matchInfo map
                matchInfo.put("matchTitle", matchTitle);
                matchInfo.put("teams", teams);
                matchInfo.put("matchUrl", matchUrl); // Add the match URL
                matchInfo.put("status", status);
                matchInfo.put("team1", team1Element); // Team 1 name and score
                matchInfo.put("team2",  team2Element); // Team 2 name and score

                // Add match details to the list
                liveMatches.add(matchInfo);
            }
        }

        // Convert the list to JSON using Jackson
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
        // List to store score details
        List<Map<String, Object>> liveScores = new ArrayList<>();

        // URL of the live match score (replace with actual URL)
        String url = "https://m.cricbuzz.com/live-cricket-scores/"+matchId+"/"+matchName;

        // Fetch the HTML content from the URL
        Document doc = Jsoup.connect(url).get();
        Map<String, Object> matchDetails = new HashMap<>();

        // Extract Team 1 details
        Element team1Element = doc.selectFirst("div.flex.flex-row.font-light.text-base .mr-2");
        String team1 = team1Element != null ? team1Element.text() : "N/A";
        matchDetails.put("team1", team1);

        Element team1ScoreElement = doc.selectFirst("div.flex.flex-row.font-light.text-base .flex");
        String team1Score = team1ScoreElement != null ? team1ScoreElement.text() : "N/A";
        matchDetails.put("team1Score", team1Score);

        // Extract Team 2 details
        Element team2Element = doc.selectFirst("div.flex.flex-row.font-bold.text-xl .mr-2");
        String team2 = team2Element != null ? team2Element.text() : "N/A";
        matchDetails.put("team2", team2);

        Elements team2Score = doc.select("div.flex.flex-row.font-bold.text-xl.flex");
        String team2Scores = team2Score != null ? team2Score.text() : "N/A";
        matchDetails.put("team2Score", team2Scores);

        // Extract Current Run Rate (CRR)
        Element crrElement = doc.selectFirst("div.text-cbTxtSec span:contains(CRR) + span");
        String crr = crrElement != null ? crrElement.text() : "N/A";
        matchDetails.put("crr", crr);

        // Extract Partnership
        Element pshipElement = doc.select("div.text-cbTxtSec").select("div.mb-3")
                .select("span.font-bold:contains(Partnership)").first();
        String pship = pshipElement != null ? pshipElement.parent().ownText() : "N/A";
        matchDetails.put("pship", pship);

        // Extract Last 10 overs
        Element last10Element = doc.select("div.text-cbTxtSec").select("div.mb-3")
                .select("span.font-bold:contains(Last 10 overs)").first();
        String last10 = last10Element != null ? last10Element.parent().ownText() : "N/A";
        matchDetails.put("last10", last10);

        // Extract Toss details
        Element tossElement = doc.select("div.text-cbTxtSec span.font-bold:contains(Toss)").first();
        String toss = tossElement != null ? tossElement.parent().ownText().replace("Toss: ", "").trim() : "N/A";
        matchDetails.put("toss", toss);

        // Extract Live Update
        Element liveUpdateElement = doc.selectFirst("div.text-cbTxtLive");
        String liveUpdate = liveUpdateElement != null ? liveUpdateElement.text() : "N/A";
        matchDetails.put("liveUpdate", liveUpdate);

        // Extract Batter Details
        Elements batters = doc.select("div.text-sm.mb-2").select(".grid.scorecard-bat-grid");

        List<Map<String, String>> batterDetailsList = new ArrayList<>();
        for (int i = 1; i < batters.size(); i++) {
            Map<String, String> batterDetails = new HashMap<>();

            String playerName = batters.get(i).select(".flex").get(0).text();
            String playerUrl = batters.get(i).select(".flex").get(0).select("a").attr("href");
            String runs = batters.get(i).select(".flex").get(1).text();
            String balls = batters.get(i).select(".flex").get(2).text();
            String fours = batters.get(i).select(".flex").get(3).text();
            String sixes = batters.get(i).select(".flex").get(4).text();
            String strikeRate = batters.get(i).select(".flex").get(5).text();

            batterDetails.put("playerName", playerName);
            batterDetails.put("playerUrl", playerUrl);
            batterDetails.put("R", runs);
            batterDetails.put("B", balls);
            batterDetails.put("fours", fours);
            batterDetails.put("sixs", sixes);
            batterDetails.put("SR", strikeRate);

            batterDetailsList.add(batterDetails);
        }
        matchDetails.put("batterDetails", batterDetailsList);

        // Extract Bowler Details
        Elements bowlers = doc.select(".grid.scorecard-bat-grid");
        Map<String, String> bowlerDetails = new HashMap<>();
        int size = batters.size() + 1;
        String bowlerName = bowlers.get(size).select(".flex").get(0).text();
        String bowlerUrl =bowlers.get(size).select(".flex").get(0).select("a").attr("href");
        String overs = bowlers.get(size).select(".flex").get(1).text();
        String maidens = bowlers.get(size).select(".flex").get(2).text();
        String bowlerRuns = bowlers.get(size).select(".flex").get(3).text();
        String wickets = bowlers.get(size).select(".flex").get(4).text();
        String economy = bowlers.get(size).select(".flex").get(5).text();
        bowlerDetails.put("playerName", bowlerName);
        bowlerDetails.put("playerUrl",bowlerUrl);
        bowlerDetails.put("O", overs);
        bowlerDetails.put("M", maidens);
        bowlerDetails.put("R", bowlerRuns);
        bowlerDetails.put("W", wickets);
        bowlerDetails.put("ECO", economy);

        matchDetails.put("bowlerDetails", bowlerDetails);

        // Extract Commentary
        Elements commentaryDivs = doc.select("div.mb-2 div.flex.gap-4.wb\\:gap-6.mx-4.wb\\:mx-4.py-2.border-t.border-dotted.border-cbChineseSilver.wb\\:border-0");

        List<Map<String, String>> commentaryList = new ArrayList<>();
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

        // Add match details to the list
        liveScores.add(matchDetails);

        // Convert the list to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(liveScores);
    }
}
