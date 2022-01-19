/*
 * Copyright (c) 2022 Bosch Software Innovations GmbH. All rights reserved.
 */

package net.catenax.semantics.hub;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
public class ModelsApiFilterTest {
   @Autowired
   private MockMvc mvc;

   @BeforeAll
   public void init() throws Exception {
      createModel( TestConstants.TRACEABILITY_MODEL_PATH, "RELEASED" );
      createModel( TestConstants.MODEL_WITH_REFERENCE_TO_TRACEABILITY_MODEL_PATH, "DRAFT" );
   }

   private void createModel( String fileName, String status ) throws Exception {
      String modelWithReferenceToTraceability = loadModelFromResources( fileName );
      mvc.perform(
               MockMvcRequestBuilders
                     .post( "/api/v1/models" )
                     .accept( MediaType.APPLICATION_JSON )
                     .contentType( MediaType.APPLICATION_JSON )
                     .content( createNewModelRequestJson( modelWithReferenceToTraceability, status ) )
         )
         .andDo( MockMvcResultHandlers.print() )
         .andExpect( status().isOk() );
   }

   @Test
   public void testGetByNamespaceExpectFoundResults() throws Exception {
      mvc.perform(
               MockMvcRequestBuilders.get(
                                           "/api/v1/models?namespaceFilter=urn:bamm:com.catena" )
                                     .accept( MediaType.APPLICATION_JSON )
         )
         .andDo( MockMvcResultHandlers.print() )
         .andExpect( jsonPath( "$.items" ).isArray() )
         .andExpect( jsonPath( "$.items.length()" ).value( 2 ) )
         .andExpect( MockMvcResultMatchers.status().isOk() );

      mvc.perform(
               MockMvcRequestBuilders.get(
                                           "/api/v1/models?namespaceFilter=urn:bamm:com.catenax.traceability" )
                                     .accept( MediaType.APPLICATION_JSON )
         )
         .andDo( MockMvcResultHandlers.print() )
         .andExpect( jsonPath( "$.items" ).isArray() )
         .andExpect( jsonPath( "$.items.length()" ).value( 1 ) )
         .andExpect( MockMvcResultMatchers.status().isOk() );
   }

   @Test
   public void testGetModelListByNotAvailablePropertyTypeExpectEmptyResult() throws Exception {
      mvc.perform(
               MockMvcRequestBuilders.get(
                                           "/api/v1/models?nameType=bamm:SingleEntity&nameFilter=SpatialPositionCharacteristic" )
                                     .accept( MediaType.APPLICATION_JSON )
         )
         .andDo( MockMvcResultHandlers.print() )
         .andExpect( jsonPath( "$.items" ).isArray() )
         .andExpect( jsonPath( "$.items" ).isEmpty() )
         .andExpect( MockMvcResultMatchers.status().isOk() );
   }

   @Test
   public void testGetModelListByAvailablePropertyTypeExpectResultsFound() throws Exception {
      mvc.perform(
               MockMvcRequestBuilders.get(
                                           "/api/v1/models?nameType=bamm:Property&nameFilter=Static%20Data" )
                                     .accept( MediaType.APPLICATION_JSON )
         )
         .andDo( MockMvcResultHandlers.print() )
         .andExpect( jsonPath( "$.items" ).isArray() )
         .andExpect( jsonPath( "$.items.length()" ).value( 2 ) )
         .andExpect( MockMvcResultMatchers.status().isOk() );

      mvc.perform(
               MockMvcRequestBuilders.get(
                                           "/api/v1/models?nameType=bamm:Property&nameFilter=Individual%20Data" )
                                     .accept( MediaType.APPLICATION_JSON )
         )
         .andDo( MockMvcResultHandlers.print() )
         .andExpect( jsonPath( "$.items" ).isArray() )
         .andExpect( jsonPath( "$.items.length()" ).value( 1 ) )
         .andExpect( MockMvcResultMatchers.status().isOk() );
   }

   @Test
   public void testCombinedFilters() throws Exception {
      mvc.perform(
               MockMvcRequestBuilders.get(
                                           "/api/v1/models?namespaceFilter=urn:bamm:com.catenax.traceability&nameType=bamm:Property&nameFilter=Individual%20Data" )
                                     .accept( MediaType.APPLICATION_JSON )
         )
         .andDo( MockMvcResultHandlers.print() )
         .andExpect( jsonPath( "$.items" ).isArray() )
         .andExpect( jsonPath( "$.items.length()" ).value( 1 ) )
         .andExpect( MockMvcResultMatchers.status().isOk() );

      mvc.perform(
               MockMvcRequestBuilders.get(
                                           "/api/v1/models?namespaceFilter=urn:bamm:com.catenaX.modelwithreferencetotraceability&nameType=bamm:Property&nameFilter=Individual%20Data" )
                                     .accept( MediaType.APPLICATION_JSON )
         )
         .andDo( MockMvcResultHandlers.print() )
         .andExpect( jsonPath( "$.items" ).isArray() )
         .andExpect( jsonPath( "$.items.length()" ).value( 0 ) )
         .andExpect( MockMvcResultMatchers.status().isOk() );
   }

   @Test
   public void testGetModelListByDescriptionExpectSuccess() throws Exception {
      mvc.perform(
               MockMvcRequestBuilders.get(
                                           "/api/v1/models?nameType=_DESCRIPTION_&nameFilter=This%20model%20references" )
                                     .accept( MediaType.APPLICATION_JSON )
         )
         .andDo( MockMvcResultHandlers.print() )
         .andExpect( jsonPath( "$.items[*].urn", hasItem(
               "urn:bamm:com.catenaX.modelwithreferencetotraceability:0.1.1#ModelWithReferenceToTraceability" ) ) )
         .andExpect( jsonPath( "$.items[*].version", hasItem( "0.1.1" ) ) )
         .andExpect( jsonPath( "$.items[*].name", hasItem( "ModelWithReferenceToTraceability" ) ) )
         .andExpect( jsonPath( "$.items[*].type", hasItem( "BAMM" ) ) )
         .andExpect( jsonPath( "$.items[*].status", hasItem( "DRAFT" ) ) )
         .andExpect( MockMvcResultMatchers.status().isOk() );
   }

   @Test
   public void testGetModelListByStatusExpectSuccess() throws Exception {
      mvc.perform(
               MockMvcRequestBuilders.get( "/api/v1/models?status=DRAFT" )
                                     .accept( MediaType.APPLICATION_JSON )
         )
         .andDo( MockMvcResultHandlers.print() )
         .andExpect( jsonPath( "$.items" ).isArray() )
         .andExpect( jsonPath( "$.items[*].urn", hasItem(
               "urn:bamm:com.catenaX.modelwithreferencetotraceability:0.1.1#ModelWithReferenceToTraceability" ) ) )
         .andExpect( jsonPath( "$.items[*].version", hasItem( "0.1.1" ) ) )
         .andExpect( jsonPath( "$.items[*].name", hasItem( "ModelWithReferenceToTraceability" ) ) )
         .andExpect( jsonPath( "$.items[*].type", hasItem( "BAMM" ) ) )
         .andExpect( jsonPath( "$.items[*].status", hasItem( "DRAFT" ) ) )
         .andExpect( MockMvcResultMatchers.status().isOk() );

      mvc.perform(
               MockMvcRequestBuilders.get( "/api/v1/models?status=RELEASED" )
                                     .accept( MediaType.APPLICATION_JSON )
         )
         .andDo( MockMvcResultHandlers.print() )
         .andExpect( jsonPath( "$.items" ).isArray() )
         .andExpect( jsonPath( "$.items[*].urn", hasItem(
               "urn:bamm:com.catenax.traceability:0.1.1#Traceability" ) ) )
         .andExpect( jsonPath( "$.items[*].version", hasItem( "0.1.1" ) ) )
         .andExpect( jsonPath( "$.items[*].name", hasItem( "Traceability" ) ) )
         .andExpect( jsonPath( "$.items[*].type", hasItem( "BAMM" ) ) )
         .andExpect( jsonPath( "$.items[*].status", hasItem( "RELEASED" ) ) )
         .andExpect( MockMvcResultMatchers.status().isOk() );
   }

   private String loadModelFromResources( String resourceName ) throws IOException {
      return IOUtils.resourceToString( resourceName, StandardCharsets.UTF_8, getClass().getClassLoader() );
   }

   private String createNewModelRequestJson( String model, String status ) {
      return String.format( "{\n"
            + "  \"model\": \"%s\",\n"
            + "  \"status\": \"%s\",\n"
            + "  \"type\": \"BAMM\"\n"
            + "}", StringEscapeUtils.escapeJava( model ), status );
   }
}
