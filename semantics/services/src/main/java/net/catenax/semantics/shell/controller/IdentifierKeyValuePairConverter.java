package net.catenax.semantics.shell.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.catenax.semantics.aas.registry.model.IdentifierKeyValuePair;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This converter is required so that Spring is able to convert query parameters to custom objects.
 */
@Component
public class IdentifierKeyValuePairConverter implements Converter<String, List<IdentifierKeyValuePair>> {

    private ObjectMapper objectMapper;

    IdentifierKeyValuePairConverter(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @Override
    public List<IdentifierKeyValuePair> convert(String source) {
        try {
            return objectMapper.readValue(source, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
