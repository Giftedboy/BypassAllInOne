package extension.core;

import burp.api.montoya.http.message.HttpRequestResponse;
import extension.config.Type;

import java.util.List;

public class Bypass {
    List<HttpRequestResponse> RequestResponses;
    Type BypassType;
    public Bypass(List<HttpRequestResponse> requestResponses, Type bypassType){
        RequestResponses = requestResponses;
        BypassType = bypassType;
    }
    public void Scan(){

    }
}
