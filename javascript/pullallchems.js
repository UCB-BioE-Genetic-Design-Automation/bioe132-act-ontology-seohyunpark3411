//mongo --quiet synapse < pullallchems.js > allchemspulled.txt

var myCursor = db.chemicals.find();

var count = 0;

print("chemid" + "\t" + "name" + "\t" + "smiles" + "\t" + "inchi");

while (myCursor.hasNext()) {
    var data = myCursor.next();
    var id = data['_id'];
    var smiles = data.SMILES;
    var inchi = data.InChI;

    var name = null;
    var names = data.names;
    
    try {
        name = names.brenda[0];
    } catch (err1) {
    }

    if (!name) {
        try {
            name = names.synonyms[0];
        } catch (err2) {
        }
    }

    if (!name) {
        try {
            name = names.pubchem[0];
        } catch (err3) {
        }
    }
    

    print(id + "\t" + name + "\t" + smiles + "\t" + inchi);
}
