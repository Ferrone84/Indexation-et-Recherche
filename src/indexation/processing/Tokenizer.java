package indexation.processing;

import indexation.content.Token;
import tools.Configuration;
import tools.FileTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Objet segmentant des textes en utilisant tous les caractères non
 * alphanumériques comme séparateurs.
 */
public class Tokenizer implements Serializable {
	/** Class id (juste pour éviter le warning) */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////
	// TRAITEMENT
	////////////////////////////////////////////////////
	/**
	 * Tokenize tout le corpus et renvoie les tokens obtenus via la liste passée en
	 * paramètre. La méthode renvoie aussi le nombre de documents traités.
	 * 
	 * @param tokens Liste de tokens résultant du traitement.
	 * @return Nombre de documents traités.
	 * 
	 * @throws UnsupportedEncodingException Problème de décodage lors de la lecture
	 *                                      d'un document.
	 */
	public int tokenizeCorpus(List<Token> tokens) throws UnsupportedEncodingException {
		int docId = 0;

		String folder = FileTools.getCorpusFolder();
		File[] listOfFiles = new File(folder).listFiles();

		Arrays.sort(listOfFiles, new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				String fileName1 = f1.getName();
				String fileName2 = f2.getName();

				return fileName1.compareTo(fileName2);
			}
		});

		for (File file : listOfFiles) {
			if (file.isFile()) {
				tokenizeDocument(file, docId, tokens);
				docId++;
			}
		}

		return docId;
	}

	/**
	 * Méthode qui segmente le document spécifié, et renvoie le résultat en
	 * complétant la liste passée en paramètre.
	 * 
	 * @param document Fichier contenant le document à traiter.
	 * @param docId    Numéro du document à traiter.
	 * @param tokens   La liste de tokens à compléter.
	 * 
	 * @throws UnsupportedEncodingException Problème de décodage lors de la lecture
	 *                                      d'un document.
	 */
	public void tokenizeDocument(File document, int docId, List<Token> tokens) throws UnsupportedEncodingException {
		try {
			FileInputStream fileInputStream = new FileInputStream(document);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			Scanner scanner = new Scanner(inputStreamReader);

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				for (String token : tokenizeString(line)) {
					if (!token.isEmpty()) {
						tokens.add(new Token(token, docId));
					}
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Renvoie la liste des tokens pour la chaîne de caractères spécifiée.
	 * 
	 * @param string Chaîne de caractères à traiter.
	 * @return La liste de types correspondant.
	 */
	public List<String> tokenizeString(String string) {
		String regex = "[^\\pL\\pN]+";
		return Arrays.asList(string.split(regex));
	}

	////////////////////////////////////////////////////
	// TEST
	////////////////////////////////////////////////////
	/**
	 * Test des méthodes de cette classe.
	 * 
	 * @param args Pas utilisé.
	 * 
	 * @throws Exception Problème quelconque rencontré.
	 */
	public static void main(String[] args) throws Exception {
		Tokenizer tokenizer = new Tokenizer();

		// test de tokenizeString
		List<String> tokens = tokenizer.tokenizeString(
				"En grammaire, une phrase peut être considérée comme un ensemble autonome, réunissant des unités syntaxiques organisées selon différents réseaux de relations plus ou moins complexes appelés subordination, coordination ou juxtaposition.");
		System.out.println("tokenizeString display: ");
		for (String token : tokens) {
			System.out.print(token + "/");
		}
		System.out.println("\ntokenizeString display end.");

		// test de tokenizeDocument
		String file = ".." + File.separator + "Common" + File.separator + "wp" + File.separator
				+ "001f1107-8e72-4250-8b83-ef02eeb4d4a4.txt";
		List<Token> tokensList = new ArrayList<Token>();
		tokenizer.tokenizeDocument(new File(file), 0, tokensList);
		System.out.println("tokenizeDocument display: ");
		for (Token token : tokensList) {
			System.out.print(token + "/");
		}
		System.out.println("\ntokenizeDocument display end.");

		// test de tokenizeCorpus
		tokensList = new ArrayList<Token>();
		Configuration.setCorpusName("wp_test");
		int documentNumber = tokenizer.tokenizeCorpus(tokensList);
		System.out.println("tokenizeCorpus: " + tokensList.size() + " number of tokens for " + documentNumber + " documents.");
	}
}
