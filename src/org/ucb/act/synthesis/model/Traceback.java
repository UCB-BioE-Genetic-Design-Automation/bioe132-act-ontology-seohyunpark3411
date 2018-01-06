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
public class Traceback {
    private final Map<Long, Set<Reaction>> chemPdtToReactions;
    private final Map<Long, Integer> chemicalToShell;
    
    private final Map<Long, Cascade> chemIdToCascade;

    public Traceback(Map<Long, Set<Reaction>> chemPdtToReactions, Map<Long, Integer> chemicalToShell) {
        this.chemPdtToReactions = chemPdtToReactions;
        this.chemicalToShell = chemicalToShell;
        
        //Populate the cascades
        chemIdToCascade = new HashMap<>();
        
        //For each Reachable, compute the Cascade
        for(Long chemId : chemicalToShell.keySet()) {
            Cascade cascade = calculateCascade(chemId);
        }
    }
    
    private Cascade calculateCascade(Long chemId) {
        //If it has already been calculated, return it
        if(chemIdToCascade.containsKey(chemId)) {
            return chemIdToCascade.get(chemId);
        }

        //If it is level 0, return a cascade with no children
        if (chemicalToShell.get(chemId) == 0) {
            Cascade out = new Cascade(chemId);
            chemIdToCascade.put(chemId, out);
            return out;
        }
        
        //If it gets here it is a new chem which needs recursion
        Cascade recout = new Cascade(chemId);
        chemIdToCascade.put(chemId, recout);

        //Recursively calculate the child cascades
        Set<Reaction> children = chemPdtToReactions.get(chemId);
        for(Reaction childRxn : children) {
            for(Long childSub : childRxn.getSubstrates()) {
                Cascade childCascade = calculateCascade(childSub);
                recout.addChild(childCascade);
            }
        }
        
        return recout;
    }

    public Cascade getCascade(Long chemId) {
        return chemIdToCascade.get(chemId);
    }

    public Set<Long> getProductIds() {
        return chemPdtToReactions.keySet();
    }
    
    public Map<Long, Cascade> getCascades() {
        return chemIdToCascade;
    }
}
