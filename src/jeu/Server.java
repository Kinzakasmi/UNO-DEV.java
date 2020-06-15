package jeu;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class Server.
 * Defines the server of the game.
 * @author Kinza Kasmi
 *
 */
public class Server {
	private ServerSocket serverSocket;
	private int nb_players;

	/**
	 * Constructor.
	 * created a serverSocket.
	 * @param port 
	 * @param nb_players the number of clients, ie of players, the server has to accept.
	 * @throws IOException
	 */
	public Server(int port, int nb_players) throws IOException {
		serverSocket = new ServerSocket(port);
		this.nb_players = nb_players;
	}

	/**
	 * Starts the game.
	 * @param filename the file containing a unsorted list of UNO cards.
	 * @throws IOException
	 */
	public void acceptLoop(String filename) throws IOException {
		System.out.println("Ready, waiting for players");
		
		// Connexion aux joueurs
		Service service = new Service();
		for (int i = 0; i < this.nb_players; i++) {
			Socket client = serverSocket.accept();
			service.accept(i,client);
		}
		
		// Debut du jeu
		service.play_boucle(filename);
		this.close();
	}

	public void close() throws IOException {
		this.serverSocket.close();
	}
}
