package com.ecom.productservice.dto;

public class NotificationDto {

    public static class NotificationRequest {
        private String type;
        private String title;
        private String message;
        private String recipient;
        private String metadata;

        public NotificationRequest() {}

        public NotificationRequest(String type, String title, String message, String recipient, String metadata) {
            this.type = type;
            this.title = title;
            this.message = message;
            this.recipient = recipient;
            this.metadata = metadata;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getRecipient() {
            return recipient;
        }

        public void setRecipient(String recipient) {
            this.recipient = recipient;
        }

        public String getMetadata() {
            return metadata;
        }

        public void setMetadata(String metadata) {
            this.metadata = metadata;
        }
    }
}
