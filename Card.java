
public class Card {
	private String cardSuit;
	private String cardValue;
	
	public Card(String suit, String value){
		cardSuit = suit;
		cardValue = value;
	}
	
	public String getSuit(){
		return this.cardSuit;
	}
	
	public String getValue(){
		return this.cardValue;
	}
}
