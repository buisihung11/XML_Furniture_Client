/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import config.Constants;
import daos.CategoryDAO;
import dtos.Category;
import dtos.ListCategories;
import dtos.SubCategory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 *
 * @author Admin
 */
public class Spider {

    private static String dtdPath = "";

    public String name;
    public String startURL;
    public String src;

    public Spider(String name, String startURL) {
        this.name = name;
        this.startURL = startURL;
    }

    public void startExecution() throws IOException, Exception {
        System.out.println("START SPIDER " + name);
        // 1. get raw HTML
        System.out.println("1. DOWNLOAD FILE AND REFINED");
        String rawHTML = GetHTML.getHTMLToString(startURL);
        this.src = rawHTML;
    }

    public void saveToFile(String filePath) {
        FileUtil.saveSrcToFile(filePath, src);
    }

    public void saveToFile(String filePath, String containerTag) throws IOException {
        String splitted = XMLUtils.splitSection(src, containerTag);
        splitted = "<html>\n<head>\nHeader</head>\n<body>\n" + splitted + "\n</body>\n</html>";
        FileUtil.saveSrcToFile(filePath, splitted);
    }

    public void saveToFile(String filePath, String[] containerTag, String[] beforeEndTag) throws IOException {
        String splitted = XMLUtils.splitSection(src, containerTag, beforeEndTag);
        FileUtil.saveSrcToFile(filePath, splitted);
    }

    public String splitSrc(String containerTag) throws IOException,
            ParserConfigurationException, SAXException {
        return XMLUtils.splitSection(src, containerTag);
    }

    public String splitSrcFromFile(String filePath, String containerTag) throws IOException,
            ParserConfigurationException, SAXException {
        String srcFromFile = FileUtil.getSrcFromFile(filePath);
        return XMLUtils.splitSection(srcFromFile, containerTag);
    }

    public Document splitSrcToDOM(String containerTag) throws IOException,
            ParserConfigurationException, SAXException {
        String splitedSrc = XMLUtils.splitSection(src, containerTag);
        System.out.println("SPLITED: ");
        splitedSrc = "<html><head></head><body>" + splitedSrc + "</body></html>";
        return XMLUtils.parseStringToDom(splitedSrc);
    }

//
//    public XMLEventReader splitSrcToXML(String containerTag) throws IOException,
//            FileNotFoundException, UnsupportedEncodingException, XMLStreamException {
//        String splitedSrc = XMLUtils.splitSection(src, containerTag);
//        System.out.println("SPLITED: ");
//        System.out.println(splitedSrc);
//        return XMLUtils.parseStringToXMLEvent(splitedSrc);
//    }
//
//    public XMLEventReader splitSrcToXML(String[] containerTag, String[] beforeEndTag) throws IOException,
//            FileNotFoundException, XMLStreamException, UnsupportedEncodingException {
//        String splitedSrc = XMLUtils.splitSection(src, containerTag, beforeEndTag);
//        System.out.println("SPLITED: ");
//        System.out.println(splitedSrc);
//        return XMLUtils.parseStringToXMLEvent(splitedSrc);
//    }
}
