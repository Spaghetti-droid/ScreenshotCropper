package fun.gbr;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import fun.gbr.options.Options;

public class ArgParser {
	
	public static ParsedArgs parseArgs(String[] args){
		
		try {			
			Options options = new Options();
			boolean save = true;
			int i=0;
			// reset is only done if it is the first argument 
			if("--reset".equals(args[0])) {
				options.reset();
				i++;
			}
			while(i<args.length) {
				switch(args[i]) {
				case "-p":
					options.setPath(Path.of(args[++i]));
					break;
				case "-x":
					options.setXOffset(Integer.valueOf(args[++i]));
					break;
				case "-y":
					options.setYOffset(Integer.valueOf(args[++i]));
					break;
				case "-W":
					options.setWidth(Integer.valueOf(args[++i]));
					break;
				case "-H":
					options.setHeight(Integer.valueOf(args[++i]));
					break;
				case "-h":
					return displayHelpAndQuit();
				case "-f":
					save = false;
					break;
				default:
					final String arg = args[i];
					Logger.getLogger(ArgParser.class.getCanonicalName()).warning(()-> "Invalid argument: " + arg);
					return displayHelpAndQuit();
				}
				++i;
			}

			return new ParsedArgs(options, false, save);

		} catch (Exception e) {
			Logger.getLogger(ArgParser.class.getCanonicalName()).log(Level.SEVERE, e, () -> "Error parsing arguments!");
			return displayHelpAndQuit();
		}
	}
	
	
	
	
	/** Prints the apps help message
	 * @return ParsedArgs indicating that program should quit
	 */
	private static ParsedArgs displayHelpAndQuit() {
		System.out.println("""
				Usage <cropper> [--reset] [-h] [-f] [-p PATH] [-x X_OFFSET] [-y Y_OFFSET] [-W WIDTH] [-H HEIGHT] [-o PATH]
				
				Options:
					Note that all values set below are saved to the user's preferences
					-h: Display this message and quit. Disregard any other options.
					-f: Forget; Do not save option changes 
					-p: Specify the path to the screenshot folder to use. Defaults to ./Screenshots.
					-x: The offset from which to crop in the x direction. Defaults to 0.
					-y: The offset from which to crop in the y direction. Defaults to 0.
					-W: The width to crop. Defaults to 1920.
					-H: The height to crop. Defaults to 1080.
					--reset: Resets saved options to defaults. ONLY VALID IF IT IS THE FIRST OPTION SPECIFIED!
				""");
		return new ParsedArgs(null, true, false);
	}
	
	public static record ParsedArgs(Options options, boolean exitNow, boolean shouldSave) {}	
}
