/**
 * Copyright (c) 2013 Kurt Raschke
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
package org.onebusaway.nyc.geocoder.model;

import org.onebusaway.geospatial.model.CoordinateBounds;
import org.onebusaway.geospatial.services.SphericalGeometryLibrary;
import org.onebusaway.nyc.geocoder.service.NycGeocoderResult;

import java.io.Serializable;

public class NominatimGeocoderResult implements NycGeocoderResult, Serializable {

    private static final long serialVersionUID = 1L;
    private String formattedAddress = null;
    private String neighborhood = null;
    private Double latitude = null;
    private Double longitude = null;
    private Double northeastLatitude = null;
    private Double northeastLongitude = null;
    private Double southwestLatitude = null;
    private Double southwestLongitude = null;

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public void setNortheastLatitude(Double northeastLatitude) {
        this.northeastLatitude = northeastLatitude;
    }

    public void setNortheastLongitude(Double northeastLongitude) {
        this.northeastLongitude = northeastLongitude;
    }

    public void setSouthwestLatitude(Double southwestLatitude) {
        this.southwestLatitude = southwestLatitude;
    }

    public void setSouthwestLongitude(Double southwestLongitude) {
        this.southwestLongitude = southwestLongitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public Double getLatitude() {
        return latitude;
    }

    @Override
    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String getFormattedAddress() {
        return this.formattedAddress;
    }

    @Override
    public CoordinateBounds getBounds() {
        if (northeastLatitude != null && northeastLongitude != null && southwestLatitude != null && southwestLongitude != null) {

            return new CoordinateBounds(northeastLatitude, northeastLongitude, southwestLatitude, southwestLongitude);
        } else {
            return null;
        }
    }

    @Override
    public boolean isRegion() {
        CoordinateBounds bounds = getBounds();
        if (bounds != null) {
            double height = SphericalGeometryLibrary.distanceFaster(bounds.getMaxLat(), bounds.getMinLon(), bounds.getMinLat(),
                    bounds.getMinLon());
            double width = SphericalGeometryLibrary.distanceFaster(bounds.getMinLat(), bounds.getMinLon(), bounds.getMinLat(),
                    bounds.getMaxLon());
            double area = width * height;
            // region must be larger than 1200 x 1200 meters
            if (area > 1440000) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }
}
