package gene.game.renderer;

import java.util.ArrayList;
import java.util.List;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Loader {
	
	private List<Integer> VAOs = new ArrayList<Integer>();
	private List<Integer> VBOs = new ArrayList<Integer>();
	
	public RawModel loadToVAO(float[] positions, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, positions);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
//	public int loadTexture(String fileName) {
//		Texture texture = null;
//		texture = TextureLoader.getTexture("PNG", new FileInputStream("res/"+ fileName+".png"));
//	}
	
	public void cleanUp() {
		for (int vao: VAOs) {
			glDeleteVertexArrays(vao);
		}
		for (int vbo: VBOs) {
			glDeleteBuffers(vbo);
		}
	}
	
	private int createVAO() {
		int vaoID = glGenVertexArrays();
		VAOs.add(vaoID);
		glBindVertexArray(vaoID);
		return vaoID;
	}
	
	private void storeDataInAttributeList(int attributeNum, float[] data) {
		int vboID = glGenBuffers();
		VBOs.add(vboID);
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		
		// Once the buffer is bind, you can store data into it
		// data has to be stored in VBO as a FloatBuffer
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(attributeNum, 3, GL_FLOAT, false, 0, 0);
		
		// unbind buffer
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	private void bindIndicesBuffer(int[] indices) {
		int vboID = glGenBuffers();
		VBOs.add(vboID);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
	}

	private void unbindVAO() {
		glBindVertexArray(0);
	}
	
	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		// create empty FloatBuffer
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		
		buffer.put(data);
		
		// prepare to be read from VBO
		buffer.flip();
		
		return buffer;
	}
}
