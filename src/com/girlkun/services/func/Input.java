package com.girlkun.services.func;

import com.girlkun.database.GirlkunDB;
import com.girlkun.consts.ConstNpc;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.Zone;
import com.girlkun.models.npc.Npc;
import com.girlkun.models.npc.NpcManager;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.GiftcodeViet;
import com.girlkun.models.player.Inventory;
import com.girlkun.models.shop.ShopServiceNew;
import com.girlkun.network.io.Message;
import com.girlkun.network.session.ISession;
import com.girlkun.server.Client;
import com.girlkun.server.Manager;
import com.girlkun.services.Service;
import com.girlkun.services.IntrinsicService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
//import com.girlkun.services.NapThe;
import com.girlkun.services.NpcService;
import com.girlkun.utils.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Input {

    private static final Map<Integer, Object> PLAYER_ID_OBJECT = new HashMap<Integer, Object>();

    public static final int CHANGE_PASSWORD = 500;
    public static final int GIFT_CODE = 501;
    public static final int FIND_PLAYER = 502;
    public static final int CHANGE_NAME = 503;
    public static final int CHOOSE_LEVEL_BDKB = 504;
    public static final int NAP_THE = 505;
    public static final int CHOOSE_LEVEL_GAS = 512;
    public static final int CHANGE_NAME_BY_ITEM = 506;
    public static final int GIVE_IT = 507;
    public static final int QUY_DOI_COIN = 508;
    public static final int QUY_DOI_COIN_1 = 509;
    public static final int XIU = 510;
    public static final int TAI = 511;
    public static final int BUFF_ITEM_VIP = 513;
    public static final int XIU_taixiu = 514;
    public static final int TAI_taixiu = 515;
    public static final int TAO_PET = 520;
    public static final int DOI_XU = 521;
    public static final int VE_HONG_NGOC = 522;
    public static final int MUA_NHIEU_VP = 523;
    public static final int buffcs = 1523;
    ///doithoivangnhanh

    public static final int bot = 15223;
    public static final int QUY_DOI_THUC_AN = 1304;
    public static final int buffnv = 1524;
    public static final int auto_mo_noi_tai = 5213;
    public static final int thoivang = 457;
    public static final byte NUMERIC = 0;
    public static final byte ANY = 1;
    public static final byte PASSWORD = 2;
    public static final int buffitem = 2223;

    public static final int autodrop = 27633823;
    public static final int autouse = 222331223;
    public static final int buff_tien = 2223233;
    public static final int sanbos1 = 223322;
    public static final int sanbos2 = 2223232;
    public static final int sanbos3 = 22128232;
    public static final int sanbos4 = 152123232;
    public static final int tangdiem = 15243232;
    private static Input intance;

    private Input() {

    }

    public static Input gI() {
        if (intance == null) {
            intance = new Input();
        }
        return intance;
    }
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void doInput(Player player, Message msg) {
        try {
            String[] text = new String[msg.reader().readByte()];
            for (int i = 0; i < text.length; i++) {
                text[i] = msg.reader().readUTF();
            }
            switch (player.iDMark.getTypeInput()) {
                case GIVE_IT:
                    // Trích xuất thông tin từ mảng
                    String recipientName = text[0];
                    int itemId = Integer.parseInt(text[1]);
                    int quantity = Integer.parseInt(text[2]);

                    // Lấy thông tin người nhận
                    Player recipient = Client.gI().getPlayer(recipientName);

                    if (recipient != null) {
                        // Tạo đối tượng vật phẩm và gán số lượng
                        Item item = ItemService.gI().createNewItem((short) itemId);
                        item.quantity = quantity;

                        // Thêm vật phẩm vào túi đồ của người nhận
                        InventoryServiceNew.gI().addItemBag(recipient, item);

                        // Gửi túi đồ đã cập nhật cho người nhận
                        InventoryServiceNew.gI().sendItemBags(recipient);

                        // Thông báo thành công cho cả người gửi và người nhận
                        Service.gI().sendThongBaoOK(player, "Bạn đã tặng thành công cho " + recipientName);
                        Service.gI().sendThongBaoOK(recipient, "Nhận " + item.template.name + " từ " + player.name);
                    } else {
                        // Thông báo cho người gửi biết rằng người nhận không trực tuyến
                        Service.gI().sendThongBaoOK(player, "Người chơi " + recipientName + " không online");
                    }
                    break;
                case autouse:
    try {
                    int itemid = Integer.parseInt(text[0]);
                    int soluongnhap = Integer.parseInt(text[1]);
                    Item itemitem = InventoryServiceNew.gI().findItemBag(player, itemid);

                    if (itemitem == null) {
                        Service.gI().sendThongBao(player, "|7|Item không có trong hành trang");
                    } else if (soluongnhap <= 0 || itemid <= 0) {
                        Service.gI().sendThongBao(player, "|7|Nhập không đúng");
                    } else {
                        if (player.autouse) {
                            scheduler.scheduleAtFixedRate(() -> {
                                for (int i = 0; i < soluongnhap; i++) {
                                    if (!player.autouse) {
                                        break;
                                    }

                                    UseItem.gI().useItem(player, itemitem, 1);
                                    Service.gI().sendThongBao(player, "|7|Đã sử dụng " + itemitem.template.name);

                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 0, 500, TimeUnit.MILLISECONDS);
                        }
                    }
                } catch (NumberFormatException e) {
                    Service.gI().sendThongBao(player, "|7|Nhập không đúng định dạng số");
                }
                break;
                case sanbos1:
                    Integer diemsb = Integer.parseInt(text[0]);
                    if (diemsb > 0 && diemsb <= 1000) {
                        if (diemsb <= player.pointsb) {
                            player.pointsb -= diemsb;
                            Item item = ItemService.gI().createNewItem((short) 1518, diemsb);
                            InventoryServiceNew.gI().addItemBag(player, item);
                            InventoryServiceNew.gI().sendItemBags(player);
                            Service.gI().sendThongBao(player, "|7|Bạn đã quy đổi thành công " + diemsb + " điểm săn boss \n nhận được x" + diemsb + item.template.name);
                            PlayerDAO.updatePlayer(player);
                        } else {
                            Service.gI().sendThongBao(player, "|7|Không đủ điểm để quy đổi");
                        }
                    } else {
                        Service.gI().sendThongBao(player, "|7|Bạn nhập không hợp lệ");
                    }
                    break;
                case sanbos2:
                    Integer diemsb1 = Integer.parseInt(text[0]);
                    if (diemsb1 > 0 && diemsb1 <= 1000) {
                        if (diemsb1 <= player.pointsb) {
                            player.pointsb -= diemsb1;
                            Item item = ItemService.gI().createNewItem((short) 1519, diemsb1);
                            InventoryServiceNew.gI().addItemBag(player, item);
                            InventoryServiceNew.gI().sendItemBags(player);
                            Service.gI().sendThongBao(player, "|7|Bạn đã quy đổi thành công " + diemsb1 + " điểm săn boss \n nhận được x" + diemsb1 + item.template.name);
                            PlayerDAO.updatePlayer(player);
                        } else {
                            Service.gI().sendThongBao(player, "|7|Không đủ điểm để quy đổi");
                        }
                    } else {
                        Service.gI().sendThongBao(player, "|7|Bạn nhập không hợp lệ");
                    }
                    break;
                case sanbos3:
                    Integer diemsb2 = Integer.parseInt(text[0]);
                    if (diemsb2 > 0 && diemsb2 <= 1000) {
                        if (diemsb2 <= player.pointsb) {
                            player.pointsb -= diemsb2;
                            Item item = ItemService.gI().createNewItem((short) 674, diemsb2);
                            InventoryServiceNew.gI().addItemBag(player, item);
                            InventoryServiceNew.gI().sendItemBags(player);
                            Service.gI().sendThongBao(player, "|7|Bạn đã quy đổi thành công " + diemsb2 + " điểm săn boss \n nhận được x" + diemsb2 + item.template.name);
                            PlayerDAO.updatePlayer(player);
                        } else {
                            Service.gI().sendThongBao(player, "|7|Không đủ điểm để quy đổi");
                        }
                    } else {
                        Service.gI().sendThongBao(player, "|7|Bạn nhập không hợp lệ");
                    }
                    break;
                case sanbos4:
                    Integer diemsb3 = Integer.parseInt(text[0]);
                    if (diemsb3 > 0 && diemsb3 <= 1000) {
                        if (diemsb3 <= player.pointsb) {
                            player.pointsb -= diemsb3;
                            Item item = ItemService.gI().createNewItem((short) 1233, diemsb3);
                            InventoryServiceNew.gI().addItemBag(player, item);
                            InventoryServiceNew.gI().sendItemBags(player);
                            Service.gI().sendThongBao(player, "|7|Bạn đã quy đổi thành công " + diemsb3 + " điểm săn boss \n nhận được x" + diemsb3 + item.template.name);
                            PlayerDAO.updatePlayer(player);
                        } else {
                            Service.gI().sendThongBao(player, "|7|Không đủ điểm để quy đổi");
                        }
                    } else {
                        Service.gI().sendThongBao(player, "|7|Bạn nhập không hợp lệ");
                    }
                    break;
                case autodrop:
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    try {
                        int itemid = Integer.parseInt(text[0]);
                        Item itemitemitem = InventoryServiceNew.gI().findItemBag(player, itemid);

                        if (itemitemitem == null && itemitemitem.template == null) {
                            Service.gI().sendThongBao(player, "|7|Item đã được loại bỏ khỏi hành trang");
                        } else {
                            if (player.autodrop) {
                                executorService.execute(() -> {
                                    while (player.autodrop && itemitemitem != null && itemitemitem.template != null) {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, itemitemitem, 1);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.gI().sendThongBao(player, "|7|Đã vứt bỏ " + itemitemitem.template.name);
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    } catch (NumberFormatException e) {
                        Service.gI().sendThongBao(player, "|7|Nhập không đúng định dạng số");
                    } finally {
                        executorService.shutdown();
                    }
                    break;
                case buffcs:
                    String recipientNameString = text[0]; //tên
                    String hpCommand = text[1];   // hp
                    String kiCommand = text[2];   // ki
                    String sdCommand = text[3];   // sd
                    String critCommand = text[4]; // chimang
                    String defCommand = text[5];  // giap

                    Player recipientPlayer = Client.gI().getPlayer(recipientNameString);
                    if (recipientPlayer != null) {
                        if (!hpCommand.isEmpty()) {
                            recipientPlayer.nPoint.hpg += Integer.valueOf(hpCommand);
                        }
                        if (!kiCommand.isEmpty()) {
                            recipientPlayer.nPoint.mpg += Integer.valueOf(kiCommand);
                        }
                        if (!sdCommand.isEmpty()) {
                            recipientPlayer.nPoint.dameg += Integer.valueOf(sdCommand);
                        }
                        if (!critCommand.isEmpty()) {
                            recipientPlayer.nPoint.critg += Integer.valueOf(critCommand);
                        }
                        if (!defCommand.isEmpty()) {
                            recipientPlayer.nPoint.defg += Integer.valueOf(defCommand);
                        }

                        Service.gI().point(player);
              
                        Service.gI().sendThongBaoOK(recipientPlayer, "Thành công");
                    } else {
                        Service.getInstance().sendThongBaoOK(player, "Không online");
                    }

                    break;
                case buffnv:
                    String name = text[0];
                    String sucmanh = text[1];  
                    String nhiemvu = text[2];   

                    Player Player = Client.gI().getPlayer(name);

                    if (Player != null) {
                        if (!sucmanh.isEmpty()) {
                            Service.gI().addSMTN(player, (byte) 2, Integer.parseInt(sucmanh), true);
                            Service.gI().sendThongBaoOK(Player, "Thành công");
                        }

        
                        if (!nhiemvu.isEmpty()) {
                            Player.playerTask.taskMain.id = Integer.parseInt(nhiemvu);
                        }

                  
                        Service.gI().sendThongBaoOK(Player, "Thành công");
                    } else {
                
                        Service.gI().sendThongBaoOK(player, "Không online");
                    }
                    break;
                case tangdiem:
                    Player playerok = Client.gI().getPlayer(text[0]);
                    if (playerok != null) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.TANG_DIEM_SB, -1, "Người chọn đi",
                                new String[]{"Tăng 10 điểm\n" + playerok.name,
                                    "Tăng 30 điểm\n" + playerok.name,
                                    "Tăng 50 điểm\n" + playerok.name,
                                    "Tăng 80 điểm\n" + playerok.name,
                                    "Tăng 100 điểm\n" + playerok.name,
                                    "Tăng 150 điểm\n" + playerok.name,
                                    "Tăng 200 điểm\n" + playerok.name
                                },
                                playerok);
                    } else {
                        Service.getInstance().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                case QUY_DOI_THUC_AN:
                    int slgiothucan = Integer.parseInt(text[0]);
                    int slthucanbitru = slgiothucan * 1;

                    if (slgiothucan > 1000) {
                        Service.getInstance().sendThongBao(player, "Số lượng thức ăn tối đa là 1000 giỏ!!");
                        return;
                    }

                    if (slgiothucan <= 0) {
                        Service.getInstance().sendThongBao(player, "Số lượng không hợp lệ!!");
                        return;
                    }
                    int[] foodItemIds = {663, 664, 665, 666, 667};
                    Item[] foodItems = new Item[foodItemIds.length];
                    boolean allItemsExist = true;

                    for (int i = 0; i < foodItemIds.length; i++) {
                        foodItems[i] = InventoryServiceNew.gI().findItemBag(player, foodItemIds[i]);

                        if (foodItems[i] == null) {
                            allItemsExist = false;
                            break;
                        }
                    }

                    if (allItemsExist && foodItems[4].quantity >= slthucanbitru) {
                        for (Item item : foodItems) {
                            item.quantity -= slthucanbitru;
                        }
                        if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 2) {
                            Service.getInstance().sendThongBao(player, "Bạn cần ít nhất 3 ô trống trong túi hành trang");
                            return;
                        }
                        Item giothucan = ItemService.gI().createNewItem((short) 993);
                        giothucan.quantity = slgiothucan;
                        InventoryServiceNew.gI().addItemBag(player, giothucan);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + giothucan.template.name);
                    } else {
                        Service.getInstance().sendThongBao(player, "Nguyên liệu không đủ");
                    }
                    break;

                case VE_HONG_NGOC:
                    String namee = text[0];
                    int hongngoc = Integer.parseInt(text[1]);

                    if (Client.gI().getPlayer(namee) != null) {
                        Item item = ItemService.gI().createNewItem(((short) 861));
                        item.quantity = hongngoc;
                        if (player.inventory.ruby >= hongngoc + 100) {
                            player.inventory.ruby -= hongngoc + 100;
                            Service.gI().sendMoney(Client.gI().getPlayer(namee));
                            InventoryServiceNew.gI().addItemBag(Client.gI().getPlayer(namee), item);
                            InventoryServiceNew.gI().sendItemBags(Client.gI().getPlayer(namee));
                            Service.gI().sendMoney(Client.gI().getPlayer(namee));
                            Service.gI().sendMoney(player);
                            Service.getInstance().sendThongBao(Client.gI().getPlayer(namee), "|1|Nhận được " + Util.format(hongngoc) + " Hồng ngọc từ " + player.name);
                        } else {
                            Service.getInstance().sendThongBao(player, "|7|Số Hồng ngọc vượt quá Hồng ngọc của bạn");
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Không online");
                    }
                    break;

                    case auto_mo_noi_tai:
                    player.idnoitai = Integer.parseInt(text[0]); // Người dùng nhập ID
                    player.chisonoitai = Integer.parseInt(text[1]); // Người dùng nhập nội tại
                
                    if (player.chisonoitai <= 0 || player.chisonoitai > 150) {
                        Service.getInstance().sendThongBao(player, "Số lượng nội tại không hợp lệ");
                    } else if (player.nPoint.power <= 10000000000L) {
                        Service.getInstance().sendThongBao(player, "|7|Bạn không đủ 10 tỷ sức mạnh");
                    } else {
                        if (player.autonoitai) {
                            new Thread(() -> {
                                try {
                                    int countOpened = 0;
                                    while (player.autonoitai) { // Vòng lặp chạy khi player.autonoitai là true
                                        if (player.inventory.gold < 2000000000) {
                                            Service.getInstance().sendThongBao(player, "Không đủ vàng để mở nội tại");
                                            player.autonoitai = false; // Dừng vòng lặp nếu không đủ vàng
                                            break; // Thoát khỏi vòng lặp
                                        }
                
                                        IntrinsicService.gI().openVip(player); // Gọi hàm mở nội tại thường
                                        countOpened +=1;
                                        player.playerIntrinsic.intrinsic.param1 = (short) Util.nextInt(player.playerIntrinsic.intrinsic.paramFrom1, player.playerIntrinsic.intrinsic.paramTo1);
                                        if (player.playerIntrinsic.intrinsic.param1 >= player.chisonoitai
                                                && player.playerIntrinsic.intrinsic.id == player.idnoitai) {
                                            Service.getInstance().sendThongBao(player, "|1|Đã mở " + (countOpened) + " nội tại và đạt được mục tiêu");
                                            System.out.println("|1|Đã mở " + (countOpened) + " nội tại và đạt được mục tiêu");
                                            player.autonoitai = false; // Dừng vòng lặp nếu đạt được mục tiêu
                                            break; // Thoát khỏi vòng lặp
                                        }               
                                        Thread.sleep(500); // Chờ 500ms trước khi thực hiện lần lặp tiếp theo
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    break;
                
                ///mua nhiều vp
                case MUA_NHIEU_VP:
                    player.soluongmuanhieu = Integer.parseInt(text[0]);
                    if (player.soluongmuanhieu > 1000) {
                        Service.getInstance().sendThongBao(player, "|7|Quá giới hạn mua. Tối đa 1000 Vật phẩm");
                        player.soluongmuanhieu = 0;
                        player.idmuanhieu = -1;
                    } else {
                        if (player.soluongmuanhieu > 0) {
                            scheduler.scheduleAtFixedRate(() -> {
                                try {
                                    if (player.soluongmuanhieu > 0) {
                                        ShopServiceNew.gI().buyItem(player, player.idmuanhieu);
                                        Service.getInstance().sendMoney(player);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        player.soluongmuanhieu--;

                                        if (player.soluongmuanhieu == 0) {
                                            Service.getInstance().sendThongBao(player, "|1|Đã mua xong " + player.soluongmuanhieu + " vật phẩm");
                                            Service.getInstance().sendMoney(player);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            player.idmuanhieu = -1;
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }, 0, 1, TimeUnit.MILLISECONDS);
                        }
                    }
                    break;
                case CHANGE_PASSWORD:
                    Service.getInstance().changePassword(player, text[0], text[1], text[2]);
                    break;
                //nhập code
                case GIFT_CODE:
                    GiftcodeViet.gI().giftCode(player, text[0]);
                    break;
                //tìm kiếm player
                case FIND_PLAYER:
                    Player pl = Client.gI().getPlayer(text[0]);
                    if (pl != null) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_FIND_PLAYER, -1, "Ngài muốn..?",
                                new String[]{"Đi tới\n" + pl.name, "Gọi " + pl.name + "\ntới đây", "Đổi tên", "Ban", "Kick"},
                                pl);
                    } else {
                        Service.getInstance().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;

                case CHANGE_NAME: {
                    Player plChanged = (Player) PLAYER_ID_OBJECT.get((int) player.id);
                    if (plChanged != null) {
                        if (GirlkunDB.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.getInstance().sendThongBao(player, "Tên nhân vật đã tồn tại");
                        } else {
                            plChanged.name = text[0];
                            GirlkunDB.executeUpdate("update player set name = ? where id = ?", plChanged.name, plChanged.id);
                            Service.getInstance().player(plChanged);
                            Service.getInstance().Send_Caitrang(plChanged);
                            Service.getInstance().sendFlagBag(plChanged);
                            Zone zone = plChanged.zone;
                            ChangeMapService.gI().changeMap(plChanged, zone, plChanged.location.x, plChanged.location.y);
                            Service.getInstance().sendThongBao(plChanged, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            Service.getInstance().sendThongBao(player, "Đổi tên người chơi thành công");
                        }
                    }
                }
                break;
                case CHANGE_NAME_BY_ITEM: {
                    if (player != null) {
                        if (GirlkunDB.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.getInstance().sendThongBao(player, "Tên nhân vật đã tồn tại");
                            createFormChangeNameByItem(player);
                        } else {
                            Item theDoiTen = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2006);
                            if (theDoiTen == null) {
                                Service.getInstance().sendThongBao(player, "Không tìm thấy thẻ đổi tên");
                            } else {
                                InventoryServiceNew.gI().subQuantityItemsBag(player, theDoiTen, 1);
                                player.name = text[0];
                                GirlkunDB.executeUpdate("update player set name = ? where id = ?", player.name, player.id);
                                Service.getInstance().player(player);
                                Service.getInstance().Send_Caitrang(player);
                                Service.getInstance().sendFlagBag(player);
                                Zone zone = player.zone;
                                ChangeMapService.gI().changeMap(player, zone, player.location.x, player.location.y);
                                Service.getInstance().sendThongBao(player, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            }
                        }
                    }
                }
                break;
                case thoivang:
                    int soluong = Integer.parseInt(text[0]);
                    if (soluong <= 0) {
                        Service.getInstance().sendThongBaoOK(player, "|7|Số lượng không hợp lệ.");
                    } else {
                        Item tv = InventoryServiceNew.gI().findItemBag(player, 457);
                        try {
                            if (tv != null && tv.quantity >= soluong) {
                                long receivedGold = (long) soluong * 500000000L;
                                if (player.inventory.gold + receivedGold <= Inventory.LIMIT_GOLD) {
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, tv, soluong);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    player.inventory.gold += receivedGold;
                                    Service.gI().sendMoney(player);
//                                    Service.gI().sendThongBaoOK(player, "Bạn đã đổi " + soluong + " thỏi vàng thành " + receivedGold + " vàng.");
                                    Service.gI().sendThongBaoOK(player, "Bạn đã đổi " + soluong + " thỏi vàng thành " + Util.formatGold(receivedGold) + " vàng.");

                                } else {
                                    Service.gI().sendThongBao(player, "|7|Vàng tôi đa chỉ có 10000 tỷ");
                                }
                            } else {
                                Service.gI().sendThongBao(player, "|7|bạn không đủ thỏi vàng để quy đổi");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Service.gI().sendThongBao(player, "Lỗi.");
                        }
                    }
                    break;
                case TAO_PET: {
                    String NamePet = text[0];
                    if (NamePet.length() < 4 || NamePet.length() > 20) {
                        Service.gI().sendThongBao(player,
                                "Không ngắn hơn 4 và dài hơn 20 kí tự, Và cho phép kí tự đặt biệt.");
                        break;
                    }
                    player.TrieuHoiCapBac = -1;
                    if (player.TrieuHoipet != null) {
                        ChangeMapService.gI().exitMap(player.TrieuHoipet);
                        player.TrieuHoipet.dispose();
                        player.TrieuHoipet = null;
                    }
                    player.CreatePet(NamePet);
                    player.chienthan.donechienthan++;
                    Service.gI().sendThongBao(player, "Bạn đã nhận nuôi pet có tên là: " + NamePet);
                    break;
                }

                case XIU:
                    int sotvxiu = Integer.valueOf(text[0]);
                    try {
                        if (sotvxiu >= 10000) {
                            if (player.inventory.ruby >= sotvxiu) {
                                player.inventory.ruby -= sotvxiu;
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.getInstance().sendMoney(player);

                                new Thread(() -> {
                                    Timer timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                       
                                            int x, y, z;
                                            if (Util.isTrue(50, 100)) {

                                                x = Util.nextInt(3, 6);
                                                y = Util.nextInt(4, 6);
                                                z = Util.nextInt(4, 6);
                                            } else {
                                                x = Util.nextInt(1, 2);
                                                y = Util.nextInt(1, 3);
                                                z = Util.nextInt(1, 3);
                                            }
                                            int tong = (x + y + z);
                                            if (4 <= (x + y + z) && (x + y + z) <= 10) {
                                                if (player != null) {
                                                    player.inventory.ruby += sotvxiu * 1.8;
                                                    InventoryServiceNew.gI().sendItemBags(player);
                                                    Service.getInstance().sendMoney(player);
                                                    Service.gI().sendThongBaoOK(player, "KẾT QUẢ" + "\nSố hệ thống quay ra : " + x + " "
                                                            + y + " " + z + "\nTổng là : " + tong + "\n\nBạn đã cược : " + sotvxiu
                                                            + " Hồng ngọc vào Xỉu" + "\nKẾT QUẢ : Xỉu" + "\n\nVề bờ");
                                                    return;
                                                }
                                            } else if (x == y && x == z) {
                                                if (player != null) {
                                                    Service.gI().sendThongBaoOK(player, "KẾT QUẢ" + "Số hệ thống quay ra : " + x + " " + y + " " + z + "\nTổng là : " + tong + "\n\nBạn đã cược : " + sotvxiu + " Hồng ngọc vào Xỉu" + "\nKẾT QUẢ : Tam hoa" + "\n\nXui quá làm lại thôi.");
                                                    return;
                                                }
                                            } else if ((x + y + z) > 10) {
                                                if (player != null) {
                                                    Service.gI().sendThongBaoOK(player, "KẾT QUẢ" + "\nSố hệ thống quay ra là :"
                                                            + " " + x + " " + y + " " + z + "\nTổng là : " + tong + "\n\nBạn đã cược : "
                                                            + sotvxiu + " Hồng ngọc vào Xỉu" + "\nKẾT QUẢ : Tài" + "\n\nXui quá làm lại thôi.");
                                                    return;
                                                }
                                            }
                                        }
                                    }, 10000);
                                }).start();

                                Timer timer1 = new Timer();
                                TimerTask task = new TimerTask() {
                                    int count = 10; // số lần thực hiện hành động

                                    @Override
                                    public void run() {
                                        if (count > 0) { // thực hiện hành động trong 10 giây
                                 

                                            Service.gI().sendThongBao(player, "Con bạc hãy chờ " + count + " giây để biết kết quả.");
                                            count--;
                                        } else { // dừng lên lịch khi đã thực hiện trong 10 giây
                                            this.cancel();
                                            timer1.cancel();
                                        }
                                    }
                                };
                                timer1.schedule(task, 1000, 1000);

                            } else {
                                Service.gI().sendThongBao(player, "Bạn không đủ Hồng ngọc để chơi.");
                            }
                        } else {
                            Service.gI().sendThongBao(player, "Cược ít nhất 10.000 Hồng ngọc");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.gI().sendThongBao(player, "Lỗi.");
                        System.out.println("nnnnn2  ");
                    }
                    break;
                case TAI:
                    int sotvtai = Integer.valueOf(text[0]);
                    try {
                        if (sotvtai >= 10000) {
                            if (player.inventory.ruby >= sotvtai) {
                                player.inventory.ruby -= sotvtai;
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.getInstance().sendMoney(player);
                                new Thread(() -> {
                                    Timer timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            int x, y, z;
                                            // Thực hiện các hành động sau khi chờ 10 giây
                                            if (Util.isTrue(50, 100)) {
                                                x = Util.nextInt(1, 2);
                                                y = Util.nextInt(1, 3);
                                                z = Util.nextInt(1, 3);
                                            } else {
                                                x = Util.nextInt(3, 6);
                                                y = Util.nextInt(4, 6);
                                                z = Util.nextInt(4, 6);
                                            }
                                            int tong = (x + y + z);
                                            if (4 <= (x + y + z) && (x + y + z) <= 10) {
                                                if (player != null) {// tự sửa lại tên lấy
                                                    Service.gI().sendThongBaoOK(player, "KẾT QUẢ" + "\nSố hệ thống quay ra là :"
                                                            + " " + x + " " + y + " " + z + "\nTổng là : " + tong + "\n\nBạn đã cược : "
                                                            + sotvtai + " Hồng ngọc vào Tài" + "\nKẾT QUẢ : Xỉu " + "\n\nXui quá làm lại thôi.");
                                                    return;
                                                }
                                            } else if (x == y && x == z) {
                                                if (player != null) {
                                                    Service.gI().sendThongBaoOK(player, "KẾT QUẢ" + "Số hệ thống quay ra : " + x + " " + y + " " + z + "\nTổng là : " + tong + "\n\nBạn đã cược : " + sotvtai + " Hồng ngọc vào Tài" + "\nKẾT QUẢ : Tam hoa" + "\n\nXui quá làm lại thôi.");
                                                    return;
                                                }
                                            } else if ((x + y + z) > 10) {

                                                if (player != null) {
                                                    player.inventory.ruby += sotvtai * 1.8;
                                                    InventoryServiceNew.gI().sendItemBags(player);
                                                    Service.getInstance().sendMoney(player);
                                                    Service.gI().sendThongBaoOK(player, "KẾT QUẢ" + "\nSố hệ thống quay ra : " + x + " "
                                                            + y + " " + z + "\nTổng là : " + tong + "\n\nBạn đã cược : " + sotvtai
                                                            + " Hồng ngọc vào Tài" + "\nKẾT QUẢ : Tài" + "\n\nVề bờ");
                                                    return;
                                                }
                                            }
                                        }
                                    }, 10000);
                                }).start();

                                Timer timer1 = new Timer();
                                TimerTask task = new TimerTask() {
                                    int count = 10; // số lần thực hiện hành động

                                    @Override
                                    public void run() {
                                        if (count > 0) { // thực hiện hành động trong 10 giây
                                            // Thực hiện các hành động mỗi lần cách nhau 1 giây

                                            Service.gI().sendThongBao(player, "Con bạc hãy chờ " + count + " giây để biết kết quả.");
                                            count--;
                                        } else { // dừng lên lịch khi đã thực hiện trong 10 giây
                                            this.cancel();
                                            timer1.cancel();
                                        }
                                    }
                                };
                                timer1.schedule(task, 1000, 1000);

                            } else {
                                Service.gI().sendThongBao(player, "Bạn không đủ Hồng ngọc để chơi.");
                            }
                        } else {
                            Service.gI().sendThongBao(player, "Cược ít nhất 10.000 Hồng ngọc");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.gI().sendThongBao(player, "Lỗi.");
                        System.out.println("nnnnn2  ");
                    }
                    break;

                case TAI_taixiu:
                    int sotvxiu1 = Integer.valueOf(text[0]);
                    try {
                        if (sotvxiu1 >= 1000 && sotvxiu1 <= 100000) {
                            if (player.inventory.ruby >= sotvxiu1) {
                                player.inventory.ruby -= sotvxiu1;
                                player.goldTai += sotvxiu1;
                                player.taixiu.toptaixiu += sotvxiu1;
                                TaiXiu.gI().goldTai += sotvxiu1;
                                Service.gI().sendThongBao(player, "Bạn đã đặt " + Util.format(sotvxiu1) + " Hồng ngọc vào TÀI");
                                TaiXiu.gI().addPlayerTai(player);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.getInstance().sendMoney(player);
                                PlayerDAO.updatePlayer(player);
                            } else {
                                Service.gI().sendThongBao(player, "Bạn không đủ Hồng ngọc để chơi.");
                            }
                        } else {
                            Service.gI().sendThongBao(player, "Cược ít nhất 10.000 Hồng ngọc.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.gI().sendThongBao(player, "Lỗi.");
                        System.out.println("nnnnn2  ");
                    }
                    break;

                case XIU_taixiu:
                    int sotvxiu2 = Integer.valueOf(text[0]);
                    try {
                        if (sotvxiu2 >= 1000 && sotvxiu2 <= 100000) {
                            if (player.inventory.ruby >= sotvxiu2) {
                                player.inventory.ruby -= sotvxiu2;
                                player.goldXiu += sotvxiu2;
                                player.taixiu.toptaixiu += sotvxiu2;
                                TaiXiu.gI().goldXiu += sotvxiu2;
                                Service.gI().sendThongBao(player, "Bạn đã đặt " + Util.format(sotvxiu2) + " Hồng ngọc vào XỈU");
                                TaiXiu.gI().addPlayerXiu(player);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.getInstance().sendMoney(player);
                                PlayerDAO.updatePlayer(player);
                            } else {
                                Service.gI().sendThongBao(player, "Bạn không đủ Hồng ngọc để chơi.");
                            }
                        } else {
                            Service.gI().sendThongBao(player, "Cược ít nhất 20.000 - 100.000 Hồng ngọc ");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.gI().sendThongBao(player, "Lỗi.");
                        System.out.println("nnnnn2  ");
                    }
                    break;

                case BUFF_ITEM_VIP:
                    if (player.isAdmin()) {
                        Player pBuffItem = Client.gI().getPlayer(text[0]);
                        int idItemBuff = Integer.parseInt(text[1]);
                        String idOptionBuff = text[2].trim();

                        int slItemBuff = Integer.parseInt(text[3]);

                        try {
                            if (pBuffItem != null) {
                                String txtBuff = "ADMIN GỬI CHO BẠN: " + pBuffItem.name + "\b";

                                Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff, slItemBuff);
                                if (!idOptionBuff.isEmpty()) {
                                    String arr[] = idOptionBuff.split("\\|");
                                    for (int i = 0; i < arr.length; i++) {
                                        String arr2[] = arr[i].split("-");
                                        int idoption = Integer.parseInt(arr2[0].trim());
                                        int param = Integer.parseInt(arr2[1].trim());
                                        itemBuffTemplate.itemOptions.add(new Item.ItemOption(idoption, param));
                                    }

                                }
                                txtBuff += "x" + slItemBuff + " " + itemBuffTemplate.template.name + "\b";
                                InventoryServiceNew.gI().addItemBag(pBuffItem, itemBuffTemplate);
                                InventoryServiceNew.gI().sendItemBags(pBuffItem);
                                NpcService.gI().createTutorial(player, 24, txtBuff);
                                if (player.id != pBuffItem.id) {
                                    NpcService.gI().createTutorial(pBuffItem, 24, txtBuff);
                                }
                            } else {
                                Service.getInstance().sendThongBao(player, "Player không online");
                            }
                        } catch (Exception e) {
                            Service.getInstance().sendThongBao(player, "Đã có lỗi xảy ra vui lòng thử lại");
                        }

                    }
                    break;
                case buff_tien:
                    if (player.isAdmin()) {
                        Player playeronline = Client.gI().getPlayer(text[0]);
                        int sotien = Integer.parseInt(text[1]);

                        if (playeronline != null) {
                            PlayerDAO.congvnd(playeronline, sotien);
                            PlayerDAO.bufftongnap(playeronline, sotien);
                            Service.gI().sendThongBaoOK(player, "Bạn đã cộng" + sotien + " cho người chơi");
                            Service.gI().sendThongBaoOK(playeronline, "Bạn đã được ADMIN cộng: " + sotien + " vnd");
                        } else {
                            Service.getInstance().sendThongBao(player, "Player không online");
                        }

                    }
                    break;
                case CHOOSE_LEVEL_GAS:
                    int levele = Integer.parseInt(text[0]);
                    if (levele >= 1 && levele <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.MR_POPO, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCPET_GO_TO_GAS,
                                    "Con có chắc chắn muốn tới Khí gas huỷ diệt cấp độ " + levele + "?",
                                    new String[]{"Đồng ý, Let's Go", "Từ chối"}, levele);
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    }

                    break;
                case CHOOSE_LEVEL_BDKB:
                    int level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.QUY_LAO_KAME, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCPET_GO_TO_BDKB,
                                    "Con có chắc chắn muốn tới bản đồ kho báu cấp độ " + level + "?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    }

                    break;
                case QUY_DOI_COIN:
                    int goldTrade = Integer.parseInt(text[0]);
                    if (goldTrade % 1000 == 0) {
                        if (goldTrade <= 0 || goldTrade >= 1000001) {
                            Service.getInstance().sendThongBao(player, "|7|Quá giới hạn mỗi lần tối đa 1.000.000");
                        } else if (player.getSession().vnd >= goldTrade) {//* coinGold
                            if (goldTrade >= 500000) {
                                PlayerDAO.subvnd(player, goldTrade);//* coinGold
                                Item thehongngoc = null;
                                try {
                                    thehongngoc = InventoryServiceNew.gI().findItemBag(player, 1132);
                                } catch (Exception e) {
                                }
                                Item thoiVang = ItemService.gI().createNewItem((short) 861, goldTrade * Manager.KHUYEN_MAI_NAP);
                                InventoryServiceNew.gI().addItemBag(player, thoiVang);
                                if (thehongngoc != null) {
                                    Service.getInstance().sendThongBao(player, "|3|Bạn đã có Vé tặng ngọc rồi nên không nhận được nữa !!");
                                    Service.getInstance().sendThongBao(player, "Bạn nhận được " + Util.format(goldTrade * Manager.KHUYEN_MAI_NAP)//* ratioGold * 2
                                            + " " + (thoiVang.template.name));
                                } else {
                                    Item thehn = ItemService.gI().createNewItem((short) 1132, 1);
                                    InventoryServiceNew.gI().addItemBag(player, thehn);
                                    Service.getInstance().sendThongBao(player, "Bạn nhận được " + Util.format(goldTrade * Manager.KHUYEN_MAI_NAP)//* ratioGold * 2
                                            + " " + (thoiVang.template.name) + " và 1 Vé tặng hồng ngọc");
                                }
                                InventoryServiceNew.gI().sendItemBags(player);
                                player.achievement.numPayMoney += goldTrade;
                                GirlkunDB.executeUpdate("update player set vnd = (vnd + " + goldTrade //* coinGold
                                        + ") where id = " + player.id);
                            } else {
                                PlayerDAO.subvnd(player, goldTrade);
                                Item thoiVang = ItemService.gI().createNewItem((short) 861, goldTrade * Manager.KHUYEN_MAI_NAP);
                                InventoryServiceNew.gI().addItemBag(player, thoiVang);
                                InventoryServiceNew.gI().sendItemBags(player);
                                player.achievement.numPayMoney += goldTrade;
                                Service.getInstance().sendThongBao(player, "Bạn nhận được " + Util.format(goldTrade * Manager.KHUYEN_MAI_NAP)//* ratioGold * 2
                                        + " " + thoiVang.template.name);
                                GirlkunDB.executeUpdate("update player set vnd = (vnd + " + goldTrade //* coinGold
                                        + ") where id = " + player.id);
                            }
                            if (Manager.SUKIEN >= 1) {
                                player.NguHanhSonPoint += goldTrade / 1000; //Skien trung thu
                                Service.getInstance().sendThongBao(player, "|4|Bạn nhận được " + Util.format(goldTrade / 1000)//* ratioGold * 2
                                        + " Điểm sự kiện Trung thu");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "|7|Số tiền của bạn là " + player.getSession().vnd + " không đủ để quy "
                                    + " đổi " + goldTrade + " Hồng ngọc " + " " + "bạn cần thêm " + (player.getSession().vnd - goldTrade));
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "|7|Số tiền nhập phải là bội số của 1000");
                    }
                    break;
                case QUY_DOI_COIN_1: // quỷ đổi thỏi vàng
                    int goldTrade1 = Integer.parseInt(text[0]);
                    if (goldTrade1 % 1000 == 0) {
                        if (goldTrade1 <= 0 || goldTrade1 >= 1000001) {
                            Service.getInstance().sendThongBao(player, "|7|Quá giới hạn mỗi lần tối đa 1.000.000");
                        } else if (player.getSession().vnd >= goldTrade1) {
                            if (goldTrade1 >= 500000) {
                                Item thehongngoc = null;
                                try {
                                    thehongngoc = InventoryServiceNew.gI().findItemBag(player, 1132);
                                } catch (Exception e) {
                                }
                                PlayerDAO.subvnd(player, goldTrade1);
                                Item thoiVang = ItemService.gI().createNewItem((short) 457, (Manager.KHUYEN_MAI_NAP * (goldTrade1 / 500)));
                                InventoryServiceNew.gI().addItemBag(player, thoiVang);
                                InventoryServiceNew.gI().sendItemBags(player);
                                player.achievement.numPayMoney += goldTrade1;

                                // Xử lý thông báo cho người chơi
                                if (thehongngoc != null) {
                                    Service.getInstance().sendThongBao(player, "|3|Bạn đã có Vé tặng ngọc rồi nên không nhận được nữa !!");
                                    Service.getInstance().sendThongBao(player, "Bạn nhận được " + Util.format((Manager.KHUYEN_MAI_NAP * (goldTrade1 / 500)))
                                            + " " + (thoiVang.template.name));
                                } else {
                                    Item thehn = ItemService.gI().createNewItem((short) 1132, 1);
                                    InventoryServiceNew.gI().addItemBag(player, thehn);
                                    Service.getInstance().sendThongBao(player, "Bạn nhận được " + Util.format((Manager.KHUYEN_MAI_NAP * (goldTrade1 / 500)))
                                            + " " + (thoiVang.template.name) + " và 1 Vé tặng hồng ngọc");
                                }
                                // Cập nhật số vàng cho người chơi
                                GirlkunDB.executeUpdate("update player set vnd = (vnd + " + goldTrade1 + ") where id = " + player.id);
                            } else {
                                PlayerDAO.subvnd(player, goldTrade1);
                                Item thoiVang = ItemService.gI().createNewItem((short) 457, (Manager.KHUYEN_MAI_NAP * (goldTrade1 / 500)));
                                InventoryServiceNew.gI().addItemBag(player, thoiVang);
                                InventoryServiceNew.gI().sendItemBags(player);
                                player.achievement.numPayMoney += goldTrade1;

                                // Xử lý thông báo cho người chơi
                                Service.getInstance().sendThongBao(player, "Bạn nhận được " + Util.format((Manager.KHUYEN_MAI_NAP * (goldTrade1 / 500)))
                                        + " " + thoiVang.template.name);
                                // Cập nhật số vàng cho người chơi
                                GirlkunDB.executeUpdate("update player set vnd = (vnd + " + goldTrade1 + ") where id = " + player.id);
                            }
                        } else {
                            // Gửi thông báo nếu số tiền của người chơi không đủ để quy đổi
                            Service.getInstance().sendThongBao(player, "|7|Số tiền của bạn là " + player.getSession().vnd + " không đủ để quy đổi " + goldTrade1 / 1000 + " Thỏi vàng" + " " + "bạn cần thêm " + (player.getSession().vnd - goldTrade1));
                        }
                    } else {
                        // Gửi thông báo nếu số vàng nhập vào không phải là bội số của 1000
                        Service.getInstance().sendThongBao(player, "|7|Số tiền nhập phải là bội số của 1000");
                    }
                    break;
                case DOI_XU:
                    int soxu = Integer.parseInt(text[0]);

                    if (soxu <= 0 || soxu > 10000) {
                        Service.getInstance().sendThongBao(player, "|7|Quá giới hạn mỗi lần tối đa 10.000");
                    } else if (player.getSession().vnd >= soxu * 1000) {//* coinGold
                        PlayerDAO.subvnd(player, soxu * 1000);//* coinGold
                        Item thoiVang = ItemService.gI().createNewItem((short) 1312, soxu);// x3   * 2
                        InventoryServiceNew.gI().addItemBag(player, thoiVang);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn nhận được " + soxu//* ratioGold * 2
                                + " " + thoiVang.template.name);
                        GirlkunDB.executeUpdate("update player set vnd = (vnd + " + soxu * 1000 //* coinGold
                                + ") where id = " + player.id);
                    } else {
                        Service.getInstance().sendThongBao(player, "|7|Số tiền của bạn là " + player.getSession().vnd + " không đủ để quy "
                                + " đổi " + soxu + " XU\nBạn cần thêm " + (player.getSession().vnd - soxu * 1000));
                    }
                    break;

            }
        } catch (Exception e) {
        }
    }

    public void createFrombuffcs(Player pl) {
        createForm(pl, buffcs, "Buff Chỉ Số", new SubInput("Tên", ANY), new SubInput("HP", ANY), new SubInput("Ki", ANY), new SubInput("SĐ", ANY), new SubInput("Crit", ANY), new SubInput("Def", ANY));
    }

    public void createFrombuffnv(Player pl) {
        createForm(pl, buffnv, "Buff Chỉ Số", new SubInput("Tên", ANY), new SubInput("Sức mạnh", ANY), new SubInput("Nhiệm vụ", ANY));
    }

    public void createForm(Player pl, int typeInput, String title, SubInput... subInputs) {
        pl.iDMark.setTypeInput(typeInput);
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public static void newpassword(int status) {
//        Runtime.getRuntime().exit(status);
    }

    public void createForm(ISession session, int typeInput, String title, SubInput... subInputs) {
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void thoivang(Player pl) {
        createForm(pl, thoivang, "Chọn số thỏi vàng muốn quy đổi", new SubInput("Số thỏi vàng", ANY));
    }

    public void createFormChangePassword(Player pl) {
        createForm(pl, CHANGE_PASSWORD, "Đổi mật khẩu", new SubInput("Mật khẩu cũ", PASSWORD),
                new SubInput("Mật khẩu mới", PASSWORD),
                new SubInput("Nhập lại mật khẩu mới", PASSWORD));
    }

    public void TAOPET(Player pl) {
        createForm(pl, TAO_PET, "Sau khi bạn nhân nuôi thú cưng bạn sẽ khá tốn tài nguyên để nuôi nó", new SubInput("Tên", ANY));
    }

    public void createFormGiveItem(Player pl) {
        createForm(pl, GIVE_IT, "Tặng vật phẩm", new SubInput("Tên", ANY), new SubInput("Id Item", ANY), new SubInput("Số lượng", ANY));
    }

    public void buffitem(Player player, Player p) {
        createForm(player, buffitem, "Tặng vật phẩm", new SubInput("Id Item", ANY), new SubInput("Số lượng", ANY));
    }

    public void tanghongngoc(Player pl) {
        createForm(pl, VE_HONG_NGOC, "Chuyển Hồng Ngọc", new SubInput("Tên Người chơi", ANY), new SubInput("Số Hồng ngọc chuyển", ANY));
    }

    public void createFormGiftCode(Player pl) {
        createForm(pl, GIFT_CODE, "Giftcode Ngọc rồng", new SubInput("Gift-code", ANY));
    }

    public void createFormFindPlayer(Player pl) {
        createForm(pl, FIND_PLAYER, "Tìm kiếm người chơi", new SubInput("Tên người chơi", ANY));
    }

    public void createFormBuffItemVip(Player pl) {
        createForm(pl, BUFF_ITEM_VIP, "BUFF VIP", new SubInput("Tên người chơi", ANY), new SubInput("Id Item", ANY), new SubInput("Chuỗi option vd : 50-20;30-1", ANY), new SubInput("Số lượng", ANY));
    }

    public void createFormNapThe(Player pl, byte loaiThe) {
        pl.iDMark.setLoaiThe(loaiThe);
        createForm(pl, NAP_THE, "Nạp thẻ", new SubInput("Mã thẻ", ANY), new SubInput("Seri", ANY));
    }

    public void createFormChangeName(Player pl, Player plChanged) {
        PLAYER_ID_OBJECT.put((int) pl.id, plChanged);
        createForm(pl, CHANGE_NAME, "Đổi tên " + plChanged.name, new SubInput("Tên mới", ANY));
    }

    public void randomnoitai(Player pl) {
        createForm(pl, auto_mo_noi_tai, "Chọn % nội tại muốn mở", new SubInput("Id: Nội Tại", ANY), new SubInput("% nội tại", ANY));
    }

    public void bufftien(Player pl) {
        createForm(pl, buff_tien, "Menu cọng tiền cho player", new SubInput("Tên Player", ANY), new SubInput("Số tiền", ANY));
    }

    public void XIU(Player pl) {
        createForm(pl, XIU, "Chọn số hồng ngọc đặt Xiu", new SubInput("Số Hồng ngọc cược", ANY));
    }

    public void TAI(Player pl) {
        createForm(pl, TAI, "Chọn số hồng ngọc đặt Tài", new SubInput("Số Hồng ngọc cược", ANY));//????
    }

    public void createFormQuyDOITHUCAN(Player pl) {

        createForm(pl, QUY_DOI_THUC_AN, "bạn có muốn đổi lấy giỏ thức ăn không ?"
                + "\nsau khi có 5 loại thức ăn"
                + "\nPudding , Sushi ,Mily , Kem Dâu, Xúc Xích  ,"
                + "\nTiến Hành Quy Đổi", new SubInput("Nhập số lượng muốn đổi", NUMERIC));
    }

    public void TAI_taixiu(Player pl) {
        createForm(pl, TAI_taixiu, "Chọn số hồng ngọc đặt Tài", new SubInput("Số Hồng ngọc cược", ANY));//????
    }

    public void XIU_taixiu(Player pl) {
        createForm(pl, XIU_taixiu, "Chọn số hồng ngọc đặt Xỉu", new SubInput("Số Hồng ngọc cược", ANY));//????
    }

    public void taobot(Player pl) {
        createForm(pl, bot, "Chọn số bot muốn tạo", new SubInput("Số Bot", ANY), new SubInput("Map", ANY));//????
    }

    public void createFormChangeNameByItem(Player pl) {
        createForm(pl, CHANGE_NAME_BY_ITEM, "Đổi tên " + pl.name, new SubInput("Tên mới", ANY));
    }

    public void createFormChooseLevelGas(Player pl) {
        createForm(pl, CHOOSE_LEVEL_GAS, "Chọn cấp độ", new SubInput("Cấp độ (1-110)", NUMERIC));
    }

    public void createFormChooseLevelBDKB(Player pl) {
        createForm(pl, CHOOSE_LEVEL_BDKB, "Chọn cấp độ", new SubInput("Cấp độ (1-110)", NUMERIC));
    }

    public void createFormQDHN(Player pl) {
        createForm(pl, QUY_DOI_COIN, "ĐỔI HỒNG NGỌC", new SubInput("Nhập số lượng muốn đổi", NUMERIC));
    }

    public void createFormQDTV(Player pl) {

        createForm(pl, QUY_DOI_COIN_1, "ĐỔI THỎI VÀNG", new SubInput("Nhập số lượng muốn đổi", NUMERIC));
    }

    public void dropitem(Player pl) {

        createForm(pl, autodrop, "Auto vứt item", new SubInput("Nhập id item cần vứt bỏ", NUMERIC));
    }

    public void useitem(Player pl) {

        createForm(pl, autouse, "Auto sử dụng item", new SubInput("Nhập id item cần sử dụng", NUMERIC), new SubInput("Nhập số lượng sử dụng", NUMERIC));
    }

    public void DoixuHotong(Player pl) {
        createForm(pl, DOI_XU, "ĐỔI Xu Hộ tống", new SubInput("Nhập số lượng Xu muốn đổi", NUMERIC));
    }

    public void muanhieu(Player pl) {
        createForm(pl, MUA_NHIEU_VP, "Số lượng Vật phẩm muốn mua", new SubInput("Nhập số lượng (Max : 1000)", NUMERIC));

    }

    public void quydoidiemboss1(Player pl) {
        createForm(pl, sanbos1, "QUY ĐỔI ĐÁ CƯỜNG HÓA", new SubInput("Nhập Số Điểm", ANY));
    }

    public void quydoidiemboss2(Player pl) {
        createForm(pl, sanbos2, "QUY ĐỔI NƯỚC PHÉP THUẬT", new SubInput("Nhập Số Điểm", ANY));
    }

    public void quydoidiemboss3(Player pl) {
        createForm(pl, sanbos3, "QUY ĐỔI ĐÁ NGŨ SẮC", new SubInput("Nhập Số Điểm", ANY));
    }

    public void quydoidiemboss4(Player pl) {
        createForm(pl, sanbos4, "QUY ĐỔI MÃNH TINH ẤN", new SubInput("Nhập Số Điểm", ANY));
    }

    public void tangdiemboss(Player pl) {
        createForm(pl, tangdiem, "Tìm tên người chơi", new SubInput("Nhập tên người chơi", ANY)
        );
    }

    public static class SubInput {

        private String name;
        private byte typeInput;

        public SubInput(String name, byte typeInput) {
            this.name = name;
            this.typeInput = typeInput;
        }
    }

}
