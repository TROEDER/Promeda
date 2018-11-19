/*
 * Copyright (C) Jerry Huxtable 1998-2001. All rights reserved.
 */
package com.jhlabs.jai;

import java.awt.image.*;
import java.awt.image.renderable.*;
import java.io.*;
import javax.media.jai.*;
import com.sun.media.jai.codec.*;

public final class XMLCodec extends ImageCodec {

	public XMLCodec() {
	}

	public String getFormatName() {
		return "jaixml";
	}

	public Class getEncodeParamClass() {
		return null;
	}

	public Class getDecodeParamClass() {
		return Object.class;
	}

	public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param) {
		return false;
	}

	protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param) {
		return null;
	}

	protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param) {
		if (!(src instanceof BufferedInputStream)) {
			src = new BufferedInputStream(src);
		}
		return new XMLImageDecoder(new ForwardSeekableStream(src), null);
	}

	protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param) {
		return new XMLImageDecoder(src, null);
	}

	public int getNumHeaderBytes() {
		 return 4;
	}

	public boolean isFormatRecognized(byte[] header) {
		return ((header[0] == '<') &&
				(header[1] == '?') &&
				(header[2] == 'x') &&
				(header[3] == 'm'));
	}
}

class XMLImageDecoder extends ImageDecoderImpl {

	public XMLImageDecoder(SeekableStream input, ImageDecodeParam param) {
		super(input, param);
	}

	public RenderedImage decodeAsRenderedImage(int page) throws IOException {
		if (page != 0) {
			throw new IOException("Illegal page requested from an XML image.");
		}
		ParameterBlock pb = new ParameterBlock();
		pb.add("");
		pb.add(input);
		return JAI.create("xml", pb);
	}
}
