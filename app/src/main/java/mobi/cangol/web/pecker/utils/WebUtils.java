package mobi.cangol.web.pecker.utils;

import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class WebUtils {

    public static String filterAgent(String agent,HttpServletRequest request) {
        if (StringUtils.isEmpty(agent)) {
            String userAgent = getOSType(request.getHeader("User-Agent"));
            if ("IPhone".equals(userAgent) || "Mac OS".equals(userAgent)) {
                agent = "iOS";
            } else {
                agent = "android";
            }
        } else {
            if (agent.toLowerCase().equals("ios")
                    || agent.toLowerCase().equals("iphone")
                    || agent.toLowerCase().equals("mac os")) {
                agent = "iOS";
            } else {
                agent = "android";
            }
        }
        return agent;
    }

    public static String getOSType(String userAgent) {
        if (userAgent == null) return "UNKNOWN";
        String os = "";
        String agent = userAgent.toLowerCase();
        if (agent.indexOf("win") > -1) {
            if (agent.indexOf("windows 95") > -1 || agent.indexOf("win95") > -1) {
                os = "Windows 95";
            }
            if (agent.indexOf("windows 98") > -1 || agent.indexOf("win98") > -1) {
                os = "Windows 98";
            }
            if (agent.indexOf("windows nt") > -1
                    || agent.indexOf("winnt") > -1) {
                os = "Windows NT";
            }
            if (agent.indexOf("winxp") > -1
                    || agent.indexOf("windows xp") > -1) {
                os = "Windows XP";
            }
        } else if (agent.indexOf("mac") > -1) {
            os = "Mac OS";
        } else if (agent.indexOf("android") > -1) {
            os = "Android";
        } else if (agent.indexOf("iphone") > -1) {
            os = "IPhone";
        } else {
            os = "UNKNOWN";
        }
        return os;
    }

    public static String getHost(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName();
    }

    public static boolean isWechat(HttpServletRequest request) {
        return  request.getHeader("user-agent")
                .toLowerCase().indexOf("micromessenger") > 0;
    }
    public static String outputPlist(String identifier,String name,String version, String url) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<dict>\n" +
                "\t<key>items</key>\n" +
                "\t<array>\n" +
                "\t\t<dict>\n" +
                "\t\t\t<key>assets</key>\n" +
                "\t\t\t<array>\n" +
                "\t\t\t\t<dict>\n" +
                "\t\t\t\t\t<key>kind</key>\n" +
                "\t\t\t\t\t<string>software-package</string>\n" +
                "\t\t\t\t\t<key>url</key>\n" +
                "\t\t\t\t\t<string>" + url + "</string>\n" +
                "\t\t\t\t</dict>\n" +
                "\t\t\t\t<dict>\n" +
                "\t\t\t\t\t<key>kind</key>\n" +
                "\t\t\t\t\t<string>display-image</string>\n" +
                "\t\t\t\t\t<key>url</key>\n" +
                "\t\t\t\t\t<string></string>\n" +
                "\t\t\t\t</dict>\n" +
                "\t\t\t\t<dict>\n" +
                "\t\t\t\t\t<key>kind</key>\n" +
                "\t\t\t\t\t<string>full-size-image</string>\n" +
                "\t\t\t\t\t<key>url</key>\n" +
                "\t\t\t\t\t<string></string>\n" +
                "\t\t\t\t</dict>\n" +
                "\t\t\t</array>\n" +
                "\t\t\t<key>metadata</key>\n" +
                "\t\t\t<dict>\n" +
                "\t\t\t\t<key>bundle-identifier</key>\n" +
                "\t\t\t\t<string>" + identifier + "</string>\n" +
                "\t\t\t\t<key>bundle-version</key>\n" +
                "\t\t\t\t<string>" + version + "</string>\n" +
                "\t\t\t\t<key>kind</key>\n" +
                "\t\t\t\t<string>software</string>\n" +
                "\t\t\t\t<key>title</key>\n" +
                "\t\t\t\t<string>" + name + "</string>\n" +
                "\t\t\t</dict>\n" +
                "\t\t</dict>\n" +
                "\t</array>\n" +
                "</dict>\n" +
                "</plist>\n";
    }
}
