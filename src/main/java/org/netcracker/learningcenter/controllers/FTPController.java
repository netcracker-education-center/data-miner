package org.netcracker.learningcenter.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.netcracker.learningcenter.services.FTPService;
import org.netcracker.learningcenter.utils.FTPFileData;
import org.netcracker.learningcenter.utils.FtpClient;
import org.netcracker.learningcenter.filter.DateFilter;
import org.netcracker.learningcenter.filter.ExtensionFilter;
import org.netcracker.learningcenter.utils.Validations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@RestController
public class FTPController {
    private final FTPService service;
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String PORT = "port";
    private static final String SERVER = "server";
    private static final String PATHTODIR = "pathToDir";
    private static final String DATEFILTER = "dateFilter";
    private static final String EXTENSIONFILTER = "extensionFilter";


    @Autowired
    public FTPController(FTPService service) {
        this.service = service;
    }

    @PostMapping(value = "/read", produces = "application/json", consumes = "application/json")
    public List<FTPFileData> readFilesFromFtp(@RequestBody JsonNode jsonNode) throws Exception {
        JsonNode jLogin = jsonNode.path(LOGIN);
        JsonNode jPassword = jsonNode.path(PASSWORD);
        JsonNode jPort = jsonNode.path(PORT);
        JsonNode jServer = jsonNode.path(SERVER);
        JsonNode jPath = jsonNode.path(PATHTODIR);
        JsonNode dateFilter = jsonNode.path(DATEFILTER);
        JsonNode extensionFilter = jsonNode.path(EXTENSIONFILTER);
        Validations.checkJsonNode(jLogin, jPassword, jPort, jServer, jPath);

        List<FTPFileFilter> filters = new ArrayList<>();
        if (!dateFilter.isMissingNode()) {
            filters.add(new DateFilter(dateFilter.asText()));
        }
        if (!extensionFilter.isMissingNode()) {
            List<String> extensions = new ArrayList<>();
            Iterator<JsonNode> extFilter = extensionFilter.elements();
            while (extFilter.hasNext()) {
                extensions.add(extFilter.next().asText());
            }
            filters.add(new ExtensionFilter(extensions));
        }
        FtpClient client = new FtpClient(jServer.asText(), jPort.asInt(), jLogin.asText(), jPassword.asText());
        return service.getDataFromFiles(client, jPath.asText(), filters);
    }
}
