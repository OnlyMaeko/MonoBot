package defaulta;

public class Properties {

	//Attributes declaration
	private String Prop_Name;
	private double base_rent;
	private double rent_one;
	private double rent_two;
	private double rent_three;
	private double rent_four;
	private double rent_hotel;
	private double Value_Mortgage;
	private double Cost_of_House;
	private double Cost_of_Hotel;
	private String Color_of_Set;
	private double Price_to_Buy;
	private double Board_Position;
	private int Number_of_Houses;
	private boolean Is_Hotel;
	private String Who_Owns;
	private boolean Is_Fully_Owned;
	private boolean Is_Mortgaged;
	private double Price_Sell_House;
	private double Price_Sell_Hotel;


	public Properties(String Name, double Rent_Base, double Rent_1, double Rent_2, double Rent_3, double Rent_4, double Rent_Hotel, double Mortgage_Value,
	double Cost_House, double Cost_Hotel, String Color_Set, double Buy_Price, double Postion_on_Board, int Number_Houses, boolean Hotel, String Owner,
	boolean Fully_Owned, boolean Mortgaged, double House_Sell_Price, double Hotel_Sell_Price) {


		this.Prop_Name = Name;
		this.base_rent = Rent_Base;
		this.rent_one = Rent_1;
		this.rent_two = Rent_2;
		this.rent_three = Rent_3;
		this.rent_four = Rent_4;
		this.rent_hotel = Rent_Hotel;
		this.Value_Mortgage = Mortgage_Value;
		this.Cost_of_House = Cost_House;
		this.Cost_of_Hotel = Cost_Hotel;
		this.Color_of_Set = Color_Set;
		this.Price_to_Buy = Buy_Price;
		this.Board_Position = Postion_on_Board;
		this.Number_of_Houses = Number_Houses;
		this.Is_Hotel = Hotel;
		this.Who_Owns = Owner;
		this.Is_Fully_Owned = Fully_Owned;
		this.Is_Mortgaged = Mortgaged;
		this.Price_Sell_House = House_Sell_Price;
		this.Price_Sell_Hotel = Hotel_Sell_Price;





	}


// 	We need getters and setters lol bc that would just make things easier



		public void setPropName(String PropName)
			{
				this.Prop_Name = PropName;
					}
		public void setBaseRent(double RentBase)
			{
				this.base_rent = RentBase;
					}



				}
