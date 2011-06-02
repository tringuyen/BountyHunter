package com.bountyhunter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
	
	private String roomClicked;
	private static final String BASE_URL = "http://good-hunting.appspot.com/gamemode";
	
	private ListView lv;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.rooms);
		
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
		     
		     roomClicked = ((TextView) view).getText().toString();
		     
		     // Create our pop-up first and join the room
		     // Intent myIntent = new Intent(BountyRoom.this, BountyDialogue.class);
		     // BountyRoom.this.startActivity(myIntent);
		     displayDialogue(roomClicked);
		     
		     
		   }
		});
	}
	
	public String getRoomClicked() {
		return roomClicked;
	}
	
	public void displayDialogue(String room) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		alert.setMessage("Desired user name to join room with: ");
		alert.setView(input);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String input_name = input.getText().toString().trim();
				Toast.makeText(getApplicationContext(), input_name,
						Toast.LENGTH_SHORT).show();
				
				// Send the join request to the server
				sendJoin(input_name, getRoomClicked());
				
				// Display the room information after joining the room
				displayRoom(getRoomClicked());
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.show();
		
	}
	
	public void sendJoin(String playerName, String roomName) {
		// Get the current location
		Location cur_location = BountyGetLocation.getLocation(this);
		float lat = (float) (cur_location.getLatitude());
		float lng = (float) (cur_location.getLongitude());
		
		Toast.makeText(this, "Latitude: " + String.valueOf(lat), Toast.LENGTH_SHORT).show();

		String responseString = "";
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(BASE_URL);
		
		try {
			// Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
	        nameValuePairs.add(new BasicNameValuePair("playerName", playerName));
	        nameValuePairs.add(new BasicNameValuePair("RoomName", roomName));
	        nameValuePairs.add(new BasicNameValuePair("latitude", Float.toString(lat)));
	        nameValuePairs.add(new BasicNameValuePair("longitude", Float.toString(lng)));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        
			HttpResponse response = httpclient.execute(httppost);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK){
			    ByteArrayOutputStream out = new ByteArrayOutputStream();
			    response.getEntity().writeTo(out);
			    out.close();
			    responseString = out.toString();
			      
			} else {
			    //Closes the connection.
			    response.getEntity().getContent().close();
			    throw new IOException(statusLine.getReasonPhrase());
			}
		  } catch (Exception e) {
			  // Do something
			  e.printStackTrace();
		  }
		 
		 // Parse JSON response
		 try {
			 JSONObject json_response = new JSONObject(responseString);
			 
			 // Display the correct response
			 if (!json_response.isNull("creation")) {
				 Toast.makeText(this, json_response.getString("creation"), Toast.LENGTH_SHORT).show();
			 } 
			 else {
				 Toast.makeText(this, json_response.getString("joining"), Toast.LENGTH_SHORT).show();
			 }
			 
			 // Display the room information
			 updateRoomList();
			 displayRoom(roomName);
			 
		 } catch(JSONException e) {
			e.printStackTrace();
		 }
		 
			 
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