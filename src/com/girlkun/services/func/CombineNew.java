
package com.girlkun.services.func;

import com.girlkun.models.item.Item;
import java.util.ArrayList;
import java.util.List;

public class CombineNew {

    public long lastTimeCombine;  // Thời điểm cuối cùng kết hợp

    public List<Item> itemsCombine;  // Danh sách các đối tượng cần kết hợp
    public int typeCombine;  // Loại kết hợp

    public int goldCombine;  // Số vàng cần cho quá trình kết hợp
    public int gemCombine;  // Số ngọc cần cho quá trình kết hợp
    public float ratioCombine;  // Tỷ lệ thành công của quá trình kết hợp
    public int countDaNangCap;  // Số lần đã nâng cấp
    public short countDaBaoVe;  // Số lần đã bảo vệ
    public short quantities = 1;  // Số lượng đối tượng cần kết hợp (mặc định là 1)
    public int DiemNangcap;  // Điểm nâng cấp
    public int DaNangcap;  // Đã nâng cấp
    public float TileNangcap;  // Tỉ lệ nâng cấp
    public  int ratiocuonghoa;
     public  int ratiofusion;
    
    public CombineNew() {
        this.itemsCombine = new ArrayList<>();
    }

    /**
     * Thiết lập loại kết hợp.
     * @param type Loại kết hợp
     */
    public void setTypeCombine(int type){
        this.typeCombine = type;
    }

    /**
     * Xóa danh sách các đối tượng cần kết hợp.
     */
    public void clearItemCombine(){
        this.itemsCombine.clear();
    }

    /**
     * Xóa các tham số của quá trình kết hợp.
     */
    public void clearParamCombine(){
        this.goldCombine = 0;
        this.gemCombine = 0;
        this.ratioCombine = 0;
        this.countDaNangCap = 0;
        this.countDaBaoVe = 0;
        this.quantities = 1;
    }

    /**
     * Giải phóng bộ nhớ khi không còn sử dụng.
     */
   
    public void dispose(){
        this.itemsCombine = null;
    }
}