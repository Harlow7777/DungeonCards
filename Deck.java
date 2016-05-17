import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Deck {
	private List<Card> deck = new ArrayList<Card>();
	private List<Card> discardPile = new ArrayList<Card>();

	public void populate() {
		List<String> suits = new ArrayList<String>(Arrays.asList("S", "H", "C", "D"));
		List<String> values = new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "Jack", "Queen", "King"));
		for(int i = 0; i < suits.size(); i++){
			for(int j = 0; j < values.size(); j++){
				deck.add(new Card(suits.get(i), values.get(j)));
			}
		}
	}
	
	/**
	 * Shuffles the deck (i.e. randomly reorders the cards in the deck). 
	 */
	public void shuffle() {
		if(deck.isEmpty()){
			deck.addAll(discardPile);
			Collections.shuffle(deck);
			discardPile.clear();
		} else {
			Collections.shuffle(deck);
		}
	}
	
	public Card drawCard() {
		Card currentCard = deck.get(0);
		deck.remove(0);
		discardPile.add(currentCard);
		//if the deck runs out of cards, shuffle the discardPile back into the deck
		if(deck.isEmpty()){
			deck.addAll(discardPile);
			Collections.shuffle(deck);
			discardPile.clear();
		}
		return currentCard;
	}
	
	public void dispose(Card c){
		discardPile.remove(c);
	}

}
