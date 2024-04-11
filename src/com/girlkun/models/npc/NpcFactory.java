package com.girlkun.models.npc;

import com.girlkun.consts.ConstMap;
import com.girlkun.models.map.challenge.MartialCongressService;
import com.girlkun.services.*;
import com.girlkun.consts.ConstNpc;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.consts.ConstTask;
import com.girlkun.models.map.bdkb.BanDoKhoBau;
import com.girlkun.models.map.bdkb.BanDoKhoBauService;
import com.girlkun.database.GirlkunDB;
import com.girlkun.jdbc.daos.GodGK;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.list_boss.MiNuong;
import com.girlkun.models.boss.list_boss.NhanBan;
import com.girlkun.models.clan.Clan;
import com.girlkun.models.clan.ClanMember;
import com.girlkun.models.player.Thu_TrieuHoi;

import java.util.HashMap;
import java.util.List;

import com.girlkun.services.func.ChangeMapService;
import com.girlkun.services.func.SummonDragon;

import static com.girlkun.services.func.SummonDragon.SHENRON_1_STAR_WISHES_1;
import static com.girlkun.services.func.SummonDragon.SHENRON_1_STAR_WISHES_2;
import static com.girlkun.services.func.SummonDragon.SHENRON_SAY;

import com.girlkun.models.player.Player;
import com.girlkun.models.item.Item;
import com.girlkun.models.kygui.ShopKyGuiService;
import com.girlkun.models.map.Map;
import com.girlkun.models.map.blackball.BlackBallWar;
import com.girlkun.models.map.MapMaBu.MapMaBu;
import com.girlkun.models.map.doanhtrai.DoanhTrai;
import com.girlkun.models.map.doanhtrai.DoanhTraiService;
import com.girlkun.models.map.gas.Gas;
import com.girlkun.models.map.gas.GasService;
import com.girlkun.models.matches.PVPService;
import com.girlkun.models.matches.TOP;
import com.girlkun.models.matches.pvp.DaiHoiVoThuat;
import com.girlkun.models.matches.pvp.DaiHoiVoThuatService;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.shop.ShopServiceNew;
import com.girlkun.models.skill.Skill;
import com.girlkun.network.io.Message;
import com.girlkun.server.Client;
import com.girlkun.server.Maintenance;
import com.girlkun.server.Manager;
import static com.girlkun.services.NgocRongNamecService.TIME_OP;
import com.girlkun.services.func.CombineServiceNew;
import com.girlkun.services.func.Input;
import com.girlkun.services.func.LuckyRound;
import com.girlkun.utils.Logger;
import com.girlkun.utils.TimeUtil;
import com.girlkun.utils.Util;
import java.util.ArrayList;
import com.girlkun.services.func.GoiRongBang;
import com.girlkun.services.func.GoiRongXuong;
import com.girlkun.services.func.TaiXiu;
import com.girlkun.utils.SkillUtil;
import java.sql.Connection;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NpcFactory {

    private static final int COST_HD = 50000000;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static boolean nhanVang = false;
    private static boolean nhanDeTu = false;

    public static int noibanh = 0;
    //playerid - object
    public static final java.util.Map<Long, Object> PLAYERID_OBJECT = new HashMap<Long, Object>();
    public static ScheduledExecutorService run = Executors.newSingleThreadScheduledExecutor();

    private NpcFactory() {

    }

    private static Npc champa(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "|7|Tiêu diệt Boss Zeno-sama sẽ rơi các vật phẩm cực hiếm"
                                + "\n|7|Gồm các item [cải trang,linh thú, đá cường hóa , đá ngũ sắc , đồng xu vàng..."
                                + "\n|7|Người tiêu diệt boss sẽ được cộng 100 điểm boss"
                                + "\n|7|Số lương rơi ngẫu nhiên từ x99 - x999", "Đến Tiêu\nDiệt Zeno", "Từ chối");
                    } else if (this.mapId == 209) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "|7|Người muốn gì", "Về Nhà", "Đéo");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 209, -1, 306);
                                    break;
                                }
                            }
                        }
                    } else if (this.mapId == 209) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 306);
                                break;

                        }
                    }
                }
            }
        };
    }

    private static Npc trungLinhThu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 104 || this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đổi Trứng Linh thú cần:\b|7|X99 Hồn Linh Thú + 1 Tỷ vàng", "Đổi Trứng\nLinh thú", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 104 || this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    Item honLinhThu = null;
                                    try {
                                        honLinhThu = InventoryServiceNew.gI().findItemBag(player, 2029);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (honLinhThu == null || honLinhThu.quantity < 99) {
                                        this.npcChat(player, "Bạn không đủ 99 Hồn Linh thú");
                                    } else if (player.inventory.gold < 1_000_000_000) {
                                        this.npcChat(player, "Bạn không đủ 1 Tỷ vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        player.inventory.gold -= 1_000_000_000;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu, 99);
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 2028);
                                        trungLinhThu.itemOptions.add(new Item.ItemOption(30, 1));
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 Trứng Linh thú");
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc trungdetu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                Item trung1 = InventoryServiceNew.gI().findItemBag(player, 1738);
                Item trung2 = InventoryServiceNew.gI().findItemBag(player, 1739);
                Item trung3 = InventoryServiceNew.gI().findItemBag(player, 1740);
                Item trung4 = InventoryServiceNew.gI().findItemBag(player, 1741);
                Item trung5 = InventoryServiceNew.gI().findItemBag(player, 1742);
                if (canOpenNpc(player)) {
                    if (mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.APTRUNG,
                                "|7|Người muốn gì ở ta",
                                "Ấp Trứng"
                        );

                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    Item trung1 = InventoryServiceNew.gI().findItemBag(player, 1738);
                    Item trung2 = InventoryServiceNew.gI().findItemBag(player, 1739);
                    Item trung3 = InventoryServiceNew.gI().findItemBag(player, 1740);
                    Item trung4 = InventoryServiceNew.gI().findItemBag(player, 1741);
                    Item trung5 = InventoryServiceNew.gI().findItemBag(player, 1742);
                    if (mapId == 5) {
                        switch (player.iDMark.getIndexMenu()) {
                            case ConstNpc.APTRUNG:
                                this.createOtherMenu(player, ConstNpc.LUACHONTRUNG, "|7|Hãy chọn trứng mà con muốn ấp",
                                        "Ấp Trứng\nMa Bư",
                                        "Ấp Trứng\nBeerus",
                                        "Ấp Trứng\n Siêu Broly",
                                        "Ấp Trứng\nNgộ Không",
                                        "Ấp Trứng\nFusion Zamus"
                                );
                                break;
                            case ConstNpc.LUACHONTRUNG:
                                switch (select) {
                                    case 0:
                                        this.createOtherMenu(player, ConstNpc.DTMABU, "|7|Hãy chọn hành tinh",
                                                "Trái Đất",
                                                "Namek",
                                                "Xayda"
                                        );
                                        break;
                                    case 1:
                                        this.createOtherMenu(player, ConstNpc.DTBERUS, "|7|Hãy chọn hành tinh",
                                                "Trái Đất",
                                                "Namek",
                                                "Xayda"
                                        );
                                        break;
                                    case 2:
                                        this.createOtherMenu(player, ConstNpc.DTSUPER, "|7|Hãy chọn hành tinh",
                                                "Trái Đất",
                                                "Namek",
                                                "Xayda"
                                        );
                                        break;
                                    case 3:
                                        this.createOtherMenu(player, ConstNpc.DTWUKONG, "|7|Hãy chọn hành tinh",
                                                "Trái Đất",
                                                "Namek",
                                                "Xayda"
                                        );
                                        break;
                                    case 4:
                                        this.createOtherMenu(player, ConstNpc.DTZAMUS, "|7|Hãy chọn hành tinh",
                                                "Trái Đất",
                                                "Namek",
                                                "Xayda"
                                        );
                                        break;
                                }
                                break;
                            case ConstNpc.DTMABU:
                                switch (select) {
                                    case 0:
                                        if (player.pet != null) {
                                            if (trung1 != null && trung1.quantity >= 1) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, trung1, 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                PetService.gI().changeMabuPet(player, 0);
                                            } else {
                                                this.npcChat(player, "|7|Bạn không có trứng");
                                            }
                                        } else {
                                            this.npcChat(player, "|7| Bạn chưa có đệ tử thường");
                                        }
                                        break;
                                    case 1:
                                        if (player.pet != null) {
                                            if (trung1 != null && trung1.quantity >= 1) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, trung1, 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                PetService.gI().changeMabuPet(player, 1);
                                            } else {
                                                this.npcChat(player, "|7|Bạn không có trứng");
                                            }
                                        } else {
                                            this.npcChat(player, "|7| Bạn chưa có đệ tử thường");
                                        }
                                        break;
                                    case 2:
                                        if (player.pet != null) {
                                            if (trung1 != null && trung1.quantity >= 1) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, trung1, 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                PetService.gI().changeMabuPet(player, 2);
                                            } else {
                                                this.npcChat(player, "|7|Bạn không có trứng");
                                            }
                                        } else {
                                            this.npcChat(player, "|7| Bạn chưa có đệ tử thường");
                                        }
                                        break;
                                }
                                break;
                            case ConstNpc.DTBERUS:
                                switch (select) {
                                    case 0:
                                        if (player.pet != null) {
                                            if (trung2 != null && trung2.quantity >= 1) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, trung2, 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                PetService.gI().changeBerusPet(player, 0);
                                            } else {
                                                this.npcChat(player, "|7|Bạn không có trứng");
                                            }
                                        } else {
                                            this.npcChat(player, "|7| Bạn chưa có đệ tử thường");
                                        }
                                        break;
                                    case 1:
                                        if (player.pet != null) {
                                            if (trung2 != null && trung2.quantity >= 1) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, trung2, 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                PetService.gI().changeBerusPet(player, 1);
                                            } else {
                                                this.npcChat(player, "|7|Bạn không có trứng");
                                            }
                                        } else {
                                            this.npcChat(player, "|7| Bạn chưa có đệ tử thường");
                                        }
                                        break;
                                    case 2:
                                        if (player.pet != null) {
                                            if (trung2 != null && trung2.quantity >= 1) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, trung2, 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                PetService.gI().changeBerusPet(player, 2);
                                            } else {
                                                this.npcChat(player, "|7|Bạn không có trứng");
                                            }
                                        } else {
                                            this.npcChat(player, "|7| Bạn chưa có đệ tử thường");
                                        }
                                        break;
                                }
                                break;
                            case ConstNpc.DTSUPER:
                                switch (select) {
                                    case 0:
                                        if (player.pet != null) {
                                            if (trung3 != null && trung3.quantity >= 1) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, trung3, 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                PetService.gI().changePicPet(player, 0);
                                            } else {
                                                this.npcChat(player, "|7|Bạn không có trứng");
                                            }
                                        } else {
                                            this.npcChat(player, "|7| Bạn chưa có đệ tử thường");
                                        }
                                        break;
                                    case 1:
                                        if (player.pet != null) {
                                            if (trung3 != null && trung3.quantity >= 1) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, trung3, 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                PetService.gI().changePicPet(player, 1);
                                            } else {
                                                this.npcChat(player, "|7|Bạn không có trứng");
                                            }
                                        } else {
                                            this.npcChat(player, "|7| Bạn chưa có đệ tử thường");
                                        }
                                        break;
                                    case 2:
                                        if (player.pet != null) {
                                            if (trung3 != null && trung3.quantity >= 1) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, trung3, 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                PetService.gI().changePicPet(player, 2);
                                            } else {
                                                this.npcChat(player, "|7|Bạn không có trứng");
                                            }
                                        } else {
                                            this.npcChat(player, "|7| Bạn chưa có đệ tử thường");
                                        }
                                        break;
                                }
                                break;
                            case ConstNpc.DTWUKONG:
                                switch (select) {
                                    case 0:
                                        if (player.pet != null) {
                                            if (trung4 != null && trung4.quantity >= 1) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, trung4, 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                PetService.gI().changegohanzombie(player, 0);
                                            } else {
                                                this.npcChat(player, "|7|Bạn không có trứng");
                                            }
                                        } else {
                                            this.npcChat(player, "|7| Bạn chưa có đệ tử thường");
                                        }
                                        break;
                                    case 1:
                                        if (player.pet != null) {
                                            if (trung4 != null && trung4.quantity >= 1) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, trung4, 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                PetService.gI().changegohanzombie(player, 1);
                                            } else {
                                                this.npcChat(player, "|7|Bạn không có trứng");
                                            }
                                        } else {
                                            this.npcChat(player, "|7| Bạn chưa có đệ tử thường");
                                        }
                                        break;
                                    case 2:
                                        if (player.pet != null) {
                                            if (trung4 != null && trung4.quantity >= 1) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, trung4, 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                PetService.gI().changegohanzombie(player, 2);
                                            } else {
                                                this.npcChat(player, "|7|Bạn không có trứng");
                                            }
                                        } else {
                                            this.npcChat(player, "|7| Bạn chưa có đệ tử thường");
                                        }
                                        break;
                                }
                                break;
                            case ConstNpc.DTZAMUS:
                                switch (select) {
                                    case 0:
                                        if (player.pet != null) {
                                            if (trung5 != null && trung5.quantity >= 1) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, trung5, 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                PetService.gI().changeGokuEVO(player, 0);
                                            } else {
                                                this.npcChat(player, "|7|Bạn không có trứng");
                                            }
                                        } else {
                                            this.npcChat(player, "|7| Bạn chưa có đệ tử thường");
                                        }
                                        break;
                                    case 1:
                                        if (player.pet != null) {
                                            if (trung5 != null && trung5.quantity >= 1) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, trung5, 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                PetService.gI().changeGokuEVO(player, 1);
                                            } else {
                                                this.npcChat(player, "|7|Bạn không có trứng");
                                            }
                                        } else {
                                            this.npcChat(player, "|7| Bạn chưa có đệ tử thường");
                                        }
                                        break;
                                    case 2:
                                        if (player.pet != null) {
                                            if (trung5 != null && trung5.quantity >= 1) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, trung5, 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                PetService.gI().changeGokuEVO(player, 2);
                                            } else {
                                                this.npcChat(player, "|7|Bạn không có trứng");
                                            }
                                        } else {
                                            this.npcChat(player, "|7| Bạn chưa có đệ tử thường");
                                        }
                                        break;
                                }
                        }

                    }

                }
            }

        };
    }

    private static Npc popo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.clan == null) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "|7|KHÍ GAS\n|6|Map Khí Gas chỉ dành cho những người có bang hội",
                                "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (player.getSession().is_gift_box) {
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "|7|KHÍ GAS\n|6|Thượng đế vừa phát hiện 1 loại khí đang âm thầm\nhủy diệt mọi mầm sống trên Trái Đất,\nnó được gọi là Destron Gas.\nTa sẽ đưa các cậu đến nơi ấy, các cậu sẵn sàng chưa?", "Thông Tin Chi Tiết", "OK", " Bố Từ Chối");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 1:
                                if (player.clan != null) {
                                    if (player.clan.khiGas != null) {
                                        this.createOtherMenu(player, ConstNpc.MENU_OPENED_GAS,
                                                "|7|KHÍ GAS\n|6|Bang hội của con đang đi DesTroy Gas cấp độ "
                                                + player.clan.khiGas.level + "\nCon có muốn đi theo không?",
                                                "Đồng ý", "Từ chối");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.MENU_OPEN_GAS,
                                                "|7|KHÍ GAS\n|6|Khí Gas Huỷ Diệt đã chuẩn bị tiếp nhận các đợt tấn công của quái vật\n"
                                                + "các con hãy giúp chúng ta tiêu diệt quái vật \n"
                                                + "Ở đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                                                "Chọn\ncấp độ", "Từ chối");
                                    }
                                } else {
                                    this.npcChat(player, "Con phải có bang hội ta mới có thể cho con đi");
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPENED_GAS) {
                        switch (select) {
                            case 0:
                                if (player.isAdmin() || player.nPoint.power >= Gas.POWER_CAN_GO_TO_GAS) {
                                    ChangeMapService.gI().goToGas(player);
                                } else {
                                    this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                            + Util.numberToMoney(Gas.POWER_CAN_GO_TO_GAS));
                                }
                                break;

                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_GAS) {
                        switch (select) {
                            case 0:
                                if (player.isAdmin() || player.nPoint.power >= Gas.POWER_CAN_GO_TO_GAS) {
                                    Input.gI().createFormChooseLevelGas(player);
                                } else {
                                    this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                            + Util.numberToMoney(Gas.POWER_CAN_GO_TO_GAS));
                                }
                                break;
                        }

                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCPET_GO_TO_GAS) {
                        switch (select) {
                            case 0:
                                if (player.chienthan.tasknow == 7) {
                                    player.chienthan.dalamduoc++;
                                }
                                GasService.gI().openKhiGas(player, Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
                                break;
                        }
                    }
                }
            }
        };
    }

    private static Npc maygap(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.playerTask.taskMain.id >= 10) {
                            this.createOtherMenu(player, 1234, "|7|- •⊹٭Ngọc Rồng٭⊹• -\nMÁY GẮP QUÀ"
                                    + "\n|1|Có thể gắp ra cải trang hay item VIP hơn shop bán",
                                    "Dùng\n Xu Vàng", "Rương Quà", "Đổi Điẻm");

                        } else {
                            this.createOtherMenu(player, 332332, "|7|Yều cầu hoàn thành nhiệm vụ 10"
                            );
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        int soxu = 1;
                        int sodiem = 3;
                        int[] listvatphamcui = new int[]{381, 382, 383, 384, 457,
                            1529, 1518, 1519, 1099, 1100, 1101, 1102
                        };
                        int[] listvatphamvip = new int[]{1334, 1531, 1532, 1528
                        };
                        int[] listvanbay = new int[]{1297, 1298, 1345, 1200};
                        int[] listpet = new int[]{547, 548, 549, 285, 1196};
                        int[] listcaitrang = new int[]{1437, 1438, 1439, 1446, 1447};
                        int[] listvpdl = new int[]{1323, 1324, 1325, 1255, 1240, 1245, 1998, 1999, 1316, 1248, 1985, 1986, 1987}; //danh sách các item  
                        ///duyệt qua list mảng
                        int randomvpcui = new Random().nextInt(listvatphamcui.length);
                        int randomvpvip = new Random().nextInt(listvatphamvip.length);
                        int randomIndex = new Random().nextInt(listvpdl.length);
                        int randomIndex1 = new Random().nextInt(listcaitrang.length);
                        int randomIndex2 = new Random().nextInt(listvanbay.length);
                        int randomIndex3 = new Random().nextInt(listpet.length);
                        //chon id
                        int selectedItemId = listvpdl[randomIndex];
                        int selectedItemId1 = listcaitrang[randomIndex1];
                        int selectedItemId2 = listvanbay[randomIndex2];
                        int selectedItemId3 = listpet[randomIndex3];
                        Item vpdl = Util.petviprandom(selectedItemId);
                        Item caitrang = Util.gapcaitrang(selectedItemId1);
                        Item pet = Util.petviprandom(selectedItemId2);
                        Item vanbay = Util.petviprandom(selectedItemId3);
                        Item vpcui = ItemService.gI().createNewItem((short) listvatphamcui[randomvpcui]);
                        Item vpvip = ItemService.gI().createNewItem((short) listvatphamvip[randomvpvip]);
                        Item xu = InventoryServiceNew.gI().findItemBag(player, 1529);
                        if (player.iDMark.getIndexMenu() == 1234) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, 12345, "|6|Gắp Quà"
                                            + "\n|7|Có thể gắp ra những vật phẩm hiếm",
                                            "Gắp x1", "Gắp x10", "Gắp x100", "Rương Đồ");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.RUONG_PHU,
                                            "|1|Tình yêu như một dây đàn\n"
                                            + "Tình vừa được thì đàn đứt dây\n"
                                            + "Đứt dây này anh thay dây khác\n"
                                            + "Mất em rồi anh biết thay ai?",
                                            "Rương Phụ\n(" + (player.inventory.itemsBoxCrackBall.size()
                                            - InventoryServiceNew.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall))
                                            + " món)",
                                            "Xóa Hết\nRương Phụ", "Đóng");
                                    break;
                                case 2:
                                    this.createOtherMenu(player, 12349, "|7|Hiện tại bạn có: " + player.gtPoint + " điểm gáp thú"
                                            + "\n|7|Tỉ lệ quy đổi 10 điểm = 1 xu  ",
                                            "Quy Đổi\n10 điểm", "Quy Đổi\n50 điểm", "Quy Đổi\n100 điểm", "Quy Đổi\n200 điểm", "Quy Đổi\n500 điểm");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 12349) {
                            switch (select) {
                                case 0:
                                    if (player.gtPoint >= soxu * 10) {
                                        player.gtPoint -= soxu * 10;
                                        Item nhanxu = ItemService.gI().createNewItem((short) 1529, soxu * 1);
                                        InventoryServiceNew.gI().addItemBag(player, nhanxu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    }
                                    break;
                                case 1:
                                    if (player.gtPoint >= soxu * 50) {
                                        player.gtPoint -= soxu * 50;
                                        Item nhanxu = ItemService.gI().createNewItem((short) 1529, soxu * 5);
                                        InventoryServiceNew.gI().addItemBag(player, nhanxu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    }
                                    break;
                                case 2:
                                    if (player.gtPoint >= soxu * 100) {
                                        player.gtPoint -= soxu * 100;
                                        Item nhanxu = ItemService.gI().createNewItem((short) 1529, soxu * 10);
                                        InventoryServiceNew.gI().addItemBag(player, nhanxu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    }
                                    break;
                                case 3:
                                    if (player.gtPoint >= soxu * 200) {
                                        player.gtPoint -= soxu * 200;
                                        Item nhanxu = ItemService.gI().createNewItem((short) 1529, soxu * 20);
                                        InventoryServiceNew.gI().addItemBag(player, nhanxu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    }
                                    break;
                                case 4:
                                    if (player.gtPoint >= soxu * 500) {
                                        player.gtPoint -= soxu * 500;
                                        Item nhanxu = ItemService.gI().createNewItem((short) 1529, soxu * 50);
                                        InventoryServiceNew.gI().addItemBag(player, nhanxu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    }
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 12345) {
                            switch (select) {
                                case 0: //x1
                                    if (xu != null && xu.quantity >= soxu) {
                                        player.gtPoint += 1;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, xu, soxu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        if (Util.isTrue(20, 100)) {
                                            InventoryServiceNew.gI().addItemBag(player, vpcui);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            this.createOtherMenu(player, 12345, "|2|Bạn vừa gắp được : " + vpcui.template.name + "\nSố Xu Đã Bị Trừ : 1" + "\n|7|Chiến tiếp ngay!",
                                                    "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                        } else if (Util.isTrue(10, 100)) {
                                            InventoryServiceNew.gI().addItemBag(player, vpvip);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            this.createOtherMenu(player, 12345, "|2|Bạn vừa gắp được : " + vpcui.template.name + "\nSố Xu Đã Bị Trừ : 1" + "\n|7|Chiến tiếp ngay!",
                                                    "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                        } else if (Util.isTrue(1, 100)) {
                                            InventoryServiceNew.gI().addItemBag(player, pet);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            this.createOtherMenu(player, 12345, "|2|Bạn vừa gắp được : " + pet.template.name + "\nSố Xu Đã Bị Trừ : 1" + "\n|7|Chiến tiếp ngay!",
                                                    "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                        } else if (Util.isTrue(1, 100)) {
                                            InventoryServiceNew.gI().addItemBag(player, vpdl);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            this.createOtherMenu(player, 12345, "|2|Bạn vừa gắp được : " + vpdl.template.name + "\nSố Xu Đã Bị Trừ : 1" + "\n|7|Chiến tiếp ngay!",
                                                    "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                        } else if (Util.isTrue(1, 100)) {
                                            InventoryServiceNew.gI().addItemBag(player, caitrang);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            this.createOtherMenu(player, 12345, "|2|Bạn vừa gắp được : " + caitrang.template.name + "\nSố Xu Đã Bị Trừ : 1" + "\n|7|Chiến tiếp ngay!",
                                                    "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                        } else if (Util.isTrue(1, 100)) {
                                            InventoryServiceNew.gI().addItemBag(player, vanbay);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            this.createOtherMenu(player, 12345, "|2|Bạn vừa gắp được : " + vanbay.template.name + "\nSố Xu Đã Bị Trừ : 1" + "\n|7|Chiến tiếp ngay!",
                                                    "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                        } else {
                                            this.createOtherMenu(player, 12345, "|6|Gắp hụt rồi, bạn bỏ cuộc sao?" + "\nSố Xu Đã Bị Trừ : 1" + "\n|7|Chiến tiếp ngay!",
                                                    "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                        }
                                    } else {
                                        this.npcChat(player, "Bạn đủ không xu ");
                                    }
                                    break;
                                case 1: //x10
                                    if (xu != null && xu.quantity >= soxu * 10) {
                                        try {
                                            Service.gI().sendThongBao(player, "Tiến hành auto gắp x10 lần");
                                            int timex10 = 10;
                                            int hn = 0;
                                            while (timex10 > 0) {
                                                timex10--;
                                                hn += 1;
                                                Thread.sleep(100);
                                                if (1 + player.inventory.itemsBoxCrackBall.size() > 100) {
                                                    this.createOtherMenu(player, 12345, "|7|DỪNG AUTO GẮP, RƯƠNG PHỤ ĐÃ ĐẦY!\n" + "|2|TỔNG LƯỢT GẮP : " + hn / 1 + " LƯỢT" + "\n|7|VUI LÒNG LÀM TRỐNG RƯƠNG PHỤ!",
                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    break;
                                                }
                                                player.gtPoint += 1;;
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, xu, soxu * 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                                    if (Util.isTrue(30, 100)) {
                                                        InventoryServiceNew.gI().addItemBag(player, vpcui);
                                                        InventoryServiceNew.gI().sendItemBags(player);
                                                        this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X10\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Đã gắp được : " + vpcui.template.name + "\nSố xu đã bị trừ: " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(10, 100)) {
                                                        InventoryServiceNew.gI().addItemBag(player, vpvip);
                                                        InventoryServiceNew.gI().sendItemBags(player);
                                                        this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X10\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Đã gắp được : " + vpvip.template.name + "\nSố xu đã bị trừ: " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(1, 100)) {
                                                        InventoryServiceNew.gI().addItemBag(player, caitrang);
                                                        InventoryServiceNew.gI().sendItemBags(player);
                                                        this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X10\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Đã gắp được : " + caitrang.template.name + "\nSố xu đã bị trừ: " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(1, 100)) {
                                                        InventoryServiceNew.gI().addItemBag(player, vpdl);
                                                        InventoryServiceNew.gI().sendItemBags(player);
                                                        this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X10\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Đã gắp được : " + vpdl.template.name + "\nSố xu đã bị trừ: " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(1, 100)) {
                                                        InventoryServiceNew.gI().addItemBag(player, vanbay);
                                                        InventoryServiceNew.gI().sendItemBags(player);
                                                        this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X10\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Đã gắp được : " + vanbay.template.name + "\nSố xu đã bị trừ: " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else {
                                                        this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X10\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu đã trừ   : " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    }
                                                }
                                                if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                                    if (Util.isTrue(30, 100)) {
                                                        player.inventory.itemsBoxCrackBall.add(vpcui);
                                                        this.createOtherMenu(player, 12345, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Đã gắp được : " + vpcui.template.name + "\nSố xu đã trừ : " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint,
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(10, 100)) {
                                                        player.inventory.itemsBoxCrackBall.add(vpvip);
                                                        this.createOtherMenu(player, 12345, "|2|Bạn vừa gắp được : " + vpvip.template.name + "\nSố xu đã trừ : 1" + "\n|7|Chiến tiếp ngay!",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(1, 100)) {
                                                        player.inventory.itemsBoxCrackBall.add(caitrang);
                                                        this.createOtherMenu(player, 12345, "|2|Bạn vừa gắp được : " + caitrang.template.name + "\nSố xu đã trừ : 1" + "\n|7|Chiến tiếp ngay!",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(1, 100)) {
                                                        player.inventory.itemsBoxCrackBall.add(vpdl);
                                                        this.createOtherMenu(player, 12345, "|2|Bạn vừa gắp được : " + vpdl.template.name + "\nSố xu đã  : 1000" + "\n|7|Chiến tiếp ngay!",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(1, 100)) {
                                                        player.inventory.itemsBoxCrackBall.add(vanbay);
                                                        this.createOtherMenu(player, 12345, "|2|Bạn vừa gắp được : " + vanbay.template.name + "\nSố xu đã trừ : 1" + "\n|7|Chiến tiếp ngay!",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else {
                                                        this.createOtherMenu(player, 12345, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X10 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex10 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu đã  ngọc đã trừ : " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint,
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                        }
                                    } else {
                                        this.npcChat(player, "Bạn đủ không xu ");
                                    }
                                    break;
                                case 2: //x100
                                    if (xu != null && xu.quantity >= soxu * 100) {
                                        try {
                                            Service.gI().sendThongBao(player, "Tiến hành auto gắp x100 lần");
                                            int timex100 = 100;
                                            int hn = 0;
                                            while (timex100 > 0) {
                                                timex100--;
                                                hn += 1;
                                                Thread.sleep(100);
                                                if (1 + player.inventory.itemsBoxCrackBall.size() > 100) {
                                                    this.createOtherMenu(player, 12345, "|7|DỪNG AUTO GẮP, RƯƠNG PHỤ ĐÃ ĐẦY!\n" + "|2|TỔNG LƯỢT GẮP : " + hn / 1 + " LƯỢT" + "\n|7|VUI LÒNG LÀM TRỐNG RƯƠNG PHỤ!",
                                                            "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    break;
                                                }

                                                InventoryServiceNew.gI().subQuantityItemsBag(player, xu, soxu * 1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                player.gtPoint += 1;
                                                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                                    if (Util.isTrue(30, 100)) {
                                                        InventoryServiceNew.gI().addItemBag(player, vpcui);
                                                        InventoryServiceNew.gI().sendItemBags(player);
                                                        this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X100\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + vpcui.template.name + "\nSố xu đã trừ : " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(10, 100)) {
                                                        InventoryServiceNew.gI().addItemBag(player, vpvip);
                                                        InventoryServiceNew.gI().sendItemBags(player);
                                                        this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X100\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + vpvip.template.name + "\nSố xu đã trừ : " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(1, 100)) {
                                                        InventoryServiceNew.gI().addItemBag(player, caitrang);
                                                        InventoryServiceNew.gI().sendItemBags(player);
                                                        this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X100\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + caitrang.template.name + "\nSố xu đã trừ : " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(1, 100)) {
                                                        InventoryServiceNew.gI().addItemBag(player, vpdl);
                                                        InventoryServiceNew.gI().sendItemBags(player);
                                                        this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X100\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + vpdl.template.name + "\nSố xu đã trừ : " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(1, 100)) {
                                                        InventoryServiceNew.gI().addItemBag(player, pet);
                                                        InventoryServiceNew.gI().sendItemBags(player);
                                                        this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X100\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + pet.template.name + "\nSố xu đã trừ : " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else {
                                                        this.createOtherMenu(player, 12345, "|7|ĐANG TIẾN HÀNH GẮP AUTO X100\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố xu đã trừ : " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint + "\nNẾU HÀNH TRANG ĐẦY, ITEM SẼ ĐƯỢC THÊM VÀO RƯƠNG PHỤ",
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    }
                                                }
                                                if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                                    if (Util.isTrue(30, 100)) {
                                                        player.inventory.itemsBoxCrackBall.add(vpcui);
                                                        this.createOtherMenu(player, 12345, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X100 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + vpcui.template.name + "\nSố xu đã trừ : " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint,
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(10, 100)) {
                                                        player.inventory.itemsBoxCrackBall.add(vpvip);
                                                        this.createOtherMenu(player, 12345, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X100 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + vpvip.template.name + "\nSố xu đã trừ  : " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint,
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(1, 100)) {
                                                        player.inventory.itemsBoxCrackBall.add(caitrang);
                                                        this.createOtherMenu(player, 12345, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X100 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + caitrang.template.name + "\nSố  xu đã trừ   : " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint,
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(1, 100)) {
                                                        player.inventory.itemsBoxCrackBall.add(vpdl);
                                                        this.createOtherMenu(player, 12345, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X100 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + vpdl.template.name + "\nSố  xu đã trừ  : " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint,
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else if (Util.isTrue(1, 100)) {
                                                        player.inventory.itemsBoxCrackBall.add(pet);
                                                        this.createOtherMenu(player, 12345, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X100 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Đã gắp được : " + pet.template.name + "\nSố  xu đã trừ   : " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint,
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    } else {
                                                        this.createOtherMenu(player, 12345, "|7|HÀNH TRANG ĐÃ ĐẦY\nĐANG TIẾN HÀNH GẮP AUTO X100 VÀO RƯƠNG PHỤ\nSỐ LƯỢT CÒN : " + timex100 + " LƯỢT\n" + "|2|Gắp hụt rồi!" + "\nSố  xu đã trừ  : " + hn + "\n|7|TỔNG ĐIỂM : " + player.gtPoint,
                                                                "Gắp X1", "Gắp X10", "Gắp X100", "Rương Đồ");
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                        }
                                    } else {
                                        this.npcChat(player, "Bạn đủ không xu ");
                                    }
                                    break;
                                case 3:
                                    this.createOtherMenu(player, ConstNpc.RUONG_PHU,
                                            "|1|Tình yêu như một dây đàn\n"
                                            + "Tình vừa được thì đàn đứt dây\n"
                                            + "Đứt dây này anh thay dây khác\n"
                                            + "Mất em rồi anh biết thay ai?",
                                            "Rương Phụ\n(" + (player.inventory.itemsBoxCrackBall.size()
                                            - InventoryServiceNew.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall))
                                            + " món)",
                                            "Xóa Hết\nRương Phụ", "Đóng");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.RUONG_PHU) {
                            switch (select) {
                                case 0:
                                    ShopServiceNew.gI().opendShop(player, "ITEMS_LUCKY_ROUND", true);
                                    break;
                                case 1:
                                    NpcService.gI().createMenuConMeo(player,
                                            ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND, this.avartar,
                                            "|3|Bạn chắc muốn xóa hết vật phẩm trong rương phụ?\n"
                                            + "|7|Sau khi xóa sẽ không thể khôi phục!",
                                            "Đồng ý", "Hủy bỏ");
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    private static Npc Skien_trungthu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "|7| SỰ KIỆN TRUNG THU"
                            + "\n\n|2|Nguyên liệu cần nấu bánh Trung thu"
                            + "\n|-1|- Bánh Hạt sen : 99 Hạt sen + 50 Bột nếp + 2 Mồi lửa"
                            + "\n|-1|- Bánh Đậu xanh : 99 Đậu xanh + 50 Bột nếp + 2 Mồi lửa"
                            + "\n|-1|- Bánh Thập cẩm : 99 Hạt sen + 99 Đậu xanh + 99 Bột nếp + 5 Mồi lửa"
                            + "\n|7|Làm bánh sẽ tốn phí 2Ty Vàng/lần"
                            + "\n\n|1|Điểm sự kiện : " + Util.format(player.NguHanhSonPoint) + " Điểm",
                            "Thể lệ", "Làm bánh", "Đổi điểm\nTrung thu");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: {
                                this.createOtherMenu(player, 1234,
                                        "|7|SỰ KIỆN TRUNG THU NRO"
                                        + "\n\n|2|Cách thức tìm nguyên liệu nấu bánh Trung thu"
                                        + "\n|4|- Hạt sen : Đánh các quái bay trên không"
                                        + "\n- Đậu xanh : Đánh các quái dưới đất"
                                        + "\n- Bột nếp : Đánh quái Sên ở Tương lai"
                                        + "\n- Mồi lửa : Giết Boss Thỏ trắng (5 phút xuất hiện 1 lần)"
                                        + "\n\n|5|Ăn bánh trung thu nhận được Điểm tích lũy Sự kiện Trung thu đổi được các phần quà hấp dẫn"
                                        + "\n|-1|- Bánh Hạt sen : Nhận 2 Điểm Sự kiện"
                                        + "\n|-1|- Bánh Đậu xanh : Nhận 2 Điểm Sự kiện"
                                        + "\n|-1|- Bánh Thập cẩm : Nhận 5 Điểm Sự kiện"
                                        + "\n|5|- Quy đổi tiền 1.000Đ nhận thêm 1 Điểm Sự kiện (Không tính đổi Xu)"
                                        + "\n\n|7|Chị hiểu hôn ???",
                                        "Đã hiểu");
                                break;
                            }
                            case 1: {
                                this.createOtherMenu(player, 1111,
                                        "|7|SỰ KIỆN TRUNG THU Nro"
                                        + "\n\n|2|Bạn muốn làm bánh gì?",
                                        "Bánh\nHạt sen", "Bánh\nĐậu xanh", "Bánh\nThập cẩm");
                                break;
                            }
                            case 2: {
                                this.createOtherMenu(player, 2222,
                                        "|7|TÍCH ĐIỂM SỰ KIỆN TRUNG THU"
                                        + "\n\n|1|Khi đổi điểm thì sẽ được cộng điểm đua top trung thu\n"
                                        + "|2|Mốc 500 Điểm\n"
                                        + "|4|200 Mảnh thiên sứ ngẫu nhiên, 50 rương thần linh, 30 Hộp quà Trung thu, 30 Thẻ gia hạn, 1 Phiếu giảm giá + 250k HN \n"
                                        + "|2|Mốc 300 Điểm\n"
                                        + "|4|100 Mảnh thiên sứ ngẫu nhiên, 40 rương thần linh, 10 Thẻ gia hạn, 15 Hộp Trung thu + 150k HN \n"
                                        + "|2|Mốc 200 Điểm\n"
                                        + "|4|50 Mảnh thiên sứ ngẫu nhiên, 30 rương thần linh, 5 Thẻ gia hạn, 10 Hộp Trung thu + 100k HN \n"
                                        + "|2|Mốc 50 Điểm\n"
                                        + "|4|10 Mảnh thiên sứ ngẫu nhiên, 5 rương thần linh + 25k HN"
                                        + "\n\n|7|Điểm sự kiện : " + Util.format(player.NguHanhSonPoint) + " Điểm"
                                        + "\n|1|Điểm Top Trung thu : " + Util.format(player.inventory.coupon) + " Điểm",
                                        "500 Điểm", "300 Điểm", "200 Điểm", "50 Điểm");
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 1111) {
                        switch (select) {
                            case 0: {
                                Item hatsen = null;
                                Item botnep = null;
                                Item moilua = null;
                                try {
                                    hatsen = InventoryServiceNew.gI().findItemBag(player, 1340);
                                    botnep = InventoryServiceNew.gI().findItemBag(player, 1338);
                                    moilua = InventoryServiceNew.gI().findItemBag(player, 1341);
                                } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                }
                                if (hatsen == null || hatsen.quantity < 99) {
                                    this.npcChat(player, "|7|Bạn không đủ 99 Hạt sen");
                                } else if (botnep == null || botnep.quantity < 50) {
                                    this.npcChat(player, "|7|Bạn không đủ 50 Bột nếp");
                                } else if (moilua == null || moilua.quantity < 2) {
                                    this.npcChat(player, "|7|Bạn không đủ 2 Mồi lửa");
                                } else if (player.inventory.gold < 2_000_000_000) {
                                    this.npcChat(player, "|7|Bạn không đủ 2Ty Vàng");
                                } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                    this.npcChat(player, "|7|Hành trang của bạn không đủ chỗ trống");
                                } else {
                                    player.inventory.gold -= 2_000_000_000;
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, hatsen, 99);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, botnep, 50);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, moilua, 2);
                                    Service.getInstance().sendMoney(player);
                                    Item banhtrungthu = ItemService.gI().createNewItem((short) 1336);
                                    InventoryServiceNew.gI().addItemBag(player, banhtrungthu);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    this.npcChat(player, "|4|Bạn nhận được Bánh trung thu Hạt sen");
                                }
                                break;
                            }
                            case 1: {
                                Item dauxanh = null;
                                Item botnep = null;
                                Item moilua = null;
                                try {
                                    dauxanh = InventoryServiceNew.gI().findItemBag(player, 1339);
                                    botnep = InventoryServiceNew.gI().findItemBag(player, 1338);
                                    moilua = InventoryServiceNew.gI().findItemBag(player, 1341);
                                } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                }
                                if (dauxanh == null || dauxanh.quantity < 99) {
                                    this.npcChat(player, "|7|Bạn không đủ 99 Đậu xanh");
                                } else if (botnep == null || botnep.quantity < 50) {
                                    this.npcChat(player, "|7|Bạn không đủ 50 Bột nếp");
                                } else if (moilua == null || moilua.quantity < 2) {
                                    this.npcChat(player, "|7|Bạn không đủ 2 Mồi lửa");
                                } else if (player.inventory.gold < 2_000_000_000) {
                                    this.npcChat(player, "|7|Bạn không đủ 2Ty Vàng");
                                } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                    this.npcChat(player, "|7|Hành trang của bạn không đủ chỗ trống");
                                } else {
                                    player.inventory.gold -= 2_000_000_000;
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, dauxanh, 99);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, botnep, 50);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, moilua, 2);
                                    Service.getInstance().sendMoney(player);
                                    Item banhtrungthu = ItemService.gI().createNewItem((short) 1335);
                                    InventoryServiceNew.gI().addItemBag(player, banhtrungthu);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    this.npcChat(player, "|4|Bạn nhận được Bánh trung thu Đậu xanh");
                                }
                                break;
                            }
                            case 2: {
                                Item hatsen = null;
                                Item dauxanh = null;
                                Item botnep = null;
                                Item moilua = null;
                                try {
                                    hatsen = InventoryServiceNew.gI().findItemBag(player, 1340);
                                    dauxanh = InventoryServiceNew.gI().findItemBag(player, 1339);
                                    botnep = InventoryServiceNew.gI().findItemBag(player, 1338);
                                    moilua = InventoryServiceNew.gI().findItemBag(player, 1341);
                                } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                }
                                if (hatsen == null || hatsen.quantity < 99) {
                                    this.npcChat(player, "|7|Bạn không đủ 99 Hạt sen");
                                } else if (botnep == null || botnep.quantity < 99) {
                                    this.npcChat(player, "|7|Bạn không đủ 99 Bột nếp");
                                } else if (dauxanh == null || dauxanh.quantity < 99) {
                                    this.npcChat(player, "|7|Bạn không đủ 99 Đậu xanh");
                                } else if (moilua == null || moilua.quantity < 5) {
                                    this.npcChat(player, "|7|Bạn không đủ 5 Mồi lửa");
                                } else if (player.inventory.gold < 2_000_000_000) {
                                    this.npcChat(player, "|7|Bạn không đủ 2Ty Vàng");
                                } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                    this.npcChat(player, "|7|Hành trang của bạn không đủ chỗ trống");
                                } else {
                                    player.inventory.gold -= 2_000_000_000;
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, hatsen, 99);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, dauxanh, 99);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, botnep, 99);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, moilua, 5);
                                    Service.getInstance().sendMoney(player);
                                    Item banhtrungthu = ItemService.gI().createNewItem((short) 1337);
                                    InventoryServiceNew.gI().addItemBag(player, banhtrungthu);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    this.npcChat(player, "|4|Bạn nhận được Bánh trung thu Thập cẩm");
                                }
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 2222) {
                        switch (select) {
                            case 0: {
                                byte randommanh = (byte) new Random().nextInt(Manager.manhthiensu.length);
                                int manh = Manager.manhthiensu[randommanh];
                                if (player.NguHanhSonPoint < 500) {
                                    this.npcChat(player, "|7|Bạn không đủ 500 Điểm sự kiện");
                                } else if (InventoryServiceNew.gI().getCountEmptyBag(player) < 5) {
                                    this.npcChat(player, "|7|Hành trang của bạn cần ít nhất 5 ô trống");
                                } else {
                                    player.NguHanhSonPoint -= 500;
                                    player.inventory.coupon += 500;
                                    player.inventory.ruby += 2500;
                                    Service.getInstance().sendMoney(player);
                                    Item manhthiensu = ItemService.gI().createNewItem((short) manh);
                                    Item ruongthan = ItemService.gI().createNewItem((short) 1334);
                                    Item hoptt = ItemService.gI().createNewItem((short) 1342);
                                    Item phieugg = ItemService.gI().createNewItem((short) 459);
                                    Item thegh = ItemService.gI().createNewItem((short) 1346);
                                    phieugg.itemOptions.add(new Item.ItemOption(30, 1));
                                    manhthiensu.quantity = 200;
                                    ruongthan.quantity = 50;
                                    hoptt.quantity = 30;
                                    thegh.quantity = 30;
                                    InventoryServiceNew.gI().addItemBag(player, manhthiensu);
                                    InventoryServiceNew.gI().addItemBag(player, ruongthan);
                                    InventoryServiceNew.gI().addItemBag(player, hoptt);
                                    InventoryServiceNew.gI().addItemBag(player, phieugg);
                                    InventoryServiceNew.gI().addItemBag(player, thegh);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    this.npcChat(player, "|4|Bạn nhận được 200 " + manhthiensu.template.name
                                            + ", 50 Rương thần linh, 30 Hộp Trung thu, 30 Thẻ gia hạn, 1 Phiếu giảm giá và 250k Hồng ngọc");
                                }
                                break;
                            }
                            case 1: {
                                byte randommanh = (byte) new Random().nextInt(Manager.manhthiensu.length);
                                int manh = Manager.manhthiensu[randommanh];
                                if (player.NguHanhSonPoint < 300) {
                                    this.npcChat(player, "|7|Bạn không đủ 300 Điểm sự kiện");
                                } else if (InventoryServiceNew.gI().getCountEmptyBag(player) < 4) {
                                    this.npcChat(player, "|7|Hành trang của bạn cần ít nhất 4 ô trống");
                                } else {
                                    player.NguHanhSonPoint -= 300;
                                    player.inventory.coupon += 300;
                                    player.inventory.ruby += 100;
                                    Service.getInstance().sendMoney(player);
                                    Item manhthiensu = ItemService.gI().createNewItem((short) manh);
                                    Item ruongthan = ItemService.gI().createNewItem((short) 1334);
                                    Item hoptt = ItemService.gI().createNewItem((short) 1342);
                                    Item thegh = ItemService.gI().createNewItem((short) 1346);
                                    manhthiensu.quantity = 100;
                                    ruongthan.quantity = 40;
                                    hoptt.quantity = 15;
                                    thegh.quantity = 10;
                                    InventoryServiceNew.gI().addItemBag(player, manhthiensu);
                                    InventoryServiceNew.gI().addItemBag(player, ruongthan);
                                    InventoryServiceNew.gI().addItemBag(player, hoptt);
                                    InventoryServiceNew.gI().addItemBag(player, thegh);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    this.npcChat(player, "|4|Bạn nhận được 100 " + manhthiensu.template.name
                                            + ", 40 Rương thần linh, 15 Hộp Trung thu, 10 Thẻ gia hạn và 150k Hồng ngọc");
                                }
                                break;
                            }
                            case 2: {
                                byte randommanh = (byte) new Random().nextInt(Manager.manhthiensu.length);
                                int manh = Manager.manhthiensu[randommanh];
                                if (player.NguHanhSonPoint < 200) {
                                    this.npcChat(player, "|7|Bạn không đủ 200 Điểm sự kiện");
                                } else if (InventoryServiceNew.gI().getCountEmptyBag(player) < 4) {
                                    this.npcChat(player, "|7|Hành trang của bạn cần ít nhất 4 ô trống");
                                } else {
                                    player.NguHanhSonPoint -= 200;
                                    player.inventory.coupon += 200;
                                    player.inventory.ruby += 1000;
                                    Service.getInstance().sendMoney(player);
                                    Item manhthiensu = ItemService.gI().createNewItem((short) manh);
                                    Item ruongthan = ItemService.gI().createNewItem((short) 1334);
                                    Item hoptt = ItemService.gI().createNewItem((short) 1342);
                                    Item thegh = ItemService.gI().createNewItem((short) 1346);
                                    manhthiensu.quantity = 50;
                                    ruongthan.quantity = 30;
                                    hoptt.quantity = 10;
                                    thegh.quantity = 5;
                                    InventoryServiceNew.gI().addItemBag(player, manhthiensu);
                                    InventoryServiceNew.gI().addItemBag(player, ruongthan);
                                    InventoryServiceNew.gI().addItemBag(player, hoptt);
                                    InventoryServiceNew.gI().addItemBag(player, thegh);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    this.npcChat(player, "|4|Bạn nhận được 50 " + manhthiensu.template.name
                                            + ", 30 Rương thần linh, 10 Hộp Trung thu, 5 Thẻ gia hạn và 100k Hồng ngọc");
                                }
                                break;
                            }
                            case 3: {
                                byte randommanh = (byte) new Random().nextInt(Manager.manhthiensu.length);
                                int manh = Manager.manhthiensu[randommanh];
                                if (player.NguHanhSonPoint < 50) {
                                    this.npcChat(player, "|7|Bạn không đủ 50 Điểm sự kiện");
                                } else if (InventoryServiceNew.gI().getCountEmptyBag(player) < 2) {
                                    this.npcChat(player, "|7|Hành trang của bạn cần ít nhất 2 ô trống");
                                } else {
                                    player.NguHanhSonPoint -= 50;
                                    player.inventory.coupon += 50;
                                    player.inventory.ruby += 20;
                                    Service.getInstance().sendMoney(player);
                                    Item manhthiensu = ItemService.gI().createNewItem((short) manh);
                                    Item ruongthan = ItemService.gI().createNewItem((short) 1334);
                                    manhthiensu.quantity = 10;
                                    ruongthan.quantity = 5;
                                    InventoryServiceNew.gI().addItemBag(player, manhthiensu);
                                    InventoryServiceNew.gI().addItemBag(player, ruongthan);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    this.npcChat(player, "|4|Bạn nhận được 10 " + manhthiensu.template.name
                                            + ", 5 Rương thần linh và 25k Hồng ngọc");
                                }
                                break;
                            }
                        }
                    }
                }
            }
        };
    }

    private static Npc npcuub(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 104) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "|7| NHẬN ĐÊJ TỬ"
                                + "\n\n|2|Hộ tộng bulma x500 lần để đổi đệ ngô không",
                                "Đổi ĐỆ", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 104) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    this.npcChat(player, "|7| BẠN CHƯA DẮT ĐỦ X500 LẦN");
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    private static Npc fide(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 104 || this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đổi Trứng con cua cần:\b|7|X1 Lọ nước phép + 1 Tỷ vàng", "Đổi con\ncua", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 104 || this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    Item honLinhThu = null;
                                    try {
                                        honLinhThu = InventoryServiceNew.gI().findItemBag(player, 1029);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (honLinhThu == null || honLinhThu.quantity < 1) {
                                        this.npcChat(player, "Bạn không đủ 1 Lọ nước phép");
                                    } else if (player.inventory.gold < 1_000_000_000) {
                                        this.npcChat(player, "Bạn không đủ 1 Tỷ vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        player.inventory.gold -= 1_000_000_000;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu, 1);
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 697);
                                        trungLinhThu.itemOptions.add(new Item.ItemOption(30, 1));
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 con cua");
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc GhiDanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            String[] menuselect = new String[]{};

            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (this.mapId == 52) {
                        createOtherMenu(pl, 0, DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).Giai(pl),
                        "Thông tin\nChi tiết",
                        DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).CanReg(pl) ? "Đăng ký"
                                : "OK",
                        "Đại Hội\nVõ Thuật\nLần thứ\n23", "Giải siêu hạng\n");
                    } else if (this.mapId == 129) {
                        if (pl.levelWoodChest == 0) {
                            menuselect = new String[]{"Thi đấu\n" + 200 + " Hồng ngọc", "Về\nĐại Hội\nVõ Thuật"};
                        } else {
                            menuselect = new String[]{"Thi đấu\n" + 200 + " Hồng ngọc", "Nhận thưởng\nRương cấp\n" + pl.levelWoodChest, "Về\nĐại Hội\nVõ Thuật"};
                        }
                        this.createOtherMenu(pl, ConstNpc.BASE_MENU, "Đại hội võ thuật lần thứ 23\nDiễn ra bất kể ngày đêm,ngày nghỉ ngày lễ\nPhần thưởng vô cùng quý giá\nNhanh chóng tham gia nào", menuselect, "Từ chối");

                    } else {
                        super.openBaseMenu(pl);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 52) {
                        switch (select) {
                            case 0:
                                Service.gI().sendPopUpMultiLine(player, tempId, avartar, DaiHoiVoThuat.gI().Info());
                                break;
                            case 1:
                                if (DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).CanReg(player)) {
                                    DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).Reg(player);
                                }
                                break;
                            case 2:
                                ChangeMapService.gI().changeMapNonSpaceship(player, 129, player.location.x, 360);
                                break;
                            case 3:
                                ChangeMapService.gI().changeMapNonSpaceship(player, 113, player.location.x, 360);
                                break;
                        }
                    } else if (this.mapId == 129) {
                        // int goldchallenge = player.goldChallenge;
                        if (player.levelWoodChest == 0) {
                            switch (select) {
                                case 0:
                                    if (InventoryServiceNew.gI().finditemWoodChest(player)) {
                                        if (player.inventory.ruby >= 200) {
                                            MartialCongressService.gI().startChallenge(player);
                                            player.inventory.ruby -= 200;
                                            PlayerService.gI().sendInfoHpMpMoney(player);
                                            player.goldChallenge += 2000000;
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Không đủ vàng, còn thiếu "
                                                    + Util.numberToMoney(200 - player.inventory.ruby) + " Hồng ngọc");
                                        }
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Hãy mở rương báu vật trước");
                                    }
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapNonSpaceship(player, 52, player.location.x, 336);
                                    break;
                            }
                        } else {
                            switch (select) {
                                case 0:
                                    if (InventoryServiceNew.gI().finditemWoodChest(player)) {
                                        if (player.inventory.ruby >= 200) {
                                            MartialCongressService.gI().startChallenge(player);
                                            player.inventory.ruby -= (200);
                                            PlayerService.gI().sendInfoHpMpMoney(player);
                                            player.goldChallenge += 200;
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Không đủ vàng, còn thiếu "
                                                    + Util.numberToMoney(200 - player.inventory.ruby) + " vàng");
                                        }
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Hãy mở rương báu vật trước");
                                    }
                                    break;
                                case 1:
                                    if (!player.receivedWoodChest) {
                                        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                            Item it = ItemService.gI().createNewItem((short) 570);
                                            it.itemOptions.add(new Item.ItemOption(72, player.levelWoodChest));
                                            it.itemOptions.add(new Item.ItemOption(30, 0));
                                            it.createTime = System.currentTimeMillis();
                                            InventoryServiceNew.gI().addItemBag(player, it);
                                            InventoryServiceNew.gI().sendItemBags(player);

                                            player.receivedWoodChest = true;
                                            player.levelWoodChest = 0;
                                            Service.getInstance().sendThongBao(player, "Bạn nhận được rương gỗ");
                                        } else {
                                            this.npcChat(player, "Hành trang đã đầy");
                                        }
                                    } else {
                                        Service.getInstance().sendThongBao(player,
                                                "Mỗi ngày chỉ có thể nhận rương báu 1 lần");
                                    }
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapNonSpaceship(player, 52, player.location.x, 336);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    private static Npc monaito(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 104 || this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đổi Lấy Đá Cầu Vòng:\b|7|X99 đá ngũ sắc + 1 Tỷ vàng", "Đổi Cầu\nVòng", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 104 || this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    Item honLinhThu = null;
                                    try {
                                        honLinhThu = InventoryServiceNew.gI().findItemBag(player, 674);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (honLinhThu == null || honLinhThu.quantity < 99) {
                                        this.npcChat(player, "Bạn không đủ 99 đá ngũ sắc");
                                    } else if (player.inventory.gold < 1_000_000_000) {
                                        this.npcChat(player, "Bạn không đủ 1 Tỷ vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        player.inventory.gold -= 1_000_000_000;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu, 99);
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 1083);
                                        trungLinhThu.itemOptions.add(new Item.ItemOption(30, 1));
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 Đá Cầu Vòng");
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    private static Npc poTaGe(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 140) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đa vũ trụ song song \b|7|Con muốn gọi con trong đa vũ trụ \b|1|Với giá 200tr vàng không?", "Gọi Boss\nNhân bản", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 140) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {

//                                    Service.getInstance().sendThongBao(player, "Chức năng đang bảo trì");
                                    Boss oldBossClone = BossManager.gI().getBossById(Util.createIdBossClone((int) player.id));
                                    if (oldBossClone != null) {
                                        this.npcChat(player, "Nhà ngươi hãy tiêu diệt Boss lúc trước gọi ra đã, con boss đó đang ở khu " + oldBossClone.zone.zoneId);
                                    } else if (player.inventory.gold < 200_000_000) {
                                        this.npcChat(player, "Nhà ngươi không đủ 200 Triệu vàng ");
                                    } else {
                                        List<Skill> skillList = new ArrayList<>();
                                        for (byte i = 0; i < player.playerSkill.skills.size(); i++) {
                                            Skill skill = player.playerSkill.skills.get(i);
                                            if (skill.point > 0) {
                                                skillList.add(skill);
                                            }
                                        }
                                        int[][] skillTemp = new int[skillList.size()][3];
                                        for (byte i = 0; i < skillList.size(); i++) {
                                            Skill skill = skillList.get(i);
                                            if (skill.point > 0) {
                                                skillTemp[i][0] = skill.template.id;
                                                skillTemp[i][1] = skill.point;
                                                skillTemp[i][2] = skill.coolDown;
                                            }
                                        }
                                        BossData bossDataClone = new BossData(
                                                "Nhân Bản" + player.name,
                                                player.gender,
                                                new short[]{player.getHead(), player.getBody(), player.getLeg(), player.getFlagBag(), player.getAura(), player.getEffFront()},
                                                player.nPoint.dame,
                                                new double[]{player.nPoint.hpMax},
                                                new int[]{140},
                                                skillTemp,
                                                new String[]{"|-2|Boss nhân bản đã xuất hiện rồi"}, //text chat 1
                                                new String[]{"|-1|Ta sẽ chiếm lấy thân xác của ngươi hahaha!"}, //text chat 2
                                                new String[]{"|-1|Lần khác ta sẽ xử đẹp ngươi"}, //text chat 3
                                                60
                                        );

                                        try {
                                            new NhanBan(Util.createIdBossClone((int) player.id), bossDataClone, player.zone, player);
                                        } catch (Exception e) {
                                            System.out.println("ccccc");
                                        }
                                        //trừ vàng khi gọi boss
                                        player.inventory.gold -= 200_000_000;
                                        Service.getInstance().sendMoney(player);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    private static Npc quyLaoKame(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (player.getSession().is_gift_box) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào con, con muốn ta giúp gì nào?", "Giải tán bang hội", "Lãnh địa\nbang hội", "Nhận quà\nđền bù");
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào con, con muốn ta giúp gì nào?", "Giải tán bang hội", "Lãnh địa\nbang hội");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
//                            case 0:
//                                if (Manager.KHUYEN_MAI_NAP != 1) {
//                                    this.createOtherMenu(player, ConstNpc.QUY_DOI_HN,
//                                            "|7|QUY ĐỔI HỒNG NGỌC\n|6|Quy đổi Hồng ngọc, giới hạn đổi không quá 1.000.000đ\n\n|1|Tiền hiện còn : " + " " + Util.format(player.getSession().vnd)
//                                            + "\n\n|5|Nhập 10.000Đ được 10.000 Hồng ngọc"
//                                            + "\n\n|3| Server đang x" + Manager.KHUYEN_MAI_NAP + " Quy đổi "
//                                            + "(10.000Đ = " + Util.format(Manager.KHUYEN_MAI_NAP * 10000) + " Hồng ngọc)"
//                                            + "\n\n|7|(>= 500.000đ Được tặng Vé chuyển Hồng ngọc)",
//                                            "Đồng ý", "Từ chối");
//                                } else if (Manager.SUKIEN == 1) {
//                                    this.createOtherMenu(player, ConstNpc.QUY_DOI_HN,
//                                            "|7|QUY ĐỔI HỒNG NGỌC\n|6|Quy đổi Hồng ngọc, giới hạn đổi không quá 1.000.000đ\n\n|1|Tiền hiện còn : " + " " + Util.format(player.getSession().vnd)
//                                            + "\n\n|5|Nhập 10.000Đ được 10.000 Hồng ngọc và được 10 Điểm Sự kiện" + "\n\n|7|(>= 500.000đ Được tặng Vé chuyển Hồng ngọc)",
//                                            "Đồng ý", "Từ chối");
//                                } else {
//                                    this.createOtherMenu(player, ConstNpc.QUY_DOI_HN,
//                                            "|7|QUY ĐỔI HỒNG NGỌC\n|6|Quy đổi Hồng ngọc, giới hạn đổi không quá 1.000.000đ\n\n|1|Tiền hiện còn : " + " " + Util.format(player.getSession().vnd)
//                                            + "\n\n|5|Nhập 10.000Đ được 10.000 Hồng ngọc" + "\n\n|7|(>= 500.000đ được x2 Hồng ngọc và Được tặng Vé chuyển Hồng ngọc)",
//                                            "Đồng ý", "Từ chối");
//                                }
//                                break;
//                            case 1:
//                                if (Manager.KHUYEN_MAI_NAP != 1) {
//                                    this.createOtherMenu(player, ConstNpc.QUY_DOI_TV,
//                                            "|7|QUY ĐỔI THỎI VÀNG\n|6|Quy đổi Thỏi vàng, giới hạn đổi không quá 1.000.000đ\n\n|1|Tiền hiện còn : " + " " + Util.format(player.getSession().vnd)
//                                            + "\n\n|5|Nhập 10.000Đ được 50 Thỏi vàng"
//                                            + "\n\n|3| Server đang x" + Manager.KHUYEN_MAI_NAP + " Quy đổi "
//                                            + "(10.000Đ = " + Util.format(Manager.KHUYEN_MAI_NAP * 50) + " Thỏi vàng)"
//                                            + "\n\n|7|(>= 500.000đ Được tặng Vé chuyển Hồng ngọc)",
//                                            "Đồng ý", "Từ chối");
//                                } else if (Manager.SUKIEN == 1) {
//                                    this.createOtherMenu(player, ConstNpc.QUY_DOI_TV,
//                                            "|7|QUY ĐỔI THỎI VÀNG\n|6|Quy đổi Thỏi vàng, giới hạn đổi không quá 1.000.000đ\n\n|1|Tiền hiện còn : " + " " + Util.format(player.getSession().vnd)
//                                            + "\n\n|5|Nhập 10.000Đ được 50 Thỏi vàng ",
//                                            "Đồng ý", "Từ chối");
//                                } else {
//                                    this.createOtherMenu(player, ConstNpc.QUY_DOI_TV,
//                                            "|7|QUY ĐỔI THỎI VÀNG\n|6|Quy đổi Thỏi vàng, giới hạn đổi không quá 1.000.000đ\n\n|1|Tiền hiện còn : " + " " + Util.format(player.getSession().vnd)
//                                            + "\n\n|5|Nhập 10.000Đ được 50 Thỏi vàng",
//                                            "Đồng ý", "Từ chối");
//                                }
//                                break;
                            case 0:
                                Clan clan = player.clan;
                                if (clan != null) {
                                    ClanMember cm = clan.getClanMember((int) player.id);
                                    if (cm != null) {
                                        if (clan.members.size() > 1) {
                                            Service.getInstance().sendThongBao(player, "Bang phải còn một người");
                                            break;
                                        }
                                        if (!clan.isLeader(player)) {
                                            Service.getInstance().sendThongBao(player, "Phải là bảng chủ");
                                            break;
                                        }
//                                        
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DISSOLUTION_CLAN, -1, "Con có chắc chắn muốn giải tán bang hội không? Ta cho con 2 lựa chọn...",
                                                "Yes you do!", "Từ chối!");
                                    }
                                    break;
                                }
                                Service.getInstance().sendThongBao(player, "Có bang hội đâu ba!!!");
                                break;
                            case 1:
                                if (player.clan != null) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 153, -1, -1);
                                } else {
                                    Service.getInstance().sendThongBao(player, "Yêu cầu có bang hội !!!");
                                }
                                break;
                            // case 2:
                            //     if (player.clan != null) {
                            //         if (player.clan.banDoKhoBau != null) {
                            //             this.createOtherMenu(player, ConstNpc.MENU_OPENED_BDKB,
                            //                     "|7|BẢN ĐỒ KHO BÁU\n|6|Bang hội của con đang đi Bản đồ Kho báu cấp độ "
                            //                     + player.clan.banDoKhoBau.level + "\nCon có muốn đi theo không?",
                            //                     "Đồng ý", "Từ chối");
                            //         } else {
                            //             this.createOtherMenu(player, ConstNpc.MENU_OPEN_BDKB,
                            //                     "|7|BẢN ĐỒ KHO BÁU\n|6|Bản đồ Kho báu đã chuẩn bị tiếp nhận các đợt tấn công của quái vật\n"
                            //                     + "các con hãy giúp chúng ta tiêu diệt quái vật \n"
                            //                     + "Ở đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                            //                     "Chọn\ncấp độ", "Từ chối");
                            //         }
                            //     } else {
                            //         this.npcChat(player, "Con phải có bang hội ta mới có thể cho con đi");
                            //     }
                            //     break;
                            case 2:
                                if (player.getSession().is_gift_box) {
                                    if (PlayerDAO.setIs_gift_box(player)) {
                                        player.getSession().is_gift_box = false;
                                        player.inventory.coupon += 5;
                                        Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 5 điểm Coupon");
                                        Service.getInstance().sendMoney(player);
                                    }
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPENED_BDKB) {
                        switch (select) {
                            case 0:
                                if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_BDKB) {
                                    BanDoKhoBauService.gI().joinBDKB(player);
                                } else {
                                    this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                            + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_BDKB));
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_BDKB) {
                        switch (select) {
                            case 0:
                                if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_BDKB) {
                                    Input.gI().createFormChooseLevelBDKB(player);
                                } else {
                                    this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                            + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_BDKB));
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCPET_GO_TO_BDKB) {
                        switch (select) {
                            case 0:
                                if (player.chienthan.tasknow == 8) {
                                    player.chienthan.dalamduoc++;
                                }
                                BanDoKhoBauService.gI().openBanDoKhoBau(player, Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.QUY_DOI_HN) {
                        switch (select) {
                            case 0:
                                Input.gI().createFormQDHN(player);
                                break;

                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.QUY_DOI_TV) {
                        switch (select) {
                            case 0:
                                Input.gI().createFormQDTV(player);
                                break;

                        }
                    }
                }
            }
        };
    }

    public static Npc ATM(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        createOtherMenu(player, ConstNpc.BASE_MENU,
                                "|7|Xin chào"
                                + "\b|3|ta đang giữ giúp ngươi quỹ đen !!!"
                                + "\b\b|2|Tổng số tiền: " + Util.format(player.getSession().vnd) + " vnđ"
                                + "\b\b|2|Tổng điểm nạp: " + Util.format(player.getSession().tongnap) + " điểm"
                                + "\b\b|7|TỔNG SỐ TIỀN ĐÃ TIÊU : " + Util.format(player.vnd) + "vnđ",
                                "Rút VND", "Quà Tích\nNạp", "Quy đổi\nXu Vàng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            int thoivang = 2;
                            int hongngoc = 1000;
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, 1,
                                            "|7|Máy rút tiền"
                                            + "\b\b|7|Rút thỏi vàng và hồng ngọc giới hạn không quá 1.000.000 đ"
                                            + "\b\b|5|Số tiền của bạn còn: " + Util.format(player.getSession().vnd) + " VNĐ"
                                            + "\b|2|Sever hiện tại đang X " + Manager.KHUYEN_MAI_NAP + "  Rút tiền"
                                            + "\b|1|1.000VNĐ = " + Util.format(Manager.KHUYEN_MAI_NAP * thoivang) + " Thỏi vàng"
                                            + "\b|1|1.000VNĐ = " + Util.format(Manager.KHUYEN_MAI_NAP * hongngoc) + " Hồng ngọc",
                                            "Thỏi Vàng", "Hồng ngọc", "Từ chối");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, 2,
                                            "|7|Nhận quà mốc nạp"
                                            + "\b\b|7|Mỗi mốc nạp sẽ nhận được quà siêu ngon nhé"
                                            + "\b\b|5|Tổng điểm nap: " + Util.format(player.getSession().tongnap) + "Điểm"
                                            + "\b|1|10k điểm  nhận được:   x10 xu vàng"
                                            + "\b|1|20k điểm  nhận được:   x20 xu vàng"
                                            + "\b|1|50k điểm  nhận được:   x50  xu vàng"
                                            + "\b|1|100k điểm  nhận được:  x150 xu vàng"
                                            + "\b|1|200k điểm  nhận được:  x300 xu vàng"
                                            + "\b|1|500k điểm  nhận được:  x800 xu vàng",
                                            "Nhận Mốc\n'10k'", "Nhận Mốc\n'20k'", "Nhận Mốc\n'50k'", "Nhận Mốc\n'100k'", "Nhận Mốc\n'200k'", "Nhận Mốc\n'500k'");
                                    break;
                                case 2:
                                    this.createOtherMenu(player, 3,
                                            "|7|"
                                            + "\b\b|7|Chọn các mốc quy đổi sau"
                                            + "\b\b|5|Con còn: " + Util.format(player.getSession().vnd) + " VND"
                                            + "\b|1|10k điểm  nhân đươc:   x30 xu vàng"
                                            + "\b|1|20k điểm  nhận được:   x90 xu vàng"
                                            + "\b|1|50k điểm  nhận được:   x150  xu vàng"
                                            + "\b|1|100k điểm  nhận được:  x300 xu vàng"
                                            + "\b|1|200k điểm  nhận được:  x600 xu vàng"
                                            + "\b|1|500k điểm  nhận được:  x1500 xu vàng",
                                            "Đổi\n'10k'", "Đổi\n'20k'", "Đổi\n'50k'", "Đổi\n'100k'", "Đổi\n'200k'", "Đổi\n'500k'");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 1) {
                            switch (select) {
                                case 0:
                                    Input.gI().createFormQDTV(player);
                                    break;
                                case 1:
                                    Input.gI().createFormQDHN(player);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 3) {
                            switch (select) {
                                case 0:
                                    if (player.getSession().vnd >= 10000) {
                                        PlayerDAO.subvnd(player, 10000);

                                        Item thoivang = ItemService.gI().createNewItem((short) 1529, 30);
                                        InventoryServiceNew.gI().addItemBag(player, thoivang);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "|1|Bạn đã nhận thành công mốc 10k");
                                    } else {
                                        this.npcChat(player, "|1|Bạn không đủ điểm để quy đổi mốc nạp");
                                    }
                                    break;

                                case 1:
                                    if (player.getSession().vnd >= 20000) {
                                        PlayerDAO.subvnd(player, 20000);

                                        Item thoivang = ItemService.gI().createNewItem((short) 1529, 60);
                                        InventoryServiceNew.gI().addItemBag(player, thoivang);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "|1|Bạn đã nhận thành công mốc 20k");
                                        InventoryServiceNew.gI().sendItemBags(player);

                                    } else {
                                        this.npcChat(player, "|1|Bạn không đủ điểm để quy đổi mốc nạp");
                                    }
                                    break;

                                case 2:
                                    if (player.getSession().vnd >= 50000) {
                                        PlayerDAO.subvnd(player, 50000);

                                        Item thoivang = ItemService.gI().createNewItem((short) 1529, 90);
                                        InventoryServiceNew.gI().addItemBag(player, thoivang);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "|1|Bạn đã nhận thành công mốc 50k");
                                        InventoryServiceNew.gI().sendItemBags(player);

                                    } else {
                                        this.npcChat(player, "|1|Bạn không đủ điểm để quy đổi mốc nạp");
                                    }
                                    break;
                                case 3:
                                    if (player.getSession().vnd >= 100000) {
                                        PlayerDAO.subvnd(player, 100000);

                                        Item thoivang = ItemService.gI().createNewItem((short) 1529, 300);
                                        InventoryServiceNew.gI().addItemBag(player, thoivang);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "|1|Bạn đã nhận thành công mốc 100k");

                                    } else {
                                        this.npcChat(player, "|1|Bạn không đủ điểm để quy đổi mốc nạp");
                                    }
                                    break;
                                case 4:
                                    if (player.getSession().vnd >= 200000) {
                                        PlayerDAO.subvnd(player, 200000);

                                        Item thoivang = ItemService.gI().createNewItem((short) 1529, 600);
                                        InventoryServiceNew.gI().addItemBag(player, thoivang);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "|1|Bạn đã nhận thành công mốc 200k");

                                    } else {
                                        this.npcChat(player, "|1|Bạn không đủ điểm để quy đổi mốc nạp");
                                    }
                                    break;
                                case 5:
                                    if (player.getSession().vnd >= 500000) {
                                        PlayerDAO.subvnd(player, 500000);

                                        Item thoivang = ItemService.gI().createNewItem((short) 1529, 1500);
                                        InventoryServiceNew.gI().addItemBag(player, thoivang);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "|1|Bạn đã nhận thành công mốc 500k");
                                    } else {
                                        this.npcChat(player, "|1|Bạn không đủ điểm để quy đổi mốc nạp");
                                    }
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 2) {
                            switch (select) {
                                case 0:
                                    if (player.getSession().tongnap >= 10000) {
                                        PlayerDAO.subtongnap(player, 10000);

                                        Item thoivang = ItemService.gI().createNewItem((short) 1529, 10);
                                        InventoryServiceNew.gI().addItemBag(player, thoivang);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "|1|Bạn đã nhận thành công mốc 10k");
                                    } else {
                                        this.npcChat(player, "|1|Bạn không đủ điểm để quy đổi mốc nạp");
                                    }
                                    break;

                                case 1:
                                    if (player.getSession().tongnap >= 20000) {
                                        PlayerDAO.subtongnap(player, 20000);

                                        Item thoivang = ItemService.gI().createNewItem((short) 1529, 20);
                                        InventoryServiceNew.gI().addItemBag(player, thoivang);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "|1|Bạn đã nhận thành công mốc 20k");
                                        InventoryServiceNew.gI().sendItemBags(player);

                                    } else {
                                        this.npcChat(player, "|1|Bạn không đủ điểm để quy đổi mốc nạp");
                                    }
                                    break;

                                case 2:
                                    if (player.getSession().tongnap >= 50000) {
                                        PlayerDAO.subtongnap(player, 50000);

                                        Item thoivang = ItemService.gI().createNewItem((short) 1529, 50);
                                        InventoryServiceNew.gI().addItemBag(player, thoivang);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "|1|Bạn đã nhận thành công mốc 50k");
                                        InventoryServiceNew.gI().sendItemBags(player);

                                    } else {
                                        this.npcChat(player, "|1|Bạn không đủ điểm để quy đổi mốc nạp");
                                    }
                                    break;
                                case 3:
                                    if (player.getSession().tongnap >= 100000) {
                                        PlayerDAO.subtongnap(player, 100000);

                                        Item thoivang = ItemService.gI().createNewItem((short) 1529, 150);
                                        InventoryServiceNew.gI().addItemBag(player, thoivang);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "|1|Bạn đã nhận thành công mốc 100k");

                                    } else {
                                        this.npcChat(player, "|1|Bạn không đủ điểm để quy đổi mốc nạp");
                                    }
                                    break;
                                case 4:
                                    if (player.getSession().tongnap >= 200000) {
                                        PlayerDAO.subtongnap(player, 200000);

                                        Item thoivang = ItemService.gI().createNewItem((short) 1529, 300);
                                        InventoryServiceNew.gI().addItemBag(player, thoivang);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "|1|Bạn đã nhận thành công mốc 200k");

                                    } else {
                                        this.npcChat(player, "|1|Bạn không đủ điểm để quy đổi mốc nạp");
                                    }
                                    break;

                                case 5:
                                    if (player.getSession().tongnap >= 500000) {
                                        PlayerDAO.subtongnap(player, 500000);

                                        Item thoivang = ItemService.gI().createNewItem((short) 1529, 800);
                                        InventoryServiceNew.gI().addItemBag(player, thoivang);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "|1|Bạn đã nhận thành công mốc 500k");
                                    } else {
                                        this.npcChat(player, "|1|Bạn không đủ điểm để quy đổi mốc nạp");
                                    }
                                    break;

                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc truongLaoGuru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc vuaVegeta(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }
    /////////////////////////NPC NHà///////////////////////////////////////////////////////////////////////////

    public static Npc ongGohan_ongMoori_ongParagus(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    String checkvnd;
                    int thoivang;
                    int xu;
                    int hongngoc;
                    if (player.vnd >= 10000 && player.vnd < 100000) {
                        checkvnd = "VIP 1";
                        thoivang = 20;
                        hongngoc = 10000;
                        xu = 3;
                    } else if (player.vnd >= 100000 && player.vnd < 1000000) {
                        checkvnd = "VIP 2";
                        thoivang = 40;
                        xu = 10;
                        hongngoc = 35000;
                    } else if (player.vnd >= 1000000) {
                        checkvnd = "VIP 3";
                        thoivang = 70;
                        xu = 30;
                        hongngoc = 80000;
                    } else {
                        checkvnd = "Bạn là cày chay chính hiệu";
                    }
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Cố Gắng Có Làm Mới Có Ăn Con, đừng lo lắng cho ta.\n"
                                        .replaceAll("%1", player.gender == ConstPlayer.TRAI_DAT ? "Quy lão Kamê"
                                                : player.gender == ConstPlayer.NAMEC ? "Trưởng lão Guru" : "Vua Vegeta") + "Ta đang giữ tiền tiết kiệm của con\n|1| Hiện tại con đang có: " + player.getSession().vnd + "VND"
                                + "\n|7| Chào mừng con đến với NRO :",
                                "Nhận ngọc", "Nhận\nĐệ Tử", "Giftcode", "Điểm danh",
                                "Nhận Vàng",
                                "Lệnh\ncơ bản"
                        );

                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select
            ) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: // đổi mk
                                if (player.inventory.gem >= 200000000) {
                                    this.npcChat(player, "Tham Lam");
                                    break;
                                }
                                player.inventory.gem = 200000000;
                                Service.getInstance().sendMoney(player);
                                Service.getInstance().sendThongBao(player, "|1|Bạn vừa nhận được 20tr Ngọc xanh");
                                break;
                            case 1: //nhận ngọc
                                if (player.pet == null) {
                                    PetService.gI().createNormalPet(player, Util.nextInt(0, 2));
                                    Service.getInstance().sendThongBao(player, "Bạn vừa nhận được đệ tử");
                                } else {
                                    this.npcChat(player, "Tham Lam");
                                }
                                break;
                            case 2: //nhận đệ tử
                                Input.gI().createFormGiftCode(player);
                                break;
                            case 3:
                                if (player.diemdanh == 0) {
                                    int soluongtv = 200;
                                    int soluonghn = 200000;
                                    Item thoivang = ItemService.gI().createNewItem((short) 457, soluongtv);
                                    Item hongngoc = ItemService.gI().createNewItem((short) 861, soluonghn);
                                    thoivang.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, hongngoc);
                                    InventoryServiceNew.gI().addItemBag(player, thoivang);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    player.diemdanh = System.currentTimeMillis();
                                    Service.getInstance().sendThongBao(player, "|7|Bạn vừa nhận được x " + soluonghn + " " + hongngoc.template.name);
                                    Service.getInstance().sendThongBao(player, "|7|Bạn vừa nhận được x " + soluongtv + " " + thoivang.template.name);
                                } else {
                                    this.npcChat(player, "Mai nhận tiếp nhé !!!!!!");
                                }
                                break;
                            case 4:
                                if (player.inventory.gold <= 10_000_000_000L) {
                                    player.inventory.gold += 10_000_000_000L;
                                    Service.gI().sendMoney(player);
                                    this.npcChat(player, "Bạn đã nhận đươc 10 tỷ vàng");
                                } else {
                                    this.npcChat(player, "Tiêu hết đi rồi nhận");
                                }
                                break;
                            case 5:
                                this.createOtherMenu(player, 1231, ""
                                        + "|7|CÁC LỆNH CƠ BẢN HIÊN TẠI CỦA GAME"
                                        + "\n|1|chat > auto: hiển thị menu tự động"
                                        + "\n|1|chat > map: hiển menu lệnh di chuyẻn nhanh"
                                        + "\n|1|chat > tt : hiển thị thông tin của bạn"
                                        + "\n|1|chat > pet : hiển thị thông tin thú cưng của bạn", "Đóng");
                                break;

                        }
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.QUA_TAN_THU) {
                    switch (select) {
                        case 0:
//                                        if (!player.gift.gemTanThu) {
                            if (true) {
                                player.inventory.gem = 100000;
                                Service.getInstance().sendMoney(player);
                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 100K ngọc xanh");
                                player.gift.gemTanThu = true;
                            } else {
                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Con đã nhận phần quà này rồi mà",
                                        "Đóng");
                            }
                            break;
                        case 1:
                            if (nhanDeTu) {
                                if (player.pet == null) {
                                    PetService.gI().createNormalPet(player, Util.nextInt(0, 2));
                                    Service.getInstance().sendThongBao(player, "Bạn vừa nhận được đệ tử");
                                } else {
                                    this.npcChat("Con đã nhận đệ tử rồi");
                                }
                            }
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHAN_THUONG) {
                    switch (select) {
                        case 0:
                            ShopServiceNew.gI().opendShop(player, "ITEMS_REWARD", true);
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.NAP_THE) {
                    Input.gI().createFormNapThe(player, (byte) select);
                }
            }
        };
    }

    public static Npc bulmaQK(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Cậu cần trang bị gì cứ đến chỗ tôi nhé", "Cửa\nhàng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.TRAI_DAT) {
                                    ShopServiceNew.gI().opendShop(player, "BUNMA", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất", "Đóng");
                                }
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc dende(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {

                        int nPlSameClan1 = 0;
                        for (Player pl : player.zone.getPlayers()) {
                            if (!pl.equals(player) && pl.clan != null
                                    && pl.clan.equals(player.clan) && pl.location.x >= 53
                                    && pl.location.x <= 1188 && pl.idNRNM != -1) {
                                nPlSameClan1++;
                            }
                        }
                        if (player.zone.map.mapId == 7 && nPlSameClan1 < 6 && player.idNRNM != -1) {
                            createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Ngươi phải có ít nhất đồng đội cùng bang đứng gần mới có thể\nvào\n"
                                    + "tuy nhiên ta khuyên ngươi nên đi cùng với 7 người để khỏi chết.\n"
                                    + "Hahaha.", "OK", "Hướng\ndẫn\nthêm");
                            return;
                        } else if (player.zone.map.mapId == 7 && nPlSameClan1 >= 6 && player.idNRNM != -1 && player.idNRNM == 353) {

                            this.createOtherMenu(player, 1, "Ồ, ngọc rồng namếc, bạn thật là may mắn\nnếu tìm đủ 7 viên sẽ được Rồng Thiêng Namếc ban cho điều ước", "Hướng\ndẫn\nGọi Rồng", "Gọi rồng", "Từ chối");
                        } //                        if (player.idNRNM != -1) {
                        else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Anh cần trang bị gì cứ đến chỗ em nhé", "Cửa\nhàng");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.NAMEC) {
                                    ShopServiceNew.gI().opendShop(player, "DENDE", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi anh, em chỉ bán đồ cho dân tộc Namếc", "Đóng");
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == 1) {
                        if (player.zone.map.mapId == 7 && player.idNRNM != -1) {
                            if (player.idNRNM == 353) {
                                NgocRongNamecService.gI().firstNrNamec = true;
                                NgocRongNamecService.gI().timeNrNamec = 0;
                                NgocRongNamecService.gI().doneDragonNamec();
                                NgocRongNamecService.gI().initNgocRongNamec((byte) 1);
                                NgocRongNamecService.gI().reInitNrNamec((long) TIME_OP);
                                SummonDragon.gI().summonNamec(player);
                            } else {
                                Service.getInstance().sendThongBao(player, "Anh phải có viên ngọc rồng Namếc 1 sao");
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc appule(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi cần trang bị gì cứ đến chỗ ta nhé", "Cửa\nhàng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.XAYDA) {
                                    ShopServiceNew.gI().opendShop(player, "APPULE", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Về hành tinh hạ đẳng của ngươi mà mua đồ cùi nhé. Tại đây ta chỉ bán đồ cho người Xayda thôi", "Đóng");
                                }
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc sukientet(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {

                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|7|HIỆN TẠI SEVER NRO ĐANG DIỄN RA SƯ KIỆN TẾT"
                            + "\n|7|Số bánh đã được quyên góp là: " + noibanh
                            + "\n|1|Mỗi lần quyên góp tét sẽ tăng tnsm toàn sever"
                            + "\n|1|TNSM SEVER HIỆN TẠI ĐÃ TĂNG " + noibanh / Manager.RATE_EXP_SERVER * 10 + "%"
                            + "\n|1|LUU Ý : mỗi ngày sẽ reset góp bánh tét",
                            "Góp Bánh Tét", "Nấu Bánh Chưng", "Shop Tết");

                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        Item banhtet = InventoryServiceNew.gI().findItemBag(player, 752);
                        switch (select) {
                            case 0:
                                if (banhtet != null && banhtet.quantity >= 1) {
                                    if (noibanh < 200) {
                                        noibanh += 1;
                                        Manager.RATE_EXP_SERVER += ((byte) noibanh * 3 / 100);
                                        this.npcChat(player, "|7|Góp bánh tét thành công");
                                    } else {
                                        this.npcChat(player, "|7|Hôm nay đã quyên góp tối đa rồi");
                                    }
                                } else {
                                    this.npcChat(player, "|7|Bạn không đủ bánh tét để góp");
                                }
                                break;
                            case 1:
                                Item item1 = InventoryServiceNew.gI().findItemBag(player, 748);
                                Item item2 = InventoryServiceNew.gI().findItemBag(player, 749);
                                Item item3 = InventoryServiceNew.gI().findItemBag(player, 750);
                                Item item4 = InventoryServiceNew.gI().findItemBag(player, 751);
                                if (item1 != null && item1.quantity >= 10
                                        && item2 != null && item2.quantity >= 10
                                        && item3 != null && item3.quantity >= 10
                                        && item4 != null && item4.quantity >= 10) {
                                    int soluong = Util.nextInt(1, 5);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, item1, 10);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, item2, 10);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, item3, 10);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, item4, 10);
                                    Item banhchung = ItemService.gI().createNewItem((short) 753, soluong);
                                    InventoryServiceNew.gI().addItemBag(player, banhchung);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    this.npcChat(player, "Bạn nhận được x" + soluong + " " + banhchung.template.name);
                                } else {
                                    this.npcChat(player, "Bạn không đủ x10 nguyên liệu");
                                }
                                break;
                            case 2:
                                ShopServiceNew.gI().opendShop(player, "SHOPTET", false);
                                break;

                        }
                    }
                }
            }
        };
    }

    public static Npc drDrief(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (this.mapId == 84) {
                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                pl.gender == ConstPlayer.TRAI_DAT ? "Đến\nTrái Đất" : pl.gender == ConstPlayer.NAMEC ? "Đến\nNamếc" : "Đến\nXayda");
                    } else if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                        if (pl.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(pl, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                    + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                        } else {
                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                    "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                    "Đến\nNamếc", "Đến\nXayda", "Siêu thị");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 84) {
                        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 24, -1, -1);
                    } else if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                break;
                            case 1:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                break;
                            case 2:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc cargo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                        if (pl.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(pl, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                    + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                        } else {
                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                    "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                    "Đến\nTrái Đất", "Đến\nXayda", "Siêu thị");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                break;
                            case 1:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                break;
                            case 2:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc cui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            private final int COST_FIND_BOSS = 50000000;

            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                        if (pl.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(pl, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                    + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                        } else {
                            if (this.mapId == 19) {

                                int taskId = TaskService.gI().getIdTask(pl);
                                switch (taskId) {
                                    case ConstTask.TASK_19_0:
                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_KUKU,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến chỗ\nKuku\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến Cold", "Đến\nNappa", "Từ chối");
                                        break;
                                    case ConstTask.TASK_19_1:
                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_MAP_DAU_DINH,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến chỗ\nMập đầu đinh\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến Cold", "Đến\nNappa", "Từ chối");
                                        break;
                                    case ConstTask.TASK_19_2:
                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_RAMBO,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến chỗ\nRambo\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến Cold", "Đến\nNappa", "Từ chối");
                                        break;
                                    default:
                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến Cold", "Đến\nNappa", "Từ chối");

                                        break;
                                }
                            } else if (this.mapId == 68) {
                                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                        "Ngươi muốn về Thành Phố Vegeta", "Đồng ý", "Từ chối");
                            } else {
                                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                        "Tàu vũ trụ Xayda sử dụng công nghệ mới nhất, "
                                        + "có thể đưa ngươi đi bất kỳ đâu, chỉ cần trả tiền là được.",
                                        "Đến\nTrái Đất", "Đến\nNamếc", "Siêu thị");
                            }
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 26) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                    break;
                            }
                        }
                    }
                    if (this.mapId == 19) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_KUKU) {
                            switch (select) {
                                case 0:
                                    Boss kuku = BossManager.gI().getBossById(BossID.KUKU);
                                    if (kuku != null) {
                                        ChangeMapService.gI().changeMap(player, kuku.zone, kuku.location.x, kuku.location.y);
                                    } else {
                                        Service.gI().sendThongBao(player, "Boss chết rồi vui lòng đợi 5 giây");
                                        break;
                                    }
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_MAP_DAU_DINH) {
                            switch (select) {
                                case 0:
                                    Boss mapdaudinh = BossManager.gI().getBossById(BossID.MAP_DAU_DINH);
                                    if (mapdaudinh != null) {
                                        ChangeMapService.gI().changeMap(player, mapdaudinh.zone, mapdaudinh.location.x, mapdaudinh.location.y);

                                    } else {
                                        Service.gI().sendThongBao(player, "Boss chết rồi vui lòng đợi 5 giây");
                                        break;
                                    }
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_RAMBO) {
                            switch (select) {
                                case 0:
                                    Boss rambo = BossManager.gI().getBossById(BossID.RAMBO);
                                    if (rambo != null) {
                                        ChangeMapService.gI().changeMap(player, rambo.zone, rambo.location.x, rambo.location.y);
                                    } else {
                                        Service.gI().sendThongBao(player, "Boss chết rồi vui lòng đợi 5 giây");
                                        break;
                                    }
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                    break;
                            }
                        }
                    }
                    if (this.mapId == 68) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 19, -1, 1100);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc santa(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?",
                            "Shop\nNoob", "Shop Santa", "Shop\nPrenium");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: //shop
                                    ShopServiceNew.gI().opendShop(player, "Shop_Santa_VIP", true);
                                    break;
                                case 1: //tiệm hồng ngọc
                                    ShopServiceNew.gI().opendShop(player, "SANTA", false);

                                    break;
                                case 2: //tiệm hồng ngọc
                                    ShopServiceNew.gI().opendShop(player, "SHOPVIP", false);

                                    break;

                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc uron(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    ShopServiceNew.gI().opendShop(pl, "URON", false);
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc baHatMit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5 || this.mapId == 13) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi tìm ta có việc gì?",
                                "Ép sao\ntrang bị",
                                "Pha lê\nhóa\ntrang bị",
                                "Tinh ấn\ntrang bị",
                                "Chân mệnh",
                                "Ramdom SKH",
                                "Nâng Cấp SKH",
                                "Nâng Cấp\nLEVEL SKH"
                        );
                    } else if (this.mapId == 121) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi tìm ta có việc gì?",
                                "Về đảo\nrùa");
                    } else {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi tìm ta có việc gì?",
                                "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm",
                                "Nhập\nNgọc Rồng",
                                "Nâng Bông Tai",
                                "Mở chỉ số bông tai"
                        );
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5 || this.mapId == 13) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.EP_SAO_TRANG_BI);
                                    break;
                                case 1:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHA_LE_HOA_TRANG_BI);
                                    break;
                                case 2:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.AN_TRANG_BI);
                                    break;
                                case 3: //nâng cấp Chân mệnh
                                    this.createOtherMenu(player, 5701,
                                            "|7|CHÂN MỆNH"
                                            + "\n\n|1|Bạn đang có: " + Util.format(player.pointsb) + " Điểm Săn Boss"
                                            + "\n\n|5| Tặng Free  Chân Mệnh cấp 1 Bú Nhanh"
                                            + "\n|3| Lưu ý: Chỉ được nhận Chân mệnh 1 lần (Hành trang chỉ tồn tại 1 Chân mệnh)"
                                            + "\nNếu đã có Chân mệnh. Ta sẽ giúp ngươi nâng cấp bậc lên với các dòng chỉ số cao hơn",
                                            "Nhận Chân mệnh", "Nâng cấp Chân mệnh");
                                    break;
                                case 4:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.RANDOM_SKH);
                                    break;
                                case 5:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.RANDOM_SKHC2);
                                    break;
                                case 6:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_LEVEL_SKH);
                                    break;

                            }

                        } else if (player.iDMark.getIndexMenu() == ConstNpc.SELECTSKH) {
                            switch (select) {
                                case 0:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_SKH_VIP);
                                    break;
                                case 1:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.CUONGHOASKH);
                                    break;
                                case 2:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.FUSION_SKH);
                                    break;

                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENUFUSIONSKH) {
                            if (select == 0) {
                                CombineServiceNew.gI().startCombine(player);
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENUCUONGHOASKH) {
                            if (select == 0) {
                                CombineServiceNew.gI().startCombine(player);
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINELVL) {
                            if (select == 0) {
                                CombineServiceNew.gI().startCombine(player);
                            }
                        } else if (player.iDMark.getIndexMenu() == 5701) {
                            switch (select) {
                                case 0:
                                    for (int i = 0; i < 9; i++) {
                                        Item findItemBag = InventoryServiceNew.gI().findItemBag(player, 1300 + i);
                                        Item findItemBody = InventoryServiceNew.gI().findItemBody(player, 1300 + i);
                                        if (findItemBag != null || findItemBody != null) {
                                            Service.gI().sendThongBao(player, "|7|Ngươi đã có Chân mệnh rồi mà");
                                            return;
                                        }
                                    }
                                    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                        player.inventory.event -= 0;
                                        Item chanmenh = ItemService.gI().createNewItem((short) 1300);
                                        chanmenh.itemOptions.add(new Item.ItemOption(50, 10));
                                        chanmenh.itemOptions.add(new Item.ItemOption(77, 10));
                                        chanmenh.itemOptions.add(new Item.ItemOption(103, 10));
                                        chanmenh.itemOptions.add(new Item.ItemOption(30, 1));
                                        InventoryServiceNew.gI().addItemBag(player, chanmenh);
                                        Service.getInstance().sendMoney(player);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "|1|Bạn nhận được Chân mệnh Cấp 1");
                                    } else {
                                        this.npcChat(player, "|1|Kiểm Tra Hành Trang Của Ngươi Đi");
                                    }
                                    break;
                                case 1:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_CHAN_MENH);
                                    break;

                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.EP_SAO_TRANG_BI:
                                case CombineServiceNew.AN_TRANG_BI:
                                case CombineServiceNew.CHUYEN_HOA_TRANG_BI:
                                case CombineServiceNew.NANG_CAP_CHAN_MENH:
                                case CombineServiceNew.RANDOM_SKHC2:
                                case CombineServiceNew.CHUYEN_HOA_DO_CT_SKH:
                                    switch (select) {
                                        case 0:
                                            if (player.combineNew.typeCombine == CombineServiceNew.EP_SAO_TRANG_BI) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 1:
                                            if (player.combineNew.typeCombine == CombineServiceNew.EP_SAO_TRANG_BI) {
                                                player.combineNew.quantities = 2;
                                            }
                                            break;
                                        case 2:
                                            if (player.combineNew.typeCombine == CombineServiceNew.EP_SAO_TRANG_BI) {
                                                player.combineNew.quantities = 3;
                                            }
                                            break;
                                        case 3:
                                            if (player.combineNew.typeCombine == CombineServiceNew.EP_SAO_TRANG_BI) {
                                                player.combineNew.quantities = 4;
                                            }
                                            break;
                                        case 4:
                                            if (player.combineNew.typeCombine == CombineServiceNew.EP_SAO_TRANG_BI) {
                                                player.combineNew.quantities = 5;
                                            }
                                            break;
                                        case 6:
                                            if (player.combineNew.typeCombine == CombineServiceNew.EP_SAO_TRANG_BI) {
                                                player.combineNew.quantities = 6;
                                            }
                                            break;
                                        case 7:
                                            if (player.combineNew.typeCombine == CombineServiceNew.EP_SAO_TRANG_BI) {
                                                player.combineNew.quantities = 7;
                                            }
                                            break;
                                        case 8:
                                            if (player.combineNew.typeCombine == CombineServiceNew.EP_SAO_TRANG_BI) {
                                                player.combineNew.quantities = 8;
                                            }
                                            break;
                                    }
                                    CombineServiceNew.gI().startCombine(player);
                                    break;
                                case CombineServiceNew.PHA_LE_HOA_TRANG_BI:
                                case CombineServiceNew.DUNGHOPSKH:
                                    switch (select) {
                                        case 0:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 1:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 10;
                                            }
                                            break;
                                        case 2:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 100;
                                            }
                                            break;
                                    }
                                    CombineServiceNew.gI().startCombine(player);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHUYEN_HOA_DO_HUY_DIET) {
                            if (select == 0) {
                                CombineServiceNew.gI().startCombine(player);
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_RANDOM_SKH) {
                            if (select == 0) {
                                CombineServiceNew.gI().startCombine(player);
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DUNG_HOP) {
                            if (select == 0) {
                                CombineServiceNew.gI().startCombine(player);
                            }
                        }
                    } else if (this.mapId == 112) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                    break;
                            }
                        }
                    } else if (this.mapId == 42 || this.mapId == 43 || this.mapId == 44 || this.mapId == 84) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: //shop bùa
                                    createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA,
                                            "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để "
                                            + "mạnh mẽ à, mua không ta bán cho, xài rồi lại thích cho mà xem.",
                                            "Bùa\n1 tháng", "Đóng");
                                    break;
                                case 1:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_VAT_PHAM);
                                    break;
                                case 2:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NHAP_NGOC_RONG);
                                    break;
                                case 3: //làm phép nhập đá
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_BONG_TAI);
                                    break;
                                case 4: //làm phép nhập đá
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.OPTIONBONGTAI);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_SHOP_BUA) {
                            switch (select) {
                                case 0:
                                    ShopServiceNew.gI().opendShop(player, "BUA_1M", true);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.NANG_CAP_VAT_PHAM:
                                case CombineServiceNew.LAM_PHEP_NHAP_DA:
                                case CombineServiceNew.NHAP_NGOC_RONG:
                                    switch (select) {
                                        case 0:
                                            if (player.combineNew.typeCombine == CombineServiceNew.NHAP_NGOC_RONG) {
                                                player.combineNew.quantities = 1;

                                            }
                                            break;
                                        case 1:
                                            if (player.combineNew.typeCombine == CombineServiceNew.NHAP_NGOC_RONG) {
                                                player.combineNew.quantities = 100;

                                            }
                                            break;

                                    }
                                    CombineServiceNew.gI().startCombine(player);
                                case CombineServiceNew.PHAN_RA_DO_THAN_LINH:
                                case CombineServiceNew.NANG_CAP_SKH_VIP:
                                // case CombineServiceNew.MO_CHI_SO_BONG_TAI_2:
                                // case CombineServiceNew.MO_CHI_SO_BONG_TAI_3:
                                // case CombineServiceNew.MO_CHI_SO_BONG_TAI_4:
                                case CombineServiceNew.NANG_CAP_BONG_TAI:
                                //case CombineServiceNew.NANGCAPBONGTAI:
                                case CombineServiceNew.OPTIONBONGTAI:
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc whisdots(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 154) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "|7|NÂNG CẤP ĐỒ THIÊN SỨ\n|6| Mang cho ta Công thức + Đá cầu vòng và 999 Mảnh thiên sứ ta sẽ chế tạo đồ Thiên sứ cho ngươi"
                                + "\nĐồ Thiên sứ khi chế tạo sẽ random chỉ số 0-15%"
                                + "\n|2|(Khi mang đủ 5 món Hủy diệt ngươi hãy theo Osin qua Hành tinh ngục tù tìm kiếm Mảnh thiên sứ và săn BOSS Thiên sứ để thu thập Đá cầu vòng)"
                                + "\n|1| Ngươi có muốn nâng cấp không?",
                                "Hướng dẫn", "Nâng Cấp \nĐồ Thiên Sứ", "Shop\n Thiên sứ", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 154) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DO_TS);
                                    break;
                                case 1:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_DO_TS);
                                    break;
                                case 2:
                                    ShopServiceNew.gI().opendShop(player, "THIEN_SU", true);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.NANG_CAP_DO_TS:
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_DO_TS) {
                            if (select == 0) {
                                CombineServiceNew.gI().startCombine(player);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc bandetu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        String moneyText = String.format("|1|Tiền hiện còn : %s", Util.format(player.getSession().vnd));

                        String menuContent = String.format(
                                "|7|Shop Bán Đệ Tử\n"
                                + "|8|Người muốn mạnh thì nạp tiền cho ta!!!\n"
                                + "|1|Tiền hiện còn : %s", Util.format(player.getSession().vnd) + "\n"
                                + "|2|10k Đệ Ma Bư tăng 10% chỉ số\n"
                                + "|2|20k Đệ Beerus tăng 20%% chỉ số\n"
                                + "|2|30k Đệ Siêu Broly tăng 30% chỉ số\n"
                                + "|2|40k Đệ Ngộ Khỉ 40% chỉ số\n"
                                + "|2|50k Đệ Fushion Zamus 50%");

                        this.createOtherMenu(player, ConstNpc.BASE_MENU, menuContent,
                                "Nhận\nMa bư", "Nhận \nBeerus", "Nhận \nSiêu Broly", "Nhận\nNgộ Khỉ", "Nhận\nFushion Zamus");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            Item trung1 = ItemService.gI().createNewItem((short) 1738, 1);
                            Item trung2 = ItemService.gI().createNewItem((short) 1739, 1);
                            Item trung3 = ItemService.gI().createNewItem((short) 1740, 1);
                            Item trung4 = ItemService.gI().createNewItem((short) 1741, 1);
                            Item trung5 = ItemService.gI().createNewItem((short) 1742, 1);
                            switch (select) {
                                case 0:
                                    if (player.pet != null) {
                                        if (player.getSession().vnd >= 10000) {
                                            InventoryServiceNew.gI().addItemBag(player, trung1);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subvnd(player, 10000);
                                        } else {
                                            this.npcChat(player, "|1|Không đủ tiền nạp đi");
                                        }
                                    } else {
                                        this.npcChat(player, "|1|Không có đệ thườg sao đổi được");
                                    }
                                    break;
                                case 1:
                                    if (player.pet != null) {
                                        if (player.getSession().vnd >= 20000) {
                                            InventoryServiceNew.gI().addItemBag(player, trung2);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subvnd(player, 20000);
                                        } else {
                                            this.npcChat(player, "|1|Không đủ tiền nạp đi");
                                        }
                                    } else {
                                        this.npcChat(player, "|1|Không có đệ thườg sao đổi được");
                                    }
                                    break;
                                case 2:
                                    if (player.pet != null) {
                                        if (player.getSession().vnd >= 30000) {
                                            InventoryServiceNew.gI().addItemBag(player, trung3);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subvnd(player, 30000);
                                        } else {
                                            this.npcChat(player, "|1|Không đủ tiền nạp đi");
                                        }
                                    } else {
                                        this.npcChat(player, "|1|Không có đệ thườg sao đổi được");
                                    }
                                    break;
                                case 3:
                                    if (player.pet != null) {
                                        if (player.getSession().vnd >= 40000) {
                                            InventoryServiceNew.gI().addItemBag(player, trung4);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subvnd(player, 40000);
                                        } else {
                                            this.npcChat(player, "|1|Không đủ tiền nạp đi");
                                        }
                                    } else {
                                        this.npcChat(player, "|1|Không có đệ thườg sao đổi được");
                                    }
                                    break;
                                case 4:
                                    if (player.pet != null) {
                                        if (player.getSession().vnd >= 50000) {
                                            InventoryServiceNew.gI().addItemBag(player, trung5);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subvnd(player, 50000);
                                        } else {
                                            this.npcChat(player, "|1|Không đủ tiền nạp đi");
                                        }
                                    } else {
                                        this.npcChat(player, "|1|Không có đệ thườg sao đổi được");
                                    }
                                    break;
                            }

                        }
                    }
                }
            }
        };
    }

    public static Npc ruongDo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    InventoryServiceNew.gI().sendItemBox(player);
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc dauThan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    player.magicTree.openMenuTree();
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    TaskService.gI().checkDoneTaskConfirmMenuNpc(player, this, (byte) select);
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_LEFT_PEA:
                            if (select == 0) {
                                player.magicTree.harvestPea();
                            } else if (select == 1) {
                                if (player.magicTree.level == 10) {
                                    player.magicTree.fastRespawnPea();
                                } else {
                                    player.magicTree.showConfirmUpgradeMagicTree();
                                }
                            } else if (select == 2) {
                                player.magicTree.fastRespawnPea();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_FULL_PEA:
                            if (select == 0) {
                                player.magicTree.harvestPea();
                            } else if (select == 1) {
                                player.magicTree.showConfirmUpgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_CONFIRM_UPGRADE:
                            if (select == 0) {
                                player.magicTree.upgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_UPGRADE:
                            if (select == 0) {
                                player.magicTree.fastUpgradeMagicTree();
                            } else if (select == 1) {
                                player.magicTree.showConfirmUnuppgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_CONFIRM_UNUPGRADE:
                            if (select == 0) {
                                player.magicTree.unupgradeMagicTree();
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc calick(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            private final byte COUNT_CHANGE = 50;
            private int count;

            private void changeMap() {
                if (this.mapId != 102) {
                    count++;
                    if (this.count >= COUNT_CHANGE) {
                        count = 0;
                        this.map.npcs.remove(this);
                        Map map = MapService.gI().getMapForCalich();
                        this.mapId = map.mapId;
                        this.cx = Util.nextInt(100, map.mapWidth - 100);
                        this.cy = map.yPhysicInTop(this.cx, 0);
                        this.map = map;
                        this.map.npcs.add(this);
                    }
                }
            }

            @Override
            public void openBaseMenu(Player player) {
                player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
                if (TaskService.gI().getIdTask(player) < ConstTask.TASK_20_0) {
                    Service.getInstance().hideWaitDialog(player);
                    Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    return;
                }
                if (this.mapId != player.zone.map.mapId) {
                    Service.getInstance().sendThongBao(player, "Calích đã rời khỏi map!");
                    Service.getInstance().hideWaitDialog(player);
                    return;
                }

                if (this.mapId == 102) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Chào chú, cháu có thể giúp gì?",
                            "Kể\nChuyện", "Quay về\nQuá khứ");
                } else {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Chào chú, cháu có thể giúp gì?", "Kể\nChuyện", "Đi đến\nTương lai", "Từ chối");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (this.mapId == 102) {
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            //kể chuyện
                            NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                        } else if (select == 1) {
                            //về quá khứ
                            ChangeMapService.gI().goToQuaKhu(player);
                        }
                    }
                } else if (player.iDMark.isBaseMenu()) {
                    if (select == 0) {
                        //kể chuyện
                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                    } else if (select == 1) {
                        //đến tương lai
//                                    changeMap();
                        if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_20_0) {
                            ChangeMapService.gI().goToTuongLai(player);
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    }
                }
            }
        };
    }

    public static Npc jaco(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 0) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "|7| KHU VỰC BOSS NHÂN BẢN"
                                + "\n\n|6|Gô Tên, Calich và Monaka đang gặp chuyện ở hành tinh Potaufeu"
                                + "\nĐánh bại những kẻ giả mạo ngươi sẽ nhận được những phần thưởng hấp dẫn"
                                + "\n|3|Hạ Boss Nhân Bản sẽ nhận được Item Siêu cấp"
                                + "\n|2|Hãy đến đó ngay",
                                "Đến \nPotaufeu");
                    } else if (this.mapId == 139 || this.mapId == 170) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Người muốn trở về?", "Quay về", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 24) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                //đến potaufeu
                                ChangeMapService.gI().goToPotaufeu(player);
                            }
                        }
                    } else if (this.mapId == 139 || this.mapId == 170) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, 354);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc Potage(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 149) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "tét", "Gọi nhân bản");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (select == 0) {
                        BossManager.gI().createBoss(-214);
                    }
                }
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////
//    public static Npc npcChienthan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        return new Npc(mapId, status, cx, cy, tempId, avartar) {
//            @Override
//            public void openBaseMenu(Player player) {
//                if (TaskService.gI().getIdTask(player) < ConstTask.TASK_24_0) {
//                    // chưa thỏa dkien để nhận nhiệm vụ
//                    createOtherMenu(player, 0, "|7|NHẬN thú cưng"
//                            + "\n\n|5|Ngươi muốn nhận quả đào tiên để gọi khỉ con ?"
//                            + "\nTa không thể để các vị Thần mạnh mẽ đi theo kẻ yếu kém được"
//                            + "\n|3|Để nhận được quả đào tiên ngươi cần đạt đến Nhiệm vụ 24 và Phải hoàn thành 10 nhiệm vụ ngoại tuyến mà ta đưa ra !!"
//                            + "\n|5|Hãy tiếp tục cố gắng để trở nên mạnh hơn nhé",
//                            "Đóng");
//                }
//                // đã hoàn thành nhiệm vụ thứ...=> trả nhiệm vụ, menu 100
//                if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0 && player.chienthan.dalamduoc >= player.chienthan.maxcount && player.chienthan.maxcount != 0 && player.chienthan.tasknow < 10) {
//                    createOtherMenu(player, 100, "|7|NHẬN thú cưng"
//                            + "\n\n|1|Nhiệm vụ hiện tại: " + player.nhiemvuchienthan(player.chienthan.tasknow)
//                            + "\n\n|2|Đã hoàn thành: " + player.chienthan.dalamduoc + "/" + player.chienthan.maxcount
//                            + "\n|3|Tiến độ: " + player.chienthan.tasknow + "/" + player.chienthan.maxtask + " (Nhiệm vụ)"
//                            + "\n|7| Đã xong nhiệm vụ thứ " + player.chienthan.tasknow,
//                            "Trả nhiệm vụ", "Đóng");
//                }
//                // chưa hoàn thành nhiệm vụ, menu 100
//                if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0 && player.chienthan.dalamduoc < player.chienthan.maxcount && player.chienthan.maxcount != 0 && player.chienthan.tasknow != 10) {
//                    createOtherMenu(player, 100, "|7|NHẬN thú cưng"
//                            + "\n\n|1|Nhiệm vụ hiện tại: " + player.nhiemvuchienthan(player.chienthan.tasknow)
//                            + "\n\n|2|Đã hoàn thành: " + player.chienthan.dalamduoc + "/" + player.chienthan.maxcount
//                            + "\n|3|Tiến độ: " + player.chienthan.tasknow + "/" + player.chienthan.maxtask + " (Nhiệm vụ)",
//                            "Đóng");
//                }
//                // Nhận nhiệm vụ tiếp theo
//                if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0 && player.chienthan.maxcount == 0 && player.chienthan.tasknow < 10) {
//                    createOtherMenu(player, 101, "|7|NHẬN thú cưng"
//                            + "\n\n|5|Ngươi muốn nhận quả đào tiên để gọi khỉ con ?"
//                            + "\nTa không thể để các vị Thần mạnh mẽ đi theo kẻ yếu kém được"
//                            + "\n|3|Để nhận được quả đào tiên ngươi cần đạt đến Nhiệm vụ 24 và Phải hoàn thành 10 nhiệm vụ ngoại tuyến mà ta đưa ra !!"
//                            + "\n|5|Hãy tiếp tục cố gắng để trở nên mạnh hơn nhé"
//                            + "\n|1|Tiến độ: " + player.chienthan.tasknow + "/" + player.chienthan.maxtask + " (Nhiệm vụ)",
//                            "Nhận nhiệm vụ", "Đóng");
//                }
//
//                // Hoàn thành hết => Nhận Chiến thần
//                if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0 && player.chienthan.tasknow >= 10) {
//                    createOtherMenu(player, 104, "|7|NHẬN thú cưng"
//                            + "\n\n|1|Nhiệm vụ hiện tại: " + player.nhiemvuchienthan(player.chienthan.tasknow)
//                            + "\n\n|2|Đã hoàn thành: " + player.chienthan.dalamduoc + "/" + player.chienthan.maxcount
//                            + "\n|3|Tiến độ: " + player.chienthan.tasknow + "/" + player.chienthan.maxtask + " (Nhiệm vụ)"
//                            + "\n|7|Bạn đã hoàn thành tất cả nhiệm vụ",
//                            "NHẬN thú cưng", "Đóng");
//                }
//            }
//
//            @Override
//            public void confirmMenu(Player player, int select) {
//                if (canOpenNpc(player)) {
//                    if (player != null && player.iDMark.getIndexMenu() == 100) {
//                        // đã hoàn thành nhiệm vụ thứ...=> trả nhiệm vụ
//                        if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0 && player.chienthan.dalamduoc >= player.chienthan.maxcount && player.chienthan.maxcount != 0) {
//                            if (select == 0) {
//                                if (player.chienthan.tasknow < 10) {
//                                    player.chienthan.dalamduoc = 0;
//                                    player.chienthan.maxcount = 0;
//                                    if (player.chienthan.tasknow != 0 && player.chienthan.maxcount == 0 && player.chienthan.tasknow < 10) {
//                                        createOtherMenu(player, 101, "|7|NHẬN thú cưng"
//                                                + "\n\n|5|Ngươi muốn nhận quả đào tiên để gọi khỉ con ?"
//                                                + "\nTa không thể để các vị Thần mạnh mẽ đi theo kẻ yếu kém được"
//                                                + "\n|3|Để nhận được quả đào tiên ngươi cần đạt đến Nhiệm vụ 24 và Phải hoàn thành 10 nhiệm vụ ngoại tuyến mà ta đưa ra !!"
//                                                + "\n|5|Hãy tiếp tục cố gắng để trở nên mạnh hơn nhé"
//                                                + "\n|1|Tiến độ: " + player.chienthan.tasknow + "/" + player.chienthan.maxtask + " (Nhiệm vụ)",
//                                                "Nhận nhiệm vụ", "Đóng");
//                                    }
//                                } else if (player.chienthan.tasknow == 10 && player.chienthan.dalamduoc >= player.chienthan.maxcount) { //&& player.chienthan.dalamduoc >= player.chienthan.maxcount
//                                    createOtherMenu(player, 104, "|7|NHẬN thú cưng"
//                                            + "\n\n|1|Nhiệm vụ hiện tại: " + player.nhiemvuchienthan(player.chienthan.tasknow)
//                                            + "\n\n|2|Đã hoàn thành: " + player.chienthan.dalamduoc + "/" + player.chienthan.maxcount
//                                            + "\n|3|Tiến độ: " + player.chienthan.tasknow + "/" + player.chienthan.maxtask + " (Nhiệm vụ)"
//                                            + "\n|7|Đã hoàn thành tất cả nhiệm vụ",
//                                            "NHẬN thú cưng", "Đóng");
//                                }
//                            }
//                        }
//                    } else if (player != null && player.iDMark.getIndexMenu() == 101) {
//                        // Nhận nhiệm vụ tiếp theo
//                        if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0) {
//                            if (select == 0) {
//                                player.chienthan.tasknow++;
//                                if (player.chienthan.tasknow != 0 && player.chienthan.tasknow != player.chienthan.maxcount && player.chienthan.tasknow <= 10) {
//                                    createOtherMenu(player, 103, "|7|NHẬN thú cưng"
//                                            + "\n\n|1|Nhiệm vụ hiện tại: " + player.nhiemvuchienthan(player.chienthan.tasknow)
//                                            + "\n\n|2|Đã hoàn thành: " + player.chienthan.dalamduoc + "/" + player.chienthan.maxcount
//                                            + "\n|3|Tiến độ: " + player.chienthan.tasknow + "/" + player.chienthan.maxtask + " (Nhiệm vụ)",
//                                            "Đóng");
//                                } else if (player.chienthan.tasknow == 10 && player.chienthan.dalamduoc >= player.chienthan.maxcount) {// && player.chienthan.dalamduoc >= player.chienthan.maxcount
//                                    createOtherMenu(player, 104, "|7|NHẬN thú cưng"
//                                            + "\n\n|1|Nhiệm vụ hiện tại: " + player.nhiemvuchienthan(player.chienthan.tasknow)
//                                            + "\n\n|2|Đã hoàn thành: " + player.chienthan.dalamduoc + "/" + player.chienthan.maxcount
//                                            + "\n|3|Tiến độ: " + player.chienthan.tasknow + "/" + player.chienthan.maxtask + " (Nhiệm vụ)"
//                                            + "\n|7|Đã hoàn thành tất cả nhiệm vụ",
//                                            "NHẬN thú cưng", "Đóng");
//                                }
//                            }
//                        }
//                    } else if (player != null && player.iDMark.getIndexMenu() == 104) {
//                        if (select == 0) {
//                            if (player.chienthan.dalamduoc >= 100) {
//                                if (player.chienthan.donechienthan == 0) {
//                                    Input.gI().TAOPET(player);
//                                } else {
//                                    this.npcChat(player, "|7|Bạn đã nhân nuôi 1 bé khỉ rồi mà");
//                                };
//                            } else {
//                                this.npcChat(player, "|7|Bạn chưa tiêu diệt 100 BOSS ");
//                            }
//                        }
//                    }
//                }
//            }
//        };
//    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Npc minuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|7|HỘ TỐNG BUNMA NGOẠI QUỐC"
                            + "\n\n|1|BUNMA đang muốn đi kham phá vùng đất ngọc rồng ngươi hãy giúp ta đưa nàng đến nơi bunma cần \n Ta trao thưởng quà Hậu hĩnh !!"
                            + "\n\n|2|Hôm nay đã hộ tống: " + player.taixiu.hotong + " lần",
                            "Hướng dẫn\n Hộ Tống Bunma", "Hộ Tống", "Shop Mị Nương", "Mua máy dò boss");//, " Shop Xu" ,"Đóng")
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 0) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, 1, "|2|Gặp Npc Mị Nương: Chọn Hộ Tống Rồi Dắt BUNMA đến Vị Trí được chỉ định\n"
                                            + "Phần quà Random 1-30 Thỏi vàng", "Hiểu rồi");
                                    break;
                                case 1:
                                    if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_20_0) {
                                        this.createOtherMenu(player, 12345,
                                                "|7|Bạn có chắc muốn dùng 1 xu để thực hiện hộ không?", "Đồng ý", "Từ chối");
                                    } else {
                                        this.npcChat(player, "|7|Vui lòng làm đến nhiệm vụ Gặp Tiểu đội sát thủ");
                                    }
                                    break;
                                case 2:
                                    ShopServiceNew.gI().opendShop(player, "MI NUONG", true);
                                    break;
                                case 3:
                                    this.createOtherMenu(player, 997,
                                            "|7|Máy dò boss"
                                            + "\n\n|1|Bạn đang có: " + Util.format(player.getSession().vnd) + " vnd"
                                            + "\n\n|2|Máy dò boss thời hạn dung vĩnh viễn "
                                            + "\n\n|5|Giá 20k Bạn có Muốn không?"
                                            + "\n|6|------------~o~------------"
                                            + "\n|7|Lưu ý:bạn phải đủ 1 xu vàng thì mởi sử dụng được tele",
                                            "Mua luôn", "Từ chối");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 997) {
                            switch (select) {
                                case 0:
                                    if (player.getSession().vnd >= 20000) {
                                        PlayerDAO.subvnd(player, 20000);
                                        Item maydoboss = ItemService.gI().createNewItem((short) 1456, 1);
                                        InventoryServiceNew.gI().addItemBag(player, maydoboss);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "|1|bạn đã nhận được máy dò boss vĩnh viễn");

                                    } else {
                                        this.npcChat(player, "|1|Không 20k đủ tiền nạp đi");
                                    }
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 12345) {
                            switch (select) {
                                case 0:
                                    if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_18_0) {

                                        Item xu = null;
                                        xu = InventoryServiceNew.gI().findItemBag(player, 1312);
                                        if (xu != null) {
                                            Boss oldDuongTank = BossManager.gI().getBossById(Util.createIdDuongTank((int) player.id));
                                            if (oldDuongTank != null) {
                                                this.npcChat(player, "|7|Bulma đang được hộ tống " + oldDuongTank.zone.zoneId);
                                            } else if (player.taixiu.hotong >= 10) {
                                                this.npcChat(player, "|7|Mỗi ngày chỉ được Hộ tống tối đa 10 lần");
                                            } else {
                                                BossData bossDataClone = new BossData(
                                                        "Bulma hộ tống by" + player.name,
                                                        (byte) 2,
                                                        new short[]{1396, 1397, 1398, 48, -1, -1},
                                                        100000,
                                                        new double[]{player.nPoint.hpMax * 2},
                                                        new int[]{103},
                                                        new int[][]{
                                                            {Skill.TAI_TAO_NANG_LUONG, 7, 15000}},
                                                        new String[]{}, //text chat 1
                                                        new String[]{}, //text chat 2
                                                        new String[]{}, //text chat 3
                                                        60
                                                );
                                                try {
                                                    //tạo bot
                                                    MiNuong dt = new MiNuong(Util.createIdDuongTank((int) player.id), bossDataClone, player.zone, player.location.x - 20, player.location.y);
                                                    dt.playerTarger = player;
                                                    int[] mapcuoi = {6, 29, 30, 4, 5, 27, 28};
                                                    dt.mapHoTong = mapcuoi[Util.nextInt(mapcuoi.length)];
                                                    player.haveBeQuynh = true;
                                                    player.lastTimeHoTong = System.currentTimeMillis();
                                                } catch (Exception e) {
                                                }
                                                //trừ ruby khi gọi boss
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, xu, 1);
                                                Service.getInstance().sendMoney(player);
                                                break;
                                            }
                                        } else {
                                            this.npcChat(player, "|7|Bạn không đủ xu để hộ tống");
                                        }
                                    } else {
                                        this.npcChat(player, "|7|Vui lòng làm đến nhiệm vụ 18");
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }
/////////////////////////////////////////////////////////////////////////////////////////

    private static Npc TrongTai(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 113) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đại hội võ thuật Siêu Hạng\n(thử nghiệm)\ndiễn ra 24/7 kể cả ngày lễ và chủ nhật\nHãy thi đấu để khẳng định đẳng cấp của mình nhé", "Top 100\nCao thủ\n(thử nghiệm)", "Hướng\ndẫn\nthêm", "Đấu ngay\n(thử nghiệm)", "Về\nĐại Hội\nVõ Thuật");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 113) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    try (Connection con = GirlkunDB.getConnection()) {
                                    Manager.topSieuHang = Manager.realTopSieuHang(con);
                                } catch (Exception ignored) {
                                    Logger.error("Lỗi đọc top");
                                }
                                Service.gI().showListTop(player, Manager.topSieuHang, (byte) 1);
                                break;
                                case 2:
                                    List<TOP> tops = new ArrayList<>();
                                    tops.addAll(Manager.realTopSieuHang(player));
                                    Service.gI().showListTop(player, tops, (byte) 1);
                                    tops.clear();
                                    break;
                                case 3:
                                    ChangeMapService.gI().changeMapNonSpaceship(player, 52, -1, 432);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc thuongDe(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 45) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "|7| Thu thập đủ  logo đến đây nhận quà nhé", "Đến Kaio", "Nhận SKH\nThượng Đế", "Shop Thượng\nĐế");
                    }
                    if (this.mapId == 129 || this.mapId == 141) {
                        this.createOtherMenu(player, 0,
                                "Con muốn gì nào?", "Quay về");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 129) {
                        switch (select) {
                            case 0: // quay ve
                                ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, 354);
                                break;
                        }
                    }
                    if (this.mapId == 141) {
                        switch (select) {
                            case 0: // quay ve
                                ChangeMapService.gI().changeMapBySpaceShip(player, 48, -1, 354);
                                break;
                        }
                    }
                    if (this.mapId == 45) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 48, -1, 354);
                                    break;
                                case 1:
                                    Item logo = InventoryServiceNew.gI().findItemBag(player, 1854);

                                    if (logo != null && logo.quantity >= 2000) {
                                        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 5) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, logo, 2000);
                                            if (player.gender == 0) {
//                                        int[] itemskh = {0, 6, 21, 27, 12};
                                                int[] ao = {0};//áo 
                                                int[] quan = {6};// quần
                                                int[] gang = {21};// găng
                                                int[] giay = {27};//giày 
                                                int[] rada = {12};// rada
                                                int randomct = new Random().nextInt(ao.length);
                                                Item ao1 = ItemService.gI().createNewItem((short) ao[randomct]);
                                                Item quan1 = ItemService.gI().createNewItem((short) quan[randomct]);
                                                Item gang1 = ItemService.gI().createNewItem((short) gang[randomct]);
                                                Item giay1 = ItemService.gI().createNewItem((short) giay[randomct]);
                                                Item rada1 = ItemService.gI().createNewItem((short) rada[randomct]);
                                                //áo
                                                ao1.itemOptions.add(new Item.ItemOption(47, Util.nextInt(1, 30)));
                                                ao1.itemOptions.add(new Item.ItemOption(211, 1));
                                                ao1.itemOptions.add(new Item.ItemOption(214, 1));
                                                //quần
                                                quan1.itemOptions.add(new Item.ItemOption(6, Util.nextInt(5, 30)));
                                                quan1.itemOptions.add(new Item.ItemOption(211, 1));
                                                quan1.itemOptions.add(new Item.ItemOption(214, 1));
                                                ///găng
                                                gang1.itemOptions.add(new Item.ItemOption(0, Util.nextInt(5, 30)));
                                                gang1.itemOptions.add(new Item.ItemOption(211, 1));
                                                gang1.itemOptions.add(new Item.ItemOption(214, 1));
                                                //giày
                                                giay1.itemOptions.add(new Item.ItemOption(7, Util.nextInt(5, 30)));
                                                giay1.itemOptions.add(new Item.ItemOption(211, 1));
                                                giay1.itemOptions.add(new Item.ItemOption(214, 1));
                                                //rada
                                                rada1.itemOptions.add(new Item.ItemOption(14, Util.nextInt(1, 3)));
                                                rada1.itemOptions.add(new Item.ItemOption(211, 1));
                                                rada1.itemOptions.add(new Item.ItemOption(214, 1));
                                                InventoryServiceNew.gI().addItemBag(player, ao1);
                                                InventoryServiceNew.gI().addItemBag(player, quan1);
                                                InventoryServiceNew.gI().addItemBag(player, gang1);
                                                InventoryServiceNew.gI().addItemBag(player, giay1);
                                                InventoryServiceNew.gI().addItemBag(player, rada1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + ao1.template.name);
                                                Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + quan1.template.name);
                                                Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + gang1.template.name);
                                                Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + giay1.template.name);
                                                Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + rada1.template.name);
                                                break;
                                            } else if (player.gender == 1) {
                                                int[] ao = {1};//áo 
                                                int[] quan = {7};// quần
                                                int[] gang = {22};// găng
                                                int[] giay = {28};//giày 
                                                int[] rada = {12};// rada
                                                int randomct = new Random().nextInt(ao.length);
                                                Item ao1 = ItemService.gI().createNewItem((short) ao[randomct]);
                                                Item quan1 = ItemService.gI().createNewItem((short) quan[randomct]);
                                                Item gang1 = ItemService.gI().createNewItem((short) gang[randomct]);
                                                Item giay1 = ItemService.gI().createNewItem((short) giay[randomct]);
                                                Item rada1 = ItemService.gI().createNewItem((short) rada[randomct]);
                                                //áo
                                                ao1.itemOptions.add(new Item.ItemOption(47, Util.nextInt(1, 30)));
                                                ao1.itemOptions.add(new Item.ItemOption(211, 1));
                                                ao1.itemOptions.add(new Item.ItemOption(214, 1));
                                                //quần
                                                quan1.itemOptions.add(new Item.ItemOption(6, Util.nextInt(5, 30)));
                                                quan1.itemOptions.add(new Item.ItemOption(211, 1));
                                                quan1.itemOptions.add(new Item.ItemOption(214, 1));
                                                ///găng
                                                gang1.itemOptions.add(new Item.ItemOption(0, Util.nextInt(5, 30)));
                                                gang1.itemOptions.add(new Item.ItemOption(211, 1));
                                                gang1.itemOptions.add(new Item.ItemOption(214, 1));
                                                //giày
                                                giay1.itemOptions.add(new Item.ItemOption(7, Util.nextInt(5, 30)));
                                                giay1.itemOptions.add(new Item.ItemOption(211, 1));
                                                giay1.itemOptions.add(new Item.ItemOption(214, 1));
                                                //rada
                                                rada1.itemOptions.add(new Item.ItemOption(14, Util.nextInt(1, 3)));
                                                rada1.itemOptions.add(new Item.ItemOption(211, 1));
                                                rada1.itemOptions.add(new Item.ItemOption(214, 1));
                                                InventoryServiceNew.gI().addItemBag(player, ao1);
                                                InventoryServiceNew.gI().addItemBag(player, quan1);
                                                InventoryServiceNew.gI().addItemBag(player, gang1);
                                                InventoryServiceNew.gI().addItemBag(player, giay1);
                                                InventoryServiceNew.gI().addItemBag(player, rada1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + ao1.template.name);
                                                Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + quan1.template.name);
                                                Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + gang1.template.name);
                                                Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + giay1.template.name);
                                                Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + rada1.template.name);
                                                break;
                                            } else if (player.gender == 2) {
                                                int[] itemskh = {2, 8, 23, 29, 12};
                                                int[] ao = {2};//áo 
                                                int[] quan = {8};// quần
                                                int[] gang = {23};// găng
                                                int[] giay = {29};//giày 
                                                int[] rada = {12};// rada
                                                int randomct = new Random().nextInt(ao.length);
                                                Item ao1 = ItemService.gI().createNewItem((short) ao[randomct]);
                                                Item quan1 = ItemService.gI().createNewItem((short) quan[randomct]);
                                                Item gang1 = ItemService.gI().createNewItem((short) gang[randomct]);
                                                Item giay1 = ItemService.gI().createNewItem((short) giay[randomct]);
                                                Item rada1 = ItemService.gI().createNewItem((short) rada[randomct]);
                                                //áo
                                                ao1.itemOptions.add(new Item.ItemOption(47, Util.nextInt(1, 30)));
                                                ao1.itemOptions.add(new Item.ItemOption(211, 1));
                                                ao1.itemOptions.add(new Item.ItemOption(214, 1));
                                                //quần
                                                quan1.itemOptions.add(new Item.ItemOption(6, Util.nextInt(5, 30)));
                                                quan1.itemOptions.add(new Item.ItemOption(211, 1));
                                                quan1.itemOptions.add(new Item.ItemOption(214, 1));
                                                ///găng
                                                gang1.itemOptions.add(new Item.ItemOption(0, Util.nextInt(5, 30)));
                                                gang1.itemOptions.add(new Item.ItemOption(211, 1));
                                                gang1.itemOptions.add(new Item.ItemOption(214, 1));
                                                //giày
                                                giay1.itemOptions.add(new Item.ItemOption(7, Util.nextInt(5, 30)));
                                                giay1.itemOptions.add(new Item.ItemOption(211, 1));
                                                giay1.itemOptions.add(new Item.ItemOption(214, 1));
                                                //rada
                                                rada1.itemOptions.add(new Item.ItemOption(14, Util.nextInt(1, 3)));
                                                rada1.itemOptions.add(new Item.ItemOption(211, 1));
                                                rada1.itemOptions.add(new Item.ItemOption(214, 1));
                                                InventoryServiceNew.gI().addItemBag(player, ao1);
                                                InventoryServiceNew.gI().addItemBag(player, quan1);
                                                InventoryServiceNew.gI().addItemBag(player, gang1);
                                                InventoryServiceNew.gI().addItemBag(player, giay1);
                                                InventoryServiceNew.gI().addItemBag(player, rada1);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + ao1.template.name);
                                                Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + quan1.template.name);
                                                Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + gang1.template.name);
                                                Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + giay1.template.name);
                                                Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + rada1.template.name);
                                                break;
                                            } else {

                                            }
                                        } else {
                                            this.npcChat(player, "|1|Chừa lại 5 ồ hành trang trống đi");
                                        }
                                        break;
                                    } else {
                                        this.npcChat(player, "|7|KHông đủ x2.000 logo");
                                    }
                                    break;
                                case 2:
                                    ShopServiceNew.gI().opendShop(player, "Shop_thuongde", true);
                                    break;
                            }

                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHOOSE_LUCKY_ROUND) {
                            switch (select) {
                                case 0:
                                    LuckyRound.gI().openCrackBallUI(player, LuckyRound.USING_GOLD);
                                    break;
                                case 1:
                                    ShopServiceNew.gI().opendShop(player, "ITEMS_LUCKY_ROUND", true);
                                    break;
                                case 2:
                                    NpcService.gI().createMenuConMeo(player,
                                            ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND, this.avartar,
                                            "Con có chắc muốn xóa hết vật phẩm trong rương phụ? Sau khi xóa "
                                            + "sẽ không thể khôi phục!",
                                            "Đồng ý", "Hủy bỏ");
                                    break;
                            }
                        }
                    }

                }
            }
        };
    }

    public static Npc thanVuTru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 48 || this.mapId == 0) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con muốn làm gì nào", "Di chuyển");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 48 || this.mapId == 0) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.MENU_DI_CHUYEN,
                                            "Con muốn đi đâu?", "Về\nthần điện", "Thánh địa\nKaio", "Con\nđường\nrắn độc", "Từ chối");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DI_CHUYEN) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 45, -1, 354);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMap(player, 141, -1, 318, 336);//con đường rắn độc
                                    break;
                            }
                        }
                    }
                }
            }

        };
    }

    public static Npc duongtank(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "|7|Con muốn làm gì nào", "Di chuyển");
                    }
                    if (this.mapId == 123 || this.mapId == 159) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "|7|Chưa gì đã muốn về nhà rồi", "Đúng vậy");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.MENU_DI_CHUYEN,
                                            "|7|Con muốn đi đâu?"
                                            + "\n|7|Map Ngũ Hành Sơn x4 Tnsm"
                                            + "\n|7|LƯU Ý: Chỉ hỗ trợ cho tân thủ dưới 80 tỷ", "Map Ngũ\nHành Sơn", "Từ chối");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DI_CHUYEN) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 123, -1, 354);
                                    break;

                            }
                        }
                    }
                    if (this.mapId == 123 || this.mapId == 159) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, 354);
                                    break;
                            }
                        }
                    }
                }

            }
        };
    }

    public static Npc Toppo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.playerTask.taskMain.id >= 20) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Con muốn làm gì nào", "Di chuyển");
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "|7|Người quá yếu không thể đi đến map mới được"
                                    + "\n|7|Hãy hoàn thành nhiêm vụ 20 người có thể qua");
                        }
                    }
                    if (this.mapId == 171) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con muốn làm gì nào", "Di chuyển");
                    }
                    if (this.mapId == 169) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con muốn làm gì nào", "Di chuyển");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.MENU_DI_CHUYEN,
                                            "Con muốn đi đâu?", "Đến Tiên Môn", "Đến Chiến Trường");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DI_CHUYEN) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 171, -1, 384);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 169, -1, 384);
                                    break;
                            }
                        }
                    }
                    if (this.mapId == 171) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.MENU_DI_CHUYEN,
                                            "Con muốn đi đâu?", "Về nhà");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DI_CHUYEN) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 384);
                                    break;

                            }
                        }
                    }
                    if (this.mapId == 169) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.MENU_DI_CHUYEN,
                                            "Con muốn đi đâu?", "Về nhà");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DI_CHUYEN) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 384);
                                    break;

                            }
                        }
                    }
                }
            }

        };
    }

    public static Npc rongomege1(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "|7|Hãy chọn các loại thức tỉnh dưới đây", "Thức Tỉnh\nĐệ Tử", "Thức tỉnh\nBản Thân");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, 123,
                                            "|1|Thu thập đủ x99 đá tiến hóa"
                                            + "\n|1|20 viên đá ngũ sắc"
                                            + "\n|1|3 viên ngọc rồng siêu cấp"
                                            + "\n|7|LƯU Ý: TỈ LỆ THỨC TỈNH THÀNH CÔNG LÀ 50% VÀ KHI THỨC TỈNH TĂNG THÊM 10% CHỈ SỐ",
                                            "Đồng ý", "Từ chối");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 123) {
                            Item dalv2 = InventoryServiceNew.gI().findItemBag(player, 1517);
                            Item dns = InventoryServiceNew.gI().findItemBag(player, 674);
                            Item nrosieucap = InventoryServiceNew.gI().findItemBag(player, 1015);
                            int gender = player.pet.gender;
                            switch (select) {
                                case 0:
                                    if (player.pet.typePet <= 5) {
                                        if (dalv2 != null && dns != null && nrosieucap != null && dalv2.quantity >= 99 && dns.quantity >= 20
                                                && nrosieucap.quantity >= 3) {
                                            if (Util.isTrue(20, 100)) {
                                                if (player.pet.typePet == 1) {
                                                    PetService.gI().changemabulv2(player, gender);
                                                } else if (player.pet.typePet == 2) {
                                                    PetService.gI().changhuydietlv2(player, gender);
                                                } else if (player.pet.typePet == 3) {
                                                    PetService.gI().changsuperlv2(player, gender);
                                                } else if (player.pet.typePet == 4) {
                                                    PetService.gI().changwukonglv2(player, gender);
                                                } else if (player.pet.typePet == 5) {
                                                    PetService.gI().changefusionlv2(player, gender);
                                                }
                                                Service.gI().sendThongBao(player, "|7|Thức tỉnh đệ tử thành công"
                                                );
                                            } else {
                                                Service.gI().sendThongBao(player, "|7|Thức tỉnh thất bại");
                                            }
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 20);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dalv2, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, nrosieucap, 3);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            GoiRongXuong.gI().activeRongXuong(player, true);
                                        } else {
                                            this.npcChat(player, "|7|Bạn không đủ nguyên liệu để thức tỉnh đệ tử");
                                        }
                                    } else {
                                        this.npcChat(player, "|7|Đệ tử của bạn đã thức tỉnh rồi");
                                    }
                                    break;
                                case 1:

                                    break;

                            }
                        }
                    }
                }
            }

        };
    }

    public static Npc EVENT(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (Manager.SUKIEN == 7) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "|7|Hiện tại đang có sự kiện Hallowen", "Nhận Quà", "Đổi Quà", "Thoát");
                        } else if (Manager.SUKIEN == 6) {
                            this.createOtherMenu(player, 123, "|7|Hiện tại đang có sự kiện Giáng Sinh"
                                    + "\nThu thâp x99 kẹo tới dây đổi tất giang sinh với ta", "OK", "Thôi");
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "|7|Hiện tại chưa có sự kiện gì cả");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            handleBaseMenu(player, select);
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.NHAN_QUA_MENU) {
                            handleNhanQuaMenu(player, select);
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.DOI_QUA_MENU) {
                            handleDoiQuaMenu(player, select);
                        } else if (player.iDMark.getIndexMenu() == 123) {
                            switch (select) {
                                case 0:
                                    Item keo = InventoryServiceNew.gI().findItemBag(player, 533);
                                    if (keo != null && keo.quantity >= 99) {
                                        Item tat = ItemService.gI().createNewItem((short) 649, 1);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, keo, 99);
                                        InventoryServiceNew.gI().addItemBag(player, tat);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "|7|Đổi quà thành công");
                                    } else {
                                        this.npcChat(player, "|7|Không kẹo đủ để quy đổi");
                                    }
                                    break;
                            }
                        }
                    }
                }
            }

            private void handleBaseMenu(Player player, int select) {
                switch (select) {
                    case 0:
                        this.createOtherMenu(player, ConstNpc.NHAN_QUA_MENU,
                                "|7|1.Hãy dùng giỏ kẹo để thu thập kẹo \n2.Thu thâp đủ rồi quay lại đổi với ta", "Nhận Giỏ Kẹo", "Nhận Giỏ Kẹo Đầy", "Thoát");
                        break;
                    case 1:
                        this.createOtherMenu(player, ConstNpc.DOI_QUA_MENU,
                                "Ngươi muốn đổi cái gì:\n|7|1.Cải trang cần x99 giỏ kẹo \n2.Linh Thú cần x99 giỏ kẹo\n3.Pet cần x99 giỏ kẹo \n4.Đệ Tử Cần X99 Giỏ Kẹo", "Cải Trang", "Linh Thú", "VPDL", "Đệ Tử");
                        break;
                }
            }

            private void handleNhanQuaMenu(Player player, int select) {
                switch (select) {
                    case 0:
                        handleDoiGioKho(player);
                        break;
                    case 1:
                        handleDoiGioKhoDay(player);
                        break;
                }
            }

            private void handleDoiQuaMenu(Player player, int select) {
                switch (select) {
                    case 0:
                        handleDoiCaiTrang(player);
                        break;
                    case 1:
                        handleDoiLinhThu(player);
                        break;
                    case 2:
                        handleDoiPet(player);
                        break;
                    case 3:
                        handleDoiDeTu(player);
                        break;
                }
            }

            // Các phương thức xử lý cụ thể
            private void handleDoiGioKho(Player player) {
                Item thoivang = null;

                try {
                    thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
                } catch (Exception e) {
                    // Xử lý ngoại lệ tại đây nếu cần
                }

                if (thoivang == null) {
                    this.npcChat(player, "|1|Bạn không đủ thỏi vàng để đổi");
                } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                    this.npcChat(player, "|1|Hành trang của bạn không đủ chỗ trống");
                } else {
                    // Kiểm tra nếu người chơi đã đổi trong ngày
                    Instant diemdanhInstant = Instant.ofEpochMilli(player.diemdanh1);
                    LocalDate diemdanhDate = LocalDateTime.ofInstant(diemdanhInstant, ZoneId.systemDefault()).toLocalDate();
                    LocalDate currentDate = LocalDate.now();

                    if (diemdanhDate.isEqual(currentDate)) {
                        this.npcChat(player, "|1|Hôm nay đã nhận rồi mà !!!");
                    } else {
                        // Thực hiện đổi giỏ kẹo
                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 1);
                        Item nhangiokeo = ItemService.gI().createNewItem((short) 1990, 5);
                        InventoryServiceNew.gI().addItemBag(player, nhangiokeo);
                        InventoryServiceNew.gI().sendItemBags(player);

                        // Cập nhật điểm danh
                        player.diemdanh1 = System.currentTimeMillis();

                        // Gửi thông báo cho người chơi
                        this.npcChat(player, "|1|Bạn nhận được giỏ kẹo");
                    }
                }
            }

            private void handleDoiGioKhoDay(Player player) {
                Item item1 = null;
                Item item2 = null;
                Item item3 = null;
                Item item4 = null;

                try {
                    item1 = InventoryServiceNew.gI().findItemBag(player, 1995);
                    item2 = InventoryServiceNew.gI().findItemBag(player, 1996);
                    item3 = InventoryServiceNew.gI().findItemBag(player, 1997);
                    item4 = InventoryServiceNew.gI().findItemBag(player, 1992);
                } catch (Exception e) {
                    // Xử lý ngoại lệ tại đây nếu cần
                }

                if ((item1 == null || item1.quantity < 99)
                        || (item2 == null || item2.quantity < 99)
                        || (item3 == null || item3.quantity < 99)
                        || (item4 == null || item4.quantity < 99)) {
                    this.npcChat(player, "|1|Bạn không đủ x99 kẹo để quy đổi");
                } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                    this.npcChat(player, "|1|Hành trang của bạn không đủ chỗ trống");
                } else {
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item1, 99);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item2, 99);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item3, 99);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item4, 99);
                    Item nhangiokeoday = ItemService.gI().createNewItem((short) 1989);
                    InventoryServiceNew.gI().addItemBag(player, nhangiokeoday);
                    InventoryServiceNew.gI().sendItemBags(player);
                    this.npcChat(player, "|1|Bạn nhận được giỏ kẹo");
                }
            }

            private void handleDoiCaiTrang(Player player) {
                Item giokeoday = null;
                try {
                    giokeoday = InventoryServiceNew.gI().findItemBag(player, 1989);
                } catch (Exception e) {
                    // Xử lý ngoại lệ tại đây nếu cần
                }

                if (giokeoday == null || giokeoday.quantity < 99) {
                    this.npcChat(player, "|1|Bạn không đủ x99 giỏ kẹo đầy");
                } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                    this.npcChat(player, "|1|Hành trang của bạn không đủ chỗ trống");
                } else {
                    int[] caitrang = new int[]{1440};
                    int[] randomoption = new int[]{5, 14, 94, 108, 5, 216};
                    int randomct = new Random().nextInt(caitrang.length);
                    int randomop = new Random().nextInt(randomoption.length);
                    Item createcaitrang = ItemService.gI().createNewItem((short) caitrang[randomct]);
                    createcaitrang.itemOptions.add(new Item.ItemOption(50, Util.nextInt(20, 40)));
                    createcaitrang.itemOptions.add(new Item.ItemOption(77, Util.nextInt(20, 40)));
                    createcaitrang.itemOptions.add(new Item.ItemOption(103, Util.nextInt(20, 40)));
                    createcaitrang.itemOptions.add(new Item.ItemOption(5, Util.nextInt(1, 20)));
                    createcaitrang.itemOptions.add(new Item.ItemOption(216, Util.nextInt(1, 50))); // bât tử
                    InventoryServiceNew.gI().subQuantityItemsBag(player, giokeoday, 99); //trừ giỏ kẹo
                    InventoryServiceNew.gI().addItemBag(player, createcaitrang);
                    InventoryServiceNew.gI().sendItemBags(player);
                    this.npcChat(player, "|1|chức mừng bạn đã nhận được 1 cải trang");

                }
            }
            //// linh thú

            private void handleDoiLinhThu(Player player) {
                Item giokeoday = null;
                try {
                    giokeoday = InventoryServiceNew.gI().findItemBag(player, 1989);
                } catch (Exception e) {
                    // Xử lý ngoại lệ tại đây nếu cần
                }

                if (giokeoday == null || giokeoday.quantity < 99) {
                    this.npcChat(player, "|1|Bạn không đủ x99 giỏ kẹo đầy");
                } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                    this.npcChat(player, "|1|Hành trang của bạn không đủ chỗ trống");
                } else {
                    // Xử lý nhận cải trang
                    int[] caitranggohanxacuop = new int[]{1619, 1620};
                    int[] randomoption = new int[]{5, 14, 94, 108, 5, 216};
                    int randomct = new Random().nextInt(caitranggohanxacuop.length);
                    int randomop = new Random().nextInt(randomoption.length);
                    Item linhthu = ItemService.gI().createNewItem((short) caitranggohanxacuop[randomct]);
                    linhthu.itemOptions.add(new Item.ItemOption(50, Util.nextInt(10, 30)));
                    linhthu.itemOptions.add(new Item.ItemOption(77, Util.nextInt(10, 30)));
                    linhthu.itemOptions.add(new Item.ItemOption(103, Util.nextInt(10, 30)));
                    linhthu.itemOptions.add(new Item.ItemOption(5, Util.nextInt(1, 10))); // chí mạng
                    InventoryServiceNew.gI().subQuantityItemsBag(player, giokeoday, 99);
                    InventoryServiceNew.gI().addItemBag(player, linhthu);
                    InventoryServiceNew.gI().sendItemBags(player);
                    this.npcChat(player, "|1|chức mừng bạn đã nhận được 1 linh thú");

                }
            }
            //vật phẩm đeo lưng

            private void handleDoiPet(Player player) {
                Item giokeoday = null;
                try {
                    giokeoday = InventoryServiceNew.gI().findItemBag(player, 1989);
                } catch (Exception e) {
                    // Xử lý ngoại lệ tại đây nếu cần
                }

                if (giokeoday == null || giokeoday.quantity < 99) {
                    this.npcChat(player, "|1|Bạn không đủ x99 giỏ kẹo đầy");
                } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                    this.npcChat(player, "|1|Hành trang của bạn không đủ chỗ trống");
                } else {
                    // Xử lý nhận cải trang
                    int[] caitranggohanxacuop = new int[]{1998, 1999};
                    int[] randomoption = new int[]{5, 14, 94, 108, 5, 216};
                    int randomct = new Random().nextInt(caitranggohanxacuop.length);
                    int randomop = new Random().nextInt(randomoption.length);
                    Item linhthu = ItemService.gI().createNewItem((short) caitranggohanxacuop[randomct]);
                    linhthu.itemOptions.add(new Item.ItemOption(50, Util.nextInt(10, 30)));
                    linhthu.itemOptions.add(new Item.ItemOption(77, Util.nextInt(10, 30)));
                    linhthu.itemOptions.add(new Item.ItemOption(103, Util.nextInt(10, 30)));
                    linhthu.itemOptions.add(new Item.ItemOption(5, Util.nextInt(1, 10))); // chí mạng
                    InventoryServiceNew.gI().subQuantityItemsBag(player, giokeoday, 99);
                    InventoryServiceNew.gI().addItemBag(player, linhthu);
                    InventoryServiceNew.gI().sendItemBags(player);
                    this.npcChat(player, "|1|chức mừng bạn đã nhận được 1 vật phẩm đeo lưng");

                }
            }

            private void handleDoiDeTu(Player player) {
                Item giokeoday = null;
                try {
                    giokeoday = InventoryServiceNew.gI().findItemBag(player, 1989);
                } catch (Exception e) {
                    // Xử lý ngoại lệ tại đây nếu cần
                }

                if (giokeoday == null || giokeoday.quantity < 99) {
                    this.npcChat(player, "|1|Bạn không đủ x99 giỏ kẹo đầy");
                } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                    this.npcChat(player, "|1|Hành trang của bạn không đủ chỗ trống");
                } else {
                    if (InventoryServiceNew.gI().getCountEmptyBody(player.pet) == 8) {
                        if (player.pet != null) {
                            int gender = player.pet.gender;
                            PetService.gI().changegohanzombie(player, gender);
                            InventoryServiceNew.gI().subQuantityItemsBag(player, giokeoday, 99);
                            InventoryServiceNew.gI().sendItemBags(player);
                            this.npcChat(player, "|1|chức mừng bạn đã nhận được Đệ Tử Gohan Zombie");
                        } else {
                            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Vui lòng tháo hết đồ đệ tử");
                    }
                }
            }
        };
    }

    public static Npc kibit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Đến\nKaio", "Từ chối");
                    }
                    if (this.mapId == 114) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc GOGETASSJ4(int mapId, int status, int cx, int cy, int tempId, int avartar) {

        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?"
                                + "\n|1|Bạn có thể lưa chọn sử dụng điểm săn boss hoặc đồng xu vàng để quy đổi vật phẩm hiếm",
                                "Đổi\nXu Gold", "Đổi\nĐiểm", "Nhận\nThú Cưng");
                    }
                }
            }

            @Override

            public void confirmMenu(Player player, int select) {
                Item dongxu = InventoryServiceNew.gI().findItemBag(player, 1529);
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {

                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, 123,
                                            "|7|Bạn muốn dùng xu để quy đổi thứ gì"
                                            + "\n|1|Hiện tại người đang có " + (dongxu == null ? "0" : dongxu.quantity) + " xu\n",
                                            "x99 Đá Cường Hóa\nTốn 50 xu", "x99 Nước Phép Thuật\nTốn 50 xu", "x99 Đá Ngũ Sắc \nTốn 99 xu",
                                            "x99 Mãnh Ấn \nTốn 50 xu", "x99 Đá Tiến Hóa\nTốn 50 xu");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, 1231, ""
                                            + "|5|Dùng điểm săn boss để quy đổi các vật phẩm dưới đây"
                                            + "\n|7|Tỉ lệ quỷ đổi 1 : 1"
                                            + "\n|7|Hiện tại người đang có " + player.pointsb + " Điểm",
                                            "Đá\nCường Hóa", "Nước\nPhép Thuật", "Đá\nNgũ Sắc", "Mãnh Ấn");
                                    break;
                                case 2:
                                    this.createOtherMenu(player, 1232, ""
                                            + "|5|Bạn có muốn đổi x999 điểm săn boss để nhận thú cưng hay không?"
                                            + "\n|7|Hiện tại người đang có " + player.pointsb + " Điểm",
                                            "Có", "Không");
                                    break;

                            }
                        } else if (player.iDMark.getIndexMenu() == 1231) {
                            switch (select) {
                                case 0:
                                    Input.gI().quydoidiemboss1(player);
                                    break;
                                case 1:
                                    Input.gI().quydoidiemboss2(player);
                                    break;
                                case 2:
                                    Input.gI().quydoidiemboss3(player);
                                    break;
                                case 3:
                                    Input.gI().quydoidiemboss4(player);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 1232) {
                            switch (select) {
                                case 0:
                                    if (player.inventory.event >= 999) {

                                    } else {
                                        this.npcChat(player, "|7|Bạn không đủ điểm để nhận thú cưng");
                                    }
                                    break;

                            }
                        } else if (player.iDMark.getIndexMenu() == 123) {
                            switch (select) {
                                case 0:
                                    if (dongxu != null && dongxu.quantity >= 50) {
                                        Item dacuonghoa = ItemService.gI().createNewItem((short) 1518, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, dongxu, 50);

                                        InventoryServiceNew.gI().addItemBag(player, dacuonghoa);
                                        InventoryServiceNew.gI().sendItemBags(player);

                                    } else {
                                        this.npcChat(player, "|7|Không đủ xu để quy đổi");
                                    }
                                    break;
                                case 1:
                                    if (dongxu != null && dongxu.quantity >= 50) {
                                        Item dacuonghoa = ItemService.gI().createNewItem((short) 1519, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, dongxu, 50);
                                        InventoryServiceNew.gI().addItemBag(player, dacuonghoa);

                                        InventoryServiceNew.gI().sendItemBags(player);
                                    } else {
                                        this.npcChat(player, "|7|Không đủ xu để quy đổi");
                                    }
                                    break;
                                case 2:
                                    if (dongxu != null && dongxu.quantity >= 50) {
                                        Item dacuonghoa = ItemService.gI().createNewItem((short) 674, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, dongxu, 99);
                                        InventoryServiceNew.gI().addItemBag(player, dacuonghoa);

                                        InventoryServiceNew.gI().sendItemBags(player);
                                    } else {
                                        this.npcChat(player, "|7|Không đủ xu để quy đổi");
                                    }
                                    break;
                                case 3:
                                    if (dongxu != null && dongxu.quantity >= 50) {
                                        Item dacuonghoa = ItemService.gI().createNewItem((short) 1232, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, dongxu, 50);
                                        InventoryServiceNew.gI().addItemBag(player, dacuonghoa);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    } else {
                                        this.npcChat(player, "|7|Không đủ xu để quy đổi");
                                    }
                                    break;
                                case 4:
                                    if (dongxu != null && dongxu.quantity >= 50) {
                                        Item dacuonghoa = ItemService.gI().createNewItem((short) 1517, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, dongxu, 50);
                                        InventoryServiceNew.gI().addItemBag(player, dacuonghoa);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    } else {
                                        this.npcChat(player, "|7|Không đủ xu để quy đổi");
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc giuma(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 6 || this.mapId == 25 || this.mapId == 26) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Gô Tên, Calich và Monaka đang gặp chuyện ở hành tinh Potaufeu \n Hãy đến đó ngay", "Đến \nPotaufeu");
                    } else if (this.mapId == 139) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Người muốn trở về?", "Quay về", "Từ chối");
                    }//lãnh địa bang
                    else if (this.mapId == 153) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Theo ta, ta sẽ đưa ngươi đến Khu vực Thánh địa\nNơi đây ngươi sẽ truy tìm mảnh bông tai cấp 2 và Hồn bông tai để mở chỉ số Bông tai Cấp 3."
                                + "\n|7|Ngươi có muốn đến đó không?", "Đến\nThánh địa", "Từ chối");
                    } else if (this.mapId == 156) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Người muốn trở về?", "Quay về", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                //đến potaufeu
                                ChangeMapService.gI().goToPotaufeu(player);
                            }
                        }
                    } else if (this.mapId == 139) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                //về trạm vũ trụ
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 24 + player.gender, -1, -1);
                                    break;
                            }
                        }
                    } else if (this.mapId == 153) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                //lãnh địa bang
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 156, -1, -1);
                                    break;
                            }
                        }
                    } else if (this.mapId == 156) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                //về trạm vũ trụ
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 21 + player.gender, -1, -1);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc osin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Đến\nKaio", "Đến\nhành tinh\nBill", "Từ chối");
                    } else if (this.mapId == 154) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?"
                                + "\n|7| Để đến được Hành tinh ngục tù và farm quái ra Mảnh Thiên sứ"
                                + "\n|6| Tiềm năng sức mạnh sẽ tăng x5 lần"
                                + "\n Yêu cầu : Mang 5 món Hủy diệt lên người",
                                "Về thánh địa", "Đến\nhành tinh\nngục tù", "Từ chối");
                    } else if (this.mapId == 155) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Quay về", "Từ chối");
                    } else if (this.mapId == 52) {
                        try {
                            MapMaBu.gI().setTimeJoinMapMaBu();
                            if (this.mapId == 52) {
                                long now = System.currentTimeMillis();
                                if (now > MapMaBu.TIME_OPEN_MABU && now < MapMaBu.TIME_CLOSE_MABU) {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPEN_MMB, "Đại chiến Ma Bư đã mở, "
                                            + "ngươi có muốn tham gia không?",
                                            "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_MMB,
                                            "Ta có thể giúp gì cho ngươi?", "Hướng dẫn", "Từ chối");
                                }

                            }
                        } catch (Exception ex) {
                            Logger.error("Lỗi mở menu osin");
                        }

                    } else if (this.mapId >= 114 && this.mapId < 120 && this.mapId != 116) {
                        if (player.fightMabu.pointMabu >= player.fightMabu.POINT_MAX) {
                            this.createOtherMenu(player, ConstNpc.GO_UPSTAIRS_MENU, "Ta có thể giúp gì cho ngươi ?",
                                    "Lên Tầng!", "Quay về", "Từ chối");
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                    "Quay về", "Từ chối");
                        }
                    } else if (this.mapId == 120 || this.mapId == 170) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Quay về", "Từ chối");
                    } else {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                                    break;
                            }
                        }
                    } else if (this.mapId == 154) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                    break;
                                case 1:
                                    if (player.setClothes.setDHD == 5) {
                                        ChangeMapService.gI().changeMap(player, 155, -1, 111, 792);
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Yêu cầu mang đủ 5 món đồ Hủy diệt", "Đóng");
                                    }
                                    break;
                            }
                        }
                    } else if (this.mapId == 155) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                            }
                        }
                    } else if (this.mapId == 52) {
                        switch (player.iDMark.getIndexMenu()) {
                            case ConstNpc.MENU_REWARD_MMB:
                                break;
                            case ConstNpc.MENU_OPEN_MMB:
                                if (select == 0) {
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MAP_MA_BU);
                                } else if (select == 1) {
//                                    if (!player.getSession().actived) {
//                                        Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");
//                                    } else
                                    ChangeMapService.gI().changeMap(player, 114, -1, 318, 336);
                                }
                                break;
                            case ConstNpc.MENU_NOT_OPEN_BDW:
                                if (select == 0) {
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MAP_MA_BU);
                                }
                                break;
                        }
                    } else if (this.mapId >= 114 && this.mapId < 120 && this.mapId != 116) {
                        if (player.iDMark.getIndexMenu() == ConstNpc.GO_UPSTAIRS_MENU) {
                            if (select == 0) {
                                player.fightMabu.clear();
                                ChangeMapService.gI().changeMap(player, this.map.mapIdNextMabu((short) this.mapId), -1, this.cx, this.cy);
                            } else if (select == 1) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            }
                        } else {
                            if (select == 0) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            }
                        }
                    } else if (this.mapId == 120 || this.mapId == 170) {
                        if (player.iDMark.getIndexMenu() == ConstNpc.BASE_MENU) {
                            if (select == 0) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc docNhan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.clan == null) {
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Chỉ tiếp các bang hội, miễn tiếp khách vãng lai", "Đóng");
                        return;
                    }
                    if (player.clan.doanhTrai_haveGone) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Ta đã thả ngọc rồng ở tất cả các map,mau đi nhặt đi. Hẹn ngươi quay lại vào ngày mai", "OK");
                        return;
                    }

                    boolean flag = true;
                    for (Mob mob : player.zone.mobs) {
                        if (!mob.isDie()) {
                            flag = false;
                        }
                    }
                    for (Player boss : player.zone.getBosses()) {
                        if (!boss.isDie()) {
                            flag = false;
                        }
                    }

                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_JOIN_DOANH_TRAI:
                            if (select == 0) {
                                DoanhTraiService.gI().joinDoanhTrai(player);
                            } else if (select == 2) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                            }
                            break;
                        case ConstNpc.IGNORE_MENU:
                            if (select == 1) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc linhCanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.clan == null) {
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Chỉ tiếp các bang hội, miễn tiếp khách vãng lai", "Đóng");
                        return;
                    }
                    if (player.clan.getMembers().size() < DoanhTrai.N_PLAYER_CLAN) {
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Bang hội phải có ít nhất 5 thành viên mới có thể mở", "Đóng");
                        return;
                    }
                    if (player.clan.doanhTrai != null && player.clanMember.getNumDateFromJoinTimeToToday() >= 2 && !player.clan.doanhTrai_haveGone) {
                        createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
                                "Bang hội của ngươi đang đánh trại độc nhãn\n"
                                + "Thời gian còn lại là "
                                + TimeUtil.getMinLeft(player.clan.doanhTrai.getLastTimeOpen(), DoanhTrai.TIME_DOANH_TRAI / 1000)
                                + " Phút" + ". Ngươi có muốn tham gia không?",
                                "Tham gia", "Không", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    int nPlSameClan = 0;
                    for (Player pl : player.zone.getPlayers()) {
                        if (!pl.equals(player) && pl.clan != null
                                && pl.clan.equals(player.clan) && pl.location.x >= 1285
                                && pl.location.x <= 1645) {
                            nPlSameClan++;
                        }
                    }
                    if (nPlSameClan < DoanhTrai.N_PLAYER_MAP) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Ngươi phải có ít nhất " + DoanhTrai.N_PLAYER_MAP + " đồng đội cùng bang đứng gần mới có thể\nvào\n"
                                + "tuy nhiên ta khuyên ngươi nên đi cùng với 3-4 người để khỏi chết.\n"
                                + "Hahaha.", "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    if (player.clanMember.getNumDateFromJoinTimeToToday() < 0 || (player.clan.doanhTrai != null && player.clanMember.getNumDateFromJoinTimeToToday() < 0)) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Doanh trại chỉ cho phép những người ở trong bang trên 2 ngày. Hẹn ngươi quay lại vào lúc khác",
                                "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }

                    if (!player.clan.doanhTrai_haveGone) {
                        player.clan.doanhTrai_haveGone = (new java.sql.Date(player.clan.doanhTrai_lastTimeOpen)).getDay() == (new java.sql.Date(System.currentTimeMillis())).getDay();
                    }
                    if (player.clan.doanhTrai_haveGone) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Bang hội của ngươi đã đi trại lúc " + TimeUtil.formatTime(player.clan.doanhTrai_lastTimeOpen, "HH:mm:ss") + " hôm nay. Người mở\n"
                                + "(" + player.clan.doanhTrai_playerOpen + "). Hẹn ngươi quay lại vào ngày mai", "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
                            "Hôm nay bang hội của ngươi chưa vào trại lần nào. Ngươi có muốn vào\n"
                            + "không?\nĐể vào, ta khuyên ngươi nên có 3-4 người cùng bang đi cùng",
                            "Vào\n(miễn phí)", "Không", "Hướng\ndẫn\nthêm");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_JOIN_DOANH_TRAI:
                            if (select == 0) {
                                if (player.clan.doanhTrai != null && TimeUtil.getMinLeft(player.clan.doanhTrai.getLastTimeOpen(), DoanhTrai.TIME_DOANH_TRAI / 1000) == 0) {
                                    Service.getInstance().sendThongBao(player, "Hết 30p gòi, đợi mai đê !!!!");
                                } else if (player.clan.doanhTrai == null) {
                                    DoanhTraiService.gI().joinDoanhTrai(player);
                                } else if (player.clan.doanhTrai != null && TimeUtil.getMinLeft(player.clan.doanhTrai.getLastTimeOpen(), DoanhTrai.TIME_DOANH_TRAI / 1000) > 0) {
                                    ChangeMapService.gI().changeMapInYard(player, 53, -1, 60);
                                    ItemTimeService.gI().sendTextDoanhTrai(player);

                                }
                            } else if (select == 2) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                            }
                            break;
                        case ConstNpc.IGNORE_MENU:
                            if (select == 1) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc quaTrung(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            private final int COST_AP_TRUNG_NHANH = 1000000000;

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    player.mabuEgg.sendMabuEgg();
                    if (player.mabuEgg.getSecondDone() != 0) {
                        this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "Bư bư bư...",
                                "Hủy bỏ\ntrứng", "Ấp nhanh\n" + Util.numberToMoney(COST_AP_TRUNG_NHANH) + " vàng", "Đóng");
                    } else {
                        this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Bư bư bư...", "Nở", "Hủy bỏ\ntrứng", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.CAN_NOT_OPEN_EGG:
                            if (select == 0) {
                                this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                        "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                            } else if (select == 1) {
                                if (player.inventory.gold >= COST_AP_TRUNG_NHANH) {
                                    player.inventory.gold -= COST_AP_TRUNG_NHANH;
                                    player.mabuEgg.timeDone = 0;
                                    Service.getInstance().sendMoney(player);
                                    player.mabuEgg.sendMabuEgg();
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "Bạn không đủ vàng để thực hiện, còn thiếu "
                                            + Util.numberToMoney((COST_AP_TRUNG_NHANH - player.inventory.gold)) + " vàng");
                                }
                            }
                            break;
                        case ConstNpc.CAN_OPEN_EGG:
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                            "Bạn có chắc chắn cho trứng nở?\n"
                                            + "Đệ tử của bạn sẽ được thay thế bằng đệ Mabư",
                                            "Đệ mabư\nTrái Đất", "Đệ mabư\nNamếc", "Đệ mabư\nXayda", "Từ chối");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                            "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                                    break;
                            }
                            break;
                        case ConstNpc.CONFIRM_OPEN_EGG:
                            switch (select) {
                                case 0:
                                    player.mabuEgg.openEgg(ConstPlayer.TRAI_DAT);
                                    break;
                                case 1:
                                    player.mabuEgg.openEgg(ConstPlayer.NAMEC);
                                    break;
                                case 2:
                                    player.mabuEgg.openEgg(ConstPlayer.XAYDA);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case ConstNpc.CONFIRM_DESTROY_EGG:
                            if (select == 0) {
                                player.mabuEgg.destroyEgg();
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc duahau(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    player.timedua.sendTimedua();
                    if (player.timedua.getSecondDone() != 0) {
                        this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_DUA, "Thu hoạch dưa hấu nhận 15000 Hồng ngọc",
                                "Hủy bỏ\nDưa hấu", "Đóng");
                    } else {
                        this.createOtherMenu(player, ConstNpc.CAN_OPEN_DUA, "Dưa chín rồi nè", "Thu hoạch", "Hủy bỏ\nDưa hấu", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.CAN_NOT_OPEN_DUA:
                            if (select == 0) {
                                this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_DUA,
                                        "Bạn có chắc chắn muốn hủy bỏ Dưa hấu?", "Đồng ý", "Từ chối");
                            }
                            break;
                        case ConstNpc.CAN_OPEN_DUA:
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_DUA,
                                            "Bạn có chắc chắn THU HOẠCH DƯA?\n"
                                            + "Sẽ nhận được 15000 hồng ngọc",
                                            "Thu hoạch");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_DUA,
                                            "Bạn có chắc chắn muốn hủy bỏ dưa hấu?", "Đồng ý", "Từ chối");
                                    break;
                            }
                            break;
                        case ConstNpc.CONFIRM_OPEN_DUA:
                            switch (select) {
                                case 0:
                                    player.inventory.ruby += 150;
                                    Service.getInstance().sendMoney(player);
                                    this.npcChat(player, "Bạn nhận được 15000 hồng ngọc");
                                    break;

                            }

                        case ConstNpc.CONFIRM_DESTROY_DUA:
                            if (select == 0) {
                                player.timedua.destroydua();
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc quocVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Con muốn nâng giới hạn sức mạnh cho bản thân hay đệ tử?",
                        "Bản thân", "Đệ tử", "Từ chối");
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                if (player.nPoint.limitPower < 9) {
                                    this.createOtherMenu(player, ConstNpc.OPEN_POWER_MYSEFT,
                                            "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của bản thân lên "
                                            + Util.numberToMoney(player.nPoint.getPowerNextLimit()),
                                            "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " vàng", "Đóng");
                                } else if (player.nPoint.limitPower == 9) {
                                    this.createOtherMenu(player, ConstNpc.OPEN_POWER_MYSEFT,
                                            "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của bản thân lên "
                                            + "400 tỷ Sức mạnh",
                                            "Nâng ngay\n" + "1" + " hồng ngọc", "Đóng");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "Sức mạnh của con đã đạt tới giới hạn",
                                            "Đóng");
                                }
                                break;
                            case 1:
                                if (player.pet != null) {
                                    if (player.pet.nPoint.limitPower < 9) {
                                        this.createOtherMenu(player, ConstNpc.OPEN_POWER_PET,
                                                "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của đệ tử lên "
                                                + Util.numberToMoney(player.pet.nPoint.getPowerNextLimit()),
                                                "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " vàng", "Đóng");
                                    } else if (player.pet.nPoint.limitPower == 9) {
                                        this.createOtherMenu(player, ConstNpc.OPEN_POWER_PET,
                                                "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của Đệ tử lên "
                                                + "300 tỷ Sức mạnh",
                                                "Nâng ngay\n" + "1" + " hồng ngọc", "Đóng");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                "Sức mạnh của đệ con đã đạt tới giới hạn",
                                                "Đóng");
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                                }
                                //giới hạn đệ tử
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_MYSEFT && player.nPoint.limitPower < 9) {
                        switch (select) {
                            case 0:
                                if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                    if (OpenPowerService.gI().openPowerSpeed(player)) {
                                        player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                        Service.getInstance().sendMoney(player);
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "Bạn không đủ vàng để mở, còn thiếu "
                                            + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + " vàng");
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_MYSEFT && player.nPoint.limitPower == 9) {
                        switch (select) {
                            case 0:
                                if (player.inventory.ruby >= 1) {
                                    if (OpenPowerService.gI().openPowerSpeed(player)) {
                                        player.inventory.ruby -= 1;
                                        Service.getInstance().sendMoney(player);
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "Bạn không đủ hồng ngọc để mở, còn thiếu "
                                            + Util.numberToMoney((1 - player.inventory.ruby)) + " hồng ngọc");
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_PET && player.pet.nPoint.limitPower < 9) {
                        if (select == 0) {
                            if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                if (OpenPowerService.gI().openPowerSpeed(player.pet)) {
                                    player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                    Service.getInstance().sendMoney(player);
                                }
                            } else {
                                Service.getInstance().sendThongBao(player,
                                        "Bạn không đủ vàng để mở, còn thiếu "
                                        + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + " vàng");
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_PET && player.pet.nPoint.limitPower == 9) {
                        switch (select) {
                            case 0:
                                if (player.inventory.ruby >= 1) {
                                    if (OpenPowerService.gI().openPowerSpeed(player.pet)) {
                                        player.inventory.ruby -= 1;
                                        Service.getInstance().sendMoney(player);
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "Bạn không đủ hồng ngọc để mở, còn thiếu "
                                            + Util.numberToMoney((1 - player.inventory.ruby)) + " hồng ngọc");
                                }
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc bulmaTL(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 104 || this.mapId == 5) {
                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Shop VIP nè, đốt xèng vô đê?", "Cửa hàng", "Đóng");
                        }
                    } else if (this.mapId == 104 || this.mapId == 102) {
                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chụy chỉ bán những vật phẩm tương lai thui!!", "Cửa hàng", "Shop Đá Quý", "Đóng");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 102) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ShopServiceNew.gI().opendShop(player, "TUONG_LAI", true);
                            } else {
                                ShopServiceNew.gI().opendShop(player, "CHAN MENH", true);
                            }
                        }
                    }
                }
            }
        };
    }

    private static Npc kyGui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, 0, "|7|CỬA HÀNG KÝ GỬI\n|6|Cửa hàng chúng tôi chuyên mua bán hàng hiệu, hàng độc, cảm ơn bạn đã ghé thăm.\n|7|Lưu ý : Vật phẩm ký gửi nếu không được mua sẽ được Hoàn trả và xóa khỏi Shop sau 2 ngày tính từ lúc đăng bán", "Hướng\ndẫn\nthêm", "Mua bán\nKý gửi", "Từ chối");
                }
            }

            @Override
            public void confirmMenu(Player pl, int select) {
                if (canOpenNpc(pl)) {
                    switch (select) {
                        case 0:
                            Service.getInstance().sendPopUpMultiLine(pl, tempId, avartar, "Cửa hàng chuyên nhận ký gửi mua bán vật phẩm\bChỉ với 5 hồng ngọc\bGiá trị ký gửi là hồngngọc\bMột người bán, vạn người mua, mại dô, mại dô");
                            break;
                        case 1:
                            if (pl.playerTask.taskMain.id > 20) {

                                ShopKyGuiService.gI().openShopKyGui(pl);

                            } else {
                                Service.gI().sendThongBaoOK(pl, "Cần 150 Tỷ Và Qua Nhiệm Vụ 20");
                            }

                            break;
                    }
                }
            }
        };
    }

    public static Npc caythong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
//                    if (this.mapId == 104 || this.mapId == 5) {
//                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
//                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Shop VIP nè, đốt xèng vô đê?", "Cửa hàng", "Đóng");
//                        }
//                    } else 
                    if (this.mapId == 104) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Kính chào Ngài Linh thú sư!", "Cửa hàng", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 104 || this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ShopServiceNew.gI().opendShop(player, "PET", true);
                            }
                        }
                    } else if (this.mapId == 104 || this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
//                                ShopServiceNew.gI().opendShop(player, "BUNMA_LINHTHU", true);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc rongOmega(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    BlackBallWar.gI().setTime();
                    if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                        try {
                            long now = System.currentTimeMillis();
                            if (now > BlackBallWar.TIME_OPEN && now < BlackBallWar.TIME_CLOSE) {
                                this.createOtherMenu(player, ConstNpc.MENU_OPEN_BDW, "Đường đến với ngọc rồng sao đen đã mở, "
                                        + "ngươi có muốn tham gia không?",
                                        "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
                            } else {
                                String[] optionRewards = new String[7];
                                int index = 0;
                                for (int i = 0; i < 7; i++) {
                                    if (player.rewardBlackBall.timeOutOfDateReward[i] > System.currentTimeMillis()) {
                                        String quantily = player.rewardBlackBall.quantilyBlackBall[i] > 1 ? "x" + player.rewardBlackBall.quantilyBlackBall[i] + " " : "";
                                        optionRewards[index] = quantily + (i + 1) + " sao";
                                        index++;
                                    }
                                }
                                if (index != 0) {
                                    String[] options = new String[index + 1];
                                    for (int i = 0; i < index; i++) {
                                        options[i] = optionRewards[i];
                                    }
                                    options[options.length - 1] = "Từ chối";
                                    this.createOtherMenu(player, ConstNpc.MENU_REWARD_BDW, "Ngươi có một vài phần thưởng ngọc "
                                            + "rồng sao đen đây!",
                                            options);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_BDW,
                                            "Ta có thể giúp gì cho ngươi?", "Hướng dẫn", "Từ chối");
                                }
                            }
                        } catch (Exception ex) {
                            Logger.error("Lỗi mở menu rồng Omega");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_REWARD_BDW:
                            player.rewardBlackBall.getRewardSelect((byte) select);
                            break;
                        case ConstNpc.MENU_OPEN_BDW:
                            if (select == 0) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                            } else if (select == 1) {
//                                if (!player.getSession().actived) {
//                                    Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");
//
//                                } else
                                player.iDMark.setTypeChangeMap(ConstMap.CHANGE_BLACK_BALL);
                                ChangeMapService.gI().openChangeMapTab(player);
//                                BlackBallWar.gI().reInitNrd();
                            }
                            break;
                        case ConstNpc.MENU_NOT_OPEN_BDW:
                            if (select == 0) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                            }
                            break;
                    }
                }
            }

        };
    }

    public static Npc rong1_to_7s(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isHoldBlackBall()) {
                        this.createOtherMenu(player, ConstNpc.MENU_PHU_HP, "Ta có thể giúp gì cho ngươi?", "Phù hộ", "Từ chối");
                    } else {
                        if (BossManager.gI().existBossOnPlayer(player)
                                || player.zone.items.stream().anyMatch(itemMap -> ItemMapService.gI().isBlackBall(itemMap.itemTemplate.id))
                                || player.zone.getPlayers().stream().anyMatch(p -> p.iDMark.isHoldBlackBall())) {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME, "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối");
                        } else {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME, "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối", "Gọi BOSS");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHU_HP) {
                        if (select == 0) {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_PHU_HP,
                                    "Ta sẽ giúp ngươi tăng HP lên mức kinh hoàng, ngươi chọn đi",
                                    "x3 HP\n" + Util.numberToMoney(BlackBallWar.COST_X3) + " vàng",
                                    "x5 HP\n" + Util.numberToMoney(BlackBallWar.COST_X5) + " vàng",
                                    "x7 HP\n" + Util.numberToMoney(BlackBallWar.COST_X7) + " vàng",
                                    "Từ chối"
                            );
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_GO_HOME) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                        } else if (select == 2) {
                            BossManager.gI().callBoss(player, mapId);
                        } else if (select == 1) {
                            this.npcChat(player, "Để ta xem ngươi trụ được bao lâu");
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PHU_HP) {
                        if (player.effectSkin.xHPKI > 1) {
                            Service.getInstance().sendThongBao(player, "Bạn đã được phù hộ rồi!");
                            return;
                        }
                        switch (select) {
                            case 0:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X3);
                                break;
                            case 1:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X5);
                                break;
                            case 2:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X7);
                                break;
                            case 3:
                                this.npcChat(player, "Để ta xem ngươi trụ được bao lâu");
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc npcThienSu64(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (this.mapId == 14) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ dẫn cậu tới hành tinh Berrus với điều kiện\n 2. đạt 80 tỷ sức mạnh và Đến nhiệm vụ 24 "
                            + "\n 3. chi phí vào cổng  50 triệu vàng", "Tới ngay", "Từ chối");
                }
                if (this.mapId == 7) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ dẫn cậu tới hành tinh Berrus với điều kiện\n 2. đạt 80 tỷ sức mạnh và Đến nhiệm vụ 24 "
                            + "\n 3. chi phí vào cổng  50 triệu vàng", "Tới ngay", "Từ chối");
                }
                if (this.mapId == 0) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ dẫn cậu tới hành tinh Berrus với điều kiện\n 2. đạt 80 tỷ sức mạnh và Đến nhiệm vụ 24"
                            + "\n 3. chi phí vào cổng  50 triệu vàng", "Tới ngay", "Từ chối");
                }
                if (this.mapId == 146) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu không chịu nổi khi ở đây sao?\nCậu sẽ khó mà mạnh lên được", "Trốn về", "Ở lại");
                }
                if (this.mapId == 147) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu không chịu nổi khi ở đây sao?\nCậu sẽ khó mà mạnh lên được", "Trốn về", "Ở lại");
                }
                if (this.mapId == 148) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu không chịu nổi khi ở đây sao?\nCậu sẽ khó mà mạnh lên được", "Trốn về", "Ở lại");
                }
                if (this.mapId == 48) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đã tìm đủ nguyên liệu cho tôi chưa?\n Tôi sẽ giúp cậu mạnh lên kha khá đấy!\n\b|7| Điều kiện học Tuyệt kỹ\b|5| -Khi chưa học skill cần: x999 Bí kiếp tuyệt kỹ + 200k Hồng ngọc và SM trên 60 Tỷ\n -Mỗi một cấp yêu cầu Thông thạo đạt MAX 100% và cần 200k Hồng ngọc", "Hướng Dẫn",
                            "Đổi SKH VIP", "Học\ntuyệt kĩ", "Từ Chối");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu() && this.mapId == 7) {
                        if (select == 0) {
                            if (player.getSession().player.nPoint.power >= 80000000000L && player.inventory.gold > COST_HD && TaskService.gI().getIdTask(player) > ConstTask.TASK_24_0) {
                                player.inventory.gold -= COST_HD;
                                Service.getInstance().sendMoney(player);
                                ChangeMapService.gI().changeMapBySpaceShip(player, 146, -1, -1);
                            } else {
                                this.npcChat(player, "Bạn chưa đủ điều kiện để vào");
                            }
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 14) {
                        if (select == 0) {
                            if (player.getSession().player.nPoint.power >= 80000000000L && player.inventory.gold > COST_HD && TaskService.gI().getIdTask(player) > ConstTask.TASK_24_0) {
                                player.inventory.gold -= COST_HD;
                                Service.getInstance().sendMoney(player);
                                ChangeMapService.gI().changeMapBySpaceShip(player, 148, -1, -1);
                            } else {
                                this.npcChat(player, "Bạn chưa đủ điều kiện để vào");
                            }
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 0) {
                        if (select == 0) {
                            if (player.getSession().player.nPoint.power >= 80000000000L && player.inventory.gold > COST_HD && TaskService.gI().getIdTask(player) > ConstTask.TASK_24_0) {
                                player.inventory.gold -= COST_HD;
                                Service.getInstance().sendMoney(player);
                                ChangeMapService.gI().changeMapBySpaceShip(player, 147, -1, -1);
                            } else {
                                this.npcChat(player, "Bạn chưa đủ điều kiện để vào");
                            }
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 147) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, -1);
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 148) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, 14, -1, -1);
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 146) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, 7, -1, -1);
                        }
                        if (select == 1) {
                        }

                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 48) {
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOI_SKH_VIP);
                        }
                        if (select == 1) {
                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_SKH_VIP);
                        }
                        if (select == 2) {
                            Message msg;
                            try {
                                if (player.gender == 0) {
                                    Skill curSkill = SkillUtil.getSkillbyId(player, Skill.SUPER_KAME);
                                    if (curSkill.point == 0) {
                                        Item honLinhThu = null;
                                        try {
                                            honLinhThu = InventoryServiceNew.gI().findItemBag(player, 1215);
                                        } catch (Exception e) {
                                            //                                        throw new RuntimeException(e);
                                        }
                                        if (player.nPoint.power >= 60000000000L) {
                                            if (honLinhThu == null || honLinhThu.quantity < 999) {
                                                this.npcChat(player, "Bạn không đủ 999 bí kíp tuyệt kĩ");
                                            } else if (player.inventory.ruby > 2000 && honLinhThu.quantity >= 999) {
                                                player.inventory.ruby -= 2000;
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu, 999);
                                                Service.getInstance().sendMoney(player);
                                                curSkill = SkillUtil.createSkill(Skill.SUPER_KAME, 1);
                                                SkillUtil.setSkill(player, curSkill);
                                                msg = Service.getInstance().messageSubCommand((byte) 23);
                                                msg.writer().writeShort(curSkill.skillId);
                                                player.achievement.plusCount(12);
                                                player.sendMessage(msg);
                                                msg.cleanup();
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Không đủ Hồng ngọc");
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Yêu cầu sức mạnh trên 60 Tỷ");
                                        }
                                    } else if (curSkill.point > 0 && curSkill.point < 9) {
                                        if (curSkill.currLevel == 1000 && player.inventory.ruby < 2000) {
                                            Service.getInstance().sendThongBao(player, "Không đủ Hồng ngọc");
                                        } else if (curSkill.currLevel == 1000 && player.inventory.ruby > 2000) {
                                            player.inventory.ruby -= 2000;
                                            Service.getInstance().sendMoney(player);
                                            curSkill = SkillUtil.createSkill(Skill.SUPER_KAME, curSkill.point + 1);
                                            SkillUtil.setSkill(player, curSkill);
                                            msg = Service.getInstance().messageSubCommand((byte) 62);
                                            msg.writer().writeShort(curSkill.skillId);
                                            player.achievement.plusCount(12);
                                            player.sendMessage(msg);
                                            msg.cleanup();
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Thông thạo của bạn chưa đủ 100%");
                                        }
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Tuyệt kĩ của bạn đã đạt cấp tối đa");
                                    }
                                }
                                if (player.gender == 1) {
                                    Skill curSkill = SkillUtil.getSkillbyId(player, Skill.MA_PHONG_BA);
                                    if (curSkill.point == 0) {
                                        Item honLinhThu = null;
                                        try {
                                            honLinhThu = InventoryServiceNew.gI().findItemBag(player, 1215);
                                        } catch (Exception e) {
                                            //                                        throw new RuntimeException(e);
                                        }
                                        if (player.nPoint.power >= 60000000000L) {
                                            if (honLinhThu == null || honLinhThu.quantity < 999) {
                                                this.npcChat(player, "Bạn không đủ 999 bí kíp tuyệt kĩ");
                                            } else if (player.inventory.ruby > 2000 && honLinhThu.quantity >= 999) {
                                                player.inventory.ruby -= 2000;
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu, 999);
                                                Service.getInstance().sendMoney(player);
                                                curSkill = SkillUtil.createSkill(Skill.MA_PHONG_BA, 1);
                                                SkillUtil.setSkill(player, curSkill);
                                                msg = Service.getInstance().messageSubCommand((byte) 23);
                                                msg.writer().writeShort(curSkill.skillId);
                                                player.achievement.plusCount(12);
                                                player.sendMessage(msg);
                                                msg.cleanup();
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Không đủ Hồng ngọc");
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Yêu cầu sức mạnh trên 60 Tỷ");
                                        }
                                    } else if (curSkill.point > 0 && curSkill.point < 9) {
                                        if (curSkill.currLevel == 1000 && player.inventory.ruby < 200_000) {
                                            Service.getInstance().sendThongBao(player, "Không đủ Hồng ngọc");
                                        } else if (curSkill.currLevel == 1000 && player.inventory.ruby > 200_000) {
                                            player.inventory.ruby -= 2000;
                                            Service.getInstance().sendMoney(player);
                                            curSkill = SkillUtil.createSkill(Skill.MA_PHONG_BA, curSkill.point + 1);
                                            SkillUtil.setSkill(player, curSkill);
                                            msg = Service.getInstance().messageSubCommand((byte) 62);
                                            msg.writer().writeShort(curSkill.skillId);
                                            player.achievement.plusCount(12);
                                            player.sendMessage(msg);
                                            msg.cleanup();
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Thông thạo của bạn chưa đủ 100%");
                                        }
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Tuyệt kĩ của bạn đã đạt cấp tối đa");
                                    }
                                }
                                if (player.gender == 2) {
                                    Skill curSkill = SkillUtil.getSkillbyId(player, Skill.LIEN_HOAN_CHUONG);
                                    if (curSkill.point == 0) {
                                        Item honLinhThu = null;
                                        try {
                                            honLinhThu = InventoryServiceNew.gI().findItemBag(player, 1215);
                                        } catch (Exception e) {
                                            //                                        throw new RuntimeException(e);
                                        }
                                        if (player.nPoint.power >= 60000000000L) {
                                            if (honLinhThu == null || honLinhThu.quantity < 999) {
                                                this.npcChat(player, "Bạn không đủ 999 bí kíp tuyệt kĩ");
                                            } else if (player.inventory.ruby > 200_000 && honLinhThu.quantity >= 999) {
                                                player.inventory.ruby -= 2000;
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu, 999);
                                                Service.getInstance().sendMoney(player);
                                                curSkill = SkillUtil.createSkill(Skill.LIEN_HOAN_CHUONG, 1);
                                                SkillUtil.setSkill(player, curSkill);
                                                msg = Service.getInstance().messageSubCommand((byte) 23);
                                                msg.writer().writeShort(curSkill.skillId);
                                                player.achievement.plusCount(12);
                                                player.sendMessage(msg);
                                                msg.cleanup();
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Không đủ Hồng ngọc");
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Yêu cầu sức mạnh trên 60 Tỷ");
                                        }
                                    } else if (curSkill.point > 0 && curSkill.point < 9) {
                                        if (curSkill.currLevel == 1000 && player.inventory.ruby < 200_000) {
                                            Service.getInstance().sendThongBao(player, "Không đủ Hồng ngọc");
                                        } else if (curSkill.currLevel == 1000 && player.inventory.ruby > 200_000) {
                                            player.inventory.ruby -= 200_000;
                                            Service.getInstance().sendMoney(player);
                                            curSkill = SkillUtil.createSkill(Skill.LIEN_HOAN_CHUONG, curSkill.point + 1);
                                            SkillUtil.setSkill(player, curSkill);
                                            msg = Service.getInstance().messageSubCommand((byte) 62);
                                            msg.writer().writeShort(curSkill.skillId);
                                            player.achievement.plusCount(12);
                                            player.sendMessage(msg);
                                            msg.cleanup();
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Thông thạo của bạn chưa đủ 100%");
                                        }
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Tuyệt kĩ của bạn đã đạt cấp tối đa");
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                System.out.println("loi ne   4534     ClassCastException ");
                            }
                        }

                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_DOI_SKH_VIP) {
                        if (select == 0) {
                            CombineServiceNew.gI().startCombine(player);
                        }
                    }
                }
            }

        };
    }

    public static Npc bill(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|7|SHOP ĐỒ HỦY DIỆT\n|6| Mang đủ 5 món đồ Thần linh và đem 99 Thức ăn đến cho ta. Ta sẽ bán đồ Hủy diệt cho ngươi",
                            "SHOP HỦY DIỆT", "Đóng");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.BASE_MENU:
                            if (select == 0) {
                                ShopServiceNew.gI().opendShop(player, "BILL", true);
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc boMong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 47 || this.mapId == 84) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Xin chào, cậu muốn tôi giúp gì?", "Nhiệm vụ\nhàng ngày", "Tích lũy\nNạp", "Danh hiệu", "Nhận Quà\nGiới Thiệu","Giftcode", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 47 || this.mapId == 84) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    // if (player.playerTask.sideTask.template != null) {
                                    //     String npcSay = "Nhiệm vụ hiện tại: " + player.playerTask.sideTask.getName() + " ("
                                    //             + player.playerTask.sideTask.getLevel() + ")"
                                    //             + "\nHiện tại đã hoàn thành: " + player.playerTask.sideTask.count + "/"
                                    //             + player.playerTask.sideTask.maxCount + " ("
                                    //             + player.playerTask.sideTask.getPercentProcess() + "%)\nSố nhiệm vụ còn lại trong ngày: "
                                    //             + player.playerTask.sideTask.leftTask + "/" + ConstTask.MAX_SIDE_TASK;
                                    //     this.createOtherMenu(player, ConstNpc.MENU_OPTION_PAY_SIDE_TASK,
                                    //             npcSay, "Trả nhiệm\nvụ", "Hủy nhiệm\nvụ");
                                    // } else {
                                    //     this.createOtherMenu(player, ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK,
                                    //             "Tôi có vài nhiệm vụ theo cấp bậc, "
                                    //             + "sức cậu có thể làm được cái nào?\b|5|Phần thưởng:\b|7| Dễ: 1 Thỏi vàng + 200 hồng ngọc\n Bình thường: 2 Thỏi vàng + 400 hồng ngọc\n Khó: 3 Thỏi vàng + 600 hồng ngọc\n Siêu khó: 4 Thỏi vàng + 800 hồng ngọc\n Địa ngục: 5 Thỏi vàng + 1000 hồng ngọc",
                                    //             "Dễ", "Bình thường", "Khó", "Siêu khó", "Địa ngục", "Từ chối");
                                    // }
                                    break;
                                case 1:
                                    //player.achievement.Show();
                                    break;
                                case 2:
                                    this.createOtherMenu(player, 888,
                                            "|7|CHỨC NĂNG DANH HIỆU"
                                            + "\n\n|2|Đây là danh hiệu mà ngươi có"
                                            + "\n\n|2|Danh hiệu 'Tối Thượng' tăng 5% Chỉ số"
                                            + "\n\n|2|Danh hiệu 'Thiên Tử' tăng 5% Chỉ số"
                                            + "\n\n|2|Danh hiệu 'VIP' tăng 5% Chỉ số"
                                            + "\n\n|7|Lưu ý: phải bật danh hiệu lên mới tăng chỉ số"
                                            + (player.lastTimeTitle1 > 0 ? "\n\n|4|Danh hiệu 1: " + Util.msToTime(player.lastTimeTitle1) : "")
                                            + (player.lastTimeTitle2 > 0 ? "\n Danh hiệu 2: " + Util.msToTime(player.lastTimeTitle2) : "")
                                            + (player.lastTimeTitle3 > 0 ? "\n Danh hiệu 3: " + Util.msToTime(player.lastTimeTitle3) : "")
                                            + (player.lastTimeTitle4 > 0 ? "\n Danh hiệu 4: " + Util.msToTime(player.lastTimeTitle4) : ""),
                                            ("Nhận danh hiệu"),
                                            ("Danh hiệu 1\n" + (player.isTitleUse1 == true ? "'ON'" : "'OFF'")),
                                            ("Danh hiệu 2\n" + (player.isTitleUse2 == true ? "'ON'" : "'OFF'") + "\n"),
                                            ("Danh hiệu 3\n" + (player.isTitleUse3 == true ? "'ON'" : "'OFF'") + "\n"),
                                            ("Danh hiệu 4\n" + (player.isTitleUse4 == true ? "'ON'" : "'OFF'") + "\n"));
                                    break;
                                case 3:
                                    this.createOtherMenu(player, 123422, "|7|Hiện tại bạn đang có =>" + player.session.tichdiem + " điểm giới thiệu"
                                            + "\n|7|Sử dụng điểm để quy đổi cái lưa chọn dưới đây", "cải trang\n2 điểm",
                                            "Thỏi vàng\n1 điểm",
                                            "Hồng ngọc\n1 điểm",
                                            "Mở thành viên\n10 diểm");

                                    break;
                                case 4:
                                    Input.gI().createFormGiftCode(player);
                                break;

                            }
                        } else if (player.iDMark.getIndexMenu() == 123422) {
                            switch (select) {
                                case 0:
                                    if (player.session.tichdiem >= 2) {
                                        PlayerDAO.subtichdiem(player, 2);
                                        Item caitrang = ItemService.gI().createNewItem((short) 711, 1);
                                        caitrang.itemOptions.add(new Item.ItemOption(50, 20));
                                        caitrang.itemOptions.add(new Item.ItemOption(77, 20));
                                        caitrang.itemOptions.add(new Item.ItemOption(103, 20));
                                        caitrang.itemOptions.add(new Item.ItemOption(8, 2));
                                        caitrang.itemOptions.add(new Item.ItemOption(160, Util.nextInt(50, 100)));
                                        caitrang.itemOptions.add(new Item.ItemOption(101, Util.nextInt(100, 150)));
                                        if (Util.isTrue(80, 100)) {
                                            caitrang.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 2)));
                                        }
                                        InventoryServiceNew.gI().addItemBag(player, caitrang);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        npcChat(player, "|7|Bạn đã nhận được " + caitrang.template.name);
                                    }
                                    break;
                                case 1:
                                    int soluongtv = Util.nextInt(10, 50);
                                    if (player.session.tichdiem >= 1) {
                                        PlayerDAO.subtichdiem(player, 1);
                                        Item thoivang = ItemService.gI().createNewItem((short) 457, soluongtv);
                                        thoivang.itemOptions.add(new Item.ItemOption(30, 1));
                                        InventoryServiceNew.gI().addItemBag(player, thoivang);

                                        InventoryServiceNew.gI().sendItemBags(player);
                                        npcChat(player, "|7|Bạn đã nhận được x" + soluongtv + " " + thoivang.template.name);
                                    }
                                    break;
                                case 2:
                                    int soluonghn = Util.nextInt(10000, 50000);
                                    if (player.session.tichdiem >= 1) {
                                        PlayerDAO.subtichdiem(player, 1);
                                        Item hongngoc = ItemService.gI().createNewItem((short) 861, soluonghn);
                                        hongngoc.itemOptions.add(new Item.ItemOption(30, 1));
                                        InventoryServiceNew.gI().addItemBag(player, hongngoc);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        npcChat(player, "|7|Bạn đã nhận được x" + soluonghn + " " + hongngoc.template.name);
                                    }
                                    break;
                                case 3:
                                    if (player.session.tichdiem >= 10) {
                                        PlayerDAO.subtichdiem(player, 10);
                                        player.session.actived = true;
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        npcChat(player, "|7|Mở thành viên thành công");
                                    }
                                    break;

                            }

                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK) {
                            switch (select) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                    TaskService.gI().changeSideTask(player, (byte) select);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PAY_SIDE_TASK) {
                            switch (select) {
                                case 0:
                                    TaskService.gI().paySideTask(player);
                                    break;
                                case 1:
                                    TaskService.gI().removeSideTask(player);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 888) {
                            switch (select) {
                                case 0:
                                    if (player.nPoint.power >= 100000000000L) {
                                        if (player.lastTimeTitle1 == 0) {
                                            if (player.lastTimeTitle1 == 0) {
                                                player.lastTimeTitle1 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 3);
                                            } else {
                                                player.lastTimeTitle1 += (1000 * 60 * 60 * 24 * 360);
                                            }
                                            player.isTitleUse1 = true;
                                            Service.gI().point(player);
                                            Service.gI().sendTitle(player, 888);
                                            Service.gI().sendThongBao(player, "Bạn nhận được vĩnh viễn !");
                                        } else {
                                            Service.gI().sendThongBao(player, "bạn dã nhận danh hiệu này rồi !");
                                        }
                                    } else {
                                        this.npcChat(player, "Yều cầu đạt 100 tỷ sức mạnh");
                                    }
                                    break;
                                    case 1:
                                    if (player.lastTimeTitle1 > 0) {
                                        player.isTitleUse1 = !player.isTitleUse1;
                                        Service.gI().point(player);
                                        Service.gI().sendThongBao(player, "Đã " + (player.isTitleUse1 ? "Bật" : "Tắt") + " Danh Hiệu!");
                                        // Service.gI().sendTitle(player, 891);
                                        // Service.gI().sendTitle(player, 890);
                                        // Service.gI().sendTitle(player, 889);
                                        // Service.gI().sendTitle(player, 888);
                                        Service.gI().removeTitle(player);
                                    }  else if (player.lastTimeTitle3 > 0) {
                                        player.isTitleUse3 = !player.isTitleUse3;
                                        Service.gI().point(player);
                                        Service.gI().sendThongBao(player, "Đã " + (player.isTitleUse3 ? "Bật" : "Tắt") + " Danh Hiệu!");
                                        // Service.gI().sendTitle(player, 891);
                                        // Service.gI().sendTitle(player, 890);
                                        // Service.gI().sendTitle(player, 889);
                                        // Service.gI().sendTitle(player, 888);
                                    }
                                    
                                    break;
                                case 2:
                                if (player.lastTimeTitle2 > 0) {
                                    player.isTitleUse2 = !player.isTitleUse2;
                                    Service.gI().point(player);
                                    Service.gI().sendThongBao(player, "Đã " + (player.isTitleUse4 ? "Bật" : "Tắt") + " Danh Hiệu!");
                                    // Service.gI().sendTitle(player, 891);
                                    // Service.gI().sendTitle(player, 890);
                                    // Service.gI().sendTitle(player, 889);
                                    // Service.gI().sendTitle(player, 888);
                                    Service.gI().removeTitle(player);
                                }
                                    break;
                                case 3:
                                if (player.lastTimeTitle3 > 0) {
                                    player.isTitleUse3 = !player.isTitleUse3;
                                    Service.gI().point(player);
                                    Service.gI().sendThongBao(player, "Đã " + (player.isTitleUse4 ? "Bật" : "Tắt") + " Danh Hiệu!");
                                    // Service.gI().sendTitle(player, 891);
                                    // Service.gI().sendTitle(player, 890);
                                    // Service.gI().sendTitle(player, 889);
                                    // Service.gI().sendTitle(player, 888);
                                    Service.gI().removeTitle(player);
                                }
                                    break;
                                case 4:
                                if (player.lastTimeTitle4 > 0) {
                                    player.isTitleUse4 = !player.isTitleUse4;
                                    Service.gI().point(player);
                                    Service.gI().sendThongBao(player, "Đã " + (player.isTitleUse4 ? "Bật" : "Tắt") + " Danh Hiệu!");
                                    // Service.gI().sendTitle(player, 891);
                                    // Service.gI().sendTitle(player, 890);
                                    // Service.gI().sendTitle(player, 889);
                                    // Service.gI().sendTitle(player, 888);
                                    Service.gI().removeTitle(player);
                                }
                                    break;
                                
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc karin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 102) {
                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?", "Cửa hàng", "Đóng");
                        }
                    } else if (this.mapId == 46 || this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Kính chào Ngài Linh thú sư!", "Cửa hàng", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 102) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ShopServiceNew.gI().opendShop(player, "BUNMA_FUTURE", true);
                            }
                        }
                    } else if (this.mapId == 46 || this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ShopServiceNew.gI().opendShop(player, "KARIN", true);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc vados(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|7| BẢNG XẾP HẠNG\n|6|"
                            + "\b|1|Người Muốn Xem TOP Gì?", "Săn Boss",
                            "Đóng");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 5:
                            switch (player.iDMark.getIndexMenu()) {
                                case ConstNpc.BASE_MENU:
//                                    if (select == 0) {
//                                        Service.gI().showListTop(player, Manager.topHP);
//                                        break;
//                                    }
//                                    if (select == 0) {
//                                        Service.gI().showListTop(player, Manager.topSM);
//                                        break;
//                                    }
//                                    if (select == 1) {
//                                        Service.gI().showListTop(player, Manager.topNV);
//                                        break;
//                                    }
                                    if (select == 0) {
                                        Service.gI().showListTop(player, Manager.topSanBoss);
                                        break;
                                    }
//                                    if (select == 2) {
//                                        Service.gI().showListTop(player, Manager.topVND);
//                                        break;
//                                    }
                                    // if (select == 0) {
                                    //     Service.gI().showListTop(player, Manager.topGapThu);
                                    //     break;
                                    // }
                                    break;
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc gokuSSJ_2(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|7|Đưa ta x9999 bí kíp ta sẽ giúp người phù phép cải trang của ngươi",
                            "Ok",
                            "Thôi"
                    );

                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                Item.ItemOption option = null;
                                Item bikip = InventoryServiceNew.gI().findItemBag(player, 1634);
                                Item caitrang = player.inventory.itemsBody.get(5);
                                if (caitrang.isNotNullItem()) {
                                    for (Item.ItemOption io : caitrang.itemOptions) {
                                        option = io;
                                    }
                                    if (option.optionTemplate != null && (option.optionTemplate.id == 251 || option.optionTemplate.id == 33)) {
                                        if (option != null && option.optionTemplate.id == 251) {
                                            if (bikip != null && bikip.quantity >= 9999) {
                                                option.param += 10;
                                                Service.getInstance().sendThongBao(player, "|7|Phù phép thành công");
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, bikip, 9999);
                                                InventoryServiceNew.gI().sendItemBody(player);
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player, "|7|Không đủ bí kíp để thực hiện");
                                        }
                                    } else {
                                        if (bikip != null && bikip.quantity >= 9999) {
                                            caitrang.itemOptions.add(new Item.ItemOption(33, 1));
                                            caitrang.itemOptions.add(new Item.ItemOption(251, 15));
                                            InventoryServiceNew.gI().sendItemBody(player);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, bikip, 9999);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            Service.getInstance().sendThongBao(player, "|7|Phù phép thành công vc ");
                                        } else {
                                            Service.getInstance().sendThongBao(player, "|7|Không đủ bí kíp để thực hiện");
                                        }
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player, "|7|Yêu Cầu Mang Cải Trang Vào");
                                }
                                break;

                        }
                    }
                }
            }

        };
    }

    public static Npc gokuSSJ_1(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 80) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin chào, tôi có thể giúp gì cho cậu?", "Tới hành tinh\nYardart", "Từ chối");
                    } else if (this.mapId == 131) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin chào, tôi có thể giúp gì cho cậu?", "Quay về", "Từ chối");
                    } else {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.BASE_MENU:
                            if (this.mapId == 131) {
                                if (select == 0) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 80, -1, 870);
                                }
                            }
                            if (this.mapId == 80) {
                                if (select == 0) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 131, -1, 870);
                                }
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc createNPC(int mapId, int status, int cx, int cy, int tempId) {
        int avatar = Manager.NPC_TEMPLATES.get(tempId).avatar;
        try {
            switch (tempId) {
                case ConstNpc.TRUNG_THU:
                    return Skien_trungthu(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.MR_POPO:
                    return popo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRUNG_LINH_THU:
                    return trungLinhThu(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GHI_DANH:
                    return GhiDanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.POTAGE:
                    return poTaGe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUY_LAO_KAME:
                    return quyLaoKame(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NOI_BANH:
                    return sukientet(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRUONG_LAO_GURU:
                    return truongLaoGuru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VUA_VEGETA:
                    return vuaVegeta(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_GOHAN:
                case ConstNpc.ONG_MOORI:
                case ConstNpc.ONG_PARAGUS:
                    return ongGohan_ongMoori_ongParagus(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA:
                    return bulmaQK(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DENDE:
                    return dende(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.APPULE:
                    return appule(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DR_DRIEF:
                    return drDrief(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CARGO:
                    return cargo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUI:
                    return cui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.SANTA:
                    return santa(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.URON:
                    return uron(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BA_HAT_MIT:
                    return baHatMit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.trungdetu:
                    return trungdetu(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.gogetassj4:
                    return GOGETASSJ4(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.WHIS:
                    return whisdots(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RUONG_DO:
                    return ruongDo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DAU_THAN:
                    return dauThan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CALICK:
                    return calick(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.JACO:
                    return jaco(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THUONG_DE:
                    return thuongDe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.Toppo:
                    return Toppo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VADOS:
                    return vados(mapId, status, cx, cy, tempId, avatar);

                case ConstNpc.THAN_VU_TRU:
                    return thanVuTru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.KIBIT:
                    return kibit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.OSIN:
                    return osin(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUA_HANG_KY_GUI:
                    return kyGui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.MI_NUONG:
                    return minuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CHIEN_THAN:
                    return bandetu(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LINH_CANH:
                    return linhCanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.FIDE:
                    return fide(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GIUMA_DAU_BO:
                    return giuma(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUA_TRUNG:
                    return quaTrung(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DUA_HAU:
                    return duahau(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.UUB:
                    return npcuub(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUOC_VUONG:
                    return quocVuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA_TL:
                    return bulmaTL(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.Monaito:
                    return monaito(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.rongomega:
                    return rongomege1(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NPC_EVENT:
                    return EVENT(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.maygap:
                    return maygap(mapId, status, cx, cy, tempId, avatar);

                case ConstNpc.RONG_OMEGA:
                    return rongOmega(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_1S:
                case ConstNpc.RONG_2S:
                case ConstNpc.RONG_3S:
                case ConstNpc.RONG_4S:
                case ConstNpc.RONG_5S:
                case ConstNpc.RONG_6S:
                case ConstNpc.RONG_7S:
                    return rong1_to_7s(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NPC_64:
                    return npcThienSu64(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BILL:
                    return bill(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_GIA_NOEL:
                    return bill(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRONG_TAI:
                    return TrongTai(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BO_MONG:
                    return boMong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THAN_MEO_KARIN:
                    return karin(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.champa:
                    return champa(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ:
                    return gokuSSJ_1(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CAY_THONG_NOEL:
                    return caythong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ_2:
                    return gokuSSJ_2(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DUONG_TANG:
                    return duongtank(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ATM:
                    return ATM(mapId, status, cx, cy, tempId, avatar);
                default:
                    return new Npc(mapId, status, cx, cy, tempId, avatar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                super.openBaseMenu(player);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                            }
                        }
                    };

            }
        } catch (Exception e) {
            Logger.logException(NpcFactory.class,
                    e, "Lỗi load npc");
            return null;
        }
    }

///////////////////////////////////////////////Rồng///////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
    public static void createNpcRongThieng() {
        new Npc(-1, -1, -1, -1, ConstNpc.RONG_THIENG, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:
                        break;
                    case ConstNpc.SHENRON_CONFIRM:
                        if (select == 0) {
                            SummonDragon.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragon.gI().reOpenShenronWishes(player);
                        }
                        break;
                    case ConstNpc.SHENRON_1_1:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_1 && select == SHENRON_1_STAR_WISHES_1.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_2, SHENRON_SAY, SHENRON_1_STAR_WISHES_2);
                            break;
                        }
                    case ConstNpc.SHENRON_1_2:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_2 && select == SHENRON_1_STAR_WISHES_2.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_1, SHENRON_SAY, SHENRON_1_STAR_WISHES_1);
                            break;
                        }
                    default:
                        SummonDragon.gI().showConfirmShenron(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                }
            }
        };
    }

    public static void createNpcRongXuong() {
        new Npc(-1, -1, -1, -1, ConstNpc.RONG_XUONG, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.HALLOWEN_CONFIRM:
                        if (select == 0) {
                            GoiRongXuong.gI().confirmWish();
                        } else if (select == 1) {
                            GoiRongXuong.gI().reOpenRongxuongWishes(player);
                        }
                        break;
                    default:
                        GoiRongXuong.gI().showConfirmRongxuong(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                }
            }
        };
    }

    public static void createNpcRongICE() {
        new Npc(-1, -1, -1, -1, ConstNpc.RONG_ICE, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:
                        break;
                    case ConstNpc.ICE_CONFIRM:
                        if (select == 0) {
                            GoiRongBang.gI().confirmWish();
                        } else if (select == 1) {
                            GoiRongBang.gI().reOpenRongICEWishes(player);
                        }
                        break;
                    default:
                        GoiRongBang.gI().showConfirmRongICE(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                }
            }
        };
    }

    public static void createNpcConMeo() {
        Npc npc;
        npc = new Npc(-1, -1, -1, -1, ConstNpc.CON_MEO, 351) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.MAKE_MATCH_PVP: //                       
                    {
                        if (Maintenance.isRuning) {
                            break;
                        }
                        PVPService.gI().sendInvitePVP(player, (byte) select);
                        break;
                    }
                    case ConstNpc.MAKE_FRIEND:
                        if (select == 0) {
                            Object playerId = PLAYERID_OBJECT.get(player.id);
                            if (playerId != null) {
                                FriendAndEnemyService.gI().acceptMakeFriend(player,
                                        Integer.parseInt(String.valueOf(playerId)));
                            }
                        }
                        break;
                    case ConstNpc.REVENGE:
                        if (select == 0) {
                            PVPService.gI().acceptRevenge(player);
                        }
                        break;
                    case ConstNpc.TUTORIAL_SUMMON_DRAGON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        }
                        break;
                    case ConstNpc.SUMMON_SHENRON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        } else if (select == 1) {
                            SummonDragon.gI().summonShenron(player);
                        }
                        break;
                    case ConstNpc.TUTORIAL_RONG_XUONG:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, GoiRongXuong.RONG_XUONG_TUTORIAL);
                        }
                        break;
                    case ConstNpc.TUTORIAL_RONG_ICE:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, GoiRongBang.RONG_ICE_TUTORIAL);
                        }
                        break;
                    case ConstNpc.RONG_HALLOWEN:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, GoiRongXuong.RONG_XUONG_TUTORIAL);
                        } else if (select == 1) {
                            GoiRongXuong.gI().summonRongxuong(player);
                        }
                        break;
                    case ConstNpc.RONG_ICE:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, GoiRongBang.RONG_ICE_TUTORIAL);
                        } else if (select == 1) {
                            GoiRongBang.gI().summonRongICE(player);
                        }
                        break;
                    case ConstNpc.MENU_OPTION_USE_ITEM1105:
                        if (select == 0) {
                            IntrinsicService.gI().sattd(player);
                        } else if (select == 1) {
                            IntrinsicService.gI().satnm(player);
                        } else if (select == 2) {
                            IntrinsicService.gI().setxd(player);
                        }
                        break;
                    case ConstNpc.MENU_OPTION_USE_ITEM2000:
                    case ConstNpc.MENU_OPTION_USE_ITEM2001:
                    case ConstNpc.MENU_OPTION_USE_ITEM2002:
                        try {
                        ItemService.gI().OpenSKH(player, player.iDMark.getIndexMenu(), select);
                    } catch (Exception e) {
                        Logger.error("Lỗi mở hộp quà");
                    }
                    break;
                    case ConstNpc.MENU_OPTION_USE_ITEM2003:
                    case ConstNpc.MENU_OPTION_USE_ITEM2004:
                    case ConstNpc.MENU_OPTION_USE_ITEM2005:
                        try {
                        ItemService.gI().OpenDHD(player, player.iDMark.getIndexMenu(), select);
                    } catch (Exception e) {
                        Logger.error("Lỗi mở hộp quà");
                    }
                    break;
                    case ConstNpc.MENU_OPTION_USE_ITEM1533:
                    case ConstNpc.MENU_OPTION_USE_ITEM1534:
                    case ConstNpc.MENU_OPTION_USE_ITEM1535:
                        try {
                        ItemService.gI().OpenDTL(player, player.iDMark.getIndexMenu(), select);
                    } catch (Exception e) {
                        Logger.error("Lỗi mở hộp quà");
                    }
                    break;
                    case ConstNpc.MENU_OPTION_USE_ITEM736:
                        try {
                        ItemService.gI().OpenDHD(player, player.iDMark.getIndexMenu(), select);
                    } catch (Exception e) {
                        Logger.error("Lỗi mở hộp quà");
                    }
                    break;
                    case ConstNpc.INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().showAllIntrinsic(player);
                        } else if (select == 1) {
                            IntrinsicService.gI().showConfirmOpen(player);
                        } else if (select == 2) {
                            IntrinsicService.gI().showConfirmOpenVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().open(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP:
                        if (select == 0) {
                            IntrinsicService.gI().openVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_LEAVE_CLAN:
                        if (select == 0) {
                            ClanService.gI().leaveClan(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_NHUONG_PC:
                        if (select == 0) {
                            ClanService.gI().phongPc(player, (int) PLAYERID_OBJECT.get(player.id));
                        }
                        break;
                    case ConstNpc.BAN_PLAYER:
                        if (select == 0) {
                            PlayerService.gI().banPlayer((Player) PLAYERID_OBJECT.get(player.id));
                            Service.getInstance().sendThongBao(player, "Ban người chơi " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                        }
                        break;
                    //// buff pet
                    case ConstNpc.BUFF_PET:
                        if (select == 0) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            if (pl.pet == null) {
                                PetService.gI().createNormalPet(pl, Util.nextInt(0, 2));
                                Service.getInstance().sendThongBao(player, "Phát đệ tử cho " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                            }
                        }
                        break;
                    case ConstNpc.MENU_AUTO:
                        switch (select) {
                            case 0:
                                if (player.autodosat == false) {
                                    Service.gI().sendThongBao(player, "|2|Đã Bật Auto tàn Sát");
                                    player.autodosat = true;
                                    ItemTimeService.gI().sendCanAutoPlay(player);
                                } else {
                                    Service.gI().sendThongBao(player, "|7|Đã Dừng Auto tàn sát");
                                    player.autodosat = false;
                                    ItemTimeService.gI().sendCanAutoPlay(player);
                                }
                                break;
                            case 1:
                                if (player.autohoisinh) {
                                    // Đã bật, tắt Auto hồi sinh
                                    Service.gI().sendThongBao(player, "|7|Đã Dừng Auto hồi sinh");
                                    player.autohoisinh = false;
                                } else {
                                    // Chưa bật, bật Auto hồi sinh
                                    Service.gI().sendThongBao(player, "|2|Đã Bật Auto hồi sinh");
                                    player.autohoisinh = true;
                                    scheduler.scheduleAtFixedRate(() -> PlayerService.gI().hoiSinh(player), 0, 500, TimeUnit.MILLISECONDS);
                                }
                                break;
                            case 2:
                                if (player.autodau == false) {
                                    Service.gI().sendThongBao(player, "|2|Đã Bật Auto Buff Đậu khi HP,KI đệ tử dưới 30%");
                                    player.autodau = true;
                                } else {
                                    Service.gI().sendThongBao(player, "|7|Đã Dừng Auto Buff Đậu đệ tử");
                                    player.autodau = false;
                                }
                                break;
                            case 3:
                                if (player.autocso == true) {
                                    Service.gI().sendThongBao(player, "|7|Đã dừng lệnh Auto Cộng chỉ số");
                                    player.autocso = false;
                                    player.autoHP = false;
                                    player.autoKI = false;
                                    player.autoSD = false;
                                    player.autoGiap = false;
                                    return;
                                }
                                Service.gI().chisonhanh(player);
                                break;
                            case 4:
                                if (player.autonoitai == false) {
                                    Input.gI().randomnoitai(player);
                                    player.autonoitai = true;
                                } else {
                                    player.autonoitai = false;
                                    Service.gI().sendThongBao(player, "|7|Đã dừng lệnh Auto nội tại");
                                }
                                break;
                            case 5:
                                this.createOtherMenu(player, ConstNpc.AUTO_ITEM,
                                        "|7|Hãy chọn auto dưới đây",
                                        ("Mua Nhiều \n" + (player.muanhieu == true ? "'ON'" : "'OFF'")),
                                        ("Sử Dụng \n" + (player.autouse == true ? "'ON'" : "'OFF'")),
                                        ("Vứt Bỏ\n" + (player.autodrop == true ? "'ON'" : "'OFF'")),
                                        "Quay lại");
                                break;
                            case 6:
                                Service.gI().sendThongBao(player, "|7|Đã dừng tất cả lệnh Auto");
                                player.soluongmuanhieu = 0;
                                player.idmuanhieu = -1;
                                player.autocso = false;
                                player.autoHP = false;
                                player.autoKI = false;
                                player.autoSD = false;
                                player.autoGiap = false;
                                player.autodau = false;
                                player.autohoisinh = false;
                                player.autodosat = false;
                                player.autonoitai = false;
                                player.autouse = false;
                                player.autodrop = false;
                                break;
                        }

                        break;
                    case ConstNpc.XMAP:
                        switch (select) {
                            case 0:
                                if (player.gender == 0) {
                                    ChangeMapService.gI().changeMapNonSpaceship(player, 21, -1, 320);
                                } else if (player.gender == 1) {
                                    ChangeMapService.gI().changeMapNonSpaceship(player, 22, -1, 320);
                                } else {
                                    ChangeMapService.gI().changeMapNonSpaceship(player, 23, -1, 320);
                                }
                                break;
                            case 1:
                                ChangeMapService.gI().changeMapNonSpaceship(player, 5, -1, 320);
                            case 2:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.MAP_TUONG_LAI, -1,
                                        "|7| Hãy chọn các chức năng auto dưới đây ",
                                        "Nhà Bulma", "Thành phố\nPhía Nam", "Đảo Bale", "Thị Trấn\nGinder", "Võ Đài Cell");
                                break;
                            case 3:
                                ChangeMapService.gI().changeMapNonSpaceship(player, 105, -1, 320);
                                break;
                            case 4:
                                ChangeMapService.gI().changeMapNonSpaceship(player, 79, -1, 320);
                                break;
                            case 5:
                                ChangeMapService.gI().changeMapNonSpaceship(player, 154, -1, 320);
                                break;
                        }
                        break;
                    case ConstNpc.MAP_TUONG_LAI:
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapNonSpaceship(player, 102, -1, 320);
                                break;
                            case 1:
                                ChangeMapService.gI().changeMapNonSpaceship(player, 93, -1, 320);
                                break;
                            case 2:
                                ChangeMapService.gI().changeMapNonSpaceship(player, 94, -1, 320);
                                break;

                            case 3:
                                ChangeMapService.gI().changeMapNonSpaceship(player, 100, -1, 320);
                                break;
                            case 4:
                                ChangeMapService.gI().changeMapNonSpaceship(player, 103, -1, 320);
                                break;
                        }
                        break;
                    case ConstNpc.AUTO_ITEM:
                        switch (select) {
                            case 0:
                                if (player.muanhieu == false) {
                                    Service.gI().sendThongBao(player, "|2|Đã Bật Auto Mua số lượng lớn");
                                    player.muanhieu = true;
                                } else {
                                    Service.gI().sendThongBao(player, "|7|Đã Dừng Auto Buff Mua số lượng lớn");
                                    player.muanhieu = false;
                                }
                                break;
                            case 1:
                                if (player.autouse == false) {
                                    player.autouse = true;
                                    Input.gI().useitem(player);
                                } else {
                                    Service.gI().sendThongBao(player, "|7|Đã dừng auto sử dụng");
                                    player.autouse = false;
                                }
                                break;
                            case 2:
                                if (player.autodrop == false) {
                                    player.autodrop = true;
                                    Input.gI().dropitem(player);
                                } else {
                                    Service.gI().sendThongBao(player, "|7|Đã dừng auto vứt item");
                                    player.autodrop = false;
                                }
                                break;
                        }
                        break;

                    case ConstNpc.MENU_ADMIN:
                        switch (select) {
                            case 0:
                                Manager.gI().reloadShop();
                                Service.getInstance().sendThongBaoOK(player, "load shop thành công");
                                break;
                            case 1:
                                if (player.pet == null) {
                                    PetService.gI().createNormalPet(player, Util.nextInt(0, 2));
                                } else {
                                    if (player.pet.typePet == 1) {
                                        PetService.gI().changePicPet(player, Util.nextInt(0, 2));
                                    } else if (player.pet.typePet == 2) {
                                        PetService.gI().changeMabuPet(player, Util.nextInt(0, 2));
                                    } else if (player.pet.typePet == 3) {
                                        PetService.gI().changegohanzombie(player, Util.nextInt(0, 2));
                                    } else if (player.pet.typePet == 4) {
                                        PetService.gI().changegohanzombie(player, Util.nextInt(0, 2));
                                    }
                                    PetService.gI().changeBerusPet(player, Util.nextInt(0, 2));
                                }
                                break;
                            case 2: //baotri
                                if (player.isAdmin()) {
                                    Maintenance.gI().start(15);

                                }
                                break;
                            case 3: //timkiemplayer
                                Input.gI().bufftien(player);
                                break;
                            case 4: //buffitemoption
                                Input.gI().createFormBuffItemVip(player);
                                break;
                            case 5: //đổi gender
                                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                    if (player.inventory.itemsBody.get(0).quantity < 1
                                            && player.inventory.itemsBody.get(1).quantity < 1
                                            && player.inventory.itemsBody.get(2).quantity < 1
                                            && player.inventory.itemsBody.get(3).quantity < 1
                                            && player.inventory.itemsBody.get(4).quantity < 1) {
                                        player.gender += 1;
                                        if (player.gender > 2) {
                                            player.gender = 0;
                                        }
                                        short[] headtd = {30, 31, 64};
                                        short[] headnm = {9, 29, 32};
                                        short[] headxd = {27, 28, 6};
                                        player.playerSkill.skills.clear();
                                        for (Skill skill : player.playerSkill.skills) {
                                            skill.point = 1;
                                        }
                                        int[] skillsArr = player.gender == 0 ? new int[]{0, 1, 6, 9, 10, 20, 22, 24, 19}
                                                : player.gender == 1 ? new int[]{2, 3, 7, 11, 12, 17, 18, 26, 19}
                                                : new int[]{4, 5, 8, 13, 14, 21, 23, 25, 19};
                                        for (int i = 0; i < skillsArr.length; i++) {
                                            player.playerSkill.skills.add(SkillUtil.createSkill(skillsArr[i], 1));
                                        }
                                        player.playerIntrinsic.intrinsic = IntrinsicService.gI().getIntrinsicById(0);
                                        player.playerIntrinsic.intrinsic.param1 = 0;
                                        player.playerIntrinsic.intrinsic.param2 = 0;
                                        player.playerIntrinsic.countOpen = 0;
                                        switch (player.gender) {
                                            case 0:
                                                player.head = headtd[Util.nextInt(headtd.length)];
                                                break;
                                            case 1:
                                                player.head = headnm[Util.nextInt(headnm.length)];
                                                break;
                                            case 2:
                                                player.head = headxd[Util.nextInt(headxd.length)];
                                                break;
                                            default:
                                                break;
                                        }
                                        Service.gI().sendThongBao(player, "|1|Đổi hành tinh thành công");
                                        Service.gI().player(player);
                                        Service.getInstance().sendFlagBag(player);
                                        Service.getInstance().Send_Caitrang(player);
                                        PlayerService.gI().sendInfoHpMpMoney(player);
                                        Service.gI().Send_Info_NV(player);
                                    } else {
                                        Service.gI().sendThongBao(player, "Tháo hết 5 món đầu đang mặc ra nha");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Balo đầy");
                                }
                                Service.gI().player(player);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.gI().sendMoney(player);
                                break;
                            case 6: //give item
                                Input.gI().createFormGiveItem(player);
                                break;
                            case 7: //buff chỉ số
                                Input.gI().createFrombuffcs(player);
                                break;
                            case 8:
                                GodGK.editgame = true;
                                break;
                            case 9:
                                Input.gI().createFrombuffnv(player);
                                break;
                            case 10:
                                this.createOtherMenu(player, ConstNpc.CALL_BOSS,
                                        "|7|Chọn Boss?", "Full Cụm\nANDROID", "BLACK", "Boss Fusion", "Cụm\nCell",
                                        "Cụm\nDoanh trại", "DOREMON", "FIDE", "FIDE\nBlack", "Cụm\nGINYU", "Cụm\nNAPPA", "NGỤC\nTÙ");
                                break;
                            case 11:
                                TaiXiu.gI().baotri = true;
                                Service.getInstance().sendThongBao(player, "Đã ĐÓNG chức năng tham gia Tài Xĩu");
                                break;
                            case 12:
                                TaiXiu.gI().baotri = false;
                                Service.getInstance().sendThongBao(player, "Đã MỞ chức năng tham gia Tài Xĩu");
                                break;

                        }
                        break; //bán thỏi vàng nhanh         
                    case ConstNpc.CALL_BOSS:
                        switch (select) {
                            case 0:
                                BossManager.gI().createBoss(BossID.ANDROID_13);
                                BossManager.gI().createBoss(BossID.ANDROID_14);
                                BossManager.gI().createBoss(BossID.ANDROID_15);
                                BossManager.gI().createBoss(BossID.ANDROID_19);
                                BossManager.gI().createBoss(BossID.DR_KORE);
                                BossManager.gI().createBoss(BossID.KING_KONG);
                                BossManager.gI().createBoss(BossID.PIC);
                                BossManager.gI().createBoss(BossID.POC);
                                break;
                            case 1:
                                BossManager.gI().createBoss(BossID.BLACK);
                                break;
                            case 2:
                                BossManager.gI().createBoss(BossID.GogetaSJJ4);
                                BossManager.gI().createBoss(BossID.SUPER_ANDROID_17);
                                break;
                            case 3:
                                BossManager.gI().createBoss(BossID.SIEU_BO_HUNG);
                                BossManager.gI().createBoss(BossID.XEN_BO_HUNG);
                                break;
                            case 4:
                                Service.getInstance().sendThongBao(player, "Không có boss");
                                break;
                            case 5:
                                BossManager.gI().createBoss(BossID.CHAIEN);
                                BossManager.gI().createBoss(BossID.XEKO);
                                BossManager.gI().createBoss(BossID.XUKA);
                                BossManager.gI().createBoss(BossID.NOBITA);
                                BossManager.gI().createBoss(BossID.DORAEMON);
                                break;
                            case 6:
                                BossManager.gI().createBoss(BossID.FIDE);
                                break;
                            case 7:
                                BossManager.gI().createBoss(BossID.FIDE_ROBOT);
                                BossManager.gI().createBoss(BossID.VUA_COLD);
                                break;
                            case 8:
                                BossManager.gI().createBoss(BossID.SO_1);
                                BossManager.gI().createBoss(BossID.SO_2);
                                BossManager.gI().createBoss(BossID.SO_3);
                                BossManager.gI().createBoss(BossID.SO_4);
                                BossManager.gI().createBoss(BossID.TIEU_DOI_TRUONG);
                                break;
                            case 9:
                                BossManager.gI().createBoss(BossID.KUKU);
                                BossManager.gI().createBoss(BossID.MAP_DAU_DINH);
                                BossManager.gI().createBoss(BossID.RAMBO);
                                break;
                            case 10:
                                BossManager.gI().createBoss(BossID.COOLER_GOLD);
                                BossManager.gI().createBoss(BossID.CUMBER);
                                BossManager.gI().createBoss(BossID.SONGOKU_TA_AC);
                                break;
                        }
                        break;
                    case ConstNpc.BAN_NHIEU_THOI_VANG:
                        Item thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
                        switch (select) {
                            case 0:
                                if (select == 0 && (thoivang == null || thoivang.quantity < 1) && player.inventory.gold <= 950_000_000_000L) {
                                    Service.gI().sendThongBao(player, "Cần có đủ 1 Thỏi\nvàng để thực hiện");
                                    return;
                                }
                                if (select == 0 && thoivang != null && thoivang.quantity >= 1 && player.inventory.gold > 950_000_000_000L) {
                                    Service.gI().sendThongBao(player, "Số vàng sau khi bán vượt quá 10 tỷ");
                                    return;
                                }
                                if (thoivang != null && thoivang.quantity >= 1 && player.inventory.gold <= 950_000_000_000L) {
                                    player.inventory.gold += 500_000_000;
                                    Service.gI().sendThongBao(player, "|4|Bạn nhận được 500 Triệu Vàng");
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 1);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendMoney(player);
                                    break;
                                }
                                break;
                            case 1:
                                if ((thoivang == null || thoivang.quantity < 5) && player.inventory.gold <= 950_000_000_000L) {
                                    Service.gI().sendThongBao(player, "Cần có đủ 5 Thỏi\nvàng để thực hiện");
                                    return;
                                }
                                if (thoivang != null && thoivang.quantity >= 5 && player.inventory.gold > 950_000_000_000L) {
                                    Service.gI().sendThongBao(player, "Số vàng sau khi bán vượt quá 10 tỷ");
                                    return;
                                }
                                if (thoivang != null && thoivang.quantity >= 5 && player.inventory.gold <= 950_000_000_000L) {
                                    player.inventory.gold += 2_500_000_000L;
                                    Service.gI().sendThongBao(player, "|4|Bạn nhận được 2,5 Tỷ Vàng");
                                    Service.gI().sendMoney(player);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 5);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    break;
                                }
                                break;
                            case 2:
                                if ((thoivang == null || thoivang.quantity < 10) && player.inventory.gold < 950_000_000_000L) {
                                    Service.gI().sendThongBao(player, "Cần có đủ 10 Thỏi\nvàng để thực hiện");
                                    return;
                                }
                                if (thoivang != null && thoivang.quantity >= 10 && player.inventory.gold > 950_000_000_000L) {
                                    Service.gI().sendThongBao(player, "Số vàng sau khi bán vượt quá 10 tỷ");
                                    return;
                                }
                                if (thoivang != null && thoivang.quantity >= 10 && player.inventory.gold <= 950_000_000_000L) {
                                    player.inventory.gold += 5_000_000_000L;
                                    Service.gI().sendThongBao(player, "|4|Bạn nhận được 5 Tỷ Vàng");
                                    Service.gI().sendMoney(player);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 10);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    break;
                                }
                                break;
                            case 3:
                                if ((thoivang == null || thoivang.quantity < 100) && player.inventory.gold <= 950_000_000_000L) {
                                    Service.gI().sendThongBao(player, "Cần có đủ 100 Thỏi\nvàng để thực hiện");
                                    return;
                                }
                                if (thoivang != null && thoivang.quantity >= 100 && player.inventory.gold > 950_000_000_000L) {
                                    Service.gI().sendThongBao(player, "Số vàng sau khi bán vượt quá 1000 tỷ");
                                    return;
                                }
                                if (thoivang != null && thoivang.quantity >= 100 && player.inventory.gold <= 950_000_000_000L) {
                                    player.inventory.gold += 50_000_000_000L;
                                    Service.gI().sendThongBao(player, "|4|Bạn nhận được 50 Tỷ Vàng");
                                    Service.gI().sendMoney(player);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 100);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    break;
                                }
                        }
                        break;
                    case ConstNpc.TAIXIU:
                        String time = ((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) + " giây";
                        int ketqua = TaiXiu.gI().z + TaiXiu.gI().y + TaiXiu.gI().x;
                        if (((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0 && player.goldTai == 0 && player.goldXiu == 0 && TaiXiu.gI().baotri == false) {
                            switch (select) {
                                case 0:
                                    if (TaiXiu.gI().baotri == false) {
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039, "\n|7|---Trò chơi may mắn---\n"
                                                + "\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z + " " + (ketqua >= 10 ? "Tài" : "Xỉu")
                                                + "\n|1|Kết quả kì trước" + "\n"
                                                + "|3| " + TaiXiu.gI().tongHistoryString
                                                + "\n\n|1|Tổng Cược TÀI: " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                                + "\n\n|1|Tổng Cược XỈU: " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n"
                                                + "\n|5|Đếm ngược: " + time, "Cập nhập", "Cược\n'Tài'", "Cược\n'Xỉu' ", "Đóng");
                                    }
                                    break;
                                case 1:
                                    if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0) {
                                        Input.gI().TAI_taixiu(player);
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Bạn chưa đủ điều kiện để chơi");
                                    }
                                    break;
                                case 2:
                                    if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0) {
                                        Input.gI().XIU_taixiu(player);
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Bạn chưa đủ điều kiện để chơi");
                                    }
                                    break;
                            }
                        } else if (((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0 && player.goldTai > 0 && TaiXiu.gI().baotri == false) {
                            switch (select) {
                                case 0:
                                    createOtherMenu(player, ConstNpc.TAIXIU, "\n|7|---Trò chơi may mắn---\n"
                                            + "\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                            + "\n\n|1|Tổng nhà 'Tài'=> " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                            + "\n\n|1|Tổng nhà 'Xỉu'=> " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n"
                                            + "\n|5|Thời gian còn lại: " + time, "Cập nhập", "Cược\n'Tài'", "Cược\n'Xỉu' ", "Đóng");
                                    break;
                            }
                        } else if (((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0 && player.goldXiu > 0 && TaiXiu.gI().baotri == false) {
                            switch (select) {
                                case 0:
                                    createOtherMenu(player, ConstNpc.TAIXIU, "\n|7|---Trò chơi may mắn---\n"
                                            + "\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                            + "\n\n|1|Tổng nhà 'Tài'=> " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                            + "\n\n|1|Tổng nhà 'Xỉu'=> " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n"
                                            + "\n|5|Thời gian còn lại: " + time, "Cập nhập", "Cược\n'Tài'", "Cược\n'Xỉu' ", "Đóng");
                                    break;
                            }
                        } else if (((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0 && player.goldTai > 0 && TaiXiu.gI().baotri == true) {
                            switch (select) {
                                case 0:
                                    createOtherMenu(player, ConstNpc.TAIXIU, "\n|7|---Trò chơi may mắn---\n"
                                            + "\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                            + "\n\n|1|Tổng nhà 'Tài'=> " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                            + "\n\n|1|Tổng nhà 'Xỉu'=> " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n"
                                            + "\n|5|Thời gian còn lại: " + time, "Cập nhập", "Cược\n'Tài'", "Cược\n'Xỉu' ", "Đóng");
                                    break;
                            }
                        } else if (((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0 && player.goldXiu > 0 && TaiXiu.gI().baotri == true) {
                            switch (select) {
                                case 0:
                                    createOtherMenu(player, ConstNpc.TAIXIU, "\n|7|---Trò chơi may mắn---\n"
                                            + "\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                            + "\n\n|1|Tổng nhà 'Tài'=> " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                            + "\n\n|1|Tổng nhà 'Xỉu'=> " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n"
                                            + "\n|5|Thời gian còn lại: " + time, "Cập nhập", "Cược\n'Tài'", "Cược\n'Xỉu' ", "Đóng");
                                    break;
                            }
                        } else if (((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0 && player.goldXiu == 0 && player.goldTai == 0 && TaiXiu.gI().baotri == true) {
                            switch (select) {
                                case 0:
                                    createOtherMenu(player, ConstNpc.TAIXIU, "\n|7|---Trò chơi may mắn---\n"
                                            + "\n|3|Kết quả kì trước:  " + TaiXiu.gI().x + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                            + "\n\n|1|Tổng nhà 'Tài'=> " + Util.format(TaiXiu.gI().goldTai) + " Hồng ngọc"
                                            + "\n\n|1|Tổng nhà 'Xỉu'=> " + Util.format(TaiXiu.gI().goldXiu) + " Hồng ngọc\n"
                                            + "\n|5|Thời gian còn lại: " + time, "Cập nhập", "Cược\n'Tài'", "Cược\n'Xỉu' ", "Đóng");
                                    break;
                            }
                        }
                        break; //đệ tử vip

                    case ConstNpc.NpcThanThu:
                        if (player.TrieuHoiCapBac != -1) {
                            switch (select) {
                                case 0:
                                    Service.gI().showthanthu(player);
                                    break;
                                case 1:
                                    if (player.TrieuHoiThucAn < 200) {
                                        if (player.inventory.ruby < 200) {
                                            Service.gI().sendThongBaoOK(player,
                                                    "Không đủ Hồng ngọc");
                                        } else {
                                            player.TrieuHoiThucAn++;
                                            player.inventory.ruby -= 2000;
                                        }

                                    } else {
                                        Service.gI().sendThongBaoOK(player,
                                                "Thú cưng đã ăn quá no");
                                    }
                                    Service.gI().showthanthu(player);
                                    break;
                                case 2:
                                    if (player.TrieuHoipet != null) {
                                        player.TrieuHoipet.changeStatus(Thu_TrieuHoi.FOLLOW);
                                    }
                                    break;
                                case 3:
                                    if (player.TrieuHoipet != null) {
                                        player.TrieuHoipet.changeStatus(Thu_TrieuHoi.ATTACK_PLAYER);
                                        player.TrieuHoipet.effectSkill.removeSkillEffectWhenDie();
                                        Service.gI().sendThongBao(player, "|2|Thú cưng đã chuyển qua trang thái tấn công người chơi");
                                    }

                                    break;

                                case 4:
                                    if (player.TrieuHoipet != null) {
                                        player.TrieuHoipet.changeStatus(Thu_TrieuHoi.ATTACK_MOB);
                                        Service.gI().sendThongBao(player, "|2|Thú cưng đã chuyển qua trang thái tấn công quái");
                                    }

                                    break;
                                case 5:
                                    if (player.TrieuHoipet != null) {
                                        player.TrieuHoipet.changeStatus(Thu_TrieuHoi.GOHOME);
                                        Service.gI().sendThongBao(player, "|2|Thú cưng đã di chuyển về nhà");

                                    }
                                    break;
                                case 6:
                                    if (player.trangthai == false) {
                                        player.trangthai = true;
                                        if (player.inventory.ruby < 200) {
                                            Service.gI().sendThongBao(player,
                                                    "|7|Không đủ Hồng ngọc");
                                            return;
                                        }
                                        player.inventory.ruby -= 200;
                                        player.TrieuHoiThucAn++;
                                        player.Autothucan = System.currentTimeMillis();
                                        if (player.TrieuHoiThucAn > 200) {
                                            player.TrieuHoiCapBac = -1;
                                            Service.gI().sendThongBao(player,
                                                    "|7|Vì cho thú cưng ăn quá no nên Khỉ con đã bạo thể mà chết.");
                                        } else {
                                            Service.gI().sendThongBao(player,
                                                    "|2|Thức ăn cho khỉ: " + player.TrieuHoiThucAn
                                                    + "%\n|1|Đã cho thú cưng ăn\n|7|Lưu ý: khi cho quá 200% thú cưng sẽ no quá mà chết");
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player,
                                                "|1|Đã dừng Auto cho thú cưng ăn");
                                        player.trangthai = false;
                                    }
                                    break;
                                case 7:
                                    if (player.TrieuHoiCapBac != -1 && player.TrieuHoiCapBac < 10) {
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.DOT_PHA_THANTHU, 12713,
                                                "|7|ĐỘT PHÁ LVL CỦA thú cưng "
                                                + "\n\n|2|Cấp bậc hiện tại: " + player.NameThanthu(player.TrieuHoiCapBac)
                                                + "\n|2|Level: " + player.TrieuHoiLevel
                                                + "\n|2|Kinh nghiệm: " + Util.format(player.TrieuHoiExpThanThu)
                                                + "\n|1| Yêu cầu thú cưng đạt cấp 100"
                                                + "\n\n|7|Cần: " + (player.TrieuHoiCapBac + 1) * 9 + " " + player.DaDotpha(player.TrieuHoiCapBac)
                                                + "\nĐể Đột phá lên lvl " + player.NameThanthu(player.TrieuHoiCapBac + 1)
                                                + "\b\b|3|*Thành công: LVL thú cưng nâng 1 bậc và Level trở về 0"
                                                + "\b|3|*Thất bại: Trừ nguyên liệu Đột phá"
                                                + "\b|6|-----------------------------"
                                                + "\n|7|Tỉ lệ Thành công: " + (100 - player.TrieuHoiCapBac * 10) + "%",
                                                "Đột phá", "Đóng");
                                    } else {
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.DOT_PHA_THANTHU, 12713,
                                                "|7|ĐỘT PHÁ LEVEL "
                                                + "\n\n|2|LEVEL hiện tại: " + player.NameThanthu(player.TrieuHoiCapBac)
                                                + "\n|7| Thú cưng của bạn đã đạt Cấp bậc Cao nhất",
                                                "Đóng");
                                    }
                                    break;
                            }
                        } else {
                            Service.gI().sendThongBao(player, "|7|Bạn chưa có khỉ con để sài tính năng này.");
                        }
                        break;
                    case ConstNpc.DOT_PHA_THANTHU:
                        switch (select) {
                            case 0:
                                Item linhthach = null;
                                try {
                                    if (player.TrieuHoiCapBac != -1 && player.TrieuHoiCapBac >= 0 && player.TrieuHoiCapBac < 4) {
                                        linhthach = InventoryServiceNew.gI().findItemBag(player, 1266);
                                    } else {
                                        linhthach = InventoryServiceNew.gI().findItemBag(player, 1269 - player.TrieuHoiCapBac);
                                    }
                                } catch (Exception e) {
                                    System.out.println("vvvvv");
                                }
                                if (player.TrieuHoiCapBac != -1 && player.TrieuHoiLevel == 100 && player.TrieuHoiCapBac < 10) {
                                    if (linhthach != null && linhthach.quantity >= (player.TrieuHoiCapBac + 1) * 9) {
                                        if (Util.isTrue(100 - player.TrieuHoiCapBac * 10, 100)) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, linhthach, (player.TrieuHoiCapBac + 1) * 9);
                                            player.TrieuHoiLevel = 0;
                                            player.TrieuHoiExpThanThu = 0;
                                            player.TrieuHoiCapBac++;
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            Service.gI().sendThongBao(player, "|2|HAHAHA ĐỘT PHÁ thú cưng THÀNH CÔNG " + player.NameThanthu(player.TrieuHoiCapBac) + " rồi\nTất cả quỳ xuống !!");
                                        } else {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, linhthach, (player.TrieuHoiCapBac + 1) * 9);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            Service.gI().sendThongBao(player, "|7|Khốn khiếp, lại đột phá thất bại rồi");
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "|7| Chưa đủ " + player.DaDotpha(player.TrieuHoiCapBac));
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "|7| Yêu cầu thú cưng đạt Cấp 100");
                                }
                                break;
                        }
                        break;
                    case ConstNpc.INFO_ALL:
                        switch (select) {
                            case 0:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.INFO_ALL, 12713,
                                        "|7|THÔNG TIN NHÂN VẬT"
                                        + "\b\b|7|Tên nhân vật: " + (player.name)
                                        + "\b\b|5|Số tiền: " + Util.format(player.getSession().vnd) + " VNĐ"
                                        + "\b|5|Trang Thái: " + (player.getSession().actived == false ? "Chưa kích hoạt" : " Đã kích hoạt")
                                        + "\b\b|2|Nhiệm vụ: " + Util.numberToMoney(player.playerTask.taskMain.id)
                                        + "\b|2|Sức mạnh: " + Util.numberToMoney(player.nPoint.power)
                                        + "\b|2|Tiềm năng: " + Util.numberToMoney(player.nPoint.tiemNang)
                                        + "\b\b|1|HP: " + Util.powerToStringnew(player.nPoint.hpg)
                                        + "\b|1|KI: " + Util.powerToStringnew(player.nPoint.mpg)
                                        + "\b|1|Sức đánh: " + Util.powerToStringnew(player.nPoint.dameg)
                                        + "\b|1|Giáp : " + Util.powerToStringnew(player.nPoint.defg)
                                        + "\b\b|5|Tổng vàng nhặt: " + Util.format(player.vangnhat)
                                        + "\b|3|Tổng Hồng ngọc nhặt: " + Util.format(player.hngocnhat),
                                        "Thông tin\n nhân vật", "Thông tin\nđệ tử", "Thông tin\nđồ mặc");
                                break;
                            case 1:
                                if (player.pet != null) {
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.INFO_ALL, 12713,
                                            "|7|THÔNG TIN ĐỆ TỬ"
                                            + "\b\b|7|Hành tinh: " + Service.gI().get_HanhTinh(player.pet.gender)
                                            + "\b|2|HP ĐỆ TỬ: " + Util.powerToStringnew(player.pet.nPoint.hp) + "/" + Util.powerToStringnew(player.pet.nPoint.hpMax)
                                            + "\b|2|KI ĐỆ TỬ: " + Util.powerToStringnew(player.pet.nPoint.mp) + "/" + Util.powerToStringnew(player.pet.nPoint.mpMax)
                                            + "\b|2|Sức đánh: " + Util.powerToStringnew(player.pet.nPoint.dame)
                                            + "\b|2|Giáp: " + Util.powerToStringnew(player.pet.nPoint.def)
                                            + "\b\b|1|Sức mạnh: " + Util.powerToStringnew(player.pet.nPoint.power)
                                            + "\b|1|Tiềm năng " + Util.powerToStringnew(player.pet.nPoint.tiemNang),
                                            "Thông tin\n nhân vật", "Thông tin\nđệ tử", "Thông tin\nđồ mặc");
                                } else {
                                    Service.gI().sendThongBao(player, "|7|Bạn chưa có đệ tử");
                                }
                                break;
                            case 2:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.CHISODO, 12713,
                                        "|1|Bạn muốn xem chỉ số đồ bị giới hạn hiện thị:",
                                        "Chỉ số\nô 1", "Chỉ số\nô 2", "Chỉ số\nô 3",
                                        "Chỉ số\nô 4", "Chỉ số\nô 5", "Chỉ số\nô 6",
                                        "Chỉ số\nô 7", "Chỉ số\nô 8", "Chỉ số\nô 9",
                                        "Chỉ số\nô 10", "Chỉ số\nô 11", "Chỉ số\nô 12");
                                break;
                            case 3:
                                NpcService.gI().createMenuConMeo(player, ConstNpc.INFO_ALL, 12679,
                                        "|7|THÔNG TIN CẤP BẬC thú cưng"
                                        + "\b\b|4|-Khỉ con LVL1 : Tăng HP cho Chủ nhân"
                                        + "\b|5|- Khỉ con LVL2: Tăng KI cho Chủ nhân"
                                        + "\b|4| -Khỉ con LVL3  : Tăng Sức đánh cho Chủ nhân"
                                        + "\b|5| -Khỉ con LVL4 : Tăng HP,KI,SD cho Chủ nhân"
                                        + "\b|4|- Khỉ con LVL5: Tăng HP,KI,SD,Giáp cho Chủ nhân"
                                        + "\b|5|- Khỉ con LVL6: Tăng %SD cho Chủ nhân"
                                        + "\b|4|- Khỉ con LVL7: Tăng %SDCM cho Chủ nhân"
                                        + "\b|5|- Khỉ con LVL8: Tăng %HP,KI,Giáp,SD cho Chủ nhân"
                                        + "\b|3|=> Tăng %HP,KI,Giáp,SD cho Chủ nhân. Farm Hồng ngọc"
                                        + "\b\b|7| Lưu ý: Chỉ số cộng thêm cũng như Hồng ngọc tìm được sẽ Tăng theo cấp của khỉ con (MAX: Cấp 100)",
                                        "Thông tin\n nhân vật", "Thông tin\nđệ tử");
                                break;
                        }
                        break;
                    case ConstNpc.CHISODO: {
                        Item it = player.inventory.itemsBody.get(select);
                        if (it.quantity < 1) {
                            NpcService.gI().createMenuConMeo(player, ConstNpc.CHISODO, 12713,
                                    "|7|Ô này không có đồ!!!"
                                    + "\nBạn muốn xem chỉ số đồ bị giới hạn hiện thị:",
                                    "Chỉ số\nô 1", "Chỉ số\nô 2", "Chỉ số\nô 3",
                                    "Chỉ số\nô 4", "Chỉ số\nô 5", "Chỉ số\nô 6",
                                    "Chỉ số\nô 7", "Chỉ số\nô 8", "Chỉ số\nô 9",
                                    "Chỉ số\nô 10", "Chỉ số\nô 11", "Chỉ số\nô 12");
                            return;
                        }
                        NpcService.gI().createMenuConMeo(player, ConstNpc.CHISODO, 12713,
                                "|2|Tên Vật phẩm: " + it.template.name
                                + "\n|7|Chỉ số:"
                                + "\n|1|" + it.getInfo(),
                                "Chỉ số\nô 1", "Chỉ số\nô 2", "Chỉ số\nô 3",
                                "Chỉ số\nô 4", "Chỉ số\nô 5", "Chỉ số\nô 6",
                                "Chỉ số\nô 7", "Chỉ số\nô 8", "Chỉ số\nô 9",
                                "Chỉ số\nô 10", "Chỉ số\nô 11", "Chỉ số\nô 12");
                    }
                    break;
                    case ConstNpc.CHISO_NHANH: //auto cộng chỉ số nhanh
                        switch (select) {
                            case 0:
                                player.autocso = true;
                                player.autoHP = true;
                                Service.gI().sendThongBao(player, "|1|Bạn đang Auto cộng chỉ số HP");
                                run.scheduleAtFixedRate(() -> {
                                    if (player.autoHP == true && player.nPoint != null) {
                                        player.nPoint.increasePoint((byte) 0, (short) 20);
                                    }
                                }, 0, 1, TimeUnit.MILLISECONDS);
                                break;
                            case 1:
                                player.autocso = true;
                                player.autoKI = true;
                                Service.gI().sendThongBao(player, "|1|Bạn đang Auto cộng chỉ số KI");
                                run.scheduleAtFixedRate(() -> {
                                    if (player.autoKI == true && player.nPoint != null) {
                                        player.nPoint.increasePoint((byte) 1, (short) 20);
                                    }
                                }, 0, 1, TimeUnit.MILLISECONDS);
                                break;
                            case 2:
                                player.autocso = true;
                                player.autoSD = true;
                                Service.gI().sendThongBao(player, "|1|Bạn đang Auto cộng chỉ số Sức đánh");
                                run.scheduleAtFixedRate(() -> {
                                    if (player.autoSD == true && player.nPoint != null) {
                                        player.nPoint.increasePoint((byte) 2, (short) 10);
                                    }
                                }, 0, 1, TimeUnit.MILLISECONDS);
                                break;
                            case 3:
                                player.autocso = true;
                                player.autoGiap = true;
                                Service.gI().sendThongBao(player, "|1|Bạn đang Auto cộng chỉ số Giáp");
                                run.scheduleAtFixedRate(() -> {
                                    if (player.autoGiap == true && player.nPoint != null) {
                                        player.nPoint.increasePoint((byte) 3, (short) 1);
                                    }
                                }, 0, 1, TimeUnit.MILLISECONDS);
                                break;

                        }
                        break;
                    case ConstNpc.menutd:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().settaiyoken(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setgenki(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setkamejoko(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.menunm:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().setgodki(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setgoddam(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setsummon(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.menuxd:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().setgodgalick(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setmonkey(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setgodhp(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.CONFIRM_DISSOLUTION_CLAN:
                        switch (select) {
                            case 0:
                                Clan clan = player.clan;
                                clan.deleteDB(clan.id);
                                Manager.CLANS.remove(clan);
                                player.clan = null;
                                player.clanMember = null;
                                ClanService.gI().sendMyClan(player);
                                ClanService.gI().sendClanId(player);
                                Service.getInstance().sendThongBao(player, "Đã giải tán bang hội.");
                                break;
                        }
                        break;

                    case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND:
                        if (select == 0) {
                            for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                                player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
                            }
                            player.inventory.itemsBoxCrackBall.clear();
                            Service.getInstance().sendThongBao(player, "Đã xóa hết vật phẩm trong rương");
                        }
                        break; //tìm kiếm player

                    case ConstNpc.MENU_QUAN_LY_PLAYER:
                        switch (select) {
                            case 0:
                                this.createOtherMenu(player, ConstNpc.MENU_FIND_PLAYER,
                                        "|7|Ngài muốn làm gì ạ?", "Gọi", "Tele", "Đổi Tên", "Kick"
                                );
                                break;
                            case 1:
                                this.createOtherMenu(player, ConstNpc.TANG_DE_TU,
                                        "|7|Ngài muốn làm gì?", "Đệ Thường",
                                        "Đệ Bư", "Đệ Beerus", ""
                                        + "Đệ Siêu Broly", "Đệ Khỉ ",
                                        "Đệ Fusion", "Đệ 1",
                                        "Đệ 2", "Đệ 3",
                                        "Đệ 4", "Đệ 5"
                                );
                                break;
                            case 2: //baotri

                                break;
                            case 3: //timkiemplayer

                                this.createOtherMenu(player, ConstNpc.CALL_BOSS,
                                        "|7|Chọn Boss?", "Full Cụm\nANDROID", "BLACK", "Boss Fusion", "Cụm\nCell",
                                        "Cụm\nDoanh trại", "DOREMON", "FIDE", "FIDE\nBlack", "Cụm\nGINYU", "Cụm\nNAPPA", "NGỤC\nTÙ");
                                break;

                        }
                        break; //bán thỏi vàng nhanh   
                    case ConstNpc.NRO_SIEU_CAP:
                        Item nrosc = InventoryServiceNew.gI().findItemBag(player, 1015);
                        switch (select) {
                            case 0:
                                break;

                        }
                        break;
                    case ConstNpc.MENU_FIND_PLAYER:
                        Player p = (Player) PLAYERID_OBJECT.get(player.id);
                        if (p != null) {
                            switch (select) {
                                case 0:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMapYardrat(player, p.zone, p.location.x, p.location.y);

                                    }
                                    break;
                                case 1:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMap(p, player.zone, player.location.x, player.location.y);
                                    }
                                    break;
                                case 2:
                                    Input.gI().createFormChangeName(player, p);
                                    break;
                                case 3:
                                    String[] selects = new String[]{"Đồng ý", "Hủy"};
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, -1,
                                            "Bạn có chắc chắn muốn ban " + p.name, selects, p);
                                    break;
                                case 4:
                                    Service.getInstance().sendThongBao(player, "Kik người chơi " + p.name + " thành công");
                                    Client.gI().getPlayers().remove(p);
                                    Client.gI().kickSession(p.getSession());
                                    break;
                                case 5: //buff
                                    Input.gI().buffitem(player, p);
                                    break;
                            }
                        }
                        break;
                    case ConstNpc.TANG_DIEM_SB:
                        Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                        int select1 = 10;
                        int select2 = 30;
                        int select3 = 50;
                        int select4 = 80;
                        int select5 = 100;
                        int select6 = 150;
                        int select7 = 200;
                        if (pl != null) {
                            switch (select) {
                                case 0:
                                    if (player.pointsb >= select1) {
                                        player.pointsb -= select1;
                                        pl.inventory.event += select1;
                                        Service.gI().sendThongBaoOK(player, "Đã tặng thành công " + select1 + " điểm săn boss cho " + pl.name);
                                        Service.gI().sendThongBaoOK(pl, "Bạn đã được " + player.name + " tặng " + select1 + " điểm săn boss");
                                    } else {
                                        Service.gI().sendThongBaoOK(player, "Không đủ điểm để tặng");
                                    }
                                    break;
                                case 1:
                                    if (player.pointsb >= select2) {
                                        player.pointsb -= select2;
                                        pl.inventory.event += select2;
                                        Service.gI().sendThongBaoOK(player, "Đã tặng thành công " + select2 + " điểm săn boss cho " + pl.name);
                                        Service.gI().sendThongBaoOK(pl, "Bạn đã được " + player.name + " tặng " + select2 + " điểm săn boss");
                                    } else {
                                        Service.gI().sendThongBaoOK(player, "Không đủ điểm để tặng");
                                    }
                                    break;
                                case 2:
                                    if (player.pointsb >= select3) {
                                        player.pointsb -= select3;
                                        pl.inventory.event += select3;
                                        Service.gI().sendThongBaoOK(player, "Đã tặng thành công " + select3 + " điểm săn boss cho " + pl.name);
                                        Service.gI().sendThongBaoOK(pl, "Bạn đã được " + player.name + " tặng " + select3 + " điểm săn boss");
                                    } else {
                                        Service.gI().sendThongBaoOK(player, "Không đủ điểm để tặng");
                                    }
                                    break;
                                case 3:
                                    if (player.pointsb >= select4) {
                                        player.pointsb -= select4;
                                        pl.inventory.event += select4;
                                        Service.gI().sendThongBaoOK(player, "Đã tặng thành công " + select4 + " điểm săn boss cho " + pl.name);
                                        Service.gI().sendThongBaoOK(player, "Bạn đã được " + player.name + " tặng " + select4 + " điểm săn boss");
                                    } else {
                                        Service.gI().sendThongBaoOK(pl, "Không đủ điểm để tặng");
                                    }
                                    break;
                                case 4:
                                    if (player.pointsb >= select5) {
                                        player.pointsb -= select5;
                                        pl.inventory.event += select5;
                                        Service.gI().sendThongBaoOK(player, "Đã tặng thành công " + select5 + " điểm săn boss cho " + pl.name);
                                        Service.gI().sendThongBaoOK(pl, "Bạn đã được " + player.name + " tặng " + select5 + " điểm săn boss");
                                    } else {
                                        Service.gI().sendThongBaoOK(player, "Không đủ điểm để tặng");
                                    }
                                    break;
                                case 5:
                                    if (player.pointsb >= select6) {
                                        player.pointsb -= select6;
                                        pl.inventory.event += select6;
                                        Service.gI().sendThongBaoOK(player, "Đã tặng thành công " + select6 + " điểm săn boss cho " + pl.name);
                                        Service.gI().sendThongBaoOK(pl, "Bạn đã được " + player.name + " tặng " + select6 + " điểm săn boss");
                                    } else {
                                        Service.gI().sendThongBaoOK(player, "Không đủ điểm để tặng");
                                    }
                                    break;
                                case 6:
                                    if (player.pointsb >= select7) {
                                        player.pointsb -= select7;
                                        pl.inventory.event += select7;
                                        Service.gI().sendThongBaoOK(player, "Đã tặng thành công " + select7 + " điểm săn boss cho " + pl.name);
                                        Service.gI().sendThongBaoOK(pl, "Bạn đã được " + player.name + " tặng " + select7 + " điểm săn boss");
                                    } else {
                                        Service.gI().sendThongBaoOK(player, "Không đủ điểm để tặng");
                                    }
                                    break;

                            }
                        }
                        break;
                    case ConstNpc.TANG_DE_TU:
                        Player onPlayer = (Player) PLAYERID_OBJECT.get(player.id);
                        if (onPlayer != null) {
                            int gender = onPlayer.gender;
                            switch (select) {
                                case 0:
                                    PetService.gI().changeNormalPet(onPlayer, gender);
                                    break;
                                case 1:
                                    if (player.pet != null) {
                                        PetService.gI().changeMabuPet(onPlayer, gender);
                                        break;
                                    }
                                    break;
                                case 2:
                                    if (player.pet != null) {
                                        PetService.gI().changeBerusPet(onPlayer, gender);
                                        break;
                                    }
                                    break;
                                case 3:
                                    if (player.pet != null) {
                                        PetService.gI().changePicPet(onPlayer, gender);
                                        break;
                                    }
                                    break;
                                case 4:
                                    if (player.pet != null) {
                                        PetService.gI().changegohanzombie(onPlayer, gender);
                                        break;
                                    }
                                    break;
                                case 5:
                                    if (player.pet != null) {
                                        PetService.gI().changeGokuEVO(onPlayer, gender);
                                        break;
                                    }
                                    break;
                                case 6:
                                    if (player.pet != null) {
                                        PetService.gI().changemabulv2(onPlayer, gender);
                                        break;
                                    }
                                    break;
                                case 7:
                                    if (player.pet != null) {
                                        PetService.gI().changhuydietlv2(onPlayer, gender);
                                        break;
                                    }
                                    break;
                                case 8:
                                    if (player.pet != null) {
                                        PetService.gI().changsuperlv2(onPlayer, gender);
                                        break;
                                    }
                                    break;
                                case 9:
                                    if (player.pet != null) {
                                        PetService.gI().changwukonglv2(onPlayer, gender);
                                        break;
                                    }
                                    break;
                                case 10:
                                    if (player.pet != null) {
                                        PetService.gI().changefusionlv2(onPlayer, gender);
                                        break;
                                    }
                                    break;
                            }
                            break;
                        }
                        break;

                    case ConstNpc.MENU_EVENT:
                        switch (select) {
                            case 0:
                                Service.getInstance().sendThongBaoOK(player, "Điểm sự kiện: " + player.pointsb + " ngon ngon...");
                                break;
                            case 1:
                                Service.gI().showListTop(player, Manager.topSD);
                                break;
                            case 2:
                                Service.getInstance().sendThongBao(player, "Sự kiện đã kết thúc...");
//                                NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_GIAO_BONG, -1, "Người muốn giao bao nhiêu bông...",
//                                        "100 bông", "1000 bông", "10000 bông");
                                break;
                            case 3:
                                Service.getInstance().sendThongBao(player, "Sự kiện đã kết thúc...");
//                                NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DOI_THUONG_SU_KIEN, -1, "Con có thực sự muốn đổi thưởng?\nPhải giao cho ta 3000 điểm sự kiện đấy... ",
//                                        "Đồng ý", "Từ chối");
                                break;

                        }
                        break;
                    case ConstNpc.MENU_GIAO_BONG:
                        ItemService.gI().giaobong(player, (int) Util.tinhLuyThua(10, select + 2));
                        break;
                    case ConstNpc.CONFIRM_DOI_THUONG_SU_KIEN:
                        if (select == 0) {
                            ItemService.gI().openBoxVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_TELE_NAMEC:
                        if (select == 0) {
                            NgocRongNamecService.gI().teleportToNrNamec(player);
                            player.inventory.subGemAndRuby(50);
                            Service.getInstance().sendMoney(player);
                        }
                        break;
                }
            }
        };
    }
}
