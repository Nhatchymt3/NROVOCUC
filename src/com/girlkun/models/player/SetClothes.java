package com.girlkun.models.player;

import com.girlkun.models.item.Item;
import java.util.List;

public class SetClothes {

    private Player player;

    public SetClothes(Player player) {
        this.player = player;
    }

    public byte songoku;
    public byte thienXinHang;
    public byte kirin;

    public byte ocTieu;
    public byte pikkoroDaimao;
    public byte picolo;

    public byte kakarot;
    public byte cadic;
    public byte nappa;

    public byte setthanlinh;
    //setnew
    public byte thuongde;
    public byte broly;
    public byte cell;

    public byte nhatchymte;
    public byte setDHD;
    public byte setDTS;
    public byte setDTL;
    public byte tinhan;
    public byte nguyetan;
    public byte nhatan;

    public byte setzeni;
//    c2
    public byte songokuc2;
    public byte thienXinHangc2;
    public byte kirinc2;

    public byte ocTieuc2;
    public byte pikkoroDaimaoc2;
    public byte picoloc2;

    public byte kakarotc2;
    public byte cadicc2;
    public byte nappac2;

    public byte supskhtd;
    public byte supskhnm;
    public byte supskhxd;

    public byte cuonghoaskh;

    public boolean godClothes;
    public int ctHaiTac = -1;
    public int ctbattu = -1;

    public int level1;
    public int level2;
    public int level3;
    public int level4;
    public int level5;
    public int level6;
    public int level7;

    public void setup() {
        setDefault();
        skhlevel();
        skhthuongde();
        setzeni();
        setupAN();
        setupDTS();
        setupDHD();
        setupDTL();
        upskh1();
        upskh2();
        upskh3();
        upskh4();
        upskh5();
        upskh6();
        upskh7();
        upskh8();
        upskh9();
        this.godClothes = true;
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id < 555 || item.template.id > 567) {
                    this.godClothes = false;
                    break;
                }
            } else {
                this.godClothes = false;
                break;
            }
        }
        Item ct = this.player.inventory.itemsBody.get(5);
        if (ct.isNotNullItem()) {
            switch (ct.template.id) {
                case 618:
                case 619:
                case 620:
                case 621:
                case 622:
                case 623:
                case 624:
                case 626:
                case 627:
                    this.ctHaiTac = ct.template.id;
                    break;

            }
        }
    }

    private void skhthuongde() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean skhtd = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 211:
                        case 214:
                            skhtd = true;
                            thuongde++;
                            break;

                    }
                    if (skhtd) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void upskh9() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean skh9 = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 134:
                        case 137:
                            skh9 = true;
                            cadic++;
                            break;

                    }
                    if (skh9) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void upskh8() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean skh8 = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 133:
                        case 136:
                            skh8 = true;
                            kakarot++;
                            break;

                    }
                    if (skh8) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void upskh7() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean skh7 = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 135:
                        case 138:
                            skh7 = true;
                            nappa++;
                            break;

                    }
                    if (skh7) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void upskh6() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean skh6 = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 130:
                        case 142:
                            skh6 = true;
                            picolo++;
                            break;

                    }
                    if (skh6) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void upskh5() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean skh5 = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 132:
                        case 144:
                            skh5 = true;
                            pikkoroDaimao++;
                            break;
                    }
                    if (skh5) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void upskh4() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean skh3 = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 131:
                        case 143:
                            skh3 = true;
                            ocTieu++;
                            break;
                    }
                    if (skh3) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void upskh3() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean skh3 = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 128:
                        case 140:
                            skh3 = true;
                            kirin++;
                            break;
                    }
                    if (skh3) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void upskh2() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean skh2 = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 127:
                        case 139:
                            skh2 = true;
                            thienXinHang++;
                            break;
                    }
                    if (skh2) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void upskh1() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean skh1 = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 129:
                        case 141:
                            skh1 = true;
                            songoku++;
                            break;
                    }
                    if (skh1) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void skhlevel() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean level = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 217://kaioken
                            if (io.param == 1) {
                                level = true;
                                thienXinHang++;
                                level1++;
                            }
                            if (io.param == 2) {
                                level = true;
                                thienXinHang++;
                                level2++;
                            }
                            if (io.param == 3) {
                                level = true;
                                thienXinHang++;
                                level3++;
                            }
                            if (io.param == 4) {
                                level = true;
                                thienXinHang++;
                                level4++;
                            }
                            if (io.param == 5) {
                                level = true;
                                thienXinHang++;
                                level5++;
                            }
                            if (io.param == 6) {
                                level = true;
                                thienXinHang++;
                                level6++;
                            }
                            if (io.param == 7) {
                                level = true;
                                thienXinHang++;
                                level7++;
                            }
                            break;
                        case 218://kaioken
                            if (io.param == 1) {
                                level = true;
                                kirin++;
                                level1++;
                            }
                            if (io.param == 2) {
                                level = true;
                                kirin++;
                                level2++;
                            }
                            if (io.param == 3) {
                                level = true;
                                kirin++;
                                level3++;
                            }
                            if (io.param == 4) {
                                level = true;
                                kirin++;
                                level4++;
                            }
                            if (io.param == 5) {
                                level = true;
                                kirin++;
                                level5++;
                            }
                            if (io.param == 6) {
                                level = true;
                                kirin++;
                                level6++;
                            }
                            if (io.param == 7) {
                                level = true;
                                kirin++;
                                level7++;
                            }
                            break;
                        case 219://kaioken
                            if (io.param == 1) {
                                level = true;
                                songoku++;
                                level1++;
                            }
                            if (io.param == 2) {
                                level = true;
                                songoku++;
                                level2++;
                            }
                            if (io.param == 3) {
                                level = true;
                                songoku++;
                                level3++;
                            }
                            if (io.param == 4) {
                                level = true;
                                songoku++;
                                level4++;
                            }
                            if (io.param == 5) {
                                level = true;
                                songoku++;
                                level5++;
                            }
                            if (io.param == 6) {
                                level = true;
                                songoku++;
                                level6++;
                            }
                            if (io.param == 7) {
                                level = true;
                                songoku++;
                                level7++;
                            }
                            break;
                        case 220://kaioken
                            if (io.param == 1) {
                                level = true;
                                picolo++;
                                level1++;
                            }
                            if (io.param == 2) {
                                level = true;
                                picolo++;
                                level2++;
                            }
                            if (io.param == 3) {
                                level = true;
                                picolo++;
                                level3++;
                            }
                            if (io.param == 4) {
                                level = true;
                                picolo++;
                                level4++;
                            }
                            if (io.param == 5) {
                                level = true;
                                picolo++;
                                level5++;
                            }
                            if (io.param == 6) {
                                level = true;
                                picolo++;
                                level6++;
                            }
                            if (io.param == 7) {
                                level = true;
                                picolo++;
                                level7++;
                            }
                            break;
                        case 221://kaioken
                            if (io.param == 1) {
                                level = true;
                                ocTieu++;
                                level1++;
                            }
                            if (io.param == 2) {
                                level = true;
                                ocTieu++;
                                level2++;
                            }
                            if (io.param == 3) {
                                level = true;
                                ocTieu++;
                                level3++;
                            }
                            if (io.param == 4) {
                                level = true;
                                ocTieu++;
                                level4++;
                            }
                            if (io.param == 5) {
                                level = true;
                                ocTieu++;
                                level5++;
                            }
                            if (io.param == 6) {
                                level = true;
                                ocTieu++;
                                level6++;
                            }
                            if (io.param == 7) {
                                level = true;
                                ocTieu++;
                                level7++;
                            }
                            break;
                        case 222://kaioken
                            if (io.param == 1) {
                                level = true;
                                pikkoroDaimao++;
                                level1++;
                            }
                            if (io.param == 2) {
                                level = true;
                                pikkoroDaimao++;
                                level2++;
                            }
                            if (io.param == 3) {
                                level = true;
                                pikkoroDaimao++;
                                level3++;
                            }
                            if (io.param == 4) {
                                level = true;
                                pikkoroDaimao++;
                                level4++;
                            }
                            if (io.param == 5) {
                                level = true;
                                pikkoroDaimao++;
                                level5++;
                            }
                            if (io.param == 6) {
                                level = true;
                                pikkoroDaimao++;
                                level6++;
                            }
                            if (io.param == 7) {
                                level = true;
                                pikkoroDaimao++;
                                level7++;
                            }
                            break;
                        case 223://kaioken
                            if (io.param == 1) {
                                level = true;
                                kakarot++;
                                level1++;
                            }
                            if (io.param == 2) {
                                level = true;
                                kakarot++;
                                level2++;
                            }
                            if (io.param == 3) {
                                level = true;
                                kakarot++;
                                level3++;
                            }
                            if (io.param == 4) {
                                level = true;
                                kakarot++;
                                level4++;
                            }
                            if (io.param == 5) {
                                level = true;
                                kakarot++;
                                level5++;
                            }
                            if (io.param == 6) {
                                level = true;
                                kakarot++;
                                level6++;
                            }
                            if (io.param == 7) {
                                level = true;
                                kakarot++;
                                level7++;
                            }
                            break;
                        case 224://kaioken
                            if (io.param == 1) {
                                level = true;
                                cadic++;
                                level1++;
                            }
                            if (io.param == 2) {
                                level = true;
                                cadic++;
                                level2++;
                            }
                            if (io.param == 3) {
                                level = true;
                                cadic++;
                                level3++;
                            }
                            if (io.param == 4) {
                                level = true;
                                cadic++;
                                level4++;
                            }
                            if (io.param == 5) {
                                level = true;
                                cadic++;
                                level5++;
                            }
                            if (io.param == 6) {
                                level = true;
                                cadic++;
                                level6++;
                            }
                            if (io.param == 7) {
                                level = true;
                                cadic++;
                                level7++;
                            }
                            break;
                        case 225://kaioken
                            if (io.param == 1) {
                                level = true;
                                nappa++;
                                level1++;
                            }
                            if (io.param == 2) {
                                level = true;
                                nappa++;
                                level2++;
                            }
                            if (io.param == 3) {
                                level = true;
                                nappa++;
                                level3++;
                            }
                            if (io.param == 4) {
                                level = true;
                                nappa++;
                                level4++;
                            }
                            if (io.param == 5) {
                                level = true;
                                nappa++;
                                level5++;
                            }
                            if (io.param == 6) {
                                level = true;
                                nappa++;
                                level6++;
                            }
                            if (io.param == 7) {
                                level = true;
                                nappa++;
                                level7++;
                            }
                            break;
                    }
                    if (level) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void cuonghoaskh() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean cuọnghoaskh = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 241:
                            cuọnghoaskh = true;
                            cuonghoaskh++;
                            break;
                    }
                    if (cuọnghoaskh) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    //ấn trang bị
    private void setupAN() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSett = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 34:
                            isActSett = true;
                            tinhan++;
                            break;
                        case 35:
                            isActSett = true;
                            nguyetan++;
                            break;
                        case 36:
                            isActSett = true;
                            nhatan++;
                            break;
                    }
                    if (isActSett) {
                        break;
                    }

                }
            } else {
                break;
            }
        }
    }

    public void setzeni() {
        for (int i = 5; i < 11; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 192:
                        case 193:
                            isActSet = true;
                            setzeni++;
                            break;
                    }
                    if (isActSet) {
                        break;
                    }

                }
            } else {
                break;
            }
        }
    }

    private void setupDTS() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 21:
                            if (io.param == 120) {
                                setDTS++;
                            }
                            break;
                    }
                    if (isActSet) {
                        break;
                    }

                }
            } else {
                break;
            }
        }
    }

    private void setupDHD() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 21:
                            if (io.param == 80) {
                                setDHD++;
                            }
                            break;
                    }
                    if (isActSet) {
                        break;
                    }

                }
            } else {
                break;
            }
        }
    }

    private void setupDTL() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 21:
                            if (io.param == 15) {
                                setDTL++;
                            }
                            break;
                    }
                    if (isActSet) {
                        break;
                    }

                }
            } else {
                break;
            }
        }
    }

    private void setDefault() {
        this.broly = 0;
        this.thuongde = 0;
        this.cell = 0;
        this.songoku = 0;
        this.thienXinHang = 0;
        this.kirin = 0;
        this.level1 = 0;
        this.level2 = 0;
        this.level3 = 0;
        this.level4 = 0;
        this.level5 = 0;
        this.level6 = 0;
        this.level7 = 0;
        this.ocTieu = 0;
        this.pikkoroDaimao = 0;
        this.picolo = 0;
        this.kakarot = 0;
        this.cadic = 0;
        this.nappa = 0;
        this.setzeni = 0;
        this.songokuc2 = 0;
        this.thienXinHangc2 = 0;
        this.kirinc2 = 0;
        this.ocTieuc2 = 0;
        this.pikkoroDaimaoc2 = 0;
        this.picoloc2 = 0;
        this.kakarotc2 = 0;
        this.cadicc2 = 0;
        this.nappac2 = 0;
        this.cuonghoaskh = 0;
        this.supskhtd = 0;
        this.supskhnm = 0;
        this.supskhxd = 0;

        setthanlinh = 0;
        this.setDHD = 0;
        this.setDTS = 0;
        this.setDTL = 0;
        this.nhatchymte = 0;
        this.tinhan = 0;
        this.nhatan = 0;
        this.nguyetan = 0;
        this.godClothes = false;
        this.ctHaiTac = -1;
        this.ctbattu = -1;
    }

    public void dispose() {
        this.player = null;
    }
}
