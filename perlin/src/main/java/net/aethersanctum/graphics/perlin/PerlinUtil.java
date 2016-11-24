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

public final class PerlinUtil {

    /**
     * One dimensional linear interpolation.
     * <p>
     * Calculates a point between 2 points based on a distance fraction.
     *
     * @param distance - fraction of distance between the points expressed as values from 0.0 to 1.0.
     * @param firstPoint - location of first point
     * @param secondPoint - location of second point
     * @return the resulting interpolated point
     */
    public static double lerp(double distance, double firstPoint, double secondPoint) {
        return firstPoint + distance * (secondPoint - firstPoint);
    }

    /**
     * Sigmoid curve.
     * <p>
     * Smooths out beginnings and ends of interpolations.
     *
     * @param t value between 0.0 and 1.0 to smooth out
     * @return smoothed value, also between 0.0 and 1.0
     */
    public static double sCurve(double t) {
        return t * t * (3.0 - 2.0 * t);
    }
}
