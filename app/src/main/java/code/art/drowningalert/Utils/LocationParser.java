package code.art.drowningalert.Utils;


import java.util.StringTokenizer;

public class LocationParser {
    public static final int LONGITUDE = 0;
    public static final int LATITUDE =1;
//    public static String parseLocation(String orl){
//        StringTokenizer st = new StringTokenizer(orl,",");
//        st.nextToken();
//        st.nextToken();
//        String orLatitude =st.nextToken();
//        st.nextToken();
//        String orLongtitude = st.nextToken();
//        orLatitude = orLatitude.substring(0,4)+"."+orLatitude.substring(5);
//        orLongtitude=   orLongtitude.substring(0,5)+"."+orLongtitude.substring(6);
//        orLatitude = orLatitude.replaceAll("[^0-9.]","0");
//        orLongtitude = orLongtitude.replaceAll("[^0-9.]","0");
//
//        String latitude = DM2D(orLatitude,LATITUDE);
//        String longitude =DM2D(orLongtitude,LONGITUDE);
//        return new String("{\"latitude\":\""+latitude+"\",\"longitude\":\""+longitude+"\"}");
//    }
//    public static String DM2D(String orl,int flag){
//        String sDegree;
//        String sMinute;
//        if(flag != LONGITUDE){
//            sDegree = orl.substring(0,2);
//            sMinute = orl.substring(2,4)+orl.substring(5,9);
//
//        }
//        else{
//            sDegree = orl.substring(0,3);
//            sMinute = orl.substring(3,5)+orl.substring(6,10);
//        }
//        double result = Double.parseDouble(sDegree)+Double.parseDouble(sMinute)/600000;
//        return String.valueOf(result).substring(0,9);
//
//    }

    public static double parse(String loc,int flag){
        String sDegree="0";
        String sMinute="0";
        double degree;
        double minute;
        switch (flag){
            case LONGITUDE:
                sDegree=loc.substring(0,3);
                sMinute=loc.substring(3);
                break;
            case LATITUDE:
                sDegree= loc.substring(0,2);
                sMinute = loc.substring(2);
                break;
        }
        degree = Double.parseDouble(sDegree);
        minute = Double.parseDouble(sMinute);
        return degree+minute/60;

    }


}
