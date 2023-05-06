package fun.gbr.options;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Options {
	
	// Get saved preferences
	private Preferences prefs = Preferences.userNodeForPackage(getClass());
	
	private Path path;
	private int xOffset;
	private int yOffset;
	private int width;
	private int height;	
	
	public Options() {
		try {
			this.path = Path.of(prefs.get(PATH_KEY, DEFAULT_PATH));
		} catch(InvalidPathException e) {
			// Shouldn't happen
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e, () -> "Saved path preference is not a valid path! Resetting to default.");
			this.setPath(Path.of(DEFAULT_PATH));
		}
		this.xOffset = prefs.getInt(X_OFFSET_KEY, DEFAULT_X_OFFSET);
		this.yOffset = prefs.getInt(Y_OFFSET_KEY, DEFAULT_Y_OFFSET);
		this.width = prefs.getInt(WIDTH_KEY, DEFAULT_WIDTH);
		this.height = prefs.getInt(HEIGHT_KEY, DEFAULT_HEIGHT);
	}
	
	/** Saves value to prefs if this.savePrefs is true
	 * @param key		Preference key
	 * @param value		New preference value
	 */
	private void saveInt(String key, Integer value) {
		if(value != null) {
			this.prefs.putInt(key, value);
		}
	}
	
	/**
	 * Save options
	 */
	public void save() {
		if(path != null) {
			prefs.put(PATH_KEY, path.toString());
		}
		saveInt(X_OFFSET_KEY, xOffset);
		saveInt(Y_OFFSET_KEY, yOffset);
		saveInt(WIDTH_KEY, width);
		saveInt(HEIGHT_KEY, height);
	}
	
	/**
	 * Reset clear saved user preferences
	 */
	public void reset() {
		try {
			prefs.clear();
		} catch (BackingStoreException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e, () -> "Failed to clear preferences!");
		}
	}
	
	public Path getPath() {
		return path;
	}
	public void setPath(Path path) {
		this.path = path;
	}
	public int getXOffset() {
		return xOffset;
	}
	public void setXOffset(int xOffset) {
		this.xOffset = xOffset;
	}
	public int getYOffset() {
		return yOffset;
	}
	public void setYOffset(int yOffset) {
		this.yOffset = yOffset;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	@Override
	public String toString() {
		return "Options [Screenshot path=" + path + ", x Offset=" + xOffset + ", y Offset=" + yOffset
				+ ", Crop width=" + width + ", Crop height=" + height + "]";
	}

	private static final String PATH_KEY = "PATH";
	private static final String X_OFFSET_KEY = "X_OFFSET";
	private static final String Y_OFFSET_KEY = "Y_OFFSET";
	private static final String WIDTH_KEY = "WIDTH";
	private static final String HEIGHT_KEY = "HEIGHT";
	
	private static final String DEFAULT_PATH = "Screenshots";
	private static final int DEFAULT_X_OFFSET = 0;
	private static final int DEFAULT_Y_OFFSET = 0;
	private static final int DEFAULT_WIDTH = 1920;
	private static final int DEFAULT_HEIGHT = 1080;
}
