package com.example.huang.test.net;

/**
 * Created by huang on 2018/4/12.
 */

public class Controller {
    /**
     * 手机登入接口
     */
    public static String login = Ip.ip + "/mobilelogin.action";

    /**
     * 手机邮箱验证唯一性
     */
    public static String emailonly = Ip.ip + "/user/emailisonly.action";
    /**
     * 手机号码验证唯一性
     */
    public static String phoneonly = Ip.ip + "/user/phoneisonly.action";
    /**
     * 手机注册接口
     */
    public static String resign = Ip.ip + "/mobileadduser.action";
    /**
     * 邮箱验证
     */
    public static String findemailpassword = Ip.ip + "/send.action";
    /**
     * 修改密码
     */
    public static String changepassword = Ip.ip + "/changepassword.action";
    /**
     * 获取场馆信息
     */
    public static String getVenuesinfo = Ip.ip + "/mobile/collectioninfo.action";
    /**
     * 获取场馆详细信息
     */
    public static String getAllocationinfo = Ip.ip + "/venuesallcoation/venuesallocationinfo.action";
    /**
     * 收藏
     */
    public static String Collection = Ip.ip + "/mobile/collectio.action";

    /**
     * 收藏
     */
    public static String getCollection = Ip.ip + "/mobile/getcollectio.action";
    /**
     * 获取用户信息
     */
    public static String getUserinfobyid = Ip.ip + "/mobilefinduserinfo.action";

    /**
     * 完善用户信息
     */
    public static String fininshuserinfo = Ip.ip + "/user/edituser.action";
    /**
     * 添加银行卡
     */
    public static String addBank = Ip.ip + "/mobilebank/addbank.action";
    /**
     * 获取用户银行卡信息
     */
    public static String getBankinfo = Ip.ip + "/mobilebank/showinfo.action";
    /**
     * 支付接口
     */
    public static String pay = Ip.ip + "/mobilepay/pay.action";
    /**
     * 支付接口
     */
    public static String payhistory = Ip.ip + "/orders/showallorderinfo.action";
    /**
     *参加场馆活动
     */
    public static String joinActivityVenues = Ip.ip + "/join/joinVenuesActivity.action";
    /**
     *参加场馆活动
     */
    public static String joineditOrderActivityVenues = Ip.ip + "/join/editOrderVenuesActivity.action";
    /**
     *修改馆活动
     */
    public static String joineditActivityVenuesinfo = Ip.ip + "/join/allinfoVenuesActivity.action";
    /**
     *取消馆活动
     */
    public static String cancelActivityVenuesinfo = Ip.ip + "/join/editFinishtime.action";
    /**
     *评论
     */
    public static String comment = Ip.ip + "/join/comment.action";

    /**
     *增加用户自定义活动
     */
    public static String usercustomeractivity = Ip.ip + "/usercustomactivity/addinfo.action";
    /**
     *上传用户自定义活动图片
     */
    public static String uploadimage = Ip.ip + "/usercustomactivity/filesUpload.action";
    /**
     *修改
     */
    public static String editusercustomeractivity = Ip.ip + "/usercustomactivity/editinfo.action";
    /**
     *获取用户自定义活动
     */
    public static String getUserCustomeract = Ip.ip + "/usercustomactivity/allinfo.action";
    /**
     *管理用户自定义活动
     */
    public static String getManagetUserCustomeract = Ip.ip + "/usercustomer/allinfoweb.action";

    /**
     *参加用户自定义活动
     */
    public static String partuseract = Ip.ip + "/mobileparticipateuser/addparticipate.action";
    /**
     *修改用户参加自定义活动
     */
    public static String editpartuseract = Ip.ip + "/mobileparticipateuser/editparticipate.action";
    /**
     *修改用户参加自定义活动
     */
    public static String showpartuseract = Ip.ip + "/mobileparticipateuser/allinfo.action";
}


