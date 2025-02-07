package com.live.cric.wave.scrapfiles.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

    @Service
    public class CricketTeamsScrap {

        @Value("${base.url}")
        private String baseUrl;
        @Value("${scrape.base.url}")
        private String scrapebaseurl;
        public String getPlayerDetails(String playerId, String playerName) {
            // Build the URL to fetch the player's details
            String url = scrapebaseurl+"/profiles/" + playerId + "/" + playerName;
            Map<String, Object> playerDetails = new HashMap<>();

            try {
                // Fetch and parse the HTML content
                Document document = Jsoup.connect(url).get();

                // Extract Personal Info
                String profileImage = document.select(".cb-col-20 img").attr("src");
                if (profileImage.isEmpty()) {
                    profileImage = "N/A";
                }

                System.out.println("profile = "+profileImage);
                playerDetails.put("Name", document.select("div.cb-col.cb-col-80.cb-player-name-wrap")
                        .select("h1").text() + ", " + document.select("div.cb-col.cb-col-80.cb-player-name-wrap")
                        .select("h3").text());
                playerDetails.put("profileImage", profileImage);

                Elements personalinfo = document.select("div.cb-col.cb-col-33.text-black");
                Elements personalanswer = personalinfo.select("div.cb-col.cb-col-60");
                Map<String, String> personalInfo = new HashMap<>();
                personalInfo.put("Born", personalanswer.size() > 0 ? personalanswer.get(0).text() : "N/A");
                personalInfo.put("BirthPlace", personalanswer.size() > 1 ? personalanswer.get(1).text() : "N/A");
                personalInfo.put("Height", personalanswer.size() > 2 ? personalanswer.get(2).text() : "N/A");
                personalInfo.put("Role", personalanswer.size() > 3 ? personalanswer.get(3).text() : "N/A");
                personalInfo.put("BattingStyle", personalanswer.size() > 4 ? personalanswer.get(4).text() : "N/A");
                personalInfo.put("BowlingStyle", personalanswer.size() > 5 ? personalanswer.get(5).text() : "N/A");
                personalInfo.put("Teams", personalanswer.size() > 6 ? personalanswer.get(6).text() : "N/A");
                playerDetails.put("PersonalInfo", personalInfo);

                // Extract "ICC Rankings"
                Map<String, String> batting = new HashMap<>();
                batting.put("Test", document.select(".cb-col.cb-col-25.cb-plyr-rank").size() > 4 ? document.select(".cb-col.cb-col-25.cb-plyr-rank").get(4).text() : "N/A");
                batting.put("ODI", document.select(".cb-col.cb-col-25.cb-plyr-rank").size() > 5 ? document.select(".cb-col.cb-col-25.cb-plyr-rank").get(5).text() : "N/A");
                batting.put("T20", document.select(".cb-col.cb-col-25.cb-plyr-rank").size() > 6 ? document.select(".cb-col.cb-col-25.cb-plyr-rank").get(6).text() : "N/A");
                playerDetails.put("Batting", batting);

                Map<String, String> bowling = new HashMap<>();
                bowling.put("Test", document.select(".cb-col.cb-col-25.cb-plyr-rank").size() > 8 ? document.select(".cb-col.cb-col-25.cb-plyr-rank").get(8).text() : "N/A");
                bowling.put("ODI", document.select(".cb-col.cb-col-25.cb-plyr-rank").size() > 9 ? document.select(".cb-col.cb-col-25.cb-plyr-rank").get(9).text() : "N/A");
                bowling.put("T20", document.select(".cb-col.cb-col-25.cb-plyr-rank").size() > 10 ? document.select(".cb-col.cb-col-25.cb-plyr-rank").get(10).text() : "N/A");
                playerDetails.put("Bowling", bowling);

                // Extract Bio
                Elements bio = document.select("div.cb-col.cb-col-67.cb-bg-white.cb-plyr-rt-col")
                        .select("div.cb-hm-rght.cb-player-bio");

                Elements battable = bio.select("div.cb-plyr-tbl")
                        .select("table.table.cb-col-100.cb-plyr-thead");

                Elements rows = battable.select("tbody > tr");

                // Iterate through each row and extract data
                Map<String, Map<String, String>> battingcareer = new HashMap<>();
                for (int i = 0; i < 4; i++) {
                    Elements cells = rows.size() > i ? rows.get(i).select("td") : new Elements();
                    Map<String, String> matchtype = new HashMap<>();

                    matchtype.put("MatchesPlayed", cells.size() > 1 ? cells.get(1).text() : "N/A");
                    matchtype.put("NoofInningsBatted", cells.size() > 2 ? cells.get(2).text() : "N/A");
                    matchtype.put("NoofNotOuts", cells.size() > 3 ? cells.get(3).text() : "N/A");
                    matchtype.put("NoofRunsScored", cells.size() > 4 ? cells.get(4).text() : "N/A");
                    matchtype.put("HighestScore", cells.size() > 5 ? cells.get(5).text() : "N/A");
                    matchtype.put("BattingAverage", cells.size() > 6 ? cells.get(6).text() : "N/A");
                    matchtype.put("NoofBallsFaced", cells.size() > 7 ? cells.get(7).text() : "N/A");
                    matchtype.put("BattingStrikeRate", cells.size() > 8 ? cells.get(8).text() : "N/A");
                    matchtype.put("Noof100sScored", cells.size() > 9 ? cells.get(9).text() : "N/A");
                    matchtype.put("Noof200sScored", cells.size() > 10 ? cells.get(10).text() : "N/A");
                    matchtype.put("Noof50sScored", cells.size() > 11 ? cells.get(11).text() : "N/A");
                    matchtype.put("Nooffourshit", cells.size() > 12 ? cells.get(12).text() : "N/A");
                    matchtype.put("Noofsixeshit", cells.size() > 13 ? cells.get(13).text() : "N/A");
                    battingcareer.put(cells.size() > 0 ? cells.get(0).text() : "N/A", matchtype);
                    playerDetails.put("BatCareerSummary", battingcareer);
                }

                Map<String, Map<String, String>> bowlingcareer = new HashMap<>();
                for (int i = 4; i < 8; i++) {
                    Elements cells = rows.size() > i ? rows.get(i).select("td") : new Elements();
                    Map<String, String> matchtype = new HashMap<>();

                    matchtype.put("MatchesPlayed", cells.size() > 1 ? cells.get(1).text() : "N/A");
                    matchtype.put("NoofInningsBatted", cells.size() > 2 ? cells.get(2).text() : "N/A");
                    matchtype.put("NoofBallsBowled", cells.size() > 3 ? cells.get(3).text() : "N/A");
                    matchtype.put("NoofRunsScored", cells.size() > 4 ? cells.get(4).text() : "N/A");
                    matchtype.put("Wickets", cells.size() > 5 ? cells.get(5).text() : "N/A");
                    matchtype.put("BestBowlinginInnings", cells.size() > 6 ? cells.get(6).text() : "N/A");
                    matchtype.put("BestBowlinginMatch", cells.size() > 7 ? cells.get(7).text() : "N/A");
                    matchtype.put("Economy", cells.size() > 8 ? cells.get(8).text() : "N/A");
                    matchtype.put("BowlingAverage", cells.size() > 9 ? cells.get(9).text() : "N/A");
                    matchtype.put("BowlingStrikeRate", cells.size() > 10 ? cells.get(10).text() : "N/A");
                    matchtype.put("FiveWicketsinanInnings", cells.size() > 11 ? cells.get(11).text() : "N/A");
                    matchtype.put("TenWicketsinanMatch", cells.size() > 12 ? cells.get(12).text() : "N/A");
                    bowlingcareer.put(cells.size() > 0 ? cells.get(0).text() : "N/A", matchtype);
                    playerDetails.put("BowlCareerSummary", bowlingcareer);
                }

                Elements elements2 = bio.select(".cb-col.cb-col-100").select("div.cb-col.cb-col-16.text-bold.cb-ftr-lst");
                Elements elements3 = bio.select(".cb-col.cb-col-100").select("div.cb-col.cb-col-84.cb-ftr-lst");

                Map<String, String> careerinfo = new HashMap<>();
                for (int i = 0; i < elements2.size(); i++) {
                    careerinfo.put(elements2.get(i).text().trim().replace(" ", ""), elements3.size() > i ? elements3.get(i).select("a.cb-text-link").text() : "N/A");
                }
                playerDetails.put("CareerInfo", careerinfo);

                Elements profiledescription = bio.select(".cb-col.cb-col-100.cb-player-bio");
                playerDetails.put("profileDescription", profiledescription.size() > 0 ? profiledescription.text() : "N/A");

                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(playerDetails);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("error = "+e.getMessage());
                return "{\"error\": \"Unable to fetch player details\"}";
            }
        }
//        https://m.cricbuzz.com/live-cricket-scores/101058/wi-vs-pak-2nd-test-west-indies-tour-of-pakistan-2025
//        https://m.cricbuzz.com/live-cricket-scores/101058/wi-vs-pak-2nd-test-west-indies-tour-of-pakistan-2025
        public String getResults(String teamName, String teamId) throws IOException {
            // Define the URL to scrape
            String url = scrapebaseurl + "/cricket-team/" + teamName + "/" + teamId + "/results";

            // Fetch and parse the HTML from the URL
            Document document = Jsoup.connect(url).get();

            // Select the container for match results
            Elements matchElements = document.select("#series-matches .cb-col-100.cb-col.cb-brdr-thin-btm");
            // List to store match details
            List<Map<String, String>> resultList = new ArrayList<>();

            // Loop through each match element and extract details
            for (Element match : matchElements) {
                Map<String, String> matchDetails = new HashMap<>();

                Element dateElement = match.selectFirst("span");

                if (dateElement != null) {
                    // Extract the value of the 'ng-bind' attribute
                    String ngBindValue = dateElement.attr("ng-bind");

                    // Extract the timestamp part (before the '|')
                    String timestampString = ngBindValue.split("\\|")[0].trim();
                    long timestamp = Long.parseLong(timestampString); // Convert to long

                    // Convert timestamp to a Date object
                    Date date = new Date(timestamp);

                    // Format the date to display "MMM dd, EEE HH:mm:ss z"
                    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, EEE HH:mm:ss z");

                    // Set time zone (+11:00 extracted from the ng-bind value)
                    formatter.setTimeZone(TimeZone.getTimeZone("GMT+11"));

                    // Format and print the output
                    String formattedDate = formatter.format(date);
                    matchDetails.put("Date ", formattedDate);
                } else {
                }
                // Extract match title and link
                Element matchTitleElement = match.selectFirst(".cb-col-60 a.text-hvr-underline");
                if (matchTitleElement != null) {
                    matchDetails.put("title", matchTitleElement.text());
                    matchDetails.put("matchLink", baseUrl + matchTitleElement.attr("href"));
                }

                // Extract series name
                String series = match.select(".cb-col-60 .text-gray").get(0).text();
                matchDetails.put("series", series);

                // Extract venue
                String venue = match.select(".cb-col-60 .text-gray").get(1).text();
                matchDetails.put("venue", venue);

                // Extract result
                String result = match.select(".cb-col-60 .cb-text-complete").text();
                matchDetails.put("result", result);

                // Add match details to the result list
                resultList.add(matchDetails);
            }

            // Convert the list of match details to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(resultList);
        }
        public String getNews(String teamName, String teamId) throws JsonProcessingException {
            // Define the URL to scrape
            String url = scrapebaseurl+"/cricket-team/" + teamName + "/" + teamId + "/news";

            // List to store news details
            List<Map<String, String>> newsList = new ArrayList<>();

            try {
                // Connect to the URL and parse the HTML document
                Document doc = Jsoup.connect(url).get();

                // Select the news list container
                Elements newsElements = doc.select("#series-news-list .cb-lst-itm");
System.out.println("jemin ====== "+newsElements);
                // Iterate through each news item
                for (Element newsElement : newsElements) {
                    // Extract the headline, link, and description
                    String headline = newsElement.select(".cb-nws-hdln").text();
                    String link = baseUrl+ newsElement.select(".cb-nws-hdln a").attr("href");
                    String description = newsElement.select(".cb-nws-intr").text();
                    String date = newsElement.select(".cb-nws-time").text();
                    String imageUrl = newsElement.select("img").hasAttr("src")
                            ? newsElement.select("img").attr("src")
                            : newsElement.select("img").hasAttr("source")
                            ? newsElement.select("img").attr("source")
                            : null;
                    // Store the extracted data in a map
                    Map<String, String> newsMap = new HashMap<>();
                    newsMap.put("headline", headline);
                    newsMap.put("link", link);
                    newsMap.put("description", description);
                    newsMap.put("date", date);
                    newsMap.put("imageUrl", imageUrl);

                    // Add the map to the list
                    newsList.add(newsMap);
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "Error fetching news.";
            }

            // Convert the list of photos to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(newsList);
        }
        public String getNewsDetails(String newsId, String newsName) throws JsonProcessingException {
            // Define the URL to scrape
            String url = scrapebaseurl+"/cricket-news/" + newsId + "/" + newsName ;
            // List to store news details
            List<Map<String, String>> newsList = new ArrayList<>();

            try {
                // Connect to the URL and parse the HTML document
                Document doc = Jsoup.connect(url).get();
                System.out.println(doc+"--===ue=======l");

                // Select the news list container
                Map<String, String> newsMap = new HashMap<>();
                    newsMap.put("title", doc.select("h1.nws-dtl-hdln").text());
                newsMap.put("imageUrl", scrapebaseurl+doc.select("img.cursor-pointer").attr("src"));
                newsMap.put("title", doc.select("h1.nws-dtl-hdln").text());
                newsMap.put("description", doc.select("p.cb-nws-para").text());

                    // Add the map to the list
                    newsList.add(newsMap);


            } catch (IOException e) {
                e.printStackTrace();
                return "Error fetching news.";
            }

            // Convert the list of photos to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(newsList);
        }

    public String getTeamSchedule(String teamName, String teamId) {
        String url = "https://www.cricbuzz.com/cricket-team/" + teamName + "/" + teamId + "/schedule";
        List<Map<String, String>> scheduleList = new ArrayList<>();

        try {
            // Fetch and parse the HTML content
            Document document = Jsoup.connect(url).get();

            // Select all match blocks
            Elements matches = document.select(".cb-series-matches");

            for (Element match : matches) {
                Map<String, String> matchDetails = new HashMap<>();

                // Get match date
                // Extract match date
                Element dateElement = match.selectFirst(".schedule-date span");
                if (dateElement != null) {
                    // Parsing date from 'ng-bind' or direct text content
                    String rawDate = dateElement.attr("ng-bind"); // If date is inside 'ng-bind'
                    String timestampString = rawDate.split("\\|")[0].trim();
                    long timestamp = Long.parseLong(timestampString); // Convert to long

                    // Convert timestamp to a Date object
                    Date date = new Date(timestamp);

                    // Format the date to display "MMM dd, EEE HH:mm:ss z"
                    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, EEE HH:mm:ss z");

                    // Set time zone (+11:00 extracted from the ng-bind value)
//                    formatter.setTimeZone(TimeZone.getTimeZone("GMT+11"));

                    // Format and print the output
                    String formattedDate = formatter.format(date);
                    matchDetails.put("Date ", formattedDate);

                } else {
                    matchDetails.put("date", "N/A");
                }


                // Format the extracted date (if needed)
//                    matchDetails.put("date", rawDate);
                // Get match title
                Element titleElement = match.selectFirst(".cb-srs-mtchs-tm a span");
                matchDetails.put("title", titleElement != null ? titleElement.text() : "N/A");
                String matchLinkElement = match.selectFirst(".cb-srs-mtchs-tm a").attr("href");
                matchDetails.put("match_link", baseUrl+matchLinkElement != null ? baseUrl+matchLinkElement : "N/A");

                // Get series title
                Element seriesElement = match.selectFirst(".cb-srs-mtchs-tm .text-gray");
                matchDetails.put("series", seriesElement != null ? seriesElement.text() : "N/A");

                // Get venue
                Element venueElement = match.selectFirst(".text-gray.cb-ovr-flo");
                matchDetails.put("venue", venueElement != null ? venueElement.text() : "N/A");

                // Get match start time and timezone
                Element timeElement = match.selectFirst(".cb-text-upcoming");
                matchDetails.put("startTime", timeElement != null ? timeElement.text() : "N/A");


                // Add match details to the list
                scheduleList.add(matchDetails);
            }

            // Convert the list to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(scheduleList);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Unable to fetch schedule\"}";
        }
    }
    public String getPhotos(String teamName, String teamId) {
        // Build the URL for fetching photos
        String url = scrapebaseurl+"/cricket-team/" + teamName + "/" + teamId + "/photos";
        List<Map<String, String>> photoList = new ArrayList<>();

        try {
            // Fetch and parse the HTML content
            Document document = Jsoup.connect(url).get();

            // Select the main container holding the photos
            Elements photoElements = document.select("#cb-pht-main .cb-pht-block");

            // Iterate over each photo block
            for (Element photoElement : photoElements) {
                // Extract photo details
                String title = photoElement.select("a").attr("title");


                // Extract image link
                String link = photoElement.select("img").attr("source");
                if (!link.startsWith("http")) {
                    link = baseUrl+ link;
                }

                // Extract the publication date
                String date = photoElement.select("meta[itemprop='datePublished']").attr("content");

                // Create a map for the photo details
                Map<String, String> photoData = new HashMap<>();
                photoData.put("title", title);
                photoData.put("link", link);
                photoData.put("date", date);

                // Add the photo data to the list
                photoList.add(photoData);
            }

            // Convert the list of photos to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(photoList);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Unable to fetch photos\"}";
        }
    }


    public String getTeams(String teamlevel) throws JsonProcessingException {
        String url = "https://www.cricbuzz.com/cricket-team"+"/"+teamlevel;
        List<Map<String, String>> teamList = new ArrayList<>();

        try {
            // Fetch and parse the HTML document
            Document document = Jsoup.connect(url).get();

            // Select team containers
            Elements teamContainers = document.select("div.cb-team-item");

            // List to hold cricket teams
//            List<CricketTeam> teams = new ArrayList<>();

            for (Element teamContainer : teamContainers) {
                Map<String, String> teamData = new HashMap<>();

                // Extract team name
                String name = teamContainer.select("a").text();
                teamData.put("title",name);
                // Extract team image URL
                String imageUrl = teamContainer.select("img.cb-lst-img").attr("src");
                teamData.put("image_url",imageUrl);
//                "/players");
//                team.put("photos_link", baseUrl + "/cricket-team/" + title.toLowerCase().replace(" ", "-") + "/" + id + "/photos");
//                team.put("news_link", baseUrl + "/cricket-team/" + title.toLowerCase().replace(" ", "-") + "/" + id + "/news");
//                team.put("results_link", baseUrl + "/cricket-team/" + title.toLowerCase().replace(" ", "-") + "/" + id + "/results");
//                team.put("schedule_link", baseUrl + "/cricket-team/" + title.toLowerCase().replace(" ", "-") + "/" + id + "/schedule");
                // Extract team link
                String teamLink = teamContainer.select("a.cb-teams-flag-img").attr("href");
                teamData.put("player_link",baseUrl+teamLink+"/players");
                teamData.put("photos_link",baseUrl+teamLink+"/photos");
                teamData.put("news_link",baseUrl+teamLink+"/news");
                teamData.put("results_link",baseUrl+teamLink+"/results");
                teamData.put("schedule_link",baseUrl+teamLink+"/schedule");

                // Add "https://www.cricbuzz.com" to relative links
                if (!teamLink.startsWith("http")) {
                    teamLink = "https://www.cricbuzz.com" + teamLink;
                }

                // Create a CricketTeam object and add it to the list
                teamList.add(teamData);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert the list of players to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(teamList);

    }
    public String getPlayer(String teamName, String teamId) {
        // Build the URL to fetch the team stats
        String url = scrapebaseurl+"/cricket-team/" + teamName + "/" + teamId + "/players";
        List<Map<String, String>> playerList = new ArrayList<>();

        try {
            // Fetch and parse the HTML content
            Document document = Jsoup.connect(url).get();

            // Select the main container holding the players' details
            Elements rows = document.select("#series-news-list > .cb-col-67.cb-col.cb-left.cb-top-zero > a");

            String currentRole = ""; // To keep track of the player's role

            for (Element row : rows) {
                // Check if there's a new role heading (e.g., BATSMEN)
                Element roleElement = row.previousElementSibling();
                if (roleElement != null && roleElement.tagName().equals("h3")) {
                    currentRole = roleElement.text();
                }

                Map<String, String> playerData = new HashMap<>();

                // Extract player name
                String playerName = row.select(".cb-font-16.text-hvr-underline").text();
                playerData.put("name", playerName);

                // Assign the current role
                playerData.put("role", currentRole);

                // Extract player profile URL
                String profileUrl =  row.attr("href");
                playerData.put("profileUrl", profileUrl);

                // Extract player image URL
                String imageUrl = row.select("img").attr("src");
                playerData.put("imageUrl", imageUrl);

                // Add the player data to the list
                playerList.add(playerData);
            }

            // Convert the list of players to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(playerList);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Unable to fetch team stats\"}";
        }
    }




}
