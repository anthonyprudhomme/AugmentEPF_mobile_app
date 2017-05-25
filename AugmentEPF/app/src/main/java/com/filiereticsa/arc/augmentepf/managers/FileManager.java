package com.filiereticsa.arc.augmentepf.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.filiereticsa.arc.augmentepf.activities.AugmentEPFApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by anthony on 07/05/2017.
 */

// This manager will help you to handle file writing and reading
public class FileManager {

    private static final String TAG = "FileManager";
    private String directoryName;
    private String filename;

    public FileManager(String directoryName, String filename) {
        this.directoryName = directoryName;
        this.filename = filename;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    // this method will read the file with the name you gave in the constructor
    public String readFile() {
        FileInputStream fileInputStream; //Object that let you read in the file system of the mobile
        String valueInFile = null; //Value read in the file
        File directory; // Name of the directory given in parameter
        File file; // Name of the file given in parameter

        try {
            //Check if you gave a directory name. If not that means you wanna save it to the root of
            //the folder of the application
            if (directoryName != null) {
                directory = new File(
                        AugmentEPFApplication.getAppContext().getFilesDir(), directoryName);
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        Log.d(TAG, directory.getName() + " was created");
                    }
                }
                file = new File(
                        AugmentEPFApplication.getAppContext().getFilesDir(),
                        directoryName + File.separator + filename);
            } else {
                file = new File(
                        AugmentEPFApplication.getAppContext().getFilesDir(), filename);
            }
            if (!file.exists()) {
                if (file.createNewFile()) {
                    Log.d(TAG, file.getName() + " was created");
                }
            }
            // reading of the file
            int octet;
            ArrayList<Integer> byteList = new ArrayList<>();
            fileInputStream = new FileInputStream(file);
            while ((octet = fileInputStream.read()) != -1) {
                byteList.add(octet);
            }
            fileInputStream.close();
            byte bytes[] = new byte[byteList.size()];
            for (int i = 0; i < byteList.size(); i++) {
                bytes[i] = byteList.get(i).byteValue();
            }
            valueInFile = new String(bytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valueInFile;
    }

    // this method will save the file with the name you gave in the constructor. The content of this
    // file will be the data you gave in the parameter
    void saveFile(String data) {
        FileOutputStream outputStream;
        File directory;
        File file;
        try {
            if (directoryName != null) {
                directory = new File(
                        AugmentEPFApplication.getAppContext().getFilesDir(), directoryName);
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        Log.d(TAG, directory.getName() + " was created");
                    }
                }
                file = new File(
                        AugmentEPFApplication.getAppContext().getFilesDir(),
                        directoryName + File.separator + filename);
            } else {
                file = new File(
                        AugmentEPFApplication.getAppContext().getFilesDir(), filename);
            }
            if (!file.exists()) {
                if (file.createNewFile()) {
                    Log.d(TAG, file.getName() + " was created");
                }
            }
            outputStream = new FileOutputStream(file, false);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this method will load a bitmap from the file of the name you gave in the constructor
    public Bitmap loadBitmapFromFile() {
        FileInputStream in;
        Bitmap bitmapLoaded = null;
        File directory;
        File file;
        try {
            if (directoryName != null) {
                directory = new File(
                        AugmentEPFApplication.getAppContext().getFilesDir(), directoryName);
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        Log.d(TAG, directory.getName() + " was created");
                    }
                }
                file = new File(
                        AugmentEPFApplication.getAppContext().getFilesDir(),
                        directoryName + File.separator + filename);
            } else {
                file = new File(AugmentEPFApplication.getAppContext().getFilesDir(), filename);
            }
            if (!file.exists()) {
                if (file.createNewFile()) {
                    Log.d(TAG, file.getName() + " was created");
                }
            }
            in = new FileInputStream(file);
            bitmapLoaded = BitmapFactory.decodeStream(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmapLoaded;
    }

    // this method will save a bitmap in the file of the name given in the constructor
    public void saveBitmapToFile(Bitmap bitmapToSave) {
        FileOutputStream outputStream = null;
        File directory;
        File file;
        try {
            if (directoryName != null) {
                directory = new File(
                        AugmentEPFApplication.getAppContext().getFilesDir(), directoryName);
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        Log.d(TAG, directory.getName() + " was created");
                    }
                }
                file = new File(
                        AugmentEPFApplication.getAppContext().getFilesDir(),
                        directoryName + File.separator + filename);
            } else {
                file = new File(AugmentEPFApplication.getAppContext().getFilesDir(), filename);
            }
            if (!file.exists()) {
                if (file.createNewFile()) {
                    Log.d(TAG, file.getName() + " was created");
                }
            }
            outputStream = new FileOutputStream(file, false);
            bitmapToSave.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // this method can save any serializable data in the file of the name given in the constructor
    public void saveSerializableFile(Serializable data) {
        FileOutputStream outputStream;
        File directory;
        File file;
        try {
            if (directoryName != null) {
                directory = new File(
                        AugmentEPFApplication.getAppContext().getFilesDir(), directoryName);
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        Log.d(TAG, directory.getName() + " was created");
                    }
                }
                file = new File(
                        AugmentEPFApplication.getAppContext().getFilesDir(),
                        directoryName + File.separator + filename);
            } else {
                file = new File(
                        AugmentEPFApplication.getAppContext().getFilesDir(), filename);
            }
            if (!file.exists()) {
                if (file.createNewFile()) {
                    Log.d(TAG, file.getName() + " was created");
                }
            }
            outputStream = new FileOutputStream(file, false);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(data);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}