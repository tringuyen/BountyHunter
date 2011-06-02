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

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class BountyGameState extends MapActivity {
	
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
	private static final String BATTLE_URL = "http://good-hunting.appspot.com/battle";

	//private MapController mapController;
	//private MapView mapView;
	private LocationManager locationManager;
	//private List<Overlay> mapOverlays;
	
	private Drawable drawable;
	private BountyItemizedOverlay itemizedoverlay;
	
	private Drawable drawable_target;
	private BountyItemizedOverlay itemizedoverlay_target;
	
	private String m_curPlayerName;
	private String m_curRoomName;
	private float m_curLat = (float) 34.07315;
	private float m_curLng = (float) -118.450905;
	
	private float  m_targetLat;
	private float  m_targetLng;
	private String  m_targetDistance;
	private String  m_targetName;

	private GeoPoint  m_targetPoint;
	private GeoPoint  m_curPoint;
	
	private Context m_context;
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.gamestate); // bind the layout to the activity

		// Toast message
    	Toast.makeText(this, "Game has started", Toast.LENGTH_SHORT).show();

    	m_context = this;
		// Extract the activity information
		Bundle extras = this.getIntent().getExtras();
		m_curPlayerName = extras.getString("playerName");
		m_curRoomName = extras.getString("roomName");
		
		int lat = (int) (34.07315 * 1E6);
		int lng = (int) (-118.450905 * 1E6);
		m_curPoint = new GeoPoint(lat, lng);
		
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
				MINIMUM_TIME_BETWEEN_UPDATES, new GeoUpdateHandler());
		
		
		
		// Update target location
		updateTargetInformation();
		
		
		//drawable_target = this.getResources().getDrawable(R.drawable.androidmarker_red);
		//itemizedoverlay_target = new BountyItemizedOverlay(drawable_target);
		
		// Update the Overlays();
		updateOverlays();
		
	}

	public void updateTargetInformation() {
		// Start the game by sending the request to the server
		String responseString = "";
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(BATTLE_URL);
		
		try {
			// Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
	        nameValuePairs.add(new BasicNameValuePair("playerName", m_curPlayerName));
	        nameValuePairs.add(new BasicNameValuePair("RoomName", m_curRoomName));
	        nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(m_curLat)));
	        nameValuePairs.add(new BasicNameValuePair("longitude", String.valueOf(m_curLng)));
	        nameValuePairs.add(new BasicNameValuePair("kill", "false"));
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
			 
			 // Update the target's information
			 m_targetLat = Float.valueOf(json_response.getString("targetLatitude"));
			 m_targetLng = Float.valueOf(json_response.getString("targetLongitude"));
			 m_targetDistance = json_response.getString("distance");
			 m_targetName = json_response.getString("targetName");
			 
			 // Update the GeoPoint of the target
			 m_targetPoint = new GeoPoint((int) (m_targetLat * 1E6), (int) (m_targetLng * 1E6));
			 
		 } catch(JSONException e) {
			e.printStackTrace();
		 }
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public void updateOverlays() {
		//private MapController mapController;
		//private MapView mapView;
		//private LocationManager locationManager;
		//private List<Overlay> mapOverlays;
		// Create map view
		MapView mapView = (MapView) findViewById(R.id.gamemapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setStreetView(true);
		MapController mapController = mapView.getController();
		mapController.setZoom(18); // Zoom 1 is world view
		
		// Instantiate our Overlay Items
		List<Overlay> mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		itemizedoverlay = new BountyItemizedOverlay(drawable, this);
		
		//drawable_target = this.getResources().getDrawable(R.drawable.androidmarker_red);
		
		// Add the overlay item for the player
		OverlayItem overlayitem = new OverlayItem(m_curPoint, "This is You", "You are currently located at " +
				"Latitude: " + String.valueOf(m_curLat) + " and Longitude: " + String.valueOf(m_curLng));
		
		// Add the overlay item for the target
		OverlayItem overlayitem2 = new OverlayItem(m_targetPoint, "Your Target: " + m_targetName, "He is located at " +
				"Latitude: " + String.valueOf(m_targetLat) + " and Longitude: " + String.valueOf(m_targetLng));
		
		itemizedoverlay.addOverlay(overlayitem);
		itemizedoverlay.addOverlay(overlayitem2);
		mapOverlays.add(itemizedoverlay);
		
		mapController.animateTo(m_curPoint); //	mapController.setCenter(point);
	}

	public void setPoint(GeoPoint point, float curLat, float curLng) {
		m_curPoint = point;
		m_curLat = curLat;
		m_curLng = curLng;
	}
	
	public void printMessage() {
    	Toast.makeText(this, "Location updated!", Toast.LENGTH_SHORT).show();
    	
	}
	
	public void sendKill(View button) {
		// Start the game by sending the request to the server
		String responseString = "";
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(BATTLE_URL);
		
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
	        nameValuePairs.add(new BasicNameValuePair("playerName", m_curPlayerName));
	        nameValuePairs.add(new BasicNameValuePair("RoomName", m_curRoomName));
	        nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(m_curLat)));
	        nameValuePairs.add(new BasicNameValuePair("longitude", String.valueOf(m_curLng)));
	        nameValuePairs.add(new BasicNameValuePair("kill", "true"));
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
			 if (!json_response.isNull("error")) {
				 Toast.makeText(this, json_response.getString("error"), Toast.LENGTH_LONG).show();
				 
			 } 
			 else {
				  AlertDialog.Builder dialog = new AlertDialog.Builder(m_context);
				  dialog.setTitle("Target Killed");
				  dialog.setMessage(json_response.getString("kill"));
				  dialog.show();
			 }
			
			 // Check the room state
			 try {  
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
			  
			  json_response = new JSONObject(responseString);
			  
			  if (!json_response.isNull("roomState")) {
				  AlertDialog.Builder dialog2 = new AlertDialog.Builder(m_context);
				  dialog2.setTitle("Game Ended");
				  dialog2.setMessage("There are no more targets left! You've won the game!");
				  dialog2.show();
					
			  } 
			  else {
				  Toast.makeText(this, "Gathering next target", Toast.LENGTH_LONG).show();
				 
			  }
			 
		 } catch(JSONException e) {
			e.printStackTrace();
		 }
		  
		  
	}
	
	public class GeoUpdateHandler implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
	    	printMessage();

			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			
			// Update our current location for future updates
			float curLat = (float) (location.getLatitude());
			float curLng = (float) (location.getLongitude());
			
			setPoint(point, curLat, curLng);
			
			// Update our target's information
			updateTargetInformation();
			
			// Update our overlays
			updateOverlays();
			
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
		
}
