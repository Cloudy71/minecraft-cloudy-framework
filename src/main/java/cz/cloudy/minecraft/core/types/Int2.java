/*
  User: Cloudy
  Date: 02/02/2022
  Time: 03:01
*/

package cz.cloudy.minecraft.core.types;

import com.google.common.base.Objects;

/**
 * @author Cloudy
 */
public class Int2 {
    private int x;
    private int y;

    /**
     * X and Y coordinate constructor.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public Int2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * String constructor.
     * Primitive values like "64,32" are parsed.
     *
     * @param str String
     */
    public Int2(String str) {
        String[] data = str.split(",");
        if (data.length != 2)
            return;

        this.x = Integer.parseInt(data[0]);
        this.y = Integer.parseInt(data[1]);
    }

    /**
     * Getter for X coordinate
     *
     * @return X coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Setter for X coordinate.
     *
     * @param x X coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Getter for Y coordinate
     *
     * @return Y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Setter for Y coordinate.
     *
     * @param y Y coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets product of both X and Y coordinate.
     * The result is X * Y.
     *
     * @return Product of both coordinates
     */
    public int getProduct() {
        return this.x * this.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Int2 int2 = (Int2) o;
        return x == int2.x && y == int2.y;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y);
    }
}
