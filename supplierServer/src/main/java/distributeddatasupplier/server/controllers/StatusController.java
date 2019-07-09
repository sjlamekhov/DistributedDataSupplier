package distributeddatasupplier.server.controllers;

import distributeddatasupplier.server.services.status.ServerStatus;
import distributeddatasupplier.server.services.status.ServerStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @Autowired
    private ServerStatusService serverStatusService;

    @RequestMapping(value = "/status",
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ServerStatus getServerStatus() {
        return serverStatusService.getServerStatus();
    }

}
