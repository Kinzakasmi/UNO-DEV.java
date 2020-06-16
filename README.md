# **Introduction** #
This repository contains an UNO game in JAVA. The command lines are unfortunately in french. 

*Remarque* : Le client est fonctionnel pour une manche à plusieurs joueurs (même 1 joueur vs un bot) limitée aux cartes numérotées de 0 à 9 et aux cartes +2, soit un sous-ensemble de 84 cartes.

# **1. Client** #
Le jeu doit reste ouvert à la prise en compte de nouvelles cartes par sous-typage, vous pouvez facilement en rajouter et changer quelques lignes du code.

Aucun graphisme n'est fait. Le client se limite à un mode texte dans la console, en respectant les noms suivants :

- carte numérotée : 0-rouge, 1-vert, 2-jaune ... 9-bleu
- carte +2 : +2-rouge, etc.
- carte "Passer ton tour" : Passer-rouge, etc.
- carte "Inversion" : Inversion-rouge, etc.
- carte +4 : +4
- carte "Joker" : Joker

**Protocole de communication :**
Le client respecte le protocole de communication suivant :

- TCP mode texte
- Démarrage du jeu :
  - lancer un serveur pour une partie à N joueurs
  - C → S : je-suis Cesar : le client demande à rejoindre la partie avec le pseudo Cesar
    - réponse S → C : bienvenue pour les N premier clients, puis si votre serveur continue à accepter des connexions : Erreur : plus de place pour les clients suivants.
  - S → C : debut-de-manche indique au joueur qu'une nouvelle manche va débuter et que la distribution des cartes va commencer
  - S → C : prends 7-rouge distribue la carte indiquée au joueur, envoyé 7 fois par joueur
- Jeu effectif :
  - S → C : nouveau-talon 7-vert indique à chaque joueur la nouvelle carte sur le talon
  - S → C : joue : indique au client qu'il doit jouer
    - réponse C → S : je-pose 7-rouge pour poser la carte indiquée sur le talon
      - réponse S → C : OK si la carte est autorisée
      - réponse S → C : prends 7-rouge, prends 3-bleu puis prends 4-jaune si la carte n'est pas autorisée (le joueur doit alors reprendre la carte posée et piocher 2 cartes)
    - réponse C → S : je-pioche pour piocher
      - réponse S → C : prends 3-bleu puis joue (message oublié dans la spécification initiale).
      - réponse C → S : je-passe pour passer son tour
      - réponse C → S : je-pose 3-bleu pour poser une carte (généralement la carte piochée)
  - S → C : fin-de-manche Cesar 0 Escartefigue 50 Panisse 7 Brun 42 pour une manche avec 4 joueurs ayant choisi les pseudos Cesar, Escartefigue, Panisse et Brun (noms tirés de la trilogie marseillaise de Marcel Pagnol).
  - S → C : prends 5-rouge si le joueur doit piocher une carte sans jouer (le joueur précédent a posé une carte +2 ou +4, message envoyé 2 ou 4 fois)
- Erreurs de protocole : si votre client envoie un message non conforme, le serveur répond avec un message Erreur : message invalide
- Les messages de notification suivants sont envoyés au client pour ne pas jouer "à l'aveugle"

  - S → C : joueur Cesar passe indique au joueur que le joueur spécifié (ici Cesar) vient de passer son tour
  - S → C : joueur Cesar pioche 1 indique au joueur que le joueur spécifié vient de piocher 1 carte
  - S → C : joueur Cesar pose 3-rouge indique au joueur que le joueur spécifié vient de poser une carte et que cette carte est autorisée
- Pour les extensions :
  - S → C : à la requête jouer votre client pourra aussi répondre :
    - réponse C → S : je-pose Joker-bleu pour poser une carte Joker sur le talon et choisir la couleur bleue
    - réponse C → S : je-pose +4-rouge pour poser une carte +4 sur le talon et choisir la couleur rouge
- Fin de partie :
  - S → C : fin-de-partie Cesar 500 Escartefigue 250 Panisse 447 Brun 142 quand la partie est terminé
  
  # **2. Serveur** #
  Le serveur respecte le protocole décrit ci-dessus
