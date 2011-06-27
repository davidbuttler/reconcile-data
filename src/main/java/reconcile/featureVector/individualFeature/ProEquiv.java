package reconcile.featureVector.individualFeature;

import java.util.Map;

import reconcile.DataConstructor;
import reconcile.data.Annotation;
import reconcile.data.Document;
import reconcile.featureVector.Feature;
import reconcile.featureVector.NominalFeature;


/*
 * This feature is: I if both NPs are pronouns, agree in GENDER and NUMBER and appear in consecutive sentences C
 * otherwise
 */

public class ProEquiv
    extends NominalFeature {

public ProEquiv() {
  name = this.getClass().getSimpleName();
}

@Override
public String[] getValues()
{
  return IC;
}

@Override
public String produceValue(Annotation np1, Annotation np2, Document doc, Map<Feature, String> featVector)
{
  if (!featVector.get(DataConstructor.createFeature("BothPronouns")).equals(COMPATIBLE)) return INCOMPATIBLE;
  if (!featVector.get(DataConstructor.createFeature("Gender")).equals(COMPATIBLE)
      && !featVector.get(DataConstructor.createFeature("Gender")).equals(SAME)) return INCOMPATIBLE;
  if (!featVector.get(DataConstructor.createFeature("Number")).equals(COMPATIBLE)) return INCOMPATIBLE;
  if (!DataConstructor.createFeature("SentNum").getValue(np1, np2, doc, featVector).equals("1")) return INCOMPATIBLE;

  return COMPATIBLE;
}

}
