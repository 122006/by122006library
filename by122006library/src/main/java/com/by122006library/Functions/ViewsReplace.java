package com.by122006library.Functions;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.by122006library.Functions.AttBinder.AttBinder;
import com.by122006library.Functions.AttBinder.TimeAtt;
import com.by122006library.Functions.AttBinder.ViewAtt;
import com.by122006library.Functions.CycleTask.CycleTask;
import com.by122006library.Interface.UIThread;
import com.by122006library.Utils.ViewUtils;

import static com.by122006library.Utils.ViewUtils.removeViewFromParents;

/**
 * Created by admin on 2017/6/13.
 */

public class ViewsReplace {

    FrameLayout framelayout;
    ViewGroup.LayoutParams params;
    private View from, to;
    private long durTime = 200;

    private View endV;

    public static ViewsReplace from(View v) {
        ViewsReplace viewsReplace = new ViewsReplace();
        viewsReplace.setFrom(v);
        return viewsReplace;
    }

    public View getFrom() {
        return from;
    }

    protected void setFrom(View from) {
        this.from = from;
    }

    public View getTo() {
        return to;
    }

    protected void setTo(View to) {
        this.to = to;
        endV = to;
    }

    public ViewsReplace to(View v) {
        setTo(v);
        return this;
    }

    protected long getDurTime() {
        return durTime;
    }

    public ViewsReplace setDurTime(long durTime) {
        this.durTime = durTime;
        return this;
    }
    TimeAtt time;
    @UIThread
    public ViewsReplace transform() {
        if (SmartRun.sPrepare(this)) return this;
        mLog.isNoNull(to.getParent());
        if (to.getParent() != null && ((ViewGroup) to.getParent()).getTag() instanceof ViewsReplace) {
            ViewsReplace tag = (ViewsReplace) ((ViewGroup) to.getParent()).getTag();
            if ((tag.from == from && tag.to == to) || (tag.to == from && tag.from == to)) {
                mLog.i("已有变化中，强制更改结果");
                tag.time.remove();
                tag.endV = to;
                return this;
            }
        }
        if (from.getParent() != null && ((ViewGroup) from.getParent()).getTag() instanceof ViewsReplace) {
            ViewsReplace tag = (ViewsReplace) ((ViewGroup) from.getParent()).getTag();
            if ((tag.from == from && tag.to == to) || (tag.to == from && tag.from == to)) {
                mLog.i("已有变化中，强制更改结果");
                tag.time.remove();
                tag.endV = to;
                return this;
            }
        }
        if (to.getParent() != null && from.getParent() != null) {
            removeViewFromParents(to);
        }
        mLog.isNoNull(to.getParent());
        mLog.isNoNull(from.getParent());
        if (!CycleTask.isRunning) mLog.i("CycleTask.isRunning=" + CycleTask.isRunning);
        framelayout = ViewUtils.surroundViewGroup(from, new FrameLayout(from.getContext()));
        framelayout.setTag(this);
        params = from.getLayoutParams();
        to.setAlpha(0f);
        framelayout.addView(to);
        final AttBinder binder = new AttBinder();
        ViewAtt att1 = new ViewAtt(from, ViewAtt.AttStyle.Alpha, 1, 0);
        att1.setReverse();
        ViewAtt att2 = new ViewAtt(to, ViewAtt.AttStyle.Alpha, 1, 0);
//        att2.logProgress("to");
        time = new TimeAtt((long) (durTime)) {
            @Override
            @UIThread
            public void remove() {
                if (SmartRun.sPrepare(this)) return;
                super.remove();
                if(binder.logProgress)mLog.i("===========完成变换");
                try {
                    ViewGroup vg = (ViewGroup) framelayout.getParent();
                    if (vg == null) return;
                    framelayout.removeView(from);
                    int index = vg.indexOfChild(framelayout);
                    vg.removeView(framelayout);
                    framelayout.removeView(to);
                    to.setLayoutParams(params);
                    removeViewFromParents(to);
                    vg.addView(endV, index);
                    endV.setAlpha(1f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
//        time.logProgress("time").logProgress("时间轴");
        time.addTimeProgressAction(new TimeAtt.TimeProgressAction(durTime) {
            @Override
            @UIThread
            public void event() {
                time.remove();
            }
        });
//        binder.logProgress();
        binder.bind(att1, att2, time);
        return this;
    }


}
