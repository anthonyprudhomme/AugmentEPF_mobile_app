package com.filiereticsa.arc.augmentepf.localization.guidage;

import android.util.Pair;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.activities.AugmentEPFApplication;

/**
 * Created by ARC© Team for AugmentEPF project on 07/06/2017.
 */

public class TrajectorySegment {

    private Pair<Integer, Integer> newDirectionCoordinates;
    private String directionInstruction;
    private String code;

    public TrajectorySegment(String code) {
        this.code = code;

        int whichDirection = 0;

        // We have different directions (choices) with this code
        switch (code) {
            // Turn Left
            case "0140":
                this.newDirectionCoordinates = new Pair<>(-1,0);
                whichDirection = 1;
                break;
            case "0320":
                this.newDirectionCoordinates = new Pair<>(1,0);
                whichDirection = 1;
                break;
            case "2001":
                this.newDirectionCoordinates = new Pair<>(0,-1);
                whichDirection = 1;
                break;
            case "4003":
                this.newDirectionCoordinates = new Pair<>(0,1);
                whichDirection = 1;
                break;
            case "0141":
                this.newDirectionCoordinates = new Pair<>(-1,-1);
                whichDirection = 1;
                break;
            case "0323":
                this.newDirectionCoordinates = new Pair<>(1,1);
                whichDirection = 1;
                break;
            case "2021":
                this.newDirectionCoordinates = new Pair<>(1,-1);
                whichDirection = 1;
                break;
            case "4043":
                this.newDirectionCoordinates = new Pair<>(-1,1);
                whichDirection = 1;
                break;

            // Turn Right
            case "0120":
                this.newDirectionCoordinates = new Pair<>(-1,0);
                whichDirection = 2;
                break;
            case "0340":
                this.newDirectionCoordinates = new Pair<>(1,0);
                whichDirection = 2;
                break;
            case "2003":
                this.newDirectionCoordinates = new Pair<>(0,-1);
                whichDirection = 2;
                break;
            case "4001":
                this.newDirectionCoordinates = new Pair<>(0,1);
                whichDirection = 2;
                break;
            case "0121":
                this.newDirectionCoordinates = new Pair<>(-1,-1);
                whichDirection = 2;
                break;
            case "0343":
                this.newDirectionCoordinates = new Pair<>(1,1);
                whichDirection = 2;
                break;
            case "2023":
                this.newDirectionCoordinates = new Pair<>(1,-1);
                whichDirection = 2;
                break;
            case "4041":
                this.newDirectionCoordinates = new Pair<>(-1,1);
                whichDirection = 2;
                break;

            // Straight on
            case "0101": // top
                this.newDirectionCoordinates = new Pair<>(0,-1);
                whichDirection = 3;
                break;
            case "0303": // bottom
                this.newDirectionCoordinates = new Pair<>(0,1);
                whichDirection = 3;
                break;
            case "2020": // right
                this.newDirectionCoordinates = new Pair<>(1,0);
                whichDirection = 3;
                break;
            case "2101": // once user have turned left
                this.newDirectionCoordinates = new Pair<>(0,-1);
                whichDirection = 3;
                break;
            case "2120": // once user have turned right
                this.newDirectionCoordinates = new Pair<>(1,0);
                whichDirection = 3;
                break;
            case "2303": // once user have turned right
                this.newDirectionCoordinates = new Pair<>(0,1);
                whichDirection = 3;
                break;
            case "2320": // once user have turned left
                this.newDirectionCoordinates = new Pair<>(1,0);
                whichDirection = 3;
                break;
            case "4040": // left
                this.newDirectionCoordinates = new Pair<>(-1,0);
                whichDirection = 3;
                break;
            case "4101": // once user have turned right
                this.newDirectionCoordinates = new Pair<>(0,-1);
                whichDirection = 3;
                break;
            case "4140": // once user have turned right
                this.newDirectionCoordinates = new Pair<>(-1,0);
                whichDirection = 3;
                break;
            case "4303": // once user have turned right
                this.newDirectionCoordinates = new Pair<>(0,1);
                whichDirection = 3;
                break;
            case "4340": // once user have turned right
                this.newDirectionCoordinates = new Pair<>(-1,0);
                whichDirection = 3;
                break;
        }
        switch (whichDirection) {
            case 0:
                this.directionInstruction =
                    AugmentEPFApplication.getAppContext().getString(R.string.guidanceProblem);
                break;
            case 1:
                this.directionInstruction =
                        AugmentEPFApplication.getAppContext().getString(R.string.guidanceTurnLeft);
                break;
            case 2:
                this.directionInstruction =
                        AugmentEPFApplication.getAppContext().getString(R.string.guidanceTurnRight);
                break;
            case 3:
                this.directionInstruction =
                        AugmentEPFApplication.getAppContext().getString(R.string.guidanceStraightAhead);
                break;
            default:
                this.directionInstruction =
                        AugmentEPFApplication.getAppContext().getString(R.string.guidanceProblem);
                break;
        }
    }

    public Pair<Integer, Integer> getNewDirectionCoordinates() {
        return newDirectionCoordinates;
    }

    public void setNewDirectionCoordinates(Pair<Integer, Integer> newDirectionCoordinates) {
        this.newDirectionCoordinates = newDirectionCoordinates;
    }

    public String getDirectionInstruction() {
        return directionInstruction;
    }

    public void setDirectionInstruction(String directionInstruction) {
        this.directionInstruction = directionInstruction;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
