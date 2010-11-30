package com.ibm.jaql.doc.processors;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ibm.jaql.doc.FnTextTag;
import com.ibm.jaql.doc.UDFDesc;

public class WikiProcessor implements DataProcessor {
	public HashMap<String, ArrayList<UDFDesc>> categories;
	public String wikiOutputPath = null;
	
	public WikiProcessor() {
		categories = new HashMap<String, ArrayList<UDFDesc>>();
	}
	
	@Override
	public void process(List<UDFDesc> list, HashMap<String, String> options) {
		wikiOutputPath = options.get("-wikiout");
		
		for (UDFDesc desc : list) {
			categorizeUDF(desc);
		}
		
		generateWikiDoc();
	}
	
	void categorizeUDF(UDFDesc udf) {
		ArrayList<UDFDesc> category =  categories.get(udf.getPackageName());
		if(category == null) {
			category = new ArrayList<UDFDesc>();
			categories.put(udf.getPackageName(), category);
		}
		category.add(udf);
	}
	
	PrintStream createOutputStream() {
		if(wikiOutputPath == null) {
			return System.out;
		} else {
			try {
				File f = new File(wikiOutputPath);
				if(!f.exists()) f.createNewFile();
				if(!f.isFile() || !f.canWrite()) {
					throw new IllegalArgumentException("-wikiout argument is not a file or not writable");
				}
				return new PrintStream(f);
			} catch (IOException e) {
				throw new IllegalArgumentException("Problem while creating output stream from -wikiout argument", e);
			}
		}
		
	}
	void generateWikiDoc() {
		PrintStream out = createOutputStream();
		out.println("#summary JAQL built-in function list - autogenerated");
		out.println("#sidebar TableOfContents ");
		out.println("<wiki:toc max_depth=\"2\" />");

		for (String category : categories.keySet()) {
			printCategory(category, out);
		}
	}
	
	void printCategory(String category, PrintStream out) {
		out.println("=" + category.substring(category.lastIndexOf(".")+1) + "=");
		
		ArrayList<UDFDesc> functions = categories.get(category);
		for (UDFDesc desc : functions) {
			printFunction(desc, out);
		}
		out.println("----");
	}
	
	void printFunction(UDFDesc desc, PrintStream out) {
		out.println("== "+desc.getName()+"() ==");
		out.println();
		
		//Print description
		if(!desc.DESCRIPTION.isEmpty()) {
			out.println("  _*Description*_ " + desc.DESCRIPTION.get(0).getText());
		}
		out.println();
		
		//Print Parameters with number of arguments
		if(desc.getMinArgs() == desc.getMaxArgs()) {
			out.println("  _*Parameters*_ (" + desc.getMinArgs()+")");
		} else {
			if(desc.getMaxArgs() == Integer.MAX_VALUE) {
				out.println("  _*Parameters*_ (" + desc.getMinArgs() + " - ...)");
			} else {
				out.println("  _*Parameters*_ (" + desc.getMinArgs() + " - " + desc.getMaxArgs() +")");
			}
		}
		
		out.println( "input: {{{" + desc.getArgInfo() + "}}}");
		out.println(" output: {{{" + desc.getReturnSchema() + "}}}");
		/*if(!desc.PARAMETERS.getTagData().isEmpty()) {
			for (FnParamTag param : desc.PARAMETERS.getTagData()) {
				out.println("      # " + param.getText());
			}
		}
		out.println();
		
		//Print Return
		if(!desc.RETURN.getTagData().isEmpty()) {
			out.println("  _*Return*_ " + desc.RETURN.getTagData().get(0).getText());
		}
		*/
		out.println();
		
		//Print Examples
		if(!desc.EXAMPLES.isEmpty()) {
			out.println("  _*Examples*_ ");
			out.println("{{{");
			for (FnTextTag example : desc.EXAMPLES) {
				out.println("jaql> "+example.getText());
				out.println();
			}
			out.println("}}}");
		}
	}
}
