package com.girlkun.models.map;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.consts.ConstTask;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.dhvt.BossDHVT;
import com.girlkun.models.item.Item;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.npc.Npc;
import com.girlkun.models.npc.NpcManager;
import com.girlkun.models.player.Pet;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.Referee;
import com.girlkun.models.player.Referee1;

import com.girlkun.network.io.Message;
import com.girlkun.services.ItemMapService;
import com.girlkun.services.ItemService;
import com.girlkun.services.MapService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.NgocRongNamecService;
import static com.girlkun.services.NgocRongNamecService.TIME_BL;
import static com.girlkun.services.NgocRongNamecService.TIME_OP;
import com.girlkun.models.player.Thu_TrieuHoi;
import com.girlkun.utils.FileIO;
import com.girlkun.utils.Logger;
import static com.girlkun.utils.Logger.GREEN;
import static com.girlkun.utils.Logger.RED;
import static com.girlkun.utils.Logger.RESET;
import com.girlkun.utils.Util;
import com.nroluz.models.boss.boss_new.OngGiaNoel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import lombok.Getter;
import lombok.Setter;

public class Zone {

    public static final byte PLAYERS_TIEU_CHUAN_TRONG_MAP = 7;

    public int countItemAppeaerd = 0;

    public Map map;
    public int zoneId;
    public int maxPlayer;

    private final List<Player> humanoids; //player, boss, pet
    public final List<Player> notBosses; //player, pet
    public final List<Player> players; //player
    private final List<Player> bosses; //boss
    private final List<Player> pets; //pet

    public final List<Mob> mobs;
    public final List<ItemMap> items;

    public long lastTimeDropBlackBall;
    public boolean finishBlackBallWar;
    public boolean finishMapMaBu;

    public List<TrapMap> trapMaps;
    public boolean haveBeQuynh;
    @Setter
    @Getter
    private Player referee;

    private Player referee1;
    private Player testdame;

    public boolean isFullPlayer() {
        return this.players.size() >= this.maxPlayer;
    }

    public boolean isTrungUyTrangAlive;
    public boolean isbulon13Alive;
    public boolean isbulon14Alive;

    public boolean finishMapSatan;

    private void udMob() {
        for (Mob mob : this.mobs) {
            mob.update();
        }
    }

    public Mob getRandomMobInMap() {
        if (!this.mobs.isEmpty()) {
            return this.mobs.get(Util.nextInt(this.mobs.size()));
        }
        return null;
    }

    private void udPlayer() {
        List<Player> copyList = new ArrayList<>(notBosses);
        for (Player pl : copyList) {
            if (!pl.isNewPet && !pl.isTrieuhoipet) {
                pl.update();
            } else {
                notBosses.remove(pl);
            }
        }
    }

    private void udItem() {
        for (int i = this.items.size() - 1; i >= 0; i--) {
            this.items.get(i).update();
        }
    }

    public void update() {
        udMob();
        udPlayer();
        udItem();
    }

    public Zone(Map map, int zoneId, int maxPlayer) {
        this.map = map;
        this.zoneId = zoneId;
        this.maxPlayer = maxPlayer;
        this.humanoids = new ArrayList<>();
        this.notBosses = new ArrayList<>();
        this.players = new ArrayList<>();
        this.bosses = new ArrayList<>();
        this.pets = new ArrayList<>();
        this.mobs = new ArrayList<>();
        this.items = new ArrayList<>();
        this.trapMaps = new ArrayList<>();
    }

    public int getNumOfPlayers() {
        return this.players.size();
    }

    public boolean isBossCanJoin(Boss boss) {
        for (Player b : this.bosses) {
            if (b.id == boss.id) {
                return false;
            }
        }
        return true;
    }

    public List<Player> getNotBosses() {
        return this.notBosses;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public List<Player> getHumanoids() {
        return this.humanoids;
    }

    public List<Player> getBosses() {
        return this.bosses;
    }

    public void addPlayer(Player player) {
        if (player != null) {
            if (!this.humanoids.contains(player)) {
                this.humanoids.add(player);
            }
            if (!player.isBoss && !this.notBosses.contains(player)) {
                this.notBosses.add(player);
            }
            if (!player.isBoss && !player.isNewPet && !player.isPet && !player.isTrieuhoipet && !this.players.contains(player)) {
                this.players.add(player);
            }
            if (player.isBoss) {
                this.bosses.add(player);
            }
            if (player.isPet || player.isNewPet || player.isTrieuhoipet) {
                this.pets.add(player);
            }
        }
    }

    public boolean isKhongCoTrongTaiTrongKhu() {
        boolean kovao = true;
        for (Player pl : players) {
            if (!pl.isPl()) {
                kovao = false;
                break;
            }
            if ((pl.zone.map.mapId >= 21 && pl.zone.map.mapId <= 23) || pl.zone.map.mapId == 170 || pl.zone.map.mapId == 153
                    || pl.zone.map.mapId == 52 || pl.zone.map.mapId == 113 || pl.zone.map.mapId == 129
                    || MapService.gI().isMapDoanhTrai(pl.zone.map.mapId)
                    || MapService.gI().isMapBlackBallWar(pl.zone.map.mapId)
                    || MapService.gI().isMapBanDoKhoBau(pl.zone.map.mapId)
                    || MapService.gI().isMapKhiGas(pl.zone.map.mapId)
                    || MapService.gI().isMapMaBu(pl.zone.map.mapId)
                    || MapService.gI().isMapOffline(pl.zone.map.mapId)) {
                kovao = false;
            }
        }
        return kovao;
    }

    public void removePlayer(Player player) {
        this.humanoids.remove(player);
        this.notBosses.remove(player);
        this.players.remove(player);
        this.bosses.remove(player);
        this.pets.remove(player);
    }

    public ItemMap getItemMapByItemMapId(int itemId) {
        for (ItemMap item : this.items) {
            if (item.itemMapId == itemId) {
                return item;
            }
        }
        return null;
    }

    public ItemMap getItemMapByTempId(int tempId) {
        for (ItemMap item : this.items) {
            if (item.itemTemplate.id == tempId) {
                return item;
            }
        }
        return null;
    }

    public List<ItemMap> getItemMapsForPlayer(Player player) {
        List<ItemMap> list = new ArrayList<>();
        for (ItemMap item : items) {
            if (item.itemTemplate.id == 78) {
                if (TaskService.gI().getIdTask(player) != ConstTask.TASK_3_1) {
                    continue;
                }
            }
            if (item.itemTemplate.id == 74) {
                if (TaskService.gI().getIdTask(player) < ConstTask.TASK_3_0) {
                    continue;
                }
            }
            list.add(item);
        }
        return list;
    }

    public Player getPlayerInMap(long idPlayer) {
        for (Player pl : humanoids) {
            if (pl.id == idPlayer) {
                return pl;
            }
        }
        return null;
    }

    public void pickItem(Player player, int itemMapId) {
        ItemMap itemMap = getItemMapByItemMapId(itemMapId);
        if (itemMap != null) {
            if (itemMap.itemTemplate.type != 22) {
                if (itemMap.playerId == player.id * 100000 || itemMap.playerId == player.id || itemMap.playerId == -1) {
                    Item item = ItemService.gI().createItemFromItemMap(itemMap);
                    boolean picked = true;
                    if (item.template.id == 934 && player.taixiu.bongtai >= 2000) {
                        Service.getInstance().sendThongBao(player, "Mỗi ngày chỉ nhặt được 2000 mảnh hồn");
                        return;
                    } else if (item.template.id == 934 && player.taixiu.bongtai < 2000) {
                        player.taixiu.bongtai++;
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendToAntherMePickItem(player, itemMapId);
                    }
                    if (!ItemMapService.gI().isNamecBall(item.template.id)) {
                        picked = InventoryServiceNew.gI().addItemBag(player, item);
                    }
                    if (picked) {
                        int itemType = item.template.type;
                        Message msg;
                        try {
                            msg = new Message(-20);
                            msg.writer().writeShort(itemMapId);
                            switch (itemType) {
                                case 9:
                                    player.vangnhat += item.quantity;
                                    msg.writer().writeUTF("");
                                    PlayerService.gI().sendInfoHpMpMoney(player);
                                    break;
                                case 10:
                                case 34:
                                    player.hngocnhat += item.quantity;
                                    msg.writer().writeUTF("");
                                    PlayerService.gI().sendInfoHpMpMoney(player);
                                    break;
                                default:
                                    switch (item.template.id) {
                                        case 362:
                                            Service.getInstance().sendThongBao(player, "Chỉ là cục đá thôi, nhặt làm gì?");
                                            break;
                                        case 353:
                                        case 354:
                                        case 355:
                                        case 356:
                                        case 357:
                                        case 358:
                                        case 359:
                                            if (System.currentTimeMillis() >= TIME_OP && System.currentTimeMillis() <= TIME_BL) {
                                                if (player.idNRNM == -1) {
                                                    PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.PK_ALL);
                                                    player.idNRNM = item.template.id;
                                                    NgocRongNamecService.gI().mapNrNamec[item.template.id - 353] = player.zone.map.mapId;
                                                    NgocRongNamecService.gI().nameNrNamec[item.template.id - 353] = player.zone.map.mapName;
                                                    NgocRongNamecService.gI().zoneNrNamec[item.template.id - 353] = (byte) player.zone.zoneId;
                                                    NgocRongNamecService.gI().pNrNamec[item.template.id - 353] = player.name;
                                                    NgocRongNamecService.gI().idpNrNamec[item.template.id - 353] = (int) player.id;
                                                    player.lastTimePickNRNM = System.currentTimeMillis();
                                                    Service.getInstance().sendFlagBag(player);
                                                    msg.writer().writeUTF("Bạn đã nhặt được " + item.template.name);
                                                    msg.writer().writeShort(item.quantity);
                                                    player.sendMessage(msg);
                                                    msg.cleanup();

                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Bạn đã mang ngọc rồng trên người");
                                                    ItemMap itm = new ItemMap(itemMap);
                                                    itm.x = player.location.x + Util.nextInt(-20, 20);
                                                    itm.y = itm.zone.map.yPhysicInTop(itm.x, player.location.y);
                                                    Service.getInstance().dropItemMap(player.zone, itm);
                                                }
                                            } else {
                                                Service.getInstance().sendThongBao(player, "NRNM chỉ nhặt được trong khung giờ 14h đến 16h hằng ngày !!!!");
                                                ItemMap itm = new ItemMap(itemMap);
                                                itm.x = player.location.x + Util.nextInt(-20, 20);
                                                itm.y = itm.zone.map.yPhysicInTop(itm.x, player.location.y);
                                                Service.getInstance().dropItemMap(player.zone, itm);
                                            }
                                            break;
                                        case 73:
                                            msg.writer().writeUTF("");
                                            msg.writer().writeShort(item.quantity);
                                            player.sendMessage(msg);
                                            msg.cleanup();
                                            break;
                                        case 74:
                                            msg.writer().writeUTF("Bạn mới vừa ăn " + item.template.name);
                                            break;
                                        case 78:
                                            msg.writer().writeUTF("Wow, một cậu bé dễ thương!");
                                            msg.writer().writeShort(item.quantity);
                                            player.sendMessage(msg);
                                            msg.cleanup();
                                            break;
                                        default:
                                            if (item.template.type >= 0 && item.template.type < 5) {
                                                msg.writer().writeUTF(item.template.name + " ngon ngon...");
                                            } else {
                                                msg.writer().writeUTF("Bạn mới nhặt được " + item.template.name);
                                            }
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            break;
                                    }
                            }
                            msg.writer().writeShort(item.quantity);
                            player.sendMessage(msg);
                            msg.cleanup();
                            Service.getInstance().sendToAntherMePickItem(player, itemMapId);
                            if (!(this.map.mapId >= 21 && this.map.mapId <= 23
                                    && itemMap.itemTemplate.id == 74
                                    || this.map.mapId >= 42 && this.map.mapId <= 44
                                    && itemMap.itemTemplate.id == 78)) {
                                removeItemMap(itemMap);
                            }

                        } catch (Exception e) {
                            System.out.println("lỗi tại pickItem "+e);
                            Logger.logException(Zone.class, e);
                        }
                    } else {
                        if (!ItemMapService.gI().isBlackBall(item.template.id)) {
                            String text = "Hành trang không còn chỗ trống";
                            Service.getInstance().sendThongBao(player, text);
                        }
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Không thể nhặt vật phẩm của người khác");
                }
            } else {
                Service.getInstance().sendThongBao(player, "Không thể nhặt Vệ tinh");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            removeItemMap(itemMap);
        }
        TaskService.gI().checkDoneTaskPickItem(player, itemMap);
        TaskService.gI().checkDoneSideTaskPickItem(player, itemMap);
    }

    public void addItem(ItemMap itemMap) {
        if (itemMap != null && !items.contains(itemMap)) {
            items.add(0, itemMap);
        }
    }

    
    //xóa item map
    public void removeItemMap(ItemMap itemMap) {
        this.items.remove(itemMap);
    }

    public Player getRandomPlayerInMap() {
        if (!this.notBosses.isEmpty()) {
            return this.notBosses.get(Util.nextInt(0, this.notBosses.size() - 1));
        } else {
            return null;
        }
    }

    public Player getRandomPlayerInMapPlayer() {
        if (!this.players.isEmpty()) {
            return this.players.get(Util.nextInt(0, this.players.size() - 1));
        } else {
            return null;
        }
    }

    public Player PlayerPKinmap() {
        if (!this.humanoids.isEmpty()) {
            return this.humanoids.get(Util.nextInt(0, this.humanoids.size() - 1));
        } else {
            return null;
        }
    }

    public void load_Me_To_Another(Player player) {
        try {
            if (player.zone != null) {
                if (MapService.gI().isMapOffline(this.map.mapId)) {
                    if (player.isPet) {
                        Pet pet = (Pet) player;
                        if (this.equals(pet.master.zone)) {
                            infoPlayer(pet.master, player);
                        }
                    } else if (player.isTrieuhoipet) {
                        Thu_TrieuHoi trieuhoiPet = (Thu_TrieuHoi) player;
                        if (this.equals(trieuhoiPet.masterr.zone)) {
                            infoPlayer(trieuhoiPet.masterr, player);
                        }
                    }
                } else {
                    for (Player pl : players) {
                        if (!player.equals(pl)) {
                            infoPlayer(pl, player);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("Đã xảy ra lỗi khi thực hiện load_Me_To_Another"+ e);
        }
    }

    public void load_Another_To_Me(Player player) {
        try {
            if (MapService.gI().isMapOffline(this.map.mapId)) {
                for (Player pl : this.humanoids) {
                    if (pl != null && (pl.id == -player.id)) {
                        infoPlayer(player, pl);
                        break;
                    }
                }
            } else {
                for (Player pl : this.humanoids) {
                    if (pl != null && !player.equals(pl)) {
                        infoPlayer(player, pl);
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("Đã xảy ra lỗi khi thực hiện load_Another_To_Me");
        }
    }

    
    
    public void loadBoss(Boss boss) {
        try {
            if (MapService.gI().isMapOffline(this.map.mapId)) {
                Player targetPlayer = findPlayerById((int) -boss.id, this.humanoids);
                if (targetPlayer != null) {
                    infoPlayer(boss, targetPlayer);
                }
            } else {
                for (Player pl : this.bosses) {
                    if (!boss.equals(pl)) {
                        infoPlayer(boss, pl);
                        infoPlayer(pl, boss);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("        Lỗi trong phương thức loadBoss" + e);
            Logger.logException(MapService.class, e);
        }
    }

    
    //tìm kiếm player ở trong map
    public Player findPlayerById(int playerId, List<Player> players) {
        for (Player pl : players) {
            if (pl.id == playerId) {
                return pl;
            }
        }
        return null;
    }

    public void infoPlayer(Player plReceive, Player plInfo) {
        Message msg;
        try {
            String name;//test
            msg = new Message(-5);
            msg.writer().writeInt((int) plInfo.id);
            if (plInfo.clan != null) {
                msg.writer().writeInt(plInfo.clan.id);
            } else {
                msg.writer().writeInt(-1);
            }
            msg.writer().writeByte(Service.getInstance().getCurrLevel(plInfo));
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(plInfo.typePk);
            msg.writer().writeByte(plInfo.gender);
            msg.writer().writeByte(plInfo.gender);
            msg.writer().writeShort(plInfo.getHead());
            msg.writer().writeUTF(plInfo.name);//plInfo.name
            msg.writer().writeInt(Util.DoubleGioihan(plInfo.nPoint.hp));
            msg.writer().writeInt(Util.DoubleGioihan(plInfo.nPoint.hpMax));
            msg.writer().writeShort(plInfo.getBody());
            msg.writer().writeShort(plInfo.getLeg());
            msg.writer().writeByte(plInfo.getFlagBag()); //bag
            msg.writer().writeByte(-1);
            msg.writer().writeShort(plInfo.location.x);
            msg.writer().writeShort(plInfo.location.y);
            msg.writer().writeShort(0);
            msg.writer().writeShort(0); //

            msg.writer().writeByte(0);

            msg.writer().writeByte(plInfo.iDMark.getIdSpaceShip());

            msg.writer().writeByte(plInfo.effectSkill.isMonkey ? 1 : 0);
            msg.writer().writeShort(plInfo.getMount());
            msg.writer().writeByte(plInfo.cFlag);
            msg.writer().writeByte(0);

            msg.writer().writeShort(-1); //idauraeff plInfo.getAura()
            msg.writer().writeByte(-1); //seteff plInfo.getEffFront()
            plReceive.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            System.out.println("lỗi tại infoPlayer" + e);
        }
        Service.getInstance().sendFlagPlayerToMe(plReceive, plInfo);

        if (!plInfo.isBoss && !plInfo.isPet && !plInfo.isNewPet && !plInfo.isTrieuhoipet && !(plInfo instanceof BossDHVT) && !(plInfo instanceof Referee) & !(plInfo instanceof Referee1)) {
            Service.getInstance().sendPetFollowToMe(plReceive, plInfo);
            if (plInfo.inventory.itemsBody.get(11).isNotNullItem()) {
                Service.getInstance().sendFootRv(plInfo, plReceive, (short) plInfo.inventory.itemsBody.get(11).template.id);
            }
        }
        if (plInfo.isPl() && plInfo.name.compareTo("Nro") != 0 && plInfo.name.compareTo("BOT") != 0) {
            if (plInfo.inventory.itemsBody.size() > 5 && plInfo.inventory.itemsBody.get(5).isNotNullItem()) {
                Service.getInstance().sendTitleRv(plInfo, plReceive, (short) plInfo.inventory.itemsBody.get(5).template.id);
            }
        }
        Service.getInstance().sendTitleRv(plInfo, plReceive, (short) 888);
        Service.getInstance().sendTitleRv(plInfo, plReceive, (short) 889);
        Service.getInstance().sendTitleRv(plInfo, plReceive, (short) 890);
        Service.getInstance().sendTitleRv(plInfo, plReceive, (short) 891);
        try {
            if (plInfo.isDie()) {
                msg = new Message(-8);
                msg.writer().writeInt((int) plInfo.id);
                msg.writer().writeByte(0);
                msg.writer().writeShort(plInfo.location.x);
                msg.writer().writeShort(plInfo.location.y);
                plReceive.sendMessage(msg);
                msg.cleanup();
            }
        } catch (Exception e) {
            System.out.println("        loi zone6");
        }
    }

    public void mapInfo(Player pl) {
        Message msg;
        try {
            // Khởi tạo Message với mã -24
            msg = new Message(-24);

            // Ghi thông tin cơ bản về bản đồ và vị trí người chơi vào Message
            msg.writer().writeByte(this.map.mapId);
            msg.writer().writeByte(this.map.planetId);
            msg.writer().writeByte(this.map.tileId);
            msg.writer().writeByte(this.map.bgId);
            msg.writer().writeByte(this.map.type);
            msg.writer().writeUTF(this.map.mapName);
            msg.writer().writeByte(this.zoneId);

            msg.writer().writeShort(pl.location.x);
            msg.writer().writeShort(pl.location.y);

            // Ghi thông tin về waypoints vào Message
            List<WayPoint> wayPoints = this.map.wayPoints;
            msg.writer().writeByte(wayPoints.size());
            for (WayPoint wp : wayPoints) {
                msg.writer().writeShort(wp.minX);
                msg.writer().writeShort(wp.minY);
                msg.writer().writeShort(wp.maxX);
                msg.writer().writeShort(wp.maxY);
                msg.writer().writeBoolean(wp.isEnter);
                msg.writer().writeBoolean(wp.isOffline);
                msg.writer().writeUTF(wp.name);
            }

            // Ghi thông tin về mobs vào Message
            List<Mob> mobs = this.mobs;
            msg.writer().writeByte(mobs.size());
            for (Mob mob : mobs) {
                msg.writer().writeBoolean(false); //is disable
                msg.writer().writeBoolean(false); //is dont move
                msg.writer().writeBoolean(false); //is fire
                msg.writer().writeBoolean(false); //is ice
                msg.writer().writeBoolean(false); //is wind
                msg.writer().writeByte(mob.tempId);
                msg.writer().writeByte(0);
                msg.writer().writeInt(Util.DoubleGioihan(mob.point.gethp()));
                msg.writer().writeByte(mob.level);
                msg.writer().writeInt((Util.DoubleGioihan(mob.point.getHpFull())));
                msg.writer().writeShort(mob.location.x);
                msg.writer().writeShort(mob.location.y);
                msg.writer().writeByte(mob.status);
                msg.writer().writeByte(mob.lvMob);
                msg.writer().writeBoolean(false);
            }

            msg.writer().writeByte(0);

            // Ghi thông tin về NPCs vào Message
            List<Npc> npcs = NpcManager.getNpcsByMapPlayer(pl);
            msg.writer().writeByte(npcs.size());
            for (Npc npc : npcs) {
                msg.writer().writeByte(npc.status);
                msg.writer().writeShort(npc.cx);
                msg.writer().writeShort(npc.cy);
                msg.writer().writeByte(npc.tempId);
                msg.writer().writeShort(npc.avartar);
            }

            // Ghi thông tin về items vào Message
            List<ItemMap> itemsMap = this.getItemMapsForPlayer(pl);
            msg.writer().writeByte(itemsMap.size());
            for (ItemMap it : itemsMap) {
                msg.writer().writeShort(it.itemMapId);
                msg.writer().writeShort(it.itemTemplate.id);
                msg.writer().writeShort(it.x);
                msg.writer().writeShort(it.y);
                msg.writer().writeInt((int) it.playerId);
            }

            // Ghi thông tin về background item vào Message
            try {
                byte[] bgItem = FileIO.readFile("data/girlkun/map/item_bg_map_data/" + this.map.mapId);
                msg.writer().write(bgItem);
            } catch (Exception e) {
                System.out.println("        loi zone7    " + this.map.mapId);
                msg.writer().writeShort(0);
            }
            try {
                byte[] effItem = FileIO.readFile("data/girlkun/map/eff_map/" + this.map.mapId);
                msg.writer().write(effItem);
            } catch (Exception e) {
                System.out.print(RED + "Lỗi zone 8 tại map " + this.map.mapName + " id: " + this.map.mapId + RESET + "\n");

                msg.writer().writeShort(0);
            }

            // Ghi thông tin về loại background vào Message
            msg.writer().writeByte(this.map.bgType);
            msg.writer().writeByte(pl.iDMark.getIdSpaceShip());
            msg.writer().writeByte(0);

            // Gửi Message tới Player
            pl.sendMessage(msg);

            // Dọn dẹp Message
            msg.cleanup();

        } catch (Exception e) {
            System.out.println("        loi zone9");
            Logger.logException(Service.class, e);
        }
    }

    public TrapMap isInTrap(Player player) {
        for (TrapMap trap : this.trapMaps) {
            if (player.location.x >= trap.x && player.location.x <= trap.x + trap.w
                    && player.location.y >= trap.y && player.location.y <= trap.y + trap.h) {
                return trap;
            }
        }
        return null;
    }

    public int getNumOfBosses() {
        return this.bosses.size();
    }
}
