package org.ucomplex.ucomplex.Interfaces.MVP;

import android.content.Context;
import android.widget.Toast;

import org.ucomplex.ucomplex.Model.Users.UserInterface;

/**
 * ---------------------------------------------------
 * Created by Sermilion on 12/11/2016.
 * Project: uComplex_v_2
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */

public interface Presenter {
    Context getAppContext();
    Context getActivityContext();
    void onDestroy(boolean isChangingConfiguration);
    void setView(ViewToPresenter view);
    ViewToPresenter getView();
    void onConfigurationChanged(ViewToPresenter view);
    void setModel(Model models);
    UserInterface getUser();

}
