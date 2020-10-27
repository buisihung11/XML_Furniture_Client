
import config.Constants;
import daos.CategoryDAO;
import dtos.Category;
import dtos.SubCategory;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.Spider;
import utils.XMLUtils;
import utils.XSLTApllier;
import utils.OneKingLaneUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Admin
 */
public class TestMain {

    private static CategoryDAO cateDAO = new CategoryDAO();
    private static String url = "https://www.onekingslane.com/home.do";
    private static Spider oneKingLaneSpider = new Spider("OneKingLane", url);

    public static void main(String[] args) throws Exception {
//        oneKingLaneSpider.startExecution();
//        oneKingLaneSpider.saveToFile("onekinglane_categories.html", Constants.ONEKINGLAND_CATEGORIES_CONTAINER_XPATH);
        String categoriesHTMLSrc = oneKingLaneSpider.splitSrcFromFile("onekinglane_categories.html", Constants.ONEKINGLAND_CATEGORIES_CONTAINER_XPATH);
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
            Category category = OneKingLaneUtils.getCategory(cateNode);
            if (category != null) {
                categories.add(category);
            }
        }

        categories.forEach((category) -> {
            System.out.println("CategoryName: " + category.getName());
//            cateDAO.insertCategory(category);
            for (SubCategory subCategory : category.getSubcategory()) {
                System.out.println("\tSub-Name: " + subCategory.getName());
            }
        });

        System.out.println("START CRAWL PRODUCT");

        // get product
        categories.forEach((category) -> {
//            System.out.println("GET Product of CategoryName: " + category.getName());
            for (SubCategory subCategory : category.getSubcategory()) {
                try {
                    System.out.println("\tGET Product of Sub-Name: " + subCategory.getName());
                    System.out.println("PRODUCT URL: " + subCategory.getUrl());
                    String subFileName = subCategory.getName().replaceAll("\\s+", "-").concat(".html");
                    Spider productSpider = new Spider(subCategory.getName(), subCategory.getUrl());
                    // First crawl and get the total product
                    productSpider.startExecution();
                    String totalProductHTMLSrc = productSpider.splitSrc(Constants.ONELINGLAND_TOTAL_PRODUCT_XAPTH);
                    Document preProductDom = XMLUtils.parseStringToDom(totalProductHTMLSrc);
                    Element totalElement = preProductDom.getElementById("totalProducts");
                    String totalProduct = totalElement.getTextContent().trim();

                    System.out.println("Total Product: " + totalProduct);

                } catch (Exception ex) {
                    Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }
}
