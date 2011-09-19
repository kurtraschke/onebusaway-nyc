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
package org.onebusaway.nyc.transit_data_federation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.onebusaway.nyc.transit_data.model.NycQueuedInferredLocationBean;
import org.onebusaway.nyc.transit_data.model.NycVehicleManagementStatusBean;
import org.onebusaway.nyc.transit_data.services.VehicleTrackingManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class VehicleTrackingManagementServiceImpl implements VehicleTrackingManagementService {

  private static Logger _log = LoggerFactory.getLogger(VehicleTrackingManagementServiceImpl.class);

  HashMap<String, NycVehicleManagementStatusBean> _vehicleIdToVehicleStatusBeanMap = 
		  new HashMap<String, NycVehicleManagementStatusBean>();
  
  @Override
  public void setVehicleStatus(String vehicleId, boolean status) throws Exception {
	  NycVehicleManagementStatusBean b = _vehicleIdToVehicleStatusBeanMap.get(vehicleId);
	  _vehicleIdToVehicleStatusBeanMap.put(vehicleId, b);

	  throw new Exception("Not implemented.");
  }

  @Override
  public void resetVehicleTrackingForVehicleId(String vehicleId) throws Exception {
	  _vehicleIdToVehicleStatusBeanMap.remove(vehicleId);

	  throw new Exception("Not implemented.");
  }

  @Override
  public List<NycVehicleManagementStatusBean> getAllVehicleManagementStatusBeans() {
	  return new ArrayList<NycVehicleManagementStatusBean>(_vehicleIdToVehicleStatusBeanMap.values());
  }

  @Override
  public NycVehicleManagementStatusBean getVehicleManagementStatusBeanForVehicleId(String vehicleId) {
	  return _vehicleIdToVehicleStatusBeanMap.get(vehicleId);
  }
  
  public void handleRecord(NycQueuedInferredLocationBean record) {
	  if(record != null)
		  _vehicleIdToVehicleStatusBeanMap.put(record.getVehicleId(), record.getManagementRecord());  
  }
}