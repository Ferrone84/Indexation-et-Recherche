package query;

import indexation.AbstractIndex;
import indexation.content.IndexEntry;
import indexation.content.Posting;
import indexation.processing.Normalizer;
import indexation.processing.Tokenizer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Objet capable de traiter une requête de type sac-de-mots.
 */
public class RankingQueryEngine {
	/**
	 * Initialise ce moteur de requête avec l'index passé en paramètre, qui sera
	 * considéré comme index de référence lors de l'évaluation des requêtes
	 * reçues.
	 * 
	 * @param index
	 *            Index de référence.
	 */
	public RankingQueryEngine(AbstractIndex index) {
		this.index = index;
	}

	// //////////////////////////////////////////////////
	// TRAITEMENT GENERAL
	// //////////////////////////////////////////////////
	/**
	 * Traite la requête passée en paramètre et renvoie la liste ordonnée des
	 * {@code k} documents les plus pertinents, sous dorme d'objets
	 * {@link DocScore}. La valeur zéro pour {@code k} signifie qu'on veut tous
	 * les documents du corpus.
	 * 
	 * @param query
	 *            Requête à traiter.
	 * @param k
	 *            Nombre maximum de documents à renvoyer, ou zéro pour tous les
	 *            documents.
	 * @return Liste ordonnée des documents sélectionnés, avec leurs scores.
	 */
	public List<DocScore> processQuery(String query, int k) {
		System.out.println("Processing query \"" + query + "\"");
		long start = System.currentTimeMillis();
		
		// on décompose la requête et identifie les termes
		List<IndexEntry> queryEntries = new LinkedList<IndexEntry>();
		splitQuery(query, queryEntries);
		
		// on calcule les scores
		List<DocScore> result = new LinkedList<DocScore>();
		sortDocuments(queryEntries, k, result);
		
		long end = System.currentTimeMillis();
		System.out.println("Query processed, returned " + result.size()
				+ " values,	duration=" + (end - start) + " ms");
		
		return result;
	}

	/**
	 * Tokénise et normalise la requête, de manière à obtenir une liste de
	 * termes. Ces termes sont ensuite traités pour récupérer les entrées
	 * correspondantes dans l'index, et surtout leurs listes de postings.
	 * 
	 * @param query
	 *            Requête à traiter.
	 * @param result
	 *            Liste résultat à compléter, qui doit contenir à la fin du
	 *            traitement les entrées de l'index correspondant aux termes
	 *            obtenus après nettoyage de la requête.
	 */
	private void splitQuery(String query, List<IndexEntry> result) {
		// on tokénize la requête
		Tokenizer tokenizer = index.getTokenizer();
		List<String> types = tokenizer.tokenizeString(query);
		// on normalise chaque type
		Normalizer normalizer = index.getNormalizer();

		// la normalisation du type donne le terme (ou null)
		for (String type : types) {
			String term = normalizer.normalizeType(type);

			if (term != null) { // on récupère l'entrée associée au terme dans
								// l'index
				IndexEntry entry = index.getEntry(term);
				// si pas dans l'index, on n'ajoute rien dans la liste
				// sinon, on ajoute l'entrée dans la liste résultat
				if (entry != null)
					result.add(entry);
			}
		}
	}

	// //////////////////////////////////////////////////
	// POIDS
	// //////////////////////////////////////////////////
	/**
	 * Calcule la pondération log-fréquence associée à un terme dans un
	 * document.
	 * 
	 * @param posting
	 *            Document à traiter, sous forme de posting.
	 * @return Poids correspondant au document.
	 */
	private float processWf(Posting posting) {
		int tf = posting.getFrequency();
		float result = 0;
		if (tf > 0)
			result = 1 + (float) Math.log10(tf);
		return result;
	}

	/**
	 * Calcule la fréquence de document inverse associée à un terme dans une
	 * collection.
	 * 
	 * @param entry
	 *            Terme considéré, sous la forme d'une entrée d'index.
	 * @return Fréquence inverse correspondant.
	 */
	private float processIdf(IndexEntry entry) {
		float df = entry.getFrequency();
		int docNbr = index.getDocumentNumber();
		float result = (float) Math.log10(docNbr / df);
		return result;
	}

	// //////////////////////////////////////////////////
	// SCORES
	// //////////////////////////////////////////////////
	/**
	 * Trie les documents en fonction de leur similarité avec la requête
	 * spécifiée, et ne garde que les {@code k} plus pertinents (ainsi que leurs
	 * scores). La valeur zéro pour {@code k} indique que l'on veut renvoyer la
	 * liste complète de tous les documents (toujours avec leur score).
	 * 
	 * @param queryEntries
	 *            Entrées correspondant à la requête à traiter.
	 * @param k
	 *            Nombre de documents désiré (ou zéro pour tous les documents).
	 * @param docScores
	 *            DocIds et scores des {@code k} documents les plus pertinents.
	 */
	private void sortDocuments(List<IndexEntry> queryEntries, int k,
			List<DocScore> docScores) {
		// initialisation
		TreeSet<DocScore> orderedIds = new TreeSet<DocScore>();
		int docNbr = index.getDocumentNumber();
		final float scores[] = new float[docNbr];
		Arrays.fill(scores, 0);
		float norms[] = new float[docNbr];
		Arrays.fill(norms, 0);
		float queryNorm = 0;

		// on parcourt tous les termes de la requête
		for (IndexEntry entry : queryEntries) {
			float idf = processIdf(entry);
			// on calcule le poids individuel du terme pour la requête
			float stq = idf;
			// on met à jour sa norme
			queryNorm = queryNorm + (float) Math.pow(stq, 2);

			// pour tous les postings contenant le terme traité
			List<Posting> postings = entry.getPostings();
			for (Posting posting : postings) { // on calcule le score individuel
												// du terme pour le document
				float std = processWf(posting) * idf;
				// on met à jour les scores et normes
				int docId = posting.getDocId();
				scores[docId] = scores[docId] + stq * std;
				norms[docId] = norms[docId] + (float) Math.pow(std, 2);
			}
		}

		// on termine le calcul des normes
		for (int i = 0; i < norms.length; i++)
			norms[i] = (float) Math.sqrt(norms[i]);
		queryNorm = (float) Math.sqrt(queryNorm);

		// on termine le calcul des scores et on ordonne les documents
		for (int i = 0; i < scores.length; i++) {
			if (norms[i] == 0)
				scores[i] = 0;
			else
				scores[i] = scores[i] / (norms[i] * queryNorm);

			DocScore docScore = new DocScore(i, scores[i]);
			orderedIds.add(docScore);
		}

		// on garde tout si k vaut zéro
		if (k == 0)
			k = orderedIds.size();

		// on ajoute dans le bon ordre
		Iterator<DocScore> it = orderedIds.descendingIterator();
		int i = 0;
		while (i < k && it.hasNext()) {
			DocScore docScore = it.next();
			docScores.add(docScore);
			i++;
		}
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
		// test de processWf
		// TODO méthode à compléter (TP6-ex7)

		// test de processIdf
		// TODO méthode à compléter (TP6-ex8)

		// test de sortDocuments
		// TODO méthode à compléter (TP6-ex9)

		// test de splitQuery
		// TODO méthode à compléter (TP6-ex10)

		// test de processQuery
		// TODO méthode à compléter (TP6-ex11)
	}
}
