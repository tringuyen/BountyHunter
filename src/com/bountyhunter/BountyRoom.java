package com.bountyhunter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BountyRoom extends ListActivity {
	
	private ListView lv1;
	private String [] room_names;
	private TextView roomtext;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		// Update with the list of rooms
		updateRoomList();

		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, room_names));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view,
		   int position, long id) {
		     // When clicked, show a toast with the TextView text
		     Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
		         Toast.LENGTH_SHORT).show();
		   }
		});
	}
	
	public void updateRoomList() {
		try {
			JSONObject rooms = new JSONObject(BountyRequest.getResponse("gamemode"));
			JSONArray roomlist = rooms.getJSONArray("roomList");
			JSONObject cur_room;
			room_names = new String[roomlist.length()];
		
			for (int i = 0; i < roomlist.length(); i++) {
				cur_room = roomlist.getJSONObject(i);
				room_names[i] = cur_room.getString("roomName");
			}
			
		} catch(JSONException e) {
			e.printStackTrace();
		}
		/*BountyServer.get("gamemode", null, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				
				try {
					JSONObject rooms = new JSONObject(response);
					JSONArray roomlist = rooms.getJSONArray("roomList");
					JSONObject cur_room;
					room_names = new String[roomlist.length()];
					roomtext.setText(String.valueOf(roomlist.length()));
					
					for (int i = 0; i < roomlist.length(); i++) {
						cur_room = roomlist.getJSONObject(i);
						room_names[i] = cur_room.getString("roomName");
					}
					
				} catch(JSONException e) {
					e.printStackTrace();
				}
				
			}
		});*/
	
	}
	
	
}