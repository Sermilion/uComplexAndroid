package org.ucomplex.ucomplex.Modules.Login;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.Gson;

import net.oneread.aghanim.components.utility.MVPCallback;
import net.oneread.aghanim.mvp.abstractmvp.AbstractModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.ucomplex.ucomplex.CommonDependencies.Constants;
import org.ucomplex.ucomplex.CommonDependencies.FacadeMedia;
import org.ucomplex.ucomplex.CommonDependencies.FacadePreferences;
import org.ucomplex.ucomplex.CommonDependencies.HttpFactory;
import org.ucomplex.ucomplex.Model.Users.User;
import org.ucomplex.ucomplex.Model.Users.UserInterface;

/**
 * Model layer on Model View PresenterToViewInterface Pattern
 * <p>
 * ---------------------------------------------------
 * Created by @Sermilion on 07/11/16.
 * Project: UComplex
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */
public class LoginModel extends AbstractModel {

    private UserInterface mUser;

    private static final String JSON_SESSION_KEY = "session";
    private static final String JSON_ROLES_KEY = "roles";
    private Context mContext;

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public LoginModel() {

    }


    public String sendResetRequest(String email) {
        String json = "\"email\":\"" + email + "\"";
        return null;
    }


    public Object getDataFromJson(String jsonString) throws JSONException {
        UserInterface user;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.getJSONArray(JSON_ROLES_KEY) == null) {
                return null;
            } else {
                user = getUserFromJson(jsonString);
                if (user.getPhoto() == 1) {
                    Uri bitmapUri = FacadeMedia.createFileForBitmap();
                    String uriString = user.getBitmapUriStringFromUri(bitmapUri);
                    user.setBitmapUriString(uriString);
                } else {
                    FacadePreferences.deleteFromPref(null, FacadePreferences.KEY_PREF_PROFILE_PHOTO);
                }
                return user;
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    private User getUserFromJson(String rolesJsonStr) throws JSONException {
        JSONObject rolesJson = new JSONObject(rolesJsonStr);
        Gson gson = new Gson();
        JSONObject userSession = rolesJson.getJSONObject(JSON_SESSION_KEY);
        return gson.fromJson(userSession.toString(), User.class);
    }

    @Override
    public void loadData(MVPCallback mvpCallback, Bundle... bundles) {
        String password = mUser.getPassword();
        final String encodedAuth = HttpFactory.encodeLoginData(mUser.getLogin() + ":" + mUser.getPassword());
        HttpFactory.getInstance().httpVolley(HttpFactory.AUTHENTICATIO_URL,
                encodedAuth,
                mContext,
                Constants.REQUEST_LOGIN,
                null,
                mvpCallback,
                password);
    }

    @Override
    public UserInterface processJson(String s) {
        UserInterface user;
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (jsonObject.getJSONArray(JSON_ROLES_KEY) == null) {
                return null;
            } else {
                user = getUserFromJson(s);
                if (user.getPhoto() == 1) {
                    Uri bitmapUri = FacadeMedia.createFileForBitmap();
                    String uriString = user.getBitmapUriStringFromUri(bitmapUri);
                    user.setBitmapUriString(uriString);
                } else {
                    FacadePreferences.deleteFromPref(null, FacadePreferences.KEY_PREF_PROFILE_PHOTO);
                }
                return user;
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setUser(UserInterface user) {
        this.mUser = user;
    }

    public UserInterface getUser() {
        return mUser;
    }
}
