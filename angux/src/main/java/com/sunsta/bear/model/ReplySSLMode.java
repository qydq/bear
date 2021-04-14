package com.sunsta.bear.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReplySSLMode implements Serializable {
    private static final long serialVersionUID = 4143429237644708086L;
    private boolean showSmall;
    private boolean showLarge;
    private boolean xmlAuto;
    private int type;
    private String ntc;
    private String spkey;
    private String pass;
    private List<SSLS> ssls = new ArrayList<>();

    public boolean isXmlAuto() {
        return xmlAuto;
    }

    public String getSpkey() {
        return spkey;
    }

    public void setSpkey(String spkey) {
        this.spkey = spkey;
    }

    public void setXmlAuto(boolean xmlAuto) {
        this.xmlAuto = xmlAuto;
    }

    public boolean isShowSmall() {
        return showSmall;
    }

    public void setShowSmall(boolean showSmall) {
        this.showSmall = showSmall;
    }

    public boolean isShowLarge() {
        return showLarge;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setShowLarge(boolean showLarge) {
        this.showLarge = showLarge;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNtc() {
        return ntc;
    }

    public void setNtc(String ntc) {
        this.ntc = ntc;
    }

    public List<SSLS> getSsls() {
        return ssls;
    }

    public void setSsls(List<SSLS> ssls) {
        this.ssls = ssls;
    }

    public static class SSLS implements Serializable {
        private static final long serialVersionUID = -7296304074592996517L;
        private String sslp;
        private String ntc;

        public String getSslp() {
            return sslp;
        }

        public void setSslp(String sslp) {
            this.sslp = sslp;
        }

        public String getNtc() {
            return ntc;
        }

        public void setNtc(String ntc) {
            this.ntc = ntc;
        }
    }
}
