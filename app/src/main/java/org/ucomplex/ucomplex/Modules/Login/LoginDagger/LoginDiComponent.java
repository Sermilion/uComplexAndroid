package org.ucomplex.ucomplex.Modules.Login.LoginDagger;

import org.ucomplex.ucomplex.Modules.Login.LoginActivityView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * ---------------------------------------------------
 * Created by Sermilion on 10/11/2016.
 * Project: uComplex_v_2
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */
@Singleton
@Component(modules = {LoginModule.class})
public interface LoginDiComponent {
    void inject(LoginActivityView activity);
}
