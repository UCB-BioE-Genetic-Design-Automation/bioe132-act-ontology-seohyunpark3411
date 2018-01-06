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
public class Cascade3 {
    Chemical chemical;  //This Cascade represents all routes to this chemical
    Set<Reaction2> childReactions;  //These are all Reactions that produce the chemical above
    
    public Map<Reaction2, Cascade> getChildren() {
        Map<Reaction2, Cascade> out = new HashMap<>();
        for(Reaction2 rxn : childReactions) {
            for(Chemical achem : rxn.getSubstrates()) {
                
            }
        }
        return out;
    }
}
