package com.cattsoft.phone.quality.utils;

/**
 * Created by Xiaohong on 2014/5/9.
 */
public enum CallDropCause {
    UNKNOW, /* 自定义默认值 */
    NOT_DISCONNECTED,/* has not yet disconnected 连接失败*/
    INCOMING_MISSED,/* an incoming call that was missed and never answered 错过了来电*/
    NORMAL,/* normal; remote 对方挂断电话*/
    LOCAL,/* normal; local hangup 自己挂断电话*/
    BUSY,/* outgoing call to busy line 拨打的号码正在通话*/
    CONGESTION,/* outgoing call to congested network 网络拥挤*/
    MMI,/*not presently used; dial()returns null拨号的号码未被使用*/
    INVALID_NUMBER,/* invalid dial string 无效的号码串*/
    LOST_SIGNAL,
    LIMIT_EXCEEDED,/* eg GSM ACM limit exceeded 超出限额的GSM ACM。ACM(Association for Computing Machinery )中文：美国计算机协会是一个世界性的计算机从业员专业组织*/
    INCOMING_REJECTED,/* an incoming call that was rejected 来电被拒绝*/
    POWER_OFF,/* radio is turned off explicitly 无线网络被关闭*/
    OUT_OF_SERVICE,/* out of service 服务区外*/
    ICC_ERROR,/* No ICC, ICC locked, or other ICC error */
    CALL_BARRED,/* call was blocked by call barrring 被“呼叫限制”拦截*/
    FDN_BLOCKED,/* call was blocked by fixed dial number 被“固定拨号”拦截*/
    CS_RESTRICTED,/* call was blocked by restricted all voice access 被“拒绝语音接入”拦截*/
    CS_RESTRICTED_NORMAL,/* call was blocked by restricted normal voice access 被“拒绝通话”拦截*/
    CS_RESTRICTED_EMERGENCY,/* call was blocked by restricted emergency voice access */
    UNOBTAINABLE_NUMBER,/* Unassigned number (3GPP TS 24.008 table 10.5.123) 未分配的号码*/
    IMSI_UNKNOWN_IN_VLR,/* IMSI is not known at the VLR VLR不能识别IMSI。国际移动用户识别码（IMSI：International Mobile Subscriber Identification Number）是区别移动用户的标志，储存在SIM卡中，可用于区别移动用户的有效信息。其总长度不超过15位，同样使用0～9的数字。其中MCC是移动用户所属国家代号，占3位数字，中国的MCC规定为460；MNC是移动网号码，最多由两位数字组成，用于识别移动用户所归属的移动通信网；MSIN是移动用户识别码，用以识别某一移动通信网中的移动用户。VLR (Visitor Location Register)：拜访位置寄存器。是一个数据库，是存储所管辖区域中MS（统称拜访客户）的来话、去话呼叫所需检索的信息以及用户签约业务和附加业务的信息，例如客户的号码，所处位置区域的识别，向客户提供的服务等参数。*/
    IMEI_NOT_ACCEPTED,/* network does not accept emergency call establishment using an IMEI 网络不接受使用紧急呼叫建立一个IMEI。IMEI(International Mobile Equipment Identity)是国际移动设备身份码的缩写，国际移动装备辨识码，是由15位数字组成的"电子串号"，它与每台手机一一对应，而且该码是全世界唯一的。每一只手机在组装完成后都将被赋予一个全球唯一的一组号码，这个号码从生产到交付使用都将被制造生产的厂商所记录。*/
    CDMA_LOCKED_UNTIL_POWER_CYCLE,/* MS is locked until next power cycle 直到下一个电话周期，MS被关闭。MS：Mobile station 的缩写，移动台*/
    CDMA_DROP,
    CDMA_INTERCEPT,/* INTERCEPT order received, MS state idle entered 接到拦截命令，MS进入闲置状态*/
    CDMA_REORDER,/* MS has been redirected, call is cancelled呼叫被取消 */
    CDMA_SO_REJECT,/* service option rejection 拒绝服务选项*/
    CDMA_RETRY_ORDER,/* requeseted service is rejected, retry delay is set 请求被拒绝，设置延时重试*/
    CDMA_ACCESS_FAILURE,
    CDMA_PREEMPTED,
    CDMA_NOT_EMERGENCY,/* not an emergency call 不是紧急号码*/
    CDMA_ACCESS_BLOCKED,/* Access Blocked by CDMA network 被CDMA网络拦截*/
    ERROR_UNSPECIFIED;

    public CallDropCause toCause(String cause) {
        for (CallDropCause c : CallDropCause.values()) {
            if (c.toString().contentEquals(cause))
                return c;
        }
        return UNKNOW;
    }

    public boolean isDropCall() {
        return this == NOT_DISCONNECTED || this == ERROR_UNSPECIFIED
                || this == OUT_OF_SERVICE || this == POWER_OFF || this == CONGESTION
                || this == LOST_SIGNAL || this == LIMIT_EXCEEDED || this == IMEI_NOT_ACCEPTED;
    }
}
