package com.teboz.egg.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/12.
 */
public class CardInfo implements Serializable {
    private int id;
    private String name;
    private boolean isShowDelet;
    private Integer imageId;
    private Integer imageId2;
    private int cmd;
    private int imageindex;

    public CardInfo() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsShowDelet() {
        return isShowDelet;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setIsShowDelet(boolean isShowDelet) {
        this.isShowDelet = isShowDelet;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Integer getImageId2() {
        return imageId2;
    }

    public void setImageId2(Integer imageId2) {
        this.imageId2 = imageId2;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getImageindex() {
        return imageindex;
    }

    public void setImageindex(int imageindex) {
        this.imageindex = imageindex;
    }

    @Override
    public String toString() {
        return "EssayInfo [pic_link=" + id + ", name=" + name + ", isShowDelet=" + isShowDelet + ", imageId="
                + imageId + ", imageId=" + imageId2 +", cmd=" +cmd+", imageindex=" +imageindex+"]";
    }


}
