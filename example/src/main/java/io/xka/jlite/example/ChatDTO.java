package io.xka.jlite.example;

import java.util.List;

public class ChatDTO {

    public static class Message {
        private String role = "user";
        private String content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    private String model = "gpt-3.5-turbo";

    private List<Message> messages;

    private String id;

    private String object;

    private String created;


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
