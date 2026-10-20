# 《RecipeApp 项目技术档案》

> 说明：本档案仅基于项目现有代码生成，不含推测性功能。

## 1. 项目概览

- **一句话定位**：一个基于 DummyJSON 公开接口的菜谱浏览 App，支持列表筛选/排序/搜索、详情查看、收藏（本地）与"添加菜谱"（提交到远端）的单模块 Android 应用。
- **模块划分**：单模块工程，仅 `:app` 一个模块（`settings.gradle.kts` 中 `include(":app")`），内部按 **分层包** 组织，无多模块拆分。
- **代码量估算（按文件数）**：
  - 主源码 Kotlin：**36 个**（`app/src/main/java`）
  - 单元测试：**3 个**（`app/src/test`）
  - 仪器测试：**1 个**（`app/src/androidTest`）
  - 资源/清单/构建脚本：约 10 个
  - 总规模大致 **40 个左右的源码级文件**，单文件体量较大（如 `HomeScreen.kt` 634 行、`AddRecipeScreen.kt` 256 行、`RecipeDetailContent.kt` 239 行）。

## 2. 技术栈

| 类别 | 技术/库 | 版本（或来源） | 证据 |
|---|---|---|---|
| 语言 | Kotlin | 2.2.10 | `gradle/libs.versions.toml` |
| UI 框架 | Jetpack Compose + Material3 | BOM `2026.02.01` | `app/build.gradle.kts`、`buildFeatures.compose=true` |
| 架构 | MVVM + Repository 分层 | — | `presentation/viewmodels/*`、`data/repository/*` |
| 网络 | Ktor Client（Android 引擎）+ ContentNegotiation + kotlinx-serialization-json | Ktor 3.5.0 / serialization 1.11.0 | `data/remote/KtorClient.kt`、`RecipeAPIService.kt` |
| 存储 | Room（菜谱缓存） + DataStore Preferences（收藏） | Room 2.7.0 / DataStore 1.1.1 | `data/local/RecipeDatabase.kt`、`FavoritesRepository.kt` |
| 异步 | Kotlin Coroutines + Flow | — | `viewModelScope`、`RecipeDao.getAll(): Flow` |
| DI | Koin | 4.0.4 | `di/AppModule.kt`、`RecipeApplication.kt` |
| 图片 | Coil | 2.7.0 | `HomeScreen.kt`（SubcomposeAsyncImage）、`RecipeDetailContent.kt`（AsyncImage） |
| 导航 | Navigation Compose（类型安全路由，`@Serializable` route） | 2.9.8 | `presentation/navigation/Router.kt`、`RecipeNaviHost.kt` |
| 测试 | JUnit4 + Ktor MockEngine +（仅声明未使用的）Compose UI Test / Espresso | JUnit 4.13.2 | `app/build.gradle.kts`、`RecipeAPIServiceTest.kt` |

构建环境：AGP 9.1.0、Gradle 9.3.1、KSP 2.2.10-2.0.2、compileSdk 36 / minSdk 24 / targetSdk 36、Java 11。

## 3. 架构模式

- **模式**：**MVVM**（Compose + ViewModel）+ **Repository 模式** 的分层架构，近似"Clean Architecture"但 domain 层未真正隔离（见第 5、6 节）。
- **分层方式**：

```
com.example.recipeapp
├── ui/theme          # 主题（Color/Type/Theme，含动态取色）
├── core              # 通用：UiState 密封接口、异常→用户文案映射
├── di                # Koin 模块装配
├── data
│   ├── remote        # Ktor 客户端 + API Service + DTO
│   ├── local         # Room(Entity/DAO/DB/Converters) + DataStore(收藏)
│   └── repository    # RepositoryImpl（组合 remote + local）
├── demain/repository # 领域层接口（注意：包名拼写为 "demain"，应为 domain）
└── presentation
    ├── viewmodels    # 3 个 ViewModel + 排序枚举
    ├── navigation    # 类型安全路由
    ├── components    # 复用组件（TopBar、Error 组件）
    └── screens       # home / recipe_detail / add_recipe 三个页面
```

- **数据流方向**：UI → ViewModel → Repository（接口）→ RepositoryImpl → APIService/LocalDataSource → Ktor/Room。
- **状态暴露方式**：ViewModel 用 Compose `mutableStateOf` 暴露状态（**非** `StateFlow`/`LiveData`），UI 直接观察 Compose state（`koinViewModel()` 注入）。

## 4. 关键实现清单

### ① 网络层封装
- **证据**：`data/remote/KtorClient.kt`（`object KtorClient`）、`data/remote/RecipeAPIService.kt`
- **实现**：单例 `HttpClient(Android)` 统一安装 `ContentNegotiation` + kotlinx JSON（`ignoreUnknownKeys=true`）；`RecipeAPIService` 封装 3 个 suspend 接口（列表/详情/新增），返回强类型 DTO，`BASE_URL = "https://dummyjson.com/"`。

### ② 本地缓存 + 网络失败回退
- **证据**：`data/repository/RecipeRepositoryImpl.kt`（`getAllRecipes` / `getRecipeById`）
- **实现**：network-first——先请求远端并 `insertAll` 写 Room 缓存；捕获异常后读缓存 `getCachedRecipes()/getCachedRecipe()` 兜底，缓存为空则重新抛出异常。

### ③ Room 数据库 + 复杂类型转换
- **证据**：`data/local/RecipeDatabase.kt`、`RecipeDao.kt`、`Converters.kt`
- **实现**：单表 `recipes`（`version=1, exportSchema=false`）；DAO 提供 `Flow` 查询、`REPLACE` 插入、`clearAll`；用 `TypeConverter` 把 `List<String>`（配料/步骤/标签/餐型）经 kotlinx JSON 序列化为字符串存储。

### ④ DTO ↔ Entity 双向映射
- **证据**：`data/local/RecipeEntity.kt`（`RecipeDTO.toEntity()` / `RecipeEntity.toDto()`）
- **实现**：以顶层扩展函数做手写字段级映射，字段一一对应，供缓存读写与回退时转换。

### ⑤ 收藏功能（DataStore）
- **证据**：`data/local/FavoritesRepository.kt`
- **实现**：`preferencesDataStore("favorites")` 存 `Set<String>`（favorite id 集合），暴露 `Flow<Set<Int>>`（字符串解析为 Int，非法值 `mapNotNull{it.toIntOrNull()}` 过滤），`toggle` 原子增删。

### ⑥ 列表搜索/筛选/排序
- **证据**：`presentation/viewmodels/HomeViewModel.kt`（`applyFilters`）、`SortOption.kt`
- **实现**：全部菜谱加载进内存后，纯 Kotlin 集合链式过滤（按名称/tag/配料模糊匹配 + cuisine/difficulty/mealType 精确匹配 + 仅收藏），再按 5 种排序枚举排序；筛选选项（分类/难度/餐型）在首次加载后从数据 `distinct().sorted()` 动态生成。

### ⑦ 分页加载（"加载更多"）
- **证据**：`HomeViewModel.kt`（`visibleCount` / `PAGE_SIZE=10` / `loadMore`）、`HomeScreen.kt` 底部 `LaunchedEffect{loadMore()}`
- **实现**：内存分页——`visibleCount` 控制渲染 `recipes.take(visibleCount)`；列表尾部放一个哨兵 item，进入组合时触发 `loadMore()` 每次 +10 直到上限。

### ⑧ 统一错误处理与用户文案映射
- **证据**：`core/Errors.kt`（`Throwable.toUserMessage()`）、`core/UiState.kt`
- **实现**：`generateSequence(this){it.cause}` 遍历 cause 链识别 `UnknownHost/SocketTimeout/Connect` 异常并返回网络提示，否则回退 `message` 或通用文案；`UiState` 密封接口统一 Loading/Success/Error 三态。

### ⑨ 类型安全导航
- **证据**：`presentation/navigation/Router.kt`、`RecipeNaviHost.kt`
- **实现**：用 `@Serializable` 定义 route（`HomeRouter`/`AddRecipeRoute`/`RecipeDetailRoute(recipeId:Int)`），`composable<T>` + `backStackEntry.toRoute<T>()` 类型安全传参。

### ⑩ 图片加载（带占位/错误态）
- **证据**：`HomeScreen.kt`（`SubcomposeAsyncImage`）、`RecipeDetailContent.kt`（`AsyncImage`）
- **实现**：Coil 加载网络图片；列表卡片用 `SubcomposeAsyncImage` 提供 `loading`（转圈）与 `error`（"Error"占位）槽位，`ContentScale.Crop`。

### ⑪ 添加菜谱表单 + 客户端校验
- **证据**：`presentation/viewmodels/AddRecipeViewModel.kt`（`submit`）、`AddRecipeScreen.kt`
- **实现**：字段用 Compose state 收集；提交前校验必填（名称/≥1配料/≥1步骤），数字字段 `toIntOrNull() ?: 0` 容错，标签逗号拆分；`isSubmitting` 防重复提交，`try/finally` 复位。

### ⑫ 依赖注入装配
- **证据**：`di/AppModule.kt`、`RecipeApplication.kt`
- **实现**：`startKoin` 注册单例（HttpClient/APIService/Room DB/DAO/LocalDataSource/Repository/FavoritesRepository），`viewModelOf` 自动装配 3 个 ViewModel。

## 5. 可能的技术难点候选（有设计取舍处）

1. **缓存策略取舍**：network-first + 失败回退，但**无 TTL/过期、无主动刷新、无"陈旧但可用"的过期标记**。`cacheRecipes` 用 `insertAll(REPLACE)` 全量覆盖，`clearAll()` 已定义但**从未被调用**——远端若删菜谱，本地旧数据可能残留。
2. **宽泛异常捕获**：Repository 与 ViewModel 都 `catch (Exception)`；`toUserMessage` 只区分网络类异常，HTTP 4xx/5xx 与 JSON 解析失败会被归入通用文案。
3. **状态管理方式**：用 `mutableStateOf` 而非 `StateFlow`，`uiState` 与派生属性（`recipes`、筛选字段）分离暴露；状态可变且直接暴露给 UI，丢失不可变性与背压语义。
4. **领域层泄漏**：`RecipeRepository` 接口直接返回 `data.remote.dto.RecipeDTO`（网络 DTO 穿透到 presentation），未定义领域模型；且 `AddRecipeViewModel` **绕过 Repository 直接依赖 `RecipeAPIService`**，破坏分层一致性。
5. **过滤/排序位置**：全部在 ViewModel 内存中做集合操作，未利用 Room SQL 过滤；数据量增大时内存与主线程压力是隐患。
6. **线程切换**：依赖 suspend + `viewModelScope`（默认 Main 调度器，Ktor/Room 内部切 IO）隐式处理；`getCachedRecipes()` 用 `Flow.first()` 取一次快照而非持续订阅。
7. **图片处理**：详情页 `AsyncImage` 无 `error/loading` 槽位（列表页有），错误态处理不一致。

## 6. 缺失项（代码中确实没有）

- **CI/CD**：无 `.github/workflows`、无任何 `*.yml` 流水线。
- **多模块化**：仅单 `:app` 模块，无 feature/data/core 等 Gradle 模块拆分。
- **测试覆盖薄弱**：仅 2 个有效单测（`RecipeAPIServiceTest` 用 Ktor MockEngine 测解析、`RecipeEntityMappersTest` 测往返映射）+ 2 个模板示例测试；**无 ViewModel 测试、无 Room DAO 测试、无 Repository 缓存回退测试、无 Compose UI 测试**（虽在 `build.gradle.kts` 声明了 Compose UI Test / Espresso 依赖但无对应用例）。
- **无 Mock 框架**：未引入 MockK/Mockito。
- **无 Room 迁移策略**：`version=1` 且 `exportSchema=false`，无迁移/版本管理。
- **无混淆**：`isMinifyEnabled = false`（release 未开）。
- **无国际化**：`strings.xml` 仅 `app_name`，其余文案硬编码英文在 Composable/ViewModel 中。
- **无网络日志/拦截器**：无 HttpLogging、无鉴权、无超时/重试配置。
- **无安全存储**：收藏用明文 DataStore Preferences。
- **包名/文件拼写问题**：`demain`（应为 `domain`）、`RcipeTopBar.kt`（应为 `RecipeTopBar`）——如实记录。

## 7. 面试可追问点

**① 网络层封装**
- 为什么选 Ktor 而非 Retrofit？`HttpClient` 被声明为单例，其引擎/生命周期如何管理？
- `ignoreUnknownKeys=true` 的作用？若后端字段改名或缺字段，`@Serializable` 默认会怎样（无默认值字段会抛异常）？

**② 缓存 + 失败回退**
- 缓存如何判断"新鲜"？现有实现是否有过期问题？你会怎么加 TTL？
- `getAllRecipes` 成功后为什么从不 `clearAll()`？远端数据收缩时会发生什么？

**③ Room + TypeConverter**
- `List<String>` 序列化成 JSON 存 TEXT 的利弊？和"关联表/JSON 列"如何权衡？查询/更新单个元素是否可行？
- `exportSchema=false` 的后果？要做迁移时你会怎么补 schema？

**④ DTO/Entity 映射**
- 手写映射 vs 自动映射如何选择？字段增多时手写映射的可维护性？

**⑤ 收藏（DataStore）**
- 为什么用 `Set<String>` 存 id 而不是 Room 一张收藏表？两者在一致性、查询、扩展性上的差异？
- `favoriteIds` 里 `toIntOrNull` 过滤的健壮性考虑？并发 `toggle` 是否有竞态？

**⑥ 搜索/筛选/排序**
- 全部在内存过滤，数据量大时的性能与线程问题？如何改成数据库/SQL 层过滤或加 debounce？
- `applyFilters` 每次都重建 `uiState=Success(result)`，会触发多少次重组？`recipes` 派生属性与 `uiState` 是否可能不一致？

**⑦ 分页加载**
- 当前是"内存分页"还是"服务端分页"？`loadMore` 被 `LaunchedEffect` 触发，滚动时是否可能重复触发？
- `recipes.take(visibleCount)` 在列表中使用 `key={it.id}` 的作用？

**⑧ 错误处理**
- `catch (Exception)` 的粒度是否过粗？CancellationException 会不会被误吞？
- Ktor 中 HTTP 非 2xx 默认抛异常吗？`body()` 反序列化失败和连接失败如何区分处理？

**⑨ 类型安全导航**
- `@Serializable` route 相比字符串 route 的优势？`RecipeDetailRoute(recipeId:Int)` 的入参如何序列化进返回栈？

**⑩ 图片加载**
- `SubcomposeAsyncImage` 与 `AsyncImage` 区别？为什么列表用前者？
- Coil 的缓存策略？同一 URL 图片变化时如何失效？

**⑪ 添加菜谱表单**
- 客户端校验之外，服务端校验失败如何反馈？`toIntOrNull() ?: 0` 把非法输入静默变 0 是否合理？
- 提交后 `successMessage` 保留，页面返回/重建时状态如何保持（ViewModel 作用域）？

**⑫ DI 装配**
- Koin vs Hilt 的选择考量？`viewModelOf` 与 `viewModel{}` 工厂的差异？
- 为什么 `RecipeRepository` 用接口而 `FavoritesRepository`/`AddRecipeViewModel` 直接用实现类？这对可测试性的影响？
