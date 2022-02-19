/*
  User: Cloudy
  Date: 23/01/2022
  Time: 03:24
*/

package cz.cloudy.minecraft.core.math;

import org.bukkit.util.Vector;

/**
 * @author Cloudy
 */
public class VectorUtils {
    /**
     * Gets right direction of vector.
     *
     * @param direction Vector
     * @return Right direction
     */
    public static Vector getRightDirection(Vector direction) {
        return direction.clone().crossProduct(new Vector(1, 0, 0));
//        Vector originalPosition = location.toVector();
//        float yaw = location.getYaw() - 90;
//        Vector newPosition = new Vector(
//                originalPosition.getX() + Math.cos(yaw),
//                originalPosition.getY(),
//                originalPosition.getZ() + Math.sin(yaw)
//        );
//        return newPosition.subtract(originalPosition).normalize();
    }

    /**
     * Gets up direction of vector.
     *
     * @param direction Vector
     * @return Up direction
     */
    public static Vector getUpDirection(Vector direction) {
        return direction.clone().crossProduct(new Vector(0, 1, 0));
//        Vector originalPosition = location.toVector();
//
//        float yaw = location.getYaw();
//        float pitch = location.getPitch() + 90;
//        Vector newPosition = new Vector(
//                originalPosition.getX() + Math.sin(pitch)*Math.cos(pitch),
//                originalPosition.getY() + Math.cos(pitch),
//                originalPosition.getZ() + Math.sin(pitch)*Math.cos(pitch)
//        );
//        return newPosition.subtract(originalPosition).normalize();
    }

    /**
     * Checks two vectors and swaps values if second edge vector coordinate is smaller than first edge vector coordinate.
     *
     * @param firstEdge  First edge vector
     * @param secondEdge Second edge vector
     */
    public static void boundingBoxNormalize(Vector firstEdge, Vector secondEdge) {
        if (firstEdge.getX() > secondEdge.getX()) {
            double x = firstEdge.getX();
            firstEdge.setX(secondEdge.getX());
            secondEdge.setX(x);
        }
        if (firstEdge.getY() > secondEdge.getY()) {
            double y = firstEdge.getY();
            firstEdge.setY(secondEdge.getY());
            secondEdge.setY(y);
        }
        if (firstEdge.getZ() > secondEdge.getZ()) {
            double z = firstEdge.getZ();
            firstEdge.setZ(secondEdge.getZ());
            secondEdge.setZ(z);
        }
    }
}
