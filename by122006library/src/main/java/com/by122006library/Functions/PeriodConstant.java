package com.by122006library.Functions;

/**
 * 周期常量：内部模拟了一个double变量，该变量会在指定的范围内周期循环。不依赖于线程
 * <p>
 * Created by admin on 2017/4/18.
 */

public class PeriodConstant {
    private long startTime;
    private Builder builder;

    public void reStart() {
        startTime = System.currentTimeMillis();
    }


    public double getDouble() {
        long time = System.currentTimeMillis() - startTime;
        int times = (int) (time / builder.cycleTime);
        long thisTime = time % builder.cycleTime;
        return builder.formula(times, thisTime);
    }
    public int getInt() {
        return (int) getDouble();
    }


    public String toString(){
        String str="PeriodConstant :{ now:%f startTime=%d , range=[%f , %f] , cycleTime=%d  }";
        str=String.format(str,getDouble(),startTime,builder.start,builder.end,builder.cycleTime);
        return str;
    }

    public static class Builder_Add extends Builder {

        public Builder_Add(int start, int end, long cycleTime) {
            super(start, end, cycleTime);
        }

        @Override
        public double formula(int constant, long thisTime) {
            if (constant % 2 == 0) return start + (end - start) * thisTime / cycleTime;
            else return end - (end - start) * thisTime / cycleTime;
        }
    }


    public static abstract class Builder {
        double start, end;
        long cycleTime;

        public Builder(int start, int end, long cycleTime) {
            this.start = start;
            this.end = end;
            this.cycleTime = cycleTime;
        }

        public abstract double formula(int constant, long thisTime);

        public PeriodConstant builder() {
            PeriodConstant periodConstant = new PeriodConstant();
            periodConstant.startTime = System.currentTimeMillis();
            periodConstant.builder = this;
            return periodConstant;
        }
    }

}
