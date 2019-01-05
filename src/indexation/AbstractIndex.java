package indexation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import tools.FileTools;
import indexation.content.IndexEntry;
import indexation.processing.Builder;
import indexation.processing.Normalizer;
import indexation.processing.Tokenizer;
import indexation.content.Token;

/**
 * Objet représentant un index sous la forme d'un fichier inverse simple. Les
 * classes filles différent dans la structure de données qu'elles utilisent pour
 * représenter le lexique.
 */
public abstract class AbstractIndex implements Serializable {
	/** Class id (juste pour éviter le warning) */
	private static final long serialVersionUID = 1L;

	/**
	 * Méthode de classe permettant la création d'un index prenant la forme d'un
	 * fichier inverse.
	 * 
	 * @param tokenListType
	 *            Type de liste à utiliser pour stocker les tokens lors de
	 *            l'indexation.
	 * @param lexiconType
	 *            Type de structure de données utilisée pour stocker le lexique.
	 * @return Index représentant le corpus.
	 * 
	 * @throws UnsupportedEncodingException
	 *             Problème de décodage lors de la lecture d'un document.
	 * @throws FileNotFoundException
	 *             Problème de lecture de fichier
	 */
	public static AbstractIndex indexCorpus(TokenListType tokenListType,
			LexiconType lexiconType) throws UnsupportedEncodingException,
			FileNotFoundException {

		AbstractIndex result = null;
		Tokenizer tokenizer;
		Normalizer normalizer;
		List<Token> tokens = null;

		switch (tokenListType) {
		case ARRAY:
			tokens = new ArrayList<Token>();
			break;
		case LINKED:
			tokens = new LinkedList<Token>();
			break;
		}

		int docNbr;
		long startTotal = System.currentTimeMillis();

		System.out.println("Tokenizing corpus...");
		long start = System.currentTimeMillis();
		tokenizer = new Tokenizer();
		docNbr = tokenizer.tokenizeCorpus(tokens);
		long end = System.currentTimeMillis();
		System.out.println(tokens.size() + " tokens were found, duration="
				+ (end - start) + " ms\n");

		System.out.println("Normalizing tokens...");
		start = System.currentTimeMillis();
		normalizer = new Normalizer();
		normalizer.normalizeTokens(tokens);
		end = System.currentTimeMillis();
		System.out.println(tokens.size()
				+ " tokens remaining after normalization, duration="
				+ (end - start) + " ms\n");

		System.out.println("Building index...");
		start = System.currentTimeMillis();
		Builder builder = new Builder();
		result = builder.buildIndex(tokens, lexiconType);
		end = System.currentTimeMillis();
		System.out.println("There are " + result.getSize()
				+ " entries in the index, token list=" + tokenListType
				+ ", duration=" + (end - start) + " ms\n");

		long endTotal = System.currentTimeMillis();
		System.out.println("Total duration=" + (endTotal - startTotal)
				+ " ms\n");

		System.out.println("Content of the final index :");
		result.print();
		result.tokenizer = tokenizer;
		result.normalizer = normalizer;
		result.docNbr = docNbr;
		// TODO méthode à modifier (TP2-ex8)
		return result;
	}

	/**
	 * Permet de controler le type de liste utilisé pour stocker les tokens lors
	 * de l'indexation.
	 */
	public enum TokenListType {
		/** Utilise une liste tabulée pour stocker les tokens */
		ARRAY,
		/** Utilise une liste chaînée pour stocker les tokens */
		LINKED;
	}

	/**
	 * Permet de controler le type de lexique utilisé dans l'index.
	 */
	public enum LexiconType {
		/** Utilise un tableau */
		ARRAY,
		/** Utilise une table de hashage */
		HASH,
		/** Utilise un arbre */
		TREE;
	}

	// //////////////////////////////////////////////////
	// CORPUS
	// //////////////////////////////////////////////////
	/** Nombre de documents dans la collection */
	private int docNbr;

	/**
	 * Renvoie la taille du corpus indexé, exprimée en nombre de documents.
	 * 
	 * @return Nombre de documents dans le corpus indexé.
	 */
	public int getDocumentNumber() {
		return docNbr;
	}

	// //////////////////////////////////////////////////
	// TERMES
	// //////////////////////////////////////////////////
	/**
	 * Renvoie l'entrée correspondant au terme passé en paramètre. Si une telle
	 * entrée n'existe pas, alors la méthode renvoie {@code null}.
	 * 
	 * @param term
	 *            Terme à rechercher.
	 * @return Entrée associéée au terme.
	 */
	public abstract IndexEntry getEntry(String term);

	/**
	 * Ajoute une entrée dans l'index, à la suite de celles qui y sont déjà
	 * stockées.
	 * 
	 * @param indexEntry
	 *            L'entrée à ajouter à l'index.
	 * @param rank
	 *            Le numéro de l'entrée dans le lexique. Cette information n'est
	 *            utile que dans le cas où le lexique est un tableau.
	 */
	public abstract void addEntry(IndexEntry indexEntry, int rank);

	/**
	 * Renvoie la taille de cet index (exprimée en nombre de termes).
	 * 
	 * @return Un entier correspondant au nombre de termes dans cet index.
	 */
	public abstract int getSize();

	// //////////////////////////////////////////////////
	// TOKÉNISATION
	// //////////////////////////////////////////////////
	/** Objet utilisé pour tokéniser le texte lors de l'indexation */
	private Tokenizer tokenizer;

	/**
	 * Renvoie le tokéniseur utilisé lors de la construction de cet index.
	 * 
	 * @return Tokéniseur utilisé lors de l'indexation.
	 */
	public Tokenizer getTokenizer() {
		return tokenizer;
	}

	// //////////////////////////////////////////////////
	// NORMALISATION
	// //////////////////////////////////////////////////
	/** Objet utilisé pour normaliser le texte lors de l'indexation */
	private Normalizer normalizer;

	/**
	 * Renvoie le normalisateur utilisé lors de la construction de cet index.
	 * 
	 * @return Normalisateur utilisé lors de l'indexation.
	 */
	public Normalizer getNormalizer() {
		return normalizer;
	}

	// //////////////////////////////////////////////////
	// STOCKAGE
	// //////////////////////////////////////////////////
	/**
	 * Lecture d'un index dans le fichier configuré. On utilise simplement le
	 * mécanisme de sérialisation de Java.
	 * 
	 * @return L'index lu dans le fichier.
	 * 
	 * @throws IOException
	 *             Problème lors de la lecture de l'index.
	 * @throws ClassNotFoundException
	 *             Problème lors de la lecture de l'index.
	 */
	public static AbstractIndex read() throws IOException,
			ClassNotFoundException {
		System.out.println("Loading the index");
		long start = System.currentTimeMillis();
		
		String fileName = FileTools.getIndexFile();
		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		AbstractIndex result = (AbstractIndex) ois.readObject();
		ois.close();
		
		long end = System.currentTimeMillis();
		System.out.println("Index loaded, duration=" + (end - start) + " ms\n");
		return result;

	}

	/**
	 * Enregistrement de cet index dans le fichier configuré. On utilise
	 * simplement le mécanisme de sérialisation de Java.
	 * 
	 * @throws IOException
	 *             Problème lors de l'écriture de l'index.
	 */
	public void write() throws IOException {
		System.out.println("Writing the index");
		long start = System.currentTimeMillis();

		String fileName = FileTools.getIndexFile();
		File file = new File(fileName);
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);

		oos.writeObject(this);
		oos.close();

		long end = System.currentTimeMillis();
		System.out
				.println("Index written, duration=" + (end - start) + " ms\n");
	}

	// //////////////////////////////////////////////////
	// AFFICHAGE
	// //////////////////////////////////////////////////
	/**
	 * Affiche le contenu de l'index.
	 */
	public abstract void print();

	// //////////////////////////////////////////////////
	// TEST
	// //////////////////////////////////////////////////
	/**
	 * Test des méthodes de cette classe.
	 * 
	 * @param args
	 *            Pas utilisé.
	 * 
	 * @throws Exception
	 *             Problème quelconque rencontré.
	 */
	public static void main(String[] args) throws Exception {
		// test de indexCorpus
		// TODO méthode à compléter (TP2-ex4)

		// test de write
		// TODO méthode à compléter (TP2-ex10)

		// test de read
		// TODO méthode à compléter (TP2-ex11)
	}
}
