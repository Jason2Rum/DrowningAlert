package code.art.drowningalert.Utils;



public class LocationParser {
    public static final int LONGITUDE = 0;
    public static final int LATITUDE =1;


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
