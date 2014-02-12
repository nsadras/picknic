package com.picknic.android.basket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Multimap;
import com.picknic.android.R;
 
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
	 
    private Context g_context;
    private List<String> g_listDataHeader; // header title
    private Multimap<String, ArrayList<PurchaseHistory>> g_listDataChild;
 
    public ExpandableListAdapter(Context context, List<String> listDataHeader, Multimap<String, ArrayList<PurchaseHistory>> listChildData) {
        this.g_context = context;
        this.g_listDataHeader = listDataHeader;
        this.g_listDataChild = listChildData;
    }
    
    @Override
    public PurchaseHistory getChild(int groupPosition, int childPosition) {
       	Log.d("groupPosition(childPosition)", " "+groupPosition + "("+childPosition+")");
    	int index = 0;

    	for (Iterator<ArrayList<PurchaseHistory>> iter = g_listDataChild.get(g_listDataHeader.get(groupPosition)).iterator(); iter.hasNext(); ) {
    		
    		if(childPosition == index) {    			
    			return iter.next().get(0);
    		}
    		else {
    			iter.next();
    			index++;	
    		}
    	}
		return null;
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
    	
    	PurchaseHistory entry = getChild(groupPosition, childPosition);    	
    	
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.g_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_item, null);
        }
        TextView tvProvider = (TextView) convertView.findViewById(R.id.tvProvider);
        TextView tvPoints = (TextView) convertView.findViewById(R.id.tvPoints);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        
        if(entry == null) {
    		Log.d("getChildView", "Entry not found in the map!!");            
            tvProvider.setText("No Info");
            tvPoints.setText("No Info");
            tvTime.setText("No Info");
    	}
        else {
            tvProvider.setText(entry.getProvider());
            tvPoints.setText(entry.getPoints());
            tvTime.setText(entry.getTime());	
        }        
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return this.g_listDataChild.get(this.g_listDataHeader.get(groupPosition)).size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this.g_listDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this.g_listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.g_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_group, null);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.listGrp);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
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
}