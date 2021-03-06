package org.ucomplex.ucomplex.Modules.Subject.SubjectMaterials;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import net.oneread.aghanim.components.utility.IRecyclerItem;
import net.oneread.aghanim.components.utility.MVPCallback;
import net.oneread.aghanim.components.utility.OnClickStrategy;
import net.oneread.aghanim.components.utility.RecyclerOnClickListener;
import net.oneread.aghanim.mvp.abstractmvp.MVPAbstractPresenterRecycler;
import net.oneread.aghanim.mvp.basemvp.MVPModel;
import net.oneread.aghanim.mvp.recyclermvp.MVPModelRecycler;
import net.oneread.aghanim.mvp.recyclermvp.MVPViewRecycler;

import org.ucomplex.ucomplex.BaseComponents.DaggerApplication;
import org.ucomplex.ucomplex.CommonDependencies.FacadeCommon;
import org.ucomplex.ucomplex.CommonDependencies.MVPUtility;
import org.ucomplex.ucomplex.CommonDependencies.Network.HttpFactory;
import org.ucomplex.ucomplex.CommonDependencies.Network.InputStreamVolleyRequest;
import org.ucomplex.ucomplex.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import static org.ucomplex.ucomplex.CommonDependencies.Constants.AUTH_STRING;
import static org.ucomplex.ucomplex.CommonDependencies.Constants.UC_ACTION_DOWNLOAD_COMPLETE;
import static org.ucomplex.ucomplex.Modules.Subject.SubjectMaterials.NotificationService.EXTRA_BODY;
import static org.ucomplex.ucomplex.Modules.Subject.SubjectMaterials.NotificationService.EXTRA_LARGE_ICON;
import static org.ucomplex.ucomplex.Modules.Subject.SubjectMaterials.NotificationService.EXTRA_TITLE;
import static org.ucomplex.ucomplex.Modules.Subject.SubjectMaterials.SubjectMaterialsModel.EXTRA_KEY_MY_MATERIALS;

/**
 * ---------------------------------------------------
 * Created by Sermilion on 18/01/2017.
 * Project: uComplex_v_2
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */

public class SubjectMaterialsPresenter extends MVPAbstractPresenterRecycler<String> implements Response.Listener<byte[]>, Response.ErrorListener {

    static final int TYPE_FILE = 0;
    static final int TYPE_FOLDER = 1;
    public static final String EXTRA_KEY_FOLDER = "folder";
    private static final String EXTRA_KEY_GET_FOLDER = "get_folder";
    private String filename;
    private InputStreamVolleyRequest request;
    private boolean mMyFiles;
    private String mPreviousName;
    private String authString;
    private String[] studentMenuActions;
    private String[] teacherMenuActions;

    public void setMyFiles(boolean mMyFiles) {
        this.mMyFiles = mMyFiles;
    }

    private void pageUp() {
        ((SubjectMaterialsModel) mModel).pageUp();
    }

    void pageDown() {
        ((SubjectMaterialsModel) mModel).pageDown();
        Pair<List<IRecyclerItem>, String> history = getHistory(((SubjectMaterialsModel) mModel).getCurrentPage());
        populateRecyclerView(history.first);
    }

    int getCurrentPage() {
        return ((SubjectMaterialsModel) mModel).getCurrentPage();
    }

    private void addHistory(Pair<List<IRecyclerItem>, String> list) {
        ((SubjectMaterialsModel) mModel).addHistory(list);
    }

    private Pair<List<IRecyclerItem>, String> getHistory(int index) {
        return ((SubjectMaterialsModel) mModel).getHistory(index);
    }

    @Override
    public void setModel(MVPModel<String, List<IRecyclerItem>> models, Bundle... bundle) {
        this.mModel = models;
        this.mModel.setContext(this.getActivityContext());
        this.authString = ((DaggerApplication) getAppContext()).getAuthString();
        studentMenuActions = new String[]{getActivityContext().getString(R.string.rename), getActivityContext().getString(R.string.delete)};
        teacherMenuActions = new String[]{getActivityContext().getString(R.string.rename), getActivityContext().getString(R.string.delete), getActivityContext().getString(R.string.share)};
    }

    private void setupOnClickListener(SubjectMaterialsViewHolder holder, int viewType) {
        RecyclerOnClickListener clickListener = new RecyclerOnClickListener();
        OnClickStrategy strategy = view -> {
            if (((MVPModelRecycler) mModel).getItemCount() > 0) {
                SubjectMaterialsItem item = (SubjectMaterialsItem) ((MVPModelRecycler) mModel).getItem(holder.getAdapterPosition());
                if (viewType == TYPE_FILE) {
                    if (ContextCompat.checkSelfPermission(getActivityContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivityContext(), getActivityContext().getString(R.string.need_storage_permissions), Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(getActivityContext(), getActivityContext().getString(R.string.file_download_started), Toast.LENGTH_SHORT).show();
                    filename = item.getAddress() + "." + item.getType();
                    startNotificationService(filename, "Загрузка файла началась.", null);
                    String mUrl = HttpFactory.DOWNLOAD_MATERIAL_URL;
                    mUrl = mUrl + item.getOwnersId() + "/" + filename;
                    request = new InputStreamVolleyRequest(Request.Method.GET, mUrl, this, this, null);
                    RequestQueue mRequestQueue = Volley.newRequestQueue(getAppContext(),
                            new HurlStack());
                    mRequestQueue.add(request);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(AUTH_STRING, authString);
                    bundle.putString(EXTRA_KEY_FOLDER, item.getAddress());
                    bundle.putBoolean(EXTRA_KEY_GET_FOLDER, true);
                    bundle.putBoolean(EXTRA_KEY_MY_MATERIALS, mMyFiles);
                    loadData(bundle);
                }
            }
        };
        clickListener.setStrategy(strategy);
        holder.mClickArea.setOnClickListener(clickListener);
    }

    private void startNotificationService(String filename, String message, Uri largeIcon) {
        Intent notificationIntent = new Intent(getActivityContext(), NotificationService.class);
        notificationIntent.putExtra(EXTRA_TITLE, filename);
        notificationIntent.putExtra(EXTRA_BODY, message);
        if (largeIcon != null) {
            notificationIntent.putExtra(EXTRA_LARGE_ICON, largeIcon);
        }
        getActivityContext().startService(notificationIntent);
    }

    private void showToast(String text) {
        Toast.makeText(getActivityContext(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    public SubjectMaterialsViewHolder createViewHolder(ViewGroup parent, int viewType) {
        View viewRow;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        int tempLayout = MVPUtility.isAvailableListViewItem((MVPModelRecycler) mModel, getActivityContext(), 0);
        MVPUtility.LayoutResolveStrategy layoutResolveStrategy = viewType1 -> {
            int temp = -1;
            if (itemLayout != R.layout.list_item_no_content && itemLayout != R.layout.list_item_no_internet) {
                switch (viewType1) {
                    case TYPE_FILE:
                        temp = R.layout.list_item_material_file;
                        break;
                    case TYPE_FOLDER:
                        temp = R.layout.list_item_material_folder;
                        break;
                }
            }
            return temp;
        };
        tempLayout = MVPUtility.resolveLayout(tempLayout, viewType, layoutResolveStrategy);
        viewRow = inflater.inflate(tempLayout, parent, false);
        setCreator((view, i) -> new SubjectMaterialsViewHolder(view, viewType));
        SubjectMaterialsViewHolder holder = (SubjectMaterialsViewHolder) this.creator.getViewHolder(viewRow, tempLayout);
        if (tempLayout != R.layout.list_item_no_internet && tempLayout != R.layout.list_item_no_content) {
            setupOnClickListener(holder, viewType);
            if (mMyFiles) {
                holder.mMenuButton.setVisibility(View.VISIBLE);
                holder.mMenuButton.setOnClickListener(view -> {
                    int position = holder.getAdapterPosition();
                    SubjectMaterialsItem item;
                    if (position > -1) {
                        item = (SubjectMaterialsItem) ((MVPModelRecycler) mModel).getItem(position);
                        String[] menuItems;
                        menuItems = ((DaggerApplication) getAppContext()).getSharedUser().getType() == 3 ? teacherMenuActions : studentMenuActions;
                        createItemMenu(item.getAddress(), item.getName(), position, menuItems).show();
                    }
                });
            } else {
                holder.mMenuButton.setVisibility(View.GONE);
            }
        }
        return holder;
    }

    @Override
    public void loadData(Bundle... bundle) {
        ((SubjectMaterialsFragment) getView()).showProgress();
        if (bundle.length > 0 &&
                !bundle[0].containsKey(EXTRA_KEY_GET_FOLDER) &&
                !mMyFiles
                && ((SubjectMaterialsModel) mModel).getHistoryCount() > 0) {
            populateRecyclerView(((SubjectMaterialsModel) mModel).getHistory(getItemCount()).first);
        } else {
            mModel.loadData(new MVPCallback<List<IRecyclerItem>>() {

                @Override
                public void onSuccess(List<IRecyclerItem> o) {
                    ((SubjectMaterialsFragment) getView()).hideProgress();
                    if (o.size() > 0) {
                        populateRecyclerView(o);
                        addHistory(new Pair<>(o, bundle[0].getString(EXTRA_KEY_FOLDER)));
                    } else {
                        ((MVPModelRecycler) mModel).clear();
                        ((SubjectMaterialsFragment) getView()).notifyDataSetChanged();
//                        int end = ((MVPModelRecycler)mModel).getItemCount();
//                        ((MVPModelRecycler)mModel).clear();
//                        ((SubjectMaterialsFragment) getView()).notifyItemRangeRemoved(0,end);
//                        populateRecyclerView(MVPUtility.initNoContent());
                    }
                    pageUp();
                }

                @Override
                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                    populateRecyclerView(MVPUtility.initNoContent());
                    ((SubjectMaterialsFragment) getView()).hideProgress();
                }
            }, bundle);
        }
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SubjectMaterialsViewHolder holder = (SubjectMaterialsViewHolder) viewHolder;
        IRecyclerItem item = ((MVPModelRecycler) mModel).getItem(position);
        if (!holder.allNullElements() && item instanceof SubjectMaterialsItem) {
            SubjectMaterialsItem mItem = (SubjectMaterialsItem) item;
            holder.mFileName.setText(mItem.getName());
            if (mItem.getTime() != null) {
                holder.mFileTime.setText(FacadeCommon.makeDate(mItem.getTime()));
            } else {
                holder.mFileTime.setText(getActivityContext().getString(R.string.just_now));
            }

            switch (getItemViewType(position)) {
                case TYPE_FILE:
                    holder.mSize.setText(FacadeCommon.readableFileSize(mItem.getSize(), false));
                    break;
                case TYPE_FOLDER:
                    holder.mOwnersName.setText(mItem.getOwnersName());
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        IRecyclerItem item = ((MVPModelRecycler) mModel).getItem(position);
        if (item instanceof SubjectMaterialsItem) {
            if (((SubjectMaterialsItem) item).getType().equals("f")) {
                return TYPE_FOLDER;
            }
        }
        return TYPE_FILE;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        Intent intent = new Intent();
        intent.setAction(UC_ACTION_DOWNLOAD_COMPLETE);
        getActivityContext().sendBroadcast(intent);
        Toast.makeText(getActivityContext(), getActivityContext().getString(R.string.error_loadig_file), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(byte[] response) {
        Intent intent = new Intent();
        intent.setAction(UC_ACTION_DOWNLOAD_COMPLETE);
        try {
            if (response != null) {
                String[] tempName = request.getUrl().split("/");
                String name = tempName[tempName.length - 1];
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name);
                FileOutputStream out = new FileOutputStream(file.getPath());
                out.write(response);
                out.close();
                Toast.makeText(getActivityContext(), getActivityContext().getString(R.string.file_saved_to_downloads), Toast.LENGTH_LONG).show();
                getActivityContext().sendBroadcast(intent);
            } else throw new Exception();
        } catch (Exception e) {
            Toast.makeText(getActivityContext(), getActivityContext().getString(R.string.error_loadig_file), Toast.LENGTH_LONG).show();
            getActivityContext().sendBroadcast(intent);
            e.printStackTrace();
        }
    }

    private void showRenameDialog(String file, String oldName, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivityContext());
        View promptView = layoutInflater.inflate(R.layout.dialog_input, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivityContext());
        alertDialogBuilder.setView(promptView);
        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        editText.setTextColor(ContextCompat.getColor(getActivityContext(), R.color.color_uc_ListText));
        editText.setText(oldName);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Готово", (dialog, id) -> {
                    if (FacadeCommon.isNetworkConnected(getActivityContext())) {
                        String newName = editText.getText().toString();
                        if (!newName.equals("")) {
                            mPreviousName = ((SubjectMaterialsItem) getItem(position)).getName();
                            ((SubjectMaterialsModel) mModel).renameFile(authString, file, newName, new MVPCallback<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    ((SubjectMaterialsItem) getItem(position)).setName(newName);
                                    ((SubjectMaterialsFragment) getView()).notifyItemChanged(position);
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    ((SubjectMaterialsItem) getItem(position)).setName(mPreviousName);
                                    Toast.makeText(getActivityContext(), getActivityContext().getString(R.string.error), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(getActivityContext(), "Название не может быть пустым.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivityContext(), "Проверте интернет соединение.", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("Отмена",
                (dialog, id) -> dialog.cancel());
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private AlertDialog.Builder createItemMenu(String code, String name, int position, String[] menuItems) {
        AlertDialog.Builder build = new AlertDialog.Builder(getActivityContext());
        build.setItems(menuItems, (dialog, which) -> {
            switch (which) {
                case 0:
                    showRenameDialog(code, name, position);
                    break;
                case 1:
                    deleteFile(code, position);
                    break;
                case 2:
                    ((SubjectMaterialsModel) mModel).shareFile(this.authString, code, null);
                    break;
            }
        });
        return build;
    }

    private void deleteFile(String code, final int position) {
        ((SubjectMaterialsFragment) getView()).showProgress();
        ((SubjectMaterialsModel) mModel).deleteFile(this.authString, code, new MVPCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if(((SubjectMaterialsModel) mModel).getItemCount()>position) {
                    ((SubjectMaterialsModel) mModel).remove(position);
                    if (getCurrentPage() <= ((SubjectMaterialsModel) mModel).getHistoryCount()) {
                        getHistory(getCurrentPage()).first.remove(position);
                    }
                    ((SubjectMaterialsFragment) getView()).notifyItemRemoved(position);
                    Toast.makeText(getActivityContext(), getActivityContext().getString(R.string.file_was_deleted), Toast.LENGTH_LONG).show();
                }
                ((SubjectMaterialsFragment) getView()).hideProgress();
            }

            @Override
            public void onError(Throwable throwable) {
                ((SubjectMaterialsFragment) getView()).hideProgress();
                Toast.makeText(getActivityContext(), getActivityContext().getString(R.string.error_deleting_file), Toast.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        });
    }

    public void uploadFile(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(UC_ACTION_DOWNLOAD_COMPLETE);

        File file = new File(uri.getPath());
        startNotificationService(file.getName(), getActivityContext().getString(R.string.upload_started), uri);
        ((SubjectMaterialsModel) mModel).uploadFile(authString, uri, new MVPCallback<List<IRecyclerItem>>() {

            @Override
            public void onSuccess(List<IRecyclerItem> o) {
                ((SubjectMaterialsModel) mModel).addAll(o);
                if(getCurrentPage()<((SubjectMaterialsModel)mModel).getHistoryCount()){
                    getHistory(getCurrentPage()).first.addAll(o);
                }
                ((MVPViewRecycler) getView()).notifyItemInserted(((SubjectMaterialsModel) mModel).getItemCount());
                showToast(getActivityContext().getString(R.string.file_uploaded));
                getActivityContext().sendBroadcast(intent);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                showToast(getActivityContext().getString(R.string.error_file_upload));
                getActivityContext().sendBroadcast(intent);
            }
        });
    }

    public void createFolder(String folderName) {
        ((SubjectMaterialsFragment) getView()).showProgress();
        ((SubjectMaterialsModel) mModel).createFolder(authString, folderName, new MVPCallback<List<IRecyclerItem>>() {

            @Override
            public void onSuccess(List<IRecyclerItem> o) {
                if (o != null) {
                    ((SubjectMaterialsModel) mModel).addAll(o);
                    if(getCurrentPage()<((SubjectMaterialsModel)mModel).getHistoryCount()){
                        getHistory(getCurrentPage()).first.addAll(o);
                    }
                    ((MVPViewRecycler) getView()).notifyItemInserted(((SubjectMaterialsModel) mModel).getItemCount());
                }
                ((SubjectMaterialsFragment) getView()).hideProgress();
                showToast(getActivityContext().getString(R.string.folder_created));
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                ((SubjectMaterialsFragment) getView()).hideProgress();
                showToast(getActivityContext().getString(R.string.error_creating_folder));
            }
        });
    }
}
