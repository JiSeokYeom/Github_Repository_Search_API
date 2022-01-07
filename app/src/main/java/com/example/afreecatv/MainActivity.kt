package com.example.afreecatv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var search_btn : ImageButton
    private lateinit var repository_input : EditText
    private lateinit var repoList: MutableList<Item>
    private lateinit var adapterRV: RvAdapter
    private lateinit var mainRV : RecyclerView
    val baseUrl = "https://api.github.com"
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(SearchService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        search_btn = findViewById(R.id.btn_search)
        repository_input = findViewById(R.id.repository_input)
        mainRV = findViewById(R.id.main_rv)

        search_btn.setOnClickListener {
            repoList = mutableListOf()


            var repository = repository_input.text.toString()
            val response = getRepository(repository)
            response.observe(this,{response ->
                response.items.let {
                    for(item in it){
                        repoList.add(item)
                        Log.d("tt1", item.toString())
                    }
                }
            })
            adapterRV = RvAdapter()
            adapterRV.mData = repoList
            var rv = findViewById<RecyclerView>(R.id.main_rv)
            rv.apply {
                layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
                adapter = adapterRV
            }

        }
    }


    fun addData(){

    }

    private fun getRepository(repository : String) : MutableLiveData<MainRvData> {
        val result : MutableLiveData<MainRvData> = MutableLiveData()
        val call: Call<MainRvData> = service.searchRepositories(repository)

        call.enqueue(object : Callback<MainRvData> {
            override fun onResponse(call: Call<MainRvData>, response: Response<MainRvData>) {
                Log.d("onResponse", "onResponse")
                if (response.isSuccessful) {
                    Log.d("response.isSuccessful", "onResponse success")
                    result.value = response.body() as MainRvData
                    Log.d("test", response.body().toString())
                } else {
                    Log.d("response.isSuccessful", "onResponse fail")
                }
            }

            override fun onFailure(call: Call<MainRvData>, t: Throwable) {
                Log.d("onFailure", "onFailure")
            }
        })

        return result
    }
}