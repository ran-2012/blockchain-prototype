
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

## TODO

- Wallet
  - 新交易+签名 2步

- Network 接口

- Mining fork处理

- UTXO
  - data 数据
  - storage 存储
  - mining 维护
- 
