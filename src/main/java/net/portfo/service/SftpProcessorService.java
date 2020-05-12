package net.portfo.service;

import lombok.extern.slf4j.Slf4j;
import net.portfo.dto.ResponseData;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static net.portfo.utility.Constant.*;
import static net.portfo.utility.RemoteFileDownloader.fetchRemoteFilesToBeUploaded;

/**
 * Created by chesterjavier on 5/12/20.
 */
@Service
@Slf4j
public class SftpProcessorService {

    @Value("${remote.server.private.key}")
    private String PRIVATE_KEY;
    @Value("${remote.server.sftp.upload.url}")
    private String SFTP_UPLOAD_URL;
    @Value("${remote.server.sftp.config.username}")
    private String SFTP_USERNAME_CONFIG;
    @Value("${remote.server.sftp.config.password}")
    private String SFTP_PASSWORD_CONFIG;
    @Value("${remote.server.sftp.config.port}")
    private String SFTP_PORT_CONFIG;
    @Value("${remote.server.sftp.config.host}")
    private String SFTP_HOST_CONFIG;
    @Value("${remote.server.sftp.config.directory}")
    private String SFTP_DIRECTORY_CONFIG;
    @Value("${memory.directory.path}")
    private String TMP_DIRECTORY;

    public ResponseData uploadToSpecifiedRemoteServer() {

        RestTemplate restTemplate = new RestTemplate();

        File[] remoteFiles = fetchRemoteFilesToBeUploaded();

        ResponseData responseData = new ResponseData();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        LinkedMultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();

        multipartRequest.add(JSON, constructSftpConfig());
        for (File f : remoteFiles) {
            multipartRequest.add(FILES, new FileSystemResource(f));
        }

        fetchExternalPrivateKey(multipartRequest);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest,
                headers);
        ResponseEntity<String> response;
        try {

            response = restTemplate.postForEntity(SFTP_UPLOAD_URL, requestEntity, String.class);

            JSONObject json = new JSONObject();
            json.put(MESSAGE, response.getBody());
            responseData.setData(json);
        } catch (HttpClientErrorException e) {
            log.error(ERROR, e.getRawStatusCode() + " - " + e.getStatusCode().getReasonPhrase());
            log.error(ERROR, e.getLocalizedMessage());
            responseData.setError(e.getRawStatusCode() + " - " + e.getStatusCode().getReasonPhrase());
        }

        return responseData;
    }

    private void fetchExternalPrivateKey(LinkedMultiValueMap<String, Object> multipartRequest) {
        try {
            FileUtils.writeByteArrayToFile(new File(System.getProperty(TMP_DIRECTORY) + ID_RSA), Files.readAllBytes(Paths.get(PRIVATE_KEY)));
            multipartRequest.add(PRIV_KEY, new FileSystemResource(new File(System.getProperty(TMP_DIRECTORY) + ID_RSA)));
        } catch (IOException e) {
            log.error(ERROR, e.getLocalizedMessage());
        }
    }

    private JSONObject constructSftpConfig() {
        JSONObject config = new JSONObject();
        config.put("username", SFTP_USERNAME_CONFIG);
        config.put("port", SFTP_PORT_CONFIG);
        config.put("host", SFTP_HOST_CONFIG);
        config.put("directory", SFTP_DIRECTORY_CONFIG);
        if (StringUtils.isBlank(SFTP_PASSWORD_CONFIG)) {
            config.put("password", SFTP_PASSWORD_CONFIG);
        }
        return config;
    }
}
