package jeu;

/**
 * Class Numbered.
 * The cards that contains a number and a color.
 * @author Kinza Kasmi
 *
 */
public class Numbered extends Card {
	
	/**
	 * Constructor.
	 * Creates a card with given number and color and with a point equal to the number of the card.
	 * @param color the color of the card.
	 * @param number the number of the card.
	 */
	public Numbered(String color, int number) {
		super(color, number,number);
		
		// Permet de retourner une erreur si la couleur n'est pas admissible par le jeu.
		if (number < 0 || number > 9) {
			throw new IllegalArgumentException("Carte inconnue");
		}
	}

	/**
	 * Displays the card following the format : n-color with n=number.
	 */
	public String display() {
		return this.number + "-" + this.color;
	}
	
	/**
	 * Displays the card following the format : N-C with N=Number and C=Color.
	 */
	public String print() {
		return this.number+""+Character.toUpperCase(this.color.charAt(0));
	}

}
