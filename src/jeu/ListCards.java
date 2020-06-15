package jeu;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class ListCards.
 * Defines a list of cards, ie a deck or a hand.
 * @author Kinza Kasmi
 */
public class ListCards {
	ArrayList<Card> cards;
	
	/**
	 * Constructor.
	 * Creates an empty array of cards.
	 */
	public ListCards() {
		this.cards = new ArrayList<Card>();
	}
	
	/**
	 * Constructor.
	 * Creates an array of cards from a given file.
	 * @param filename a file that contains an unsorted list of all the cards in a UNO game.
	 * @throws IOException
	 */
	public ListCards(String filename) throws IOException {
		this.cards = new ArrayList<Card>();

		FileReader in = new FileReader(filename);
		BufferedReader bin = new BufferedReader(in);
		bin.readLine();
		int i = 0;
		while (bin.ready()) {
			i = i + 1;
			String line = bin.readLine();
			try {
				this.cards.add(Card.stringToCarte(line));
			} catch (IllegalArgumentException e) {
				System.out.println("Erreur carte n: "+i+" non valide");
			}
		}
		bin.close();
	}
	
	/**
	 * Calculates a player's score.
	 * @return the sum score of a player's hand.
	 */
	public int points() {
		int s=0;
		for (int j=0;j<this.size();j++) {
			s=s+this.get(j).point;
		}
		return s;
	}
	
	/**
	 * Gets a card from a list of cards.
	 * @param i the position of the wanted card.
	 * @return a card whose position in the list "this.cards" is i.
	 */
	public Card get(int i) {
		return this.cards.get(i);
	}
	
	/**
	 * Removes a card from a list of cards.
	 * @param i the position of the card to remove.
	 * @return the card in the position i after being removed from the list "this.cards".
	 */
	public Card remove(int i) {
		return this.cards.remove(i);
	}
	
	/**
	 * Removes a card from a list of cards.
	 * @param carte the card to be removed.
	 * @return true if the card is in the list (after being removed from "this.cards"), false otherwise.
	 */
	public boolean remove(Card carte) {
		for (int i=0; i<this.cards.size();i++) {
			if (cards.get(i).color.contentEquals(carte.color) && cards.get(i).number==carte.number) {
				this.cards.remove(i);
				return true;
			} 
		}
		return false;
	}
	
	/**
	 * adds a card at the end of a list of cards.
	 * @param carte
	 */
	public void add(Card carte) {
		this.cards.add(carte);
	}
	
	/**
	 * Adds a card at a given position in a list of cards.
	 * @param i the position in which the card will be added.
	 * @param carte the card to be added.
	 */
	public void add(int i, Card carte) {
		this.cards.add(i,carte);
	}

	/**
	 * Gives the size of a list of cards.
	 * @return the size of a list of cards "this.cards".
	 */
	public int size() {
		return this.cards.size();
	} 
	
	/**
	 * Prints the list of cards following this format : N-C N-C N-C ... with n:number and c:color.
	 * @return
	 */
	public String print() {
		String string = "";
		for (Card carte : this.cards) {
			string = string+" "+carte.print();
		}
		return string;
	}
	
	/**
	 * Verifies whether a list is empty or not.
	 * @return true if the list "this.cards" is empty, false otherwise.
	 */
	public boolean isEmpty() {
		if (this.cards == null || this.cards.size()==0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Initialize a deck.
	 */
	public void initialiseMain() {
		while (this.cards.isEmpty() == false) {
			this.cards.remove(0);
		}
	}

	
}
