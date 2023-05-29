package fun.gbr;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import fun.gbr.options.Options;

public class ArgParser {
	
	/** Reads input arguments sets them into the appropriate objects and settings
	 * @param args
	 * @return Record of containing parsing result
	 */
	public static ParsedArgs parseArgs(String[] args){
		
		try {			
			Options options = new Options();
			boolean saveOptions = false;
			Level logLevel = Level.SEVERE;
			int i=0;
			// reset is only done if it is the first argument 
			if(args.length > 0 && "--reset".equals(args[0])) {
				options.reset();
				i++;
			}
			while(i<args.length) {
				String arg = args[i];
				if(arg == "--log-level") {
					try {
						logLevel = Level.parse(args[++i]);
					} catch(Exception e) {
						final String badArg = args[i];
						Logger.getLogger(ArgParser.class.getCanonicalName()).severe(() -> "Invalid log level: " + badArg);
						return displayHelpAndQuit();
					}
				}
				if(arg.startsWith("-")) {
					arg = arg.substring(1);
				} else {
					// There are no positional arguments
					return handleBadArgument(arg);
				}
				// valueUsed indicates if args[i+1] has already been used as a value
				Character valueUser = null;
				for(int j=0; j<arg.length(); j++) {
					char c = arg.charAt(j);
					switch(c) {
					case 'p':
						valueUser = requireValueNotUsed(valueUser, c, arg);
						options.setPath(Path.of(args[++i]));
						break;
					case 'x':
						valueUser = requireValueNotUsed(valueUser, c, arg);
						options.setXOffset(Integer.valueOf(args[++i]));
						break;
					case 'y':
						valueUser = requireValueNotUsed(valueUser, c, arg);
						options.setYOffset(Integer.valueOf(args[++i]));
						break;
					case 'W':
						valueUser = requireValueNotUsed(valueUser, c, arg);
						options.setWidth(Integer.valueOf(args[++i]));
						break;
					case 'H':
						valueUser = requireValueNotUsed(valueUser, c, arg);
						options.setHeight(Integer.valueOf(args[++i]));
						break;
					case 'h':
						return displayHelpAndQuit();
					case 's':
						saveOptions = true;
						break;
					default:
						return handleBadArgument(arg);
					}
				}
				
				++i;
			}

			return new ParsedArgs(options, false, saveOptions, logLevel);

		} catch (@SuppressWarnings("unused") ArrayIndexOutOfBoundsException ae) {
			Logger.getLogger(ArgParser.class.getCanonicalName()).log(Level.SEVERE, () -> "Argument '" + args[args.length-1] + "' requires a value!");
			return displayHelpAndQuit();
		} catch (Exception e) {
			Logger.getLogger(ArgParser.class.getCanonicalName()).log(Level.SEVERE, e, () -> "Error parsing arguments!");
			return displayHelpAndQuit();
		}
	}	
	
	/** Check that option in argument is not already using next argument as value
	 * @param valueUser 	char that should be null
	 * @param newValueUser	option that needs args[i+1] as a value
	 * @param arg			Currently parsed argument (without - prefix) for error reporting
	 * @throws IllegalArgumentException if valueUsed is true
	 * @return true
	 */
	private static char requireValueNotUsed(Character valueUser, char newValueUser, String arg) {
		if(valueUser != null) {
			throw new IllegalArgumentException("Option '" + valueUser + "' supplied in '-" + arg + "' requires a value!");
		}
		return newValueUser;
	}
	
	/** Prints the apps help message
	 * @return ParsedArgs indicating that program should quit
	 */
	private static ParsedArgs displayHelpAndQuit() {
		System.out.println("""
				Usage <cropper> [--reset] [-h] [-s] [-p PATH] [-x X_OFFSET] [-y Y_OFFSET] [-W WIDTH] [-H HEIGHT]
				
				Options:
					Note that all values set below are saved to the user's preferences
					-h: Display this message and quit. Disregard any other options.
					-s: Save; Save option changes to user preferences, to be used as new defaults.
					-p: Specify the path to the screenshot folder to use. Defaults to saved preference or ./Screenshots.
					-x: The offset from which to crop in the x direction. Defaults to saved preference or 0.
					-y: The offset from which to crop in the y direction. Defaults to saved preference or 0.
					-W: The width to crop. Defaults to saved preference or 1920.
					-H: The height to crop. Defaults to saved preference or 1080.
					--reset: Resets saved options to defaults. ONLY VALID IF IT IS THE FIRST OPTION SPECIFIED!
				""");
		return new ParsedArgs(null, true, false, Level.WARNING);
	}
	
	/** Logs bad argument, displays help and returns quit-signalling ParsedArgs
	 * @param badArg
	 * @return quit-signalling ParsedArgs
	 */
	private static ParsedArgs handleBadArgument(String badArg) {
		Logger.getLogger(ArgParser.class.getCanonicalName()).severe(() -> "Invalid argument: " + badArg);
		return displayHelpAndQuit();
	}
	
	public static record ParsedArgs(Options options, boolean exitNow, boolean saveOptions, Level logLevel) {}	
}
