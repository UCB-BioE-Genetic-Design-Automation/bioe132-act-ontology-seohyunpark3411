package org.ucb.act.synthesis.model;

import java.util.Set;

/**
 *
 * @author J. Christopher Anderson
 */
public class Reaction {
    private final Long id;
    private final Set<Long> substrates;
    private final Set<Long> products;

    public Reaction(Long id, Set<Long> substrates, Set<Long> products) {
        this.id = id;
        this.substrates = substrates;
        this.products = products;
    }

    public Long getId() {
        return id;
    }

    public Set<Long> getSubstrates() {
        return substrates;
    }

    public Set<Long> getProducts() {
        return products;
    }
    
    
}
