package com.bountyhunter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class BountyRoomInfo extends Activity {
	private String m_roomName;
	private String m_owner;
	private String m_status;
	private int m_numOfPlayers;
	private String [] m_currentPlayers;
	
	public BountyRoomInfo(Room room) {
		m_roomName = room.getRoomName();
		m_owner = room.getOwnerName();
		m_status = room.getStatus();
		m_numOfPlayers = room.getNumOfPlayers();
		m_currentPlayers = room.getPlayerList();
		

		
		TextView roomName = (TextView) findViewById(R.id.RoomNameData);
		TextView owner = (TextView) findViewById(R.id.OwnerData);
		TextView status = (TextView) findViewById(R.id.StatusData);
		TextView numOfPlayers = (TextView) findViewById(R.id.NumPlayersData);
		
		roomName.setText(m_roomName);
		owner.setText(m_owner);
		status.setText(m_status);
		numOfPlayers.setText(String.valueOf(m_numOfPlayers));
		
		
	}
	
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_room);
	}
}
