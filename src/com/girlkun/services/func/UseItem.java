package com.girlkun.services.func;

import com.girlkun.models.card.Card;
import com.girlkun.models.card.RadarCard;
import com.girlkun.models.card.RadarService;
import com.girlkun.consts.ConstMap;
import com.girlkun.models.item.Item;
import com.girlkun.consts.ConstNpc;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.consts.ConstTask;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.item.Item.ItemOption;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Inventory;
import com.girlkun.services.NpcService;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.network.io.Message;
import com.girlkun.server.Manager;
import com.girlkun.utils.SkillUtil;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import com.girlkun.server.io.MySession;
import com.girlkun.services.ItemService;
import com.girlkun.services.ItemTimeService;
import com.girlkun.services.PetService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.TaskService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.MapService;
import com.girlkun.services.NgocRongNamecService;
import com.girlkun.services.RewardService;

import com.girlkun.utils.Logger;
import com.girlkun.utils.TimeUtil;
import java.util.Date;
import java.util.Random;

//import static sun.audio.AudioPlayer.player;
public class UseItem {

    private static final int ITEM_BOX_TO_BODY_OR_BAG = 0;
    private static final int ITEM_BAG_TO_BOX = 1;
    private static final int ITEM_BODY_TO_BOX = 3;
    private static final int ITEM_BAG_TO_BODY = 4;
    private static final int ITEM_BODY_TO_BAG = 5;
    private static final int ITEM_BAG_TO_PET_BODY = 6;
    private static final int ITEM_BODY_PET_TO_BAG = 7;

    private static final byte DO_USE_ITEM = 0;
    private static final byte DO_THROW_ITEM = 1;
    private static final byte ACCEPT_THROW_ITEM = 2;
    private static final byte ACCEPT_USE_ITEM = 3;
    public static final int[][][] LIST_ITEM_CLOTHES = {
        // áo , quần , găng ,giày,rada
        //td -> nm -> xd
        {{0, 33, 3, 34, 136, 137, 138, 139, 230, 231, 232, 233, 555}, {6, 35, 9, 36, 140, 141, 142, 143, 242, 243, 244, 245, 556}, {21, 24, 37, 38, 144, 145, 146, 147, 254, 255, 256, 257, 562}, {27, 30, 39, 40, 148, 149, 150, 151, 266, 267, 268, 269, 563}, {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561}},
        {{1, 41, 4, 42, 152, 153, 154, 155, 234, 235, 236, 237, 557}, {7, 43, 10, 44, 156, 157, 158, 159, 246, 247, 248, 249, 558}, {22, 46, 25, 45, 160, 161, 162, 163, 258, 259, 260, 261, 564}, {28, 47, 31, 48, 164, 165, 166, 167, 270, 271, 272, 273, 565}, {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561}},
        {{2, 49, 5, 50, 168, 169, 170, 171, 238, 239, 240, 241, 559}, {8, 51, 11, 52, 172, 173, 174, 175, 250, 251, 252, 253, 560}, {23, 53, 26, 54, 176, 177, 178, 179, 262, 263, 264, 265, 566}, {29, 55, 32, 56, 180, 181, 182, 183, 274, 275, 276, 277, 567}, {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561}}
    };

    private static UseItem instance;

    private UseItem() {

    }

    public static UseItem gI() {
        if (instance == null) {
            instance = new UseItem();
        }
        return instance;
    }

    /**
     * Xử lý việc nhận vật phẩm từ tin nhắn.
     *
     * @param session Đối tượng MySession của người chơi.
     * @param msg Tin nhắn chứa thông tin về loại và vị trí của vật phẩm.
     */
    public void getItem(MySession session, Message msg) {
        // Lấy đối tượng người chơi từ phiên MySession
        Player player = session.player;

        // Hủy giao dịch nếu có
        TransactionService.gI().cancelTrade(player);

        try {
            // Đọc loại và vị trí của vật phẩm từ tin nhắn
            int type = msg.reader().readByte();
            int index = msg.reader().readByte();

            // Nếu vị trí là -1, thoát khỏi phương thức
            if (index == -1) {
                return;
            }

            // Xử lý theo loại vật phẩm
            switch (type) {
                case ITEM_BOX_TO_BODY_OR_BAG:
                    InventoryServiceNew.gI().itemBoxToBodyOrBag(player, index);
                    TaskService.gI().checkDoneTaskGetItemBox(player);
                    break;
                case ITEM_BAG_TO_BOX:
                    InventoryServiceNew.gI().itemBagToBox(player, index);
                    break;
                case ITEM_BODY_TO_BOX:
                    InventoryServiceNew.gI().itemBodyToBox(player, index);
                    break;
                case ITEM_BAG_TO_BODY:
                    InventoryServiceNew.gI().itemBagToBody(player, index);
                    break;
                case ITEM_BODY_TO_BAG:
                    InventoryServiceNew.gI().itemBodyToBag(player, index);
                    break;
                case ITEM_BAG_TO_PET_BODY:
                    InventoryServiceNew.gI().itemBagToPetBody(player, index);
                    break;
                case ITEM_BODY_PET_TO_BAG:
                    InventoryServiceNew.gI().itemPetBodyToBag(player, index);
                    break;
                // Các case khác có thể được thêm vào nếu cần
            }

            // Cài đặt trang phục của người chơi
            player.setClothes.setup();

            // Cài đặt trang phục của thú cưng nếu có
            if (player.pet != null) {
                player.pet.setClothes.setup();
            }

            // Cập nhật thông tin nhóm của người chơi
            player.setClanMember();

            // Cập nhật điểm của người chơi
            Service.getInstance().point(player);

        } catch (Exception e) {
            // Ghi log nếu có lỗi xảy ra trong quá trình xử lý
            Logger.logException(UseItem.class, e);
        }
    }

    public void testItem(Player player, Message _msg) {
        TransactionService.gI().cancelTrade(player);
        Message msg;
        try {
            byte type = _msg.reader().readByte();
            int where = _msg.reader().readByte();
            int index = _msg.reader().readByte();
            System.out.println("type: " + type);
            System.out.println("where: " + where);
            System.out.println("index: " + index);
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);
        }
    }

    public void doItem(Player player, Message _msg) {
        TransactionService.gI().cancelTrade(player);
        Message msg;
        byte type;
        try {
            type = _msg.reader().readByte();
            int where = _msg.reader().readByte();
            int index = _msg.reader().readByte();
//            System.out.println(type + " " + where + " " + index);
            switch (type) {
                case DO_USE_ITEM:
                    if (player != null && player.inventory != null) {
                        if (index != -1) {
                            Item item = player.inventory.itemsBag.get(index);
                            if (item.isNotNullItem()) {
                                if (item.template.type == 7) {
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc chắn học " + player.inventory.itemsBag.get(index).template.name + "?");
                                    player.sendMessage(msg);
                                } else {
                                    UseItem.gI().useItem(player, item, index);
                                }
                            }
                        } else {
                            this.eatPea(player);
                        }
                    }
                    break;
                case DO_THROW_ITEM:
                    if (!(player.zone.map.mapId == 21 || player.zone.map.mapId == 22 || player.zone.map.mapId == 23)) {
                        Item item = null;
                        if (where == 0) {
                            item = player.inventory.itemsBody.get(index);
                        } else {
                            item = player.inventory.itemsBag.get(index);
                        }
                        msg = new Message(-43);
                        msg.writer().writeByte(type);
                        msg.writer().writeByte(where);
                        msg.writer().writeByte(index);
                        msg.writer().writeUTF("Bạn chắc chắn muốn vứt " + item.template.name + "?");
                        player.sendMessage(msg);
                    } else {
                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    }
                    break;
                case ACCEPT_THROW_ITEM:
                    InventoryServiceNew.gI().throwItem(player, where, index);
                    Service.getInstance().point(player);
                    InventoryServiceNew.gI().sendItemBags(player);
                    break;
                case ACCEPT_USE_ITEM:
                    UseItem.gI().useItem(player, player.inventory.itemsBag.get(index), index);
                    break;
            }
        } catch (Exception e) {
//            Logger.logException(UseItem.class, e);
        }
    }

    public void useItem(Player pl, Item item, int indexBag) {
        if (item.template.strRequire <= pl.nPoint.power) {
            switch (item.template.type) {
//         
                case 7: //sách học, nâng skill
                    learnSkill(pl, item);
                    break;
                case 33:
                    UseCard(pl, item);
                    break;
                case 6: //đậu thần
                    this.eatPea(pl);
                    break;
                case 12: //ngọc rồng các loại
                    controllerCallRongThan(pl, item);
                    break;
                case 23: //thú cưỡi mới
                case 24: //thú cưỡi cũ
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    break;
                case 11: //item bag
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    Service.getInstance().sendFlagBag(pl);
                    break;
                case 17: //item bag
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    Service.getInstance().sendFlagBag(pl);
                    break;
                case 74:
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    Service.gI().sendFoot(pl, item.template.id);
                    break;
                case 75:
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    Service.getInstance().sendchienlinh(pl, (short) (item.template.iconID - 1));
                    break;
                case 18:
                    if (pl.newpet != null) {
                        ChangeMapService.gI().exitMap(pl.newpet);
                        pl.newpet.dispose();
                        pl.newpet = null;
                    }
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    PetService.Pet2(pl, item.template.head, item.template.body, item.template.leg, item.template.name);
                    Service.getInstance().point(pl);
                    break;
                case 72: {
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    Service.getInstance().sendPetFollow(pl, (short) (item.template.iconID - 1));
                    break;
                }
                default:
                    switch (item.template.id) {
                        case 457:
                            Input.gI().thoivang(pl);
                            break;
                        case 1132:
                            Input.gI().tanghongngoc(pl);
                            break;
                        case 992:
                            pl.type = 1;
                            pl.maxTime = 5;
                            Service.getInstance().Transport(pl);
                            Service.gI().clearMap(pl);
                            break;
                        case 1322:
                            pl.type = 1;
                            pl.maxTime = 6;
                            Service.getInstance().Transport(pl);
                            Service.gI().clearMap(pl);
                            break;
                        case 361:
                            if (pl.idNRNM != -1) {
                                Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                                return;
                            }
                            pl.idGo = (short) Util.nextInt(0, 6);
                            NpcService.gI().createMenuConMeo(pl, ConstNpc.CONFIRM_TELE_NAMEC, -1, "1 Sao (" + NgocRongNamecService.gI().getDis(pl, 0, (short) 353) + " m)\n2 Sao (" + NgocRongNamecService.gI().getDis(pl, 1, (short) 354) + " m)\n3 Sao (" + NgocRongNamecService.gI().getDis(pl, 2, (short) 355) + " m)\n4 Sao (" + NgocRongNamecService.gI().getDis(pl, 3, (short) 356) + " m)\n5 Sao (" + NgocRongNamecService.gI().getDis(pl, 4, (short) 357) + " m)\n6 Sao (" + NgocRongNamecService.gI().getDis(pl, 5, (short) 358) + " m)\n7 Sao (" + NgocRongNamecService.gI().getDis(pl, 6, (short) 359) + " m)", "Đến ngay\nViên " + (pl.idGo + 1) + " Sao\n50 ngọc", "Kết thức");
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            InventoryServiceNew.gI().sendItemBags(pl);
                            break;
                        case 211: //nho tím
                        case 212: //nho xanh
                            eatGrapes(pl, item);
                            break;
                        case 1105://hop qua skh, item 2002 xd
                            UseItem.gI().Hopts(pl, item);
                        case 1015:
                            break;
                        case 342:
                        case 343:
                        case 344:
                        case 345:
                            if (pl.zone.items.stream().filter(it -> it != null && it.itemTemplate.type == 22).count() < 5) {
                                Service.getInstance().DropVeTinh(pl, item, pl.zone, pl.location.x, pl.location.y);
                                InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            } else {
                                Service.getInstance().sendThongBao(pl, "Đặt ít thôi con");
                            }
                            break;
                        case 380: //cskb
                            openCSKB(pl, item);
                            break;
                        case 1296: //cskb
                            maydoboss1(pl, item);
                            break;
                        case 1456://cskb
                            NpcService.gI().createMenuConMeo(pl, ConstNpc.DOBOSS, 0, "Bạn có muốn sử dụng dò boss vĩnh viễn không ?"
                                    + "\n Chat 'a' Để sử dụng chức năng", "Có", "Không");
                            PlayerDAO.updatechucnang(pl);
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            break;
                        case 1482: //thú cưng
                            Input.gI().TAOPET(pl);
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            InventoryServiceNew.gI().sendItemBags(pl);
                            break;
                        case 668: //hopquatanthu
                            hopquatanthu(pl, item);
                            break;
                        case 1334: //hộp đồ thần linh
                            hopthanlinh(pl, item);
                            break;
                        case 2007: //hộp đồ thần linh
                            hopnoel(pl, item);
                            break;

                        case 722: //cskb
                            openCSH(pl, item);
                            break;
                        case 570: //cskb
                            openWoodChest(pl, item);
                            break;
                        case 381: //cuồng nộ
                        case 382: //bổ huyết
                        case 383: //bổ khí
                        case 384: //giáp xên
                        case 385: //ẩn danh
                        case 379: //máy dò capsule
                        case 1990: //máy dò kẹo
                        case 1201:

                        case 663: //bánh pudding
                        case 664: //xúc xíc
                        case 665: //kem dâu
                        case 666: //mì ly
                        case 667: //sushi
                        case 579: //duoi khi
                        case 1099:
                        case 1100:
                        case 1101:
                        case 1102:
                        case 1103:
                        case 899:
                        case 1317:
                        case 465:
                        case 466:
                        case 472:
                        case 473:
                            useItemTime(pl, item);
                            break;
                        case 521: //tdlt
                            useTDLT(pl, item);
                            break;
                        case 454:
                        case 921:
                        case 1165:
                        case 1129:
                            useporata(pl, item);                            
                            break;
                        case 193: //gói 10 viên capsule
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                        case 194: //capsule đặc biệt
                            openCapsuleUI(pl);
                            break;
                        case 1241: //cskb
                            doiskill4(pl, item);
                            break;
                        case 401: //đổi đệ tử
                            changePet(pl, item);
                            break;
                        case 1108: //đổi đệ tử
                            changePetBerus(pl, item);
                            break;
                        case 543: //đổi đệ tử
                        {
                            if (InventoryServiceNew.gI().getCountEmptyBag(pl) == 0) {
                                Service.getInstance().sendThongBao(pl, "Hành trang không đủ chỗ trống");
                            } else {
                                int[] pet = new int[]{942, 1180, 1181, 1196, 1197, 1198, 1107, 1140};
                                int randomPet = new Random().nextInt(pet.length);
                                InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                                Item linhThu = ItemService.gI().createNewItem((short) pet[randomPet]);
                                linhThu.itemOptions.add(new Item.ItemOption(50, Util.nextInt(2, 8)));
                                linhThu.itemOptions.add(new Item.ItemOption(77, Util.nextInt(2, 8)));
                                linhThu.itemOptions.add(new Item.ItemOption(103, Util.nextInt(2, 8)));
                                linhThu.itemOptions.add(new Item.ItemOption(80, Util.nextInt(2, 8)));
                                linhThu.itemOptions.add(new Item.ItemOption(81, Util.nextInt(2, 8)));
                                if (Util.isTrue(98, 100)) {
                                    linhThu.itemOptions.add(new Item.ItemOption(93, Util.nextInt(3, 5)));
                                }
                                InventoryServiceNew.gI().addItemBag(pl, linhThu);
                                InventoryServiceNew.gI().sendItemBags(pl);
                                Service.getInstance().sendThongBao(pl, "Chúc mừng bạn nhận được Pet " + linhThu.template.name);
                            }
                            break;
                        }
                        case 1690:
                            changePetPic(pl, item);
                            break;
                        case 542: //đổi đệ tử
                            changePetPic(pl, item);
                            break;
                        case 402: //sách nâng chiêu 1 đệ tử
                        case 403: //sách nâng chiêu 2 đệ tử
                        case 404: //sách nâng chiêu 3 đệ tử
                        case 759: //sách nâng chiêu 4 đệ tử
                            upSkillPet(pl, item);
                            break;
                        case 2000://hop qua skh, item 2000 td
                        case 2001://hop qua skh, item 2001 nm
                        case 2002://hop qua skh, item 2002 xd
                            UseItem.gI().ItemSKH(pl, item);
                            break;
                        case 1531: //random linthu vip
                            UseItem.gI().Openhoplinhthu(pl, item);
                            break;
                        case 1532: //random thucuoivip
                            UseItem.gI().Openhopthucuoi(pl, item);
                            break;
                        case 1528:
                            UseItem.gI().Openhopdanhhieu(pl, item);
                            break;
                        case 1988://hop qua skh, item 2005 xd
                            UseItem.gI().tangdiemboss(pl, item);
                            break;
                        case 2003://hop qua skh, item 2003 td
                        case 2004://hop qua skh, item 2004 nm
                        case 2005://hop qua skh, item 2005 xd
                            UseItem.gI().ItemDHD(pl, item);
                            break;
                        case 1533://hop qua skh, item 2003 td
                        case 1534://hop qua skh, item 2004 nm
                        case 1535://hop qua skh, item 2005 xd
                            UseItem.gI().ItemDTL(pl, item);
                            break;
                        case 1991:
                            UseItem.gI().nextnv(pl, item);
                            break;
                        case 1530:
                            UseItem.gI().opendetuvip(pl, item);
                            break;
                        case 736:
                            ItemService.gI().OpenItem736(pl, item);
                            break;
                        case 1237: //cskb
                            openphapsu(pl, item);
                            break;
                        case 1335: //bánh trung thu
                        case 1336:
                        case 1337:
                            banhtrungthu(pl, item);
                            break;
                        case 1342:
                            hoptrungthu(pl, item);
                            break;
                        case 987:
                            Service.getInstance().sendThongBao(pl, "Bảo vệ trang bị không bị rớt cấp"); //đá bảo vệ
                            break;
                        case 2006:
                            Input.gI().createFormChangeNameByItem(pl);
                            break;
                        case 2028: {
                            if (InventoryServiceNew.gI().getCountEmptyBag(pl) == 0) {
                                Service.getInstance().sendThongBao(pl, "Hành trang không đủ chỗ trống");
                            } else {
                                int[] pet = new int[]{2019, 2020, 2021, 2022, 2023, 2024, 2025, 2026, 2033, 2034, 2036, 2037, 2038, 2039, 2040};
                                int randomPet = new Random().nextInt(pet.length);
                                InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                                Item linhThu = ItemService.gI().createNewItem((short) pet[randomPet]);
                                linhThu.itemOptions.add(new Item.ItemOption(50, Util.nextInt(1, 5)));
                                linhThu.itemOptions.add(new Item.ItemOption(77, Util.nextInt(1, 5)));
                                linhThu.itemOptions.add(new Item.ItemOption(103, Util.nextInt(1, 5)));
                                linhThu.itemOptions.add(new Item.ItemOption(95, Util.nextInt(5, 8)));
                                linhThu.itemOptions.add(new Item.ItemOption(96, Util.nextInt(5, 8)));
                                InventoryServiceNew.gI().addItemBag(pl, linhThu);
                                InventoryServiceNew.gI().sendItemBags(pl);
                                Service.getInstance().sendThongBao(pl, "Chúc mừng bạn nhận được Linh thú " + linhThu.template.name);
                            }
                            break;
                        }
                        case 2027: {
                            if (InventoryServiceNew.gI().getCountEmptyBag(pl) == 0) {
                                Service.getInstance().sendThongBao(pl, "Hành trang không đủ chỗ trống");
                            } else {
                                InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                                Item linhThu = ItemService.gI().createNewItem((short) Util.nextInt(1273, 1295));
                                linhThu.itemOptions.add(new Item.ItemOption(50, Util.nextInt(2, 8)));
                                linhThu.itemOptions.add(new Item.ItemOption(77, Util.nextInt(2, 8)));
                                linhThu.itemOptions.add(new Item.ItemOption(103, Util.nextInt(2, 8)));
                                linhThu.itemOptions.add(new Item.ItemOption(95, Util.nextInt(1, 8)));
                                linhThu.itemOptions.add(new Item.ItemOption(96, Util.nextInt(1, 1)));
                                linhThu.itemOptions.add(new Item.ItemOption(30, 1));
                                InventoryServiceNew.gI().addItemBag(pl, linhThu);
                                InventoryServiceNew.gI().sendItemBags(pl);
                                Service.getInstance().sendThongBao(pl, "Chúc mừng bạn nhận được Linh thú " + linhThu.template.name);
                            }
                            break;
                        }
                    }
                    break;
            }

            InventoryServiceNew.gI()
                    .sendItemBags(pl);
        } else {
            Service.getInstance().sendThongBaoOK(pl, "Sức mạnh không đủ yêu cầu");
        }
    }

    private void useItemChangeFlagBag(Player player, Item item) {
        switch (item.template.id) {
            case 994: //vỏ ốc
                break;
            case 995: //cây kem
                break;
            case 996: //cá heo
                break;
            case 997: //con diều
                break;
            case 998: //diều rồng
                break;
            case 999: //mèo mun
                if (!player.effectFlagBag.useMeoMun) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useMeoMun = !player.effectFlagBag.useMeoMun;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 1000: //xiên cá
                if (!player.effectFlagBag.useXienCa) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useXienCa = !player.effectFlagBag.useXienCa;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 1001: //phóng heo
                if (!player.effectFlagBag.usePhongHeo) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.usePhongHeo = !player.effectFlagBag.usePhongHeo;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
        }
        Service.getInstance().point(player);
        Service.getInstance().sendFlagBag(player);
    }

    public void UseCard(Player pl, Item item) {
        RadarCard radarTemplate = RadarService.gI().RADAR_TEMPLATE.stream().filter(c -> c.Id == item.template.id)
                .findFirst().orElse(null);
        if (radarTemplate == null) {
            return;
        }
        if (radarTemplate.Require != -1) {
            RadarCard radarRequireTemplate = RadarService.gI().RADAR_TEMPLATE.stream()
                    .filter(r -> r.Id == radarTemplate.Require).findFirst().orElse(null);
            if (radarRequireTemplate == null) {
                return;
            }
            Card cardRequire = pl.Cards.stream().filter(r -> r.Id == radarRequireTemplate.Id).findFirst().orElse(null);
        }
        Card card = pl.Cards.stream().filter(r -> r.Id == item.template.id).findFirst().orElse(null);
        if (card == null) {
            Card newCard = new Card(item.template.id, (byte) 1, radarTemplate.Max, (byte) -1, radarTemplate.Options);
            if (pl.Cards.add(newCard)) {
                RadarService.gI().RadarSetAmount(pl, newCard.Id, newCard.Amount, newCard.MaxAmount);
                RadarService.gI().RadarSetLevel(pl, newCard.Id, newCard.Level);
                InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                InventoryServiceNew.gI().sendItemBags(pl);
            }
        } else {
            if (card.Level >= 2) {
                Service.gI().sendThongBao(pl, "Thẻ này đã đạt cấp tối đa");
                return;
            }
            card.Amount++;
            if (card.Amount >= card.MaxAmount) {
                card.Amount = 0;
                if (card.Level == -1) {
                    card.Level = 1;
                } else {
                    card.Level++;
                }
                Service.gI().point(pl);
            }
            RadarService.gI().RadarSetAmount(pl, card.Id, card.Amount, card.MaxAmount);
            RadarService.gI().RadarSetLevel(pl, card.Id, card.Level);
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
        }
    }
    public void useporata (Player player, Item item)
    {
        Item bongtai = InventoryServiceNew.gI().findItemBag(player, item.template.id);
            if (bongtai.isNotNullItem()) {
                int fusionOptionId = 0; // ID của option cho việc hợp thể
                    for (Item.ItemOption io : bongtai.itemOptions) {
                        if (io.optionTemplate.id == 242 || io.optionTemplate.id == 243 || io.optionTemplate.id == 244 || io.optionTemplate.id == 245) {
                            fusionOptionId = io.optionTemplate.id;
                            break;
                        }
                    }

                        if (player.pet == null || player.fusion.typeFusion == 4) {// lưỡng long hợp thể
                            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                        } else {
                                    if (fusionOptionId == 242) {
                                        if (player.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                                            player.pet.fusion(true);
                                        } else {
                                            player.pet.unFusion();
                                        }
                                    } else if (fusionOptionId == 243) {
                                        if (player.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                                            player.pet.fusion2(true);
                                        } else {
                                            player.pet.unFusion();
                                        }
                                    } else if (fusionOptionId == 244) {
                                        if (player.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                                            player.pet.fusion3(true);
                                        } else {
                                            player.pet.unFusion();
                                        }
                                    } else if (fusionOptionId == 245) {
                                        if (player.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                                            player.pet.fusion4(true);
                                        } else {
                                            player.pet.unFusion();
                                        }
                                    }
                                }
                            }
    }

    private void changePet(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBody(player.pet) == 8) {
            if (player.pet != null) {
                int gender = player.pet.gender + 1;
                if (gender > 2) {
                    gender = 0;
                }
                PetService.gI().changeNormalPet(player, gender);
                InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                InventoryServiceNew.gI().sendItemBags(player);
            } else {
                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Vui lòng tháo hết đồ đệ tử");
        }
    }

    private void changePetBerus(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBody(player.pet) == 8) {
            if (player.pet != null) {
                int gender = player.pet.gender;
                PetService.gI().changeBerusPet(player, gender);
                InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                InventoryServiceNew.gI().sendItemBags(player);
            } else {
                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Vui lòng tháo hết đồ đệ tử");
        }
    }

    private void changePetMabu(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender;
            PetService.gI().changeMabuPet(player, gender);
            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
        }
    }

    private void changePetPic(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBody(player.pet) == 8) {
            if (player.pet != null) {
                int gender = player.pet.gender;
                PetService.gI().changePicPet(player, gender);
                InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                InventoryServiceNew.gI().sendItemBags(player);
            } else {
                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Vui lòng tháo hết đồ đệ tử");
        }
    }

    private void openPhieuCaiTrangHaiTac(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            Item ct = ItemService.gI().createNewItem((short) Util.nextInt(618, 626));
            ct.itemOptions.add(new ItemOption(147, 3));
            ct.itemOptions.add(new ItemOption(77, 3));
            ct.itemOptions.add(new ItemOption(103, 3));
            ct.itemOptions.add(new ItemOption(149, 0));
            if (item.template.id == 2006) {
                ct.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));
            } else if (item.template.id == 2007) {
                ct.itemOptions.add(new ItemOption(93, Util.nextInt(7, 30)));
            }
            InventoryServiceNew.gI().addItemBag(pl, ct);
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
            CombineServiceNew.gI().sendEffectOpenItem(pl, item.template.iconID, ct.template.iconID);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void eatGrapes(Player pl, Item item) {
        int percentCurrentStatima = pl.nPoint.stamina * 100 / pl.nPoint.maxStamina;
        if (percentCurrentStatima > 50) {
            Service.getInstance().sendThongBao(pl, "Thể lực vẫn còn trên 50%");
            return;
        } else if (item.template.id == 211) {
            pl.nPoint.stamina = pl.nPoint.maxStamina;
            Service.getInstance().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 100%");
        } else if (item.template.id == 212) {
            pl.nPoint.stamina += (pl.nPoint.maxStamina * 20 / 100);
            Service.getInstance().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 20%");
        }
        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
        InventoryServiceNew.gI().sendItemBags(pl);
        PlayerService.gI().sendCurrentStamina(pl);
    }

    private void opendetuvip(Player player, Item item) {
        Input.gI().TAOPET(player);
        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
    }

    private void tangdiemboss(Player pl, Item item) {
        pl.inventory.event++;
        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
        Service.getInstance().sendThongBao(pl, "Bạn đã tăng điểm giết boss thành công");

    }

    private void nextnv(Player pl, Item item) {
        if (pl.playerTask.taskMain.id >= 1 && pl.playerTask.taskMain.id <= 17) {
            pl.playerTask.taskMain.id++; // Increment the id
            TaskService.gI().sendTaskMain(pl);
            TaskService.gI().sendNextTaskMain(pl);
            pl.playerTask.taskMain.index = 0;
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            Service.getInstance().sendThongBao(pl, "Bỏ qua nhiệm vụ thành công");

        } else {
            Service.getInstance().sendThongBao(pl, "Bạn không thể bỏ qua nhiệm vụ này");
        }
    }

    private void openCSKB(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = {76, 188, 189, 190, 381, 382, 383, 384, 385};
            int[][] gold = {{5000, 20000}};
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (index <= 3) {
                pl.inventory.gold += Util.nextInt(gold[0][0], gold[0][1]);
                if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
                    pl.inventory.gold = Inventory.LIMIT_GOLD;
                }
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 930;
            } else {
                Item it = ItemService.gI().createNewItem(temp[index]);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryServiceNew.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);

            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    public void maydoboss1(Player pl, Item item) {
        BossManager.gI().dobosss(pl);
        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);

    }

    private void doiskill4(Player pl, Item item) {
        if (pl.pet.nPoint.power > 20000000000L) {
            if (pl.pet != null) {
                if (pl.pet.playerSkill.skills.get(2).skillId != -1) {
                    pl.pet.openSkill4();
                    Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                    InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                    InventoryServiceNew.gI().sendItemBags(pl);
                } else {
                    Service.getInstance().sendThongBao(pl, "Ít nhất đệ tử ngươi phải có chiêu 3 chứ!");
                }
            } else {
                Service.getInstance().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
            }
        } else {
            Service.getInstance().sendThongBao(pl, "Yêu cầu đệ tử có skill 4");
        }
    }

    private void openphapsu(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            short[] manh = {1232, 1233, 1234};
            short da = 1235;
            short bua = 1236;
            short[] rac = {579, 1201, 15};
            byte index = (byte) Util.nextInt(0, manh.length - 1);
            byte index2 = (byte) Util.nextInt(0, rac.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (Util.isTrue(35, 100)) {
                Item it = ItemService.gI().createNewItem(rac[index2]);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryServiceNew.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
                Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
            } else if (Util.isTrue(13, 100)) {
                Item it = ItemService.gI().createNewItem(da);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryServiceNew.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
                Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
            } else if (Util.isTrue(3, 100)) {
                Item it = ItemService.gI().createNewItem(bua);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryServiceNew.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
                Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
            } else {
                Item it = ItemService.gI().createNewItem(manh[index]);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryServiceNew.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
                Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void banhtrungthu(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            short[] rac = {579, 1201, 899, 1099, 1100, 1101, 1102};
            int[][] gold = {{10000000, 30000000}};
            byte index2 = (byte) Util.nextInt(0, rac.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (Util.isTrue(60, 100)) {
                int vang = Util.nextInt(gold[0][0], gold[0][1]);
                pl.inventory.gold += vang;
                if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
                    pl.inventory.gold = Inventory.LIMIT_GOLD;
                }
                icon[1] = 930;
                Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + Util.format(vang) + " Vàng");
                if (item.template.id == 1335 || item.template.id == 1336) {
                    pl.NguHanhSonPoint += 2;
                    Service.gI().sendMoney(pl);
                    Service.getInstance().sendThongBao(pl, "|4|Bạn nhận được 2 Điểm Sự kiện");
                } else {
                    pl.NguHanhSonPoint += 5;
                    Service.gI().sendMoney(pl);
                    Service.getInstance().sendThongBao(pl, "|4|Bạn nhận được 5 Điểm Sự kiện");
                }
            } else {
                Item it = ItemService.gI().createNewItem(rac[index2]);
                if (item.template.id == 1337) {
                    it.quantity = 2;
                }
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryServiceNew.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
                Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
                if (item.template.id == 1335 || item.template.id == 1336) {
                    pl.NguHanhSonPoint += 2;
                    Service.gI().sendMoney(pl);
                    Service.getInstance().sendThongBao(pl, "|4|Bạn nhận được 2 Điểm Sự kiện");
                } else {
                    pl.NguHanhSonPoint += 5;
                    Service.gI().sendMoney(pl);
                    Service.getInstance().sendThongBao(pl, "|4|Bạn nhận được 5 Điểm Sự kiện");
                }
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void hoptrungthu(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            short[] rac = {1333, 1344, 1345};
            byte index2 = (byte) Util.nextInt(0, rac.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item it = ItemService.gI().createNewItem(rac[index2]);
//            System.out.println("    it    " + it.template.id);
            if (it.template.id == 1345) {
                it.itemOptions.add(new ItemOption(50, Util.nextInt(20, 50)));
                it.itemOptions.add(new ItemOption(77, Util.nextInt(25, 60)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(25, 60)));
                it.itemOptions.add(new ItemOption(14, Util.nextInt(5, 15)));
                if (Util.isTrue(30, 100)) {
                    it.itemOptions.add(new ItemOption(100, Util.nextInt(100, 1000)));
                    it.itemOptions.add(new ItemOption(101, Util.nextInt(100, 500)));
                }
            } else {
                it.itemOptions.add(new ItemOption(50, Util.nextInt(15, 40)));
                it.itemOptions.add(new ItemOption(77, Util.nextInt(20, 50)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(20, 50)));
                it.itemOptions.add(new ItemOption(14, Util.nextInt(2, 10)));
                it.itemOptions.add(new ItemOption(95, Util.nextInt(2, 10)));
                it.itemOptions.add(new ItemOption(96, Util.nextInt(2, 10)));
            }
            if (Util.isTrue(99, 100)) {
                it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 5)));
            }
            it.itemOptions.add(new ItemOption(30, 0));
            InventoryServiceNew.gI().addItemBag(pl, it);
            icon[1] = it.template.iconID;
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    public void hopquatanthu(Player player, Item item) {

        if (player.gender == 0) {
            Item itemReward = ItemService.gI().createNewItem((short) 0);
            Item itemReward1 = ItemService.gI().createNewItem((short) 6);
            Item itemReward2 = ItemService.gI().createNewItem((short) 12);
            Item itemReward3 = ItemService.gI().createNewItem((short) 21);
            Item itemReward4 = ItemService.gI().createNewItem((short) 27);
            itemReward.quantity = 1;
            itemReward1.quantity = 1;
            itemReward2.quantity = 1;
            itemReward3.quantity = 1;
            itemReward4.quantity = 1;
            if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
                itemReward.itemOptions.add(new ItemOption(47, 5));
                itemReward1.itemOptions.add(new ItemOption(7, 30));
                itemReward2.itemOptions.add(new ItemOption(14, 1));
                itemReward3.itemOptions.add(new ItemOption(0, 5));
                itemReward4.itemOptions.add(new ItemOption(6, 30));

                itemReward.itemOptions.add(new ItemOption(107, 12));
                itemReward1.itemOptions.add(new ItemOption(107, 12));
                itemReward2.itemOptions.add(new ItemOption(107, 12));
                itemReward3.itemOptions.add(new ItemOption(107, 12));
                itemReward4.itemOptions.add(new ItemOption(107, 12));

                itemReward.itemOptions.add(new ItemOption(30, 1));
                itemReward1.itemOptions.add(new ItemOption(30, 1));
                itemReward2.itemOptions.add(new ItemOption(30, 1));
                itemReward3.itemOptions.add(new ItemOption(30, 1));
                itemReward4.itemOptions.add(new ItemOption(30, 1));

                InventoryServiceNew.gI().addItemBag(player, itemReward);
                InventoryServiceNew.gI().addItemBag(player, itemReward1);
                InventoryServiceNew.gI().addItemBag(player, itemReward2);
                InventoryServiceNew.gI().addItemBag(player, itemReward3);
                InventoryServiceNew.gI().addItemBag(player, itemReward4);

                Service.getInstance().sendThongBao(player, "Bạn đã nhận được set đồ 10 sao !");
                InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                InventoryServiceNew.gI().sendItemBags(player);
            } else {
                Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
            }
            return;
        } else if (player.gender == 1) {
            Item itemReward = ItemService.gI().createNewItem((short) 1);
            Item itemReward1 = ItemService.gI().createNewItem((short) 7);
            Item itemReward2 = ItemService.gI().createNewItem((short) 12);
            Item itemReward3 = ItemService.gI().createNewItem((short) 22);
            Item itemReward4 = ItemService.gI().createNewItem((short) 28);
            itemReward.quantity = 1;
            itemReward1.quantity = 1;
            itemReward2.quantity = 1;
            itemReward3.quantity = 1;
            itemReward4.quantity = 1;
            if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {

                itemReward.itemOptions.add(new ItemOption(47, 5));
                itemReward1.itemOptions.add(new ItemOption(7, 30));
                itemReward2.itemOptions.add(new ItemOption(14, 1));
                itemReward3.itemOptions.add(new ItemOption(0, 5));
                itemReward4.itemOptions.add(new ItemOption(6, 30));

                itemReward.itemOptions.add(new ItemOption(107, 12));
                itemReward1.itemOptions.add(new ItemOption(107, 12));
                itemReward2.itemOptions.add(new ItemOption(107, 12));
                itemReward3.itemOptions.add(new ItemOption(107, 12));
                itemReward4.itemOptions.add(new ItemOption(107, 12));

                itemReward.itemOptions.add(new ItemOption(30, 1));
                itemReward1.itemOptions.add(new ItemOption(30, 1));
                itemReward2.itemOptions.add(new ItemOption(30, 1));
                itemReward3.itemOptions.add(new ItemOption(30, 1));
                itemReward4.itemOptions.add(new ItemOption(30, 1));

                InventoryServiceNew.gI().addItemBag(player, itemReward);
                InventoryServiceNew.gI().addItemBag(player, itemReward1);
                InventoryServiceNew.gI().addItemBag(player, itemReward2);
                InventoryServiceNew.gI().addItemBag(player, itemReward3);
                InventoryServiceNew.gI().addItemBag(player, itemReward4);

                Service.getInstance().sendThongBao(player, "Bạn đã nhận được set đồ 10 sao !");
                InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                InventoryServiceNew.gI().sendItemBags(player);
            } else {
                Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
            }
            return;
        } else {
            Item itemReward = ItemService.gI().createNewItem((short) 2);
            Item itemReward1 = ItemService.gI().createNewItem((short) 8);
            Item itemReward2 = ItemService.gI().createNewItem((short) 12);
            Item itemReward3 = ItemService.gI().createNewItem((short) 23);
            Item itemReward4 = ItemService.gI().createNewItem((short) 29);
            itemReward.quantity = 1;
            itemReward1.quantity = 1;
            itemReward2.quantity = 1;
            itemReward3.quantity = 1;
            itemReward4.quantity = 1;
            if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
                itemReward.itemOptions.add(new ItemOption(47, 5));
                itemReward1.itemOptions.add(new ItemOption(7, 30));
                itemReward2.itemOptions.add(new ItemOption(14, 1));
                itemReward3.itemOptions.add(new ItemOption(0, 5));
                itemReward4.itemOptions.add(new ItemOption(6, 30));

                itemReward.itemOptions.add(new ItemOption(107, 12));
                itemReward1.itemOptions.add(new ItemOption(107, 12));
                itemReward2.itemOptions.add(new ItemOption(107, 12));
                itemReward3.itemOptions.add(new ItemOption(107, 12));
                itemReward4.itemOptions.add(new ItemOption(107, 12));

                itemReward.itemOptions.add(new ItemOption(30, 1));
                itemReward1.itemOptions.add(new ItemOption(30, 1));
                itemReward2.itemOptions.add(new ItemOption(30, 1));
                itemReward3.itemOptions.add(new ItemOption(30, 1));
                itemReward4.itemOptions.add(new ItemOption(30, 1));

                InventoryServiceNew.gI().addItemBag(player, itemReward);
                InventoryServiceNew.gI().addItemBag(player, itemReward1);
                InventoryServiceNew.gI().addItemBag(player, itemReward2);
                InventoryServiceNew.gI().addItemBag(player, itemReward3);
                InventoryServiceNew.gI().addItemBag(player, itemReward4);

                Service.getInstance().sendThongBao(player, "Bạn đã nhận được set đồ 10 sao !");
                InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                InventoryServiceNew.gI().sendItemBags(player);
            } else {
                Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
            }
            return;
        }

    }

    public void hopnoel(Player player, Item item) {
        byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length);
        Item thanlinh = Util.randomthanlinh(Manager.itemIds_TL[randomDo]);
        short[] temp = {457, 17, 18, 1232, 1233, 1234, 1312, 383, 384, 385};
        int[] randomlinhthu = new int[]{1291, 1279, 1287, 1276, 1282, 1290, 1285, 1275, 1274, 1281, 1292, 1286, 1286, 1284};
        int[] randomoption = new int[]{5, 14, 94, 108, 5};
        int randomct = new Random().nextInt(randomlinhthu.length);
        int randomop = new Random().nextInt(randomoption.length);
        double random = Math.random() * 100;
        byte index = (byte) Util.nextInt(0, temp.length - 1);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;

        Item tatgiangsinh = InventoryServiceNew.gI().findItemBag(player, 649);

        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 1) {
            if (tatgiangsinh != null && tatgiangsinh.quantity >= 1) {
                if (Util.isTrue(90, 100)) { //50% ra cái này
                    Item it = ItemService.gI().createNewItem(temp[index]);
                    InventoryServiceNew.gI().addItemBag(player, it);
                    Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + it.template.name);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                    icon[1] = it.template.iconID;
                    InventoryServiceNew.gI().sendItemBags(player);
                    CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
                } else if (Util.isTrue(70, 100)) {
                    Item linhthu = ItemService.gI().createNewItem((short) randomlinhthu[randomct]);
                    if (random < 20) {
                        linhthu.itemOptions.add(new Item.ItemOption(50, Util.nextInt(5, 10)));
                    } else if (random < 40) {
                        linhthu.itemOptions.add(new Item.ItemOption(77, Util.nextInt(5, 10)));
                    } else if (random < 60) {
                        linhthu.itemOptions.add(new Item.ItemOption(103, Util.nextInt(5, 10)));
                    } else if (random < 80) {
                        linhthu.itemOptions.add(new Item.ItemOption(5, Util.nextInt(5, 10)));
                    } else {
                        linhthu.itemOptions.add(new Item.ItemOption(94, Util.nextInt(55, 10)));
                    }
                    if (Util.isTrue(99, 100)) {
                        linhthu.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 2)));
                    }
                    icon[1] = linhthu.template.iconID;
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                    InventoryServiceNew.gI().addItemBag(player, linhthu);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + linhthu.template.name);
                    CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
                } else {
                    InventoryServiceNew.gI().addItemBag(player, thanlinh);
                    Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + thanlinh.template.name);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                    icon[1] = thanlinh.template.iconID;
                    InventoryServiceNew.gI().sendItemBags(player);
                    CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
                }
                InventoryServiceNew.gI().subQuantityItemsBag(player, tatgiangsinh, 1);
            } else {
                Service.getInstance().sendThongBao(player, "Bạn không đủ tất giang sinh để mở");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    public void hopthanlinh(Player player, Item item) {
        byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length);
        Item thanlinh = Util.randomthanlinh(Manager.itemIds_TL[randomDo]);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 1) {
            InventoryServiceNew.gI().addItemBag(player, thanlinh);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + thanlinh.template.name);
            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            icon[1] = thanlinh.template.iconID;
            InventoryServiceNew.gI().sendItemBags(player);
            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    private void openCSH(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = {861, 861};
            int[][] gold = {{70, 150}};
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (index <= 1) {
                pl.inventory.ruby += Util.nextInt(gold[0][0], gold[0][1]);
                if (pl.inventory.ruby > 2000000000) {
                    pl.inventory.ruby = 2000000000;
                }
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 7743;
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);

            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void openWoodChest(Player pl, Item item) {
        int time = (int) TimeUtil.diffDate(new Date(), new Date(item.createTime), TimeUtil.DAY);
        if (time != 0) {
            Item itemReward = null;

            int param = pl.inventory.getParam(item, 72);
            short[] temp = {1079, 722};
            int[][] gold = {{1000, 2000}};
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (param < 9) {
                pl.inventory.ruby += Util.nextInt(gold[0][0], gold[0][1]);
                if (pl.inventory.ruby > 2000000000) {
                    pl.inventory.ruby = 2000000000;
                }
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 7743;
            } else if (param == 9 || param == 10) {
                itemReward = ItemService.gI().createNewItem((short) 861);
                itemReward.quantity = Util.nextInt(2000, 5000);
                InventoryServiceNew.gI().addItemBag(pl, itemReward);
                icon[1] = itemReward.template.iconID;
            }
            if (param == 11) {
                Item it = ItemService.gI().createNewItem(temp[index]);
                it.quantity = Util.nextInt(50, 100);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryServiceNew.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            PlayerService.gI().sendInfoHpMpMoney(pl);
            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Vui lòng đợi 24h");
        }
    }

    private int randClothes(int level) {
        return LIST_ITEM_CLOTHES[Util.nextInt(0, 2)][Util.nextInt(0, 4)][level - 1];
    }

    private void useItemTime(Player pl, Item item) {
        switch (item.template.id) {
            case 382: //bổ huyết
                pl.itemTime.lastTimeBoHuyet = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet = true;
                break;
            case 383: //bổ khí
                pl.itemTime.lastTimeBoKhi = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi = true;
                break;
            case 384: //giáp xên
                pl.itemTime.lastTimeGiapXen = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen = true;
                break;
            case 381: //cuồng nộ
                pl.itemTime.lastTimeCuongNo = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo = true;
                Service.getInstance().point(pl);
                break;
            case 385: //ẩn danh
                pl.itemTime.lastTimeAnDanh = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh = true;
                break;
            case 379: //máy dò capsule
                pl.itemTime.lastTimeUseMayDo = System.currentTimeMillis();
                pl.itemTime.isUseMayDo = true;
                break;
            case 1990: //máy dò kẹo
                pl.itemTime.lastTimegiodungkeo = System.currentTimeMillis();
                pl.itemTime.isgiodungkeo = true;
                break;
            case 1317:// cuồng bạo
                pl.itemTimesieucap.lastTimeUseXiMuoi = System.currentTimeMillis();
                pl.itemTimesieucap.isUseXiMuoi = true;
                Service.getInstance().point(pl);
                break;
            case 1099:// cn
                pl.itemTimesieucap.lastTimeCuongNo3 = System.currentTimeMillis();
                pl.itemTimesieucap.isUseCuongNo3 = true;
                Service.getInstance().point(pl);
                break;
            case 1100:// bo huyet
                pl.itemTimesieucap.lastTimeBoHuyet3 = System.currentTimeMillis();
                pl.itemTimesieucap.isUseBoHuyet3 = true;
                break;
            case 1102://bo khi
                pl.itemTimesieucap.lastTimeBoKhi3 = System.currentTimeMillis();
                pl.itemTimesieucap.isUseBoKhi3 = true;
                break;
            case 1101://xbh
                pl.itemTimesieucap.lastTimeGiapXen3 = System.currentTimeMillis();
                pl.itemTimesieucap.isUseGiapXen3 = true;
                break;
            case 1103://an danh
                pl.itemTimesieucap.lastTimeAnDanh3 = System.currentTimeMillis();
                pl.itemTimesieucap.isUseAnDanh3 = true;
                break;
            case 899:// item x2 tnsm
                pl.itemTimesieucap.lastTimeKeo = System.currentTimeMillis();
                pl.itemTimesieucap.isKeo = true;
                break;
            // bánh trung thu
            case 465:
            case 466:
            case 472:
            case 473:
                pl.itemTimesieucap.lastTimeUseBanh = System.currentTimeMillis();
                pl.itemTimesieucap.isUseTrungThu = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTimesieucap.iconBanh);
                pl.itemTimesieucap.iconBanh = item.template.iconID;
                break;
            case 663: //bánh pudding
            case 664: //xúc xíc
            case 665: //kem dâu
            case 666: //mì ly
            case 667://sushi
                pl.itemTimesieucap.lastTimeMeal = System.currentTimeMillis();
                pl.itemTimesieucap.isEatMeal = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTimesieucap.iconMeal);
                pl.itemTimesieucap.iconMeal = item.template.iconID;
                break;
            case 579: // đuôi khỉ
                pl.itemTime.lastTimeDuoikhi = System.currentTimeMillis();
                pl.itemTime.isDuoikhi = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconDuoi);
                pl.itemTime.iconDuoi = item.template.iconID;
                break;
            case 1201: //máy dò đồ
                pl.itemTime.lastTimeUseMayDo2 = System.currentTimeMillis();
                pl.itemTime.isUseMayDo2 = true;
                break;
        }
        Service.getInstance().point(pl);
        ItemTimeService.gI().sendAllItemTime(pl);
        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
        InventoryServiceNew.gI().sendItemBags(pl);
    }

    private void controllerCallRongThan(Player pl, Item item) {
        int tempId = item.template.id;
        if (tempId >= SummonDragon.NGOC_RONG_1_SAO && tempId <= SummonDragon.NGOC_RONG_7_SAO) {
            switch (tempId) {
                case SummonDragon.NGOC_RONG_1_SAO:
                case SummonDragon.NGOC_RONG_2_SAO:
                    SummonDragon.gI().openMenuSummonShenron(pl, (byte) (tempId - 13));
                    break;
                default:
                    NpcService.gI().createMenuConMeo(pl, ConstNpc.TUTORIAL_SUMMON_DRAGON,
                            -1, "Bạn chỉ có thể gọi rồng từ ngọc 2 sao, 1 sao", "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        }
        if (tempId >= GoiRongXuong.XUONG_1_SAO && tempId <= GoiRongXuong.XUONG_7_SAO) {
            switch (tempId) {
                case GoiRongXuong.XUONG_1_SAO:
                    GoiRongXuong.gI().openMenuRongXuong(pl, (byte) (tempId - 701));
                    break;              
                default:
                    NpcService.gI().createMenuConMeo(pl, ConstNpc.TUTORIAL_RONG_XUONG,
                            -1, "Bạn chỉ có thể gọi rồng từ ngọc 1 sao", "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        }
        if (tempId >= GoiRongBang.BANG_1_SAO && tempId <= GoiRongBang.BANG_7_SAO) {
            switch (tempId) {
                case GoiRongBang.BANG_1_SAO:
                    GoiRongBang.gI().openMenuRongBang(pl, (byte) (tempId - 924));
                    break;
                default:
                    NpcService.gI().createMenuConMeo(pl, ConstNpc.TUTORIAL_RONG_ICE,
                            -1, "Bạn chỉ có thể gọi rồng từ ngọc 1 sao", "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        }
    }

    private void learnSkill(Player pl, Item item) {
        Message msg;
        try {
            if (item.template.gender == pl.gender || item.template.gender == 3) {
                String[] subName = item.template.name.split("");
                byte level = Byte.parseByte(subName[subName.length - 1]);
                Skill curSkill = SkillUtil.getSkillByItemID(pl, item.template.id);
                if (curSkill.point == 7) {
                    Service.getInstance().sendThongBao(pl, "Kỹ năng đã đạt tối đa!");
                } else {
                    if (curSkill.point == 0) {
                        if (level == 1) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.getInstance().messageSubCommand((byte) 23);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else {
                            Skill skillNeed = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            Service.getInstance().sendThongBao(pl, "Vui lòng học " + skillNeed.template.name + " cấp " + skillNeed.point + " trước!");
                        }
                    } else {
                        if (curSkill.point + 1 == level) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);

                            SkillUtil.setSkill(pl, curSkill);
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.getInstance().messageSubCommand((byte) 62);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else {
                            Service.getInstance().sendThongBao(pl, "Vui lòng học " + curSkill.template.name + " cấp " + (curSkill.point + 1) + " trước!");
                        }
                    }
                    InventoryServiceNew.gI().sendItemBags(pl);
                }
            } else {
                Service.getInstance().sendThongBao(pl, "Không thể thực hiện");

            }
        } catch (Exception e) {
            Logger.logException(UseItem.class,
                    e);
        }
    }

    private void useTDLT(Player pl, Item item) {
        if (pl.itemTime.isUseTDLT) {
            ItemTimeService.gI().turnOffTDLT(pl, item);
        } else {
            ItemTimeService.gI().turnOnTDLT(pl, item);
        }
    }

    private void devboiquoc(Player pl, Item item) {
        Item vatphamduoctangchi = InventoryServiceNew.gI().findItemBody(pl, 114);
        Item.ItemOption tangchiso = null;
        int level = 0;
        for (Item.ItemOption io : vatphamduoctangchi.itemOptions) {
            tangchiso = io;
            if (tangchiso.optionTemplate.id == 73) {
                level = tangchiso.param;
            }
        }
        if (level <= 3) {
            if (level == 0) {
                if (item.quantity >= 100) {
                    tangchiso.param += 5;
                    vatphamduoctangchi.itemOptions.add(new ItemOption(73, 1));
                    InventoryServiceNew.gI().sendItemBody(pl);
                    InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 100);
                } else {
                    Service.gI().sendThongBao(pl, "Yều cầu đủ x100");
                }
            } else if (level == 1) {
                 if (item.quantity >= 200) {
                    tangchiso.param += 5;
                    level++;
                    InventoryServiceNew.gI().sendItemBody(pl);
                    InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 200);
                } else {
                    Service.gI().sendThongBao(pl, "Yều cầu đủ x300"); 
                }
            } else {
                if (item.quantity >= 300) {
                    tangchiso.param += 5;
                    level++;
                    InventoryServiceNew.gI().sendItemBody(pl);
                    InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 300);
                } else {
                    Service.gI().sendThongBao(pl, "Yều cầu đủ x300"); 
                }
            }
        }

    }

    private void openCapsuleUI(Player pl) {
        pl.iDMark.setTypeChangeMap(ConstMap.CHANGE_CAPSULE);
        ChangeMapService.gI().openChangeMapTab(pl);
    }

    public void choseMapCapsule(Player pl, int index) {
        int zoneId = -1;
        if (index < 0) {
            return;
        }
        Zone zoneChose = pl.mapCapsule.get(index);
        if (zoneChose.getNumOfPlayers() > 30
                || MapService.gI().isMapDoanhTrai(zoneChose.map.mapId)
                || MapService.gI().isMapMaBu(zoneChose.map.mapId)
                || MapService.gI().isMapHuyDiet(zoneChose.map.mapId)
                || MapService.gI().isMapBanDoKhoBau(zoneChose.map.mapId)
                || MapService.gI().isMapKhiGas(zoneChose.map.mapId)) {
            Service.getInstance().sendThongBao(pl, "Hiện tại không thể vào được khu!");
            return;
        }
        if (index != 0 || zoneChose.map.mapId == 21
                || zoneChose.map.mapId == 22
                || zoneChose.map.mapId == 23) {
            pl.mapBeforeCapsule = pl.zone;
        } else {
            zoneId = pl.mapBeforeCapsule != null ? pl.mapBeforeCapsule.zoneId : -1;
            pl.mapBeforeCapsule = null;
        }
        ChangeMapService.gI().changeMapBySpaceShip(pl, pl.mapCapsule.get(index).map.mapId, zoneId, -1);
    }

    public void eatPea(Player player) {
        Item pea = null;
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.type == 6) {
                pea = item;
                break;
            }
        }
        if (pea != null) {
            int hpKiHoiPhuc = 0;
            int lvPea = Integer.parseInt(pea.template.name.substring(13));
            for (Item.ItemOption io : pea.itemOptions) {
                if (io.optionTemplate.id == 2) {
                    hpKiHoiPhuc = io.param * 1000;
                    break;
                }
                if (io.optionTemplate.id == 48) {
                    hpKiHoiPhuc = io.param;
                    break;
                }
            }
            player.nPoint.setHp(player.nPoint.hp + hpKiHoiPhuc);
            player.nPoint.setMp(player.nPoint.mp + hpKiHoiPhuc);
            PlayerService.gI().sendInfoHpMp(player);
            Service.getInstance().sendInfoPlayerEatPea(player);
            if (player.pet != null && player.zone.equals(player.pet.zone) && !player.pet.isDie()) {
                int statima = 100 * lvPea;
                player.pet.nPoint.stamina += statima;
                if (player.pet.nPoint.stamina > player.pet.nPoint.maxStamina) {
                    player.pet.nPoint.stamina = player.pet.nPoint.maxStamina;
                }
                player.pet.nPoint.setHp(player.pet.nPoint.hp + hpKiHoiPhuc);
                player.pet.nPoint.setMp(player.pet.nPoint.mp + hpKiHoiPhuc);
                Service.getInstance().sendInfoPlayerEatPea(player.pet);
                Service.getInstance().chatJustForMe(player, player.pet, "Cảm ơn sư phụ đã cho con đậu thần");
            }
            if (player.TrieuHoipet != null && player.zone.equals(player.TrieuHoipet.zone) && !player.TrieuHoipet.isDie()) {
                player.TrieuHoipet.nPoint.setHp(player.TrieuHoipet.nPoint.hp + hpKiHoiPhuc);
                player.TrieuHoipet.nPoint.setMp(player.TrieuHoipet.nPoint.mp + hpKiHoiPhuc);
                Service.getInstance().sendInfoPlayerEatPea(player.TrieuHoipet);
                Service.getInstance().chatJustForMe(player, player.TrieuHoipet, "Đa tạ Chủ Thượng");
            }

            InventoryServiceNew.gI().subQuantityItemsBag(player, pea, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        }
    }

    private void upSkillPet(Player pl, Item item) {
        if (pl.pet == null) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        try {
            switch (item.template.id) {
                case 402: //skill 1
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 0)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 403: //skill 2
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 1)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 404: //skill 3
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 2)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 759: //skill 4
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 3)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;

            }

        } catch (Exception e) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        }
    }

    //////////////////////////////////////////////////////////////////////////
    private void Openhoplinhthu(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            int id = Util.nextInt(0, 100);
            int[] randomlinhthu = new int[]{1291, 1279, 1287, 1276, 1282, 1290, 1285, 1275, 1274, 1281, 1292, 1286, 1286, 1284};
            int[] randomoption = new int[]{5, 14, 94, 108, 5};
            int randomct = new Random().nextInt(randomlinhthu.length);
            int randomop = new Random().nextInt(randomoption.length);
            Item linhthu = ItemService.gI().createNewItem((short) randomlinhthu[randomct]);
            linhthu.itemOptions.add(new Item.ItemOption(50, Util.nextInt(2, 8)));
            linhthu.itemOptions.add(new Item.ItemOption(77, Util.nextInt(2, 8)));
            linhthu.itemOptions.add(new Item.ItemOption(103, Util.nextInt(2, 8)));
            linhthu.itemOptions.add(new Item.ItemOption(5, Util.nextInt(1, 8)));
            if (Util.isTrue(99, 100)) {
                linhthu.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 2)));
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().addItemBag(pl, linhthu);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + linhthu.template.name);
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống trong hành trang.");
        }
    }

    private void Openhopthucuoi(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            int id = Util.nextInt(0, 100);
            int[] randomlinhthu = new int[]{1298, 1297, 1345};
            int[] randomoption = new int[]{5, 14, 94, 108, 5};
            int randomct = new Random().nextInt(randomlinhthu.length);
            int randomop = new Random().nextInt(randomoption.length);
            Item linhthu = ItemService.gI().createNewItem((short) randomlinhthu[randomct]);
            linhthu.itemOptions.add(new Item.ItemOption(50, Util.nextInt(2, 8)));
            linhthu.itemOptions.add(new Item.ItemOption(77, Util.nextInt(2, 8)));
            linhthu.itemOptions.add(new Item.ItemOption(103, Util.nextInt(2, 8)));
            linhthu.itemOptions.add(new Item.ItemOption(5, Util.nextInt(1, 8)));
            if (Util.isTrue(99, 100)) {
                linhthu.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 2)));
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().addItemBag(pl, linhthu);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + linhthu.template.name);
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống trong hành trang.");
        }
    }

    private void Openhopdanhhieu(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            int id = Util.nextInt(0, 100);
            int[] randomlinhthu = new int[]{1323, 1324, 1325, 1326};
            int[] randomoption = new int[]{5, 14, 94, 108, 5};
            int randomct = new Random().nextInt(randomlinhthu.length);
            int randomop = new Random().nextInt(randomoption.length);
            Item linhthu = ItemService.gI().createNewItem((short) randomlinhthu[randomct]);
            linhthu.itemOptions.add(new Item.ItemOption(50, Util.nextInt(2, 8)));
            linhthu.itemOptions.add(new Item.ItemOption(77, Util.nextInt(2, 8)));
            linhthu.itemOptions.add(new Item.ItemOption(103, Util.nextInt(2, 8)));
            linhthu.itemOptions.add(new Item.ItemOption(5, Util.nextInt(1, 8)));
            if (Util.isTrue(99, 100)) {
                linhthu.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 2)));
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().addItemBag(pl, linhthu);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + linhthu.template.name);
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống trong hành trang.");
        }
    }

    public void NroSieuCap(Player pl, Item item) {//hop qua skh
        GoiRongXuong.gI().isRongxuongAppear = true;
        GoiRongXuong.gI().activeRongXuong(pl, true);
        NpcService.gI().createMenuConMeo(pl, ConstNpc.NRO_SIEU_CAP, -1, "|7|Hãy chọn điều ước mà người muốn"
                + "\n|8|Tỉ lệ thức tỉnh đệ tử là 50%: ",
                "Thức Tỉnh\nĐệ Tử"
        );
    }

//    public void NroIce(Player pl, Item item) {//hop qua skh
//        Item nroice2s = InventoryServiceNew.gI().findItemBag(pl, 926);
//        Item nroice3s = InventoryServiceNew.gI().findItemBag(pl, 927);
//        Item nroice4s = InventoryServiceNew.gI().findItemBag(pl, 928);
//        Item nroice5s = InventoryServiceNew.gI().findItemBag(pl, 929);
//        Item nroice6s = InventoryServiceNew.gI().findItemBag(pl, 930);
//        Item nroice7s = InventoryServiceNew.gI().findItemBag(pl, 931);
//        if (nroice2s != null
//                && nroice3s != null
//                && nroice4s != null
//                && nroice5s != null
//                && nroice6s != null
//                && nroice7s != null) {
//            NpcService.gI().createMenuConMeo(pl, ConstNpc.NRO_ICE, -1, "|7|Hãy chọn điều ước mà người muốn"
//                    + "\n|8|Hãy chọn các đièu ước sau ",
//                    "Nâng Găng\n[+7]", "X1 SKH\nRandom", "x1 Quà", "x10 Nro\nSiêu Cấp",
//                    "x100 Đá\nBảo Vệ"
//            );
//        } else {
//            Service.gI().sendThongBao(pl, "Yều cầu đủ 1-7 sao ngọc rồng băng");
//        }
//
//    }
    //////////////////////////////
    private void ItemSKH(Player pl, Item item) {//hop qua skh
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Hãy chọn một món quà", "Áo", "Quần", "Găng", "Giày", "Rada", "Từ Chối");
    }

    private void ItemDHD(Player pl, Item item) {//hop qua do huy diet
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Hãy chọn một món quà", "Áo", "Quần", "Găng", "Giày", "Rada", "Từ Chối");
    }

    private void ItemDTL(Player pl, Item item) {//hop qua do huy diet
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Hãy chọn một món quà", "Áo", "Quần", "Găng", "Giày", "Rada", "Từ Chối");
    }

    private void Hopts(Player pl, Item item) {//hop qua do huy diet
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Chọn hành tinh của mày đi", "Set trái đất", "Set namec", "Set xayda", "Từ chổi");
    }

}
