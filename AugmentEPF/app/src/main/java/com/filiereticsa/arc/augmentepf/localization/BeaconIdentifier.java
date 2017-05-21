package com.filiereticsa.arc.augmentepf.localization;

/**
 * Created by anthonyprudhomme on 08/09/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */
public class BeaconIdentifier {

    private String uuid;
    private int major = -1;
    private int minor = -1;

    BeaconIdentifier(String uuid, int major, int minor) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
    }

    String getUuid() {
        return uuid;
    }

    int getMajor() {
        return major;
    }

    int getMinor() {
        return minor;
    }

}
