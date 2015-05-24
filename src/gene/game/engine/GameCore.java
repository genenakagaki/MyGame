package gene.game.engine;

import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import gene.game.renderer.*;
import gene.game.shaders.StaticShader;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
 
public class GameCore {
 
    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
 
    // The window handle
    private long window;
    
    private Loader loader = new Loader();
    private Renderer renderer = new Renderer();
    private StaticShader shader;
    
    private float[] vertices = {
    		-0.5f, 0.5f, 0f,
    		-0.5f, -0.5f, 0f,
    		0.5f, -0.5f, 0f,
    		0.5f, 0.5f, 0f
    };
    private int[] indices = {
    		0, 1, 3,
    		3, 1, 2
    };
    
    RawModel model;
    
    private boolean running = true;
 
    public void run() {
        System.out.println("Hello LWJGL " + Sys.getVersion() + "!");
 
        try {
            init();
            loop();
 
            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.release();
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            errorCallback.release();
        }
    }
 
    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
 
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( glfwInit() != GL11.GL_TRUE )
            throw new IllegalStateException("Unable to initialize GLFW");
 
        new Setting();
        
        createWindow();
        
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new KeyListener());
        
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLContext.createFromCurrent();
 
        // Set the clear color
//        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        
        model = loader.loadToVAO(vertices, indices);
        shader = new StaticShader();
    }
 
    private void loop() {
    	double previousTime = glfwGetTime();
    	double currentTime;
    	double deltaTime;
    	
    	int ups = 60;
    	double tickInterval = 1.0/ups;
    	
    	int updates = 0;
    	int frames = 0;
    	long timer = System.currentTimeMillis();
    	
        while (running) {
        	currentTime = glfwGetTime();
        	deltaTime = currentTime - previousTime;
        	
        	while (deltaTime - tickInterval >= 0) {
        		update();
        		updates++;
        		previousTime += tickInterval;
        		deltaTime = currentTime - previousTime;
        	}
        	render();
        	frames++;
        	
        	if (System.currentTimeMillis() - timer >= 1000) {
        		System.out.println("ups: "+ updates + ", fps: "+ frames);
        		updates = 0;
        		frames = 0;
        		timer += 1000;
        	}
        }
        shader.cleanUp();
        loader.cleanUp();
    }
    
    public void update() {
    	if (glfwWindowShouldClose(window) == GL_TRUE) {
    		stopGame();
    	}
    	
    	// Poll for window events. The key callback above will only be
    	// invoked during this call.
    	glfwPollEvents();
    }
    
    public void render() {
//    	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
    	    	
    	glfwSwapBuffers(window); // swap the color buffers
    	
    	renderer.prepare();
    	shader.start();
    	renderer.render(model);
    	shader.stop();
    }
    
    public void stopGame() {
    	running = false;
    }
    
    private void createWindow() {
        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
//        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
//        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        
        int WIDTH = 800;
        int HEIGHT = 640;
 
        if (Setting.get(Setting.FULLSCREEN) == 0) {
        	window = glfwCreateWindow(WIDTH, HEIGHT, "My Game", NULL, NULL);
        	if ( window == NULL )
        		throw new RuntimeException("Failed to create the GLFW window");
        	
        	// Get the resolution of the primary monitor
        	ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        	// Center our window
        	glfwSetWindowPos(
        			window,
        			(GLFWvidmode.width(vidmode) - WIDTH) / 2,
        			(GLFWvidmode.height(vidmode) - HEIGHT) / 2
        			);
        }
        else {
        	window = glfwCreateWindow(WIDTH, HEIGHT, "My Game", glfwGetPrimaryMonitor(), NULL);
        	if ( window == NULL )
        		throw new RuntimeException("Failed to create the GLFW window");
        }
     
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }
 
    public static void main(String[] args) {
        new GameCore().run();
    }
 
}