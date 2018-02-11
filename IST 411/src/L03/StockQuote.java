
package L03;

import java.util.Date;

/**
 *
 * @author Team 2
 */
public class StockQuote {
    private String ticker;
    private Date date;
    private double open, high, low, close;
    private int volume;
    
    public StockQuote() {
        
    }
    public StockQuote(String ticker, Date date, double open, double high,
            double low, double close, int volume) {
        this.ticker = ticker;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public String getTicker() {
        return ticker;
    }

    public Date getDate() {
        return date;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public int getVolume() {
        return volume;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
    
    public String toString() {
        String ret = "\nStock Quote: " + ticker + " " + date;
        ret += "\nOpen: " + open + "\nHigh: " + high;
        ret += "\nLow: " + low + "\nClose: " + close;
        ret += "\nVolume: " + volume;
        return ret;
    }
}
