package edu.ucla.cs.grafter.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;

import edu.ucla.cs.grafter.config.GrafterConfig;

public class CompileRunner {
	public static boolean run() throws IOException{
		File workingDir = new File(GrafterConfig.root_dir);
		
		if(GrafterConfig.clean_enable){
			Process clean = Runtime.getRuntime().exec(GrafterConfig.clean_cmd, null, workingDir);
			try{
				clean.waitFor();
			} catch (InterruptedException e){
				e.printStackTrace();
			}
		}
		
		Process compile = Runtime.getRuntime().exec(GrafterConfig.compile_cmd, null, workingDir);
		try {
			// wait till the process terminates
			compile.waitFor(); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(compile.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(compile.getErrorStream()));
		
		String s = null;
		while((s = stdInput.readLine()) != null){
			if(StringUtils.containsIgnoreCase(s, "build failure")){
				return false;
			}
		}
		
		if((s = stdError.readLine()) != null){
			return false;
		}
		
		return true;
	}
}
