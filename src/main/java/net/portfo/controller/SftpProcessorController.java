package net.portfo.controller;

import lombok.extern.slf4j.Slf4j;
import net.portfo.dto.BaseResponse;
import net.portfo.dto.ResponseData;
import net.portfo.service.SftpProcessorService;
import net.portfo.utility.Constant;
import net.portfo.utility.ResponseUtility;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Created by chesterjavier on 5/12/20.
 */
@RestController
@Slf4j
@RequestMapping(value = "/download")
public class SftpProcessorController {
    @Autowired
    private SftpProcessorService sftpProcessorService;

    @GetMapping(path = "/hit", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> downloadHit() {
        final ResponseData responseData = sftpProcessorService.uploadToSpecifiedRemoteServer();
        JSONObject responseResult = (JSONObject) JSONValue.parse(responseData.getData().get("message").toString());
        return Optional.ofNullable(responseData.getData())
                .map(result -> new ResponseEntity<>(new BaseResponse(Integer.valueOf(
                        String.valueOf(responseResult.get("status"))),
                        responseResult.get("message").toString()), HttpStatus.OK))
                .orElse(ResponseUtility.response(Constant.FAIL, responseData.getError()));
    }
}
