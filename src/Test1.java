import indexation.AbstractIndex;
import indexation.AbstractIndex.LexiconType;
import indexation.AbstractIndex.TokenListType;
import indexation.content.Posting;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import performance.AbstractEvaluator;
import performance.AbstractEvaluator.MeasureName;
import performance.BooleanEvaluator;
import query.AndOrQueryEngine;
import query.AndQueryEngine;
import query.DocScore;
import query.RankingQueryEngine;
import tools.Configuration;
import tools.FileTools;

/**
 * Classe permettant de tester notre indexation.
 */
public class Test1 {
	/**
	 * Méthode principale.
	 * 
	 * @param args
	 *            Pas utilisé.
	 * 
	 * @throws IOException
	 *             Problème lors d'un accès fichier.
	 * @throws ClassNotFoundException
	 *             Problème lors de la lecture de l'index
	 */
	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		// configuration de l'index
		Configuration.setCorpusName("springer");
		// Configuration.setStemmingTokens(true);
		//Configuration.setFilteringStopWords(true);

		// test de l'indexation
		testIndexation();

		// test du chargement d'index
		AbstractIndex index = AbstractIndex.read();
		System.out.println("\nPrint de l'index une fois lu : ");
		index.print();

		// test du traitement de requêtes
		testQuery();

		// test de l'évaluation de performance
		try {
			testEvaluation();
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
	}

	// //////////////////////////////////////////////////
	// INDEXATION
	// //////////////////////////////////////////////////
	/**
	 * Teste les classes permettant de créer le fichier inverse.
	 * 
	 * @throws IOException
	 *             Problème lors d'un accès fichier.
	 */
	private static void testIndexation() throws IOException {
		AbstractIndex index = AbstractIndex.indexCorpus(TokenListType.LINKED,
				LexiconType.ARRAY);
		index.write();
	}

	// //////////////////////////////////////////////////
	// REQUÊTES
	// //////////////////////////////////////////////////
	/**
	 * Teste les classes permettant de traiter les requêtes.
	 * 
	 * @throws IOException
	 *             Problème lors d'un accès fichier.
	 * @throws ClassNotFoundException
	 *             Problème lors de la lecture de l'index
	 */
	private static void testQuery() throws IOException, ClassNotFoundException {
		List<String> queries = Arrays.asList("roman",
				"recherche d'information sur le Web",
				"panneaux solaires électricité");

		// chargement de l'index
		AbstractIndex index = AbstractIndex.read();

		// résolution de la requête
		int k = 5;
		RankingQueryEngine engine = new RankingQueryEngine(index);
		for (String query : queries) {
			List<DocScore> docScores = new LinkedList<DocScore>();
			docScores = engine.processQuery(query, k);
			System.out.println("Result: " + docScores.size() + " document(s)\n"
					+ docScores);
			System.out.println("Files:\n"
					+ FileTools.getFileNamesFromDocScores(docScores) + "\n");
		}
	}

	// //////////////////////////////////////////////////
	// ÉVALUATION
	// //////////////////////////////////////////////////
	/**
	 * Calcule les performances du moteur de recherche, et les affiche dans la
	 * console.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	private static void testEvaluation() throws ClassNotFoundException,
			IOException, ParserConfigurationException, SAXException {
		// chargement de l'index
		AbstractIndex index = AbstractIndex.read();
		AndQueryEngine engine = new AndQueryEngine(index);

		// chargement de la vérité terrain et évaluation
		BooleanEvaluator evaluator = new BooleanEvaluator();
		List<Map<MeasureName, Float>> perfs = evaluator.evaluateEngine(engine);
		List<String> queries = evaluator.getGroundTruth().getQueries();
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);

		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);

		// on calcule et affiche les performances
		for (MeasureName measureName : MeasureName.values())
			System.out.print(measureName + "\t");
		System.out.println();

		for (int i = 0; i < queries.size(); i++) {
			String query = queries.get(i);
			Map<MeasureName, Float> map = perfs.get(i);

			for (MeasureName measureName : MeasureName.values())
				System.out.print(nf.format(map.get(measureName)) + "\t\t");
			System.out.println(query);
		}

		System.out
				.println("---------------------------------------------------");
		Map<MeasureName, Float> map = perfs.get(perfs.size() - 1);

		for (MeasureName measureName : MeasureName.values())
			System.out.print(nf.format(map.get(measureName)) + "\t\t");
		System.out.println();
	}
}
