package performance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import tools.FileTools;
import indexation.content.Posting;

/**
 * Classe utilisée pour mettre en commun les méthodes et champs nécessaires à
 * l'évaluation de la performance d'un index sur un jeu de requêtes prédéfinies,
 * pour lesquelles on connait la vérité terrain.
 */
public abstract class AbstractEvaluator {
	/**
	 * Initialise un évaluateur pour la vérité terrain spécifiée dans la
	 * configuration.
	 * 
	 * @throws ParserConfigurationException
	 *             Problème lors de la lecture de la vérité terrain
	 * @throws IOException
	 *             Problème lors de la lecture de la vérité terrain
	 * @throws SAXException
	 *             Problème lors de la lecture de la vérité terrain
	 */
	public AbstractEvaluator() throws ParserConfigurationException,
			SAXException, IOException {
		groundTruth = new GroundTruth();
	}

	/**
	 * Nom d'une mesure de performance utilisée pour évaluer le processus
	 * d'indexation et de recherche.
	 */
	public enum MeasureName {
		/** Précision */
		PRECISION,
		/** Rappel */
		RECALL,
		/** F-mesure ou F1-score */
		F_MEASURE;
	}

	// //////////////////////////////////////////////////
	// VÉRITÉ TERRAIN
	// //////////////////////////////////////////////////
	/** Vérité terrain */
	protected GroundTruth groundTruth;

	/**
	 * Renvoie la vérité terrain chargée à la construction de cet évaluateur.
	 * 
	 * @return Objet représentant la vérité terrain.
	 */
	public GroundTruth getGroundTruth() {
		return groundTruth;
	}

	// //////////////////////////////////////////////////
	// MESURES
	// //////////////////////////////////////////////////
	/**
	 * Calcule les trois mesures du cours permettant d'évaluer le resultat de la
	 * requête considérée. Le paramètre {@code queryId} est le numéro de cette
	 * requête parmi celles constituant la vérité terrain, et le paramètre
	 * {@code answer} est la liste de posting renvoyée par le moteur de
	 * recherche pour la requête.
	 * 
	 * @param queryId
	 *            Numéro de la requête parmi celles constituant la vérité
	 *            terrain.
	 * @param answer
	 *            Liste de postings renvoyée par le moteur de recherche pour
	 *            cette requête.
	 * @return Map contenant les performances calculées pour cette requête et ce
	 *         résultat.
	 */
	protected Map<MeasureName, Float> evaluateQueryAnswer(int queryId,
			List<Posting> answer) {
		List<Posting> reference = groundTruth.getPostingList(queryId);
		// on pourrait plutot utiliser la méthode processConjunction
		// du moteur de recherche, mais ici, la vitesse n'est pas une priorité
		List<Posting> inter = new ArrayList<Posting>(reference);
		inter.retainAll(answer);

		float precision = 0;
		float recall = 1;
		float fmeasure = 0;
		int tp = inter.size();
		int fp = answer.size() - tp;
		int fn = reference.size() - tp;

		if (!answer.isEmpty())
			precision = tp / (float) (tp + fp);
		if (!reference.isEmpty())
			recall = tp / (float) (tp + fn);
		if (precision != 0 && recall != 0)
			fmeasure = 2 * precision * recall / (precision + recall);

		Map<MeasureName, Float> result = new HashMap<MeasureName, Float>();
		result.put(MeasureName.PRECISION, precision);
		result.put(MeasureName.RECALL, recall);
		result.put(MeasureName.F_MEASURE, fmeasure);

		return result;
	}

	/**
	 * Calcule les trois mesures sur toutes les requêtes considérées Le
	 * paramètre {@code answers} est la liste des réponses renvoyées par le
	 * moteur de recherche pour toutes ces requêtes.
	 * 
	 * @param answers
	 *            Liste de listes de postings renvoyée par le moteur de
	 *            recherche pour les requêtes traitées.
	 * @return Liste de maps contenant les performances calculées pour les
	 *         requêtes. Chaque map correspond à une requête de la vérité
	 *         terrain, sauf la dernière, qui contient les valeurs moyennes.
	 */
	protected List<Map<MeasureName, Float>> evaluateQueryAnswers(
			List<List<Posting>> answers) {
		List<Map<MeasureName, Float>> result = new ArrayList<Map<MeasureName, Float>>();
		float totalPre = 0;
		float totalRec = 0;
		float totalFM = 0;

		// on traite chaque requête de la vérité terrain
		int queryId = 0;
		for (List<Posting> answer : answers) {
			Map<MeasureName, Float> map = evaluateQueryAnswer(queryId, answer);
			totalPre = totalPre + map.get(MeasureName.PRECISION);
			totalRec = totalRec + map.get(MeasureName.RECALL);
			totalFM = totalFM + map.get(MeasureName.F_MEASURE);
			result.add(map);
			queryId++;
		}

		// on calcule les valeurs moyennes
		Map<MeasureName, Float> map = new HashMap<MeasureName, Float>();
		map.put(MeasureName.PRECISION, totalPre / answers.size());
		map.put(MeasureName.RECALL, totalRec / answers.size());
		map.put(MeasureName.F_MEASURE, totalFM / answers.size());
		result.add(map);

		return result;
	}

	// //////////////////////////////////////////////////
	// EXPORTATION
	// //////////////////////////////////////////////////
	/**
	 * Ecrit dans le fichier texte spécifié dans la configuration les valeurs
	 * passées en paramètre. Chaque type de mesure prend la forme d'une colonne
	 * dans le fichier.
	 * 
	 * @param values
	 *            Listes de maps de valeurs à traiter.
	 * @throws FileNotFoundException
	 *             Problème à l'ouverture du fichier de performances.
	 * @throws UnsupportedEncodingException
	 *             Problème à l'ouverture du fichier de performances.
	 */
	protected void writePerformances(List<Map<MeasureName, Float>> values)
			throws FileNotFoundException, UnsupportedEncodingException {
		// on ouvre le fichier en écriture
		String fileName = FileTools.getPerformanceFile();
		File file = new File(fileName);
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		PrintWriter writer = new PrintWriter(osw);
		
		// on traite chaque map l'une après l'autre
		for (Map<MeasureName, Float> map : values) {
			boolean first = true;
			for (MeasureName measureName : MeasureName.values()) {
				float val = map.get(measureName);
				if (first)
					first = false;
				else
					writer.print("\t");
				writer.print(val);
			}
			writer.println();
		}
		
		// on ferme le flux
		writer.close();
	}
}
