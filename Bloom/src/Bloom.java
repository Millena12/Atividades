import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Millena on 21/06/2017.
 */
public class Bloom {

    static BufferedImage add(BufferedImage img, BufferedImage img1) throws IOException
    {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int y = 0; y < img.getHeight(); y++)
        {
            for(int x = 0; x < img.getWidth(); x++)
            {
                Color pixel = new Color(img.getRGB(x,y));
                Color pixel2 = new Color(img1.getRGB(x,y));

                int r = pixel2.getRed() + pixel.getRed();
                int g = pixel2.getGreen() + pixel.getGreen();
                int b = pixel2.getBlue() + pixel.getBlue();

                r = r < 0 ? r = 0 :(r > 255 ? r = 255: r);
                g = g < 0 ? g = 0 :(g > 255 ? g = 255: g);
                b = b < 0 ? b = 0 :(b > 255 ? b = 255: b);

                Color result = new Color(r, g, b);
                out.setRGB(x, y, result.getRGB());
            }
        }
        return out;
    }


    public static BufferedImage BloomEach(BufferedImage img, int kernelSize)
    {
        BufferedImage out = new BufferedImage(img.getWidth(),img.getHeight(), BufferedImage.TYPE_INT_RGB);

        float cor = 0;

        float[] HSB = new float[3];

        for (int y = 0; y < img.getHeight(); y++)
        {
            for (int x = 0; x < img.getWidth(); x++)
            {
                for(int i = 0; i < kernelSize; i++ )
                {
                    for(int j = 0; j < kernelSize; j++)
                    {
                        int px = x + (i - 1);
                        int py = y + (j - 1);
                        if(px >= 0 && py >= 0 && px < img.getWidth() && py <img.getHeight())
                        {
                            Color cor2 = new Color(img.getRGB(px, py));

                            Color.RGBtoHSB(cor2.getRed(), cor2.getGreen(), cor2.getBlue(), HSB);
                            if( HSB[2] > 0.8f)
                            {
                                cor = (float)HSB[2] * (float)(1/(float)kernelSize);
                                out.setRGB(x, y, Color.HSBtoRGB(HSB[0], HSB[1], cor));
                            }
                        }
                    }
                }
            }
        }
        return out;
    }

    public static BufferedImage Bloom(BufferedImage img) throws IOException
    {
        BufferedImage bloom = BloomEach(img, 5);
        img = add(img, bloom);
        bloom = BloomEach(img, 11);
        img = add(img, bloom);
        bloom = BloomEach(img, 21);
        img = add(img, bloom);
        bloom = BloomEach(img, 41);
        img = add(img, bloom);

        return img;
    }


    public void run() throws IOException {
            BufferedImage img = ImageIO.read(new File("mario.jpg"));

            BufferedImage img2 = Bloom(img);
            ImageIO.write(img2,"jpg",new File ("Bloom.jpg"));
    }

    public static void main(String[] args) throws IOException {
        new Bloom().run();
    }
}
