package com.sunsta.bear.faster;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.config.LoadingConfig;
import com.sunsta.bear.engine.DownloadService;
import com.sunsta.bear.entity.Barrage;
import com.sunsta.bear.model.ReplySSLMode;
import com.sunsta.bear.model.adapter.BarrageDataAdapter;
import com.sunsta.bear.presenter.BaseInternetApi;
import com.sunsta.bear.presenter.net.InternetClient;
import com.sunsta.bear.presenter.net.InternetException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * <h2>?????????????????????Bgwan??? ??????an???????????????????????????livery???????????????????????????20190922-?????????????????????...</h2>
 * ?????????????????????????????? * <br/>
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------??????????????????????????????????????????an???????????????????????????????????????????????????????????????</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">???????????????(C) 2016 The Android Developer Sunst</a></h3>
 * <br>???????????????????????????2016/9/7
 * <br>??????Email???qyddai@gmail.com
 * <br>Github???<a href ="https://qydq.github.io">qydq</a>
 * <br>???????????????<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 4.0 |   2020/03/19 23:51       |   ??????????????????????????????????????????????????????????????????????????????.????????????????????????https://zhuanlan.zhihu.com/p/83080230
 */
public class DataService {
    public static final String TIME_FORMAT_EE = "yyyyMMdd-HH:mm:ss:SSS";
    public static final String TIME_FORMAT_E = "yyyy-MM-dd hh:mm:ss";

    private final String name = "OBJECT_DATA";
    private String TAG = getClass().getName();

    private static final byte[] hexNumberTable = new byte[128];
    private static final char[] lookUpHexAlphabet = new char[16];
    private boolean available = false;

    private static byte[] DESIV = {1, 2, 3, 4, 5, 6, 7, 8};
    private final static String HEX = "0123456789ABCDEF";
    private static final String DESAlgorithm = "DES";
    private static final String AESAlgorithm = "AES";
    private static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
    /**
     * des ?????? key
     */
    public final static String DES_KEY_STRING = "12345678";


    /**
     * ?????????????????????????????????????????????????????????private
     */
    //????????????
    private static final String rInternetAddress = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";
    private static final Pattern pInternetAddress = Pattern.compile(rInternetAddress);

    //????????????
    private static final String rEmail = "^[A-Za-z0-9_]+([-+.][A-Za-z0-9_]+)*@[A-Za-z0-9_]+([-.][A-Za-z0-9_]+)*.[A-Za-z0-9_]+([-.][A-Za-z0-9_]+)*$";
    private static final Pattern pEmail = Pattern.compile(rEmail);

    //????????????
    private static final String rMobiles = "^((13[0-9])|(15[^4,\\D])|(17[6-8])|(18[0-9])|(14[5,7]))\\d{8}$";
    private static final Pattern pMobiles = Pattern.compile(rMobiles);

    //??????
    private static final Pattern pNumeric = Pattern.compile("[0-9]*");

    //???????????????
    private static final String rCharAt = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~???@#???%??????&*????????????+|{}????????????????????????????????????]";
    private static final Pattern pCharAt = Pattern.compile(rCharAt);

    //ip??????
    private static final String rIp = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    private static final Pattern pIp = Pattern.compile(rIp);


    static {
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        byteBuffer.put((byte) 2);
    }

    static {
        int i;
        for (i = 0; i < 128; ++i) {
            hexNumberTable[i] = -1;
        }
        for (i = 57; i >= 48; --i) {
            hexNumberTable[i] = (byte) (i - 48);
        }
        for (i = 70; i >= 65; --i) {
            hexNumberTable[i] = (byte) (i - 65 + 10);
        }
        for (i = 102; i >= 97; --i) {
            hexNumberTable[i] = (byte) (i - 97 + 10);
        }
        for (i = 0; i < 10; ++i) {
            lookUpHexAlphabet[i] = (char) (48 + i);
        }
        for (i = 10; i <= 15; ++i) {
            lookUpHexAlphabet[i] = (char) (65 + i - 10);
        }
    }

    private DataService() {
    }

    public static DataService getInstance() {
        return LaEnumDataServer.INSTANCE.getInstance();
    }

    private enum LaEnumDataServer {
        INSTANCE;
        private DataService laInstance;

        LaEnumDataServer() {
            laInstance = new DataService();
        }

        private DataService getInstance() {
            return laInstance;
        }
    }


    /**
     * ?????????????????????IP??????
     * @param ipAddress IP??????
     */
    public boolean regexCheckIp(@NonNull String ipAddress) {
        return pIp.matcher(ipAddress).matches();
    }

    /**
     * ?????????????????????????????????????????????????????????????????????RxJava??????????????????
     * @param httpURL ????????????
     */
    public void reUrlAvailable(@NonNull String httpURL, @NonNull AvailableListener availableListener) {
        if (httpURL.startsWith("http") || httpURL.startsWith("Http") || httpURL.startsWith("HTTP") ||
                httpURL.startsWith("ftp") || httpURL.startsWith("Ftp") || httpURL.startsWith("FTP")) {
            Observable.timer(10, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(Long aLong) {
                    if (!available) {
                        availableListener.unAvailable(httpURL);
                    }
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onComplete() {
                }
            });
            Observable.just(httpURL)
                    .subscribeOn(Schedulers.computation())
                    .map(s -> pInternetAddress.matcher(httpURL).matches())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            available = aBoolean;
                            if (aBoolean) {
                                availableListener.available(httpURL);
                            } else {
                                availableListener.unAvailable(httpURL);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            availableListener.unAvailable(httpURL);
        }
    }

    public interface AvailableListener {
        void available(String availableUrl);

        void unAvailable(String url);
    }

    /**
     * ???????????????????????????
     * @param email ??????
     * @return true || false
     */
    public boolean reEmailAvailable(@NonNull String email) {
        Matcher matcher = pEmail.matcher(email);
        return matcher.matches();
    }

    /**
     * ???????????????????????????
     * @param mobiles ????????????
     * @return true || false
     */
    public boolean reMobilesAvailabe(@NonNull String mobiles) {
        return pMobiles.matcher(mobiles).matches();
    }

    /**
     * ???????????????????????????
     * @param mobiles ????????????
     * @return this 158****0609
     */
    public String getSecurityMobiles(@NonNull String mobiles) {
        return reMobilesAvailabe(mobiles) ? mobiles.substring(0, 3) + "****" + mobiles.substring(mobiles.length() - 4) : "not exist mobiles";
    }

    /**
     * ???????????????????????????
     * @param numeric numeric?????????
     */
    public boolean reNumericLegal(@NonNull String numeric) {
        return pNumeric.matcher(numeric).matches();
    }

    /**
     * ??????????????????????????????????????????true??????????????????false
     * @param txt ?????????????????????
     */
    public boolean reCharLegal(@NonNull String txt) {
        return pCharAt.matcher(txt).matches();
    }

    /**
     * ?????????????????????
     * @param numeric numeric?????????
     * @return true || false
     */
    public boolean isNumeric(@NonNull String numeric) {
        for (int i = 0; i < numeric.length(); i++) {
            if (!Character.isDigit(numeric.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * ??????
     */
    public void copyClipboad(@NonNull Context context, String copyContent) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", copyContent);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(mClipData);
        }
    }

    public String defaultEmpty(String str, String defaultStr) {
        return TextUtils.isEmpty(str) ? defaultStr : str;
    }

    /**
     * ????????? MD5??????
     * @param str ??????????????????
     * @return MD5???????????????
     */
    public String md5utf8(@NonNull String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] result = md.digest(str.getBytes("utf-8"));
            return hexToString(result);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * byte[]?????????String
     * @param bytes
     */
    public String hexToString(@NonNull byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toLowerCase());
        }
        return sb.toString();
    }

    /**
     * ????????? MD5??????
     * @param serviceStr ???????????????????????????
     * @return MD5???????????????
     */
    public String md5service(@NonNull String serviceStr) {
        String resultString = null;
        try {
            resultString = new String(serviceStr);
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultString;
    }

    /**
     * md5??????????????????
     * @param text ?????????????????????
     * @return ?????????????????????
     */
    public String md5(@NonNull String text) {
        try {
            // ????????????MD5????????????????????????SHA1???????????????SHA1??????
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // ???????????????????????????????????????
            byte[] inputByteArray = text.getBytes();
            // inputByteArray?????????????????????????????????????????????
            messageDigest.update(inputByteArray);
            // ???????????????????????????????????????????????????16?????????
            byte[] resultByteArray = messageDigest.digest();
            // ????????????????????????????????????
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public byte[] byteArrayToHex(@NonNull String hexString) {
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * ??????????????????????????????????????????16??????????????????
     * @param byteArray
     * @return 16???????????????
     */
    public String byteArrayToHex(@NonNull byte[] byteArray) {
        // ??????????????????????????????????????????????????????16????????????
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        // new???????????????????????????????????????????????????????????????????????????????????????byte??????????????????????????????2????????????????????????2???8????????????16???2????????????
        char[] resultCharArray = new char[byteArray.length * 2];
        // ??????????????????????????????????????????????????????????????????????????????????????????????????????
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }

    /**
     * ??????????????????
     * @return ????????????
     */
    public String getCurrentTimeByDate() {
        Date date = new Date(System.currentTimeMillis());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss SSS");
        return sdf.format(date);
    }

    public String getTimePrefix() {
        SimpleDateFormat df = new SimpleDateFormat(TIME_FORMAT_EE, Locale.ENGLISH);
        return df.format(System.currentTimeMillis());
    }


    /**
     * TODO ??????????????????
     * @return Date    ????????????
     */
    public Date getCurrentTimeByCalendar() {
        Calendar c = Calendar.getInstance();
        Date date = new Date();
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day);
        Date nowDay = c.getTime();
        return nowDay;
    }

    /**
     * ?????????????????????
     * @return ???????????????
     */
    public String getShotDateTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd  HH:mm");
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }

    /**
     * ?????????????????????
     * @return ??????????????????
     */
    public String getLongDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd.  HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }

    /**
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) HEX.indexOf(c);
    }

    /**
     * ???????????????????????????list???????????????
     * @param lists     ????????????
     * @param valueName ??????????????????
     * @return ????????????????????????
     */
    public List<String> removeItemByName(@NonNull List<String> lists,
                                         @NonNull String valueName) {
        List<String> resLists = new ArrayList<>();
        if (lists.size() == 0) {
        } else {
            for (int i = 0; i < lists.size(); i++) {
                if (valueName.equals(lists.get(i))) {
                    lists.remove(valueName);
                }
            }
            resLists = lists;
        }
        return resLists;
    }

    /**
     * ???????????????????????????????????????list???????????????
     * @param lists    ????????????
     * @param position ??????
     * @return ????????????????????????lists
     */
    public List<String> removeItemByPosition(@NonNull List<String> lists,
                                             @NonNull int position) {
        lists.remove(position);
        return lists;
    }

//    //????????????
//    public List likeString(List lists, String likename) {
//        List results = new ArrayList();
//        Pattern pattern = Pattern.compile(name);
//        for (int i = 0; i < lists.size(); i++) {
//            Matcher matcher = pattern.matcher(((Employee) lists.get(i)).getName());
//            if (matcher.find()) {
//                results.add(list.get(i));
//            }
//        }
//        return results;
//    }

    /**
     * ???????????????????????????????????????
     * @param strDate ???????????????
     * @param pattern ????????????
     * @return???Date
     */

    public Date parse(@NonNull String strDate,
                      @NonNull String pattern) {

        if (TextUtils.isEmpty(strDate)) {
            return null;
        }
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ?????????????????????????????????
     * @param date    ??????
     * @param pattern ????????????
     * @return???String???????????????
     */

    public String format(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }

    /**
     * Google Gson
     * @param gsonString
     * @return true json??????false???
     */
    public boolean checkJson(@NonNull Gson gson, @NonNull String gsonString) {
        try {
            gson.fromJson(gsonString, Object.class);
            return true;
        } catch (JsonSyntaxException ex) {
            return false;
        }
    }

    public boolean checkJson(@NonNull String gsonString) {
        return checkJson(new Gson(), gsonString);
    }

    /**
     * ??????????????????JsonArray (alibaba ?????????1?????????JSON???)
     * ?????????[{a:b}]  [{'a':'b'}]  [{"a":"b"}]
     * @param targetStr
     * @return
     */
    public boolean checkJsonArray(String targetStr) {
        if (TextUtils.isEmpty(targetStr)) {
            return false;
        }
        try {
            new Gson().fromJson(targetStr, JsonArray.class);
            return true;
        } catch (JsonSyntaxException ex) {
            return false;
        }
    }

    //todo ?????????int??????byte??????
    public byte[] int2ByteArrayInIO(String date) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(baos);
        try {
            dataOutputStream.writeByte(-12);
            dataOutputStream.writeLong(12);
            dataOutputStream.writeChar('1');
            dataOutputStream.writeFloat(1.01f);
            dataOutputStream.writeUTF("???");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    //todo ?????????byte??????int
    public void bytes2IntInIO(byte[] serializeId) {
        ByteArrayInputStream bais = new ByteArrayInputStream(serializeId);
        DataInputStream dis = new DataInputStream(bais);
        try {
            System.out.println(dis.readByte());
            System.out.println(dis.readLong());
            System.out.println(dis.readChar());
            System.out.println(dis.readFloat());
            System.out.println(dis.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //?????????
    public void main(String[] args) {
        int v = 123456;
        byte[] bytes = ByteBuffer.allocate(4).putInt(v).array();
        for (byte t : bytes) {
            System.out.println(t);
        }
        System.out.println("----- ????????? -------");
        byte[] bytes2 = int2ByteArray(v);
        for (byte t : bytes2) {
            System.out.println(t);
        }
    }

    //    int???byte[]?????????????????????
    public byte[] int2ByteArray(int value) {
        byte[] result = new byte[4];
        result[0] = (byte) ((value >> 24) & 0xFF);
        result[1] = (byte) ((value >> 16) & 0xFF);
        result[2] = (byte) ((value >> 8) & 0xFF);
        result[3] = (byte) (value & 0xFF);
        return result;
    }

    //    byte[]???int?????????????????????
    public int byteArray2Int(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;// ????????????
        }
        return value;
    }

    //    offset????????????
    public int[] bytes2IntArray(byte[] bytes, int offset) {
        int[] values = new int[bytes.length / 4];
        for (int i = 0; i < bytes.length / 4; i++) {
            int value = (int) ((bytes[offset] & 0xFF)
                    | ((bytes[offset + 1] & 0xFF) << 8)
                    | ((bytes[offset + 2] & 0xFF) << 16)
                    | ((bytes[offset + 3] & 0xFF) << 24));
            values[i] = value;
            offset += 4;
        }
        return values;
    }

    //    name="offset">????????????,?????????0
    public byte[] intArray2Bytes(int[] value, int offset) {
        byte[] values = new byte[value.length * 4];
        for (int i = 0; i < value.length; i++) {
            values[offset + 3] = (byte) ((value[i] >> 24) & 0xFF);
            values[offset + 2] = (byte) ((value[i] >> 16) & 0xFF);
            values[offset + 1] = (byte) ((value[i] >> 8) & 0xFF);
            values[offset] = (byte) (value[i] & 0xFF);
            offset += 4;
        }
        return values;
    }

    //???byte???????????????int????????????
    public int[][] bytes2DimensionInt(byte[] bytes, int offset) {
        int[][] values = new int[bytes.length / 8][2];
        for (int i = 0; i < bytes.length / 8; i++) {
            for (int j = 0; j < 2; j++) {
                int value = (int) ((bytes[offset] & 0xFF)
                        | ((bytes[offset + 1] & 0xFF) << 8)
                        | ((bytes[offset + 2] & 0xFF) << 16)
                        | ((bytes[offset + 3] & 0xFF) << 24));
                values[i][j] = value;
                offset += 4;
            }
        }
        return values;
    }

    //???????????????byte[]
    public byte[] file2ByteArray(String filePath) {
        File file = new File(filePath);
        ByteArrayOutputStream out = null;
        try {
            FileInputStream in = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int i = 0;
            while ((i = in.read(b)) != -1) {
                out.write(b, 0, b.length);
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert out != null;
        return out.toByteArray();
    }

    //bitmap??????base64
    public String bitmap2Base64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

//    ?????????????????????????????????????????????Serializable???????????????????????????????????????????????????
//    ???????????????Serializable?????????transient?????????????????????????????????,???????????????set,get????????????

    /**
     * ??????????????????,LaPreference????????????????????????
     */
    public void saveObject(Object object, Context mContext) {
        FileOutputStream stream;
        ObjectOutputStream oos;
        try {
            stream = mContext.openFileOutput(name, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(stream);
            oos.writeObject(object);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????????????????
     */
    public Object getObject(Context mContext) {
        FileInputStream stream;
        Object obj = null;
        try {
            stream = mContext.openFileInput(name);
            ObjectInputStream ois = new ObjectInputStream(stream);
            obj = ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * ???????????????,????????????byte[]
     * <p>
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    public String serialize(Object object) throws IOException {
        Log.d("sunst888" + TAG, "into serialize1 : = not null");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        String serializeString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        objectOutputStream.close();
//        objectOutputStream.flush();//clear
//        byteArrayOutputStream.flush();//flush
//        byteArrayOutputStream.reset();//reset
//        objectOutputStream.reset();//reset
        byteArrayOutputStream.close();
        Log.d("sunst888" + TAG, "into serialize2 : = not null");
//        byte[] bytes = serializeString.getBytes();
        return serializeString;
    }

    /*??????????????????*/
    public Object unSerialize(String serializeId) throws IOException, ClassNotFoundException {
        Log.d("sunst888" + TAG, "into unSerialize1 : = not null");
        byte[] mobileBytes = Base64.decode(serializeId.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        Log.d("sunst888" + TAG, "into unSerialize2 : = not null");
        return object;
    }

    /*hashMap?????????*/
    public byte[] serialize(HashMap<String, String> hashMap) {
        try {
            ByteArrayOutputStream mem_out = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(mem_out);
            out.writeObject(hashMap);
            out.close();
            mem_out.close();
            byte[] bytes = mem_out.toByteArray();
            return bytes;
        } catch (IOException e) {
            return null;
        }
    }

    /*hashMap?????????*/
    public HashMap<String, String> deserialize(byte[] bytes) {
        try {
            ByteArrayInputStream mem_in = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(mem_in);
            HashMap<String, String> hashMap = (HashMap<String, String>) in.readObject();
            in.close();
            mem_in.close();
            return hashMap;
        } catch (StreamCorruptedException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public int getTestCommandId(int type) {
        byte commandId = (byte) (type - (11 << 8));
        Log.d("sunst888" + TAG, "into getCommandId : commandId=" + commandId);
        return commandId;
    }

    public int getCommandId(int type) {
        byte commandId = (byte) (type & 0xff);
        Log.d("sunst888" + TAG, "into getCommandId : commandId=" + commandId);
        return commandId;
    }

    public int getServiceId(int type) {
        byte serviceId = (byte) (type >> 8 & 0xff);
        Log.d("sunst888" + TAG, "into getCommandId : serviceId=" + serviceId);
        return serviceId;
    }

    /*---------?????????--su--*/
/*???????????????
0xff ??????????????????32???int???
byte b = 11111111 ??? // ?????????????????????????????? -1
b & 0xff ???????????? 000...000(24???)11111111
??????????????????32???int?????????24???0?????????????????????????????????int?????? 255*/
    public int byte2Int(byte b) {
        return (int) b;
    }

    public int byteInt(byte b) {
        return (int) (b & 0xff);
    }

    /*----------?????????---co*/
    public String byteToHex(byte[] var0) {
        if (var0 == null) {
            return "";
        } else {
            StringBuilder var1 = new StringBuilder("");
            int var2 = 0;

            for (int var3 = var0.length; var2 < var3; ++var2) {
                String var4 = Integer.toHexString(var0[var2] & 255);
                if (var4.length() == 1) {
                    var1.append("0").append(var4);
                } else {
                    var1.append(var4);
                }
            }

            return var1.toString().toUpperCase(Locale.ENGLISH).trim();
        }
    }

    public String byteToHex(Byte[] var0) {
        if (var0 == null) {
            return "";
        } else {
            StringBuilder var1 = new StringBuilder("");
            int var2 = 0;
            for (int var3 = var0.length; var2 < var3; ++var2) {
                String var4 = Integer.toHexString(var0[var2] & 255);
                if (var4.length() == 1) {
                    var1.append("0").append(var4);
                } else {
                    var1.append(var4);
                }
            }

            return var1.toString().toUpperCase(Locale.ENGLISH).trim();
        }
    }

    public long bytesToLong(byte[] var0) {
        long var1 = 0L;
        int var3 = 0;

        for (int var4 = var0.length; var3 < var4; ++var3) {
            var1 = var1 << 8 | (long) (var0[var3] & 255);
        }
        return var1;
    }

    private final byte[] NULL_BYTE_ARRAY = new byte[0];

    public byte[] hexToBytes(String var0) {
        if (isEmpty(var0)) {
            return NULL_BYTE_ARRAY;
        } else {
            String var1 = var0.replace(" ", "");
            int var2 = var1.length() / 2;
            byte[] var7 = new byte[var2];

            for (int var3 = 0; var3 < var2; ++var3) {
                int var4 = var3 * 2 + 1;
                String var5 = var1.substring(var3 * 2, var4);
                String var6 = var1.substring(var4, var4 + 1);
                var7[var3] = (byte) Integer.parseInt(var5 + var6, 16);
            }

            return var7;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String hexToString(String var0) {
        if (isEmpty(var0)) {
            return "";
        } else {
            char[] var1 = var0.toCharArray();
            byte[] var2 = new byte[var0.length() / 2];

            for (int var3 = 0; var3 < var2.length; ++var3) {
                var2[var3] = (byte) (HEX.indexOf(var1[var3 * 2]) * 16 + HEX.indexOf(var1[var3 * 2 + 1]) & 255);
            }

            var0 = new String(var2, StandardCharsets.UTF_8);
            return var0;
        }
    }

    public String int2Uint16Hex(int var0) {
        String var1 = intToHex(var0 >> 8 & 255);
        return var1 + intToHex(var0 & 255);
    }

    public String int2Uint32Hex(int var0) {
        String var1 = intToHex(var0 >> 24 & 255);
        var1 = var1 + intToHex(var0 >> 16 & 255);
        var1 = var1 + intToHex(var0 >> 8 & 255);
        return var1 + intToHex(var0 & 255);
    }

    public byte[] intTo16UnitBytes(int var0) {
        return new byte[]{(byte) (('\uff00' & var0) >> 8), (byte) (var0 & 255)};
    }

    public byte[] intTo32UnitBytes(int var0) {
        return new byte[]{(byte) (var0 >> 24 & 255), (byte) (var0 >> 16 & 255), (byte) (var0 >> 8 & 255), (byte) (var0 & 255)};
    }

    public byte[] intTo64UnitBytes(long var0) {
        byte[] var2 = new byte[8];

        for (int var3 = 0; var3 < var2.length; ++var3) {
            var2[var2.length - var3 - 1] = (byte) ((int) (255L & var0));
            var0 >>= 8;
        }

        return var2;
    }

    public String intToHex(int var0) {
        String var1;
        if (var0 >= 0) {
            if (Integer.toHexString(var0).length() % 2 != 0) {
                var1 = "0" + Integer.toHexString(var0);
            } else {
                var1 = Integer.toHexString(var0);
            }
        } else {
            var1 = Integer.toHexString(var0);
            var1 = var1.substring(var1.length() - 4, var1.length());
        }

        return var1;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String stringToHex(String var0) {
        int var1 = 0;
        if (isEmpty(var0)) {
            return "";
        } else {
            char[] var2 = HEX.toCharArray();
            StringBuilder var3 = new StringBuilder("");
            Object var4 = null;
            byte[] var8 = var0.getBytes(StandardCharsets.UTF_8);
            if (var8.length <= 0) {
                return "";
            } else {
                for (int var5 = var8.length; var1 < var5; ++var1) {
                    byte var6 = var8[var1];
                    var3.append(var2[(var6 & 240) >> 4]);
                    var3.append(var2[var6 & 15]);
                }

                return var3.toString().trim();
            }
        }
    }

    /**
     * rand????????????????????? MIN ??? MAX ?????????????????????
     */
    public int randomNextInt(int MIN, int MAX) {
        return new Random().nextInt(MAX - MIN + 1) + MIN;
    }

    /**
     * ???????????????????????????2?????????
     */
    public float randomNextFloat(float begin, float end) {
        BigDecimal bigDecimal = new BigDecimal(end - begin);
        BigDecimal point = BigDecimal.valueOf(Math.random());
        BigDecimal pointBetween = point.multiply(bigDecimal);
        BigDecimal result = pointBetween.add(new BigDecimal(begin)).setScale(2, BigDecimal.ROUND_FLOOR);
        return result.floatValue();
    }

    public List<String> transferArrayToList(String[] array) {
        List<String> transferedList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            List<String> finalTransferedList = transferedList;
            Arrays.stream(array).forEach(arr -> finalTransferedList.add(arr));
        } else {
            transferedList = new ArrayList<>(Arrays.asList(array));
        }
        return transferedList;
    }

    public String stringToUnicode(String var0) {
        if (isEmpty(var0)) {
            return "";
        } else {
            StringBuilder var1 = new StringBuilder();

            for (int var2 = 0; var2 < var0.length(); ++var2) {
                char var3 = var0.charAt(var2);
                String var4 = Integer.toHexString(var3);
                if (var3 > 128) {
                    var1.append(var4);
                } else {
                    var1.append("00").append(var4);
                }
            }

            return var1.toString();
        }
    }

    private boolean isEmpty(String str) {
        return null == str || str.isEmpty();
    }

    public String unicodeToString(String var0) {
        if (isEmpty(var0)) {
            return "";
        } else {
            StringBuilder var1 = new StringBuilder();
            int var2 = var0.length() / 4;

            for (int var3 = 0; var3 < var2; ++var3) {
                String var4 = var0.substring(var3 * 4, (var3 + 1) * 4);
                String var5 = var4.substring(2);
                var1.append(new String(Character.toChars(Integer.valueOf(var4.substring(0, 2) + "00", 16) + Integer.valueOf(var5, 16))));
            }

            return var1.toString();
        }
    }

    public byte[] arraySplice(byte[] prep, byte[] after) {
        byte[] result = new byte[prep.length + after.length];
        System.arraycopy(prep, 0, result, 0, prep.length);
        System.arraycopy(after, 0, result, prep.length, after.length);
        return result;
    }

    public byte[] String2Hex(String encoded) {
        if (encoded == null) {
            return null;
        } else {
            int lengthData = encoded.length();
            if (lengthData % 2 != 0) {
                return null;
            } else {
                char[] binaryData = encoded.toCharArray();
                int lengthDecode = lengthData / 2;
                byte[] decodedData = new byte[lengthDecode];

                for (int i = 0; i < lengthDecode; ++i) {
                    char tempChar = binaryData[i * 2];
                    byte temp1 = tempChar < 128 ? hexNumberTable[tempChar] : -1;
                    if (temp1 == -1) {
                        return null;
                    }

                    tempChar = binaryData[i * 2 + 1];
                    byte temp2 = tempChar < 128 ? hexNumberTable[tempChar] : -1;
                    if (temp2 == -1) {
                        return null;
                    }

                    decodedData[i] = (byte) (temp1 << 4 | temp2);
                }

                return decodedData;
            }
        }
    }

    public void rxJavaPluginsPatch() {
        String lkssl = SPUtils.getInstance().getString("lkssl");
        if (TextUtils.isEmpty(lkssl)) {
            ValueOf.intercept = true;
            BaseInternetApi api = InternetClient.getInstance().obtainBaseApi();
            if (api == null) {
                ValueOf.intercept = false;
                return;
            }
            InternetClient.getInstance().addDispose(api
                    .observableReallyGet(desDecrypt(DownloadService.lk1ssl, DataService.DES_KEY_STRING)
                            + desDecrypt(DownloadService.lk2ssl, DataService.DES_KEY_STRING))
                    .compose(Convert.io_main())
                    .subscribe((Consumer<ResponseBody>) rb -> {
                        String result = Objects.requireNonNull(rb).string();
                        if (!TextUtils.isEmpty(result) && DataService.getInstance().checkJson(result)) {
                            lkssl(result);
                            ValueOf.intercept = false;
                        }
                    }, new InternetException() {
                        @Override
                        public void onError(int code, String msg) {
                            if (!TextUtils.isEmpty(msg) && msg.contains("Failed to connect")) {
                                rxJavaAliPluginsPatch();
                            }
                        }
                    }));
        }
    }

    public void rxJavaAliPluginsPatch() {
        ValueOf.intercept = true;
        BaseInternetApi api = InternetClient.getInstance().obtainBaseApi();
        if (api == null) {
            ValueOf.intercept = false;
            return;
        }
        InternetClient.getInstance().addDispose(api
                .observableReallyGet(desDecrypt(DownloadService.lk3ssl, DataService.DES_KEY_STRING)
                        + desDecrypt(DownloadService.lk2ssl, DataService.DES_KEY_STRING))
                .compose(Convert.io_main())
                .subscribe((Consumer<ResponseBody>) rb -> {
                    String result = Objects.requireNonNull(rb).string();
                    if (!TextUtils.isEmpty(result) && DataService.getInstance().checkJson(result)) {
                        lkssl(result);
                        ValueOf.intercept = false;
                    }
                }, new InternetException() {
                    @Override
                    public void onError(int code, String msg) {
                    }
                }));
    }

    /**
     * @param cleartext      ??????
     * @param encryptSeedKey ??????
     * @return ??????????????????
     */
    public String des(String cleartext, String encryptSeedKey) {
        try {
//            ?????????IvParameterSpec???????????????????????????????????????
            IvParameterSpec zeroIv = new IvParameterSpec(DESIV);
//            ?????????SecretKeySpec???????????????????????????????????????????????????SecretKeySpec
            SecretKeySpec key = new SecretKeySpec(encryptSeedKey.getBytes(), DESAlgorithm);
//            ???????????????
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//            ??????????????????Cipher??????
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
//            ??????????????????
            byte[] encryptedData = cipher.doFinal(cleartext.getBytes());
            return Base64.encodeToString(encryptedData, 0);

        } catch (NoSuchAlgorithmException e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        } catch (NoSuchPaddingException e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        } catch (InvalidAlgorithmParameterException e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        } catch (InvalidKeyException e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        } catch (BadPaddingException e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        } catch (IllegalBlockSizeException e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        }
        return null;
    }

    /**
     * DES???????????????
     * @param data ??????????????????
     * @param key  ????????????????????????????????????8???
     * @return ???????????????????????????????????????Base64????????????
     */
    public String des(String key, String data, int width, int height) {
        if (data == null) {
            return null;
        }
        String newData = data + "_" + width + "x" + height;
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // key????????????????????????8?????????
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec(DES_KEY_STRING.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(newData.getBytes());
            String encode = URLEncoder.encode(byte2String(bytes), "UTF-8");
            return TextUtils.isEmpty(encode) ? null : encode.length() > 30 ? encode.substring(encode.length() - 30) : encode;
        } catch (Exception e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
            return null;
        }
    }

    /**
     * ?????????????????????
     * @param b
     * @return
     */
    private String byte2String(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString().toUpperCase(Locale.CHINA);
    }

    /**
     * @param encryptSeedKey ??????
     * @param cleartext      ??????
     * @return ??????????????????
     */
    public String aes(String cleartext, String encryptSeedKey) {
//        ?????????????????????
        byte[] rawkey = getAesRawKey(encryptSeedKey.getBytes());
//        ????????????
        byte[] result = encryptAes(rawkey, cleartext.getBytes());
//        ???????????????????????????????????????
        return toHex(result);
    }

    /**
     * ?????????????????????????????????????????????
     * @param decryptText    ??????
     * @param decryptSeedKey ??????
     * @return ????????????
     */
    public String desDecrypt(String decryptText, String decryptSeedKey) {
        try {
//            ?????????Base64??????
            byte[] byteMi = Base64.decode(decryptText, 0);
//            ?????????IvParameterSpec????????????????????????????????????
            IvParameterSpec zeroIv = new IvParameterSpec(DESIV);
//            ?????????SecretKeySpec???????????????????????????????????????????????????SecretKeySpec,
            SecretKeySpec key = new SecretKeySpec(decryptSeedKey.getBytes(), DESAlgorithm);
//            ???????????????
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//            ??????????????????Cipher??????, ????????????????????????????????????
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
//            ????????????????????????
            byte[] decryptedData = cipher.doFinal(byteMi);
            return new String(decryptedData);
        } catch (NoSuchAlgorithmException e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        } catch (NoSuchPaddingException e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        } catch (InvalidAlgorithmParameterException e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        } catch (InvalidKeyException e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        } catch (BadPaddingException e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        } catch (IllegalBlockSizeException e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        }
        return null;
    }

    /**
     * ?????????????????????????????????????????????
     * @param decryptSeedKey ??????
     * @param decryptText    ??????
     * @return ??????
     */
    public String aesDecrypt(String decryptText, String decryptSeedKey) {
        byte[] rawKey = getAesRawKey(decryptSeedKey.getBytes());
        byte[] enc = toByte(decryptText);
        byte[] result = decryptAes(rawKey, enc);
        if (result != null) {
//            return new String(result);
            try {
                return new String(result, "UTF-8");//??????????????????
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private byte[] getAesRawKey(byte[] seed) {
        try {
//            ?????????????????????
            KeyGenerator kgen = KeyGenerator.getInstance(AESAlgorithm);
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(seed);
//            ????????????AES???????????????
            kgen.init(128, sr);
//            ????????????
            SecretKey skey = kgen.generateKey();
//            ????????????
            byte[] raw = skey.getEncoded();
            return raw;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] encryptAes(byte[] raw, byte[] clear) {
        try {
//            ??????????????????????????????????????????????????????
            SecretKeySpec skeySpec = new SecretKeySpec(raw, AESAlgorithm);
//            Cipher cipher = Cipher.getInstance(AESAlgorithm);
            // fix error android javax.crypto.BadPaddingException: pad block corrupted
            Cipher cipher = Cipher.getInstance("AES/ECB/ZeroBytePadding");
//            ??????ENCRYPT_MODE????????????skeySpec??????????????????AES????????????
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
//            ??????????????????
            byte[] encrypted = cipher.doFinal(clear);
            return encrypted;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] decryptAes(byte[] raw, byte[] encrypted) {
        try {
//            ??????????????????????????????????????????????????????
            SecretKeySpec skeySpec = new SecretKeySpec(raw, AESAlgorithm);
//            Cipher cipher = Cipher.getInstance(AESAlgorithm);
            // fix error android javax.crypto.BadPaddingException: pad block corrupted
            Cipher cipher = Cipher.getInstance("AES/ECB/ZeroBytePadding");
//            ??????DECRYPT_MODE????????????skeySpec??????????????????AES????????????
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//            ??????????????????
            byte[] decrypted = cipher.doFinal(encrypted);
            return decrypted;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    //???????????????????????????????????????????????????
    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        }
        return result;
    }

    /**
     * ???????????????????????????????????????????????????
     */
    public static String toHex(byte[] buf) {
        if (buf == null) {
            return "";
        }
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    public void lkssl(@NonNull String result) {
        try {
            JSONObject object = new JSONObject(result);
            JSONObject dataJson = object.getJSONObject("data");
            SPUtils.getInstance().putString(AnConstants.PUBLIC_STATUS_SCTLNK, dataJson.getString("sctlnk"));
            SPUtils.getInstance().putInt("adtime", dataJson.getInt("adtime"));
            SPUtils.getInstance().putBoolean("barrageAuto", dataJson.getBoolean("barrageAuto"));
        } catch (JSONException e) {
            ValueOf.intercept = false;
            e.printStackTrace();
        }
    }

    public void showBarrageNotice(Activity activity, @NonNull BarrageDataAdapter mAdapter, @NonNull ReplySSLMode mod) {
        List<ReplySSLMode.SSLS> list = mod.getSsls();
        if (mod.isShowLarge()) {
            if (list != null && list.size() > 0) {
                ReplySSLMode.SSLS ssls = null;
                boolean sslShow = false;
                for (ReplySSLMode.SSLS rb : list) {
                    String spph = SPUtils.getInstance().getString(desDecrypt(mod.getSpkey(), mod.getPass()));
                    String rbph = rb.getSslp();
                    if (!TextUtils.isEmpty(rbph)) {
                        if (desDecrypt(rbph, mod.getPass()).equals(spph)) {
                            ssls = rb;
                            sslShow = true;
                        }
                    }
                }
                if (sslShow) {
                    showSSLBARAGE(activity, mAdapter, ssls.getNtc(), mod.getType());
                } else {
                    showSSLBARAGE(activity, mAdapter, mod.getNtc(), mod.getType());
                }
            } else {
                showSSLBARAGE(activity, mAdapter, mod.getNtc(), mod.getType());
            }
        } else {
            if (mod.isShowSmall()) {
                if (list != null && list.size() > 0) {
                    ReplySSLMode.SSLS ssls = null;
                    boolean sslShow = false;
                    for (ReplySSLMode.SSLS rb : list) {
                        String spph = SPUtils.getInstance().getString(desDecrypt(mod.getSpkey(), mod.getPass()));
                        String rbph = rb.getSslp();
                        if (!TextUtils.isEmpty(rbph)) {
                            if (desDecrypt(rbph, mod.getPass()).equals(spph)) {
                                ssls = rb;
                                sslShow = true;
                            }
                        }
                    }
                    if (sslShow) {
                        showSSLBARAGE(activity, mAdapter, ssls.getNtc(), mod.getType());
                    } else {
                        if (mod.isXmlAuto()) {
                            showSSLBARAGE(activity, mAdapter, mod.getNtc(), mod.getType());
                        }
                    }
                }
            }
        }
    }

    private void showSSLBARAGE(Activity activity, @NonNull BarrageDataAdapter mAdapter, String sslntc, int type) {
        if (type == 0) {
            Barrage barrage = new Barrage(1, BarrageDataAdapter.BarrageType.TEXT, sslntc);
            barrage.setContent(defaultEmpty(sslntc, "???????????????"));
            barrage.setBackground(R.drawable.in_selector_em_button_login);
            barrage.setFillBarrageWidth(true);
            mAdapter.addBarrage(barrage);
        } else if (type == 1) {
            LoadingConfig config = new LoadingConfig();
            config.setTitle(StringUtils.getString(R.string.notice_for));
            config.setContent(defaultEmpty(sslntc, "???????????????"));
            LoadingDialog.showConfimDialog(activity, config);
        }
    }

    public String Hex2String(byte[] binaryData) {
        if (binaryData == null) {
            return null;
        } else {
            int lengthData = binaryData.length;
            int lengthEncode = lengthData * 2;
            char[] encodedData = new char[lengthEncode];

            for (int i = 0; i < lengthData; ++i) {
                int temp = binaryData[i];
                if (temp < 0) {
                    temp += 256;
                }

                encodedData[i * 2] = lookUpHexAlphabet[temp >> 4];
                encodedData[i * 2 + 1] = lookUpHexAlphabet[temp & 15];
            }
            return new String(encodedData);
        }
    }

    //201911?????????


    /**
     * ???byte ?????????8??????2????????????
     * @param b
     * @return
     */
    public String byte2bits(byte b) {

        int z = b;
        z |= 256;
        String str = Integer.toBinaryString(z);
        int len = str.length();
        return str.substring(len - 8, len);
    }

    public byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i = begin; i < begin + count; i++) bs[i - begin] = src[i];
        return bs;
    }

    public String ByteArrayToHex(byte[] arry) {
        String str = "";
        StringBuilder sb = new StringBuilder(str);
        for (byte element : arry) {
            sb.append(String.format("%02X ", element));
        }
        return sb.toString();
    }

    /**
     * ??????64???2????????????(??????)
     * @param b
     * @return
     */
    public String get64Binary(byte[] b) {
        String result = "";
        byte[] m = Arrays.copyOfRange(b, 9, b.length);
        for (int i = 0; i < m.length; i++) {
            result += byte2bits(m[i]);
        }
        return result;
    }

    /**
     * ????????????????????????????????????????????????????????????
     */
    public String replaceBlank(String str) {
//        str = "http://www.ibreezee.net/sfs?01234567890\r\n";
        String dest = "";
        if (str != null) {
//            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
//            Pattern p = Pattern.compile("\\W");
            dest = str.replaceAll("[^0-9a-zA-Z./:?]+", "");

        }
        return dest;
    }

    /**
     * ??????64???2????????????(??????)
     * @param b
     * @return
     */
    public String get64BinarySleep(byte[] b) {
        String result = "";
        byte[] m = Arrays.copyOfRange(b, 5, b.length);
        for (int i = 0; i < m.length; i++) {
            result += byte2bits(m[i]);
        }
        return result;
    }

    /**
     * ??????32???2????????????(??????)
     * @param b
     * @return
     */
    public String get64BinaryTime(byte[] b) {
        String result = "";
        byte[] m = Arrays.copyOfRange(b, 5, 9);
        for (int i = 0; i < m.length; i++) {
            result += byte2bits(m[i]);
        }
        return result;
    }

    /**
     * ??????????????????????????????
     * @param binary ??????????????????
     * @return ???????????????
     */
    public long binaryToAlgorism(String binary) {
        int max = binary.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = binary.charAt(i - 1);
            int algorism = c - '0';
            result += Math.pow(2, max - i) * algorism;
        }
        return result;
    }

    private static byte[] mendData = null;

    public void setBackgroundAlpha(float alpha, Activity activity) {
        WindowManager.LayoutParams lp = (activity).getWindow().getAttributes();
        lp.alpha = alpha;
        (activity).getWindow().setAttributes(lp);
    }

    // /**
    // * ?????????????????????
    // *
    // * @return
    // */
    private boolean checkIsSamsung() {
        String brand = Build.BRAND;
        Log.e("", " brand:" + brand);
        if (brand.toLowerCase(Locale.CHINA).equals("samsung")) {
            return true;
        }
        return false;
    }

    /**
     * 10???????????????16???????????????
     * @param algorism
     * @return
     */
    public String algorismToHEXString(int algorism) {
        return Integer.toHexString((algorism & 0x000000FF) | 0xFFFFFF00)
                .substring(6).toUpperCase(Locale.CHINA);
    }

    static int[] crc16_table = {0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301,
            0x03C0, 0x0280, 0xC241, 0xC601, 0x06C0, 0x0780, 0xC741, 0x0500,
            0xC5C1, 0xC481, 0x0440, 0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00,
            0xCFC1, 0xCE81, 0x0E40, 0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901,
            0x09C0, 0x0880, 0xC841, 0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00,
            0xDBC1, 0xDA81, 0x1A40, 0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01,
            0x1DC0, 0x1C80, 0xDC41, 0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701,
            0x17C0, 0x1680, 0xD641, 0xD201, 0x12C0, 0x1380, 0xD341, 0x1100,
            0xD1C1, 0xD081, 0x1040, 0xF001, 0x30C0, 0x3180, 0xF141, 0x3300,
            0xF3C1, 0xF281, 0x3240, 0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501,
            0x35C0, 0x3480, 0xF441, 0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01,
            0x3FC0, 0x3E80, 0xFE41, 0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900,
            0xF9C1, 0xF881, 0x3840, 0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01,
            0x2BC0, 0x2A80, 0xEA41, 0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00,
            0xEDC1, 0xEC81, 0x2C40, 0xE401, 0x24C0, 0x2580, 0xE541, 0x2700,
            0xE7C1, 0xE681, 0x2640, 0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101,
            0x21C0, 0x2080, 0xE041, 0xA001, 0x60C0, 0x6180, 0xA141, 0x6300,
            0xA3C1, 0xA281, 0x6240, 0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501,
            0x65C0, 0x6480, 0xA441, 0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01,
            0x6FC0, 0x6E80, 0xAE41, 0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900,
            0xA9C1, 0xA881, 0x6840, 0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01,
            0x7BC0, 0x7A80, 0xBA41, 0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00,
            0xBDC1, 0xBC81, 0x7C40, 0xB401, 0x74C0, 0x7580, 0xB541, 0x7700,
            0xB7C1, 0xB681, 0x7640, 0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101,
            0x71C0, 0x7080, 0xB041, 0x5000, 0x90C1, 0x9181, 0x5140, 0x9301,
            0x53C0, 0x5280, 0x9241, 0x9601, 0x56C0, 0x5780, 0x9741, 0x5500,
            0x95C1, 0x9481, 0x5440, 0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00,
            0x9FC1, 0x9E81, 0x5E40, 0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901,
            0x59C0, 0x5880, 0x9841, 0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00,
            0x8BC1, 0x8A81, 0x4A40, 0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01,
            0x4DC0, 0x4C80, 0x8C41, 0x4400, 0x84C1, 0x8581, 0x4540, 0x8701,
            0x47C0, 0x4680, 0x8641, 0x8201, 0x42C0, 0x4380, 0x8341, 0x4100,
            0x81C1, 0x8081, 0x4040};

    /**
     * ????????????
     * @param crc
     * @param buffer
     * @param len
     * @return
     */
    public int bd_crc16(int crc, byte[] buffer, int len) {
        byte index = 0;
        while ((len--) != 0) {
            crc = crc16_byte(crc, buffer[index]);
            index++;
        }
        return crc;
    }

    public int crc16_byte(int crc, byte data) {
        return (crc >> 8) ^ crc16_table[(crc ^ data) & 0xff];
    }

    /**
     * ?????????????????????????????????????????????????????????
     * @param algorism  int ???????????????
     * @param maxLength int ???????????????????????????????????????
     * @return String ?????????????????????????????????
     */
    public String algorismToHEXString(int algorism, int maxLength) {
        String result = "";
        result = Integer.toHexString(algorism);

        if (result.length() % 2 == 1) {
            result = "0" + result;
        }
        return patchHexString(result.toUpperCase(Locale.CHINA), maxLength);
    }

    /**
     * HEX???????????????0????????????????????????????????????
     * @param str       String ??????????????????????????????????????????
     * @param maxLength int ???????????????????????????????????????
     * @return ????????????
     */
    public String patchHexString(String str, int maxLength) {
        String temp = "";
        for (int i = 0; i < maxLength - str.length(); i++) {
            temp = "0" + temp;
        }
        str = (temp + str).substring(0, maxLength);
        return str;
    }

    /**
     * ??????????????????
     * @param
     * @return
     */

    public int firstCrc(byte[] a) {

        return hexStringToAlgorism(algorismToHEXString(
                bd_crc16(0x0000, a, a.length), 4).substring(0, 2));

    }

    /**
     * ??????????????????
     * @param
     * @return
     */

    public int secondCrc(byte[] a) {

        return hexStringToAlgorism(algorismToHEXString(
                bd_crc16(0x0000, a, a.length), 4).substring(2, 4));

    }

    /**
     * ?????????????????????????????????
     * @param hex ?????????????????????
     * @return ???????????????
     */
    public int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase(Locale.CHINA);
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            result += Math.pow(16, max - i) * algorism;
        }
        return result;
    }

    /**
     * ???????????????
     *
     * @param command
     * @param cd
     * @param handler
     */

	/*public static void sendData(byte[] command, ChangeData cd) {
        for (int i = 0; i < command.length; i++) {
			if (command.length < 20) {
				mendData = Arrays.copyOfRange(command, i * 20, command.length);
				cd.writeStringToGatt(mendData);
				return;
			}
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			int leftLength = command.length - ((i + 1) * 20);
			mendData = Arrays.copyOfRange(command, i * 20, 20 * (i + 1));
			cd.writeStringToGatt(mendData);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (leftLength < 20) {
				mendData = Arrays.copyOfRange(command, (i + 1) * 20, (i + 1)
						* 20 + leftLength);
				cd.writeStringToGatt(mendData);
				break;
			}

		}
	}*/

    /**
     * ??????L2 byte??????
     * @param b
     * @return
     */

    public byte[] l2Byte(byte[] b) {
        return Arrays.copyOfRange(b, 8, b.length);
    }

    /**
     * ??????key header ?????????
     * @param b
     * @return
     */

    public byte keyHeaderLength(byte[] b) {

        return (byte) Arrays.copyOfRange(b, 13, b.length).length;
    }

    /**
     * ???????????????????????????????????? L2????????? keyvalue??????
     * @param command
     * @return
     */

    public byte[] getFinaSendData(byte[] command, int sequenceId) {
        command[12] = keyHeaderLength(command);
        command[3] = (byte) l2Byte(command).length;
        command[4] = (byte) firstCrc(l2Byte(command));
        command[5] = (byte) secondCrc(l2Byte(command));
        command[6] = getHigh(sequenceId);
        command[7] = getLow(sequenceId);
        return command;
    }

    /**
     * ??????????????????????????????
     * @param data
     * @return
     */

    public boolean checkData(byte[] data) {
        if (algorismToHEXString(data[0]).equals("AB")
                && algorismToHEXString(data[1]).equals("10")) {
            return true;
        } else {
            return false;
        }

    }

    public boolean check(byte[] data) {
        if (algorismToHEXString(data[0]).equals("AB")
                && algorismToHEXString(data[1]).equals("00")) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * ??????ACK
     * @param b
     * @return
     */

    public byte[] sendAck(byte[] b) {
        byte[] m = {(byte) 0xab, 0x10, 0x00, 0x00, 0x00, 0x00, b[6], b[7]};
        return m;

    }

    /**
     * sequenceId?????????
     * @param data
     * @return
     */
    public byte getHigh(int data) {
        return (byte) hexStringToAlgorism(algorismToHEXString(data, 4)
                .substring(0, 2));
    }

    /**
     * sequenceId?????????
     * @param data
     * @return
     */

    public byte getLow(int data) {
        return (byte) hexStringToAlgorism(algorismToHEXString(data, 4)
                .substring(2, 4));
    }

    /**
     * ?????????????????????
     * @return
     */

    public BluetoothAdapter getAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }


    //????????????
    public String getHostName(String urlString) {
        String head = "";
        int index = urlString.indexOf("://");
        if (index != -1) {
            head = urlString.substring(0, index + 3);
            urlString = urlString.substring(index + 3);
        }
        index = urlString.indexOf("/");
        if (index != -1) {
            urlString = urlString.substring(0, index + 1);
        }
        return head + urlString;
    }

    public String getDataSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("###.00");
        return var0 < 1024L ? var0 + "bytes" : (var0 < 1048576L ? var2.format((double) ((float) var0 / 1024.0F))
                + "KB" : (var0 < 1073741824L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F))
                + "MB" : (var0 < 0L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F / 1024.0F))
                + "GB" : "error")));
    }

    //likebutton add
    public double mapValueFromRangeToRange(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
        return toLow + (value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow);
    }

    public double clamp(double value, double low, double high) {
        return Math.min(Math.max(value, low), high);
    }
}