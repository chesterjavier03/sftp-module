package net.portfo.utility;

import net.portfo.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by chesterjavier on 5/12/20.
 */
public class ResponseUtility {

    public static final ResponseEntity response(Integer status, String message) {
        return new ResponseEntity<>(new BaseResponse(status, message), HttpStatus.OK);
    }
}
