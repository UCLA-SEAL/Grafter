package edu.ucla.cs.grafter.compare;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;

public class GrafterSerializer {
	static int counter = 0;
	
	public static void serialize(int i, String name){
		Integer i2 = new Integer(i);
		XStream xs = new XStream();
		String xml = xs.toXML(i2);
		try {
			writeToFile(xml, name + "_" + counter + ".xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void serialize(boolean b, String name){
		Boolean b2 = Boolean.valueOf(b);
		XStream xs = new XStream();
		String xml = xs.toXML(b2);
		try {
			writeToFile(xml, name + "_" + counter + ".xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void serialize(byte b, String name){
		Byte b2 = new Byte(b);
		XStream xs = new XStream();
		String xml = xs.toXML(b2);
		try {
			writeToFile(xml, name + "_" + counter +  ".xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void serialize(char c, String name){
		Character c2 = new Character(c);
		XStream xs = new XStream();
		String xml = xs.toXML(c2);
		try {
			writeToFile(xml, name + "_" + counter + ".xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void serialize(float f, String name){
		Float f2 = new Float(f);
		XStream xs = new XStream();
		String xml = xs.toXML(f2);
		try {
			writeToFile(xml, name + "_" + counter + ".xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void serialize(long l, String name){
		long l2 = new Long(l);
		XStream xs = new XStream();
		String xml = xs.toXML(l2);
		try {
			writeToFile(xml, name + "_" + counter +  ".xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void serialize(short s, String name){
		Short s2 = new Short(s);
		XStream xs = new XStream();
		String xml = xs.toXML(s2);
		try {
			writeToFile(xml, name + "_" + counter + ".xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void serialize(double d, String name){		 
		Double d2 = new Double(d);
		XStream xs = new XStream();
		String xml = xs.toXML(d2);
		try {
			writeToFile(xml, name + "_" + counter + ".xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void serialize(Object o, String name){
		XStream xs = new XStream();
		String xml = xs.toXML(o);
		try {
			writeToFile(xml, name + "_" + counter + ".xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void writeToFile(String content, String path) throws IOException{
		File log = new File(path);
		FileWriter w = new FileWriter(log, false);
		BufferedWriter writer = new BufferedWriter(w);
		writer.write(content + "\n");
		writer.flush();
		writer.close();
	}
	
	public static void main(String[] args){
		int i = 0;
		while(i < 5){
			GrafterSerializer.serialize(5, "test");
			GrafterSerializer.counter++;
			i++;
		}
	}
}
