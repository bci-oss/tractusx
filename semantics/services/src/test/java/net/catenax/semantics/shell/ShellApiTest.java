package net.catenax.semantics.shell;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.catenax.semantics.hub.TestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ShellApiTest {

    private static final String SHELL_BASE_PATH = "/registry/shell-descriptors";
    private static final String SINGLE_SHELL_BASE_PATH = "/registry/shell-descriptors/{shellIdentifier}";
    private static final String SUB_MODEL_BASE_PATH = "/registry/shell-descriptors/{shellIdentifier}/submodel-descriptors";
    private static final String SINGLE_SUB_MODEL_BASE_PATH = "/registry/shell-descriptors/{shellIdentifier}/submodel-descriptors/{submodelIdentifier}";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testCreateShellExpectSuccess() throws Exception {
        ObjectNode shellPayload = createShell();
        performShellCreateRequest( toJson(shellPayload));

        ObjectNode onlyRequiredFieldsShell = createBaseIdPayload(uuid("external"), "exampleShortId");
        performShellCreateRequest( toJson(onlyRequiredFieldsShell));
    }

    @Test
    public void testGetShellExpectSuccess() throws Exception {
        ObjectNode shellPayload = createShell();
        performShellCreateRequest( toJson(shellPayload));
        String shellId =  getId(shellPayload);
        mvc.perform(
                        MockMvcRequestBuilders
                                .get( SINGLE_SHELL_BASE_PATH, shellId)
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect( status().isOk() )
                .andExpect(content().json(toJson(shellPayload)));
    }

    @Test
    public void testGetShellExpectNotFound() throws Exception {
        mvc.perform(
                        MockMvcRequestBuilders
                                .get( SINGLE_SHELL_BASE_PATH, "NotExistingShellId")
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect( status().isNotFound() );
    }

    @Test
    public void testGetAllShellsExpectSuccess() throws Exception {
        ObjectNode shellPayload = createShell();
        performShellCreateRequest( toJson(shellPayload));
        mvc.perform(
                        MockMvcRequestBuilders
                                .get( SHELL_BASE_PATH)
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.[*]" ).isArray() )
                // we expect at least on entry
                .andExpect( jsonPath( "$.[*]", hasSize(greaterThan(0)) ) );
    }

    @Test
    public void testUpdateShellExpectSuccess() throws Exception {
        ObjectNode shellPayload = createShell();
        performShellCreateRequest( toJson(shellPayload));

        ObjectNode updateDescription = shellPayload.deepCopy();
        updateDescription.set("description", emptyArrayNode()
                .add(createDescription("fr", "exampleFrtext")));
        String shellId =  updateDescription.get("identification").textValue();
        mvc.perform(
                        MockMvcRequestBuilders
                                .put( SINGLE_SHELL_BASE_PATH, shellId)
                                .accept( MediaType.APPLICATION_JSON )
                                .contentType( MediaType.APPLICATION_JSON )
                                .content( toJson(updateDescription) )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect( status().isNoContent() );

        mvc.perform(
                        MockMvcRequestBuilders
                                .get( SINGLE_SHELL_BASE_PATH, shellId)
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect( status().isOk() )
                .andExpect(content().json(toJson(updateDescription)));
    }


    @Test
    public void testUpdateShellExpectNotFound() throws Exception {
        mvc.perform(
                        MockMvcRequestBuilders
                                .put( SINGLE_SHELL_BASE_PATH, "shellIdthatdoesnotexists")
                                .accept( MediaType.APPLICATION_JSON )
                                .contentType( MediaType.APPLICATION_JSON )
                                .content( toJson(createShell()) )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect( status().isNotFound() )
                .andExpect(jsonPath("$.error.message", is("Shell for identifier shellIdthatdoesnotexists not found")));
    }

    @Test
    public void testUpdateShellWithDifferentIdInPayloadExpectPathIdIsTaken() throws Exception {
        ObjectNode shellPayload = createShell();
        performShellCreateRequest( toJson(shellPayload));
        String shellId = getId(shellPayload);

        // assigning a new identification to an existing shell must not be possible in an update
        ObjectNode updatedShell = shellPayload.deepCopy()
                .put("identification", "newIdInUpdateRequest")
                .put("idShort", "newIdShortInUpdateRequest");

        mvc.perform(
                        MockMvcRequestBuilders
                                .put( SINGLE_SHELL_BASE_PATH, shellId)
                                .accept( MediaType.APPLICATION_JSON )
                                .contentType( MediaType.APPLICATION_JSON )
                                .content( toJson(updatedShell) )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect( status().isNoContent() );

        // verify that anything expect the identification can be updated
        ObjectNode expectedShellAfterUpdate = updatedShell
                .deepCopy()
                .put("identification", shellId);
        mvc.perform(
                        MockMvcRequestBuilders
                                .get( SINGLE_SHELL_BASE_PATH, shellId)
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect( status().isOk() )
                .andExpect(content().json(toJson(expectedShellAfterUpdate)));
    }

    @Test
    public void testDeleteShellExpectSuccess() throws Exception {
        ObjectNode shellPayload = createShell();
        performShellCreateRequest( toJson(shellPayload));
        String shellId =  getId(shellPayload);
        mvc.perform(
                        MockMvcRequestBuilders
                                .delete( SINGLE_SHELL_BASE_PATH, shellId)
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect( status().isNoContent() );
    }

    @Test
    public void testDeleteShellExpectNotFound() throws Exception {
        ObjectNode shellPayload = createShell();
        performShellCreateRequest( toJson(shellPayload));
        String shellId =  getId(shellPayload);
        mvc.perform(
                        MockMvcRequestBuilders
                                .delete( SINGLE_SHELL_BASE_PATH, shellId)
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect( status().isNoContent() );
    }


    @Test
    public void testCreateSubmodelExpectSuccess() throws Exception {
        ObjectNode shellPayload = createShell();
        performShellCreateRequest( toJson(shellPayload));
        String shellId = getId(shellPayload);

        ObjectNode submodel = createSubmodel(uuid("submodelExample"));
        performSubmodelCreateRequest(toJson(submodel), shellId);

        mvc.perform(
                        MockMvcRequestBuilders
                                .get( SINGLE_SHELL_BASE_PATH, shellId)
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect(status().isOk())
                .andExpect( jsonPath( "$.submodelDescriptors" ,  hasSize(3) ))
                .andExpect(jsonPath("$.submodelDescriptors[*].identification", hasItem(getId(submodel))));
    }

    @Test
    public void testUpdateSubModelExpectSuccess() throws Exception {
        ObjectNode shellPayload = createShell();
        performShellCreateRequest( toJson(shellPayload));
        String shellId = getId(shellPayload);

        ObjectNode submodel = createSubmodel(uuid("submodelExample"));
        performSubmodelCreateRequest(toJson(submodel), shellId);
        String submodelId = getId(submodel);

        ObjectNode updatedSubmodel = submodel.deepCopy()
                .put("idShort", "updatedSubmodelId").set("description", emptyArrayNode()
                        .add(createDescription("es", "spanish description" )));

        mvc.perform(
                        MockMvcRequestBuilders
                                .put( SINGLE_SUB_MODEL_BASE_PATH, shellId, submodelId)
                                .accept( MediaType.APPLICATION_JSON )
                                .contentType( MediaType.APPLICATION_JSON )
                                .content( toJson(updatedSubmodel) )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect(status().isNoContent());

        mvc.perform(
                        MockMvcRequestBuilders
                                .get( SINGLE_SUB_MODEL_BASE_PATH, shellId, submodelId)
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(updatedSubmodel)));
    }

    @Test
    public void testUpdateSubmodelExpectNotFound() throws Exception {
        // verify shell is missing
        mvc.perform(
                        MockMvcRequestBuilders
                                .get( SINGLE_SUB_MODEL_BASE_PATH, "notexistingshell", "notexistingsubmodel")
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message", is("Shell for identifier notexistingshell not found")));


        ObjectNode shellPayload = createShell();
        performShellCreateRequest( toJson(shellPayload));
        String shellId = getId(shellPayload);
        // verify submodel is missing
        mvc.perform(
                        MockMvcRequestBuilders
                                .get( SINGLE_SUB_MODEL_BASE_PATH, shellId, "notexistingsubmodel")
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message", is("Submodel for identifier notexistingsubmodel not found.")));
    }

    @Test
    public void testUpdateSubmodelWithDifferentIdInPayloadExpectPathIdIsTaken() throws Exception {
        ObjectNode shellPayload = createShell();
        performShellCreateRequest( toJson(shellPayload));
        String shellId = getId(shellPayload);

        ObjectNode submodel = createSubmodel(uuid("submodelExample"));
        performSubmodelCreateRequest(toJson(submodel), shellId);
        String submodelId = getId(submodel);

        // assigning a new identification to an existing submodel must not be possible in an update
        ObjectNode updatedSubmodel = submodel.deepCopy()
                .put("identification", "newIdInUpdateRequest")
                .put("idShort", "newIdShortInUpdateRequest");

        mvc.perform(
                        MockMvcRequestBuilders
                                .put( SINGLE_SUB_MODEL_BASE_PATH, shellId, submodelId)
                                .accept( MediaType.APPLICATION_JSON )
                                .contentType( MediaType.APPLICATION_JSON )
                                .content( toJson(updatedSubmodel) )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect( status().isNoContent() );

        // verify that anything expect the identification can be updated
        ObjectNode expectedShellAfterUpdate = updatedSubmodel
                .deepCopy()
                .put("identification", submodelId);
        mvc.perform(
                        MockMvcRequestBuilders
                                .get( SINGLE_SUB_MODEL_BASE_PATH, shellId, submodelId)
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect( status().isOk() )
                .andExpect(content().json(toJson(expectedShellAfterUpdate)));
    }

    @Test
    public void testDeleteSubmodelExpectSuccess() throws Exception {

        ObjectNode shellPayload = createShell();
        performShellCreateRequest( toJson(shellPayload));
        String shellId = getId(shellPayload);

        ObjectNode submodel = createSubmodel(uuid("submodelExample"));
        performSubmodelCreateRequest(toJson(submodel), shellId);
        String submodelId = getId(submodel);

        mvc.perform(
                        MockMvcRequestBuilders
                                .delete( SINGLE_SUB_MODEL_BASE_PATH, shellId, submodelId)
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect(status().isNoContent());

        mvc.perform(
                        MockMvcRequestBuilders
                                .get( SINGLE_SUB_MODEL_BASE_PATH, shellId, submodelId)
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteSubmodelExpectNotFound() throws Exception {
        // verify shell is missing
        mvc.perform(
                        MockMvcRequestBuilders
                                .delete( SINGLE_SUB_MODEL_BASE_PATH, "notexistingshell", "notexistingsubmodel")
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message", is("Shell for identifier notexistingshell not found")));


        ObjectNode shellPayload = createShell();
        performShellCreateRequest( toJson(shellPayload));
        String shellId = getId(shellPayload);
        // verify submodel is missing
        mvc.perform(
                        MockMvcRequestBuilders
                                .delete( SINGLE_SUB_MODEL_BASE_PATH, shellId, "notexistingsubmodel")
                                .accept( MediaType.APPLICATION_JSON )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message", is("Submodel for identifier notexistingsubmodel not found.")));
    }

    private String getId(ObjectNode payload){
        return payload.get("identification").textValue();
    }

    private void performSubmodelCreateRequest(String payload, String shellIdentifier) throws Exception {
        mvc.perform(
                        MockMvcRequestBuilders
                                .post( SUB_MODEL_BASE_PATH, shellIdentifier  )
                                .accept( MediaType.APPLICATION_JSON )
                                .contentType( MediaType.APPLICATION_JSON )
                                .content( payload )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect( status().isOk() )
                .andExpect(content().json(payload));
    }

    private void performShellCreateRequest(String payload) throws Exception {
        mvc.perform(
                        MockMvcRequestBuilders
                                .post( SHELL_BASE_PATH )
                                .accept( MediaType.APPLICATION_JSON )
                                .contentType( MediaType.APPLICATION_JSON )
                                .content( payload )
                )
                .andDo( MockMvcResultHandlers.print() )
                .andExpect( status().isOk() )
                .andExpect(content().json(payload));
    }


    private ObjectNode createShell() throws JsonProcessingException {
        ObjectNode shellPayload = createBaseIdPayload(uuid("external"), "exampleShortId");
        shellPayload.set("description", emptyArrayNode()
                .add(createDescription("en", "this is an example description"))
                .add(createDescription("de", "das ist ein beispiel")));

        shellPayload.set("specificAssetIds", emptyArrayNode()
                .add(specificAssetId("vin1", "valueforvin1"))
                .add(specificAssetId("enginenumber1", "enginenumber1")));

        shellPayload.set("submodelDescriptors", emptyArrayNode()
                .add(createSubmodel(uuid("submodel_external1")))
                .add(createSubmodel(uuid("submodel_external2"))));
        return shellPayload;
    }

    private ObjectNode createSubmodel(String submodelId) throws JsonProcessingException {
        ObjectNode submodelPayload = createBaseIdPayload(submodelId, "exampleShortId");
        submodelPayload.set("description",  emptyArrayNode()
                .add(createDescription("en", "this is an example submodel description"))
                .add(createDescription("de", "das ist ein Beispiel submodel")));
        submodelPayload.set("endpoints", emptyArrayNode()
                .add(createEndpoint()));
        submodelPayload.set("semanticId", createSemanticId("urn:net.catenax.vehicle:1.0.0#Parts") );
        return submodelPayload;
    }

    private static String uuid(String prefix){
        return prefix + "_" + UUID.randomUUID();
    }



    private ArrayNode emptyArrayNode(){
        return mapper.createArrayNode();
    }

    private ObjectNode createBaseIdPayload(String identification, String idShort) throws JsonProcessingException {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("identification", identification );
        objectNode.put("idShort", idShort );
        return objectNode;
    }

    private ObjectNode createDescription(String language, String text){
        ObjectNode description = mapper.createObjectNode();
        description.put("language", language );
        description.put("text", text );
        return description;
    }

    private ObjectNode specificAssetId(String key, String value){
        ObjectNode specificAssetId = mapper.createObjectNode();
        specificAssetId.put("key", key );
        specificAssetId.put("value", value );
        return specificAssetId;
    }

    private ObjectNode createSemanticId(String value){
        ObjectNode semanticId = mapper.createObjectNode();
        semanticId.set("value", emptyArrayNode().add(value) );
        return semanticId;
    }

    private ObjectNode createEndpoint(){
        ObjectNode endpoint = mapper.createObjectNode();
        endpoint.put("interface", "interfaceName");
        endpoint.set("protocolInformation",  mapper.createObjectNode()
                .put("endpointAddress", "https://catena-xsubmodel-vechile.net/path")
                .put("endpointProtocol", "https")
                .put("subprotocol", "Mca1uf1")
                .put("subprotocolBody", "Mafz1")
                .put("subprotocolBodyEncoding", "Fj1092ufj")
        );
        return endpoint;
    }

    private String toJson(ObjectNode objectNode) throws JsonProcessingException {
        return mapper.writeValueAsString(objectNode);
    }

}
