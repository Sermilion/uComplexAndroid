package org.ucomplex.ucomplex.Activities.Login;


import org.json.JSONException;
import org.json.JSONObject;
import org.ucomplex.ucomplex.Model.Users.User;
import org.ucomplex.ucomplex.Utility.HttpFactory;

/**
 * Model layer on Model View Presenter Pattern
 * <p>
 * ---------------------------------------------------
 * Created by @Sermilion on 07/11/16.
 * Project: UComplex
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */
public class LoginModel implements MVP_Login.ProvidedModelOpsFromPresenter {

    // Presenter reference
    private MVP_Login.RequiredPresenterOpsToModel mPresenter;
    private LoginRepository mDAO;

    /**
     * Main constructor, called by Activity during MVP setup
     *
     * @param presenter Presenter instance
     */
    public LoginModel(MVP_Login.RequiredPresenterOpsToModel presenter) {
        this.mPresenter = presenter;
        mDAO = new LoginRepository(mPresenter.getAppContext());
    }

    /**
     * Test contructor. Called only during unit testing
     *
     * @param presenter Presenter instance
     * @param dao       DAO instance
     */
    public LoginModel(MVP_Login.RequiredPresenterOpsToModel presenter, LoginRepository dao) {
        this.mPresenter = presenter;
        mDAO = dao;
    }

    /**
     * Called by Presenter when View is destroyed
     *
     * @param isChangingConfiguration true configuration is changing
     */
    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mPresenter = null;
            mDAO = null;
        }
    }

    /**
     * Loads all Data, getting EventItems from DB
     *
     * @return true with success
     */
    @Override
    public User loadData(String login, String password) {
        User user;
        String jsonData = mDAO.login(login, password);
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            if (jsonObject.getJSONObject("roles") == null) {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        user = mDAO.getUser(jsonData);
        return user;
    }

    @Override
    public String sendResetRequest(String login,String password, String email) {
        String encodedAuth = HttpFactory.encodeLoginData( login+ ":" + password);
        String json = "\"email\":\""+email+"\"";
        return HttpFactory.httpPost(HttpFactory.RESTORE_PASSWORD_URL, encodedAuth, json);
    }

}
