package jeu;

/**
 * Class PlusTwo.
 * Defines the card +2.
 * @author Kinza Kasmi
 *
 */
public class PlusTwo extends Card {
	/**
	 * Constructor.
	 * Creates a card with given color and with a point equal to 20.
	 * By default, the number associated to a PlusTwo card is -2.
	 * @param color the color of the card.
	 * @param number the number of the card.
	 */
	public PlusTwo(String color) {
		super(color, -2,20); // par defaut, le chiffre est -2
	}

	/**
	 * Displays the card following the format : +2-color.
	 */
	public String display() {
		return "+2-" + this.color;
	}
	
	/**
	 * Displays the card following the format : +2-C with C=Color.
	 */
	public String print() {
		return "+2"+Character.toUpperCase(this.color.charAt(0));
	}
}
