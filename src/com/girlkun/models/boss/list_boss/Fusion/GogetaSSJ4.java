package com.girlkun.models.boss.list_boss.Fusion;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.SmallBoss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.network.io.Message;
import com.girlkun.server.Manager;
import com.girlkun.server.ServerNotify;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Util;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author delb1
 */
public class GogetaSSJ4 extends Boss {

    private KakarotSSJ4 gogeta;
    protected boolean isReady;
    public boolean isFusion;
    protected long lastTimeFusion;
    private final int timeToFusion = 3000;
    protected long lastTimecanAttack;
    public boolean canAttack;

    public GogetaSSJ4() throws Exception {
        super(BossID.GogetaSJJ4, BossesData.VEGETA4);
        this.gogeta = null;
        this.isReady = false;
        this.isFusion = false;
    }

    @Override
    public void initBase() {
        BossData data = this.data[this.currentLevel];
        this.name = String.format(data.getName(), Util.nextInt(0, 100));
        this.gender = data.getGender();
        this.nPoint.mpg = 23_07_2003;
        this.nPoint.dameg = (long) data.getDame();
        this.nPoint.hpg = (long) data.getHp()[Util.nextInt(0, data.getHp().length - 1)];
        this.nPoint.hp = nPoint.hpg;
        this.nPoint.defg = (short) (this.nPoint.hpg / 100000);
        this.nPoint.calPoint();
        this.initSkill();
        this.resetBase();
    }

    public void createSmallBoss() {
        try {
            this.gogeta = new KakarotSSJ4(this, this.zone, (short) this.location.x, (short) this.location.y, BossesData.KAKAROT4);
        } catch (Exception ex) {

        }
    }

    public void hoptheAdr() {
        if (this.gogeta != null && this.gogeta.typePk == ConstPlayer.NON_PK && this.gogeta.isReady
                && this.typePk == ConstPlayer.NON_PK && this.isReady && !this.isFusion) {
            if (Util.canDoWithTime(lastTimeFusion, this.timeToFusion)) {
                this.isFusion = true;
                setBaseFusion();
                ChangeMapService.gI().changeMapInYard(this, this.zone, this.location.x);
                fusion(false);
                ServerNotify.gI().notify("BOSS " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
            }
        }
    }

    private void setBaseFusion() {
        BossData data = this.data[this.currentLevel];
        this.name = "Gogeta SSJ4";
        this.nPoint.mpg = 23_07_2003;
        this.nPoint.dameg = 100_100;
        this.nPoint.hpg = (long) (data.getHp()[Util.nextInt(0, data.getHp().length - 1)] + 2_000_000_000);
        this.nPoint.calPoint();
        this.initSkill();
        this.resetBase();
        //   ChangeMapService.gI().changeMapInYard(this, this.zone, this.location.x);
        this.chat("Lưỡng long nhất thể");
        this.chat("Hahaa giờ ngươi tuổi tôm nhé");
        PlayerService.gI().hoiPhuc(this, this.nPoint.hpMax, 0);

    }

    public void fusion(boolean porata) {
        ChangeMapService.gI().exitMap(this.gogeta);
        this.gogeta.leaveMap();
        this.gogeta = null;
        this.fusion.typeFusion = ConstPlayer.LUONG_LONG_NHAT_THE;
        fusionEffect(this.fusion.typeFusion);
        this.location.y = this.zone.map.yPhysicInTop(this.location.x, this.location.y);
        Service.getInstance().Send_Caitrang(this);

    }

    @Override
    public void reward(Player plKill) {
        int sb = Util.nextInt(1, 3);
        plKill.chienthan.dalamduoc++;
        plKill.achievement.plusCount(3);
        plKill.pointsb += sb;
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được +" + sb + " điểm săn Boss");
        byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length - 1); // Lấy danh sách đồ thần linh ở manager
        // byte randomNR = (byte) new Random().nextInt(Manager.itemIds_NR_SB.length);
        int random = new Random().nextInt(100);
        Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, Util.nextInt(748, 749), 1, this.location.x, this.location.y, plKill.id));

        if (random < 30) {
            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 1529, 1, this.location.x, this.location.y, plKill.id));
        } else if (random < 80) {
            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 17, 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));;
        } else if (random < 90) {
            Service.getInstance().dropItemMap(this.zone, Util.gogeta(zone, 1448, 1, this.location.x, this.location.y, plKill.id));
        } else {
            Service.getInstance().dropItemMap(this.zone, Util.roidotlskh(zone, Manager.itemIds_TL[randomDo], 1, this.location.x, this.location.y, plKill.id));
        }
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    private void fusionEffect(int id) {
        Message msg;
        try {

            msg = new Message(125);
            msg.writer().writeByte(id);
            msg.writer().writeInt((int) this.id);
            Service.gI().sendMessAllPlayerInMap(this, msg);
            Service.getInstance().Send_Caitrang(this);
            msg.cleanup();

        } catch (IOException e) {
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
            if (damage >= this.nPoint.hp && !this.isReady && this.gogeta != null) {
                this.changeToTypeNonPK();
                this.nPoint.hp = 1;
                this.isReady = true;
                this.effectSkill.removeSkillEffectWhenDie();
                if (this.gogeta.isReady) {
                    this.lastTimeFusion = System.currentTimeMillis();
                    this.lastTimecanAttack = System.currentTimeMillis();

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
    public void die(Player plKill) {
        if (plKill != null) {
            reward(plKill);
            //ServerNotify.gI().notify(plKill.name + " vừa tiêu diệt được " + this.name);
        }
        this.fusion.typeFusion = ConstPlayer.NON_FUSION;
        this.isFusion = false;
        this.isReady = false;
        this.canAttack = false;
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void active() {
        hoptheAdr();
        if (this.typePk == ConstPlayer.NON_PK && !isReady) {
            this.changeToTypePK();
            return;
        }
        if (this.gogeta == null && Util.canDoWithTime(lastTimecanAttack, timeToFusion * 2) && !this.canAttack) {
            this.changeToTypePK();
            this.canAttack = true;
            return;
        }
        this.attack();
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        this.createSmallBoss();
        st = System.currentTimeMillis();
    }
    private long st;

}
