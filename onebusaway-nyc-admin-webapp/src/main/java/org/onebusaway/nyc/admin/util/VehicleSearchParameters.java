package org.onebusaway.nyc.admin.util;

/**
 * Holds parameters used for searching vehicles
 * @author abelsare
 *
 */
public enum VehicleSearchParameters {
	
	VEHICLE_ID("vehicleId"),
	ROUTE("route"),
	DEPOT("depot"),
	DSC("dsc"),
	INFERRED_PHASE("inferredPhase"),
	PULLOUT_STATUS("pulloutStatus"),
	EMERGENCY_STATUS("emergencyStatus"),
	FORMAL_INFERRENCE("formalInferrence");
	
	private String value;

	private VehicleSearchParameters(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

}
