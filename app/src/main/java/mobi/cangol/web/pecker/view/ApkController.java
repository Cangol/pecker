package mobi.cangol.web.pecker.view;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import mobi.cangol.web.pecker.core.model.Apk;
import mobi.cangol.web.pecker.core.model.App;
import mobi.cangol.web.pecker.core.model.AppServer;
import mobi.cangol.web.pecker.core.service.*;
import mobi.cangol.web.pecker.utils.ApkUtils;
import mobi.cangol.web.pecker.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ApkController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AppService appService;
    @Autowired
    private AppServerService appServerService;
    @Autowired
    private SeafileService seafileService;
    @Autowired
    private SmbfileService smbfileService;
    @Autowired
    private FtpfileService ftpfileService;

    @GetMapping("/apk/list")
    public String apk(Long appId, String category, String agent, String version, String date, String flag, Model model, HttpServletRequest request) throws IOException {
        log.debug("apk appId={}，category={},version={},date={},flag={}",appId,category,version,date,flag);
        agent = WebUtils.filterAgent(agent,request);
        List<Apk> apks = new ArrayList<>();
        App app = appService.get(appId);
        AppServer appServer = appServerService.get(app.getServer());
        log.debug("appServer "+appServer);
        model.addAttribute("app", app);
        model.addAttribute("isWechat", WebUtils.isWechat(request));
        model.addAttribute("appId", appId);
        model.addAttribute("agent", agent);
        model.addAttribute("category", category);
        model.addAttribute("version", version);
        model.addAttribute("date", date);
        model.addAttribute("flag", flag);
        String path = ApkUtils.getPathByApp(app, category);
        listFile(appServer,path ,appId,category, apks);
        model.addAttribute("releases", ApkUtils.filter(apks, version, date, null, flag));
        model.addAttribute("versions", ApkUtils.getVersions(apks));
        model.addAttribute("dates", ApkUtils.getDates(apks));
        model.addAttribute("flags", ApkUtils.getFlags(apks));
        return "apk";
    }

    private List<Apk> listFile(AppServer appServer, String dir, Long appId, String category, List<Apk> apks) throws IOException {
        if("sea".equalsIgnoreCase(appServer.getType())){
            return seafileService.listFile(appServer,dir,appId,category,apks);
        }else if("smb".equalsIgnoreCase(appServer.getType())){
            if(dir.startsWith("/"))
                dir=dir.substring(1);
            return smbfileService.listFile(appServer,dir,appId,category,apks);
        }else if("ftp".equalsIgnoreCase(appServer.getType())){
            if(!dir.startsWith("."))
                dir="."+dir;
            return ftpfileService.listFile(appServer,dir,appId,category,apks);
        }
        return apks;
    }

    @ResponseBody
    @GetMapping(value = "/apk/plist", produces = "text/xml")
    public String plist(String link,String version,Long appId,String agent, HttpServletRequest request) throws IOException{
        log.debug("plist version={},link={},appId={},agent={}",version,link,appId,agent);
        agent = WebUtils.filterAgent(agent,request);
        App app = appService.get(appId);
        String ver = version.replace("V", "");
        ver = ver.substring(0, ver.lastIndexOf("."));
        return WebUtils.outputPlist(app.getIdentifier(),app.getName(),ver, link.replace("&", "&amp;"));
    }

    @GetMapping("/apk/image")
    public void image(String src,HttpServletRequest request, HttpServletResponse response) throws IOException, WriterException {
        String url= URLDecoder.decode(src,"utf-8");
        Map<EncodeHintType, Object> config = new HashMap<>();
        config.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        config.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        config.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 200, 200, config);
        MatrixToImageWriter.writeToStream(bitMatrix, "png", response.getOutputStream());
    }

    @GetMapping("/apk/download")
    public ResponseEntity<Resource> download(Long appId, String agent, String category, String path, String name, HttpServletRequest request) throws IOException {
        log.debug("download appId={},agent={},path={}",appId,agent,path);
        agent = WebUtils.filterAgent(agent,request);
        App app = appService.get(appId);
        AppServer appServer = appServerService.get(app.getServer());
        String dir = ApkUtils.getPathByApp(app, category)+"/"+path;
        InputStream inputStream =null;
        if("sea".equalsIgnoreCase(appServer.getType())){
            inputStream= seafileService.getFileInputStream(appServer,dir+"/"+name);
        }else if("smb".equalsIgnoreCase(appServer.getType())){
            if(dir.startsWith("/"))
                dir=dir.substring(1);
            inputStream= smbfileService.getFileInputStream(appServer,dir+"/"+name);
        }else if("ftp".equalsIgnoreCase(appServer.getType())){
            if(!dir.startsWith("."))
                dir="."+dir;
            inputStream= ftpfileService.getFileInputStream(appServer,dir+"/"+name);
        }

        if(inputStream!=null) {
            InputStreamResource resource = new InputStreamResource(inputStream);
            if (agent.equals("iOS")) {
                if (name.equals("manifest.plist")) {
                    log.info("file is manifest.plist");
                    return ResponseEntity.ok()
                            .header("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(name, "UTF-8"))
                            .contentType(MediaType.parseMediaType("text/xml"))
                            .body(resource);
                } else {
                    log.info("file is ipa");
                    return ResponseEntity.ok()
                            .header("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(name, "UTF-8"))
                            .contentType(MediaType.parseMediaType("application/octet-stream"))
                            .body(resource);
                }
            }else{
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(name, "UTF-8"))
                        .contentType(MediaType.parseMediaType("application/vnd.android.package-archive"))
                        .body(resource);
            }
        }else{
            return ResponseEntity.badRequest().build();
        }
    }
}

