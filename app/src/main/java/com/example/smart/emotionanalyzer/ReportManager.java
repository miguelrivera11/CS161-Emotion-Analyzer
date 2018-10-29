package com.example.smart.emotionanalyzer;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReportManager {
    private DatabaseReference reportsRef;

    public ReportManager() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reportsRef = database.getReference("Reports/");
    }

    public void addReport(final String topicID, final String reporterID, final int reportType) {
        reportsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userReportedAlready = false;
                Report report = new Report(topicID);
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    if(child.getKey().toString().equals(topicID)) {
                        report = child.getValue(Report.class);
                        if(report.getReporters().contains(reporterID)){
                            userReportedAlready = true;
                        }
                        break;
                    }
                }
                if (!userReportedAlready) {
                    report.reportTopic(reportType, reporterID);
                    reportsRef.child(topicID).setValue(report);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteReport(String topicID) {
        reportsRef.child(topicID).removeValue();
    }
}
