package org.onebusaway.nyc.transit_data_manager.importers;

import java.util.List;

import tcip_final_3_0_5_1.CPTFleetSubsetGroup;

public interface BusDepotAssignmentsOutputConverter {
  List<CPTFleetSubsetGroup> convertAssignments();
}
