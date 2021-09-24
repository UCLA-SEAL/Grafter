package edu.ucla.cs.grafter.compare;

import java.io.IOException;

import com.thoughtworks.xstream.XStream;

import edu.ucla.cs.grafter.file.FileUtils;

public class Deserializer {
	
	public static int deserialize_int(String path){
		XStream xs = new XStream();
		String xml = "";
		try {
			xml = FileUtils.readFileToString(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Integer i = (Integer)xs.fromXML(xml);
		return i.intValue();
	}
	
	public static boolean deserialize_bool(String path){
		XStream xs = new XStream();
		String xml = "";
		try {
			xml = FileUtils.readFileToString(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Boolean b = (Boolean)xs.fromXML(xml);
		return b.booleanValue();
	}
	
	public static byte deserialize_byte(String path){
		XStream xs = new XStream();
		String xml = "";
		try {
			xml = FileUtils.readFileToString(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Byte b = (Byte)xs.fromXML(xml);
		return b.byteValue();
	}
	
	public static char deserialize_char(String path){
		XStream xs = new XStream();
		String xml = "";
		try {
			xml = FileUtils.readFileToString(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Character c = (Character)xs.fromXML(xml);
		return c.charValue();
	}
	
	public static float deserialize_float(String path){
		XStream xs = new XStream();
		String xml = "";
		try {
			xml = FileUtils.readFileToString(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Float f = (Float)xs.fromXML(xml);
		return f.floatValue();
	}
	
	public static long deserialize_long(String path){
		XStream xs = new XStream();
		String xml = "";
		try {
			xml = FileUtils.readFileToString(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Long l = (Long)xs.fromXML(xml);
		return l.longValue();
	}
	
	public static short deserialize_short(String path){
		XStream xs = new XStream();
		String xml = "";
		try {
			xml = FileUtils.readFileToString(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Short s = (Short)xs.fromXML(xml);
		return s.shortValue();
	}
	
	public static double deserialize_double(String path){
		XStream xs = new XStream();
		String xml = "";
		try {
			xml = FileUtils.readFileToString(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Double d = (Double)xs.fromXML(xml);
		return d.doubleValue();
	}
	
	public static Object deserialize_object(String path){
		XStream xs = new XStream();
		String xml = "";
		try {
			xml = FileUtils.readFileToString(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Object o = (Object)xs.fromXML(xml);
		return o;
	}
}
