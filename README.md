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
> LiveData, ViewModel, Room, Retrofit, Dagger 2, RxJava, slidinguppanel(슬라이드 뷰), databinding, tedpermission, glide, naver map



## App Architecture
 디자인 패턴은 MVVM으로 진행.

 전반적인 구조는 BaseViewModel을 정의하고 BaseBiewModel 에서 각각 필요한 Module들을 초기화하여 주입시킴.

 ViewModel에서 UI업데이트와 데이터 송수신을 처리해주고, UI업데이트를 위하여 DataBinding 이용.
 
 viewModel에서 restaurant ID 별로 hash map에 review list, marker, restaurant 저장하여 필요시에 불러다가 사용.
 
 slide view는 1개로 운영하며 option page 상태일 때는 restaurant page를 숨기고, restaurant page 상태일 때는 option page를 숨김.
 
 recyclerview 내부 item을 개별 review item viewModel을 이용하여 binding
 
 option page에서 적용 button 클릭 시에 filter 상태에 따라서 원하는 음식점을 필터하고 화면에 marker를 통하여 표시

 refresh button 클릭시 현재 사용자 위치 기반으로 갱신
 
 
