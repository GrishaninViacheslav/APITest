package APITest;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Main {
    public static LinkedList<Integer> debug_prices = new LinkedList<>();
    public static int debug_getPrice(){
        int price = debug_prices.getFirst();
        debug_prices.removeFirst();
        return price;
    }
    public static int getPriceFunc() throws ParserConfigurationException, SAXException, IOException {
        return getPriсe();
        //return debug_getPrice();
    }
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
//        debug_prices.add(57501);
//        debug_prices.add(57500);
//        debug_prices.add(57480);
//        debug_prices.add(57401);
//        debug_prices.add(57402);
//        debug_prices.add(57403);
//        debug_prices.add(57404);

//        debug_prices.add(57502);
//        debug_prices.add(57501);
//        debug_prices.add(57500);
//        debug_prices.add(57401);
//        debug_prices.add(57502);
//        debug_prices.add(57503);


        System.out.println("START");
        final long TIME_PERIOD = 240_000;//1000;//120_000;
        final BigDecimal PERCENTAGE = new BigDecimal(0.0014865261957029, new MathContext(16));
        int lastPrice = getPriceFunc();
        while (true){
            Thread.sleep(TIME_PERIOD);
            int currPrice = getPriceFunc();
            //System.out.println(currPrice);
            int compResult = new BigDecimal(1.0).subtract(new BigDecimal(currPrice).divide(new BigDecimal(lastPrice), 16, RoundingMode.HALF_UP)).compareTo(PERCENTAGE);
//            System.out.println("res: " + new BigDecimal(1.0).subtract(new BigDecimal(currPrice).divide(new BigDecimal(lastPrice), 16, RoundingMode.HALF_UP)));
//            System.out.println("per: " + PERCENTAGE);
            if( compResult == 0 || compResult == 1)
            {
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                Date date = new Date(System.currentTimeMillis());
                System.out.println("ALERT on " + formatter.format(date));
            }
            lastPrice = currPrice;
        }

//        int lastPriсe = 0;
//        long lastUdp = System.currentTimeMillis();
//        while (true) {
//            if(getPriсe() != lastPriсe){
//                System.out.print("[" + (System.currentTimeMillis() - lastUdp) + "] ");
//                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
//                Date date = new Date(System.currentTimeMillis());
//                System.out.print(formatter.format(date) + ": ");
//                System.out.println(getPriсe());
//                lastPriсe = getPriсe();
//                lastUdp = System.currentTimeMillis();
//            }
//            Thread.sleep(10000);
//       }
//        while (true){
//            printAllMarketdataPriсes();
//            printAllSecuritiesPriсes();
//            System.out.println();
//            Thread.sleep(2500);
//        }

    }

    public static int getPriсe() throws IOException, ParserConfigurationException, SAXException {
        String url = "https://iss.moex.com/iss/engines/stock/markets/foreignshares/boards/FQBR/securities.xml?iss.meta=off&iss.only=marketdata&marketdata.columns=SECID,LAST";

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        StringBuilder xmlStringBuilder = new StringBuilder();
        xmlStringBuilder.append(response.toString());
        ByteArrayInputStream input = new ByteArrayInputStream(
                xmlStringBuilder.toString().getBytes("UTF-8"));
        Document doc = builder.parse(input);

        NodeList rows = doc.getDocumentElement().getFirstChild().getFirstChild().getChildNodes();
        for (int i = 0; i < rows.getLength(); i++) {
            Node row = rows.item(i);
            NamedNodeMap rowAttributes = row.getAttributes();
            if (rowAttributes.item(1).getNodeValue().equals("TSLA-RM"))
                return Integer.parseInt(rowAttributes.item(0).getNodeValue());

        }
        return -1;
    }

    public static void printAllSecuritiesPriсes() throws IOException, ParserConfigurationException, SAXException {
        //String url = "https://iss.moex.com/iss/engines/stock/markets/foreignshares/boards/FQBR/securities.xml?iss.meta=off&iss.only=marketdata";
        String url = "https://iss.moex.com/iss/engines/stock/markets/foreignshares/boards/FQBR/securities.xml?iss.meta=off&iss.only=securities";



        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        StringBuilder xmlStringBuilder = new StringBuilder();
        xmlStringBuilder.append(response.toString());
        ByteArrayInputStream input = new ByteArrayInputStream(
                xmlStringBuilder.toString().getBytes("UTF-8"));
        Document doc = builder.parse(input);

        NodeList rows = doc.getDocumentElement().getFirstChild().getFirstChild().getChildNodes();
//        for(int i = 0; i < rows.item(0).getAttributes().getLength(); i++)
//            System.out.println(i + ": " + rows.item(0).getAttributes().item(i));

        for (int i = 0; i < rows.getLength(); i++) {
            Node row = rows.item(i);
            NamedNodeMap rowAttributes = row.getAttributes();
            if(rowAttributes.item(21).getNodeValue().equals("TSLA-RM")){
                System.out.print(rowAttributes.item(14) + " || ");
                System.out.print(rowAttributes.item(16) + " || ");
                System.out.print(rowAttributes.item(17) + " || ");
                System.out.print(rowAttributes.item(18) + " || ");
//                for(int j = 0; j < rowAttributes.getLength(); j++)
//                    System.out.print(rowAttributes.item(j) + " || ");
            }
//            for(int j = 0; j < rowAttributes.getLength(); j++)
//                System.out.println(rowAttributes.item(j).getNodeValue());
            //if (rowAttributes.item(1).getNodeValue().equals("TSLA-RM"))
            //break;
            //return Integer.parseInt(rowAttributes.item(0).getNodeValue());
        }
        //return -1;
    }

    public static void printAllMarketdataPriсes() throws IOException, ParserConfigurationException, SAXException {
        String url = "https://iss.moex.com/iss/engines/stock/markets/foreignshares/boards/FQBR/securities.xml?iss.meta=off&iss.only=marketdata";

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        StringBuilder xmlStringBuilder = new StringBuilder();
        xmlStringBuilder.append(response.toString());
        ByteArrayInputStream input = new ByteArrayInputStream(
                xmlStringBuilder.toString().getBytes("UTF-8"));
        Document doc = builder.parse(input);

        NodeList rows = doc.getDocumentElement().getFirstChild().getFirstChild().getChildNodes();
//        for(int i = 0; i < rows.item(0).getAttributes().getLength(); i++)
//            System.out.println(i + ": " + rows.item(0).getAttributes().item(i));

        for (int i = 0; i < rows.getLength(); i++) {
            Node row = rows.item(i);
            NamedNodeMap rowAttributes = row.getAttributes();
            if(rowAttributes.item(35).getNodeValue().equals("TSLA-RM")){
                System.out.print(rowAttributes.item(9) + " || ");
                System.out.print(rowAttributes.item(11) + " || ");
                System.out.print(rowAttributes.item(19) + " || ");
                System.out.print(rowAttributes.item(20) + " || ");
                System.out.print(rowAttributes.item(22) + " || ");
                System.out.print(rowAttributes.item(31) + " || ");
                System.out.print(rowAttributes.item(32) + " || ");
                System.out.print(rowAttributes.item(46) + " || ");
                System.out.print(rowAttributes.item(49) + " || ");
//                for(int j = 0; j < rowAttributes.getLength(); j++)
//                    System.out.print(rowAttributes.item(j) + " || ");
            }
//            for(int j = 0; j < rowAttributes.getLength(); j++)
//                System.out.println(rowAttributes.item(j).getNodeValue());
            //if (rowAttributes.item(1).getNodeValue().equals("TSLA-RM"))
                //break;
                //return Integer.parseInt(rowAttributes.item(0).getNodeValue());
        }
        //return -1;
    }
}
