package com.example.vincenzo.myfirstofficialeopencvtest.core.support_libraries;

import org.opencv.core.Point;

/**
 * Created by vincenzo on 29/09/2015.
 */
public class EquationOfLineManager {

    public static double getSlope(Point lowePoint, Point higherPoint){
        double y = higherPoint.y;
        double x = higherPoint.x;

        double y1 = lowePoint.y;
        double x1 = lowePoint.x;

        double divisor = (x-x1);

        if(divisor == 0)
            return 0;

        return (y-y1)/(x-x1);
    }

    public static double getDistancePointRect(Point pt, Point a, Point b) {
        //ax+by+c=0 equation of line
        double m = getSlope(a,b);
        double c = a.y -m*a.x;

        double numerator = Math.abs(m*pt.x -1*pt.y + c);
        double denominator = Math.sqrt(m*m + 1);
        double distance = numerator/denominator;

        return distance;
    }
}
