package indexation.content;

/**
 * Représente un token, i.e. un couple (mot,numéro) de document.
 */
public class Token implements Comparable<Token> {
	/**
	 * Crée un nouveau token à partir du type et du numéro de document passés en
	 * paramètres.
	 * 
	 * @param type  Type dont le token est une occurrence.
	 * @param docId Numéro du document concerné.
	 */
	public Token(String type, int docId) {
		this.type = type;
		this.docId = docId;
	}

	////////////////////////////////////////////////////
	// TYPE
	////////////////////////////////////////////////////
	/** Type associé au token */
	private String type;

	/**
	 * Renvoie le type associé à ce token.
	 * 
	 * @return Chaîne de caractères représentant le type de ce token.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Modifie le type associé à ce token.
	 * 
	 * @param type Chaîne de caractères représentant le nouveau type de ce token.
	 */
	public void setType(String type) {
		this.type = type;
	}

	////////////////////////////////////////////////////
	// DOC ID
	////////////////////////////////////////////////////
	/** Numéro du document contenant le token */
	private int docId;

	/**
	 * Renvoie le docId associé à ce token.
	 * 
	 * @return Entier représentant le docId de ce token.
	 */
	public int getDocId() {
		return docId;
	}

	////////////////////////////////////////////////////
	// COMPARABLE
	////////////////////////////////////////////////////
	@Override
	public int compareTo(Token token) {
		int result = type.compareTo(token.type);
		return (result == 0) ? (docId - token.docId) : result;
	}

	////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////
	@Override
	public String toString() {
		return ("(" + type + ", " + docId + ")");
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Token)) {
			return false;
		}
		return (this.compareTo((Token) o) == 0);
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
		// test du constructeur et de toString
		Token token1 = new Token("plante", 42);
		Token token2 = new Token("feuille", 68);
		Token token3 = new Token("sol", 12);
		Token token4 = new Token("sol", 12);
		Token token5 = new Token("sol", 88);
		System.out.println("toString n°1: " + token1);
		System.out.println("toString n°2: " + token2);

		// test de equals
		String testEquals = "test";
		System.out.println("equals with string: " + token1.equals(testEquals));
		System.out.println("equals with identical: " + token1.equals(token1));
		System.out.println("equals with identical but not same object: " + token3.equals(token4));
		System.out.println("equals with different: " + token1.equals(token2));
		System.out.println("equals with identical but not same object reverse: " + token4.equals(token3));

		// test de compareTo
		System.out.println("compareTo with identical: " + token1.compareTo(token1));
		System.out.println("compareTo with lower: " + token1.compareTo(token2));
		System.out.println("compareTo with higher: " + token1.compareTo(token3));
		System.out.println("compareTo with identical but not same object: " + token3.compareTo(token4));
		System.out.println("compareTo with same type: " + token4.compareTo(token5));
		System.out.println("compareTo with identical but not same object reverse: " + token4.compareTo(token3));
	}
}
