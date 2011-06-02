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

import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BountyCreate extends ListActivity { 
	
	private static final String BASE_URL = "http://good-hunting.appspot.com/gamemode";
	private static final String START_URL = "http://good-hunting.appspot.com/battle";
	
	private String [] room_names;
	private String [] player_list;
	private JSONObject rooms;
	private JSONObject cur_room;
	private JSONArray playerList;
	private JSONArray roomlist;
	
	private String m_playerName;
	private String m_roomName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.create);
	    
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
	
	public void sendCreate(View button) {
		final EditText roomNameField = (EditText) findViewById(R.id.roomName);  
		m_roomName = roomNameField.getText().toString();  
		
		final EditText playerNameField = (EditText) findViewById(R.id.playerName);  
		m_playerName = playerNameField.getText().toString(); 
		
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
	        nameValuePairs.add(new BasicNameValuePair("playerName", m_playerName));
	        nameValuePairs.add(new BasicNameValuePair("RoomName", m_roomName));
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
				 
				 // Display the room information
				 updateRoomList();
				 displayRoom(m_roomName, true);
			 } 
			 else {
				 Toast.makeText(this, json_response.getString("joining"), Toast.LENGTH_SHORT).show();
				 
				 // Display the room information
				 updateRoomList();
				 displayRoom(m_roomName, false);
			 }
			 

			 
		 } catch(JSONException e) {
			e.printStackTrace();
		 }
		 
			 
	}
	
	public void displayRoom(String target_room, boolean isOwner) {
		
		try {
			// Reset the list
			
			// Search for the current room
			for (int i = 0; i < roomlist.length(); i++) {
				cur_room = roomlist.getJSONObject(i);
				// Find the room we want to display
				if (target_room.equals(cur_room.getString("roomName"))) {
					if (isOwner) {
						setContentView(R.layout.display_room_owner);
						
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
						
						// Wait until user press starts or updates room information
					}
					else {
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
						
						// Keep waiting until the game starts from room owner
					}

				}
			}
			
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void startGame(View button) {
		// Start the game by sending the request to the server
		String responseString = "";
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(START_URL);
		
		try {
			// Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
	        nameValuePairs.add(new BasicNameValuePair("playerName", m_playerName));
	        nameValuePairs.add(new BasicNameValuePair("RoomName", m_roomName));
	        nameValuePairs.add(new BasicNameValuePair("ready", "1"));
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
		/* try {
			 JSONObject json_response = new JSONObject(responseString);
			 
			 
		 } catch(JSONException e) {
			e.printStackTrace();
		 }*/
		  
		// Jump to the game state!
		 Intent i = new Intent(BountyCreate.this, BountyGameState.class);
		 Bundle b = new Bundle();
		 
		 b.putString("playerName", m_playerName);
		 b.putString("roomName", m_roomName);
		 i.putExtras(b);
		 
		 BountyCreate.this.startActivity(i);
			
	}
	
	
}
