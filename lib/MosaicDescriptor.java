/*
 * Copyright (C) Jerry Huxtable 1998-2001. All rights reserved.
 */
package com.jhlabs.jai;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import javax.media.jai.*;


/**
 * A JAI operation descriptor for the Mosaic operation. This operation produces
 * an image from a grid of tile sources, each tile being a separate JAI image.
 * All source images must be the same size and have the same tile size, sample
 * model and color model. The resulting image will have a tile size the same as
 * the sources. The parameters are the number of rows and columns in
 * the grid and a two dimensional array of the source tiles.
 * @author Jerry Huxtable
 */
public class MosaicDescriptor extends OperationDescriptorImpl implements RenderedImageFactory {

	private static final String[][] resources = {
		{"GlobalName",  "Mosaic"},
		{"LocalName",   "Mosaic"},
		{"Vendor",	  	"com.jhlabs"},
		{"Description", "An operation that tiles together images"},
		{"DocURL",	  	"http://www.jhlabs.com/MosaicDescriptor.html"},
		{"Version",	 	"1.0"},
		{"arg0Desc",	"rows"},
		{"arg1Desc",	"cols"},
	};

	private static final String[] paramNames = {
		"rows",
		"cols",
	};

	/** 
	 * The class types for the parameters of the "Mosaic" operation.  
	 */
	private static final Class[] paramClasses = {
		java.lang.Integer.class,
		java.lang.Integer.class,
	};
 
	/**
	 * The default parameter values for the "Mosaic" operation
	 * when using a ParameterBlockJAI.
	 */
	private static final Object[] paramDefaults = {
		new Integer(0),
		new Integer(0),
	};
 
	private static boolean registered = false;
	
	public static void register() {
		if (!registered) {
			OperationRegistry or = JAI.getDefaultInstance().getOperationRegistry();

			MosaicDescriptor d = new MosaicDescriptor();
			String operationName = "mosaic";
			String productName = "com.jhlabs";
			or.registerOperationDescriptor(d, operationName);
			or.registerRIF(operationName, productName, d);
			registered = true;
		}
	}

	/**
	 * Construct a MosaicDescriptor.
	 */
	public MosaicDescriptor() {
		super(resources, 0, paramClasses, paramNames, paramDefaults);
	}

	/** 
	 *  Creates a MosaicOpImage with the given ParameterBlock if the 
	 *  MosaicOpImage can handle the particular ParameterBlock.
	 */
	public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
		if (!validateParameters(paramBlock))
			return null;

		return new MosaicOpImage(
			((Integer)paramBlock.getObjectParameter(0)).intValue(),
			((Integer)paramBlock.getObjectParameter(1)).intValue(),
			paramBlock.getSources()
		);
	}

	/**
	 *  Checks that all parameters in the ParameterBlock have the 
	 *  correct type before constructing the MosaicOpImage
	 */
	public boolean validateParameters(ParameterBlock paramBlock) {
		for (int i = 0; i < this.getNumParameters(); i++) {
			Object arg = paramBlock.getObjectParameter(i);
			if (arg == null) {
				return false;
			}
			if ((i == 0 || i == 1) && !(arg instanceof Integer))
				return false;
		}
		return true;
	}
}

/**
 * The MosaicOpImage class.
 * @see MosaicDescriptor
 */
class MosaicOpImage extends OpImage {

	/**
	 * The number of rows in the mosaic.
	 */
	private int rows;

	/**
	 * The number of columns in the mosaic.
	 */
	private int cols;

	/**
	 * The image tile width.
	 */
	private int tileWidth;

	/**
	 * The image tile height.
	 */
	private int tileHeight;

	/**
	 * The image width.
	 */
	private int width;

	/**
	 * The image height.
	 */
	private int height;

	/**
	 * The number of tiles across the image.
	 */
	private int tilesX;

	/**
	 * The number of tiles down the image.
	 */
	private int tilesY;

	/**
	 * The width of the source tiles.
	 */
	private int majorTileWidth;

	/**
	 * The height of the source tiles.
	 */
	private int majorTileHeight;

	public MosaicOpImage(int rows, int cols, Vector sources) {
		super((Vector)null, null, null, true);
		this.rows = rows;
		this.cols = cols;

		if (sources.size() < rows*cols)
			throw new IllegalArgumentException("Not enough sources supplied to MosaicOperator");

		int count = sources.size();
		for (int i = 0; i < count; i++)
			addSource((PlanarImage)sources.elementAt(i));

		PlanarImage baseTile = (PlanarImage)sources.firstElement();
		majorTileWidth = baseTile.getWidth();
		majorTileHeight = baseTile.getHeight();
		tileWidth = baseTile.getTileWidth();
		tileHeight = baseTile.getTileHeight();
		width = cols * majorTileWidth;
		height = rows * majorTileHeight;

		tilesX = (width + tileWidth - 1) / tileWidth;
		tilesY = (height + tileHeight - 1) / tileHeight;
		
		SampleModel sampleModel = baseTile.getSampleModel();
		ColorModel colorModel = baseTile.getColorModel();

		ImageLayout imageLayout = new ImageLayout(0, 0, width, height, 0, 0, tileWidth, tileHeight, sampleModel, colorModel);
		setImageLayout(imageLayout);
	}

	public Raster computeTile(int x, int y) {
		DataBuffer dataBuffer = sampleModel.createDataBuffer();

		if (x < 0 || x >= tilesX || y < 0 || y >= tilesY) {
			System.out.println("Error: illegal tile requested from a MosaicOpImage.");
			Raster raster = Raster.createRaster(sampleModel, dataBuffer, new Point(x * tileWidth, y * tileHeight));
			return raster;
		}
		try {
			int tx = x * tileWidth / majorTileWidth;
			int ty = y * tileHeight / majorTileHeight;
			Rectangle r = new Rectangle(tileWidth * (x % (majorTileWidth/tileWidth)), tileHeight * (y % (majorTileHeight/tileHeight)), tileWidth, tileHeight);
			PlanarImage op = getSourceImage(ty*cols+tx);
			Raster raster = null;
			synchronized(op) {
				raster = op.getData(r);
				raster = raster.createTranslatedChild(x * tileWidth, y * tileHeight);
			}
			return raster;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return Raster.createRaster(sampleModel, dataBuffer, new Point(x * tileWidth, y * tileHeight));
	}

    public Rectangle mapDestRect(Rectangle rectangle, int i) {
    	return rectangle;
    }
    
    public Rectangle mapSourceRect(Rectangle rectangle, int i) {
    	return rectangle;
    }
}
