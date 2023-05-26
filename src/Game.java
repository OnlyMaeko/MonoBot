import java.util.ArrayList;

public class Game {

	private ArrayList<Player> players;
	private Board board;
	private Deck deck;
	private boolean isGameOver;

	public void startGame() {
		board = new Board();
		deck = new Deck();
		players = new ArrayList<>();
		isGameOver = false;

		for (int i = 0; i < 2; i++) {
			players.add(new Player("Player " + (i + 1), 0, 
									new ArrayList<Properties>(), new ArrayList<Properties>(), 
									new ArrayList<Properties>(), 1500, false));
		}
	}

	public void playTurn(Player player, int speedingCount, int jailCount) {

		// Roll the dice
		int dice1 = (int) (Math.random() * 6) + 1;
		int dice2 = (int) (Math.random() * 6) + 1;
		int totalDiceRoll = dice1 + dice2;

		if (player.getInJail() == true && (dice1 == dice2)) {
			player.setInJail(false);
			jailCount = 0;
		}

		else if	(player.getInJail() == true && (jailCount < 2)) {
			// The only thing you can't do while in jail is move and land on properties so we've just gotta skip the location update
			jailCount++;
		}

		else if	(player.getInJail() == true && (jailCount == 2) && (dice1 != dice2)) {
			player.setInJail(false);
			// TODO: Check to make sure the boul isnt broke && make sure to check goojf and ask to see if they want to pay
			player.changeMoney(-50);
			jailCount = 0;
		}

			// TODO: Declaration for currentplayerlocation must be outside this if statement - we can rework all of jail into a method maybe? So game is smooth
		
		
		if (player.getInJail() == false) {

			// Move the player
			int currentPlayerLocation = player.getLocation();
			int newPlayerLocation = (currentPlayerLocation + totalDiceRoll) % 40;
			
			// If you've crossed Go it means it went under 0 so you gain $200
			if (newPlayerLocation < currentPlayerLocation) {
				player.changeMoney(200);
			}

			// Update the player's location
			player.setLocation(newPlayerLocation);

			// Perform actions based on the new location
			Properties currentProperty = board.getProperty(newPlayerLocation);

			if (dice1 == dice2) {
				speedingCount++;
				if (speedingCount <= 2) {
					playTurn(player, speedingCount,0);
				}
				else if (speedingCount == 2) {
					player.setLocation(10);
					player.setInJail(true);
				}
			}

			// TODO: Implement the logic for the player's actions based on the current property

			if (currentProperty.getBaseRent() != 0) {
				if (currentProperty.getOwner() == null) {
					buy(player, board);
					System.out.println(player.getPlayerName() + " bought " + currentProperty.getPropName());
				} else {
					if (currentProperty.getIsMortgaged() == false && currentProperty.getOwner() != player) {
						if (currentProperty.getSetColor().equals("Utility")) {
							int money = 0;
							if (currentProperty.getIsFullyOwned()) {
								money = (currentProperty.getRentOne() * totalDiceRoll);
							} else {
								money = (currentProperty.getBaseRent() * totalDiceRoll);
							}
							player.changeMoney(-money);
							currentProperty.getOwner().changeMoney(money);
							System.out.println(player.getPlayerName() + " paid " + currentProperty.getOwner().getPlayerName() + " $" + money);
						} else {
							rent(player, currentProperty.getOwner(), board);
						}
						if (player.getMoneyAmount() <= 0 && player.getOwnedProperties().isEmpty()) {
							players.remove(player);
						}
					}
				}
			} else {
				if (currentProperty.getBoardPosition() == 30) {
					player.setLocation(10);
					player.setInJail(true);
				} else if (currentProperty.getPropName().equals("Chance")) {
					Cards card = deck.getChanceDeck().get(0);
					deck.getChanceDeck().remove(0);
					deck.getChanceDeck().add(card);

					String cardName = card.getCardName();
					switch (cardName) {
						case "Boardwalk":
							player.setLocation(39);
							updateGameState(player, currentProperty, totalDiceRoll);
							break;
						case "Go":
							player.setLocation(0);
							updateGameState(player, currentProperty, totalDiceRoll);
							player.changeMoney(200);
							break;
						case "Illinois":
							player.setLocation(24);
							updateGameState(player, currentProperty, totalDiceRoll);
							if (newPlayerLocation < currentPlayerLocation) {
								player.changeMoney(200);
							}
							break;
						case "StCharles":
							player.setLocation(11);
							updateGameState(player, currentProperty, totalDiceRoll);
							if (newPlayerLocation < currentPlayerLocation) {
								player.changeMoney(200);
							}
							break;
						case "Railroad":
						player.setLocation((player.getLocation() % 10) + 5);
							updateGameState(player, currentProperty, totalDiceRoll);
							if (newPlayerLocation < currentPlayerLocation) {
								player.changeMoney(200);
							}
							break;
						case "Utility":
						// TODO: Change mod 10 to 12 and 28
							player.setLocation((player.getLocation() % 10) + 5);
							updateGameState(player, currentProperty, totalDiceRoll);
							if (newPlayerLocation < currentPlayerLocation) {
								player.changeMoney(200);
							}		
							break;
						case "Get50":
							player.changeMoney(50);
							break;
						case "GetOutOfJail":
							// TODO: Implement logic for acquiring a "Get Out of Jail Free" card
							break;
						case "GoBack3":
							player.setLocation(player.getLocation() - 3);
							break;
						case "GoToJail":
							player.setLocation(10);
							player.setInJail(true);
							break;
						case "PropertyRepairs":
							// TODO: Implement logic for making repairs on all owned properties

							for(int i = 0; i < player.getOwnedProperties().size(); i++) {
								
							}

							break;
						case "Pay15":
							player.changeMoney(-15);
							break;
						case "Reading":
							player.setLocation(5);
							player.changeMoney(200);
							break;
						case "PayEachPlayer50":
							player.changeMoney(players.size() * 50);
							for(int i = 0; i <= players.size(); i++){
							// TODO: players<i>	.changeMoney(50);
							}
							break;
						case "Get150":
							player.changeMoney(150);
							break;
						default:
							System.out.println("wtf");
							break;
					}
				} else if (currentProperty.getPropName().equals("Community Chest")) {

				}
			}
		// Print the dice roll and new location
		System.out.println(player.getPlayerName() + " rolled the dice: " + dice1 + " + " + dice2 + " = " + totalDiceRoll);
		System.out.println(player.getPlayerName() + " landed on " + currentProperty.getPropName());
		}

	}

	public static void main(String[] args) {
		Game game = new Game();
		game.startGame();
		
		// Play a turn for each player
		for (int i = 0; i < 50; i++) {
			for (Player player : game.players) {
				game.playTurn(player, 0, 0);
			}
		}

		for (Player player : game.players) {
			System.out.println(player.getMoneyAmount());
		}

	}

	/*
	 * HELPER METHODS
	 */

	public void rent(Player player, Player owner, Board board) {

		int currentPlayerLocation = player.getLocation();
		Properties currentProperty = board.getProperty(currentPlayerLocation);
		int money;

		if (currentProperty.getIsFullyOwned() == false) {

			money = currentProperty.getBaseRent();
			player.changeMoney(-money);
			owner.changeMoney(money);
			System.out.println(player.getPlayerName() + " paid " + currentProperty.getOwner().getPlayerName() + " $" + money);

		} else {

			int houses = currentProperty.getNumberOfHouses();
			boolean hotel = currentProperty.getIsHotel();

			if (houses == 0) {
				money = currentProperty.getBaseRent() * 2;
				player.changeMoney(-money);
				owner.changeMoney(money);
				System.out.println(player.getPlayerName() + " paid " + currentProperty.getOwner().getPlayerName() + " $" + money);
			}

			if (hotel == true) {
				money = currentProperty.getRentHotel();
				player.changeMoney(-money);
				owner.changeMoney(money);
				System.out.println(player.getPlayerName() + " paid " + currentProperty.getOwner().getPlayerName() + " $" + money);
			}

			if (houses == 1) {
				money = currentProperty.getRentOne();
				player.changeMoney(-money);
				owner.changeMoney(money);
				System.out.println(player.getPlayerName() + " paid " + currentProperty.getOwner().getPlayerName() + " $" + money);
			}

			if (houses == 2) {
				money = currentProperty.getRentTwo();
				player.changeMoney(-money);
				owner.changeMoney(money);
				System.out.println(player.getPlayerName() + " paid " + currentProperty.getOwner().getPlayerName() + " $" + money);
			}

			if (houses == 3) {
				money = currentProperty.getRentThree();
				player.changeMoney(-money);
				owner.changeMoney(money);
				System.out.println(player.getPlayerName() + " paid " + currentProperty.getOwner().getPlayerName() + " $" + money);
			}

			if (houses == 4) {
				money = currentProperty.getRentFour();
				player.changeMoney(-money);
				owner.changeMoney(money);
				System.out.println(player.getPlayerName() + " paid " + currentProperty.getOwner().getPlayerName() + " $" + money);
			}
		}
	}

	public void buy(Player player, Board board) {
		int currentPlayerLocation = player.getLocation();
		Properties currentProperty = board.getProperty(currentPlayerLocation);
		if (player.getMoneyAmount() >= currentProperty.getPrice()) {
			if (currentProperty.getSetColor().equals("Railroad")) {
				// add property to list of railroads, if you own more than 1, set isFullyOwned to true
				// to maintain proper rent functionality
				player.getOwnedRailroads().add(currentProperty);
				for (Properties railroad : player.getOwnedRailroads()) {
					if (player.getOwnedRailroads().size() >= 2) {
						railroad.setIsFullyOwned(true);
					}
					railroad.setNumberOfHouses(player.getOwnedRailroads().size() - 1);
				}
			} else if (currentProperty.getSetColor().equals("Utility")) {
				// add property to list of utilities, if you own more than 1, set isFullyOwned to true
				// to maintain proper rent functionality
				player.getOwnedUtilities().add(currentProperty);
				for (Properties utility : player.getOwnedUtilities()) {
					if (player.getOwnedUtilities().size() == 2) {
						utility.setIsFullyOwned(true);
					}
					utility.setNumberOfHouses(player.getOwnedUtilities().size() - 1);
				}
			} else {
				// add property to list of properties, if you own all of that set, set isFullyOwned for
				// all properties of that set color to true
				player.getOwnedProperties().add(currentProperty);
				if (hasMonopoly(player.getOwnedProperties(), currentProperty.getSetColor(), board)) {
					for (Properties property : player.getOwnedProperties()) {
						if (property.getSetColor().equals(currentProperty.getSetColor())) {
							property.setIsFullyOwned(true);
						}
					}
				}
			}
			currentProperty.setOwner(player);
			player.changeMoney(-currentProperty.getPrice());
		}
	}

	public void upgradeProperty(Properties property) {
		int numHouses = property.getNumberOfHouses();
		if (numHouses < 4) {
			property.setNumberOfHouses(numHouses++);
		} else {
			property.setIsHotel(true);
		}
	}

	public boolean hasMonopoly(ArrayList<Properties> propertyList, String setColor, Board board) {
		int count = 0;
		for (Properties property : propertyList) {
			if (property.getSetColor().equals(setColor)) {
				count++;
			}
		}
		return count == board.getPropertySetSize(setColor);
	}

	public void updateGameState(Player player, Properties currentProperty, int totalDiceRoll) {

		

		if (currentProperty.getBaseRent() != 0) {
			if (currentProperty.getOwner() == null) {
				buy(player, board);
					System.out.println(player.getPlayerName() + " bought " + currentProperty.getPropName());
			} else {
				if (currentProperty.getIsMortgaged() == false && currentProperty.getOwner() != player) {
					if (currentProperty.getSetColor().equals("Utility")) {
							int money = 0;
					if (currentProperty.getIsFullyOwned()) {
						money = (currentProperty.getRentOne() * totalDiceRoll);
					} 
					else {
						money = (currentProperty.getBaseRent() * totalDiceRoll);
					}
						player.changeMoney(-money);
						currentProperty.getOwner().changeMoney(money);
						System.out.println(player.getPlayerName() + " paid " + currentProperty.getOwner().getPlayerName() + " $" + money);
					} 
					else {
						rent(player, currentProperty.getOwner(), board);
					}
					if (player.getMoneyAmount() <= 0 && player.getOwnedProperties().isEmpty()) {
						players.remove(player);
					}
				}
			}

	}

}