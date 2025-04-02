package com.nashm.ultimate.ultimate_frisbee_ai.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Query request")
public class QueryRequest {

    @Schema(description = "The question about Ultimate Frisbee",
            example = "What are the basic rules of Ultimate Frisbee?")
    private String query;

    // Getters and setters
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
