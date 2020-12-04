package mobi.cangol.web.pecker.core.service;

import mobi.cangol.web.pecker.core.model.Apk;
import mobi.cangol.web.pecker.core.model.AppServer;
import mobi.cangol.web.pecker.core.model.SeaFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class GitlabService extends RemoteService{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppServerService appServerService;

    @Override
    public List<Apk> listFile(AppServer appServer, String dir, Long appId, String category, List<Apk> apks) throws IOException {
        log.debug("listFile "+dir);
        return apks;
    }

    public Apk toApkFile(AppServer appServer, String dir, SeaFile seaFile,  Long appId, String category) {
        Apk apk = new Apk();
        return apk;
    }

    @Override
    public InputStream getFileInputStream(AppServer appServer, String filePath) throws IOException {
        log.debug("getFileInputStream "+filePath);
        return null;
    }
}
