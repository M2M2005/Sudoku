// ETAT : MARCHE NICKEL

import java.util.*;
import java.lang.*;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class SudokuBase {
    private static int[][] gOrdi;

    public static int saisirEntierMinMax(int min, int max) {
        int n = saisirEntier();

        while (min > n || n > max) {
            System.out.println("Veuillez réessayer. Entrez un entier compris entre " + min + " et " + max + ":");
            n = saisirEntier();
        }
        return n;
    }

    public static void copieMatrice(int[][] mat1, int[][] mat2) {
        for (int i = 0; i < mat1.length; i++) {
            System.arraycopy(mat1[i], 0, mat2[i], 0, mat1[0].length);
        }
    }

    public static void afficheGrille(int k, int[][] g) {
        System.out.print("    ");
        for (int ligne = 1; ligne <= g.length; ligne++) {
            System.out.print(ligne + " ");
            if (ligne % k == 0 && ligne < g.length) {
                System.out.print("  ");
            }
        }
        System.out.println();
        for (int i = 0; i < g.length; i++) {
            if (i % k == 0 && i != 0) {
                System.out.print("   ");
                for (int j = 0; j < g.length + k - 1; j++) {
                    System.out.print("--");
                }
                System.out.println();
            }
            System.out.print(i + 1 + " | ");
            for (int j = 0; j < g[i].length; j++) {
                if (j % k == 0 && j != 0) {
                    System.out.print("| ");
                }
                System.out.print(g[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.print("   ");
        for (int j = 0; j < g.length + k - 1; j++) {
            System.out.print("--");
        }
        System.out.print("\n");
    }

    // .........................................................................

    public static int[] debCarre(int k, int i, int j) {
        int[] coordonnees = new int[2];
        coordonnees[0] = (i / k) * k;
        coordonnees[1] = (j / k) * k;
        return coordonnees;
    }

    // .........................................................................

    public static void initGrilleComplete(int[][] gComplete) {
        int[][] grilleRemplie = {
                {6, 2, 9, 7, 8, 1, 3, 4, 5},
                {4, 7, 3, 9, 6, 5, 8, 1, 2},
                {8, 1, 5, 2, 4, 3, 6, 9, 7},
                {9, 5, 8, 3, 1, 2, 4, 7, 6},
                {7, 3, 2, 4, 5, 6, 1, 8, 9},
                {1, 6, 4, 8, 7, 9, 2, 5, 3},
                {3, 8, 1, 5, 2, 7, 9, 6, 4},
                {5, 9, 6, 1, 3, 4, 7, 2, 8},
                {2, 4, 7, 6, 9, 8, 5, 3, 1}
        };
        copieMatrice(grilleRemplie, gComplete);
        // System.out.println("Grille Ordi de base :");
        // afficheGrille(3, gComplete);
        int transformations = randomMinMax(10, 30);
        for (int i = 0; i < transformations; i++) {
            int idtransfo = randomMinMax(1, 4);
            if (idtransfo == 1) {
                rotation90deg(gComplete);
            }
            if (idtransfo == 2) {
                symetrieHorizontale(gComplete);
            }
            if (idtransfo == 3) {
                symetrieDiagonale(gComplete);
            }
            if (idtransfo == 4) {
                echangeLignes(gComplete, randomMinMax(1, 9), randomMinMax(1, 9));
            }
        }
        // System.out.println("Grille Ordi transfomée :");
        // afficheGrille(3, gComplete);
    }

    /// //

    // .........................................................................
    public static void initGrilleIncomplete(int nbTrous, int[][] gSecret, int[][] gIncomplete) {
        copieMatrice(gSecret, gIncomplete);
        int hauteur;
        int largeur;
        for (int j = 0; j < nbTrous; j++) {
            hauteur = randomMinMax(0, 8);
            largeur = randomMinMax(0, 8);
            if (gIncomplete[hauteur][largeur] != 0) {
                gIncomplete[hauteur][largeur] = 0;
            } else {
                j--;
            }
        }
    }

    // .........................................................................

    public static void saisirGrilleIncomplete(int nbTrous, int[][] g) {
        int n = 0;
        int[][] gVide = new int[9][9];
        System.out.println("Veuiller remplir la grille avec trou (Tout doit être JUSTE !)");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                g[i][j] = saisirEntierMinMax(0, 9);
                if (g[i][j] == 0) {
                    n++;
                }
            }
            afficheGrille(3, g);
        }
        if (n != nbTrous) {
            System.out.println("Le nombre de trous ne correspond pas");
            copieMatrice(gVide, g);
            saisirGrilleIncomplete(nbTrous, g);
        }
        afficheGrille(3, g);
    }

    public static void saisirGrilleIncompleteFichier(int nbTrous, int[][] g, String fic) {
        int n = 0;
        int erreur = 0;
        fic = System.getProperty("user.dir") + "/" + fic + ".txt";
        try (BufferedReader lecteur = new BufferedReader(new FileReader(fic))) {
            for (int i = 0; i < 9; i++) {
                String ligne = lecteur.readLine();
                String[] valeurs = ligne.split("\\s+");
                for (int j = 0; j < 9; j++) {
                    int valeur = Integer.parseInt(valeurs[j]);
                    g[i][j] = valeur;
                    if (g[i][j] == 0) {
                        n++;
                    }
                }
            }
            if (n != nbTrous) {
                System.out.println("ERREUR : Le nombre de trous n'est pas correct");
                System.out.println("Nombre de trous demandé :" + nbTrous);
                erreur++;
            }
        } catch (IOException e) {
            System.out.println("ERREUR : fichier introuvable, saisir le nom du fichier (sans .txt):");
        }
        if (erreur > 0) {
            System.out.println("Corrigé les erreur : " + erreur + " erreur détecter");
            System.out.println("Saisir le nom du nouveau fichier :");
            saisirGrilleIncompleteFichier(nbTrous, g, saisirChaine());
        }
    }

    // .........................................................................

    public static void initPleines(int[][] gOrdi, boolean[][][] valPossibles, int[][] nbValPoss) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (gOrdi[i][j] == 0) {
                    for (int n = 1; n < 10; n++) {
                        valPossibles[i][j][n] = true;
                    }
                    nbValPoss[j][i] = 9;
                }
            }
        }

    }

    // .........................................................................

    public static void suppValPoss(int[][] gOrdi, int i, int j, boolean[][][] valPossibles, int[][] nbValPoss) {
        // COLONNE
        for (int colonne = 0; colonne < 9; colonne++) {
            valPossibles[i][j][gOrdi[i][colonne]] = false;
            if (valPossibles[i][colonne][gOrdi[i][j]]) {
                valPossibles[i][colonne][gOrdi[i][j]] = false;
                nbValPoss[i][colonne]--;
            }
        }
        // LIGNE
        for (int ligne = 0; ligne < 9; ligne++) {
            valPossibles[i][j][gOrdi[ligne][j]] = false;
            if (valPossibles[ligne][j][gOrdi[i][j]]) {
                valPossibles[ligne][j][gOrdi[i][j]] = false;
                nbValPoss[ligne][j]--;
            }
        }
        // CARRE
        int[] coordonnees = debCarre(3, i, j);
        int DebLigne = coordonnees[0];
        int DebColonne = coordonnees[1];
        for (int ligne = DebLigne; ligne < DebLigne + 3; ligne++) {
            for (int colonne = DebColonne; colonne < DebColonne + 3; colonne++) {
                valPossibles[i][j][gOrdi[ligne][colonne]] = false;
                if (valPossibles[ligne][colonne][gOrdi[i][j]]) {
                    valPossibles[ligne][colonne][gOrdi[i][j]] = false;
                    nbValPoss[ligne][colonne]--;
                }
            }
        }
        // CALCULE NBVALPOSS
        nbValPoss[i][j] = 0;
        for (int n = 1; n < 10; n++) {
            if (valPossibles[i][j][n]) {
                nbValPoss[i][j]++;
            }
        }
    }

    // .........................................................................

    public static void initPossibles(int[][] gOrdi, boolean[][][] valPossibles, int[][] nbValPoss) {
        initPleines(gOrdi, valPossibles, nbValPoss);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (gOrdi[i][j] == 0) {
                    suppValPoss(gOrdi, i, j, valPossibles, nbValPoss);
                } else {
                    for (int n = 0; n < 10; n++) {
                        valPossibles[i][j][n] = false;
                    }
                    nbValPoss[i][j] = 0;
                }
            }
        }
    }

    public static int initPartie(int[][] gSecret, int[][] gHumain, int[][] gOrdi, boolean[][][] valPossibles,
                                 int[][] nbValPoss) {
        System.out.println("Bienvenue dans le jeu du Sudoku");
        System.out.println("Vous allez jouer contre l'ordinateur");
        System.out.println("Saisir nbTrous compris entre 0 et 81 : ");
        int nbtrou = saisirEntierMinMax(0, 81);
        System.out.println("Voulez vous remplir la grille de l'ordinateur : OUI via le chat (1) / OUI via fichier (2) / NON (génération aléatoire) (3)");
        int choix = saisirEntierMinMax(1, 3);
        initGrilleComplete(gSecret);
        initGrilleIncomplete(nbtrou, gSecret, gHumain);
        if (choix == 1) { // SI choix = OUI via le chat (1)
            saisirGrilleIncomplete(nbtrou, gOrdi);
        } else if (choix == 2) { // SI choix = OUI via fichier (2)
            System.out.println("Saisir nom fichier (il doit être dans le même répertoire que Sudoku):");
            saisirGrilleIncompleteFichier(nbtrou, gOrdi, saisirChaine());
        } else { // SI choix = NON (3)
            initGrilleIncomplete(nbtrou, gSecret, gOrdi);
        }
        System.out.println("\n" + "Grille Ordi :");
        afficheGrille(3, gOrdi);
        initPossibles(gOrdi, valPossibles, nbValPoss);
        return nbtrou;
    }

    public static int tourHumain(int[][] gSecret, int[][] gHumain) {
        int penalite = 0;
        int valeur;

        afficheGrille(3, gHumain);
        System.out.println("\n" + "Saisir le numéro de la ligne que vous souhaitez remplir :");
        int ligne = saisirEntierMinMax(1, 9);
        System.out.println("Saisir le numéro de la colonne que vous souhaitez remplir :");
        int colonne = saisirEntierMinMax(1, 9);
        while (gHumain[ligne - 1][colonne - 1] != 0) {
            System.out.println("\n" + "La case choisie est déjà remplie, veuillez réessayer :");
            System.out.println("Saisir le numéro de la ligne que vous souhaitez remplir :");
            ligne = saisirEntierMinMax(1, 9);
            System.out.println("Saisir le numéro de la colonne que vous souhaitez remplir :");
            colonne = saisirEntierMinMax(1, 9);
        }
        System.out.println("Vous êtes sur un trou, faite une proposition ou écrire joker :");
        String s = saisirChaine();
        try{
            if(s.equals("Joker") || s.equals("joker")){
                penalite++;
                gHumain[ligne - 1][colonne - 1] = gSecret[ligne - 1][colonne - 1];
            }
            else {
                valeur = Integer.parseInt(s);
                while (valeur != gSecret[ligne - 1][colonne - 1]) {
                    System.out.println("\n" + "Faux ! Réessayer une valeur :");
                    valeur = saisirEntierMinMax(1, 9);
                    penalite++;
                }
                gHumain[ligne - 1][colonne - 1] = valeur;
            }
        }
        catch(NumberFormatException ex){
            System.err.println("Ce n'est pas un entier valide ou Joker");
            return saisirEntier();
        }
        return penalite;
    }

    public static int[] chercheTrou(int[][] gOrdi, int[][] nbValPoss) {
        SudokuBase.gOrdi = gOrdi;
        int[] matricetrouij = new int[3];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (nbValPoss[i][j] == 1) {
                    matricetrouij[0] = i;
                    matricetrouij[1] = j;
                    matricetrouij[2] = 0;
                    return matricetrouij;
                }
            }
        }

        for (int i = 0; i < 9; i++) { // JOKER --> 2
            for (int j = 0; j < 9; j++) {
                if (nbValPoss[i][j] > 1) {
                    matricetrouij[0] = i;
                    matricetrouij[1] = j;
                    matricetrouij[2] = 2;
                    return matricetrouij;
                }
            }
        }
        return matricetrouij;
        // 0 -> i 1
        // 1 -> j 2
        // 2 -> n 0
    }

    // .........................................................................

    public static int tourOrdinateur(int[][] gOrdi, boolean[][][] valPossibles, int[][] nbValPoss) {
        int penalite = 0;
        int[] trou;
        trou = chercheTrou(gOrdi, nbValPoss);
        if (trou[2] == 0) {
            for (int n = 1; n < 10; n++) {
                if (valPossibles[trou[0]][trou[1]][n]) {
                    gOrdi[trou[0]][trou[1]] = n;
                }
            }
            suppValPoss(gOrdi, trou[0], trou[1], valPossibles, nbValPoss);
        }

        if (trou[2] == 2) {
            System.out.println("\n" + "\n" + "L'ordinateur donne sa langue au chat." + "\n");
            afficheGrille(3, gOrdi);
            System.out.println("Saisissez la valeur pour le trou " + (trou[0] + 1) + "," + (trou[1] + 1) + " :");
            gOrdi[trou[0]][trou[1]] = saisirEntierMinMax(1, 9);
            for (int n = 0; n < 10; n++) {
                valPossibles[trou[0]][trou[1]][n] = false;
            }
            suppValPoss(gOrdi, trou[0], trou[1], valPossibles, nbValPoss);
            penalite++;
        }
        return penalite;
        // 0:Trou Evident 2:Aide Joueur 3:FIN
    }

    // .........................................................................

    public static void partie() {
        int penaliteHumain = 0;
        int penaliteOrdi = 0;
        boolean[][][] valPossibles = new boolean[9][9][10];
        int[][] nbValPoss = new int[9][9];
        int[][] grilleIncompleteChoisieParOrdi = new int[9][9];
        int[][] grilleCompleteChoisieParOrdi = new int[9][9];
        int[][] grilleIncompleteChoisieParHumain = new int[9][9];

        int nbtrou = initPartie(grilleCompleteChoisieParOrdi, grilleIncompleteChoisieParOrdi,
                grilleIncompleteChoisieParHumain, valPossibles, nbValPoss);

        while (nbtrou != 0) { // PARTIE
            // HUMAIN
            System.out.println("\n" + "Votre Grille :");
            penaliteHumain += tourHumain(grilleCompleteChoisieParOrdi, grilleIncompleteChoisieParOrdi);
            System.out.println("\n" + "Pénalité(s) Joueur :" + penaliteHumain);

            // ORDI
            penaliteOrdi += tourOrdinateur(grilleIncompleteChoisieParHumain, valPossibles, nbValPoss);
            System.out.println("Pénalité(s) Ordi:" + penaliteOrdi);
            System.out.println("\n" + "L'ordi a joué, voici sa grille :");
            afficheGrille(3, grilleIncompleteChoisieParHumain);

            nbtrou--;
        }

        System.out.println("La partie est finie");
        if (penaliteHumain < penaliteOrdi) { // FIN CHOISIE GAGNANT
            System.out.println("Vous avez gagné !");
            System.out.println("Vos penalites : " + penaliteHumain);
            System.out.println("Penalites de l'ordinateur :" + penaliteOrdi);
        }
        if (penaliteHumain > penaliteOrdi) {
            System.out.println("L'ordinateur a gagné !");
            System.out.println("Vos penalites : " + penaliteHumain);
            System.out.println("Penalite de l'ordinateur :" + penaliteOrdi);
        }
        if (penaliteHumain == penaliteOrdi) {
            System.out.println("Égalité !");
            System.out.println("Vos penalites : " + penaliteHumain);
            System.out.println("Penalites de l'ordinateur : " + penaliteOrdi);
        }
    }

    public static void rotation90deg(int[][] gOrdi) { // EXTENSION 4
        int taille = gOrdi.length;
        for (int i = 0; i < taille / 2; i++) {
            for (int j = i; j < taille - 1 - i; j++) {
                int temp = gOrdi[i][j];
                gOrdi[i][j] = gOrdi[j][taille - 1 - i];
                gOrdi[j][taille - 1 - i] = gOrdi[taille - 1 - i][taille - 1 - j];
                gOrdi[taille - 1 - i][taille - 1 - j] = gOrdi[taille - 1 - j][i];
                gOrdi[taille - 1 - j][i] = temp;
            }
        }
    }

    public static void symetrieHorizontale(int[][] gOrdi) { // EXTENSION 4
        int taille = gOrdi.length;
        int axeSymetrie = taille / 2;
        for (int i = 0; i < taille / 2; i++) {
            for (int j = 0; j < taille; j++) {
                if (i < axeSymetrie) {
                    int temp = gOrdi[i][j];
                    gOrdi[i][j] = gOrdi[taille - 1 - i][j];
                    gOrdi[taille - 1 - i][j] = temp;
                }
            }
        }
    }

    public static void symetrieDiagonale(int[][] gOrdi) { // EXTENSION 4
        int taille = gOrdi.length;
        for (int i = 0; i < taille; i++) {
            for (int j = i + 1; j < taille; j++) {
                int temp = gOrdi[i][j];
                gOrdi[i][j] = gOrdi[j][i];
                gOrdi[j][i] = temp;
            }
        }
    }

    public static void echangeLignes(int[][] gOrdi, int ligne1, int ligne2) { // EXTENSION 4
        ligne1 -= 1;
        ligne2 -= 1;
        int carreLigne1 = ligne1 / 3;
        int carreLigne2 = ligne2 / 3;

        while (carreLigne1 != carreLigne2 || (ligne1 - ligne2) > 2 || (ligne1 - ligne2) < -2 || ligne1 == ligne2) {
            ligne1 = randomMinMax(0, 8);
            ligne2 = randomMinMax(0, 8);
            carreLigne1 = ligne1 / 3;
            carreLigne2 = ligne2 / 3;
        }
        for (int i = 0; i < 9; i++) {
            int temp = gOrdi[ligne1][i];
            gOrdi[ligne1][i] = gOrdi[ligne2][i];
            gOrdi[ligne2][i] = temp;
        }
    }

    public static int randomMinMax(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }

    public static int saisirEntier() {
        String s = saisirChaine();
        int lu;
        try {
            lu = Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            System.err.println("Ce n'est pas un entier valide");
            return saisirEntier();
        }
        return lu;
    }

    public static String saisirChaine() {
        Scanner clavier = new Scanner(System.in);
        return clavier.nextLine();
    }

    public static void main(String[] args) {
        partie();
    }
}
// fin SudokuBase