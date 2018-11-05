package indexation;

import indexation.content.IndexEntry;
import indexation.content.Posting;

/**
 * Objet représentant un index sous la forme d'un fichier inverse simple, dont
 * le lexique est stocké dans un tableau.
 */
public class ArrayIndex extends AbstractIndex {
	/** Class id (juste pour éviter le warning) */
	private static final long serialVersionUID = 1L;

	/**
	 * Construit un nouvel index vide, de la taille indiquée en paramétre.
	 * 
	 * @param size Taille de l'index (expriée en nombre de termes).
	 */
	public ArrayIndex(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Size cannot be negative.");
		}
		data = new IndexEntry[size];
	}

	////////////////////////////////////////////////////
	// DONNÉES
	////////////////////////////////////////////////////
	/** Lexique et postings de l'index */
	private IndexEntry[] data;

	@Override
	public void addEntry(IndexEntry indexEntry, int rank) {
		// TODO méthode à compléter (TP1-ex12)
		//data[0] = indexEntry;
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

	/**
	 * Renvoie le tableau correspondant au lexique de cet index.
	 * 
	 * @return Lexique sous forme de tableau d'entrées.
	 */
	public IndexEntry[] getEntries() {
		return data;
	}

	////////////////////////////////////////////////////
	// AFFICHAGE
	////////////////////////////////////////////////////
	/**
	 * Affiche le contenu de l'index.
	 */
	@Override
	public void print() {
		for (IndexEntry indexEntry : data) {
			System.out.println(indexEntry);
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
		Posting posting1 = new Posting(5);
		Posting posting2 = new Posting(10);
		Posting posting3 = new Posting(2);
		Posting posting4 = new Posting(2);
		
		IndexEntry indexEntry1 = new IndexEntry("maison");
		indexEntry1.addPosting(posting1);
		indexEntry1.addPosting(posting2);
		indexEntry1.addPosting(posting3);
		indexEntry1.addPosting(posting4);
		
		ArrayIndex arrayIndex = new ArrayIndex(5);

		// test de print
		arrayIndex.print();

		// test de addEntry
		// TODO méthode à compléter (TP1-ex12)
		//arrayIndex.addEntry(indexEntry1, rank);

		// test de getEntry
		// TODO méthode à compléter (TP1-ex13)

		// test de getSize
		// TODO méthode à compléter (TP1-ex14)
	}
}
