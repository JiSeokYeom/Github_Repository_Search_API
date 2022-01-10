package com.example.afreecatv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afreecatv.api.SearchApi
import com.example.afreecatv.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var search_btn : ImageButton
    private lateinit var repository_input : EditText
    private lateinit var repoList: MutableList<Item>
    private lateinit var adapterRV: RvAdapter
    private lateinit var mainRV : RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var repository : String
    private val PER_PAGE = 10
    private var page = 1
    private lateinit var activity_main : MainActivity
    private val baseUrl = "https://api.github.com"
    private val service = RetrofitClient.getClient(baseUrl)?.create(SearchApi::class.java)
    companion object{
         var loading = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity_main = this
        search_btn = findViewById(R.id.btn_search)
        repository_input = findViewById(R.id.repository_input)
        mainRV = findViewById(R.id.main_rv)
        progressBar = findViewById(R.id.progress_bar)

        // 리사이클러뷰 설정
        mainRV.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        adapterRV = RvAdapter()
        mainRV.adapter=adapterRV

        progressBar.visibility = View.GONE

        search_btn.setOnClickListener {
            repoList = mutableListOf() // 새로 검색하면 새로운 리스트를 표시하기 위해 초기화
            page = 1

            repository = repository_input.text.toString()

            //입력 값이 비어 있으면 알려줌
            if (repository.isBlank()) {
                Toast.makeText(this, "검색하실 Repository를 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else {

                val response = getRepository(repository)
                response.observe(this, { response ->
                    response.items.let {
                        for (item in it) {
                            repoList.add(item)
                        }
                    }
                })

                adapterRV.mData = repoList
                adapterRV.notifyDataSetChanged()

                // EditText 입력값을 비워준다
                repository_input.text.clear()
            }
        }

        mainRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                // 스크롤이 끝에 도달했는지 확인
                if (!recyclerView.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && !loading) {
                    moreItem()
                }

            }

        })




    }


    fun getRepository(repository : String) : MutableLiveData<MainRvData> {
        val result : MutableLiveData<MainRvData> = MutableLiveData()
        val call: Call<MainRvData> = service!!.searchRepositories(repository,PER_PAGE, page)
        call.enqueue(object : Callback<MainRvData> {
            override fun onResponse(call: Call<MainRvData>, response: Response<MainRvData>) {
                // 통신에 성공
                Log.d("onResponse", "onResponse")
                if (response.isSuccessful) {
                    // 응답을 잘 받은 경우
                    Log.d("response.isSuccessful", "onResponse success")
                    result.value = response.body() as MainRvData

                    // notifyItemRangeInserted를 통해 스크롤이 올라가는 것을 방지
                    adapterRV.notifyItemRangeInserted((page - 1) * 10, 10)
                } else {
                    // 통신에 성공 하지만 응답에 문제 있음
                    Log.d("response.isSuccessful", "onResponse fail")
                    Toast.makeText(applicationContext, "더 이상 아이템 개수가 없습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MainRvData>, t: Throwable) {
                // 통신 실패
                Log.d("onFailure", "onFailure")
            }
        })

        return result
    }

    fun moreItem(){
        loading = true
        progressBar.visibility = View.VISIBLE

        // 페이지가 불러와질때까지 delay를 줘서 로딩 하게 함
        Handler().postDelayed(Runnable {
            page++
            val response = getRepository(repository)
            response.observe(activity_main,{ response ->
                response.items.let {
                        for (item in it) {
                                repoList.add(item)
                        }
                }
            })
            adapterRV.mData = repoList
            progressBar.visibility = View.GONE
            loading = false
        }, 1500)
    }
}