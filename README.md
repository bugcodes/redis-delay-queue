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
