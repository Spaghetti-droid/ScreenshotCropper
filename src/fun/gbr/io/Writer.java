package fun.gbr.io;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

/**
 * Used to write screenshots to disk
 *
 */
public class Writer {
	
	private Path parentDir;
	private long nextScreenshotIdx;
	// Date that above index is for
	private LocalDate idxDate;
	private String prefix = BASE_SCREENSHOT_NAME + " ";

	public Writer(Path parentDir) throws IOException {
		super();
		this.parentDir = parentDir;
		this.idxDate = LocalDateTime.now().toLocalDate();
		this.nextScreenshotIdx = getNextScreenshotIdx(this.idxDate);
	}
	
	/** Parses parentDir's children to obtain the next filename index to be used
	 * @return
	 * @throws IOException In cases where there are issues creating or accessing parent directory
	 */
	private long getNextScreenshotIdx(LocalDate date) throws IOException {
		if(!Files.isDirectory(parentDir)) {
			Files.createDirectory(parentDir);
			// Nothing in dir
			return 1;
		}
		
		long maxIdx = 0;
		try(var children = Files.list(parentDir)){
			var it = children.iterator();
			while(it.hasNext()) {
				Matcher matcher = makeScreenshotNamePattern(date).matcher(it.next().getFileName().toString());
				if(matcher.matches()) {
					long nameIdx = Long.parseLong(matcher.group(1));
					if(nameIdx>maxIdx) {
						maxIdx = nameIdx; 
					}
				}
			}
		}
		return maxIdx+1;
	}
	
	/** Generates the pattern used to find previously generated screenshots and their indices
	 * @param date
	 * @return The pattern to match
	 */
	private Pattern makeScreenshotNamePattern(LocalDate date) {
		return Pattern.compile(Pattern.quote(this.prefix + date.toString()) + "_(\\d+)" + Pattern.quote(NAME_EXTENSION));
	}
	
	/** Write image to disk
	 * @param image
	 * @throws IOException
	 */
	public void write(BufferedImage image) throws IOException {
		
		Logger.getLogger(this.getClass().getCanonicalName()).info("Writing cropped image to file");
		
		Path scPath = getNextSCPath(0);
		if(ImageIO.write(image, "png", scPath.toFile())){
			nextScreenshotIdx++;
		} else {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, () -> "ImageIO failed to write to " + scPath.getFileName() + "!");
		}
	}
	
	/** Generates the path for the next screenshot to be saved
	 * @param attempt Should be 0 on first call. Used to avoid endless recursion.
	 * @return Am unused path
	 * @throws IOException if an unused path could not be found
	 */
	private Path getNextSCPath(int attempt) throws IOException {
		
		LocalDate today = LocalDateTime.now().toLocalDate();
		if(!today.equals(this.idxDate)){
			this.nextScreenshotIdx = 1;
			this.idxDate = today;
		}
		String name = prefix + today + "_" + nextScreenshotIdx + NAME_EXTENSION;
		Path scPath = this.parentDir.resolve(name);
		if(Files.exists(scPath)) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Next screenshot index already exists!");
			if(attempt<MAX_NAME_ATTEMPTS) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Recalculating...");
			} else {
				throw new IOException("Failed to find valid screenshot index!");
			}
			this.nextScreenshotIdx = getNextScreenshotIdx(today);
			scPath = getNextSCPath(attempt+1);
		}
		
		return scPath;
	}
	
	private static final int MAX_NAME_ATTEMPTS = 2;
	private static final String BASE_SCREENSHOT_NAME = "Screenshot";
	private static final String NAME_EXTENSION = ".png";
	//private static final Pattern SCREENSHOT_NAME_PATTERN = Pattern.compile(Pattern.quote(BASE_SCREENSHOT_NAME) + " (\\d+)" + Pattern.quote(NAME_EXTENSION));
}
