//mongo --quiet synapse < pullallrxns.js > allrxnspulled.txt

var myCursor = db.actfamilies.find();

var tosscount = 0;

print("rxnid" + "\t" + "ecnum" + "\t" + "substrates" + "\t" + "products");

while (myCursor.hasNext()) {
    var data = myCursor.next();
    
    //Check if metacyc
    var src = data.datasource;
    if(src != "METACYC") {
        tosscount++;
        continue;
    }
    
    var id = data['_id'];
    var ecnum = data.ecnum;
    
    var msg = "";
    msg += id;
    msg += "\t";
    msg += ecnum;
    msg += "\t";
    
    for(var i = 0; i < data.enz_summary.substrates.length; i++) {
        var jason = data.enz_summary.substrates[i];
        var chemid = jason.pubchem;
        var text = "" + chemid;
        msg += text;
        msg += " ";
    }
    
    msg += "\t";

    for (var i = 0; i < data.enz_summary.products.length; i++) {
        var jason = data.enz_summary.products[i];
        var chemid = jason.pubchem;
        var text = "" + chemid;
        msg += text;
        msg += " ";
    }
   
    print (msg);
    
//    count++;
//    if(count > 100) {
//        break;
//    }
}

//print("tosscount: " + tosscount);
