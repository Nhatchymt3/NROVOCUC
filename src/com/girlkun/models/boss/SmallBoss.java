
package com.girlkun.models.boss;

import com.girlkun.models.boss.list_boss.android.*;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;

/**
 *
 * @author delb1
 */
public class SmallBoss extends Boss {

    protected Boss bigBoss;

    public SmallBoss(int idgroup, BossData... data) throws Exception {
        super(idgroup, data);
    }

    public SmallBoss(int idgroup, Boss bigBoss, BossData... data) throws Exception {
        super(idgroup, data);
        this.bigBoss = bigBoss;
    }
    
   
}