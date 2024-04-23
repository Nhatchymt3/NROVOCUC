package com.girlkun.models.boss.list_boss.Cooler;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import java.util.Random;

/**
 * @@Stole By Hoàng Việt
 */
public class Cooler extends Boss {

    public Cooler() throws Exception {
        super(BossID.COOLER, BossesData.COOLER_1, BossesData.COOLER_2);
    }

    @Override
    public void reward(Player plKill) {
        plKill.pointsb++;
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được 1 điểm săn Boss");
        byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length - 1);
        byte randomDo1 = (byte) new Random().nextInt(Manager.itemIds_CUI.length - 1);
        if (Util.isTrue(20, 100)) {
            if (Util.isTrue(1, 20)) {
                Service.getInstance().dropItemMap(this.zone, Util.RaitiDoc12(zone, Manager.itemIds_TL[randomDo1], 1, this.location.x, this.location.y, plKill.id));
            } else {
                Service.getInstance().dropItemMap(this.zone, Util.ratiDTL(zone, Manager.itemIds_TL[randomDo], 1, this.location.x, this.location.y, plKill.id));
            }

        } else {
            if (Util.isTrue(50, 100)) {
                Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 674, 1, this.location.x, this.location.y, plKill.id));
            } else {
                Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 1529, 1, this.location.x, this.location.y, plKill.id));
            }
        }
        Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, Util.nextInt(748, 749), 1, this.location.x, this.location.y, plKill.id));

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
}
