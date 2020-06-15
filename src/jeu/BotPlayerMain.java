package jeu;
import java.io.IOException;

/**
 * Class BotPlayerMain
 * Implements the Main class of a bot player
 * @author Kinza KASMI
 *
 */
public class BotPlayerMain {

	public static void main(String[] args) throws IOException {
		// Connexion du client au serveur
		BotPlayer player = new BotPlayer("localhost", 6789, "Khadija");

		player.jouer();
	}
}