package com.example.vincenzo.guessandcheckers.core.support_libraries;

import org.opencv.core.Size;

/**
 * Created by vincenzo on 03/01/2016.
 */
public class SizeAdapter {

    /**
     * This method resize the size given by realWidth and realHeight params according to wishedWidth and wishedHeight params preserving initial aspect ratio
     * @param realWidth width of the size we want to adapt
     * @param realHeight height of the size we want to adapt
     * @param wishedWidth width of the target size
     * @param wishedHeight height of the target size
     * @return a new Size obj containing adapted values of realWidth and realHeight
     */
    public static Size adaptPreservingAspectRatio(float realWidth, float realHeight, float wishedWidth, float wishedHeight) {

        if(wishedHeight == Integer.MAX_VALUE && wishedWidth == Integer.MAX_VALUE)
            throw new RuntimeException("wrong parameters. At least one parameter must be specified");

        if(wishedWidth == Integer.MAX_VALUE){
            int newWishedWidth = Math.round((wishedHeight * realWidth) / realHeight);
            return new Size(newWishedWidth, wishedHeight);
        } else if(wishedHeight == Integer.MAX_VALUE){
            int newWishedHeight = Math.round((wishedWidth * realHeight) / realWidth);
            return new Size(wishedWidth, newWishedHeight);
        }

        float maxImageSize = Math.min(wishedWidth, wishedHeight);

        float ratio = Math.min(
                maxImageSize / realWidth,
                maxImageSize / realHeight);
        int width = Math.round(ratio * realWidth);
        int height = Math.round(ratio * realHeight);

        return new Size(width, height);
    }
}
