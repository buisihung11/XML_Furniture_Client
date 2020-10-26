/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.Writer;

public class GetHTML {

    public static void getHTMLToFile(String filePath, String uri) {
        try {
            String src = Internet.getHTML(uri);
            FileUtil.saveSrcToFile(filePath, src);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getHTMLToString(String uri) {
        Writer writer = null;
        try {
            String src = Internet.getHTML(uri);
            src = TextUtils.refineHtml(src);
            return src;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
