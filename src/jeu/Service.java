package jeu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Class Service.
 * Defines the service loop dedicated to the connected clients and other necessary functions.
 * @author 	Kinza KASMI
 */
public class Service {
	private ArrayList<Socket> clientSocket;
	private ArrayList<BufferedReader> txtIn;
	private ArrayList<PrintWriter> txtOut;
	private ArrayList<String> nom_client;
	private ArrayList<ListCards> mains;
	private ListCards drawPile;
	private ListCards discardPile;

	/**
	* Constructor.
	* Initializes the attributes.
	*/
	public Service() throws IOException {
		// Chaque indice dans une ArrayList correspond à l'indice d'un joueur.
		this.clientSocket = new ArrayList<Socket>();
		this.txtIn = new ArrayList<BufferedReader>();
		this.txtOut = new ArrayList<PrintWriter>();
		this.drawPile = new ListCards();
		this.discardPile = new ListCards();
		this.nom_client = new ArrayList<String>();
		this.mains = new ArrayList<ListCards>();
	}

	/**
	 * Adds the accepted clientSocket to the players list.
	 * Initializes its input and output streams.
	 * Defines its name and adds it to the list of names.
	 * 
	 * @param i the number associated to the client.
	 * @param clientSocket the new clientSocket.
	 * @throws IOException
	 */
	public void accept(int i, Socket clientSocket) throws IOException {
		// Ajout du nouveau client à la liste des joueurs.
		this.clientSocket.add(clientSocket);
		txtIn.add(new BufferedReader(new InputStreamReader(this.clientSocket.get(i).getInputStream())));
		txtOut.add(new PrintWriter(this.clientSocket.get(i).getOutputStream(), true)); // true sets autoflush on

		// Reception du nom du joueur.
		String message = this.receive(i);
		this.nom_client.add(message.split(" ")[1]);
		this.send(i, "bienvenue");
		
		// Initialisation de la main du joueur.
		this.mains.add(new ListCards());
	}
	
	/**
	 * Receives a message from a specefic player.
	 * @param joueur the index of the player.
	 * @return the message received.
	 * @throws IOException
	 */
	public String receive(int joueur) throws IOException {
		String message = txtIn.get(joueur).readLine();
		try {
			System.out.println("S <- " + this.nom_client.get(joueur) + ": " + message);
		} catch (Exception e) {
			System.out.println("S <- null :" + message);
		}
		return message;
	}

	/**
	 * Sends a message to a specefic player.
	 * @param joueur the index of the player.
	 * @param message the message to be sent.
	 */
	private void send(int joueur, String message) {
		txtOut.get(joueur).println(message);
		System.out.println("S -> " + this.nom_client.get(joueur) + ": " + message);
	}

	/**
	 * Sends a message to all players.
	 * @param message the message to be sent.
	 */
	private void sendAll(String message) {
		int nb = this.mains.size();
		for (int i = 0; i < nb; i++) {
			txtOut.get(i).println(message);
			System.out.println("S -> " + this.nom_client.get(i) + ": " + message);
		}
	}

	/**
	 * Verifies whether one of the hands is empty.
	 * @return true if one of the hands is empty. False otherwise.
	 */
	public boolean mainIsEmpty() {
		for (int i = 0; i < this.mains.size(); i++) {
			if (this.mains.get(i).isEmpty() == true) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Initializes the draw pile from the card pile when the first one is empty.
	 */
	public void justInCaseDrawIsEmpty() {
		if (this.drawPile.isEmpty()) {
			for (int i = 0; i < this.discardPile.size(); i++) {
				this.drawPile.add(this.discardPile.remove(this.discardPile.size()-1));
			}
			System.out.println("new draw Pile : " + drawPile.print());
			System.out.println("new discard Pile : " + discardPile.print());
		}
	}

	 /**
	 * Receives a card and adds it to the hand of specefic player.
	 * 
	 * @param joueur the indew of the player.
	 * @param message a String whose syntax is : "prends n-couleur" with n-couleur
	 *                the received card.
	 * @throws IOException
	 */
	private void prendre(int joueur, Card carte) {
		this.justInCaseDrawIsEmpty();
		this.mains.get(joueur).add(carte);
		this.send(joueur, "prends " + carte.display());
		this.drawPile.remove(0);
	}

	/**
	 * Poses a card if this one is valid.
	 * @param i the index of the current player.
	 * @param carte
	 * @param main the hand of the current player.
	 * @param nom_client the name of the current player.
	 * @param message
	 * @throws IOException
	 */
	private void poseValide(int i, Card carte, ListCards main, String nom_client, String message) throws IOException {
		this.send(i, "OK");
		this.sendAll("joueur " + nom_client + " pose " + carte.print());
		this.discardPile.add(0, carte);
		main.remove(carte);
		this.sendAll("nouveau-talon " + carte.display());
		this.drawPile.print();
		this.discardPile.print();

		// le cas ou le joueur pose un +2
		if (carte.number == -2) {
			// determination du joueur suivant
			int j = 0;
			if (i + 1 == this.mains.size()) {
				j = 0;
			} else {
				j = i + 1;
			}
			this.justInCaseDrawIsEmpty();
			this.prendre(j, this.drawPile.get(0));
			this.justInCaseDrawIsEmpty();
			this.prendre(j, this.drawPile.get(0));
			//verification que la main du joueur n'est pas vide, cf script-fin2-sur-+2
			if (this.mainIsEmpty()) {
				return;
			}
			this.action(i, main, nom_client, 1);
		}
	}

	/**
	 * Deals with the case where the card posed by the current player is not valid.
	 * @param i the index of the current player.
	 * @param carte the invalid card.
	 * @param main the hand of the current player.
	 */
	private void poseInvalide(int i, Card carte, ListCards main) {
		this.prendre(i, carte);
		this.prendre(i, this.drawPile.get(0));
		this.prendre(i, this.drawPile.get(0));
	}

	/**
	 * Returns the maximum of an array.
	 * @param tab
	 * @param n the size of the array.
	 * @return
	 */
	private int max(int[] tab, int n) {
		int max = 0;
		for (int nb : tab) {
			if (nb > max) {
				max = nb;
			}
		}
		return max;
	}

	/**
	 * Gets the top of the discard pile.
	 * @return
	 */
	private Card getTalon() {
		this.justInCaseDrawIsEmpty();
		return discardPile.get(0);
	}

	/**
	 * Calculates all the scores.
	 * @param somme the array of scores.
	 */
	private void calculatePoints(int[] somme) {
		for (int i = 0; i < this.mains.size(); i++) {
			somme[i] = somme[i] + this.mains.get(i).points();
		}
	}

	/**
	 * Sends the scores to the players.
	 * @param m the String : "fin-de-manche" or "fin-de-partie".
	 * @param somme the array of scores.
	 */
	private void sendPoints(String m, int[] somme) {
		String message = m;
		for (int i = 0; i < this.mains.size(); i++) {
			message = message + this.nom_client.get(i) + " " + somme[i] + " ";
		}
		this.sendAll(message);
	}

	private void initialiseMain() {
		for (int i = 0; i < this.mains.size(); i++) {
			this.mains.get(i).initialiseMain();
		}
	}

	/**
	 * The loop that deals with a player's decisions and actions.
	 * @param i the index of the current player.
	 * @param main the hand of the current player.
	 * @param nom_client the name of the current player.
	 * @param rec the number of consecutive times the player has played.
	 * @throws IOException
	 * @throws IOException
	 */
	private void action(int i, ListCards main, String nom_client, int rec) throws IOException {
		if (rec < 3) { // on rappel que le joueur ne peut jouer que deux fois de suite, ie. ne piocher qu'une fois
			this.send(i, "joue");
			// Rappel de la pioche et du talon
			System.out.println("draw Pile : " + drawPile.print());
			System.out.println("discard Pile : " + discardPile.print());
			System.out.println("la main : "+main.print());
			System.out.println("Le talon = " + this.getTalon().display());
			
			
			String message = this.receive(i);

			if (message.matches("je-pose.*")) {
				message = message.split(" ")[1];
				Card carte = Card.stringToCarte(message);

				// le cas ou la carte est valide
				if (carte.compatibilite(this.getTalon()) == true) {
					this.poseValide(i, carte, main, nom_client, message);

					// le cas ou la carte n'est pas valide
				} else {
					this.poseInvalide(i, carte, main);
				}

			} else if (message.matches("je-passe.*")) {
				this.sendAll("joueur " + nom_client + " passe");
				return;

			} else if (message.matches("je-pioche.*")) {
				this.prendre(i, this.drawPile.get(0));
				this.sendAll("joueur " + nom_client + " pioche 1");
				
				// cette condition s'assure que les drawPile et discardPile ne sont pas vides (a l'exception du talon), donc que le jeu n'est pas fini 
				if (this.drawPile.isEmpty() && this.discardPile.size()==1) {
					return;
				}
				// le meme joueur doit encore jouer
				this.action(i, main, nom_client, rec + 1); // rec+1 car la pioche est limitee a une fois
			}
		} else {
			this.send(i, "Erreur : message invalide");
			return;
		}

	}
	
	/**
	 * Main loop of the game
	 * @param filename the file containing the cards
	 * @throws IOException
	 */
	public void play_boucle(String filename) throws IOException {
		int nb = nom_client.size();
		int somme[] = new int[nb];
		
		// les regles imposent que le jeu doit se terminer a 500 points mais pour pouvoir executer 
		// les scripts donnes par le prof, il ne faut faire qu'une seule manche, d'ou le this.max()<1
		
		while (this.max(somme, nb) < 1) {   
			this.sendAll("debut-de-manche");
			// distribution des cartes
			this.drawPile = new ListCards(filename);
			for (int i = 0; i < 7 * nb; i++) {
				this.prendre(i % nb, drawPile.get(0));
			}

			// Initialisation du tas
			this.discardPile.add(0, drawPile.remove(0));
			this.sendAll("nouveau-talon " + this.getTalon().display());

			// Debut du jeu effectif
			int j = 0; // indice du joueur
			int s = 0; // nb de tours

			int rec = 1; // recurrence de la boucle action, permet de limiter la pioche a 1fois(limite
							// fixee par wikipedia)
			
			//  la DEUXIEME condition suivante est tres rare mais il se peut que les deux joueurs soient mauvais et donc il n'y a pas de gagnant
			while (this.mainIsEmpty() == false && !(this.drawPile.isEmpty() && this.discardPile.size()==1 )) {
				
				this.action(j, mains.get(j), nom_client.get(j), rec);

				rec = 1;
				s = s + 1;
				j = s % nb;
			}
			this.calculatePoints(somme);
			this.sendPoints("fin-de-manche ", somme);
			this.initialiseMain();
		}
		this.sendPoints("fin-de-partie ", somme);
	}

}
