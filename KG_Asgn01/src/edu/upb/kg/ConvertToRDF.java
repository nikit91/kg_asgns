package edu.upb.kg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

public class ConvertToRDF {

	public static final String RD_PREFIX = "@prefix rdf:\t<http://www.w3.org/1999/02/22-rdf-syntax-ns#>.";
	public static final String DBP_PREFIX = "@prefix ex:\t<http://dbpedia.org/ontology/>.";
	public static final String MAIN_URI_TMPL = "<http://dbpedia.org/ontology/${propName}#${value}>";
	public static final String DATA_TMPL = "ex:${propName} \"${value}\" ";

	public static void main(String[] args) {
		try {
			// verify the input
			if (args.length != 2) {
				System.out.println("Invalid input count! 2 inputs required.");
				return;
			}
			Path path = Paths.get(args[0]);
			List<String> entryList = new ArrayList<String>();
			Files.lines(path).forEachOrdered(s -> entryList.add(s));
			ConvertToRDF obj = new ConvertToRDF();
			obj.processToRDF(entryList, args[1]);
		} catch (IOException exception) {
			System.out.println("Error locating files.");
		}
	}
	/**
	 * Method to process the TSV entry into RDF (ttl)
	 * @param entryList list of lines of TSV file
	 * @param outputFilePath output file path
	 * @throws IOException
	 */
	public void processToRDF(List<String> entryList, String outputFilePath) throws IOException {

		String[] predicates = entryList.get(0).split("\\t");
		int predLen = predicates.length;
		// Generate main URI template
		String mainPropName = predicates[0];
		List<String> outputList = new ArrayList<String>();
		outputList.add(RD_PREFIX);
		outputList.add(DBP_PREFIX);
		String[] curEntries;
		Map<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put("propName", mainPropName);

		StrSubstitutor sub;
		for (int i = 1; i < entryList.size(); i++) {
			curEntries = entryList.get(i).split("\\t");
			valuesMap.put("value", curEntries[0]);
			sub = new StrSubstitutor(valuesMap);
			for (int j = 1; j < predLen; j++) {
				// if condition for mainProp
				if (j == 1)
					outputList.add(sub.replace(MAIN_URI_TMPL));
				// normal prop append
				else
					outputList.add(getPropStr(predicates[j], curEntries[j], j == predLen - 1));
			}
		}

		writeOutputFile(outputList, outputFilePath);
	}
	/**
	 * Method to generate the part of RDF triple based on a template
	 * @param propName name of predicate
	 * @param value value of predicate
	 * @param isLast if property is last property for current entity
	 * @return partially generated triple.
	 */
	public String getPropStr(String propName, String value, boolean isLast) {
		String propStr = null;
		Map<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put("propName", propName);
		valuesMap.put("value", value);
		StrSubstitutor sub = new StrSubstitutor(valuesMap);
		propStr = sub.replace(DATA_TMPL);
		return "\t" + propStr + (isLast ? " ." : " ;");
	}

	/**
	 * Method to write the list to an output file
	 * 
	 * @param resList
	 *            list to written
	 * @param outputFile
	 *            path to the output file
	 * @throws IOException
	 */
	public void writeOutputFile(List<String> resList, String outputFile) throws IOException {
		Path path = Paths.get(outputFile);
		Files.write(path, resList);
		System.out.println("output successfully written to: " + path.toAbsolutePath());
	}

}
