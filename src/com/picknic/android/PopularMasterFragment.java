package com.picknic.android;

import com.picknic.android.content.RewardListContent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/*
 *  A fragment that displays the most popular deals
 */
public class PopularMasterFragment extends Fragment{
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_popular_master, container, false);
        Activity activity = getActivity();
        String listTag = "popular_list";
        
        Bundle arguments = new Bundle();
		//arguments.putString(PopularDetailFragment.ARG_ITEM_ID, id);
		PopularListFragment fragment = new PopularListFragment();
		fragment.setArguments(arguments);
		
		getChildFragmentManager().beginTransaction()
				.add(R.id.event_list_container, fragment, listTag).commit();
		
        if (activity.findViewById(R.id.event_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			
			// In two-pane mode, list items should be given the
			// 'activated' state when touched.

			((PopularListFragment) getChildFragmentManager().findFragmentByTag(
					listTag)).setActivateOnItemClick(true);
		}
        
		if(savedInstanceState == null){	
			RewardListContent.setContent(activity, this);	
		}
        return rootView;
    }

}
