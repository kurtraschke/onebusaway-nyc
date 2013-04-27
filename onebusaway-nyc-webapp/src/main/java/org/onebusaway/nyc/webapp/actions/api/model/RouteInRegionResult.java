package org.onebusaway.nyc.webapp.actions.api.model;

import org.onebusaway.nyc.presentation.model.SearchResult;
import org.onebusaway.transit_data.model.RouteBean;

import java.util.List;

/**
 * Route available near or within an area.
 * 
 * @author jmaki
 * 
 */
public class RouteInRegionResult implements SearchResult {

	private RouteBean route;

	private List<String> polylines;

	public RouteInRegionResult(RouteBean route, List<String> polylines) {
		this.route = route;
		this.polylines = polylines;
	}

	public String getId() {
		return route.getId();
	}

	public String getShortName() {
		return route.getShortName();
	}
        
        public String getLongName() {
                return route.getLongName();
        }

	public String getDescription() {
		return route.getDescription();
	}
        
	public String getColor() {
		if (route.getColor() != null) {
			return route.getColor();
		} else {
			return "000000";
		}
	}

	public List<String> getPolylines() {
		return polylines;
	}

        public String getAgency() {
                return route.getAgency().getName();
        }
}