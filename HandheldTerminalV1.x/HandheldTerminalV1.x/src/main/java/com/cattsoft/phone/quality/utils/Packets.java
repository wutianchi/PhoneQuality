package com.cattsoft.phone.quality.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.github.kevinsawicki.http.HttpRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cattsoft.phone.quality.utils.LogUtils.LOGD;
import static com.cattsoft.phone.quality.utils.LogUtils.makeLogTag;
import static com.cattsoft.phone.quality.utils.Strings.isNullOrEmpty;

/**
 * Created by Xiaohong on 2015/4/8.
 */
public class Packets {
    public static final String TAG = makeLogTag(Packets.class);
    protected static Pattern expCase = Pattern.compile("\\{(.*?)\\}");
    private static boolean isScan = false;

    public static final String REPORT_DIG_TEMPLATE = "+++BEGIN{newline}date={date} {time}{newline}userid={userid}{newline}ip={ip}{newline}mac={mac}{newline}lac={lac}{newline}cid={cid}{newline}asu={asu}{newline}model={model}{newline}operator={operator}{newline}band={band}{newline}mobiletype={mobiletype}{newline}networktype={networktype}{newline}longitude={longitude}{newline}latitude={latitude}{newline}address={address}{newline}content:{newline}{content}{newline}{newline}---END{newline}{newline}";
    public static final String REPORT_SPEED_TEMPLATE = "+++BEGIN{newline}date={date} {time}{newline}userid={userid}{newline}ip={ip}{newline}mac={mac}{newline}lac={lac}{newline}cid={cid}{newline}asu={asu}{newline}model={model}{newline}operator={operator}{newline}band={band}{newline}mobiletype={mobiletype}{newline}networktype={networktype}{newline}longitude={longitude}{newline}latitude={latitude}{newline}address={address}{newline}server_id={server_id}{newline}ping={ping}{newline}download_avg={download_avg}{newline}download_max={download_max}{newline}upload_avg={upload_avg}{newline}upload_max={upload_max}{newline}{newline}---END{newline}{newline}";

    protected static Map<String, String> dictionary(Context context) {
        Map<String, String> dictionary = new HashMap<String, String>();
        dictionary.put("userid", "100000");
        dictionary.put("ip", Devices.getRealIp());
        dictionary.put("mac", Strings.nullToEmpty(Devices.getWifiMac(context)));
        int[] cells = PrefUtils.getLastBaseCellInfo(context);
        dictionary.put("lac", Integer.toString(cells[0]));//基站LAC码
        dictionary.put("cid", Integer.toString(cells[1]));//基站编号
        dictionary.put("asu", Integer.toString(PrefUtils.getLastBaseCellAsu(context)));//信号强度
        dictionary.put("model", Build.MODEL);
        dictionary.put("operator", Devices.getOperatorCode(context));//移动运营商编号
        dictionary.put("band", Integer.toString(MobileNetType.getPhoneType(context)));
        dictionary.put("mobiletype", Integer.toString(MobileNetType.getMobileTypeValue(context)));
        dictionary.put("networktype", Integer.toString(MobileNetType.getNetWorkType(context).type));
        LastLocation location = PrefUtils.getLastLocation(context);
        dictionary.put("longitude", Double.toString(location.getLongitude()));//经度
        dictionary.put("latitude", Double.toString(location.getLatitude()));//纬度
        dictionary.put("address", Strings.nullToEmpty(location.getAddress()));//地址

        dictionary.put("date", new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()));
        dictionary.put("time", new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()));
        dictionary.put("timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()));
        dictionary.put("newline", System.getProperty("line.separator", "\n"));

        return dictionary;
    }

    /**
     * 报文打包
     * @param context
     * @param
     * @param map 填充表
     * @return 打包完成，返回压缩文件路径
     */
    public static File pack(Context context, String template, Map<String, String> map) {
        Map<String, String> dictionary = dictionary(context);
        dictionary.putAll(map);

//        String content = context.getString(resId);
        String content = template;
        Matcher expMatcher = expCase.matcher(content);
        try {
            while (expMatcher.find()) {
                String group = expMatcher.group();
                String match = expMatcher.group(1);
                String quote = Pattern.quote(group);

                String dv = dictionary.get(match);
                if(null == dv) {
                    if(!isNullOrEmpty(System.getProperty(match)))
                        content = content.replaceAll(quote, Matcher.quoteReplacement(System.getProperty(match)));
                } else {
                    content = content.replaceAll(quote, Matcher.quoteReplacement(dv));
                }
            }
            String zipname = dictionary.get("timestamp") + "_1026_Mbcollect_" + dictionary.get("type").toLowerCase() + "_" + dictionary.get("userid") + ".zip";
            String inner = "Mbcollect_" + dictionary.get("type").toLowerCase() + "_" + dictionary.get("timestamp");
            String msg = buildMsg(zipname, inner, content);
            boolean res = send(msg,dictionary.get("sever"));

            /*if (!res){
                File source = new File(context.getFilesDir(), "Mbcollect_" + dictionary.get("type").toLowerCase() + "_" + dictionary.get("timestamp")+"_"+dictionary.get("sever"));
                // 数据写入文件
                FileOutputStream fos = null;
                OutputStreamWriter out = null;
                try {
                    fos = new FileOutputStream(source, false);
                    out = new OutputStreamWriter(fos, "GB2312");

                    out.write(msg);
                    out.flush();

                    fos.flush();
                } catch (Exception e) {
                    source.delete();
                    throw new Exception("报文数据写入文件失败!", e);
                } finally {
                    try {
                        if (null != out)
                            out.close();
                    } catch (Exception e) {
                    }
                    try {
                        if (null != fos)
                            fos.close();
                    } catch (Exception e) {
                    }
                }
            }*/
//            // 报文写入文件并压缩
//            File source = new File(context.getFilesDir(), "Mbcollect_" + dictionary.get("type").toLowerCase() + "_" + dictionary.get("timestamp"));
//            File zipSource = new File(context.getFilesDir(), dictionary.get("timestamp") + "_1026_Mbcollect_" + dictionary.get("type").toLowerCase() + "_" + dictionary.get("userid") + ".zip");
//
//            // 数据写入文件
//            FileOutputStream fos = null;
//            OutputStreamWriter out = null;
//            try {
//                fos = new FileOutputStream(source, false);
//                out = new OutputStreamWriter(fos, "GB2312");
//
//                out.write(content);
//                out.flush();
//
//                fos.flush();
//            } catch (Exception e) {
//                source.delete();
//                throw new Exception("报文数据写入文件失败!", e);
//            } finally {
//                try {
//                    if (null != out)
//                        out.close();
//                } catch (Exception e) {
//                }
//                try {
//                    if (null != fos)
//                        fos.close();
//                } catch (Exception e) {
//                }
//            }
//            try {
//                ZipUtil.packEntry(source, zipSource);
//                // 删除源文件
//                source.delete();
//                // 报文上传
//                ReportService.startActionUpload(context, "", "");
//
//                return zipSource;
//            } catch (Exception e) {
//                throw new Exception("打包报文数据文件时出现异常", e);
//            }
        } catch (Exception e) {
            LOGD(TAG, "打包报文时出现异常", e);
        }
        return null;
    }

    public static String buildMsg(String zipname,String innername,String content){
         return  "{\"logType\":\"mobileTestSpeed\",\"zipname\":\""+zipname+"\",\"logname\":\""+innername+"\",\"logContent\":\""+content+"\"}";
    }


    public static boolean send(final String msg, final String sever){
        Thread t = new Thread(){
            public void run(){
                Log.d("Packets", msg);
                boolean b = false;
                try {
//                    HttpRequest request =  HttpRequest.get("http://"+sever+"/speed/uploadData");
                    HttpRequest request =  HttpRequest.get("http://192.168.3.18:9092/dataServer/uploadData");
                    HttpURLConnection conn = request.getConnection();
                    conn.setDoOutput(true);
                    OutputStream out = conn.getOutputStream();

                    out.write(msg.getBytes());
                    out.close();
                    String body = request.body();
                    if ("success".equals(body)){
                        b=true;
                    }
                    Log.d("BoDy", body);
                    System.out.println(body);
                } catch (Exception e) {
                    Log.e("UPEXC", e.getMessage());
                    String s = Log.getStackTraceString(e);
                    Log.e("UPEXCSTR", s);
                }
            }
        };
        t.start();
        try {
            t.join(5000);
        } catch (InterruptedException e) {
        }
        return true;
    }

    public static void send(File file) {
        FileInputStream fin = null;
        InputStreamReader inreader = null;
        String sever =file.getName().substring(file.getName().lastIndexOf("_"));
        try {
            fin = new FileInputStream(file);
            inreader = new InputStreamReader(fin, "GB2312");
            char[] ch = new char[1024];
            StringBuffer sb = new StringBuffer();
            int len = -1;
            while((len = inreader.read(ch)) != -1){
                sb.append(ch, 0, len);
            }
            boolean res = send(sb.toString(),sever);
            if(res){
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void scanFile(final Context context){
        /*if (isScan){
            return;
        }
        isScan=true;
        new Thread(){
            public void run(){
                while(true) {
                    File file = new File(String.valueOf(context.getFilesDir()));
                    File[] files = file.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            return filename.matches("^Mbcollect_.*?");
                        }
                    });

                    for (int i = 0; i < files.length; i++) {
                        File f = files[i];
                        send(f);
                    }

                    try {
                        TimeUnit.MINUTES.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();*/
    }
}
