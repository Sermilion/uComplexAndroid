package org.ucomplex.ucomplex.Modules.Subject.SubjectTimeline;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import net.oneread.aghanim.components.base.MVPViewBaseFragment;
import net.oneread.aghanim.components.utility.IRecyclerItem;
import net.oneread.aghanim.mvp.recyclermvp.MVPModelRecycler;
import net.oneread.aghanim.mvp.recyclermvp.MVPPresenterRecycler;

import org.ucomplex.ucomplex.BaseComponents.DaggerApplication;
import org.ucomplex.ucomplex.Modules.Subject.SubjectDetails.SubjectDetailsModel;
import org.ucomplex.ucomplex.R;

import java.util.List;

import javax.inject.Inject;

import static org.ucomplex.ucomplex.CommonDependencies.Constants.AUTH_STRING;

/**
 * ---------------------------------------------------
 * Created by Sermilion on 22/01/2017.
 * Project: uComplex_v_2
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */

public class SubjectTimelineFragment extends MVPViewBaseFragment<String, List<IRecyclerItem>> {

    public static final String DATA_REQUESTED = "dataRequested";
    protected boolean dataRequested;

    @Inject
    @Override
    public void setPresenter(MVPPresenterRecycler<String, List<IRecyclerItem>> presenter) {
        presenter.setView(this);
        mPresenter = presenter;
    }

    @Inject
    public void setModel(MVPModelRecycler<String, List<IRecyclerItem>> mModel) {
        this.mPresenter.setModel(mModel);
    }

    public static SubjectTimelineFragment getFragment(Activity mContext) {
        SubjectTimelineFragment fragment = new SubjectTimelineFragment();
        fragment.setContext(mContext);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            dataRequested = savedInstanceState.getBoolean(DATA_REQUESTED);
        }
        ((DaggerApplication) mContext.getApplication()).getSubjectTimelineDiComponent().inject(this);
        this.mFragmentLayout = R.layout.fragment_recycler;
        this.mRecyclerViewId = R.id.recyclerView;
        this.mProgressViewId = R.id.progressBar;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(DATA_REQUESTED, dataRequested);
        super.onSaveInstanceState(outState);
    }

    public void onFragmentVisible() {
        if (!dataRequested) {
            mPresenter.loadData(getArguments());
            dataRequested = true;
        }
    }
}


