package org.ucb.act.synthesis;

import java.util.HashSet;
import java.util.Set;
import org.ucb.act.synthesis.model.Chemical;
import org.ucb.act.synthesis.model.Reaction2;

/**
 *
 * @author J. Christopher Anderson
 */
public class Cascade4 {
    private final Chemical product;  //This Cascade represents all routes to this chemical
    private final Set<Reaction2> rxnsThatFormPdt= new HashSet<>();

    public Cascade4(Chemical product) {
        this.product = product;
    }

    public Chemical getProduct() {
        return product;
    }

    public Set<Reaction2> getRxnsThatFormPdt() {
        return rxnsThatFormPdt;
    }
    
    void addReaction(Reaction2 rxn) {
        rxnsThatFormPdt.add(rxn);
    }
}
