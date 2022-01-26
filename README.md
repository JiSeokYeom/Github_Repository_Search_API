# Github Repository Search Service  :tophat:

## 어플 설명

### 
+ GitHub API를 사용하여 GitHub Repository를 조회하고 리스트를 보여주는 어플 입니다.
+ API 명세서와 예시 화면을 참고하여 요구사항을 구현합니다.

### 필요한 기능들
+ 검색하고 싶은 Repository를 입력 받고, GitHub Search Repositories API의 응답으로 받아온 데이터를 사용하여 리스트를 표시합니다.
+ 리스트에 반드시 포함되어야 할 요소는 유저아바타(avatar_url), 	Repository명(full_name), 사용한 언어(language) 입니다.
+ 리스트를 상하 스크롤이 가능하도록 표시합니다.
+ 페이징 기능 구현 : 한번에 10개의 데이터를 불러오고 리스트의 마지막에 도달하면 다음 페이지 데이터를 로딩해서 표시해줍니다. 로딩중 일 때 리스트 맨 밑에 로딩바를 활용하여 로딩중임을 알 수 있도록 해야 합니다.
+ 새로 검색을 하면 기존의 리스트는 제거하고, 검색결과에 따른 새로운 리스트를 표시합니다.

## API 명세서 

### 1. Search Repositories API
+ 해당 API 출처 : [https://docs.github.com/en/rest/reference/search#search-repositories](https://docs.github.com/en/rest/reference/search#search-repositories)
+ API URL : https://api.github.com/search/repositories
+ API Method : GET "/search/repositories"


### 2. Query Parameter
+ q : String 타입으로, 해당 키워드를 가진 Repository들을 검색합니다.
+ per_page : Integer  타입으로 한 페이지당 몇 개의 데이터를 받을 것인지 설정해줍니다. 본 과제에서는 10으로 설정해주시면 됩니다.
+ page : Integer 타입으로, 전체 데이터를 per_page로 나누었을 때 원하는 page를 불러옵니다.

### 3. Response

- 전체 Response중에, 사용할 filed는 아래와 같습니다.

| Field | Type | Description |
| --- | --- | --- |
| items | Array | Repository에 대한 다양한 데이터를 담고 있습니다. |
| full_name | String | Repository명을 나타냅니다. |
| owner | Object | 유저 프로필관련 정보를 담고 있습니다. |
| avatar_url | String | 유저 아바타 이미지 주소를 나타냅니다. |
| language | String | 해당 프로젝트에서 사용한 언어를 나타냅니다. |

### 설명
API를 사용하기 위해 Retrofit2를 사용한다.
### Retrofit이란?
- 통신 라이브러리 중 가장 많이 사용되는 대표적인 라이브러리
- Android에서 REST API통신을 지원하기 위한 라이브러리
### Retrofit 장점
- 빠른 성능
- 간단한 구현(반복된 작업을 라이브러리 넘겨서 처리)
- 가독성 (Annotation(애노테이션) 사용으로 코드의 가독성이 뛰어남, 직관적인 설계가 가능)
- 동기/비동기 쉬운 구현 
### 필요한 기능 구현 방법
### 검색하고 싶은 Repository를 입력 받고, GitHub Search Repositories API의 응답으로 받아온 데이터를 사용하여 리스트를 표시합니다.
 activity_main에 Repository를 입력받을 수 있는 EditText / 검색 할 수 있는 ImageButton / 받아온 데이터를 표시해주는 recyclerview / 페이징 기능을 위한 ProgressBar를 삽입해준다.
### 리스트에 반드시 포함되어야 할 요소는 유저아바타(avatar_url), 	Repository명(full_name), 사용한 언어(language) 입니다.
#### 1. avatar_url, full_name, language이 세가지 요소를 recyclerview에 표시하기 위해 item.xml을 만든다. 여기서 avatar_url은 url형식으로 받기 때문에 Glide 라이브러리를 이용 한다.
#### 2. 요소들이 담길 Data.class를 작성한다.
```
data class MainRvData(val items: List<Item>)
data class Item (val owner : Owner,val full_name : String, val language : String )
data class Owner(val avatar_url : String)
```
#### 3. 어떠한 요청을 어떻게 보낼건지 메서드를 정의하기 위해 Interface 정의한다 여기서는 GET으로 한다
```
interface SearchApi {
    @GET("/search/repositories")
    fun searchRepositories(@Query("q") q: String,
                           @Query("per_page") per_page : Int,
                           @Query("page") page : Int
    ) : Call<MainRvData>
}
```
#### 4. Retrofit 객체 생성 해주는 함수 작성
```
object RetrofitClient {

    private var retrofitClient : Retrofit? = null

    fun getClient(baseUrl : String): Retrofit?{
         retrofitClient = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
   return retrofitClient
    }
}
```
Object로 한 이유 : 만약 서버 호출이 필요할 때마다 인터페이스를 구현해야 한다면 너무 비효율적이기 때문에
Client 파일은 싱글톤(Object)으로 제작하는 것이 바람직하기 때문

#### 5. HTTP 요청과 응답을 할 함수 작성
```
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
                }
            }

            override fun onFailure(call: Call<MainRvData>, t: Throwable) {
                // 통신 실패
                Log.d("onFailure", "onFailure")
            }
        })

        return result
    }
```
MutableLiveData를 이용해 result을 넘겨 준다 

#### 6. 만들어준 getRepository에서 나온 값을 변수에 담아주고 자료형이 MutableLiveData이기 때문에 observe를 달아준다. 
- 데이터가 변경되면 item들을 담아놓은 mutableList자료형인 repoList에 add 해준다.
- 만들어준 adapter mData에 repoList를 넣어주고 adapter를 갱신 해준다
```
        val response = getRepository(repository)
                response.observe(this, { response ->
                    response.items.let {
                        for (item in it) {
                            repoList.add(item)
                        }
                    }
                })
```

### 페이징 기능 구현 : 한번에 10개의 데이터를 불러오고 리스트의 마지막에 도달하면 다음 페이지 데이터를 로딩해서 표시. 
### 로딩중 일 때 리스트 맨 밑에 로딩바를 활용하여 로딩중임을 알 수 있도록 해야 합니다.

- MainActivity에 전역변수로 PER_PAGE = 10, page = 1를 선언한다.
- adapter.addOnScrollListener의 onScrolled를 이용하여 스크롤이 끝에 도달했는지 확인하고 만들어 놓은 moreItem 호출
```
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
```
##### moreItem 함수
```
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
```

### 새로 검색을 하면 기존의 리스트는 제거하고, 검색결과에 따른 새로운 리스트를 표시합니다.
- 검색 버튼을 눌렀을때 기존 item을 담고 있던 변수를 초기화를 시켜준다.
- 추가 기능 : 입력 값이 비어 있으면 검색 안되게 함
```
      search_btn.setOnClickListener {
            repoList = mutableListOf() // 새로 검색하면 새로운 리스트를 표시하기 위해 초기화
            page = 1

            repository = repository_input.text.toString()

            //입력 값이 비어 있으면 알려줌
            if (repository.isNullOrBlank()) {
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
```

### 구현사진
#### 시작화면
![시작화면](https://user-images.githubusercontent.com/38849158/148775434-714e8b8d-0941-4fe0-a9b3-73d4ef9f5dd7.PNG)
#### 검색 후 화면
![검색](https://user-images.githubusercontent.com/38849158/148775751-24bd23b1-8cda-4997-85e4-1d40ce95a676.PNG)
#### 페이징 화면
![페이징](https://user-images.githubusercontent.com/38849158/148775800-ef0b721f-bb1e-42f7-aa0a-ce3575657a1f.PNG)
