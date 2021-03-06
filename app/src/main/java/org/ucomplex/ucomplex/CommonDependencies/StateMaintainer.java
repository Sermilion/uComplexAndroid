package org.ucomplex.ucomplex.CommonDependencies;

/**
 * ---------------------------------------------------
 * Created by @Sermilion on 07/11/16.
 * Project: UComplex
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Retains and maintain object'SubjectsListRepository state between configuration changes
 * in Activitys and Fragments.
 *
 * Created by @Sermilion on 7/011/16. <br>
 * Project: UComplex <br>
 *
 */
public class StateMaintainer {

    protected final String TAG = getClass().getSimpleName();

    private final String mStateMaintenerTag;
    private final WeakReference<FragmentManager> mFragmentManager;
    private StateMngFragment mStateMaintainerFrag;
    private boolean mIsRecreating;

    /**
     * Constructor
     */
    public StateMaintainer(FragmentManager fragmentManager, String stateMaintainerTAG) {
        mFragmentManager = new WeakReference<>(fragmentManager);
        mStateMaintenerTag = stateMaintainerTAG;
    }

    /**
     * Creates the Fragment responsible to maintain the objects.
     * @return  true: fragment just created
     */
    public boolean firstTimeIn() {
        try {
            mStateMaintainerFrag = (StateMngFragment)
                    mFragmentManager.get().findFragmentByTag(mStateMaintenerTag);

            // Criando novo RetainedFragment
            if (mStateMaintainerFrag == null) {
                Log.d(TAG, "Criando novo RetainedFragment " + mStateMaintenerTag);
                mStateMaintainerFrag = new StateMngFragment();
                mFragmentManager.get().beginTransaction()
                        .add(mStateMaintainerFrag, mStateMaintenerTag).commit();
                mIsRecreating = false;
                return true;
            } else {
                Log.d(TAG, "Retornando retained fragment existente " + mStateMaintenerTag);
                mIsRecreating = true;
                return false;
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "Erro firstTimeIn()");
            return false;
        }
    }

    /**
     * Return <strong>true</strong> it the current Activity was recreated at least one time
     * @return  If the Activity was recreated
     */
    public boolean wasRecreated() { return mIsRecreating; }


    /**
     * Insert the object to be preserved.
     * @param key   object'SubjectsListRepository TAG
     * @param obj   object to maintain
     */
    public void put(String key, Object obj) {
        mStateMaintainerFrag.put(key, obj);
    }

    /**
     * Insert the object to be preserved.
     * @param obj   object to maintain
     */
    public void put(Object obj) {
        put(obj.getClass().getName(), obj);
    }


    /**
     * Recovers the object saved
     * @param key   Object'SubjectsListRepository TAG
     * @param <T>   Object type
     * @return      Object saved
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key)  {
        return mStateMaintainerFrag.get(key);
    }

    /**
     * Checks the existence of a given object
     * @param key   Key to verification
     * @return      true: Object exists
     */
    public boolean hasKey(String key) {
        return mStateMaintainerFrag.get(key) != null;
    }


    /**
     * Fragment resposible to preserve objects.
     * Instantiated only once. Uses a hashmap to save objs
     */
    public static class StateMngFragment extends Fragment {
        private HashMap<String, Object> mData = new HashMap<>();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Grants that the fragment will be preserved
            setRetainInstance(true);
        }

        /**
         * Insert objects on the hashmap
         * @param key   Reference key
         * @param obj   Object to be saved
         */
        public void put(String key, Object obj) {
            mData.put(key, obj);
        }

        /**
         * Insert objects on the hashmap
         * @param object    Object to be saved
         */
        public void put(Object object) {
            put(object.getClass().getName(), object);
        }

        /**
         * Recovers saved object
         * @param key   Reference key
         * @param <T>   Object type
         * @return      Object saved
         */
        @SuppressWarnings("unchecked")
        public <T> T get(String key) {
            return (T) mData.get(key);
        }
    }

}
