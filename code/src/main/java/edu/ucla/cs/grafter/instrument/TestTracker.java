package edu.ucla.cs.grafter.instrument;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class TestTracker {
	final static String prefix = "org.apache.xml.security";
	final static boolean enableFiltering = true;
	
	public static String getTestName() {
        // For JUnit 4, try to find a method on the stack which is annotated with @Test -- if so, that's the one.
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace();

        for (int i = 1; i < elements.length; i++) {
            StackTraceElement element = elements[i];
            try {
            	String className = element.getClassName();
            	if(enableFiltering && !(className.contains(prefix) || className.contains("javax.xml.crypto"))){
            		// skip JDK and other irrelevant callers
            		continue;            	
            	}
            	
            	Class clz = null;
            	try {
            		clz = Class.forName(className);
            	} catch (ClassNotFoundException classNotFoundException) {
                    continue;
                }
            	
                Method method = null;
                try {
                	method = clz.getMethod(element.getMethodName(), new Class[0]);
                } catch (NoSuchMethodException ex) {
                	continue;
                }

                for (Annotation annotation : method.getAnnotations()) {
                    String annoName = annotation.annotationType().getName();
                    if (annoName.equals("org.junit.Test") || annoName.equals("org.junit.Before") || annoName.equals("org.junit.After")) {
                        return clz.getName() + "." + element.getMethodName();
                    }
                }
            } catch (SecurityException ex) {
            	System.out.println("[Grafter]Security Exception.");
            }
        }
        

        // return an empty string
        return "";
	}
}
