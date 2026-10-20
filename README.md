# RecipeApp

一个基于 [DummyJSON](https://dummyjson.com/) 公开接口的菜谱浏览 Android 应用。支持列表搜索/筛选/排序、详情查看、本地收藏，以及向远端提交"添加菜谱"。单模块工程，采用 **Jetpack Compose + MVVM** 分层架构。

## ✨ 功能特性

- 🔍 **搜索 / 筛选**：按名称、标签、配料模糊匹配；按菜系（cuisine）、难度（difficulty）、餐型（mealType）精确筛选；支持"仅看收藏"
- ↕️ **排序**：5 种排序方式（名称、评分等）
- 📄 **菜谱详情**：图片、配料、步骤、标签、评分、烹饪时间等
- ❤️ **收藏**：本地持久化（DataStore Preferences）
- ➕ **添加菜谱**：客户端表单校验，提交到远端接口
- 📶 **离线兜底**：network-first + Room 缓存，网络失败时回退到本地缓存
- ♾️ **分页加载**：列表滚动到底部自动"加载更多"

## 🖼️ 截图

> 待补充（构建运行后截图放在此处）。

## 🧱 技术栈

| 类别 | 技术 / 库 |
|---|---|
| 语言 | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material3（BOM 2026.02.01） |
| 架构 | MVVM + Repository 分层 |
| 网络 | Ktor Client 3.5.0（Android 引擎）+ kotlinx-serialization-json |
| 本地存储 | Room 2.7.0（菜谱缓存）+ DataStore Preferences（收藏） |
| 异步 | Kotlin Coroutines + Flow |
| 依赖注入 | Koin 4.0.4 |
| 图片加载 | Coil 2.7.0 |
| 导航 | Navigation Compose 2.9.8（类型安全路由） |
| 构建 | AGP 9.1.0 / Gradle 9.3.1 / KSP / compileSdk 36 · minSdk 24 · targetSdk 36 |

## 🏗️ 架构

**MVVM（Compose + ViewModel）+ Repository 模式**，数据流方向：

```
UI → ViewModel → Repository（接口）→ RepositoryImpl → APIService / LocalDataSource → Ktor / Room
```

```
com.example.recipeapp
├── ui/theme          # 主题（Color / Type / Theme，含动态取色）
├── core              # 通用：UiState 密封接口、异常 → 用户文案映射
├── di                # Koin 模块装配
├── data
│   ├── remote        # Ktor 客户端 + API Service + DTO
│   ├── local         # Room（Entity / DAO / DB / Converters）+ DataStore（收藏）
│   └── repository    # RepositoryImpl（组合 remote + local）
├── demain/repository # 领域层接口
└── presentation
    ├── viewmodels    # ViewModel + 排序枚举
    ├── navigation    # 类型安全路由
    ├── components    # 复用组件（TopBar、Error）
    └── screens       # home / recipe_detail / add_recipe
```

## 📱 页面

| 页面 | 说明 |
|---|---|
| Home | 菜谱列表，搜索 / 筛选 / 排序 / 收藏过滤，滚动加载更多 |
| Recipe Detail | 详情：图片、配料、步骤、标签、评分等 |
| Add Recipe | 表单新增菜谱，客户端校验后提交远端 |

## 🔌 数据源

所有数据来自公开接口 [DummyJSON](https://dummyjson.com/)（`BASE_URL = "https://dummyjson.com/"`），不涉及鉴权。

## 🚀 构建与运行

**环境要求**

- JDK 17+
- Android SDK（compileSdk 36）
- Android Studio（推荐最新版）或命令行 Gradle

**步骤**

1. 用 Android Studio 打开项目（首次会自动配置 SDK），或手动配置 `local.properties` 中的 `sdk.dir`
2. 命令行构建：

```bash
./gradlew assembleDebug          # Windows 使用 gradlew.bat
```

3. 生成的 APK 位于 `app/build/outputs/apk/debug/`

## 📁 项目结构

单模块工程（`:app`），主源码位于 `app/src/main/java/com/example/recipeapp/`，按分层包组织（见上文架构图）。

## 🗺️ Roadmap / 待改进

- [ ] CI/CD（尚无 `.github/workflows`）
- [ ] 测试覆盖：补齐 ViewModel / Repository 缓存回退 / Room DAO / Compose UI 测试
- [ ] Room 迁移策略与 schema 导出（当前 `version=1`、`exportSchema=false`）
- [ ] 缓存过期（TTL）与主动刷新机制
- [ ] release 混淆（`isMinifyEnabled=false`）
- [ ] 国际化文案（当前 `strings.xml` 仅 `app_name`，其余硬编码英文）
- [ ] 网络日志 / 超时重试配置
- [ ] 领域层隔离：DTO 目前穿透到 presentation 层
- [ ] 包名 `demain` 拼写（应为 `domain`）

## 📄 License

暂无（建议补充 Apache-2.0 或 MIT 许可证）。
