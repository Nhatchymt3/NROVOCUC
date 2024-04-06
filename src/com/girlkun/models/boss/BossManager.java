package com.girlkun.models.boss;

import BossDetu.DetuMabu;
import com.girlkun.models.boss.list_boss.AnTrom;
import com.girlkun.models.boss.list_boss.BLACK.*;
import com.girlkun.models.boss.list_boss.Cooler.Cooler;

import com.girlkun.models.boss.list_boss.FideBack.Kingcold;
import BossDetu.DetuBerus;
import BossDetu.DetuBroly;
import BossDetu.DetuMabu;
import BossDetu.DetuWukong;
import BossThucung.Lancon;
import BossThucung.Lanme;
import BossThucung.Thucung;
import com.girlkun.models.boss.list_boss.Fusion.GogetaSSJ4;
import com.girlkun.models.boss.list_boss.Fusion.Zamus;
import com.girlkun.models.boss.list_boss.cell.Xencon;
import com.girlkun.models.boss.list_boss.ginyu.TDST;
import com.girlkun.models.boss.list_boss.android.*;
import com.girlkun.models.boss.list_boss.cell.SieuBoHung;
import com.girlkun.models.boss.list_boss.cell.XenBoHung;
import com.girlkun.models.boss.list_boss.Fusion.SuperAndroid17;
import com.girlkun.models.boss.list_boss.Fusion.kakarot;

import com.girlkun.models.boss.list_boss.FideBack.FideRobot;

import com.girlkun.models.boss.list_boss.fide.Fide;
import com.girlkun.models.boss.list_boss.NRD.Rong1Sao;
import com.girlkun.models.boss.list_boss.NRD.Rong2Sao;
import com.girlkun.models.boss.list_boss.NRD.Rong3Sao;
import com.girlkun.models.boss.list_boss.NRD.Rong4Sao;
import com.girlkun.models.boss.list_boss.NRD.Rong5Sao;
import com.girlkun.models.boss.list_boss.NRD.Rong6Sao;
import com.girlkun.models.boss.list_boss.NRD.Rong7Sao;
import com.girlkun.models.boss.list_boss.Mabu12h.MabuBoss;
import com.girlkun.models.boss.list_boss.Mabu12h.BuiBui;
import com.girlkun.models.boss.list_boss.Mabu12h.BuiBui2;
import com.girlkun.models.boss.list_boss.Mabu12h.Drabura;
import com.girlkun.models.boss.list_boss.Mabu12h.Drabura2;
import com.girlkun.models.boss.list_boss.Mabu12h.Yacon;
import com.girlkun.models.boss.list_boss.MiNuong;
//import com.girlkun.models.boss.list_boss.ThoTrang;
import com.girlkun.models.boss.list_boss.doanh_trai.NinjaTim;
import com.girlkun.models.boss.list_boss.doanh_trai.RobotVeSi;
import com.girlkun.models.boss.list_boss.doanh_trai.TrungUyThep;
import com.girlkun.models.boss.list_boss.doanh_trai.TrungUyTrang;
import com.girlkun.models.boss.list_boss.doanh_trai.TrungUyXanhLo;
import com.girlkun.models.boss.list_boss.nappa.*;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.network.io.Message;
import com.girlkun.server.ServerManager;
import com.girlkun.services.ItemMapService;
import com.girlkun.services.MapService;
import com.nroluz.models.boss.boss_new.BossAndroid17;
import com.nroluz.models.boss.boss_new.BossCumber; //new
import com.nroluz.models.boss.boss_new.BossToppo; //new

import com.nroluz.models.boss.boss_new.Boss_Minuong;
import com.nroluz.models.boss.boss_new.OngGiaNoel;
import com.nroluz.models.boss.boss_new.BossBabyVegeta;
import com.nroluz.models.boss.boss_new.PicoloDaikamao;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BossManager implements Runnable {

    boolean bossongia = true;
    protected ConcurrentHashMap<Integer, Long> setTimeSpawnBoss;
    private static BossManager I;
    public static final byte ratioReward = 2;

    public static BossManager gI() {
        if (BossManager.I == null) {
            BossManager.I = new BossManager();
        }
        return BossManager.I;
    }

    private BossManager() {
        this.bosses = new ArrayList<>();
        this.setTimeSpawnBoss = new ConcurrentHashMap<>();
    }

    private boolean loadedBoss;
    private final List<Boss> bosses;

    public void addBoss(Boss boss) {
        this.bosses.add(boss);
    }

    public List<Boss> getBosses() {
        return this.bosses;
    }

    public void removeBoss(Boss boss) {
        this.bosses.remove(boss);
    }

    public void loadBoss() {
        if (this.loadedBoss) {
            return;
        }
        try {
            this.createBoss(BossID.TDST);
            this.createBoss(BossID.PIC);
            this.createBoss(BossID.POC);
            this.createBoss(BossID.KING_KONG);
            this.createBoss(BossID.SONGOKU_TA_AC);
            this.createBoss(BossID.CUMBER);
            this.createBoss(BossID.COOLER_GOLD);
            this.createBoss(BossID.XEN_BO_HUNG);
            this.createBoss(BossID.SIEU_BO_HUNG);
            this.createBoss(BossID.XEN_CON_1);
            this.createBoss(BossID.ZAMASZIN);
            this.createBoss(BossID.BLACK2);
            this.createBoss(BossID.BLACK);
            this.createBoss(BossID.BLACK3);
            this.createBoss(BossID.bossminuong);
            this.createBoss(BossID.KUKU);
            this.createBoss(BossID.MAP_DAU_DINH);
            this.createBoss(BossID.RAMBO);
            this.createBoss(BossID.FIDE);
            this.createBoss(BossID.DR_KORE);
            this.createBoss(BossID.ANDROID_14);
            this.createBoss(BossID.BOSS_DETU_BERUS);
            this.createBoss(BossID.BOSS_Android17);
            this.createBoss(BossID.BOSS_DETU_BL);
            this.createBoss(BossID.COOLER);
            this.createBoss(BossID.BOSS_NRO1S1);
            this.createBoss(BossID.BOSS_NRO1S2);
            this.createBoss(BossID.BOSS_NRO1S3);
            this.createBoss(BossID.BOSS_NRO1S4);
            this.createBoss(BossID.BOSS_Cumber);
            this.createBoss(BossID.BOSS_Toppo);
            this.createBoss(BossID.SUPER_ANDROID_17);
            this.createBoss(BossID.GogetaSJJ4);
            this.createBoss(BossID.BROLY);
            this.createBoss(BossID.DTWUKONG);
            this.createBoss(BossID.BOSS_BabyVegeta);
            this.createBoss(BossID.Picolo_Daikamao);
            this.createBoss(BossID.gokussj);
            this.createBoss(BossID.thucung);
            this.createBoss(BossID.lanme);
            if (bossongia == false) {
                this.createBoss(BossID.OngGiaNoel);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("lỗi load boss" + ex);
        }
        this.loadedBoss = true;
        new Thread(BossManager.I, "Update boss").start();
    }

    public Boss createBossDoanhTrai(Zone zone, int bossID, double dame, double hp) {
        try {
            switch (bossID) {
                case BossID.TRUNG_UY_TRANG:
                    return new TrungUyTrang(zone, dame, hp);
                case BossID.TRUNG_UY_XANH_LO:
                    return new TrungUyXanhLo(zone, dame, hp);
                case BossID.TRUNG_UY_THEP:
                    return new TrungUyThep(zone, dame, hp);
                case BossID.NINJA_AO_TIM:
                    return new NinjaTim(zone, dame, hp);
                case BossID.ROBOT_VE_SI_1:
                case BossID.ROBOT_VE_SI_2:
                case BossID.ROBOT_VE_SI_3:
                case BossID.ROBOT_VE_SI_4:
                    return new RobotVeSi(zone, dame, hp, bossID);
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public Boss createBoss(int bossID) {
        try {
            switch (bossID) {
                case BossID.KUKU:
                    return new Kuku();
                case BossID.MAP_DAU_DINH:
                    return new MapDauDinh();
                case BossID.RAMBO:
                    return new Rambo();
                case BossID.DRABURA:
                    return new Drabura();
                case BossID.DRABURA_2:
                    return new Drabura2();

                case BossID.BUI_BUI:
                    return new BuiBui();
                case BossID.BUI_BUI_2:
                    return new BuiBui2();
                case BossID.YA_CON:
                    return new Yacon();
                case BossID.MABU_12H:
                    return new MabuBoss();

                case BossID.bossminuong:
                    return new Boss_Minuong();
                case BossID.Rong_1Sao:
                    return new Rong1Sao();
                case BossID.Rong_2Sao:
                    return new Rong2Sao();
                case BossID.Rong_3Sao:
                    return new Rong3Sao();

                case BossID.Rong_4Sao:
                    return new Rong4Sao();
                case BossID.Rong_5Sao:
                    return new Rong5Sao();
                case BossID.Rong_6Sao:
                    return new Rong6Sao();
                case BossID.Rong_7Sao:
                    return new Rong7Sao();
                case BossID.FIDE:
                    return new Fide();
                case BossID.DTWUKONG:
                    return new DetuWukong();
                case BossID.DR_KORE:
                    return new DrKore();
                case BossID.thucung:
                    return new Thucung();
                case BossID.ANDROID_19:
                    return new Android19();
                case BossID.ANDROID_13:
                    return new Android13();
                case BossID.ANDROID_14:
                    return new Android14();
                case BossID.ANDROID_15:
                    return new Android15();
                case BossID.PIC:
                    return new Pic();
                case BossID.gokussj:
                    return new GokuSSJ();
                case BossID.BOSS_Android17:
                    return new BossAndroid17();
                case BossID.POC:
                    return new Poc();
                case BossID.KING_KONG:
                    return new KingKong();
                case BossID.BOSS_DETU_BERUS:
                    return new DetuBerus();
                case BossID.BOSS_DETU_BL:
                    return new DetuBroly();
                case BossID.XEN_BO_HUNG:
                    return new XenBoHung();
                case BossID.SIEU_BO_HUNG:
                    return new SieuBoHung();
                case BossID.COOLER:
                    return new Cooler();
                case BossID.XEN_CON_1:
                    return new Xencon();
                case BossID.TDST:
                    return new TDST();
                case BossID.BOSS_Cumber:
                    return new BossCumber();
                case BossID.BOSS_Toppo:
                    return new BossToppo();
                case BossID.SUPER_ANDROID_17:
                    return new SuperAndroid17();

                case BossID.BOSS_BabyVegeta:
                    return new BossBabyVegeta();
                case BossID.Picolo_Daikamao:
                    return new PicoloDaikamao();

                case BossID.GogetaSJJ4:
                    return new GogetaSSJ4();
                case BossID.BROLY:
                    return new DetuMabu();
                case BossID.AN_TROM:
                    return new AnTrom();
                case BossID.OngGiaNoel:
                    return new OngGiaNoel();
                case BossID.lanme:
                    return new Lanme();
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public boolean existBossOnPlayer(Player player) {
        return !player.zone.getBosses().isEmpty();
    }

    public void showListBoss(Player player) {
        if (!player.isAdmin()) {
            return;
        }

        Message msg;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Boss");

            List<Boss> aliveBosses = bosses.stream()
                    .filter(boss -> boss != null
                    && boss.zone != null
                    && !MapService.gI().isMapMaBu(boss.data[0].getMapJoin()[0])
                    && !MapService.gI().isMapDoanhTrai(boss.data[0].getMapJoin()[0])
                    && !MapService.gI().isMapBanDoKhoBau(boss.data[0].getMapJoin()[0])
                    && !MapService.gI().isMapKhiGas(boss.data[0].getMapJoin()[0])
                    && !MapService.gI().isMapNhanBan(boss.data[0].getMapJoin()[0])
                    && !(boss instanceof MiNuong)
                    && !(boss instanceof Zamus)
                    && !(boss instanceof AnTrom)
                    && !(boss instanceof OngGiaNoel)
                    && !(boss instanceof kakarot)
                    && !MapService.gI().isMapBlackBallWar(boss.data[0].getMapJoin()[0])
                    )
                    .collect(Collectors.toList());

            msg.writer().writeByte(aliveBosses.size());

            for (Boss boss : aliveBosses) {
                try {
                    if (boss.data.length > 0) {
                        msg.writer().writeInt((int) boss.id);
                        msg.writer().writeInt((int) boss.id);
                        msg.writer().writeShort(boss.data[0].getOutfit()[0]);

                        if (player.getSession().version > 214) {
                            msg.writer().writeShort(-1);
                        }

                        msg.writer().writeShort(boss.data[0].getOutfit()[1]);
                        msg.writer().writeShort(boss.data[0].getOutfit()[2]);
                        msg.writer().writeUTF(boss.data[0].getName());

                        if (boss.zone != null) {
                            msg.writer().writeUTF("Rơi ITEM");
                            msg.writer().writeUTF(boss.zone.map.mapName + "(" + boss.zone.map.mapId + ") khu " + boss.zone.zoneId + "");

                        } else {

                        }
                    } else {
                        System.out.println("Lỗi: Mảng data của boss rỗng. ID của boss: " + boss.id);
                    }
                } catch (Exception e) {
                    System.out.println("Lỗi khi xử lý boss: " + e.getMessage());
                    System.out.println("ID của boss lỗi: " + boss.id);
                    msg.writer().writeUTF(boss.data.length > 0 ? boss.data[0].getName() : "Không có tên");
                    e.printStackTrace();
                }
            }

            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            System.out.println("Lỗi khi gửi danh sách boss: " + e.getClass().getName());
            e.printStackTrace();
        }
    }

    public void dobosss(Player player) {
        Message msg;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Boss");

            List<Boss> aliveBosses = bosses.stream()
                    .filter(boss -> boss != null
                    && boss.zone != null
                    && !MapService.gI().isMapMaBu(boss.data[0].getMapJoin()[0])
                    && !MapService.gI().isMapDoanhTrai(boss.data[0].getMapJoin()[0])
                    && !MapService.gI().isMapBanDoKhoBau(boss.data[0].getMapJoin()[0])
                    && !MapService.gI().isMapKhiGas(boss.data[0].getMapJoin()[0])
                    && !MapService.gI().isMapNhanBan(boss.data[0].getMapJoin()[0])
                    && !(boss instanceof MiNuong)
                    && !(boss instanceof Zamus)
                    && !(boss instanceof kakarot)
                    && !MapService.gI().isMapBlackBallWar(boss.data[0].getMapJoin()[0])
                    )
                    .collect(Collectors.toList());

            msg.writer().writeByte(aliveBosses.size());

            for (Boss boss : aliveBosses) {
                try {
                    if (boss.data.length > 0) {
                        msg.writer().writeInt((int) boss.id);
                        msg.writer().writeInt((int) boss.id);
                        msg.writer().writeShort(boss.data[0].getOutfit()[0]);

                        if (player.getSession().version > 214) {
                            msg.writer().writeShort(-1);
                        }

                        msg.writer().writeShort(boss.data[0].getOutfit()[1]);
                        msg.writer().writeShort(boss.data[0].getOutfit()[2]);
                        msg.writer().writeUTF(boss.data[0].getName());

                        if (boss.zone != null) {
                            msg.writer().writeUTF("Sống");

                            msg.writer().writeUTF(boss.zone.map.mapName + "" + boss.zone.map.mapId);
                        } else {
                            msg.writer().writeUTF("Chết");
                            msg.writer().writeUTF("Chết rồi");
                        }
                    } else {
                        System.out.println("Lỗi: Mảng data của boss rỗng. ID của boss: " + boss.id);
                    }
                } catch (Exception e) {
                    System.out.println("Lỗi khi xử lý boss: " + e.getMessage());
                    System.out.println("ID của boss lỗi: " + boss.id);
                    msg.writer().writeUTF(boss.data.length > 0 ? boss.data[0].getName() : "Không có tên");
                    e.printStackTrace();
                }
            }

            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            System.out.println("Lỗi khi gửi danh sách boss: " + e.getClass().getName());
            e.printStackTrace();
        }
    }

    public synchronized void callBoss(Player player, int mapId) {
        try {
            if (BossManager.gI().existBossOnPlayer(player)
                    || player.zone.items.stream().anyMatch(itemMap -> ItemMapService.gI().isBlackBall(itemMap.itemTemplate.id))
                    || player.zone.getPlayers().stream().anyMatch(p -> p.iDMark.isHoldBlackBall())) {
                return;
            }
            Boss k = null;
            switch (mapId) {
                case 85:
                    k = BossManager.gI().createBoss(BossID.Rong_1Sao);
                    break;
                case 86:
                    k = BossManager.gI().createBoss(BossID.Rong_2Sao);
                    break;
                case 87:
                    k = BossManager.gI().createBoss(BossID.Rong_3Sao);
                    break;
                case 88:
                    k = BossManager.gI().createBoss(BossID.Rong_4Sao);
                    break;
                case 89:
                    k = BossManager.gI().createBoss(BossID.Rong_5Sao);
                    break;
                case 90:
                    k = BossManager.gI().createBoss(BossID.Rong_6Sao);
                    break;
                case 91:
                    k = BossManager.gI().createBoss(BossID.Rong_7Sao);
                    break;
            }
            if (k != null) {
                k.currentLevel = 0;
                k.joinMapByZone(player);
            }
        } catch (Exception e) {
            System.out.println("ooo");
        }
    }

    public Boss getBossById(int bossId) {
        return BossManager.gI().bosses.stream().filter(boss -> boss.id == bossId && !boss.isDie()).findFirst().orElse(null);
    }

    //data boss mới
    private Timer timer;
    private TimerTask timerTask;
    private boolean active = false;

    private ScheduledExecutorService runhehe = Executors.newSingleThreadScheduledExecutor();

    public void action(int delay) {
        if (!active) {
            active = true;
            runhehe.scheduleAtFixedRate(() -> {
                updateBossTask();
                loopCreateBoss();
            }, delay, delay, TimeUnit.MILLISECONDS);
        }
    }

    private void updateBossTask() {
        long st = System.currentTimeMillis();
        for (int i = 0; i < bosses.size(); i++) {
            Boss boss = bosses.get(i);
            if (boss != null && boss.bossInstance != null) {
                boss.update();
            }
        }

    }

    private void loopCreateBoss() {
        for (Map.Entry<Integer, Long> entry : setTimeSpawnBoss.entrySet()) {
            if (System.currentTimeMillis() >= entry.getValue()) {
                System.out.println("Value: " + entry.getKey());
                createBoss(entry.getKey());
                setTimeSpawnBoss.remove(entry.getKey());
            }
        }
    }

    @Override
    public void run() {
        while (ServerManager.isRunning) {
            try {
                long st = System.currentTimeMillis();
                for (Boss boss : this.bosses) {
                    boss.update();
                }
                Thread.sleep(150 - (System.currentTimeMillis() - st));
            } catch (Exception ignored) {

            }

        }
    }
}
