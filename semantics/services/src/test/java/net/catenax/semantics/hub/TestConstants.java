/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.catenax.semantics.hub;

public class TestConstants {

   private static final String MODELS_ROOT_PATH = "net/catenax/semantics/hub/persistence/models/";
   public static final String TRACEABILITY_MODEL_PATH = MODELS_ROOT_PATH + "Traceability.ttl";
   public static final String MODEL_WITH_REFERENCE_TO_TRACEABILITY_MODEL_PATH =
         MODELS_ROOT_PATH + "ModelWithReferenceToTraceability.ttl";
   public static final String PRODUCT_USAGE_MODEL_PATH = MODELS_ROOT_PATH + "ProductUsage.ttl";
   public static final String PRODUCT_USAGE_DETAIL_MODEL_PATH = MODELS_ROOT_PATH + "ProductUsageDetail.ttl";

   public static final String VEHICLE_WITH_NOT_AVAILABLE_EXTERNAL_REFERENCE =
         MODELS_ROOT_PATH + "VehicleWithNotAvailableExternalReference.ttl";
}
