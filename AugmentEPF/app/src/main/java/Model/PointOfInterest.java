package Model;

import java.util.ArrayList;

/**
 * Created by anthony on 07/05/2017.
 */

public class PointOfInterest {

    private static ArrayList<PointOfInterest> pointOfInterests;
    private String nom;
    private Position position;
    private String information;

    public PointOfInterest(String nom, Position position, String information) {
        this.nom = nom;
        this.position = position;
        this.information = information;
    }

    public static ArrayList<PointOfInterest> getPointOfInterests() {
        return pointOfInterests;
    }

    public static void setPointOfInterests(ArrayList<PointOfInterest> pointOfInterests) {
        PointOfInterest.pointOfInterests = pointOfInterests;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }
}
