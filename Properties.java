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

	// Getters	
	public String getPropName() 
	{
	return propName;
	}
	
	public double getBaseRent() 
	{
	return baseRent;
	}
	
	public double  getRentOne() 
	{
	return rentOne;	
	}
	
	public double getRentTwo() 
	{
	return rentTwo;	
	}

	public double getRentThree() 
	{
	return rentThree;	
	}
	
	public double getRentFour() 
	{
	return rentFour;	
	}
	
	public double getRentHotel() 
	{
	return rentHotel;	
	}
	
	public double getMortgageValue() 
	{
	return mortgageValue;	
	}
	
	public double getHouseCost() 
	{
	return houseCost;	
	}
	
	public double getHotelCost() 
	{
	return hotelCost;	
	}
	
	public String getSetColor() 
	{
	return setColor;	
	}
	
	public double getPrice() 
	{
	return price;	
	}
	
	public double getBoardPosition() 
	{
	return boardPosition;	
	}
	
	public int getNumberOfHouses() 
	{
	return numberOfHouses;	
	}
	
	public boolean getIsHotel() 
	{
	return isHotel;	
	}
	
	public boolean getIsFullyOwned() 
	{
	return isFullyOwned;	
	}

	public String getOwner() 
	{
	return owner;	
	}
	
	public boolean getIsMortgaged() 
	{
	return isMortgaged;	
	}
	
	public double getHouseSellPrice() 
	{
	return houseSellPrice;	
	}
	
	public double getHotelSellPrice() 
	{
	return hotelSellPrice;	
	}
	
	//	Setters

	public void setPropName(String propertyName)
	{
		this.propName = propertyName;
	}
	
	public void setBaseRent(double OriginalRent)
	{
		this.baseRent = OriginalRent;
	}
	
	public void setRentOne(double rent1)
	{
		this.rentOne = rent1;
	}
	
	public void setRentTwo(double rent2)
	{
		this.rentTwo = rent2;
	}
	
	public void setRentThree(double rent3)
	{
		this.rentThree = rent3;
	}
	
	public void setRentFour(double rent4)
	{
		this.rentFour = rent4;
	}
	
	public void setRentHotel(double rentH)
	{
		this.rentHotel = rentH;
	}
	
	public void setMortgageValue(double mortgageV)
	{
		this.mortgageValue = mortgageV;
	}
	
	public void setHouseCost(double housePrice)
	{
		this.houseCost = housePrice;
	}
	
	public void setHotelCost(double hotelPrice)
	{
		this.hotelCost = hotelPrice;
	}
	
	public void setSetColor(String colorOfSet)
	{
		this.setColor = colorOfSet;
	}
	
	public void setPrice(double cost)
	{
		this.price = cost;
	}
	
	public void setBoardPosition(double boardPos)
	{
		this.boardPosition = boardPos;
	}
	
	public void setNumberOfHouses(double numberHouses)
	{
		this.numberOfHouses = numberHouses;
	}
	
	public void setIsHotel(boolean ifHotel)
	{
		this.isHotel = ifHotel;
	}
	
	public void setOwner(String player)
	{
		this.owner = player;
	}
	
	public void setIsFullyOwned(boolean ifFullyOwned)
	{
		this.isFullyOwned = ifFullyOwned;
	}
	
	public void setIsMortgaged(boolean ifMortgaged)
	{
		this.isMortgaged = ifMortgaged;
	}
	
	public void setHouseSellPrice(double houseSellCost)
	{
		this.houseSellPrice = houseSellCost;
	}
	
	public void setHotelSellPrice(double hotelSellCost)
	{
		this.hotelSellPrice = hotelSellCost;
	}

}