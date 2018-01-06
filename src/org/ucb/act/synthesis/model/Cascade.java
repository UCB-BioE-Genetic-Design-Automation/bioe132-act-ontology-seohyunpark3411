/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ucb.act.synthesis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author J. Christopher Anderson
 */
public class Cascade {
    private final Long id;
    private final Set<Cascade> children;

    public Cascade(Long id) {
        this.id = id;
        this.children = new HashSet<>();
    }
    
    public void addChild(Cascade child) {
        children.add(child);
    }

    public Long getId() {
        return id;
    }

    public Set<Cascade> getChildren() {
        return children;
    }
}
