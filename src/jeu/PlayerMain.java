package jeu;
import java.io.IOException;

/**
 * Class PlayerMain.
 * Implements the Main class of an interactive player.
 * @author Kinza Kasmi
 */
public class PlayerMain {

	public static void main(String[] args) throws IOException {
		// Connexion du client au serveur
		Player joueur = new Player("localhost", 6789);

		joueur.jouer();
	}
}