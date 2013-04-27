/**
 * Copyright (c) 2011 Metropolitan Transportation Authority
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
package org.onebusaway.nyc.transit_data_federation.services.nyc;

import java.util.List;

import org.onebusaway.geospatial.model.CoordinatePoint;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.nyc.transit_data_federation.bundle.tasks.stif.NonRevenueStopData;

/**
 * Locations of bases/depots.
 * @author jmaki
 *
 */
public interface BaseLocationService {

  public String getBaseNameForLocation(CoordinatePoint location);

  public String getTerminalNameForLocation(CoordinatePoint location);
  
  public List<NonRevenueStopData> getNonRevenueStopsForTripId(AgencyAndId tripId);

}
