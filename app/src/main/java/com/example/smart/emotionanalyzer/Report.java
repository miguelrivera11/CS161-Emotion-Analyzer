package com.example.smart.emotionanalyzer;

import java.util.ArrayList;
import java.util.HashMap;

public class Report {
    final public static int SPAM = 0;
    final public static int INAPPROPIATE = 1;
    final public static int DUPLICATE = 2;

    private HashMap <String, Integer> reportDetails;
    private ArrayList<String> reporters;
    private String topicID;

    public Report(String topicID) {
        reportDetails = new HashMap<>();
        reportDetails.put("spam", 0);
        reportDetails.put("inappropiate", 0);
        reportDetails.put("duplicate", 0);
        reporters = new ArrayList<>();
        this.topicID = topicID;
    }

    public Report() {
    }


    public HashMap<String, Integer> getReportDetails() {
        return reportDetails;
    }

    public String getTopicID() {
        return topicID;
    }

    public ArrayList<String> getReporters() {
        return reporters;
    }

    public void reportTopic(int reportType, String reporterID) {
        if (reportType == Report.DUPLICATE) {
            int duplicateCount = reportDetails.get("duplicate");
            reportDetails.put("duplicate", duplicateCount + 1);
        }
        else if (reportType == Report.INAPPROPIATE) {
            int inappropiateCount = reportDetails.get("inappropiate");
            reportDetails.put("inappropiate", inappropiateCount + 1);
        }
        else if (reportType == Report.SPAM) {
            int spamCount = reportDetails.get("spam");
            reportDetails.put("spam", spamCount + 1);
        }
        reporters.add(reporterID);
    }
}
