package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Moshe Malka on 31/05/2016.
 */
public class TakeScreenshot {

    private Context TheThis;
    private String NameOfFolder = "/BabyKeeper";
    private String NameOfFile   = "babykeeper_pic_";

    public void SaveImage(Context context,Bitmap imageToSave){
        TheThis = context;
        //String file_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + NameOfFolder;
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/BabyKeeper/");
        String CurrentDateAndTime= getCurrentDateAndTime();
        //File dir = new File(file_path);

        if(!folder.exists()){
            if(folder.mkdirs()){
                Log.i("created new directory",folder.getAbsolutePath());
            }
        }

        File file = new File(folder, NameOfFile +CurrentDateAndTime+ ".jpg");
        try {
            if(file.createNewFile()){
                Log.i("file.createNewFile()","Created a new file");
            }
            FileOutputStream fOut = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            imageToSave.recycle();
            MakeSureFileWasCreatedThenMakeAvailable(file);
            AbleToSave();

        }
        catch (FileNotFoundException e)
        {
            UnableToSave();
            Log.e("FileNotFoundException",e.getMessage());
        }
        catch (IOException e)
        {
            UnableToSave();
            Log.e("IOException",e.getMessage());
        }



    }



    private void MakeSureFileWasCreatedThenMakeAvailable(File file) {
        MediaScannerConnection.scanFile(TheThis,
                new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("ExternalStorage", "Scanned " + path + ":");
                        Log.e("ExternalStorage", "-> uri=" + uri);

                    }
                });

    }



    private String getCurrentDateAndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return df.format(c.getTime());
    }


    private void UnableToSave() {
        Toast.makeText(TheThis, "Picture cannot be saved to gallery", Toast.LENGTH_SHORT).show();

    }

    private void AbleToSave() {
        Toast.makeText(TheThis, "Picture saved to gallery", Toast.LENGTH_SHORT).show();

    }
}
