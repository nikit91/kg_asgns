package edu.upb.kg;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;

public class FindPublication {
	Model model;
	Query query;

	public static void main(String[] args) {
		/*String corpusPath = args[0];
		String authName = args[1];*/
		String corpusPath = "C:\\Users\\Nikit\\Desktop\\dogfood.nt";
		String authName = "Axel-Cyrille Ngonga Ngomo";
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("PREFIX dbo: <http://swrc.ontoware.org/ontology/> ");
		queryStr.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns/> ");
		queryStr.append("PREFIX attr:<http://purl.org/dc/elements/1.1/> ");
		queryStr.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema> ");
		queryStr.append("SELECT ?title (count(?author) as ?coauthcount) ");
		queryStr.append("WHERE { ");
		queryStr.append("?publication rdf:type dbo:InProceedings . ");
		queryStr.append("?publication dbo:author ?author . ");
		queryStr.append("?author rdfs:label ?authname . ");
		queryStr.append("?publication attr:title ?title . ");
		queryStr.append("FILTER (?authname = \""+authName+"\") ");
		queryStr.append("} GROUP BY ?title ORDER BY ?title ");
		FindPublication findPublication = new FindPublication();
		findPublication.query = QueryFactory.create(queryStr.toString());
		findPublication.searchData(corpusPath);

	}

	public void searchData(String corpusPath) {
		Model model = RDFDataMgr.loadModel(corpusPath);
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				RDFNode x = soln.get("title"); // Get a result variable by name.
				System.out.println(x);
			}
		}

	}

}
