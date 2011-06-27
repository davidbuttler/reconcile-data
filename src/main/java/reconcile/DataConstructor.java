package reconcile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import reconcile.featureVector.AllFeatures;
import reconcile.featureVector.Feature;

public class DataConstructor {

@SuppressWarnings("rawtypes")
public static Feature createFeature(String name)
{
  if (AllFeatures.featMap == null) {
    AllFeatures.featMap = new HashMap<String, Feature>();
  }

  Feature feat = AllFeatures.featMap.get(name);
  if (feat == null) {
    try {
      String className = name;
      if (!className.contains(".")) {
        className = "reconcile.featureVector.individualFeature." + name;
      }

      Class featClass = Class.forName(className);
      feat = (Feature) featClass.newInstance();
      AllFeatures.featMap.put(name, feat);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  return feat;
}

/*
 * Create the required features and return them in a list
 */
public static List<Feature> createFeatures(String[] features)
{
  ArrayList<Feature> result = new ArrayList<Feature>();

  for (String feat : features) {
    Feature newFeat = createFeature(feat);
    result.add(newFeat);
  }
  return result;
}

}
