import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Millena on 21/06/2017.
 */
public class Linha {

    public void linha(BufferedImage img, int x1, int y1, int x2, int y2, Color color)  throws IOException {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        // equacao da reta
        double m;

        double dx = x2 - x1;
        double dy = y2 - y1;
        m = dy / dx;
        if (Math.abs(m) <= 1.0) {
            if (x1 > x2) {
                linha(img, x2, y2, x1, y1, color);
            }
            float y = y1;
            for (int i = x1; i < x2; i++) {
                out.setRGB(i, (int) y, color.getRGB());
                y += m;
            }
        } else {
            if (y1 > y2) {
                linha(img, x2, y2, x1, y1, color);
            }
            double my = 1.0f / m;
            double x = x1;
            for (int y = y1; y <= y2; y++) {
                out.setRGB((int) x, y, color.getRGB());
                x += my;
            }
        }

        ImageIO.write(out, "png", new File("linha.png"));
    }
    public void run() throws IOException {
        BufferedImage img = ImageIO.read(new File("mario.jpg"));
        linha(img,0,0,300,300, new Color(255,255,255));
    }

    public static void main(String[] args) throws IOException{
        new Linha().run();

    }

}
