package indexation.content;

import java.io.Serializable;

/**
 * Représente un posting, c'est à dire ici : simplement le numéro du document
 * contenant un token donné.
 */
public class Posting implements Serializable, Comparable<Posting> {
	/** Class id (juste pour éviter le warning) */
	private static final long serialVersionUID = 1L;

	/**
	 * Construit un nouveau posting à partir du numéro de document passé en
	 * paramètre. <br/>
	 * <b>Note :</b> À utiliser à partir du TP 1.
	 * 
	 * @param docId Numéro du document concerné.
	 */
	public Posting(int docId) {
		this.docId = docId;
		this.frequency = 0;
	}

	/**
	 * Construit un nouveau posting à partir du numéro de document et de la
	 * fréquence passés en paramètre. <br/>
	 * <b>Note :</b> À utiliser dans le TP 6
	 * 
	 * @param docId     Numéro du document concerné.
	 * @param frequency Fréquence du terme dans ce document.
	 */
	public Posting(int docId, int frequency) {
		this.docId = docId;
		this.frequency = frequency;
	}

	////////////////////////////////////////////////////
	// DOC ID
	////////////////////////////////////////////////////
	/** Numéro du document représenté par ce posting */
	private int docId;

	/**
	 * Renvoie le docId associé à ce posting.
	 * 
	 * @return Entier représentant le docId de ce posting.
	 */
	public int getDocId() {
		return docId;
	}

	////////////////////////////////////////////////////
	// FREQUENCE
	////////////////////////////////////////////////////
	/** Fréquence du terme dans le document */
	private int frequency;

	/**
	 * Renvoie la fréquence du terme dans le document correspondant à ce posting.
	 * 
	 * @return Nombre d'occurrences du terme dans le document.
	 */
	public int getFrequency() {
		return frequency;
	}

	////////////////////////////////////////////////////
	// COMPARABLE
	////////////////////////////////////////////////////
	@Override
	public int compareTo(Posting posting) {
		return docId - posting.getDocId();
	}

	////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////
	@Override
	public String toString() {
		String result = "<" + docId + " [" + frequency + "]>";
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Posting)) {
			return false;
		}
		return (this.compareTo((Posting) o) == 0);
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
		
		// test de compareTo
		System.out.println("compareTo with identical: " + posting1.compareTo(posting1));
		System.out.println("compareTo with lower: " + posting1.compareTo(posting2));
		System.out.println("compareTo with higher: " + posting1.compareTo(posting3));
		System.out.println("compareTo with identical but not same object: " + posting3.compareTo(posting4));

		// test de equals
		System.out.println("equals with identical: " + posting1.equals(posting1));
		System.out.println("equals with different: " + posting1.equals(posting2));
		System.out.println("equals with identical but not same object: " + posting3.equals(posting4));

		// test de toString
		System.out.println("toString n°1 : " + posting1);
		System.out.println("toString n°2 : " + posting2);
		System.out.println("toString n°3 : " + posting3);
	}
}
