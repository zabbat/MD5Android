package net.wandroid.md5.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;

import net.wandroid.md5.util.Tick;

public class Md5Reader {
	
	public String loadFileToString(BufferedReader reader) throws IOException {
		Tick t = new Tick();
		t.start();
		
		StringBuffer buffer = new StringBuffer();		
		try {
			String s = "";
			while ((s = reader.readLine()) != null) {// read all lines and store it in a framebuffer
				buffer.append(s);
				buffer.append("\n");
			}
		} finally {
			reader.close();
		}
		
		String file = buffer.toString();
		
		t.tock("reading file into memory");
		return file;
	}
	
	protected String labelValue(Matcher match,final String label){
		match.find();
		String s=match.group();
		if (!s.equalsIgnoreCase(label)) {
			throw new ModelParseException("could not find '"+label+"'"+", found '"+s+"'");
		}
		match.find();
		return match.group();
	}

	
}
