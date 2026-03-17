package com.seodong.portfolio.common.dto;

public record SimpleResponse(boolean success) {
    public static SimpleResponse ok() {
        return new SimpleResponse(true);
    }
}
