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

import static org.junit.Assert.*;

import org.onebusaway.collections.MappingLibrary;
import org.onebusaway.transit_data.model.RouteBean;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LexicographicRouteComparatorServiceImplTest {

  private List<RouteBean> testRoutes = new ArrayList<RouteBean>();

  @Before
  public void setup() {
    RouteBean.Builder b = RouteBean.builder();

    b.setId("Aaa");
    b.setShortName("Aaa");
    b.setLongName("Long Name");

    testRoutes.add(b.create());

    b = RouteBean.builder();

    b.setId("Bbb");
    b.setShortName("Bbb");
    b.setLongName("Long Name");

    testRoutes.add(b.create());

    b = RouteBean.builder();

    b.setId("Ccc");
    b.setShortName("Ccc");
    b.setLongName("Long Name");

    testRoutes.add(b.create());

    testRoutes = Collections.unmodifiableList(testRoutes);
  }

  @Test
  public void testSortById() {
    Comparator<RouteBean> c = new LexicographicRouteComparatorServiceImpl(
        Collections.singletonList("id"));

    List<RouteBean> shuffledRoutes = new ArrayList<RouteBean>(testRoutes);
    Collections.shuffle(shuffledRoutes);

    Collections.sort(shuffledRoutes, c);

    assertArrayEquals(
        MappingLibrary.map(testRoutes, "id").toArray(new String[] {}),
        MappingLibrary.map(shuffledRoutes, "id").toArray(new String[] {}));
  }

  @Test
  public void testSortByLongAndShortName() {
    Comparator<RouteBean> c = new LexicographicRouteComparatorServiceImpl(
        Lists.newArrayList("longName", "shortName"));

    List<RouteBean> shuffledRoutes = new ArrayList<RouteBean>(testRoutes);
    Collections.shuffle(shuffledRoutes);

    Collections.sort(shuffledRoutes, c);

    assertArrayEquals(
        MappingLibrary.map(testRoutes, "id").toArray(new String[] {}),
        MappingLibrary.map(shuffledRoutes, "id").toArray(new String[] {}));
  }

}
