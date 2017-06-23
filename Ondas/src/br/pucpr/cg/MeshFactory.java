package br.pucpr.cg;

import java.io.File;
import java.io.IOException;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.joml.Vector3f;

import br.pucpr.mage.Mesh;
import br.pucpr.mage.MeshBuilder;

import javax.imageio.ImageIO;

public class MeshFactory {

    public static Mesh Wave(int width, int depth){
        float hw = width / 2.0f;
        float hd = depth / 2.0f;

        // Criação dos vértices
        List<Vector3f> positions = new ArrayList<>();
        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {

                positions.add(new Vector3f(x - hw, 0, z - hd));
            }
        }




        //Criação dos índices
        List<Integer> indices = new ArrayList<>();
        for (int z = 0; z < depth - 1; z++) {
            for (int x = 0; x < width - 1; x++) {
                int zero = x + z * width;
                int one = (x + 1) + z * width;
                int two = x + (z + 1) * width;
                int three = (x + 1) + (z + 1) * width;

                indices.add(zero);
                indices.add(three);
                indices.add(one);

                indices.add(zero);
                indices.add(two);
                indices.add(three);
            }
        }

    List<Vector3f> pixelColor = new ArrayList<>();
        //Calculo das normais
        for (int i = 0; i < depth; i ++) {
        for(int j=0;j<width;j++){
            pixelColor.add(new Vector3f(0.2f,0.5f,0.3f));
        }
        }


        return new MeshBuilder()
                .addVector3fAttribute("aPosition", positions)
                .addVector3fAttribute("aColor", pixelColor)
                .setIndexBuffer(indices)
                .loadShader("/br/pucpr/resource/wave")
                .create();
    }

    public static Mesh Sphere(int height, int width){


        List<Vector3f> normals = new ArrayList<>();
        List<Vector3f> positions = new ArrayList<>();

        for (int z = 0; z <= height; z++) {

            double zenite = z * Math.PI/width;
            double senoZenite = Math.sin(zenite);
            double cosZenite = Math.cos(zenite);

            for (int x = 0; x <= width; x++) {
                double azimute = (x * (Math.PI * 2))/width;
                double senoAzimute = Math.sin(azimute);
                double cosAzimute = Math.cos(azimute);

                Vector3f aux =new Vector3f((float)(cosAzimute * senoZenite),(float)cosZenite , (float)(senoZenite*senoAzimute));

                positions.add(aux);
                normals.add(new Vector3f(aux.x,aux.y,-aux.z).normalize());
            }
        }

        List<Integer> indices = new ArrayList<>();
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                int zero = x + z * width;
                int one = (x + 1) + z * width;
                int two = x + (z + 1) * width;
                int three = (x + 1) + (z + 1) * width;

                indices.add(zero);
                indices.add(three);
                indices.add(one);

                indices.add(zero);
                indices.add(two);
                indices.add(three);
            }
        }

        return new MeshBuilder()
                .addVector3fAttribute("aPosition", positions)
                .addVector3fAttribute("aNormal", normals)
                .setIndexBuffer(indices)
                .loadShader("/br/pucpr/resource/phong")
                .create();

    }


    public static Mesh loadTerrain(File file, float scale) throws IOException {
        BufferedImage img = ImageIO.read(file);

        int width = img.getWidth();
        int depth = img.getHeight();

        float hw = width / 2.0f;
        float hd = depth / 2.0f;

        // Criação dos vértices
        List<Vector3f> positions = new ArrayList<>();
        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
              /*
                int r = new Color(img.getRGB(x, y)).getRed();
                int g = new Color(img.getRGB(x, y)).getGreen();
                int b = new Color(img.getRGB(x, y)).getBlue();

                int gray = (r + g + b) / 3;
                */

                int tone = new Color(img.getRGB(x, z)).getRed();
                positions.add(new Vector3f(x - hw, tone * scale, z - hd));
            }
        }




        //Criação dos índices
        List<Integer> indices = new ArrayList<>();
        for (int z = 0; z < depth - 1; z++) {
            for (int x = 0; x < width - 1; x++) {
                int zero = x + z * width;
                int one = (x + 1) + z * width;
                int two = x + (z + 1) * width;
                int three = (x + 1) + (z + 1) * width;

                indices.add(zero);
                indices.add(three);
                indices.add(one);

                indices.add(zero);
                indices.add(two);
                indices.add(three);
            }
        }

        List<Vector3f> normals = new ArrayList<Vector3f>();
        for (int i = 0; i < indices.size(); i++) {
            normals.add(new Vector3f());
        }
        //Calculo das normais
        for (int i = 0; i < indices.size(); i += 3) {
            int i1 = indices.get(i);
            int i2 = indices.get(i+1);
            int i3 = indices.get(i+2);

            Vector3f v1 = positions.get(i1);
            Vector3f v2 = positions.get(i2);
            Vector3f v3 = positions.get(i3);

            Vector3f side1 = new Vector3f(v2).sub(v1);
            Vector3f side2 = new Vector3f(v3).sub(v1);

            Vector3f normal = new Vector3f(side1).cross(side2);

            normals.get(i1).add(normal);
            normals.get(i2).add(normal);
            normals.get(i3).add(normal);
        }

        for (Vector3f normal : normals) {
            normal.normalize();
        }


        return new MeshBuilder()
                .addVector3fAttribute("aPosition", positions)
                .addVector3fAttribute("aNormal", normals)
                .setIndexBuffer(indices)
                .loadShader("/br/pucpr/resource/phong")
                .create();
    }

}
