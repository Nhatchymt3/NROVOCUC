package com.girlkun.services.func;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.network.io.Message;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.ItemTimeService;
import com.girlkun.services.NpcService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//
///**
// *
// * @author delb1
// */

public class GoiRongBang {

    public static final byte WISHED = 0;
    public static final byte TIME_UP = 1;

    public static final short BANG_1_SAO = 925;
    public static final short BANG_2_SAO = 926;
    public static final short BANG_3_SAO = 927;
    public static final short BANG_4_SAO = 928;
    public static final short BANG_5_SAO = 929;
    public static final short BANG_6_SAO = 930;
    public static final short BANG_7_SAO = 931;

    public static final String RONG_ICE_TUTORIAL
            = "Chỉ được gọi Rồng BĂNG 1 sao\n"
            + "Các ngọc 2 sao đến 7 sao không thể gọi rồng thần được\n"
            + "Để gọi Rồng Xương 1 sao cần ngọc từ 1 sao đến 7 sao\n"
            + "Điều ước rồng 1 sao: 20k Hồng ngọc ; Buff 30% Chỉ số 30p; 200 Thỏi vàng ; Đổi skill 4 Đệ\n"
            + "Ngọc Bí Ngô sẽ mất ngay khi gọi rồng dù bạn có ước hay không\n"
            + "Quá 5 phút nếu không ước Rồng Xương sẽ bay mất";
    public static final String ICE_SAY
            = "Ta sẽ ban cho người 1 điều ước, ngươi có 5 phút, hãy suy nghĩ thật kỹ trước khi quyết định";

    public static final String[] ICE_1_STAR_WISHES_1
            = new String[]{"Giàu có\n20k Hồng ngọc", "Tăng 20% HP,KI,SD\ntrong 30 Phút", "200 Thỏi vàng",
                "Thay\nChiêu 4\nĐệ tử"};
    //--------------------------------------------------------------------------
    private static GoiRongBang instance;
    private final Map pl_dragonStar;
    public long lastTimeICEAppeared;
    private long lastTimeRongICEWait;
    public final int timeReRongICE = 10000;
    public boolean isRongICEAppear = false;
    private final int timeRongICEWait = 150000;

    private final Thread update;
    private boolean active;

    public boolean isPlayerDisconnect;
    public Player playerRongICE;
    private int playerRongICEId;
    public Zone mapRongICEAppear;
    private byte RongICEStar;
    private int menuRongICE;
    private byte select;

    private GoiRongBang() {
        this.pl_dragonStar = new HashMap<>();
        this.update = new Thread(() -> {
            while (active) {
                try {
                    if (isRongICEAppear == true) {
                        if (isPlayerDisconnect) {
                            List<Player> players = mapRongICEAppear.getPlayers();
                            for (Player plMap : players) {
                                if (plMap.id == playerRongICEId) {
                                    playerRongICE = plMap;
                                    reSummonRongICE();
                                    isPlayerDisconnect = false;
                                    break;
                                }
                            }
                        }
                        if (Util.canDoWithTime(lastTimeRongICEWait, timeReRongICE)) {
                            RongICELeave(playerRongICE, TIME_UP);
                        }
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Logger.logException(GoiRongBang.class, e);
                }
            }
        });
        this.active();
    }

    private void active() {
        if (!active) {
            active = true;
            this.update.start();
        }
    }

    public static GoiRongBang gI() {
        if (instance == null) {
            instance = new GoiRongBang();
        }
        return instance;
    }

    public void openMenuRongBang(Player pl, byte dragonBallStar) {
        this.pl_dragonStar.put(pl, dragonBallStar);
        NpcService.gI().createMenuConMeo(pl, ConstNpc.RONG_ICE, -1, "Bạn muốn gọi Rồng Băng ?",
                "Hướng\ndẫn thêm\n(mới)", "Gọi\nRồng Băng\n" + dragonBallStar + " Sao");
    }

    public synchronized void summonRongICE(Player pl) {
        if (pl.zone.map.mapId == 105) {
            if (checkRongICE(pl)) {
                if (isRongICEAppear == true) {
                    Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    return;
                }
                long currentTimeMillis = System.currentTimeMillis();
                long timeSinceLastRongICEgAppeared = currentTimeMillis - lastTimeICEAppeared;
                long timeLeftUntilResummon = timeReRongICE - timeSinceLastRongICEgAppeared;

                if (timeSinceLastRongICEgAppeared < timeReRongICE) {
                    int timeLeftInSeconds = (int) (timeLeftUntilResummon / 1000);
                    String timeLeftString = (timeLeftInSeconds < 7200) ? (timeLeftInSeconds + " giây") : ((timeLeftInSeconds / 60) + " phút");
                    Service.getInstance().sendThongBao(pl, "Vui lòng đợi " + timeLeftString + " để gọi rồng");
                    return;
                }

                // Summon RongBang
                isRongICEAppear = true;
                playerRongICE = pl;
                playerRongICEId = (int) pl.id;
                mapRongICEAppear = pl.zone;
                int begin = BANG_1_SAO;
                for (int i = begin; i <= BANG_7_SAO; i++) {
                    try {
                        InventoryServiceNew.gI().subQuantityItemsBag(pl, InventoryServiceNew.gI().findItemBag(pl, i), 1);
                    } catch (Exception ex) {

                    }
                }
                InventoryServiceNew.gI().sendItemBags(pl);
                sendNotifyRongICE();
                activeRongICE(pl, true);
                sendWhishesRongICE(pl);
            }
        } else {
            Service.getInstance().sendThongBao(pl, "Chỉ được gọi Rồng Xương ở Hành tinh Cold");
        }
    }
    public boolean checkRongICE(Player pl) {
        byte dragonStar = (byte) this.pl_dragonStar.get(pl);
        if (dragonStar == 1) {
            if (!InventoryServiceNew.gI().KtraItemBag(pl, BANG_1_SAO)) {
                Service.getInstance().sendThongBao(pl, "Bạn còn thiếu 1 Bí ngô 2 sao");
                return false;
            }
            if (!InventoryServiceNew.gI().KtraItemBag(pl, BANG_3_SAO)) {
                Service.getInstance().sendThongBao(pl, "Bạn còn thiếu 1 Bí ngô 3 sao");
                return false;
            }
        } else if (dragonStar == 2) {
            if (!InventoryServiceNew.gI().KtraItemBag(pl, BANG_4_SAO)) {
                Service.getInstance().sendThongBao(pl, "Bạn còn thiếu 1 Bí ngô 3 sao");
                return false;
            }
        }
        if (!InventoryServiceNew.gI().KtraItemBag(pl, BANG_4_SAO)) {
            Service.getInstance().sendThongBao(pl, "Bạn còn thiếu 1 Bí ngô 4 sao");
            return false;
        }
        if (!InventoryServiceNew.gI().KtraItemBag(pl, BANG_5_SAO)) {
            Service.getInstance().sendThongBao(pl, "Bạn còn thiếu 1 Bí ngô 5 sao");
            return false;
        }
        if (!InventoryServiceNew.gI().KtraItemBag(pl, BANG_6_SAO)) {
            Service.getInstance().sendThongBao(pl, "Bạn còn thiếu 1 Bí ngô 6 sao");
            return false;
        }
        if (!InventoryServiceNew.gI().KtraItemBag(pl, BANG_7_SAO)) {
            Service.getInstance().sendThongBao(pl, "Bạn còn thiếu 1 Bí ngô 7 sao");
            return false;
        }
        return true;
    }

    private void sendNotifyRongICE() {
        Message msg;
        try {
            msg = new Message(-25);
            msg.writer().writeUTF(playerRongICE.name + " vừa gọi Rồng Xương tại "
                    + playerRongICE.zone.map.mapName + " khu vực " + playerRongICE.zone.zoneId);
            Service.getInstance().sendMessAllPlayerIgnoreMe(playerRongICE, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private void sendWhishesRongICE(Player pl) {
        byte dragonStar;
        try {
            dragonStar = (byte) pl_dragonStar.get(pl);
            this.RongICEStar = dragonStar;
        } catch (Exception e) {
            dragonStar = this.RongICEStar;
        }
        switch (dragonStar) {
            case 1:
                NpcService.gI().createMenuRongICE(pl, ConstNpc.ICE_1_1, ICE_SAY, ICE_1_STAR_WISHES_1);
                break;
        }
    }

    public void activeRongICE(Player pl, boolean appear) {
        Message msg;
        try {
            msg = new Message(-83);
            msg.writer().writeByte(0);
            if (appear) {
                msg.writer().writeShort(pl.zone.map.mapId);
                msg.writer().writeShort(pl.zone.map.bgId);
                msg.writer().writeByte(pl.zone.zoneId);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeUTF("");
                msg.writer().writeShort(pl.location.x);
                msg.writer().writeShort(pl.location.y);
                msg.writer().writeByte(1);
                lastTimeRongICEWait = System.currentTimeMillis();
            }
            Service.getInstance().sendMessAllPlayer(msg);
        } catch (Exception e) {
        }
    }

    private void reSummonRongICE() {
        activeRongICE(playerRongICE, true);
        sendWhishesRongICE(playerRongICE);
    }

    public void confirmWish() {
        switch (this.menuRongICE) {
            case ConstNpc.ICE_1_1:
                switch (this.select) {
                    case 0:
                        
                        break;
                }
                break;
        }
        RongICELeave(this.playerRongICE, WISHED);
    }

    public void showConfirmRongICE(Player pl, int menu, byte select) {
        this.menuRongICE = menu;
        this.select = select;
        String wish = null;
        switch (menu) {
            case ConstNpc.ICE_1_1:
                wish = ICE_1_STAR_WISHES_1[select];
                break;
        }
        NpcService.gI().createMenuRongICE(pl, ConstNpc.ICE_CONFIRM, "Ngươi có chắc muốn ước?", wish, "Từ chối");
    }

    public void reOpenRongICEWishes(Player pl) {
        switch (menuRongICE) {
            case ConstNpc.ICE_1_1:
                NpcService.gI().createMenuRongICE(pl, ConstNpc.ICE_1_1, ICE_SAY, ICE_1_STAR_WISHES_1);
                break;
        }
    }

    public void RongICELeave(Player pl, byte type) {
        if (type == WISHED) {
            NpcService.gI().createTutorial(pl, -1, "Điều ước của ngươi đã trở thành sự thật\nHẹn gặp ngươi lần sau, ta đi ngủ đây, bái bai");
        } else {
            NpcService.gI().createMenuRongICE(pl, ConstNpc.IGNORE_MENU, "Ta buồn ngủ quá rồi\nHẹn gặp ngươi lần sau, ta đi đây, bái bai");
        }
        RongICEBienMat(pl, false);
        this.isRongICEAppear = false;
        this.menuRongICE = -1;
        this.select = -1;
        this.playerRongICE = null;
        this.playerRongICEId = -1;
        this.RongICEStar = -1;
        this.mapRongICEAppear = null;
        lastTimeRongICEWait = System.currentTimeMillis();
    }

    public void RongICEBienMat(Player pl, boolean appear) {
        Message msg;
        try {
            msg = new Message(-83);
            msg.writer().writeByte(1);
            if (appear) {
                msg.writer().writeShort(pl.zone.map.mapId);
                msg.writer().writeShort(pl.zone.map.bgId);
                msg.writer().writeByte(pl.zone.zoneId);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeUTF("");
                msg.writer().writeShort(pl.location.x);
                msg.writer().writeShort(pl.location.y);
                msg.writer().writeByte(0);
            }
            Service.getInstance().sendMessAllPlayer(msg);
        } catch (Exception e) {
        }
    }

    //--------------------------------------------------------------------------
}
