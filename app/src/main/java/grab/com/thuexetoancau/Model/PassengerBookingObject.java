package grab.com.thuexetoancau.Model;

/**
 * Created by DatNT on 12/9/2016.
 */

public class PassengerBookingObject {
    private int id;
    private int bookingId;
    private int phone;
    private String name;
    private String dateTimeOrder;

    public PassengerBookingObject() {
    }

    public PassengerBookingObject(int id, int bookingId, int phone, String name, String dateTimeOrder) {
        this.id = id;
        this.bookingId = bookingId;
        this.phone = phone;
        this.name = name;
        this.dateTimeOrder = dateTimeOrder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTimeOrder() {
        return dateTimeOrder;
    }

    public void setDateTimeOrder(String dateTimeOrder) {
        this.dateTimeOrder = dateTimeOrder;
    }
}
