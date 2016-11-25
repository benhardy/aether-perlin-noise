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
package net.aethersanctum.graphics.perlin.demo;

import static net.aethersanctum.graphics.perlin.PerlinUtil.lerp;
import static net.aethersanctum.graphics.perlin.PerlinUtil.sCurve;

import java.awt.*;


class ColorGradient {
    private final Color[] gradientColors;
    private final double[] gradientPoints;

    public ColorGradient(Color[] colors, double[] points) {
        if (colors.length != points.length) {
            throw new IllegalArgumentException("point array must be same length as color array");
        }
        this.gradientColors = colors;
        this.gradientPoints = points;
    }

    public Color colorAt(double position) {
        return gradient(position, gradientColors, gradientPoints);
    }

    private Color gradient(double position, Color first, Color second) {
        final int red = (int) (lerp(sCurve(position), first.getRed(), second.getRed()));
        final int green = (int) (lerp(sCurve(position), first.getGreen(), second.getGreen()));
        final int blue = (int) (lerp(sCurve(position), first.getBlue(), second.getBlue()));
        final Color color = new Color(red, green, blue);
        return color;
    }

    private Color gradient(double position, Color[] colors, double[] points) {
        if (position < 0) {
            return colors[0];
        }
        if (position <= points[0]) {
            return colors[0];
        }
        if (position >= 1.0) {
            return colors[colors.length - 1];
        }
        if (position > points[points.length - 1]) {
            return colors[colors.length - 1];
        }
        int index = 0;
        while(position > points[index]) {
            index++;
        }
        if (index == 0) {
            return colors[0];
        }
        double low = points[index - 1];
        double high = points[index];
        double t = (position - low) / (high - low);
        //return new Color((int)(255*t),(int)(255*t),(int)(255*t));
        final Color interpolated = gradient(t, colors[index - 1], colors[index]);
        return interpolated;

    }

}
