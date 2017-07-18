package io.dcos;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

class DcosPluginHelper {

  @SuppressWarnings("deprecation")
  static Map<String, Object> readJsonFileToMap(File file) {
    Type stringStringMap = new TypeToken<Map<String, Object>>() {
    }.getType();
    try {
      return new Gson().fromJson(FileUtils.readFileToString(file), stringStringMap);
    } catch (Exception e) {
      throw new RuntimeException("Unable to read marathon app definition", e);
    }
  }

  @SuppressWarnings("deprecation")
  static Map<String, Object> readJsonFileToMap(File file, File fallbackFile) {
    if (file.exists()) {
      return readJsonFileToMap(file);
    }
    return readJsonFileToMap(fallbackFile);
  }

  @SuppressWarnings("deprecation")
  static String readToken(File tokenFile) {
    try {
      return FileUtils.readFileToString(tokenFile).trim().replace("\\n", "");
    } catch (IOException e) {
      throw new RuntimeException("Unable to read the token", e);
    }
  }

  static String cleanUrl(String url) {
    // TODO refactor
    return StringUtils.replace(url, "//", "/").replace(":/", "://");
  }

  @SuppressWarnings("deprecation")
  static CloseableHttpClient buildClient(boolean ignoreSSL) throws Exception {
    SSLSocketFactory sslsf = new SSLSocketFactory(new TrustStrategy() {

      public boolean isTrusted(
          final X509Certificate[] chain, String authType) throws CertificateException {
        // Oh, I am easy...
        return true;
      }

    });
    if (ignoreSSL) {
      return HttpClients.custom().setSSLSocketFactory(sslsf).build();
    } else {
      return HttpClients.createDefault();
    }
  }

}
