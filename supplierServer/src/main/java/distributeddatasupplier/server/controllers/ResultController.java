package distributeddatasupplier.server.controllers;

import distributeddatasupplier.server.services.ResultService;
import objects.Result;
import objects.ResultUri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResultController {

    @Autowired
    private ResultService resultService;

    @RequestMapping(value = "/tenants/{tenantId}/results/{resultId}",
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Result getResultById(@PathVariable("tenantId") String tenantId,
                                @PathVariable("resultId") String resultId) {
        try {
            return resultService.getByUri(new ResultUri(resultId, tenantId));
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

}
