/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.openmessaging.benchmark.utils.payload;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads a file containing a top-level JSON array and returns one payload per array element. Each
 * element is re-serialized as compact JSON (UTF-8).
 */
public class JsonArrayPayloadReader {

    private static final ObjectMapper mapper = new ObjectMapper();

    public List<byte[]> load(String resourceName) {
        try {
            JsonNode root = mapper.readTree(new File(resourceName));
            if (root == null || !root.isArray() || root.size() == 0) {
                throw new PayloadException(resourceName + " must contain a non-empty JSON array");
            }
            List<byte[]> payloads = new ArrayList<>(root.size());
            for (JsonNode element : root) {
                payloads.add(mapper.writeValueAsString(element).getBytes(StandardCharsets.UTF_8));
            }
            return payloads;
        } catch (IOException e) {
            throw new PayloadException(e.getMessage());
        }
    }
}
