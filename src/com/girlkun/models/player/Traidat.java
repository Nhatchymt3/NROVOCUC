package com.girlkun.models.player;

// đây
import java.util.ArrayList;
import java.util.List;

import com.girlkun.models.boss.BossesData;
import com.girlkun.models.boss.list_boss.nhanban.Traidatnhanban;
import com.girlkun.models.map.Map;
import com.girlkun.models.map.Zone;
import com.girlkun.server.Manager;
import com.girlkun.services.MapService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.utils.Util;

/**
 * @author BTH sieu cap vippr0
 */
public class Traidat extends Player {

  private long lastTimeChat;
  private Player playerTarget;

  private long lastTimeTargetPlayer;
  private long timeTargetPlayer = 5000;
  private long lastZoneSwitchTime;
  private long zoneSwitchInterval;
  private List<Zone> availableZones;

  public void initTraidat() {
    init();
  }

  @Override
  public short getHead() {
    return 569;
  }

  @Override
  public short getBody() {
    return 472;
  }

  @Override
  public short getLeg() {
    return 473;
  }

  @Override
  public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
    this.checkAnThan(plAtt);
    if (plAtt != null && plAtt.isPl() && (plAtt.playerTask.taskMain.id == 11 || plAtt.playerTask.taskMain.id == 27)) {
      TaskService.gI().sendNextTaskMain(plAtt);
      this.chat("Á đù người hướng nội !");
    }
    if (this.nPoint.hp <= 500000000) {
      this.nPoint.hp = this.nPoint.hpMax;
      Service.gI().point(this);
    }
    if (plAtt != null && plAtt.isPl() && plAtt.cFlag == 1) {
      if (!plAtt.haveNhanBan) {
        try {
          Traidatnhanban traidatnhanban = new Traidatnhanban(Util.randomBossId(),
              BossesData.TRAI_DAT, plAtt.zone, plAtt);
          plAtt.haveNhanBan = true;
        } catch (Exception e) {

        }
      }

    }
    if (!this.isDie()) {
      if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 100)) {
        this.chat("Xí hụt");
        return 0;
      }
      this.chat("|7| Sát thương vừa nhận: " + Util.numberToMoney((long)damage));
      return damage;
    } else {
      return 0;
    }
  }

  public void joinMap(Zone z, Player player) {
    MapService.gI().goToMap(player, z);
    z.load_Me_To_Another(player);
  }

  @Override
  public void update() {
    String[] textBaoCat = { "|7|ADMIN đẹp trai", "|7|Nhatchymte", "|7|Welcome to NRO Vô Cực!" };
    if (Util.canDoWithTime(lastTimeChat, 5000)) {
      try {
      Service.getInstance().chat(this, textBaoCat[Util.nextInt(textBaoCat.length)]);
        
      } catch (Exception e) {
        // TODO: handle exception
      }
      lastTimeChat = System.currentTimeMillis();
      this.nPoint.setFullHpMp();
      Service.gI().point(this);
    }
  }

  private void init() {

    int id = Util.randomBossId();
    for (Map m : Manager.MAPS) {
      if (m.mapId == 190) {
        for (Zone z : m.zones) {
          Traidat pl = new Traidat();
          pl.name = "Trái đất";
          pl.gender = 0;
          pl.id = id;
          pl.isBuNhin = true;
          pl.nPoint.hpMax = (int) 2000000000;
          pl.nPoint.hpg = (int) 2000000000;
          pl.nPoint.hp = (int) 2000000000;
          pl.nPoint.tlPST = 5;
          pl.nPoint.setFullHpMp();
          pl.location.x = 784;
          pl.location.y = 384;
          pl.nPoint.power = 99999999999L;
          pl.cFlag = 8;
          joinMap(z, pl);
          z.setReferee(pl);

        }
      }
    }
  }
}

// }
