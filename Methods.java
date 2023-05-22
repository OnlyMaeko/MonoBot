package defaulta;

public class Methods {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		public void rent(Properties property, Player player_name)
		
		{
		if(Number_Houses == 0) 
		{
			current_rent = Rent_Base;
		}
		else if(Number_Houses == 1)
		{
			current_rent = Rent_1;
		}
		else if(Number_Houses == 2)
		{
			current_rent = Rent_2;
		}
		else if(Number_Houses == 3)
		{
			current_rent = Rent_3;
		}
		else if(Number_Houses == 4)
		{
			current_rent = Rent_4;
		}
		else if(Hotel = true)
		{
			current_rent = Rent_Hotel;
		}
		else if(Fully_Owned = true)
		{
		current_rent = Rent_Base * 2;
		}
		
		this.actual_rent = current_rent;
		
		
		
		
		
	}

}