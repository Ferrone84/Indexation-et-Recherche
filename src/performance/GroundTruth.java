package performance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import tools.FileTools;
import indexation.content.Posting;

/**
 * Classe utilisée pour représenter une vérité terrain, i.e. une séquence de
 * requêtes d'évaluation, chacune accompagnée de sa liste de documents
 * pertinents.
 */
public class GroundTruth {
	/**
	 * Initialise la vérité terrain à partir du fichier XML spécifié dans la
	 * configuration.
	 * 
	 * @throws ParserConfigurationException
	 *             Problème lors de la lecture de la vérité terrain.
	 * @throws IOException
	 *             Problème lors de la lecture de la vérité terrain.
	 * @throws SAXException
	 *             Problème lors de la lecture de la vérité terrain.
	 */
	public GroundTruth() throws ParserConfigurationException, SAXException,
			IOException {
		String groundTruthFile = FileTools.getGroundTruthFile();
		System.out.println("Reading ground truth file " + groundTruthFile);

		// initialisation des listes
		queries = new ArrayList<String>();
		postingLists = new ArrayList<List<Posting>>();

		// ouverture du fichier XML en lecture et parsing
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();
		Document document = documentBuilder.parse(groundTruthFile);
		Element rootElt = document.getDocumentElement();

		// on traite chaque requête listée dans le fichier
		NodeList queryElts = rootElt.getElementsByTagName("query");

		System.out.print("Found " + queryElts.getLength() + " queries ( ");
		// on stocke la requête elle-même
		for (int i = 0; i < queryElts.getLength(); i++) {
			Element queryElt = (Element) queryElts.item(i);
			String queryStr = queryElt.getAttribute("expr").trim();
			queries.add(queryStr);
			List<String> nameList = new ArrayList<String>();
			
			// on lit les noms des fichiers pertinents pour la requête
			NodeList docElts = queryElt.getElementsByTagName("doc");
			System.out.print(docElts.getLength() + " ");
			
			for (int j = 0; j < docElts.getLength(); j++) {
				Element docElt = (Element) docElts.item(j);
				String file = docElt.getTextContent().trim();
				nameList.add(file);
			}
			
			// on convertit les noms de fichiers en postings
			List<Posting> postingList = FileTools
					.getPostingsFromFileNames(nameList);
			Collections.sort(postingList);
			postingLists.add(postingList);
		}
		System.out.println(")");
	}

	// //////////////////////////////////////////////////
	// REQUÊTES
	// //////////////////////////////////////////////////
	/** Liste des requêtes utilisées lors de l'évaluation */
	private List<String> queries;

	/**
	 * Renvoie les requêtes d'évaluation associées à cette vérité terrain.
	 * 
	 * @return Une liste de chaînes de caractères correspondant chacune à une
	 *         requête.
	 */
	public List<String> getQueries() {
		return queries;
	}

	// //////////////////////////////////////////////////
	// DOCUMENTS
	// //////////////////////////////////////////////////
	/** Liste de documents pertinents pour chaque requête d'évaluation */
	private List<List<Posting>> postingLists;

	/**
	 * Renvoie la liste de postings associée à larequête d'évaluation dont le
	 * numéro est spécifié en paramètre.
	 * 
	 * @param queryId
	 *            Numéro de la requête concernée.
	 * @return Liste de liste de postings.
	 */
	public List<Posting> getPostingList(int queryId) {
		List<Posting> result = postingLists.get(queryId);
		return result;
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
		// test du constructeur
		// TODO méthode à compléter (TP4-ex3)
	}
}
