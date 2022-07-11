package github.tornaco.android.thanos.infinite;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.AndroidViewModel;

import com.elvishew.xlog.XLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import github.tornaco.android.rhino.plugin.Verify;
import github.tornaco.android.thanos.common.AppListModel;
import github.tornaco.android.thanos.core.app.ThanosManager;
import github.tornaco.android.thanos.core.app.infinite.RemovePackageCallback;
import github.tornaco.android.thanos.core.pm.AppInfo;
import github.tornaco.android.thanos.core.util.Rxs;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import rx2.android.schedulers.AndroidSchedulers;
import util.Consumer;

public class InfiniteZAppsViewModel extends AndroidViewModel {
    private final ObservableBoolean isDataLoading = new ObservableBoolean(false);
    protected final List<Disposable> disposables = new ArrayList<>();
    protected final ObservableArrayList<AppListModel> listModels = new ObservableArrayList<>();

    public InfiniteZAppsViewModel(@NonNull Application application) {
        super(application);
    }

    void start() {
        loadModels();
    }

    @Verify
    private void loadModels() {
        if (isDataLoading.get()) return;
        isDataLoading.set(true);
        disposables.add(Single
                .create((SingleOnSubscribe<List<AppListModel>>) emitter ->
                        emitter.onSuccess(getInfiniteZApps()))
                .map(listModels -> {
                    Collections.sort(listModels);
                    return listModels;
                })
                .flatMapObservable((Function<List<AppListModel>, ObservableSource<AppListModel>>) Observable::fromIterable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> listModels.clear())
                .subscribe(listModels::add, Rxs.ON_ERROR_LOGGING, () -> isDataLoading.set(false)));
    }

    private List<AppListModel> getInfiniteZApps() {
        ThanosManager thanosManager = ThanosManager.from(getApplication());
        if (!thanosManager.isServiceInstalled()) {
            return new ArrayList<>(0);
        }
        List<AppListModel> res = new ArrayList<>();
        for (AppInfo appInfo : thanosManager.getInfiniteZ().getInstalledPackages()) {
            XLog.w("getInfiniteZApps, pkg: " + appInfo.getPkgName());
            AppListModel model = new AppListModel(appInfo);
            res.add(model);
        }
        return res;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

    public ObservableBoolean getIsDataLoading() {
        return this.isDataLoading;
    }

    public ObservableArrayList<AppListModel> getListModels() {
        return this.listModels;
    }

    public void uninstall(AppInfo appInfo, Runnable onSuccess, Consumer<String> onError) {
        ThanosManager thanosManager = ThanosManager.from(getApplication());
        thanosManager.getInfiniteZ().removePackage(appInfo.getPkgName(), new RemovePackageCallback() {
            @Override
            public void onSuccessMain() {
                onSuccess.run();
                loadModels();
            }

            @Override
            public void onErrorMain(String errorMessage, int errorCode) {
                onError.accept(errorMessage);
                loadModels();
            }
        });
    }
}
