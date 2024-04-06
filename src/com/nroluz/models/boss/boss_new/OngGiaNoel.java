package com.nroluz.models.boss.boss_new;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.server.Client;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.PetService;
import com.girlkun.services.Service;
import com.girlkun.services.SkillService;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Util;
import java.util.Random;

public class OngGiaNoel extends Boss {

    private long lastTimeAnTrom;
    private long lastTimeJoinMap;
    private long lastDropTime = 0;
    private static final long timeChangeMap = 1000; // thời gian đổi map 1 lần

    public OngGiaNoel() throws Exception {
        super(BossID.OngGiaNoel, BossesData.OngGiaNoel);
    }

    @Override
    public void joinMap() {
        super.joinMap();
        lastTimeJoinMap = System.currentTimeMillis() + timeChangeMap;
    }

    @Override
    public void active() {

        int[] nemqua = new int[]{2007};
        int randomqua = new Random().nextInt(nemqua.length);
        super.active();
        currentTime = System.currentTimeMillis();

        if (currentTime - lastDropTime >= 15000) {
            int a = 0;
            for (int i = 0; i < 3; i++) {
                ItemMap it = new ItemMap(this.zone, nemqua[randomqua], 1, this.location.x + a,
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
                Service.getInstance().dropItemMap(this.zone, it);
                a += 10;
                this.chat("Lụm quà đi con ơi !!!!!!!");
            }
            lastDropTime = currentTime;
        }
        if (Util.canDoWithTime(lastTimeJoinMap, 20000)) {
            this.chat("Hô hô giờ ta đi đây tạm biết còn lần sau nhé");
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            damage = 1;
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
            SkillService.gI().useSkill(this, plAtt, null, null);
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void update() {
        super.update();
        if (Client.gI().getPlayers().isEmpty() || this.isDie()) {
            return;
        }
        try {
            Player ramdonPlayer = Client.gI().getPlayers().get(Util.nextInt(0, Client.gI().getPlayers().size()));
            if (ramdonPlayer != null && ramdonPlayer.zone.isKhongCoTrongTaiTrongKhu()) {
                if (ramdonPlayer.zone.map.mapId != 51 && ramdonPlayer.zone.map.mapId != 113 && ramdonPlayer.zone.map.mapId != 129 && this.zone.getPlayers().size() <= 0 && System.currentTimeMillis() > this.lastTimeJoinMap) {
                    if (ramdonPlayer.id != -1000000) {
                        lastTimeJoinMap = System.currentTimeMillis() + timeChangeMap;
                        ChangeMapService.gI().spaceShipArrive(this, (byte) 2, ChangeMapService.DEFAULT_SPACE_SHIP);
//                        System.err.println("Ông Già Noel Đã Tìm Thấy: " + this.zone.getPlayers().size() + " Player");
                        ChangeMapService.gI().exitMap(this);
                        this.zoneFinal = null;
                        this.lastZone = null;
                        this.zone = ramdonPlayer.zone;
                        this.location.x = Util.nextInt(100, zone.map.mapWidth - 100);
                        this.location.y = zone.map.yPhysicInTop(this.location.x, 100);
                        this.joinMap();
                    }
                }
            }
        } catch (Exception e) {

        }
    }
}
