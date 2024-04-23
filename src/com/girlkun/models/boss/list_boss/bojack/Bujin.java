package com.girlkun.models.boss.list_boss.bojack;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class Bujin extends Boss {

    public Bujin() throws Exception {
        super(BossID.BUJIN_GIANGHO, BossesData.BUJIN_GIANGHO);
    }


 @Override
    public void reward(Player plKill) {
        for (int i = -100; i <= 100; i += 20) {
            if (i != 0) { // Loại trừ tọa độ 0
                Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 457, 1, this.location.x + i, this.zone.map.yPhysicInTop(this.location.x,this.location.y -24), -1));  
            }
        }
    }

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if(Util.canDoWithTime(st,900000)){
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }
    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st= System.currentTimeMillis();
    }
    private long st;
    
}





















