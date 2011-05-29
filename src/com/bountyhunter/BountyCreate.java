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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class BountyCreate extends Activity { 
	
	private static final String BASE_URL = "http://good-hunting.appspot.com/gamemode";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.create);
	    
	}
	
	public void sendCreate(View button) {
		final EditText roomNameField = (EditText) findViewById(R.id.roomName);  
		String roomName = roomNameField.getText().toString();  
		
		final EditText playerNameField = (EditText) findViewById(R.id.playerName);  
		String playerName = playerNameField.getText().toString(); 
		
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
			 if(statusLine.getStatusCode() == HttpStatus.SC_OK){
			     ByteArrayOutputStream out = new ByteArrayOutputStream();
			     response.getEntity().writeTo(out);
			     out.close();
			     responseString = out.toString();
			      
			 } else{
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
		 } catch(JSONException e) {
			e.printStackTrace();
		 }
		 
		 
			 
	}
}
