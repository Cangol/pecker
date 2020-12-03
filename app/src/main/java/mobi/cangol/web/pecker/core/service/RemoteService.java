package mobi.cangol.web.pecker.core.service;

import mobi.cangol.web.pecker.core.model.Apk;
import mobi.cangol.web.pecker.core.model.AppServer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public abstract  class RemoteService {

    abstract public List<Apk> listFile(AppServer appServer, String dir, Long appId, String category, List<Apk> apks) throws IOException;

    abstract public InputStream getFileInputStream(AppServer appServer, String filePath) throws IOException;

    protected String getHost() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getScheme() + "://" + request.getServerName();
    }
}
