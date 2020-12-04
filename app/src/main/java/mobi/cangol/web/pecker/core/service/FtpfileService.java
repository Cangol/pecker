package mobi.cangol.web.pecker.core.service;

import mobi.cangol.web.pecker.core.model.Apk;
import mobi.cangol.web.pecker.core.model.AppServer;
import mobi.cangol.web.pecker.utils.TimeUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;


@Service
public class FtpfileService extends RemoteService{
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private FTPClient ftpClient;
    private FTPClient getFTPClient(AppServer appServer) throws IOException {
        if(ftpClient==null){
            ftpClient=new FTPClient();
            ftpClient.connect(appServer.getHost(),appServer.getPort());
            ftpClient.login(appServer.getUsername(), appServer.getPassword());
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        }
        return ftpClient;
    }
    @Override
    public List<Apk> listFile(AppServer appServer, String dir, Long appId, String category, List<Apk> apks) throws IOException {
        log.debug("listFile="+dir);
        FTPClient ftpClient=getFTPClient(appServer);
        FTPFile[] files = ftpClient.listFiles(new String(dir.getBytes(), ftpClient.getControlEncoding()));
        for (FTPFile ftpFile : files) {
            if (!".".equals(ftpFile.getName())&&!"..".equals(ftpFile.getName())&&ftpFile.isDirectory()) {
                log.debug("dir="+new String(ftpFile.getName().getBytes(ftpClient.getControlEncoding())));
                listFile(appServer,dir+"/"+ftpFile.getName(),appId,category, apks);
            } else {
                if (ftpFile.isFile()&& (ftpFile.getName().toLowerCase().contains(".apk") || ftpFile.getName().toLowerCase().contains(".ipa"))) {
                    apks.add(toApkFile(ftpFile, dir,appId, category));
                }
            }
        }
        return apks;
    }
    @Override
    public InputStream getFileInputStream(AppServer appServer, String filePath) throws IOException {
        log.debug("getFileInputStream "+filePath);
        return getFTPClient(appServer).retrieveFileStream(new String(filePath.getBytes(), ftpClient.getControlEncoding()));
    }

    private Apk toApkFile(FTPFile ftpFile, String dir, Long appId, String category) {
        Apk apk = new Apk();
        apk.setAgent(ftpFile.getName().toLowerCase().contains(".apk") ? "android" : "iOS");
        apk.setName(ftpFile.getName());
        apk.setCategory(category);
        apk.setDate(TimeUtils.formatYmdHms(ftpFile.getTimestamp().getTimeInMillis()));
        try {
            apk.setPath(dir.split("/")[dir.split("/").length - 1]);
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
}
