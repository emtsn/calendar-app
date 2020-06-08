package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;

public class PageReader {
    // EFFECTS: returns string from a web page url
    public static String readWebPage(String theURL) throws IOException {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(theURL);
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return sb.toString();
    }
}
