package fun.gbr;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import fun.gbr.core.Cropper;
import fun.gbr.core.ShotEventHandler;
import fun.gbr.io.Writer;

/**
 * TODO:
 * - GUI
 * - Find better way to delay clipboard check on lost ownership
 * - Log to file
 * - Have constants in own class
 * 
 * Launches the program in command line mode
 */
public class Launcher {

	public static void main(String[] args) {
		
		try(Scanner scanner = new Scanner(System.in)){
			System.out.println("Starting up...\n");
			// Initialise
			Writer writer = new Writer(DEFAULT_PATH);
			Cropper cropper = new Cropper(DEFAULT_X_OFFSET, DEFAULT_Y_OFFSET, DEFAULT_WIDTH, DEFAULT_HEIGHT);
			ShotEventHandler handler = new ShotEventHandler(cropper, writer);
			System.out.println("Ready to start listening. Type anything to start, type again to stop");
			scanner.nextLine();
			// start event listening
			handler.startListening();
			scanner.nextLine();
			// stop listening
			Logger.getLogger(Launcher.class.getCanonicalName()).info("Stopping listening");
		} catch (IOException e) {
			Logger.getLogger(Launcher.class.getCanonicalName()).log(Level.SEVERE, "Initialisation failed!", e);
		}

	}
	
	private static final Path DEFAULT_PATH = Path.of("Screenshots");
	private static final int DEFAULT_X_OFFSET = 0;
	private static final int DEFAULT_Y_OFFSET = 0;
	private static final int DEFAULT_WIDTH = 1920;
	private static final int DEFAULT_HEIGHT = 1080;

}
