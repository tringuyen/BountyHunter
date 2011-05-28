package com.bountyhunter;

public class Room {
	
	public static final int MAX_PLAYERS = 10;
	
	private static String m_roomName;
	private static String m_ownerName;
	private static String m_status;
	private static int m_numOfPlayers;
	private String m_players[] = new String[MAX_PLAYERS];
	
	public Room (String roomName, String ownerName, String status, int numOfPlayers, String[] players) {
		m_roomName = roomName;
		m_ownerName = ownerName;
		m_status = status;
		m_numOfPlayers = numOfPlayers;
		
		for (int i = 0; i < players.length; i++) {
			m_players[i] = players[i];
		}
		
	}
	
	public String getRoomName() {
		return m_roomName;
	}
	
	public String getOwnerName() {
		return m_ownerName;
	}
	
	public String getStatus() {
		return m_status;
	}
	
	public int getNumOfPlayers() {
		return m_numOfPlayers;
	}
	
	public String[] getPlayerList() {
		return m_players;
	}
}
