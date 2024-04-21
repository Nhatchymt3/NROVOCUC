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
import java.util.Optional;
import java.util.stream.Collectors;


public class ShopKyGuiService {

    private static ShopKyGuiService instance;

    public static ShopKyGuiService gI() {
        if (instance == null) {
            instance = new ShopKyGuiService();
        }
        return instance;
    }

    private List<ItemKyGui> getItemKyGui2(Player pl, byte tab, byte to, byte max) {
//        System.err.print("\nTab in :" + tab);
        List<ItemKyGui> its = new ArrayList<>();
        List<ItemKyGui> listSort = new ArrayList<>();
        List<ItemKyGui> listSort2 = new ArrayList<>();
        ShopKyGuiManager.gI().listItem.stream().filter((it) -> (it != null && it.tab == tab && !it.isBuy)).forEachOrdered((it) -> {
            its.add(it);
        });
        its.stream().filter(i -> i != null).sorted(Comparator.comparing(i -> i.isUpTop, Comparator.reverseOrder())).forEach(i -> listSort.add(i));
        for (int i = to; i <= max && i < listSort.size(); i++) {
            listSort2.add(listSort.get(i));
        }
        return listSort2;
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

    private boolean isKyGui(Item item) {
        switch (item.template.type) {
            case 27:
                switch (item.template.id) {
                    case 921:
                    case 1155:
                    case 1156:
                    case 568:
                        return true;
                }
                return false;
            case 21:
            case 72:
                return true;
        }
        for (int i = 0; i < item.itemOptions.size(); i++) {
            if (item.itemOptions.get(i).optionTemplate.id == 86) {
                return true;
            }
        }
        return false;
    }

    private boolean SubThoiVang(Player pl, int quatity) {
        for (Item item : pl.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 457 && item.quantity >= quatity) {
                InventoryServiceNew.gI().subQuantityItemsBag(pl, item, quatity);
                return true;
            }
        }
        return false;
    }
    
    public void buyItem(Player pl, int id) {
        // if (pl.nPoint.power < 17000000000L) {
        //     Service.gI().sendThongBao(pl, "Yêu cầu sức mạnh lớn hơn 17 tỷ");
        //     openShopKyGui(pl);
        //     return;
        // }
        ItemKyGui it = getItemBuy(id);
        if (it == null || it.isBuy) {
            Service.gI().sendThongBao(pl, "Vật phẩm không tồn tại hoặc đã được bán");
            return;
        }
        if (it.player_sell == pl.id) {
            Service.gI().sendThongBao(pl, "Không thể mua vật phẩm bản thân đăng bán");
            openShopKyGui(pl);
            return;
        }
        boolean isBuy = false;
        if (it.goldSell > 0) {
            if (SubThoiVang(pl, it.goldSell)) {
                isBuy = true;
            } else {
                Service.gI().sendThongBao(pl, "Bạn không đủ thỏi vàng để mua vật phẩm");
                isBuy = false;
            }
        } else if (it.gemSell > 0) {
            if (pl.inventory.ruby >= it.gemSell) {
                pl.inventory.ruby -= it.gemSell;
                isBuy = true;
            } else {
                Service.gI().sendThongBao(pl, "Bạn không đủ hồng ngọc để mua vật phẩm này!");
                isBuy = false;
            }
        }
        Service.gI().sendMoney(pl);
        if (isBuy) {
            Item item = ItemService.gI().createNewItem(it.itemId);
            item.quantity = it.quantity;
            item.itemOptions.addAll(it.options);
            it.isBuy = true;
            if (it.isBuy) {
                InventoryServiceNew.gI().addItemBag(pl, item);
                InventoryServiceNew.gI().sendItemBags(pl);
                UpdateItem(id);
                ShopKyGuiManager.gI().updateItem(it);
                Service.gI().sendThongBao(pl, "Bạn đã nhận được " + item.template.name);
//                ShopKyGuiManager.gI().SaveandLoad();
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
    public void UpdateItem(int id) {
        for (ItemKyGui it : ShopKyGuiManager.gI().listItem) {
            if (it != null && it.id == id) {
                it.isBuy=true;
            }
        }
    }

    public ItemKyGui getItemBuy(Player pl, ItemKyGui itk) {
        for (ItemKyGui it : ShopKyGuiManager.gI().listItem) {
            if (it != null && it.id == itk.id && it.player_sell == pl.id&&itk.gemSell != -1 || itk.goldSell != -1) {
                return it;
            }
        }
        return null;
    }

    public void openShopKyGui(Player pl, byte index, int page) {
        if (page > getItemKyGui(pl, index).size()) {
            return;
        }
        Message msg = null;
        try {
            msg = new Message(-100);
            msg.writer().writeByte(index);
            List<ItemKyGui> items = getItemKyGui(pl, index);
            List<ItemKyGui> itemsSend = getItemKyGui2(pl, index, (byte) (page * 10), (byte) (page * 10 + 10));
            byte tab = (byte) (items.size() / 10 > 0 ? (items.size() / 10) : 1);
            msg.writer().writeByte(tab); // max page
            msg.writer().writeByte(page);
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
                msg.writer().writeShort(it.template.id);
                msg.writer().writeShort(itk.id);
                msg.writer().writeInt(itk.goldSell);
                msg.writer().writeInt(itk.gemSell);
                msg.writer().writeByte(0); // buy type
                if (pl.getSession().version >= 222) {
                    msg.writer().writeInt(itk.quantity);
                } else {
                    msg.writer().writeByte(itk.quantity);
                }
                msg.writer().writeByte(itk.player_sell == pl.id ? 1 : 0); // isMe
                msg.writer().writeByte(it.itemOptions.size());
                for (int a = 0; a < it.itemOptions.size(); a++) {
                    msg.writer().writeByte(it.itemOptions.get(a).optionTemplate.id);
                    msg.writer().writeShort(it.itemOptions.get(a).param);
                }
                msg.writer().writeByte(0);
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
            System.out.println("Lỗi open shop ký gửi");
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void upItemToTop(Player pl, int id) {
        ItemKyGui it = getItemBuy(id);
        if (it == null || it.isBuy) {
            Service.gI().sendThongBao(pl, "Vật phẩm không tồn tại hoặc đã được bán");
            return;
        }
        if (it.player_sell != pl.id) {
            Service.gI().sendThongBao(pl, "Vật phẩm không thuộc quyền sở hữu");
            openShopKyGui(pl);
            return;
        }
        pl.iDMark.setIdItemUpTop(id);
        NpcService.gI().createMenuConMeo(pl, ConstNpc.UP_TOP_ITEM, -1, "Bạn có muốn đưa vật phẩm ['" + ItemService.gI().createNewItem(it.itemId).template.name + "'] của bản thân lên trang đầu?\nYêu cầu 1000 ruby.", "Đồng ý", "Từ Chối");
    }

    public void StartupItemToTop(Player pl) {
        if (pl.inventory.ruby < 1000) {
            Service.gI().sendThongBao(pl, "Bạn cần có ít nhất 1000 ruby đưa vật phẩm lên trang đầu");
            return;
        }
        for (ItemKyGui its : ShopKyGuiManager.gI().listItem) {
            if (its.id == pl.iDMark.getIdItemUpTop()) {
                its.isUpTop = 1;
                Service.gI().sendThongBao(pl, "Đưa vật phẩm lên trang đầu thành công");
                ShopKyGuiManager.gI().updateItem(its);
                break;
            }
        }

        openShopKyGui(pl);
    }

    public void claimOrDel(Player pl, byte action, int id) {
        List<ItemKyGui> itk = getItemCanKiGui(pl);
        Optional<ItemKyGui> foundItem = itk.stream()
                                            .filter(item -> item.getId() == id)
                                            .findFirst();
        ItemKyGui it = getItemBuy(pl, foundItem.get());
        switch (action) {
            case 1: // hủy vật phẩm
                if (it == null || it.isBuy) {
                    Service.gI().sendThongBao(pl, "Vật phẩm không tồn tại hoặc đã được bán");
                    return;
                }
                if (it.player_sell != pl.id) {
                    Service.gI().sendThongBao(pl, "Vật phẩm không thuộc quyền sở hữu");
                    openShopKyGui(pl);
                    return;
                }
                Item item = ItemService.gI().createNewItem(it.itemId);
                item.quantity = it.quantity;
                item.itemOptions.addAll(it.options);
                if (ShopKyGuiManager.gI().listItem.remove(it)) {
                    InventoryServiceNew.gI().addItemBag(pl, item);
                    InventoryServiceNew.gI().sendItemBags(pl);
                    Service.gI().sendMoney(pl);
                    //ShopKyGuiManager.gI().SaveandLoad();
                    Service.gI().sendThongBao(pl, "Hủy bán vật phẩm thành công");
                    openShopKyGui(pl);
                }
                break;
            case 2: // nhận tiền
                if (it == null || !it.isBuy) {
                    Service.gI().sendThongBao(pl, "Vật phẩm không tồn tại hoặc chưa được bán");
                    return;
                }
                if (it.player_sell != pl.id) {
                    Service.gI().sendThongBao(pl, "Vật phẩm không thuộc quyền sở hữu");
                    openShopKyGui(pl);
                    return;
                }
                if (it.goldSell > 0) {
                    Item tvAdd = ItemService.gI().createNewItem((short) 457);
                    tvAdd.quantity = it.goldSell - it.goldSell * 10 / 100;
                    InventoryServiceNew.gI().addItemBag(pl, tvAdd);
                } else if (it.gemSell > 0) {
                    pl.inventory.ruby += it.gemSell - it.gemSell * 10 / 100;
                }
                if (ShopKyGuiManager.gI().listItem.remove(it)) {
                    Service.gI().sendMoney(pl);
                    ShopKyGuiManager.gI().deleteItem(id);
                    Service.gI().sendThongBao(pl, "Bạn đã bán vật phẩm thành công");
                    openShopKyGui(pl);

                }
                break;
        }
    }

    public List<ItemKyGui> getItemCanKiGui(Player pl) {
        List<ItemKyGui> its = new ArrayList<>();
        ShopKyGuiManager.gI().listItem.stream().filter((it) -> (it != null && it.player_sell == pl.id)).forEachOrdered((it) -> {
            its.add(it);
        });
        pl.inventory.itemsBag.stream().filter((it) -> (it.isNotNullItem())).forEachOrdered((it) -> {
            its.add(new ItemKyGui(InventoryServiceNew.gI().getIndexBag(pl, it), it.template.id, (int) pl.id, (byte) 4, -1, -1, it.quantity, (byte) -1, it.itemOptions, false));
        });
        return its;
    }

    public int getMaxId() {
        try {
            List<Integer> idList = new ArrayList<>();
            for (ItemKyGui it : ShopKyGuiManager.gI().listItem) {
                if (it != null) {
                    idList.add(it.id);
                }
            }
    
            // Sắp xếp danh sách theo thứ tự tăng dần
            Collections.sort(idList);
    
            int smallestId = 0;
            for (int id : idList) {
                if (id > smallestId + 1) {
                    // Nếu có số cách xa số nhỏ nhất hiện tại hơn 1 đơn vị, trả về số nhỏ nhất + 1
                    return smallestId + 1;
                } else if (id == smallestId + 1) {
                    // Nếu có số kế tiếp, cập nhật số nhỏ nhất
                    smallestId++;
                }
            }
    
            // Nếu không có số kế tiếp, trả về số nhỏ nhất + 1
            return smallestId + 1;
        } catch (Exception e) {
            System.out.println("Lỗi get max id");
            return 0;
        }
    }
    

    public byte getTabKiGui(Item it) {
        if (it.template.type >= 0 && it.template.type <= 2) {
            return 0;
        } else if ((it.template.type >= 3 && it.template.type <= 4)) {
            return 1;
        } else if (it.template.type == 29 || it.template.type == 33 || it.template.type == 12) {
            return 2;
        } else {
            return 3;
        }
    }

    public void KiGui(Player pl, int id, int money, int moneyType, int quantity) {
        try {
            if (!SubThoiVang(pl, 1)) {
                Service.gI().sendThongBao(pl, "Bạn cần có ít nhất 1 thỏi vàng để làm phí đăng bán");
                return;
            }

            Item it = ItemService.gI().copyItem(pl.inventory.itemsBag.get(id));
            for (Item.ItemOption daubuoi : it.itemOptions) {
                if (daubuoi.optionTemplate.id == 30) {
                    Service.gI().sendThongBao(pl, "Vật phẩm không thể kí gửi");
                    openShopKyGui(pl);
                    return;
                }
            }
            if (money <= 0 || quantity > it.quantity) {
                openShopKyGui(pl);
                return;
            }

            if (quantity > 99) {
                Service.gI().sendThongBao(pl, "Ký gửi tối đa x99");
                openShopKyGui(pl);
                return;
            }
            pl.inventory.gem -= 5;
            switch (moneyType) {
                case 0:// vàng
                    if (money > 100000) {
                        Service.gI().sendThongBao(pl, "không thể ký gửi quá 100000 thỏi vàng");
                    } else {
                        ItemKyGui item = new ItemKyGui(getMaxId(), it.template.id, (int) pl.id, getTabKiGui(it), money, -1, quantity, (byte) 0, it.itemOptions, false);
                        InventoryServiceNew.gI().subQuantityItemsBag(pl, pl.inventory.itemsBag.get(id), quantity);
                        ShopKyGuiManager.gI().listItem.add(item);
                        InventoryServiceNew.gI().sendItemBags(pl);
                        openShopKyGui(pl);
                        Service.gI().sendMoney(pl);
                        Service.gI().sendThongBao(pl, "Đăng bán thành công");
                        ShopKyGuiManager.gI().SaveItem(item);
                    }
                    break;
                // case 1:// hồng ngọc
                //     if (money > 100000) {
                //         Service.gI().sendThongBao(pl, "không thể ký gửi quá 100000 ngọc");
                //     } else {
                //         InventoryServiceNew.gI().subQuantityItemsBag(pl, pl.inventory.itemsBag.get(id), quantity);
                //         ShopKyGuiManager.gI().listItem.add(new ItemKyGui(getMaxId() + 1, it.template.id, (int) pl.id, getTabKiGui(it), -1, money, quantity, (byte) 0, it.itemOptions, false));
                //         InventoryServiceNew.gI().sendItemBags(pl);
                //         openShopKyGui(pl);
                //         Service.gI().sendMoney(pl);
                //         Service.gI().sendThongBao(pl, "Đăng bán thành công");
                //         //ShopKyGuiManager.gI().SaveandLoad();
                //     }

                    // break;
                default:
                    Service.gI().sendThongBao(pl, "Có lỗi xảy ra");
                    openShopKyGui(pl);
                    break;
            }
        } catch (Exception e) {
            System.out.println("Lỗi ký gửi");
        }
    }

    public void openShopKyGui(Player pl) {
        Message msg = null;
        try {
            msg = new Message(-44);
            msg.writer().writeByte(2);
            msg.writer().writeByte(5);
            for (byte i = 0; i < 5; i++) {
                if (i == 4) {
                    msg.writer().writeUTF(ShopKyGuiManager.gI().tabName[i]);
                    msg.writer().writeByte(0);
                    msg.writer().writeByte(getItemCanKiGui(pl).size());
                    for (int j = 0; j < getItemCanKiGui(pl).size(); j++) {
                        ItemKyGui itk = getItemCanKiGui(pl).get(j);
                        if (itk == null) {
                            continue;
                        }
                        Item it = ItemService.gI().createNewItem(itk.itemId);
                        it.itemOptions.clear();
                        if (itk.options.isEmpty()) {
                            it.itemOptions.add(new ItemOption(73, 0));
                        } else {
                            it.itemOptions.addAll(itk.options);
                        }
                        msg.writer().writeShort(it.template.id);
                        msg.writer().writeShort(itk.id);
                        msg.writer().writeInt(itk.goldSell);
                        msg.writer().writeInt(itk.gemSell);
                        if (itk.isBuy) {
                            msg.writer().writeByte(2);// buy type 
                        } else if (getItemBuy(pl, itk) != null) {
                            msg.writer().writeByte(1); 
                        } else {
                            msg.writer().writeByte(0);
                        }
                        msg.writer().writeInt(itk.quantity);
                        msg.writer().writeByte(1); // isMe
                        msg.writer().writeByte(it.itemOptions.size());
                        for (int a = 0; a < it.itemOptions.size(); a++) {
                            msg.writer().writeByte(it.itemOptions.get(a).optionTemplate.id);
                            msg.writer().writeShort(it.itemOptions.get(a).param);
                        }
                        msg.writer().writeByte(0);
                        msg.writer().writeByte(0);
                    }
                } else {
                    List<ItemKyGui> items = getItemKyGui(pl, i);
                    List<ItemKyGui> itemsSend = getItemKyGui2(pl, i, (byte) 0, (byte) 10);
                    msg.writer().writeUTF(ShopKyGuiManager.gI().tabName[i]);
                    byte tab = (byte) (items.size() / 10 > 0 ? (items.size() / 10) : 1);
                    msg.writer().writeByte(tab); // max page
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
                        msg.writer().writeShort(it.template.id);
                        msg.writer().writeShort(itk.id);
                        msg.writer().writeInt(itk.goldSell);
                        msg.writer().writeInt(itk.gemSell);
                        msg.writer().writeByte(0); // buy type
                        msg.writer().writeInt(itk.quantity);
                        msg.writer().writeByte(itk.player_sell == pl.id ? 1 : 0); // isMe
                        msg.writer().writeByte(it.itemOptions.size());
                        for (int a = 0; a < it.itemOptions.size(); a++) {
                            msg.writer().writeByte(it.itemOptions.get(a).optionTemplate.id);
                            msg.writer().writeShort(it.itemOptions.get(a).param);
                        }
                        msg.writer().writeByte(0);
                        msg.writer().writeByte(0);
                    }
                }
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
            System.out.println("Lỗi open shop");
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
}
