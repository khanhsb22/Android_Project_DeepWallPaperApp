package khanhle.imageapp.repository

import androidx.room.Dao
import io.reactivex.Observable
import khanhle.imageapp.model.Category
import khanhle.imageapp.model.Image
import retrofit2.http.GET
import retrofit2.http.Path

// JSONApi using for CategoryActivity, ImageOfCategoryActivity and RandomFragment
interface JSONApi {
    // These methods call from Laravel
    @GET("getAllCategory")
    fun getAllCategory(): Observable<List<Category>>

    @GET("getImageOfCategory/{name}")
    fun getImageOfCategory(@Path("name") category_name: String): Observable<List<Image>>

    // For premium
    @GET("getPremiumImageOfCategory/{name}")
    fun getPremiumImageOfCategory(@Path("name") category_name: String): Observable<List<Image>>

    @GET("getAllImage")
    fun getAllImage(): Observable<List<Image>>

    // For premium
    @GET("getAllPremiumImages")
    fun getAllPremiumImages(): Observable<List<Image>>

}