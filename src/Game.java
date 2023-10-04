import java.util.ArrayList;

public class Game {

	private ArrayList<Player> players;
	private Board board;
	private Deck deck;

	public void startGame() {
		board = new Board();
		deck = new Deck();
		players = new ArrayList<>();

		for (int i = 0; i < 2; i++) {
			players.add(new Player("Player " + (i + 1), 0, 
									new ArrayList<Properties>(), new ArrayList<Properties>(), 
									new ArrayList<Properties>(),new ArrayList<Properties>(),
									 1500, 0, false, false, false));
		}
	}

	public void playTurn(Player player, int speedingCount) {

		System.out.println("/////////////////////////");

		// Roll the dice
		int dice1 = (int) (Math.random() * 6) + 1;
		int dice2 = (int) (Math.random() * 6) + 1;
		int totalDiceRoll = dice1 + dice2;
		
		// Checks if the player is in jail - moved into a method for readability
		jailChecker(player, dice1, dice2);

		if (dice1 == dice2 && speedingCount == 2) {
			player.setLocation(10);
			player.setInJail(true);
		}
		
		if (player.getInJail() == false) {

			// Move the player
			int currentPlayerLocation = player.getLocation();
			int newPlayerLocation = (currentPlayerLocation + totalDiceRoll) % 40;
			
			// If you've crossed Go it means it went under 0 so you gain $200
			// TODO: Edge case: if the player rolled double 1's and draws the 
			// go back 3 spaces card they will collect 200
			if (newPlayerLocation < currentPlayerLocation) {
				player.changeMoney(200);
			}

			// Update the player's location
			player.setLocation(newPlayerLocation);

			// Perform actions based on the new location
			Properties currentProperty = board.getProperty(newPlayerLocation);

			if (currentProperty.getBaseRent() != 0) {
				updateGameState(player, currentProperty, totalDiceRoll);
			} else {
				if (currentProperty.getBoardPosition() == 4) {
					player.changeMoney(-200);
				} else if (currentProperty.getBoardPosition() == 38) {
					player.changeMoney(-100);
				} else if (currentProperty.getBoardPosition() == 30) {
					player.setLocation(10);
					player.setInJail(true);
				} else if (currentProperty.getPropName().equals("Chance")) {
					Cards card = deck.getChanceDeck().get(0);
					deck.getChanceDeck().remove(0);
					String cardName = card.getCardName();
					if (!(cardName.equals("GetOutOfJail"))) {
						deck.getChanceDeck().add(card);
					}
					switch (cardName) {
						case "Boardwalk":
							player.setLocation(39);
							currentProperty = board.getProperty(player.getLocation());
							updateGameState(player, currentProperty, totalDiceRoll);
							break;
						case "Go":
							player.setLocation(0);
							player.changeMoney(200);
							break;
						case "Illinois":
							player.setLocation(24);
							currentProperty = board.getProperty(player.getLocation());
							updateGameState(player, currentProperty, totalDiceRoll);
							if (player.getLocation() < newPlayerLocation) {
								player.changeMoney(200);
							}
							break;
						case "StCharles":
							player.setLocation(11);
							currentProperty = board.getProperty(player.getLocation());
							updateGameState(player, currentProperty, totalDiceRoll);
							if (player.getLocation() < newPlayerLocation) {
								player.changeMoney(200);
							}
							break;
						case "Railroad":
							if (player.getLocation() == 7) {
								player.setLocation(15);
							} else if (player.getLocation() == 22) {
								player.setLocation(25);
							} else {
								player.setLocation(5);
								player.changeMoney(200);
							}
							currentProperty = board.getProperty(player.getLocation());
							updateGameState(player, currentProperty, totalDiceRoll);
							break;
						case "Utility":
							if (player.getLocation() == 7) {
								player.setLocation(12);
							} else if (player.getLocation() == 22) {
								player.setLocation(28);
							} else {
								player.setLocation(12);
								player.changeMoney(200);
							}
							currentProperty = board.getProperty(player.getLocation());
							updateGameState(player, currentProperty, totalDiceRoll);		
							break;
						case "Get50":
							player.changeMoney(50);
							break;
						case "GetOutOfJail":
							player.setGetOutOfJailFreeChance(true);
							break;
						case "GoBack3":
							player.setLocation(player.getLocation() - 3);
							break;
						case "GoToJail":
							player.setLocation(10);
							player.setInJail(true);
							break;
						case "PropertyRepairs":
							int totalRepairCost = 0;
							for (Properties property : player.getOwnedProperties()) {
								int numberOfHouses = property.getNumberOfHouses();
								if (property.getIsHotel()) {
									totalRepairCost += 100;
								} else {
									totalRepairCost += 25 * numberOfHouses;
								}
							}
							player.changeMoney(-totalRepairCost);
							break;
						case "Pay15":
							player.changeMoney(-15);
							break;
						case "Reading":
							player.setLocation(5);
							player.changeMoney(200);
							currentProperty = board.getProperty(player.getLocation());
							updateGameState(player, currentProperty, totalDiceRoll);
							break;
						case "PayEachPlayer50":
							int paymentAmount = (players.size() - 1) * 50;
							player.changeMoney(-paymentAmount);
							for (Player otherPlayer : players) {
								if (otherPlayer != player) {
									otherPlayer.changeMoney(50);
								}
								checkBroke(player, board);
							}
							break;
						case "Get150":
							player.changeMoney(150);
							break;
						default:
							System.out.println("wtf");
							break;
					}
					System.out.println(cardName);
				} else if (currentProperty.getPropName().equals("Community Chest")) {
					Cards card = deck.getCommunityChestDeck().get(0);
					deck.getCommunityChestDeck().remove(0);
					deck.getCommunityChestDeck().add(card);
					String cardName = card.getCardName();
					switch (cardName) {
						case "Get200":
							player.changeMoney(200);
							break;
						case "Go":
							player.setLocation(0);
							player.changeMoney(200);
							break;
						case "Pay50":
							player.changeMoney(-50);
							break;
						case "Get50":
							player.changeMoney(50);
							break;
						case "GetOutOfJail":
							player.setGetOutOfJailFreeChest(true);
							break;
						case "GoToJail":
							player.setLocation(10);
							player.setInJail(true);
							break;		
						case "Get100":
							player.changeMoney(100);
							break;
						case "Get20":
							player.changeMoney(20);
							break;
						case "Get10FromEachPlayer":
							int paymentAmount = (players.size() - 1) * 10;
							player.changeMoney(paymentAmount);
							for (Player otherPlayer : players) {
								if (otherPlayer != player) {
									otherPlayer.changeMoney(-10);
									checkBroke(otherPlayer, board);
								}
							}
							break;
						case "Pay100":
							player.changeMoney(-100);
							break;
						case "Get25":
							player.changeMoney(25);
							break;
						case "StreetRepairs":
							int totalRepairCost = 0;
							for (Properties property : player.getOwnedProperties()) {
								int numberOfHouses = property.getNumberOfHouses();
								if (property.getIsHotel()) {
									totalRepairCost += 115;
								} else {
									totalRepairCost += 40 * numberOfHouses;
								}
							}
							player.changeMoney(-totalRepairCost);
							break;
						case "Get10":
							player.changeMoney(-10);
							break;
						default:
							System.out.println("wtf");
							break;
					}
					System.out.println(cardName);
				}
			}

			checkBroke(player, board);

			// Print the dice roll and new location
			System.out.println(player.getPlayerName() + " rolled the dice: " + dice1 + " + " + dice2 + " = " + totalDiceRoll);
			System.out.println(player.getPlayerName() + " landed on " + currentProperty.getPropName());

			if (dice1 == dice2 && player.getInJail() == false) {
				speedingCount++;
				playTurn(player, speedingCount);
			}
		}
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.startGame();
		
		// Play a turn for each player
		int i = 0;
		int turns = 0;
		while (game.players.size() > 1 && turns <= 1000) {
			Player currPlayer = game.players.get(i);
			game.playTurn(currPlayer, 0);
			System.out.println(currPlayer.getMoneyAmount());
			if (i >= game.players.size() - 1) {
				i = 0;
			} else {
				i++;
			}
			turns++;
		}

		for (Player player : game.players) {
			System.out.println(player.getPlayerName());
			System.out.println("ended the game with: ");
			System.out.println(player.getMoneyAmount());
			System.out.println("This player owned: ");
			for (int j = 0; j < player.getOwnedProperties().size(); j++) {
				System.out.print(player.getOwnedProperties().get(j).getPropName());
				System.out.print(", ");
				System.out.print(player.getOwnedProperties().get(j).getNumberOfHouses());
				System.out.print(", ");
			}
			System.out.println("");
		}
	}

	/*
	 * HELPER METHODS
	 */

	public void checkState(ArrayList<Player> players) {
		//TODO: Input other game state checks (houses arent above 4, rent for a hotel is what it is supposed to be, etc)

		/*
		TLDR: This method checks the things we know to be true in the state of the game to see when and where things are going wrong, it is run after every turn
		As of now there are two parts to it, the first makes sure players don't randomly have negative money, the second is to make sure there aren't duplicate properties
		If either of these two things are true a boolean will be set to false in an arraylist called checks and then there will be an error output at the turn in question
		I have created a mirror arraylist to work with to make sure that we don't edit the in game ones
		*/ 

		// Using Boolean vs boolean takes up more memory - this is hard because we kind of need these to be objects, idk if we can find a workaround
		ArrayList<Boolean> checks = new ArrayList<Boolean>();
		ArrayList<Properties> checkProp = new ArrayList<Properties>();
		for(Player player : players){	
			// Checks state requirement - that the player has positive money
			if(player.getMoneyAmount() < 0){
				checks.add(false);
				}
				// Find a way to check the arraylist to see if it contains a duplicate because that doesn't use a for loop
				checkProp.clear();
				for (Properties Property : player.getOwnedMonopolies()) {
					// Check to see if the property set contains duplicates 
					if (checkProp.contains(Property)==true){
						checks.add(false);
					}
					else{
						checkProp.add(Property);
					}
				}
		}
			// This is the arraylist checks and if anything is false
			if(checks.contains(false) == true){
				System.out.println("************THERE WAS AN ERROR ON THIS TURN************");
			}
	}

	public void rent(Player player, Player owner, Board board) {

		int currentPlayerLocation = player.getLocation();
		Properties currentProperty = board.getProperty(currentPlayerLocation);
		int money = 0;

		if (currentProperty.getIsFullyOwned() == false) {

			money = currentProperty.getBaseRent();
			player.changeMoney(-money);
			int afterRent = checkBroke(player, board);
			if (afterRent < 0) {
				owner.changeMoney(money + afterRent);
			} else {
				owner.changeMoney(money);
			}
			System.out.println(player.getPlayerName() + " paid " + currentProperty.getOwner().getPlayerName() + " $" + money);

		} else {

			int houses = currentProperty.getNumberOfHouses();
			boolean hotel = currentProperty.getIsHotel();

			if (houses == 0) {
				money = currentProperty.getBaseRent() * 2;
			}

			if (hotel == true) {
				money = currentProperty.getRentHotel();
			}

			if (houses == 1) {
				money = currentProperty.getRentOne();
			}

			if (houses == 2) {
				money = currentProperty.getRentTwo();
			}

			if (houses == 3) {
				money = currentProperty.getRentThree();
			}

			if (houses == 4) {
				money = currentProperty.getRentFour();
			}

			player.changeMoney(-money);
			int afterRent = checkBroke(player, board);
			if (afterRent < 0) {
				owner.changeMoney(money + afterRent);
			} else {
				owner.changeMoney(money);
			}
			System.out.println(player.getPlayerName() + " paid " + currentProperty.getOwner().getPlayerName() + " $" + money);

		}
	}

	public void buy(Player player, Board board) {
		int currentPlayerLocation = player.getLocation();
		Properties currentProperty = board.getProperty(currentPlayerLocation);
		if (currentProperty.getPrice() != 0) {
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
								player.getOwnedMonopolies().add(currentProperty);
							}
						}
					}
				}
				currentProperty.setOwner(player);
				player.changeMoney(-currentProperty.getPrice());
			}
			else {
				auction(currentProperty);
			}
		}
	}

	public int checkBroke(Player player, Board board) {
		if (player.getMoneyAmount() < 0) {	
			if (checkBrokeHelper(player) != 0) {
				mortgage(player);
				checkBroke(player, board);
			}	
			else if(player.getOwnedProperties().size() > 0 || player.getOwnedRailroads().size() > 0 || player.getOwnedUtilities().size() > 0) {
				sell(player, board);
				checkBroke(player, board);
			}
			else {
				int temp = player.getMoneyAmount();
				// TODO: CHECK STATE IS HERE!!!
				checkState(players);
				players.remove(player);
				System.out.println(player.getPlayerName() + " is broke... :C");
				return temp;
			}
		}
		return player.getMoneyAmount();
	}

	public int checkBrokeHelper(Player player) {
		int unmortgagedProperties = 0;
		for (int i = 0; i < player.getOwnedProperties().size(); i++) {
			if (player.getOwnedProperties().get(i).getIsMortgaged() != true) {
				unmortgagedProperties++;
			}
		} 
		return unmortgagedProperties;
	}

	public void auction(Properties property) {
		
		ArrayList<Player> auctionList = new ArrayList<Player>(players);

		// for now, auctions will be won by the player with the most money

		int highestBid = 0;
		int secondHighestBid = 0;

		for (int i = 0; i < players.size(); i++) {
			Player player = auctionList.get(i);
			if (player.getMoneyAmount() / 5 > highestBid) {
				secondHighestBid = highestBid;
				highestBid = player.getMoneyAmount() / 5;
			} else if (player.getMoneyAmount() / 5 > secondHighestBid && player.getMoneyAmount() / 5 != highestBid) {
				secondHighestBid = player.getMoneyAmount() / 5;
			}
		}

		property.setPrice(secondHighestBid + 1);
		for (int i = 0; i < players.size(); i++){
			if (players.get(i).getPlayerName().equals(auctionList.get(0).getPlayerName()) && players.get(i).getMoneyAmount() >= secondHighestBid+1) {
					buy(players.get(i), board);
			}
		}
		
	}

	public void sell(Player player, Board board){
		if (player.getOwnedUtilities().size() != 0) {
			Properties property = player.getOwnedUtilities().get(0);
			player.getOwnedUtilities().remove(0);
			System.out.println(property.getPropName() + " WAS SOLD!!!!!!!!");
			auction(property);
		}
		else if (player.getOwnedRailroads().size() != 0) {
			Properties property = player.getOwnedRailroads().get(0);
			player.getOwnedRailroads().remove(0);
			System.out.println(property.getPropName() + " WAS SOLD!!!!!!!!");
			auction(property);
		}
		else if (player.getOwnedProperties().size() != 0) {
			Properties property = player.getOwnedProperties().get(0);
			if (property.getIsFullyOwned() == true) {
				// TODO: Rethink if we need this functionality
				for (Properties setProperty : player.getOwnedMonopolies()) {
					if (property.getSetColor().equals(setProperty.getSetColor())) {
						for(int i = setProperty.getNumberOfHouses(); i > 0; i--) {
							downgradeProperty(setProperty);
							setProperty.setIsFullyOwned(false);

						}
					}
				}
			}
			player.getOwnedProperties().remove(0);
			System.out.println(property.getPropName() + " WAS SOLD!!!!!!!!");
			auction(property);
		}
	}


	public void downgradeProperty(Properties property) {
		if (property.getIsHotel() == true) {
			property.setIsHotel(false);
			property.getOwner().changeMoney(property.getHouseSellPrice());
		}
		else if (property.getNumberOfHouses() > 0) {
			property.setNumberOfHouses(property.getNumberOfHouses() - 1);
			property.getOwner().changeMoney(property.getHouseSellPrice());
		}
		System.out.println(property.getPropName() + " was downgraded!!!");
	}


	public void mortgage(Player player) {
		// TODO: Mortgaging functionality to utilites and railroads
		int i = 0;
		for (Properties property : player.getOwnedProperties()) {
			if (property.getIsMortgaged() == true) {
				i++;
			}
			else {
				property = player.getOwnedProperties().get(i);
				if (property.getIsFullyOwned() == true) {
					for (Properties setProperty : player.getOwnedMonopolies()) {
						if (property.getSetColor().equals(setProperty.getSetColor())) {
							for (int j = setProperty.getNumberOfHouses(); j > 0; j--) {
								downgradeProperty(setProperty);
								player.changeMoney(setProperty.getHouseSellPrice());
							}
						}
					}
				}
				mortgageHelper(property);
				break;
			}
		}
	}

	public void unmortgage(Player player) {
		int i = 0;
		for(Properties property : player.getOwnedProperties()){
			if(property.getIsMortgaged() == false) {
				i++;
			}
			else {
				property = player.getOwnedProperties().get(i);
				unmortgageHelper(property);
				break;
			}
		}
	}

	public void mortgageHelper(Properties property){
		property.setIsMortgaged(true);
		System.out.println(property.getPropName() + " WAS MORTGAGED!!!!!!!!");
		property.getOwner().changeMoney(property.getPrice()/2);
	}

	public void unmortgageHelper(Properties property){
		property.setIsMortgaged(false);
		System.out.println(property.getPropName() + " WAS UNMORTGAGED!!!!!!!!");
		property.getOwner().changeMoney(-((property.getPrice()/2) + property.getPrice()/10));
	}

	public void buyHouse(Player player, Board board) {
		for (Properties property : player.getOwnedMonopolies()) {
			if (player.getMoneyAmount() >= property.getHouseCost() + 100) {
				int minHouses = getMinHouses(player.getOwnedMonopolies(), property.getSetColor());
				if (property.getNumberOfHouses() <= 4 && property.getNumberOfHouses() <= minHouses && !property.getIsHotel() && property.getIsMortgaged() == false) {
					upgradeProperty(property);
					player.changeMoney(-property.getHouseCost());
					System.out.println(player.getPlayerName() + " bought a house (or hotel) on " + property.getPropName() + " and money amount is " + player.getMoneyAmount() + "!!!");
				}
			}
		}
	}
	
	public int getMinHouses(ArrayList<Properties> monopolies, String setColor) {
		int minHouses = Integer.MAX_VALUE;
		for (Properties monopolizedProperty : monopolies) {
			if (monopolizedProperty.getSetColor().equals(setColor)) {
				minHouses = Math.min(minHouses, monopolizedProperty.getNumberOfHouses());
			}
		}
		return minHouses;
	}

	public void upgradeProperty(Properties property) {
		int numHouses = property.getNumberOfHouses();
		if (numHouses < 4) {
			numHouses++;
			property.setNumberOfHouses(numHouses);
		} else {
			if(property.getIsHotel() == false){
				property.setIsHotel(true);
			}
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

	public void jailChecker(Player player, int dice1, int dice2) {
			if (player.getInJail() == true && (player.getGetOutOfJailFreeChance() == true || player.getGetOutOfJailFreeChest() == true)) {
			if (player.getGetOutOfJailFreeChance() == true) {
				player.setInJail(false);
				player.setGetOutOfJailFreeChance(false);
				deck.getChanceDeck().add(new Cards("GetOutOfJail"));
			}
			else {
				player.setInJail(false);
				player.setGetOutOfJailFreeChest(false);
				deck.getCommunityChestDeck().add(new Cards("GetOutOfJail"));
			}
		}
		if (player.getInJail() == true && (dice1 == dice2)) {
			player.setInJail(false);
			player.setJailCount(0);
		}

		else if	(player.getInJail() == true && (player.getJailCount() < 2)) {
			// The only thing you can't do while in jail is move and land on properties so we've just gotta skip the location update
			player.setJailCount(player.getJailCount() + 1);
			// TODO: Player can decide to pay to get out of jail but not move until next turn, decision making related thing.
		}

		else if	(player.getInJail() == true && (player.getJailCount() == 2) && (dice1 != dice2)) {
			player.setInJail(false);
			player.changeMoney(-50);
			player.setJailCount(0);
		}
	}

	public void updateGameState(Player player, Properties currentProperty, int totalDiceRoll) {

		if (currentProperty.getOwner() == null) {
			buy(player, board);
			if (currentProperty.getPrice() != 0) {
				System.out.println(player.getPlayerName() + " bought " + currentProperty.getPropName());
			}
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
			}
		}
		
		if (player.getMoneyAmount() > 220) {
			unmortgage(player);
		}

		if (!(player.getOwnedMonopolies().isEmpty())) {
			buyHouse(player, board);
		}
			// TODO: CHECK STATE IS HERE!!!
			checkState(players);
		
		
	}

}