package tools;

import indexation.content.Token;
import indexation.processing.Normalizer;
import indexation.processing.Tokenizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Objet comptant les occurrences de termes dans un corpus et exportant le
 * résultat sous forme de fichier texte.
 */
public class TermCounter {
	/**
	 * Méthode principale.
	 * 
	 * @param args
	 *            Pas utilisé.
	 * 
	 * @throws FileNotFoundException
	 *             Problème lors de la création du fichier.
	 * @throws UnsupportedEncodingException
	 *             Problème de décodage lors de la lecture d'un document.
	 */
	public static void main(String[] args) throws FileNotFoundException,
			UnsupportedEncodingException {
		Configuration.setCorpusName("springer");
		Configuration.setStemmingTokens(true);
		TermCounter.processCorpus();
	}

	/**
	 * Compte le nombre d'occurrences de chaque terme présent dans le corpus
	 * courant, puis enregistre ces décomptes dans un fichier CSV.
	 * 
	 * @throws FileNotFoundException
	 *             Problème lors de la création du fichier.
	 * @throws UnsupportedEncodingException
	 *             Problème de décodage lors de la lecture d'un document.
	 */
	public static void processCorpus() throws FileNotFoundException,
			UnsupportedEncodingException {
		List<Token> tokens = new LinkedList<Token>();
		Map<String, Integer> counts;
		long startTotal = System.currentTimeMillis();

		// tokénization
		{
			System.out.println("Tokenizing corpus");
			long start = System.currentTimeMillis();
			Tokenizer tokenizer = new Tokenizer();
			tokenizer.tokenizeCorpus(tokens);
			long end = System.currentTimeMillis();
			System.out.println(tokens.size() + " tokens were found, duration="
					+ (end - start) + " ms\n");
		}

		// normalisation
		{
			System.out.println("Normalizing tokens");
			long start = System.currentTimeMillis();
			Normalizer normalizer = new Normalizer();
			normalizer.normalizeTokens(tokens);
			long end = System.currentTimeMillis();
			System.out.println(tokens.size()
					+ " tokens remaining after normalization, duration="
					+ (end - start) + " ms\n");
		}

		// décompte
		{
			System.out.println("Counting terms");
			long start = System.currentTimeMillis();
			counts = countTerms(tokens);
			long end = System.currentTimeMillis();
			System.out.println("There are " + counts.size()
					+ " distinct terms in the corpus, duration="
					+ (end - start) + " ms\n");
		}

		// enregistrement
		{
			String outFile = FileTools.getTermCountFile();
			System.out.println("Recording counts in " + outFile);
			long start = System.currentTimeMillis();
			writeCounts(counts, outFile);
			long end = System.currentTimeMillis();
			System.out.println("Counts recorded, duration=" + (end - start)
					+ " ms\n");
		}

		// affichage du nom du fichier des mots vides
		String stopWordsFile = FileTools.getStopWordsFile();
		System.out.println("Stop-words file: " + stopWordsFile + "\n");
		long endTotal = System.currentTimeMillis();
		System.out.println("Total duration=" + (endTotal - startTotal)
				+ " ms\n");
	}

	/**
	 * Compte le nombre d'occurrences de chaque terme dans la liste passée en
	 * paramètre.
	 * 
	 * @param tokens
	 *            Liste de tokens normalisés à traiter.
	 * @return Map associant son nombre d'occurrences à chaque terme.
	 */
	private static Map<String, Integer> countTerms(List<Token> tokens) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		for (Token token : tokens) {
			String term = token.getType();
			Integer count = result.get(term);
			if (count == null)
				count = 0;
			count++;
			result.put(term, count);
		}

		return result;
	}

	/**
	 * Enregistre les décomptes des termes.
	 * 
	 * @param counts
	 *            Map contenant les décomptes des termes.
	 * @param fileName
	 *            Nom du fichier texte à créer.
	 * 
	 * @throws FileNotFoundException
	 *             Problème lors de la création du fichier.
	 * @throws UnsupportedEncodingException
	 *             Problème lors de l'écriture des résultats.
	 */
	private static void writeCounts(Map<String, Integer> counts, String fileName)
			throws FileNotFoundException, UnsupportedEncodingException {
		File file = new File(fileName);
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		PrintWriter writer = new PrintWriter(osw);

		for (Entry<String, Integer> entry : counts.entrySet()) {
			String term = entry.getKey();
			Integer count = entry.getValue();
			writer.println("\"" + term + "\"," + count);
		}
		writer.close();
	}
}
