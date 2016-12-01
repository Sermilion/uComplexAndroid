package org.ucomplex.ucomplex.Modules.Events;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.ucomplex.ucomplex.AbstractClasses.AbstractPresenter;
import org.ucomplex.ucomplex.Interfaces.MVP.ViewToPresenterRecycler;
import org.ucomplex.ucomplex.Model.EventItem;
import org.ucomplex.ucomplex.Model.Users.UserInterface;
import org.ucomplex.ucomplex.R;
import org.ucomplex.ucomplex.Utility.Constants;
import org.ucomplex.ucomplex.Utility.FacadeMedia;
import org.ucomplex.ucomplex.Utility.HttpFactory;

/**
 * ---------------------------------------------------
 * Created by Sermilion on 07/11/2016.
 * Project: uComplex_v_2
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */

public class EventsPresenter extends AbstractPresenter implements MVP_Events.PresenterInterface {

    private boolean hasMoreEvents = true;
    private static final int TYPE_COMMON = 0;
    private static final int TYPE_FOOTER = 1;

    public EventsPresenter() {

    }

    @Override
    public EventViewHolder createViewHolder(ViewGroup parent, int viewType) {
        EventViewHolder viewHolder;
        int layout = viewType == 0 ? R.layout.list_item_event : R.layout.list_item_event_footer;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View viewTaskRow = inflater.inflate(layout, parent, false);
        viewHolder = new EventViewHolder(viewTaskRow);
        return viewHolder;
    }

    @Override
    public void bindViewHolder(final RecyclerView.ViewHolder aHolder, int position) {
        EventViewHolder holder = (EventViewHolder) aHolder;
        if (position != getItemCount() - 1) {
            final EventItem event = ((EventsModel) mModel).getItem(position);
            String personName = event.getParams().getName();
            if (personName == null || personName.equals(Constants.STRING_EMPTY)) {
                event.getParams().setName(getActivityContext().getResources().getString(R.string.ucomplex));
            }
            holder.eventPersonName.setText(event.getParams().getName());
            holder.eventTextView.setText(event.getEventText());
            holder.eventTime.setText(event.getTime());
            int id = event.getParams().getId();
            String name = event.getParams().getName();
            Drawable textDrawable = FacadeMedia.getTextDrawable(id, name, getActivityContext());
            holder.eventsImageView.setImageDrawable(textDrawable);
            if (event.getEventImageBitmap() != null) {
                holder.eventsImageView.setImageBitmap(event.getEventImageBitmap());
            } else {
                if (event.getParams().getCode() == null) {
                    holder.eventsImageView.setImageDrawable(textDrawable);
                } else {
                    Glide.with(getActivityContext())
                            .load(HttpFactory.LOAD_PROFILE_URL + event.getParams().getCode() + Constants.IMAGE_FORMAT)
                            .into(holder.eventsImageView);
                }
            }

//            holder.eventDetailsLayout.setOnClickListener(view -> {
////                Intent intent = new Intent(getActivityContext(), null);
//            });
        } else {
            if (hasMoreEvents) {
                holder.loadMoreEventsButton.setOnClickListener(new View.OnClickListener() {
                                                                   @Override
                                                                   public void onClick(View view) {
                                                                       getActivityContext().sendBroadcast(new Intent(Constants.EVENTS_LOAD_MORE_BROADCAST));
                                                                   }
                                                               }
                );
            } else {
                holder.loadMoreEventsButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == ((EventsModel) mModel).getItemCount() - 1 ? TYPE_FOOTER : TYPE_COMMON;
    }

    @Override
    public void loadMoreEvents(final int start) {
        try {
            getView().showProgress();
            ((EventsModel) mModel).loadMoreEvents(start);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return ((EventsModel) mModel).getItemCount();
    }

    @Override
    public void dataLoaded(boolean loaded, int start, int end) {
        getView().hideProgress();
        if (loaded) {
            ((ViewToPresenterRecycler) getView()).notifyItemRangeRemoved(start, end);
            ((ViewToPresenterRecycler) getView()).notifyItemRangeInserted(start, end);
        } else
            hasMoreEvents = false;
    }
}

