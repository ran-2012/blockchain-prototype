
## 参考

- Awesome Java
  https://github.com/akullpp/awesome-java
- Javalin
  https://javalin.io/

## 外部依赖

### MongoDB

- 7.0.2
- https://www.mongodb.com/try/download/community

### Redis

- 6.0.16
- https://redis.io/docs/getting-started/installation/install-redis-on-windows
- 注：官方不支持Windows，可以安装在WSL下，或使用社区的Windows build版本。

## 说明

- 使用Java 17
- 记得加日志`blockchain.utility.Log`
  - ERROR: 错误
  - WARN：可解决的错误
  - INFO： 重要信息
  - DEBUG： 调试信息
- 函数、变量命名用camelCase.
- 文档注释可以写也可以不写

### Data

数据类要求：
- 所有字段必须有默认值，最好不为空
- 所有字段必须能够通过构造函数初始化
- 所有字段需要有相应的getter和setter
- 需要有无参构造函数
- 使用列表时需要用`ArrayList`

否则在进行数据库存储时会非常麻烦

## 模块

### data

各个模块之前交互需要的数据定义

### mining

Miner/transaction相关

### network

网络

### storage

存储

### utility

通用模块

### wallet

钱包


## 运行

### 启动节点

`java -jar main.jar`

可以设置启动的节点数量和矿工数量：

`java -jar main.jar 6 2`，6个节点2个矿工

### 钱包

显示帮助：

`java -jar wallet.jar`

转账：

`java -jar wallet.jar transfer-idx 0 1 1000`，从账户0转1000到账户1

