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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NycRouteComparatorServiceImpl implements RouteComparatorService {

  /**
   * This pattern is derived from observed route names rather than any official
   * naming convention - that is, it is possible that new route names will
   * require it to be updated.
   */
  private final static Pattern NYC_ROUTE_PATTERN = Pattern.compile("^([A-Za-z]{1,3})([0-9]{1,3})([A-Z]?)((?:-SBS)?)$");

  private final static int BOROUGH_GROUP = 1;
  private final static int ROUTE_NUMBER_GROUP = 2;
  private final static int SUFFIX_GROUP = 3;
  private final static int SBS_SUFFIX_GROUP = 4;

  @Override
  public int compare(Object a, Object b) {
    String aShortName = (String) PropertyPathExpression.evaluate(a, "shortName");
    String bShortName = (String) PropertyPathExpression.evaluate(b, "shortName");

    if (aShortName != null && bShortName != null) {
      Matcher matcherA = NYC_ROUTE_PATTERN.matcher(aShortName);
      Matcher matcherB = NYC_ROUTE_PATTERN.matcher(bShortName);

      if (matcherA.matches() && matcherB.matches()) {

        String boroughA = safe(matcherA.group(BOROUGH_GROUP), "");
        String boroughB = safe(matcherB.group(BOROUGH_GROUP), "");

        Integer routeA = new Integer(safe(matcherA.group(ROUTE_NUMBER_GROUP),
            "0"));
        Integer routeB = new Integer(safe(matcherB.group(ROUTE_NUMBER_GROUP),
            "0"));

        String suffixA = safe(matcherA.group(SUFFIX_GROUP), "");
        String suffixB = safe(matcherB.group(SUFFIX_GROUP), "");

        String sbsSuffixA = safe(matcherA.group(SBS_SUFFIX_GROUP), "");
        String sbsSuffixB = safe(matcherB.group(SBS_SUFFIX_GROUP), "");

        return chainedComparator(boroughA.compareTo(boroughB),
            routeA.compareTo(routeB), suffixA.compareTo(suffixB),
            sbsSuffixA.compareTo(sbsSuffixB));

      } else {
        return fallbackCompare(a, b);
      }
    } else {
      return fallbackCompare(a, b);
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public int fallbackCompare(Object a, Object b) {
    return ((Comparable) a).compareTo((Comparable) b);
  }

  private <T> T safe(T a, T b) {
    return (a == null) ? b : a;
  }

  private int chainedComparator(Integer... values) {
    for (Integer i : values) {
      if (i != 0) {
        return i;
      }
    }
    return 0;
  }

}
