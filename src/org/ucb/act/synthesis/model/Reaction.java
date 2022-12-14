package org.ucb.act.synthesis.model;

import java.util.Set;

/**
 * Immutable class describing a chemical reaction
 * 
 * @author J. Christopher Anderson
 * @author Seohyun Park seohyunpark3411
 */
public class Reaction {
    private final Long id;
    private final String reaction;
    private final Set<Chemical> substrates;
    private final Set<Chemical> products;

    public Reaction(Long id, Set<Chemical> substrates, Set<Chemical> products) {
        this.id = id;
        this.reaction = null;
        this.substrates = substrates;
        this.products = products;
    }
    public Reaction(String reaction, Set<Chemical> substrates, Set<Chemical> products) {
        this.id = null;
        this.reaction = reaction;
        this.substrates = substrates;
        this.products = products;
    }

    public String getReaction() { return reaction; }

    public Long getId() {
        return id;
    }

    public Set<Chemical> getSubstrates() {
        return substrates;
    }

    public Set<Chemical> getProducts() {
        return products;
    }
}
