package BossDetu;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import java.util.Random;

public class DetuWukong extends Boss {

    public DetuWukong() throws Exception {
        super(BossID.DTWUKONG, BossesData.WUKONG);
    }

    @Override
    public void reward(Player plKill) {
        plKill.pointsb++;
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được 1 điểm săn Boss");
        byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length - 1); // Lấy danh sách đồ thần linh ở manager
        byte randomNR = (byte) new Random().nextInt(Manager.itemIds_NR_SB.length);
        int random = new Random().nextInt(100);
        if (Util.isTrue(98, 100)) {
            if (Util.isTrue(70, 100)) {
                Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, Util.nextInt(14, 15), Util.nextInt(1, 2), this.location.x, this.location.y, plKill.id));
            } else {
                Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 1529, Util.nextInt(1, 3), this.location.x, this.location.y, plKill.id));
            }
        } else {
            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 1741, 1, this.location.x, this.location.y, plKill.id));
        }
    }

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 2500000)) {
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
