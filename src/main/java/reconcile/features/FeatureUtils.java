/**
 *
 */
package reconcile.features;


import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import reconcile.data.Annotation;
import reconcile.data.AnnotationSet;
import reconcile.data.Document;
import reconcile.features.properties.ContainsProperName;
import reconcile.features.properties.ParNum;
import reconcile.features.properties.Pronoun;
import reconcile.features.properties.ProperName;
import reconcile.features.properties.SentNum;
import reconcile.general.Constants;
import reconcile.general.CustomDate;
import reconcile.general.SyntaxUtils;
import reconcile.general.Utils;


/**
 * @author ves
 *
 *         General utility methods that are used for generation of features
 */
public class FeatureUtils {

public FeatureUtils() {

}

public static boolean isPronoun(String s)
{
  String[] words = getWords(s);
  if (words.length != 1) return false;
  s = words[0];
  return memberArray(s, PRONOUN_LIST) && (s.length() == 1 || !allUpercase(s)) && (s.length() > 1 || allUpercase(s));
}

public static boolean isPronoun(String[] words)
{

  if (words == null || words.length != 1) return false;

  return isPronoun(words[0]);
}

public static boolean isPronoun(Annotation np, Document doc)
{
  return !Pronoun.getValue(np, doc).equals(PRTypeEnum.NONE);
}

public static PRTypeEnum getPronounType(String s)
{
  if (memberArray(s, S_PRONOUNS) || s.equals("I")) return PRTypeEnum.SUBJECT;
  if (memberArray(s, O_PRONOUNS)) return PRTypeEnum.OBJECT;
  if (memberArray(s, POSSESIVE_LIST)) return PRTypeEnum.POSSESSIVE;
  if (memberArray(s, REFLEXIVE_LIST)) return PRTypeEnum.SUBJECT;
  if (memberArray(s, SO_PRONOUNS)) return PRTypeEnum.UNKNOWN;
  return PRTypeEnum.NONE;
}

public static PersonPronounTypeEnum getPronounPerson(String s)
{
  if (memberArray(s, FIRST_PERSON_PRONOUNS)) return PersonPronounTypeEnum.FIRST;
  if (memberArray(s, SECOND_PERSON_PRONOUNS)) return PersonPronounTypeEnum.SECOND;
  if (memberArray(s, THIRD_PERSON_PRONOUNS)) return PersonPronounTypeEnum.THIRD;
  return PersonPronounTypeEnum.NONE;
}
//public static int getPronounPerson(String s)
//{
//  if (memberArray(s, FIRST_PERSON_PRONOUNS)) return 1;
//  if (memberArray(s, SECOND_PERSON_PRONOUNS)) return 2;
//  if (memberArray(s, THIRD_PERSON_PRONOUNS)) return 3;
//  return -1;
//}
public static CountPronounTypeEnum getPronounCount(String s)
{
  if (memberArray(s, SINGULAR_PRONOUNS)) return CountPronounTypeEnum.SINGULAR;
  if (memberArray(s, PLURAL_PRONOUNS)) return CountPronounTypeEnum.PLURAL;
  if (memberArray(s, SINGULAR_OR_PLURAL_PRONOUNS)) return CountPronounTypeEnum.EITHER;
  return CountPronounTypeEnum.NONE;
}

public static Date getDate(String s)
{
  Date result = null;
  try {
    result = new SimpleDateFormat("MM/dd/yy").parse(s);
  }
  catch (java.text.ParseException pe) {
    result = null;
  }
  return result;
}

public static boolean isDate(String s)
{
  if (CustomDate.getDate(s) != null) return true;
  return memberArray(s, DATES);
}

public static boolean isAllCaps(String s)
{
  return s.matches("[A-Z\\.\\&]*");
}

public static boolean isCapitalized(String s)
{
  return s.matches("[A-Z].*");
}

public static boolean sameSentence(Annotation np1, Annotation np2, Document doc)
{
  return SentNum.getValue(np1, doc).equals(SentNum.getValue(np2, doc));
}

public static boolean sameParagraph(Annotation np1, Annotation np2, Document doc)
{
  return ParNum.getValue(np1, doc).equals(ParNum.getValue(np2, doc));
}

public static boolean isCorpDesign(String s)
{
  return getCorpDesign().contains(s.toLowerCase());
}

public static Set<String> stringArray2TreeSet(String[] in)
{
  Set<String> result = new HashSet<String>();
  if (in != null) {
    for (String s : in) {
      result.add(s.toLowerCase());
    }
  }
  return result;
}

private static Set<String> getCorpDesign()
{
  if (CORP_DESIGN_SET == null) {
    CORP_DESIGN_SET = stringArray2TreeSet(CORP_DESIGN);
  }
  return CORP_DESIGN_SET;
}

private static Set<String> getDeterminers()
{
  if (DETERMINERS_SET == null) {
    DETERMINERS_SET = stringArray2TreeSet(DETERMINERS);
  }
  return DETERMINERS_SET;
}

private static Set<String> getReportingVerbs()
{
  if (REPORTING_VERBS_SET == null) {
    REPORTING_VERBS_SET = stringArray2TreeSet(REPORTING_VERBS);
  }
  return REPORTING_VERBS_SET;
}

public static Set<String> getTitles()
{
  if (TITLES_SET == null) {
    TITLES_SET = stringArray2TreeSet(TITLES);
  }
  return TITLES_SET;
}

private static Set<String> getUninfWords()
{
  if (UNINF_WORDS == null) {
    UNINF_WORDS = stringArray2TreeSet(UNINF_WORDS_AR);
  }
  return UNINF_WORDS;
}

public static boolean isStopword(String s)
{
  return getStopwords().contains(s.toLowerCase());
}

public static boolean isMaleName(String s)
{
  return getMaleNames().contains(s.toLowerCase());
}

public static boolean isFemaleName(String s)
{
  return getFemaleNames().contains(s.toLowerCase());
}

public static boolean isStopword(String[] words)
{
  if (words == null || words.length != 1) return false;

  return isStopword(words[0]);
}

public static boolean isNumeral(String np)
{
  return np.matches("(\\d|\\.)*");
}

public static boolean isPossesive(String s)
{
  return memberArray(s, POSSESIVE_LIST);
}

public static boolean isReflexive(String s)
{
  return memberArray(s, REFLEXIVE_LIST);
}

public static boolean isUninfWord(String s)
{
  return getUninfWords().contains(s.toLowerCase());
}

public static boolean isCardinalNumber(Annotation pos)
{
  final String[] CN_POS = { "CC" };
  return memberArray(pos.getType(), CN_POS);
}

public static boolean isProperNoun(Annotation pos)
{
  return pos.getType().startsWith("NNP");
}

public static boolean isPredeterminer(Annotation pos)
{
  return pos.getType().equals("PDT");
}

public static boolean isPossesivePronoun(Annotation pos)
{
  return pos.getType().equals("PRP$");
}

public static boolean isAdjective(Annotation pos)
{
  final String[] ADJ_POS = { "JJ", "JJR", "JJS" };
  return memberArray(pos.getType(), ADJ_POS);
}

public static boolean isReportingVerb(String s)
{
  return getReportingVerbs().contains(s);
}

public static boolean isAdverb(Annotation pos)
{
  final String[] ADV_POS = { "RB", "RBR", "RBS" };
  return memberArray(pos.getType(), ADV_POS);
}

public static boolean isDemonstrative(String s)
{
  String[] w = getWords(s);
  if (w == null || w.length <= 0) return false;
  return memberArray(w[0], DEMONSTRATIVES);
}

public static boolean isAlphabetStr(String w)
{
  if (w == null || w.length() != 1) return false;

  return w.matches("[a-zA-Z]");
}

public static boolean containsProperName(Annotation np, Document doc)
{
  Annotation cont = ContainsProperName.getValue(np, doc);
  if (cont.getId() < 0)
    return false;
  else
    return true;
}

public static Annotation getContainedProperName(Annotation np, Document doc)
{
  Annotation cont = ContainsProperName.getValue(np, doc);
  if (cont.getId() < 0)
    return null;
  else
    return cont;
}

public static ArticleTypeEnum articleType(Annotation np, Document doc)
{
  String[] indefs = { "a", "an", "one" };
  // String[] defs = { "the", "this", "that", "these", "those" };
  String[] quans = { "every", "all", "some", "most", "few", "many", "much" };
  String[] words = doc.getWords(np);
  String first = words[0];
  if (ProperName.getValue(np, doc)) return ArticleTypeEnum.DEFINITE;
  if (isPronoun(np, doc)) return ArticleTypeEnum.DEFINITE;
  if (memberArray(first, indefs)) return ArticleTypeEnum.INDEFINITE;
  if (memberArray(first, quans)) {
    if (words.length < 2 || !words[1].equalsIgnoreCase("of"))
      return ArticleTypeEnum.QUANTIFIED;
    else
      return ArticleTypeEnum.DEFINITE;
  }
  AnnotationSet pos = doc.getAnnotationSet(Constants.POS);
  pos = pos.getContained(np);
  if (pos != null && pos.size() > 0) {
    if (isCardinalNumber(pos.getFirst()) || isPredeterminer(pos.getFirst())) return ArticleTypeEnum.QUANTIFIED;
    if (isPossesivePronoun(pos.getFirst()) || isProperNoun(pos.getLast())) return ArticleTypeEnum.DEFINITE;
  }
  return ArticleTypeEnum.INDEFINITE;
}

public static boolean isIndefinite(Annotation np, Document doc)
{

  String[] indefs = { "a", "an", "one" };
  String[] words = doc.getWords(np);
  String first = words[0];
  return memberArray(first, indefs);
  /*
  	if(memberArray(first, defs))
  		return false;
  	if(isPronoun(first))
  		return false;
  	if(containsProperName(np, annotations, text))
  		return false;
  	return true;*/
}

public static boolean memberArray(String s, String[] a)
{

  if (s == null || s.length() == 0) return false;

  for (String mem : a) {
    if (s.equalsIgnoreCase(mem)) return true;
  }

  return false;
}

public static boolean memberArray(String s, String[] a, int start)
{

  if (s == null || s.length() == 0) return false;

  for (int i = start; i < a.length; i++) {
    if (s.equalsIgnoreCase(a[i])) return true;
  }

  return false;
}

/*
 * Do the two string arrays have any element(string) in common?
 */
public static boolean overlaps(String[] a1, String[] a2)
{
  for (String s1 : a1) {
    for (String s2 : a2) {
      if (s1.equalsIgnoreCase(s2)) return true;
    }
  }

  return false;
}

public static int intersection(String[] a1, String[] a2)
{
  Set<String> s1 = new HashSet<String>();
  int count = 0;
  for (String s : a1) {
    s1.add(s.toLowerCase());
  }
  for (String s : a2)
    if (s1.contains(s.toLowerCase())) {
      count++;
    }
  return count;
}

public static boolean allUpercase(String s)
{
  if (s == null || s.length() == 0) return true;

  return s.toUpperCase().equals(s);
}

public static String[] removeWords(String[] words, Set<String> wordlist)
{
  ArrayList<String> result = new ArrayList<String>();

  for (String w : words) {

    if (!wordlist.contains(w)) {
      result.add(w);
    }
  }

  return result.toArray(new String[0]);
}

public static String[] removeWords(String[] words, String[] wordlist)
{
  ArrayList<String> result = new ArrayList<String>();

  for (String w : words) {

    if (!memberArray(w, wordlist)) {
      result.add(w);
    }
  }

  return result.toArray(new String[0]);
}

public static String[] removeUninfWords(String[] words)
{
  ArrayList<String> result = new ArrayList<String>();

  for (String w : words) {

    if (!getUninfWords().contains(w.toLowerCase()) && !(isAlphabetStr(w) || "I".endsWith(w)) && !isCorpDesign(w)) {
      result.add(w);
    }
  }

  return result.toArray(new String[0]);
}

public static String[] removeUninfWordsLeaveCorpDesign(String[] words)
{
  ArrayList<String> result = new ArrayList<String>();

  for (String w : words) {

    if (!getUninfWords().contains(w.toLowerCase()) && !(isAlphabetStr(w) || "I".endsWith(w)) && !isCorpDesign(w)) {
      result.add(w);
    }
  }

  return result.toArray(new String[0]);
}

public static String[] removeDeterminers(String[] words)
{
  return removeWords(words, getDeterminers());
}

public static String[] removeSoonUninf(String[] words)
{
  String[] w = removeDeterminers(words);
  ArrayList<String> result = new ArrayList<String>();

  for (String s : w) {
    if (!isAlphabetStr(s) || !s.equalsIgnoreCase("I")) {
      result.add(s);
    }
  }

  return result.toArray(new String[0]);
}

public static boolean equalsIgnoreCase(String[] w1, String[] w2)
{

  if (w1 == null || w2 == null || w1.length == 0) return false;

  if (w1.length != w2.length) return false;

  for (int i = 0; i < w1.length; i++) {
    if (!w1[i].equalsIgnoreCase(w2[i])) return false;
  }

  return true;
}

public static String[] getWords(String textSpan)
{
  // remove leading non-word characters
  textSpan = textSpan.replaceAll("\\A\\W*", "");
  return textSpan.split("\\W+|\\-");
}

public static int MED(String s1, String s2)
{

  s1 = s1.toLowerCase();
  s2 = s2.toLowerCase();
  int m = s1.length(), n = s2.length();

  // d is a table with m+1 rows and n+1 columns
  int[][] d = new int[m + 1][n + 1];

  for (int i = 0; i <= m; i++) {
    d[i][0] = i;
  }

  for (int j = 1; j <= n; j++) {
    d[0][j] = j;
  }

  int cost;

  for (int i = 1; i <= m; i++) {
    for (int j = 1; j <= n; j++) {

      cost = 1;

      if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
        cost = 0;
      }

      d[i][j] = min(d[i - 1][j] + 1, // deletion
          d[i][j - 1] + 1, // insertion
          d[i - 1][j - 1] + cost); // substitution
    }
  }

  return d[m][n];
}

public static double medMeasure(String s1, String s2)
{

  int m = s1.length();
  double MED = MED(s1, s2);
  double result = 0;

  if (m != 0) {
    result = (m - MED) / m;
  }

  // System.err.println("Len is "+m+" med is "+MED+" result "+result);
  // int decimalPlace = 5;
  // BigDecimal bd = new BigDecimal(result);
  // bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
  // result = bd.doubleValue();
  return result;
}

public static Annotation getHeadVerb(Annotation verb, Map<String, AnnotationSet> annotations, String text)
{
  AnnotationSet verbs = annotations.get(Constants.POS).getContained(verb);
  if (verbs == null || verbs.size() < 1) return verb;
  Annotation result = verbs.getFirst();
  for (Annotation v : verbs) {
    if (!SyntaxUtils.isPreposition(v.getType())) {
      result = v;
    }
  }
  return result;
}

private static int min(int i1, int i2, int i3)
{
  if ((i1 < i2) && (i1 < i3)) return i1;
  return i2 < i3 ? i2 : i3;
}

private static Set<String> getStopwords()
{
  if (STOPWORDS == null) {
    InputStream in = Utils.getStopwords();
    try {
      // System.out.println("Reading in stopwords");
      STOPWORDS = Utils.readStringsSet(in);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  return STOPWORDS;
}

private static Set<String> getFemaleNames()
{
  if (FEMALE_NAMES == null) {
    InputStream in = Utils.getFemaleNames();
    try {
      FEMALE_NAMES = Utils.readStringsSet(in);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  return FEMALE_NAMES;
}

private static Set<String> getMaleNames()
{
  if (MALE_NAMES == null) {
    InputStream in = Utils.getMaleNames();
    try {
      MALE_NAMES = Utils.readStringsSet(in);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  return MALE_NAMES;
}

private static final String[] PRONOUN_LIST = { "I", "me", "my", "mine", "you", "your", "yours", "he", "him", "his",
    "she", "her", "hers", "we", "us", "our", "ours", "it", "its", "they", "them", "their", "theirs", "myself",
    "yourself", "yourselves", "himself", "herself", "ourselves", "themselves", "itself" };// , "that", "these", "this",
                                                                                          // "those"};

private static final String[] FIRST_PERSON_PRONOUNS = { "I", "me", "my", "mine", "we", "us", "our", "ours", "myself",
    "ourselves" };

private static final String[] SECOND_PERSON_PRONOUNS = { "you", "your", "yours", "yourself", "yourselves" };

public static final String[] THIRD_PERSON_PRONOUNS = { "he", "him", "his", "she", "her", "hers", "it", "its", "they",
    "them", "their", "theirs", "himself", "herself", "themselves", "itself" };// , "that", "these", "this", "those" };

public static final String[] PLURAL_PRONOUNS = { "we", "us", "our", "they", "them", "their", "theirs", "ours",
    "ourselves", "themselves" };// , "these", "those" };

public static final String[] SINGULAR_PRONOUNS = { "I", "me", "my", "mine", "myself", "yourself", "he", "him", "his",
	  "she", "her", "hers", "it", "its", "himself", "herself", "itself",  };

	public static final String[] SINGULAR_OR_PLURAL_PRONOUNS = { "you", "your", "yours" };


public static final String[] THIRD_PERSON_SINGULAR_PRONOUNS = { "he", "him", "his", "she", "her", "hers", "it", "its",
  "himself", "herself", "itself" };

public static final String[] THIRD_PERSON_PLURAL_PRONOUNS = { "they", "them", "their", "theirs", "themselves",  };



public static final String[] UNKNWNS = { "you", "your", "yours", "yourselves" };

public static final String[] UN_ANIMATE_PRONOUN_LIST = { "it", "its", "itself" };// , "that", "these", "this", "those"
                                                                                 // };

public static final String[] MALE_PRONOUN_LIST = { "he", "him", "his", "himself" };

public static final String[] FEMALE_PRONOUN_LIST = { "she", "her", "hers", "herself" };

public static final String[] NEITHER_PRONOUN_LIST = { "it", "its", "itself" };// , "that", "these", "this", "those" };

public static final String[] UNKNOWN_PRONOUN_LIST = { "they", "them", "their", "themselves" };

private static final String[] POSSESIVE_LIST = { "his", "their", "its", "my", "your", "our", "her" };

private static final String[] REFLEXIVE_LIST = { "myself", "ourselves", "yourself", "yourselves", "himself", "herself",
    "themselves" };

private static final String[] S_PRONOUNS = { "he", "she", "they", "we" };

private static final String[] O_PRONOUNS = { "him", "her", "them", "me", "us" };

private static final String[] SO_PRONOUNS = { "you", "it" };// , "that", "these", "this", "those"};

private static final String[] UNINF_WORDS_AR = { "the", "its", "his", "her", "my", "your", "their", "our", "Mr.",
    "Mrs.", "Ms.", "Dr.", "Gov.", "an", "a", "Mr", "Mrs", "Ms", "Dr", "Gov", "this", "that", "these", "those" };

private static Set<String> UNINF_WORDS;
private static final String[] DETERMINERS = { "a", "an", "the", "this", "these", "that", "those" };

public static final String[] DEMONSTRATIVES = { "this", "these", "that", "those" };

public static final String[] PERSON_PREFIXES = { "mr", "mr.", "sir", "ms", "ms.", "mrs", "mrs.", "dr.", "dr" };
public static final String[] MALE_PERSON_PREFIXES = { "mr", "mr.", "sir" };
public static final String[] FEMALE_PERSON_PREFIXES = { "ms", "ms.", "mrs", "mrs.", "madame", "madmoaselle" };

public static Set<String> STOPWORDS = null;
public static Set<String> FEMALE_NAMES = null;
public static Set<String> MALE_NAMES = null;

public static String[] CORP_DESIGN = { "co.", "co", "cie", "cie.", "cos.", "company", "corp", "corp.", "corporation",
    "inc.", "inc", "ltd.", "ltd", "ltda.", "ltda", "l.p.", "lp", "Associates", "Assoc.", "group", "groupe", "grupo",
    "bros", "bros.", "bancorp", "bancorp.", "sdn", "sdn.", "bhd", "bhd.", "plc", "plc.", "s.a.", "s.a", "sa",
    "M.e.T.A.", "M.e.T.A", "g.m.b.h.", "g.m.b.h", "gmbh", "s.p.a.", "s.p.a", "spa", "c.a.", "c.a", "a.g.", "a.g", "ag",
    "a.b.", "a.b", "ab", "aktiebolaget", "aktiengesellschaft", "n.v.", "n.v", "nv", "bv", "b.v.", "b.v", "p.c.", "p.c",
    "de c.v.", "de c.v", "de cv", "b.d.d.p.", "b.d.d.p", "bddp" };

private static Set<String> CORP_DESIGN_SET;
private static Set<String> DETERMINERS_SET;
private static Set<String> REPORTING_VERBS_SET;

public static String[] KNOWN_GRAM_RELATIONS = { "SUBJECT", "OBJECT", "MODIFIER", "NONE" };

public static final String[] TITLES = { "Mr.", "President", "vice", "Senator", "Correspondent", "Mrs.", "Ms.", "Dr.",
    "Speaker", "Mister", "Governor", "Chairman", "Professor", "Minister", "Mayor", "Congressman", "Commissioner",
    "Ambassador", "Attorney", "Lady", "Prosecutor", "Leader", "Spokesman", "Secretary", "Representative",
    "Millionaire", "Executive", "Officer", "Lieutenant", "Inspector", "Front-Runner", "Comedienne", "Columnist",
    "Businessman", "Senators", "Essayist", "Director", "Captain", "Actress", "Lawyer", "Chief", "Actor", "Republican",
    "Quarterback", "Publicist", "Principal", "Paramedic", "Legislator", "Sergeant ", "Reverend", "Reporter",
    "Official", "Diplomat", "Colonel", "Brother", "Writer", "Warden", "Mother", "Madame", "Madam", "Author", "Rev.",
    "Miss", "Father", "Democrat", "Republican", "official", "representative", "spokesman", "spokesperson",
    "spokeswoman", "chief", "head", "mr.", "mr", "messrs", "messrs.", "ms.", "ms", "mrs.", "mrs", "dr.", "dr", "prof.",
    "prof", "rev", "rev.", "rep.", "rep", "reps.", "reps", "sen.", "sen", "sens.", "sens", "gov.", "gov", "gen.",
    "gen", "maj.", "maj", "col.", "col", "lt.", "lt", "sgt.", "sgt" };

private static Set<String> TITLES_SET;

private static final String[] DATES = { "today", "yesterday", "tomorrow", "this year", "last year", "next year",
    "this month", "last month", "next month", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
    "Sunday", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October",
    "November", "December" };


static final String[] COMP_SUPERTYPES = { "person", "location", "organization", "time", "time_period", "group",
    "date", "day", "money", "sum", "cognitive_state", "measure", "transferred_property", "property", "quantity",
    "statistic" };


final private static String[] REPORTING_VERBS = { "say", "says", "said", "tell", "tells", "told", "recall", "recalls",
    "recalled", "remark", "remarks", "remarked", "acknowledge", "acknowledges", "acknowledged", "allege", "alleges",
    "alleged", "add", "adds", "added", "stress", "stresses", "stressed", "note", "notes", "noted", "aver", "agree",
    "agrees", "agreed", "argue", "argues", "argued", "assert", "asserts", "asserted", "deny", "denies", "denied",
    "lash", "lashes", "lashed", "maintain", "maintains", "maintained", "insist", "insists", "insisted", "suppose",
    "supposes", "supposed", "think", "thinks", "thought", "speculate", "speculates", "speculated", "inform", "informs",
    "informed", "recount", "recounts", "recounted", "order", "orders", "ordered", "request", "requests", "requested",
    "ask", "asks", "asked", "infer", "infers", "inferred", "guess", "guesses", "guessed", "understand", "understands",
    "understood", "affirm", "affirms", "affirmed", "declare", "declares", "declared", "state", "states", "stated",
    "confirm", "confirms", "confirmed", "asertain", "asertains", "asertained", "label", "labels", "labeled", "admit",
    "admits", "admitted", "comment", "comments", "commented", "warn", "warns", "warned", "speak", "speaks", "spoke",
    "believe", "believes", "believed", "suggest", "suggests", "suggested", "indicate", "indicates", "indicated",
    "reckon", "reckons", "reckoned", "charge", "charges", "charged", "demand", "demands", "demanded", "preach",
    "preaches", "preached", "emphasize", "emphasizes", "emphasized", "want", "wants", "wanted", "question",
    "questions", "questioned", "feel", "feels", "felt", "blame", "blames", "blamed", "shout", "shouts", "shouted",
    "advise", "advises", "advised", "dismiss", "dismissed", "dismisses", "proclaim", "proclaims", "proclaimed",
    "mention", "mentions", "mentioned", "recognize", "recognizes", "recognized", "describe", "describes", "described",
    "accuse", "accuses", "accused", "refuse", "refused", "refuses", "consider", "considers", "considers", "claim",
    "claims", "claimed" };

/*
 * Some enumerations that are used
 */
public enum NumberEnum {
  SINGLE, PLURAL, UNKNOWN
};

public enum GenderEnum {
  FEMININE, MASC, NEUTER, EITHER, UNKNOWN
};

public enum AnimacyEnum {
  ANIMATE, UNANIMATE, UNKNOWN
};

public enum PRTypeEnum {
  SUBJECT, OBJECT, POSSESSIVE, PLEONASTIC, NONE, UNKNOWN
}

public enum ArticleTypeEnum {
  DEFINITE, INDEFINITE, QUANTIFIED, NONE, UNKNOWN
}

public enum PersonPronounTypeEnum {
	FIRST, SECOND, THIRD, NONE
	}
	public enum CountPronounTypeEnum {
	SINGULAR, PLURAL, EITHER, NONE
	}
}
