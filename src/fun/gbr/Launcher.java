package fun.gbr;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import fun.gbr.ArgParser.ParsedArgs;
import fun.gbr.core.Cropper;
import fun.gbr.core.ShotEventHandler;
import fun.gbr.io.Writer;
import fun.gbr.options.Options;

/**
 * TODO:
 * - GUI
 * - Find better way to delay clipboard check on lost ownership
 * - Log to file
 * - Have constants in own class
 * - Change naming style to date-number
 * - Take command line args
 * 
 * Launches the program in command line mode
 */
public class Launcher {

	public static void main(String[] args) {
		
		ParsedArgs parsed = ArgParser.parseArgs(args);
		if(parsed.exitNow()) {
			return;
		}
		try(Scanner scanner = new Scanner(System.in)){			
			System.out.println("Starting up...\n");
			
			// Initialise
			
			Options options = parsed.options();
			if(parsed.shouldSave()) {
				options.save();
			}
			
			Writer writer = new Writer(options.getPath());
			Cropper cropper = new Cropper(options.getXOffset(), options.getYOffset(), options.getWidth(), options.getHeight());
			ShotEventHandler handler = new ShotEventHandler(cropper, writer);
			System.out.println("Ready to start listening. Enter to start, enter again to stop");
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
}
