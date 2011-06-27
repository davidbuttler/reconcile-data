/**
 *
 */
package reconcile.features;

import java.util.Map;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.list.PointerTargetNode;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.data.relationship.RelationshipFinder;
import net.didion.jwnl.data.relationship.RelationshipList;
import net.didion.jwnl.dictionary.Dictionary;

import com.google.common.collect.Maps;

import gov.llnl.text.util.FileUtils;
import gov.llnl.text.util.StringInputStream;
import gov.llnl.text.util.StringUtil;

import reconcile.data.Annotation;
import reconcile.data.Document;
import reconcile.featureVector.NumericFeature;
import reconcile.features.properties.NPSemanticType;
import reconcile.features.properties.Synsets;
import reconcile.features.properties.WNSemClass;
import reconcile.general.Utils;

/**
 * @author ves
 *
 *         General utility methods that are used for generation of features
 */
public class WordNetUtils {

public enum NPSemTypeEnum {
  PERSON, ORGANIZATION, DATE, TIME, MONEY, PERCENTAGE, LOCATION, NUMBER, GPE, VEHICLE, FAC, UNKNOWN
}

private static final String propsFile = "/file_properties.xml"; // Utils.getDataDirectory() + Utils.SEPARATOR +
private static Dictionary wordnet_a;
private static Dictionary wordnet;

public WordNetUtils() {
  initializeWordNet_a();
  // // Startup Wordnet
  // try {
  // JWNL.initialize(new FileInputStream(propsFile));
  // wordnet = Dictionary.getInstance();
  // } catch (Exception ex) {
  // throw new RuntimeException(ex);
  // }
}


public static synchronized Dictionary initializeWordNet0_a()
{

  if (wordnet_a != null) return wordnet_a;

  try {
    String propsFileText = FileUtils.readFile(propsFile);
    Map<String, String> map = Maps.newTreeMap();
    if (Utils.isConfigured()) {
      map.put("WordNet_dictionary_path", Utils.getConfig().getString("WordNet_dictionary_path"));
      propsFileText = StringUtil.macroReplace(propsFileText, map);
    }
    JWNL.initialize(new StringInputStream(propsFileText));
    // JWNL.initialize(new FileInputStream(propsFile));
    wordnet_a = net.didion.jwnl.dictionary.Dictionary.getInstance();
  }
  catch (Exception ex) {
    throw new RuntimeException(ex);
  }

  SUPERTYPE_SYNSETS = new Synset[SUPERTYPES.length];
  Synset[] classSynset;
  IndexWord iw;
  int count = 0;
  for (String type : SUPERTYPES) {
    try {
      iw = wordnet_a.getIndexWord(POS.NOUN, type);
    }
    catch (JWNLException e) {
      throw new RuntimeException(e);
    }
    if (iw == null) {
      System.err.println(type);
      continue;
    }

    try {
      classSynset = iw.getSenses();
    }
    catch (JWNLException e) {
      throw new RuntimeException(e);
    }
    // System.err.println("**********************");
    if (classSynset.length > 1) {
      // for(Synset cs:classSynset)
      // System.err.println(cs);
      if (type.equals("abstraction")) {
        SUPERTYPE_SYNSETS[count] = classSynset[5];
      }
      else if (type.equals("measure")) {
        SUPERTYPE_SYNSETS[count] = classSynset[2];
      }
      else if (type.equals("state")) {
        SUPERTYPE_SYNSETS[count] = classSynset[3];
      }
      else if (type.equals("act")) {
        SUPERTYPE_SYNSETS[count] = classSynset[1];
      }
      else {
        SUPERTYPE_SYNSETS[count] = classSynset[0];
      }
    }
    count++;
  }
  if (wordnet_a == null)
    throw new RuntimeException("WordNet not intialized");
  else {
    System.out.println("Wordnet initialized " + wordnet_a);
  }
  return wordnet_a;

}


/*
 * Used for WordNetClass feature.
 */
public static final String[] SUPERTYPES = { "person", "location", "organization", "time", "time_period", "date", "day",
    "money", "measure", "relation", "act", "phenomenon", "psychological_feature", "event", "group", "artifact",
    "commodity", "property", "sum", "cognitive_state",
    // "abstraction", "object",
    // "vehicle", "facility",
    "male", "female", "transferred_property", "quantity", "statistic" };

public static final String[] COMP_SUPERTYPES = { "person", "location", "organization", "time", "time_period", "group",
    "date", "day", "money", "sum", "cognitive_state", "measure", "transferred_property", "property", "quantity",
    "statistic" };

private static Synset[] SUPERTYPE_SYNSETS;


public static synchronized Dictionary initializeWordNet_a()
{

  if (wordnet_a != null) return wordnet_a;

  try {
    String propsFileText = FileUtils.readFile(Utils.class.getResourceAsStream(propsFile));
    Map<String, String> map = Maps.newTreeMap();
    map.put("WordNet_dictionary_path", Utils.getConfig().getString("WordNet_dictionary_path"));
    propsFileText = StringUtil.macroReplace(propsFileText, map);
    JWNL.initialize(new StringInputStream(propsFileText));
    // JWNL.initialize(new FileInputStream(propsFile));
    wordnet_a = Dictionary.getInstance();
  }
  catch (Exception ex) {
    throw new RuntimeException(ex);
  }

  SUPERTYPE_SYNSETS = new Synset[SUPERTYPES.length];
  Synset[] classSynset;
  IndexWord iw;
  int count = 0;
  for (String type : SUPERTYPES) {
    try {
      iw = wordnet_a.getIndexWord(POS.NOUN, type);
    }
    catch (JWNLException e) {
      throw new RuntimeException(e);
    }
    if (iw == null) {
      System.err.println(type);
      continue;
    }

    try {
      classSynset = iw.getSenses();
    }
    catch (JWNLException e) {
      throw new RuntimeException(e);
    }
    // System.err.println("**********************");
    if (classSynset.length > 1) {
      // for(Synset cs:classSynset)
      // System.err.println(cs);
      if (type.equals("abstraction")) {
        SUPERTYPE_SYNSETS[count] = classSynset[5];
      }
      else if (type.equals("measure")) {
        SUPERTYPE_SYNSETS[count] = classSynset[2];
      }
      else if (type.equals("state")) {
        SUPERTYPE_SYNSETS[count] = classSynset[3];
      }
      else if (type.equals("act")) {
        SUPERTYPE_SYNSETS[count] = classSynset[1];
      }
      else {
        SUPERTYPE_SYNSETS[count] = classSynset[0];
      }
    }
    count++;
  }
  if (wordnet_a == null)
    throw new RuntimeException("WordNet not intialized");
  else {
    System.out.println("Wordnet initialized " + wordnet_a);
  }
  return wordnet_a;

}


/*
 * Returns the WordNet graph traversal distance of a relationship between two
 * words.
 */
public static int wnDist(Annotation np1, Annotation np2, Document doc)
    throws JWNLException
{
  // if(!getNPSemType(np1, annotations, text).equals(NPSemTypeEnum.UNKNOWN)&&getNPSemType(np1, annotations,
  // text).equals(getNPSemType(np2, annotations, text)))
  // return 0;
  Synset[] synset1 = Synsets.getValue(np1, doc);
  Synset[] synset2 = Synsets.getValue(np2, doc);

  if (synset1 == null || synset2 == null || synset1.length == 0 || synset2.length == 0) return NumericFeature.WN_MAX;

  return WordNetUtils.getDistance(synset1[0], synset2[0], NumericFeature.WN_MAX);
}


public static int getWNSense(Annotation np1, Annotation np2, Document doc)
    throws JWNLException
{
  Synset[] synset1 = Synsets.getValue(np1, doc);
  Synset[] synset2 = Synsets.getValue(np2, doc);
  if (synset1 == null || synset2 == null || synset1.length == 0 || synset2.length == 0)
    return NumericFeature.WN_SENSE_MAX;
  return getWNSense(synset1, synset2);
}


public static int getWNSense(Synset[] synset1, Synset[] synset2)
{
  for (Synset element : synset1) {
    for (int j = 0; j < synset2.length && j < NumericFeature.WN_SENSE_MAX; j++) {
      try {
        // get the highest hypernym
        PointerTargetNodeList dp;
        Synset cur = element;
        while ((dp = PointerUtils.getInstance().getDirectHypernyms(cur)) != null && !dp.isEmpty()) {
          Object pt = dp.get(0);
          PointerTargetNode ptn = (PointerTargetNode) pt;
          cur = ptn.getSynset();
        }
        // System.err.println("top level parent: "+cur.getWord(0).getLemma()+" "+isWNHypernym(synset2[j],
        // cur)+" "+cur.equals(synset2[j]));
        if (WordNetUtils.isWNHypernym(synset2[j], cur) >= 0) // System.out.println(synset2[j]+" *** "+cur+" *** "+synset1[i]);
          return (j + 1);
      }
      catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  return NumericFeature.WN_SENSE_MAX;
}


public static int isWNHypernym(Synset child, String parent)
{
  WordNetUtils.initializeWordNet();
  PointerTargetNodeList dp;
  if (child.getWord(0).getLemma().equalsIgnoreCase(parent)) return 0;
  Synset cur = child;
  int curDepth = 0;
  try {
    while ((dp = PointerUtils.getInstance().getDirectHypernyms(cur)) != null && !dp.isEmpty()) {
      curDepth++;
      // System.out.println("*****");
      Object pt = dp.get(0);
      PointerTargetNode ptn = (PointerTargetNode) pt;
      // System.out.println(ptn.getSynset().getWords()[0].getLemma());
      cur = ptn.getSynset();
      // System.out.println(cur.getWord(0).getLemma());
      if (cur.getWord(0).getLemma().equalsIgnoreCase(parent)) return curDepth;

      // if (dp.size() > 1)
      // throw new RuntimeException("More than 1 hypernym");

    }
  }
  catch (Exception e) {
    throw new RuntimeException(e);
  }
  return -1;
}


public static int isWNHypernym(Synset child, Synset parent)
{
  WordNetUtils.initializeWordNet();
  if (child.equals(parent)) return 0;
  PointerTargetNodeList dp;
  Synset cur = child;
  int curDepth = 0;
  try {
    while ((dp = PointerUtils.getInstance().getDirectHypernyms(cur)) != null && !dp.isEmpty()) {
      curDepth++;
      // System.out.println("*****");
      Object pt = dp.get(0);
      PointerTargetNode ptn = (PointerTargetNode) pt;
      // System.out.println(ptn.getSynset().getWords()[0].getLemma());
      cur = ptn.getSynset();
      if (cur.equals(parent)) return curDepth;

      // if (dp.size() > 1)
      // throw new RuntimeException("More than 1 hypernym");

    }
  }
  catch (Exception e) {
    throw new RuntimeException(e);
  }
  return -1;
}


public static int isWNHypernym(Synset child, Synset parent, int depth)
{
  WordNetUtils.initializeWordNet();
  PointerTargetNodeList dp;
  if (child.equals(parent)) return 0;
  Synset cur = child;
  int curDepth = 0;
  try {
    while ((dp = PointerUtils.getInstance().getDirectHypernyms(cur)) != null && !dp.isEmpty() && curDepth < depth) {
      curDepth++;
      // if (dp.size() > 1)
      // throw new RuntimeException("More than 1 hypernym");

      Object pt = dp.get(0);
      PointerTargetNode ptn = (PointerTargetNode) pt;
      cur = ptn.getSynset();
      if (cur.equals(parent)) return curDepth;
    }

  }
  catch (Exception e) {
    throw new RuntimeException(e);
  }
  return -1;
}


public static int getDistance(Synset s1, Synset s2, int max)
{
  WordNetUtils.initializeWordNet();
  PointerTargetNodeList dp;
  if (s1.equals(s2)) return 0;

  Synset cur = s2;
  int depth = 0, depth2 = isWNHypernym(s1, cur, max);
  if (depth2 >= 0) return depth2;
  try {
    while ((dp = PointerUtils.getInstance().getDirectHypernyms(cur)) != null && !dp.isEmpty() && depth < max) {
      depth++;
      Object pt = dp.get(0);
      PointerTargetNode ptn = (PointerTargetNode) pt;

      cur = ptn.getSynset();
      // System.out.println(cur);
      if ((depth2 = isWNHypernym(s1, cur, max)) >= 0) return depth + depth2 < max ? depth + depth2 : max;
    }

  }
  catch (Exception e) {
    throw new RuntimeException(e);
  }
  return max;
}


public static synchronized Dictionary initializeWordNet0()
{

  if (wordnet != null) return wordnet;

  try {
    String propsFileText = FileUtils.readFile(propsFile);
    Map<String, String> map = Maps.newTreeMap();
    if (Utils.isConfigured()) {
      map.put("WordNet_dictionary_path", Utils.getConfig().getString("WordNet_dictionary_path"));
      propsFileText = StringUtil.macroReplace(propsFileText, map);
    }
    JWNL.initialize(new StringInputStream(propsFileText));
    // JWNL.initialize(new FileInputStream(propsFile));
    wordnet = Dictionary.getInstance();
  }
  catch (Exception ex) {
    throw new RuntimeException(ex);
  }

  SUPERTYPE_SYNSETS = new Synset[SUPERTYPES.length];
  Synset[] classSynset;
  IndexWord iw;
  int count = 0;
  for (String type : SUPERTYPES) {
    try {
      iw = wordnet.getIndexWord(POS.NOUN, type);
    }
    catch (JWNLException e) {
      throw new RuntimeException(e);
    }
    if (iw == null) {
      System.err.println(type);
      continue;
    }

    try {
      classSynset = iw.getSenses();
    }
    catch (JWNLException e) {
      throw new RuntimeException(e);
    }
    // System.err.println("**********************");
    if (classSynset.length > 1) {
      // for(Synset cs:classSynset)
      // System.err.println(cs);
      if (type.equals("abstraction")) {
        SUPERTYPE_SYNSETS[count] = classSynset[5];
      }
      else if (type.equals("measure")) {
        SUPERTYPE_SYNSETS[count] = classSynset[2];
      }
      else if (type.equals("state")) {
        SUPERTYPE_SYNSETS[count] = classSynset[3];
      }
      else if (type.equals("act")) {
        SUPERTYPE_SYNSETS[count] = classSynset[1];
      }
      else {
        SUPERTYPE_SYNSETS[count] = classSynset[0];
      }
    }
    count++;
  }
  if (wordnet == null)
    throw new RuntimeException("WordNet not intialized");
  else {
    System.out.println("Wordnet initialized " + wordnet);
  }
  return wordnet;

}


public static synchronized Dictionary initializeWordNet()
{

  if (wordnet != null) return wordnet;

  try {
    String propsFileText = FileUtils.readFile(Utils.class.getResourceAsStream(propsFile));
    Map<String, String> map = Maps.newTreeMap();
    map.put("WordNet_dictionary_path", Utils.getConfig().getString("WordNet_dictionary_path"));
    propsFileText = StringUtil.macroReplace(propsFileText, map);
    JWNL.initialize(new StringInputStream(propsFileText));
    // JWNL.initialize(new FileInputStream(propsFile));
    wordnet = Dictionary.getInstance();
  }
  catch (Exception ex) {
    throw new RuntimeException(ex);
  }

  SUPERTYPE_SYNSETS = new Synset[SUPERTYPES.length];
  Synset[] classSynset;
  IndexWord iw;
  int count = 0;
  for (String type : SUPERTYPES) {
    try {
      iw = wordnet.getIndexWord(POS.NOUN, type);
    }
    catch (JWNLException e) {
      throw new RuntimeException(e);
    }
    if (iw == null) {
      System.err.println(type);
      continue;
    }

    try {
      classSynset = iw.getSenses();
    }
    catch (JWNLException e) {
      throw new RuntimeException(e);
    }
    // System.err.println("**********************");
    if (classSynset.length > 1) {
      // for(Synset cs:classSynset)
      // System.err.println(cs);
      if (type.equals("abstraction")) {
        SUPERTYPE_SYNSETS[count] = classSynset[5];
      }
      else if (type.equals("measure")) {
        SUPERTYPE_SYNSETS[count] = classSynset[2];
      }
      else if (type.equals("state")) {
        SUPERTYPE_SYNSETS[count] = classSynset[3];
      }
      else if (type.equals("act")) {
        SUPERTYPE_SYNSETS[count] = classSynset[1];
      }
      else {
        SUPERTYPE_SYNSETS[count] = classSynset[0];
      }
    }
    count++;
  }
  if (wordnet == null)
    throw new RuntimeException("WordNet not intialized");
  else {
    System.out.println("Wordnet initialized " + wordnet);
  }
  return wordnet;

}


public static boolean ancestorWN(Annotation np1, Annotation np2, Document doc)
    throws JWNLException
{
  /*
  // Startup Wordnet
  if (wordnet == null) {
  	try {
  		JWNL.initialize(new FileInputStream(propsFile));
  		wordnet = Dictionary.getInstance();
  	} catch (Exception ex) {
  		throw new RuntimeException(ex);
  	}
  }
  IndexWord w1;
  IndexWord w2;

  // This searches the wordnet database w/o any morph processing done
  // to the word.
  w1 = wordnet.getIndexWord(POS.NOUN, word1);
  w2 = wordnet.getIndexWord(POS.NOUN, word2);

  // To have WN do morph processing...
  // IndexWord w1 = wordnet.lookupIndexWord(POS.NOUN, word1);
  // IndexWord w2 = wordnet.lookupIndexWord(POS.NOUN, word2);
   */
  Synset[] synset1 = Synsets.getValue(np1, doc);
  Synset[] synset2 = Synsets.getValue(np2, doc);

  if (synset1 == null || synset2 == null || synset1.length == 0 || synset2.length == 0) return false;

  // Check for relationships amongst all senses.
  for (Synset element : synset1) {
    for (Synset element2 : synset2) {
      RelationshipList hypo = RelationshipFinder.getInstance()
          .findRelationships(element, element2, PointerType.HYPONYM);
      if (!hypo.isEmpty()) return true;

    }
  }

  return false;
}


/*
 * WordNet features.
 */
/*
 * Returns 0 - If NA 1 - If Incompatible 2 - If compatible
 */
public static int sameSemanticClass(Annotation np1, Annotation np2, Document doc)
{

  NPSemTypeEnum type1 = NPSemanticType.getValue(np1, doc);
  NPSemTypeEnum type2 = NPSemanticType.getValue(np2, doc);

  if (type1.equals(type2) && !type1.equals(NPSemTypeEnum.UNKNOWN)) return 2;

  String[] wn_senses1 = WNSemClass.getValue(np1, doc);
  String[] wn_senses2 = WNSemClass.getValue(np2, doc);

  if (wn_senses1.length < 1 || wn_senses2.length < 1) return 0;

  for (String s : FeatureUtils.COMP_SUPERTYPES) {
    if (FeatureUtils.memberArray(s, wn_senses1) && FeatureUtils.memberArray(s, wn_senses2)) return 2;
  }

  if (FeatureUtils.overlaps(wn_senses1, wn_senses2)) {
    if (FeatureUtils.isPronoun(np1, doc) || FeatureUtils.isPronoun(np2, doc)) return 2;
    String[] w1 = doc.getWords(np1);
    String[] w2 = doc.getWords(np2);
    if (FeatureUtils.intersection(w1, w2) > 0) return 2;
  }

  return 1;
}


public static boolean isSubclass(Annotation np1, Annotation np2, Document doc)
{
  Synset[] synset1 = Synsets.getValue(np1, doc);
  Synset[] synset2 = Synsets.getValue(np2, doc);
  if (synset1 == null || synset2 == null || synset1.length == 0 || synset2.length == 0) return false;
  return isSubclass(synset1, synset2);
}


public static boolean isSubclass(Synset[] synset1, Synset[] synset2)
{
  for (int i = 0; i < synset1.length && i < NumericFeature.WN_MAX; i++) {
    for (Synset element : synset2) {

      try {
        // is the one synset hypernym of the other?
        if (isWNHypernym(synset1[i], element) > 0 || isWNHypernym(element, synset1[i]) > 0) return true;
      }
      catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  return false;
}

}

