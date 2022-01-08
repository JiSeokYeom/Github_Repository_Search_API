package com.example.afreecatv

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.LongDef
import androidx.annotation.NonNull
import androidx.core.widget.NestedScrollView
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
    private lateinit var progressBar: ProgressBar
    private lateinit var repository : String
    private val per_page = 10
    private var page = 1
    private var SW = false
    private lateinit var activity_main : MainActivity
    private val baseUrl = "https://api.github.com"
    private val service = GithubRepository.getClient(baseUrl)?.create(SearchService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity_main = this
        search_btn = findViewById(R.id.btn_search)
        repository_input = findViewById(R.id.repository_input)
        mainRV = findViewById(R.id.main_rv)
        progressBar = findViewById(R.id.progress_bar)

        progressBar.visibility = View.GONE



        search_btn.setOnClickListener {
            repoList = mutableListOf()
            page = 1
            repository = repository_input.text.toString()
            val response = getRepository(repository)
            response.observe(this,{response ->
                response.items.let {
                    for(item in it){
                        repoList.add(item)
                        Log.d("tt1", it.toString())
                    }
                }
            })
            adapterRV = RvAdapter()
            adapterRV.mData = repoList
            mainRV.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
            mainRV.adapter=adapterRV


            // EditText 입력값을 비워준다
            repository_input.text.clear()
        }
        mainRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                 if(!recyclerView.canScrollVertically(1) && !SW){
                     progressBar.visibility = View.VISIBLE

                     addItem()
            /*        val response = getRepository(repository)
                    response.observe(activity_main!!,{ response ->
                        response.items.let {
                            for(item in it){
                                repoList.add(item)
                                Log.d("tt2", it.toString())
                            }
                        }
                    })

                    adapterRV.mData = repoList
                    var rv = findViewById<RecyclerView>(R.id.main_rv)
                    rv.apply {
                        layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
                        adapter = adapterRV
                    }*/
                }
            }
        })



    }


    fun getRepository(repository : String) : MutableLiveData<MainRvData> {
        val result : MutableLiveData<MainRvData> = MutableLiveData()
        val call: Call<MainRvData> = service!!.searchRepositories(repository,per_page, page)
        Log.d("페이지 테스트","$page")
        call.enqueue(object : Callback<MainRvData> {
            override fun onResponse(call: Call<MainRvData>, response: Response<MainRvData>) {
                Log.d("onResponse", "onResponse")
                if (response.code() == 200) {
                    progressBar.visibility = View.GONE
                    Log.d("response.isSuccessful", "onResponse success")
                    result.value = response.body() as MainRvData
                    Log.d("tt2", "${response.body()}")
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

    fun addItem(){
        SW = true

        adapterRV = RvAdapter()

        Handler().postDelayed(Runnable {
            page++
            val response = getRepository(repository)
            response.observe(activity_main!!,{ response ->
                response.items.let {
                    for(item in it){
                        repoList.add(item)
                        Log.d("tt2", it.toString())
                    }
                }
            })

            adapterRV = RvAdapter()
            adapterRV.mData = repoList
            mainRV.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
            mainRV.adapter=adapterRV

            adapterRV.notifyDataSetChanged()
            progressBar.visibility = View.GONE
            SW = false
        }, 1000)
    }
}