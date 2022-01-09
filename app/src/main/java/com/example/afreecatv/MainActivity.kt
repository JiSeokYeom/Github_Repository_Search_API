package com.example.afreecatv

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Layout
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.marginBottom
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var search_btn : ImageButton
    private lateinit var repository_input : EditText
    private lateinit var repoList: MutableList<Item>
    private lateinit var adapterRV: RvAdapter
    private lateinit var mainRV : RecyclerView
 //   private lateinit var progressBar: View
    private lateinit var repository : String
    private val PER_PAGE = 10
    private var page = 1
    private var loading = false
    private lateinit var activity_main : MainActivity
    private val baseUrl = "https://api.github.com"
    private val service = RetrofitClient.getClient(baseUrl)?.create(SearchService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity_main = this
        search_btn = findViewById(R.id.btn_search)
        repository_input = findViewById(R.id.repository_input)
        mainRV = findViewById(R.id.main_rv)
       // progressBar = findViewById(R.id.progress_bar)

        // 리사이클러뷰 설정
        mainRV.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        adapterRV = RvAdapter()
        mainRV.adapter=adapterRV

      //  progressBar.visibility = View.GONE

        search_btn.setOnClickListener {
            repoList = mutableListOf()
            page = 1

            repository = repository_input.text.toString()
          //  progressBar.visibility = View.VISIBLE

            val response = getRepository(repository)
            response.observe(this,{response ->
                response.items.let {
                    for(item in it){
                        repoList.add(item)
                        Log.d("tt1", it.toString())
                    }
                }
            })

            adapterRV.mData = repoList
           // adapterRV.notifyDataSetChanged()

            // EditText 입력값을 비워준다
            repository_input.text.clear()
        }

        mainRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1
                Log.d("몰라","$lastVisibleItemPosition , $itemTotalCount")
                // 스크롤이 끝에 도달했는지 확인
                if (!recyclerView.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && !loading) {
                    addItem()
                }

            }

        /*    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                 if(!recyclerView.canScrollVertically(1) && !loading){

                     progressBar.visibility = View.VISIBLE
                     addItem()
                }
            }*/
        })




    }


    fun getRepository(repository : String) : MutableLiveData<MainRvData> {
        val result : MutableLiveData<MainRvData> = MutableLiveData()
        val call: Call<MainRvData> = service!!.searchRepositories(repository,PER_PAGE, page)
        Log.d("페이지 테스트","$page")
       // progressBar.visibility = View.VISIBLE
        call.enqueue(object : Callback<MainRvData> {
            override fun onResponse(call: Call<MainRvData>, response: Response<MainRvData>) {
                Log.d("onResponse", "onResponse")
                if (response.code() == 200) {
                    Log.d("response.isSuccessful", "onResponse success")
                    result.value = response.body() as MainRvData
                    adapterRV.notifyItemRangeInserted((page - 1) * 10, 10)
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
        loading = true
      //  progressBar.visibility = View.VISIBLE
        Handler().postDelayed(Runnable {
            page++
            val response = getRepository(repository)
            response.observe(activity_main,{ response ->
                response.items.let {
                        for (item in it) {
                                repoList.add(item)
                                Log.d("repoList", repoList.toString())
                        }
                }
            })
            Log.d("개수", repoList.size.toString())

            adapterRV.mData = repoList
            loading = false
        }, 1000)
    }
}