package net.aethersanctum.graphics.perlin.demo;

import java.awt.*;

public class TerrainColors {

    private static final Color snow = new Color(255, 255, 255);
    private static final Color slate = new Color(70, 70, 80);
    private static final Color brown = new Color(80, 50 , 10);
    private static final Color gold = new Color(200, 190, 20);
    private static final Color grass = new Color(15, 80, 10);

    private final Color[] gradientColors = { grass, grass, gold, brown, slate, snow, snow };
    private final double[] gradientPoints = { 0,    0.4, 0.5, 0.6, 0.65, 0.66, 1.0 };

    private final ColorGradient colors = new ColorGradient(gradientColors, gradientPoints);

    public Color colorAt(double elevation) {
        return colors.colorAt(elevation);
    }
}
