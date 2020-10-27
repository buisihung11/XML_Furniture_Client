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

    private static CategoryDAO cateDAO = new CategoryDAO();

    public static void main(String[] args) throws Exception {
        String url = "https://www.onekingslane.com/home.do";
        Spider spider = new Spider("OneKingLane", url);
        spider.startExecution();
//        spider.saveToFile("test-1.html");
//        spider.saveToFile("test.html", "<ul class=\"nav navbar-nav ml-navbar-nav ml-navbar-menu\"");
//        Document docCategories = spider
//                .splitSrcToDOM("<ul class=\"nav navbar-nav ml-navbar-nav ml-navbar-menu\"");

        String categoriesHTMLSrc = spider.splitSrc("<ul class=\"nav navbar-nav ml-navbar-nav ml-navbar-menu\"");
        String categoriesXSLTPath = "web\\WEB-INF\\" + "categories.xsl";
        String categoriesXMLSrc = new XSLTApllier()
                .applyStylesheet(categoriesXSLTPath, categoriesHTMLSrc);

        // validate
//        boolean isValid = XMLUtils.validateXMLSchema(dtdPath,c categoriesXMLSrc);
        // Unmarshall XML source and validate via Schema to get object
//        JAXBContext jaxb = JAXBContext.newInstance(ListCategories.class);
//        Unmarshaller unmarshaller = jaxb.createUnmarshaller();
//        ListCategories product = (ListCategories) unmarshaller.unmarshal(new ByteArrayInputStream(categoriesXMLSrc.getBytes(StandardCharsets.UTF_8)));
        Document categoriesDom = XMLUtils.parseStringToDom(categoriesXMLSrc);
        NodeList categoriesNodeList = categoriesDom.getElementsByTagName("Category");
        ArrayList<Category> categories = new ArrayList<>();
        for (int i = 0; i < categoriesNodeList.getLength(); i++) {
            Node cateNode = categoriesNodeList.item(i);
            Category category = getCategory(cateNode);
            if (category != null) {
                categories.add(category);
            }
        }

        categories.forEach((category) -> {
            System.out.println("CategoryName: " + category.getName());
            cateDAO.insertCategory(category);
            for (SubCategory subCategory : category.getSubcategory()) {
                System.out.println("\tSub-Name: " + subCategory.getName());
            }
        });
    }

    public static Category getCategory(Node cateNode) {
        NodeList cateNodeChildList = cateNode.getChildNodes();
        Category cate = new Category();
        for (int j = 0; j < cateNodeChildList.getLength(); j++) {

            Node cateEle = cateNodeChildList.item(j);
            if (cateEle instanceof Element) {
                String eleName = ((Element) cateEle).getTagName();
                if (eleName.equals("name")) {
                    cate.setName(cateEle.getTextContent());
                } else if (eleName.equals("url")) {
                    cate.setUrl(cateEle.getTextContent());
                }

            }
        }

        // tim subCate
        NodeList subNodeList = ((Element) cateNode).getElementsByTagName("subcategory");
        ArrayList<SubCategory> subCategories = new ArrayList<>();
        for (int j = 0; j < subNodeList.getLength(); j++) {
            Node subCateNode = subNodeList.item(j);
            SubCategory subCate = getSubCategory(subCateNode);
            if (subCate != null) {
                subCategories.add(subCate);
            }
        }
        if (!subCategories.isEmpty()) {
            cate.setSubcategory(subCategories);
        }
        return cate;
    }

    public static SubCategory getSubCategory(Node subNode) {
        NodeList subCateNodeChildList = subNode.getChildNodes();
        SubCategory sub = new SubCategory();
        for (int j = 0; j < subCateNodeChildList.getLength(); j++) {
            Node cateEle = subCateNodeChildList.item(j);
            if (cateEle instanceof Element) {
                String eleName = ((Element) cateEle).getTagName();
                if (eleName.equals("name")) {
                    sub.setName(cateEle.getTextContent());
                } else if (eleName.equals("url")) {
                    sub.setUrl(cateEle.getTextContent());
                }

            }
        }
        return sub;
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

    public String splitSrc(String containerTag) throws IOException,
            ParserConfigurationException, SAXException {
        return XMLUtils.splitSection(src, containerTag);
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
