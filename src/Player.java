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

	public void changeMoney(int money) {
		this.moneyAmount += money;
	}


	public void rent(Player player) {

		int currentPlayerLocation = player.getLocation();
		Properties currentProperty = board.getProperty(currentPlayerLocation);

	if (currentProperty.getBaseRent() != 0) {

		if (currentProperty.getOwner() == null) {
			// Buy - down below but maybe put the buy call here?

		}
		else if(currentProperty.getIsFullyOwned() == false){
			{
				money = currentProperty.getBaseRent();
				player.changeMoney(- money);
				currentProperty.getOwner().changeMoney(money);
			}

		}
			else if (currentProperty.getIsFullyOwned() == true){

				houses = currentProperty.getNumberOfHouses();
				hotel = currentProperty.getIsHotel();

				if(houses == 0){
				money = currentProperty.getBaseRent() * 2;
				player.getMoneyAmount().changeMoney(- money);
				currentProperty.getOwner().changeMoney(money);
				}

				if(hotel == true){
				money = currentProperty.getRentHotel();
				player.getMoneyAmount().changeMoney(- money);
				currentProperty.getOwner().changeMoney(money);
				}

				if(houses == 1){
				money = currentProperty.getRentOne();
				player.getMoneyAmount().changeMoney(- money);
				currentProperty.getOwner().changeMoney(money);
				}

				if(houses == 2){
				money = currentProperty.getRentTwo();
				player.getMoneyAmount().changeMoney(- money);
				currentProperty.getOwner().changeMoney(money);
				}

				if(houses == 3){
				money = currentProperty.getRentThree();
				player.getMoneyAmount().changeMoney(- money);
				currentProperty.getOwner().changeMoney(money);
				}

				if(houses == 4){
				money = currentProperty.getRentFour();
				player.getMoneyAmount().changeMoney(- money);
				currentProperty.getOwner().changeMoney(money);
				}



			}



	}


}

public void buy(Player player) {

	int currentPlayerLocation = player.getLocation();
	Properties currentProperty = board.getProperty(currentPlayerLocation);

		if (player.getMoneyAmount() >= currentProperty.getPrice()) {

			player.getOwnedProperties().add(currentProperty);
			player.getMoneyAmount().changeMoney(- currentProperty.getPrice());

		}


}


}
