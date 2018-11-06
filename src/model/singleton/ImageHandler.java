/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.singleton;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.BreakIterator;

import javax.imageio.stream.ImageInputStream;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageParser;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.formats.psd.PsdImageParser;
import org.apache.sanselan.util.IOUtils;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.AdvancedResizeOp.UnsharpenMask;
import com.mortennobel.imagescaling.MultiStepRescaleOp;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

import psd.model.Psd;
import psd.parser.PsdInputStream;

/**
 *
 * @author Win10 Pro x64
 */
public class ImageHandler {

	PropertiesModel prop;

	public ImageHandler() {
		this.prop = new PropertiesModel();
		prop.loadAppProperties();
	}

	/**
	 *
	 * @param psdFiles
	 * @param imageSizes
	 * @throws IOException
	 */
	/*public void createImgFromPsd(List<File> psdFiles, List<ImageSize> imageSizes) throws IOException {
		Psd psd;
		BufferedImage img;
		File imgFile;
		String imgFileName;
		for (Iterator psdFilesIterator = psdFiles.iterator(); psdFilesIterator.hasNext();) {
			File psdFile = (File) psdFilesIterator.next();
			psd = new Psd(psdFile);
			img = psd.getImage();
			for (Iterator imageSizeIterator = imageSizes.iterator(); imageSizeIterator.hasNext();) {
				ImageSize imageSize = (ImageSize) imageSizeIterator.next();
				BufferedImage scaledImage = resizeImage(imageSize.getWidth(), imageSize.getHeight(), img);
				imgFileName = FilenameUtils.getBaseName(psdFile.getName()) + imageSize.getName() + ".png";
				imgFile = new File(prop.get("filePathTemporary") + imgFileName);
				ImageIO.write(scaledImage, "png", imgFile);

			}
		}
	}*/

	public BufferedImage getImageFromPsd(File psdFile) throws IOException {
		Psd psd;
		BufferedImage img;
		psd = new Psd(psdFile);
		img = psd.getImage();
		return img;
	}
	
	public BufferedImage getImageFromPsd2(File psdFile) throws IOException {
		BufferedImage img = null;
		ImageFormat imageFormat;
		try {
			imageFormat = Sanselan.guessFormat(psdFile);
			if (imageFormat.equals(ImageFormat.IMAGE_FORMAT_PSD) || imageFormat.equals(ImageFormat.IMAGE_FORMAT_JPEG))
		    {
		    	img = Sanselan.getBufferedImage(psdFile);
		    	System.out.println(Sanselan.dumpImageFile(psdFile));
		    }		
		} catch (ImageReadException e) {
			e.printStackTrace();
			return null;
		}  
		return img;
	}
	
	public BufferedImage getImageFromPsd3(File psdFile) throws IOException {
		PSDParser r = new PSDParser();
		InputStream input = new FileInputStream(psdFile);
		  r.read(input);
		  int n = r.getFrameCount();
		  System.out.println(n);
		  BufferedImage image = r.getImage();
		  Graphics2D graphics = image.createGraphics();
		  for (int i = 1; i < n; i++) {
		  	BufferedImage layer = r.getLayer(i);
		  	graphics.drawImage(layer, 0, 0, null);
		  }
		return image;
	}
	
	/**
	 *
	 * @param width
	 * @param height
	 * @param bImage
	 * @return
	 */
	public BufferedImage resizeImage(int width, int height, BufferedImage bImage) {
		ResampleOp resampleOp = new ResampleOp(width, height);
		new ResampleFilters();
		//ImprovedMultistepRescaleOp rescaleOp = new ImprovedMultistepRescaleOp(width, height);
		System.out.println(resampleOp.getFilter().getName());
		resampleOp.setFilter(ResampleFilters.getBiCubicFilter());
		System.out.println(resampleOp.getFilter().getName());
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);
		System.out.println(resampleOp.getUnsharpenMask().name());
		//System.out.println(resampleOp.getRenderingHints().values().toString());
		//rescaleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp);
		//rescaleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);
		
		BufferedImage rescaledBImage = resampleOp.filter(bImage,
				new BufferedImage(width, height, bImage.getType()));
		//BufferedImage rescaledBImage = rescaleOp.filter(bImage,
		//		new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
		return rescaledBImage;
	}

	/**
	 *
	 * @param width
	 * @param height
	 * @param bImage
	 * @return
	 */
	public BufferedImage resizeImage2(int width, int height, BufferedImage bImage) {
		//ResampleOp resampleOp = new ResampleOp(width, height);
		MultiStepRescaleOp rescaleOp = new MultiStepRescaleOp(width, height);
		//resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);
		//rescaleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp);
		rescaleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);
		
		// BufferedImage rescaledBImage = resampleOp.filter(bImage,
		//		new BufferedImage(width, height, bImage.getType()));
		BufferedImage rescaledBImage = rescaleOp.filter(bImage,
				new BufferedImage(width, height, bImage.getType()));
//		BufferedImage rescaledBImage = rescaleOp.doFilter(bImage,
//				rescaleOp.createCompatibleDestImage(bImage,
//						bImage.getColorModel()), width, height);
		return rescaledBImage;
	}
	
	/**
	 *
	 * @param imageARGB
	 * @return returns an bufferedImage without transparency
	 */
	public BufferedImage removeAlphaChannel(BufferedImage imageARGB) {
		BufferedImage imageRGB = new BufferedImage(imageARGB.getWidth(), imageARGB.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = imageRGB.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, imageRGB.getWidth(), imageRGB.getHeight());
		g2d.drawImage(imageARGB, 0, 0, null);
		g2d.dispose();
		return imageRGB;
	}


}
