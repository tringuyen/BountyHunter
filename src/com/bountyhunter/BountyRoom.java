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
	
	private String [] room_names;
	private String [] player_list;
	private JSONObject rooms;
	private JSONObject cur_room;
	private JSONArray playerList;
	private JSONArray roomlist;
	
	private ListView lv;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		// Update with the list of rooms
		updateRoomList();

		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, room_names));

		lv = getListView();
		lv.setTextFilterEnabled(true);

		// Listen for which item gets selected
		lv.setOnItemClickListener(new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view,
		   int position, long id) {
		     // When clicked, show a toast with the TextView text
		     Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
		         Toast.LENGTH_SHORT).show();
		     
		     // Display the room information
		     displayRoom(((TextView) view).getText().toString());
		   }
		});
	}
	
	public void updateRoomList() {
		try {
			rooms = new JSONObject(BountyRequest.getResponse("gamemode"));
			roomlist = rooms.getJSONArray("roomList");
			
			room_names = new String[roomlist.length()];
		
			for (int i = 0; i < roomlist.length(); i++) {
				cur_room = roomlist.getJSONObject(i);
				room_names[i] = cur_room.getString("roomName");
			}
			
		} catch(JSONException e) {
			e.printStackTrace();
		}
	
	}
	
	public void displayRoom(String target_room) {
		try {
			// Reset the list
			
			// Search for the current room
			for (int i = 0; i < roomlist.length(); i++) {
				cur_room = roomlist.getJSONObject(i);
				// Find the room we want to display
				if (target_room.equals(cur_room.getString("roomName"))) {
					setContentView(R.layout.display_room);
					
					// Get the list of players and store it in our array
					player_list = new String[cur_room.getInt("numOfPlayers")];
					playerList = cur_room.getJSONArray("listOfPlayerNames");
					for (int j = 0; j < cur_room.getInt("numOfPlayers"); j++) {
						player_list[j] = playerList.getString(j);
					}
					
					TextView roomName = (TextView) findViewById(R.id.RoomNameData);
					TextView owner = (TextView) findViewById(R.id.OwnerData);
					TextView status = (TextView) findViewById(R.id.StatusData);
					TextView numOfPlayers = (TextView) findViewById(R.id.NumPlayersData);
					
					roomName.setText(cur_room.getString("roomName"));
					owner.setText(cur_room.getString("ownerName"));
					status.setText(cur_room.getString("status"));
					numOfPlayers.setText(cur_room.getString("numOfPlayers"));
					
					setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, player_list));

				}
			}
			
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}
	
	
}