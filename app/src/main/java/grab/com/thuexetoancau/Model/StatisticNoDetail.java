package grab.com.thuexetoancau.Model;

/**
 * Created by DatNT on 3/25/2017.
 */

public class StatisticNoDetail {
    private String carNumber;
    private double sum;
    private int count;

    public StatisticNoDetail() {
    }

    public StatisticNoDetail(String carNumber, double sum, int count) {
        this.carNumber = carNumber;
        this.sum = sum;
        this.count = count;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
