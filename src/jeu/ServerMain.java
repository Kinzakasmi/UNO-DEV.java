package jeu;

import java.io.IOException;

/**
 * Class ServerMain.
 * Implements the Main class of the server.
 * @author Kinza Kasmi
 */
public class ServerMain {

	public static void main(String[] args) throws IOException {
		Server serveur = new Server(6789, 2);
		serveur.acceptLoop("data/deck-numbers-plus2.txt");

		serveur.close();
	}
}
