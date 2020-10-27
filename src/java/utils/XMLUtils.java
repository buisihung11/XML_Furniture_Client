/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.Validator;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Admin
 */
public class XMLUtils {
    
    public static void main(String[] args) throws Exception {
        String url = "https://www.onekingslane.com/home.do";
        Spider spider = new Spider("OneKingLane", url);
        spider.startExecution();
        
        spider.saveToFile("test-1.html");
        spider.saveToFile("test.html", "<div class=\"tab-pane nav-active Nav-100002\"");
//        Document docCategories = spider
//                .splitSrcToDOM("<div class=\"tab-pane nav-active Nav-100002\"");

        XPath xPath = XMLUtils.createXPath();
    }
    
    public static XPath createXPath() throws Exception {
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xPath = xpf.newXPath();
        return xPath;
    }
    
    public static boolean validateXMLSchema(String xsdPath, String xmlSrc) {
        try {
            SchemaFactory factory
                    = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            
            javax.xml.validation.Validator validator = schema.newValidator();
            validator.validate(new SAXSource(new InputSource(new StringReader(xmlSrc))));
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            return false;
        } catch (SAXException e1) {
            System.out.println("SAX Exception: " + e1.getMessage());
            return false;
        }
        
        return true;
        
    }
    
    public static Document parseStringToDom(String src) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(src));
//        is.setEncoding("UTF-8");
        Document doc = db.parse(is);
        return doc;
    }
    
    public static Document parseFileToDom(String filePath) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        File f = new File(filePath);
        Document doc = db.parse(f);
        return doc;
    }
    
    public static String splitSection(String src, String containerTag) throws MalformedURLException, IOException {
        String content = "";
        try {
            Reader inputString = new StringReader(src);
            BufferedReader bf = new BufferedReader(inputString);
            String inputLine;
            boolean isFound = false;
            Stack<String> stack = new Stack<>();
            do {
                inputLine = bf.readLine();
                if ((inputLine) == null || (isFound && stack.isEmpty())) {
                    break;
                }
                if (inputLine.contains(containerTag)) {
                    isFound = true;
                }

//                System.out.println("Inputline:  " + inputLine);
                String temp = inputLine.trim();
                temp = temp.replaceAll("</", "\n</")
                        .replaceAll(">", ">\n");
                String[] lines = temp.split("\n+");
                for (String line : lines) {
                    String openTagReg = "<\\s*(\\w+)([^>]*)\\s*>";
                    Pattern p = Pattern.compile(openTagReg);
                    Matcher m = p.matcher(line);
                    String endTagReg = "<\\s*\\/\\s*(\\w+)\\s*>";
                    Pattern endPattern = Pattern.compile(endTagReg);
                    Matcher endMatcher = endPattern.matcher(line);
                    if (isFound && m.matches() && !line.contains("/>")) {
                        stack.push(m.group(1).trim());
                    }
                    if (endMatcher.matches() && !stack.empty()) {
                        String topItem = stack.peek();
                        String endTagName = endMatcher.group(1).trim();
                        if (topItem.equals(endTagName)) {
                            stack.pop();
                        }
                    }
                }
                
                if (isFound) {
                    
                    content += temp;
                }
            } while ((inputLine) != null || !stack.isEmpty());
            bf.close();
        } catch (FileNotFoundException ex) {
        } finally {
            
        }
        return content;
    }
    
    public static String splitSection(String src, String[] containerTag, String[] beforeEndTag) throws UnsupportedEncodingException, IOException {
        String content = "";
        String startContainerTag = containerTag[0];
        String endContainerTag = containerTag[1];
        String startBeforeTag = beforeEndTag[0];
        String endBeforeTag = beforeEndTag[1];
        try {
            Reader inputString = new StringReader(src);
            BufferedReader bf = new BufferedReader(inputString);
            String inputLine;
            boolean isFound = false;
            boolean isStartEnd = false;
            boolean isDivEnd = false;
            while ((inputLine = bf.readLine()) != null) {
                
                if (inputLine.contains(startContainerTag)) {
                    isFound = true;
                }
                if (inputLine.contains(startBeforeTag)) {
                    isStartEnd = true;
                }
                if (isFound) {
                    String temp = inputLine.trim();
                    temp = temp.replaceAll("</", "\n</")
                            .replaceAll(">", ">\n");
                    content += temp;
                }
                if (isDivEnd && inputLine.contains(endContainerTag)) {
                    break;
                }
                if (isStartEnd && inputLine.contains(endBeforeTag)) {
                    isDivEnd = true;
                }
            }
            bf.close();
        } catch (FileNotFoundException ex) {
        } finally {
            
        }
        return content;
    }
    
    public static XMLEventReader parseFileToXMLEvent(String filePath) throws FileNotFoundException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(filePath));
        return reader;
    }
    
    public static XMLEventReader parseStringToXMLEvent(String xml) throws FileNotFoundException, XMLStreamException, UnsupportedEncodingException {
        byte[] byteArray = xml.getBytes("UTF-8");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        inputFactory.setProperty(XMLInputFactory.IS_VALIDATING, false);
        XMLEventReader reader = inputFactory.createXMLEventReader(inputStream);
        return reader;
    }
    
    public static XMLEventReader remakeFile(String filePath, int startRow,
            int posClose, int endRow, int posOpen) {
        List<String> lines = new ArrayList<String>();
        String line = "";
        File file = new File(filePath);
        boolean flagRemoved = false;
        try {
            int curRow = 0;
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            // Readfile, remvoe content error 
            while ((line = br.readLine()) != null) {
                curRow++;
                if (curRow == startRow) {
                    line = removeErrorTagWithPosClose(line, posClose);
                    lines.add(line);
                    flagRemoved = true;
                } // end curRow == startRow
                if (!flagRemoved) {
                    lines.add(line);
                }
                if (curRow == endRow) {
                    line = remmoveErrorTagWithPosOpen(line, posOpen);
                    flagRemoved = false;
                    lines.add(line);
                }// end curRow == endRow
            }
            fr.close();
            br.close();

            // Write new file
            FileWriter fw = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fw);
            for (String s : lines) {
                if (!s.trim().equals("")) {
                    out.write(s + "\n");
                }
            }
            out.flush();
            fw.close();
            out.close();
            XMLInputFactory factory = XMLInputFactory.newInstance();
            return factory.createXMLEventReader(new FileInputStream(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static String removeErrorTagWithPosClose(String line, int posClose) {
        int posOpen = 0;
        --posClose;

        // Position of close tag count by java location from 1,
        // but in arr from 0
        // so posClose - 1
        for (int i = posClose; i > 0; i--) {
            if (line.charAt(i) == '<') {
                posOpen = i;
                break;
            }
        }
        line = line.substring(0, posOpen);
        return line;
    }
    
    private static String remmoveErrorTagWithPosOpen(String line, int posOpen) {
        int posClose = 0;

        // Position of close tag count by java location from 1,
        // but in arr from 0
        // so posClose - 1
        for (int i = posOpen; i < line.length(); i++) {
            if (line.charAt(i) == '>') {
                posClose = i;
                break;
            }
        }
        line = line.substring(posClose + 1);
        return line;
    }
    
    public static void printAllData(Iterator<XMLEvent> iterator) {
        String result = "";
        while (iterator.hasNext()) {
            XMLEvent event = iterator.next();
            if (event.isStartElement()) {
                StartElement se = (StartElement) event;
                result += "<" + se.getName().toString();
                Iterator childIter = se.getAttributes();
                while (childIter.hasNext()) {
                    Attribute attr = (Attribute) childIter.next();
                    String value = attr.getValue().replace("&", "&#38");
                    result += " " + attr.getName().toString() + "=" + "\"" + value + "\"";
                }
                result += ">";
            } // endif event.isStartElement
            if (event.isCharacters()) {
                Characters chars = (Characters) event;
                if (!chars.isWhiteSpace()) {
                    result += chars.getData().replace("&", "&#38").trim();
                }
            } // endif event.isCharacter
            if (event.isEndElement()) {
                EndElement end = (EndElement) event;
                result += end.toString();
            } // endif event.isEndEleemnt
//            System.out.println(result);
            result = "";
        }
    }
    
    public static LinkedList<XMLEvent> removeListFrom(LinkedList<XMLEvent> lEvents, Integer from) {
        int to = lEvents.size();
        for (int i = from; i < to; i++) {
            lEvents.removeLast();
        }
        return lEvents;
        
    }
}
