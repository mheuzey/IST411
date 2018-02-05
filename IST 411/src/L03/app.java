package L03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author Team 2
 */
public class app {
    public static void main(String[] args) {
        System.out.println("Connecting to site.......");
        try {
            URL url = new URL("https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=AAPL&interval=1min&apikey=EK35BIPXHQP01Z6W");
            HttpsURLConnection is= (HttpsURLConnection) url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is.getInputStream()));
            String s;
            while ((s = rd.readLine()) != null) {
                System.out.println(rd.readLine());
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(app.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(app.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
