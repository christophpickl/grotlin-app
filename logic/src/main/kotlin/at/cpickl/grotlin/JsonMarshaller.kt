package at.cpickl.grotlin

import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.JsonProcessingException
import org.slf4j.LoggerFactory


public class JsonMarshaller {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<JsonMarshaller>())
    }

    private val mapper = ObjectMapper()

    public fun <T> fromJson(jsonString: String, type: Class<T>): T {
        try {
            return mapper.readValue(jsonString, type)
        } catch(e: JsonProcessingException) {
            LOG.error("Invalid JSON: '{}'", jsonString)
            throw e
        }
    }

    public fun toJson(entity: Any): String {
        return mapper.writeValueAsString(entity)
    }

}