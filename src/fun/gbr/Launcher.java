package fun.gbr;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import fun.gbr.argParsing.ArgParser;
import fun.gbr.argParsing.ArgParser.ParsedArgs;
import fun.gbr.core.Cropper;
import fun.gbr.core.ShotEventHandler;
import fun.gbr.io.Writer;
import fun.gbr.options.Options;

/**
 * TODO:
 * - Find better way to delay clipboard check on lost ownership
 * - Make screen autodetection function and give option to choose a screen
 * 
 * Launches the program in command line mode
 */
public class Launcher {

	public static void main(String[] args) {
				
		// Launcher expects user input from the command line. So stop execution if there is no command line.
		if(System.console() == null) {
			// Unlikely to be seen in current setup, but might be useful if we ever log to file
			Logger.getLogger(Launcher.class.getCanonicalName()).severe("No console detected!");
			return;
		}
		
		// Parse command line arguments
		
		ParsedArgs parsed = ArgParser.parseArgs(args);
		if(parsed.exitNow()) {
			return;
		}
		setLogLevel(parsed.getLogLevel());
		
		try(Scanner scanner = new Scanner(System.in)){			
			System.out.println("Starting up...\n");
			
			// Initialise
			
			Options options = parsed.getOptions();
			System.out.println("Using " + options);
			if(parsed.saveOptions()) {
				options.save();
				System.out.println("Options saved for current user");
			}
			
			Writer writer = new Writer(options.getPath());
			Cropper cropper = new Cropper(options.getXOffset(), options.getYOffset(), options.getWidth(), options.getHeight());
			ShotEventHandler handler = new ShotEventHandler(cropper, writer);
			
			// start event listening
			
			handler.startListening();
			
			System.out.println("\nListening for screenshots. Press enter to stop.\n");
			scanner.nextLine();
			
			// stop listening
			
			Logger.getLogger(Launcher.class.getCanonicalName()).warning("Stopping listening");
		} catch (IOException e) {
			Logger.getLogger(Launcher.class.getCanonicalName()).log(Level.SEVERE, "Initialisation failed!", e);
		}
	}
	
	/** Set default log level for the program
	 * @param level
	 */
	private static void setLogLevel(Level level) {
		Logger rootLogger = Logger.getLogger("");
		rootLogger.setLevel(level);
		for(var hdl : rootLogger.getHandlers()) {
			hdl.setLevel(level);
		}
	}
}
