package com.picknic.android;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
			
			redeemButton = ((Button) rootView.findViewById(R.id.redeemButton));
			redeemButton.setText("Redeem - " + mItem.deal.getInt("cost") + " points");
			redeemButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onRedeemButtonClick();	
				}
				
			});
			
		} 

		return rootView;
	}
	
	private void onRedeemButtonClick(){
		//implement
		Log.d("debug", "redeem button pressed");
		ParseUser user = ParseUser.getCurrentUser();
		// TODO: add call to refresh, maybe throw in the loading dialog
		int user_points = user.getInt("points");
		int deal_cost = mItem.deal.getInt("cost");
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		if(user_points < deal_cost){
			builder.setMessage("Sorry, not enough points")
		       .setTitle("Error");
			dialog = builder.create();
			dialog.show();
		} else {
			user.put("points", user_points - deal_cost);
			user.saveInBackground();
			builder.setMessage("Enjoy your rewards!")
		       .setTitle("Congrats!");
			dialog = builder.create();
			dialog.show();
		}
	}
}
