package com.ashleyjain.class_pundit;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by ashleyjain on 24/06/16.
 */
public class kidsfragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.kids_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout kids_ll = (LinearLayout) view.findViewById(R.id.kids_layout);

        Iterator<?> keys = MapsActivity.kids.keys();
        while( keys.hasNext() ) {
            String key = (String) keys.next();
            try {
                if (MapsActivity.kids.get(key) instanceof JSONObject) {
                    CheckBox cb = new CheckBox(getActivity());
                    cb.setText((String)MapsActivity.cat_hm.get(key));
                    cb.setTypeface(Typeface.DEFAULT_BOLD);
                    cb.setTextSize(20);
                    cb.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                    kids_ll.addView(cb);

                    JSONObject subcat = (JSONObject) MapsActivity.kids.get(key);
                    Iterator<?> keys2 = subcat.keys();
                    while( keys2.hasNext() ) {
                        String key2 = (String) keys2.next();
                        MapsActivity.p("key2: "+key2);
                        CheckBox cb2 = new CheckBox(getActivity());
                        cb2.setText((String)MapsActivity.subcat_hm.get(key2));
                        kids_ll.addView(cb2);
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}