import java.util.ArrayList;

public class Player {

	private int location;
	private String playerName;
	private ArrayList<Properties> ownedProperties;
	private int moneyAmount;
	private boolean inJail;

	public Player(String playerName, int locationOnBoard, ArrayList<Properties> ownedProperties, int moneyAmount, boolean inJail) {
		this.playerName = playerName;
		this.location = locationOnBoard;
		this.ownedProperties = ownedProperties;
		this.moneyAmount = moneyAmount;
		this.inJail = inJail;
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

	public boolean getInJail() {
		return inJail;
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

	public void changeMoney(int money) {
		this.moneyAmount += money;
	}

	public void setInJail(boolean inJail) {
		this.inJail = inJail;
	}
}
