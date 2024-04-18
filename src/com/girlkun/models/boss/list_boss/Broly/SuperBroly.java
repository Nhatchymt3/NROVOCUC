package com.girlkun.models.boss.list_boss.Broly;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.PetService;
import com.girlkun.services.SkillService;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.Util;

public class SuperBroly extends Boss {

    // public SuperBroly(Zone zone, int x, int y) throws Exception {
    //     super(BossID.BROLY1, BossesData.SUPPER_BROLY);
    // }
    public SuperBroly() throws Exception {
        super(BossID.SUPERBROLY, BossesData.SUPPER_BROLY);
    }
    @Override
    public void reward(Player plKill) {
        if (plKill.pet == null) {
            PetService.gI().createNormalPet(plKill, Util.nextInt(0, 2) );
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if(Util.canDoWithTime(st,600000)){
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }
    private long st;
    public Boolean checlSkill(Skill skill)
    {
        if (skill.skillId ==Skill.DEMON || skill.skillId ==Skill.DRAGON || skill.skillId == Skill.GALICK) {
            return true;
        }
        return false;
    }
    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon - plAtt.nPoint.tlchinhxac, 1)) {
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
            if (checlSkill(plAtt.playerSkill.skillSelect)) {
                
                if (damage >= 500000) {
                    damage = 500000;
                }
            }else
            {
                damage = 0;
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
