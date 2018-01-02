package org.ucb.act.cleanup;

import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.ucb.act.utils.FileUtils;

/**
 *
 * @author J. Christopher Anderson
 */
public class CleanupFlatfiles2 {
    private final static String chempath = "/Users/jca20n/data/2015-10-01-new_metacyc_with_more_directions/allchemspulled.txt";
    private final static String rxnpath = "/Users/jca20n/data/2015-10-01-new_metacyc_with_more_directions/allrxnspulled.txt";
    
    Map<Long, String> chemIdToInchi;
    Map<Long, String> chemIdToName;
    Map<Long, String> chemIdToSmiles;
    
    Set<Long> validatedChems;
    
    Writer dudRxnWriter;
    Writer goodRxnWriter;
    
    Writer dudChemWriter;
    
    public void initiate() throws Exception {
        
        File file1 = new File("dud_reactions2.txt");
        dudRxnWriter = new FileWriter(file1);
        dudRxnWriter.write("rxnid\tecnum\tsubstrates\tproducts\n");
        
        File file2 = new File("good_reactions2.txt");
        goodRxnWriter = new FileWriter(file2);
        goodRxnWriter.write("rxnid\tecnum\tsubstrates\tproducts\n");
        
        File file3 = new File("dud_chemicals2.txt");
        dudChemWriter = new FileWriter(file3);
        
        //Put all the inchis into a Map for the chems
        chemIdToInchi = new HashMap<>();
        chemIdToName = new HashMap<>();
        chemIdToSmiles = new HashMap<>();
        
        validatedChems = new HashSet<>();
        
        String chemdata = FileUtils.readFile(chempath);
        String[] lines = chemdata.trim().split("\\r|\\r?\\n");
        for(int i=1; i<lines.length; i++) {
            String aline = lines[i];
            String[] tabs = aline.trim().split("\t");
            Long id = Long.parseLong(tabs[0]);
            String inchi = tabs[3];
            String smiles = tabs[2];
            String name = tabs[1];
            chemIdToInchi.put(id, inchi);
            chemIdToName.put(id, name);
            chemIdToSmiles.put(id, smiles);
        }
    }
    
    public void run() throws Exception {
        //Read in all the reactions
        String rxndata = FileUtils.readFile(rxnpath);
        String[] lines = rxndata.trim().split("\\r|\\r?\\n");
        for(int i=1; i<lines.length; i++) {
            String aline = lines[i];
            try {
                String[] tabs = aline.trim().split("\t");
                Long id = Long.parseLong(tabs[0]);
                String substrates = tabs[2];
                String products = tabs[3];
                handleChemIdList(substrates);
                handleChemIdList(products);
                
                goodRxnWriter.write(aline + "\n");
                
            } catch (Exception err) {
                dudRxnWriter.write(aline + "\n");
            }
        }
        
        goodRxnWriter.close();
        dudRxnWriter.close();
        dudChemWriter.close();
        
        //for each validated chemical, write it to file
        File cfile = new File("good_chems2.txt");
        Writer chemFilewriter = new FileWriter(cfile);
        chemFilewriter.write("id\tname\tinchi\n");
                
        for(Long id : validatedChems) {
            String name = chemIdToName.get(id);
            String inchi = chemIdToInchi.get(id);
            String smiles = chemIdToSmiles.get(id);
            chemFilewriter.write(id + "\t" + name + "\t" + inchi + "\t" + smiles + "\n");
        }
        chemFilewriter.close();
    }
    
    /**
     * Helper method for handling the chemical references
     * 
     * @param chemidstring
     * @return
     * @throws Exception 
     */
    private void handleChemIdList(String chemidstring) throws Exception {
        String[] stringids = chemidstring.trim().split("\\s");
        for(int i=0; i<stringids.length; i++) {
            Long id = Long.parseLong(stringids[i]);
            validatedChems.add(id);
        }
    }
    
    public static void main(String[] args) throws Exception {
        CleanupFlatfiles2 cleaner = new CleanupFlatfiles2();
        cleaner.initiate();
        cleaner.run();
    }
}
