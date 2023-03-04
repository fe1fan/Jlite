package io.xka.jlite.example.chatgpt;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class BO {

    @JsonProperty("id")
    @JSONField(name = "id")
    private String id;
    @JsonProperty("object")
    private String object;
    @JsonProperty("created")
    private Integer created;
    @JsonProperty("model")
    private String model;
    @JsonProperty("choices")
    private List<ChoicesDTO> choices;

    @NoArgsConstructor
    @Data
    public static class ChoicesDTO {

        private String role;

        @JsonProperty("delta")
        private DeltaDTO delta;
        @JsonProperty("index")
        private Integer index;
        @JsonProperty("finish_reason")
        @JSONField(name = "finish_reason")
        private Object finishReason;

        @NoArgsConstructor
        @Data
        public static class DeltaDTO {
            @JsonProperty("content")
            private String content;
        }
    }
}
