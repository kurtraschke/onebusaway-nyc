package org.onebusaway.nyc.webapp.actions.m.model;

import org.onebusaway.transit_data.model.StopBean;
import org.onebusaway.transit_data_federation.services.AgencyAndIdLibrary;

/**
 * A stop on a route, the route being the top-level search result.
 * @author jmaki
 *
 */
public class StopOnRoute {

  private StopBean stop;
  
  private String distanceAways;
  
  public StopOnRoute(StopBean stop, String distanceAways) {
    this.stop = stop;
    this.distanceAways = distanceAways;
  }
  
  public String getId() {
    return stop.getId();
  }
  
  public String getIdWithoutAgency() {
    return AgencyAndIdLibrary.convertFromString(getId()).getId();
  }
  
  public String getName() {
    return stop.getName();
  }
  
  public Double getLatitude() {
    return stop.getLat();
  }
  
  public Double getLongitude() {
    return stop.getLon();
  }

  public String getStopDirection() {
   return stop.getDirection();
  }  
  
  public String getDistanceAways() {
    return distanceAways;
  }

}
