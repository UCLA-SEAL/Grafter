package edu.ucla.cs.grafter.config;

import java.io.IOException;

import javax.swing.JOptionPane;

import edu.ucla.cs.grafter.file.FileUtils;

public class GrafterConfig {
	final static String path = "./Grafter.config";
	
	public static String root_dir;
	public static String src_dir;
	public static String test_dir;
	public static String bin_src_dir;
	public static String bin_test_dir;
	public static String compile_cmd;
	public static String test_cmd;
	public static int junit_version;
	public static BuildTool build;
	public static String mvn_local_repo;
	public static String ant_lib;
	public static boolean clean_enable;
	public static String clean_cmd;
	
	public static boolean batch = false;
	public static boolean verbose = true;
	
	/**
	 * created for the test purpose, you should not call it from the application
	 * 
	 * @param config
	 */
	public static void config(String config){
		try {
			String[] contents = FileUtils.readFileToArray(config);
			String[] settings = new String[13];
			// remove comments
			int count = 0;
			for(String s : contents){
				if(s.trim().startsWith("#")){
					continue;
				}else if(s.trim().contains("#")){
					settings[count] = s.substring(0, s.indexOf("#"));
					count++;
				}else{
					settings[count] = s;
					count++;
				}
			}
			
			root_dir = settings[0].substring(settings[0].indexOf('=') + 1).trim();
			src_dir = settings[1].substring(settings[1].indexOf('=') + 1).trim();
			test_dir = settings[2].substring(settings[2].indexOf('=') + 1).trim();
			bin_src_dir = settings[3].substring(settings[3].indexOf('=') + 1).trim();
			bin_test_dir = settings[4].substring(settings[4].indexOf('=') + 1).trim();
			compile_cmd = settings[5].substring(settings[5].indexOf('=') + 1).trim();
			test_cmd = settings[6].substring(settings[6].indexOf('=') + 1).trim();
			junit_version = Integer.parseInt(settings[7].substring(settings[7].indexOf('=') + 1));
			String build_tool = settings[8].substring(settings[8].indexOf('=') + 1).trim();
			if(build_tool.equalsIgnoreCase("maven")){
				build = BuildTool.Maven;
				mvn_local_repo = settings[9].substring(settings[9].indexOf('=') + 1).trim();
			}else if(build_tool.equalsIgnoreCase("ant")){
				build = BuildTool.Ant;
				ant_lib = settings[10].substring(settings[10].indexOf('=') + 1).trim();
			}else{
				if(GrafterConfig.batch){
					System.out.println("[Grafter]Unknown build tool. Currently Grafter only supports maven and ant.");
				}else{
					JOptionPane.showMessageDialog(null, "[Grafter]Unknown build tool. Currently Grafter only supports maven and ant.");
				}
				
				System.exit(-1);
			}
			clean_enable = Boolean.valueOf(settings[11].substring(settings[11].indexOf('=') + 1).trim());
			if(clean_enable){
				clean_cmd = settings[12].substring(settings[12].indexOf('=') + 1).trim();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void config(){
		config(path);
	}
	
	public static void main(String[] args){
		GrafterConfig.config();
	}
}