package br.pucpr.cg;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.joml.Matrix4f;

import br.pucpr.mage.Keyboard;
import br.pucpr.mage.Mesh;
import br.pucpr.mage.Scene;
import br.pucpr.mage.Shader;
import br.pucpr.mage.Window;
import org.joml.Vector3f;

import br.pucpr.mage.phong.DirectionalLight;
import br.pucpr.mage.phong.Material;

public class LitCube implements Scene {
    private Keyboard keys = Keyboard.getInstance();
    
    private Mesh mesh;
    private float angleX = 0.0f;
    private float angleY = 0.5f;
    private Camera camera = new Camera();

    private Vector3f lightDir = new Vector3f(0.0f, 1.0f, 1.0f);

    private float degX = 0.0f;
    private float specPower = 1.0f;


    private Material material = new Material(
            //ambientColor
            new Vector3f(0.5f, 0.0f, 0.5f),
            //diffuseColor
            new Vector3f(0.5f, 0.0f, 0.5f),
            //specular
            new Vector3f(1.0f, 1.0f, 1.0f),
            32.0f);

    private DirectionalLight light = new DirectionalLight(
            //direction
            new Vector3f( 1.0f, -1.0f, -1.0f),
            //ambient
            new Vector3f( 0.1f,  0.1f,  0.1f),
            //diffuse
            new Vector3f( 1.0f,  1.0f,  0.8f),
            //specular
            new Vector3f( 1.0f,  1.0f,  1.0f));

    @Override
    public void init() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mesh = MeshFactory.createCube();
        camera.getPosition().y = 1.0f;
    }

    @Override
    public void update(float secs) {
        if (keys.isPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(glfwGetCurrentContext(), GLFW_TRUE);
            return;
        }

        if (keys.isDown(GLFW_KEY_A) || keys.isDown(GLFW_KEY_LEFT)) {
            angleY += Math.toRadians(180) * secs;
        }

        if (keys.isDown(GLFW_KEY_D)|| keys.isDown(GLFW_KEY_RIGHT)) {
            angleY -= Math.toRadians(180) * secs;
        }
        
        if (keys.isDown(GLFW_KEY_W)|| keys.isDown(GLFW_KEY_UP)) {
            angleX += Math.toRadians(180) * secs;
        }

        if (keys.isDown(GLFW_KEY_S)|| keys.isDown(GLFW_KEY_DOWN)) {
            angleX -= Math.toRadians(180) * secs;
        }

        if(keys.isDown(GLFW_KEY_J)){
            degX += 10f;
        }

        if(keys.isDown(GLFW_KEY_K)){
            degX -= 10f;
        }

        if(keys.isDown(GLFW_KEY_E)){
            specPower += 0.1f;

            System.out.println(specPower);
        }
        if(keys.isDown(GLFW_KEY_Q)){
            specPower -= 0.1f;

            System.out.println(specPower);
        }

    }

    private Vector3f vecByAngle (float deg)
    {
        double rad = Math.toRadians(deg);

        return new Vector3f((float) Math.cos(rad), 0.0f, (float)Math.sin(rad));
    }

@Override
public void draw() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    
    Shader shader = mesh.getShader();
    shader.bind()
            .setUniform("uProjection", camera.getProjectionMatrix())
            .setUniform("uView", camera.getViewMatrix())
            .setUniform("uCameraPosition", camera.getPosition())

            .setUniform("uAmbientLight", new Vector3f(0.05f, 0.05f, 0.05f))
            .setUniform("uLightDir", new Vector3f(1.0f, -1.0f, -1.0f));
    light.apply(shader);
    material.apply(shader);
    shader.unbind();

    mesh.setUniform("uWorld", new Matrix4f().rotateY(angleY).rotateX(angleX));
    mesh.draw();
}

    @Override
    public void deinit() {
    }

    public static void main(String[] args) {
        new Window(new LitCube(), "Cube with lights", 800, 600).show();
    }
}
