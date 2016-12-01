package org.ucomplex.ucomplex.Modules.RoleSelect;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.ucomplex.ucomplex.Interfaces.MVP.BaseMVP.Model;
import org.ucomplex.ucomplex.Interfaces.MVP.ModelRecycler;
import org.ucomplex.ucomplex.Interfaces.MVP.PresenterRecycler;
import org.ucomplex.ucomplex.Interfaces.MVP.ViewToPresenterRecycler;
import org.ucomplex.ucomplex.Interfaces.MVP.BaseMVP.ViewToPresenter;
import org.ucomplex.ucomplex.Interfaces.OnDataLoadedListener;
import org.ucomplex.ucomplex.Model.Users.UserInterface;
import org.ucomplex.ucomplex.Modules.Events.EventsActivity;
import org.ucomplex.ucomplex.R;
import org.ucomplex.ucomplex.Utility.Constants;
import org.ucomplex.ucomplex.Utility.FacadePreferences;

import java.lang.ref.WeakReference;
import java.util.Random;

import static org.ucomplex.ucomplex.Utility.HttpFactory.encodeLoginData;

/**
 * ---------------------------------------------------
 * Created by Sermilion on 08/11/2016.
 * Project: uComplex_v_2
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */

public class RolePresenter implements PresenterRecycler, OnDataLoadedListener {

    private WeakReference<ViewToPresenterRecycler> mView;
    private ModelRecycler mModel;

    public RolePresenter(ViewToPresenterRecycler view) {
        mView = new WeakReference<>(view);
    }

    public RolePresenter() {

    }


    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        mView = null;
        mModel.onDestroy(isChangingConfiguration);
        if (!isChangingConfiguration) {
            mModel = null;
        }
    }

    @Override
    public void setView(ViewToPresenter view) {
        mView = new WeakReference<>((ViewToPresenterRecycler) view);
    }

    @Override
    public void onConfigurationChanged(ViewToPresenter view) {
        mView = new WeakReference<>((ViewToPresenterRecycler) view);
    }


    @Override
    public void setModel(Model model) {
        mModel = (ModelRecycler) model;
        ((RoleModel) mModel).setOnDataLoadedListener(this);
        loadData();
    }

    @Override
    public UserInterface getUser() {
        return mModel.getUser();
    }

    @Override
    public void loadData() {
        mModel.loadData();
    }

    @Override
    public ViewToPresenterRecycler getView() throws NullPointerException {
        if (mView != null)
            return mView.get();
        else
            throw new NullPointerException("View is unavailable");
    }

    @Override
    public RoleViewHolder createViewHolder(ViewGroup parent, int viewType) {
        RoleViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View viewTaskRow = inflater.inflate(R.layout.list_item_role, parent, false);
        Random random = new Random();
        int rand = random.nextInt(5);
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_focused}, createBitmapDrawbale(rand));
        states.addState(new int[]{android.R.attr.state_pressed, -android.R.attr.state_focused}, createBitmapDrawbale(rand + 1));
        viewTaskRow.setBackground(states);
        viewHolder = new RoleViewHolder(viewTaskRow, getActivityContext());
        return viewHolder;
    }

    private BitmapDrawable createBitmapDrawbale(int position) {
        if (position > 4) {
            position = 1;
        }
        return new BitmapDrawable(BitmapFactory.decodeResource(getActivityContext().getResources(),
                Constants.colorsUserSelect[position]));
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder aHolder, final int position) {
        RoleViewHolder holder = (RoleViewHolder) aHolder;
        final RoleItem role = (RoleItem) mModel.getItem(position);
        holder.roleName.setText(role.getRoleName());
        holder.roleIcon.setImageResource(role.getRoleIcon());
        holder.roleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final UserInterface user = ((RoleModel) mModel).getmUser();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        String login = user.getLogin();
                        String password = user.getPassword();
                        int role = user.getRoles().get(position).getId();
                        user.setType(user.getRoles().get(position).getType());
                        ((RoleModel) mModel).getmUser().setType(user.getType());
                        String encodedAuth = encodeLoginData(login + ":" + password + ":" + role);
                        FacadePreferences.setLoginDataToPref(getActivityContext(), encodedAuth);
                        FacadePreferences.setUserDataToPref(getActivityContext(), user);
                        return null;
                    }
                }.execute();
                Intent intent = new Intent(getActivityContext(), EventsActivity.class);
                intent.putExtra(Constants.EXTRA_KEY_USER, (Parcelable) user);
                getActivityContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mModel.getItemCount();
    }

    @Override
    public Context getAppContext() {
        try {
            return getView().getAppContext();
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public Context getActivityContext() {
        try {
            return getView().getActivityContext();
        } catch (NullPointerException e) {
            return null;
        }
    }

    private Toast makeToast(String msg) {
        return Toast.makeText(getView().getAppContext(), msg, Toast.LENGTH_SHORT);
    }

    @Override
    public void dataLoaded(boolean loaded, int start, int end) {
        getView().hideProgress();
        if (loaded)
            ((RoleSelectActivity) getView()).rolesLoaded(loaded);
        else
            getView().showToast(makeToast(getActivityContext().getString(R.string.error_loading_data)));
    }
}
