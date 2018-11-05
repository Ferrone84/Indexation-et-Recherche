package indexation.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une entrée de l'index, comprenant un terme, une liste de postings
 * et la fréquence du terme exprimée en documents.
 */
public class IndexEntry implements Serializable, Comparable<IndexEntry> {
	/** Class id (juste pour éviter le warning) */
	private static final long serialVersionUID = 1L;

	/**
	 * Crée une nouvelle entrée d'index, à partir du terme passé en paramètre.
	 * 
	 * @param term Terme inséré dans l'index.
	 */
	public IndexEntry(String term) {
		this.term = term;
		this.postings = new ArrayList<Posting>();
		this.frequency = 0;
	}

	////////////////////////////////////////////////////
	// TERME
	////////////////////////////////////////////////////
	/** Terme concerné par ce posting */
	private String term;

	/**
	 * Renvoie le terme associé à cette entrée de l'index.
	 * 
	 * @return Le terme de cette entrée.
	 */
	public String getTerm() {
		return term;
	}

	////////////////////////////////////////////////////
	// POSTINGS
	////////////////////////////////////////////////////
	/** Liste des postings contenant le terme */
	private List<Posting> postings;

	/**
	 * Renvoie la liste de postings associée à cette entrée de l'index.
	 * 
	 * @return La liste de postings de cette entrée.
	 */
	public List<Posting> getPostings() {
		return postings;
	}

	/**
	 * Ajoute le posting spécifié à la liste associée à cette entrée de l'index.
	 * 
	 * @param posting Posting à ajouter à la liste de cette entrée.
	 */
	public void addPosting(Posting posting) {
		postings.add(posting);
		incrementFrequency();
	}

	////////////////////////////////////////////////////
	// FREQUENCE
	////////////////////////////////////////////////////
	/** Fréquence du terme exprimée en nombre de documents */
	private int frequency;

	/**
	 * Renvoie la fréquence associée à cette entrée de l'index, exprimée en nombre
	 * de documents.
	 * 
	 * @return La fréquence de cette entrée, en nombre de documents.
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * Incrémente la fréquence de cette entrée, d'une unité.
	 */
	public void incrementFrequency() {
		frequency++;
	}

	////////////////////////////////////////////////////
	// COMPARABLE
	////////////////////////////////////////////////////
	@Override
	public int compareTo(IndexEntry entry) {
		return term.compareTo(entry.getTerm());
	}

	////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////
	@Override
	public String toString() {
		StringBuilder postingsString = new StringBuilder();
		for (Posting posting : postings) {
			postingsString.append(posting.toString());
			postingsString.append(" ");
		}
		
		return ("<"+term+" ["+frequency+"] ( "+postingsString+")>");
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IndexEntry)) {
			return false;
		}
		return (this.compareTo((IndexEntry) o) == 0);
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
		Posting posting1 = new Posting(5);
		Posting posting2 = new Posting(10);
		Posting posting3 = new Posting(2);
		Posting posting4 = new Posting(2);
		
		IndexEntry indexEntry1 = new IndexEntry("maison");
		indexEntry1.addPosting(posting1);
		indexEntry1.addPosting(posting2);
		indexEntry1.addPosting(posting3);
		indexEntry1.addPosting(posting4);

		IndexEntry indexEntry2 = new IndexEntry("voiture");
		indexEntry2.addPosting(posting1);
		IndexEntry indexEntry3 = new IndexEntry("bateau");
		IndexEntry indexEntry4 = new IndexEntry("bateau");
		
		
		// test de equals
		System.out.println("equals with identical: " + indexEntry1.equals(indexEntry1));
		System.out.println("equals with different: " + indexEntry1.equals(indexEntry2));
		System.out.println("equals with identical but not same object: " + indexEntry3.equals(indexEntry4));


		// test de compareTo
		System.out.println("compareTo with identical: " + indexEntry1.compareTo(indexEntry1));
		System.out.println("compareTo with lower: " + indexEntry1.compareTo(indexEntry2));
		System.out.println("compareTo with higher: " + indexEntry1.compareTo(indexEntry3));
		System.out.println("compareTo with identical but not same object: " + indexEntry3.compareTo(indexEntry4));

		// test de toString
		System.out.println("toString n°1 : " + indexEntry1);
		System.out.println("toString n°2 : " + indexEntry2);
		System.out.println("toString n°3 : " + indexEntry3);
	}
}
