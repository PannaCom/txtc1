package grab.com.thuexetoancau.Model;

/**
 * Created by DatNT on 4/17/2017.
 */

public class Owe {
    private int id;
    private String carNumber;
    private String driverName;
    private int moneyMonth;
    private String dateMonth;
    private int moneyPeriod;
    private String datePeriod;
    private int moneyYear;
    private String dateYear;
    private String dateTime;

    public Owe() {
    }

    public Owe(int id, String carNumber, String driverName, int moneyMonth, String dateMonth, int moneyPeriod, String datePeriod, int moneyYear, String dateYear, String dateTime) {
        this.id = id;
        this.carNumber = carNumber;
        this.driverName = driverName;
        this.moneyMonth = moneyMonth;
        this.dateMonth = dateMonth;
        this.moneyPeriod = moneyPeriod;
        this.datePeriod = datePeriod;
        this.moneyYear = moneyYear;
        this.dateYear = dateYear;
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public int getMoneyMonth() {
        return moneyMonth;
    }

    public void setMoneyMonth(int moneyMonth) {
        this.moneyMonth = moneyMonth;
    }

    public String getDateMonth() {
        return dateMonth;
    }

    public void setDateMonth(String dateMonth) {
        this.dateMonth = dateMonth;
    }

    public int getMoneyPeriod() {
        return moneyPeriod;
    }

    public void setMoneyPeriod(int moneyPeriod) {
        this.moneyPeriod = moneyPeriod;
    }

    public String getDatePeriod() {
        return datePeriod;
    }

    public void setDatePeriod(String datePeriod) {
        this.datePeriod = datePeriod;
    }

    public int getMoneyYear() {
        return moneyYear;
    }

    public void setMoneyYear(int moneyYear) {
        this.moneyYear = moneyYear;
    }

    public String getDateYear() {
        return dateYear;
    }

    public void setDateYear(String dateYear) {
        this.dateYear = dateYear;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
