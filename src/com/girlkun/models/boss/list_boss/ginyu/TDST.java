package com.girlkun.models.boss.list_boss.ginyu;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.utils.Util;
import java.util.Random;

public class TDST extends Boss {

    public TDST() throws Exception {
        super(BossID.TDST, BossesData.SO_4, BossesData.SO_3, BossesData.SO_2, BossesData.SO_1, BossesData.TIEU_DOI_TRUONG);
    }

    @Override
    public void moveTo(int x, int y) {
        if (this.currentLevel == 1) {
            return;
        }
        super.moveTo(x, y);
    }

    @Override
    public void reward(Player plKill) {
        super.reward(plKill);
        Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, Util.nextInt(748, 749), 1, this.location.x, this.location.y, plKill.id));

        if (this.currentLevel == 1) {
            return;
        }
        if (Util.isTrue(50, 100)) {
            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 674, 1, this.location.x, this.location.y, plKill.id));
        } else {
            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 1529, 1, this.location.x, this.location.y, plKill.id));
        }
    }

    @Override
    protected void notifyJoinMap() {
        if (this.currentLevel == 1) {
            return;
        }
        super.notifyJoinMap();
    }

    @Override
    public void active() {
        super.active();
        if (Util.canDoWithTime(st, 30 * 60 * 1000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }
    private long st;
}

/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức.
 */
