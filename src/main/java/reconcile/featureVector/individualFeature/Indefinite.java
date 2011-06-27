package reconcile.featureVector.individualFeature;

import java.util.Map;

import reconcile.DataConstructor;
import reconcile.data.Annotation;
import reconcile.data.Document;
import reconcile.featureVector.Feature;
import reconcile.featureVector.NominalFeature;
import reconcile.features.FeatureUtils;
import reconcile.general.Constants;


/*
 * This feature is: I if the second NP is an indefinite and is not an appositive C otherwise
 */

public class Indefinite
    extends NominalFeature {

public Indefinite() {
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
  if (DataConstructor.createFeature(Constants.APPOSITIVE).getValue(np1, np2, doc, featVector).equals(COMPATIBLE))
    return COMPATIBLE;
  if (FeatureUtils.isIndefinite(np2, doc)) return INCOMPATIBLE;
  return COMPATIBLE;
}

}
