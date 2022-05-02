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
package io.catenax.semantics.hub.domain;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;

public class ModelPackageUrn {
   private final String urn;

   public ModelPackageUrn( String urn ) {
      this.urn = urn;
   }

   public String getUrn() {
      return urn;
   }

   public static ModelPackageUrn fromUrn( AspectModelUrn aspectModelUrn ) {
      return new ModelPackageUrn( aspectModelUrn.getUrnPrefix() );
   }

   public static ModelPackageUrn fromUrn( String urn ) {
      return new ModelPackageUrn( urn );
   }
}