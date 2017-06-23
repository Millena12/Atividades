import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Millena on 23/06/2017.
 */
public class Dithering {
    class Palletes {
        int[] pallete64 = {
                0x000000, 0x00AA00, 0x0000AA, 0x00AAAA, 0xAA0000, 0xAA00AA, 0xAAAA00, 0xAAAAAA,
                0x000055, 0x0000FF, 0x00AA55, 0x00AAFF, 0xAA0055, 0xAA00FF, 0xAAAA55, 0xAAAAFF,
                0x005500, 0x0055AA, 0x00FF00, 0x00FFAA, 0xAA5500, 0xAA55AA, 0xAAFF00, 0xAAFFAA,
                0x005555, 0x0055FF, 0x00FF55, 0x00FFFF, 0xAA5555, 0xAA55FF, 0xAAFF55, 0xAAFFFF,
                0x550000, 0x5500AA, 0x55AA00, 0x55AAAA, 0xFF0000, 0xFF00AA, 0xFFAA00, 0xFFAAAA,
                0x550055, 0x5500FF, 0x55AA55, 0x55AAFF, 0xFF0055, 0xFF00FF, 0xFFAA55, 0xFFAAFF,
                0x555500, 0x5555AA, 0x55FF00, 0x55FFAA, 0xFF5500, 0xFF55AA, 0xFFFF00, 0xFFFFAA,
                0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
        };

        int[] pallete48 = {
                0xD2E3F5, 0x2F401E, 0x3E0A11, 0x4B3316,
                0xA5BDE5, 0x87A063, 0x679327, 0x3A1B0F,
                0x928EB1, 0xBFE8AC, 0xA4DA65, 0x5A3810,
                0x47506D, 0x98E0E8, 0x989721, 0x8E762C,
                0x0B205C, 0x55BEd7, 0xB8B366, 0xD8C077,
                0x134D9C, 0x2A6E81, 0xE1EAB6, 0xF0DEA6,
                0xFFF3D0, 0x610A0A, 0x7D000E, 0x45164B,
                0xFFFCCC, 0x6B330F, 0x990515, 0x250D3B,
                0xB24801, 0x8B4517, 0xE0082D, 0x50105A,
                0xFFF991, 0xB96934, 0xC44483, 0x8E2585,
                0xDF5900, 0xF8A757, 0xC44483, 0xD877CF,
                0xFFEF00, 0xDF7800, 0xF847CE, 0xF0A6E8

        };

        int[] pallete16 = { // 4bit pallete
                0x000000, 0x800000, 0xFF0000, 0xFF00FF,
                0xFF8080, 0x008000, 0x00FF00, 0x00FFFF,
                0x000080, 0x800080, 0x0000FF, 0xC0C0C0,
                0x808080, 0x808000, 0xFFFF00, 0xFFFFFF
        };
    }

    public float getcolordistance(Color color1, Color color2){
        float r = Math.abs(color1.getRed() - color2.getRed());
        r = r*r;
        float g = Math.abs(color1.getGreen() - color2.getGreen());
        g = g*g;
        float b = Math.abs(color1.getBlue() - color2.getBlue());
        b = b*b;
        return (float) Math.sqrt(r+g+b);

    }


    public int convertcolorto(Color color, int typepallete){
        int pallete[];
        if(typepallete == 64)
            pallete = new Palletes().pallete64;
        else if(typepallete == 48)
            pallete = new Palletes().pallete48;
        else
            pallete = new Palletes().pallete16;

        float aux = getcolordistance(color, new Color(pallete[0]));
        int closedcolor = pallete[0];

        for(int i = 1; i < pallete.length; i++){
            float tmp = getcolordistance(color, new Color(pallete[i]));
            if(tmp < aux){
                aux = tmp;
                closedcolor = pallete[i];
            }
        }
        return closedcolor;

    }


    public Color getnewcolor(Color pixel, int[] quant_error, float weight){
        //System.out.println(weight);
        Color out;
        int r = (int)(pixel.getRed()    + quant_error[0]     * weight);
        int g = (int)(pixel.getGreen()  + quant_error[1]     * weight);
        int b = (int)(pixel.getBlue()   + quant_error[2]     * weight);

        r = r > 255 ? 255 : r < 0 ? 0 : r;
        g = g > 255 ? 255 : g < 0 ? 0 : g;
        b = b > 255 ? 255 : b < 0 ? 0 : b;

        out = new Color(r, g, b);
        return out;
    }


    public int[] subtractcolor(Color color1, Color color2){
        int r = color1.getRed()    - color2.getRed();
        int g = color1.getGreen()  - color2.getGreen();
        int b = color1.getBlue()   - color2.getBlue();
        int[] s = {r, g, b};

        return s;
    }


    public BufferedImage convert(BufferedImage img, int typepallete){
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);


        for(int y = 0; y < img.getHeight(); y++){
            for(int x = 0; x < img.getWidth(); x++){
                Color oldpixel = new Color(out.getRGB(x, y));
                Color newpixel = new Color(convertcolorto(oldpixel, typepallete));


                out.setRGB(x, y, newpixel.getRGB());

                Color originalpixel = new Color(img.getRGB(x, y));

                out.setRGB(x , y , convertcolorto(originalpixel, typepallete));



                int[] quant_error = subtractcolor(oldpixel, newpixel);

                if(x + 1 < img.getWidth())
                    out.setRGB(x + 1,       y, getnewcolor(new Color(out.getRGB(x + 1, y        )), quant_error, (7.f/16.f)).getRGB());

                if((x - 1 > 0) && (y + 1 < img.getHeight()))
                    out.setRGB(x - 1, y + 1, getnewcolor(new Color(out.getRGB(x - 1, y + 1)), quant_error, (3.f/16.f)).getRGB());

                if(y + 1 < img.getHeight())

                    out.setRGB(x       , y + 1, getnewcolor(new Color(out.getRGB(x      , y + 1)), quant_error, (5.f/16.f)).getRGB());

                if((x + 1 < img.getWidth()) && (y + 1 < img.getHeight()))
                    out.setRGB(x + 1, y + 1, getnewcolor(new Color(out.getRGB(x + 1, y + 1)), quant_error, (1.f/16.f)).getRGB());

            }
        }



        return out;
    }



    public void run() throws IOException {
        BufferedImage img = ImageIO.read(new File("puppy.png"));

        BufferedImage paleta64 = convert(img, 64);
        ImageIO.write(paleta64, "png", new File("64.png"));

        BufferedImage paleta48 = convert(img, 48);
        ImageIO.write(paleta48, "png", new File("48.png"));

        BufferedImage paleta16 = convert(img, 16);
        ImageIO.write(paleta16, "png", new File("16.png"));



    }

    public static void main(String[] args) throws IOException {
        new Dithering().run();
    }

}
