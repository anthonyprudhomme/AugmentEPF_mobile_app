package com.filiereticsa.arc.augmentepf.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.ThumbnailUtils;
import android.os.Build;

import com.filiereticsa.arc.augmentepf.activities.AugmentEPFApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by anthony on 07/05/2017.
 */

// This class helps you handle bitmaps. It contains method that let you for example load or save
// images
public class BitmapManager {


    // This method will return an image with round borders. It needs the squared bitmap as entry
    public static Bitmap getRoundedBitmapFromBitmap(Bitmap bitmap) {
        Bitmap imageRounded = null;
        if (bitmap != null) {
            imageRounded = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), bitmap.getConfig());
            Canvas canvas = new Canvas(imageRounded);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            canvas.drawRoundRect((new RectF(0, 0, bitmap.getWidth(), bitmap.getWidth())), bitmap.getWidth() / 5, bitmap.getWidth() / 5, paint);
        }
        return imageRounded;
    }

    // this method will return an int that correspond to the number of time the bitmap has to be
    // divided by two in order to be approximately the size required in the method getBitmapFromURL
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (reqWidth != 0) {
            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }
        }
        return inSampleSize;
    }

    // This method will return a bitmap loaded from an URL. You can give the height and the width
    // you desire and if you want it to be a squared image or not
    @SuppressWarnings({"SuspiciousNameCombination", "ConstantConditions"})
    public Bitmap getBitmapFromURL(String src, boolean squared, int reqWidth, int reqHeight) {
        try {
            InputStream input = getHTTPConnectionInputStream(src);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            if (input != null) {
                input.close();
            }
            input = getHTTPConnectionInputStream(src);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            if (input != null) {
                input.close();
            }
            if(bitmap!=null) {
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    height = height / 2;
                    width = width / 2;
                }
                if (squared) {
                    if (bitmap.getWidth() > bitmap.getHeight()) {
                        return ThumbnailUtils.extractThumbnail(bitmap, height, height);
                    } else {
                        return ThumbnailUtils.extractThumbnail(bitmap, width, width);
                    }
                } else {
                    return ThumbnailUtils.extractThumbnail(bitmap, width, height);
                }
            }else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // this method will save a bitmap in the phone to the name given in the parameter in the folder
    // of the application
    public void saveBitmapToFile(Bitmap bitmapToSave, String filename) {
        FileOutputStream out = null;
        File file = new File(AugmentEPFApplication.getAppContext().getFilesDir(), filename);
        try {
            out = new FileOutputStream(file);
            bitmapToSave.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // this method will load a bitmap you saved in the folder of the application given the name of
    // that image
    public Bitmap loadBitmapFromFile(String filename) {
        FileInputStream in;
        Bitmap bitmapLoaded = null;
        try {
            in = AugmentEPFApplication.getAppContext().openFileInput(filename);
            bitmapLoaded = BitmapFactory.decodeStream(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmapLoaded;
    }

    // This method will return the inputStream received to a specific source given in parameter
    private InputStream getHTTPConnectionInputStream(String src) throws IOException {
        java.net.URL url = new java.net.URL(src);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setConnectTimeout(5000);
        connection.connect();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK && connection.getResponseCode() != HttpURLConnection.HTTP_MOVED_PERM) {
            return null;
        }
        return connection.getInputStream();
    }
}