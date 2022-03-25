package khanhle.imageapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import khanhle.imageapp.model.Image
import khanhle.imageapp.repository.Instance
import khanhle.imageapp.repository.JSONApi
import org.json.JSONArray

class RandomImageViewModel : ViewModel() {

    private var gson = Gson()

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

    fun getImages(): LiveData<List<Image>> {
        return images
    }

    fun getAllImages(): LiveData<List<Image>> {
        return premiumImages
    }

    private fun loadImages() {
        var jsonApi: JSONApi = Instance.getRxJavaClient()
        jsonApi.getAllImage().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Image>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<Image>) {
                    var jsonArray = JSONArray(gson.toJson(t))
                    Log.d("Random_image_response", jsonArray.toString())
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
        jsonApi.getAllPremiumImages().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Image>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<Image>) {
                    var jsonArray = JSONArray(gson.toJson(t))
                    Log.d("Random_image_response", jsonArray.toString())
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