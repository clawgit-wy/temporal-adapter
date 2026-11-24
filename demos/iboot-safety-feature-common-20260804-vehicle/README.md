
IBOOT 中台项目框架模板
===============

快速新建项目指南
-----------------------------------

- 1,gitLab上基于此项目Fork一个新项目,需选择分组,如果没有新分组则需先新建分组;
- 2,gitLab上进入到新分组新项目中,点击Settings>>General菜单,点击General project的expand按钮,将Project name修改为新项目名称,点击Save changes保存;点击Advanced的expand按钮,将Rename repository的Project name和Path修改为新项目名,点击Rename project保存;
- 3,gitLad上返回到Project页面,点击clone按钮复制Clone with HTTP连接,将代码下载到本地;
- 4,使用idea打开项目,删除不需要的子目录模块,将需要的模块和子模块里的pom文件以及目录都改为新项目名称;
- 5,修改启动模块下application-test.yml文件里项目名以及nacos配置文件名,然后在nacos管理页面,找到配置管理>>配置列表,mp组下iboot-search-admin.yaml配置文件,复制内容,然后再新建,名称为与application-test.yml里的一致,内容为刚才复制的iboot-search-admin.yaml一致;
- 6,修改logback-spring.xml文件内日志目录的项目名
- 7,去测试数据库10.66.228.174建新项目所需的数据库,建好后修改nacos中数据源配置的库名;
- 8,启动项目,以safety-module-system-biz为例,打开SystemApplication文件,右键debug模式启动;
- 9,配置网关,登录微服务管理平台新增网关,路由名称与nacos服务名一致;

架构介绍
--------------------------------------
基于jeecg的IBoot搭建，以微服务架构为核心思想，采用前后端分离架构，后端主要技术栈包括:SpringBoot2.x，SpringCloud，Ant Design&Vue，Mybatis-plus，Shiro，JWT等，前端主要技术包括： Vue3.0、Vite、Ant-Design-Vue、TypeScript 等。本文主要介绍后端项目；

后端采用maven父子工程结构，每个服务单独部署，公共环境为：nacos作为注册中心和配置中心，gateway配置网关转发，IAM统一认证登录，权限中心统一管理权限；目前整个链路完整，各环节通畅，可快速搭建一个新项目。

数据库及中间件集成介绍

| 名称                | 说明                     | 
|--------------------|------------------------|
| `数据库`    | 默认使用MySQL数据，数据库配置在nacos配置中心完成,目前已接入数据库类型还有：oracle、sqlserver、postgresql|
| `kafka`    | 分布式消息队列，采用springCloudStream方式集成，支持多个消息中间件相互通讯；配置也在nacos上，目前已搭建中台kafka集群，可直接使用 |
| `redis`    | 远程字典服务，主要应用于缓存场景，key命名规范例为`mp:auth:XXX`；配置也在nacos上，目前已搭建中台redis集群，可直接使用 |
| `prometheus`| 监控平台，项目通过集成metrics来收集服务指标数据，只需nacos配置management即可实现自动接入中台监控平台，中台已搭建prometheus+grafana平台，实时展示服务运行状况、请求明细等，还可用于报错告警、流量激增预警等 |
| `sentinel`| 流量控制平台，项目集成alibaba-sentinel组件收集数据，只需nacos配置sentinel地址即可实现自动接入中台流控平台，可通过sentinel管理页面配置，实现限流、熔断、降级 |

源码下载
-----------------------------------

| 源码                | 源码地址                     | 
|--------------------|------------------------|
| 后端开发框架模板源码   | http://10.66.192.63/iboot/iboot-boot.git      |

项目结构开发说明
-----------------------------------

| 目录                | 说明                     | 
|--------------------|------------------------|
| `iboot-base-core`    | iboot项目核心包，每个项目都需要集成该模块        |
| `iboot-starter-cloud`    | 服务请求时长统计,每个项目都需要集成,另外kafka配置相关通道        |
| `iboot-module-xxx-admin或system` | 管理端代码存放模块 |
| `iboot-module-xxx-consumer或rep` | 消费端代码存放目录,如果项目使用了消息队列,可使用此模块消费消息并处理业务逻辑,目的是为了服务隔离,避免服务之间的影响 |
| `iboot-module-xxx-core` | 项目内的核心包,每个项目拥有自己独自的core,存放实体、VO、Util、mapping等，是为了解决一个项目多个模块的代码共用问题|
| `iboot-module-xxx-job` | 项目的定时任务模块，该模块主要执行一些定时任务，若项目使用了xxl-job定时任务，可使用模块|
| `iboot-module-xxx-api或webapi` | 项目提供对外接口模块，也可作为客户端模块，如项目涉及对外提供服务接口可使用模块|
| `iboot-module-xxx-start` | 项目的启动模块，此模块没有实际的业务代码，只有一个启动类，已不再建议使用此模块，启动类放到具体模块中|

开发环境
-----------------------------------

- 语言：Java 8+ (小于17)

- IDE(JAVA)： IDEA (必须安装lombok插件 )

- IDE(前端)： Vscode、WebStorm、IDEA

- 依赖管理：Maven

- 缓存：Redis

- 数据库脚本：MySQL5.7+  &  Oracle 11g & Sqlserver2017（其他数据库，[需要自己转](https://my.oschina.net/jeecg/blog/4905722)）


技术栈详细介绍
-----------------------------------

- 基础框架：Spring Boot 2.6.14

- 微服务框架： Spring Cloud Alibaba 2021.0.1.0

- 持久层框架：MybatisPlus 3.5.1

- 报表工具： JimuReport 1.5.2

- 安全框架：Apache Shiro 1.8.0，Jwt 3.11.0

- 微服务技术栈：Spring Cloud Alibaba、Nacos、Gateway、Sentinel、Skywalking

- 数据库连接池：阿里巴巴Druid 1.1.22

- 日志打印：logback

- 其他：autopoi, fastjson，poi，Swagger-ui，quartz, lombok（简化代码）等。
