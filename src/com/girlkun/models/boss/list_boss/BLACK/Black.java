package com.girlkun.models.boss.list_boss.BLACK;

import com.girlkun.consts.ConstTask;
import com.girlkun.models.boss.*;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.utils.Util;

import java.util.Random;

public class Black extends Boss {

    public Black() throws Exception {
        super(BossID.BLACK, BossesData.BLACK_GOKU, BossesData.SP_BL);
    }

    @Override
    public void reward(Player plKill) {
        int sb = Util.nextInt(1, 3);
        plKill.chienthan.dalamduoc++;
        plKill.achievement.plusCount(3);
        plKill.pointsb += sb;
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được +" + sb + " điểm săn Boss");
        byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length ); // Lấy danh sách đồ thần linh ở manager
        if (Util.isTrue(80, 100)) {
            if (Util.isTrue(60, 100)) {
                Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 17, 1, this.location.x, this.location.y, plKill.id));
            } else {
                if (Util.isTrue(50, 100)) {
                    Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 992, 1, this.location.x, this.location.y, plKill.id));
                } else {
                    Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 865, 1, this.location.x, this.location.y, plKill.id));
                }
            }
        } else {
            Service.getInstance().dropItemMap(this.zone, Util.roidotlskh(zone, Manager.itemIds_TL[randomDo], 1, this.location.x, this.location.y, plKill.id));
        }
        if (Util.isTrue(70, 100)) {
            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 674, 1, this.location.x, this.location.y, plKill.id));
        } else {
            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 1529, 1, this.location.x, this.location.y, plKill.id));
        }
         Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, Util.nextInt(748,749), 1, this.location.x, this.location.y, plKill.id));
    }

    @Override
    public void active() {
        super.active();
        if (Util.canDoWithTime(st, 30 * 60 * 1000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon - plAtt.nPoint.tlchinhxac, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }
    private long st;

}
