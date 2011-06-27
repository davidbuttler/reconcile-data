package reconcile.featureVector.individualFeature;

import java.util.Map;

import reconcile.DataConstructor;
import reconcile.data.Annotation;
import reconcile.data.Document;
import reconcile.featureVector.Feature;
import reconcile.featureVector.NominalFeature;


/*
 * This feature is: C if both NPs are subjects of the main clause NA if exactly one is I otherwise
 */

public class BothMCSubjects
    extends NominalFeature {

public BothMCSubjects() {
  name = this.getClass().getSimpleName();
}

@Override
public String[] getValues()
{
  return ICN;
}

@Override
public String produceValue(Annotation np1, Annotation np2, Document doc, Map<Feature, String> featVector)
{
  boolean sub1 = DataConstructor.createFeature("MCSubject1").getValue(np1, np2, doc, featVector).equals("Y");

  boolean sub2 = DataConstructor.createFeature("MCSubject2").getValue(np1, np2, doc, featVector).equals("Y");

  if (sub1 && sub2) return COMPATIBLE;
  if (sub1 || sub2) return NA;
  return INCOMPATIBLE;
}

}
