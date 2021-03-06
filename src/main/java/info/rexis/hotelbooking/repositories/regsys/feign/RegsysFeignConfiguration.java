package info.rexis.hotelbooking.repositories.regsys.feign;

import feign.Client;
import feign.Logger;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class RegsysFeignConfiguration {
    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new LoggingErrorDecoderWrapper(new RegsysFeignClientErrorDecoder());
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    @ConditionalOnProperty(name = "regsys.relaxedSsl", havingValue = "true")
    public Client feignClient() {
        CloseableHttpClient client = HttpClients
                .custom()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setSSLContext(trustEverythingSSLContext())
                .build();
        return new ApacheHttpClient(client);
    }

    protected static class TrustEverythingTrustManager implements X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    }

    protected SSLContext trustEverythingSSLContext() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[] {
                    new TrustEverythingTrustManager()
            }, new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
