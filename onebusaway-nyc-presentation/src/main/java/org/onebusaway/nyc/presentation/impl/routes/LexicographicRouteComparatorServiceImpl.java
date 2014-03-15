/**
 * Copyright (c) 2014 Kurt Raschke <kurt@kurtraschke.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.onebusaway.nyc.presentation.impl.routes;

import org.onebusaway.collections.PropertyPathExpression;
import org.onebusaway.nyc.presentation.service.routes.RouteComparatorService;
import org.onebusaway.transit_data.model.RouteBean;

import java.util.ArrayList;
import java.util.List;

public class LexicographicRouteComparatorServiceImpl implements
    RouteComparatorService {

  private List<String> fieldsToCompare = new ArrayList<String>();

  public LexicographicRouteComparatorServiceImpl() {

  }

  public LexicographicRouteComparatorServiceImpl(List<String> fieldsToCompare) {
    this.fieldsToCompare.addAll(fieldsToCompare);
  }

  public List<String> getFieldsToCompare() {
    return fieldsToCompare;
  }

  public void setFieldsToCompare(List<String> fieldsToCompare) {
    this.fieldsToCompare = fieldsToCompare;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public int compare(RouteBean a, RouteBean b) {
    for (String fieldName : fieldsToCompare) {

      Comparable valueA = (Comparable) PropertyPathExpression.evaluate(a,
          fieldName);
      Comparable valueB = (Comparable) PropertyPathExpression.evaluate(b,
          fieldName);

      int v = valueA.compareTo(valueB);

      if (v != 0) {
        return v;
      }
    }
    return 0;
  }
}
