/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.singleton;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import org.apache.sanselan.ColorTools;
import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.w3c.dom.Element;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.MultiStepRescaleOp;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

import ij.IJ;
import model.prototype.JpegReader;
import psd.model.Psd;

/**
 *
 * @author Win10 Pro x64
 */
public class ImageHandler {

	private PropertiesModel prop;
	private JpegReader jpegReader;
	private ColorTools colorTools;

	public ImageHandler() {
		this.prop = new PropertiesModel();
		prop.loadAppProperties();
		jpegReader = new JpegReader();
	}

	/**
	 *
	 * @param psdFiles
	 * @param imageSizes
	 * @throws IOException
	 */
	/*
	 * public void createImgFromPsd(List<File> psdFiles, List<ImageSize> imageSizes)
	 * throws IOException { Psd psd; BufferedImage img; File imgFile; String
	 * imgFileName; for (Iterator psdFilesIterator = psdFiles.iterator();
	 * psdFilesIterator.hasNext();) { File psdFile = (File) psdFilesIterator.next();
	 * psd = new Psd(psdFile); img = psd.getImage(); for (Iterator imageSizeIterator
	 * = imageSizes.iterator(); imageSizeIterator.hasNext();) { ImageSize imageSize
	 * = (ImageSize) imageSizeIterator.next(); BufferedImage scaledImage =
	 * resizeImage(imageSize.getWidth(), imageSize.getHeight(), img); imgFileName =
	 * FilenameUtils.getBaseName(psdFile.getName()) + imageSize.getName() + ".png";
	 * imgFile = new File(prop.get("filePathTemporary") + imgFileName);
	 * ImageIO.write(scaledImage, "png", imgFile);
	 * 
	 * } } }
	 */

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
			if (imageFormat.equals(ImageFormat.IMAGE_FORMAT_PSD) || imageFormat.equals(ImageFormat.IMAGE_FORMAT_JPEG)) {
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
	 * Load image from image file
	 * 
	 * @param fileName Image file name
	 * @return Image
	 * @throws java.io.IOException
	 * @throws org.apache.sanselan.ImageReadException
	 */
	public BufferedImage imageLoad(File file) throws IOException, ImageReadException {
		ImageFormat info = Sanselan.guessFormat(file);
		ICC_Profile profile = Sanselan.getICCProfile(file);
		BufferedImage image = null;
		if (info == ImageFormat.IMAGE_FORMAT_JPEG) {
			// image = ImageIO.read(new File(fileName));
			image = jpegReader.readImage(file);
			return image;
		} else {
			ImageInputStream stream = ImageIO.createImageInputStream(file);
	        Iterator<ImageReader> iter = ImageIO.getImageReaders(stream);
	        while (iter.hasNext()) {
	            ImageReader reader = iter.next();
	            reader.setInput(stream);
			image = Sanselan.getBufferedImage(file);
			return image;
	        }
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
		// ImprovedMultistepRescaleOp rescaleOp = new ImprovedMultistepRescaleOp(width,
		// height);
		System.out.println(resampleOp.getFilter().getName());
		resampleOp.setFilter(ResampleFilters.getBiCubicFilter());
		System.out.println(resampleOp.getFilter().getName());
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);
		System.out.println(resampleOp.getUnsharpenMask().name());
		// System.out.println(resampleOp.getRenderingHints().values().toString());
		// rescaleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp);
		// rescaleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);

		BufferedImage rescaledBImage = resampleOp.filter(bImage, new BufferedImage(width, height, bImage.getType()));
		// BufferedImage rescaledBImage = rescaleOp.filter(bImage,
		// new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
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
		// ResampleOp resampleOp = new ResampleOp(width, height);
		MultiStepRescaleOp rescaleOp = new MultiStepRescaleOp(width, height);
		// resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);
		// rescaleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp);
		rescaleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);

		// BufferedImage rescaledBImage = resampleOp.filter(bImage,
		// new BufferedImage(width, height, bImage.getType()));
		BufferedImage rescaledBImage = rescaleOp.filter(bImage, new BufferedImage(width, height, bImage.getType()));
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

	public void saveAsJpeg(BufferedImage bi, String path, int quality) {
		// IJ.log("saveAsJpeg: "+path);
		System.out.println(bi.getWidth());
		bi = resizeImage(278, 278, bi);
		System.out.println(bi.getWidth());
		try {

			Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");
			try {

				ImageWriter writer = (ImageWriter) iter.next();
				writer.setOutput(ImageIO.createImageOutputStream(new File(path)));
				ImageWriteParam param = writer.getDefaultWriteParam();
				param.setCompressionMode(param.MODE_EXPLICIT);
				param.setCompressionQuality(quality / 100f);
				if (quality == 100)
					param.setSourceSubsampling(1, 1, 0, 0);
				IIOMetadata metaData = writer.getDefaultImageMetadata(new ImageTypeSpecifier(bi), param);

				for (String format : metaData.getMetadataFormatNames()) {
					System.out.println(format);
				}
//					Calibration cal = imp.getCalibration();
//					String unit = cal.getUnit().toLowerCase(Locale.US);
				// if (cal.getUnit().equals("inch")||cal.getUnit().equals("in")) {
				Element tree = (Element) metaData.getAsTree("javax_imageio_jpeg_image_1.0");
				Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
//						jfif.setAttribute("Xdensity", "" + (int)Math.round(1.0/cal.pixelWidth));
//						jfif.setAttribute("Ydensity", "" + (int)Math.round(1.0/cal.pixelHeight));
				jfif.setAttribute("Xdensity", "72");
				jfif.setAttribute("Ydensity", "72");
				jfif.setAttribute("resUnits", "1"); // density is dots per inch*/
				// metaData.setFromTree("javax_imageio_jpeg_image_1.0",tree);
				// }
				IIOImage iioImage = new IIOImage(bi, null, null);
				writer.write(null, iioImage, param);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("falling back to plain jpeg writing because of " + e);
				ImageIO.write(bi, "jpeg", new FileOutputStream(path));
			}
		} catch (Exception e) {
			IJ.error("Jpeg Writer", "" + e);
		}
	}

}
