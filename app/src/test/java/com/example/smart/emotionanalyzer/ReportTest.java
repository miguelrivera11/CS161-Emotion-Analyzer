package com.example.smart.emotionanalyzer;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ReportTest {

    Report report = new Report("LSmpgDHWV_XSDmP8Blt");

    @Test
    public void reportDetails() {
        ArrayList<String> reporters = new ArrayList<>();
        reporters.add("MP4EkiuA87YRtHilCLuiXVkKvfz1");
        report.reportTopic(0, "MP4EkiuA87YRtHilCLuiXVkKvfz1");
        report.getReporters();
        assertEquals("LSmpgDHWV_XSDmP8Blt", report.getTopicID());
        assertArrayEquals(reporters.toArray(), report.getReporters().toArray());
        assertEquals(1,(int) report.getReportDetails().get("spam"));

    }
}