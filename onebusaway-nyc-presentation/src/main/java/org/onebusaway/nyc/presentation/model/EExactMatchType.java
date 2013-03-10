/*
 * Copyright 2013 Kurt Raschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onebusaway.nyc.presentation.model;

/**
 * Enum for exact match query types.
 * 
 * Exact route matches will be prefixed with "route:", while exact stop matches
 * will be prefixed with "stop:".
 * 
 * @author kurt
 */
public enum EExactMatchType {
    NONE,
    ROUTE,
    STOP
}