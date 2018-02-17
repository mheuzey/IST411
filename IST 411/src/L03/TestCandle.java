package L03;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.xy.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.text.*;
import java.util.*;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

public class TestCandle extends JFrame {
    private String interval, stockSymbol;
    private ChartPanel chartPanel;
    private JPanel panel;
    private XYPlot mainPlot;
    private JFreeChart chart;
    private JLabel lblAdvice;
    private final String address = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY";
    private final String[] stock_list = {"AAPL", "AMZN", "DIS", "F", "GE", "GOOGL", "INTC", "MSFT", "NFLX", "NVDA", "TSLA"};
    private final String[] itv_list = {"1min", "5min", "15min", "30min", "60min"};
    private OHLCDataItem[] quotes;
    
    public TestCandle() {
        super("Stock Data");
        
        this.stockSymbol = stock_list[0];
        this.interval = itv_list[0];
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel(new BorderLayout());
            
        DateAxis    domainAxis       = new DateAxis("Date");
        NumberAxis  rangeAxis        = new NumberAxis("Price");
        CandlestickRenderer renderer = new CandlestickRenderer();
        XYDataset   dataset          = getDataSet();

        mainPlot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);
        domainAxis.setTimeline( SegmentedTimeline.newMondayThroughFridayTimeline() );

        chart = new JFreeChart(stockSymbol, null, mainPlot, false);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(1200, 600));
        
        JPanel infoPanel = new JPanel();
        JPanel selectPanel = new JPanel();
        
        OHLCDataItem high = getHighest(), low = getLowest();
        String advice = "";
        if (low.getDate().after(high.getDate())) {
            advice = "If you shorted at " + high.getHigh() + " and bought back at " + low.getLow();
        } else {
            advice = "If you bought at " + low.getLow() + " and sold at " + high.getHigh();
        }
        advice += ", you would have made ";
        double result = (high.getHigh().doubleValue() / low.getLow().doubleValue() - 1) * 100;
        advice += String.format("%.2f%s", result, "%.");
        lblAdvice = new JLabel(advice);
        infoPanel.add(lblAdvice);
        
        JLabel stockLabel = new JLabel("Stock: ");
        JLabel intervalLabel = new JLabel("Interval: ");
        JComboBox stockCB = new JComboBox(stock_list);
        JComboBox intervalCB = new JComboBox(itv_list);
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stockSymbol = (String)stockCB.getSelectedItem();
                interval = (String)intervalCB.getSelectedItem();
                
                mainPlot = new XYPlot(getDataSet(), domainAxis, rangeAxis, renderer);
                chart = new JFreeChart(stockSymbol, null, mainPlot, false);
                
                chartPanel.setChart(chart);
                chartPanel.validate();
                chartPanel.repaint();
                
                OHLCDataItem high = getHighest(), low = getLowest();
                String advice = "";
                if (low.getDate().after(high.getDate())) {
                    advice = "If you shorted at " + high.getHigh() + " and bought back at " + low.getLow();
                } else {
                    advice = "If you bought at " + low.getLow() + " and sold at " + high.getHigh();
                }
                advice += ", you would have made ";
                double result = (high.getHigh().doubleValue() / low.getLow().doubleValue() - 1) * 100;
                advice += String.format("%.2f%s", result, "%.");
                lblAdvice.setText(advice);
                
                panel.validate();
                panel.repaint();
            }
        });
        
        selectPanel.add(stockLabel);
        selectPanel.add(stockCB);
        selectPanel.add(intervalLabel);
        selectPanel.add(intervalCB);
        selectPanel.add(refreshButton);
        
        panel.add(selectPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);

        this.add(panel);
        this.pack();
    }
    
    protected AbstractXYDataset getDataSet() {
        getData();
        return new DefaultOHLCDataset(stockSymbol, quotes);
    }
   
    protected void getData() {
        List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();

        try {
            String strUrl = address + "&symbol=" + stockSymbol + "&interval=" + interval + "&apikey=42LP66DV8I8H4X64&datatype=csv";
            URL url = new URL(strUrl);
            HttpsURLConnection is = (HttpsURLConnection) url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is.getInputStream()));
            
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            String inputLine;
            rd.readLine();      // skip header line
            while ((inputLine = rd.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(inputLine, ",");

                Date timestamp  = df.parse( st.nextToken() );
                double open     = Double.parseDouble( st.nextToken() );
                double high     = Double.parseDouble( st.nextToken() );
                double low      = Double.parseDouble( st.nextToken() );
                double close    = Double.parseDouble( st.nextToken() );
                double volume   = Double.parseDouble( st.nextToken() );
                
                OHLCDataItem item = new OHLCDataItem(timestamp, open, high, low, close, volume);
                dataItems.add(item);
            }
            rd.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        //Convert the list into an array
        quotes =  dataItems.toArray(new OHLCDataItem[dataItems.size()]);
    }
    
    protected OHLCDataItem getHighest(){
        OHLCDataItem ret = quotes[0];
        for (int i = 0; i < quotes.length; i++) {
            if (quotes[i].getHigh().doubleValue() > ret.getHigh().doubleValue()) {
                ret = quotes[i];
            }
        }
        return ret;
    }
    
    protected OHLCDataItem getLowest(){
        OHLCDataItem ret = quotes[0];
        for (int i = 0; i < quotes.length; i++) {
            if (quotes[i].getLow().doubleValue() < ret.getLow().doubleValue()) {
                ret = quotes[i];
            }
        }
        return ret;
    }
    
    public static void main(String[] args) {
        new TestCandle().setVisible(true);
    }
}
