package defaulta;

public class Chance_Cards {

	private String name_of_card;
	private int position;
	
	public Chance_Cards(String card_name, int position_in_rotation ) {
		// TODO Auto-generated constructor stub
		
		this.name_of_card = card_name;
		this.position = position_in_rotation;
	}


//		Advance to Boardwalk
//		Advance to Go (Collect $200)
//		Advance to Illinois Avenue. If you pass Go, collect $200
//		Advance to St. Charles Place. If you pass Go, collect $200
//		Advance to the nearest Railroad. If unowned, you may buy it from the Bank. If owned, pay wonder twice the rental to which they are otherwise entitled
//		Advance to the nearest Railroad. If unowned, you may buy it from the Bank. If owned, pay wonder twice the rental to which they are otherwise entitled
//		Advance token to nearest Utility. If unowned, you may buy it from the Bank. If owned, throw dice and pay owner a total ten times amount thrown.
//		Bank pays you dividend of $50
//		Get Out of Jail Free
//		Go Back 3 Spaces
//		Go to Jail. Go directly to Jail, do not pass Go, do not collect $200
//		Make general repairs on all your property. For each house pay $25. For each hotel pay $100
//		Speeding fine $15
//		Take a trip to Reading Railroad. If you pass Go, collect $200
//		You have been elected Chairman of the Board. Pay each player $50
//		Your building loan matures. Collect $150

	
		public void shuffle(int position_in_rotation)
		{
		;	
		}
		
//		Getters
		
		
		public String getCardName() 
		{
			return name_of_card;
		}
		
		public int getPosition() 
		{
		return position;	
		}
		
		
//		Setters
		
		public void setCardName(String cardname)
		{
			this.name_of_card = cardname;
		}
		
		
		public void setPosition(int pos)
		{
			this.position = pos;
		}
		
		
	}



