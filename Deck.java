package defaulta;

public class Deck {

	private List<Cards> chanceDeck;
	private List<Cards> communityChestDeck;

	public Deck() {

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

		chanceDeck = new ArrayList<>();
		chanceDeck.add(new Cards("Boardwalk"));
		chanceDeck.add(new Cards("Go"));
		chanceDeck.add(new Cards("Illinois"));
		chanceDeck.add(new Cards("StCharles"));
		chanceDeck.add(new Cards("Railroad"));
		chanceDeck.add(new Cards("Railroad"));
		chanceDeck.add(new Cards("Utility"));
		chanceDeck.add(new Cards("Get50"));
		chanceDeck.add(new Cards("GetOutOfJail"));
		chanceDeck.add(new Cards("GoBack3"));
		chanceDeck.add(new Cards("GoToJail"));
		chanceDeck.add(new Cards("PropertyRepairs"));
		chanceDeck.add(new Cards("Pay15"));
		chanceDeck.add(new Cards("Reading"));
		chanceDeck.add(new Cards("PayEachPlayer50"));
		chanceDeck.add(new Cards("Get150"));
		Collections.shuffle(chanceDeck);

//	Advance to Go (Collect $200)
//	Bank error in your favor. Collect $200
//	Doctor�s fee. Pay $50
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

		communityChestDeck = new ArrayList<>();
		communityChestDeck.add(new Cards("Go"));
		communityChestDeck.add(new Cards("Get200"));
		communityChestDeck.add(new Cards("Pay50"));
		communityChestDeck.add(new Cards("Get50"));
		communityChestDeck.add(new Cards("GetOutOfJail"));
		communityChestDeck.add(new Cards("GoToJail"));
		communityChestDeck.add(new Cards("Get100"));
		communityChestDeck.add(new Cards("Get20"));
		communityChestDeck.add(new Cards("Get10FromEachPlayer"));
		communityChestDeck.add(new Cards("Get100"));
		communityChestDeck.add(new Cards("Pay100"));
		communityChestDeck.add(new Cards("Pay50"));
		communityChestDeck.add(new Cards("Get25"));
		communityChestDeck.add(new Cards("StreetRepairs"));
		communityChestDeck.add(new Cards("Get10"));
		communityChestDeck.add(new Cards("Get100"));
		Collections.shuffle(communityChestDeck);

	}

}



