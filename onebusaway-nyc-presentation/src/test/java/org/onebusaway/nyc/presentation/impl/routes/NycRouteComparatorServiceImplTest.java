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

import static org.junit.Assert.assertArrayEquals;

import org.onebusaway.collections.MappingLibrary;
import org.onebusaway.transit_data.model.RouteBean;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NycRouteComparatorServiceImplTest {

  private static final String[] ROUTE_NAMES = {
      "M1", "M2", "M15", "M15-SBS", "M34-SBS", "M34A-SBS", "M101", "M102",
      "Q10", "Q11", "X1", "X17A", "X17J"};

  private Comparator<RouteBean> routeComparator = new NycRouteComparatorServiceImpl();

  @Test
  public void testRouteSorting() {
    List<RouteBean> testRoutes = new ArrayList<RouteBean>();

    for (String routeName : ROUTE_NAMES) {
      RouteBean.Builder b = RouteBean.builder();

      b.setId(routeName);
      b.setShortName(routeName);

      testRoutes.add(b.create());
    }

    Collections.shuffle(testRoutes);

    Collections.sort(testRoutes, routeComparator);

    List<String> sortedShortNames = MappingLibrary.map(testRoutes, "shortName");

    assertArrayEquals(ROUTE_NAMES, sortedShortNames.toArray(new String[] {}));
  }
}
