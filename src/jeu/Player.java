package jeu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Class Player. Defines a UNO player.
 * 
 * @author Kinza Kasmi.
 *
 */
public class Player {
	ListCards main;
	Card talon;
	private Socket socket;
	private PrintWriter txtOut;
	private BufferedReader txtIn;

	/**
	 * Constructor. Creates a client socket and its associated deck. Initializes its
	 * input and output streams.
	 * 
	 * @param the  host IP Address.
	 * @param port
	 * @throws IOException
	 */
	public Player(String host, int port) throws IOException {
		this.main = new ListCards();
		this.talon = null;
		socket = new Socket(host, port);
		txtOut = new PrintWriter(socket.getOutputStream(), true); // true sets autoflush on
		txtIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	/**
	 * Sends a message via the outputstream.
	 * 
	 * @param message the message to be sent.
	 * @throws IOException
	 */
	public void send(String message) throws IOException {
		txtOut.println(message);
		System.out.println("My answer : " + message);
	}

	/**
	 * Receives a message via the inputstream.
	 * 
	 * @throws IOException
	 */
	public String receive() throws IOException {
		String message = txtIn.readLine();
		// pour visualiser les reponses du serveur
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
		Card carte = Card.stringToCarte(message.split(" ")[1]);
		this.main.add(carte);
	}

	/**
	 * Draws a card.
	 * 
	 * @throws IOException
	 */
	private void piocher() throws IOException {
		this.send("je-pioche");
	}

	/**
	 * Discards a card.
	 * 
	 * @param carte the card to discard.
	 * @throws IOException
	 */
	private void poser(Card carte) throws Exception {
		if (main.remove(carte) == false) {
			throw new Exception("You don't have that card");
		} else {
			this.send("je-pose " + carte.display());
			this.main.remove(carte);
		}
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
	public void action(Scanner sc) throws IOException {
		// lecture du scan
		String request = sc.nextLine();
		// Action
		if (request.matches("je-pose.*")) {
			try {
				Card carte = Card.stringToCarte(request.split(" ")[1]);
				this.poser(carte);
			} catch (Exception e) {
				System.out.println("Carte non valide. Veuillez rentrer une nouvelle carte");
				this.action(sc);
			}
		} else if (request.matches("je-pioche.*")) {
			this.piocher();
			return;
		} else if (request.matches("je-passe.*")) {
			this.passer();
			this.receive();
		}
	}

	/**
	 * Implements the main loop.
	 * 
	 * @throws IOException
	 */
	public void jouer() throws IOException {
		System.out.print("entrer votre nom : ");
		Scanner sc = new Scanner(System.in);
		String request = sc.nextLine();
		this.send(request);
		this.receive();

		// Debut de manche
		String message = this.receive();

		while (message.matches("fin-de-partie.*") == false) {
			// reception de la main
			if (message.matches("debut-de-manche")) {
				for (int i = 0; i < 7; i++) {
					message = this.receive();
					this.prendre(message);
				}
			}

			// Jeu effectif
			while (message.matches("fin-de-manche.*") == false) {
				message = this.receive();
				System.out.println("message 1 = " + message);
				if (message.matches("nouveau-talon.*")) {
					this.updateTalon(message);
					message = this.receive();
				}
				if (message.matches("prends.*")) {
					this.prendre(message);

				}
				if (message.matches("joue")) {
					// Affichage de la main.
					System.out.println("Ma main est : " + this.main.print());
					// Affichage du talon.
					System.out.println("talon = " + this.talon.display());
					this.action(sc);
				}
			}
			message = this.receive();
			this.main.initialiseMain();
		}
		this.close();

	}
}
