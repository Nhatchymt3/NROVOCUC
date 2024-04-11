package com.girlkun.server;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.database.GirlkunDB;
import com.girlkun.jdbc.daos.GodGK;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.network.server.GirlkunSessionManager;
import com.girlkun.network.session.ISession;
import com.girlkun.server.io.MySession;
import com.girlkun.services.ItemTimeService;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.services.func.SummonDragon;
import com.girlkun.services.func.TransactionService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.NgocRongNamecService;
import com.girlkun.utils.Logger;
import com.girlkun.server.io.MySession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.girlkun.models.matches.pvp.DaiHoiVoThuat;
import com.girlkun.models.matches.pvp.DaiHoiVoThuatService;
import com.girlkun.models.npc.NpcFactory;
import com.girlkun.models.player.Inventory;
import com.girlkun.models.player.TimeReset;
import static com.girlkun.models.player.TimeReset.CLOSE_RESET;
import static com.girlkun.models.player.TimeReset.TIME_RESET;
import com.girlkun.models.skill.Skill;
import com.girlkun.services.ItemService;
import com.girlkun.services.MapService;
import com.girlkun.services.func.GoiRongBang;
import com.girlkun.services.func.GoiRongXuong;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.Util;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client implements Runnable {

    private static Client i;

    private final Map<Long, Player> players_id = new HashMap<Long, Player>();
    private final Map<Integer, Player> players_userId = new HashMap<Integer, Player>();
    private final Map<String, Player> players_name = new HashMap<String, Player>();
    private final List<Player> players = new ArrayList<>();
    private int numberOfPlayers;

    private static int id = 10000;
    private boolean running = true;

    private Client() {
        new Thread(this).start();
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public static Client gI() {
        if (i == null) {
            i = new Client();
        }
        return i;
    }

    public void put(Player player) {
        if (!players_id.containsKey(player.id)) {
            this.players_id.put(player.id, player);
        }
        if (!players_name.containsValue(player)) {
            this.players_name.put(player.name, player);
        }
        if (!players_userId.containsValue(player)) {
            this.players_userId.put(player.getSession().userId, player);
        }
        if (!players.contains(player)) {
            this.players.add(player);
        }

    }

    private void remove(MySession session) {
        if (session.player != null) {
            this.remove(session.player);
            session.player.dispose();
        }
        if (session.joinedGame) {
            session.joinedGame = false;
            try {
                GirlkunDB.executeUpdate("update account set last_time_logout = ? where id = ?", new Timestamp(System.currentTimeMillis()), session.userId);
            } catch (Exception e) {
                System.out.println("bbbbb");
            }
        }
        ServerManager.gI().disconnect(session);
    }

    public void remove(Player player) {
        this.players_id.remove(player.id);
        this.players_name.remove(player.name);
        this.players_userId.remove(player.getSession().userId);
        this.players.remove(player);
        if (!player.beforeDispose) {
            DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).removePlayerWait(player);
            DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).removePlayer(player);
            player.beforeDispose = true;
            player.mapIdBeforeLogout = player.zone.map.mapId;
            if (player.idNRNM != -1) {
                ItemMap itemMap = new ItemMap(player.zone, player.idNRNM, 1, player.location.x, player.location.y, -1);
                Service.getInstance().dropItemMap(player.zone, itemMap);
                NgocRongNamecService.gI().pNrNamec[player.idNRNM - 353] = "";
                NgocRongNamecService.gI().idpNrNamec[player.idNRNM - 353] = -1;
                player.idNRNM = -1;
            }
            ChangeMapService.gI().exitMap(player);
            TransactionService.gI().cancelTrade(player);
            if (player.clan != null) {
                player.clan.removeMemberOnline(null, player);
            }

            if (SummonDragon.gI().playerSummonShenron != null
                    && SummonDragon.gI().playerSummonShenron.id == player.id) {
                SummonDragon.gI().isPlayerDisconnect = true;
            }
            if (GoiRongXuong.gI().playerRongXuong != null
                    && GoiRongXuong.gI().playerRongXuong.id == player.id) {
                GoiRongXuong.gI().isPlayerDisconnect = true;
            }
              if (GoiRongBang.gI().playerRongICE != null
                    && GoiRongBang.gI().playerRongICE.id == player.id) {
                GoiRongBang.gI().isPlayerDisconnect = true;
            }
            if (player.mobMe != null) {
                player.mobMe.mobMeDie();
            }
            if (player.pet != null) {
                if (player.pet.mobMe != null) {
                    player.pet.mobMe.mobMeDie();
                }
                ChangeMapService.gI().exitMap(player.pet);
            }
            if (player.newpet != null) {
                if (player.newpet.mobMe != null) {
                    player.newpet.mobMe.mobMeDie();
                }
                ChangeMapService.gI().exitMap(player.newpet);
            }
            if (player.TrieuHoipet != null) {
                if (player.TrieuHoipet.mobMe != null) {
                    player.TrieuHoipet.mobMe.mobMeDie();
                }
                ChangeMapService.gI().exitMap(player.TrieuHoipet);
            }
        }
        PlayerDAO.updatePlayer(player);
    }

    public void kickSession(MySession session) {
        if (session != null) {
            this.remove(session);
            session.disconnect();
        }
    }

    public Player getPlayer(long playerId) {
        return this.players_id.get(playerId);
    }

    public Player getPlayerByUser(int userId) {
        return this.players_userId.get(userId);
    }

    // hàm lấy player
    public Player getPlayer(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        } else {
            for (Player player : this.players_name.values()) {
                String playerNameWithoutPrefix = removePrefix(player.name);
                if (playerNameWithoutPrefix.equalsIgnoreCase(name.trim())) {
                    return player;
                }
            }

            return null;
        }
    }

    private String removePrefix(String playerName) {
        String[] prefixes = {"[NRO] ", "[VIP 2]", "[VIP 3]", "$"};
        for (String prefix : prefixes) {
            if (playerName.startsWith(prefix)) {
                playerName = playerName.substring(prefix.length());
            }
        }

        return playerName.trim();
    }

    public void close() {
        Logger.error("BEGIN KICK OUT SESSION.............................." + players.size() + "\n");
        while (!players.isEmpty()) {
            this.kickSession((MySession) players.remove(0).getSession());
        }
        Logger.error("...........................................SUCCESSFUL\n");
    }

    public void cloneMySessionNotConnect() {
        Logger.error("BEGIN KICK OUT MySession Not Connect...............................\n");
        Logger.error("COUNT: " + GirlkunSessionManager.gI().getSessions().size());

        List<MySession> sessionsToRemove = new ArrayList<>();

        for (ISession session : new ArrayList<>(GirlkunSessionManager.gI().getSessions())) {
            MySession m = (MySession) session;
            if (m.player == null) {
                sessionsToRemove.add(m);
            }
        }

        for (MySession session : sessionsToRemove) {
            remove(session);  // Dọn dẹp phiên không kết nối
            session.disconnect();
        }

        Logger.error("..........................................................SUCCESSFUL\n");
    }

    private void update() {
        for (ISession s : GirlkunSessionManager.gI().getSessions()) {
            MySession session = (MySession) s;
            if (session.timeWait > 0) {
                session.timeWait--;
                if (session.timeWait == 0) {
                    kickSession(session);
                }
            }
        }
    }

    @Override
    public void run() {
        while (ServerManager.isRunning) {
            try {
                long st = System.currentTimeMillis();
                update();
                if ((st > TIME_RESET && st < CLOSE_RESET)) {
                    GirlkunDB.executeUpdate("UPDATE player SET Tai_xiu = JSON_REPLACE(JSON_REPLACE(JSON_REPLACE(Tai_xiu, '$[0]', 0), '$[4]', 0), '$[5]', 0)");

                    System.out.println("==================RESET DAY THANH CONG===============");

                    Thread.sleep(800);

                }
                Thread.sleep(800 - (System.currentTimeMillis() - st));
            } catch (Exception e) {
            }
        }
    }

    public void show(Player player) {
        String txt = "";
        txt += "sessions: " + GirlkunSessionManager.gI().getSessions().size() + "\n";
        txt += "players_id: " + players_id.size() + "\n";
        txt += "players_userId: " + players_userId.size() + "\n";
        txt += "players_name: " + players_name.size() + "\n";
        txt += "players: " + players.size() + "\n";
        Service.getInstance().sendThongBao(player, txt);
    }

    public void clear() {
        List<Player> z = players;
        for (Player pl : z) {
            if (pl != null) {
                if (pl.isBot) {
                    remove(pl);
                }
            }
        }
    }
    public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public void createBot(MySession s) throws Exception {
        String[] name1 = {"zeni", "goku", "cadic", "vegeta", "ssj4", "blue", "solo", "yasuo", "go", "red", "make", "mai"};
        String[] name2 = {"trum", "hehe", "black", "gi", "manh", "nro", "kuku", "seri", "gay", "haoem", "quang", "vinh"};
        String[] name3 = {"top1", "zamus", "berrus", "fire", "son", "cuong", "top2", "sever", "gay", "slayer", "kaka", "love"};
        String[] chat = {"top1", "zamus", "berrus", "fire", "son", "cuong", "top2", "sever", "gay", "slayer", "kaka", "love"};

        Player pl = new Player();
        Player temp = Client.gI().getPlayerByUser(1);//GodGK.loadById(2275);

        pl.setSession(s);
        s.userId = id;
        pl.id = id++;

        // Tạo tên duy nhất
        String uniqueName = generateUniqueName(name1, name2, name3);
        pl.name = uniqueName;

        pl.gender = (byte) Util.nextInt(0, 2);
        pl.isBot = true;
        pl.isBoss = false;
        pl.isPet = false;

        pl.nPoint.power = Util.nextInt(2000, 20000000);

        pl.nPoint.hpg = 100000;
        pl.nPoint.hpMax = Math.max(2000, Util.nextInt(2000, 500000));
        pl.nPoint.hp = Math.min(pl.nPoint.hpMax, Math.max(1, pl.nPoint.hpMax / 2));

        pl.nPoint.mpMax = Math.max(2000, Util.nextInt(2000, 500000));
        pl.nPoint.dame = Util.nextInt(500, 2000);
        pl.nPoint.stamina = 32000;

        pl.autodosat = true;
        pl.typePk = ConstPlayer.NON_PK;

        if (pl.nPoint.hp == 0) {
            Service.gI().hsChar(pl, pl.nPoint.hpMax, pl.nPoint.mpMax);
        }

        //skill
        int[] skillsArr = pl.gender == 0 ? new int[]{0, 1, 6, 9}
                : pl.gender == 1 ? new int[]{2, 3, 7}
                : new int[]{4, 5, 8};

        for (int j = 0; j < skillsArr.length; j++) {
            Skill skill = SkillUtil.createSkill(skillsArr[j], Util.nextInt(1, 7));
            pl.playerSkill.skills.add(skill);
        }

        pl.inventory = new Inventory();
        for (int i = 0; i < 12; i++) {
            pl.inventory.itemsBody.add(ItemService.gI().createItemNull());
        }

        pl.inventory.gold = 2000000000;
        pl.inventory.itemsBody.set(5, Manager.CAITRANG.get(Util.nextInt(0, Manager.CAITRANG.size() - 1)));

        pl.location.y = 50;
        pl.zone = MapService.gI().getMapCanJoin(pl, Util.nextInt(150), Util.nextInt(0, 5));

        if (pl.zone != null && pl.zone.map != null
                && pl.zone.map.mapId != 21 && pl.zone.map.mapId != 22 && pl.zone.map.mapId != 23
                && pl.zone.map.mapId != 135 && pl.zone.map.mapId != 136
                && pl.zone.map.mapId != 137 && pl.zone.map.mapId != 57
                && pl.zone.map.mapId != 138 && pl.zone.map.mapId != 58
                && pl.zone.map.mapId != 53 && pl.zone.map.mapId != 59
                && pl.zone.map.mapId != 60 && pl.zone.map.mapId != 61
                && pl.zone.map.mapId != 62 && pl.zone.map.mapId != 85
                && pl.zone.map.mapId != 54 && pl.zone.map.mapId != 86
                && pl.zone.map.mapId != 55 && pl.zone.map.mapId != 87
                && pl.zone.map.mapId != 88 && pl.zone.map.mapId != 89 && pl.zone.map.mapId != 90 && pl.zone.map.mapId != 91
                && pl.zone.map.mapId != 114 && pl.zone.map.mapId != 115 && pl.zone.map.mapId != 116
                && pl.zone.map.mapId != 117 && pl.zone.map.mapId != 118 && pl.zone.map.mapId != 119
                && pl.zone.map.mapId != 120 && pl.zone.map.mapId != 47 && pl.zone.map.mapId != 48
                && pl.zone.map.mapId != 49 && pl.zone.map.mapId != 50 && pl.zone.map.mapId != 51
                && pl.zone.map.mapId != 52
                && pl.zone.map.mapId != 56) {
            pl.location.x = Util.nextInt(20, 1000);
            pl.zone.addPlayer(pl);
            pl.zone.load_Me_To_Another(pl);
            Client.gI().put(pl);

        }
        scheduler.scheduleAtFixedRate(() -> {
            try {
                Service.gI().chat(pl, "Sever Vô Cực Nro Nro Mãi Đỉnh");
            } catch (Exception e) {

            }
        }, 0, 60000, TimeUnit.MILLISECONDS);

    }

    private String generateUniqueName(String[] name1, String[] name2, String[] name3) {
        String generatedName;
        do {
            generatedName = name1[Util.nextInt(name1.length)] + name2[Util.nextInt(name2.length)] + name3[Util.nextInt(name3.length)];
        } while (isNameAlreadyUsed(generatedName));
        return generatedName;
    }

    private boolean isNameAlreadyUsed(String name) {
        return false;
    }

}
