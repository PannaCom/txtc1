package grab.com.thuexetoancau.Utilities;


import java.util.ArrayList;

/**
 * Created by DatNT on 6/29/2016.
 */
public class Defines {
    public static  final String HOSTNAME                    = "http://thuexetoancau.vn/";
    public static  final String     URL_GET_CAR_HIRE_TYPE       = HOSTNAME + "api/getCarHireType";
    public static  final String     URL_GET_CAR_SIZE            = HOSTNAME + "api/getCarSize";
    public static  final String     URL_GET_CAR_TYPE            = HOSTNAME + "api/getListCarType";
    public static  final String     URL_GET_CAR_MADE            = HOSTNAME + "api/getCarMadeList";
    public static  final String     URL_GET_CAR_MODEL           = HOSTNAME + "api/getCarModelListFromMade";
    public static  final String     URL_GET_WHO_HIRE            = HOSTNAME + "api/getCarWhoHire ";
    public static  final String     URL_BOOKING_TICKET          = HOSTNAME + "api/booking";
    public static  final String     URL_GET__BOOKING            = HOSTNAME + "api/getBooking";
    public static  final String     URL_PURCHASE                = HOSTNAME + "api/bookingFinal";
    public static  final String     URL_REGISTER_DRIVER         = HOSTNAME + "api/driverRegister";
    public static  final String     URL_GET_AIRPORT             = HOSTNAME + "api/getAirportName";
    public static  final String     URL_GET_LONLAT_AIRPORT      = HOSTNAME + "api/getLonLatAirport";
    public static  final String     URL_LOGIN                   = HOSTNAME + "api/login";

    public static  final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;
    public static  final int REQUEST_CODE_LOCATION_PERMISSIONS = 234;
    public static  final int REQUEST_CODE_COARSE_LOCATION_PERMISSIONS = 345;


    public static  final String VEHICLE_PASS_ACTION         = "1";
    public static  final String CAR_TYPE_FROM_ACTION        = "2";
    public static  final String CAR_MADE_TO_ACTION          = "3";
    public static  final String CAR_MODEL_ACTION            = "4";
    public static  final String CAR_SIZE_ACTION             = "7";
    public static  final String VEHICLE_ID_ACTION           = "5";
    public static  final String OWNER_ID_ACTION             = "6";
    public static  final String CAR_NAME_ACTION             = "8";
    public static String token                 = "";


    public static  final int        LOOP_TIME                   = 5*1000;

    public static String[] carModel = {};
    public static ArrayList<String> provinceFrom;

    public static boolean startThread = false;
}
