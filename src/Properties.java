public class Properties {

	private String propName;
	private int baseRent;
	private int rentOne;
	private int rentTwo;
	private int rentThree;
	private int rentFour;
	private int rentHotel;
	private int mortgageValue;
	private int houseCost;
	private int hotelCost;
	private String setColor;
	private int price;
	private int boardPosition;
	private int numberOfHouses;
	private boolean isHotel;
	private Player owner;
	private boolean isFullyOwned;
	private boolean isMortgaged;
	private int houseSellPrice;
	private int hotelSellPrice;

	public Properties(String propName, int baseRent, int rentOne, int rentTwo, int rentThree, int rentFour, int rentHotel, int mortgageValue,
			int houseCost, int hotelCost, String setColor, int price, int boardPosition, int numberOfHouses, boolean isHotel, Player owner,
			boolean isFullyOwned, boolean isMortgaged, int houseSellPrice, int hotelSellPrice) {

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

	// Getters
	public String getPropName() {
		return propName;
	}

	public int getBaseRent() {
		return baseRent;
	}

	public int getRentOne() {
		return rentOne;
	}

	public int getRentTwo() {
		return rentTwo;
	}

	public int getRentThree() {
		return rentThree;
	}

	public int getRentFour() {
		return rentFour;
	}

	public int getRentHotel() {
		return rentHotel;
	}

	public int getMortgageValue() {
		return mortgageValue;
	}

	public int getHouseCost() {
		return houseCost;
	}

	public int getHotelCost() {
		return hotelCost;
	}

	public String getSetColor() {
		return setColor;
	}

	public int getPrice() {
		return price;
	}

	public int getBoardPosition() {
		return boardPosition;
	}

	public int getNumberOfHouses() {
		return numberOfHouses;
	}

	public boolean getIsHotel() {
		return isHotel;
	}

	public boolean getIsFullyOwned() {
		return isFullyOwned;
	}

	public Player getOwner() {
		return owner;
	}

	public boolean getIsMortgaged() {
		return isMortgaged;
	}

	public int getHouseSellPrice() {
		return houseSellPrice;
	}

	public int getHotelSellPrice() {
		return hotelSellPrice;
	}

	// Setters

	public void setPropName(String propertyName) {
		this.propName = propertyName;
	}

	public void setBaseRent(int OriginalRent) {
		this.baseRent = OriginalRent;
	}

	public void setRentOne(int rent1) {
		this.rentOne = rent1;
	}

	public void setRentTwo(int rent2) {
		this.rentTwo = rent2;
	}

	public void setRentThree(int rent3) {
		this.rentThree = rent3;
	}

	public void setRentFour(int rent4) {
		this.rentFour = rent4;
	}

	public void setRentHotel(int rentH) {
		this.rentHotel = rentH;
	}

	public void setMortgageValue(int mortgageV) {
		this.mortgageValue = mortgageV;
	}

	public void setHouseCost(int housePrice) {
		this.houseCost = housePrice;
	}

	public void setHotelCost(int hotelPrice) {
		this.hotelCost = hotelPrice;
	}

	public void setSetColor(String colorOfSet) {
		this.setColor = colorOfSet;
	}

	public void setPrice(int cost) {
		this.price = cost;
	}

	public void setBoardPosition(int boardPos) {
		this.boardPosition = boardPos;
	}

	public void setNumberOfHouses(int numberHouses) {
		this.numberOfHouses = numberHouses;
	}

	public void setIsHotel(boolean ifHotel) {
		this.isHotel = ifHotel;
	}

	public void setOwner(Player player) {
		this.owner = player;
	}

	public void setIsFullyOwned(boolean ifFullyOwned) {
		this.isFullyOwned = ifFullyOwned;
	}

	public void setIsMortgaged(boolean ifMortgaged) {
		this.isMortgaged = ifMortgaged;
	}

	public void setHouseSellPrice(int houseSellCost) {
		this.houseSellPrice = houseSellCost;
	}

	public void setHotelSellPrice(int hotelSellCost) {
		this.hotelSellPrice = hotelSellCost;
	}
}