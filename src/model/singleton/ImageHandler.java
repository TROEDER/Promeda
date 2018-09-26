/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.singleton;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;

import psd.model.Psd;

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
	

	/**
	 *
	 * @param width
	 * @param height
	 * @param bImage
	 * @return
	 */
	public BufferedImage resizeImage(int width, int height, BufferedImage bImage) {
		ResampleOp resampleOp = new ResampleOp(width, height);
		//ImprovedMultistepRescaleOp rescaleOp = new ImprovedMultistepRescaleOp(width, height);

		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);
		//rescaleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp);
		//rescaleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);
		
		BufferedImage rescaledBImage = resampleOp.filter(bImage,
				new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
		//BufferedImage rescaledBImage = rescaleOp.filter(bImage,
		//		new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
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
