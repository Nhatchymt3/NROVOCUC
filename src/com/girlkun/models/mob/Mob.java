package com.girlkun.models.mob;

import com.girlkun.consts.ConstMap;
import com.girlkun.consts.ConstMob;
import com.girlkun.consts.ConstTask;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;

import java.util.List;

import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Location;
import com.girlkun.models.player.Pet;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.Thu_TrieuHoi;
import com.girlkun.models.reward.ItemMobReward;
import com.girlkun.models.reward.MobReward;
import com.girlkun.models.skill.PlayerSkill;
import com.girlkun.models.skill.Skill;
import com.girlkun.network.io.Message;
import com.girlkun.server.Maintenance;
import com.girlkun.server.Manager;
import com.girlkun.services.*;
import com.girlkun.utils.Logger;
import static com.girlkun.utils.Logger.RED;
import static com.girlkun.utils.Logger.RESET;
import com.girlkun.utils.Util;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Random;

public class Mob {

    public int id;
    public Zone zone;
    public int tempId;
    public String name;
    public byte level;

    public MobPoint point;
    public MobEffectSkill effectSkill;
    public Location location;

    public byte pDame;
    public int pTiemNang;
    private long maxTiemNang;

    public long lastTimeDie;
    public int lvMob = 0;
    public int status = 5;

    public boolean isMobMe;

    public Mob(Mob mob) {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
        this.id = mob.id;
        this.tempId = mob.tempId;
        this.level = mob.level;
        this.point.setHpFull(mob.point.getHpFull());
        this.point.sethp(this.point.getHpFull());
        this.location.x = mob.location.x;
        this.location.y = mob.location.y;
        this.pDame = mob.pDame;
        this.pTiemNang = mob.pTiemNang;
        this.setTiemNang();
    }

    public Mob() {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
    }

    public static void initMopbKhiGas(Mob mob, byte level) {
        mob.point.dame = (level * 3250 * mob.level * 4) * 5;
        mob.point.maxHp = (level * 12472 * mob.point.hp + level * 7263 * mob.tempId) / 2;

    }

    public static void initMopbbdkb(Mob mob, byte level) {
        mob.point.dame = (level * 3250 * mob.level * 4) * 5;
        mob.point.maxHp = (level * 12472 * mob.point.hp + level * 7263 * mob.tempId) / 2;
        if (mob.point.dame >= 2000000000L) {
            mob.point.dame = 2000000000L;
        }
        if (mob.point.maxHp >= 2000000000L) {
            mob.point.maxHp = 2000000000L;
        }

    }

    public void setTiemNang() {
        this.maxTiemNang = Util.DoubleGioihan(this.point.getHpFull() * (this.pTiemNang + Util.nextInt(-2, 2)) / 100);
    }

    private long lastTimeAttackPlayer;

    public boolean isDie() {
        return this.point.gethp() <= 0;
    }

    public boolean isSieuQuai() {
        return this.lvMob > 0;
    }

    public boolean isQuaiBay() {
        return this.tempId == 10 || this.tempId == 69 || this.tempId == 30 || this.tempId == 37
                || this.tempId == 21 || this.tempId == 49 || this.tempId == 25 || this.tempId == 33 || this.tempId == 32
                || this.tempId == 28 || this.tempId == 29 || this.tempId == 31 || this.tempId == 12 || this.tempId == 79
                || this.tempId == 75 || this.tempId == 11 || this.tempId == 50 || this.tempId == 43
                || this.tempId == 9 || this.tempId == 8 || this.tempId == 7;
    }

    public boolean isQuaiSen() {
        return this.tempId == 58 || this.tempId == 59 || this.tempId == 60 || this.tempId == 61
                || this.tempId == 62 || this.tempId == 63 || this.tempId == 64 || this.tempId == 65;
    }

    public synchronized void injured(Player plAtt, double damage, boolean dieWhenHpFull) {
        if (!this.isDie()) {
            if (damage >= this.point.hp) {
                damage = this.point.hp;
            }
            if (!dieWhenHpFull) {
                if (this.point.hp == this.point.maxHp && damage >= this.point.hp) {
                    damage = this.point.hp - 1;
                }
                if (this.tempId == 0 && damage > 10) {
                    damage = 10;
                }
            }
            if (plAtt != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                        if (plAtt.nPoint.multicationChuong > 0 && Util.canDoWithTime(plAtt.nPoint.lastTimeMultiChuong, PlayerSkill.TIME_MUTIL_CHUONG)) {
                            damage *= plAtt.nPoint.multicationChuong;
                            plAtt.nPoint.lastTimeMultiChuong = System.currentTimeMillis();
                        }

                }
            }
            this.point.hp -= damage;
            if (this.isDie()) {
                this.lvMob = 0;
                this.status = 0;
                this.sendMobDieAffterAttacked(plAtt, damage);
                TaskService.gI().checkDoneTaskKillMob(plAtt, this);
                TaskService.gI().checkDoneSideTaskKillMob(plAtt, this);
                this.lastTimeDie = System.currentTimeMillis();
                if (this.id == 13) {
                    this.zone.isbulon13Alive = false;
                }
                if (this.id == 14) {
                    this.zone.isbulon14Alive = false;
                }
                if (plAtt != null && this.isQuaiBay() == true) {
                    if (plAtt.chienthan.tasknow == 5) {
                        plAtt.chienthan.dalamduoc++;
                    }
                    plAtt.achievement.plusCount(6);
                }
                if (plAtt != null && this.tempId == 0) {
                    plAtt.achievement.plusCount(7);
                }
            } else {
                this.sendMobStillAliveAffterAttacked(damage, plAtt != null ? plAtt.nPoint.isCrit : false);
            }
            if (plAtt != null) {
                Service.getInstance().addSMTN(plAtt, (byte) 2, getTiemNangForPlayer(plAtt, damage), true);
            }
        }
    }

    public boolean isMemInMap(Player me) {
        if (me.clan != null) {
            if (me.zone.players.stream().allMatch(pl -> pl != null && !pl.isBoss && !pl.isNewPet && pl.zone.zoneId == me.zone.zoneId && pl.clan != null && pl.clan.id == me.clan.id)) {
                return true;
            }
        }
        return false;
    }

    public long getTiemNangForPlayer(Player pl, double dame) {
        int levelPlayer = Service.getInstance().getCurrLevel(pl);
        int n = levelPlayer - this.level;
        long pDameHit = 0;
        if (point.getHpFull() >= 100000000) {
            pDameHit = Util.DoubleGioihan(dame) * 500 / Util.DoubleGioihan(point.getHpFull());
        } else {
            pDameHit = Util.DoubleGioihan(dame) * 100 / Util.DoubleGioihan(point.getHpFull());
        }

        long tiemNang = pDameHit * maxTiemNang / 100;
        if (n >= 0) {
            for (int i = 0; i < n; i++) {
                long sub = tiemNang * 10 / 100;
                if (sub <= 0) {
                    sub = 1;
                }
                tiemNang -= sub;
            }
        } else {
            for (int i = 0; i < -n; i++) {
                long add = tiemNang * 10 / 100;
                if (add <= 0) {
                    add = 1;
                }
                tiemNang += add;
            }
        }
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        tiemNang = Util.DoubleGioihan(pl.nPoint.calSucManhTiemNang(tiemNang));
        if (pl.zone.map.mapId >= 122 && pl.zone.map.mapId <= 124) {
            if (pl.nPoint.power < 20000000000L) {
                tiemNang *= 2;
            } else {
                tiemNang = 0;
            }
        }
        if (pl.zone.map.mapId == 155) {
            tiemNang *= 5;
        }
        if (pl.zone.items.stream().anyMatch(it -> it != null && (it.playerId == pl.id || isMemInMap(pl)) && it.itemTemplate.id == 343 && Util.getDistance(it.x, it.y, pl.location.x, pl.location.y) <= 200)) {
            tiemNang += tiemNang * 20 / 100;
        }
        return tiemNang;
    }

    public void update() {
        if (this.isDie() && !Maintenance.isRuning) {
            switch (zone.map.type) {
                case ConstMap.MAP_DOANH_TRAI:
                    if (this.zone.isTrungUyTrangAlive == true) {
                        if (this.tempId == 22 && this.zone.map.mapId == 59) {
                            if (Util.canDoWithTime(lastTimeDie, 5000)) {
                                if (this.id == 13) {
                                    this.zone.isbulon13Alive = true;
                                }
                                if (this.id == 14) {
                                    this.zone.isbulon14Alive = true;
                                }
                                this.hoiSinh();
                                this.sendMobHoiSinh();
                            }
                        }
                    }
                    break;
                case ConstMap.MAP_BAN_DO_KHO_BAU:
                    if (this.tempId == 72 || this.tempId == 71) {//ro bot bao ve
                        if (System.currentTimeMillis() - this.lastTimeDie > 3000) {
                            try {
                                Message t = new Message(102);
                                t.writer().writeByte((this.tempId == 71 ? 7 : 6));
                                Service.getInstance().sendMessAllPlayerInMap(this.zone, t);
                                t.cleanup();
                            } catch (IOException e) {
                                System.out.println("lỗi update map bdkb");
                            }
                        }
                    }
                    break;
                case ConstMap.MAP_KHI_GAS:
                    break;
                default:
                    if (Util.canDoWithTime(lastTimeDie, 5000)) {
                        this.randomSieuQuai();
                        this.hoiSinh();
                        this.sendMobHoiSinh();
                    }
            }
        }
        effectSkill.update();
        attackPlayer();
    }

    private void attackPlayer() {
        if (!isDie() && !effectSkill.isHaveEffectSkill() && !(tempId == 0)) {

            if ((this.tempId == 72 || this.tempId == 71) && Util.canDoWithTime(lastTimeAttackPlayer, 300)) {
                List<Player> pl = getListPlayerCanAttack();
                if (!pl.isEmpty()) {
                    this.sendMobBossBdkbAttack(pl, Util.DoubleGioihan(this.point.getDameAttack()));
                } else {
                    if (this.tempId == 71) {
                        Player plA = getPlayerCanAttack();
                        if (plA != null) {
                            try {
                                Message t = new Message(102);
                                t.writer().writeByte(5);
                                t.writer().writeByte(plA.location.x);
                                this.location.x = plA.location.x;
                                Service.getInstance().sendMessAllPlayerInMap(this.zone, t);
                                t.cleanup();
                            } catch (IOException e) {
                            }
                        }

                    }
                }
                this.lastTimeAttackPlayer = System.currentTimeMillis();
            } else if (Util.canDoWithTime(lastTimeAttackPlayer, 2000)) {
                Player pl = getPlayerCanAttack();
                if (pl != null) {
                    this.mobAttackPlayer(pl);
                }
                this.lastTimeAttackPlayer = System.currentTimeMillis();
            }

        }
    }

    private void sendMobBossBdkbAttack(List<Player> players, long dame) {
        if (this.tempId == 72) {
            try {
                Message t = new Message(102);
                int action = Util.nextInt(0, 2);
                t.writer().writeByte(action);
                if (action != 1) {
                    this.location.x = players.get(Util.nextInt(0, players.size() - 1)).location.x;
                }
                t.writer().writeByte(players.size());
                for (Byte i = 0; i < players.size(); i++) {
                    t.writer().writeInt((int) players.get(i).id);
                    t.writer().writeInt((int) players.get(i).injured(null, (int) dame, false, true));
                }
                Service.getInstance().sendMessAllPlayerInMap(this.zone, t);
                t.cleanup();
            } catch (IOException e) {
            }
        } else if (this.tempId == 71) {
            try {
                Message t = new Message(102);
                t.writer().writeByte(Util.getOne(3, 4));
                t.writer().writeByte(players.size());
                for (Byte i = 0; i < players.size(); i++) {
                    t.writer().writeInt((int) players.get(i).id);
                    t.writer().writeInt((int) players.get(i).injured(null, (int) dame, false, true));
                }
                Service.getInstance().sendMessAllPlayerInMap(this.zone, t);
                t.cleanup();
            } catch (IOException e) {
            }
        }
    }

    private List<Player> getListPlayerCanAttack() {
        List<Player> plAttack = new ArrayList<>();
        int distance = (this.tempId == 71 ? 250 : 600);
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && !pl.isBoss && !pl.effectSkin.isVoHinh) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= distance) {
                        plAttack.add(pl);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("loi ne mob ne 1 ");
        }

        return plAttack;
    }

    private Player getPlayerCanAttack() {
        try {
            List<Player> players = this.zone.getNotBosses();

            Player plAttack = players.stream()
                    .filter(pl -> !pl.isDie() && !pl.isBoss && !pl.effectSkin.isVoHinh && Util.getDistance(pl, this) <= 100)
                    .min(Comparator.comparingInt(pl -> Util.getDistance(pl, this)))
                    .orElse(null);

            return plAttack;
        } catch (Exception e) {

            System.out.print(RED + "Lỗi khi lấy người chơi có thể tấn công" + e.getMessage() + RESET + "\n");
            return null;
        }
    }

    //**************************************************************************
    private void mobAttackPlayer(Player player) {
        double dameMob = this.point.getDameAttack();
        if (!player.isDie() && !player.isNewPet && !player.isBoss && !player.zone.items.stream().anyMatch(it -> it != null && (it.playerId == player.id || isMemInMap(player)) && it.itemTemplate.id == 344 && Util.getDistance(it.x, it.y, player.location.x, player.location.y) <= 200)) {
            if (player.charms.tdDaTrau > System.currentTimeMillis()) {
                dameMob /= 2;
            }
            if (this.isSieuQuai()) {
                dameMob = player.nPoint.hpMax / 10;
            }
            double dame = player.injured(null, dameMob, false, true);
            this.sendMobAttackMe(player, dame);
            this.sendMobAttackPlayer(player);
        }
    }

    private void sendMobAttackMe(Player player, double dame) {
        if (!player.isPet && !player.isNewPet && !player.isTrieuhoipet) {
            Message msg;
            try {
                msg = new Message(-11);
                msg.writer().writeByte(this.id);
                msg.writer().writeInt(Util.DoubleGioihan(dame));
                player.sendMessage(msg);
                msg.cleanup();
            } catch (IOException e) {
            }
        }
    }

    private void sendMobAttackPlayer(Player player) {
        Message msg;
        try {
            msg = new Message(-10);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeInt(Util.DoubleGioihan(player.nPoint.hp));
            Service.getInstance().sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void randomSieuQuai() {
        if (this.tempId != 0 && MapService.gI().isMapKhongCoSieuQuai(this.zone.map.mapId) && Util.nextInt(0, 150) < 1) {
            this.lvMob = 1;
        }
    }

    public void hoiSinh() {
        this.status = 5;
        this.point.hp = this.point.maxHp;
        this.setTiemNang();
    }

    public void sendMobHoiSinh() {
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(lvMob);
            msg.writer().writeInt(Util.DoubleGioihan(this.point.hp));
            Service.getInstance().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    //**************************************************************************
    private void sendMobDieAffterAttacked(Player plKill, double dameHit) {
        Message msg;
        try {
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(Util.DoubleGioihan(dameHit));
            msg.writer().writeBoolean(plKill.nPoint.isCrit); // crit
            List<ItemMap> items = mobReward(plKill, this.dropItemTask(plKill), msg);
            Service.getInstance().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
            hutItem(plKill, items);
        } catch (IOException e) {
        }
    }

    public void sendMobDieAfterMobMeAttacked(Player plKill, int dameHit) {
        this.status = 0;
        Message msg;
        try {
            if (this.id == 13) {
                this.zone.isbulon13Alive = false;
            }
            if (this.id == 14) {
                this.zone.isbulon14Alive = false;
            }
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(dameHit);
            msg.writer().writeBoolean(false); // crit

            List<ItemMap> items = mobReward(plKill, this.dropItemTask(plKill), msg);
            Service.getInstance().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
            hutItem(plKill, items);
        } catch (IOException e) {
            Logger.logException(Mob.class, e);
        }
        this.lastTimeDie = System.currentTimeMillis();
    }

    private void hutItem(Player player, List<ItemMap> items) {
        if (player.isPl()) {
            if (player.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    ItemMapService.gI().pickItem(player, item.itemMapId, true);
                }
            }
        } else if (player.isTrieuhoipet) {
            if (((Thu_TrieuHoi) player).masterr.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    ItemMapService.gI().pickItem(((Thu_TrieuHoi) player).masterr, item.itemMapId, true);
                }
            }
        } else {
            if (((Pet) player).master.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    ItemMapService.gI().pickItem(((Pet) player).master, item.itemMapId, true);
                }
            }
        }
    }

    private List<ItemMap> mobReward(Player player, ItemMap itemTask, Message msg) {
        List<ItemMap> itemReward = new ArrayList<>();
        try {
            int mapid = player.zone.map.mapId;
            if ((!player.isPet && player.setClothes.setDTS == 5) || (player.isPet && ((Pet) player).setClothes.setDTS == 5)) {
                if (Util.isTrue(30, 100)) {
                    byte random = 1;
                    if (Util.isTrue(2, 100)) {
                        random = 2;
                    }
                    Item i = Manager.RUBY_REWARDS.get(Util.nextInt(0, Manager.RUBY_REWARDS.size() - 1));
                    i.quantity = random;
                    InventoryServiceNew.gI().addItemBag(player, i);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + random + " hồng ngọc");
                }
            }

            itemReward = this.getItemMobReward(player, this.location.x + Util.nextInt(-10, 10),
                    this.location.y);
            if (itemTask != null) {
                itemReward.add(itemTask);
            }
            msg.writer().writeByte(itemReward.size()); //sl item roi
            for (ItemMap itemMap : itemReward) {
                msg.writer().writeShort(itemMap.itemMapId);// itemmapid
                msg.writer().writeShort(itemMap.itemTemplate.id); // id item
                msg.writer().writeShort(itemMap.x); // xend item
                msg.writer().writeShort(itemMap.y); // yend item
                msg.writer().writeInt((int) itemMap.playerId); // id nhan nat
            }
        } catch (IOException e) {
            System.out.println("llllll");
        }
        return itemReward;

    }

    public List<ItemMap> getItemMobReward(Player player, int x, int yEnd) { //quái rơi vật phẩm
        List<ItemMap> list = new ArrayList<>();
        final Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(11);

        int tileVang = 0;
        byte randommanhan = (byte) new Random().nextInt(Manager.manhtinhan.length); // rơi mảnh tinh ấn
        byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length); // rơi đồ thần linh
        byte randomVp = (byte) new Random().nextInt(Manager.itemSkien.length);
        byte randommanhthiensu = (byte) new Random().nextInt(Manager.manhthiensu.length);
        byte randomspl = (byte) new Random().nextInt(Manager.spl.length); //rơi sao pha lê
        byte randomthucan = (byte) new Random().nextInt(Manager.thucan.length); //rơi thức ăn
        byte randomdanangcap = (byte) new Random().nextInt(Manager.danangcap.length);
        byte randomchienthan = (byte) new Random().nextInt(Manager.dachienthan.length);
        byte radaThuong = (byte) new Random().nextInt(Manager.manhradaThuong.length);
        byte radaVIP = (byte) new Random().nextInt(Manager.manhradaVIP.length);
        byte randomngocrong = (byte) new Random().nextInt(Manager.itemIds_NR.length);
        byte randomHallowen = (byte) new Random().nextInt(Manager.itemSukienhallowen.length);
        byte randomTrungthu = (byte) new Random().nextInt(Manager.SuKien_TrungThu.length);

        byte randomitemsieucap = (byte) new Random().nextInt(Manager.itemsieucap.length);
        byte skhtd = (byte) new Random().nextInt(Manager.settd.length);
        byte skhnm = (byte) new Random().nextInt(Manager.setnm.length);
        byte skhxd = (byte) new Random().nextInt(Manager.setxd.length);

        if (player.cFlag >= 1 && Util.isTrue(100, 100) && this.tempId == 0 && hour % 2 != 0 && hour < 24) {
            for (int i = 0; i < 4; i++) {
                if (Util.isTrue(50, 100) && this.tempId == 0) {
                    list.add(new ItemMap(zone, 590, 1, x, player.location.y, player.id));
                }
            }
        }

        //roi cskb
        if (player.itemTime.isUseMayDo && Util.isTrue(20, 100) && this.tempId > 57 && this.tempId < 66) {
            if (player.chienthan.tasknow == 4) {
                player.chienthan.dalamduoc++;
            }
            list.add(new ItemMap(zone, 380, 1, x, player.location.y, player.id));
            player.achievement.plusCount(5);
        }

        //roi ngocrong
        if (Util.isTrue(5, 100)) {
            list.add(new ItemMap(zone, Manager.itemIds_NR[randomngocrong], Util.nextInt(1, 2), this.location.x, this.location.y, player.id));
        }
        // if (Util.isTrue(1, 1000)) {
        //     list.add(new ItemMap(zone, 16, 1, this.location.x, this.location.y, player.id));
        // }
        // if (Util.isTrue(1, 1000)) {
        //     list.add(new ItemMap(zone, 752, 1, this.location.x, this.location.y, player.id));
        // }
        if (Util.isTrue(1, 300)) {
            list.add(new ItemMap(zone, Util.nextInt(750, 751), 1, this.location.x, this.location.y, player.id));// vật phẩm lá dong và gạo nếp
        }
        if (Util.isTrue(1, 2000)) {
            list.add(new ItemMap(zone, 1517, Util.nextInt(1, 2), this.location.x, this.location.y, player.id));// đá nâng cấp pet
        }
        // if (Util.isTrue(1, 300)) {
        //     list.add(new ItemMap(zone, 457, Util.nextInt(1, 2), this.location.x, this.location.y, player.id));
        // }
        if (Manager.SUKIEN == 6) {
            if (Util.isTrue(1, 5)) {
                list.add(new ItemMap(zone, 533, Util.nextInt(1, 2), this.location.x, this.location.y, player.id));// pháo bông => 6 là sự kiện tết
            }
        }
        if (Util.isTrue(1, 3)) {
            list.add(new ItemMap(zone, 1854, 1, this.location.x, this.location.y, player.id));
        }
        if (this.zone.map.mapId >= 160 && this.zone.map.mapId <= 163) {
            if (Util.isTrue(1, 200)) {
                list.add(new ItemMap(zone, Manager.itemsieucap[randomitemsieucap], 1, this.location.x, this.location.y, player.id));
            }
        }

        // if (Util.isTrue(1, 1000)) {
        //     list.add(new ItemMap(zone, 649, 1, this.location.x, this.location.y, player.id));// tất vớ giáng sinh
        // }
        if (this.zone.map.mapId == 131) {
            if (Util.isTrue(1, 1)) {
                list.add(new ItemMap(zone, 1634, Util.nextInt(1, 50), this.location.x, this.location.y, player.id));
            }
        }
        //////Sự kiện HALLOWEN ///////
        if (player.itemTime.isgiodungkeo) {
            if (Util.isTrue(1, 5)) {
                list.add(new ItemMap(zone, Manager.itemSukienhallowen[randomHallowen], 1, this.location.x, this.location.y, player.id));
            }

        }
        tileVang = player.nPoint.tlGold / 100;
        if (Util.isTrue(5, 100)) {
            int vang = (Util.nextInt(30000, 50000) + Util.nextInt(30000, 50000) * tileVang);
            list.add(new ItemMap(zone, 190, vang, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(5, 100) && (this.zone.map.mapId == 0 || this.zone.map.mapId == 7 || this.zone.map.mapId == 14)) {
            list.add(new ItemMap(zone, 1215, 10, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(1, 1) && (this.zone.map.mapId == 173)) {
            list.add(new ItemMap(zone, Manager.manhtinhan[randommanhan], 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(1, 1000) && this.zone.map.mapId >= 141 && this.zone.map.mapId <= 142) {
            list.add(new ItemMap(zone, Manager.manhradaThuong[radaThuong], 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(120, 10000) && this.zone.map.mapId >= 202 && this.zone.map.mapId <= 203) {
            list.add(new ItemMap(zone, Manager.manhradaVIP[radaVIP], 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(50, 1000) && this.zone.map.mapId == 171) {
            list.add(new ItemMap(zone, Manager.dachienthan[randomchienthan], 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(50, 1000) && this.zone.map.mapId >= 202 && this.zone.map.mapId <= 203) {
            list.add(new ItemMap(zone, Manager.dachienthan[randomchienthan], 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(3, 100) && this.zone.map.mapId > 202 && this.zone.map.mapId < 203) {
            list.add(new ItemMap(zone, 2031, 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(5, 100)) {
            list.add(Util.ratiDa(zone, Manager.danangcap[randomdanangcap], 1, this.location.x, this.location.y, player.id));
        }

        //////////////////////////////////////////////////////////////////////////
        //roi thuc an đã fix
        if (Util.isTrue(50, 100) && player.setClothes.godClothes && MapService.gI().isMapCold(this.zone.map)) {
            list.add(new ItemMap(zone, Manager.thucan[randomthucan], 1, this.location.x, this.location.y, player.id));
        }
        /// roi sao pha le 
        if (Util.isTrue(1, 100) && this.zone.map.mapId > 1 && this.zone.map.mapId < 200) {
            list.add(Util.ratiSpl(zone, Manager.spl[randomspl], 1, this.location.x, this.location.y, player.id));
        }

        /////////////////

        // rơi skh thường
        // if (Util.isTrue(1, 20) && this.zone.map.mapId >= 1 && this.zone.map.mapId <= 17) {
        //     if (player.gender == 0) {
        //         list.add(Util.settdlvl(zone, Manager.settd[skhtd], 1, this.location.x, this.location.y, player.id));
        //     } else if (player.gender == 1) {
        //         list.add(Util.setnmlvl(zone, Manager.setnm[skhnm], 1, this.location.x, this.location.y, player.id));
        //     } else {
        //         list.add(Util.setxdlvl(zone, Manager.setxd[skhxd], 1, this.location.x, this.location.y, player.id));

        //     }
        // }

        ///////////////////////////////////////////
        //////////////////////////////////// roi manh bong tai // manh hon bong tai
        if (Util.isTrue(1, 1) && this.zone.map.mapId == 156) {
            list.add(new ItemMap(zone, 933, 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(1, 10) && this.zone.map.mapId == 156) {
            list.add(new ItemMap(zone, 934, 1, this.location.x, this.location.y, player.id));
        }
        //roi manhts    
        if (Util.isTrue(1, 300) && this.zone.map.mapId == 155 && player.setClothes.setDHD >= 5) {
            list.add(new ItemMap(zone, Manager.manhthiensu[randommanhthiensu], Util.nextInt(1, 3), this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(40, 100) && this.tempId < 1 && (this.zone.map.mapId == 131)) {
            list.add(new ItemMap(zone, 590, Util.nextInt(15, 30), this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(1, 10000) && this.zone.map.mapId > 0 && this.zone.map.mapId < 203) {
            list.add(new ItemMap(zone, 17, 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(3, 10000) && this.zone.map.mapId > 0 && this.zone.map.mapId < 203) {
            list.add(new ItemMap(zone, 18, 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(4, 10000) && this.zone.map.mapId > 0 && this.zone.map.mapId < 203) {
            list.add(new ItemMap(zone, 19, 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(5, 10000) && this.zone.map.mapId > 0 && this.zone.map.mapId < 203) {
            list.add(new ItemMap(zone, 20, 1, this.location.x, this.location.y, player.id));
        }
        //rơi đồ thân linh
        if (Util.isTrue(1, 2000) && MapService.gI().isMapCold(this.zone.map)) {
            if (player.chienthan.tasknow == 2) {
                player.chienthan.dalamduoc++; //cộng điểm chiến thần
            }
            list.add(Util.ratiItem1(zone, Manager.itemIds_TL[randomDo], 1, this.location.x, this.location.y, player.id));
        }

        if (MapService.gI().isMapBanDoKhoBau(player.zone.map.mapId)) {
            int levell = player.clan.banDoKhoBau.level;
            int slhn = 1 * (levell / 10);
            if (Util.nextInt(0, 80) < 70) {
                list.add(new ItemMap(zone, 861, slhn, x, player.location.y, player.id));
                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + slhn + " hồng ngọc");
            }
        }
        if (Util.isTrue(30, 100) && this.tempId > 76 && this.tempId < 78) {
            byte random = 1;
            if (Util.isTrue(2, 100)) {
                random = 2;
            }
            Item i = Manager.RUBY_REWARDS.get(Util.nextInt(0, Manager.RUBY_REWARDS.size() - 1));
            i.quantity = random;
            InventoryServiceNew.gI().addItemBag(player, i);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + random + " hồng ngọc");
        }
        //=====================================trung thu=========================
        if (Manager.SUKIEN == 1 && Util.isTrue(5, 100)) {// 1 là sự kiện trung thu
            if (this.isQuaiBay()) {
                list.add(new ItemMap(zone, Manager.SuKien_TrungThu[2], 1, this.location.x, this.location.y, player.id));
            }
            if (!this.isQuaiBay() && !this.isQuaiSen()) {
                list.add(new ItemMap(zone, Manager.SuKien_TrungThu[1], 1, this.location.x, this.location.y, player.id));
            }
            if (this.isQuaiSen()) {
                list.add(new ItemMap(zone, Manager.SuKien_TrungThu[0], 1, this.location.x, this.location.y, player.id));
            }
        }

        return list;
    }

    private ItemMap dropItemTask(Player player) {
        ItemMap itemMap = null;
        switch (this.tempId) {
            case ConstMob.KHUNG_LONG:
            case ConstMob.LON_LOI:
            case ConstMob.QUY_DAT:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_2_0) {
                    itemMap = new ItemMap(this.zone, 73, 1, this.location.x, this.location.y, player.id);
                }
                break;
        }
        if (itemMap != null) {
            return itemMap;
        }
        return null;
    }

    private void sendMobStillAliveAffterAttacked(double dameHit, boolean crit) {
        Message msg;
        try {
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(Util.DoubleGioihan(this.point.gethp()));
            msg.writer().writeInt(Util.DoubleGioihan(dameHit));
            msg.writer().writeBoolean(crit); // chí mạng
            msg.writer().writeInt(-1);
            Service.getInstance().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }
}
