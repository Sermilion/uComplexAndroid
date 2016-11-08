package org.ucomplex.ucomplex.Activities.Login.RoleSelect;

import android.content.Intent;

import org.ucomplex.ucomplex.Model.Users.User;

import java.util.ArrayList;

/**
 * ---------------------------------------------------
 * Created by Sermilion on 08/11/2016.
 * Project: uComplex_v_2
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */

public class RoleModel implements MVP_RoleSelect.ProvidedModelOpsFromPresenter {

    // Presenter reference
    private MVP_RoleSelect.RequiredPresenterOpsToModel mPresenter;
    private ArrayList<RoleItem> mRoles;
    private RoleRepository mRolesRepository;
    private User user;
    private static final String ROLE_MODEL_EXTRA_KEY = "user";

    public RoleModel(MVP_RoleSelect.RequiredPresenterOpsToModel presenter, Intent intent) {

        this.mPresenter = presenter;
        this.mRolesRepository = new RoleRepository(mPresenter.getAppContext());
        if(intent.hasExtra(ROLE_MODEL_EXTRA_KEY)){
            this.user = intent.getParcelableExtra(ROLE_MODEL_EXTRA_KEY);
        }
        System.out.println();
    }

    public RoleModel(MVP_RoleSelect.RequiredPresenterOpsToModel presenter, RoleRepository repository, User user) {
        this.mPresenter = presenter;
        mRolesRepository = repository;
        this.user = user;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mPresenter = null;
            mRoles = null;
        }
    }

    @Override
    public boolean loadData() {
        mRoles = mRolesRepository.getAllRoleItems(user);
        return mRoles != null;
    }

    @Override
    public RoleItem getRole(int position) {
        return mRoles.get(position);
    }

    public void setRoles(ArrayList<RoleItem> mRoles) {
        this.mRoles = mRoles;
    }

    @Override
    public int getRolesCount() {
        return mRoles.size();
    }
}
