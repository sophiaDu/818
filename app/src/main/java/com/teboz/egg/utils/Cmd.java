package com.teboz.egg.utils;

/**
 * Created by sophia on 2016/6/13.
 */
public class Cmd {
    public static int MAX_NEWROLE = 10;
    public static int DEFAULT_ROLE_COUNT = 8;

    public static int[] DIY_POWERSOUND = {0, 70};
    public static int[] DIY_POWERSOUND_LISTEN = {0, 71};
    public static int[] DEFAULT_POWERSOUND_LISTEN = {0, 72};
    public static int[] CHECKED_DIY_POWERSOUND = {0, 74};
    public static int[] CHECKED_DEFAULT_POWERSOUND = {0, 73};
    public static int[] ROLE_SYNC = {0, 75};

    public static int[] ROLE_SINGLE = {1, 0};
    public static int[] ROLE_PRESIDENT = {1, 1};
    public static int[] ROLE_OLDDRIVER = {1, 2};
    public static int[] ROLE_GIRLMAN = {1, 3};
    public static int[] ROLE_DEAR = {1, 4};
    public static int[] ROLE_BIRTH = {1, 5};

    public static int[] ADD_ROLE = {1, 7};

    public static int[] RECORDER_TOUCHSOUND = {2, 0};
    public static int[] RECORDER_SHAKESOUND = {2, 1};
    public static int[] RECORDER_UPSIDESOUND = {2, 2};


    public static int[] DELETE_ROLE = {0,76};


    public static int[] HIGHTOGETHER = {3, 6};






/*
    基础功能

    自定义开机语音	0,70
    自定义开机语音试听	0,71
    默认开机语音试听	0,72
    选中默认开机语音	0,73
    选中自定义开机语音	0,74
    角色同步	0,75
    角色扮演

    单身狗	1,0
    霸道总裁	1,1
    老司机	1,2
    女汉子	1,3
    亲爱的	1,4
    寿星	1,5

    新建角色1	1,6
    新建角色2	1,7
    新建角色3	1,8
            ...	...

    录摸头声音	2,0
    录摇晃声音	2,1
    录倒置声音	2,2

    删除新建角色1	3,6
    删除新建角色2	3,7
    删除新建角色3	3,8
            ...	...
    多机互动

    1.我们四人走上台	10,0
            2.初次登台经验少	10,1
            3.上来说段三句半	10,2
            4.白日依山尽	10,3
            5.俺们几个话挺多	10,4
            6.上帝给我一张嘴	10,5
            7.已是网络时代	10,6
            8.精神抖擞上台	10,7
            9.研发加班真辛苦	10,8
            10.硬件软件事不少	10,9
            11.论成败	10,10
            12.你表白过吗	10,11
            13.人生好像一场戏	10,12
            14.浊风抖擞上酒场	10,13
            15.头悬粱啊锥刺股	10,14
            16.窗前明月光	10,15
            17.锄禾日当午	10,16
            18.少小离家老大回	10,17
            19.红豆生南国	10,18
            20.朝辞白帝彩云间	10,19
            21.松下问童子	10,20
            22.四大名著真是好	10,21
            23.俺们岁数共十八	10,22
            24.嗨蛋会的也不少	10,23
            25.平时没有露过脸	10,24*/

    public static int[] TEST = {77, 0};

/*
    *//**
     * 发送双码值
     *
     * @param m_ComAir5
     * @param cmd
     *//*
    public static void sendCommand(ComAir5Wrapper m_ComAir5, int[] cmd) {
        if (cmd == null || cmd.length < 2) {
            return;
        }
        m_ComAir5.PlayComAirCmd(cmd[0], 1.0f);
        SystemClock.sleep(900);
        m_ComAir5.PlayComAirCmd(cmd[1], 1.0f);

        Log.e("cmd", "send cmd:[" + cmd[0] + ", " + cmd[1] + "]");

    }*/


}
