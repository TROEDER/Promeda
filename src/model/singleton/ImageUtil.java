package model.singleton;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.color.ColorConversions;

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
        if (info == ImageFormat.IMAGE_FORMAT_JPEG || info == ImageFormat.IMAGE_FORMAT_JPEG){
            //image = ImageIO.read(new File(fileName));
        	image = jpegReader.readImage(file);
        } else {
            image = Sanselan.getBufferedImage(file);
            ICC_Profile profile = Sanselan.getICCProfile(file);
        }
        return image;
    }
	static void intToBigEndian(int value, byte[] array, int index) {
	  array[index]   = (byte) (value >> 24);
	  array[index+1] = (byte) (value >> 16);
	  array[index+2] = (byte) (value >>  8);
	  array[index+3] = (byte) (value);
	}
	public static void convertYcckToCmyk(WritableRaster raster) {
	    int height = raster.getHeight();
	    int width = raster.getWidth();
	    int stride = width * 4;
	    int[] pixelRow = new int[stride];
	    for (int h = 0; h < height; h++) {
	        raster.getPixels(0, h, width, 1, pixelRow);
	
	        for (int x = 0; x < stride; x += 4) {
	            int y = pixelRow[x];
	            int cb = pixelRow[x + 1];
	            int cr = pixelRow[x + 2];
	
	            int c = (int) (y + 1.402 * cr - 178.956);
	            int m = (int) (y - 0.34414 * cb - 0.71414 * cr + 135.95984);
	            y = (int) (y + 1.772 * cb - 226.316);
	
	            if (c < 0) c = 0; else if (c > 255) c = 255;
	            if (m < 0) m = 0; else if (m > 255) m = 255;
	            if (y < 0) y = 0; else if (y > 255) y = 255;
	
	            pixelRow[x] = 255 - c;
	            pixelRow[x + 1] = 255 - m;
	            pixelRow[x + 2] = 255 - y;
	        }
	
	        raster.setPixels(0, h, width, 1, pixelRow);
	    }
	}
	public static void convertInvertedColors(WritableRaster raster) {
	    int height = raster.getHeight();
	    int width = raster.getWidth();
	    int stride = width * 4;
	    int[] pixelRow = new int[stride];
	    for (int h = 0; h < height; h++) {
	        raster.getPixels(0, h, width, 1, pixelRow);
	        for (int x = 0; x < stride; x++)
	            pixelRow[x] = 255 - pixelRow[x];
	        raster.setPixels(0, h, width, 1, pixelRow);
	    }
	}
	public static BufferedImage convertCmykToRgb(Raster cmykRaster, ICC_Profile cmykProfile) throws IOException {
	    if (cmykProfile == null)
	        cmykProfile = ICC_Profile.getInstance(JpegReader.class.getResourceAsStream("/ISOcoated_v2_300_eci.icc"));
	
	    if (cmykProfile.getProfileClass() != ICC_Profile.CLASS_DISPLAY) {
	        byte[] profileData = cmykProfile.getData();
	
	        if (profileData[ICC_Profile.icHdrRenderingIntent] == ICC_Profile.icPerceptual) {
	            intToBigEndian(ICC_Profile.icSigDisplayClass, profileData, ICC_Profile.icHdrDeviceClass); // Header is first
	
	            cmykProfile = ICC_Profile.getInstance(profileData);
	        }
	    }
	
	    ICC_ColorSpace cmykCS = new ICC_ColorSpace(cmykProfile);
	    BufferedImage rgbImage = new BufferedImage(cmykRaster.getWidth(), cmykRaster.getHeight(), BufferedImage.TYPE_INT_RGB);
	    WritableRaster rgbRaster = rgbImage.getRaster();
	    ColorSpace rgbCS = rgbImage.getColorModel().getColorSpace();
	    ColorConvertOp cmykToRgb = new ColorConvertOp(cmykCS, rgbCS, null);
	    cmykToRgb.filter(cmykRaster, rgbRaster);
	    return rgbImage;
	}
    
}
