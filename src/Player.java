import java.util.ArrayList;

public class Player {

	private int location;
	private String playerName;
	private ArrayList<Properties> ownedProperties;
	private int moneyAmount;
	
	public Player(String playerName, int locationOnBoard, ArrayList<Properties> ownedProperties, int moneyAmount) {
		this.playerName = playerName;
		this.location = locationOnBoard;
		this.ownedProperties = ownedProperties;
		this.moneyAmount = moneyAmount;
	}

	// Getters
	public int getLocation() {
		return location;
	}

	public String getPlayerName() {
		return playerName;
	}

	public ArrayList<Properties> getOwnedProperties() {
		return ownedProperties;
	}

	public int getMoneyAmount() {
		return moneyAmount;
	}

	// Setters
	public void setLocation(int newLocation) {
		this.location = newLocation;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public void setOwnedProperties(ArrayList<Properties> ownedProperties) {
		this.ownedProperties = ownedProperties;
	}

	public void setMoneyAmount(int moneyAmount) {
		this.moneyAmount = moneyAmount;
	}
}