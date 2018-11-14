package model.singleton;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

import model.prototype.JpegReader;

public class ImageUtil {

	private JpegReader jpegReader;
	
	public ImageUtil() {
		jpegReader = new JpegReader();
	}
	 /**
     * Load image from image file
     * @param fileName Image file name
     * @return Image
     * @throws java.io.IOException
     * @throws org.apache.sanselan.ImageReadException
     */
    public BufferedImage imageLoad(File file) throws IOException, ImageReadException{
        ImageFormat info = Sanselan.guessFormat(file);
        BufferedImage image = null;
        if (info == ImageFormat.IMAGE_FORMAT_JPEG){
            //image = ImageIO.read(new File(fileName));
        	image = jpegReader.readImage(file);
        } else {
            image = Sanselan.getBufferedImage(file);
        }
        return image;
    }
    
}
