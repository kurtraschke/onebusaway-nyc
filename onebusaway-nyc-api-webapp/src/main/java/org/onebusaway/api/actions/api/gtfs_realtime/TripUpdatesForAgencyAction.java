/**
 * Copyright (C) 2013 Kurt Raschke <kurt@kurtraschke.com>
 * Copyright (C) 2013 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onebusaway.api.actions.api.gtfs_realtime;

import java.util.ArrayList;
import java.util.List;

import org.onebusaway.transit_data.model.ListBean;
import org.onebusaway.transit_data.model.VehicleStatusBean;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.VehicleDescriptor;

import uk.org.siri.siri.OnwardCallStructure;
import uk.org.siri.siri.VehicleActivityStructure;
import uk.org.siri.siri.VehicleActivityStructure.MonitoredVehicleJourney;

public class TripUpdatesForAgencyAction extends GtfsRealtimeActionSupport {

  private static final long serialVersionUID = 1L;

  @Override
  protected void fillFeedMessage(FeedMessage.Builder feed, String agencyId,
      long timestamp) {

      int maximumOnwardCalls = Integer.MAX_VALUE;
      
      //String gaLabel = "All Vehicles";
      
      List<VehicleActivityStructure> activities = new ArrayList<VehicleActivityStructure>();
      ListBean<VehicleStatusBean> vehicles = _nycTransitDataService.getAllVehiclesForAgency(agencyId, timestamp);
	
      for (VehicleStatusBean v : vehicles.getList()) {
          VehicleActivityStructure activity = _realtimeService.getVehicleActivityForVehicle(v.getVehicleId(), maximumOnwardCalls, timestamp);
          if (activity != null) {
              activities.add(activity);
          }
      }
    
    //_monitoringActionSupport.reportToGoogleAnalytics(_request, "Vehicle Monitoring", gaLabel, _configurationService);

    for (VehicleActivityStructure vehicleActivity : activities) {      
      MonitoredVehicleJourney mvj = vehicleActivity.getMonitoredVehicleJourney();
        
      FeedEntity.Builder entity = feed.addEntityBuilder();
      entity.setId(Integer.toString(feed.getEntityCount()));
      TripUpdate.Builder tripUpdate = entity.getTripUpdateBuilder();

      TripDescriptor.Builder tripDesc = tripUpdate.getTripBuilder();
      tripDesc.setTripId(normalizeId(mvj.getFramedVehicleJourneyRef().getDatedVehicleJourneyRef()));
      tripDesc.setStartDate(mvj.getFramedVehicleJourneyRef().getDataFrameRef().getValue().replace("-", ""));
      tripDesc.setStartTime(getStartTimeForTrip(mvj.getVehicleRef().getValue(), timestamp));
      tripDesc.setRouteId(normalizeId(mvj.getLineRef().getValue()));

      VehicleDescriptor.Builder vehicleDesc = tripUpdate.getVehicleBuilder();
      vehicleDesc.setId(normalizeId(mvj.getVehicleRef().getValue()));
      vehicleDesc.setLabel(removeAgencyId(mvj.getVehicleRef().getValue()));

      List<OnwardCallStructure> onwardCalls = mvj.getOnwardCalls().getOnwardCall();

      for(OnwardCallStructure call: onwardCalls) {
        TripUpdate.StopTimeUpdate.Builder stopTimeUpdate = tripUpdate.addStopTimeUpdateBuilder();
        stopTimeUpdate.setStopId(normalizeId(call.getStopPointRef().getValue()));
        
        TripUpdate.StopTimeEvent.Builder departure = stopTimeUpdate.getDepartureBuilder();
        departure.setTime(call.getExpectedDepartureTime().getTime() / 1000L);
        
        TripUpdate.StopTimeEvent.Builder arrival = stopTimeUpdate.getDepartureBuilder();
        arrival.setTime(call.getExpectedArrivalTime().getTime() / 1000L);
      }

      tripUpdate.setTimestamp(vehicleActivity.getRecordedAtTime().getTime() / 1000L);
    }
  }
}
