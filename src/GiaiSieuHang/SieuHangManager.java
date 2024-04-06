/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GiaiSieuHang;

import com.girlkun.utils.Util;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author delb1
 */
public class SieuHangManager {

    private static SieuHangManager i;
    private long lastUpdate;
    protected static final List<SieuHang> list = new ArrayList<>();
    private static final List<SieuHang> toRemove = new ArrayList<>();

    public static SieuHangManager gI() {
        if (i == null) {
            i = new SieuHangManager();
        }
        return i;
    }

    public void update() {
        if (Util.canDoWithTime(lastUpdate, 1000)) {
            lastUpdate = System.currentTimeMillis();
            synchronized (list) {
                for (SieuHang mc : list) {
                    try {
                        mc.update();
                    } catch (Exception e) {
                    }
                }
                list.removeAll(toRemove);
            }
        }
    }

    public void add(SieuHang mc) {
        synchronized (list) {
            list.add(mc);
        }
    }

    public void remove(SieuHang mc) {
        synchronized (toRemove) {
            toRemove.add(mc);
        }
    }
}
