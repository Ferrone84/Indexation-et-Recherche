package indexation;

import java.util.HashMap;
import java.util.Map;

import indexation.content.IndexEntry;
import indexation.content.Posting;

/**
 * Objet représentant un index sous la forme d'un fichier inverse simple, dont
 * le lexique est stocké dans une table de hachage.
 */
public class HashIndex extends AbstractIndex {
	/** Class id (juste pour éviter le warning) */
	private static final long serialVersionUID = 1L;

	/**
	 * Construit un nouvel index vide, de la taille indiquée en paramètre.
	 * 
	 * @param size Taille de l'index (exprimée en nombre de termes).
	 */
	public HashIndex(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Size cannot be negative.");
		}
		data = new HashMap<>(size);
	}

	////////////////////////////////////////////////////
	// DONNÉES
	////////////////////////////////////////////////////
	/** Lexique et postings de l'index */
	private HashMap<String, IndexEntry> data;

	@Override
	public void addEntry(IndexEntry indexEntry, int rank) {
		data.put(indexEntry.getTerm(), indexEntry);
	}

	@Override
	public IndexEntry getEntry(String term) {
		return data.get(term);
	}

	@Override
	public int getSize() {
		return data.size();
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
		Posting posting1 = new Posting(5);
		Posting posting2 = new Posting(10);
		Posting posting3 = new Posting(2);
		Posting posting4 = new Posting(2);

		IndexEntry indexEntry1 = new IndexEntry("maison");
		indexEntry1.addPosting(posting1);
		indexEntry1.addPosting(posting2);
		indexEntry1.addPosting(posting3);
		indexEntry1.addPosting(posting4);

		IndexEntry indexEntry2 = new IndexEntry("autre");
		indexEntry1.addPosting(posting4);

		IndexEntry indexEntry3 = new IndexEntry("lambda");

		HashIndex hashIndex = new HashIndex(5);
		
		// test de print
		hashIndex.print();

		// test de addEntry
		hashIndex.addEntry(indexEntry1, 0);
		hashIndex.print();
		System.out.println("getSize: " + hashIndex.getSize());
		hashIndex.addEntry(indexEntry2, 0);
		hashIndex.print();
		System.out.println("getSize: " + hashIndex.getSize());

		// test de getEntry
		System.out.println("getEntry on existing element: " + hashIndex.getEntry(indexEntry1.getTerm()));
		System.out.println("getEntry on non existing element: " + hashIndex.getEntry(indexEntry3.getTerm()));

		// test de getSize
		System.out.println("getSize: " + hashIndex.getSize());
		hashIndex.addEntry(indexEntry1, 0);
		System.out.println("getSize: " + hashIndex.getSize());
		hashIndex.addEntry(indexEntry3, 0);
		System.out.println("getSize: " + hashIndex.getSize());
	}
}
