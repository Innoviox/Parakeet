package com.axiom.game;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.DoubleBuffer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import com.axiom.engine.Scene;
import com.axiom.engine.Texture;
import com.axiom.engine.Window;
import com.axiom.InputHandler;
import com.axiom.engine.Item;
import com.axiom.engine.Mesh;
import com.axiom.engine.Renderer;

public class Game implements Scene {

    private int displxInc = 0;
    private int displyInc = 0;
    private int displzInc = 0;
    private int scaleInc = 0;
    private final Renderer renderer;
    private Item[] gameItems;
	private final InputHandler input = new InputHandler();
	private GLFWKeyCallback keyCallback;
	private GLFWMouseButtonCallback mouseButtonCallback;
	private GLFWScrollCallback scrollCallback;
	
	private float rx, ry, rz, x, y, z;
	private double[] oldMousePos;
    public Game() {
        renderer = new Renderer();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        // Create the Mesh
        float[] positions = new float[] {
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
                
                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,

                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,

                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,

                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f,
            };
            float[] textCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                
                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                
                // For text coords in top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                // For text coords in right face
                0.0f, 0.0f,
                0.0f, 0.5f,

                // For text coords in left face
                0.5f, 0.0f,
                0.5f, 0.5f,

                // For text coords in bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
            };
            int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7,};
        Mesh mesh = new Mesh(positions, textCoords, indices, new Texture("/textures/grassBlock.png"));
        Item gameItem = new Item(mesh);
        gameItem.setPosition(0, 0, -2);
        
        Mesh mesh2 = new Mesh(positions, textCoords, indices, new Texture("/textures/grassBlock.png"));
        Item gameItem2 = new Item(mesh);
        gameItem2.setPosition(1, 0, -4);
        gameItems = new Item[] { gameItem, gameItem2};
        
		glfwSetKeyCallback(window.getWindowHandle(), keyCallback = input.keyboard);
		glfwSetMouseButtonCallback(window.getWindowHandle(), mouseButtonCallback = input.mouse);
		glfwSetScrollCallback(window.getWindowHandle(), scrollCallback = input.scroll);
    }

    @Override
    public void input(Window window) {
        displyInc = 0;
        displxInc = 0;
        displzInc = 0;
        scaleInc = 0;
        
		int mult;
		if (Math.cos(Math.toRadians(rx)) < 0) {
			mult = -1;
		} else {
			mult = 1;
		}
		if (input.keyDown(GLFW_KEY_LEFT))
			ry += mult;
		if (input.keyDown(GLFW_KEY_RIGHT))
			ry -= mult;
		if (input.keyDown(GLFW_KEY_DOWN))
			rx += 1;
		if (input.keyDown(GLFW_KEY_UP))
			rx -= 1;
		if(input.keyDown(GLFW_KEY_W))
			displyInc = 1;
		if(input.keyDown(GLFW_KEY_S))
			displyInc = -1;
		if(input.keyDown(GLFW_KEY_D))
			displxInc = 1;
		if(input.keyDown(GLFW_KEY_A))
			displxInc = -1;
		if (window.isKeyPressed(GLFW_KEY_E)) 
            displzInc = 1;
        if (window.isKeyPressed(GLFW_KEY_Q)) 
            displzInc = -1;
		
		if (input.mouseButtonDown(0)) {
			double[] newMousePos = getMousePosition();
			double ny = (newMousePos[0] - oldMousePos[0]);
			double nx = (newMousePos[1] - oldMousePos[1]);
			ry -= mult * ny;
			rx -= nx;
		}
		
		ry = (Math.abs(ry) % 360) * Math.signum(ry);
		rx = (Math.abs(rx) % 360) * Math.signum(rx);
		
		oldMousePos = getMousePosition();

		double zoomStep = input.getScrollStates()[1] / 1000;
		if (zoomStep > 0){
			scaleInc = 1;
		} else if (zoomStep < 0) {
			scaleInc = -1;
		} else {
			scaleInc = 0;
		}
    }

    @Override
    public void update(float interval) {
        for (Item gameItem : gameItems) {
            // Update position
            Vector3f itemPos = gameItem.getPosition();
            float posx = itemPos.x + displxInc * 0.01f;
            float posy = itemPos.y + displyInc * 0.01f;
            float posz = itemPos.z + displzInc * 0.01f;
            gameItem.setPosition(posx, posy, posz);
            
            // Update scale
            float scale = gameItem.getScale();
            scale += scaleInc * 0.05f;
            if ( scale < 0 ) {
                scale = 0;
            }
            System.out.println(scale + "," + scaleInc);
            gameItem.setScale(scale);
            
            // Update rotation angle
            /*
            float rotation = gameItem.getRotation().x + 1.5f;
            if ( rotation > 360 ) {
                rotation = 0;
            }
            */
            gameItem.setRotation(rx + y * 5, ry - x * 5, rz);
        }
    }
    
	public double[] getMousePosition() {
		DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(renderer.getWindow().getWindowHandle(), xBuffer, yBuffer);
		return new double[] { xBuffer.get(0), yBuffer.get(0) };
	}
	
    @Override
    public void render(Window window) {
        renderer.render(window, gameItems);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (Item gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }

}