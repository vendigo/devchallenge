package it.devchallenge.minescanner.service;

import it.devchallenge.minescanner.exception.InvalidInputException;
import it.devchallenge.minescanner.model.ImagePixels;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class ImageParserService {

    public static final String URI_PREFIX = "data:image/png;base64,";

    @SneakyThrows
    public ImagePixels parseImage(String base64DataUri) {
        if (!base64DataUri.startsWith(URI_PREFIX)) {
            throw new InvalidInputException("Image string doesn't have uri prefix");
        }

        try {
            return parsePixels(base64DataUri);
        } catch (InvalidInputException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InvalidInputException("Unable to parse image");
        }
    }

    private ImagePixels parsePixels(String base64DataUri) throws IOException {
        String base64Image = base64DataUri.substring(URI_PREFIX.length());
        byte[] bytes = Base64.decodeBase64(base64Image);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        int width = image.getWidth();
        int height = image.getHeight();

        double[][] pixels = new double[height][];

        for (int y = 0; y < height; y++) {
            pixels[y] = new double[width];
            for (int x = 0; x < width; x++) {
                var pixelColor = new Color(image.getRGB(x, y));
                if (isNotGrey(pixelColor)) {
                    throw new InvalidInputException("Image has invalid color scheme");
                }
                pixels[y][x] = 100 - 100.0 * pixelColor.getRed() / 255;
            }
        }

        return new ImagePixels(width, height, pixels);
    }

    private boolean isNotGrey(Color color) {
        return !(color.getRed() == color.getGreen() && color.getGreen() == color.getBlue());
    }
}
