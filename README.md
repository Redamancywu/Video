# Jetpack +MVVM + Retrofit + LiveData + Room + Kotlin 实现的仿皮皮虾App
## 项目简介
本项目是一个基于Jetpack +MVVM + Retrofit + LiveData + Room + Kotlin 实现的仿皮皮虾App，采用Kotlin语言编写，实现了登录、注册、首页、视频播放、评论等功能。项目结构清晰，代码规范，适合学习和参考。

## 1.Paging3的介绍使用
  
Paging3是Jetpack中的一个组件，用于实现分页加载。它可以帮助我们更方便地处理分页数据，提高用户体验。Paging3提供了多种分页方式，包括网络分页、本地分页等。

1. 支持预加载和占位功能
2. 支持向前向后加载分页的能力
3. 支持多种数据源，包括网络和本地数据库
4. 支持kotlin协程和flow以及livedata
5. 支持刷新和重试能力
## paging3的使用
1. 添加依赖（省略）
2. pagingSource：分页数据的数据源，以及从数据源中获取数据的方法，需要实现load()函数来从数据源中获取分页数据
3. pagingData：分页数据，包含当前页的数据以及分页加载的状态，分页数据的容器被称之为pagingData，每次刷新数据时都会重新创建一个pagingData实例
4. PagingDataAdapter：分页适配器，支持重试，刷新，支持添加holder，footer，用于将分页数据展示到UI上，需要实现bindViewHolder()函数来将数据绑定到ViewHolder上
5. PagingConfig：分页配置，用于配置分页加载的相关参数，如每页加载的数据量、预加载的页数等
6. pager：分页器，用于管理分页加载，需要实现onRefresh()函数来刷新数据，以及实现onPageSelected()函数来加载下一页数据
7. PagingSource.Factory：分页数据源工厂，用于创建分页数据源，需要实现create()函数来创建分页数据源
8. PagingSource.LoadParams：分页加载参数，用于传递分页加载的相关参数，如当前页码、每页加载的数据量等
9. PagingSource.LoadResult：分页加载结果，用于返回分页加载的结果，如成功、失败、无更多数据等
## 2.视频播放的实现
1. 自定义视频播放器WrapperPlayerView的UI基于Fragment实现一个自定义播放器的页面
2. 使用ExoPlayer作为视频播放器，ExoPlayer是一个高性能、可扩展的Android视频播放器，支持多种视频格式和协议
3. 使用RecycleView实现一个视频播放布局，滑动视频实现自动播放的功能,一个列表公用一个视频播放器exoPlayer（pageListPlayer），一个ExoPlayerView（SurfaceTexture）
列表滑动后自动检测播放和暂停
4. 显示视频画面的ExoPlayerView和用于播放控制的ExoControllerView简易复杂度，自定义。通过动态添加item到WrapperPlay里面
5. 响应页面生命周期与用户手势 处理视频播放暂停，控制等显示

