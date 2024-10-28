package org.iut.mastermind.domain.proposition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Collections.unmodifiableList;

public class Reponse {
    private final String motSecret;
    private final List<Lettre> resultat = new ArrayList<>();

    public Reponse(String mot) {
        this.motSecret = mot;
    }

    public Lettre lettre(int position) {
        return resultat.get(position);
    }

    // on construit le résultat en analysant chaque lettre du mot proposé
    public List<Lettre> compare(String essai) {
        resultat.clear();
        IntStream.range(0, essai.length())
                .mapToObj(i -> evaluationCaractere(essai.charAt(i), i))
                .forEach(resultat::add);
        return resultat;
    }

    // si toutes les lettres sont placées
    public boolean lettresToutesPlacees() {
        return resultat.stream().allMatch(lettre -> lettre == Lettre.PLACEE);
    }

    public List<Lettre> lettresResultat() {
        return unmodifiableList(resultat);
    }

    private Lettre evaluationCaractere(char carCourant, int position) {
        return (position >= motSecret.length() || !estPresent(carCourant)) ? Lettre.INCORRECTE :
                (motSecret.charAt(position) == carCourant ? Lettre.PLACEE : Lettre.NON_PLACEE);
    }

    // le caractère est présent dans le mot secret
    private boolean estPresent(char carCourant) {
        return motSecret.chars().anyMatch(c -> c == carCourant);
    }
}
