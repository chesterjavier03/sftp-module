package net.portfo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

/**
 * Created by chesterjavier on 5/12/20.
 */
@Data
@JsonPropertyOrder({"status", "payload", "message"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {

    protected Integer status;
    protected JSONObject payload;
    protected String message;

    public BaseResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public BaseResponse(Integer status, JSONObject payload) {
        this.status = status;
        this.payload = payload;
    }
}
