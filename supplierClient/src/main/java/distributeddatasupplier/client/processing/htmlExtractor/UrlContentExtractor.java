package distributeddatasupplier.client.processing.htmlExtractor;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class UrlContentExtractor {

    public static String getByUrl(String url) {
        String content = null;
        URLConnection connection = null;
        try {
            connection =  new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) " +
                    "AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
            scanner.close();
        }catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return content;
    }

}