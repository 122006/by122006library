package com.by122006library.Functions;


import android.support.annotation.Nullable;

import com.by122006library.Functions.CycleTask.CycleTask;
import com.by122006library.Functions.CycleTask.DelayTask;
import com.by122006library.MyException;
import com.by122006library.Utils.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 经验排序Map
 * <p>
 * 根据用户设置的权重与使用次数进行排序，其积更大的对象更容易被搜索到
 * <p>
 * 固定容积限制
 * <p>
 * 闲时排序功能
 * <p>
 * Created by 122006 on 2017/6/27.
 */

public class ExpMap<S, E> {
    ArrayList<C> control;
    HashMap<S, E> data;
    int maxDataNum;
    /**
     * 重复存储数据的权重比例
     */
    private int putWeight = 5;

    public ExpMap() {
        init(100);
    }

    public ExpMap(int maxDataNum) {
        init(maxDataNum);
    }

    private ExpMap init(int maxDataNum) {
        stopSort();
        this.maxDataNum = maxDataNum;
        int listNum = (int) (maxDataNum / 0.8);
        if (listNum <= 0) listNum = maxDataNum;
        control = new ArrayList<>(listNum);
        data = new HashMap<>(listNum);
        return this;
    }

    public ExpMap clear() {
        stopSort();
        control.clear();
        data.clear();
        CycleTask.unRegister(this);
        return this;
    }

    public ExpMap put(S key, E value) {
        put(key, value, Weight.Normal);
        return this;
    }

    public ExpMap put(S key, E value, Weight weight) {
        put(key, value, weight.getIndex());
        return this;
    }

    public ExpMap put(S key, E value, int weight) {
        stopSort();
        if (data.containsKey(key)) {
            for(C c:control){
                c.putCount+=getPutWeight();
            }
            applySort();
        } else {
            C c = new C(key, weight);
            data.put(key, value);
            control.add(c);
        }
        return this;
    }

    public boolean containsKey(S key){
        return data.containsKey(key);
    }
    public ExpMap remove(S key){
        data.remove(key);
        for(C c:control){
            control.remove(c);
            return this;
        }
        return this;
    }


    @Nullable
    public E get(S key){
        for(int i=0;i<control.size();i++){

            C c=control.get(i);
            if(c.s.equals(key)||c.s==key){
                c.getCount++;
                applySort();
                return data.get(c.s);
            }
        }
        return null;
    }
//    private boolean Flag_needSort=false;
    /**
     * 排序打断标志
     */
    private boolean Flag_StopSort=true;
    /**
     * 申请进行排序，将在数组空闲时进行排序
     */
    private ExpMap<S, E> applySort(){
        Flag_StopSort=false;
//        Flag_needSort=true;
        new DelayTask(200) {
            @Override
            public void doCycleAction(int haveCycleCount) throws MyException {
                synchronized (ExpMap.this) {
                    if(Flag_StopSort) return;
                    ArrayList<C> re=sort((ArrayList<C>) control.clone());
                    if(re==null) return;
                    control.clear();
                    if(re.size()>maxDataNum){
                        try {
                            Method method= ReflectionUtils.getMethod(re,"removeRange",int.class,int.class);
                            method.invoke(re,maxDataNum,re.size());
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    control.addAll(re);
                }
            }

            public ArrayList<C> sort(ArrayList<C> list){
                C ci,cj;
                for(int i=0;i<list.size()-1;i++){
                    for(int j=0;j<list.size()-i-1;j++){//比较两个整数
                        if(Flag_StopSort) return null;
                        cj=list.get(j);

                        if(cj.getQuantity()>list.get(j+1).getQuantity()){
                            C temp=cj;
                            list.set(j, list.get(j+1));
                            list.set(j+1, temp);
                        }
                    }
                }
                return list;
            }

        }.setLog(false).register(this,DelayTask.SINGLETASK_COVER);
        return this;
    }



    /**
     * 立即停止当前排序,需要在元素变化时调用该方法
     */
    private ExpMap<S, E> stopSort(){
        Flag_StopSort=true;
        return this;
    }


    public int getPutWeight() {
        return putWeight;
    }

    public ExpMap<S, E> setPutWeight(int putWeight) {
        applySort();
        this.putWeight = putWeight;
        return this;
    }


    public enum Weight {
        Once(0), Unimportant(5), Normal(20), Important(100), Keep(10000);
        private int index;

        // 构造方法
        Weight(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    protected static class C<S> {
        /**
         * 对象key
         */
        S s;
        /**
         * 权重
         */
        int weight;
        int getCount = 0,putCount=0;

        C(S s, int weight) {
            this.s = s;
            this.weight = weight;
        }

        int getQuantity() {
            return weight * (getCount+putCount);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof C)) return false;
            return this.s.equals(((C) obj).s);
        }
    }

}
