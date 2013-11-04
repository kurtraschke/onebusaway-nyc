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
import java.util.Date;
import java.util.List;

import org.onebusaway.collections.tuple.T2;
import org.onebusaway.collections.tuple.Tuples;
import org.onebusaway.nyc.transit_data_federation.siri.SiriExtensionWrapper;
import org.onebusaway.transit_data.model.ListBean;
import org.onebusaway.transit_data.model.TripStopTimeBean;
import org.onebusaway.transit_data.model.VehicleStatusBean;
import org.onebusaway.transit_data.model.trips.TripDetailsBean;
import org.onebusaway.transit_data.model.trips.TripDetailsInclusionBean;
import org.onebusaway.transit_data.model.trips.TripForVehicleQueryBean;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.Position;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor;
import com.google.transit.realtime.GtfsRealtime.VehicleDescriptor;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition.VehicleStopStatus;

import uk.org.siri.siri.LocationStructure;
import uk.org.siri.siri.MonitoredCallStructure;
import uk.org.siri.siri.VehicleActivityStructure;

public class VehiclePositionsForAgencyAction extends GtfsRealtimeActionSupport {

  private static final long serialVersionUID = 1L;

  @Override
  protected void fillFeedMessage(FeedMessage.Builder feed, String agencyId,
          long timestamp) {

    int maximumOnwardCalls = Integer.MAX_VALUE;
    List<VehicleActivityStructure> activities = new ArrayList<VehicleActivityStructure>();
    ListBean<VehicleStatusBean> vehicles = _nycTransitDataService.getAllVehiclesForAgency(agencyId, timestamp);

    for (VehicleStatusBean v : vehicles.getList()) {
      VehicleActivityStructure activity = _realtimeService.getVehicleActivityForVehicle(v.getVehicleId(), maximumOnwardCalls, timestamp);
      if (activity != null) {
        activities.add(activity);
      }
    }

    for (VehicleActivityStructure vehicleActivity : activities) {
      VehicleActivityStructure.MonitoredVehicleJourney mvj = vehicleActivity.getMonitoredVehicleJourney();

      FeedEntity.Builder entity = feed.addEntityBuilder();
      entity.setId(mvj.getFramedVehicleJourneyRef().getDatedVehicleJourneyRef());

      GtfsRealtime.VehiclePosition.Builder vehiclePosition = entity.getVehicleBuilder();

      TripDescriptor.Builder tripDesc = vehiclePosition.getTripBuilder();
      tripDesc.setTripId(normalizeId(mvj.getFramedVehicleJourneyRef().getDatedVehicleJourneyRef()));
      tripDesc.setStartDate(mvj.getFramedVehicleJourneyRef().getDataFrameRef().getValue().replace("-", ""));
      tripDesc.setStartTime(getStartTimeForTrip(mvj.getVehicleRef().getValue(), timestamp));
      tripDesc.setRouteId(normalizeId(mvj.getLineRef().getValue()));

      VehicleDescriptor.Builder vehicleDesc = vehiclePosition.getVehicleBuilder();
      vehicleDesc.setId(normalizeId(mvj.getVehicleRef().getValue()));
      vehicleDesc.setLabel(removeAgencyId(mvj.getVehicleRef().getValue()));

      LocationStructure location = mvj.getVehicleLocation();
      Position.Builder position = vehiclePosition.getPositionBuilder();
      position.setLatitude(location.getLatitude().floatValue());
      position.setLongitude(location.getLongitude().floatValue());

      position.setBearing(mvj.getBearing());

      T2<Integer, VehicleStopStatus> stopStatusAndSequence = getStopStatusAndSequenceForStop(mvj, timestamp);

      vehiclePosition.setCurrentStopSequence(stopStatusAndSequence.getFirst());
      vehiclePosition.setCurrentStatus(stopStatusAndSequence.getSecond());

      vehiclePosition.setTimestamp(vehicleActivity.getRecordedAtTime().getTime() / 1000L);
    }
  }

  private T2<Integer, VehicleStopStatus> getStopStatusAndSequenceForStop(VehicleActivityStructure.MonitoredVehicleJourney mvj, long timestamp) {
    MonitoredCallStructure mc = mvj.getMonitoredCall();

    TripForVehicleQueryBean tfvqb = new TripForVehicleQueryBean();
    tfvqb.setVehicleId(mvj.getVehicleRef().getValue());
    tfvqb.setTime(new Date(timestamp));
    tfvqb.setInclusion(new TripDetailsInclusionBean(false, true, false));

    TripDetailsBean tripDetails = _nycTransitDataService.getTripDetailsForVehicleAndTime(tfvqb);

    int stopAppearanceCount = 0;
    int stopSequence = 0;

    int visitNumber = mc.getVisitNumber().intValue();
    String stopId = mc.getStopPointRef().getValue();

    for (TripStopTimeBean tripStopTimeBean : tripDetails.getSchedule().getStopTimes()) {
      stopSequence++;
      if (tripStopTimeBean.getStop().getId().equals(stopId)) {
        stopAppearanceCount++;
        if (stopAppearanceCount == visitNumber) {
          break;
        }
      }
    }

    String presentableDistance = ((SiriExtensionWrapper) mc.getExtensions().getAny()).getDistances().getPresentableDistance();
    VehicleStopStatus vss;

    if (presentableDistance.equals("at stop")) {
      vss = VehicleStopStatus.STOPPED_AT;
    } else if (presentableDistance.equals("approaching")) {
      vss = VehicleStopStatus.INCOMING_AT;
    } else {
      vss = VehicleStopStatus.IN_TRANSIT_TO;
    }

    return Tuples.tuple(stopSequence, vss);
  }
}
