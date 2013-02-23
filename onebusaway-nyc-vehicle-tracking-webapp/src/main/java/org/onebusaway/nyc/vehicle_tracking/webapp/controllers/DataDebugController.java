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
package org.onebusaway.nyc.vehicle_tracking.webapp.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.nyc.transit_data_federation.services.nyc.DestinationSignCodeService;
import org.onebusaway.nyc.transit_data_federation.services.nyc.RunService;
import org.onebusaway.transit_data_federation.model.ShapePoints;
import org.onebusaway.transit_data_federation.services.shapes.ShapePointService;
import org.onebusaway.transit_data_federation.services.transit_graph.TransitGraphDao;
import org.onebusaway.transit_data_federation.services.transit_graph.TripEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DataDebugController {

  @Autowired
  private TransitGraphDao _graphDao;
  
  @Autowired
  private DestinationSignCodeService _dscService;

  @Autowired
  private RunService _runService;

  @Autowired
  private ShapePointService _shapePointService;

  @RequestMapping(value = "/data-debug.do", method = RequestMethod.GET)
  public ModelAndView index() {

    List<AgencyAndId> tripsWithNullDSC = new ArrayList<AgencyAndId>();
    List<AgencyAndId> tripsWithNullRunId = new ArrayList<AgencyAndId>();
    List<AgencyAndId> tripsWithNoShape = new ArrayList<AgencyAndId>();
       
    for(TripEntry tripEntry : _graphDao.getAllTrips()) {
      AgencyAndId tripId = tripEntry.getId();
      String dsc = _dscService.getDestinationSignCodeForTripId(tripId);
      if(dsc == null) {
    	  tripsWithNullDSC.add(tripId);
      }
      
      Set<String> runIds = _runService.getRunIdsForTrip(tripEntry);
      if(runIds == null || runIds.isEmpty() == true) {
    	  tripsWithNullRunId.add(tripId);
      }
      
      if(tripEntry.getShapeId() == null) {
    	  tripsWithNoShape.add(tripId);
      } else {      
    	  ShapePoints shapePoints = _shapePointService.getShapePointsForShapeId(tripEntry.getShapeId());
    	  if(shapePoints == null || shapePoints.isEmpty() == true) {
    		  tripsWithNoShape.add(tripId);
    	  }
      }
    }
    
    DebugModel model = new DebugModel();
    model.setTripsWithNullDSCs(tripsWithNullDSC);
    model.setTripsWithNullRunId(tripsWithNullRunId);
    model.setTripsWithNoShape(tripsWithNoShape);
    
    return new ModelAndView("data-debug.jspx", "data", model);
  }

  public class DebugModel {
	  
	  private List<AgencyAndId> tripsWithNullDSCs;

	  private List<AgencyAndId> tripsWithNullRunId;

	  private List<AgencyAndId> tripsWithNoShape;

	  public List<AgencyAndId> getTripsWithNullDSCs() {
		  return tripsWithNullDSCs;
	  }

	  public void setTripsWithNullDSCs(List<AgencyAndId> tripsWithNullDSCs) {
		  this.tripsWithNullDSCs = tripsWithNullDSCs;
	  }

	  public List<AgencyAndId> getTripsWithNullRunId() {
		  return tripsWithNullRunId;
	  }

	  public void setTripsWithNullRunId(List<AgencyAndId> tripsWithNullRunId) {
		  this.tripsWithNullRunId = tripsWithNullRunId;
	  }

	  public List<AgencyAndId> getTripsWithNoShape() {
		  return tripsWithNoShape;
	  }

	  public void setTripsWithNoShape(List<AgencyAndId> tripsWithNoShape) {
		  this.tripsWithNoShape = tripsWithNoShape;
	  }

  }
  
}
