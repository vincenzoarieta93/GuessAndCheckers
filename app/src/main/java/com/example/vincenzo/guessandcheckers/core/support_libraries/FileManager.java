package com.example.vincenzo.guessandcheckers.core.support_libraries;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by vincenzo on 25/11/2015.
 */
public class FileManager {

    /**
     * Appends row at the end of an existing file
     * @param filePath path of the file to which append the row
     * @param row content to append at the end of filePath
     */
    public static void appendRowToFile(String filePath, String row){
        BufferedWriter out;
        try {
            out = new BufferedWriter(new FileWriter(filePath, true));
            out.write('\n' + row);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Copies a file stored into assets folder and pasts it to the specified subdirectory of the device external storage.
     * If the specified directory doesn't exists it will be created.
     * @param context context of the application
     * @param fileToWritePath path of the file we want to write to the device external storage
     * @param fileNameOnExternalStorage name which we want to assign to the file wrote in the device external storage
     * @param fileExtension extension of the file stored in the device external storage
     * @param subDirName subdirectory of the device external storage in which we want to save the file
     * @return
     */

    public static String writeFileFromAssetsToExternalStorage(Context context, String fileToWritePath, String fileNameOnExternalStorage, String fileExtension, String subDirName) {
        InputStream logicProgram = null;

        try {
            logicProgram = context.getAssets().open(fileToWritePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeFileToExternalStorage(fileNameOnExternalStorage, fileExtension, subDirName, logicProgram);
        return Environment.getExternalStorageDirectory() + File.separator + subDirName + File.separator + fileNameOnExternalStorage + fileExtension;
    }


    public static void deleteFileFromExternalStorage(Context context, String filePath){

        if(new File(filePath).delete())
            Log.i("CICCIO", filePath + " was successfully deleted");
        else
            Log.i("CICCIO", filePath + " has not been removed");
    }

    private static void writeFileToExternalStorage(String fileName, String fileExtension, String subDir, InputStream body) {
        FileOutputStream fos;

        try {
            final File dir = new File(Environment.getExternalStorageDirectory() + File.separator + subDir);

            if (!dir.exists())
                dir.mkdirs();

            final File myFile = new File(dir, fileName + fileExtension);

            if (!myFile.exists())
                myFile.createNewFile();

            fos = new FileOutputStream(myFile);
            byte[] data = new byte[body.available()];
            body.read(data);
            fos.write(data);
            body.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
