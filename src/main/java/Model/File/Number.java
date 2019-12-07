package Model.File;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Number extends Term {
    private double value;
    private String sign;


    public Number(double value, String sign) {
        this.sign = sign;
        this.value = value;
    }

    public Number(double value) {
        this(value, "#");
    }

    @Override
    public String toString() {
        String str = Double.toString(this.value);
        double num = this.value;
        if (sign.equals("#")) {
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
                if (num / 1000000 > 999999) {
                    num = num / 1000000;
                    str = d.format(num) + "M";
                }
                else if (num / 1000 > 999) {
                    num = num / 1000;
                    str = d.format(num) + "K";
                }
                else {
                    str = d.format(num);
                }
                str = str + "B";
            }
        } else if (sign.equals("$")) {
            if (this.value < 1000000) {
                if (str.charAt(str.indexOf('.') + 1) == '0' && (str.length() - 1) == (str.indexOf('.') + 1)) {
                    str = str.substring(0, str.indexOf('.')) + " Dollars";
                } else {
                    str = this.value + " Dollars";
                }
            } else {
                num = num / 1000000;
                DecimalFormat d = new DecimalFormat("###.###");
                d.setRoundingMode(RoundingMode.DOWN);
                str = d.format(num) + " M Dollars";
            }

        } else if (sign.equals("%")) {
            str = Double.toString(this.value);
            if (str.charAt(str.indexOf('.') + 1) == '0' && (str.length() - 1) == (str.indexOf('.') + 1)) {
                str = str.substring(0, str.indexOf('.')) + this.sign;
            } else {
                str = this.value + this.sign;
            }
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
        Number n = new Number(1820000000, "#");
        System.out.println(n.toString());
    }
}
