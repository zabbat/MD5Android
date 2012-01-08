package net.wandroid.md5.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;

import net.wandroid.md5.Tick;

/**
 * Class for Md5 file loader. Handling basic IO operation on Md5 files
 * @author Jungbeck
 *
 */
public class Md5Loader {
	
    /**
     * Reads a file from a BufferedReader object, and returns it content as a string
     * @param reader the BufferedReader for the file
     * @return the file as a string
     * @throws IOException in case the file could not be read
     */
	public String loadFileToString(BufferedReader reader) throws IOException {
		Tick t = new Tick();
		t.start();
		
		StringBuffer buffer = new StringBuffer();// stringbuffer.append is much faster than the String concat		
		try {
			String s = "";
			while ((s = reader.readLine()) != null) {// read all lines and store it in a StringBuffer
				buffer.append(s);
				buffer.append("\n");//readLine() removes new lines. Note that this is faster than append(s+"\n"), that will use a String concat
			}
		} finally {
			reader.close();
		}
		
		String file = buffer.toString();
		
		t.tock("reading file into memory");
		return file;
	}
	
	
	
	/**
	 * This method is usefull for reading variables, that starts with a label.
	 * Example for "version 10" , you can try to find label "version" and the function will return "10"
	 * If the label is not found , a ModelParseException is thrown.
	 * @param match the Matcher for the search
	 * @param label the label to be found
	 * @return the variable after the label, as a String
	 * @exception ModelParseException in case the label was not found 
	 */
	protected String labelValue(Matcher match,final String label)throws ModelParseException{
		match.find();
		String s=match.group();
		if (!s.equalsIgnoreCase(label)) {
			throw new ModelParseException("could not find '"+label+"'"+", found '"+s+"'");
		}
		match.find();
		return match.group();
	}

	
}
