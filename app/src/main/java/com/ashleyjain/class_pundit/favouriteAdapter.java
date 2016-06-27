package com.ashleyjain.class_pundit;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

/**
 * Created by ashleyjain on 22/06/16.
 */
public class favouriteAdapter extends BaseAdapter {

    Context context;
    ArrayList<MapsActivity.providerdetail> favouriteList;

    TextView title,address,classes,phone2,mail;
    LikeButton favourite_button;

    public favouriteAdapter(Context context,ArrayList<MapsActivity.providerdetail> favouriteList){
        this.context = context;
        this.favouriteList = favouriteList;
    }

    @Override
    public int getCount() {
        return favouriteList.size();
    }

    @Override
    public Object getItem(int position) {
        return favouriteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return favouriteList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.favourite_list_item, null);
        }

        title = (TextView) convertView.findViewById(R.id.title);
        address = (TextView) convertView.findViewById(R.id.address);
        classes = (TextView) convertView.findViewById(R.id.classes);
        phone2 = (TextView) convertView.findViewById(R.id.phone);
        mail = (TextView) convertView.findViewById(R.id.email);
        favourite_button = (LikeButton) convertView.findViewById(R.id.favourite_button);

        final MapsActivity.providerdetail row = favouriteList.get(position);

        title.setText(row.getName_provider());
        address.setText(row.getAddress());
        classes.setText("Classes offered: "+row.getMycat());
        phone2.setText("Phone: "+row.getPhone());
        mail.setText("Mail: "+row.getEmail());
        favourite_button.setLiked(MapsActivity.pref.getBoolean(row.getId(),false));
        favourite_button.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                MapsActivity.editor.remove(row.getId());
                MapsActivity.editor.putBoolean(row.getId(),true);
                MapsActivity.editor.commit();
            }
            @Override
            public void unLiked(LikeButton likeButton) {
                MapsActivity.editor.remove(row.getId());
                MapsActivity.editor.putBoolean(row.getId(),false);
                MapsActivity.editor.commit();
            }
        });

        return convertView;
    }
}
