import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Millena on 21/06/2017.
 */
public class Equalize {

    public static int[] getMinRGB( int[][] histogram){
        int hMinR = 0;
        int hMinG = 0;
        int hMinB = 0;
        for (int i = 0; i < 256; i++) {
            if ((histogram[i][0] > 0) && (hMinR == 0)) {
                hMinR = histogram[i][0];
            }

            if ((histogram[i][1] > 0) && (hMinG == 0)) {
                hMinG = histogram[i][1];
            }

            if ((histogram[i][2] > 0) && (hMinB == 0)) {
                hMinB = histogram[i][2];
            }
            if (hMinR != 0 && hMinG != 0 && hMinB != 0) {
                break;
            }
        }
        int[] hMins = {hMinR,hMinG,hMinB};

        return hMins;
    }

    public static int[][] HistTablePB(BufferedImage img)
    {
        int histTable[][] = new int[256][4];
        int tom = 0;

        //HIST TABLE PRETO E BRANCO
     /*   for (int y = 0; y < img.getHeight(); y++)
        {
            for (int x = 0; x < img.getWidth(); x++)
            {
                Color pixel = new Color(img.getRGB(x, y));
                tom = (pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3;
                histTable[tom][3]++;
            }
        }*/

        //HIST TABLE COLORIDA

        //RED
        for (int y = 0; y < img.getHeight(); y++)
        {
            for (int x = 0; x < img.getWidth(); x++)
            {
                Color pixel = new Color(img.getRGB(x, y));
                tom = pixel.getRed();
                histTable[tom][0]++;
            }
        }

        //GREEN
        for (int y = 0; y < img.getHeight(); y++)
        {
            for (int x = 0; x < img.getWidth(); x++)
            {
                Color pixel = new Color(img.getRGB(x, y));
                tom = pixel.getGreen();
                histTable[tom][1]++;
            }
        }

        //BLUE
        for (int y = 0; y < img.getHeight(); y++)
        {
            for (int x = 0; x < img.getWidth(); x++)
            {
                Color pixel = new Color(img.getRGB(x, y));
                tom = pixel.getBlue();
                histTable[tom][2]++;
            }
        }

        return histTable;
    }

    public static int[][] AcumHist(int[][] hist, int w, int h)
    {
        int ha[][] = new int[256][4];

        //PRETO E BRANCO
        for (int i = 1; i < 256; i++)
        {
            ha[i][3] = hist[i][3] + ha[i - 1][3];
        }

        //red
        for (int i = 1; i < 256; i++)
        {
            ha[i][0] = hist[i][0] + ha[i - 1][0];
        }

        //green
        for (int i = 1; i < 256; i++)
        {
            ha[i][1] = hist[i][1] + ha[i - 1][1];
        }

        //blue
        for (int i = 1; i < 256; i++)
        {
            ha[i][2] = hist[i][2] + ha[i - 1][2];
        }

        return ha;
    }

    public static void EqSaturation(BufferedImage img, int[][] histogram, int[][] ha) throws IOException {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        float[] tomHSV;

        float nT[][][] = new float[img.getHeight()][img.getWidth()][3];
        int tom[] = new int[3];


       int[] hMins = getMinRGB(histogram);


        int[][] tomEqualizado = new int[256][3];
        //USA FORMULA PRA EQUALIZAR CADA TOM
        for (int i = 0; i < 256; i++) {
            tomEqualizado[i][0] = Math.round ((ha[i][0] - hMins[0]) / ((img.getHeight() * img.getWidth()) - hMins[0]) * (256 - 1));
            tomEqualizado[i][1] = Math.round ((ha[i][1] - hMins[1]) / ((img.getHeight() * img.getWidth()) - hMins[1]) * (256 - 1));
            tomEqualizado[i][2] = Math.round ((ha[i][2] - hMins[2]) / ((img.getHeight() * img.getWidth()) - hMins[2]) * (256 - 1));

        }

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color pixel = new Color(img.getRGB(x, y));
                tomHSV = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);
                tom[0] = pixel.getRed();
                tom[1] = pixel.getGreen();
                tom[2] = pixel.getBlue();

                nT[y][x][0] = tomEqualizado[tom[0]][0];
                nT[y][x][1] = tomEqualizado[tom[1]][1];
                nT[y][x][2] = tomEqualizado[tom[2]][2];

                Color pixel2 = new Color((int) nT[y][x][0], (int) nT[y][x][1], (int) nT[y][x][2]);

                float PixelHSV[] = Color.RGBtoHSB(pixel2.getRed(), pixel2.getGreen(), pixel2.getBlue(), null);
                PixelHSV[0] = tomHSV[0];
                PixelHSV[2] = tomHSV[2] + 0.15f;

                //Passa novamente pra RGB pra podermos gravar na imagem

                int pixel3 = Color.HSBtoRGB(PixelHSV[0], PixelHSV[1], PixelHSV[2]);
                                out.setRGB(x, y, pixel3);
            }
        }

        ImageIO.write(out, "png", new File("eqS.png"));
    }

    public static void EqBright(BufferedImage img,int[][] histogram, int acumHist[][]) throws IOException
    {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        float[] tomHSV;

        float nT[][][] = new float[img.getHeight()][img.getWidth()][3];
        int tom[] = new int[3];

        int[] hMins = getMinRGB(histogram);


        int[][] tomEqualizado = new int[256][3];

        //USA FORMULA PRA EQUALIZAR CADA TOM
        for (int i = 0; i < 256; i++)
        {
            tomEqualizado[i][0] = (int)(((float)(acumHist[i][0] - hMins[0]) / ((img.getHeight()*img.getWidth()) - hMins[0])) *(256-1));
            tomEqualizado[i][1] = (int)(((float)(acumHist[i][1] - hMins[1]) / ((img.getHeight()*img.getWidth()) - hMins[1])) *(256-1));
            tomEqualizado[i][2] = (int)(((float)(acumHist[i][2] - hMins[2]) / ((img.getHeight()*img.getWidth()) - hMins[1])) *(256-1));
        }

        for (int y = 0; y < img.getHeight(); y++)
        {
            for (int x = 0; x < img.getWidth(); x++)
            {
                Color pixel = new Color(img.getRGB(x, y));
                tomHSV = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);
                tom[0] = pixel.getRed();
                tom[1] = pixel.getGreen();
                tom[2] = pixel.getBlue();

                nT[y][x][0] = tomEqualizado[tom[0]][0];
                nT[y][x][1] = tomEqualizado[tom[1]][1];
                nT[y][x][2] = tomEqualizado[tom[2]][2];


                Color pixel2 = new Color((int)nT[y][x][0], (int)nT[y][x][1], (int)nT[y][x][2]);


                float PixelHSV[] = Color.RGBtoHSB(pixel2.getRed(), pixel2.getGreen(), pixel2.getBlue(), null);
                PixelHSV[0] = tomHSV[0];
                PixelHSV[1] = tomHSV[1];

                //Passa novamente pra RGB pra podermos gravar na imagem

                int pixel3 = Color.HSBtoRGB(PixelHSV[0], PixelHSV[1], PixelHSV[2]);

                out.setRGB(x, y, pixel3);
            }
        }

        ImageIO.write(out, "png", new File("eqB.png"));
    }

    public static void run() throws IOException, ArithmeticException {
        BufferedImage img = ImageIO.read(new File("lara.png"));

        int hist[][];
        hist=HistTablePB(img);
        int ha[][] = AcumHist(hist, img.getWidth(), img.getHeight());

        EqSaturation(img, hist, ha);
        EqBright(img,hist,ha);
    }

    public static void main(String[] args) throws IOException {
    Equalize.run();
    }
}