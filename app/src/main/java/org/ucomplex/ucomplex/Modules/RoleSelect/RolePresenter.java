package org.ucomplex.ucomplex.Modules.RoleSelect;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import net.oneread.aghanim.components.utility.IRecyclerItem;
import net.oneread.aghanim.components.utility.MVPCallback;
import net.oneread.aghanim.mvp.abstractmvp.MVPAbstractPresenterRecycler;
import net.oneread.aghanim.mvp.recyclermvp.MVPModelRecycler;

import org.ucomplex.ucomplex.Modules.Events.EventsActivity;

import java.util.List;

/**
 * ---------------------------------------------------
 * Created by Sermilion on 08/11/2016.
 * Project: uComplex_v_2
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */

public class RolePresenter extends MVPAbstractPresenterRecycler<List<RoleItem>>{

    public RolePresenter() {

    }

    @Override
    public void loadData(Bundle... bundle) {
        mModel.loadData(new MVPCallback<List<IRecyclerItem>>() {
            @Override
            public void onSuccess(List<IRecyclerItem> iRecyclerItems) {
                populateRecyclerView(iRecyclerItems);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }
        }, bundle);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder aHolder, final int position) {
        RoleViewHolder holder = (RoleViewHolder) aHolder;
        final RoleItem role = (RoleItem) ((MVPModelRecycler)mModel).getItem(position);
        holder.roleName.setText(role.getRoleName());
        holder.roleIcon.setImageResource(role.getRoleIcon());
        holder.setPosition(position);
        holder.roleIcon.setOnClickListener(baseOnClickListener);
    }

}
