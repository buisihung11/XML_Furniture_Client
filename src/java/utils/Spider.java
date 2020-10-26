/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import config.Constants;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Admin
 */
public class Spider {

    public static void main(String[] args) throws Exception {
        String url = "https://www.onekingslane.com/home.do";
        Spider spider = new Spider("OneKingLane", url);
        spider.startExecution();
//        spider.saveToFile("test-1.html");
//        spider.saveToFile("test.html", "<ul class=\"nav navbar-nav ml-navbar-nav ml-navbar-menu\"");
//        Document docCategories = spider
//                .splitSrcToDOM("<ul class=\"nav navbar-nav ml-navbar-nav ml-navbar-menu\"");

        Document docCategories = XMLUtils.parseFileToDom("test.html");
        XPath xPath = XMLUtils.createXPath();

        NodeList categoriesNodeList = (NodeList) xPath
                .evaluate(Constants.ONEKINGLAND_CATEGORIES_CONTAINER_XPATH + "/a",
                        docCategories,
                        XPathConstants.NODESET);

        for (int i = 0; i < categoriesNodeList.getLength(); i++) {
            // xu ly tung 
            Node parentCategory = categoriesNodeList.item(i);
            String nodeValue = parentCategory.getTextContent().replaceAll("\n+", "").trim();
            String id = parentCategory.getParentNode().getAttributes().getNamedItem("id").getNodeValue();
            System.out.println("Category-" + id + ": " + nodeValue);
            if (!id.isEmpty()) {
                String subXpath = String.format("%s[@id=\"%s\"]/ul/li/a",
                        Constants.ONEKINGLAND_CATEGORIES_CONTAINER_XPATH, id);
                NodeList subCategoriesNodeList = (NodeList) xPath
                        .evaluate(subXpath, docCategories, XPathConstants.NODESET);
                for (int j = 0; j < subCategoriesNodeList.getLength(); j++) {
                    Node subNode = subCategoriesNodeList.item(j);
                    String subValue = subNode.getTextContent().replaceAll("\n+", "").trim();
                    String subId = subNode.getParentNode().getAttributes().getNamedItem("id").getNodeValue();
                    System.out.println("\tSub-Category-" + subId + ": " + subValue);
                }

            }
        }
    }

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
