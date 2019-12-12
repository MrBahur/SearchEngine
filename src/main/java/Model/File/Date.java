package Model.File;

public class Date extends Term {
    private int day;
    private int month;
    private int year;

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    @Override
    public String toString() {
        String str = "";
        String d = Integer.toString(this.day);
        String m = Integer.toString(this.month);
        if (this.year == -1) {
            if (this.month < 10) {
                m = "0" + this.month;
            }
            if (this.day < 10) {
                d = "0" + this.day;
            }
            str = m + "-" + d;
        }
        if (this.day == -1) {
            if (this.month < 10) {
                m = "0" + this.month;
            }
            if (this.year<1900) {
                this.year += 1900;
            }
            str = this.year + "-" + m;
        }
        return str;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Date)) {
            return false;
        }
        Date d = (Date) other;
        return this.day==d.day && this.month==d.month && this.year==d.year;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(day)*Integer.hashCode(month)*Integer.hashCode(year);
    }

    public static void main(String[] args) {
        Date d1 = new Date(-1,5,94);
        System.out.println(d1.toString());
        Date d2 = new Date(14, 5,-1);
        System.out.println(d2.toString());
    }
}
