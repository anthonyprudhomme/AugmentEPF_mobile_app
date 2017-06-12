package com.filiereticsa.arc.augmentepf.localization.guidage;

import android.util.Log;
import android.util.Pair;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.activities.AugmentEPFApplication;
import com.filiereticsa.arc.augmentepf.models.Position;

import java.util.ArrayList;

/**
 * Created by ARC© Team for AugmentEPF project on 07/06/2017.
 */

public class Guidance {

    public static final String TAG = "Ici (Guidance)";
    private ArrayList<Pair<Integer, Integer>> path;
    private ArrayList<TrajectorySegment> trajectory;
    private ArrayList<ArrayList<Pair<Integer, Integer>>> positionsSegment;

    private ArrayList<TrajectorySegment> computeAllTrajectory() {
        // To put all segments in the trajectory
        ArrayList<TrajectorySegment> trajectoryAllSeg = new ArrayList<>();

        // Size of the path for the limit of for below
        int pathSize = path.size();

        // Creation of variables
        int xBefore, xAfter, yBefore, yAfter;
        String code;

        // Start at 1 because we have i-1 & finish at (size-1) because we have i+1
        // i-1 & i+1 are for the displacements of the user
        for (int i = 1; i < pathSize - 1; i++) {
            // For x values
            xBefore = path.get(i).first - path.get(i - 1).first; // Before user displacement
            xAfter = path.get(i + 1).first - path.get(i).first; // The next event in the trajectory

            // For y values
            yBefore = path.get(i).second - path.get(i - 1).second;
            yAfter = path.get(i + 1).second - path.get(i).second;

            // Method which transform the displacement in an unique code to identify the displacement
            code = transformationOfValues(xBefore, xAfter, yBefore, yAfter);

            // Add the segment computed with the code computed
            trajectoryAllSeg.add(new TrajectorySegment(code));
            //Log.d(TAG, "trajectory: " + trajectory.get(i-1).getDirectionInstruction());
        }

        // Return the trajectory
        return trajectoryAllSeg;
    }

    /**
     * Try to find if the currentPosition exist in the list of positions of the segment (n° index)
     * @param currentPosition
     * @param index
     * @return
     */
    public int getCurrentSegment(Pair<Integer, Integer> currentPosition, int index) {
        boolean find = false;
        boolean endPath = false;

        // Compare all positions of the index segment with the current
        for (int i = 0; i < positionsSegment.get(index).size(); i++) {
            // Create a local position to test it without call it everytime
            Pair<Integer, Integer> testPosition = positionsSegment.get(index).get(i);

            // Compare the two positions
            if (testPosition.first == currentPosition.first
                    && testPosition.second == currentPosition.second) {
                // If it's the last position of the segment & the last segment => end of path
                if ((i == positionsSegment.get(index).size() - 1) &&
                        (index == positionsSegment.size() - 1)) {
                    endPath = true;
                }
                // If the position is the last position of the segment, pass at the next segment
                else if (i == positionsSegment.get(index).size() - 1) {
                    index = index + 1;
                }

                // The algorithm find a position
                find = true;
            }
        }

        // If the algorithm didn't find the position in the segment
        if (find == false) {
            index = -1; // To identify an error
        }
        if (endPath == true) {
            index = Integer.MAX_VALUE; // To identify
        }

        return index;
    }

    public ArrayList<TrajectorySegment> getTrajectory() {
        return trajectory;
    }

    public Guidance(ArrayList<Pair<Integer, Integer>> path) {
        this.path = path;

        pathAnalysis();

    }

    private void pathAnalysis() {
        // Creation and initialization of the trajectory which it's suppose to be fill of
        // TrajectorySegment in order to have a trajectory full
        ArrayList<TrajectorySegment> trajectoryAllSeg;

        // To compute all segments of the trajectory
        trajectoryAllSeg = computeAllTrajectory();

        // To avoid redundancy in the trajectory (for "straight ahead") and replace by the number of
        // meters which user must do
        trajectory = shortenTrajectory(trajectoryAllSeg);
    }

    /**
     * Set a new path, compute a new trajectory
     *
     * @param path
     */
    public void setPath(ArrayList<Pair<Integer, Integer>> path) {
        this.path = path;

        pathAnalysis();
    }

    private ArrayList<TrajectorySegment> shortenTrajectory(ArrayList<TrajectorySegment> trajectoryAllSeg) {
        positionsSegment = new ArrayList<>();
        ArrayList<TrajectorySegment> trajectory = new ArrayList<>();

        // Finish at (size-1) because we compare direction instructions of the current and next
        // segment
        for (int i = 0; i < trajectoryAllSeg.size() - 1; i++) {
            // To have independent counter of i
            // In order to have a list of positions for each segment
            Pair<Integer, Integer> absolutePosition;
            ArrayList<Pair<Integer, Integer>> allAbsolutePosition = new ArrayList<>();

            // One segment which is necessary to have the short trajectory
            TrajectorySegment trajSegment;

            // If there is a repetition
            if (trajectoryAllSeg.get(i).getDirectionInstruction().equals(trajectoryAllSeg.get(i + 1).getDirectionInstruction())) {
                // Initialization
                String oldCode, directionInstruction, distance;
                int numberOfIteration = 0;

                // While it's the same direction instruction
                while (trajectoryAllSeg.get(i).getDirectionInstruction().equals(trajectoryAllSeg.get(i + 1).getDirectionInstruction())
                        && (i < trajectoryAllSeg.size() - 2)) {
                    i = i + 1; // Increment i because we want restart after the repetition
                    numberOfIteration = numberOfIteration + 1; // To count the number of repetition

                    // To have all positions in the segment
                    absolutePosition = path.get(i);
                    allAbsolutePosition.add(absolutePosition);
                }

                // 1 instruction ~= 1m & 1 iteration = 1 instruction
                distance = AugmentEPFApplication.getAppContext().getString(R.string.guidanceFor)
                        + (numberOfIteration + 1)
                        + AugmentEPFApplication.getAppContext().getString(R.string.guidanceMeters);
                // Construct the new directionInstruction
                directionInstruction = trajectoryAllSeg.get(i).getDirectionInstruction() + distance;
                // Get the code of the direction instruction
                oldCode = trajectoryAllSeg.get(i).getCode();

                // Create a new segment with without the distance
                trajSegment = new TrajectorySegment(oldCode);
                // Set the new segment with the new direction instruction
                trajSegment.setDirectionInstruction(directionInstruction);
            } else { // If there isn't a repetition just put the normal segment
                String oldCode;

                // To have the only position of the segment
                absolutePosition = path.get(i);
                allAbsolutePosition.add(absolutePosition);

                oldCode = trajectoryAllSeg.get(i).getCode();
                trajSegment = new TrajectorySegment(oldCode);
            }
            // List of all positions with the number of the segment.
            // Form like this : (number of segment, list of all positions)
            positionsSegment.add(allAbsolutePosition);

            // Finally, we add the new segment in the short trajectory
            trajectory.add(trajSegment);
        }

        // To display all instructions of the trajectory
        /*for (int i = 0; i < trajectory.size(); i++) {
            Log.d(TAG, "trajectory: " + trajectory.get(i).getDirectionInstruction());
        }*/

        return trajectory;
    }

    private String transformationOfValues(int xBefore, int xAfter, int yBefore, int yAfter) {
        String finalCode, stringXBefore, stringXAfter, stringYBefore, stringYAfter;

        // Displacements on x axis
        switch (xBefore) {
            case -1:
                stringXBefore = "4";
                break;
            case 0:
                stringXBefore = "0";
                break;
            case 1:
                stringXBefore = "2";
                break;
            default:
                stringXBefore = "0";
                break;
        }
        switch (xAfter) {
            case -1:
                stringXAfter = "4";
                break;
            case 0:
                stringXAfter = "0";
                break;
            case 1:
                stringXAfter = "2";
                break;
            default:
                stringXAfter = "0";
                break;
        }

        // Displacements on y axis
        switch (yBefore) {
            case -1:
                stringYBefore = "1";
                break;
            case 0:
                stringYBefore = "0";
                break;
            case 1:
                stringYBefore = "3";
                break;
            default:
                stringYBefore = "0";
                break;
        }
        switch (yAfter) {
            case -1:
                stringYAfter = "1";
                break;
            case 0:
                stringYAfter = "0";
                break;
            case 1:
                stringYAfter = "3";
                break;
            default:
                stringYAfter = "0";
                break;
        }

        // Concatenate all strings to form a unique code
        finalCode = stringXBefore + stringYBefore + stringXAfter + stringYAfter;

        // Return the code
        return finalCode;
    }
}