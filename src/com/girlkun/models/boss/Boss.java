package com.girlkun.models.boss;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.iboss.IBossNew;
import com.girlkun.models.boss.iboss.IBossOutfit;
import com.girlkun.models.boss.list_boss.AnTrom;
import com.girlkun.models.boss.list_boss.Fusion.GogetaSSJ4;
import com.girlkun.models.boss.list_boss.Fusion.BlackGoku;
import com.girlkun.models.boss.list_boss.MiNuong;
import com.girlkun.models.boss.list_boss.NRD.*;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.network.io.Message;
import com.girlkun.server.ServerNotify;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.MapService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.services.SkillService;
import com.girlkun.services.TaskService;
import com.girlkun.services.func.ChangeMapService;
import static com.girlkun.utils.Logger.GREEN;
import static com.girlkun.utils.Logger.RED;
import static com.girlkun.utils.Logger.RESET;
import static com.girlkun.utils.Logger.YELLOW;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.Util;
import com.nroluz.models.boss.boss_new.OngGiaNoel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Boss extends Player implements IBossNew, IBossOutfit {

    public int currentLevel = -1;
    public final BossData[] data;

    public BossStatus bossStatus;

    protected Zone lastZone;

    protected long lastTimeRest;
    protected int secondsRest;

    protected Boss bossInstance;
    public int mapHoTong;
    private int typeBoss;
    protected long lastTimeChatS;
    protected int timeChatS;
    protected byte indexChatS;

    protected long lastTimeChatE;
    protected int timeChatE;
    protected byte indexChatE;

    protected long lastTimeChatM;
    protected int timeChatM;

    protected long lastTimeTargetPlayer;
    protected int timeTargetPlayer;
    public Player playerTarger;
    public Player lockPlayerTarget;

    protected Boss parentBoss;
    public Boss[][] bossAppearTogether;

    public Zone zoneFinal = null;
    public int idBoss;

    public Boss(int id, BossData... data) throws Exception {
        this.id = id;
        this.isBoss = true;
        if (data == null || data.length == 0) {
            throw new Exception("Dữ liệu boss không hợp lệ");
        }
        this.data = data;
        this.secondsRest = this.data[0].getSecondsRest();
        this.bossStatus = BossStatus.REST;
        BossManager.gI().addBoss(this);

        this.bossAppearTogether = new Boss[this.data.length][];
        for (int i = 0; i < this.bossAppearTogether.length; i++) {
            if (this.data[i].getBossesAppearTogether() != null) {
                this.bossAppearTogether[i] = new Boss[this.data[i].getBossesAppearTogether().length];
                for (int j = 0; j < this.data[i].getBossesAppearTogether().length; j++) {
                    Boss boss = BossManager.gI().createBoss(this.data[i].getBossesAppearTogether()[j]);
                    if (boss != null) {
                        boss.parentBoss = this;
                        this.bossAppearTogether[i][j] = boss;
                    }
                }
            }
        }
    }

    @Override
    public void initBase() {
        BossData data = this.data[this.currentLevel];
        this.name = String.format(data.getName(), Util.nextInt(0, 100));
        this.gender = data.getGender();
        this.nPoint.mpg = 1_6_2000;
        this.nPoint.dameg = (long) data.getDame();
        this.nPoint.hpg = (long) data.getHp()[Util.nextInt(0, data.getHp().length - 1)];
        this.nPoint.hp = nPoint.hpg;
        this.nPoint.calPoint();
        this.initSkill();
        this.resetBase();
    }
    

    protected void initSkill() {
        for (Skill skill : this.playerSkill.skills) {
            skill.dispose();
        }
        this.playerSkill.skills.clear();
        this.playerSkill.skillSelect = null;
        int[][] skillTemp = data[this.currentLevel].getSkillTemp();
        for (int i = 0; i < skillTemp.length; i++) {
            Skill skill = SkillUtil.createSkill(skillTemp[i][0], skillTemp[i][1]);
            if (skillTemp[i].length == 3) {
                skill.coolDown = skillTemp[i][2];
            }
            this.playerSkill.skills.add(skill);
        }
    }

    protected void resetBase() {
        this.lastTimeChatS = 0;
        this.lastTimeChatE = 0;
        this.timeChatS = 0;
        this.timeChatE = 0;
        this.indexChatS = 0;
        this.indexChatE = 0;
    }

    ///////////////////////////
    @Override
    public short getHead() {
        if (effectSkill != null && effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        }
        if (this.id == BossID.BLACKGOKU) {
            if (((BlackGoku) this).isFusion) {
                return 1680;
            }
        }
        if (this.id == BossID.GogetaSJJ4) {
            if (((GogetaSSJ4) this).isFusion) {
                return 1758;
            }
        }

        return this.data[this.currentLevel].getOutfit()[0];
    }

    @Override
    public short getBody() {
        if (effectSkill != null && effectSkill.isMonkey) {
            return 193;
        }
        if (this.id == BossID.BLACKGOKU) {
            if (((BlackGoku) this).isFusion) {
                return 1681;
            }
        }
        if (this.id == BossID.GogetaSJJ4) { // Add the missing parenthesis here
            if (((GogetaSSJ4) this).isFusion) {
                return 1759;
            }
        }
        return this.data[this.currentLevel].getOutfit()[1];
    }

    @Override
    public short getLeg() {
        if (effectSkill != null && effectSkill.isMonkey) {
            return 194;
        }
        if (this.id == BossID.BLACKGOKU) {
            if (((BlackGoku) this).isFusion) {
                return 1682;
            }
        } else if (this.id == BossID.GogetaSJJ4) {
            if (((GogetaSSJ4) this).isFusion) {
                return 1760;
            }
        }
        return this.data[this.currentLevel].getOutfit()[2];
    }

    @Override
    public short getFlagBag() {
        return this.data[this.currentLevel].getOutfit()[3];
    }

    @Override
    public byte getAura() {
        return (byte) this.data[this.currentLevel].getOutfit()[4];
    }

    @Override
    public byte getEffFront() {
        return (byte) this.data[this.currentLevel].getOutfit()[5];
    }

    public Zone getMapJoin() {
        int mapId = this.data[this.currentLevel].getMapJoin()[Util.nextInt(0, this.data[this.currentLevel].getMapJoin().length - 1)];
        Zone map = MapService.gI().getMapWithRandZone(mapId);
        //to do: check boss in map

        return map;
    }

    @Override
    public void changeStatus(BossStatus status) {
        this.bossStatus = status;
    }

    @Override
    public Player getPlayerAttack() {
        if (this.zone != null && (this.playerTarger == null || Util.canDoWithTime(this.lastTimeTargetPlayer, this.timeTargetPlayer))) {
            this.playerTarger = this.zone.getRandomPlayerInMap();
            this.lastTimeTargetPlayer = System.currentTimeMillis();
            this.timeTargetPlayer = Util.nextInt(5000, 7000);
        }
        if (this.zone != null && this.playerTarger != null && (this.playerTarger.isDie() || !this.zone.equals(this.playerTarger.zone)
                || this.playerTarger.effectSkin.isVoHinh || this.playerTarger.isNewPet)) {
            this.playerTarger = null;
        }
        if (this.lockPlayerTarget != null) {
            this.playerTarger = this.lockPlayerTarget;
            return this.lockPlayerTarget;
        }

        return this.playerTarger;
    }

    @Override
    public void changeToTypePK() {
        PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.PK_ALL);
    }

    @Override
    public void changeToTypeNonPK() {
        PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.NON_PK);
    }

    @Override
    public void update() {
        super.update();
        this.nPoint.mp = this.nPoint.mpg;
        if (this.effectSkill.isHaveEffectSkill()) {
            return;
        }
        switch (this.bossStatus) {
            case REST:
                this.rest();
                break;
            case RESPAWN:
                this.respawn();
                this.changeStatus(BossStatus.JOIN_MAP);
                break;
            case JOIN_MAP:
                this.joinMap();
                timejoin =System.currentTimeMillis();
                this.changeStatus(BossStatus.CHAT_S);
                break;
            case CHAT_S:
                if (chatS()) {
                    this.doneChatS();
                    this.lastTimeChatM = System.currentTimeMillis();
                    this.timeChatM = 5000;
                    this.changeStatus(BossStatus.ACTIVE);
                }
                break;
            case ACTIVE:
                this.chatM();
                if (this.effectSkill.isCharging && !Util.isTrue(1, 20) || this.effectSkill.useTroi) {
                    return;
                }
                this.active();
                break;
            case DIE:
                this.changeStatus(BossStatus.CHAT_E);
                break;
            case CHAT_E:
                if (chatE()) {
                    this.doneChatE();
                    this.changeStatus(BossStatus.LEAVE_MAP);
                }
                break;
            case LEAVE_MAP:
                this.leaveMap();
                break;
        }
    }

    //loop
    @Override
    public void rest() {
        int nextLevel = this.currentLevel + 1;
        if (nextLevel >= this.data.length) {
            nextLevel = 0;
        }
        if (this.data[nextLevel].getTypeAppear() == TypeAppear.DEFAULT_APPEAR
                && Util.canDoWithTime(lastTimeRest, secondsRest * 1000)) {
            this.changeStatus(BossStatus.RESPAWN);
        }
    }

    @Override
    public void respawn() {
        this.currentLevel++;
        if (this.currentLevel >= this.data.length) {
            this.currentLevel = 0;
        }
        this.initBase();
        this.changeToTypeNonPK();
    }
    public long timejoin;
    @Override
    public void joinMap() {
        if (zoneFinal != null) {
            joinMapByZone(zoneFinal);
            this.notifyJoinMap();
            return;
        }
        if (this.zone == null) {
            if (this.parentBoss != null) {
                this.zone = parentBoss.zone;
            } else if (this.lastZone == null) {
                this.zone = getMapJoin();
            } else {
                this.zone = this.lastZone;
            }
        }
        if (this.zone != null) {
            if (this.currentLevel == 0) {
                if (this.parentBoss == null) {
                    ChangeMapService.gI().changeMapBySpaceShip(this, this.zone, -1);
                } else {
                    ChangeMapService.gI().changeMapBySpaceShip(this, this.zone,
                            this.parentBoss.location.x + Util.nextInt(-100, 100));
                }
                this.wakeupAnotherBossWhenAppear();
            } else {
                ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);
            }
            Service.getInstance().sendFlagBag(this);
            this.notifyJoinMap();
        }
    }

    public void joinMapByZone(Player player) {
        if (player.zone != null) {
            this.zone = player.zone;
            ChangeMapService.gI().changeMapBySpaceShip(this, this.zone, -1);
        }

    }

    public void joinMapByZone(Zone zone) {
        if (zone != null) {
            this.zone = zone;
            ChangeMapService.gI().changeMapBySpaceShip(this, this.zone, -1);
        }
    }

    protected void notifyJoinMap() {
        if (this.zone.map.mapId == 140 || MapService.gI().isMapMaBu(this.zone.map.mapId)
                || MapService.gI().isMapDoanhTrai(this.zone.map.mapId)
                || MapService.gI().isMapKhiGas(this.zone.map.mapId)
                || MapService.gI().isMapBanDoKhoBau(this.zone.map.mapId)
                || MapService.gI().isMapBlackBallWar(this.zone.map.mapId)
                || this instanceof MiNuong
                || this instanceof OngGiaNoel
                || this instanceof AnTrom) {
            return;
        }
        ServerNotify.gI().notify("BOSS " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
    }

    @Override
    public boolean chatS() {
        if (Util.canDoWithTime(lastTimeChatS, timeChatS)) {
            if (this.indexChatS == this.data[this.currentLevel].getTextS().length) {
                return true;
            }
            String textChat = this.data[this.currentLevel].getTextS()[this.indexChatS];
            int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
            textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
            if (!this.chat(prefix, textChat)) {
                return false;
            }
            this.lastTimeChatS = System.currentTimeMillis();
            this.timeChatS = textChat.length() * 100;
            if (this.timeChatS > 2000) {
                this.timeChatS = 2000;
            }
            this.indexChatS++;
        }
        return false;
    }

    @Override
    public void doneChatS() {

    }

    @Override
    public void chatM() {
        if (this.typePk == ConstPlayer.NON_PK) {
            return;
        }
        if (this.data[this.currentLevel].getTextM().length == 0) {
            return;
        }
        if (!Util.canDoWithTime(this.lastTimeChatM, this.timeChatM)) {
            return;
        }
        String textChat = this.data[this.currentLevel].getTextM()[Util.nextInt(0, this.data[this.currentLevel].getTextM().length - 1)];
        int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
        textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
        this.chat(prefix, textChat);
        this.lastTimeChatM = System.currentTimeMillis();
        this.timeChatM = Util.nextInt(3000, 20000);
    }

    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        this.attack();
    }

    protected long lastTimeAttack;

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 100)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                                    SkillService.gI().useSkill(this, pl, null, null);
                        }else if (SkillUtil.isUseSkillCC(this)) {
                            if (pl.effectSkill.isStun || pl.effectSkill.anTroi || pl.effectSkill.isThoiMien || pl.effectSkill.isBlindDCTT) {
                                return;
                            }else
                            {
                                this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 100)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                                    SkillService.gI().useSkill(this, pl, null, null);
                            }
                        }
                        else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50));
                                    SkillService.gI().useSkill(this, pl, null, null);
                                }
                            }
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.print(RED + "Lỗi tại phương thức attack Boss" + RESET + "\n");
            }
        }
    }

    @Override
    public void checkPlayerDie(Player player) {
        if (player.isDie()) {
            this.chat("Chừa nha con!!!");
        }
    }

    public int getRangeCanAttackWithSkillSelect() {
        int skillId = this.playerSkill.skillSelect.template.id;
        if (skillId == Skill.KAMEJOKO || skillId == Skill.MASENKO || skillId == Skill.ANTOMIC) {
            return Skill.RANGE_ATTACK_CHIEU_CHUONG;
        } else if (skillId == Skill.DRAGON || skillId == Skill.DEMON || skillId == Skill.GALICK) {
            return Skill.RANGE_ATTACK_CHIEU_DAM;
        }
        return 500;
    }

    @Override
    public void die(Player plKill) {

        if (plKill != null
                && (this.zone.map.mapId != 140 || !MapService.gI().isMapMaBu(this.zone.map.mapId)
                || !MapService.gI().isMapDoanhTrai(this.zone.map.mapId)
                || !MapService.gI().isMapKhiGas(this.zone.map.mapId)
                || !MapService.gI().isMapBanDoKhoBau(this.zone.map.mapId)
                || !MapService.gI().isMapBlackBallWar(this.zone.map.mapId) || !(this instanceof MiNuong)
                || !(this instanceof AnTrom))) {
            reward(plKill);
            ServerNotify.gI().notify(plKill.name + " vừa tiêu diệt được " + this.name + ", ghê chưa ghê chưa..");
            this.changeStatus(BossStatus.DIE);
        } else {
            if (plKill != null) {
                reward(plKill);
            }
            this.changeStatus(BossStatus.DIE);
        }
    }

    @Override
    public void reward(Player plKill) {
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    @Override
    public boolean chatE() {
        if (Util.canDoWithTime(lastTimeChatE, timeChatE)) {
            if (this.indexChatE == this.data[this.currentLevel].getTextE().length) {
                return true;
            }
            String textChat = this.data[this.currentLevel].getTextE()[this.indexChatE];
            int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
            textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
            if (!this.chat(prefix, textChat)) {
                return false;
            }
            this.lastTimeChatE = System.currentTimeMillis();
            this.timeChatE = textChat.length() * 100;
            if (this.timeChatE > 2000) {
                this.timeChatE = 2000;
            }
            this.indexChatE++;
        }
        return false;
    }

    @Override
    public void doneChatE() {

    }

    //rời khỏi map
    @Override
    public void leaveMap() {
        if (this.currentLevel < this.data.length - 1) {
            this.lastZone = this.zone;
            this.changeStatus(BossStatus.RESPAWN);
        } else {
            if (this.id != BossID.SOI_HEC_QUYN && this.id != BossID.O_DO && this.id != BossID.CHA_PA && this.id != BossID.CHAN_XU
                    && this.id != BossID.JACKY_CHUN && this.id != BossID.LIU_LIU && this.id != BossID.PON_PUT
                    && this.id != BossID.TAU_PAY_PAY && this.id != BossID.THIEN_XIN_HANG
                    && this.id != BossID.THIEN_XIN_HANG_CLONE
                    && this.id != BossID.THIEN_XIN_HANG_CLONE1 && this.id != BossID.THIEN_XIN_HANG_CLONE2 && this.id != BossID.THIEN_XIN_HANG_CLONE3
                    && this.id != BossID.XINBATO && this.id != BossID.YAMCHA) {
                ChangeMapService.gI().spaceShipArrive(this, (byte) 2, ChangeMapService.DEFAULT_SPACE_SHIP);

            }
            ChangeMapService.gI().exitMap(this);
            this.lastZone = null;
            this.lastTimeRest = System.currentTimeMillis();
            this.changeStatus(BossStatus.REST);

        }
        this.wakeupAnotherBossWhenDisappear();

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
    public void moveToPlayer(Player player) {
        this.moveTo(player.location.x, player.location.y);
    }

    @Override
    public void moveTo(int x, int y) {
        byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
        byte move = (byte) Util.nextInt(40, 60);
        PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y + (Util.isTrue(3, 10) ? -10 : 0));
    }

    public void chat(String text) {
        try {
            Service.getInstance().chat(this, text);
        } catch (Exception ex) {
            Logger.getLogger(Boss.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected boolean chat(int prefix, String textChat) {
        if (prefix == -1) {
            this.chat(textChat);
        } else if (prefix == -2) {
            if (this.zone == null) {
                return false;
            }
            Player plMap = this.zone.getRandomPlayerInMap();
            if (plMap != null && !plMap.isDie() && Util.getDistance(this, plMap) <= 600) {
                try {
                    Service.getInstance().chat(plMap, textChat);
                } catch (Exception ex) {
                    Logger.getLogger(Boss.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                return false;
            }
        } else if (prefix == -3) {
            if (this.parentBoss != null && !this.parentBoss.isDie()) {
                this.parentBoss.chat(textChat);
            }
        } else if (prefix >= 0) {
            if (this.bossAppearTogether != null && this.bossAppearTogether[this.currentLevel] != null) {
                Boss boss = this.bossAppearTogether[this.currentLevel][prefix];
                if (!boss.isDie()) {
                    boss.chat(textChat);
                }
            } else if (this.parentBoss != null && this.parentBoss.bossAppearTogether != null
                    && this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel] != null) {
                Boss boss = this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel][prefix];
                if (!boss.isDie()) {
                    boss.chat(textChat);
                }
            }
        }
        return true;
    }

    @Override
    public void wakeupAnotherBossWhenAppear() {
        if (!MapService.gI().isMapMaBu(this.zone.map.mapId)
                && !MapService.gI().isMapBlackBallWar(this.zone.map.mapId)
                && !(this instanceof AnTrom)
                && !(this instanceof MiNuong)) {
        
            System.out.print(GREEN + "Boss " + this.name + " : " + this.zone.map.mapName + " khu" + this.zone.zoneId + "(mapid: " + this.zone.map.mapId + ")" + RESET + "\n");
        }
        if (this.bossAppearTogether == null || this.bossAppearTogether[this.currentLevel] == null) {
            return;
        }
        for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
            int nextLevelBoss = boss.currentLevel + 1;
            if (nextLevelBoss >= boss.data.length) {
                nextLevelBoss = 0;
            }
            if (boss.data[nextLevelBoss].getTypeAppear() == TypeAppear.CALL_BY_ANOTHER) {
                if (boss.zone != null) {
                    boss.leaveMap();
                }
            }
            if (boss.data[nextLevelBoss].getTypeAppear() == TypeAppear.APPEAR_WITH_ANOTHER) {
                if (boss.zone != null) {
                    boss.leaveMap();
                }
                boss.changeStatus(BossStatus.RESPAWN);
            }
        }
    }

    @Override
    public void wakeupAnotherBossWhenDisappear() {
        System.out.println("Boss " + this.name + " vừa bị tiêu diệt");
    }

}
