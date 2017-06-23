package br.pucpr.cg;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import br.pucpr.mage.Keyboard;
import br.pucpr.mage.Mesh;
import br.pucpr.mage.Scene;
import br.pucpr.mage.Shader;
import br.pucpr.mage.Window;
import br.pucpr.mage.phong.DirectionalLight;
import br.pucpr.mage.phong.Material;

public class TerrainLoader implements Scene {
    private Keyboard keys = Keyboard.getInstance();



    //Dados da cena
    private Camera camera = new Camera();
    private DirectionalLight light = new DirectionalLight(
            new Vector3f( 1.0f, -1.0f, -1.0f), //direction
            new Vector3f( 0.5f,  0.5f,  0.5f),   //ambient
            new Vector3f( 1.0f,  1.0f,  0.8f),   //diffuse
            new Vector3f( 1.0f,  1.0f,  1.0f));  //specular

    //Dados da malha
    private Mesh mesh;
    private Material material = new Material(
            new Vector3f(0.5f, 0.5f, 0.5f), //ambient
            new Vector3f(0.5f, 0.5f, 0.5f), //diffuse
            new Vector3f(0.5f, 0.5f, 0.5f), //specular
            100.0f);                         //specular power    
    private float angleX = 0.0f;
    private float angleY = 0.5f;
    private float time = 0;
    @Override
    public void init() {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

            mesh = MeshFactory.Wave(30,30);


       camera.getPosition().y = 50.0f;
        camera.getPosition().z = 50.0f;

       /* try {
			mesh = MeshFactory
					.loadTerrain(new File("bricks_t.jpg"), 0.5f));
		} catch (IOException e) {
			e.printStackTrace();
		}
        camera.getPosition().y = 1.0f;*/
    }

    @Override
    public void update(float secs) {
        if (keys.isPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(glfwGetCurrentContext(), GLFW_TRUE);
            return;
        }

        if (keys.isDown(GLFW_KEY_A)) {
            angleY += Math.toRadians(180) * secs;
        }

        if (keys.isDown(GLFW_KEY_D)) {
            angleY -= Math.toRadians(180) * secs;
        }
        
        if (keys.isDown(GLFW_KEY_W)) {
            angleX += Math.toRadians(180) * secs;
        }

        if (keys.isDown(GLFW_KEY_S)) {
            angleX -= Math.toRadians(180) * secs;
        }
        time+=secs;
    }

@Override
public void draw() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    
    Shader shader = mesh.getShader();
    shader.bind()
        .setUniform("uProjection", camera.getProjectionMatrix())
        .setUniform("uView", camera.getViewMatrix())
        .setUniform("uTime",time);
    shader.unbind();

  //  mesh.setUniform("uTime", time);
    mesh.setUniform("uWorld", new Matrix4f().rotateY(angleY).rotateX(angleX));
    mesh.draw();
}

    @Override
    public void deinit() {
    }

    public static void main(String[] args) {
        new Window(new TerrainLoader(), "TerrainLoader", 800, 600).show();
    }
}
