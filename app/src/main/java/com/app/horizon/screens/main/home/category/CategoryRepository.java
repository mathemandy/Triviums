package com.app.horizon.screens.main.home.category;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.app.horizon.core.network.models.UserProfile;
import com.app.horizon.core.store.offline.category.CategoryResponse;
import com.app.horizon.core.store.online.services.ApiService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;


public class CategoryRepository {

    private ApiService apiService;
    private FirebaseFirestore firestore;
    private UserProfile userProfile;
    boolean isExisting;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<CategoryResponse> mutableLiveData = new MutableLiveData<>();


    @Inject
    public CategoryRepository(ApiService apiService, FirebaseFirestore firestore,
                              UserProfile userProfile) {
        this.apiService = apiService;
        this.firestore = firestore;
        this.userProfile = userProfile;
    }

    public LiveData<CategoryResponse> getCategory() {
        fetchDataFromCloud(dataExists -> {
            if(!dataExists){
                fetchCategoryFromApi();
            }
        });
        return mutableLiveData;
    }


    private void fetchCategoryFromApi() {
        Single<Response<CategoryResponse>> responseObservable = apiService.fetchCategories();
        disposable.add(
                responseObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(categoryResponse -> {
                            Collections.sort(categoryResponse.body().getData(), (c1, c2) ->
                                    Integer.valueOf(c2.getId()) - Integer.valueOf(c1.getId()));
                            return categoryResponse;
                        })
                        .subscribeWith(new DisposableSingleObserver<Response<CategoryResponse>>() {
                            @Override
                            public void onSuccess(Response<CategoryResponse> response) {
                                firestore.collection("users")
                                        .document(userProfile.getUserUid())
                                        .collection("categories")
                                        .document("category")
                                        .set(response.body())
                                        .addOnSuccessListener(aVoid -> Log.e("Firestore Service",
                                                "Category has been added!"))
                                        .addOnFailureListener(e -> Log.e("Error!",
                                                "Could not write categories to cloud!"));

                                getCategory();
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        }));
    }


    private void fetchDataFromCloud(DataExistsCallback callback) {
        try {
        DocumentReference docRef = firestore.collection("users")
                .document(userProfile.getUserUid())
                .collection("categories")
                .document("category");

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    isExisting = true;
                    CategoryResponse response = document.toObject(CategoryResponse.class);
                    mutableLiveData.postValue(response);

                } else {
                    Log.e("Failed!", "No such document");
                }

            } else {
                Log.e("Error", task.getException().toString());
            }
            callback.onCallback(isExisting);
        });}
        catch (Exception e){
            Log.e("Horizon_", "Failed to get Data from Api, ignoring");
        }
    }


    public interface DataExistsCallback {
        void onCallback(boolean dataExists);
    }

}
