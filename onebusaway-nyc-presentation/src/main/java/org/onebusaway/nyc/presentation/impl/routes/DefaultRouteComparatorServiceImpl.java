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

import java.util.List;

public class DefaultRouteComparatorServiceImpl implements
    RouteComparatorService {
  private List<String> _fieldsToCompare;

  public DefaultRouteComparatorServiceImpl(List<String> fieldsToCompare) {
    _fieldsToCompare = fieldsToCompare;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public int compare(Object a, Object b) {
    for (String fieldName : _fieldsToCompare) {

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
