package khanhle.imageapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import khanhle.imageapp.model.Image
import khanhle.imageapp.repository.Instance
import khanhle.imageapp.repository.JSONApi

class ImageOfCategoryViewModel : ViewModel() {

    private val images: MutableLiveData<List<Image>> by lazy {
        MutableLiveData<List<Image>>().also {
            loadImages()
        }
    }

    private val premiumImages: MutableLiveData<List<Image>> by lazy {
        MutableLiveData<List<Image>>().also {
            loadPremiumImages()
        }
    }

    private var category_name = ""

    fun getImages(category_name: String): LiveData<List<Image>> {
        this.category_name = category_name
        return images
    }

    fun getPremiumImages(category_name: String): LiveData<List<Image>> {
        this.category_name = category_name
        return premiumImages
    }

    private fun loadImages() {
        var jsonApi: JSONApi = Instance.getRxJavaClient()
        jsonApi.getImageOfCategory(category_name).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Image>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<Image>) {
                    images.postValue(t)
                }

                override fun onError(e: Throwable) {
                    images.postValue(null)
                }

                override fun onComplete() {
                }
            })
    }

    private fun loadPremiumImages() {
        var jsonApi: JSONApi = Instance.getRxJavaClient()
        jsonApi.getPremiumImageOfCategory(category_name).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Image>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<Image>) {
                    premiumImages.postValue(t)
                }

                override fun onError(e: Throwable) {
                    premiumImages.postValue(null)
                }

                override fun onComplete() {
                }
            })
    }
}