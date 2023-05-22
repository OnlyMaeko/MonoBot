package defaulta;

public class Community_Chest_Cards {

	
	private String chest_card;
	private int position_chest;
	private boolean is_holdable;
	
	
	public Community_Chest_Cards(String card_name_chest, int position_in_rotation_chest, boolean holdable) {
		// TODO Auto-generated constructor stub
		
		this.chest_card = card_name_chest;
		this.position_chest = position_in_rotation_chest;
		this.is_holdable = holdable;
	}


//	Advance to Go (Collect $200)
//	Bank error in your favor. Collect $200
//	Doctor’s fee. Pay $50
//	From sale of stock you get $50
//	Get Out of Jail Free
//	Go to Jail. Go directly to jail, do not pass Go, do not collect $200
//	Holiday fund matures. Receive $100
//	Income tax refund. Collect $20
//	It is your birthday. Collect $10 from every player
//	Life insurance matures. Collect $100
//	Pay hospital fees of $100
//	Pay school fees of $50
//	Receive $25 consultancy fee
//	You are assessed for street repair. $40 per house. $115 per hotel
//	You have won second prize in a beauty contest. Collect $10
//	You inherit $100

	
	
		public void shuffle(int position_in_rotation)
		{
		;	
		}
		
//		Getters
		
		
		public String getChestCardName() 
		{
			return chest_card;
		}
		
		public boolean getHoldable() 
		{
			return is_holdable;
		}
		
		public int getPositionChest() 
		{
		return position_chest;	
		}
		
		
//		Setters
		
		public void setCardNameChest(String cardname)
		{
			this.chest_card = cardname;
		}
		
		
		public void setPositionChest(int pos)
		{
			this.position_chest = pos;
		}
		
		public void setHoldable(String holds)
		{
			this.is_holdable = holds;
		}
	}



