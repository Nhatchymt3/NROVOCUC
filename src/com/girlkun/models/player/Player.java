package com.girlkun.models.player;

import BoMong.BoMong;
import com.girlkun.models.map.MapMaBu.MapMaBu;
import com.girlkun.models.skill.PlayerSkill;

import java.util.List;

import com.girlkun.models.clan.Clan;
import com.girlkun.models.intrinsic.IntrinsicPlayer;
import com.girlkun.models.item.Item;
import com.girlkun.models.item.ItemTime;
import com.girlkun.models.npc.specialnpc.MagicTree;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.consts.ConstTask;
import com.girlkun.models.npc.specialnpc.MabuEgg;
import com.girlkun.models.npc.specialnpc.Timedua;
import com.girlkun.models.mob.MobMe;
import com.girlkun.data.DataGame;
import static com.girlkun.jdbc.daos.PlayerDAO.updatePlayer;
import com.girlkun.models.boss.list_boss.MiNuong;
import com.girlkun.models.card.Card;
import com.girlkun.models.clan.ClanMember;
import com.girlkun.models.item.ItemTimeSieuCap;
import com.girlkun.models.kygui.ItemKyGui;
import com.girlkun.models.kygui.ShopKyGuiManager;
//import com.girlkun.models.kygui.ShopKyGuiService;
import com.girlkun.models.map.TrapMap;
import com.girlkun.models.map.Zone;
import com.girlkun.models.map.bdkb.BanDoKhoBauService;
import com.girlkun.models.map.blackball.BlackBallWar;
import com.girlkun.models.map.doanhtrai.DoanhTraiService;
import com.girlkun.models.map.gas.GasService;
import com.girlkun.models.matches.IPVP;
import com.girlkun.models.matches.TYPE_LOSE_PVP;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.skill.Skill;
//import com.girlkun.models.matches.pvp.DaiHoiVoThuat;
//import com.girlkun.server.Manager;
import com.girlkun.services.Service;
import com.girlkun.server.io.MySession;
import com.girlkun.models.task.TaskPlayer;
import com.girlkun.network.io.Message;
import com.girlkun.server.Client;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.FriendAndEnemyService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.PetService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.SkillService;
import com.girlkun.services.TaskService;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.services.func.ChonAiDay;
import com.girlkun.services.func.CombineNew;
import com.girlkun.utils.Logger;
import static com.girlkun.utils.Logger.RED;
import static com.girlkun.utils.Logger.RESET;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;

public class Player {

    public long lastTimeTargetMob;
    public long timeTargetMob;
    public long lastTimeAttack;

    public boolean isBot = false;
    public boolean muanhieu = false;
    public long vangnhat = 0;
    public long hngocnhat = 0;
    public boolean resetdame = false;
    public long lastTimeDame;
    public long lastTimeaddTNSM;

    public double dametong = 0;
    public byte countBDKB;
    public boolean firstJoinBDKB;
    public long lastimeJoinBDKB;
    public boolean haveNhanBan;
    public int goldChallenge;
    public boolean receivedWoodChest;
    public List<String> textRuongGo = new ArrayList<>();

    public long timeupdateplayer;

    public MySession session;

    public boolean beforeDispose;
    public int soluongmuanhieu = 0;
    public int idmuanhieu = -1;
 

    public boolean autonoitai = false;
    public boolean autocso = false;
    public boolean autoHP = false;
    public boolean autoKI = false;
    public boolean autoSD = false;
    public boolean autoGiap = false;
    public boolean autouse = false;
    public boolean autodau = false;
    public boolean isBuNhin = false;

    public boolean autodrop = false;
    public boolean autohoisinh = false;
    public boolean autodosat = false;
    public boolean batco = false;
    public boolean haveBeQuynh;
    public int mapHoTong;
    public long lastTimeHoTong;

    public boolean titleitem;

    public boolean titlett;
    public boolean isTitleUse1;
    public long lastTimeTitle1;
    public boolean isTitleUse2;
    public long lastTimeTitle2;
    public boolean isTitleUse3;
    public long lastTimeTitle3;
    public boolean isTitleUse4;
    public long lastTimeTitle4;

    public boolean isPet;
    public boolean isNewPet;
    public boolean isTrieuhoipet;

    public boolean isBoss;
    public int NguHanhSonPoint = 0;
    public IPVP pvp;
    public int pointPvp;
    public byte maxTime = 30;
    public byte type = 0;

    public int mapIdBeforeLogout;
    public List<Zone> mapBlackBall;
    public List<Zone> mapMaBu;

    public Zone zone;
    public Zone mapBeforeCapsule;
    public List<Zone> mapCapsule;
    public List<Card> Cards = new ArrayList<>();
    public Pet pet;
    public NewPet newpet;
    public Thu_TrieuHoi TrieuHoipet;
    public NhiemvuChienthan chienthan;
    public MobMe mobMe;
    public Location location;
    public SetClothes setClothes;
    public EffectSkill effectSkill;
    public MabuEgg mabuEgg;
    public Timedua timedua;
    public TaskPlayer playerTask; //nhiệmvu
    public ItemTime itemTime;
    public ItemTimeSieuCap itemTimesieucap;
    public Fusion fusion;
    public MagicTree magicTree;
    public IntrinsicPlayer playerIntrinsic;
    public Inventory inventory;
    public Taixiu taixiu;
    public PlayerSkill playerSkill;
    public CombineNew combineNew;
    public IDMark iDMark;
    public Charms charms;
    public EffectSkin effectSkin;
    public Gift gift;
    public NPoint nPoint;
    public RewardBlackBall rewardBlackBall;
    public EffectFlagBag effectFlagBag;
    public FightMabu fightMabu;

    public SkillSpecial skillSpecial;
    public BoMong achievement;

    public Clan clan;
    public ClanMember clanMember;

    public List<Friend> friends;
    public List<Enemy> enemies;

    public long id;
    public String name;
    public byte gender;
    public boolean isNewMember;
    public short head;

    public byte typePk;

    public int gtPoint;
    public int pointsb;
    public byte cFlag;

    public boolean haveTennisSpaceShip;

    public boolean justRevived;
    public long lastTimeRevived;

    public boolean justRevived1;
    public long lastTimeRevived1;

    public long rankSieuHang;
    public long numKillSieuHang;

    public String TrieuHoiNamePlayer;
    public int TrieuHoiCapBac = -1;
    public String TenThuTrieuHoi;
    public int TrieuHoiThucAn;
    public long TrieuHoiDame;
    public long TrieuHoiHP;
    public long TrieuHoilastTimeThucan;
    public int TrieuHoiLevel;

    public long TrieuHoiExpThanThu;
    public Player TrieuHoiPlayerAttack;
    public double TrieuHoidamethanmeo;
    public long Autothucan;
    public boolean trangthai = false;

    public long diemdanh;
    public int vnd;
    public int admin;
    public byte totalPlayerViolate;
    public long timeChangeZone;
    public long lastTimeUseOption;

    public int bdkb_countPerDay;
    public long bdkb_lastTimeJoin;
    public boolean bdkb_isJoinBdkb;

    public short idNRNM = -1;
    public short idGo = -1;
    public long lastTimePickNRNM;
    public int goldNormar;
    public int goldVIP;
    public int goldTai;
    public int goldXiu;
    public long lastTimeWin;
    public boolean isWin;
    public short idAura = -1;
    public String Hppl = "\n";
    public String HpQuai = "\n";
    public int levelWoodChest;

    public long currentTime;

    public int idnoitai;
    public int chisonoitai;
    public long diemdanh1;
    public Timer timerDHVT;
    public Player _friendGiaoDich;
    public boolean lockPK;

    public Player() {
        lastTimeUseOption = System.currentTimeMillis();
        location = new Location();
        nPoint = new NPoint(this);
        inventory = new Inventory();
        taixiu = new Taixiu();
        playerSkill = new PlayerSkill(this);
        setClothes = new SetClothes(this);
        effectSkill = new EffectSkill(this);
        fusion = new Fusion(this);
        playerIntrinsic = new IntrinsicPlayer();
        rewardBlackBall = new RewardBlackBall(this);
        effectFlagBag = new EffectFlagBag();
        fightMabu = new FightMabu(this);
        //----------------------------------------------------------------------
        iDMark = new IDMark();
        combineNew = new CombineNew();
        playerTask = new TaskPlayer();
        friends = new ArrayList<>();
        enemies = new ArrayList<>();
        itemTime = new ItemTime(this);
        itemTimesieucap = new ItemTimeSieuCap(this);
        charms = new Charms();
        gift = new Gift(this);
        effectSkin = new EffectSkin(this);
        skillSpecial = new SkillSpecial(this);
        achievement = new BoMong(this);
        chienthan = new NhiemvuChienthan();
    }

    //--------------------------------------------------------------------------
    public boolean isDie() {
        if (this.nPoint != null) {
            return this.nPoint.hp <= 0;
        }
        return true;
    }

    //--------------------------------------------------------------------------
    public void setSession(MySession session) {
        this.session = session;
    }

    public void sendMessage(Message msg) {
        if (this.session != null && msg != null) {
            this.session.sendMessage(msg);
        }
    }

    public MySession getSession() {
        return this.session;
    }

    public void CreatePet(String NamePet) {
        this.TenThuTrieuHoi = NamePet;
        this.TrieuHoilastTimeThucan = System.currentTimeMillis();
        this.TrieuHoiThucAn = 100;
        this.TrieuHoiLevel = 0;
        this.TrieuHoiExpThanThu = 0;
        this.TrieuHoiCapBac = 0;
        this.TrieuHoiDame = Util.GioiHannext(10000, 10000L + ((this.TrieuHoiCapBac + 1) * 10000L));
        this.TrieuHoiHP = Util.GioiHannext(100000, 100000L + ((this.TrieuHoiCapBac + 1) * 50000L));
    }

    public boolean isPl() {
        return !isPet && !isBoss && !isNewPet && !isTrieuhoipet;
    }

    public void update() {
        if (this.isBot) {
            active();
        }
        if (this != null && this.name != null && !this.beforeDispose && !this.isBot ) {
            try {
                if (!iDMark.isBan()) {
                    if (nPoint != null) {
                        nPoint.update();
                    }
                    if (fusion != null) {
                        fusion.update();
                    }
                    if (effectSkill != null) {
                        effectSkill.update();
                    }
                    if (mobMe != null) {
                        mobMe.update();
                    }
                    if (effectSkin != null) {
                        effectSkin.update();
                    }
                    if (pet != null) {
                        pet.update();
                    }
                    if (newpet != null) {
                        newpet.update();
                    }
                    if (TrieuHoipet != null) {
                        TrieuHoipet.update();
                    }
                    if (magicTree != null) {
                        magicTree.update();
                    }
                    if (itemTime != null) {
                        itemTime.update();
                    }
                    if (itemTimesieucap != null) {
                        itemTimesieucap.update();
                    }
                    if (this.lastTimeTitle1 != 0 && Util.canDoWithTime(this.lastTimeTitle1, 6000)) {
                        lastTimeTitle1 = 0;
                        isTitleUse1 = false;
                    }
                    if (this.lastTimeTitle2 != 0 && Util.canDoWithTime(this.lastTimeTitle2, 6000)) {
                        lastTimeTitle2 = 0;
                        isTitleUse2 = false;
                    }

                    if (this.lastTimeTitle3 != 0 && Util.canDoWithTime(this.lastTimeTitle3, 6000)) {
                        lastTimeTitle3 = 0;
                        isTitleUse3 = false;
                    }
                    if (this.lastTimeTitle4 != 0 && Util.canDoWithTime(this.lastTimeTitle4, 6000)) {
                        lastTimeTitle4 = 0;
                        isTitleUse4 = false;
                    }
                    GasService.gI().update(this);
                    BanDoKhoBauService.gI().update(this);
                    DoanhTraiService.gI().update(this);
                    BlackBallWar.gI().update(this);
                    MapMaBu.gI().update(this);
                    TimeReset.gI().update(this); //time reset ngày
                    if (!this.isBoss && this.iDMark != null && this.iDMark.isGoToGas() && Util.canDoWithTime(this.iDMark.getLastTimeGotoGas(), 6000)) {
                        ChangeMapService.gI().changeMapBySpaceShip(this, 149, -1, 163);
                        this.iDMark.setGoToGas(false);
                    }
                    if (!this.isBoss && this.iDMark != null && this.iDMark.isGotoFuture() && Util.canDoWithTime(this.iDMark.getLastTimeGoToFuture(), 6000)) {
                        ChangeMapService.gI().changeMapBySpaceShip(this, 102, -1, Util.nextInt(60, 200));
                        this.iDMark.setGotoFuture(false);
                    }
                    if (!this.isBoss && this.iDMark != null && this.iDMark.isGoToBDKB() && Util.canDoWithTime(this.iDMark.getLastTimeGoToBDKB(), 6000)) {
                        ChangeMapService.gI().changeMapBySpaceShip(this, 135, -1, 35);
                        this.iDMark.setGoToBDKB(false);
                    }
                    if (this.zone != null) {
                        TrapMap trap = this.zone.isInTrap(this);
                        if (trap != null) {
                            trap.doPlayer(this);
                        }
                    }

                    if ((this.isPl() || this.isPet) && this.inventory.itemsBody.size() == 13
                            && this.inventory.itemsBody.get(7) != null) {
                        Item it = this.inventory.itemsBody.get(7);
                        if (it != null && it.isNotNullItem() && this.newpet == null) {
                            PetService.Pet2(this, it.template.head, it.template.body, it.template.leg,
                                    it.template.name);
                            Service.getInstance().point(this);
                        }
                    } else if (this.isPl() && newpet != null && !this.inventory.itemsBody.get(7).isNotNullItem()) {// && newpet1 != null
                        newpet.dispose();
                        newpet = null;
                    }
                    if (this.isPl() && this.TrieuHoipet == null && this.TrieuHoiCapBac >= 0
                            && this.TrieuHoiCapBac <= 10) {
                        PetService.Thu_TrieuHoi(this);
                    } else if (this.isPl() && this.TrieuHoipet != null && this.TrieuHoiCapBac < 0
                            && this.TrieuHoiCapBac > 10) {
                        ChangeMapService.gI().exitMap(this.TrieuHoipet);
                        TrieuHoipet.dispose();
                        TrieuHoipet = null;
                    }
                    if (this.isPl() && isWin && this.zone.map.mapId == 51 && Util.canDoWithTime(lastTimeWin, 2000)) {
                        ChangeMapService.gI().changeMapBySpaceShip(this, 52, 0, -1);
                        isWin = false;
                    }
                    if (this.location != null && this.location.lastTimeplayerMove < System.currentTimeMillis() - 30 * 60 * 1000 && this.lastTimeaddTNSM < System.currentTimeMillis() - 30 * 60 * 1000) {
                        Client.gI().kickSession(getSession());
                    }
                } else {
                    if (Util.canDoWithTime(iDMark.getLastTimeBan(), 5000)) {
                        Client.gI().kickSession(session);
                    }
                }
                if (Client.gI().getPlayer(this.name) != null) {
                    this.achievement.plusCount(8);
                }
                if (Util.canDoWithTime(this.lastTimeDame, 5000) && this.dametong != 0) {
                    this.dametong = 0;
                    this.resetdame = true;
                }

                //////////////////// HOÀN TRẢ KÝ GỬI SAU 2 NGÀY ////////////////////
                Iterator<ItemKyGui> iterator1 = ShopKyGuiManager.gI().listItem.iterator();
                int countit = 0;
                while (iterator1.hasNext()) {
                    ItemKyGui it = iterator1.next();
                    if (it != null && it.isBuy == false && it.player_sell == this.id && this.session != null
                            && it.thoigian <= System.currentTimeMillis() - 172800000) {
                        countit++;
                    }
                }

                Iterator<ItemKyGui> iterator = ShopKyGuiManager.gI().listItem.iterator();
                while (iterator.hasNext()) {
                    ItemKyGui it = iterator.next();
                    if (it != null && it.isBuy == false && it.player_sell == this.id && this.session != null
                            && it.thoigian <= System.currentTimeMillis() - 172800000) {

                        if (InventoryServiceNew.gI().getCountEmptyBag(this) < countit) {
                            Service.getInstance().sendThongBao(this, "Hành trang không đủ chỗ trống để hoàn trả vật phẩm kí gửi");
                        } else {
                            Item item = ItemService.gI().createNewItem(it.itemId);
                            item.quantity = it.quantity;
                            item.itemOptions.addAll(it.options);

                            // Remove the current item using the iterator
                            iterator.remove();

                            InventoryServiceNew.gI().addItemBag(this, item);
                            InventoryServiceNew.gI().sendItemBags(this);
                            Service.getInstance().sendMoney(this);
                            Service.getInstance().sendThongBao(this, "Vật phẩm kí đã quá 2 ngày. Vật phẩm đã được hoàn trả");
                        }
                    }
                }
                //////////////////////////////////////////////////////////////////////////////////////////

            } catch (Exception e) {
                e.getStackTrace();
                Logger.logException(Player.class, e, "Lỗi tại player: " + this.name);
            }
        }
    }

    //--------------------------------------------------------------------------
    /*
     * {380, 381, 382}: ht lưỡng long nhất thể xayda trái đất
     * {383, 384, 385}: ht porata xayda trái đất
     * {391, 392, 393}: ht namếc
     * {870, 871, 872}: ht c2 trái đất
     * {873, 874, 875}: ht c2 namếc
     * {867, 878, 869}: ht c2 xayda
     */
    private static final short[][] idOutfitFusion = {
        {380, 381, 382}, {383, 384, 385}, {391, 392, 393},// btc1        
        {1204, 1205, 1206}, {1207, 1208, 1209}, {1210, 1211, 1212}, //btc2
        {1255, 1256, 1257}, {1249, 1250, 1251}, {1246, 1247, 1248}, //btc4
        {1375, 1376, 1377}, {1372, 1373, 1374}, {1369, 1370, 1371} //btc3
    };
//////////////////aura cải trang ////////////////////////////////////////////////////////

    public byte getAura() {
        if (this.inventory.itemsBody.isEmpty() || this.inventory.itemsBody.size() < 10) {
            return -1;
        }
        Item item = this.inventory.itemsBody.get(5);
        if (!item.isNotNullItem()) {
            return -1;
        }
        switch (item.template.id) {
            case 1204:
                return 11;
            case 1440:
                return 17;
            case 2012:
                return 55;
            case 1267:
                return 56;
            case 1238:
                return 11;
            case 9500:
                return 11;
            case 9501:
                return 11;
            case 9502:
                return 11;
            default:
                return -1;
        }

    }

    public byte getEffFront() {
        return -1;
    }

    
    public short getHead() {
        if (effectSkill != null && effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 412;
        } else if (effectSkill != null && effectSkill.isBinh) {
            return 1321;
        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
                return idOutfitFusion[3 + this.gender][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
                return idOutfitFusion[9 + this.gender][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
                return idOutfitFusion[6 + this.gender][0];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            int headd = inventory.itemsBody.get(5).template.head;
            if (headd != -1) {
                return (short) headd;
            }
        }
        return this.head;
    }

    public short getBody() {
        if (effectSkill != null && effectSkill.isMonkey) {
            return 193;
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 413;
        } else if (effectSkill != null && effectSkill.isBinh) {
            return 1322;
        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
                return idOutfitFusion[3 + this.gender][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
                return idOutfitFusion[9 + this.gender][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
                return idOutfitFusion[6 + this.gender][1];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            int body = inventory.itemsBody.get(5).template.body;
            if (body != -1) {
                return (short) body;
            }
        }
        if (inventory != null && inventory.itemsBody.get(0).isNotNullItem()) {
            return inventory.itemsBody.get(0).template.part;
        }
        return (short) (gender == ConstPlayer.NAMEC ? 59 : 57);
    }

    public short getLeg() {
        if (effectSkill != null && effectSkill.isMonkey) {
            return 194;
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 414;
        } else if (effectSkill != null && effectSkill.isBinh) {
            return 1323;
        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
                return idOutfitFusion[3 + this.gender][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
                return idOutfitFusion[9 + this.gender][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
                return idOutfitFusion[6 + this.gender][2];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            int leg = inventory.itemsBody.get(5).template.leg;
            if (leg != -1) {
                return (short) leg;
            }
        }
        if (inventory != null && inventory.itemsBody.get(1).isNotNullItem()) {
            return inventory.itemsBody.get(1).template.part;
        }
        return (short) (gender == 1 ? 60 : 58);
    }

    public short getFlagBag() {
        if (this.iDMark.isHoldBlackBall()) {
            return 31;
        } else if (this.idNRNM >= 353 && this.idNRNM <= 359) {
            return 30;
        }
        if (this.inventory.itemsBody.size() == 12) {
            if (this.inventory.itemsBody.get(8).isNotNullItem()) {
                return this.inventory.itemsBody.get(8).template.part;
            }
        }
        if (this.isPet) {
            if (this.inventory.itemsBody.get(7).isNotNullItem()) {
                return this.inventory.itemsBody.get(7).template.part;
            }
        }
        if (TaskService.gI().getIdTask(this) == ConstTask.TASK_3_2) {
            return 28;
        }
        if (this.clan != null) {
            return (short) this.clan.imgId;
        }
        return -1;
    }

    public short getMount() {
        if (this.inventory.itemsBody.isEmpty() || this.inventory.itemsBody.size() < 10) {
            return -1;
        }
        Item item = this.inventory.itemsBody.get(9);
        if (!item.isNotNullItem()) {
            return -1;
        }
        if (item.template.type == 24) {
            if (item.template.gender == 3 || item.template.gender == this.gender) {
                return item.template.id;
            } else {
                return -1;
            }
        } else {
            if (item.template.id < 500) {
                return item.template.id;
            } else {
                return (short) DataGame.MAP_MOUNT_NUM.get(String.valueOf(item.template.id));
            }
        }

    }

    public String tenskill9(int skill) {
        switch (skill) {
            case 2:
                return "Cadic Liên hoàn chưởng";
            case 1:
                return "Ma Phong Ba";
            case 0:
                return "Super Kamejoko";
            default:
                return "";
        }
    }
///////////////task chiến thần

    public String nhiemvuchienthan(int nhiemvu) {
        switch (nhiemvu) {
            case 10:
                this.chienthan.maxcount = 100;
                return "Giết 50 BOSS Tương Lai";
            case 9:
                this.chienthan.maxcount = 10;
                return "Giết 10 Boss Đôremon";
            case 8:
                this.chienthan.maxcount = 10;
                return "Đi Bản đồ kho báu 10 lần";
            case 7:
                this.chienthan.maxcount = 10;
                return "Đi map Khí Gas 10 lần";
            case 6:
                this.chienthan.maxcount = 5000;
                return "Chưởng Chí mạng 5000 lần";
            case 5:
                this.chienthan.maxcount = 5000;
                return "Hạ 5.000 Quái bay";
            case 4:
                this.chienthan.maxcount = 2000;
                return "Nhặt 2000 Capsule Kì bí";
            case 3:
                this.chienthan.maxcount = 10;
                return "Hộ tống Thành công Bunma 10 lần";
            case 2:
                this.chienthan.maxcount = 10;
                return "Tìm được 10 Món đồ Thần linh bên Cold";
            case 1:
                this.chienthan.maxcount = 5000;
                return "Giết 5.000 Sên 8";
            default:
                return "";
        }
    }
    public void chat(String text) {
       try {
        Service.gI().chat(this, text);
       } catch (Exception e) {
        // TODO: handle exception
       }
    }
    public void checkAnThan(Player plAtt) {
        if (plAtt != null && (plAtt.isPl() || plAtt.isPet) && plAtt.effectSkill.isAnThan) {
            EffectSkillService.gI().removeAnThan(plAtt);
        }
    }

    public String NameThanthu(int CapBac) {
        switch (CapBac) {
            case 10:
                return "Thú Cưng LEVEL 11";
            case 9:
                return "Thú Cưng LEVEL 10";
            case 8:
                return "Thú Cưng LEVEL 9";
            case 7:
                return "Thú Cưng LEVEL 8";
            case 6:
                return "Thú Cưng LEVEL 7";
            case 5:
                return "Thú Cưng LEVEL 6";
            case 4:
                return "Thú Cưng LEVEL 5";
            case 3:
                return "Thú Cưng LEVEL 4";
            case 2:
                return "Thú Cưng LEVEL 3";
            case 1:
                return "Thú Cưng LEVEL 2";
            case 0:
                return "Thú Cưng LEVEL 1";
            default:
                return "Phế Vật";
        }
    }

    public String DaDotpha(int CapBac) {
        switch (CapBac) {
            case 9:
                return "Đế Vương Thạch";
            case 8:
                return "Hỏa Hồn Thạch";
            case 7:
                return "Thiên Mệnh Thạch";
            case 6:
                return "Huyết Tinh Thạch";
            case 5:
                return "Linh Vân Thạch";
            case 4:
                return "Mịch Lâm Thạch";
            default:
                return "Đá đột phá";
        }
    }

    public String TrieuHoiKiNang(int CapBac) {
        switch (CapBac) {
            case 10:
                return "Tìm " + ((TrieuHoiLevel + 1) * 20) + " Hồng ngọc cho Chủ nhân\n"
                        + "Tăng " + (TrieuHoiLevel + 1) + "% HP, KI, Giáp, SD, SD chí mạng cho Chủ nhân\n";
            case 9:
                return "Tìm " + ((TrieuHoiLevel + 1) * 20) + " Hồng ngọc cho Chủ nhân\n"
                        + "Tăng " + ((TrieuHoiLevel + 1) / 2) + "% HP, KI, Giáp, SD, SD chí mạng cho Chủ nhân";
            case 8:
                return "Tìm " + ((TrieuHoiLevel + 1) * 10) + " Hồng ngọc cho Chủ nhân\n"
                        + "Tăng " + ((TrieuHoiLevel + 1) / 5) + "% HP, KI, Giáp, SD cho Chủ nhân";
            case 7:
                return "Tăng " + ((TrieuHoiLevel + 1) / 5) + "% HP, KI, Giáp, SD cho Chủ nhân";
            case 6:
                return "Tăng " + ((TrieuHoiLevel + 1) / 3) + "% SD Chí mạng cho Chủ nhân";
            case 5:
                return "Tăng " + ((TrieuHoiLevel + 1) / 5) + "% SD cho Chủ nhân";
            case 4:
                return "Tăng " + ((TrieuHoiLevel + 1) * 30) + " HP, KI, SD, Giáp cho Chủ nhân";
            case 3:
                return "Tăng " + ((TrieuHoiLevel + 1) * 20) + " HP, KI\n" + ((TrieuHoiLevel + 1) * 10) + " SD cho Chủ nhân";
            case 2:
                return "Tăng " + ((TrieuHoiLevel + 1) * 10) + " Sức đánh cho Chủ nhân";
            case 1:
                return "Tăng " + ((TrieuHoiLevel + 1) * 20) + "HP, KI cho Chủ nhân";
            case 0:
                return "Tăng " + ((TrieuHoiLevel + 1) * 20) + " HP ,KI cho Chủ nhân";
            default:
                return "Phế vật mà làm được gì !!!";
        }
    }

    //--------------------------------------------------------------------------
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            int TileChinhxac = 0;
            if (plAtt != null) {
                TileChinhxac = plAtt.nPoint.tlchinhxac;
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                    case Skill.DRAGON:
                    case Skill.DEMON:
                    case Skill.GALICK:
                    case Skill.KAIOKEN:
                    case Skill.LIEN_HOAN:
                    case Skill.DE_TRUNG:
                        if (this.nPoint.voHieuChuong > 0) {
                            com.girlkun.services.PlayerService.gI().hoiPhuc(this, 0, Util.DoubleGioihan(damage * this.nPoint.voHieuChuong / 100));
                            return 0;
                        }
                }
            }
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon - TileChinhxac, 100)) {
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            } // bùa bất tử
            if (isMobAttack && this.charms.tdBatTu > System.currentTimeMillis() && damage >= this.nPoint.hp) {
                damage = Util.DoubleGioihan(this.nPoint.hp - 1); //quái tấn công
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                if (plAtt != null && this.zone.map.mapId == 129) {
                    plAtt.pointPvp++;
                }
                setDie(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }
    

    protected void setDie(Player plAtt) {
        //xóa phù
        if (this.effectSkin.xHPKI > 1) {
            this.effectSkin.xHPKI = 1;
            Service.getInstance().point(this);
        }
        //xóa tụ skill đặc biệt
        this.playerSkill.prepareQCKK = false;
        this.playerSkill.prepareLaze = false;
        this.playerSkill.prepareTuSat = false;
        //xóa hiệu ứng skill
        this.effectSkill.removeSkillEffectWhenDie();
        //
        nPoint.setHp(0);
        nPoint.setMp(0);
        //xóa trứng
        if (this.mobMe != null) {
            this.mobMe.mobMeDie();
        }
        Service.getInstance().charDie(this);
        //add kẻ thù
        if (!this.isPet && !this.isNewPet && !this.isTrieuhoipet && !this.isBoss
                && plAtt != null && !plAtt.isPet && !plAtt.isNewPet && !plAtt.isTrieuhoipet && !plAtt.isBoss) {
            if (!plAtt.itemTime.isUseAnDanh) {
                FriendAndEnemyService.gI().addEnemy(this, plAtt);
            }
        }
        //kết thúc pk
        if (this.pvp != null) {
            this.pvp.lose(this, TYPE_LOSE_PVP.DEAD);
        }

        // PVPServcice.gI().finishPVP(this, PVP.TYPE_DIE);
        BlackBallWar.gI().dropBlackBall(this);
    }

    //--------------------------------------------------------------------------
    public void setClanMember() {
        if (this.clanMember != null) {
            this.clanMember.powerPoint = this.nPoint.power;
            this.clanMember.head = this.getHead();
            this.clanMember.body = this.getBody();
            this.clanMember.leg = this.getLeg();
        }
    }

    public boolean isAdmin() {
        return this.session.isAdmin;
    }

    public boolean check99ThucAnHuyDiet() {
        for (Item item : this.inventory.itemsBag) {
            if (item != null && item.template != null && item.template.id >= 663 && item.template.id <= 667 && item.quantity >= 99) {
                return true;
            }
        }
        return false;
    }

    public void setJustRevivaled() {
        if (this.isPet) {
            this.justRevived = true;
            this.lastTimeRevived = System.currentTimeMillis();
        } else if (this.isTrieuhoipet) {
            this.justRevived1 = true;
            this.lastTimeRevived1 = System.currentTimeMillis();
        }
    }

    public void preparedToDispose() {

    }

    public void dispose() {
        if (pet != null) {
            pet.dispose();
            pet = null;
        }
        if (newpet != null) {
            newpet.dispose();
            newpet = null;
        }

        if (TrieuHoipet != null) {
            TrieuHoipet.dispose();
            TrieuHoipet = null;
        }
        if (mapBlackBall != null) {
            mapBlackBall.clear();
            mapBlackBall = null;
        }
        zone = null;
        mapBeforeCapsule = null;
        if (mapMaBu != null) {
            mapMaBu.clear();
            mapMaBu = null;
        }
        zone = null;
        mapBeforeCapsule = null;
        if (mapCapsule != null) {
            mapCapsule.clear();
            mapCapsule = null;
        }
        if (mobMe != null) {
            mobMe.dispose();
            mobMe = null;
        }
        location = null;
        if (setClothes != null) {
            setClothes.dispose();
            setClothes = null;
        }
        if (effectSkill != null) {
            effectSkill.dispose();
            effectSkill = null;
        }
        if (mabuEgg != null) {
            mabuEgg.dispose();
            mabuEgg = null;
        }
        if (timedua != null) {
            timedua.dispose();
            timedua = null;
        }
        if (taixiu != null) {
            taixiu.dispose();
            taixiu = null;
        }
        if (chienthan != null) {
            chienthan.dispose();
            chienthan = null;
        }
        if (skillSpecial != null) {
            skillSpecial.dispose();
            skillSpecial = null;
        }
        if (playerTask != null) {
            playerTask.dispose();
            playerTask = null;
        }
        if (itemTime != null) {
            itemTime.dispose();
            itemTime = null;
        }
        if (itemTimesieucap != null) {
            itemTimesieucap.dispose();
            itemTimesieucap = null;
        }
        if (fusion != null) {
            fusion.dispose();
            fusion = null;
        }
        if (magicTree != null) {
            magicTree.dispose();
            magicTree = null;
        }
        if (playerIntrinsic != null) {
            playerIntrinsic.dispose();
            playerIntrinsic = null;
        }
        if (inventory != null) {
            inventory.dispose();
            inventory = null;
        }
        if (playerSkill != null) {
            playerSkill.dispose();
            playerSkill = null;
        }
        if (combineNew != null) {
            combineNew.dispose();
            combineNew = null;
        }
        if (iDMark != null) {
            iDMark.dispose();
            iDMark = null;
        }
        if (charms != null) {
            charms.dispose();
            charms = null;
        }
        if (effectSkin != null) {
            effectSkin.dispose();
            effectSkin = null;
        }
        if (gift != null) {
            gift.dispose();
            gift = null;
        }
        if (nPoint != null) {
            nPoint.dispose();
            nPoint = null;
        }
        if (rewardBlackBall != null) {
            rewardBlackBall.dispose();
            rewardBlackBall = null;
        }
        if (effectFlagBag != null) {
            effectFlagBag.dispose();
            effectFlagBag = null;
        }
        if (pvp != null) {
            pvp.dispose();
            pvp = null;
        }
        effectFlagBag = null;
        clan = null;
        clanMember = null;
        friends = null;
        enemies = null;
        session = null;
        name = null;
    }
//////////// bot/////////////////////

    public void active() {
        if (this.isBot) {
            if (this.isDie()) {
                this.nPoint.hp = this.nPoint.hpMax;
            }
            if (this.nPoint.mp <= 0) {
                this.nPoint.mp = this.nPoint.mpMax;
            }
            this.attack();
        }
    }
    public Mob mobTarget;

    public Mob getMobAttack() {
        clearInvalidTarget();
        return findNewTarget();
    }

    private void clearInvalidTarget() {
        if (mobTarget != null && (mobTarget.isDie() || !zone.equals(mobTarget.zone))) {
            mobTarget = null;
        }
    }

    private Mob findNewTarget() {
        if (mobTarget == null && Util.canDoWithTime(lastTimeTargetMob, timeTargetMob)) {
            mobTarget = zone.getRandomMobInMap();
            lastTimeTargetMob = System.currentTimeMillis();
            timeTargetMob = 500;
        }
        return mobTarget;
    }

    public int getRangeCanAttackWithSkillSelect() {
        int skillId = this.playerSkill.skillSelect.template.id;
        if (skillId == Skill.KAMEJOKO || skillId == Skill.MASENKO || skillId == Skill.ANTOMIC) {
            return Skill.RANGE_ATTACK_CHIEU_CHUONG;
        } else if (skillId == Skill.DRAGON || skillId == Skill.DEMON || skillId == Skill.GALICK) {
            return Skill.RANGE_ATTACK_CHIEU_DAM;
        }
        return 752002;
    }

    public void moveTo(int x, int y) {
        byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
        byte move = (byte) Util.nextInt(40, 60);
        if (isBot) {
            move = (byte) (move * (byte) 2);
        }
        PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y + (Util.isTrue(3, 10) ? -50 : 0));
    }

    public void attack() {
        if (!this.isBot) {
            return;
        }

        if (!Util.canDoWithTime(lastTimeAttack, 100) || this.mobTarget == null || this.mobTarget.isDie()) {
            this.mobTarget = getMobAttack();
        }

        if (Util.canDoWithTime(lastTimeAttack, 100) && this.mobTarget != null) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Mob m = this.getMobAttack();
                if (m == null || m.isDie()) {
                    return;
                }

                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));

                if (Util.nextInt(100) < 70) {
                    this.playerSkill.skillSelect = this.playerSkill.skills.get(0);
                }

                if (Util.getDistance(this, m) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        this.moveToWithSkillChuong(m);
                    } else {
                        this.moveTo(m.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                Util.nextInt(10) % 2 == 0 ? m.location.y : m.location.y);
                    }
                    SkillService.gI().useSkill(this, null, m, null);
                } else {
                    this.moveTo(m.location.x, m.location.y);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void moveToWithSkillChuong(Mob mob) {
        int moveX = mob.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200));
        int moveY = Util.nextInt(10) % 2 == 0 ? mob.location.y : mob.location.y;
        this.moveTo(moveX, moveY);
    }

    public String percentGold(int type) {
        try {
            if (type == 0) {
                double percent = ((double) this.goldNormar / ChonAiDay.gI().goldNormar) * 100;
                return String.valueOf(Math.ceil(percent));
            } else if (type == 1) {
                double percent = ((double) this.goldVIP / ChonAiDay.gI().goldVip) * 100;
                return String.valueOf(Math.ceil(percent));
            }
        } catch (ArithmeticException e) {
            return "0";
        }
        return "0";
    }

    public void setName(String playerName) {
        this.name = playerName;
    }

}
//nplayer
