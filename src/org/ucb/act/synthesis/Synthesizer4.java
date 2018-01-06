package org.ucb.act.synthesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ucb.act.synthesis.model.Chemical;
import org.ucb.act.synthesis.model.Reaction2;
import org.ucb.act.utils.FileUtils;

/**
 *
 * @author J. Christopher Anderson
 */
public class Synthesizer4 {

    //Holds all the reactions and chemicals

    private final List<Reaction2> allReactions = new ArrayList<>();
    private final List<Chemical> allChemicals = new ArrayList<>();

    //Variables for holding state of the expansion
    private int currshell = 0;
    private final Map<Chemical, Integer> chemicalToShell = new HashMap<>();
    private final Map<Reaction2, Integer> reactionToShell = new HashMap<>();

    //Variables for holding cascades
    private final Map<Chemical, Cascade4> chemicalToCascade = new HashMap<>();

    public void populateChemicals(String chempath) throws Exception {
        //Read in all the chemicals
        String chemdata = FileUtils.readFile(chempath);
        chemdata = chemdata.replaceAll("\"", "");
        String[] lines = chemdata.trim().split("\\r|\\r?\\n");
        chemdata = null;

        //Each line of the file is a chemical after a header
        for (int i = 1; i < lines.length; i++) {
            String aline = lines[i];
            String[] tabs = aline.trim().split("\t");

            Long id = Long.parseLong(tabs[0]);
            String name = tabs[1];
            String inchi = tabs[2];
            String smiles = tabs[3];

            Chemical achem = new Chemical(id, inchi, smiles, name);
            allChemicals.add(achem);
        }

        System.out.println("done populating chemicals");
    }

    /**
     * Read in all the Reactions from file
     *
     * @throws Exception
     */
    public void populateReactions(String rxnpath) throws Exception {
        //First index all the chemId's for the Chemicals to hard-couple
        Map<Long, Chemical> idToChem = new HashMap<>();
        for (Chemical achem : allChemicals) {
            idToChem.put(achem.getId(), achem);
        }

        //Parse the Reactions from file and add to the arraylist
        String rxndata = FileUtils.readFile(rxnpath);
        rxndata = rxndata.replaceAll("\"", "");
        String[] lines = rxndata.trim().split("\\r|\\r?\\n");
        rxndata = null; //No longer need data, clear out memory

        //Iterate through lines, one line has one reaction after a header line
        for (int i = 1; i < lines.length; i++) {
            String aline = lines[i];
            try {
                //Pull out the data for one reaction
                String[] tabs = aline.trim().split("\t");
                Long id = Long.parseLong(tabs[0]);
                String substrates = tabs[2];
                String products = tabs[3];
                Set<Chemical> subs = handleChemIdList(substrates, idToChem);
                Set<Chemical> pdts = handleChemIdList(products, idToChem);

                //Instantiate the reaction, then add it
                Reaction2 rxn = new Reaction2(id, subs, pdts);
                allReactions.add(rxn);
            } catch (Exception err) {
                throw err;
            }
        }

        System.out.println("done populating reactions");
    }

    /**
     * Helper method for populateReactions
     *
     * Parses the serialized reference to the list of substrates or products
     *
     * @param chemidstring
     * @return
     * @throws Exception
     */
    private Set<Chemical> handleChemIdList(String chemidstring, Map<Long, Chemical> idToChem) {
        Set<Chemical> out = new HashSet<>();
        String[] stringids = chemidstring.trim().split("\\s");
        for (int i = 0; i < stringids.length; i++) {
            Long chemId = Long.parseLong(stringids[i]);
            Chemical achem = idToChem.get(chemId);
            out.add(achem);
        }
        return out;
    }

    public void populateNatives(String path) throws Exception {
        //Populate a HashSet to hold all native inchis
        Set<String> nativeInchis = new HashSet<>();

        //Read the inchis from file
        String nativedata = FileUtils.readFile(path);
        nativedata = nativedata.replaceAll("\"", "");
        String[] lines = nativedata.trim().split("\\r|\\r?\\n");
        nativedata = null;

        //Each line of the file is a chemical, add it to the list
        for (int i = 1; i < lines.length; i++) {
            String aline = lines[i];
            String[] tabs = aline.split("\t");
            String inchi = tabs[1];
            nativeInchis.add(inchi);
        }

        //For each chemical see if it is a native
        for (Chemical achem : allChemicals) {
            String inchi = achem.getInchi();
            if (nativeInchis.contains(inchi)) {
                chemicalToShell.put(achem, 0);
            }
        }

        System.out.println("done populating natives: " + path + ", have: " + chemicalToShell.size() + " reachables");
    }

    /**
     * One round of wavefront expansion. It iterates through all the reactions
     * and if the substrates for that reaction are enabled, then it enables the
     * products of the reaction. If any reactions are added during a round of
     * expansion, the method returns true.
     *
     * @return returns true if new reactions were added to the expansion
     * @throws Exception
     */
    public boolean ExpandOnce() throws Exception {
        //Increment the current shell
        currshell++;

        boolean isExpanded = false;

        //Iterate through reactions
        outer:
        for (Reaction2 rxn : allReactions) {
            //If the reaction has already been put in the expansion, skip this reaction
            if (reactionToShell.containsKey(rxn)) {
                continue;
            }

            //If any of the substates are not enabled, skip this reaction
            for (Chemical achem : rxn.getSubstrates()) {
                Integer shell = chemicalToShell.get(achem);
                if (shell == null) {
                    continue outer;
                }
            }

            //If gets this far, the Reaction is enabled and new, thus expansion will occur
            isExpanded = true;

            //Log the reaction into the expansion at the current shell
            reactionToShell.put(rxn, currshell);

            //For each product, enable it with current shell (if it isn't already)
            for (Chemical chemid : rxn.getProducts()) {
                Integer shell = chemicalToShell.get(chemid);
                if (shell == null) {
                    chemicalToShell.put(chemid, currshell);
                }
            }
        }

        System.out.println("Expanded shell: " + currshell + " result " + isExpanded + " with " + chemicalToShell.size() + " reachables");
        return isExpanded;
    }

    public void calculateCascades() throws Exception {
        //For each Reachable, put in a new Cascade
        for (Chemical achem : chemicalToShell.keySet()) {
            Cascade4 casc = new Cascade4(achem);
            chemicalToCascade.put(achem, casc);
        }

        //For each enabled reaction, log it in to cascades
        for (Reaction2 rxn : reactionToShell.keySet()) {
            for (Chemical apdt : rxn.getProducts()) {
                
                //Do not index reactions for natives so recursion will stop at them
                Integer shell = chemicalToShell.get(apdt);
                if (shell == 0) {
                    continue;
                }

                //Log the reaction into the chemical's cascade
                Cascade4 casc = chemicalToCascade.get(apdt);
                casc.addReaction(rxn);
            }
        }
    }

    public StringBuilder printoutCascade(Chemical achem, StringBuilder sb, int indents, Set<Chemical> visited) {
        visited.add(achem);
        
        //Put in a line of id, inchi, name into the stringbuilder for this chem
        Cascade4 casc = chemicalToCascade.get(achem);
        sb.append(">").append(indents).append("\t");
        sb.append(casc.getProduct().getId()).append("\t");
        sb.append(casc.getProduct().getName()).append("\t");
        sb.append(casc.getProduct().getInchi()).append("\n");
                
        for(Reaction2 rxn : casc.getRxnsThatFormPdt()) {
            sb.append(rxn.getId()).append("\t");
            sb.append(handleChems(rxn.getSubstrates())).append("\t-->\t");
            sb.append(handleChems(rxn.getProducts())).append("\n");
        }
        
        for(Reaction2 rxn : casc.getRxnsThatFormPdt()) {
            for(Chemical child : rxn.getSubstrates()) {
                //If the chemical has already been visited, ignore
                if(visited.contains(child)) {
                    continue;
                }
                
                printoutCascade(child,sb, indents+1, visited);
            }
            sb.append("\n");
        }
        
        return sb;
    }
    
    private String handleChems(Set<Chemical> chems) {
        String out = "";
        for(Chemical achem : chems) {
            out+=achem.getId();
            out+=" ";
        }
        return out.trim();
    }

    public void printReachables() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("id\tname\tinchi\tshell\n");
        for (Chemical achem : chemicalToShell.keySet()) {
            Long chemId = achem.getId();
            String inchi = achem.getInchi();
            String name = achem.getName();
            sb.append(chemId).append("\t").append(name).append("\t").append(inchi).append("\t").append(chemicalToShell.get(achem)).append("\n");
        }
        FileUtils.writeFile(sb.toString(), "metacyc_L2_reachables.txt");
    }

    public static void main(String[] args) throws Exception {
        Synthesizer4 synth = new Synthesizer4();

        //Populate the chemical and reaction data
        synth.populateChemicals("good_chems.txt");
        synth.populateReactions("good_reactions.txt");

        //To use non-inchi-validated version of MetaCyc, run these instead
//        synth.populateChemicals("metacyc_chemicals.txt");
//        synth.populateReactions("metacyc_reactions.txt");
        //Populate the bag of chemicals to consider as shell 0 "natives"
        synth.populateNatives("minimal_metabolites.txt");
        synth.populateNatives("universal_metabolites.txt");

        //Expand until exhausted
        while (synth.ExpandOnce()) {
        }

        //Print out the reachables and their shell
        synth.printReachables();

        //Calculate Cascades
        synth.calculateCascades();
        
        //Perform the traceback and output cascades for butanol
        StringBuilder sb = new StringBuilder();
        Chemical achem = synth.allChemicals.get(5133);
        StringBuilder cascade = synth.printoutCascade(achem, sb, 0, new HashSet<>());
        
        FileUtils.writeFile(cascade.toString(), "butanol_cascade.text");

        System.out.println("done");
    }
}
