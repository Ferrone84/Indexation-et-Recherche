package indexation;

import java.util.Arrays;

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
		if (rank < 0) {
			throw new IllegalArgumentException("Rank cannot be negative.");
		}

		if (rank >= data.length) {
			throw new IllegalArgumentException("Rank cannot be superior to the list size.");
		}

		data[rank] = indexEntry;
	}

	@Override
	public IndexEntry getEntry(String term) {
		if (term == null) {
			return null;
		}

		int searchResult = Arrays.binarySearch(data, new IndexEntry(term));
		if (searchResult < 0) {
			return null;
		}

		return data[searchResult];
	}

	@Override
	public int getSize() {
		int index = 0;

		for (IndexEntry indexEntry : data) {
			if (indexEntry != null) {
				index++;
			}
		}

		return index;
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
			if (indexEntry != null) {
				System.out.println(indexEntry);
			}
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

		IndexEntry indexEntry1 = new IndexEntry("autre");
		indexEntry1.addPosting(posting1);
		indexEntry1.addPosting(posting2);
		indexEntry1.addPosting(posting3);
		indexEntry1.addPosting(posting4);

		IndexEntry indexEntry2 = new IndexEntry("lambda");
		indexEntry1.addPosting(posting4);

		IndexEntry indexEntry3 = new IndexEntry("maison");

		ArrayIndex arrayIndex = new ArrayIndex(2);

		// test de print
		arrayIndex.print();

		// test de addEntry
		arrayIndex.addEntry(indexEntry1, 0);
		arrayIndex.addEntry(indexEntry2, 1);
		arrayIndex.print();

		// test de getEntry
		System.out.println("getEntry on existing element: " + arrayIndex.getEntry(indexEntry1.getTerm()));
		System.out.println("getEntry on non existing element: " + arrayIndex.getEntry(indexEntry3.getTerm()));

		// test de getSize
		System.out.println("getSize: " + arrayIndex.getSize());
		arrayIndex.addEntry(indexEntry1, 0);
		System.out.println("getSize: " + arrayIndex.getSize());
		arrayIndex.addEntry(indexEntry1, 2);	//Generate an exception because the array have a size of 2, not 3
		System.out.println("getSize: " + arrayIndex.getSize());
	}
}
