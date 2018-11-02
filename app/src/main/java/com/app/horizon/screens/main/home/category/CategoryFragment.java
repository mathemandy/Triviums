package com.app.horizon.screens.main.home.category;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.horizon.HorizonMainApplication;
import com.app.horizon.R;
import com.app.horizon.core.base.BaseFragment;
import com.app.horizon.core.store.offline.category.Category;
import com.app.horizon.databinding.FragmentCategoryBinding;
import com.app.horizon.screens.main.HomeScreen;
import com.app.horizon.screens.main.home.StageActivityScreen;
import com.app.horizon.screens.main.home.stage.StagesFragmentScreen;
import com.app.horizon.screens.main.profile.ContainerProviderId;
import com.app.horizon.utils.ConnectivityReceiver;
import com.app.horizon.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import me.aartikov.alligator.Navigator;
import me.aartikov.alligator.annotations.RegisterScreen;

/**
 * A simple {@link Fragment} subclass.
 */

@RegisterScreen(HomeScreen.class)
public class CategoryFragment extends BaseFragment implements ContainerProviderId {

    private FragmentCategoryBinding binding;
    private RecyclerView recyclerView;
    private List<Category> categoryList = new ArrayList<>();
    private CategoryFragmentAdapter adapter;
    private CompositeDisposable disposable = new CompositeDisposable();
    private RecyclerView.LayoutManager layoutManager;

    private Navigator mNavigator = HorizonMainApplication.getNavigator();
//
//    @Inject
//    ViewModelProvider.Factory factory;
//    private CategoryViewModel viewModel;

    public  static  final int MobileData = 2;
    public static final int WifiData = 1;
    boolean isConnected;
    @Inject
    ConnectivityReceiver connectivityReceiver;
    @Inject
    Utils utils;

    public CategoryFragment() {
        // Required empty public constructor
    }


//    @Override
//    public CategoryViewModel getViewModel() {
////        viewModel = ViewModelProviders.of(this, factory).get(CategoryViewModel.class);
//        return null;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container,
                false);
        View view = binding.getRoot();

        initRecyclerView();

        connectivityReceiver.observe(this, connectionModel -> {
            try{
            if(connectionModel.isConnected()){
                isConnected = true;
//                showCategory();
            } else {
                isConnected = false;
                utils.showSnackbar(getActivity(), getResources().getString(R.string.newtwork_unavailable));
//                showCategory();
            }
            } catch (Exception e){
                utils.showSnackbar(getActivity(), "Error fetching data");
            }
        });

        return view;
    }

    private void initRecyclerView(){
        categoryList.add(new Category("1","First", "https://firebasestorage.googleapis.com/v0/b/labs-bac05.appspot.com/o/download%20(2).jpeg?alt=media&token=3dbf991c-c9a5-4dae-acf7-de4041f20195", "43" ));
        categoryList.add(new Category("2","First", "https://firebasestorage.googleapis.com/v0/b/labs-bac05.appspot.com/o/download%20(2).jpeg?alt=media&token=3dbf991c-c9a5-4dae-acf7-de4041f20195", "43" ));
        categoryList.add(new Category("3","First", "https://firebasestorage.googleapis.com/v0/b/labs-bac05.appspot.com/o/download%20(2).jpeg?alt=media&token=3dbf991c-c9a5-4dae-acf7-de4041f20195", "43" ));
        categoryList.add(new Category("4","First", "https://firebasestorage.googleapis.com/v0/b/labs-bac05.appspot.com/o/download%20(2).jpeg?alt=media&token=3dbf991c-c9a5-4dae-acf7-de4041f20195", "43" ));
        categoryList.add(new Category("5","First", "https://firebasestorage.googleapis.com/v0/b/labs-bac05.appspot.com/o/download%20(2).jpeg?alt=media&token=3dbf991c-c9a5-4dae-acf7-de4041f20195", "43" ));
        adapter = new CategoryFragmentAdapter(getActivity(), categoryList, categoryListener);
        recyclerView = binding.categoryView;
        layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Fetches list of Category
     */
    public void showCategory(){
//        viewModel.getCategory().observe(getViewLifecycleOwner(), category -> {
//            binding.progressBar.setVisibility(View.GONE);
//            binding.loadingTxt.setVisibility(View.GONE);
//            if (category != null) {
//                categoryList.clear();
//                categoryList.addAll(category.getData());
//                adapter.notifyDataSetChanged();
//            }
//        });
    }

    private View.OnClickListener categoryListener = view -> {


        Category category = (Category) view.getTag();

        mNavigator.goForward(new StagesFragmentScreen(category.getId(), category.getName()));
//
//            Intent intent = new Intent(getActivity(), StageActivity.class);
//            intent.putExtra("CategoryId", category.getId());
//            intent.putExtra("categoryName", category.getName());
//            getActivity().startActivity(intent);
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @Override
    public int getContainerId() {
        return R.id.category_container;
    }
}
