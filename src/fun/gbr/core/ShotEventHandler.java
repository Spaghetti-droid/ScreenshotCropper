package fun.gbr.core;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.RasterFormatException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fun.gbr.io.Writer;

/**
 * Used to activate and manage listening for screenshots
 *
 */
public class ShotEventHandler implements ClipboardOwner{
	
	private Cropper cropper;
	private Writer writer;
	private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	public ShotEventHandler(Cropper cropper, Writer writer) {
		super();
		this.cropper = cropper;
		this.writer = writer;
	}

	/**
	 * Start checking for images to crop on clipboard
	 */
	public void startListening() {
		// Gain ownership of clipboard to force loss of ownership when screenshot is taken
		this.clipboard.setContents(clipboard.getContents(this), this);
		Logger.getLogger(this.getClass().getCanonicalName()).warning("Started Listening");
	}
	
	/** Check clipboard for image and crop it if it is there
	 * @param clipBoard
	 */
	private void captureImage(Clipboard clipBoard) {
		if(clipBoard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
			Logger.getLogger(this.getClass().getCanonicalName()).info("Image detected in clipboard");
			try {
				// Extract image
				Image image = (Image) clipBoard.getData(DataFlavor.imageFlavor);
				Logger.getLogger(this.getClass().getCanonicalName()).info("Image loaded");
				writer.write(cropper.crop(image));
			} catch (UnsupportedFlavorException e) {
				// Shouldn't happen
				e.printStackTrace();
			} catch (IOException e) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e, () -> "Capture and crop failed");
			} catch (RasterFormatException e) {
				Logger.getLogger(this.getClass().getCanonicalName()).info(e.getLocalizedMessage());
			}
		}	
	}

	/**
	 *	On loss of ownership, capture image and reclaim ownership
	 */
	@SuppressWarnings("hiding")
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		try {
			Thread.sleep(200);
			captureImage(clipboard);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
		clipboard.setContents(contents, this);	
	}	
}
