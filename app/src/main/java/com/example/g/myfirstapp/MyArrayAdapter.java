package com.example.g.myfirstapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SnapshotMetadata;

import java.util.ArrayList;

public class MyArrayAdapter extends ArrayAdapter<DocumentSnapshot> implements Filterable {

    /**
     * Lock used to modify the content of {@link #objects}. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */
    private final Object mLock = new Object();
    private ArrayList<DocumentSnapshot>  mOriginalValues;

    private ArrayList<DocumentSnapshot> objects;

    public MyArrayAdapter(Context context, ArrayList<DocumentSnapshot> users) {
        super(context, 0, users);
        mOriginalValues=users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DocumentSnapshot user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(android.R.id.text1);
        // Populate the data into the template view using the data object
        tvName.setText((CharSequence) user.get("email"));
        // Return the completed view to render on screen
        return convertView;
    }
    Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<DocumentSnapshot> tempList=new ArrayList<DocumentSnapshot>();
            //constraint is the result from text you want to filter against.
            //objects is your data set you will filter from
            final ArrayList<DocumentSnapshot> values;
            synchronized (mLock) {
                values = new ArrayList<>(mOriginalValues);
            }
            if(constraint != null && values!=null) {
                int length=values.size();
                final String prefixString = constraint.toString().toLowerCase();
                for (int i = 0; i < length; i++) {
                    final DocumentSnapshot value = values.get(i);
                    String mail = (String) value.get("email");
                    final String valueText = mail.toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        tempList.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {
                                tempList.add(value);
                                break;
                            }
                        }
                    }
                }
                //following two lines is very important
                //as publish result can only take FilterResults objects
                filterResults.values = tempList;
                filterResults.count = tempList.size();
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence contraint, FilterResults results) {
            objects = (ArrayList<DocumentSnapshot>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };

    @Override
    public Filter getFilter() {
        return myFilter;
    }

}