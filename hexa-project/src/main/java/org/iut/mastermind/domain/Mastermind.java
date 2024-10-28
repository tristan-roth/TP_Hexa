package org.iut.mastermind.domain;

import org.iut.mastermind.domain.partie.*;
import org.iut.mastermind.domain.proposition.*;
import org.iut.mastermind.domain.tirage.*;

import java.util.Optional;

public class Mastermind {
    private final PartieRepository partieRepository;
    private final ServiceTirageMot serviceTirageMot;

    public Mastermind(PartieRepository pr, MotsRepository mr, ServiceNombreAleatoire na) {
        this.partieRepository = pr;
        this.serviceTirageMot = new ServiceTirageMot(mr, na);
    }

    // on récupère éventuellement la partie enregistrée pour le joueur
    // si il y a une partie en cours, on renvoie false (pas de nouvelle partie)
    // sinon on utilise le service de tirage aléatoire pour obtenir un mot
    // et on initialise une nouvelle partie et on la stocke
    public boolean nouvellePartie(Joueur joueur) {
        return partieRepository.getPartieEnregistree(joueur)
                .filter(partie -> !partie.isTerminee())
                .map(partie -> false)
                .orElseGet(() -> {
                    String mot = serviceTirageMot.tirageMotAleatoire();
                    Partie nouvellePartie = Partie.create(joueur, mot);
                    partieRepository.create(nouvellePartie);
                    return true;
                });
    }

    // on récupère éventuellement la partie enregistrée pour le joueur
    // si la partie n'est pas une partie en cours, on renvoie une erreur
    // sinon on retourne le resultat du mot proposé
    public ResultatPartie evaluation(Joueur joueur, String motPropose) {
        return partieRepository.getPartieEnregistree(joueur)
                .filter(partie -> !partie.isTerminee())
                .map(partie -> calculeResultat(partie, motPropose))
                .orElse(ResultatPartie.ERROR);
    }

    // on évalue le résultat du mot proposé pour le tour de jeu
    // on met à jour la bd pour la partie
    // on retourne le résulat de la partie
    private ResultatPartie calculeResultat(Partie partie, String motPropose) {
        if (partie.isTerminee()) {
            return ResultatPartie.create(new Reponse(partie.getMot()), true);
        }

        Object[] resultatTour = partie.tourDeJeu(motPropose);

        Partie nouvellePartie = (Partie) resultatTour[0];
        Reponse reponse = (Reponse) resultatTour[1];

        partieRepository.update(nouvellePartie);

        return ResultatPartie.create(reponse, nouvellePartie.isTerminee());
    }

    // si la partie en cours est vide, on renvoie false
    // sinon, on évalue si la partie est terminée
    private boolean isJeuEnCours(Optional<Partie> partieEnCours) {
        return partieEnCours.isPresent() && !partieEnCours.get().isTerminee();
    }
}
