package com.example.smart.emotionanalyzer;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private UserManager userManager = new UserManager();
    private TopicDatabaseManager topicManager = new TopicDatabaseManager(null);
    private Topic topic;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData, Topic topic) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.topic = topic;
        TopicDetail.updatedTopic = topic;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference topicsRef = database.getReference("Topics");
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        final ImageView options = (ImageView) convertView.findViewById(R.id.imageViewReplyOptions);

        final TextView info = (TextView) convertView.findViewById(R.id.infoTextView);
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        if(topic.getComments() != null && groupPosition < topic.getComments().size()) {
            int position =topic.getComments().size() - groupPosition - 1;
            Comment comment = topic.getComments().get(position);
            if (comment.getReplies() != null && childPosition < comment.getReplies().size()) {
                int replyIndex = comment.replies.size() - childPosition - 1;
                Comment replyMessage = comment.getReplies().get(replyIndex);
                setupReplyMenu(options, replyMessage);
                info.setText(replyMessage.getCreator() + " | "  + replyMessage.getDate());
            }

        }
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference topicsRef = database.getReference("Topics");

        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
        final ImageView options = (ImageView) convertView.findViewById(R.id.imageViewOptions);

        final ImageView profile = (ImageView) convertView.findViewById(R.id.profileImageView);
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        final TextView info = (TextView) convertView.findViewById(R.id.infoTextView);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        if(topic.getComments() != null && groupPosition < topic.getComments().size()) {
            int position =topic.getComments().size() - groupPosition - 1;
            Comment c = topic.getComments().get(position);
            userManager.displayProfilePicture(profile, null, c.getCreatorID());
            setUpCommentMenu(options, c);
            info.setText(c.getCreator() + "  |  " + c.getDate());
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void setupReplyMenu(ImageView options, final Comment reply) {
        if (!userManager.getUserID().equals(reply.creatorID)) {
            options.setVisibility(View.INVISIBLE);
            return;
        }
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popUp = new PopupMenu(_context, view);
                MenuInflater inflater = popUp.getMenuInflater();
                inflater.inflate(R.menu.comment_menu, popUp.getMenu());

                popUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        topic.removeReply(reply);
                        topicManager.updateTopicAlreadyInDatabase(topic);
                        return true;
                    }
                });
                popUp.show();
            }
        });
    }

    private void setUpCommentMenu(ImageView options, final Comment comment) {
        if (!userManager.getUserID().equals(comment.creatorID) && !userManager.getUserID().equals(topic.getCreatorID())) {
            options.setVisibility(View.INVISIBLE);
            return;
        }
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popUp = new PopupMenu(_context, view);
                MenuInflater inflater = popUp.getMenuInflater();
                inflater.inflate(R.menu.comment_menu, popUp.getMenu());

                popUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        topic.removeComment(comment);
                        topicManager.updateTopicAlreadyInDatabase(topic);
                        return true;
                    }
                });
                popUp.show();
            }
        });
    }
}
