package net.aethersanctum.graphics.perlin.demo;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.aethersanctum.graphics.perlin.FractalNoise;
import net.aethersanctum.graphics.perlin.PerlinNoise;

public class PerlinDemo {

    public static void main(String[] args) throws IOException {
        new PerlinDemo().run();
    }

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    private final Color sky = new Color(40, 80, 255);
    private final Vector eye = new Vector(0, 300, 0);
    private final Vector toSun = new Vector(2, 1, -1).normalized();
    private final TerrainColors terrain = new TerrainColors();

    private final PerlinNoise perlin = new PerlinNoise();
    private final FractalNoise fractalNoise = new FractalNoise();

    private final double ambientLightLevel = 0.3;

    public void run() throws IOException {
        final BufferedImage finalImage = new BufferedImage(WIDTH * 2, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = (Graphics2D) finalImage.getGraphics();
        draw2dFractalNoise(g);
        draw3dFractalLandscape(g);
        drawColorGradientScheme(g);
        final FileOutputStream baos = new FileOutputStream("perlin.png");
        ImageIO.write(finalImage, "PNG", baos);
        baos.close();
    }

    private void draw2dFractalNoise(Graphics2D g) {
        final double scale = 10;
        for (int ypixel = 0; ypixel < HEIGHT; ypixel++) {
            for (int xpixel = 0; xpixel < WIDTH; xpixel++) {
                // on one side just do a top down view
                double ix = scale * (double) xpixel / WIDTH;
                double iy = scale * (double) ypixel / HEIGHT;
                double size = fractalNoise.fractalNoise(ix, iy);
                int p = 128 + (int) size;
                p = p < 0 ? 0 : p > 255 ? 255 : p;
                g.setColor(new Color(p, p, p));
                g.drawRect(xpixel, ypixel, 1, 1);
            }
        }
    }

    private void draw3dFractalLandscape(Graphics2D g) {
        final double scale = 0.01;
        final double yscale = 0.75;
        // use perlin noise as a distance estimation function for a raymarcher
        final Raymarching raymarching = new Raymarching(point ->
                point.getY() - yscale * fractalNoise.fractalNoiseSmoothBottom(point.getX() * scale, point.getZ() * scale)
        );
        final Vector look = new Vector(1, -3, 5).normalized();
        final Vector vertical = new Vector(0, 1, 0);
        final Vector right = vertical.cross(look).normalized();
        final Vector up = look.cross(right).normalized();
        for (int ypixel = 0; ypixel < HEIGHT; ypixel++) {
            for (int xpixel = 0; xpixel < WIDTH; xpixel++) {
                final Vector view = new Vector((xpixel - WIDTH / 2.0) / WIDTH, -(ypixel - HEIGHT / 2.0) / HEIGHT, 1).normalized();
                final Vector ray = look.plus(right.scaled(view.getX())).plus(up.scaled(view.getY())).normalized();
                final Vector position = new Vector(eye);
                final Color color;
                final Vector normal = new Vector(0, 0, 0);
                if (raymarching.findStrikeWithNormal(eye, ray, position, normal)) {
                    color = landscapePixelColor(position, normal, yscale);
                } else {
                    color = sky;
                }
                g.setColor(color);
                g.drawRect(xpixel + WIDTH, ypixel, 1, 1);
            }
        }
    }

    private void drawColorGradientScheme(Graphics2D g) {
        for (int xpixel = 0; xpixel < WIDTH; xpixel++) {
            double pos = (double) xpixel / WIDTH;
            Color color = terrain.colorAt(pos);
            g.setColor(color);
            g.drawRect(xpixel + WIDTH, HEIGHT - 10, 1, 10);
        }
    }

    private Color landscapePixelColor(Vector position, Vector normal, double yscale) {
        if (((int)position.getX() & 0x3f) == 0) {
            return Color.CYAN;
        }
        if (((int)position.getZ() & 0x3f) == 0) {
            return Color.CYAN;
        }
        final double elevation = 128 + position.getY() / yscale;
        final double lit = max(0, toSun.dotProduct(normal));
        final double illumination = ambientLightLevel + (1 - ambientLightLevel) * lit;
        final double steepnessBump = normal.getY() * 0.2;
        final Color shade = terrain.colorAt(elevation / 256.0 + steepnessBump);
        final int red = boxColor((int) (shade.getRed() * illumination));
        final int green = boxColor((int) (shade.getGreen() * illumination));
        final int blue = boxColor((int) (shade.getBlue() * illumination));
        return new Color(red, green, blue);
    }

    private static int boxColor(int level) {
        return max(0, min(255, level));
    }

}
