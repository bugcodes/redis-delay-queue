# redis-delay-queue
redis delay queue
src/main/java/com/bugcodes/
├── core/                 # 队列核心逻辑 (DelayQueueManager, DeadLetterQueue)
├── executor/             # 线程池管理 (DelayTaskExecutor)
├── retry/                # 重试策略 (RetryStrategy接口及实现)
├── model/                # 延迟任务模型 (DelayTask)
├── scheduler/            # 任务调度器 (DelayTaskScheduler)
├── admin/                # 管理后台Controller (AdminController)
├── config/               # Redis配置 (RedisConfig)
└── DelayQueueApplication.java  # Spring Boot启动入口

src/main/resources/
├── application.yml
└── templates/
├── dashboard.html
└── dead-letters.html


| 流程步骤  | 描述                         |
| :---- |:---------------------------|
| 文件生成  | 放在一个目录A下                   |
| 点击下载  | 检查【nginx static目录B】是否已有该文件 |
| 检查有效期 | 30分钟有效，且剩余时间 > 10分钟才可以直接用  |
| 如果不符合 | 把文件从A拷贝到B，并返回新的url         |
| 统一返回  | 最终总是返回一个可直接下载的URL地址        |
| 文件清理  | 公共目录下的文件，30分钟后用延迟队列自动删除    |

