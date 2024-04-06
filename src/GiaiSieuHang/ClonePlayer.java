package GiaiSieuHang;

import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.dhvt.BossDHVT;
import com.girlkun.models.player.Player;
import com.girlkun.utils.Util;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.dhvt.BossDHVT;
import com.girlkun.models.player.Player;


public class ClonePlayer extends BossDHVT {


    public ClonePlayer(Player player, BossData data, int id) throws Exception {
      super(Util.randomBossId(), data,100);
        this.playerAtt = player;
        this.idPlayer = id;
    }
}
