package gene.game.engine;

import java.io.*;
import java.net.URL;
import java.util.HashMap;

public class Setting {
	
	private static HashMap<String, Integer> settings;
	public static final String FULLSCREEN = "Fullscreen";
	
	public Setting() {
		init();
		readSettingsFile();
	}
	
	private void init() {
		settings = new HashMap<>();
		settings.put(FULLSCREEN, 0);
	}
	
	private void readSettingsFile() {
		URL fileURL = Setting.class.getResource("/res/configurations/settings");
		File file = new File(fileURL.getFile());
		
		String line;
		String[] columns;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
		
			try {	
				while ((line = reader.readLine()) != null) {
					columns = line.split("=");
					checkValue(columns[0], columns[1]);
				}
			}
			finally {
				reader.close();
			}
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void checkValue(String setting, String value) throws IOException {
		switch(setting) {
		case FULLSCREEN:
			if (value.equals("true")) {
				settings.put(FULLSCREEN, 1);
			}
			else if (value.equals("false")) {
				settings.put(FULLSCREEN, 0);
			}
			else {
				throw new IOException("Wrong value in "+ FULLSCREEN +" in settings");
			}
			break;
		}
	}

	public static int get(String setting) {
		return settings.get(setting);
	}
}
