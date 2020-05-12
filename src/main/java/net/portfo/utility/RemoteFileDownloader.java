package net.portfo.utility;

import lombok.extern.slf4j.Slf4j;
import net.portfo.dto.ResponseData;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static net.portfo.utility.Constant.E6;
import static net.portfo.utility.Constant.ERROR;

/**
 * Created by chesterjavier on 5/12/20.
 */
@Component
@Slf4j
public class RemoteFileDownloader {

    private static String PPI_NEW_BUSINESS_URL;
    private static String PPI_UPDATED_URL;
    private static String PPI_ENDED_BUSINESS_URL;
    private static String TOKEN;
    private static String TMP_DIRECTORY;

    @Value("${remote.server.ppi.new.business.url}")
    public void setPpiNewBusinessUrl(String ppiNewBusinessUrl) {
        PPI_NEW_BUSINESS_URL = ppiNewBusinessUrl;
    }

    @Value("${remote.server.ppi.updated.url}")
    public void setPpiUpdatedUrl(String ppiUpdatedUrl) {
        PPI_UPDATED_URL = ppiUpdatedUrl;
    }

    @Value("${remote.server.ppi.ended.business.url}")
    public void setPpiEndedBusinessUrl(String ppiEndedBusinessUrl) {
        PPI_ENDED_BUSINESS_URL = ppiEndedBusinessUrl;
    }

    @Value("${cronClientHash}")
    public void setToken(String token) {
        TOKEN = token;
    }

    @Value("${memory.directory.path}")
    public void setTmpDirectory(String tmpDirectory) {
        TMP_DIRECTORY = tmpDirectory;
    }

    public static File[] fetchRemoteFilesToBeUploaded() {
        ResponseData responseData = new ResponseData();
        RestTemplate template = new RestTemplate();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);
        template.setRequestFactory(requestFactory);

        List<File> files = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();

        multipartRequest.add(E6, "1");
        multipartRequest.add(Constant.TOKEN, TOKEN);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest,
                headers);
        try {
            ResponseEntity<String> ppiNewBusiness = template.postForEntity(PPI_NEW_BUSINESS_URL, requestEntity, String.class);
            files.add(FileUtils.getFile(System.getProperty(TMP_DIRECTORY), ppiNewBusiness.getBody()));

            ResponseEntity<String> ppiUpdated = template.postForEntity(PPI_UPDATED_URL, requestEntity, String.class);
            files.add(FileUtils.getFile(System.getProperty(TMP_DIRECTORY), ppiUpdated.getBody()));

            ResponseEntity<String> ppiEndedBusiness = template.postForEntity(PPI_ENDED_BUSINESS_URL, requestEntity, String.class);
            files.add(FileUtils.getFile(System.getProperty(TMP_DIRECTORY), ppiEndedBusiness.getBody()));

        } catch (HttpClientErrorException e) {
            log.error(ERROR, e.getRawStatusCode() + " - " + e.getStatusCode().getReasonPhrase());
            responseData.setError(e.getRawStatusCode() + " - " + e.getStatusCode().getReasonPhrase());
        }
        return files.toArray(new File[files.size()]);
    }
}
