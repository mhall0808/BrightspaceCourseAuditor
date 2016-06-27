/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ContentType;

import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;

/**
 *
 * @author hallm8
 */
public class CSV {

    protected String location;
    protected int numBroken;
    protected ArrayList<String> nameAndLocation;
    protected DatabaseReference ref;
    protected String identifier;
    protected String ouID;

    public CSV() {
        location = new String();
        numBroken = 0;
        nameAndLocation = new ArrayList<>();
        identifier = new String();
    }

    public CSV(String location) {
        this.location = location;
        numBroken = 0;
    }

    public CSV(String location, DatabaseReference ref) {
        this.location = location;
        this.ref = ref;
    }

    /**
     * Gather Broken: This gathers broken links. As a single function that works
     * cross-class, it allows the program to not have to differentiate between
     * one and the other, because that will happen at a different level.
     *
     * This allows for more densely packed code, and being able to do things
     * hopefully more efficiently.
     */
    public void gatherBroken() {

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getNumBroken() {
        return numBroken;
    }

    public void setNumBroken(int numBroken) {
        this.numBroken = numBroken;
    }

    public ArrayList<String> getNameAndLocation() {
        return nameAndLocation;
    }

    public void setNameAndLocation(ArrayList<String> nameAndLocation) {
        this.nameAndLocation = nameAndLocation;
    }

    public DatabaseReference getRef() {
        return ref;
    }

    public void setRef(DatabaseReference ref) {
        this.ref = ref;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOuID() {
        return ouID;
    }

    public void setOuID(String ouID) {
        this.ouID = ouID;
    }

}
