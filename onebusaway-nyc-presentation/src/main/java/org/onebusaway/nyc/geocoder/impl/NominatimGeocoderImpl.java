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
package org.onebusaway.nyc.geocoder.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.onebusaway.geospatial.model.CoordinateBounds;
import org.onebusaway.nyc.geocoder.model.NominatimGeocoderResult;
import org.onebusaway.nyc.geocoder.service.NycGeocoderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A geocoder that queries against an OSM Nominatim implementation.
 *
 * Note that some Nominatim implementations restrict use for typeahead queries
 * (which the OBA UI will make); check the terms of service before use.
 *
 * @author kurt
 */
public class NominatimGeocoderImpl extends FilteredGeocoderBase {

    private static Logger _log = LoggerFactory.getLogger(NominatimGeocoderImpl.class);
    private final String BASE_URL = "http://open.mapquestapi.com/nominatim/v1/search.php";
    private CoordinateBounds _resultBiasingBounds = null;

    public void setResultBiasingBounds(CoordinateBounds bounds) {
        _resultBiasingBounds = bounds;
    }

    @Override
    public List<NycGeocoderResult> nycGeocode(String location) {
        try {
            List<NycGeocoderResult> results = new ArrayList<NycGeocoderResult>();

            String requestUrl = BASE_URL + "?format=json&q=" + URLEncoder.encode(location, "UTF-8");

            if (_resultBiasingBounds != null) {
                requestUrl = requestUrl + "&bounded=1&bounds="
                        + URLEncoder.encode(_resultBiasingBounds.getMinLon() + ","
                        + _resultBiasingBounds.getMaxLat() + ","
                        + _resultBiasingBounds.getMaxLon() + ","
                        + _resultBiasingBounds.getMinLat(), "UTF-8");
            }

            JsonParser parser = new JsonParser();
            JsonArray response = (JsonArray) parser.parse(new InputStreamReader(new URL(requestUrl).openStream()));

            for (JsonElement e : response) {
                JsonObject geocodedPlace = e.getAsJsonObject();

                NominatimGeocoderResult geocoderResult = new NominatimGeocoderResult();

                geocoderResult.setFormattedAddress(geocodedPlace.get("display_name").getAsString());

                if (geocodedPlace.has("address") && geocodedPlace.get("address").getAsJsonObject().has("neighborhood")) {
                    geocoderResult.setNeighborhood(geocodedPlace.get("address").getAsJsonObject().get("neighborhood").getAsString());
                }

                geocoderResult.setLatitude(geocodedPlace.get("lat").getAsDouble());
                geocoderResult.setLongitude(geocodedPlace.get("lon").getAsDouble());

                JsonArray boundingBox = geocodedPlace.get("boundingbox").getAsJsonArray();

                geocoderResult.setSouthwestLatitude(boundingBox.get(0).getAsDouble());
                geocoderResult.setNortheastLatitude(boundingBox.get(1).getAsDouble());
                geocoderResult.setSouthwestLongitude(boundingBox.get(2).getAsDouble());
                geocoderResult.setNortheastLongitude(boundingBox.get(3).getAsDouble());

                results.add(geocoderResult);

            }

            results = filterResultsByWktPolygon(results);

            return results;
        } catch (Exception e) {
            _log.error("Geocoding error", e);
            return null;
        }
    }
}
