/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.aethersanctum.graphics.perlin;

import static java.lang.Math.sqrt;
import static net.aethersanctum.graphics.perlin.PerlinUtil.lerp;
import static net.aethersanctum.graphics.perlin.PerlinUtil.sCurve;

import java.util.Random;

import net.jcip.annotations.NotThreadSafe;

/**
 * Perlin's Coherent noise functions over 1, 2 or 3 dimensions.
 * <p>
 * Translated from C code by Ken Perlin, and uses many of the same
 * terse variable names from the same.
 * http://cims.nyu.edu/~perlin/doc/oscar.html#noise
 * <p>
 * This Java translation avoids object allocations during calls to
 * noise functions, by reusing some Range structures. This is why
 * it is not thread-safe to use one instance in multiple threads.
 * If you need to do that, create an instance for each thread.
 * <p>
 * Usage: create an instance then call noise1, noise2 or noise3.
 */
@NotThreadSafe
public final class PerlinNoise {
    private static final int RANGE_SIZE = 0x100;
    private static final int RANGE_MASK = 0xff;
    private static final int RANGE_OFFSET = 0x1000;

    private static final int SLOT_COUNT = RANGE_SIZE + RANGE_SIZE + 2;

    private final static int[] indices = new int[SLOT_COUNT];
    private final static double[][] seeds3d = new double[SLOT_COUNT][3];
    private final static double[][] seeds2d = new double[SLOT_COUNT][2];
    private final static double[] seeds1d = new double[SLOT_COUNT];

    private static final Random random = new Random();

    static {
        int i;
        int j;
        int k;

        for (i = 0; i < RANGE_SIZE; i++) {
            indices[i] = i;

            seeds1d[i] = randomSignedDouble();

            for (j = 0; j < 2; j++)
                seeds2d[i][j] = randomSignedDouble();
            normalize2(seeds2d[i]);

            for (j = 0; j < 3; j++)
                seeds3d[i][j] = randomSignedDouble();
            normalize3(seeds3d[i]);
        }

        while (--i != 0) {
            k = indices[i];
            j = randomPositiveInt() % RANGE_SIZE;
            indices[i] = indices[j];
            indices[j] = k;
        }

        for (i = 0; i < RANGE_SIZE + 2; i++) {
            indices[RANGE_SIZE + i] = indices[i];
            seeds1d[RANGE_SIZE + i] = seeds1d[i];
            for (j = 0; j < 2; j++)
                seeds2d[RANGE_SIZE + i][j] = seeds2d[i][j];
            for (j = 0; j < 3; j++)
                seeds3d[RANGE_SIZE + i][j] = seeds3d[i][j];
        }
    }

    /**
     * @return a noise value for a point in 1d space
     */
    public double noise1(double xArg) {
        info1dX.setup(xArg);
        final double sx = sCurve(info1dX.r0),
                u = info1dX.r0 * seeds1d[indices[info1dX.b0]],
                v = info1dX.r1 * seeds1d[indices[info1dX.b1]];
        return lerp(sx, u, v);
    }

    /**
     * @return a noise value for a point in 2d space
     */
    public double noise2(double xARg, double yArg) {
        info2dX.setup(xARg);
        info2dY.setup(yArg);

        final int i = indices[info2dX.b0],
                j = indices[info2dX.b1],
                b00 = indices[i + info2dY.b0],
                b10 = indices[j + info2dY.b0],
                b01 = indices[i + info2dY.b1],
                b11 = indices[j + info2dY.b1];

        final double sx = sCurve(info2dX.r0),
                sy = sCurve(info2dY.r0),
                u1 = dotProduct2d(seeds2d[b00], info2dX.r0, info2dY.r0),
                v1 = dotProduct2d(seeds2d[b10], info2dX.r1, info2dY.r0),
                a = lerp(sx, u1, v1),
                u2 = dotProduct2d(seeds2d[b01], info2dX.r0, info2dY.r1),
                v2 = dotProduct2d(seeds2d[b11], info2dX.r1, info2dY.r1),
                b = lerp(sx, u2, v2);

        return lerp(sy, a, b);
    }

    /**
     * @return a noise value for a point in 3d space
     */
    public double noise3(double xArg, double yArg, double zArg) {
        info3dX.setup(xArg);
        info3dY.setup(yArg);
        info3dZ.setup(zArg);

        final int i = indices[info3dX.b0],
                j = indices[info3dX.b1],
                b00 = indices[i + info3dY.b0],
                b10 = indices[j + info3dY.b0],
                b01 = indices[i + info3dY.b1],
                b11 = indices[j + info3dY.b1];

        final double u1 = dotProduct3d(seeds3d[b00 + info3dZ.b0], info3dX.r0, info3dY.r0, info3dZ.r0),
                v1 = dotProduct3d(seeds3d[b10 + info3dZ.b0], info3dX.r1, info3dY.r0, info3dZ.r0),
                u2 = dotProduct3d(seeds3d[b01 + info3dZ.b0], info3dX.r0, info3dY.r1, info3dZ.r0),
                v2 = dotProduct3d(seeds3d[b11 + info3dZ.b0], info3dX.r1, info3dY.r1, info3dZ.r0),
                u3 = dotProduct3d(seeds3d[b00 + info3dZ.b1], info3dX.r0, info3dY.r0, info3dZ.r1),
                v3 = dotProduct3d(seeds3d[b10 + info3dZ.b1], info3dX.r1, info3dY.r0, info3dZ.r1),
                u4 = dotProduct3d(seeds3d[b01 + info3dZ.b1], info3dX.r0, info3dY.r1, info3dZ.r1),
                v4 = dotProduct3d(seeds3d[b11 + info3dZ.b1], info3dX.r1, info3dY.r1, info3dZ.r1),
                t = sCurve(info3dX.r0),
                sy = sCurve(info3dY.r0),
                sz = sCurve(info3dZ.r0),
                a1 = lerp(t, u1, v1),
                b1 = lerp(t, u2, v2),
                a2 = lerp(t, u3, v3),
                b2 = lerp(t, u4, v4),
                c = lerp(sy, a1, b1),
                d = lerp(sy, a2, b2);

        return lerp(sz, c, d);
    }

    private static class Range {
        int b0;
        int b1;
        double r0;
        double r1;

        private void setup(double howFar) {
            double t = howFar + RANGE_OFFSET;
            b0 = ((int) t) & RANGE_MASK;
            b1 = (b0 + 1) & RANGE_MASK;
            r0 = t - (int) t;
            r1 = r0 - 1.0;
        }
    }

    private final Range info1dX = new Range();
    private final Range info2dX = new Range();
    private final Range info2dY = new Range();
    private final Range info3dX = new Range();
    private final Range info3dY = new Range();
    private final Range info3dZ = new Range();

    /**
     * Normalize a two dimensional vector expressed as an array of doubles.
     * @param v the vector to be normalized. Will be modified in place.
     */
    private static void normalize2(double[] v) {
        final double s = sqrt(v[0] * v[0] + v[1] * v[1]);
        v[0] = v[0] / s;
        v[1] = v[1] / s;
    }

    /**
     * Normalize a three dimensional vector expressed as an array of doubles.
     * @param v the vector to be normalized. Will be modified in place.
     */
    private static void normalize3(double[] v) {
        final double s = sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        v[0] = v[0] / s;
        v[1] = v[1] / s;
        v[2] = v[2] / s;
    }

    /**
     * calculate dot product of 2d vectors
     * @param q the first vector expressed as a 2-item array
     * @param rx the second vector's x component
     * @param ry the second vector's y component
     * @return the resulting dot product
     */
    private double dotProduct2d(double[] q, double rx, double ry) {
        return rx * q[0] + ry * q[1];
    }


    /**
     * calculate dot product of 2d vectors
     * @param q the first vector expressed as a 2-item array
     * @param rx the second vector's x component
     * @param ry the second vector's y component
     * @return the resulting dot product
     */
    private double dotProduct3d(double[] q, double rx, double ry, double rz) {
        return (rx * q[0] + ry * q[1] + rz * q[2]);
    }

    private static double randomSignedDouble() {
        return (double) ((randomPositiveInt() % (RANGE_SIZE + RANGE_SIZE)) - RANGE_SIZE) / RANGE_SIZE;
    }

    private static int randomPositiveInt() {
        return Math.abs(random.nextInt());
    }
}
