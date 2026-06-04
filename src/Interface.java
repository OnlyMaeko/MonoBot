import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

public class Interface {

	private ArrayList<Player> players;
	private Board board;
	private Deck deck;
	private Scanner scanner;
	private GameWindow guiWindow;

	public Interface() {
		scanner = new Scanner(System.in);
	}

	public void startGame() {
		board = new Board();
		deck = new Deck();
		players = new ArrayList<>();

		System.out.println("========================================");
		System.out.println("   Welcome to Monopoly - 2 Player Game");
		System.out.println("========================================\n");

		for (int i = 0; i < 2; i++) {
			players.add(new Player("Player " + (i + 1), 0, 
									new ArrayList<Properties>(), new ArrayList<Properties>(), 
									new ArrayList<Properties>(),new ArrayList<Properties>(),
									 1500, 0, false, false, false));
		}

		System.out.println("Players initialized:");
		for (Player player : players) {
			System.out.println("- " + player.getPlayerName() + " (Starting balance: $" + player.getMoneyAmount() + ")");
		}
		System.out.println();
	}

	public void setGuiWindow(GameWindow window) {
		this.guiWindow = window;
	}

	public void playGame() {
		startGame();
		SwingUtilities.invokeLater(() -> {
			new GameWindow(this);
		});
	}

	private void showTurnMenu(Player player) {
		System.out.println("\n--- " + player.getPlayerName() + "'s Turn ---");
		System.out.println("1. Roll dice and play turn");
		System.out.println("2. View game state");
		System.out.println("3. View your properties");
		System.out.println("4. View board");
		
		System.out.print("\nSelect an option (1-4): ");
		
		int choice = 0;
		try {
			choice = scanner.nextInt();
		} catch (Exception e) {
			scanner.nextLine();
			choice = 1;
		}
		
		switch (choice) {
			case 1:
				System.out.println("\nRolling dice...");
				break;
			case 2:
				displayGameState();
				showTurnMenu(player);
				break;
			case 3:
				displayPlayerProperties(player);
				showTurnMenu(player);
				break;
			default:
				System.out.println("\nInvalid choice. Rolling dice...");
				break;
		}
	}

	private boolean showPostRollMenu(Player player) {
		System.out.println("\n--- " + player.getPlayerName() + "'s Options ---");
		System.out.println("1. Continue with turn");
		System.out.println("2. End turn now");
		
		System.out.print("\nSelect an option (1-2): ");
		
		int choice = 0;
		try {
			choice = scanner.nextInt();
		} catch (Exception e) {
			scanner.nextLine();
			choice = 1;
		}
		
		return choice == 2;
	}

	private void displayPlayerInfo(Player player) {
		System.out.println("\nCurrent Player: " + player.getPlayerName());
		System.out.println("Position: " + player.getLocation());
		System.out.println("Money: $" + player.getMoneyAmount());
		System.out.println("In Jail: " + (player.getInJail() ? "Yes" : "No"));
		System.out.println("Properties Owned: " + player.getOwnedProperties().size());
	}

	private void displayPlayerProperties(Player player) {
		System.out.println("\n--- " + player.getPlayerName() + "'s Properties ---");
		if (player.getOwnedProperties().isEmpty() && player.getOwnedRailroads().isEmpty() && player.getOwnedUtilities().isEmpty()) {
			System.out.println("No properties owned yet.");
		} else {
			if (!player.getOwnedProperties().isEmpty()) {
				System.out.println("Streets:");
				for (Properties prop : player.getOwnedProperties()) {
					System.out.println("  - " + prop.getPropName() + " ($" + prop.getPrice() + ")");
				}
			}
			if (!player.getOwnedRailroads().isEmpty()) {
				System.out.println("Railroads: " + player.getOwnedRailroads().size());
			}
			if (!player.getOwnedUtilities().isEmpty()) {
				System.out.println("Utilities: " + player.getOwnedUtilities().size());
			}
		}
		System.out.println();
	}

	private void displayGameState() {
		System.out.println("\n========== GAME STATE ==========");
		for (Player player : players) {
			System.out.println("\n" + player.getPlayerName() + ":");
			System.out.println("  Balance: $" + player.getMoneyAmount());
			System.out.println("  Position: " + player.getLocation());
			System.out.println("  Properties: " + player.getOwnedProperties().size());
		}
		System.out.println("\n================================\n");
	}

	public void playTurn(Player player, int speedingCount) {

		System.out.println("/////////////////////////");

		// Roll the dice
		int dice1 = (int) (Math.random() * 6) + 1;
		int dice2 = (int) (Math.random() * 6) + 1;
		int totalDiceRoll = dice1 + dice2;
		
		// Display dice roll and ask if player wants to continue
		System.out.println("\n" + player.getPlayerName() + " rolled: " + dice1 + " + " + dice2 + " = " + totalDiceRoll);
		if (showPostRollMenu(player)) {
			System.out.println(player.getPlayerName() + " ended their turn.\n");
			return;
		}
		
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
					System.out.println("\n*** CHANCE CARD DRAWN: " + cardName + " ***");
					switch (cardName) {
						case "Boardwalk":
							player.setLocation(39);
							currentProperty = board.getProperty(player.getLocation());
							System.out.println("Action: Advance to Boardwalk");
							updateGameState(player, currentProperty, totalDiceRoll);
							break;
						case "Go":
							player.setLocation(0);
							player.changeMoney(200);
							System.out.println("Action: Advance to Go and collect $200");
							break;
						case "Illinois":
							player.setLocation(24);
							currentProperty = board.getProperty(player.getLocation());
							System.out.println("Action: Advance to Illinois Avenue");
							updateGameState(player, currentProperty, totalDiceRoll);
							if (player.getLocation() < newPlayerLocation) {
								player.changeMoney(200);
							}
							break;
						case "StCharles":
							player.setLocation(11);
							currentProperty = board.getProperty(player.getLocation());
							System.out.println("Action: Advance to St. Charles Place");
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
							System.out.println("Action: Advance to the nearest Railroad");
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
							System.out.println("Action: Advance to the nearest Utility");
							updateGameState(player, currentProperty, totalDiceRoll);		
							break;
						case "Get50":
							player.changeMoney(50);
							System.out.println("Action: Collect $50");
							break;
						case "GetOutOfJail":
							player.setGetOutOfJailFreeChance(true);
							System.out.println("Action: Get Out of Jail Free Card (save for later)");
							break;
						case "GoBack3":
							player.setLocation(player.getLocation() - 3);
							System.out.println("Action: Go back 3 spaces");
							break;
						case "GoToJail":
							player.setLocation(10);
							player.setInJail(true);
							System.out.println("Action: Go directly to Jail");
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
							System.out.println("Action: Pay for property repairs - $25 per house, $100 per hotel. Total: $" + totalRepairCost);
							break;
						case "Pay15":
							player.changeMoney(-15);
							System.out.println("Action: Pay $15");
							break;
						case "Reading":
							player.setLocation(5);
							player.changeMoney(200);
							currentProperty = board.getProperty(player.getLocation());
							System.out.println("Action: Advance to Reading Railroad and collect $200");
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
							System.out.println("Action: Pay each player $50");
							break;
						case "Get150":
							player.changeMoney(150);
							System.out.println("Action: Collect $150");
							break;
						default:
							System.out.println("Action: Unknown card effect");
							break;
					}
				} else if (currentProperty.getPropName().equals("Community Chest")) {
					Cards card = deck.getCommunityChestDeck().get(0);
					deck.getCommunityChestDeck().remove(0);
					deck.getCommunityChestDeck().add(card);
					String cardName = card.getCardName();
					System.out.println("\n*** COMMUNITY CHEST CARD DRAWN: " + cardName + " ***");
					switch (cardName) {
						case "Get200":
							player.changeMoney(200);
							System.out.println("Action: Collect $200");
							break;
						case "Go":
							player.setLocation(0);
							player.changeMoney(200);
							System.out.println("Action: Advance to Go and collect $200");
							break;
						case "Pay50":
							player.changeMoney(-50);
							System.out.println("Action: Pay $50");
							break;
						case "Get50":
							player.changeMoney(50);
							System.out.println("Action: Collect $50");
							break;
						case "GetOutOfJail":
							player.setGetOutOfJailFreeChest(true);
							System.out.println("Action: Get Out of Jail Free Card (save for later)");
							break;
						case "GoToJail":
							player.setLocation(10);
							player.setInJail(true);
							System.out.println("Action: Go directly to Jail");
							break;		
						case "Get100":
							player.changeMoney(100);
							System.out.println("Action: Collect $100");
							break;
						case "Get20":
							player.changeMoney(20);
							System.out.println("Action: Collect $20");
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
							System.out.println("Action: Collect $10 from each player");
							break;
						case "Pay100":
							player.changeMoney(-100);
							System.out.println("Action: Pay $100");
							break;
						case "Get25":
							player.changeMoney(25);
							System.out.println("Action: Collect $25");
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
							System.out.println("Action: Pay for street repairs - $40 per house, $115 per hotel. Total: $" + totalRepairCost);
							break;
						case "Get10":
							player.changeMoney(-10);
							System.out.println("Action: Pay $10");
							break;
						default:
							System.out.println("Action: Unknown card effect");
							break;
					}
				}
			}

			checkBroke(player, board);

			System.out.println(player.getPlayerName() + " landed on " + currentProperty.getPropName());

			if (dice1 == dice2 && player.getInJail() == false) {
				System.out.println("\nDouble rolled! " + player.getPlayerName() + " gets another turn.\n");
				speedingCount++;
				pauseForInput("Press Enter for your next turn...");
				showTurnMenu(player);
				playTurn(player, speedingCount);
			}
		} else {
			System.out.println(player.getPlayerName() + " is in jail and cannot move this turn.");
		}
	}

	public static void main(String[] args) {
		Interface game = new Interface();
		game.startGame();
		game.playGame();
	}

	/*
	 * HELPER METHODS
	 */

	private void pauseForInput(String message) {
		System.out.print(message);
		try {
			scanner.nextLine();
		} catch (Exception e) {
			// Ignore
		}
	}

	private void displayGameResults() {
		System.out.println("\n\n========================================");
		System.out.println("           GAME OVER");
		System.out.println("========================================\n");
		
		for (Player player : players) {
			System.out.println(player.getPlayerName());
			System.out.println("Final Balance: $" + player.getMoneyAmount());
			System.out.println("Properties Owned:");
			for (Properties property : player.getOwnedProperties()) {
				System.out.println("  - " + property.getPropName() + 
					" (" + property.getNumberOfHouses() + " houses" + 
					(property.getIsHotel() ? ", HOTEL" : "") + ")");
			}
			System.out.println();
		}
		
		if (players.size() == 1) {
			System.out.println("Winner: " + players.get(0).getPlayerName() + "!!!");
		}
	}

	public void checkState(ArrayList<Player> players) {
		ArrayList<Boolean> checks = new ArrayList<Boolean>();
		ArrayList<Properties> checkProp = new ArrayList<Properties>();
		for(Player player : players){	
			if(player.getMoneyAmount() < 0){
				checks.add(false);
			}
			checkProp.clear();
			for (Properties Property : player.getOwnedMonopolies()) {
				if (checkProp.contains(Property)==true){
					checks.add(false);
				}
				else{
					checkProp.add(Property);
				}
			}
		}
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
			// If GUI is available, use dialog. Otherwise use Scanner
			boolean wantsToBuy = false;
			if (guiWindow != null) {
				wantsToBuy = guiWindow.showPropertyPurchaseDialog(player, currentProperty);
			} else {
				System.out.println("\n" + currentProperty.getPropName() + " is available for $" + currentProperty.getPrice());
				System.out.print("Would you like to buy this property? (y/n): ");
				String choice = scanner.next().toLowerCase();
				wantsToBuy = choice.equals("y");
			}

			if (wantsToBuy) {
				if (player.getMoneyAmount() >= currentProperty.getPrice()) {
					if (currentProperty.getSetColor().equals("Railroad")) {
						player.getOwnedRailroads().add(currentProperty);
						for (Properties railroad : player.getOwnedRailroads()) {
							if (player.getOwnedRailroads().size() >= 2) {
								railroad.setIsFullyOwned(true);
							}
							railroad.setNumberOfHouses(player.getOwnedRailroads().size() - 1);
						}
					} else if (currentProperty.getSetColor().equals("Utility")) {
						player.getOwnedUtilities().add(currentProperty);
						for (Properties utility : player.getOwnedUtilities()) {
							if (player.getOwnedUtilities().size() == 2) {
								utility.setIsFullyOwned(true);
							}
							utility.setNumberOfHouses(player.getOwnedUtilities().size() - 1);
						}
					} else {
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
					if (guiWindow != null) {
						guiWindow.log(player.getPlayerName() + " bought " + currentProperty.getPropName() + " for $" + currentProperty.getPrice());
					}
				}
				else {
					if (guiWindow != null) {
						guiWindow.log("You don't have enough money to buy this property.");
					}
					auction(currentProperty);
				}
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
			player.setJailCount(player.getJailCount() + 1);
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
		checkState(players);
	}

	private class GameWindow extends JFrame implements ActionListener {
		private Interface game;
		private GameBoardPanel boardPanel;
		private JTextArea logArea;
		private JButton rollDiceButton, viewStateButton, viewPropertiesButton;
		private JButton nextTurnButton;
		private JLabel currentPlayerLabel, balanceLabel, positionLabel;
		private int currentPlayerIndex = 0;
		private int turnCount = 0;
		private boolean waitingForInput = false;

		public GameWindow(Interface game) {
			this.game = game;
			game.setGuiWindow(this);
			setTitle("Monopoly Game");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setSize(1200, 900);
			setLocationRelativeTo(null);

			// Top panel with player info
			JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			topPanel.setBackground(new Color(200, 200, 200));
			currentPlayerLabel = new JLabel("Current Player: Player 1");
			currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 14));
			balanceLabel = new JLabel("Balance: $1500");
			balanceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			positionLabel = new JLabel("Position: Go");
			positionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			topPanel.add(currentPlayerLabel);
			topPanel.add(Box.createHorizontalStrut(30));
			topPanel.add(balanceLabel);
			topPanel.add(Box.createHorizontalStrut(30));
			topPanel.add(positionLabel);

			// Center panel with board
			boardPanel = new GameBoardPanel(players, board);

			// Bottom panel with buttons and log
			JPanel bottomPanel = new JPanel(new BorderLayout());

			// Log area
			logArea = new JTextArea(8, 50);
			logArea.setEditable(false);
			logArea.setFont(new Font("Courier", Font.PLAIN, 11));
			JScrollPane scrollPane = new JScrollPane(logArea);
			bottomPanel.add(scrollPane, BorderLayout.CENTER);

			// Button panel
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			rollDiceButton = new JButton("Roll Dice");
			rollDiceButton.addActionListener(this);
			rollDiceButton.setFont(new Font("Arial", Font.BOLD, 12));

			viewStateButton = new JButton("View Game State");
			viewStateButton.addActionListener(this);
			viewStateButton.setFont(new Font("Arial", Font.PLAIN, 12));

			viewPropertiesButton = new JButton("View My Properties");
			viewPropertiesButton.addActionListener(this);
			viewPropertiesButton.setFont(new Font("Arial", Font.PLAIN, 12));

			nextTurnButton = new JButton("End Turn");
			nextTurnButton.addActionListener(this);
			nextTurnButton.setFont(new Font("Arial", Font.PLAIN, 12));

			buttonPanel.add(rollDiceButton);
			buttonPanel.add(viewStateButton);
			buttonPanel.add(viewPropertiesButton);
			buttonPanel.add(nextTurnButton);
			bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

			// Main layout
			add(topPanel, BorderLayout.NORTH);
			add(boardPanel, BorderLayout.CENTER);
			add(bottomPanel, BorderLayout.SOUTH);

			setVisible(true);

			// Start game loop
			startGameLoop();
		}

		private void startGameLoop() {
			new Thread(() -> {
				int turns = 0;
				while (game.players.size() > 1 && turns <= 1000) {
					Player currPlayer = game.players.get(currentPlayerIndex);
					updatePlayerInfo(currPlayer);
					log("\n========================================");
					log("TURN #" + (turns + 1) + " - " + currPlayer.getPlayerName() + "'s Turn");
					log("========================================");

					waitForRoll();

					// Execute turn
					game.playTurn(currPlayer, 0);

					log("End of turn balance: $" + currPlayer.getMoneyAmount());

					// Move to next player
					if (currentPlayerIndex >= game.players.size() - 1) {
						currentPlayerIndex = 0;
					} else {
						currentPlayerIndex++;
					}
					turns++;

					// Refresh board
					SwingUtilities.invokeLater(() -> boardPanel.repaint());
				}

				// Game over
				displayGameResults();
			}).start();
		}

		private void waitForRoll() {
			waitingForInput = true;
			while (waitingForInput) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public boolean showPropertyPurchaseDialog(Player player, Properties property) {
			log("\n" + property.getPropName() + " is available for $" + property.getPrice());
			log("Would you like to buy this property?\n");
			
			int response = JOptionPane.showConfirmDialog(
				GameWindow.this,
				player.getPlayerName() + ", would you like to buy " + property.getPropName() + " for $" + property.getPrice() + "?",
				"Buy Property?",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE
			);
			
			boolean buying = (response == JOptionPane.YES_OPTION);
			if (buying) {
				log(player.getPlayerName() + " chose to buy " + property.getPropName());
			} else {
				log(player.getPlayerName() + " declined to buy " + property.getPropName());
			}
			
			return buying;
		}

		public void log(String message) {
			SwingUtilities.invokeLater(() -> {
				if (logArea != null) {
					logArea.append(message + "\n");
					logArea.setCaretPosition(logArea.getDocument().getLength());
				}
			});
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == rollDiceButton) {
				if (waitingForInput) {
					waitingForInput = false;
					rollDiceButton.setEnabled(false);
				}
			} else if (e.getSource() == viewStateButton) {
				displayGameState();
			} else if (e.getSource() == viewPropertiesButton) {
				Player currPlayer = game.players.get(currentPlayerIndex);
				displayPlayerProperties(currPlayer);
			} else if (e.getSource() == nextTurnButton) {
				if (!waitingForInput) {
					waitingForInput = true;
				}
			}
		}

		private void updatePlayerInfo(Player player) {
			SwingUtilities.invokeLater(() -> {
				String[] boardPositions = {"Go", "Mediterranean", "Community Chest", "Baltic", "Income Tax", "Reading", "Oriental", "Chance", "Vermont", "Connecticut",
					"Jail", "St. Charles", "Electric", "States", "Virginia", "Pennsylvania", "St. James", "Community Chest", "Tennessee", "New York",
					"Free Parking", "Kentucky", "Chance", "Indiana", "Illinois", "B&O", "Atlantic", "Ventnor", "Water Works", "Marvin",
					"Go to Jail", "Pacific", "North Carolina", "Community Chest", "Pennsylvania", "Short Line", "Chance", "Park Place", "Luxury Tax", "Boardwalk"};
				currentPlayerLabel.setText("Current Player: " + player.getPlayerName());
				balanceLabel.setText("Balance: $" + player.getMoneyAmount());
				positionLabel.setText("Position: " + boardPositions[player.getLocation()]);
				rollDiceButton.setEnabled(true);
			});
		}

		private void displayGameState() {
			StringBuilder state = new StringBuilder("\n=== GAME STATE ===\n");
			for (Player player : game.players) {
				state.append(player.getPlayerName()).append(":\n");
				state.append("  Balance: $").append(player.getMoneyAmount()).append("\n");
				state.append("  Properties: ").append(player.getOwnedProperties().size()).append("\n");
			}
			log(state.toString());
		}

		private void displayPlayerProperties(Player player) {
			StringBuilder props = new StringBuilder("\n=== " + player.getPlayerName() + "'s Properties ===\n");
			if (player.getOwnedProperties().isEmpty() && player.getOwnedRailroads().isEmpty() && player.getOwnedUtilities().isEmpty()) {
				props.append("No properties owned yet.\n");
			} else {
				if (!player.getOwnedProperties().isEmpty()) {
					props.append("Streets:\n");
					for (Properties prop : player.getOwnedProperties()) {
						props.append("  - ").append(prop.getPropName()).append("\n");
					}
				}
				if (!player.getOwnedRailroads().isEmpty()) {
					props.append("Railroads: ").append(player.getOwnedRailroads().size()).append("\n");
				}
				if (!player.getOwnedUtilities().isEmpty()) {
					props.append("Utilities: ").append(player.getOwnedUtilities().size()).append("\n");
				}
			}
			log(props.toString());
		}

		private void displayGameResults() {
			SwingUtilities.invokeLater(() -> {
				StringBuilder results = new StringBuilder("\n\n========== GAME OVER ==========\n");
				for (Player player : game.players) {
					results.append(player.getPlayerName()).append(":\n");
					results.append("Final Balance: $").append(player.getMoneyAmount()).append("\n");
				}
				if (game.players.size() == 1) {
					results.append("\nWinner: ").append(game.players.get(0).getPlayerName()).append("!!!\n");
				}
				log(results.toString());
				rollDiceButton.setEnabled(false);
				JOptionPane.showMessageDialog(GameWindow.this, "Game Over!\nWinner: " + (game.players.size() > 0 ? game.players.get(0).getPlayerName() : "None"), "Game Over", JOptionPane.INFORMATION_MESSAGE);
			});
		}
	}

	private class GameBoardPanel extends JPanel {
		private ArrayList<Player> allPlayers;
		private Board board;
		private String[] boardPositions = {
			"Go", "Mediterranean", "Community Chest", "Baltic", "Income Tax", "Reading", "Oriental", "Chance", "Vermont", "Connecticut",
			"Jail", "St. Charles", "Electric", "States", "Virginia", "Pennsylvania", "St. James", "Community Chest", "Tennessee", "New York",
			"Free Parking", "Kentucky", "Chance", "Indiana", "Illinois", "B&O", "Atlantic", "Ventnor", "Water Works", "Marvin",
			"Go to Jail", "Pacific", "North Carolina", "Community Chest", "Pennsylvania", "Short Line", "Chance", "Park Place", "Luxury Tax", "Boardwalk"
		};
		private Color[] propertyColors = {
			null, new Color(139, 69, 19), null, new Color(139, 69, 19), null, new Color(255, 200, 0), new Color(255, 200, 0), null, new Color(255, 200, 0), new Color(255, 0, 0),
			null, new Color(255, 0, 0), null, new Color(255, 0, 0), new Color(255, 0, 0), null, new Color(255, 165, 0), null, new Color(255, 165, 0), new Color(255, 165, 0),
			null, new Color(0, 150, 0), null, new Color(0, 150, 0), new Color(0, 150, 0), null, new Color(0, 0, 255), new Color(0, 0, 255), null, new Color(0, 0, 255),
			null, new Color(128, 0, 128), new Color(128, 0, 128), null, new Color(128, 0, 128), null, null, new Color(255, 0, 0), null, new Color(0, 0, 0)
		};

		public GameBoardPanel(ArrayList<Player> allPlayers, Board board) {
			this.allPlayers = allPlayers;
			setBackground(new Color(240, 240, 240));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int panelWidth = getWidth();
			int panelHeight = getHeight();
			int boardSize = Math.min(panelWidth - 40, panelHeight - 40);
			int boardX = (panelWidth - boardSize) / 2;
			int boardY = (panelHeight - boardSize) / 2;
			int squareSize = boardSize / 10;

			g2d.setColor(Color.WHITE);
			g2d.fillRect(boardX, boardY, boardSize, boardSize);
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(2));
			g2d.drawRect(boardX, boardY, boardSize, boardSize);

			drawBoardSquares(g2d, boardX, boardY, squareSize);
			drawPlayers(g2d, boardX, boardY, squareSize);
		}

		private void drawBoardSquares(Graphics2D g2d, int boardX, int boardY, int squareSize) {
			g2d.setFont(new Font("Arial", Font.PLAIN, 8));

			// Bottom row (0-10, left to right)
			for (int i = 0; i <= 10; i++) {
				int x = boardX + i * squareSize;
				drawSquare(g2d, x, boardY + 9 * squareSize, squareSize, squareSize, i);
			}

			// Left column (11-20, bottom to top)
			for (int i = 1; i <= 10; i++) {
				int y = boardY + (9 - i) * squareSize;
				drawSquare(g2d, boardX, y, squareSize, squareSize, 10 + i);
			}

			// Top row (21-30, right to left)
			for (int i = 10; i >= 0; i--) {
				int x = boardX + i * squareSize;
				drawSquare(g2d, x, boardY, squareSize, squareSize, 30 - i);
			}

			// Right column (31-39, top to bottom)
			for (int i = 1; i <= 9; i++) {
				int y = boardY + i * squareSize;
				drawSquare(g2d, boardX + 9 * squareSize, y, squareSize, squareSize, 30 + i);
			}
		}

		private void drawSquare(Graphics2D g2d, int x, int y, int width, int height, int position) {
			Color propColor = propertyColors[position];

			if (propColor != null) {
				g2d.setColor(propColor);
				g2d.fillRect(x + 1, y + 1, width - 2, height - 2);
			} else {
				g2d.setColor(Color.WHITE);
				g2d.fillRect(x + 1, y + 1, width - 2, height - 2);
			}

			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(1));
			g2d.drawRect(x, y, width, height);

			String name = boardPositions[position].substring(0, Math.min(6, boardPositions[position].length()));
			g2d.drawString(name, x + 3, y + height - 3);
		}

		private void drawPlayers(Graphics2D g2d, int boardX, int boardY, int squareSize) {
			Color[] playerColors = { Color.RED, Color.BLUE };
			int playerIndex = 0;

			for (Player player : allPlayers) {
				int position = player.getLocation();
				int x = boardX;
				int y = boardY;

				// Calculate position on board
				if (position <= 10) {
					x += position * squareSize;
					y += 9 * squareSize;
				} else if (position <= 20) {
					x += 0;
					y += (20 - position) * squareSize;
				} else if (position <= 30) {
					x += (30 - position) * squareSize;
					y += 0;
				} else {
					x += 9 * squareSize;
					y += (position - 30) * squareSize;
				}

				// Draw player circle
				g2d.setColor(playerColors[playerIndex % 2]);
				int offset = (playerIndex % 2) * 8;
				g2d.fillOval(x + 5 + offset, y + 5 + offset, 14, 14);
				g2d.setColor(Color.BLACK);
				g2d.drawOval(x + 5 + offset, y + 5 + offset, 14, 14);

				playerIndex++;
			}
		}
	}

}
