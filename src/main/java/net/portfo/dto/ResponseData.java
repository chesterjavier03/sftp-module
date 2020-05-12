package net.portfo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;

/**
 * Created by chesterjavier on 5/12/20.
 */
@Getter
@Setter
@JsonPropertyOrder({"data", "error"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {
    private JSONObject data;
    private String error;
}
