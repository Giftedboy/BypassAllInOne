package extension.core;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import extension.BypassExtension;
import extension.config.Type;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static extension.config.Config.*;

public class Bypass {
    List<HttpRequestResponse> RequestResponses;
    Type BypassType;
    public Bypass(List<HttpRequestResponse> requestResponses, Type bypassType){
        RequestResponses = requestResponses;
        BypassType = bypassType;
    }
    public void Scan(){
        switch (BypassType){
            case Bypass40X -> Bypass40XScan();
            case BypassCORS -> BypassCORScan();
            case BypassCSRF -> BypassCSRFScan();
            case BypassSSRF -> BypassSSRFScan();
        }
    }

    private HttpRequestResponse SendRequest(HttpRequest Request){
        return BypassExtension.Api.http().sendRequest(Request);
    }

    public void Bypass40XScan(){
        for(HttpRequestResponse RequestResponse:RequestResponses){
            HttpRequest Request = RequestResponse.request();
            short OldStatusCode = RequestResponse.statusCode();
            boolean Result =  ChangeRequestMethod(Request,OldStatusCode);

        }

    }
    private boolean ChangeRequestMethod(HttpRequest Request,short OldStatusCode){
        if(Objects.equals(Request.method(), "GET")){
            Request = Request.withMethod("POST");
        }else {
            Request = Request.withMethod("GET");
        }

        short NewStatusCode = SendRequest(Request).statusCode();

        return OldStatusCode != NewStatusCode && NewStatusCode < 300;
    }
    private boolean ChangePaths(HttpRequest Request,short OldStatusCode){

        String OriginPath = "";
        try{
            //剔除开头结尾的 / 符号
            URL url = new URL(Request.url());
            OriginPath = url.getPath().substring(1);
            if (OriginPath.endsWith("/")){
                OriginPath = OriginPath.substring(0,OriginPath.length()-1);
            }

        }catch(MalformedURLException e){
            return false;
        }
        if(OriginPath.length()==0){
            //根路径得走特殊逻辑

            for(String PathRuler:ForbiddenPaths){

            }
            return false;

        }else{


            for(String PathRuler:ForbiddenPaths){

            }
            return false;
        }
    }
    private boolean AddHeaders(HttpRequest Request,short OldStatusCode){
        Random r=new Random();
        int n = ForbiddenHeaderValues.length;
        for(String ForbiddenHeader:ForbiddenHeaders){
            Request.withAddedHeader(ForbiddenHeader,ForbiddenHeaderValues[r.nextInt(n)]);
        }
        short NewStatusCode = SendRequest(Request).statusCode();
        return OldStatusCode != NewStatusCode && NewStatusCode < 300;
    }

    private boolean AddFixedHeaders(HttpRequest Request,short OldStatusCode){
        for(Map.Entry<String ,String> ForbiddenFixedHeader:ForbiddenFixedHeaders.entrySet()){
            String Key = ForbiddenFixedHeader.getKey();
            String Value = ForbiddenFixedHeader.getValue();
            if(Objects.equals(Key, "X-Rewrite-URL")){
                Value = Request.path();
            }
            Request = Request.withAddedHeader(Key,Value);
        }

        short NewStatusCode = SendRequest(Request).statusCode();
        return OldStatusCode != NewStatusCode && NewStatusCode < 300;
    }

    public void BypassCSRFScan(){

    }
    public void BypassSSRFScan(){

    }
    public void BypassCORScan(){

    }

}
