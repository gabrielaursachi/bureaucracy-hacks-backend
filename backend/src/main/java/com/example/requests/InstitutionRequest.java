package com.example.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class InstitutionRequest {
    private JSONArray institutions;

    @JsonCreator
    public InstitutionRequest(JSONArray institutions) {
        this.institutions = institutions;
    }
}
