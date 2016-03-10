package com.example.vincenzo.guessandcheckers.core.support_libraries;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;

/**
 * Created by vincenzo on 31/10/2015.
 */
public class BitmapManager {

    public static Bitmap scaleBitmap(Bitmap realImage, float wishedWidth, float wishedHeight, boolean filter) {

        Size newSize = SizeAdapter.adaptPreservingAspectRatio(realImage.getWidth(), realImage.getHeight(), wishedWidth, wishedHeight);

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, (int)newSize.width,
                (int)newSize.height, filter);
        return newBitmap;
    }

    public static void saveImage(Context context, Mat mat) {
        if (mat.type() == CvType.CV_8UC3)
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2BGRA);
        Bitmap img = convertMat2Bitmap(mat);
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard != null) {
            File mediaDir = new File(sdcard, "DCIM/Camera");
            if (!mediaDir.exists()) {
                mediaDir.mkdirs();
            }
        }
        MediaStore.Images.Media.insertImage(context.getContentResolver(), img, "ciccio", "anything");
    }


    private static Bitmap convertMat2Bitmap(Mat threshChessboardForPawnsSeeker) {
        Bitmap img = Bitmap.createBitmap(threshChessboardForPawnsSeeker.cols(), threshChessboardForPawnsSeeker.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(threshChessboardForPawnsSeeker, img);
        return img;
    }

}
