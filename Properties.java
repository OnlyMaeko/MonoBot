package defaulta;

public class Properties {

	private String propName;
	private double baseRent;
	private double rentOne;
	private double rentTwo;
	private double rentThree;
	private double rentFour;
	private double rentHotel;
	private double mortgageValue;
	private double houseCost;
	private double hotelCost;
	private String setColor;
	private double price;
	private double boardPosition;
	private int numberOfHouses;
	private boolean isHotel;
	private String owner;
	private boolean isFullyOwned;
	private boolean isMortgaged;
	private double houseSellPrice;
	private double hotelSellPrice;

	public Properties(String propName, double baseRent, double rentOne, double rentTwo, double rentThree, double rentFour, double rentHotel, double mortgageValue, 
			double houseCost, double hotelCost, String setColor, double price, double boardPosition, int numberOfHouses, boolean isHotel, String owner,
			boolean isFullyOwned, boolean isMortgaged, double houseSellPrice, double hotelSellPrice) {
		
		this.propName = propName;
		this.baseRent = baseRent;
		this.rentOne = rentOne;
		this.rentTwo = rentTwo;
		this.rentThree = rentThree;
		this.rentFour = rentFour;
		this.rentHotel = rentHotel;
		this.mortgageValue = mortgageValue;
		this.houseCost = houseCost;
		this.hotelCost = hotelCost;
		this.setColor = setColor;
		this.price = price;
		this.boardPosition = boardPosition;
		this.numberOfHouses = numberOfHouses;
		this.isHotel = isHotel;
		this.owner = owner;
		this.isFullyOwned = isFullyOwned;
		this.isMortgaged = isMortgaged;
		this.houseSellPrice = houseSellPrice;
		this.hotelSellPrice = hotelSellPrice;
		
	}

}