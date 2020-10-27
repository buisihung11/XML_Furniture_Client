/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daos;

import dtos.Category;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import utils.DBUtils;

/**
 *
 * @author Admin
 */
public class CategoryDAO implements Serializable {

    public boolean insertCategory(Category categoryDTO) {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean check = false;
        try {
            con = DBUtils.makeConnection();

            if (con != null) {
                String sql = "Insert into Categories values(?,?)";
                PreparedStatement preStm = con.prepareStatement(sql);
                preStm.setString(1, categoryDTO.getName());
                preStm.setString(2, categoryDTO.getUrl());
                check = preStm.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }
}
