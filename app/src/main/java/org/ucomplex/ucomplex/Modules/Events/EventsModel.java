package org.ucomplex.ucomplex.Modules.Events;


import android.content.Context;

import org.ucomplex.ucomplex.Interfaces.MVP.Repository;
import org.ucomplex.ucomplex.Model.EventItem;
import org.ucomplex.ucomplex.Model.Users.UserInterface;
import org.ucomplex.ucomplex.Modules.Events.AsyncTasks.LoadEventsTask;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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
public class EventsModel implements MVP_Events.ModelInterface {

    private Repository mRepository;
    private ArrayList<EventItem> mEventItems;
    private UserInterface user;
    private LoadEventsTask loadEventsTask;

    public EventsModel(UserInterface user) {
        this.user = user;
        mRepository = new EventsRepository();
    }

    public EventsModel() {

    }

    void setLoadEventsTask(LoadEventsTask loadEventsTask) {
        this.loadEventsTask = loadEventsTask;
    }


    @Override
    @SuppressWarnings("unchecked")
    public void setData(Object data) {
        if (data instanceof ArrayList)
            this.mEventItems = (ArrayList<EventItem>) data;
        else
            this.user = (UserInterface) data;
    }

    @Override
    public void setRepository(Repository repository) {
        mRepository = repository;
    }

    @Override
    public UserInterface getUser() {
        return this.user;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mRepository = null;
            mEventItems = null;
            if(loadEventsTask!=null){
                loadEventsTask.cancel(true);
                try {
                    loadEventsTask.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadData() {
        loadEventsTask.setRepository(mRepository);
        loadEventsTask.execute();
//        return mEventItems != null;
    }

    @Override
    public boolean loadMoreEvents(int start) {
        ArrayList<EventItem> loadedEvents = ((EventsRepository) mRepository).loadMoreEvents(start);
        if (loadedEvents.size() != 0) {
            mEventItems.remove(mEventItems.size() - 1);
            mEventItems.addAll(loadedEvents);
            mEventItems.add(new EventItem());
        }
//        else{
//            mEventItems.remove(mEventItems.size());
//        }
        return loadedEvents.size() != 0;
    }

    @Override
    public EventItem getEvent(int position) {
        return mEventItems.get(position);
    }

    @Override
    public int getEventsCount() {
        if (mEventItems != null)
            return mEventItems.size();
        return 0;
    }

}
