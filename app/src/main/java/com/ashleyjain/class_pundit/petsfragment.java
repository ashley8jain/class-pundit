package com.ashleyjain.class_pundit;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ashleyjain on 24/06/16.
 */
public class petsfragment extends Fragment {

    HashMap cat,sub_cat,cat_sub_cat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pets_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout kids_ll = (LinearLayout) view.findViewById(R.id.pets_layout);
        Button search = (Button) view.findViewById(R.id.filtersearch);
        Button cancel = (Button) view.findViewById(R.id.filtercancel);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity.get().filtered();
                MapsActivity.overlay.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity.overlay.dismiss();
            }
        });

        cat = new HashMap();
        sub_cat = new HashMap();
        Iterator<?> keys = MapsActivity.pets.keys();
        int id=0;
        while( keys.hasNext() ) {
            String key = (String) keys.next();
            try {
                if (MapsActivity.pets.get(key) instanceof JSONObject) {
                    id++;
                    CheckBox cb = new CheckBox(getActivity());
                    cb.setText((String)MapsActivity.cat_hm.get(key));
                    cb.setTypeface(Typeface.DEFAULT_BOLD);
                    cb.setTextSize(20);
                    cb.setId(id);
                    cb.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                    cb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onCheckBoxClicked(v);
                        }
                    });
                    kids_ll.addView(cb);
                    int id2 = 0;
                    JSONObject subcat = (JSONObject) MapsActivity.pets.get(key);
                    Iterator<?> keys2 = subcat.keys();
                    cat_sub_cat = new HashMap();
                    while( keys2.hasNext() ) {
                        id2++;
                        String key2 = (String) keys2.next();
                        CheckBox cb2 = new CheckBox(getActivity());
                        cb2.setText((String)MapsActivity.subcat_hm.get(key2));
                        cb2.setId(id*1000+id2);
                        cb2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onCheckBoxClicked(v);
                            }
                        });
                        kids_ll.addView(cb2);
                        cat_sub_cat.put(cb2,subcat.getString(key2));
                        sub_cat.put(cb2,subcat.getString(key2));
                    }
                    cat.put(cb,cat_sub_cat);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void onCheckBoxClicked(View v){
        boolean checked = ((CheckBox) v).isChecked();
        if(checked){
            if(cat.containsKey(v)){
                HashMap<CheckBox,String> hm2 = (HashMap) cat.get(v);
                for (Map.Entry<CheckBox,String> entry : hm2.entrySet())
                {
                    entry.getKey().setChecked(true);
                    String[] filtr = entry.getValue().split(",");
                    for(String sd:filtr){
                        MapsActivity.filter.add(sd);
                    }
                    //System.out.println(entry.getKey() + "/" + entry.getValue());
                }
            }
            else if(sub_cat.containsKey(v)){
                String[] fil = ((String) sub_cat.get(v)).split(",");
                for(String sd:fil){
                    MapsActivity.filter.add(sd);
                }
            }
        }
        else{
            if(cat.containsKey(v)){
                HashMap<CheckBox,String> hm2 = (HashMap) cat.get(v);
                for (Map.Entry<CheckBox,String> entry : hm2.entrySet())
                {
                    entry.getKey().setChecked(false);
                    String[] filtr = entry.getValue().split(",");
                    for(String sd:filtr){
                        MapsActivity.filter.remove(sd);
                    }
                }
            }
            else if(sub_cat.containsKey(v)){
                String[] fil = ((String) sub_cat.get(v)).split(",");
                for(String sd:fil){
                    MapsActivity.filter.remove(sd);
                }
            }
        }


    }
}
