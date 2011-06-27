package reconcile.featureVector.individualFeature;

import java.util.Map;

import reconcile.DataConstructor;
import reconcile.data.Annotation;
import reconcile.data.Document;
import reconcile.featureVector.Feature;
import reconcile.featureVector.NominalFeature;


/*
 * This feature is: C if the two NP's compatible values for GENDER, NUMBER, and do not have incompatible values for
 * CONTRAINDICES, ANIMACY, PRONOUN and CONTAINSPN I otherwise
 */

public class Constraints
    extends NominalFeature {

public Constraints() {
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
  if (!DataConstructor.createFeature("Gender").getValue(np1, np2, doc, featVector).equals(COMPATIBLE)
      && !featVector.get(DataConstructor.createFeature("Gender")).equals(SAME)) return INCOMPATIBLE;
  if (!DataConstructor.createFeature("Number").getValue(np1, np2, doc, featVector).equals(COMPATIBLE)) return INCOMPATIBLE;
  if (DataConstructor.createFeature("Contraindices").getValue(np1, np2, doc, featVector).equals(INCOMPATIBLE))
    return INCOMPATIBLE;
  if (DataConstructor.createFeature("Span").getValue(np1, np2, doc, featVector).equals(INCOMPATIBLE)) return INCOMPATIBLE;
  if (DataConstructor.createFeature("Animacy").getValue(np1, np2, doc, featVector).equals(INCOMPATIBLE))
    return INCOMPATIBLE;
  if (DataConstructor.createFeature("Pronoun").getValue(np1, np2, doc, featVector).equals(INCOMPATIBLE))
    return INCOMPATIBLE;
  if (DataConstructor.createFeature("ContainsPN").getValue(np1, np2, doc, featVector).equals(INCOMPATIBLE))
    return INCOMPATIBLE;

  return COMPATIBLE;
}

}
