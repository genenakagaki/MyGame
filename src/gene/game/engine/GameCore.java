package gene.game.engine;

import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
 
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
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
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
    	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
    	
    	glfwSwapBuffers(window); // swap the color buffers
    }
    
    public void stopGame() {
    	running = false;
    }
    
    private void createWindow() {
        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
 
        int WIDTH = 300;
        int HEIGHT = 300;
 
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