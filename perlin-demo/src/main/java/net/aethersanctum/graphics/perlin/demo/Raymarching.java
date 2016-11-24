package net.aethersanctum.graphics.perlin.demo;

import java.util.function.Function;

class Raymarching {
    private final Function<Vector, Double> distanceEstimator;

    private static final int AGGRESSIVENESS = 1000;

    public Raymarching(Function<Vector, Double> distanceEstimator) {
        this.distanceEstimator = distanceEstimator;
    }

    public boolean findStrikeWithNormal(Vector eye, Vector ray, Vector position, Vector normal) {
        if (!findStrike(ray, position)) {
            return false;
        }
        final Vector rightSlightlyRay = new Vector(ray.getX() + 0.001, ray.getY(), ray.getZ()).normalized();
        final Vector rightSlightlyPosition = new Vector(eye);
        if (!findStrike(rightSlightlyRay, rightSlightlyPosition)) {
            return false;
        }
        final Vector upSlightlyRay = new Vector(ray.getX(), ray.getY() + 0.001, ray.getZ());
        final Vector upSlightlyPosition = new Vector(eye);
        if (!findStrike(upSlightlyRay, upSlightlyPosition)) {
            return false;
        }
        final Vector up = position.minus(upSlightlyPosition).normalized();
        final Vector right = position.minus(rightSlightlyPosition).normalized();
        Vector.crossProduct(up, right, normal);
        return true;
    }

    private boolean findStrike(Vector ray, Vector position) {
        double distance = 1;
        for (int turns = AGGRESSIVENESS; turns > 0; turns--) {
            distance = distanceEstimator.apply(position);
            if (distance < 0.1) {
                return true;
            }
            double jump = distance * 0.2;
            position.add(jump * ray.getX(), jump * ray.getY(), jump * ray.getZ());
        }
        return false;
    }
}
