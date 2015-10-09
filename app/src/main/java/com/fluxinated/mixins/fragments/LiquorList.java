package com.fluxinated.mixins.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.fluxinated.mixins.MainActivity;
import com.fluxinated.mixins.R;
import com.fluxinated.mixins.adapters.EndlessStaggeredRecyclerOnScrollListener;
import com.fluxinated.mixins.adapters.StaggeredRecyclerAdapter;
import com.fluxinated.mixins.animation.TransitionAnimator;
import com.fluxinated.mixins.database.GenerateTiles;
import com.fluxinated.mixins.enums.FragmentTags;
import com.fluxinated.mixins.floatingactionbuttons.floatingactionbutton.FloatingActionButton;
import com.fluxinated.mixins.model.CardInformation;

import java.util.ArrayList;

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
    private TransitionAnimator mTransitionAnimator;

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
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
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
        Log.e("cardinformaton size",mCardInformation.size()+"");
            mGenerateTiles = new GenerateTiles((MainActivity)getActivity(),mCardInformation);
            mGenerateTiles.setMaterialPalette(getActivity().getResources().getStringArray(R.array.material_palette));
            mGenerateTiles.fetch();

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
        outState.putParcelableArrayList(CARDINFORMATION_TAG,mCardInformation);
    }

    @Override
    public void OnLoadMore(int page)
    {
        new LoadMoreTask(page);
    }

    @Override
    public void OnScrolled(int FirstVisibleItem, int VisibleItemCount, int TotalItemCount, int LastTotalItemCount)
    {

    }

    @Override
    public void OnScrollStateChanged(int previous_state, int newState)
    {
        //Log.e("RecyclerView",recyclerView.getCh);
        if (newState == RecyclerView.SCROLL_STATE_IDLE)
        {
            mTransitionAnimator.setAnimationDuration(400)
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
            mTransitionAnimator.setAnimationDuration(400)
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
            mTransitionAnimator.animate();
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
            mGenerateTiles.fetch();
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
                            this.mTransitionAnimator = new TransitionAnimator().setViewToBeAnimated(vg.getChildAt(i));
                        }
                    initializeViews((ViewGroup) vg.getChildAt(i));
                }
                else {
                    //if(vg.getChildAt(i).getTag() != null)
                    if(vg.getChildAt(i) instanceof FloatingActionButton && vg.getChildAt(i).getTag().equals("fab"))
                    {
                        vg.getChildAt(i).setOnClickListener(this);
                    }

                }

            }
        } catch (NullPointerException exp) {
            Log.e("Null", "ViewGroup is Null");
        } catch (Exception exp) {
            exp.printStackTrace();
        }
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
                Toast.makeText(getActivity(), "search", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
