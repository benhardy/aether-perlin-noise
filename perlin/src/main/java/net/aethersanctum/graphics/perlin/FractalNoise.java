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

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Use accumulations of Perlin noise at various scales to produce
 * crinkly surfaces which look like landscapes.
 */
@NotThreadSafe
public class FractalNoise {

    private final PerlinNoise perlin = new PerlinNoise();

    /**
     * Generate landscape elevations for points in a plane.
     * @param ix east-west coordinate
     * @param iz north-south coordinate
     * @return the altitude of a point at (ix, iz)
     */
    public double fractalNoise(double ix, double iz) {
        double amp = 128;
        double ascale = 0.5;
        double size = 0;
        int max_rounds = 7;
        for (int round = 0; round < max_rounds; round ++) {
            size += amp * perlin.noise2(ascale * ix, ascale * iz);
            amp *= 0.5;
            ascale *= 2;
        }
        return size;
    }

    /**
     * Generate landscape elevations for points in a plane, same as above,
     * but try to smooth out surface features at lower altitudes.
     *
     * @param ix east-west coordinate
     * @param iz north-south coordinate
     * @return the altitude of a point at (ix, iz)
     */
    public double fractalNoiseSmoothBottom(double ix, double iz) {
        double amp = 128;
        double ascale = 0.5;
        double size = 0;
        int max_rounds = 7;
        for (int round = 0; round < max_rounds; round ++) {
            double part = perlin.noise2(ascale * ix, ascale * iz);
            double atten = Math.pow((part + 1) / 2, 0.25);
            size += amp * part;
            amp *= 0.5 * atten;
            ascale *= 2;
        }
        return size;
    }
}
