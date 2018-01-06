package org.ucb.act.synthesis;

import java.util.HashSet;
import java.util.Set;
import org.ucb.act.synthesis.model.Chemical;
import org.ucb.act.synthesis.model.Reaction;

/**
 *
 * @author J. Christopher Anderson
 */
public class Cascade {
    private final Chemical product;  //This Cascade represents all routes to this chemical
    private final Set<Reaction> rxnsThatFormPdt= new HashSet<>();

    public Cascade(Chemical product) {
        this.product = product;
    }

    public Chemical getProduct() {
        return product;
    }

    public Set<Reaction> getRxnsThatFormPdt() {
        return rxnsThatFormPdt;
    }
    
    void addReaction(Reaction rxn) {
        rxnsThatFormPdt.add(rxn);
    }
}
