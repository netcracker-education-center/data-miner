package org.netcracker.learningcenter.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.netcracker.learningcenter.exceptions.ResourceNotFoundException;

public class Validations {
    public static void checkJsonNode(JsonNode... jsonNodes) throws ResourceNotFoundException {
        for (JsonNode node : jsonNodes) {
            if (node.isMissingNode()) {
                throw new ResourceNotFoundException("Mandatory field is missing");
            }
        }
    }
}
