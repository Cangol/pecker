package mobi.cangol.web.pecker.core.service;

import mobi.cangol.web.pecker.core.model.Apk;
import mobi.cangol.web.pecker.core.model.AppServer;
import mobi.cangol.web.pecker.utils.TimeUtils;
import jcifs.smb.SmbFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.smb.session.SmbSession;
import org.springframework.integration.smb.session.SmbSessionFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class SmbfileService extends RemoteService{

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private SmbSession smbSession;

    private SmbSession getSmbSession(AppServer appServer) {
        if(smbSession==null){
            SmbSessionFactory smbSessionFactory = new SmbSessionFactory();
            smbSessionFactory.setHost(appServer.getHost());
            smbSessionFactory.setPort(appServer.getPort());
            smbSessionFactory.setDomain(appServer.getDomain());
            smbSessionFactory.setUsername(appServer.getUsername());
            smbSessionFactory.setPassword(appServer.getPassword());
            smbSessionFactory.setShareAndDir("/");
            smbSession= smbSessionFactory.getSession();
        }
        return smbSession;
    }

    public List<Apk> listFile(AppServer appServer, String dir, Long appId, String category, List<Apk> apks) throws IOException {
        log.debug("listFile dir={},appId={},category={}",dir,appId,category);
        SmbSession smbSession = getSmbSession(appServer);
        if (smbSession.isOpen()) {
            log.info("isOpen");
            if (smbSession.exists(dir)) {
                SmbFile[] list = smbSession.list(dir);
                for (SmbFile smbFile : list) {
                    apks.addAll(listFile(smbFile, appId, category));
                }
            }
        }
        return apks;
    }

    private List<Apk> listFile(SmbFile smbFile,  Long appId, String category) throws IOException {
        List<Apk> apks = new ArrayList<>();
        if (smbFile.isDirectory()) {
            for (SmbFile file : smbFile.listFiles()) {
                if (file.isFile() && (file.getName().toLowerCase().contains(".apk") || file.getName().toLowerCase().contains(".ipa"))) {
                    apks.add(toApkFile(file, appId, category));
                }
            }
        } else {
            if (smbFile.isFile() && (smbFile.getName().toLowerCase().contains(".apk") || smbFile.getName().toLowerCase().contains(".ipa"))) {
                apks.add(toApkFile(smbFile, appId, category));
            }
        }
        return apks;
    }

    private Apk toApkFile(SmbFile smbFile, Long appId, String category) {
        Apk apk = new Apk();
        apk.setAgent(smbFile.getName().toLowerCase().contains(".apk") ? "android" : "iOS");
        apk.setName(smbFile.getName());
        apk.setCategory(category);
        //apkFile.setGit(path[3].substring(1));
        apk.setDate(TimeUtils.formatYmdHms(smbFile.getDate()));
        try {
            apk.setPath(smbFile.getParent().split("/")[smbFile.getParent().split("/").length - 1]);
            if (apk.getPath().split("_").length > 3)
                apk.setFlag(apk.getPath().split("_")[3]);
            apk.setVersion(apk.getPath().split("_")[0]);
            apk.setVersion(apk.getVersion().replace("V", ""));
            String url=getHost() + "/apk/download?appId=" + appId + "&category=" + apk.getCategory() + "&agent=" + apk.getAgent() + "&path=" + apk.getPath() + "&name=" + apk.getName();
            if (apk.getAgent().toLowerCase().equals("ios")) {
                String queryUrl = "/apk/plist?appId=" + appId + "&category=" + apk.getCategory() + "&agent=" + apk.getAgent() + "&path=" + apk.getPath() + "&name=" + apk.getName() + "&version=" + apk.getVersion()
                        +"&link="+ URLEncoder.encode(url, "UTF-8");//
                String plistUrl=getHost() +URLEncoder.encode(queryUrl, "UTF-8");
                log.debug("plistUrl="+getHost()+queryUrl);
                apk.setUrl("itms-services://?action=download-manifest&url=" + plistUrl);
            } else {
                apk.setUrl(url);
            }
            apk.setImage(URLEncoder.encode(apk.getUrl(),"utf-8"));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return apk;
    }

    public InputStream getFileInputStream(AppServer appServer, String filePath) throws IOException {
        log.debug("getFileInputStream="+filePath);
        SmbFile file= getSmbSession(appServer).createSmbFileObject(filePath);
        return  file.getInputStream();
    }
}
