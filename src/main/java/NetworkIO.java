import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Created by Ins on 30.12.2018.
 */
public class NetworkIO {
    private static volatile NetworkIO instance;

    private NetworkIO() {
    }

    public static NetworkIO getInstance() {
        NetworkIO localInstance = instance;
        if (instance == null) {
            synchronized (Logger.class) {
                localInstance = instance;
                if (localInstance == null)
                    instance = localInstance = new NetworkIO();
            }
        }

        return instance;
    }

    public String request(String uri, String method, Map<String, String> headers, String data) throws Exception {
        if (!(method.equals("GET") || method.equals("PUT")))
            throw new Exception("Wrong method");

        URL url = new URL(uri);

        URLConnection connection = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)connection;
        http.setRequestMethod(method);
        http.setDoOutput(true);

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                http.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        byte[] out;

        if (data != null) {
            out = data.getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            http.setFixedLengthStreamingMode(length);

            http.connect();
            try(OutputStream os = http.getOutputStream()) {
                os.write(out);
            }
        }
        else http.connect();

        //Get Response
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();

        return response.toString();
    }
}
