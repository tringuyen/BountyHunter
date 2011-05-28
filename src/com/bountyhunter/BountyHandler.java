package com.bountyhunter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

public class BountyHandler extends JsonHttpResponseHandler {
	public String [] room_names;
	private Context mContext;
	private ListView mLv1;
	private TextView roomtext;
	
	public BountyHandler(Context context, ListView lv, TextView text) {
		super();
		mContext= context;
		mLv1 = lv;
		roomtext = text;
	}
	
	@Override
	public void onSuccess(String response) {
		
		try {
			JSONObject rooms = new JSONObject(response);
			JSONArray roomlist = rooms.getJSONArray("roomList");
			JSONObject cur_room;
			room_names = new String[roomlist.length()];
			
			for (int i = 0; i < roomlist.length(); i++) {
				cur_room = roomlist.getJSONObject(i);
				room_names[i] = cur_room.getString("roomName");
			}
			
			roomtext.setText(String.valueOf(roomlist.length()));
			mLv1.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, room_names));
			
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
	}
}
