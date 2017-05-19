package com.filiereticsa.arc.augmentepf.localization;

import android.util.Pair;

/**
 * Created by anthonyprudhomme on 11/10/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */

public class UserIndoorLocationCandidate {

    Pair<Integer, Integer> indexPath;
    double weight = 0;
    public UserIndoorLocationCandidate(Pair<Integer, Integer> indexPath, double weight) {
        this.indexPath = indexPath;
        this.weight = weight;
    }
}
