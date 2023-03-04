package io.xka.jlite.example.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VO {


    @Builder.Default
    private String model = "gpt-3.5-turbo";
    private List<MessagesDTO> messages;

    @Builder.Default
    private boolean stream = true;

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessagesDTO {
        @Builder.Default
        private String role = "user";
        @com.fasterxml.jackson.annotation.JsonProperty("content")
        private String content;
    }

    //add message
    public void addMessage(String role, String content) {
        MessagesDTO dto = new MessagesDTO();
        dto.setRole(role);
        dto.setContent(content);
        if (this.messages == null)
            this.messages = new java.util.ArrayList<>();
        this.messages.add(dto);
    }
}
