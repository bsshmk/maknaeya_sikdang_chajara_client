# 막내야 식당 찾아라



## 소개
> 음식점을 찾아주는 앱은 다양합니다. 네이버 지도, 구글 맵 등등.. 하지만 다양한 필터를 적용하여 사용자의 니즈에 더 적합한 음식점 리스트를 뽑아주는 앱은 없다고 생각했습니다. "막내야 식당 찾아라" 앱이 당신이 원하는 음식점을 좀 더 정확하게 필터하여 지도 위에서 직관적으로 음식점을 찾을 수 있도록 도와드리겠습니다.


## 시스템
> Client : https://github.com/bsshmk/maknaeya_sikdang_chajara_client

> Server : https://github.com/bsshmk/maknaeya_sikdang_chajara_api

> crawling : https://github.com/bsshmk/maknaeya_sikdang_chajara_crawling




### 참여자
> 조명기(ChoMk), 김보성(bsgreentea), 조성훈(JoChoSunghoon)


# 막내야 식당 찾아라 Client


## 개발환경
> Android Studio 3.4.2



## 개발 언어
> Kotlin


## 사용 라이브러리
> LiveData, ViewModel, Room, Retrofit, Dagger 2, RxJava, slidinguppanel(슬라이드 뷰), databinding, tedpermission, glide, naver map, volley



## App Architecture
 디자인 패턴은 MVVM으로 진행.

 전반적인 구조는 BaseViewModel을 정의하고 BaseBiewModel 에서 각각 필요한 Module들을 초기화하여 주입시킴.

 ViewModel에서 UI 바인딩에 필요한 값과 데이터 송수신을 처리해주고, xml에서 UI업데이트를 위하여 DataBinding 이용.
 
 rx java와 data binding을 통하여 xml에서 view model이 처리해준 UI 값 들을 바인딩 하도록하여 activity에서는 view의 요청(click listener와 같은 view에서의 요청)을 처리하는 것에 xml에서는 view의 데이터를 보여주는 것(view model에서 갱신된 값을 바인딩)에 집중할 수 있도록 분리
 
 viewModel에서 restaurant ID 별로 array map에 review list, marker, restaurant 저장하여 필요시에 불러다가 사용.(반경 2km 이내에 있는 음식점의 수가 1000개 보다는 작을 것으로 생각했고 그래서 hash map을 array map으로 수정)
 
 slide view는 1개로 운영하며 option page 상태일 때는 restaurant page를 숨기고, restaurant page 상태일 때는 option page를 숨김.
 
 recyclerview 내부 item을 개별 review item viewModel을 이용하여 binding
 
 option page에서 적용 button 클릭 시에 filter 상태에 따라서 원하는 음식점을 필터하고 화면에 marker를 통하여 표시

 refresh button 클릭시 현재 사용자 위치 기반으로 갱신
 
 
 
 
### Demo

## version 2
<img src="https://github.com/bsshmk/maknaeya_sikdang_chajara_client/blob/master/Demo/test2.gif" alt="alt text" width="250px" height="500px">

## version 1
<img src="https://github.com/bsshmk/maknaeya_sikdang_chajara_client/blob/master/Demo/test.gif" alt="alt text" width="250px" height="500px">



## Test

### Memory leak test

Mock Up

<img src="https://github.com/bsshmk/maknaeya_sikdang_chajara_client/blob/master/Demo/memory%20leak%20test%20mock%20up.png" alt="alt text" width="600px" height="300px">

Button만 있는 흰 activity에서 main activity(FoodMapActivity)를 만들어 주자. 그리고 main activity를 파괴하고 다시 Button만 있는 흰 activity로 만들어 주자. 이를 반복하자

test result

<img src="https://github.com/bsshmk/maknaeya_sikdang_chajara_client/blob/master/Demo/memory%20leak%20test.png" alt="alt text" width="600px" height="300px">

첫 main activity(FoodMapActivity)를 초기화 이후에 button만 있는 흰 activity랑 80MB 정도 차이가 나서 memory leak으로 생각했다. 하지만 여러번 위 과정을 반복했을 때 main activity(FoodMapActivity)가 파괴되기 이전 memory와 같은 것을 발견할 수 있다. 이를 통하여 첫 main activity(FoodMapActivity) 초기화와 같이 food map view model를 초기화가 이루어지고 view model은 activity가 파괴되어도 유지되기 때문에 80MB가 결국 view model의 크기임을 알 수 있었다.

memory allocation을 통하여 heap memory가 어떻게 할당 되었는지 추적하려고 했지만 profiler에서 result fetch가 되지 않아서 위와 같은 방법으로 확인하였다.


### Short path test

<img src="https://github.com/bsshmk/maknaeya_sikdang_chajara_client/blob/master/Demo/test_short_path1.png" alt="alt text" width="250px" height="500px">

<img src="https://github.com/bsshmk/maknaeya_sikdang_chajara_client/blob/master/Demo/test_short_path2.png" alt="alt text" width="250px" height="500px">

<img src="https://github.com/bsshmk/maknaeya_sikdang_chajara_client/blob/master/Demo/test_short_path3.png" alt="alt text" width="250px" height="500px">

로컬에서 txt(도로정보)파일을 긁어서 set에 바인딩하여 bfs로 test하여 최단경로가 잘 작동하는지 확인

후에 server short path api 연결 완료!!!

server short path api로직은 https://github.com/bsshmk/maknaeya_sikdang_chajara_api 확인 !!
