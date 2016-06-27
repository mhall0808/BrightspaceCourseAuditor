/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coursefilesauditor;

import ContentType.HTMLPages;
import ContentType.Quizzes;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author hallm8
 */
public class Manifest {

    String className = new String();
    String OrgUnitID = new String();
    String manifestLocation = new String();
    int totalBroken = 0;

    /**
     *
     */
    public Manifest() {

    }

    /**
     *
     * @param newLocation
     */
    public Manifest(String newLocation) {
        manifestLocation = newLocation;
    }

    /**
     *
     */
    public void gatherCSV() {
        // First, we have to crack open the DOM parser and pull out some info.
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        try {
            
            // Create a database based on the initialized Firebase App
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            // Reference to the root of the database created.
            DatabaseReference ref = database.getReference();

            DatabaseReference rootRef = ref.child("Mark's Tool");
            //DatabaseReference testRef = dataRef.child("OU");
            //dataRef.setValue("Test completed successfully.");
            //testRef.setValue("oops.  This one.")
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(manifestLocation + "\\imsmanifest.xml");

            // Normalize the document.  
            doc.getDocumentElement().normalize();
            // Find the root element.  Necessary to find the Course ID.
            Element root = (Element) doc.getElementsByTagName("manifest").item(0);
            OrgUnitID = root.getAttribute("identifier").replace("D2L_", "");
            System.out.println("OU Found! : " + OrgUnitID);
            DatabaseReference classRef = rootRef.child(OrgUnitID);
            DatabaseReference orgUnit = classRef.child("Link");
            orgUnit.setValue("https://byui.brightspace.com/d2l/home/" + OrgUnitID);

            // Create 2 stems:  Content and Quizzes
            // If there is nothing to change, I will push nothing, keeping
            // the value:  Nothing is here.
            DatabaseReference quizzes = classRef.child("Quizzes");
            quizzes.setValue("There is nothing here!");
            DatabaseReference contentRef = classRef.child("Content Pages");
            contentRef.setValue("There is nothing here!");

            // Well, that was all the easy stuff, unfortunately.  So here we go!
            NodeList resources = doc.getElementsByTagName("resource");
            NodeList modules = doc.getElementsByTagName("item");

            for (int i = 0; i < resources.getLength(); i++) {
                Element resource = (Element) resources.item(i);
                switch (resource.getAttribute("d2l_2p0:material_type")) {
                    case "content":
                    case "contentlink":
                        if (resource.getAttribute("href").contains("http:")
                                || resource.getAttribute("href").contains("https:")
                                || resource.getAttribute("href").contains("/d2l/common/dialogs/quickLink/quickLink.d2l?ou={orgUnitId}")) {
                        } /**
                         * For this, we are going to just go ahead and parse all
                         * html files listed. Narrowing this allows us to see
                         * each HTML node, which we will then link to and use.
                         */
                        else if (resource.getAttribute("href").contains(".html")) {

                            HTMLPages html = new HTMLPages(manifestLocation + "\\"
                                    + resource.getAttribute("href"), contentRef);
                            Element module = (Element) modules.item(i);
                            ArrayList<String> nNameAndLocation = new ArrayList<>();
                            nNameAndLocation = findModules(module, nNameAndLocation);
                            html.setNameAndLocation(nNameAndLocation);
                            html.setIdentifier(resource.getAttribute("identifier").replace("RES_CONTENT_", ""));
                            html.setOuID(OrgUnitID);
                            html.checkPage();

                        } else {
                            // Now that we know everything that our files do NOT contain, we
                            // can sort the ones that are probably problematic. 
                            totalBroken++;
                            System.out.println("Resource identifier: " + resource.getAttribute("identifier"));
                            Element module = (Element) modules.item(i);
                            ArrayList<String> nNameAndLocation = new ArrayList<>();
                            nNameAndLocation = findModules(module, nNameAndLocation);

                            DatabaseReference newRef = contentRef.getRef();
                            for (int j = nNameAndLocation.size() - 1; j >= 0; j--) {
                                // Firebase does not allow these symbols.  Huh.
                                String newName = nNameAndLocation.get(j).replace(".", " ");
                                newName = newName.replace("$", " ");
                                newName = newName.replace("[", " ");
                                newName = newName.replace("]", " ");

                                newRef = newRef.child(newName);
                            }
                            newRef.child("Type").setValue("Content");
                            newRef.child("Checked").setValue("False");

                        }
                        break;
                    case "d2lquiz":
                    case "d2lquestionlibrary":
                        Quizzes newQuiz = new Quizzes(manifestLocation + "\\"
                                + resource.getAttribute("href"), quizzes);
                        newQuiz.setIdentifier(resource.getAttribute(
                        resource.getAttribute("identifier").replace("RES_CONTENT_", "")));
                        newQuiz.gatherBroken();
                        break;
                }
            }

            System.out.println(totalBroken);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(Manifest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * FIND MODULES This is a recursive function that cycles through modules
     * until it reaches the root node. This is done to list the module location
     * of the links used.
     *
     * @return
     */
    private ArrayList<String> findModules(Node moduleNode, ArrayList<String> newList) {
        Element module = (Element) moduleNode;
        newList.add(module.getElementsByTagName("title").item(0).getTextContent());
        if (moduleNode.getParentNode().getNodeName().equals("item")) {
            newList = findModules(moduleNode.getParentNode(), newList);
        }
        return newList;
    }
}
