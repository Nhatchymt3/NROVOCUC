package com.girlkun.models.boss.list_boss.bojack;
import com.girlkun.models.item.Item;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.utils.Util;
import java.util.Random;


public class Bojack extends Boss {

    public Bojack() throws Exception {
        super(BossID.BOJACK_GIANGHO, BossesData.BOJACK_GIANGHO);
    }
     @Override
    public void reward(Player plKill) {

        for (int i = -100; i <= 100; i += 20) {
            if (i != 0) { // Loại trừ tọa độ 0
                Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 457, 1, this.location.x + i, this.zone.map.yPhysicInTop(this.location.x,this.location.y), -1));  
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
/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức. Hãy tôn trọng tác
 * giả của mã nguồn này. Xin cảm ơn! - GirlBeo
 */
    