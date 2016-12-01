package org.ucomplex.ucomplex.Interfaces.MVP.AbstractMVP;

import android.content.Context;

import org.ucomplex.ucomplex.Interfaces.MVP.BaseMVP.Repository;
import org.ucomplex.ucomplex.Interfaces.OnTaskCompleteListener;

/**
 * ---------------------------------------------------
 * Created by Sermilion on 01/12/2016.
 * Project: uComplex_v_2
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */

public abstract class AbstractRepository implements Repository {

    protected Context mContext;
    protected OnTaskCompleteListener mOnTaskCompleteListener;

    @Override
    public void setContext(Context context) {
        this.mContext = context;
    }

    @Override
    public void setTaskCompleteListener(OnTaskCompleteListener mTaskCompleteListener) {
        this.mOnTaskCompleteListener = mTaskCompleteListener;
    }
}