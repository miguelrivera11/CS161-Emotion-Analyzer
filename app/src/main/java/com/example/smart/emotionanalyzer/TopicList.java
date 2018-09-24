package com.example.smart.emotionanalyzer;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TopicList extends ArrayAdapter<Topic> {
    private Activity context;
    private List<Topic> topicList;

    public TopicList(Activity context, List<Topic> topicList) {
        super(context, R.layout.topic_menu_view);
        this.context = context;
        this.topicList = topicList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View view = inflater.inflate(R.layout.topic_menu_view, null, true);
        TextView topicName = view.findViewById(R.id.topic_name);
        TextView topicCategory = view.findViewById(R.id.topic_category);

        Topic topic = topicList.get(position);
        topicName.setText(topic.getTopic());
        topicCategory.setText(topic.getCategory());

        return view;
    }

    @Override
    public int getCount() {
        return topicList.size();
    }
}
