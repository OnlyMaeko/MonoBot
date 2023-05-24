import java.util.ArrayList;

public class Game {

	private Player[] players;
	private Board board;
	private Deck deck;

	public void startGame() {
		board = new Board();
		deck = new Deck();
		players = new Player[6];

		for (int i = 0; i < players.length; i++) {
			players[i] = new Player("Player " + (i + 1), 0, new ArrayList<Properties>(), 1500);
		}
	}

	public void playTurn(Player player, int speedingCount) {

		// Roll the dice
		int dice1 = (int) (Math.random() * 6) + 1;
		int dice2 = (int) (Math.random() * 6) + 1;
		int totalDiceRoll = dice1 + dice2;

		// Move the player
		int currentPlayerLocation = player.getLocation();
		int newPlayerLocation = (currentPlayerLocation + totalDiceRoll) % 40;

		// Update the player's location
		player.setLocation(newPlayerLocation);

		// Perform actions based on the new location
		Properties currentProperty = board.getProperty(newPlayerLocation);

		// TODO: Implement the logic for the player's actions based on the current property
		if (currentProperty.getBaseRent() != 0) {
			if (currentProperty.getOwner() == null) {
				
			} else {
				currentProperty.getOwner().changeMoney(currentProperty.get);
			}
		}

		// Print the dice roll and new location
		System.out.println(player.getPlayerName() + " rolled the dice: " + dice1 + " + " + dice2 + " = " + totalDiceRoll);
		System.out.println(player.getPlayerName() + " landed on " + currentProperty.getPropName());

		if (dice1 == dice2) {
			speedingCount++;
			if (speedingCount == 2) {
				// ADD GO TO JAIL IMPLEMENTATION
			}
			playTurn(player, speedingCount);
		}

	}

	public static void main(String[] args) {
		Game game = new Game();
		game.startGame();
		
		// Play a turn for each player
		for (Player player : game.players) {
			game.playTurn(player, 0);
		}
	}

}