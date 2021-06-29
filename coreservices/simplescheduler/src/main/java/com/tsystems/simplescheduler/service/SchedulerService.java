package com.tsystems.simplescheduler.service;

import com.tsystems.simplescheduler.client.DataspaceConnectorRequestClient;
import com.tsystems.simplescheduler.property.ResourcesProperties;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RepresentationInstance;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {
    private static final String CONNECTOR_URL_TEMPLATE = "%s/api/ids/data";
    private static final String RESOURCE_URI_TEMPLATE = "https://w3id.org/idsa/autogen/resource/%s";
    private static final String ALLOW_POLICY = "{\n  \"@context\" : {\n    \"ids\" : \"https://w3id.org/idsa/core/\"\n  },\n  \"@type\" : \"ids:ContractOffer\",\n  \"@id\" : \"https://w3id.org/idsa/autogen/contractOffer/3ca8ce5e-1487-4aaf-ac19-593298c27d18\",\n  \"ids:permission\" : [ {\n    \"@type\" : \"ids:Permission\",\n    \"@id\" : \"https://w3id.org/idsa/autogen/permission/2a87f23b-a6b4-4c2d-bfbb-d035cf2ccdbd\",\n    \"ids:title\" : [ {\n      \"@value\" : \"Example Usage Policy\",\n      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n    } ],\n    \"ids:action\" : [ {\n      \"@id\" : \"idsc:USE\"\n    } ],\n    \"ids:description\" : [ {\n      \"@value\" : \"provide-access\",\n      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n    } ]\n  } ]\n}";
    private static final String VALIDATION_LINE_NAME = "Validation: ";
    private static final String RESPONSE_LINE_NAME = "Response: ";

    private final Serializer serializer = new Serializer();

    private final DataspaceConnectorRequestClient requestClient;
    private final ResourcesProperties resourcesProperties;

    @Value("${scheduler.connector.base-url}")
    String connectorBaseUrl;

    @Scheduled(cron = "${scheduler.cron}")
    public void scheduledTask() {
        log.info("Scheduled task fired");
        resourcesProperties.getResources().forEach((resourceId, recipientConnectorBaseUrl) -> {
            var recipientConnectorUrl = String.format(CONNECTOR_URL_TEMPLATE, recipientConnectorBaseUrl);
            var formattedResource = String.format(RESOURCE_URI_TEMPLATE, resourceId);

            var metadataResponse = requestClient.requestMetadata(recipientConnectorUrl, formattedResource);
            var firstMetadataLine = metadataResponse.lines().findFirst().orElse(null);
            if (firstMetadataLine == null || !firstMetadataLine.contains(VALIDATION_LINE_NAME)) {
                log.error("Validation string is not present");
                return;
            }
            var validationKey = firstMetadataLine.split(VALIDATION_LINE_NAME)[1];
            var metadataJson = metadataResponse.lines()
                    .skip(1)
                    .map(line -> line.contains(RESPONSE_LINE_NAME) ? line.split(RESPONSE_LINE_NAME)[1] : line)
                    .collect(Collectors.joining("\n"));

            String artifactId;
            try {
                var resource = serializer.deserialize(metadataJson, Resource.class);
                artifactId = Optional.ofNullable(resource.getRepresentation())
                        .flatMap(representations -> representations.stream().findFirst())
                        .map(Representation::getInstance)
                        .flatMap(representationInstances -> representationInstances.stream().findFirst())
                        .map(RepresentationInstance::getId)
                        .map(URI::toString)
                        .orElse(null);
            } catch (IOException ex) {
                log.error("Got IOException while deserializing resource " + resourceId, ex);
                return;
            }

            if (artifactId == null) {
                log.error("ArtifactId for resource {} is null", resourceId);
                return;
            }
            var contractAgreement = requestClient.requestContract(recipientConnectorUrl, artifactId, ALLOW_POLICY);
            var artifactResponse = requestClient.requestArtifact(recipientConnectorUrl, artifactId,
                    validationKey, contractAgreement);
            if (artifactResponse == null) {
                log.error("Artifact response for resource {} is null", resourceId);
            }
            log.debug("Artifact response: {}", artifactResponse);
        });
    }
}
