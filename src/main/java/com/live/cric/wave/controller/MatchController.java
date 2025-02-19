package com.live.cric.wave.controller;

import com.live.cric.wave.scrapfiles.schedule.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class MatchController {

    @Autowired
    private CricketScheduleScrap cricketScheduleScrap;
    @Autowired
    private CricketNewsScrap cricketNewsScrap;
    @Autowired
    private CricketTeamsScrap cricketTeamsScrap;
    @Autowired
    private CricketLiveScoreScrap cricketLiveScoreScrap;

    @Value("${base.url}")
    private String baseUrl;


    @GetMapping("/")
    public Map<String, String> getAllEndpoints() {
        Map<String, String> endpoints = new HashMap<>();

        // Cricket Schedules
        endpoints.put("Upcoming Series", "/cricket-schedule/upcoming-series/all");

        // Cricket News
        endpoints.put("All News", "/cricket-news");
        endpoints.put("All News Topics", "/all-topics");

        // Cricket Teams
        endpoints.put("All Teams", "/teams");

        // Live Cricket Matches
        endpoints.put("Live Matches", "/cricketmatch/livematches");
        endpoints.put("Live Scores", "/live-cricket-scores/{matchId}/{matchName}");

        // Player Profiles
        endpoints.put("Player Profile", "/profiles/{playerId}/{playerName}");

        // News Details
        endpoints.put("News Details", "/cricket-news/{newsId}/{newsName}");

        return endpoints;
    }

    // ====================== Cricket Schedules ======================

    @GetMapping("/cricket-schedule/upcoming-series/all")
    public String getDomesticSchedule() throws Exception {
        return cricketScheduleScrap.getAllSchedule();
    }

    // ====================== Cricket News ======================
    @GetMapping("/cricket-news")
    public String getAllNews(
            @RequestParam(required = false) String newsId,
            @RequestParam(required = false) String newsTopic) throws Exception {

        return cricketNewsScrap.getAllNews(newsId, newsTopic);
    }

    @GetMapping("/cricket-news/info/{newsId}/{newsTopic}")
    public String getTopicAllNews(@PathVariable String newsId, @PathVariable String newsTopic) throws Exception {

        return cricketNewsScrap.getAllNews("info/"+newsId, newsTopic);
    }
    //++++++++++++++++++++++++++++++++++++pending+++++++++++++++++++++++++++++++
    @GetMapping("/all-topics")
    public String getNewsTopics() throws Exception {
        return cricketNewsScrap.getAllTopics();
    }

    // ====================== Cricket Teams ======================
    @GetMapping("/teams")
    public String getAllTeams() throws IOException {
        return cricketTeamsScrap.getTeams("");
    }

    @GetMapping("/teams/{teamLevel}")
    public String getTeamsByLevel(@PathVariable String teamLevel) throws IOException {
        return cricketTeamsScrap.getTeams(teamLevel);
    }

    @GetMapping("/cricket-team/{teamName}/{teamId}/players")
    public String getTeamPlayers(@PathVariable String teamName, @PathVariable String teamId) throws IOException {
        return cricketTeamsScrap.getPlayer(teamName, teamId);
    }

    @GetMapping("/cricket-team/{teamName}/{teamId}/photos")
    public String getTeamPhotos(@PathVariable String teamName, @PathVariable String teamId) throws IOException {
        return cricketTeamsScrap.getPhotos(teamName, teamId);
    }

    @GetMapping("/cricket-team/{teamName}/{teamId}/schedule")
    public String getTeamSchedule(@PathVariable String teamName, @PathVariable String teamId) throws IOException {
        return cricketTeamsScrap.getTeamSchedule(teamName, teamId);
    }

    @GetMapping("/cricket-team/{teamName}/{teamId}/results")
    public String getTeamResults(@PathVariable String teamName, @PathVariable String teamId) throws IOException {
        return cricketTeamsScrap.getResults(teamName, teamId);
    }

    @GetMapping("/cricket-team/{teamName}/{teamId}/news")
    public String getTeamNews(@PathVariable String teamName, @PathVariable String teamId) throws IOException {
        return cricketTeamsScrap.getNews(teamName, teamId);
    }

    // ====================== Live Cricket Matches ======================
    @GetMapping("/cricketmatch/livematches")
    public String getLiveMatches() throws IOException {
        return cricketLiveScoreScrap.getLiveMatches();
    }

    @GetMapping("/live-cricket-scores/{matchId}/{matchName}")
    public String getLiveScore(@PathVariable String matchId, @PathVariable String matchName) throws IOException {
        return cricketLiveScoreScrap.getLiveScore(matchId, matchName);
    }

    @GetMapping("/cricket-scores/{matchId}/{matchName}")
    public String getLiveScore2(@PathVariable String matchId, @PathVariable String matchName) throws IOException {
        return cricketLiveScoreScrap.getLiveScore(matchId, matchName);
    }
    // ====================== Player Profiles ======================
    @GetMapping("/profiles/{playerId}/{playerName}")
    public String getPlayerDetails(@PathVariable String playerId, @PathVariable String playerName) throws IOException {
        return cricketTeamsScrap.getPlayerDetails(playerId, playerName);
    }

    // ====================== News Details ======================
    @GetMapping("/cricket-news/{newsId}/{newsName}")
    public String getNewsDetails(@PathVariable String newsId, @PathVariable String newsName) throws IOException {
        return cricketTeamsScrap.getNewsDetails(newsId, newsName);
    }
}