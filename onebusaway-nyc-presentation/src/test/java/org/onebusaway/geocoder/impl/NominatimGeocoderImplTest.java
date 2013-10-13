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
package org.onebusaway.geocoder.impl;

import java.util.List;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.onebusaway.nyc.geocoder.impl.NominatimGeocoderImpl;
import org.onebusaway.nyc.geocoder.service.NycGeocoderResult;

/**
 *
 * @author kurt
 */
public class NominatimGeocoderImplTest {

    @Test
    public void testGeocoding() {
        NominatimGeocoderImpl geocoder = new NominatimGeocoderImpl();

        List<NycGeocoderResult> results = geocoder.nycGeocode("silver spring, maryland");

        assertFalse(results.isEmpty());
    }
}
