package org.ucomplex.ucomplex.Modules.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.ucomplex.ucomplex.Interfaces.MVP.ViewActivityToPresenter;
import org.ucomplex.ucomplex.Interfaces.OnTaskCompleteListener;
import org.ucomplex.ucomplex.Model.Users.UserInterface;
import org.ucomplex.ucomplex.Modules.BaseActivity;
import org.ucomplex.ucomplex.Modules.Events.EventsActivity;
import org.ucomplex.ucomplex.Modules.MyApplication;
import org.ucomplex.ucomplex.Modules.RoleSelect.RoleSelectActivity;
import org.ucomplex.ucomplex.Model.Users.LoginErrorType;
import org.ucomplex.ucomplex.R;
import org.ucomplex.ucomplex.Utility.Constants;
import org.ucomplex.ucomplex.Utility.FacadePreferences;

import java.util.ArrayList;

import javax.inject.Inject;

import static org.ucomplex.ucomplex.Model.Users.LoginErrorType.EMPTY_EMAIL;
import static org.ucomplex.ucomplex.Model.Users.LoginErrorType.INVALID_PASSWORD;
import static org.ucomplex.ucomplex.Model.Users.LoginErrorType.PASSWORD_REQUIRED;
import static org.ucomplex.ucomplex.Utility.HttpFactory.encodeLoginData;

@EActivity(resName="R.layout.activity_login")
public class LoginActivityView extends BaseActivity implements ViewActivityToPresenter{

    static final String TAG = LoginActivityView.class.getName();
    @ViewById(resName="R.id.loginRequest")
    AutoCompleteTextView mLoginView;
    @ViewById(resName="R.id.password")
    EditText mPasswordView;
    @ViewById(resName="R.id.login_progress")
    View mProgressView;
    @ViewById(resName="R.id.forgot_pass_button")
    Button mForgotButton;
    @ViewById(resName="R.id.login_sign_in_button")
    Button mLoginSignInButton;

    @Inject public void setPresenter(LoginPresenter presenter) {
        super.mPresenter = presenter;
    }
    @Inject public void setModel(LoginModel model) {
        super.mModel = model;
    }
    @Inject public void setRepository(LoginRepository repository) {
        super.mRepository = repository;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getApplication()).getLoginDiComponent().inject(this);
        super.setupMVP(this, LoginActivityView.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy(isChangingConfigurations());
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public void showToast(Toast toast) {
        toast.show();
    }

    @Override
    public void showProgress() {
        mProgressView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressView.setVisibility(View.GONE);
    }

    @Override
    public void showAlert(AlertDialog dialog) {
        dialog.show();
    }

    @Click
    void forgot_pass_button() {
        ((MVP_Login.PresenterInterface) mPresenter).showRestorePasswordDialog();
    }

    @Click
    void login_sign_in_button() {
        mLoginView.setError(null);
        mPasswordView.setError(null);

        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        UserInterface user = mPresenter.getUser();
        user.setPassword(password);
        user.setLogin(login);

        ArrayList<LoginErrorType> error = ((MVP_Login.PresenterInterface) mPresenter).checkCredentials(login, password);

        if (error.contains(PASSWORD_REQUIRED)) {
            mPasswordView.setError(getString(R.string.error_field_required));
        } else if (error.contains(INVALID_PASSWORD)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
        }
        if (error.contains(EMPTY_EMAIL)) {
            mLoginView.setError(getString(R.string.error_field_required));
        }
    }

    @Override
    public void setupViews() {

    }

    public void successfulLogin(int flag) {
        UserInterface user = mPresenter.getUser();
        Intent intent;
        if(flag == 1 || user.getRoles().size() == 1){
            intent = new Intent(getActivityContext(), EventsActivity.class);
            if(user.getRoles().size() == 1){
                user.setType(user.getRoles().get(0).getType());
                FacadePreferences.setUserDataToPref(getActivityContext(), user);
                String loginData = encodeLoginData(user.getLogin()+":"+user.getPassword()+":"+user.getRoles().get(0).getId());
                FacadePreferences.setLoginDataToPref(getActivityContext(), loginData);
            }
        }else {
            intent = new Intent(getActivityContext(), RoleSelectActivity.class);
        }
        intent.putExtra(Constants.EXTRA_KEY_USER, (Parcelable) user);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivityContext().startActivity(intent);
        finish();
    }
}
