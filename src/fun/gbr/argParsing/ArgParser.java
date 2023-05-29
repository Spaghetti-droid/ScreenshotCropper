package fun.gbr.argParsing;

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
			ParsedArgs parsedArgs = new ParsedArgs();
			int i=0;
			// reset is only done if it is the first argument 
			if(args.length > 0 && "--reset".equals(args[0])) {
				parsedArgs.getOptions().reset();
				i++;
			}
			while(i<args.length) {
				String arg = args[i];
				if(arg.startsWith("--")){
					i = parseArgument(parsedArgs, arg.substring(2), args, i);
				} else if(arg.startsWith("-")) {
					i = parseFlags(parsedArgs, arg.substring(1), args, i);
				} else {
					// There are no positional arguments
					return handleBadArgument(arg);
				}				
				++i;
			}

			return parsedArgs;

		} catch (@SuppressWarnings("unused") ArrayIndexOutOfBoundsException ae) {
			Logger.getLogger(ArgParser.class.getCanonicalName()).log(Level.SEVERE, () -> "Argument '" + args[args.length-1] + "' requires a value!");
			return displayHelpAndQuit();
		} catch (IllegalArgumentException ie) {
			Logger.getLogger(ArgParser.class.getCanonicalName()).log(Level.SEVERE, ie, () -> "Argument not recognised: " + ie.getMessage());
			return displayHelpAndQuit();
		} catch (Exception e) {
			Logger.getLogger(ArgParser.class.getCanonicalName()).log(Level.SEVERE, e, () -> "Error parsing arguments!");
			return displayHelpAndQuit();
		}
	}
	
	/** Parse arg and set the corresponding parameter(s) in parsedArgs accordingly
	 * @param parsedArgs OUTPUT The object containing the parameters to set.
	 * @param arg		The argument to process, without any -- prefix.
	 * @param args		The full array of arguments, potentially needed to obtain a parameter value
	 * @param i			The current index of the item in args we are treating (ie of arg)
	 * @return			The value of the index after flag treatment is done
	 */
	private static int parseArgument(ParsedArgs parsedArgs, String arg, String[] args, int i) {
		
		switch(arg) {
		case "log-level":
			parsedArgs.withLogLevel(Level.parse(args[++i]));
			break;
		default:
			throw new IllegalArgumentException(arg);
		}
		
		return i;
	}
	
	/** Interpret each character of arg as a flag and set parameters in parsedArgs accordingly
	 * @param parsedArgs OUTPUT The object containing the parameters to set.
	 * @param arg		A series of flags. Note - prefix should have been removed by this point!
	 * @param args		The full array of arguments, potentially needed to obtain a parameter value
	 * @param i			The current index of the item in args we are treating
	 * @return			The value of the index after flag treatment is done
	 */
	private static int parseFlags(ParsedArgs parsedArgs, String arg, String[] args, int i) {
		
		// valueUsed indicates if args[i+1] has already been used as a value
		Character valueUser = null;
		for(int j=0; j<arg.length(); j++) {
			char c = arg.charAt(j);
			switch(c) {
			case 'p':
				valueUser = requireValueNotUsed(valueUser, c, arg);
				parsedArgs.getOptions().setPath(Path.of(args[++i]));
				break;
			case 'x':
				valueUser = requireValueNotUsed(valueUser, c, arg);
				parsedArgs.getOptions().setXOffset(Integer.valueOf(args[++i]));
				break;
			case 'y':
				valueUser = requireValueNotUsed(valueUser, c, arg);
				parsedArgs.getOptions().setYOffset(Integer.valueOf(args[++i]));
				break;
			case 'W':
				valueUser = requireValueNotUsed(valueUser, c, arg);
				parsedArgs.getOptions().setWidth(Integer.valueOf(args[++i]));
				break;
			case 'H':
				valueUser = requireValueNotUsed(valueUser, c, arg);
				parsedArgs.getOptions().setHeight(Integer.valueOf(args[++i]));
				break;
			case 'h':
				displayHelp();
				parsedArgs.withExitNow(true);
				// Stop parsing as soon as help is triggered
				return args.length;
			case 's':
				parsedArgs.withSaveOptions(true);
				break;
			default:
				throw new IllegalArgumentException(arg);
			}
		}
		
		return i;
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
		displayHelp();
		return new ParsedArgs().withExitNow(true);
	}
	
	/** Prints the apps help message
	 * @return ParsedArgs indicating that program should quit
	 */
	private static void displayHelp() {
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
	}
	
	/** Logs bad argument, displays help and returns quit-signalling ParsedArgs
	 * @param badArg
	 * @return quit-signalling ParsedArgs
	 */
	private static ParsedArgs handleBadArgument(String badArg) {
		Logger.getLogger(ArgParser.class.getCanonicalName()).severe(() -> "Invalid argument: " + badArg);
		return displayHelpAndQuit();
	}
	
	/**
	 * Holds the result of parsing program arguments
	 *
	 */
	public static class ParsedArgs{
		private Options options = new Options();
		private boolean exitNow = false;
		private boolean saveOptions = false;
		private Level logLevel = Level.SEVERE;		
		
		public ParsedArgs() {
			super();
		}

		public Options getOptions() {
			return options;
		}

		public ParsedArgs withOptions(Options options) {
			this.options = options;
			return this;
		}

		public boolean exitNow() {
			return exitNow;
		}

		public ParsedArgs withExitNow(boolean exitNow) {
			this.exitNow = exitNow;
			return this;
		}

		public boolean saveOptions() {
			return saveOptions;
		}

		public ParsedArgs withSaveOptions(boolean saveOptions) {
			this.saveOptions = saveOptions;
			return this;
		}

		public Level getLogLevel() {
			return logLevel;
		}

		public ParsedArgs withLogLevel(Level logLevel) {
			this.logLevel = logLevel;
			return this;
		}		
	}	
}
