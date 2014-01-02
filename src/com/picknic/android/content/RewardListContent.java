package com.picknic.android.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for fetching app content from Parse
 */
public class RewardListContent {

	/**
	 * An array of reward items.
	 */
	public static List<RewardItem> ITEMS = new ArrayList<RewardItem>();

	/**
	 * A map of reward items, by ID.
	 */
	public static Map<String, RewardItem> ITEM_MAP = new HashMap<String, RewardItem>();

	public static void setContent(){
		addItem(new RewardItem("1", "Thing 1"));
		addItem(new RewardItem("2", "Thing 2"));
		addItem(new RewardItem("3", "Thing 3"));
	}
	

	private static void addItem(RewardItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	/**
	 * A reward that can be claimed by the user
	 */
	public static class RewardItem {
		public String id;
		public String content;

		public RewardItem(String id, String content) {
			this.id = id;
			this.content = content;
		}

		@Override
		public String toString() {
			return content;
		}
	}
}
