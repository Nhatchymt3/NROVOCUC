package com.girlkun.models.boss.list_boss.ADMIN;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class Adminthan extends Boss {

    public Adminthan() throws Exception {
        super(BossID.ADMIN_THAN, BossesData.ADMIN_THAN);
    }

    @Override
    public void reward(Player plKill) {
        int sb = Util.nextInt(1, 3);
        plKill.pointsb += sb;
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được +" + sb + " điểm săn Boss");
        int[] listdots ={148,149,150,151,152,153,154,155,156,157,158,159,160,162};
        int index =listdots[Util.nextInt(1, listdots.length-1)];  
        ItemMap it = new ItemMap(this.zone, listdots[index] , 1, this.location.x * 20,
                    this.zone.map.yPhysicInTop(this.location.x,
                            this.location.y - 24),
                    -1);
        Service.getInstance().dropItemMap(this.zone, it);
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        this.checkAnThan(plAtt);
        if (Util.isTrue(20, 100) && plAtt != null) {// tỉ lệ hụt của thiên sứ
            damage = 0;
        }
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 100)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }

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
        super.joinMap(); // To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;
}

/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức. Hãy tôn trọng tác
 * giả của mã nguồn này. Xin cảm ơn! - Lucy
 */
