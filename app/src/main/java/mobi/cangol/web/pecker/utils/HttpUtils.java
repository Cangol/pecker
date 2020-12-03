package mobi.cangol.web.pecker.utils;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;


public class HttpUtils {
    protected final static Logger log = LoggerFactory.getLogger(HttpUtils.class);

    public static OkHttpClient getOkHttp() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followRedirects(true)
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    public static OkHttpClient getUnSafeOkHttp() {
        SSLContext sslContext = null;
        SSLSocketFactory sslSocketFactory = null;
        X509TrustManager trustManager = new UnSafeTrustManager();
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            log.debug("getUnSafeOkHttp:" + e.getMessage());
        }

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .sslSocketFactory(sslSocketFactory, trustManager)
                .hostnameVerifier(new UnSafeHostnameVerifier())
                .build();
        return httpClient;
    }

    public static <T> T getApiService(String host,Class<T> clazz) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getUnSafeOkHttp())
                .build();
        return  retrofit.create(clazz);
    }

    public static <T> T execute(Call<T> retrofitCall) throws IOException {
        log.debug("execute:" + retrofitCall.request().toString());
        Response<T> response = retrofitCall.execute();
        if (!response.isSuccessful()) {
            throw new IOException(response.errorBody() != null
                    ? response.errorBody().string() : "Unknown error");
        } else {
            log.debug("response:" + response.toString());
            return response.body();
        }
    }
    public static InputStream download(Call<ResponseBody> retrofitCall) throws IOException {
        log.debug("download:" + retrofitCall.request().toString());
        Response<ResponseBody> response = retrofitCall.execute();
        if (!response.isSuccessful()) {
            throw new IOException(response.errorBody() != null
                    ? response.errorBody().string() : "Unknown error");
        } else {
            log.debug("response:" + response.toString());
            return response.body().byteStream();
        }
    }
    /**
     * 不验证host
     */
    private static class UnSafeHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * 不验证证书
     */
    private static class UnSafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }
}
