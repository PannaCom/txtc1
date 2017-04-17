package grab.com.thuexetoancau.Model;

/**
 * Created by DatNT on 4/17/2017.
 */

public class Salary {
    private int id;
    private String carNumber;
    private String driverName;
    private int money;
    private String bankNumber;
    private String bankName;
    private String fromDate;
    private String toDate;

    public Salary() {
    }

    public Salary(int id, String carNumber, String driverName, int money, String bankNumber, String bankName, String fromDate, String toDate) {
        this.id = id;
        this.carNumber = carNumber;
        this.driverName = driverName;
        this.money = money;
        this.bankNumber = bankNumber;
        this.bankName = bankName;
        this.fromDate = fromDate;
        this.toDate = toDate;
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

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
