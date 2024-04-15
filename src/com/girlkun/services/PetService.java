package com.girlkun.services;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.player.NewPet;
import com.girlkun.models.player.Pet;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.Thu_TrieuHoi;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.Util;

public class PetService {

    private static PetService i;

    public static PetService gI() {
        if (i == null) {
            i = new PetService();
        }
        return i;
    }

    public void createNormalPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, false, false, false, false, false, false, false, (byte) gender
                );
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.getInstance().chatJustForMe(player, player.pet, "Xin hãy thu nhận làm đệ tử");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createMabuPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, true, false, false, false, false, false, false, false, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.getInstance().chatJustForMe(player, player.pet, "Oa oa oa...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createBerusPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, true, false, false, false, false, false, false, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.getInstance().chatJustForMe(player, player.pet, "Người muốn bay màu cả hành tinh à kaka...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createBrolyPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, true, false, false, false, false, false, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.getInstance().chatJustForMe(player, player.pet, "Tà đã ngủ 500 năm nay rồi kaka...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createWukongPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, true, false, false, false, false, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.getInstance().chatJustForMe(player, player.pet, "Haha tà là thần bất tử bất diệt..");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createGokuEVO(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, false, true, false, false, false, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.getInstance().chatJustForMe(player, player.pet, "tụi mày quỳ xuống...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void create1(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, false, false, true, false, false, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.getInstance().chatJustForMe(player, player.pet, "tụi mày quỳ xuống...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void create2(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, false, false, false, true, false, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.getInstance().chatJustForMe(player, player.pet, "tụi mày quỳ xuống...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void create3(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, false, false, false, false, true, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.getInstance().chatJustForMe(player, player.pet, "tụi mày quỳ xuống...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void create4(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, false, false, false, false, false, true, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.getInstance().chatJustForMe(player, player.pet, "tụi mày quỳ xuống...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void create5(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, false, false, false, false, false, false, true, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.getInstance().chatJustForMe(player, player.pet, "tụi mày quỳ xuống...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void changeNormalPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createNormalPet(player, gender, limitPower);
    }

    public void changeMabuPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createMabuPet(player, gender, limitPower);
    }

    public void changeBerusPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createBerusPet(player, gender, limitPower);
    }

    public void changeBrolyPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createBrolyPet(player, gender, limitPower);
    }

    public void changeWukongPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createWukongPet(player, gender, limitPower);
    }

    public void changeFunsionPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createGokuEVO(player, gender, limitPower);
    }

    public void changemabulv2(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        create1(player, gender, limitPower);
    }

    public void changhuydietlv2(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        create2(player, gender, limitPower);
    }

    public void changsuperlv2(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        create3(player, gender, limitPower);
    }

    public void changwukonglv2(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        create4(player, gender, limitPower);
    }

    public void changefusionlv2(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        create5(player, gender, limitPower);
    }

    //// đổi tên đệ tử
    public void changeNamePet(Player player, String name) {
        try {
            if (!InventoryServiceNew.gI().KtraItemBag(player, 400)) {
                Service.getInstance().sendThongBao(player, "Bạn cần thẻ đặt tên đệ tử, mua tại Santa");
                return;
            } else if (Util.haveSpecialCharacter(name)) {
                Service.getInstance().sendThongBao(player, "Tên không được chứa ký tự đặc biệt");
                return;
            } else if (name.length() > 10) {
                Service.getInstance().sendThongBao(player, "Tên quá dài");
                return;
            }
            ChangeMapService.gI().exitMap(player.pet);
            player.pet.name = "$" + name.toLowerCase().trim();
            InventoryServiceNew.gI().subQuantityItemsBag(player, InventoryServiceNew.gI().findItemBag(player, 400), 1);
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Service.getInstance().chatJustForMe(player, player.pet, "Cảm ơn sư phụ đã đặt cho con tên " + name);
                } catch (Exception e) {
                }
            }).start();
        } catch (Exception ex) {

        }
    }
///////data đệ tử

    private int[] getDataPetNormal() {
        int[] hpmp = {1700, 1800, 1900, 2000, 2100, 2200};
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 105) * 20; //hp
        petData[1] = Util.nextInt(40, 105) * 20; //mp
        petData[2] = Util.nextInt(20, 45); //dame
        petData[3] = Util.nextInt(9, 50); //def
        petData[4] = Util.nextInt(0, 2); //crit
        return petData;
    }

    private int[] getDataPet() {
        int[] hpmp = {1700, 1800, 1900, 2000, 2100, 2200};
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 105) * 20; //hp
        petData[1] = Util.nextInt(40, 105) * 20; //mp
        petData[2] = Util.nextInt(50, 120); //dame
        petData[3] = Util.nextInt(9, 50); //def
        petData[4] = Util.nextInt(0, 2); //crit
        return petData;
    }

    public void createNewPet(Player player,
            boolean isMabu,
            boolean isBerus,
            boolean isBroly,
            boolean isWukong,
            boolean isGokuEVO,
            boolean isMabulv2,
            boolean isBeruslv2,
            boolean isSuperlv2,
            boolean isWukonglv2,
            boolean isFusionlv2,
            byte... gender) {
        // Tạo một mảng data chứa thông tin thuộc tính của thú cưng dựa vào loại thú cưng và giới tính
        int[] data = isMabu
                ? isBroly
                        ? isWukong
                                ? isGokuEVO
                                        ? isMabulv2
                                                ? isBeruslv2
                                                        ? isSuperlv2
                                                                ? isWukonglv2
                                                                        ? isFusionlv2
                                                                                ? getDataPet() : getDataPet() : getDataPet() : getDataPet() : getDataPet() : getDataPet() : getDataPet() : getDataPet()
                        : getDataPet() : getDataPet();

        Pet pet = new Pet(player);
        pet.name = "$" + (isMabu ? "Mabư"
                : isBerus ? "Berus"
                        : isBroly ? "Broly"
                                : isWukong ? "Wukong"
                                        : isGokuEVO ? "Fushion Zamus"
                                                : isMabulv2 ? "[LV2] Kid Bư"
                                                        : isBeruslv2 ? "[LV2] Hủy Diệt"
                                                                : isSuperlv2 ? "[LV2]Super Broly "
                                                                        : isWukonglv2 ? "[LV2] Wukong"
                                                                                : isFusionlv2 ? "[LV2] Fusion Zamus"
                                                                                        : "Đệ tử");

        // Thiết lập giới tính của thú cưng (nếu được chỉ định, ngược lại sẽ được tạo ngẫu nhiên)
        pet.gender = (gender != null && gender.length != 0) ? gender[0] : (byte) Util.nextInt(0, 2);

        // Thiết lập các thuộc tính cơ bản cho thú cưng như ID, sức mạnh, loại thú cưng, v.v.
        pet.id = -player.id;
        pet.nPoint.power = isMabu || isBerus || isBroly || isWukong || isGokuEVO || isMabulv2 || isFusionlv2 || isBeruslv2 || isSuperlv2 || isWukonglv2 ? 1500000 : 2000;
        pet.typePet = (byte) (isMabu ? 1 : isBerus ? 2 : isBroly ? 3 : isWukong ? 4 : isGokuEVO ? 5 : isMabulv2 ? 6 : isBeruslv2 ? 7
                : isSuperlv2 ? 8 : isWukonglv2 ? 9 : isFusionlv2 ? 10 : 0);////
        pet.nPoint.stamina = 1000;
        pet.nPoint.maxStamina = 1000;
        pet.nPoint.hpg = data[0];
        pet.nPoint.mpg = data[1];
        pet.nPoint.dameg = data[2];
        pet.nPoint.defg = data[3];
        pet.nPoint.critg = data[4];

        // Tạo slot item cho thú cưng và thêm kỹ năng cho nó
        for (int i = 0; i < 8; i++) {
            pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
        }
        pet.playerSkill.skills.add(SkillUtil.createSkill(Util.nextInt(0, 2) * 2, 1));
        for (int i = 0; i < 3; i++) {
            pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
        }

        // Thiết lập các điểm sức mạnh, máu, năng lượng cho thú cưng
        pet.nPoint.setFullHpMp();

        // Gán thú cưng mới tạo cho người chơi
        player.pet = pet;
    }

    ///////////////////////////////////////////////////////////
    public static void Pet2(Player pl, int h, int b, int l, String name) {
        if (pl.newpet != null) {
            pl.newpet.dispose();
        }
        pl.newpet = new NewPet(pl, (short) h, (short) b, (short) l, name);
        pl.newpet.name = name;
        pl.newpet.gender = pl.gender;
        pl.newpet.nPoint.tiemNang = 1;
        pl.newpet.nPoint.power = 1;
        pl.newpet.nPoint.limitPower = 1;
        pl.newpet.nPoint.hpg = 500000;
        pl.newpet.nPoint.mpg = 500000;
        pl.newpet.nPoint.hp = 500000;
        pl.newpet.nPoint.mp = 500000;
        pl.newpet.nPoint.dameg = pl.nPoint.dameg;
        pl.newpet.nPoint.defg = 1;
        pl.newpet.nPoint.critg = 1;
        pl.newpet.nPoint.stamina = 1;
        pl.newpet.nPoint.setBasePoint();
        pl.newpet.nPoint.setFullHpMp();
    }

    public static void Thu_TrieuHoi(Player pl) {
        if (pl.TrieuHoipet != null) {
            pl.TrieuHoipet.dispose();
        }
        pl.TrieuHoipet = new Thu_TrieuHoi(pl);
        pl.TrieuHoipet.name = "$" + "[" + pl.NameThanthu(pl.TrieuHoiCapBac) + "] " + pl.TenThuTrieuHoi;
        pl.TrieuHoipet.gender = pl.gender;
        pl.TrieuHoipet.nPoint.tiemNang = 1;
        pl.TrieuHoipet.nPoint.power = 1;
        pl.TrieuHoipet.nPoint.limitPower = 1;
        pl.TrieuHoipet.nPoint.hpg = pl.TrieuHoiHP;
        pl.TrieuHoipet.nPoint.mpg = 500000000;
        pl.TrieuHoipet.nPoint.hp = pl.TrieuHoiHP;
        pl.TrieuHoipet.nPoint.mp = 500000000;
        pl.TrieuHoipet.nPoint.dameg = pl.TrieuHoiDame;
        pl.TrieuHoipet.nPoint.defg = 1;
        pl.TrieuHoipet.nPoint.critg = 1;
        pl.TrieuHoipet.nPoint.stamina = 10000;
        pl.TrieuHoipet.nPoint.maxStamina = 10000;
        pl.TrieuHoipet.playerSkill.skills.add(SkillUtil.createSkill(5, 7));
        pl.TrieuHoipet.nPoint.setBasePoint();
        pl.TrieuHoipet.nPoint.setFullHpMp();
    }

    //--------------------------------------------------------------------------
}
