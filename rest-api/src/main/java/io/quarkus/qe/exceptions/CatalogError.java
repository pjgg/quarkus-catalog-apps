package io.quarkus.qe.exceptions;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "CatalogError")
public class CatalogError {

    @Schema(name = "code", description = "Unique error code.")
    private int code;

    @Schema(name = "msg", description = "Error message.")
    private String msg;

    private CatalogError(Builder builder) {
        this.code = builder.code;
        this.msg = builder.msg;
    }

    public CatalogError() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class Builder {

        private int code;
        private String msg;

        public Builder() {
        }

        public Builder withCode(int code) {
            this.code = code;
            return this;
        }

        public Builder withMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public CatalogError build() {
            CatalogError catalogError = new CatalogError(this);
            return catalogError;
        }
    }
}
