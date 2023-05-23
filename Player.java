package defaulta;

import java.util.ArrayList;

public class Player {

	private double location;
	private String playerName;
	private ArrayList<Properties> ownedProperties;
	private double moneyAmount;
	
	public Player(String playerName, double locationOnBoard, ArrayList<Properties> ownedProperties, double moneyAmount) {
		this.playerName = playerName;
		this.location = locationOnBoard;
		this.ownedProperties = ownedProperties;
		this.moneyAmount = moneyAmount;
	}

	// Getters
	public double getLocation() {
		return location;
	}

	public String getPlayerName() {
		return playerName;
	}

	public ArrayList<Properties> getOwnedProperties() {
		return ownedProperties;
	}

	public double getMoneyAmount() {
		return moneyAmount;
	}

	// Setters
	public void setLocation(double newLocation) {
		this.location = newLocation;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public void setOwnedProperties(ArrayList<Properties> ownedProperties) {
		this.ownedProperties = ownedProperties;
	}

	public void setMoneyAmount(double moneyAmount) {
		this.moneyAmount = moneyAmount;
	}
}