package fun.gbr.core;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.logging.Logger;

/**
 * Used to convert Image to a cropped BufferedImage
 *
 */
public class Cropper {
	
	private int width;
	private int height;
	private int xOffset;
	private int yOffset;
	
	public Cropper(int xOffset, int yOffset, int width, int height) {
		super();
		this.width = width;
		this.height = height;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	/** Crops image
	 * @param image
	 * @return
	 * @throws RasterFormatException if crop doesn't fit in the area of the image
	 */
	public BufferedImage crop(Image image) {
		
		Logger.getLogger(this.getClass().getCanonicalName()).info("Cropping image");
		
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bufferedImage.createGraphics();
		graphics.drawImage(image, 0, 0, null);
		return bufferedImage.getSubimage(xOffset, yOffset, width, height);
	}

}
