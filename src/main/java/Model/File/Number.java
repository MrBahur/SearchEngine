package Model.File;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Number extends Phrase {
    private double value;
    private String sign;


    public Number(double value, String sign) {
        this.sign = sign;
        this.value = value;
    }

    public Number(double value) {
        this(value, null);
    }

    @Override
    public String toString() {
        String str = Double.toString(this.value);
        double num = this.value;
        if (sign == null) {
            if (this.value > 999 && this.value < 1000000) {
                num = num / 1000;
                DecimalFormat d = new DecimalFormat("###.###");
                d.setRoundingMode(RoundingMode.DOWN);
                str = d.format(num) + "k";
            }
            if (this.value > 999999 && this.value < 1000000000) {
                num = num / 1000000;
                DecimalFormat d = new DecimalFormat("###.###");
                d.setRoundingMode(RoundingMode.DOWN);
                str = d.format(num) + "M";
            }
            if (this.value > 999999999) {
                num = num / 1000000000;
                DecimalFormat d = new DecimalFormat("###.###");
                d.setRoundingMode(RoundingMode.DOWN);
                str = d.format(num) + "B";
            }
        } else if (sign.equals("$")) {
            if (this.value<1000000) {
                str = this.value + " Dollars";
            }
            else {
                num = num / 1000000;
                DecimalFormat d = new DecimalFormat("###.###");
                d.setRoundingMode(RoundingMode.DOWN);
                str = d.format(num) + "M";
            }

        } else if (sign.equals("%")) {
            str = this.value + this.sign;
        }
        return str;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Number)) {
            return false;
        }
        Number x = (Number) other;
        return this.sign.equals(x.sign) && this.value == x.value;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(this.value);
    }

    public static void main(String[] args) {

    }
}
