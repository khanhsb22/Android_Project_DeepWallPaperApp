package khanhle.imageapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import khanhle.imageapp.model.Category
import khanhle.imageapp.repository.Instance
import khanhle.imageapp.repository.JSONApi

class CategoryViewModel : ViewModel() {

    private val categorys: MutableLiveData<List<Category>> by lazy {
        MutableLiveData<List<Category>>().also {
            loadCategorys()
        }
    }

    fun getCategorys(): LiveData<List<Category>> {
        return categorys
    }

    private fun loadCategorys() {
        var jsonApi: JSONApi = Instance.getRxJavaClient()
        jsonApi.getAllCategory().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Category>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<Category>) {
                    categorys.postValue(t)
                }

                override fun onError(e: Throwable) {
                    categorys.postValue(null)
                }

                override fun onComplete() {
                }
            })
    }
}