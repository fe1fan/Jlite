package io.xka.jlite.web.serv.control.http;

public enum HttpContentType {
    MULTIPART_FORM_DATA("multipart/form-data"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_X_MPEGURL("application/x-mpegURL"),
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    TEXT_HTML("text/html"),
    TEXT_PLAIN("text/plain"),
    TEXT_XML("text/xml"),
    TEXT_EVENT_STREAM("text/event-stream"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_GIF("image/gif"),
    IMAGE_BMP("image/bmp"),
    IMAGE_TIFF("image/tiff"),
    IMAGE_SVG("image/svg+xml"),
    IMAGE_WEBP("image/webp"),
    AUDIO_MPEG("audio/mpeg"),
    AUDIO_OGG("audio/ogg"),
    AUDIO_WAV("audio/wav"),
    AUDIO_WEBM("audio/webm"),
    AUDIO_FLAC("audio/flac"),
    VIDEO_MPEG("video/mpeg"),
    VIDEO_OGG("video/ogg"),
    VIDEO_WEBM("video/webm"),
    VIDEO_MP4("video/mp4"),
    VIDEO_FLV("video/x-flv"),
    VIDEO_MKV("video/x-matroska"),
    VIDEO_AVI("video/x-msvideo"),
    VIDEO_WMV("video/x-ms-wmv"),
    VIDEO_MOV("video/quicktime");

    private final String name;

    HttpContentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
