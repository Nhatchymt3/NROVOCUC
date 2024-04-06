package BossThucung;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.boss.list_boss.MiNuong;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lanme extends Boss {

    public Lanme() throws Exception {
        super(BossID.lanme, BossesData.lanme);
    }

    @Override
    public void reward(Player plKill) {
        try {
            BossData bossDataClone = new BossData(
                    "Kì lân con",
                    (byte) 2,
                    new short[]{1894, 1895, 1896, -1, -1, -1},
                    100000,
                    new double[]{plKill.nPoint.hpMax * 2},
                    new int[]{103},
                    new int[][]{
                        {Skill.TAI_TAO_NANG_LUONG, 7, 15000}},
                    new String[]{}, //text chat 1
                    new String[]{}, //text chat 2
                    new String[]{}, //text chat 3
                    60
            );
            Lancon kilan = new Lancon(Util.createIdDuongTank((int) plKill.id), bossDataClone, plKill.zone, plKill.location.x - 20, plKill.location.y);
            kilan.playerTarger = plKill;
            int[] mapcuoi = {6, 29, 30, 4, 5, 27, 28};
            kilan.mapHoTong = mapcuoi[Util.nextInt(mapcuoi.length)];
            plKill.haveBeQuynh = true;
            plKill.lastTimeHoTong = System.currentTimeMillis();
        } catch (Exception ex) {
            Logger.getLogger(Lanme.class.getName()).log(Level.SEVERE, null, ex);
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
        super.joinMap(); 
        st = System.currentTimeMillis();
    }
    private long st;

}
