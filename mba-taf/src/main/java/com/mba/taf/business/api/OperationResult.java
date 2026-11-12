package com.mba.taf.business.api;

public record OperationResult(int httpStatus, String successMessage, String errorMessage, String redirectPath) {

    public boolean isSuccess() {
        return httpStatus == 302 && !successMessage.isBlank() && errorMessage.isBlank();
    }

    public boolean isBusinessError() {
        return httpStatus == 302 && !errorMessage.isBlank();
    }
}
