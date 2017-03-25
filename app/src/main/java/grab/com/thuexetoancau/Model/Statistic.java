package grab.com.thuexetoancau.Model;

/**
 * Created by DatNT on 3/24/2017.
 */

public class Statistic {
    private int id;
    private String type;
    private long date;
    private int money;
    private String note;

    public Statistic() {
    }

    public Statistic(int id, String type, long date, int money, String note) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.money = money;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
