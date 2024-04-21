package com.girlkun.models.kygui;

import com.girlkun.database.GirlkunDB;
import com.girlkun.models.item.Item;
import com.girlkun.models.item.Item.ItemOption;
import com.girlkun.result.GirlkunResultSet;
import com.girlkun.server.Manager;
import com.girlkun.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class ShopKyGuiManager {
    private static ShopKyGuiManager instance;
    public static ShopKyGuiManager gI() {
        if (instance == null) {
            instance = new ShopKyGuiManager();
        }
        return instance;
    }

    public long lastTimeUpdate;

    public String[] tabName = {"Trang bị","Phụ kiện","Hỗ trợ","Linh tinh",""};

    public List<ItemKyGui> listItem = new ArrayList<>();


    public synchronized void save() {
        try (Connection con = GirlkunDB.getConnection()) {
            String sql = "INSERT INTO shop_ky_gui (id, player_id, tab, item_id, gold, gem, quantity, itemOption, isUpTop, isBuy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                for (ItemKyGui it : this.listItem) {
                    if (it != null) {
                        pstmt.setInt(1, it.id);
                        pstmt.setInt(2, it.player_sell);
                        pstmt.setInt(3, it.tab);
                        pstmt.setInt(4, it.itemId);
                        pstmt.setInt(5, it.goldSell);
                        pstmt.setInt(6, it.gemSell);
                        pstmt.setInt(7, it.quantity);
                        pstmt.setString(8, JSONValue.toJSONString(it.options).equals("null") ? "[]" : JSONValue.toJSONString(it.options));
                        pstmt.setByte(9, it.isUpTop);
                        pstmt.setInt(10, it.isBuy ? 1 : 0);
                        pstmt.addBatch();
                    }
                }
                int[] affectedRows = pstmt.executeBatch();
            }
        } catch (Exception e) {
            Logger.logException(Manager.class, e, "Lỗi save ki gui");
        }
    }
    public synchronized void SaveItem(ItemKyGui item) {
        try (Connection con = GirlkunDB.getConnection()) {
            String sql = "INSERT INTO shop_ky_gui (id, player_id, tab, item_id, gold, gem, quantity, itemOption, isUpTop, isBuy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, item.id);
                pstmt.setInt(2, item.player_sell);
                pstmt.setInt(3, item.tab);
                pstmt.setInt(4, item.itemId);
                pstmt.setInt(5, item.goldSell);
                pstmt.setInt(6, item.gemSell);
                pstmt.setInt(7, item.quantity);
                pstmt.setString(8, JSONValue.toJSONString(item.options).equals("null") ? "[]" : JSONValue.toJSONString(item.options));
                pstmt.setByte(9, item.isUpTop);
                pstmt.setInt(10, item.isBuy ? 1 : 0);
                int affectedRows = pstmt.executeUpdate();
            }
        } catch (Exception e) {
            Logger.logException(Manager.class, e, "Lỗi save ki gui");
        }
    }
    public synchronized void updateItem(ItemKyGui item) {
        try (Connection con = GirlkunDB.getConnection()) {
            String sql = "UPDATE shop_ky_gui SET player_id = ?, tab = ?, item_id = ?, gold = ?, gem = ?, quantity = ?, itemOption = ?, isUpTop = ?, isBuy = ? WHERE id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, item.player_sell);
                pstmt.setInt(2, item.tab);
                pstmt.setInt(3, item.itemId);
                pstmt.setInt(4, item.goldSell);
                pstmt.setInt(5, item.gemSell);
                pstmt.setInt(6, item.quantity);
                pstmt.setString(7, JSONValue.toJSONString(item.options).equals("null") ? "[]" : JSONValue.toJSONString(item.options));
                pstmt.setByte(8, item.isUpTop);
                pstmt.setInt(9, item.isBuy ? 1 : 0);
                pstmt.setInt(10, item.id);

                int affectedRows = pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            Logger.logException(ShopKyGuiManager.class, e, "Lỗi update vật phẩm");
        }
    }

    public synchronized void deleteItem(int id) {
        try (Connection con = GirlkunDB.getConnection()) {
            String sql = "DELETE FROM shop_ky_gui WHERE id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int affectedRows = pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            Logger.logException(ShopKyGuiManager.class, e, "Lỗi xóa vật phẩm");
        }
    }
     public void Load() {
        try {
            PreparedStatement ps = null;
            Connection con = GirlkunDB.getConnection();
            ps = con.prepareStatement("SELECT * FROM shop_ky_gui");
            ResultSet rs = null;
            rs = ps.executeQuery();
            ShopKyGuiManager.gI().listItem.clear();
            while (rs.next()) {
                int i = rs.getInt("id");
                int idPl = rs.getInt("player_id");
                byte tab = rs.getByte("tab");
                short itemId = rs.getShort("item_id");
                int ruby = rs.getInt("gold");
                int gem = rs.getInt("gem");
                int quantity = rs.getInt("quantity");
                byte isUp = rs.getByte("isUpTop");
                boolean isBuy = rs.getByte("isBuy") == 1;
                List<Item.ItemOption> op = new ArrayList<>();
                JSONArray jsa2 = (JSONArray) JSONValue.parse(rs.getString("itemOption"));
                for (int j = 0; j < jsa2.size(); ++j) {
                    JSONObject jso2 = (JSONObject) jsa2.get(j);
                    int idOptions = Integer.parseInt(jso2.get("id").toString());
                    int param = Integer.parseInt(jso2.get("param").toString());
                    op.add(new Item.ItemOption(idOptions, param));
                }
                ShopKyGuiManager.gI().listItem.add(new ItemKyGui(i, itemId, idPl, tab, ruby, gem, quantity, isUp, op, isBuy));
            }
            Logger.log(Logger.PURPLE, "Load Item Ký Gửi Thành Công[" + ShopKyGuiManager.gI().listItem.size() + "] Vật Phẩm!\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // public void SaveandLoad() {
    //        List<ItemKyGui> listItemSave = new ArrayList<>(this.listItem); 
    //        try {
    //            save();
    //            Load();
    //        } catch (Exception e) {
    //            this.listItem = listItemSave;
    //            e.printStackTrace();
    //        }
    //         }
}