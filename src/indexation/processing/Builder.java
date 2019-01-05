package indexation.processing;

import indexation.AbstractIndex;
import indexation.AbstractIndex.LexiconType;
import indexation.ArrayIndex;
import indexation.HashIndex;
import indexation.TreeIndex;
import indexation.content.IndexEntry;
import indexation.content.Posting;
import indexation.content.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import tools.Configuration;

/**
 * Objet construisant un index prenant la forme d'un fichier inversé. Il a pour
 * cela besoin de recevoir la liste normalisée des paires (tokens, docId).
 */
public class Builder {
	/**
	 * Construit l'index à partir des tokens passés en paramètres.
	 * 
	 * @param tokens
	 *            Liste normalisée de tokens à traiter.
	 * @param lexiconType
	 *            Type de structure de données utilisée pour stocker le lexique.
	 * @return L'index produit.
	 */
	public AbstractIndex buildIndex(List<Token> tokens, LexiconType lexiconType) {
		int indexSize;
		AbstractIndex result = null;
		List<Integer> frequencies = new LinkedList<Integer>();

		System.out.println("Sorting tokens...");
		long start = System.currentTimeMillis();
		Collections.sort(tokens);
		long end = System.currentTimeMillis();
		System.out.println(" " + tokens.size() + " tokens sorted, duration="
				+ (end - start) + " ms\n");

		System.out.println("Filtering tokens...");
		start = System.currentTimeMillis();
		indexSize = filterTokens(tokens, frequencies);
		end = System.currentTimeMillis();
		System.out.println(" " + tokens.size()
				+ " tokens remaining, corresponding to " + indexSize
				+ " terms, duration=" + (end - start) + " ms\n");

		System.out.println("Building posting lists...");
		start = System.currentTimeMillis();
		switch (lexiconType) {
		case ARRAY:
			result = new ArrayIndex(indexSize);
			break;
		case HASH:
			result = new HashIndex(indexSize);
			break;
		case TREE:
			result = new TreeIndex();
			break;
		}
		int postingNumber = buildPostings(tokens, frequencies, result);
		end = System.currentTimeMillis();
		System.out.println(" " + postingNumber + " postings listed, lexicon="
				+ lexiconType + ", duration=" + (end - start) + " ms\n");

		// TODO méthode à modifier (TP2-ex8)
		return result;
	}

	/**
	 * Supprime de la liste les occurrences multiples de tokens, à condition
	 * qu'ils appartiennent au même document. Bien sûr, on garde quand même une
	 * occurrence.
	 * 
	 * @param tokens
	 *            La liste normalisée et triée de tokens à traiter.
	 * @return Nombre de termes distincts dans la liste.
	 */
	private int filterTokens(List<Token> tokens) {
		int result = 0;

		Token savedToken = null;
		for (Iterator<Token> iter = tokens.listIterator(); iter.hasNext();) {
			Token currentToken = iter.next();

			if (savedToken == null) {
				result++;
			} else {
				if (currentToken.equals(savedToken)) {
					iter.remove();
				}
				if (!currentToken.getType().equals(savedToken.getType())) {
					result++;
				}
			}
			savedToken = currentToken;
		}

		return result;
	}

	/**
	 * Supprime de la liste les occurrences multiples de tokens, à condition
	 * qu'elles appartiennent au même document. Bien sûr, on garde quand même
	 * une occurrence. <br/>
	 * Par rapport à {@link #filterTokens(List)}, cette méthode calcule en plus
	 * les fréquences des tokens dans chaque document où ils apparaissent.
	 * 
	 * @param tokens
	 *            La liste normalisée et triée de tokens à traiter.
	 * @param frequencies
	 *            La liste des fréquences associées à ces tokens.
	 * @return Nombre de termes distincts dans la liste.
	 */
	public int filterTokens(List<Token> tokens, List<Integer> frequencies) {
		int result = 0;
		int count = 0;

		// on passe chaque paire de tokens consécutifs en revue
		Iterator<Token> it = tokens.iterator();
		Token t1 = null;

		while (it.hasNext()) {
			Token t2 = it.next();

			if (t1 == null) {
				result = 1;
				count = 1;
			} else {// si deux tokens consécutifs sont identiques,on supprime le
					// second
				if (t1.equals(t2)) {
					it.remove();
					count++;
				} else {
					frequencies.add(count);
					count = 1;
				}
				// on compte les termes
				String type1 = t1.getType();
				String type2 = t2.getType();
				if (!type1.equals(type2))
					result++;
			}
			t1 = t2;
		}
		frequencies.add(count);
		return result;
	}

	/**
	 * Construit un index à partir de la liste de tokens normalisée, triée et
	 * filtrée passée en paramètre.
	 * 
	 * @param tokens
	 *            Liste normalisée, triée et filtrée de tokens.
	 * @param index
	 *            L'index obtenu, sous forme de fichier inverse.
	 * @return Nombre de postings listés.
	 */
	private int buildPostings(List<Token> tokens, AbstractIndex index) {
		int result = 0;
		int i = 0;
		IndexEntry entry = null;

		for (Token token : tokens) {
			String type = token.getType();

			if (entry == null || !entry.getTerm().equals(type)) {
				entry = new IndexEntry(type);
				index.addEntry(entry, i);
				i++;
			}

			int docId = token.getDocId();
			Posting posting = new Posting(docId);
			entry.addPosting(posting);
			result++;
		}

		return result;
	}

	/**
	 * Construit un index à partir de la liste de tokens normalisée, triée et
	 * filtrée passée en paramètre. <br/>
	 * La différence avec {@link #buildPostings(List, AbstractIndex)} est que
	 * cette méthode prend une liste supplémentaires contenant les fréquences
	 * des termes.
	 * 
	 * @param tokens
	 *            Liste normalisée, triée et filtrée de tokens.
	 * @param frequencies
	 *            La liste des fréquences associées à ces tokens.
	 * @param index
	 *            L'index obtenu, sous forme de fichier inverse.
	 * @return Nombre de postings listés.
	 */
	private int buildPostings(List<Token> tokens, List<Integer> frequencies,
			AbstractIndex index) {
		int result = 0;
		int i = 0;
		IndexEntry entry = null;
		
		// on traite chaque token séparément
		Iterator<Token> itTok = tokens.iterator();
		Iterator<Integer> itFreq = frequencies.iterator();
		
		while (itTok.hasNext()) {
			Token token = itTok.next();
			int frequency = itFreq.next();
			String type = token.getType();
			
			// si besoin, on crée une nouvelle entrée
			if (entry == null || !entry.getTerm().equals(type)) {
				entry = new IndexEntry(type);
				index.addEntry(entry, i);
				i++;
			}
			
			// dans tous les cas, on met à jour la liste de postings
			int docId = token.getDocId();
			Posting posting = new Posting(docId, frequency);
			entry.addPosting(posting);
			result++;
		}
		
		return result;
	}

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
		Builder builder = new Builder();
		Tokenizer tokenizer = new Tokenizer();
		List<Token> tokensList = new ArrayList<Token>();

		String corpus = "wp_test";
		Configuration.setCorpusName(corpus);
		int documentNumber = tokenizer.tokenizeCorpus(tokensList);

		// test de filterTokens
		int nbTermes = builder.filterTokens(tokensList);
		System.out.println("filterTokens on " + corpus + " : " + nbTermes);

		// test de buildPostings
		AbstractIndex abstractIndex = new HashIndex(nbTermes);
		int nbPostings = builder.buildPostings(tokensList, abstractIndex);
		System.out.println("buildPostings : " + nbPostings);

		// test de buildIndex
		System.out.println("\nbuildIndex ARRAY : ");
		AbstractIndex index = builder.buildIndex(tokensList, LexiconType.ARRAY);
		System.out.println("\nbuildIndex HASH : ");
		AbstractIndex index2 = builder.buildIndex(tokensList, LexiconType.HASH);

		// test de filterTokens
		// TODO méthode à compléter (TP6-ex4)

		// test de buildPostings
		// TODO méthode à compléter (TP6-ex5)
	}
}
