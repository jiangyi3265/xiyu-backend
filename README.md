# xiyu-backend

## 项目简介

`xiyu-backend` 是洗浴/酒店预约系统的后端服务，负责管理后台接口、用户端接口、会员、客房、餐饮、储值、优惠券、积分商城、订单与支付等核心业务能力。

## 技术栈

- Java 8
- Spring Boot 2.5.x
- RuoYi 后端体系
- Spring Security + JWT
- MyBatis
- MySQL
- Redis
- Maven

## 关联仓库

| 子项目 | 仓库 | 说明 |
|---|---|---|
| 后端服务 | [xiyu-backend](https://github.com/jiangyi3265/xiyu-backend) | 提供管理后台与用户端 API |
| 管理后台 | [xiyu-admin](https://github.com/jiangyi3265/xiyu-admin) | 运营人员使用的后台管理系统 |
| 用户端 | [xiyu-app](https://github.com/jiangyi3265/xiyu-app) | 面向用户的 uni-app/微信小程序端 |

## 快速启动

```bash
mvn clean package -DskipTests
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

默认服务地址：

```text
http://localhost:8081
```

生产环境建议通过环境变量配置数据库、Redis、Token 密钥等敏感信息。

## 简历描述示例

负责洗浴/酒店预约系统后端服务建设，基于 Spring Boot、MyBatis、Redis 实现会员、客房、订单、积分、优惠券等核心业务接口，并支撑管理后台与用户端多端协同。
