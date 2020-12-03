package mobi.cangol.web.pecker.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class UniqId {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private static UniqId me = new UniqId();
    private String hostAddr;
    private final Random random = new SecureRandom();
    private MessageDigest mHasher;
    private final UniqTimer timer = new UniqTimer();

    private final ReentrantLock opLock = new ReentrantLock();


    private UniqId() {
        try {
            final InetAddress addr = InetAddress.getLocalHost();

            hostAddr = addr.getHostAddress();
        }
        catch (final IOException e) {
            log.error("[UniqID] Get HostAddr Error", e);
            hostAddr = String.valueOf(System.currentTimeMillis());
        }

        if (null == hostAddr || hostAddr.trim().length() == 0 || "127.0.0.1".equals(hostAddr)) {
            hostAddr = String.valueOf(System.currentTimeMillis());
        }

        if (log.isDebugEnabled()) {
            log.debug("[UniqID]hostAddr is:{0}", hostAddr);
        }

        try {
            mHasher = MessageDigest.getInstance("MD5");
        }
        catch (final NoSuchAlgorithmException nex) {
            mHasher = null;
            log.error("[UniqID]new MD5 Hasher error", nex);
        }
    }


    /**
     * 获取UniqID实例
     *
     * @return UniqId
     */
    public static UniqId getInstance() {
        return me;
    }

    /**
     * 获得不会重复的毫秒数
     *
     * @return 不会重复的时间
     */
    public long getUniqTime() {
        return timer.getCurrentTime();
    }


    /**
     * 获得UniqId
     *
     * @return uniqTime-randomNum-hostAddr-threadId
     */
    public String getUniqID() {
        final StringBuilder sb = new StringBuilder();
        final long t = timer.getCurrentTime();

        sb.append(t);

        sb.append("-");

        sb.append(random.nextInt(8999) + 1000);

        sb.append("-");
        sb.append(hostAddr);

        sb.append("-");
        sb.append(Thread.currentThread().hashCode());

        if (log.isDebugEnabled()) {
            log.debug("[UniqID.getUniqID]{0}",sb.toString());
        }

        return sb.toString();
    }


    /**
     * 获取MD5之后的uniqId string
     *
     * @return uniqId md5 string
     */
    public String getUniqIDHashString() {
        return this.hashString(this.getUniqID());
    }


    /**
     * 获取MD5之后的uniqId
     *
     * @return uniqId md5 byte[16]
     */
    public byte[] getUniqIDHash() {
        return this.hash(this.getUniqID());
    }


    /**
     * 对字符串进行md5
     *
     * @param str
     * @return md5 byte[16]
     */
    public byte[] hash(final String str) {
        opLock.lock();
        try {
            final byte[] bt = mHasher.digest(str.getBytes());
            if (null == bt || bt.length != 16) {
                throw new IllegalArgumentException("md5 need");
            }
            return bt;
        }
        finally {
            opLock.unlock();
        }
    }


    /**
     * 对字符串进行md5 string
     *
     * @param str
     * @return md5 string
     */
    public String hashString(final String str) {
        final byte[] bt = this.hash(str);
        return this.bytes2string(bt);
    }


    /**
     * 将一个字节数组转化为可见的字符串
     *
     * @param bt
     * @return 每个字节两位，如f1d2
     */
    public String bytes2string(final byte[] bt) {
        final int l = bt.length;

        final char[] out = new char[l << 1];

        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = digits[(0xF0 & bt[i]) >>> 4];
            out[j++] = digits[0x0F & bt[i]];
        }
        if (log.isDebugEnabled()) {
            log.debug("[UniqID.hash]" + (new String(out)));
        }
        return new String(out);
    }

    /**
     * 实现不重复的时间
     *
     * @author dogun
     */
    private class UniqTimer {
        private final AtomicLong lastTime = new AtomicLong(System.currentTimeMillis());


        public long getCurrentTime() {
            return this.lastTime.incrementAndGet();
        }
    }
}
