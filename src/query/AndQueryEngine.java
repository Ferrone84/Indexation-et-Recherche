package query;

import indexation.AbstractIndex;
import indexation.content.IndexEntry;
import indexation.content.Posting;
import indexation.processing.Normalizer;
import indexation.processing.Tokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Objet capable de traiter une requête booléenne sur un index.
 */
public class AndQueryEngine {
	/**
	 * Initialise ce moteur de recherche avec l'index passé en paramètre, qui
	 * sera considéré comme index de référence lors de l'évaluation des requêtes
	 * reçues.
	 * 
	 * @param index
	 *            Index de référence.
	 */
	public AndQueryEngine(AbstractIndex index) {
		this.index = index;
	}

	// //////////////////////////////////////////////////
	// TRAITEMENT GENERAL
	// //////////////////////////////////////////////////
	/**
	 * Traite la requête passée en paramètre et renvoie la liste des documents
	 * concernés.
	 * 
	 * @param query
	 *            Requête à traiter.
	 * @return Liste des documents concernés.
	 */
	public List<Posting> processQuery(String query) {
		System.out.println("Processing query \"" + query + "\"");
		long start = System.currentTimeMillis();

		// on décompose la requête et identifie les termes
		List<List<Posting>> postings = new LinkedList<List<Posting>>();
		splitQuery(query, postings);
		// System.out.println(postings);

		// on traite les opérateurs ET
		List<Posting> result;
		if (postings.size() == 1) {
			result = postings.get(0);
		} else {
			result = processConjunctions(postings);
		}

		long end = System.currentTimeMillis();
		System.out.println("Query processed, returned " + result.size()
				+ " postings, duration=" + (end - start) + " ms");
		return result;
	}

	/**
	 * Comparateur traitant deux listes de postings. On utilise simplement leurs
	 * longueurs.
	 */
	private static final Comparator<List<Posting>> COMPARATOR = new Comparator<List<Posting>>() {
		@Override
		public int compare(List<Posting> l1, List<Posting> l2) {
			int result = l1.size() - l2.size();
			return result;
		}
	};

	/**
	 * Tokénise et normalise la requête, de manière à obtenir une liste de
	 * termes. Ces termes sont ensuite traités pour récupérer les entrées
	 * correspondantes dans l'index, et surtout leurs listes de postings.
	 * 
	 * @param query
	 *            Requête à traiter.
	 * @param result
	 *            Liste résultat à compléter, qui doit contenir à la fin du
	 *            traitement les postings de l'index correspondant aux termes
	 *            obtenus après nettoyage de la requête.
	 */
	private void splitQuery(String query, List<List<Posting>> result) {
		// on tokénize la requête
		Tokenizer tokenizer = index.getTokenizer();
		List<String> types = tokenizer.tokenizeString(query);
		// on normalise chaque type
		Normalizer normalizer = index.getNormalizer();
		System.out.print(" Normalizing:");

		for (String type : types) {
			String term = normalizer.normalizeType(type);
			int postNbr = 0;

			if (term != null) {
				IndexEntry entry = index.getEntry(term);

				// si pas dans l'index, on utilise une liste vide
				if (entry == null)
					result.add(new ArrayList<Posting>());
				// sinon, on prend sa liste de postings
				else {
					List<Posting> postings = entry.getPostings();
					result.add(postings);
					postNbr = postings.size();
				}
			}
			System.out.print(" \"" + term + "\"" + "(" + postNbr + ")");
		}
		System.out.println();
	}

	// //////////////////////////////////////////////////
	// CONJONCTIONS
	// //////////////////////////////////////////////////
	/**
	 * Combine les deux listes de postings passées en paramètre en utilisant
	 * l'opérateur ET.
	 * 
	 * @param list1
	 *            Première liste de postings.
	 * @param list2
	 *            Seconde liste de postings.
	 * @return Le résultat de ET sur ces deux listes.
	 */
	private List<Posting> processConjunction(List<Posting> list1,
			List<Posting> list2) {
		List<Posting> result = new LinkedList<Posting>();
		Iterator<Posting> it1 = list1.iterator();
		Iterator<Posting> it2 = list2.iterator();

		// on fusionne le début des listes
		Posting posting1 = null;
		Posting posting2 = null;

		while ((it1.hasNext() || posting1 != null)
				&& (it2.hasNext() || posting2 != null)) {
			if (posting1 == null)
				posting1 = it1.next();
			if (posting2 == null)
				posting2 = it2.next();
			int comp = posting1.compareTo(posting2);

			// posting1 < posting2
			if (comp < 0)
				posting1 = null;
			// posting1 == posting2
			else if (comp == 0) {
				result.add(posting1);
				posting1 = null;
				posting2 = null;
			}
			// posting1 > posting2
			else if (comp > 0)
				posting2 = null;
		}

		System.out.println(" Processing conjunction: (" + list1.size()
				+ ") AND (" + list2.size() + ") >> (" + result.size() + ")");

		return result;
	}

	/**
	 * Traite une conjonction de plus de deux termes.
	 * 
	 * @param lists
	 *            Liste de listes de postings de l'index, correspondant aux
	 *            termes à traiter.
	 * @return Intersection de toutes les listes de postings.
	 */
	private List<Posting> processConjunctions(List<List<Posting>> lists) {
		// on ordonne la liste de postings
		Collections.sort(lists, COMPARATOR);
		System.out.print(" Ordering posting list:");
		for (List<Posting> list : lists)
			System.out.print(" (" + list.size() + ")");
		System.out.println();

		// on traite les deux premières
		List<Posting> list1 = lists.get(0);
		lists.remove(0);
		List<Posting> list2 = lists.get(0);
		lists.remove(0);
		List<Posting> result = processConjunction(list1, list2);

		// on traite chaque liste restante une par une
		Iterator<List<Posting>> it = lists.iterator();

		while (it.hasNext() && !result.isEmpty()) {
			List<Posting> list = it.next();
			result = processConjunction(result, list);
		}

		return result;
	}

	// //////////////////////////////////////////////////
	// INDEX
	// //////////////////////////////////////////////////
	/** Index de référence */
	private AbstractIndex index;

	/**
	 * Renvoie l'index associé à ce moteur.
	 * 
	 * @return Index associé à ce moteur.
	 */
	public AbstractIndex getIndex() {
		return index;
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
		// test de splitQuery
		// TODO méthode à compléter (TP3-ex1)

		// test de processConjunction
		// TODO méthode à compléter (TP3-ex2)

		// test de COMPARATOR
		// TODO méthode à compléter (TP3-ex3)

		// test de processConjunctions
		// TODO méthode à compléter (TP3-ex4)

		// test de processQuery
		// TODO méthode à compléter (TP3-ex5)
	}
}
