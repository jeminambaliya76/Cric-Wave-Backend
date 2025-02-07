package com.live.cric.wave;

public class Temp {


//    @Service
//    public class CricketSceduleScrap {
//        private static final Logger logger = LoggerFactory.getLogger(com.live.cric.wave.scrapfiles.schedule.CricketSceduleScrap.class);
//
//        public String getInternationalSchedule() throws Exception {
//
//            Document doc = Jsoup.connect("https://www.cricbuzz.com/cricket-schedule/upcoming-series/international").get();
//            Element list = doc.getElementById("international-list");
//            Elements list2 = list.select("div.cb-col-100.cb-col").select("div.cb-col-67.cb-col");
//
//
//            System.out.println(list2.size());
//
//            List<Map<String, String>> scheduleList = new ArrayList<>();
//
//
//            for(Element lists : list2) {
//                Map<String, String> scheduleDetails = new HashMap<>();
//
//                scheduleDetails.put("date",lists.select("div.cb-ovr-flo.cb-col-50.cb-col.cb-mtchs-dy-vnu.cb-adjst-lst")
//                        .select("span[itemprop=startDate]").attr("content"));
//                scheduleDetails.put("seriesTitle", lists.select("a.cb-col-33.cb-col.cb-mtchs-dy.text-bold").text());
//                scheduleDetails.put("matchTitle", lists.select("div.cb-ovr-flo.cb-col-50.cb-col.cb-mtchs-dy-vnu.cb-adjst-lst")
//                        .select("a").text());
//                scheduleDetails.put("matchVenue", lists.select("div.cb-ovr-flo.cb-col-50.cb-col.cb-mtchs-dy-vnu.cb-adjst-lst")
//                        .select("div.cb-font-12.text-gray.cb-ovr-flo").text());
//                scheduleDetails.put("matchLink",  lists.select("div.cb-ovr-flo.cb-col-50.cb-col.cb-mtchs-dy-vnu.cb-adjst-lst")
//                        .select("a").attr("href"));
//
//                String matchTimestamp = lists.select("span.schedule-date").attr("timestamp");
//                String matchTimestampFormat = lists.select("span.schedule-date").attr("format");
//
//                if (!matchTimestamp.isEmpty()) {
//                    long timestampMillis = Long.parseLong(matchTimestamp);
//                    SimpleDateFormat formatter = new SimpleDateFormat(matchTimestampFormat);
//                    String formattedDate = formatter.format(new Date(timestampMillis));
//                    scheduleDetails.put("matchTime", formattedDate);
//                }
//
//                String matchFormat = lists.select("div.cb-col-50.cb-col.cb-mtchs-dy-tm.cb-adjst-lst")
//                        .select("div.cb-font-12.text-gray").text();
//                scheduleDetails.put("matchFormat", matchFormat);
//
//                scheduleList.add(scheduleDetails);
//            }
//
//            // Convert the list to JSON
//            ObjectMapper objectMapper = new ObjectMapper();
//            return objectMapper.writeValueAsString(scheduleList);
//        }
//    }

}
