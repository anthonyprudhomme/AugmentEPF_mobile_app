package com.filiereticsa.arc.augmentepf.models;

import java.util.ArrayList;

/**
 * Created by anthony on 07/05/2017.
 */

public class User {

    private String name;
    private String login;
    private String password;
    private SpecificAttribute specificAttribute;
    private boolean isAnonymous;
    private ArrayList<Path> plannedPaths;
    private ArrayList<Path> pathsHistory;

    public User(String name, String login, String password, SpecificAttribute specificAttribute, boolean isAnonymous) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.specificAttribute = specificAttribute;
        this.isAnonymous = isAnonymous;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SpecificAttribute getSpecificAttribute() {
        return specificAttribute;
    }

    public void setSpecificAttribute(SpecificAttribute specificAttribute) {
        this.specificAttribute = specificAttribute;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public ArrayList<Path> getPlannedPaths() {
        return plannedPaths;
    }

    public void setPlannedPaths(ArrayList<Path> plannedPathes) {
        this.plannedPaths = plannedPathes;
    }

    public ArrayList<Path> getPathsHistory() {
        return pathsHistory;
    }

    public void setPathsHistory(ArrayList<Path> pathesHistory) {
        this.pathsHistory = pathesHistory;
    }

}
