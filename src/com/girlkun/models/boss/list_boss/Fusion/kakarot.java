/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.girlkun.models.boss.list_boss.Fusion;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Util;
import com.girlkun.models.boss.SmallBoss;
/**
 *
 * @author delb1
 */
public class kakarot extends SmallBoss {

    protected boolean isReady;
    private short x;
    private short y;

    public kakarot() throws Exception {
        super(BossID.kakarot, BossesData.KAKAROT4);
    }

    public kakarot(Boss bigBoss, Zone zone, short x, short y, BossData data) throws Exception {
        super(BossID.kakarot, bigBoss, data);
        this.isReady = false;
        this.zone = zone;
        this.x = x;
        this.y = y;
    }

    @Override
    public void joinMap() {
        if (this.bigBoss == null) {
            super.joinMap();
        } else {
            ChangeMapService.gI().changeMap(this, this.zone, x + Util.getOne(-1, 1) * 50, y);
        }
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
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
            if (damage >= this.nPoint.hp && this.bigBoss != null && !this.isReady) {
                this.changeToTypeNonPK();
                this.isReady = true;
                this.nPoint.hp = 1;
                this.effectSkill.removeSkillEffectWhenDie();
                if (((GogetaSSJ4) this.bigBoss).isReady) {
                    ((GogetaSSJ4) this.bigBoss).lastTimeFusion = System.currentTimeMillis();
                    ((GogetaSSJ4) this.bigBoss).lastTimecanAttack = System.currentTimeMillis();
                }
                return 0;
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
        if (this.bigBoss == null) {
            if (this.typePk == ConstPlayer.NON_PK) {
                return;
            }
            this.attack();
        } else {
            if (this.bigBoss != null && this.bigBoss.typePk == ConstPlayer.PK_ALL && !this.isReady) {
                this.changeToTypePK();
            }
            this.attack();
        }
    }

    @Override
    public void leaveMap() {
        if (this.bigBoss == null) {
            super.leaveMap();
        } else {
            synchronized (this) {
                BossManager.gI().removeBoss(this);
            }
            this.dispose();
        }
    }

}
