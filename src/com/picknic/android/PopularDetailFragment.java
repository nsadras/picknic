package com.picknic.android;

import java.util.List;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.picknic.android.content.RewardListContent;

/**
 * A fragment representing a single Event detail screen. This fragment is either
 * contained in a {@link EventListActivity} in two-pane mode (on tablets) or a
 * {@link PopularDetailActivity} on handsets.
 */
public class PopularDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private RewardListContent.RewardItem mItem;
	private Button redeemButton;
	private AlertDialog dialog;
	AlertDialog.Builder builder;
	private ParseUser user;
	boolean claimed;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PopularDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = RewardListContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
			user = ParseUser.getCurrentUser(); // is this the best place for this?
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_deal_detail,
				container, false);

		// Show the content as text in a TextView.
		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.deal_detail))
					.setText(mItem.deal.getString("descLong"));
			

			builder = new AlertDialog.Builder(getActivity());
			
			redeemButton = ((Button) rootView.findViewById(R.id.redeemButton));
			if(mItem.claimed){
				Log.d("claimed", "already claimed");
				setAlreadyClaimed();
			} else {
				Log.d("not claimed", "not claimed");
				redeemButton.setText("Redeem - " + mItem.deal.getInt("cost") + " points");
				redeemButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						onRedeemButtonClick();	
					}
					
				});
			}
			
			
		} 

		return rootView;
	}
	
	private void onRedeemButtonClick(){
		//implement
		Log.d("debug", "redeem button pressed");
		// TODO: add call to refresh, maybe throw in the loading dialog
		int userPoints = user.getInt("points");
		int dealCost = mItem.deal.getInt("cost");
		if(userPoints < dealCost){
			builder.setMessage("Sorry, not enough points")
		       .setTitle("Error");
			dialog = builder.create();
			dialog.show();
		} else {
			user.put("points", userPoints - dealCost);				// subtract points
			ParseRelation<ParseObject> deals = user.getRelation("deals");
			deals.add(mItem.deal);	// update user's list of claimed deals	
			user.saveInBackground();
			
			ParseObject deal = mItem.deal;
			deal.increment("numClaimed");
			deal.saveInBackground();
			
			mItem.claimed = true; // change redeem button behavior
			
			builder.setMessage("Enjoy your rewards!")
		       .setTitle("Congrats!");
			dialog = builder.create();
			dialog.show();
			setAlreadyClaimed(); 
		}
	}
	
	
	private void setAlreadyClaimed(){
		redeemButton.setText("Already Claimed");
		redeemButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				alreadyClaimed();	
			}
			
		});
	}
	/**
	 * Button behavior if this deal has already been claimed
	 */
	private void alreadyClaimed(){
		builder.setMessage("Already Claimed")
	       .setTitle("Error");
		dialog = builder.create();
		dialog.show();
	}
	
}
