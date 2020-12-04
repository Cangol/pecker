package mobi.cangol.web.pecker.core.service;

import mobi.cangol.web.pecker.utils.HttpUtils;
import mobi.cangol.web.pecker.core.api.ApiService;
import mobi.cangol.web.pecker.core.model.Apk;
import mobi.cangol.web.pecker.core.model.AppServer;
import mobi.cangol.web.pecker.core.model.SeaFile;
import mobi.cangol.web.pecker.core.model.SeaToken;
import mobi.cangol.web.pecker.utils.TimeUtils;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

@Service
public class SeafileService extends RemoteService{

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AppServerService appServerService;
    @Override
    public List<Apk> listFile(AppServer appServer, String dir, Long appId, String category, List<Apk> apks) throws IOException {
        log.debug("listFile "+dir);
        if(StringUtils.isEmpty(appServer.getToken())){
            SeaToken seaToken=  HttpUtils.execute(HttpUtils.getApiService(appServer.getHost(), ApiService.class).getAuthToken(appServer.getUsername(), appServer.getPassword()));
            appServer.setToken("Token "+seaToken.getToken());
            appServerService.save(appServer);
        }
        List<SeaFile> list = HttpUtils.execute(HttpUtils.getApiService(appServer.getHost(), ApiService.class).getDirFiles(appServer.getToken(), appServer.getRepo(),dir));
        for (SeaFile seaFile : list) {
            if ("dir".equalsIgnoreCase(seaFile.getType())) {
                listFile(appServer,dir+"/"+seaFile.getName(),appId,category, apks);
            } else {
                if ("file".equalsIgnoreCase(seaFile.getType())&& (seaFile.getName().toLowerCase().contains(".apk") || seaFile.getName().toLowerCase().contains(".ipa"))) {
                    apks.add(toApkFile(appServer,dir,seaFile, appId, category));
                }
            }
        }
        return apks;
    }

    public Apk toApkFile(AppServer appServer, String dir, SeaFile seaFile,  Long appId, String category) {
        Apk apk = new Apk();
        apk.setAgent(seaFile.getName().toLowerCase().contains(".apk") ? "android" : "iOS");
        apk.setName(seaFile.getName());
        apk.setCategory(category);
        apk.setDate(TimeUtils.formatYmdHms(seaFile.getMtime()*1000));
        try {
            apk.setPath(dir.split("/")[dir.split("/").length - 1]);
            if (apk.getPath().split("_").length > 3)
                apk.setFlag(apk.getPath().split("_")[3]);
            apk.setVersion(apk.getPath().split("_")[0]);
            apk.setVersion(apk.getVersion().replace("V", ""));
            String url=getHost()+"/apk/download?appId="+appId+"&agent="+ apk.getAgent()+"&category="+ apk.getCategory()+"&name="+seaFile.getName()+"&path="+ apk.getPath();
            if (apk.getAgent().toLowerCase().equals("ios")) {
                String queryUrl = "/apk/plist?appId="+appId+"&agent="+ apk.getAgent()+"&version=" + apk.getVersion()+"&link="+ URLEncoder.encode(url, "UTF-8");
                String plistUrl=getHost() +URLEncoder.encode(queryUrl, "UTF-8");
                log.debug("plistUrl="+getHost()+queryUrl);
                apk.setUrl("itms-services://?action=download-manifest&url="+plistUrl);
            } else {
                apk.setUrl(url);
            }
            log.debug("download="+ apk.getUrl());
            apk.setImage(URLEncoder.encode(apk.getUrl(),"utf-8"));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return apk;
    }
    @Override
    public InputStream getFileInputStream(AppServer appServer, String filePath) throws IOException {
        log.debug("getFileInputStream="+filePath);
        String url = HttpUtils.execute(HttpUtils.getApiService(appServer.getHost(), ApiService.class).getFileDownloadUrl(appServer.getToken(), appServer.getRepo(),filePath));
        Request.Builder builder=new Request.Builder()
                .get()
                .url(url);
        Response response= HttpUtils.getUnSafeOkHttp().newCall(builder.build()).execute();
        return response.body().byteStream();
    }
}
