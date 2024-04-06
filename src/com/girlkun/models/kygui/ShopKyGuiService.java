package com.girlkun.models.kygui;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.item.Item;
import com.girlkun.models.item.Item.ItemOption;
import com.girlkun.models.npc.NpcFactory;
import com.girlkun.models.player.Player;
import com.girlkun.network.io.Message;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class ShopKyGuiService {

    private static ShopKyGuiService instance;

    public static ShopKyGuiService gI() {
        if (instance == null) {
            instance = new ShopKyGuiService();
        }
        return instance;
    }

    private List<ItemKyGui> getItemKyGui(Player pl, byte tab, byte... max) {
        List<ItemKyGui> its = new ArrayList<>();
        List<ItemKyGui> listSort = new ArrayList<>();
        List<ItemKyGui> listSort2 = new ArrayList<>();
        ShopKyGuiManager.gI().listItem.stream().filter((it) -> (it != null && it.tab == tab && !it.isBuy && it.player_sell != pl.id)).forEachOrdered((it) -> {
            its.add(it);
        });
        its.stream().filter(i -> i != null).sorted(Comparator.comparing(i -> i.isUpTop, Comparator.reverseOrder())).forEach(i -> listSort.add(i));
        if (max.length == 2) {
            if (listSort.size() > max[1]) {
                for (int i = max[0]; i < max[1]; i++) {
                    if (listSort.get(i) != null) {
                        listSort2.add(listSort.get(i));
                    }
                }
            } else {
                for (int i = max[0]; i <= max[0]; i++) {
                    if (listSort.get(i) != null) {
                        listSort2.add(listSort.get(i));
                    }
                }
            }
            return listSort2;
        }
        if (max.length == 1 && listSort.size() > max[0]) {
            for (int i = 0; i < max[0]; i++) {
                if (listSort.get(i) != null) {
                    listSort2.add(listSort.get(i));
                }
            }
            return listSort2;
        }
        return listSort;
    }

    private List<ItemKyGui> getItemKyGui() {
        List<ItemKyGui> its = new ArrayList<>();
        List<ItemKyGui> listSort = new ArrayList<>();
        ShopKyGuiManager.gI().listItem.stream().filter((it) -> (it != null && !it.isBuy)).forEachOrdered((it) -> {
            its.add(it);
        });
        its.stream().filter(i -> i != null).sorted(Comparator.comparing(i -> i.isUpTop, Comparator.reverseOrder())).forEach(i -> listSort.add(i));
        return listSort;
    }

    public void buyItem(Player pl, int id) {
        ItemKyGui it = getItemBuy(id);
        if (it == null || it.isBuy) {
            Service.getInstance().sendThongBao(pl, "Vật phẩm không tồn tại hoặc đã có người khác mua");
            openShopKyGui(pl);
            return;
        }
        if (it.player_sell == pl.id) {
            Service.getInstance().sendThongBao(pl, "Không thể mua vật phẩm do chính bạn bán");
            openShopKyGui(pl);
            return;
        }
        boolean isBuy = false;
        if (it.goldSell > 0) {
            if (pl.inventory.gold >= it.goldSell) {
                pl.inventory.gold -= it.goldSell;
                isBuy = true;
            } else {
                Service.getInstance().sendThongBao(pl, "Bạn không đủ vàng để mua vật phẩm này!");
                isBuy = false;
                openShopKyGui(pl);
            }
        } else if (it.gemSell > 0) {
            if (pl.inventory.ruby >= it.gemSell) {
                pl.inventory.ruby -= it.gemSell;
                isBuy = true;
            } else {
                Service.getInstance().sendThongBao(pl, "Bạn không đủ hồng ngọc để mua vật phẩm này!");
                isBuy = false;
                openShopKyGui(pl);
            }
        }
        Service.getInstance().sendMoney(pl);
        if (isBuy) {
            Item item = ItemService.gI().createNewItem(it.itemId);
            item.quantity = it.quantity;
            item.itemOptions.addAll(it.options);
            it.isBuy = true;
            if (it.isBuy) {
                InventoryServiceNew.gI().addItemBag(pl, item);
                InventoryServiceNew.gI().sendItemBags(pl);
                Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + item.template.name);
                openShopKyGui(pl);
            }
        }
    }

    public ItemKyGui getItemBuy(int id) {
        for (ItemKyGui it : getItemKyGui()) {
            if (it != null && it.id == id) {
                return it;
            }
        }
        return null;
    }

    public ItemKyGui getItemBuy(Player pl, int id) {
        for (ItemKyGui it : ShopKyGuiManager.gI().listItem) {
            if (it != null && it.id == id && it.player_sell == pl.id) {
                return it;
            }
        }
        return null;
    }

    /**
     * Mở cửa hàng Ký Gửi cho người chơi với các thông tin về vật phẩm và trang
     * hiện tại.
     *
     * @param pl Người chơi
     * @param index Chỉ số của tab cửa hàng Ký Gửi
     * @param page Trang hiện tại
     */
    public void openShopKyGui(Player pl, byte index, int page) {
        // Kiểm tra nếu trang hiện tại lớn hơn tổng số trang có sẵn
        if (page > getItemKyGui(pl, index).size()) {
            return; // Tránh truy cập trang không hợp lệ
        }

        // Khởi tạo một Message để gửi thông tin về cửa hàng Ký Gửi đến người chơi
        Message msg = null;
        try {
            msg = new Message(-100);

            // Ghi dữ liệu vào Message
            msg.writer().writeByte(index);

            // Lấy danh sách vật phẩm theo chỉ số và trang hiện tại
            List<ItemKyGui> items = getItemKyGui(pl, index);
            List<ItemKyGui> itemsSend = getItemKyGui(pl, index, (byte) (page * 100), (byte) (page * 100 + 100));

            // Tính toán số lượng trang tối đa
            byte tab = (byte) (items.size() / 100 > 0 ? items.size() / 100 : 1);
            msg.writer().writeByte(tab); // Số lượng trang tối đa
            msg.writer().writeByte(page); // Trang hiện tại

            // Ghi thông tin về các vật phẩm vào Message
            msg.writer().writeByte(itemsSend.size());
            for (int j = 0; j < itemsSend.size(); j++) {
                ItemKyGui itk = itemsSend.get(j);
                Item it = ItemService.gI().createNewItem(itk.itemId);
                it.itemOptions.clear();
                if (itk.options.isEmpty()) {
                    it.itemOptions.add(new ItemOption(73, 0));
                } else {
                    it.itemOptions.addAll(itk.options);
                }

                // Ghi thông tin về mỗi vật phẩm vào Message
                msg.writer().writeShort(it.template.id);
                msg.writer().writeShort(itk.id);
                msg.writer().writeInt(itk.goldSell);
                msg.writer().writeInt(itk.gemSell);
               
                msg.writer().writeByte(0); // Loại mua
                if (pl.getSession().version >= 222) {
                    msg.writer().writeInt(itk.quantity);
                } else {
                    msg.writer().writeByte(itk.quantity);
                }
                msg.writer().writeByte(itk.player_sell == pl.id ? 1 : 0); // Là người chơi đang xem
                msg.writer().writeByte(it.itemOptions.size());

                // Ghi thông tin về các tùy chọn của vật phẩm
                for (int a = 0; a < it.itemOptions.size(); a++) {
                    msg.writer().writeByte(it.itemOptions.get(a).optionTemplate.id);
                    msg.writer().writeShort(it.itemOptions.get(a).param);
                }
                msg.writer().writeByte(0);
                msg.writer().writeByte(0);
            }

            // Gửi Message đến người chơi
            pl.sendMessage(msg);
        } catch (Exception e) {
            System.out.println("Lỗi khi mở cửa hàng Ký Gửi: " + e.getMessage());
        } finally {
            // Đảm bảo giải phóng tài nguyên khi kết thúc
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    /**
     * Thực hiện việc đưa vật phẩm lên đầu danh sách Shop Ký Gửi.
     *
     * @param pl Người chơi
     * @param id ID của ItemKyGui trong Shop Ký Gửi
     */
    public void upItemToTop(Player pl, int id) {
        // Lấy thông tin vật phẩm từ danh sách Shop Ký Gửi
        ItemKyGui it = getItemBuy(id);

        // Kiểm tra nếu vật phẩm không tồn tại hoặc đã được người khác mua
        if (it == null || it.isBuy) {
            Service.getInstance().sendThongBao(pl, "Vật phẩm không tồn tại hoặc đã được người khác mua");
            openShopKyGui(pl);
            return;
        }

        // Kiểm tra nếu người chơi không phải là chủ sở hữu của vật phẩm
        if (it.player_sell != pl.id) {
            Service.getInstance().sendThongBao(pl, "Vật phẩm không thuộc quyền sở hữu");
            openShopKyGui(pl);
            return;
        }

        // Thiết lập ID vật phẩm cần đưa lên trang đầu
        pl.iDMark.setIdItemUpTop(id);

        // Tạo menu xác nhận từ người chơi
        NpcService.gI().createMenuConMeo(
                pl,
                ConstNpc.UP_TOP_ITEM,
                -1,
                "Bạn có muốn đưa vật phẩm ['" + ItemService.gI().createNewItem(it.itemId).template.name + "'] của bản thân lên trang đầu?\nYêu cầu 500 hồng ngọc.",
                "Đồng ý",
                "Từ Chối"
        );
    }

    /**
     * Thực hiện hành động nhận tiền hoặc hủy bán vật phẩm từ Shop Ký Gửi.
     *
     * @param pl Người chơi
     * @param action Hành động (1: Hủy bán, 2: Nhận tiền)
     * @param id ID của ItemKyGui trong Shop Ký Gửi
     */
    public void claimOrDel(Player pl, byte action, int id) {
        ItemKyGui it = getItemBuy(pl, id);

        switch (action) {
            case 1: // Hủy bán vật phẩm
                if (it == null || it.isBuy) {
                    // Kiểm tra nếu vật phẩm không tồn tại hoặc đã được bán
                    Service.getInstance().sendThongBao(pl, "Vật phẩm không tồn tại hoặc đã được bán");
                    openShopKyGui(pl);
                    return;
                }

                if (it.player_sell != pl.id) {
                    // Kiểm tra nếu người chơi không phải là chủ sở hữu của vật phẩm
                    Service.getInstance().sendThongBao(pl, "Vật phẩm không thuộc quyền sở hữu");
                    openShopKyGui(pl);
                    return;
                }

                // Tạo mới một đối tượng Item từ ItemKyGui
                Item item = ItemService.gI().createNewItem(it.itemId);
                item.quantity = it.quantity;
                item.itemOptions.addAll(it.options);

                // Xóa vật phẩm khỏi danh sách Shop Ký Gửi và thêm vào túi đồ của người chơi
                if (ShopKyGuiManager.gI().listItem.remove(it)) {
                    InventoryServiceNew.gI().addItemBag(pl, item);
                    InventoryServiceNew.gI().sendItemBags(pl);
                    Service.getInstance().sendMoney(pl);
                    Service.getInstance().sendThongBao(pl, "Hủy bán vật phẩm thành công");
                    openShopKyGui(pl);
                }
                break;

            case 2: // Nhận tiền từ việc bán vật phẩm
                if (it == null || !it.isBuy) {
                    // Kiểm tra nếu vật phẩm không tồn tại hoặc chưa được bán
                    Service.getInstance().sendThongBao(pl, "Vật phẩm không tồn tại hoặc chưa được bán");
                    openShopKyGui(pl);
                    return;
                }

                if (it.player_sell != pl.id) {
                    // Kiểm tra nếu người chơi không phải là chủ sở hữu của vật phẩm
                    Service.getInstance().sendThongBao(pl, "Vật phẩm không thuộc quyền sở hữu");
                    openShopKyGui(pl);
                    return;
                }

                // Nhận tiền từ việc bán vật phẩm và cập nhật số tiền của người chơi
                if (it.goldSell > 0) {
                    pl.inventory.gold += it.goldSell - it.goldSell * 5 / 100;
                } else if (it.gemSell > 0) {
                    pl.inventory.ruby += it.gemSell - it.gemSell * 5 / 100;
                }

                // Xóa vật phẩm khỏi danh sách Shop Ký Gửi
                if (ShopKyGuiManager.gI().listItem.remove(it)) {
                    Service.getInstance().sendMoney(pl);
                    Service.getInstance().sendThongBao(pl, "Bạn đã bán vật phẩm thành công");
                    openShopKyGui(pl);
                }
                break;
        }
    }

    /**
     * Trả về danh sách các ItemKyGui mà người chơi có thể ký gửi.
     *
     * @param pl Người chơi
     * @return Danh sách các ItemKyGui
     */
    public List<ItemKyGui> getItemCanKiGui(Player pl) {
        List<ItemKyGui> its = new ArrayList<>();

        try {
            // Lấy danh sách ItemKyGui từ cửa hàng
            ShopKyGuiManager.gI().listItem.stream()
                    .filter((it) -> it != null && it.player_sell == pl.id)
                    .forEachOrdered((it) -> its.add(it));

            // Lấy danh sách ItemKyGui từ túi đồ cá nhân
            pl.inventory.itemsBag.stream()
                    .filter((it) -> it.isNotNullItem()
                    && (InventoryServiceNew.gI().hasOptionTemplateId(it, 86) || InventoryServiceNew.gI().hasOptionTemplateId(it, 87)))
                    .forEachOrdered((it) -> {
                        // Tạo mới ItemKyGui từ Item và thêm vào danh sách
                        its.add(new ItemKyGui(
                                InventoryServiceNew.gI().getIndexBag(pl, it),
                                it.template.id,
                                (int) pl.id,
                                (byte) 4, // 4 là tab trong ShopKyGui
                                -1, // Giá vàng
                                -1, // Giá hồng ngọc
                                it.quantity,
                                (byte) -1, // Loại item
                                it.itemOptions,
                                false, // Không mua
                                System.currentTimeMillis()
                        ));
                    });
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            System.out.println("Lỗi khi lấy danh sách ItemKyGui: " + e.getMessage());
            e.printStackTrace(); // In stack trace để có thêm thông tin về lỗi
        }

        return its; // Trả về danh sách ItemKyGui
    }

    public int getMaxId() {
        try {
            List<Integer> idList = ShopKyGuiManager.gI().listItem.stream()
                    .filter(Objects::nonNull)
                    .map(it -> it.id)
                    .collect(Collectors.toList());

            if (!idList.isEmpty()) {
                return Collections.max(idList);
            } else {
                System.out.println("Danh sách rỗng, không có phần tử nào.");
                return 0;
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi tìm kiếm giá trị lớn nhất: " + e.getMessage());
            e.printStackTrace(); // In stack trace để có thêm thông tin về lỗi
            return 0;
        }
    }

    public byte getTabKiGui(Item it) {
        if (it.template.type >= 0 && it.template.type <= 2) {
            return 0;
        } else if ((it.template.type >= 3 && it.template.type <= 4) || it.template.type == 12 || it.template.type == 33) {
            return 1;
        } else if (it.template.type == 29) {
            return 2;
        } else {
            return 3;
        }
    }

    public void KiGui(Player player, int id, int money, byte moneyType, int quantity) {
        try {
            // Kiểm tra xem người chơi có đủ hồng ngọc không
            if (player.inventory.ruby < 5) {
                Service.getInstance().sendThongBao(player, "Bạn cần có ít nhất 5 hồng ngọc để làm phí ký gửi");
                openShopKyGui(player);
                return;
            }

            // Kiểm tra id và số lượng hợp lệ
            if (id < 0 || id >= player.inventory.itemsBag.size() || money <= 0 || quantity <= 0 || quantity > 999) {
                Service.getInstance().sendThongBao(player, "Có lỗi xảy ra");
                openShopKyGui(player);
                return;
            }

            Item itemToSell = ItemService.gI().copyItem(player.inventory.itemsBag.get(id));

            // Kiểm tra số lượng cần ký gửi có hợp lệ không
            if (quantity > itemToSell.quantity) {
                Service.getInstance().sendThongBao(player, "Có lỗi xảy ra");
                openShopKyGui(player);
                return;
            }

            player.inventory.ruby -= 5; // Trừ 5 hồng ngọc cho phí ký gửi

            // Xác định loại tiền (vàng hoặc hồng ngọc) và thực hiện ký gửi
            switch (moneyType) {
                case 0: // Vàng
                    InventoryServiceNew.gI().subQuantityItemsBag(player, player.inventory.itemsBag.get(id), quantity);
                    ShopKyGuiManager.gI().listItem.add(new ItemKyGui(getMaxId() + 1, itemToSell.template.id, (int) player.id, getTabKiGui(itemToSell), money, -1, quantity, (byte) 0, itemToSell.itemOptions, false, System.currentTimeMillis()));
                    InventoryServiceNew.gI().sendItemBags(player);
                    openShopKyGui(player);
                    Service.getInstance().sendMoney(player);
                    Service.getInstance().sendThongBao(player, "Đăng bán thành công");
                    break;
                case 1: // Hồng ngọc
                    InventoryServiceNew.gI().subQuantityItemsBag(player, player.inventory.itemsBag.get(id), quantity);
                    ShopKyGuiManager.gI().listItem.add(new ItemKyGui(getMaxId() + 1, itemToSell.template.id, (int) player.id, getTabKiGui(itemToSell), -1, money, quantity, (byte) 0, itemToSell.itemOptions, false, System.currentTimeMillis()));
                    InventoryServiceNew.gI().sendItemBags(player);
                    openShopKyGui(player);
                    Service.getInstance().sendMoney(player);
                    Service.getInstance().sendThongBao(player, "Đăng bán thành công");
                    break;
                default:
                    Service.getInstance().sendThongBao(player, "Có lỗi xảy ra");
                    openShopKyGui(player);
                    break;
            }
        } catch (Exception e) {
            // Xử lý ngoại lệ
            System.out.println("Lỗi khi thực hiện ký gửi"+e);
            e.printStackTrace();
        }
    }

    public void openShopKyGui(Player player) {
        Message message = null;
        try {
            message = new Message(-44);
            message.writer().writeByte(2);
            message.writer().writeByte(5);
            // Lặp qua 5 tab của cửa hàng
            for (byte i = 0; i < 5; i++) {
                if (i == 4) {
                    // Gửi thông tin về tab cuối cùng (mục có thể ký gửi)
                    message.writer().writeUTF(ShopKyGuiManager.gI().tabName[i]);
                    message.writer().writeByte(0);
                    message.writer().writeByte(getItemCanKiGui(player).size());

                    // Gửi thông tin về từng mục có thể ký gửi
                    for (int j = 0; j < getItemCanKiGui(player).size(); j++) {
                        ItemKyGui itemKyGui = getItemCanKiGui(player).get(j);
                        if (itemKyGui == null) {
                            continue;
                        }
                        // Tạo đối tượng Item từ ItemKyGui
                        Item item = ItemService.gI().createNewItem(itemKyGui.itemId);
                        item.itemOptions.clear();

                        // Thêm các options nếu có
                        if (itemKyGui.options.isEmpty()) {
                            item.itemOptions.add(new ItemOption(73, 0));
                        } else {
                            item.itemOptions.addAll(itemKyGui.options);
                        }

                        // Gửi thông tin về mục
                        message.writer().writeShort(item.template.id);
                        message.writer().writeShort(itemKyGui.id);
                        message.writer().writeInt(itemKyGui.goldSell);
                        message.writer().writeInt(itemKyGui.gemSell);

                        // Loại mua (buy type)
                        if (getItemBuy(player, itemKyGui.id) == null) {
                            message.writer().writeByte(0);
                        } else if (itemKyGui.isBuy) {
                            message.writer().writeByte(2);
                        } else {
                            message.writer().writeByte(1);
                        }

                        // Số lượng
                        if (player.getSession().version >= 222) {
                            message.writer().writeInt(itemKyGui.quantity);
                        } else {
                            message.writer().writeByte(itemKyGui.quantity);
                        }

                        // isMe
                        message.writer().writeByte(1);

                        // Các options của item
                        message.writer().writeByte(item.itemOptions.size());
                        for (int a = 0; a < item.itemOptions.size(); a++) {
                            message.writer().writeByte(item.itemOptions.get(a).optionTemplate.id);
                            message.writer().writeShort(item.itemOptions.get(a).param);
                        }

                        // Các dữ liệu không cần thiết
                        message.writer().writeByte(0);
                        message.writer().writeByte(0);
                    }
                } else {
                    // Gửi thông tin về các tab khác (các mục khác)
                    List<ItemKyGui> items = getItemKyGui(player, i);
                    List<ItemKyGui> itemsSend = getItemKyGui(player, i, (byte) 100);
                    message.writer().writeUTF(ShopKyGuiManager.gI().tabName[i]);

                    // Tính toán số trang dựa trên số lượng mục
                    byte maxPage = (byte) (items.size() / 100 > 0 ? items.size() / 100 : 1);
                    message.writer().writeByte(maxPage);

                    // Gửi thông tin về các mục
                    message.writer().writeByte(itemsSend.size());
                    for (int j = 0; j < itemsSend.size(); j++) {
                        ItemKyGui itemKyGui = itemsSend.get(j);

                        // Tạo đối tượng Item từ ItemKyGui
                        Item item = ItemService.gI().createNewItem(itemKyGui.itemId);
                        item.itemOptions.clear();

                        // Thêm các options nếu có
                        if (itemKyGui.options.isEmpty()) {
                            item.itemOptions.add(new ItemOption(73, 0));
                        } else {
                            item.itemOptions.addAll(itemKyGui.options);
                        }

                        // Gửi thông tin về mục
                        message.writer().writeShort(item.template.id);
                        message.writer().writeShort(itemKyGui.id);
                        message.writer().writeInt(itemKyGui.goldSell);
                        message.writer().writeInt(itemKyGui.gemSell);

                        // Loại mua (buy type)
                        message.writer().writeByte(0);

                        // Số lượng
                        if (player.getSession().version >= 222) {
                            message.writer().writeInt(itemKyGui.quantity);
                        } else {
                            message.writer().writeByte(itemKyGui.quantity);
                        }

                        // isMe
                        message.writer().writeByte(itemKyGui.player_sell == player.id ? 1 : 0);

                        // Các options của item
                        message.writer().writeByte(item.itemOptions.size());
                        for (int a = 0; a < item.itemOptions.size(); a++) {
                            message.writer().writeByte(item.itemOptions.get(a).optionTemplate.id);
                            message.writer().writeShort(item.itemOptions.get(a).param);
                        }

                        message.writer().writeByte(0);
                        message.writer().writeByte(0);
                    }
                }
            }

            // Gửi Message cho người chơi
            player.sendMessage(message);
        } catch (Exception e) {
            // Xử lý lỗi
            System.out.println("Lỗi khi mở cửa hàng ký gửi");
            e.printStackTrace();
        } finally {
            // Giải phóng Message
            if (message != null) {
                message.cleanup();
                message = null;
            }
        }
    }
}
