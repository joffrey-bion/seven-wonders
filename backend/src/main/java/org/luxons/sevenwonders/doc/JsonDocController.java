package org.luxons.sevenwonders.doc;

import java.util.Arrays;
import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.JSONDoc;
import org.jsondoc.core.pojo.JSONDoc.MethodDisplay;
import org.jsondoc.core.scanner.JSONDocScanner;
import org.luxons.sevenwonders.doc.scanner.JsonDocWebSocketScanner;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(name = "SevenWonders API Documentation",
     description = "This controller provides a JSON description of this documentation")
@Controller
public class JsonDocController {

    private String version;

    private String basePath;

    private List<String> packages;

    private JSONDocScanner jsondocScanner;

    private boolean playgroundEnabled = true;

    private MethodDisplay displayMethodAs = MethodDisplay.URI;

    public JsonDocController() {
        this.version = "1.0.0";
        this.basePath = "http://localhost:8080";
        this.packages = Arrays.asList("org.luxons.sevenwonders.controllers", "org.luxons.sevenwonders.doc",
                "org.luxons.sevenwonders.actions", "org.luxons.sevenwonders.game");
        this.jsondocScanner = new JsonDocWebSocketScanner();
    }

    @ApiMethod(description = "Get the Websocket API documentation for this game")
    @RequestMapping(value = "/doc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    JSONDoc getApi() {
        return jsondocScanner.getJSONDoc(version, basePath, packages, playgroundEnabled, displayMethodAs);
    }
}
