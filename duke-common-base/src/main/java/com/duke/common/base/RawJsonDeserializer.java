package com.duke.common.base;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class RawJsonDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        /*
         * [方案一][推荐]
         * 输入的JSON字符串解析为JSON对象(JsonNode)，再输出为JSON字符串，相当于对JSON字符串进行了格式化
         */
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode node = mapper.readTree(parser);
        return mapper.writeValueAsString(node);
    	/**
    	 * [方案二]
    	 * 如果希望保存原始字符串内容,空格,tab,换行，则采用如下方式,不解析为JSON对象直接将原字符串返回
    	 */
        //long begin = jp.getCurrentLocation().getCharOffset();
        //jp.skipChildren();
        //long end = jp.getCurrentLocation().getCharOffset();
        //String json = jp.getCurrentLocation().getSourceRef().toString();
        //return json.substring((int) begin - 1, (int) end);
    }
}
