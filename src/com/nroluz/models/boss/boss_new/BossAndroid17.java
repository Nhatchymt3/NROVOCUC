/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nroluz.models.boss.boss_new;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.utils.Util;
import java.util.Random;

/**
 *
 * @author delb1
 */
public class BossAndroid17 extends
        Boss {

    public BossAndroid17() throws Exception {
        super(BossID.BOSS_Android17, BossesData.SieuAndroid17);
    }

    @Override
    public void reward(Player plKill) {
        int sb = Util.nextInt(1, 3);
        plKill.chienthan.dalamduoc++;
        plKill.achievement.plusCount(3);
        plKill.pointsb += sb;
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được +" + sb + " điểm săn Boss");
        byte randomNR = (byte) new Random().nextInt(Manager.itemIds_NR_SB.length);
        byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length - 1); // Lấy danh sách đồ thần linh ở manager
        if (Util.isTrue(80, 100)) {
            if (Util.isTrue(50, 100)) {
                Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 1517, Util.nextInt(1, 30), this.location.x, this.location.y, plKill.id));
            } else {
                Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 17, 1, this.location.x, this.location.y, plKill.id));

            }
        } else {
            Service.getInstance().dropItemMap(this.zone, Util.roidotlskh(zone, Manager.itemIds_TL[randomDo], 1, this.location.x, this.location.y, plKill.id));
        }

        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
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
