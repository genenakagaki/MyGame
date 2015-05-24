package gene.game.shaders;

public class StaticShader extends ShaderProgram {
	
	private static final String VERTEX_FILE   = "src/gene/game/shaders/vertexShader";
	private static final String FRAGMENT_FILE = "src/gene/game/shaders/fragmentShader";
	
	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
