/*
 * Copyright (C) Jerry Huxtable 1998-2001. All rights reserved.
 */
package com.jhlabs.jai;

import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import javax.media.jai.*;
import com.sun.media.jai.codec.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class XMLDescriptor extends OperationDescriptorImpl implements RenderedImageFactory {

	private static final String[][] resources = {
		{"GlobalName",  "xml"},
		{"LocalName",   "xml"},
		{"Vendor",	  	"com.jhlabs"},
		{"Description", "An operation that produces an operator graph from an XML description"},
		{"DocURL",	  	"http://www.jhlabs.com/XMLDescriptor.html"},
		{"Version",	 	"1.0"},
		{"arg0Desc",	"file"},
		{"arg1Desc",	"stream"},
	};

	private static final String[] paramNames = {
		"file",
		"stream",
	};

	/** 
	 * The class types for the parameters of the "XML" operation.  
	 */
	private static final Class[] paramClasses = {
		java.lang.String.class,
		java.io.InputStream.class,
	};
 
	/**
	 * The default parameter values for the "XML" operation
	 * when using a ParameterBlockJAI.
	 */
	private static final Object[] paramDefaults = {
		null,
		null,
	};
 
	private static boolean registered = false;
	
	public static void register() {
		if (!registered) {
			OperationRegistry or = JAI.getDefaultInstance().getOperationRegistry();

			XMLDescriptor d = new XMLDescriptor();
			String operationName = "xml";
			String productName = "com.jhlabs";
			or.registerOperationDescriptor(d, operationName);
			or.registerRIF(operationName, productName, d);
			registered = true;
		}
	}

	public XMLDescriptor() {
		super(resources, 0, paramClasses, paramNames, paramDefaults);
	}

	public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
//		System.out.println("XMLDescriptor.create");
		if (!validateParameters(paramBlock))
			return null;
		String xmlFile = (String)paramBlock.getObjectParameter(0);
		InputStream is = (InputStream)paramBlock.getObjectParameter(1);
//		System.out.println("XMLDescriptor.create: "+xmlFile);
		try {
			if (is == null)
				is = new BufferedInputStream( new FileInputStream(xmlFile) );
			JAIHandler xr = new JAIHandler();
			Parser xp = ParserFactory.makeParser();
			xp.setDocumentHandler(xr);
			xp.parse(new InputSource(is));
			System.out.println("XMLDescriptor.create: "+xr.getImage());
			return xr.getImage();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	public boolean validateParameters(ParameterBlock paramBlock) {
//		System.out.println("->XMLDescriptor.validateParameters()");
		for (int i = 0; i < this.getNumParameters(); i++) {
			Object arg = paramBlock.getObjectParameter(i);
/*
			if (arg == null) {
				return false;
			}
*/
			if (i == 0 && !(arg == null || arg instanceof String))
				return false;
			else if (i == 1 && !(arg == null || arg instanceof InputStream))
				return false;
		}
		return true;
	}
}

class JAIHandler implements DocumentHandler {
	
	private OpNode root;
	private OpNode node;
	private Hashtable images = new Hashtable();
	private OperationRegistry or = JAI.getDefaultInstance().getOperationRegistry();
	private String mode = "rendered";

	public JAIHandler() {
	}
	
	public void setDocumentLocator(Locator l) {
	}

	public void startDocument() throws SAXException {
	}

	public void endDocument() throws SAXException {
	}

	public void startElement(String tag, AttributeList attrs) throws SAXException {
		String s;

		if (tag.equals("jaiop")) {
		} else if (tag.equals("image")) {
			String opName = attrs.getValue("xj:op");
			if (opName == null)
				throw new IllegalArgumentException("Missing operator name");
//			System.out.println("op="+opName);
			OpNode n;
			n = new OpNode(opName);
			if (root == null) {
				root = n;
			} else if (node != null) {
				node.add(n);
			}
			node = n;
			ParameterBlock pb = new ParameterBlock();
			RegistryElementDescriptor red = or.getDescriptor(mode, opName);
			if (red == null)
				throw new IllegalArgumentException("Operator "+opName+" was not found in the registry");
			ParameterListDescriptor pld = red.getParameterListDescriptor(mode);
			int numParams = pld.getNumParameters();
			Class[] classes = pld.getParamClasses();
			Object[] defaults = pld.getParamDefaults();
			String[] names = pld.getParamNames();
			for (int i = 0; i < numParams; i++) {
				String param = attrs.getValue(names[i]);
				if (param == null)
					pb.add(defaults[i]);
				else {
					try {
						Object o = parseParameter(param, classes[i]);
						pb.add(o);
//						System.out.println(names[i]+"="+o);
					}
					catch (Throwable e) {
						throw new SAXException(e.toString());
					}
				}
			}
			n.setParameters(pb);
			String name = attrs.getValue("xj:name");
			if (name != null) {
				images.put(name, n);
				if (name.equals("root"))
					root = n;
			}
		} else if (tag.equals("imageref")) {
			String reference = attrs.getValue("xj:name");
			if (reference == null)
				throw new IllegalArgumentException("Missing reference in imageref");
			OpNode op = (OpNode)images.get(reference);
			if (op == null)
				throw new IllegalArgumentException("Reference '"+reference+"' not found");
			OpNode n;
			n = new OpNode(op);
			if (root == null) {
				root = n;
			} else if (node != null) {
				node.add(n);
			}
		} else if (tag.equals("property")) {
			String name = attrs.getValue("xj:name");
			if (name == null)
				throw new IllegalArgumentException("Missing name in property");
			String value = attrs.getValue("xj:value");
			if (value == null)
				throw new IllegalArgumentException("Missing value in property");
			if (node == null)
				throw new IllegalArgumentException("No image for property");
			node.setProperty(name, value);
		}
	}

	public void endElement(String tag) throws SAXException {
		if (tag.equals("image")) {
			if (node != null)
				node = node.getParent();
		} else if (tag.equals("jaiop")) {
			root.dump(System.out, 0);
			root.create();
		}
	}

	public void characters(char[] buf, int offset, int len) throws SAXException {
	}

	public void ignorableWhitespace(char[] buf, int offset, int len) throws SAXException {
	}

	public void processingInstruction(String target, String data) throws SAXException	{
	}

	private String getStringValue(AttributeList attrs, String name, String defaultValue) {
		if (attrs != null) {
			name = attrs.getValue(name);
			if (name != null)
				return name;
		}
		return defaultValue;
	}
	
	private int getIntValue(AttributeList attrs, String name, int defaultValue) {
		if (attrs != null) {
			name = attrs.getValue(name);
			if (name != null) {
				try {
					return Integer.parseInt(name);
				}
				catch (NumberFormatException e) {
				}
			}
		}
		return defaultValue;
	}
	
	private Object parseParameter(String param, Class cl) throws IOException {
		if (cl == Float.class)
			return Float.valueOf(param);
		if (cl == Double.class)
			return Double.valueOf(param);
		if (cl == Integer.class)
			return Integer.valueOf(param);
		if (cl == Boolean.class)
			return Boolean.valueOf(param);
		if (cl == URL.class)
			return new URL(param);
		if (cl == Interpolation.class) {
			if (param.equals("bilinear"))
				return new InterpolationBilinear();
//			if (param.equals("bicubic"))
//				return new InterpolationBicubic();
//			if (param.equals("bicubic2"))
//				return new InterpolationBicubic2();
			return new InterpolationNearest();
		}
		if (cl == KernelJAI.class) {
			if (param.equals("floyd-steinberg"))
				return KernelJAI.ERROR_FILTER_FLOYD_STEINBERG;
			if (param.equals("jarvis"))
				return KernelJAI.ERROR_FILTER_JARVIS;
			if (param.equals("stucki"))
				return KernelJAI.ERROR_FILTER_STUCKI;
			if (param.equals("sobel-horizontal"))
				return KernelJAI.GRADIENT_MASK_SOBEL_HORIZONTAL;
			if (param.equals("sobel-vertical"))
				return KernelJAI.GRADIENT_MASK_SOBEL_VERTICAL;
		}
		if (cl == BorderExtender.class)
			return BorderExtender.createInstance(Integer.parseInt(param));
		return param;
	}

	public PlanarImage getImage() {
		return root.getImage();
	}

}

class OpNode {
	private String opName;
	private ParameterBlock parameters;
	private Vector children;
	private OpNode parent;
	private PlanarImage op;
	private OpNode ref;
	private Hashtable properties;

	public OpNode(String opName) {
		this.opName = opName;
	}
	
	public OpNode(OpNode ref) {
		this.ref = ref;
	}
	
	public void add(OpNode n) {
		if (children == null)
			children = new Vector();
		children.add(n);
		n.parent = this;
	}

	public OpNode getParent() {
		return parent;
	}

	public void create() {
		if (ref != null) {
			ref.create();
			op = ref.getImage();
			return;
		}
		if (op == null) {
			if (children != null) {
				Iterator it = children.iterator();
				while (it.hasNext()) {
					OpNode n = (OpNode)it.next();
					n.create();
					parameters.addSource(n.getImage());
				}
			}
			op = JAI.create(opName, parameters);
			if (properties != null) {
				Enumeration e = properties.keys(); 
				while (e.hasMoreElements()) {
					String name = (String)e.nextElement();
					String value = (String)properties.get(name);
					op.setProperty(name, value);
				}
			}
		}
	}

	public PlanarImage getImage() {
		return op;
	}

	public void setParameters(ParameterBlock parameters) {
		this.parameters = parameters;
	}

	public ParameterBlock getParameters() {
		return parameters;
	}

	public void setProperty(String name, String value) {
		if (properties == null)
			properties = new Hashtable();
		properties.put(name, value);
	}
	
	public void dump(PrintStream out, int indent) {
		for (int i = 0; i < indent; i++)
			out.print(" ");
		if (ref != null) {
			out.println("reference: "+ref.opName);
			return;
		} else if (opName != null) {
			out.println(opName);
			if (children != null) {
				Iterator it = children.iterator();
				while (it.hasNext()) {
					OpNode n = (OpNode)it.next();
					n.dump(out, indent+2);
				}
			}
		} else
			out.println("null node!");
	}
}
