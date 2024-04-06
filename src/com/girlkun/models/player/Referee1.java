package com.girlkun.models.player;

import com.girlkun.models.shop.ShopServiceNew;
import com.girlkun.services.MapService;
import com.girlkun.consts.ConstMap;
import com.girlkun.models.map.Map;
import com.girlkun.models.map.Zone;
import com.girlkun.server.Manager;
import com.girlkun.services.MapService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
// đây
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author BTH sieu cap vippr0
 */
public class Referee1 extends Player {

    private long lastTimeChat;
    private Player playerTarget;

    private long lastTimeTargetPlayer;
    private long timeTargetPlayer = 5000;
    private long lastZoneSwitchTime;
    private long zoneSwitchInterval;
    private List<Zone> availableZones;

    public void initReferee1() {
        init();
    }

    @Override
    public short getHead() {
        return 678;
    }

    @Override
    public short getBody() {
        return 679;
    }

    @Override
    public short getLeg() {
        return 680;
    }

    public void joinMap(Zone z, Player player) {
        MapService.gI().goToMap(player, z);
        z.load_Me_To_Another(player);
    }

    @Override
    public void update() {
        if (Util.canDoWithTime(lastTimeChat, 5000)) {
            try {
                Service.getInstance().chat(this, "Xin Chúc Mừng Top Đại Gia Sever Nro");
            } catch (Exception ex) {
                Logger.getLogger(Referee1.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                Service.getInstance().chat(this, "Danh Sách"
                        + "\n|3|Mấy con gà!!!"
                );
            } catch (Exception ex) {
                Logger.getLogger(Referee1.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                Service.getInstance().chat(this, "Chúc Các Cư Dân Online Vui Vẻ ");
            } catch (Exception ex) {
                Logger.getLogger(Referee1.class.getName()).log(Level.SEVERE, null, ex);
            }
            lastTimeChat = System.currentTimeMillis();
        }
    }

    private void init() {

    }
}

