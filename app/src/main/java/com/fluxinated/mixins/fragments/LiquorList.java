package com.fluxinated.mixins.fragments;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fluxinated.mixins.MainActivity;
import com.fluxinated.mixins.R;
import com.fluxinated.mixins.adapters.EndlessStaggeredRecyclerOnScrollListener;
import com.fluxinated.mixins.adapters.StaggeredRecyclerAdapter;
import com.fluxinated.mixins.animation.TransitionAnimator;
import com.fluxinated.mixins.database.GenerateTiles;
import com.fluxinated.mixins.enums.Bottle;
import com.fluxinated.mixins.enums.FragmentTags;
import com.fluxinated.mixins.floatingactionbuttons.floatingactionbutton.FloatingActionButton;
import com.fluxinated.mixins.floatingactionbuttons.floatingactionbutton.FloatingActionsMenu;
import com.fluxinated.mixins.model.CardInformation;
import com.fluxinated.mixins.model.Liquor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static android.animation.Animator.AnimatorListener;
import static com.fluxinated.mixins.enums.Bottle.BOTTLE1;

/**
 * Created by User on 08/10/2015.
 */
public class LiquorList extends BaseLiquorFragment implements EndlessStaggeredRecyclerOnScrollListener.LoadCallback,EndlessStaggeredRecyclerOnScrollListener.ScrollCallback {

    private RecyclerView recyclerStaggeredView;
    private ArrayList<CardInformation> mCardInformation;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private String CARDINFORMATION_TAG;
    private StaggeredRecyclerAdapter mStaggeredRecyclerAdapter;
    private GenerateTiles mGenerateTiles;
    private EndlessStaggeredRecyclerOnScrollListener mEndlessStaggeredRecyclerOnScrollListener;
    private TransitionAnimator mTransitionAnimatorFloatingActionButton,mTransitionAnimatorSearchView;
    private Map<String,View> mBottleSettingsFields;
    private FloatingActionsMenu mFloatingActionsMenu;
    private CardView mSearchView;
    private EditText mSearchField;
    private Button mSearchCloseButton;
    private AnimatorListener mTransitionAnimationListener;
    private LoadMoreTask mLoadMore;
    private LinearLayout mtoggleSettingsLayout;
    //private boolean isSearchViewToggled = false;
    private TextWatcher mSearchViewListener = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }

        @Override
        public void afterTextChanged(Editable s)
        {
            if(mSearchView.getVisibility() == View.VISIBLE)
            {
                mCardInformation = new ArrayList<>();
                mGenerateTiles = new GenerateTiles((MainActivity) getActivity(), mCardInformation);
                mGenerateTiles.setMaterialPalette(getActivity().getResources().getStringArray(R.array.material_palette));
                mGenerateTiles.fetch(s.toString());
                mStaggeredRecyclerAdapter.setList(mCardInformation);
                mStaggeredRecyclerAdapter.notifyDataSetChanged();
            }
            //((MainActivity)getActivity())
        }
    };

    public LiquorList(){}

    public static BaseLiquorFragment getInstance(String param)
    {
        LiquorList mLiquorList = new LiquorList();
        Bundle mBundle = new Bundle();
        mBundle.putString(FRAGMENT_KEY, param);
        mLiquorList.setArguments(mBundle);
        return mLiquorList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.staggeredgridview, container, false);
        this.recyclerStaggeredView = (RecyclerView) view.findViewById(R.id.staggeredgridview);
        //this.mSearchView = (CardView)view.findViewById(R.id.search_view);
        //this.mSearchField = (EditText)view.findViewById(R.id.search_field);
        //this.mSearchCloseButton = (Button)view.findViewById(R.id.search_close_btn);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.e("OnActivityCreated", "OnActivityCreated");

        CARDINFORMATION_TAG = super.mParam;
        this.mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.HORIZONTAL);
        this.mStaggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        this.mEndlessStaggeredRecyclerOnScrollListener = new EndlessStaggeredRecyclerOnScrollListener(this.mStaggeredGridLayoutManager);
        this.mEndlessStaggeredRecyclerOnScrollListener.setLoadCallback(this);
        this.mEndlessStaggeredRecyclerOnScrollListener.setScrollCallback(this);
            try
            {
                mCardInformation = savedInstanceState.getParcelableArrayList(CARDINFORMATION_TAG);
            }catch (NullPointerException exp)
            {
                //ignore
                //Log.e("cardinformaton","is not null");
            }
            if(mCardInformation == null)
            {
                mCardInformation = new ArrayList<>();
                //getArguments().putParcelableArrayList(CARDINFORMATION_TAG,mCardInformation);
            }
            //Log.e("cardinformaton size",mCardInformation.size()+"");
        if(mGenerateTiles == null)
            mGenerateTiles = new GenerateTiles((MainActivity)getActivity(),mCardInformation);
            mGenerateTiles.setMaterialPalette(getActivity().getResources().getStringArray(R.array.material_palette));
            mGenerateTiles.fetch(null);

        this.mStaggeredRecyclerAdapter = new StaggeredRecyclerAdapter();
        this.recyclerStaggeredView.setAdapter(this.mStaggeredRecyclerAdapter);
        this.recyclerStaggeredView.setLayoutManager(this.mStaggeredGridLayoutManager);
        this.recyclerStaggeredView.addOnScrollListener(this.mEndlessStaggeredRecyclerOnScrollListener);
        this.mStaggeredRecyclerAdapter.setList(mCardInformation);
        this.mStaggeredRecyclerAdapter.setListener((MainActivity) getActivity());
        this.mStaggeredRecyclerAdapter.setImageLoader(((MainActivity) getActivity()).getImageLoader());

    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Log.e("cardinformaton", "outstate");
        outState.putParcelableArrayList(CARDINFORMATION_TAG, mCardInformation);
    }

    @Override
    public void OnLoadMore(int page)
    {
        if(mLoadMore == null)
        {
            mLoadMore = new LoadMoreTask(page);
            mLoadMore.execute();
            if(mLoadMore.getStatus() == AsyncTask.Status.FINISHED)
                mLoadMore = null;
        }

    }

    @Override
    public void OnScrolled(int FirstVisibleItem, int VisibleItemCount, int TotalItemCount, int LastTotalItemCount)
    {

    }

    @Override
    public void OnScrollStateChanged(int previous_state, int newState)
    {
        if(mFloatingActionsMenu.isExpanded())
            mFloatingActionsMenu.toggle();
        //Log.e("RecyclerView",recyclerView.getCh);
        if (newState == RecyclerView.SCROLL_STATE_IDLE)
        {
            mTransitionAnimatorFloatingActionButton.setAnimationDuration(400)
                    .setTranslationX(0)
                    .setInterpolator(new OvershootInterpolator(0.9F))
                    .setStartDelay(10);
                   /* mViewAnimator.setAnimationDuration(400)
                            .setTranslationX(0)
                            .setInterpolator(new OvershootInterpolator(0.9F))
                            .setStartDelay(10);*/
        }
        //  mFabButton.moveUp(50F);
        else if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
        {
            mTransitionAnimatorFloatingActionButton.setAnimationDuration(400)
                    .setTranslationX(-100)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setStartDelay(10);
                   /* mViewAnimator.setAnimationDuration(400)
                            .setTranslationX(-100)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .setStartDelay(10);*/
        }

        if (previous_state == EndlessStaggeredRecyclerOnScrollListener.PREVIOUS_SCROLL_STATE_DEFAULT ||
                newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_IDLE)
        {
            mTransitionAnimatorFloatingActionButton.animate();
            //mViewAnimator.startAnimation();
            Log.i("OnScrollStateChanged", newState + "");

        }
    }

    class LoadMoreTask extends AsyncTask<Void,Void,Void>
    {
        int page;

        public LoadMoreTask(int page)
        {
            this.page = page;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            if(mSearchView.getVisibility() == View.GONE)
                mGenerateTiles.fetch(null);
            else
                mGenerateTiles.fetch(mSearchField.getText().toString());
            mStaggeredRecyclerAdapter.notifyItemInserted(page);
            super.onPostExecute(aVoid);
        }

    }

    @Override
    protected void initializeViews(ViewGroup vg)
    {
        try {
            for (int i = 0; i < vg.getChildCount(); i++) {

                if (vg.getChildAt(i) instanceof ViewGroup) {
                    if(vg.getChildAt(i).getTag() != null)
                        if(vg.getChildAt(i).getTag().equals("floatingactionsmenu"))
                        {
                            this.mTransitionAnimatorFloatingActionButton = new TransitionAnimator().setViewToBeAnimated(vg.getChildAt(i));
                            this.mFloatingActionsMenu = (FloatingActionsMenu) vg.getChildAt(i);
                        }
                        else if(vg.getChildAt(i).getTag().equals("cardviewsearch"))
                        {
                            this.mTransitionAnimatorSearchView = new TransitionAnimator().setViewToBeAnimated(vg.getChildAt(i));
                            this.mSearchView = (CardView) vg.getChildAt(i);
                        }

                    initializeViews((ViewGroup) vg.getChildAt(i));
                }
                else {
                    //if(vg.getChildAt(i).getTag() != null)
                    if(vg.getChildAt(i) instanceof FloatingActionButton && vg.getChildAt(i).getTag().equals("fab") || vg.getChildAt(i) instanceof Button)
                    {
                        vg.getChildAt(i).setOnClickListener(this);
                        if(vg.getChildAt(i).getId() == R.id.search_close_btn)
                            this.mSearchCloseButton = (Button) vg.getChildAt(i);
                    }
                    else if(vg.getChildAt(i) instanceof EditText)
                    {
                        this.mSearchField = (EditText) vg.getChildAt(i);
                        this.mSearchField.setText(null);
                        this.mSearchField.setHint("Search Cocktail");
                        this.mSearchField.addTextChangedListener(this.mSearchViewListener);
                    }

                }

            }
        } catch (NullPointerException exp) {
            Log.e("Null", "ViewGroup is Null");
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    private void togglesearch(boolean visible)
    {
        Log.e("visible?",visible+"");
        if(visible)
        {
            mSearchView.setVisibility(View.INVISIBLE);
            mTransitionAnimatorSearchView.setAnimationDuration(400)
                    .setAlpha(1)
                    .setTranslationY(10)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setStartDelay(10)
                    .setAnimationListener(new AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    mSearchView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {

                }

                @Override
                public void onAnimationCancel(Animator animation)
                {

                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {

                }
            });
        }
        else
        {
            mTransitionAnimatorSearchView.setAnimationDuration(400)
                    .setTranslationY(-10)
                    .setAlpha(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setStartDelay(10)
                    .setAnimationListener(new AnimatorListener()
                    {
                        @Override
                        public void onAnimationStart(Animator animation)
                        {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            mSearchView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation)
                        {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation)
                        {

                        }
                    });
        }
        mTransitionAnimatorSearchView.animate();
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.floating_side_button_1:
                ((MainActivity)getActivity()).OnFragmentChange(FragmentTags.MixLiquor,null);
                break;

            case R.id.floating_side_button_2:
                mFloatingActionsMenu.toggle();
                this.togglesearch(this.mSearchView.getVisibility() == View.GONE);
                break;

            case R.id.floating_side_button_3:
                //((MainActivity)getActivity()).OnFragmentChange(FragmentTags.SETTINGS, null);
                toggleSettings();
                break;

            case R.id.search_close_btn:
                if(!this.mSearchField.getText().toString().equals(""))
                {
                    //Log.e("edittext",this.mSearchField.getText().toString());
                    this.mSearchField.setText("");
                }
                else
                    this.togglesearch(this.mSearchView.getVisibility() == View.GONE);
                break;
        }
    }
    public int generateId(Bottle b)
    {
        switch(b)
        {
            case BOTTLE1:
                return R.id.BOTTLE1;
            case BOTTLE2:
                return R.id.BOTTLE2;
            case BOTTLE3:
                return R.id.BOTTLE3;
            case BOTTLE4:
                return R.id.BOTTLE4;
            case BOTTLE5:
                return R.id.BOTTLE5;
            default:
                return R.id.BOTTLE6;
        }
    }
    private void toggleSettings()
    {
        if(mDialog == null)
        {

            mDialog = new AlertDialog.Builder(getActivity());
            AlertDialog al = mDialog.create();


            //LinearLayout.LayoutParams mLayoutParams = (LinearLayout.LayoutParams)layout.getLayoutParams();
            //mLayoutParams.width =


            if(mBottleSettingsFields == null && mtoggleSettingsLayout == null) {

                mtoggleSettingsLayout = new LinearLayout(getActivity());
                mtoggleSettingsLayout.setOrientation(LinearLayout.VERTICAL);
                mBottleSettingsFields = Collections.synchronizedMap(new LinkedHashMap<String, View>());

                View layout;
                EditText mEditText;

                for (Bottle b : Bottle.values()) {
                    layout = LayoutInflater.from(getActivity()).inflate(R.layout.bottle_settings, null);
                    mEditText = ((EditText) layout.findViewById(R.id.liquor_name_settings));
                    mEditText.setId(generateId(b));
                    mEditText.setHint("Enter Bottle Name");
                    mBottleSettingsFields.put(b.name(), mEditText);
                    mEditText = ((EditText) layout.findViewById(R.id.liquor_name_settings));
                    mEditText.setId(generateId(b));
                    mEditText.setHint("Set Maximum Volume");
                    mBottleSettingsFields.put(b.name() + BOTTLE_VOLUME, mEditText);
                    mtoggleSettingsLayout.addView(layout);
                    break;
                }
            }
            //al.setView(LayoutInflater.from(getActivity()).inflate(R.layout.liquor_information_dialog_big,null));
            al.setView(mtoggleSettingsLayout);
            al.setCancelable(true);

            al.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            al.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            al.show();
           /* WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(al.getWindow().getAttributes());
            lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getActivity().getResources().getDisplayMetrics());
            al.getWindow().setAttributes(lp);*/

        }

    }



}
