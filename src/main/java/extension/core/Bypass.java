package extension.core;

import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.ContentType;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import extension.BypassExtension;
import extension.config.Type;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static burp.api.montoya.http.message.ContentType.*;
import static extension.config.Config.*;

public class Bypass {
    List<HttpRequestResponse> RequestResponses;
    Type BypassType;
    public Bypass(List<HttpRequestResponse> requestResponses, Type bypassType){
        RequestResponses = requestResponses;
        BypassType = bypassType;
    }
    public void Scan(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (BypassType){
                    case Bypass40X -> Bypass40XScan();
                    case BypassCORS -> BypassCORScan();
                    case BypassCSRF -> BypassCSRFScan();
                    case BypassSSRF -> BypassSSRFScan();
                }
            }
        }).start();

    }

    private HttpRequestResponse SendRequest(HttpRequest Request){
        return BypassExtension.Api.http().sendRequest(Request);
    }

    public void Bypass40XScan(){

        for(HttpRequestResponse RequestResponse:RequestResponses){
            HttpRequest Request = RequestResponse.request();
            short OldStatusCode = RequestResponse.statusCode();
            if(ChangeRequestMethod(Request,OldStatusCode) || AddHeaders(Request,OldStatusCode) || AddFixedHeaders(Request,OldStatusCode) || ChangePaths(Request,OldStatusCode)){return;}
//                    boolean MethodResult =  ChangeRequestMethod(Request,OldStatusCode);
//                    boolean PathsResult = ChangePaths(Request,OldStatusCode);
//                    boolean HeaderResult = AddHeaders(Request,OldStatusCode);
//                    boolean FixedHeader = AddFixedHeaders(Request,OldStatusCode);
        }
    }
    private boolean ChangeRequestMethod(HttpRequest Request,short OldStatusCode){
        if(Objects.equals(Request.method(), "GET")){
            Request = Request.withMethod("POST");
        }else {
            //POST改成GET需要特殊处理，post参数需要变为query
            ContentType ContentType = Request.contentType();
            //目前只做a=b&c=d的类型，json后面再写
            if(ContentType == URL_ENCODED){
                String Body = Request.bodyToString();
                String Path = Request.path();
                if(Path.contains("?")){
                    Path = Path + Body;
                }else{
                    Path = Path + "?" + Body;
                }
                Request = Request.withPath(Path);
            } else if (ContentType == JSON) {
                //暂时不做处理吧，还需要解析json，比较麻烦

            }else{

            }

            Request = Request.withMethod("GET");
        }

        short NewStatusCode = SendRequest(Request).statusCode();

        if(NewStatusCode < 300){
            //发送到 Repeater
            BypassExtension.Api.repeater().sendToRepeater(Request,"403Bypass");
            return true;
        }else{
            return false;
        }


//        return OldStatusCode != NewStatusCode && NewStatusCode < 300;
    }
    private boolean ChangePaths(HttpRequest Request,short OldStatusCode){

        String OriginPath = "";
        String Query= "";
        try{
            //剔除开头结尾的 / 符号
            URL url = new URL(Request.url());
            OriginPath = url.getPath().substring(1);
            if(url.getQuery().length()!=0){
                Query = "?" + url.getQuery();
            }
            if (OriginPath.endsWith("/")){
                OriginPath = OriginPath.substring(0,OriginPath.length()-1);
            }

        }catch(MalformedURLException e){
            BypassExtension.Api.logging().logToOutput(e.getMessage());
            return false;
        }
        boolean IsRootPath = (OriginPath.length()==0);
        for(String PathRuler:ForbiddenPaths){
            String NewPath = PathRuler.replace("$path",OriginPath).replace("$query",Query);
            if(IsRootPath){
                // 根路径需要特殊处理一下
                NewPath = NewPath.replaceAll("//","/");
            }

            Request = Request.withPath(NewPath);

            BypassExtension.Api.logging().logToOutput(Request.path());
            short NewStatusCode = SendRequest(Request).statusCode();
            //如果bypass了则直接return
            if(NewStatusCode < 300){
                //发送到 Repeater
                BypassExtension.Api.repeater().sendToRepeater(Request,"403Bypass");
                return true;
            }else{
                return false;
            }

        }
        //额外再加一个特殊逻辑 /a/b/c/../c 这种
        {
            String NewPath;
            if (IsRootPath) {
                NewPath = "/a/../";
            } else {
                String[] v = OriginPath.split("/");
                String last = v[v.length - 1];

                NewPath = "/" + OriginPath + "/../" + last;
            }
            Request = Request.withPath(NewPath);
            BypassExtension.Api.logging().logToOutput(Request.path());
            short NewStatusCode = SendRequest(Request).statusCode();
            //如果bypass了则直接return
            if(NewStatusCode < 300){
                //发送到 Repeater
                BypassExtension.Api.repeater().sendToRepeater(Request,"403Bypass");
                return true;
            }else{
                return false;
            }
        }
    }
    private boolean AddHeaders(HttpRequest Request,short OldStatusCode){
        Random r=new Random();
        int n = ForbiddenHeaderValues.length;
        for(String ForbiddenHeader:ForbiddenHeaders){
            Request.withAddedHeader(ForbiddenHeader,ForbiddenHeaderValues[r.nextInt(n)]);
        }
        short NewStatusCode = SendRequest(Request).statusCode();
        if(NewStatusCode < 300){
            //发送到 Repeater
            BypassExtension.Api.repeater().sendToRepeater(Request,"403Bypass");
            return true;
        }else{
            return false;
        }
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
        if(NewStatusCode < 300){
            //发送到 Repeater
            BypassExtension.Api.repeater().sendToRepeater(Request,"403Bypass");
            return true;
        }else{
            return false;
        }
    }

    public void BypassCORScan(){
        List<String> SimpleMethod = new ArrayList<>(Arrays.asList("GET","POST","HEAD"));
        for(HttpRequestResponse RequestResponse:RequestResponses){
            HttpRequest Request = RequestResponse.request();
            String Method = Request.method();

            HttpService RequestService = Request.httpService();

            Request = Request.withRemovedHeader("Origin").withRemovedHeader("Referer").withRemovedHeader("Sec-Fetch-Mode").withRemovedHeader("Sec-Fetch-Site").withRemovedHeader("Sec-Fetch-Dest");
            //简单请求
            if(SimpleMethod.contains(Method)){
                Request = Request.withAddedHeader("Sec-Fetch-Mode","cors").
                        withAddedHeader("Sec-Fetch-Site","cross-site").
                        withAddedHeader("Sec-Fetch-Dest","empty");
                CheckCORS(Request,0);
            }else{
                //复杂请求，put等，使用OPTION方法，【理论上来说简单请求也可以走这个逻辑】
                HttpRequest NewRequest = HttpRequest.httpRequest();
                NewRequest = NewRequest.withService(RequestService).
                        withMethod("OPTIONS").
                        withPath(Request.path()).
                        withAddedHeader("Host",RequestService.host()).
                        withAddedHeader("Access-Control-Request-Method",Request.method()).
                        withAddedHeader("Access-Control-Request-Headers","any").
                        withAddedHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.5615.50 Safari/537.36").
                        withAddedHeader("Sec-Fetch-Mode","cors").
                        withAddedHeader("Sec-Fetch-Site","cross-site").
                        withAddedHeader("Sec-Fetch-Dest","empty")
                ;
                CheckCORS(NewRequest,1);
            }
        }
    }
    private boolean CheckCORS(HttpRequest Request,int type){
        //当请求头中除了cookie还有其他代表身份的字段的话，则cors即使绕过了也无法利用

        String Host = Request.httpService().host();

        // null，可通过 iframe sandbox 伪造
        /*
        <iframe sandbox="allow-scripts allow-top-navigation allow-forms" src='data:text/html,<script>
        var xhr=new XMLHttpRequest();
        xhr.onreadystatechange = function() {
        if (xhr.readyState == XMLHttpRequest.DONE) {
                alert(xhr.responseText);
            }
        }
        xhr.open("GET", "http://www.vuln.com/a.php", true);
        xhr.withCredentials = true;
        xhr.send();</script>'>
        </iframe>
        */
        if(CheckCORSResponse(Request,"null",type)){
            return true;
        };

        // 开头
        String NewHost = "https://" + Host + "1ndex.cn";
        if(CheckCORSResponse(Request,NewHost,type)){
            return true;
        };
        // 结尾
        NewHost = "https://" + "1ndex" + Host;
        if(CheckCORSResponse(Request,NewHost,type)){
            return true;
        };
        // .未转义
        NewHost = "https://" + Host.replaceAll("\\.","");
        if(CheckCORSResponse(Request,NewHost,type)){
            return true;
        };
        //子域名就算了，还要xss
        return false;
    }
    private boolean CheckCORSResponse(HttpRequest Request,String Host,int type){
        Request = Request.withAddedHeader("Origin",Host).withAddedHeader("Referer",Host);

        HttpRequestResponse NewRequestResponse = SendRequest(Request);

        BypassExtension.Api.logging().logToOutput(NewRequestResponse.request().toString());
        BypassExtension.Api.logging().logToOutput(NewRequestResponse.response().toString());

        List<HttpHeader> ResponseHeaders = NewRequestResponse.response().headers();
        Map<String,String> HeaderMaps = new HashMap<>();
        for(HttpHeader Header:ResponseHeaders){
            HeaderMaps.put(Header.name(),Header.value());
        }
        String OriginValue = HeaderMaps.getOrDefault("Access-Control-Allow-Origin","");
        String CredentialsValue = HeaderMaps.getOrDefault("Access-Control-Allow-Credentials","");
        String MethodsValue = HeaderMaps.getOrDefault("Access-Control-Allow-Methods","");
        String HeadersValue = HeaderMaps.getOrDefault("Access-Control-Allow-Headers","");
        String MaxAgeValue = HeaderMaps.getOrDefault("Access-Control-Max-Age","");

        //如果设置*是游览器将不会发送cookies，即使你的XHR设置了withCredentials，所以不匹配
        if(type==0 && Objects.equals(OriginValue, Host) && Objects.equals(CredentialsValue, "true")){
            BypassExtension.Api.repeater().sendToRepeater(Request,"CORSBypass");
            return true;
        }else if (type==1 && Objects.equals(OriginValue, Host) && Objects.equals(CredentialsValue, "true") && !Objects.equals(MethodsValue, "") && !Objects.equals(HeadersValue, "") && !Objects.equals(MaxAgeValue, "")){
            BypassExtension.Api.repeater().sendToRepeater(Request,"CORSBypass");
            return true;
        }else{
            return false;
        }

    }

    public void BypassCSRFScan(){

    }
    public void BypassSSRFScan(){

    }


}
