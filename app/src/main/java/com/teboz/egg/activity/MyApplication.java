package com.teboz.egg.activity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teboz.egg.R;
import com.teboz.egg.bean.CardInfo;
import com.teboz.egg.utils.Cmd;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import generalplus.com.GPLib.ComAir5Wrapper;

/**
 * Created by Administrator on 2016/4/6.
 */
public class MyApplication extends Application {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String SAVE_FILE_NAME = "EGG";

    public boolean isAddRoleSuccess = false;//记录是否新添加了角色，一遍切换到选择角色页面刷新列表
    public boolean iscompletedTouch = false;//是否录制了摸头声音
    public boolean iscompletedShake = false;//是否录制了摇晃声音
    public boolean iscompletedUpside = false;//是否录制了倒置的声音

    public boolean isRocorderPowersound = false;//记录是否进行了录制开机声的操作，以方便实时接收录制开机声回码

    public boolean isSelectReturn = false;//记录是否从选择角色页面返回，以方便回到主页面决定是否需要发送角色码值
    public boolean isDirectorIn = false;//记录是否从主页面进入，以方便添加角色页面决定是否需要发送添加角色码值

    public List<CardInfo> cardInfos;//角色列表
    private AudioManager audioManager;
    private int currentVolum;

    @Override
    public void onCreate() {
        super.onCreate();

        mSharedPreferences = getSharedPreferences(SAVE_FILE_NAME, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        //将系统多媒体音量调到最大，保证超声波最大有效距离
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolum = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolum = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (currentVolum < maxVolum) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    maxVolum,
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }

        initCardData();//初始化角色列表数据
    }

    /**
     * 将音量恢复到将音量调节到最大之前的音量
     */
    public void resetVolum(){
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                currentVolum,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }



    //=====================超声波发送命令操作相关==============================
    private ComAir5Wrapper encode_ComAir5;//超声波操作对象
    //    private ComAir5Wrapper decode_ComAir5;//超声波操作对象
    private int currentCommend = -1;//记录最后发送的码值
    public int index = -1;//收到的码值的索引
    public int[] receiveCmd = new int[2];//收到的码值


    public void setCurrentCommend(int cmd) {
        currentCommend = cmd;
    }

    public int getCurrentCommend() {
        return currentCommend;
    }

    public void setEncodeComAir5(ComAir5Wrapper m_ComAir5) {
        this.encode_ComAir5 = m_ComAir5;
    }


    /**
     * 发送双码值
     *
     * @param cmd
     */
    public void sendCommand(int[] cmd) {
        if (encode_ComAir5 != null) {
            Message msg = Message.obtain();
            msg.arg1 = cmd[0];
            msg.arg2 = cmd[1];
            sendCmdHandler.sendMessage(msg);
        }
    }

    private Handler sendCmdHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            synchronized (encode_ComAir5) {
                if (encode_ComAir5 != null) {
                    lastcmd = -1;
                    index = -1;

                   // SystemClock.sleep(1000);
                    encode_ComAir5.PlayComAirCmd(msg.arg1, 1.0f);
                    setCurrentCommend(msg.arg1);
                    SystemClock.sleep(1000);
                    encode_ComAir5.PlayComAirCmd(msg.arg2, 1.0f);
                    setCurrentCommend(msg.arg2);

                    Log.e("cmd", "send cmd:[" + msg.arg1 + ", " + msg.arg2 + "]");
                }
            }

        }
    };

    public int lastcmd = -1;

    /**
     * 初始化角色列表数据
     */
    private void initCardData() {
        if (!getSpBooleanValue("haveCopyfile")) {//app第一次启动时将assets默认角色列表文件存到手机本地
            writeToStorage("cards.json");
            saveBooleanValue("haveCopyfile", true);
        }
        jsonParse();//将文件json数据解析为列表
    }

    /**
     * 重置角色列表数据
     */
    public void resetCardData() {
        writeToStorage("cards.json");
        saveBooleanValue("haveCopyfile", true);
        jsonParse();
    }

    /**
     * 将assets里面的文件copy到内部存储
     *
     * @param filename
     */
    public void writeToStorage(String filename) {
        Log.e("dj", "writeToStorage");

        InputStream is;
        FileOutputStream outputStream;
        try {
            is = getResources().getAssets().open(filename);//读取的文件
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);//写入的文件

            byte[] buf = new byte[is.available()];
            is.read(buf);
            outputStream.write(buf);
            String str = new String(buf, "UTF-8");
            //System.out.print("writetostorage: " + str);

            is.close();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 将assets里面的文件copy到内部存储
     *
     * @param filename
     */
    public void writeToStorage(String filename, byte[] bytes) {
        Log.e("dj", "writeToStorage");

        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);//写入的文件
            outputStream.write(bytes);
            String str = new String(bytes, "UTF-8");
            // System.out.print("writetostorage: " + str);
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 将角色列表转为json字符串
     *
     * @param list
     * @return
     */
    public String listToJsonArray(List<CardInfo> list) {
//        Log.e("dj", "listToJsonArray");

        String str = JSON.toJSONString(list, true);
        str = "{\"cards\":" + str + ",}";
//        System.out.print("{\"articles\":"+str+"],}");
        System.out.println(str);

        return str;
    }


    private List<Integer> defaultrolebigImages;//默认角色的大卡片图片资源
    private List<Integer> defaultrolesmallImages;//默认角色的小卡片图片资源
    public List<Integer> cardbigImageIds;//非默认角色的大卡片图片资源
    public List<Integer> cardsmallImageIds;//非默认角色的小卡片图片资源

    /**
     * 十个默认角色的状态的shareprefrence的key值
     */
    public String[] roleShareprefrecekeys = {"OneState","TwoState","ThreeState","FourState",
            "FiveState","SixState","SevenState","EightState","NineState","TenState"};

    /**
     * 将内部存储中的cards.json文件解析为角色列表
     */
    public void jsonParse() {
        //Log.e("dj", "parse json");
        try {
            //将json文件读取到buffer数组中
            InputStream is = openFileInput("cards.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            //将字节数组转换为以GB2312编码的字符串
            String json = new String(buffer, "UTF-8");
            //System.out.print(json);
            //将字符串json转换为json对象，以便于取出数据
            JSONObject jsonObject = JSON.parseObject(json);

            //解析info数组，解析中括号括起来的内容就表示一个数组，使用JSONArray对象解析
            JSONArray array = jsonObject.getJSONArray("cards");
            //Log.e("application", array.toString());
            //Log.e("dj", "array's length: ");

            //StringBuffer操作字符串的一个高效类，保存解析的结果，以便于在TextView中显示
            StringBuffer strBuf = new StringBuffer();

            if (cardInfos == null) {
                cardInfos = new ArrayList<>();
            } else {
                cardInfos.clear();
            }

            //遍历JSONArray数组
            for (int i = 0; i < array.size(); i++) {
                //取出数组的第一项
                JSONObject item = array.getJSONObject(i);
                CardInfo info = new CardInfo();
                //获得名称为title项的值
                int id = item.getInteger("id");
                String name = item.getString("name");
                int cmd = item.getInteger("cmd");
                int index = item.getInteger("imageindex");


                info.setImageindex(index);
                info.setCmd(cmd);
                info.setId(id);
                info.setName(name);
                cardInfos.add(info);
            }

            // listToJsonArray(essayInfos);//test

        } catch (Exception e) {
            Log.e("dj", e.toString());
        }

        cardbigImageIds = new ArrayList<>();
        cardbigImageIds.add(R.mipmap.card_blue_big);
        cardbigImageIds.add(R.mipmap.card_green_big);
        cardbigImageIds.add(R.mipmap.card_yellow_big);
        cardbigImageIds.add(R.mipmap.card_red_big);
        cardbigImageIds.add(R.mipmap.card_purple_big);
        cardbigImageIds.add(R.mipmap.card_orange_big);

        cardsmallImageIds = new ArrayList<>();
        cardsmallImageIds.add(R.mipmap.card_blue_small);
        cardsmallImageIds.add(R.mipmap.card_green_small);
        cardsmallImageIds.add(R.mipmap.card_yellow_small);
        cardsmallImageIds.add(R.mipmap.card_red_small);
        cardsmallImageIds.add(R.mipmap.card_purple_small);
        cardsmallImageIds.add(R.mipmap.card_orange_small);

        defaultrolebigImages = new ArrayList<>();
        defaultrolebigImages.add(R.mipmap.card_3_big);
        defaultrolebigImages.add(R.mipmap.card_5_big);
        defaultrolebigImages.add(R.mipmap.card_6_big);
        defaultrolebigImages.add(R.mipmap.card_4_big);
        defaultrolebigImages.add(R.mipmap.card_2_big);
        defaultrolebigImages.add(R.mipmap.card_7_big);
        defaultrolebigImages.add(R.mipmap.card_8_big);
        defaultrolebigImages.add(R.mipmap.card_1_big);

        defaultrolesmallImages = new ArrayList<>();
        defaultrolesmallImages.add(R.mipmap.card_3_small);
        defaultrolesmallImages.add(R.mipmap.card_5_small);
        defaultrolesmallImages.add(R.mipmap.card_6_small);
        defaultrolesmallImages.add(R.mipmap.card_4_small);
        defaultrolesmallImages.add(R.mipmap.card_2_small);
        defaultrolesmallImages.add(R.mipmap.card_7_small);
        defaultrolesmallImages.add(R.mipmap.card_8_small);
        defaultrolesmallImages.add(R.mipmap.card_1_small);

        if (cardInfos != null && cardInfos.size() > 5) {
            for (int i = 0; i < Cmd.DEFAULT_ROLE_COUNT; i++) {
                cardInfos.get(i).setImageId(defaultrolebigImages.get(i));
                cardInfos.get(i).setImageId2(defaultrolesmallImages.get(i));
            }
            for (int j = Cmd.DEFAULT_ROLE_COUNT; j < cardInfos.size(); j++) {
                cardInfos.get(j).setImageId(cardbigImageIds.get(cardInfos.get(j).getImageindex()));
                cardInfos.get(j).setImageId2(cardsmallImageIds.get(cardInfos.get(j).getImageindex()));
            }
        }

        //将存储的值与实际角色列表同步一下
        for (int i = 0; i<roleShareprefrecekeys.length;i++){
            saveBooleanValue(roleShareprefrecekeys[i], false);
        }

        if (cardInfos.size() > Cmd.DEFAULT_ROLE_COUNT) {
            for (int k = Cmd.DEFAULT_ROLE_COUNT; k < cardInfos.size(); k++){
                if(cardInfos.get(k).getCmd() == 7){
                    saveBooleanValue(roleShareprefrecekeys[0], true);
                }else if(cardInfos.get(k).getCmd() == 8){
                    saveBooleanValue(roleShareprefrecekeys[1], true);
                }else if(cardInfos.get(k).getCmd() == 9){
                    saveBooleanValue(roleShareprefrecekeys[2], true);
                }else if(cardInfos.get(k).getCmd() == 10){
                    saveBooleanValue(roleShareprefrecekeys[3], true);
                }else if(cardInfos.get(k).getCmd() == 11){
                    saveBooleanValue(roleShareprefrecekeys[4], true);
                }else if(cardInfos.get(k).getCmd() == 12){
                    saveBooleanValue(roleShareprefrecekeys[5], true);
                }else if(cardInfos.get(k).getCmd() == 13){
                    saveBooleanValue(roleShareprefrecekeys[6], true);
                }else if(cardInfos.get(k).getCmd() == 14){
                    saveBooleanValue(roleShareprefrecekeys[7], true);
                }else if(cardInfos.get(k).getCmd() == 15){
                    saveBooleanValue(roleShareprefrecekeys[8], true);
                }else if(cardInfos.get(k).getCmd() == 16){
                    saveBooleanValue(roleShareprefrecekeys[9], true);
                }
            }
        }
    }


    public List<CardInfo> getCardInfos() {
        return cardInfos;
    }


    public void saveBooleanValue(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public boolean getSpBooleanValue(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public void saveSpIntValue(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public int getSpIntValue(String key) {

        return mSharedPreferences.getInt(key, -1);
    }

    public void saveSharePreferenceStrValue(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public String getSPStrValue(String key) {

        return mSharedPreferences.getString(key, "");
    }

}
