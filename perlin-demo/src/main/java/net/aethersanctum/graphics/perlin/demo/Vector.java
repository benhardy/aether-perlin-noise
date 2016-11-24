package net.aethersanctum.graphics.perlin.demo;

import static java.lang.Math.sqrt;

/**
 * Bog-standard 3d vector.
 */
public class Vector {
    private double x;
    private double y;
    private double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(Vector other) {
        this(other.x, other.y, other.z);
    }

    public Vector normalized() {
        final double ratio = 1 / length();
        return new Vector(x * ratio, y * ratio, z * ratio);
    }

    private double length() {
        return sqrt(x * x + y * y + z * z);
    }

    public double getZ() {
        return z;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public void add(double vx, double vy, double vz) {
        x += vx;
        y += vy;
        z += vz;
    }

    public Vector plus(Vector other) {
        return new Vector(x + other.x, y + other.y, z + other.z);
    }

    public Vector scaled(double m) {
        return new Vector(x * m, y * m, z * m);
    }

    public static void crossProduct(Vector up, Vector right, Vector normal) {
        normal.x = up.y * right.z - up.z * right.y;
        normal.y = up.z * right.x - up.x * right.z;
        normal.z = up.x * right.y - up.y * right.x;
    }

    public Vector cross(Vector right) {
        return new Vector(
                this.y * right.z - this.z * right.y,
                this.z * right.x - this.x * right.z,
                this.x * right.y - this.y * right.x
        );
    }

    public Vector minus(Vector other) {
        return new Vector(x - other.x, y - other.y, z - other.z);
    }

    public double dotProduct(Vector other) {
        return x * other.x + y * other.y + z * other.z;
    }

}
