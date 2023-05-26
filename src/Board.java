import java.util.ArrayList;

public class Board {

	private ArrayList<Properties> board;

	public Board() {


		// private String propName;
		// private double baseRent;
		// private double rentOne;
		// private double rentTwo;
		// private double rentThree;
		// private double rentFour;
		// private double rentHotel;
		// private double mortgageValue;
		// private double houseCost;
		// private double hotelCost;
		// private String setColor;
		// private double price;
		// private double boardPosition;
		// private int numberOfHouses;
		// private boolean isHotel;
		// private String owner;
		// private boolean isFullyOwned;
		// private boolean isMortgaged;
		// private double houseSellPrice;
		// private double hotelSellPrice;


/*

For the railroad cards the rent 1,2,3, is the rent based on the number of railroads extra the owner Charles
The way to distinguish the railroads



*/

		board = new ArrayList<>();

		// GO
		board.add(new Properties("GO", 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 0, 0, false, null, false, false, 0, 0));

		// Mediterranean Avenue
		board.add(new Properties("Mediterranean Avenue", 2, 10, 30, 90, 160, 250, 30, 50, 50, "Brown", 60, 1, 0, false, null, false, false, 25, 25));

		// Community Chest
		board.add(new Properties("Community Chest", 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 2, 0, false, null, false, false, 0, 0));

		// Baltic Avenue
		board.add(new Properties("Baltic Avenue", 4, 20, 60, 180, 320, 450, 30, 50, 50, "Brown", 60, 3, 0, false, null, false, false, 25, 25));

		// Income Tax
		board.add(new Properties("Income Tax", 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 4, 0, false, null, false, false, 0, 0));

		// Reading Railroad
		board.add(new Properties("Reading Railroad", 25, 50, 100, 200, 0, 0, 100, 0, 0, "Railroad", 200, 5, 0, false, null, false, false, 0, 0));

		// Oriental Avenue
		board.add(new Properties("Oriental Avenue", 6, 30, 90, 270, 400, 550, 50, 50, 50, "Light Blue", 100, 6, 0, false, null, false, false, 25, 25));

		// Chance
		board.add(new Properties("Chance", 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 7, 0, false, null, false, false, 0, 0));

		// Vermont Avenue
		board.add(new Properties("Vermont Avenue", 6, 30, 90, 270, 400, 550, 50, 50, 50, "Light Blue", 100, 8, 0, false, null, false, false, 25, 25));

		// Connecticut Avenue
		board.add(new Properties("Connecticut Avenue", 8, 40, 100, 300, 450, 600, 60, 50, 50, "Light Blue", 120, 9, 0, false, null, false, false, 25, 25));

		// Jail - Just Visiting
		board.add(new Properties("Jail - Just Visiting", 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 10, 0, false, null, false, false, 0, 0));

		// St. Charles Place
		board.add(new Properties("St. Charles Place", 10, 50, 150, 450, 625, 750, 70, 100, 100, "Pink", 140, 11, 0, false, null, false, false, 50, 50));

		// Electric Company
		board.add(new Properties("Electric Company", 4, 10, 0, 0, 0, 0, 0, 0, 0, "Utility", 0, 12, 0, false, null, false, false, 0, 0));

		// States Avenue
		board.add(new Properties("States Avenue", 10, 50, 150, 450, 625, 750, 70, 100, 100, "Pink", 140, 13, 0, false, null, false, false, 50, 50));

		// Virginia Avenue
		board.add(new Properties("Virginia Avenue", 12, 60, 180, 500, 700, 900, 80, 100, 100, "Pink", 160, 14, 0, false, null, false, false, 50, 50));

		// Pennsylvania Railroad
		board.add(new Properties("Pennsylvania Railroad", 25, 50, 100, 200, 0, 0, 100, 0, 0, "Railroad", 200, 15, 0, false, null, false, false, 0, 0));

		// St. James Place
		board.add(new Properties("St. James Place", 14, 70, 200, 550, 750, 950, 90, 100, 100, "Orange", 180, 16, 0, false, null, false, false, 50, 50));

		// Community Chest
		board.add(new Properties("Community Chest", 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 17, 0, false, null, false, false, 0, 0));

		// Tennessee Avenue
		board.add(new Properties("Tennessee Avenue", 14, 70, 200, 550, 750, 950, 90, 100, 100, "Orange", 180, 18, 0, false, null, false, false, 50, 50));

		// New York Avenue
		board.add(new Properties("New York Avenue", 16, 80, 220, 600, 800, 1000, 100, 100, 100, "Orange", 200, 19, 0, false, null, false, false, 50, 50));

		// Free Parking
		board.add(new Properties("Free Parking", 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 20, 0, false, null, false, false, 0, 0));

		// Kentucky Avenue
		board.add(new Properties("Kentucky Avenue", 18, 90, 250, 700, 875, 1050, 110, 150, 150, "Red", 220, 21, 0, false, null, false, false, 75, 75));

		// Chance
		board.add(new Properties("Chance", 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 22, 0, false, null, false, false, 0, 0));

		// Indiana Avenue
		board.add(new Properties("Indiana Avenue", 18, 90, 250, 700, 875, 1050, 110, 150, 150, "Red", 220, 23, 0, false, null, false, false, 75, 75));

		// Illinois Avenue
		board.add(new Properties("Illinois Avenue", 20, 100, 300, 750, 925, 1100, 120, 150, 150, "Red", 240, 24, 0, false, null, false, false, 75, 75));

		// B & O Railroad
		board.add(new Properties("B & O Railroad", 25, 50, 100, 200, 0, 0, 100, 0, 0, "Railroad", 200, 25, 0, false, null, false, false, 0, 0));

		// Atlantic Avenue
		board.add(new Properties("Atlantic Avenue", 22, 110, 330, 800, 975, 1150, 130, 150, 150, "Yellow", 260, 26, 0, false, null, false, false, 75, 75));

		// Ventnor Avenue
		board.add(new Properties("Ventnor Avenue", 22, 110, 330, 800, 975, 1150, 130, 150, 150, "Yellow", 260, 27, 0, false, null, false, false, 75, 75));

		// Water Works
		board.add(new Properties("Water Works", 4, 10, 0, 0, 0, 0, 0, 0, 0, "Utility", 0, 28, 0, false, null, false, false, 0, 0));

		// Marvin Gardens
		board.add(new Properties("Marvin Gardens", 24, 120, 360, 850, 1025, 1200, 140, 150, 150, "Yellow", 280, 29, 0, false, null, false, false, 75, 75));

		// Go to Jail
		board.add(new Properties("Go to Jail", 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 30, 0, false, null, false, false, 0, 0));

		// Pacific Avenue
		board.add(new Properties("Pacific Avenue", 26, 130, 390, 900, 1100, 1275, 150, 200, 200, "Green", 300, 31, 0, false, null, false, false, 100, 100));

		// North Carolina Avenue
		board.add(new Properties("North Carolina Avenue", 26, 130, 390, 900, 1100, 1275, 150, 200, 200, "Green", 300, 32, 0, false, null, false, false, 100, 100));

		// Community Chest
		board.add(new Properties("Community Chest", 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 33, 0, false, null, false, false, 0, 0));

		// Pennsylvania Avenue
		board.add(new Properties("Pennsylvania Avenue", 28, 150, 450, 1000, 1200, 1400, 160, 200, 200, "Green", 320, 34, 0, false, null, false, false, 100, 100));

		// Short Line
		board.add(new Properties("Short Line", 0, 25, 50, 100, 200, 0, 100, 0, 0, "Railroad", 200, 35, 0, false, null, false, false, 0, 0));

		// Chance
		board.add(new Properties("Chance", 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 36, 0, false, null, false, false, 0, 0));

		// Park Place
		board.add(new Properties("Park Place", 35, 175, 500, 1100, 1300, 1500, 175, 200, 200, "Dark Blue", 350, 37, 0, false, null, false, false, 100, 100));

		// Luxury Tax
		board.add(new Properties("Luxury Tax", 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 38, 0, false, null, false, false, 0, 0));

		// Boardwalk
		board.add(new Properties("Boardwalk", 50, 200, 600, 1400, 1700, 2000, 200, 200, 200, "Dark Blue", 400, 39, 0, false, null, false, false, 100, 100));
	}

	public Properties getProperty(int index) {
		return board.get(index);
	}

	public int getPropertySetSize(String setColor) {
		if (setColor.equals("Dark Blue") || 
		setColor.equals("Brown")) {
			return 2;
		} else {
			return 3;
		}
	}

}
