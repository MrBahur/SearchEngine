package Model.File;

public class Date extends Term {
    private int day;
    private int month;
    private int year;

    /**
     * Constructor dor date
     * @param day the day in the month
     * @param month the month in the year
     * @param year the year
     */
    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
        if (this.year<100 && this.year>0) {
            this.year += 1900;
        }
    }

    /**
     * to String
     * @return String that represent date
     */
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
            str = this.year + "-" + m;
        }
        return str;
    }

    /**
     * equals for date
     * @param other other date
     * @return true if those are the same dates
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Date)) {
            return false;
        }
        Date d = (Date) other;
        return this.day==d.day && this.month==d.month && this.year==d.year;
    }

    /**
     * hashcode for date
     * @return hase that represent date
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(day)*Integer.hashCode(month)*Integer.hashCode(year);
    }

    /**
     * short main for tests
     * @param args not using this
     */
    public static void main(String[] args) {
        Date d1 = new Date(-1,5,94);
        System.out.println(d1.toString());
        Date d2 = new Date(14, 5,-1);
        System.out.println(d2.toString());
    }
}
