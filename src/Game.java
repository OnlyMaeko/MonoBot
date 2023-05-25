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

		for (int i = 0; i < 6; i++) {
			players.add(new Player("Player " + (i + 1), 0, new ArrayList<Properties>(), 1500, false));
		}
	}

	public void playTurn(Player player, int speedingCount, int jailCount) {

		// Roll the dice
		int dice1 = (int) (Math.random() * 6) + 1;
		int dice2 = (int) (Math.random() * 6) + 1;
		int totalDiceRoll = dice1 + dice2;

		if(player.getInJail() == true && (dice1 == dice2)){
			player.setInJail(false);
		}

		else if(player.getInJail() == true && (jailCount < 2)){
			// The only thing you can't do while in jail is move and land on properties so we've just gotta skip the location update
			jailCount++;
		}

		else if(player.getInJail() == true && (jailCount == 2)){
			player.setInJail(false);
			player.changeMoney(-50);
		}



		// Move the player
		int currentPlayerLocation = player.getLocation();
		int newPlayerLocation = (currentPlayerLocation + totalDiceRoll) % 40;
		
		// If you've crossed Go it means it went under 0 so you gain $200
		if(newPlayerLocation < currentPlayerLocation){
			player.changeMoney(200);
		}

		// Update the player's location
		player.setLocation(newPlayerLocation);

		// Perform actions based on the new location
		Properties currentProperty = board.getProperty(newPlayerLocation);

		// TODO: Implement the logic for the player's actions based on the current property
		if (currentProperty.getBaseRent() != 0) {
			if (currentProperty.getOwner() == null) {
				buy(player, board);
				System.out.println(player.getPlayerName() + " bought " + currentProperty.getPropName());
			} else {
				if (currentProperty.getIsMortgaged() == false) {
					rent(player, currentProperty.getOwner(), board);
					if (player.getMoneyAmount() <= 0 && player.getOwnedProperties().isEmpty()) {
						players.remove(player);
					}
					System.out.println(player.getPlayerName() + " paid " + currentProperty.getOwner().getPlayerName() + " $" + currentProperty.getBaseRent());
				}
			}
		}

		// Print the dice roll and new location
		System.out.println(player.getPlayerName() + " rolled the dice: " + dice1 + " + " + dice2 + " = " + totalDiceRoll);
		System.out.println(player.getPlayerName() + " landed on " + currentProperty.getPropName());

		if (dice1 == dice2) {
			speedingCount++;

			if(speedingCount <= 2) {
				playTurn(player, speedingCount);
			}

			else if (speedingCount == 2) {
				// ADD GO TO JAIL IMPLEMENTATION

				player.setLocation(10);
				player.setInJail(true);
				jailCount++;
			
			}
			
		}

	}

	public static void main(String[] args) {
		Game game = new Game();
		game.startGame();
		
		// Play a turn for each player
		while (game.players.size() > 1) {
			for (Player player : game.players) {
				game.playTurn(player, 0);
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

		} else {

			int houses = currentProperty.getNumberOfHouses();
			boolean hotel = currentProperty.getIsHotel();

			if (houses == 0) {
				money = currentProperty.getBaseRent() * 2;
				player.changeMoney(-money);
				owner.changeMoney(money);
			}

			if (hotel == true) {
				money = currentProperty.getRentHotel();
				player.changeMoney(-money);
				owner.changeMoney(money);
			}

			if (houses == 1) {
				money = currentProperty.getRentOne();
				player.changeMoney(-money);
				owner.changeMoney(money);
			}

			if (houses == 2) {
				money = currentProperty.getRentTwo();
				player.changeMoney(-money);
				owner.changeMoney(money);
			}

			if (houses == 3) {
				money = currentProperty.getRentThree();
				player.changeMoney(-money);
				owner.changeMoney(money);
			}

			if (houses == 4) {
				money = currentProperty.getRentFour();
				player.changeMoney(-money);
				owner.changeMoney(money);
			}
		}
	}

	public void buy(Player player, Board board) {
		int currentPlayerLocation = player.getLocation();
		Properties currentProperty = board.getProperty(currentPlayerLocation);
		if (player.getMoneyAmount() >= currentProperty.getPrice()) {
			player.getOwnedProperties().add(currentProperty);
			currentProperty.setOwner(player);
			player.changeMoney(-currentProperty.getPrice());
		}
	}

}