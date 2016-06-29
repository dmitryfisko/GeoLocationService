package com.yandex.testapp.location.network;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class WifiAndCellCollector extends PhoneStateListener implements Runnable {

    private static final String[] lbsPostName = new String[]{"xml"};
    private static final String[] lbsContentType = new String[]{"xml"};

    public static final String PROTOCOL_VERSION = "1.0";
    public static final String API_KEY = "AGvtc1cBAAAAiZ8gTAIAsxhAHh6worFbg2SUqjhVBsG5DwY" +
            "AAAAAAAAAAAAE7HlRvOLnLO4ppCwO2HqGlK56Mg==";

    public static final String LBS_API_HOST = "http://api.lbs.yandex.net/geolocation";

    public static final String GSM = "gsm";
    public static final String CDMA = "cdma";

    private static final long WIFI_SCAN_TIMEOUT = 30000;
    private static long mRequestsInterval;

    private NetworkLocationListener listener;
    private TelephonyManager tm;

    private String radioType;
    private String mcc;
    private String mnc;
    private List<CellInfo> cellInfos;
    private int cellId, lac, signalStrength;

    private WifiManager wifi;
    private long lastWifiScanTime;
    private List<WifiInfo> wifiInfos;

    private volatile boolean isRun;

    public static Map<Integer,String> networkTypeStr;
    static {
        networkTypeStr = new HashMap<>();
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_GPRS, "GPRS");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_EDGE, "EDGE");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_UMTS, "UMTS");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_HSDPA, "HSDPA");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_HSUPA, "HSUPA");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_HSPA, "HSPA");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_CDMA, "CDMA");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_EVDO_0, "EVDO_0");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_EVDO_A, "EVDO_A");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_1xRTT, "1xRTT");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_IDEN, "IDEN");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_UNKNOWN, "UNKNOWN");
    }

    public WifiAndCellCollector(Context context,
                                long requestsInterval,
                                NetworkLocationListener listener) {
        this.listener = listener;
        mRequestsInterval = requestsInterval;
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            radioType = getRadioType(tm.getNetworkType());
            String mccAndMnc = tm.getNetworkOperator();
            cellInfos = new ArrayList<>();
            if (mccAndMnc != null && mccAndMnc.length() > 3) {
                mcc = mccAndMnc.substring(0, 3);
                mnc = mccAndMnc.substring(3);
            } else {
                mcc = mnc = null;
            }
        }

        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy:HHmmss", Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        wifiInfos = new ArrayList<>();
        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        lastWifiScanTime = 0;
    }

    public void startCollect() {
        isRun = true;
        if (tm != null) {
            tm.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTH |
                    PhoneStateListener.LISTEN_CELL_LOCATION |
                    PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        }
        (new Thread(this)).start();
    }

    public void stopCollect() {
        isRun = false;
        if (tm != null) {
            tm.listen(this, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public void run() {
        while (isRun) {
            collectWifiInfo();
            collectCellInfo();
            requestMyLocation();
            try {
                Thread.sleep((long) (mRequestsInterval * 0.75));
            } catch (InterruptedException ignored) {}
        }
    }
    
    public void collectWifiInfo() {
        wifiInfos.clear();
        if (wifi != null && wifi.isWifiEnabled()) {
            List<ScanResult> wifiNetworks = wifi.getScanResults();
            if (wifiNetworks != null && wifiNetworks.size() > 0) {
                for (ScanResult net:wifiNetworks) {
                    WifiInfo info = new WifiInfo();
                    info.mac = net.BSSID.toUpperCase();
                    info.signalStrength = net.level;
                    wifiInfos.add(info);
                }
            }
            
            long currentTime = System.currentTimeMillis();
            if (lastWifiScanTime > currentTime) {
                lastWifiScanTime = currentTime;
            } else if (currentTime - lastWifiScanTime > WIFI_SCAN_TIMEOUT) {
                lastWifiScanTime = currentTime;
                wifi.startScan();
            }
        }
    }
    
    @SuppressWarnings("rawtypes")
    private static final Class[] emptyParamDesc = new Class[]{};
    private static final Object[] emptyParam = new Object[]{};
    
    public void collectCellInfo() {
        if (tm == null) {
            return;
        }
        cellInfos.clear();
        List<NeighboringCellInfo> cellList = tm.getNeighboringCellInfo();
        for (NeighboringCellInfo cell : cellList) {
            int cellId = cell.getCid();
            int lac = NeighboringCellInfo.UNKNOWN_CID;
            try {
                // Since: API Level 5
                Method getLacMethod = NeighboringCellInfo.class.getMethod("getLac", emptyParamDesc);
                if (getLacMethod != null) {
                    lac = (Integer) getLacMethod.invoke(cell, emptyParam);
                }
            } catch (Throwable ignored) {
            }
            
            int signalStrength = cell.getRssi();//since 1.5
            int psc = NeighboringCellInfo.UNKNOWN_CID;
            if (cellId == NeighboringCellInfo.UNKNOWN_CID) {
                try {
                    // Since: API Level 5
                    Method getPscMethod = NeighboringCellInfo.class.getMethod("getPsc", emptyParamDesc);
                    if (getPscMethod != null) {
                        psc = (Integer) getPscMethod.invoke(cell, emptyParam);
                    }
                } catch (Throwable ignored) {
                }
                cellId = psc;
            }
            
            if (cellId != NeighboringCellInfo.UNKNOWN_CID) {
                String sLac = (lac != NeighboringCellInfo.UNKNOWN_CID) ? String.valueOf(lac) : "";
                String sSignalStrength = "";
                if (signalStrength != NeighboringCellInfo.UNKNOWN_RSSI) {
                    if (GSM.equals(radioType)) {
                        sSignalStrength = String.valueOf(-113 + 2 * signalStrength);
                    } else {
                        sSignalStrength = String.valueOf(signalStrength);
                    }
                }

                CellInfo info = new CellInfo();
                info.cellId = cellId;
                info.lac = sLac;
                info.signalStrength = sSignalStrength;
                cellInfos.add(info);
            }
        }
    }

    @Override
    public void onSignalStrengthChanged(int asu) {
        signalStrength = -113 + 2 * asu;
    }
    
    @Override
    public void onCellLocationChanged(CellLocation location) {
      if (location != null) {
          if (location instanceof GsmCellLocation) {
              GsmCellLocation gsmLoc = (GsmCellLocation) location;
              lac = gsmLoc.getLac();
              cellId = gsmLoc.getCid();
          }
      }
    }

    public void requestMyLocation() {
        String xmlRequest = generateRequestLbsXml();
        byte[] request = HttpConnector.encodeMIME(lbsPostName, lbsContentType,
                new byte[][]{xmlRequest.getBytes()});
        byte[] response = HttpConnector.doRequest(LBS_API_HOST, request);
        NetworkLocationInfo location = NetworkLocationInfo.parseByteData(response);
        if (listener != null) {
            listener.onNetworkLocationChanged(location);
        }
    }
    
    private synchronized String generateRequestLbsXml() {
        StringBuilder xml = new StringBuilder(200);
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
        xml.append("<ya_lbs_request>");
        xml.append("<common>");
            xml.append("<version>").append(PROTOCOL_VERSION).append("</version>");
            xml.append("<radio_type>").append(radioType).append("</radio_type>");
            xml.append("<api_key>").append(API_KEY).append("</api_key>");
        xml.append("</common>");
        
        if (mcc != null && mnc != null) {
            xml.append("<gsm_cells>");
            
            if (cellInfos != null && cellInfos.size() > 0) {
                for (CellInfo info:cellInfos) {
                    xml.append("<cell>");
                    xml.append("<countrycode>").append(mcc).append("</countrycode>");
                    xml.append("<operatorid>").append(mnc).append("</operatorid>");
                    xml.append("<cellid>").append(info.cellId).append("</cellid>");
                    xml.append("<lac>").append(info.lac).append("</lac>");
                    xml.append("<signal_strength>").append(info.signalStrength).append("</signal_strength>");
                    xml.append("</cell>");
                }
            } else {
                xml.append("<cell>");
                xml.append("<countrycode>").append(mcc).append("</countrycode>");
                xml.append("<operatorid>").append(mnc).append("</operatorid>");
                xml.append("<cellid>").append(cellId).append("</cellid>");
                xml.append("<lac>").append(lac).append("</lac>");
                xml.append("<signal_strength>").append(signalStrength).append("</signal_strength>");
                xml.append("</cell>");
            }
            
            xml.append("</gsm_cells>");
        }
        
        if (wifiInfos != null && wifiInfos.size() > 0) {
            xml.append("<wifi_networks>");
            for (WifiInfo info:wifiInfos) {
                xml.append("<network>");
                xml.append("<mac>").append(info.mac).append("</mac>");
                xml.append("<signal_strength>").append(info.signalStrength).append("</signal_strength>");
                xml.append("</network>");
            }
            xml.append("</wifi_networks>");
        }

        xml.append("</ya_lbs_request>");
        return xml.toString();
    }
    
    private String getRadioType(int networkType) {
        switch (networkType) {
            case -1:
                return "NONE";
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return GSM;
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return CDMA;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
                return "UNKNOWN";
        }
    }

    protected static final boolean[] WWW_FORM_URL = new boolean[256];

    // Static initializer for www_form_url
    static {
        // alpha characters
        for (int i = 'a'; i <= 'z'; i++) {
            WWW_FORM_URL[i] = true;
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            WWW_FORM_URL[i] = true;
        }
        // numeric characters
        for (int i = '0'; i <= '9'; i++) {
            WWW_FORM_URL[i] = true;
        }
        // special chars
        WWW_FORM_URL['-'] = true;
        WWW_FORM_URL['_'] = true;
        WWW_FORM_URL['.'] = true;
        WWW_FORM_URL['*'] = true;
        // blank to be replaced with +
        WWW_FORM_URL[' '] = true;
    }
    
    private class CellInfo {
        private int cellId;
        private String lac;
        private String signalStrength;
    }
    
    private class WifiInfo {
        private String mac;
        private int signalStrength;
    }
}
