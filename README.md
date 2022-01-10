# afreecatv 사전과제  :tophat:

## 과제 설명

### 
+ GitHub API를 사용하여 GitHub Repository를 조회하고 리스트를 보여주는 어플리케이션을 만듭니다.
+ API 명세서와 예시 화면을 참고하여 요구사항을 구현합니다.

### 요구사항
+ 검색하고 싶은 Repository를 입력 받고, GitHub Search Repositories API의 응답으로 받아온 데이터를 사용하여 리스트를 표시합니다.
+ 리스트에 반드시 포함되어야 할 요소는 avatar_url, full_name, language 입니다.
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

- 전체 Response중에, 본 과제에서 사용할 filed는 아래와 같습니다.

| Field | Type | Description |
| --- | --- | --- |
| items | Array | Repository에 대한 다양한 데이터를 담고 있습니다. |
| full_name | String | Repository명을 나타냅니다. |
| owner | Object | 유저 프로필관련 정보를 담고 있습니다. |
| avatar_url | String | 유저 아바타 이미지 주소를 나타냅니다. |
| language | String | 해당 프로젝트에서 사용한 언어를 나타냅니다. |

### 설명
API를 사용하기 위해 Retrofit2를 사용합니다.
### Retrofit이란?
- 통신 라이브러리 중 가장 많이 사용되는 대표적인 라이브러리
- 
