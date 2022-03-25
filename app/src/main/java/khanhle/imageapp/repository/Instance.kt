package khanhle.imageapp.repository

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Instance {
    companion object {
        lateinit var jsonApi: JSONApi
        // var BASE_URL = "domain or subdomain name"
        var BASE_URL = "http://192.168.1.9:8000/api/"

        fun getRxJavaClient(): JSONApi {
            jsonApi = Retrofit.Builder().baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(JSONApi::class.java)
            return jsonApi
        }
    }
}