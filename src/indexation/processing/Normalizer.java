package indexation.processing;

import indexation.content.Token;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import tools.Configuration;

/**
 * Objet normalisant des tokens en supprimant les signes diacritiques et en les
 * passant en minuscules.
 */
public class Normalizer implements Serializable {
	/** Class id (juste pour éviter le warning) */
	private static final long serialVersionUID = 1L;

	/**
	 * Initialise le normalisateur, en fonction de la configuration de
	 * {@link Configuration}.
	 * 
	 * @throws FileNotFoundException        Problème lors de l'accès au fichier de
	 *                                      mots vides.
	 * @throws UnsupportedEncodingException Problème lors de l'accès au fichier de
	 *                                      mots vides.
	 */
	public Normalizer() throws FileNotFoundException, UnsupportedEncodingException {
		initStemmer();
		initStopWords();
	}

	////////////////////////////////////////////////////
	// TRAITEMENT
	////////////////////////////////////////////////////
	/**
	 * Nettoie les tokens reçus en paramètres.
	 * 
	 * @param tokens Liste de tokens à traiter.
	 */
	public void normalizeTokens(List<Token> tokens) {
		// TODO méthode à compléter (TP1-ex7)
	}

	/**
	 * Nettoie le type de token reçu en paramètre. S'il ne correspond pas à un
	 * terme, c'est la valeur {@code null} qui est renvoyée.
	 * 
	 * @param type La chaîne à traiter : un type associé à un token.
	 * @return La chaîne après traitement : un terme (ou {@code null}).
	 */
	public String normalizeType(String type) {
		String result = java.text.Normalizer.normalize(type, Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();

		return (result.isEmpty()) ? null : result;
		// TODO méthode à modifier (TP4-ex14)
		// TODO méthode à modifier (TP5-ex8)
	}

	////////////////////////////////////////////////////
	// RACINISATEUR
	////////////////////////////////////////////////////
	/** Stemmeur utilisé lors de la normalistion (optionnel) */
	private AbstractStemmer stemmer;

	/**
	 * Met en place le racinisateur utilisé par ce normalisateur. En l'absence de
	 * racinisateur, le normalisateur n'effectue pas de racinisation.
	 */
	private void initStemmer() {
		if (Configuration.isStemmingTokens())
			stemmer = new PorterStemmer();
		else
			stemmer = null;
	}

	////////////////////////////////////////////////////
	// MOTS-VIDES
	////////////////////////////////////////////////////
	/** Liste des mots vides */
	private TreeSet<String> stopWords;

	/**
	 * Met en place la liste de mots-vides utilisés lors du filtrage. En l'absence
	 * de liste, aucun filtrage n'est réalisé.
	 * 
	 * @throws FileNotFoundException        Problème lors de l'accès au fichier de
	 *                                      mots vides.
	 * @throws UnsupportedEncodingException Problème lors de l'accès au fichier de
	 *                                      mots vides.
	 */
	private void initStopWords() throws FileNotFoundException, UnsupportedEncodingException {
		stopWords = new TreeSet<String>();
		if (Configuration.isFilteringStopWords())
			loadStopWords();
	}

	/**
	 * Charge la liste de mots vides.
	 * 
	 * @throws FileNotFoundException        Problème lors de l'accès au fichier de
	 *                                      mots vides.
	 * @throws UnsupportedEncodingException Problème lors de l'accès au fichier de
	 *                                      mots vides.
	 */
	private void loadStopWords() throws FileNotFoundException, UnsupportedEncodingException {
		// TODO méthode à compléter (TP5-ex7)
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
		Normalizer normalizer = new Normalizer();

		// test de normalizeType
		System.out.println("caps test: " + normalizer.normalizeType("BONJOUR JE METS DES ACCENTS"));
		System.out.println("accents test: " + normalizer.normalizeType("bonjour je mèts dês accénts"));
		System.out.println("both tests: " + normalizer.normalizeType("BonjoUr JE mèts dês accénts"));
		System.out.println("null test: " + normalizer.normalizeType(""));

		// test de normalizeTokens
		List<Token> tokens = new ArrayList<Token>();

		// test de loadStopWords
		// TODO méthode à compléter (TP5-ex7)
	}
}
