/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

/**
 * Static class. For the calculation of linear equations.
 *
 * @author Sebastian
 */
public class Calculations {

    /**
     * Calculates slope of line from 2 points
     *
     * @param x1 point1.x
     * @param y1 point1.y
     * @param x2 point2.x
     * @param y2 point2.y
     * @return slope of line (from linear equation y=ax+b)
     */
    public static double linearEquationA(double x1, double y1, double x2, double y2) {
        return ((y2 - y1) / (x2 - x1));
    }

    /**
     * Calculates slope of perpendicular line to 2 points
     *
     * @param x1 point1.x
     * @param y1 point1.y
     * @param x2 point2.x
     * @param y2 point2.y
     * @return slope of line (from linear equation y=ax+b)
     */
    public static double linearEquationPerpendicularA(double x1, double y1, double x2, double y2) {
        return -((x2 - x1) / (y2 - y1));
    }

    /**
     * Calculates y-intercept of linear equation from 2 points
     *
     * @param x1 point1.x
     * @param y1 point1.y
     * @param x2 point2.x
     * @param y2 point2.y
     * @return y-intercept (from linear equation y=ax+b)
     */
    public static double linearEquationB(double x1, double y1, double x2, double y2) {
        return -(x1 * ((y2 - y1) / (x2 - x1))) + y1;
    }

    /**
     * Calculates y-intercept of linear equation of perpendicular line to 2
     * intercept with first point points
     *
     * @param x1 point1.x
     * @param y1 point1.y
     * @param x2 point2.x
     * @param y2 point2.y
     * @return y-intercept (from linear equation y=ax+b)
     */
    public static double linearEquationPerpendicularB(double x1, double y1, double x2, double y2) {
        return y1 - linearEquationPerpendicularA(x1, y1, x2, y2) * x1;
    }

    /**
     * Calculates the coordinates of the end of the vector.
     *
     * @param x1 point1 x
     * @param y1 point1 y
     * @param a slope of line (from linear eguation y=ax+b)
     * @param b y-intercept (from linear eguation y=ax+b)
     * @param l length of vector
     * @param isoOppositeDirection if true one of the result else other
     * @return double[2] result[0] = point.x result[1] = point.y
     */
    public static double[] pointFromVector(double x1, double y1, double a, double b, double l, boolean isoOppositeDirection) {
        double[] result = new double[2];
        if (isoOppositeDirection) {
            result[0] = (Math.sqrt(a * a * l * l - a * a * x1 * x1 - 2 * a * b * x1 + 2 * a * x1 * y1 - b * b + 2 * b * y1 + l * l - y1 * y1) - a * b + a * y1 + x1) / (a * a + 1);
        } else {
            result[0] = (-1 * Math.sqrt(a * a * l * l - a * a * x1 * x1 - 2 * a * b * x1 + 2 * a * x1 * y1 - b * b + 2 * b * y1 + l * l - y1 * y1) - a * b + a * y1 + x1) / (a * a + 1);
        }
        result[1] = a * result[0] + b;
        return result;
    }
}
