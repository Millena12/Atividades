package br.pucpr.cg;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import br.pucpr.mage.*;
import org.joml.Matrix4f;

public class RotatingCube implements Scene {
    private Keyboard keys = Keyboard.getInstance();

    private Camera camera = new Camera();
    private Mesh mesh;
    private float angle;
    private float angleX;

    @Override
    public void init() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mesh = MeshFactory.createCube();
    }

    @Override
    public void update(float secs) {
        if (keys.isPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(glfwGetCurrentContext(), GLFW_TRUE);
            return;
        }
        //move front
        if(keys.isDown(GLFW_KEY_UP)){
            camera.moveFront(0.1f);
        }

        if(keys.isDown(GLFW_KEY_DOWN)){
            camera.moveFront(-0.1f);
        }

        //rotaciona camera
        if(keys.isDown(GLFW_KEY_LEFT)){
            camera.rotate((float)Math.toRadians(180)*secs);
        }
        if (keys.isDown(GLFW_KEY_RIGHT)) {
            camera.rotate(-(float)(Math.toRadians(180) * secs));
        }

        //strafe

        if (keys.isDown(GLFW_KEY_Z)) {
            camera.strafeRight(-0.2f);
        }
        if (keys.isDown(GLFW_KEY_C)) {
            camera.strafeLeft(0.2f);
        }

        //gira o cubo
        if (keys.isDown(GLFW_KEY_A)) {
            angle += Math.toRadians(180) * secs;
        }

        if (keys.isDown(GLFW_KEY_D)) {
            angle -= Math.toRadians(180) * secs;
        }
        if (keys.isDown(GLFW_KEY_W)) {
            angleX -= Math.toRadians(180) * secs;
        }

        if(keys.isDown(GLFW_KEY_S)){
            angleX += Math.toRadians(180) * secs;
        }
    }

    @Override
    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        mesh.getShader()
                .bind()
                .setUniform("uProjection",	camera.getProjectionMatrix())
                .setUniform("uView",	camera.getViewMatrix())
                .unbind();

        mesh.setUniform("uWorld", new Matrix4f().rotateY(angle).rotateX(angleX));
        mesh.draw();
    }

    @Override
    public void deinit() {
    }

    public static void main(String[] args) {
        new Window(new RotatingCube(), "Rotating cube", 800, 600).show();
    }
}
