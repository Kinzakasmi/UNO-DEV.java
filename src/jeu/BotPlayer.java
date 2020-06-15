package jeu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Class BotPlayer. Defines a bot player.
 * 
 * @author Kinza Kasmi
 *
 */
public class BotPlayer {
	ListCards main;
	Card talon;
	private Socket socket;
	private PrintWriter txtOut;
	private BufferedReader txtIn;

	/**
	 * Constructor. Creates a client socket and its associated deck.
	 * 
	 * @param host the IP Address.
	 * @param port
	 * @param nom  the name of the bot player.
	 * @throws IOException
	 */
	public BotPlayer(String host, int port, String nom) throws IOException {
		this.main = new ListCards();
		this.talon = null;
		socket = new Socket(host, port);
		txtOut = new PrintWriter(socket.getOutputStream(), true); // true sets autoflush on
		txtIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.send("je-suis " + nom);
	}

	/**
	 * Sends a message via the outputstream.
	 * 
	 * @param message the message to be sent.
	 * @throws IOException
	 */
	public void send(String message) throws IOException {
		txtOut.println(message);
		// Pour une meilleure visualisation.
		System.out.println("My answer : " + message);
	}

	/**
	 * Receives a message via the inputstream.
	 * 
	 * @throws IOException
	 */
	public String receive() throws IOException {
		String message = txtIn.readLine();
		// Pour une meilleure visualisation.
		System.out.println("Server answer : " + message);
		return message;
	}

	/**
	 * Closes the socket.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		socket.close();
	}

	/**
	 * Receives a card and adds it to the hand.
	 * 
	 * @param message a String whose syntax is : "prends n-couleur" with n-couleur
	 *                the received card.
	 * @throws IOException
	 */
	private void prendre(String message) throws IOException {
		if (message.matches("prends.*")) {
			Card carte = Card.stringToCarte(message.split(" ")[1]);
			main.add(carte);
		}
	}

	/**
	 * Draws a card.
	 * 
	 * @throws IOException
	 */
	private void piocher() throws IOException {
		this.send("je-pioche");
		String message = this.receive();
		this.prendre(message);
		this.receive();
	}

	/**
	 * Discards a card.
	 * 
	 * @param carte the card to discard.
	 * @throws IOException
	 */
	private void poser(Card carte) throws IOException {
		this.send("je-pose " + carte.display());
		this.main.remove(carte);
	}

	/**
	 * Skips the round.
	 * 
	 * @throws IOException
	 */
	private void passer() throws IOException {
		this.send("je-passe");
	}

	/**
	 * Updates the discard top when a player puts a card on the pile.
	 * 
	 * @param message a String whose syntax is : "je-pose n-couleur" with n-couleur
	 *                the new discard top.
	 */
	private void updateTalon(String message) {
		this.talon = Card.stringToCarte(message.split(" ")[1]);
	}

	/**
	 * The loop that mimics a player's decisions and actions.
	 * 
	 * @throws IOException
	 */
	public void action() throws IOException {
		// Pour une meilleure visualisation, on affiche la main du joueur.
		String string = this.main.print();
		System.out.println("Ma main est : " + string);

		// Le bot recherche la carte compatible avec le talon.
		for (int i = 0; i < this.main.size(); i++) {
			Card carte = this.main.get(i);
			if (carte.compatibilite(talon)) {
				this.poser(carte);
				return;
			}
		}

		// Si le bot ne trouve aucune carte compatible, il pioche une carte.
		this.piocher();
		this.receive();
		Card pioche = this.main.get(this.main.size() - 1);

		// Le bot vérifie si la pioche est compatible avec le talon.
		if (pioche.compatibilite(talon)) {
			this.poser(pioche);
			return;
		} else {
			this.passer();
		}
	}

	/**
	 * the main loop of the game.
	 * 
	 * @throws IOException
	 */
	public void jouer() throws IOException {
		// Debut du jeu.
		String message = this.receive();

		while (message.matches("fin-de-partie.*") == false) {
			// Reception de la main.
			if (message.matches("debut-de-manche")) {
				for (int i = 0; i < 7; i++) {
					message = this.receive();
					this.prendre(message);
				}
			}

			// Jeu effectif.
			while (message.matches("fin-de-manche.*") == false) {
				message = this.receive();
				if (message.matches("nouveau-talon.*")) {
					this.updateTalon(message);
					message = this.receive();
				}
				if (message.matches("prends.*")) {
					this.prendre(message);

				}
				if (message.matches("joue")) {
					System.out.println("talon = " + this.talon.display());
					this.action();
				}
			}
			message = this.receive();
			this.main.initialiseMain(); // Initialise la main après chaque manche.
		}
		this.close();

	}
}
