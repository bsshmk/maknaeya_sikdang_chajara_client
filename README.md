# 막내야 식당 찾아라



## 소개
>


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


