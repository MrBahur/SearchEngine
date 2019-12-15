package Model.File;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Number extends Term {
    private double value;
    private String sign;
    private int mone;
    private int mechane;

    public Number (double value, String sign, int mone, int mechane) {
        this.value = value;
        this.sign = sign;
        this.mone = mone;
        this.mechane = mechane;

    }

    public Number(double value, String sign) {
        this(value, sign, 0,0);
    }
    public Number(double value, int mone, int mechane) {
        this(value,"#", mone, mechane);
    }
    public Number(double value) {
        this(value, "#", 0,0);
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
                if (this.mone != 0 && this.mechane != 0) {
                    str = d.format(num) + " " + mone + "/" + mechane + "k";
                }
                else {
                    str = d.format(num) + "k";
                }
            }
            if (this.value > 999999 && this.value < 1000000000) {
                num = num / 1000000;
                DecimalFormat d = new DecimalFormat("###.###");
                d.setRoundingMode(RoundingMode.DOWN);
                if (this.mone != 0 && this.mechane != 0) {
                    str = d.format(num) + " " + mone + "/" + mechane + "M";
                }
                else {
                    str = d.format(num) + "M";
                }
            }
            if (this.value > 999999999) {
                num = num / 1000000000;
                DecimalFormat d = new DecimalFormat("###.###");
                d.setRoundingMode(RoundingMode.DOWN);
                if (num / 1000000 > 999999) {
                    num = num / 1000000;
                    if (this.mone != 0 && this.mechane != 0) {
                        str = d.format(num) + " " + mone + "/" + mechane + "M";
                    }
                    else {
                        str = d.format(num) + "M";
                    }
                }
                else if (num / 1000 > 999) {
                    num = num / 1000;
                    if (this.mone != 0 && this.mechane != 0) {
                        str = d.format(num) + " " + mone + "/" + mechane + "k";
                    }
                    else {
                        str = d.format(num) + "k";
                    }
                }
                else {
                    if (this.mone != 0 && this.mechane != 0) {
                        str = d.format(num) + " " + mone + "/" + mechane;
                    }
                    else {
                        str = d.format(num);
                    }
                }
                str = str + "B";
            }
        } else if (sign.equals("$")) {
            if (this.value < 1000000) {
                if (str.charAt(str.indexOf('.') + 1) == '0' && (str.length() - 1) == (str.indexOf('.') + 1)) {
                    if (this.mone != 0 && this.mechane != 0) {
                        str = str.substring(0, str.indexOf('.')) + " " + mone + "/" + mechane + " Dollars";
                    }
                    else {
                        str = str.substring(0, str.indexOf('.')) + " Dollars";
                    }
                } else {
                    if (this.mone != 0 && this.mechane != 0) {
                        str = this.value + " " + mone + "/" + mechane + " Dollars";
                    }
                    else {
                        str = this.value + " Dollars";
                    }
                }
            } else {
                num = num / 1000000;
                DecimalFormat d = new DecimalFormat("###.###");
                d.setRoundingMode(RoundingMode.DOWN);
                if (this.mone != 0 && this.mechane != 0) {
                    str = d.format(num) + " " + mone + "/" + mechane + " M Dollars";
                }
                else {
                    str = d.format(num) + " M Dollars";
                }
            }

        } else if (sign.equals("%")) {
            str = Double.toString(this.value);
            if (str.charAt(str.indexOf('.') + 1) == '0' && (str.length() - 1) == (str.indexOf('.') + 1)) {
                if (this.mone != 0 && this.mechane != 0) {
                    str = str.substring(0, str.indexOf('.')) + " " + mone + "/" + mechane + this.sign;
                }
                else {
                    str = str.substring(0, str.indexOf('.')) + this.sign;
                }
            } else {
                if (this.mone != 0 && this.mechane != 0) {
                    str = this.value + " " + mone + "/" + mechane + this.sign;
                }
                else {
                    str = this.value + this.sign;
                }
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
        Number n = new Number(10120.0, "$", 3,4);
        System.out.println(n.toString());
    }
}
