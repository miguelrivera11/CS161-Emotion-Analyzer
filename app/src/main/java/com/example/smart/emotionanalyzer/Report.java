package com.example.smart.emotionanalyzer;

import java.util.HashMap;

public class Report {
    final public static int SPAM = 0;
    final public static int INAPPROPIATE = 1;
    final public static int DUPLICATE = 2;

    private HashMap <String, Integer> reportDetails;
    private String topicID;

    public Report(String topicID) {
        reportDetails = new HashMap<>();
        reportDetails.put("spam", 0);
        reportDetails.put("inappropiate", 0);
        reportDetails.put("duplicate", 0);
        this.topicID = topicID;
    }


    public HashMap<String, Integer> getReportDetails() {
        return reportDetails;
    }

}
