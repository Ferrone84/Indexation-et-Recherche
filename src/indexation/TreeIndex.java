package indexation;

import java.util.Map;
import java.util.TreeMap;

import indexation.content.IndexEntry;

/**
 * Objet représentant un index sous la forme d'un fichier inverse simple, dont
 * le lexique est stocké dans un arbre de recherche.
 */
public class TreeIndex extends AbstractIndex {
	/** Class id (juste pour éviter le warning) */
	private static final long serialVersionUID = 1L;

	/**
	 * Construit un nouvel index vide, de la taille indiquée en paramètre.
	 */
	public TreeIndex() {
		data = new TreeMap<String, IndexEntry>();
	}

	////////////////////////////////////////////////////
	// DONNÉES
	////////////////////////////////////////////////////
	/** Lexique et postings de l'index */
	private TreeMap<String, IndexEntry> data;

	@Override
	public void addEntry(IndexEntry indexEntry, int rank) {
		// TODO méthode à compléter (TP1-ex12)
	}

	@Override
	public IndexEntry getEntry(String term) {
		IndexEntry result = null;
		// TODO méthode à compléter (TP1-ex13)
		return result;
	}

	@Override
	public int getSize() {
		int result = 0;
		// TODO méthode à compléter (TP1-ex14)
		return result;
	}

	////////////////////////////////////////////////////
	// AFFICHAGE
	////////////////////////////////////////////////////
	/**
	 * Affiche le contenu de l'index.
	 */
	@Override
	public void print() {
		for(Map.Entry<String, IndexEntry> entry : data.entrySet()) {
			System.out.println(entry.getValue());
		}
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
		// test du constructeur
		HashIndex hashIndex = new HashIndex(5);

		// test de print
		hashIndex.print();

		// test de addEntry
		// TODO méthode à compléter (TP1-ex12)

		// test de getEntry
		// TODO méthode à compléter (TP1-ex13)

		// test de getSize
		// TODO méthode à compléter (TP1-ex14)
	}
}
