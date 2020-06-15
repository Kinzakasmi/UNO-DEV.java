package jeu;

/**
 * Class Card.
 * Defines a UNO card.
 * @author 	Kinza Kasmi
 */
public abstract class Card {
	String color;
	int number;
	int point;

	/**
	* Constructor.
	* Creates a card with an associated color, number and point.
	* @param color the color of the card.
	* @param number the number of the card.
	* @param the point of the card.
	*/
	public Card(String color, int number, int point) {
		
		// Permet de retourner une erreur si la couleur n'est pas admissible par le jeu.
		if (! (color.matches("jaune") || color.matches("bleu") || color.matches("vert") || color.matches("rouge"))) {
			throw new IllegalArgumentException("Carte inconnue");
		}
		// Initialisation.
		this.color = color;
		this.number = number;
		this.point = point; 
	}

	/**
	 * Verifies whether two cards are compatible.
	 * @param card the card that will be compared to.
	 * @return true if this.carte is compatible with carte, false otherwise.
	 */
	public boolean compatibilite(Card card) {
		if ((card.color.equals(this.color)) || (card.number == this.number)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Converts a card of type String to a card of type Card.
	 * @param card_string the card of type String : n-color.
	 * @return a card with card.number=n and card.color=color.
	 */
	static public Card stringToCarte(String card_string) {
		String color = card_string.split("-")[1];
		String number_string = card_string.split("-")[0];
		int number;
			if (number_string.equals("+2")) {
				return new PlusTwo(color);
			} else {
				number = Integer.parseInt(number_string);
				return new Numbered(color, number);
			}
	}
	
	protected abstract String display();

	protected abstract String print() ;
}
