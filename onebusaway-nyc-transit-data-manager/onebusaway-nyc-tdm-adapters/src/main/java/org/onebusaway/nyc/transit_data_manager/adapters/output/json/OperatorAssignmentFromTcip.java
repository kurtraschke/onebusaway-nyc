package org.onebusaway.nyc.transit_data_manager.adapters.output.json;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.onebusaway.nyc.transit_data_manager.adapters.ModelCounterpartConverter;
import org.onebusaway.nyc.transit_data_manager.adapters.output.model.json.OperatorAssignment;
import org.onebusaway.nyc.transit_data_manager.importers.tools.UtsMappingTool;

import tcip_final_3_0_5_1.SCHOperatorAssignment;

/**
 * Contains convert function to convert TCIP Ooperator assignments
 * (SCHOperatorAssignment) to Json model operator assignments
 * (OperatorAssignment).
 * 
 * @author sclark
 * 
 */
public class OperatorAssignmentFromTcip implements
    ModelCounterpartConverter<SCHOperatorAssignment, OperatorAssignment> {

  UtsMappingTool mappingTool = null;

  public OperatorAssignmentFromTcip() {
    mappingTool = new UtsMappingTool();
  }

  public OperatorAssignment convert(SCHOperatorAssignment input) {
    OperatorAssignment opAssign = new OperatorAssignment();

    opAssign.setAgencyId(mappingTool.getJsonModelAgencyIdByTcipId(input.getOperator().getAgencyId()));
    opAssign.setPassId(String.valueOf(input.getOperator().getOperatorId()));
    opAssign.setRunRoute(input.getLocalSCHOperatorAssignment().getRunRoute());
    opAssign.setRunId(mappingTool.cutRunNumberFromTcipRunDesignator(input.getRun().getDesignator()));

    DateTimeFormatter xmlDTF = ISODateTimeFormat.dateTimeNoMillis();
    DateTime serviceDate = xmlDTF.parseDateTime(input.getMetadata().getEffective());

    DateTimeFormatter shortDateDTF = ISODateTimeFormat.date();
    opAssign.setServiceDate(shortDateDTF.print(serviceDate));

    DateTimeFormatter jsonUpdateDTF = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    opAssign.setUpdated(jsonUpdateDTF.print(xmlDTF.parseDateTime(input.getMetadata().getUpdated())));

    return opAssign;
  }

}
