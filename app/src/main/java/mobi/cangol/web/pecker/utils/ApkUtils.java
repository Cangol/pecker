package mobi.cangol.web.pecker.utils;

import mobi.cangol.web.pecker.core.model.Apk;
import mobi.cangol.web.pecker.core.model.App;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ApkUtils {
    public static String getPathByApp(App app, String category) {
        String share = null;
        if (category != null && category.toLowerCase().equals("release")) {
            share = app.getRelease();
        } else if (category != null && category.toLowerCase().equals("trial")) {
            share = app.getTrial();
        } else {
            share = app.getAlpha();
        }
        return share;
    }
    public static List<Apk> filter(List<Apk> list, String version, String date, String git, String flag) {
        List<Apk> result = new ArrayList<>();
        for (Apk apk : list) {
            if ((StringUtils.isEmpty(version) || StringUtils.equals(apk.getVersion(), version))
                    && (StringUtils.isEmpty(date) || apk.getDate().startsWith(date))
                    && (StringUtils.isEmpty(git) || StringUtils.equals(apk.getGit(), git))
                    && (StringUtils.isEmpty(flag) || StringUtils.equals(apk.getFlag(), flag))) {
                result.add(apk);
            }
        }
        Collections.sort(result, Comparator.comparing(Apk::getDate));
        Collections.reverse(result);
        return result;
    }

    public static List<String> getVersions(List<Apk> list) {
        List<String> result = new ArrayList<>();
        for (Apk apk : list) {
            if (!result.contains(apk.getVersion())) {
                result.add(apk.getVersion());
            }
        }
        Collections.sort(result);
        Collections.reverse(result);
        result.add(0, "");
        return result;
    }

    public static List<String> getDates(List<Apk> list) {
        List<String> result = new ArrayList<>();
        for (Apk apk : list) {
            if (!result.contains(apk.getDate().split(" ")[0])) {
                result.add(apk.getDate().split(" ")[0]);
            }
        }
        Collections.sort(result);
        Collections.reverse(result);
        result.add(0, "");
        return result;
    }

    public static List<String> getGits(List<Apk> list) {
        List<String> result = new ArrayList<>();
        for (Apk apk : list) {
            if (!result.contains(apk.getGit())) {
                result.add(apk.getGit());
            }
        }
        Collections.sort(result);
        Collections.reverse(result);
        result.add(0, "");
        return result;
    }

    public static List<String> getFlags(List<Apk> list) {
        List<String> result = new ArrayList<>();
        for (Apk apk : list) {
            if (!result.contains(apk.getFlag())) {
                result.add(apk.getFlag());
            }
        }
        Collections.sort(result, (o1, o2) -> {
            if (o1 != null && o2 != null) {
                return o1.compareTo(o2);
            } else if (o1 != null) {
                return 1;
            } else {
                return -1;
            }
        });
        Collections.reverse(result);
        result.add(0, "");
        return result;
    }
}
