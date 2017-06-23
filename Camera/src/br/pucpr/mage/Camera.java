package br.pucpr.mage;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

/**
 * Created by Millena on 14/06/2017.
 */
public class Camera {

    private	Vector3f	position	=	new	Vector3f(0,0,2);
    private	Vector3f	up	=	new	Vector3f(0,	1,	0);
    private Vector3f target	=	new	Vector3f(0,0,-1);

    private Vector3f direction = new Vector3f(0,0,-1);
    private float angle = 0;

    private float fov	=	(float)Math.toRadians(60);
    private float near	=	0.1f;
    private float far	=	1000.0f;

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getUp() {
        return up;
    }

    public Vector3f getTarget() {
        return target;
    }

    private float getAspect()	{
        IntBuffer w	=	BufferUtils.createIntBuffer(1);
        IntBuffer h	=	BufferUtils.createIntBuffer(1);
        long window	=	glfwGetCurrentContext();
        glfwGetWindowSize(window,	w,	h);
        return w.get()	/	(float)h.get();
    }

    //posiciona a camera
    public Matrix4f getViewMatrix()	{
        return new	Matrix4f().lookAt(position,	target,	up);
    }

    public	Matrix4f	getProjectionMatrix()	{
        return new	Matrix4f().perspective(
                fov,	getAspect(),	near,	far);
    }

    public void moveFront(float distance){
        Vector3f outFront = new Vector3f(0,0,0);

        direction.mul(distance,outFront);

        position.add(outFront);
    }

    public void strafeLeft(float distance){
        Vector3f outStrafe = new Vector3f(0,0,0);

        up.cross(direction,outStrafe);

        outStrafe.mul(distance);

        position.add(outStrafe);
    }

    public void strafeRight(float distance){
        Vector3f outStrafe = new Vector3f(0,0,0);

        up.cross(direction,outStrafe);

        outStrafe.mul(distance);

        position.add(outStrafe);
    }

    public void rotate(float rad){
        angle=rad;
        direction.mul(new Matrix3f().rotateY(angle));
    }
}
