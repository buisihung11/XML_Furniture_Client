/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import dtos.Category;
import dtos.SubCategory;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Admin
 */
public class OneKingLaneUtils {

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
}
