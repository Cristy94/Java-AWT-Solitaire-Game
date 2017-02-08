package solitaire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import solitaire.Card.Suit;

/**
 * A deck class to hold all 52 cards
 */
public class Deck {
	
	ArrayList<Card> cards;
	
	/**
	 * Class constructor
	 */
	public Deck() {
		
		// Create all the 52 cards
		cards = new ArrayList<Card>();
		
		for(Suit suit : Suit.values()) {
			for(int value = 1; value <= 14; ++value) {
				if(value != 11)
					cards.add(new Card(value, suit));
			}
		}
	}

	/**
	 * Shuffles the deck by doing deck.size perumatations
	 */
	public void shuffle() {
		Random randIndex = new Random();
		int size = cards.size();
		
		for(int shuffles = 1; shuffles <= 20; ++shuffles)
			for (int i = 0; i < size; i++) 
				Collections.swap(cards, i, randIndex.nextInt(size));
		
	}
	
	/**
	 * Returns the size of the deck
	 * @return {Integer} Number of cards in deck
	 */
	public int size() {
		return cards.size();
	}
	
	/**
	 * Draws a card from the pack. Pack must not be empty.
	 * @return First card in pack
	 */
	public Card drawCard() {
		Card c = cards.get(0);
		cards.remove(0);

		return c;
	}
	
}
