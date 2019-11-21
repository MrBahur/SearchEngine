package Model.File;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Number extends Phrase {
    private double value;
    private String sign;

 /*   public Number (double value) {
        this.sign = null;
        this.value = value;
    }*/
    @Override
    public String toString() {
        String str = Double.toString(this.value);
        double num = this.value;
        if (this.value>999 && this.value<1000000) {
            num = num/1000;
            DecimalFormat d = new DecimalFormat("###.###");
            d.setRoundingMode(RoundingMode.DOWN);
            str = d.format(num) + "k";
        }
        if (this.value>999999 && this.value<1000000000) {
            num = num/1000000;
            DecimalFormat d = new DecimalFormat("###.###");
            d.setRoundingMode(RoundingMode.DOWN);
            str = d.format(num) + "M";
        }
        if (this.value>999999999) {
            num = num/1000000000;
            DecimalFormat d = new DecimalFormat("###.###");
            d.setRoundingMode(RoundingMode.DOWN);
            str = d.format(num) + "B";
        }
        System.out.println(str);
        return str;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Number)){
            return false;
        }
        Number x = (Number)other;
        return this.sign.equals(x.sign) && this.value == x.value;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
