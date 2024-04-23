package com.girlkun.models.boss.list_boss.cell;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Util;
import com.girlkun.services.PlayerService;
import java.util.Random;

public class SieuBoHung extends Boss {

    private long lastTimeHapThu;
    private int timeHapThu;

    public SieuBoHung() throws Exception {
        super(BossID.SIEU_BO_HUNG, BossesData.SIEU_BO_HUNG_1, BossesData.SIEU_BO_HUNG_2, BossesData.SIEU_BO_HUNG_3);
    }

    @Override
    public void reward(Player plKill) {
          int sb = Util.nextInt(1, 3);
        plKill.chienthan.dalamduoc++;
        plKill.achievement.plusCount(3);
        plKill.pointsb += sb;
        Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, Util.nextInt(748,749), 1, this.location.x, this.location.y, plKill.id));
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được +" + sb + " điểm săn Boss");
        byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length - 1);
        byte randomNR = (byte) new Random().nextInt(Manager.itemIds_NR_SB.length);
        if (Util.isTrue(20, 100)) {
            if (Util.isTrue(1, 10)) {
                Service.getInstance().dropItemMap(this.zone, Util.ratiItem(zone, Manager.itemIds_TL[randomDo], 1, this.location.x, this.location.y, plKill.id));
            } else if (Util.isTrue(4, 10)) {
                Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, Manager.itemIds_NR_SB[randomNR], 1, this.location.x, this.location.y, plKill.id));
            }
        } 
        if (Util.isTrue(50, 100)) {
            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 674, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
            this.location.y - 24), plKill.id));
        } else {
            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 1529, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
            this.location.y - 24), plKill.id));
        }
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    @Override
    public void active() {
        this.hapThu();
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
    private void hapThu() {
        if (!Util.canDoWithTime(this.lastTimeHapThu, this.timeHapThu) || !Util.isTrue(1, 100)) {
            return;
        }

        Player pl = this.zone.getRandomPlayerInMap();
        if (pl == null || pl.isDie()) {
            return;
        }
       ChangeMapService.gI().changeMapYardrat(this, this.zone, pl.location.x, pl.location.y);
       this.nPoint.dameg += (pl.nPoint.dame * 5 / 100);
       this.nPoint.hpg += (pl.nPoint.hp * 2 / 100);
       this.nPoint.critg++;
       this.nPoint.calPoint();
       PlayerService.gI().hoiPhuc(this, pl.nPoint.hp, 0);
       pl.injured(null, pl.nPoint.hpMax, true, false);
       Service.gI().sendThongBao(pl, "Bạn vừa bị " + this.name + " hấp thu!");
       this.chat(2, "Ui cha cha, kinh dị quá. " + pl.name + " vừa bị tên " + this.name + " nuốt chửng kìa!!!");
       this.chat("Haha, ngọt lắm đấy " + pl.name + "..");
       this.lastTimeHapThu = System.currentTimeMillis();
       this.timeHapThu = Util.nextInt(15000, 20000);
    }

}
