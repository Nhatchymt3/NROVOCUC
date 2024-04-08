package BoMong;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.Template;
import com.girlkun.models.player.Player;
import com.girlkun.network.io.Message;
import com.girlkun.server.Manager;
import com.girlkun.services.Service;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoMong {

    public static final String TEN_SIEU_CAP = "%1";
    public static final String TEN_NGUOI_BAN_HANG = "%2";
    public static final String TEN_SKILL = "%3";

    public Player player;

    public int numPvpWin;
    public int numSkillChuong;
    public int numFly;
    public int numKillMobFly;
    public int numKillNguoiRom;
    public long numHourOnline;
    public int numGivePea;
    public int numSellItem;
    public int numPayMoney;
    public int numKillSieuQuai;
    public int numHoiSinh;
    public int numSkillDacBiet;
    public int numPickGem;

    public List<Boolean> listReceiveGem;

    public BoMong(Player pl) {
        this.player = pl;
        listReceiveGem = new ArrayList<>(Manager.ACHIEVEMENTS.size());
    }

    public void Show() {
        Message msg = null;
        try {
            msg = new Message(-76);
            msg.writer().writeByte(0); // action
            msg.writer().writeByte(listReceiveGem.size()); //numArrchivement
            for (Template.AchievementTemplate temp : Manager.ACHIEVEMENTS) {

                msg.writer().writeUTF(temp.getInfo1()); //info1
                msg.writer().writeUTF( //info2
                        this.SwitchName(player, temp.getInfo2())
                        + " (" + Util.numberToMoney(getCount(temp.getIndex()))
                        + "/"
                        + Util.numberToMoney((long) temp.getCount_Purpose()) + ")");
                msg.writer().writeShort(temp.getTV());
                msg.writer().writeBoolean(getCount(temp.getIndex()) >= temp.getCount_Purpose()); //isfinish
                msg.writer().writeBoolean(this.listReceiveGem.get(temp.getIndex())); //isreceiv
            }
            this.player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
            Logger.logException(BoMong.class, e);
            e.getStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void receiveGem(int index) {
        Template.AchievementTemplate temp = Manager.ACHIEVEMENTS.stream().filter(ac -> ac.getIndex() == index).findFirst().orElse(null);

        if (temp != null) {
            Message msg = null;
            try {
                msg = new Message(-76);
                msg.writer().writeByte(1); // action
                msg.writer().writeByte(index); // index
                this.player.sendMessage(msg);
                msg.cleanup();
            } catch (IOException e) {
                Logger.logException(BoMong.class, e);
                System.out.println("lỗi bò mộng");
            } finally {
                if (msg != null) {
                    msg.cleanup();
                    msg = null;
                }
            }
            this.listReceiveGem.set(index, Boolean.TRUE);
            this.player.inventory.ruby += temp.getTV();
            Service.getInstance().sendMoney(this.player);
            Service.getInstance().sendThongBao(this.player, "Nhận thành công " + temp.getTV() + " hồng ngọc");
        } else {
            Service.getInstance().sendThongBao(this.player, "Không có phần thưởng");
        }
    }

    private String SwitchName(Player player, String text) {
        byte gender = player.gender;
        text = text.replaceAll(TEN_SIEU_CAP, gender == ConstPlayer.TRAI_DAT
                ? "Thần Trái Đất" : (gender == ConstPlayer.NAMEC
                        ? "Thần Namếc" : "Thần Xayda"));
        text = text.replaceAll(TEN_NGUOI_BAN_HANG, gender == ConstPlayer.TRAI_DAT
                ? "Bunma" : (gender == ConstPlayer.NAMEC
                        ? "Dende" : "Appule"));
        text = text.replaceAll(TEN_SKILL, gender == ConstPlayer.TRAI_DAT
                ? "Super Kamejoko" : (gender == ConstPlayer.NAMEC
                        ? "Ma Phong Ba" : "Cadic Liên hoàn chưởng"));
        return text;
    }

    public void plusCount(int indexAchie) {
        switch (indexAchie) {
            case 0:
                this.numPayMoney++;
            case 1:
//                this.player.nPoint.power++;
                this.numPayMoney++;
                break;
            case 2:
//                this.player.playerTask.taskMain.id++;
                this.numPayMoney++;
                break;
            case 3:
//                this.numPvpWin++;
                this.numPayMoney++;
                break;
            case 4:
//                this.numSkillChuong++;
                this.numPayMoney++;
                break;
            case 5:
//                this.numFly++;
                this.numPayMoney++;
                break;
            case 6:
//                this.numKillMobFly++;
                this.numPayMoney++;
                break;
            case 7:
//                this.numKillNguoiRom++;
                this.numPayMoney++;
                break;
            case 8:
//                this.numHourOnline++;
                this.numPayMoney++;
                break;
            case 9:
//                this.numGivePea++;
                this.numPayMoney++;
                break;
            case 10:
//                this.numSellItem++;
                this.numPayMoney++;
                break;
            case 11:
                this.numPayMoney++;
                break;
            case 12:
//                this.numKillSieuQuai++;
                this.numPayMoney++;
                break;
            case 13:
//                this.numHoiSinh++;
                this.numPayMoney++;
                break;
            case 14:
//                this.numSkillDacBiet++;
                this.numPayMoney++;
                break;
            case 15:
//                this.numPickGem++;
                this.numPayMoney++;
                break;
            default:
                break;
        }
    }

    private long getCount(int indexAchie) {
        switch (indexAchie) {
            case 0:
                return this.numPayMoney;
            case 1:

                return this.numPayMoney;
//                return this.player.nPoint.power;
            case 2:
                return this.numPayMoney;
//              return this.player.playerTask.taskMain.id;
            case 3:

                return this.numPayMoney;
//                return this.numPvpWin;
            case 4:
                return this.numPayMoney;
// return this.numSkillChuong;
            case 5:
                return this.numPayMoney;
            //return this.numFly;
            case 6:

                return this.numPayMoney;
            //return this.numKillMobFly;
            case 7:

                return this.numPayMoney;
//                return this.numKillNguoiRom;
            case 8:
                return this.numPayMoney;
//              return (this.numHourOnline / 60 / 60);
            case 9:
                return this.numPayMoney;
//                return this.numGivePea;
            case 10:
                return this.numPayMoney;
//  return this.numSellItem;
            case 11:
                return this.numPayMoney;
            //return this.numPayMoney;
            case 12:
                return this.numPayMoney;
//                return this.numKillSieuQuai;
            case 13:
                return this.numPayMoney;
//                return this.numHoiSinh;
            case 14:
                return this.numPayMoney;
//                return this.numSkillDacBiet;
            case 15:
//                return this.numPickGem;
                return this.numPayMoney;
            default:
                return 0;
        }
    }

    public void dispose() {
        this.player = null;
    }
}
