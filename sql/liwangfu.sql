-- ----------------------------------------------------------------------------
-- 平云山居 — 业务库脚本（在 RuoYi 基础库 ha 上叠加执行）
-- 包含：11 张业务表 + 种子数据 + 字典 + 后台菜单权限
-- 字段与 uni-app 前端 common/mock.js 一一对应，三端共用此契约
-- 执行前请先导入 ry_20250522.sql（含 sys_user/sys_menu/sys_dict 等基础表）
-- ----------------------------------------------------------------------------
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ========================= 1. 轮播图 lwf_banner =========================
DROP TABLE IF EXISTS `lwf_banner`;
CREATE TABLE `lwf_banner` (
  `banner_id`   bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '轮播ID',
  `title`       varchar(100)  DEFAULT '' COMMENT '主标题',
  `sub`         varchar(200)  DEFAULT '' COMMENT '副标题',
  `scene`       varchar(32)   DEFAULT 'room' COMMENT '配色场景(dine/room/night)',
  `sort`        int(11)       DEFAULT 0 COMMENT '显示顺序',
  `status`      char(1)       DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`   varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime      DEFAULT NULL COMMENT '创建时间',
  `update_by`   varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime      DEFAULT NULL COMMENT '更新时间',
  `remark`      varchar(500)  DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`banner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播图';

INSERT INTO `lwf_banner` (title, sub, scene, sort, status, create_by, create_time) VALUES
('平云山居 · 粤式盛宴', '一席一味 · 增城本味', 'dine', 1, '0', 'admin', sysdate()),
('618 周年庆', '客房券套餐 低至 4 折', 'room', 2, '0', 'admin', sysdate()),
('会员储值尊享好礼', '“储”即发 · 充值送豪礼', 'night', 3, '0', 'admin', sysdate());

-- ========================= 2. 房型 lwf_room =========================
DROP TABLE IF EXISTS `lwf_room`;
CREATE TABLE `lwf_room` (
  `room_id`     bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '房型ID',
  `name`        varchar(100)  NOT NULL COMMENT '房型名称',
  `area`        int(11)       DEFAULT 0 COMMENT '面积(㎡)',
  `bed`         varchar(50)   DEFAULT '' COMMENT '床型',
  `win`         varchar(50)   DEFAULT '' COMMENT '窗户',
  `price`       decimal(10,2) DEFAULT 0.00 COMMENT '价格(元/晚)',
  `cover_url`   varchar(500)  DEFAULT '' COMMENT '房型封面图',
  `scene`       varchar(16)   DEFAULT '0' COMMENT '配图场景(0-3)',
  `tags`        varchar(255)  DEFAULT '' COMMENT '标签(逗号分隔)',
  `feature`     varchar(500)  DEFAULT '' COMMENT '房型特色',
  `cancel_rule` varchar(255)  DEFAULT '' COMMENT '取消规则',
  `facilities`  varchar(500)  DEFAULT '' COMMENT '设施服务(逗号分隔)',
  `stock`       int(11)       DEFAULT 10 COMMENT '每日可售房量(库存)',
  `sort`        int(11)       DEFAULT 0 COMMENT '显示顺序',
  `status`      char(1)       DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`   varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime      DEFAULT NULL COMMENT '创建时间',
  `update_by`   varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime      DEFAULT NULL COMMENT '更新时间',
  `remark`      varchar(500)  DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='房型';

INSERT INTO `lwf_room` (name, area, bed, win, price, scene, tags, feature, cancel_rule, facilities, sort, status, create_by, create_time) VALUES
('高级双床房', 32, '2张1.35m床', '有窗', 338.00, '0', '含早,参与积分', '提前三天预订5月2日-5月5日的客房可享受五一早鸟房型升级礼遇', '在线付：不可取消', '免费WIFI,空调,24h热水,独立卫浴,洗漱用品,拖鞋', 1, '0', 'admin', sysdate()),
('高级大床房', 32, '1张2m大床', '有窗', 338.00, '1', '含早,参与积分', '提前三天预订即享免费延迟退房至14:00', '在线付：不可取消', '免费WIFI,空调,24h热水,独立卫浴,洗漱用品,拖鞋', 2, '0', 'admin', sysdate()),
('豪华江景大床房', 45, '1张2m大床', '落地窗·江景', 588.00, '2', '含双早,可升级,参与积分', '高楼层江景，含双早，会员可享延时退房至15:00', '入住前1天18:00前可免费取消', '免费WIFI,智能马桶,浴缸,江景阳台,迷你吧,24h热水', 3, '0', 'admin', sysdate()),
('行政套房', 68, '1张2m大床', '270°转角窗', 1288.00, '3', '行政礼遇,含双早,可升级', '专享行政酒廊，下午茶与欢乐时光，免费洗衣2件/天', '入住前1天18:00前可免费取消', '行政酒廊,智能马桶,按摩浴缸,客厅,迷你吧,管家服务', 4, '0', 'admin', sysdate());

-- ========================= 2.1 房型每日库存 lwf_room_stock =========================
DROP TABLE IF EXISTS `lwf_room_stock`;
CREATE TABLE `lwf_room_stock` (
  `id`          bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '主键',
  `room_id`     bigint(20)    NOT NULL COMMENT '房型ID',
  `stock_date`  date          NOT NULL COMMENT '日期',
  `sold`        int(11)       DEFAULT 0 COMMENT '当日已售数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_date` (`room_id`, `stock_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房型每日库存(已售量,可售=房型stock-sold)';

-- ========================= 3. 餐饮套餐/周年庆 lwf_product =========================
DROP TABLE IF EXISTS `lwf_product`;
CREATE TABLE `lwf_product` (
  `product_id`  bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `type`        varchar(16)   DEFAULT 'dish' COMMENT '类型(dish餐饮 promo周年庆)',
  `title`       varchar(200)  NOT NULL COMMENT '标题',
  `main`        varchar(50)   DEFAULT '' COMMENT '海报主文案(如 ¥688)',
  `sub`         varchar(100)  DEFAULT '' COMMENT '海报副文案',
  `theme`       varchar(16)   DEFAULT 'red' COMMENT '海报配色(red/wine)',
  `badge`       varchar(32)   DEFAULT '' COMMENT '角标(周年庆用)',
  `scene`       varchar(16)   DEFAULT 'dine' COMMENT '配图场景',
  `price`       decimal(10,2) DEFAULT 0.00 COMMENT '现价',
  `old_price`   decimal(10,2) DEFAULT 0.00 COMMENT '原价',
  `people`      varchar(50)   DEFAULT '' COMMENT '适用人数',
  `terms`       varchar(500)  DEFAULT '' COMMENT '使用条款(；分隔)',
  `items`       varchar(1000) DEFAULT '' COMMENT '套餐内容(逗号分隔)',
  `sort`        int(11)       DEFAULT 0 COMMENT '显示顺序',
  `status`      char(1)       DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`   varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime      DEFAULT NULL COMMENT '创建时间',
  `update_by`   varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime      DEFAULT NULL COMMENT '更新时间',
  `remark`      varchar(500)  DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='餐饮套餐/周年庆活动';

INSERT INTO `lwf_product` (type, title, main, sub, theme, badge, scene, price, old_price, people, terms, items, sort, status, create_by, create_time) VALUES
('dish', '平云山居8-10人超值套餐，欢迎预定~', '¥688', '8-10人 · 超值套餐', 'red', '', 'dine', 688.00, 1181.00, '8-10人', '需提前1天预订；法定节假日不可用；每桌限用一份。', '白切走地鸡,平云山居烧鹅,清蒸时令河鲜,蒜蓉粉丝蒸元贝,增城迟菜心,老火靓汤,时令蔬菜,例汤·甜品', 1, '0', 'admin', sysdate()),
('dish', '平云山居6-8人超值套餐，欢迎预定~', '¥468', '6-8人 · 超值套餐', 'wine', '', 'dine', 468.00, 949.00, '6-8人', '需提前1天预订；法定节假日不可用；每桌限用一份。', '白切走地鸡,豉油皇蒸鱼,蒜香骨,西兰花炒带子,增城迟菜心,老火靓汤,炒时蔬,甜品', 2, '0', 'admin', sysdate()),
('dish', '平云山居4人超值套餐，欢迎预定~', '¥228', '4人 · 超值套餐', 'red', '', 'dine', 228.00, 449.00, '4人', '需提前1天预订；法定节假日不可用；每桌限用一份。', '白切鸡(半只),糖醋咕噜肉,蒜蓉炒时蔬,砂锅豆腐,例汤', 3, '0', 'admin', sysdate()),
('promo', '618周年庆典丨¥4298享15间高级客房房券套餐！低至4折', '¥4298', '15间 · 高级客房套票', 'red', '随机立减', 'room', 4298.00, 7470.00, '套票', '请提前1天预订，到店出示订单核销；法定节假日及特殊日期不可用；每单限用一份。', '高级客房房券 ×15 间夜,每间夜含双人早餐,免费停车 1 个车位/晚,延迟退房至 14:00', 4, '0', 'admin', sysdate()),
('promo', '618周年庆典丨¥5800享15间豪华客房（含双早），超值钜惠', '¥5800', '15间 · 豪华客房含双早', 'wine', '随机立减', 'room', 5800.00, 9870.00, '套票', '请提前1天预订，到店出示订单核销；法定节假日及特殊日期不可用；每单限用一份。', '豪华客房房券 ×15 间夜,每间夜含双早,免费停车 1 个车位/晚,延迟退房至 15:00', 5, '0', 'admin', sysdate());

-- ========================= 4. 储值套餐 lwf_recharge =========================
DROP TABLE IF EXISTS `lwf_recharge`;
CREATE TABLE `lwf_recharge` (
  `recharge_id` bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
  `title`       varchar(200)  DEFAULT '' COMMENT '标题',
  `amount`      decimal(10,2) NOT NULL COMMENT '充值金额',
  `give`        decimal(10,2) DEFAULT 0.00 COMMENT '赠送金额',
  `coupon`      decimal(10,2) DEFAULT 0.00 COMMENT '赠券价值',
  `hours`       int(11)       DEFAULT 0 COMMENT '赠送畅玩时长(小时)',
  `hot`         char(1)       DEFAULT '0' COMMENT '是否热门(0否 1是)',
  `gift_name`   varchar(200)  DEFAULT '' COMMENT '赠送礼包名称',
  `gift_price`  decimal(10,2) DEFAULT 0.00 COMMENT '礼包单价',
  `gift_qty`    int(11)       DEFAULT 0 COMMENT '礼包数量',
  `gift_valid`  varchar(100)  DEFAULT '' COMMENT '礼包有效期',
  `sort`        int(11)       DEFAULT 0 COMMENT '显示顺序',
  `status`      char(1)       DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`   varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime      DEFAULT NULL COMMENT '创建时间',
  `update_by`   varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime      DEFAULT NULL COMMENT '更新时间',
  `remark`      varchar(500)  DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`recharge_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='储值充值套餐';

INSERT INTO `lwf_recharge` (title, amount, give, coupon, hours, hot, gift_name, gift_price, gift_qty, gift_valid, sort, status, create_by, create_time) VALUES
('618周年庆丨充3000送600，赠棋牌室畅玩4小时', 3000.00, 600.00, 272.00, 4, '0', '', 0.00, 0, '', 1, '0', 'admin', sysdate()),
('618周年庆丨充5000送1000，赠棋牌室畅玩6小时', 5000.00, 1000.00, 408.00, 6, '1', '【34周年庆】棋牌室畅玩2小时券', 136.00, 3, '2026-05-20 ~ 2029-06-18', 2, '0', 'admin', sysdate()),
('618周年庆丨充8000送1600，赠棋牌室畅玩8小时', 8000.00, 1600.00, 544.00, 8, '0', '', 0.00, 0, '', 3, '0', 'admin', sysdate()),
('618周年庆丨充10000送2500，赠棋牌室畅玩10小时', 10000.00, 2500.00, 680.00, 10, '0', '', 0.00, 0, '', 4, '0', 'admin', sysdate());

-- ========================= 5. 积分商城 lwf_mall =========================
DROP TABLE IF EXISTS `lwf_mall`;
CREATE TABLE `lwf_mall` (
  `mall_id`     bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `title`       varchar(200)  NOT NULL COMMENT '标题',
  `cat`         int(11)       DEFAULT 0 COMMENT '分类(0房券 1餐饮其他 2月饼 3棋牌 4自助餐)',
  `scene`       varchar(16)   DEFAULT '0' COMMENT '配图场景',
  `points`      int(11)       NOT NULL COMMENT '所需积分',
  `sort`        int(11)       DEFAULT 0 COMMENT '显示顺序',
  `status`      char(1)       DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`   varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime      DEFAULT NULL COMMENT '创建时间',
  `update_by`   varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime      DEFAULT NULL COMMENT '更新时间',
  `remark`      varchar(500)  DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`mall_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='积分商城商品';

INSERT INTO `lwf_mall` (title, cat, scene, points, sort, status, create_by, create_time) VALUES
('积分兑换丨日历房代金券100元', 0, '2', 1888, 1, '0', 'admin', sysdate()),
('积分兑换丨平云山居豪华客房房券1张', 0, '1', 11960, 2, '0', 'admin', sysdate()),
('积分兑换丨平云山居豪华套房房券1张', 0, '3', 23960, 3, '0', 'admin', sysdate()),
('积分兑换丨平云山居双人餐代金券', 1, 'dine', 3680, 4, '0', 'admin', sysdate()),
('积分兑换丨双黄白莲蓉月饼礼盒', 2, 'mooncake', 2980, 5, '0', 'admin', sysdate()),
('积分兑换丨棋牌室畅玩4小时券', 3, 'night', 2360, 6, '0', 'admin', sysdate());

-- ========================= 6. 会员权益 lwf_benefit =========================
DROP TABLE IF EXISTS `lwf_benefit`;
CREATE TABLE `lwf_benefit` (
  `benefit_id`  bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '权益ID',
  `name`        varchar(50)   NOT NULL COMMENT '权益名称',
  `val`         varchar(50)   DEFAULT '' COMMENT '权益数值(如倍率)',
  `icon`        varchar(50)   DEFAULT 'gift' COMMENT '图标名',
  `sort`        int(11)       DEFAULT 0 COMMENT '显示顺序',
  `status`      char(1)       DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`   varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime      DEFAULT NULL COMMENT '创建时间',
  `update_by`   varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime      DEFAULT NULL COMMENT '更新时间',
  `remark`      varchar(500)  DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`benefit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='会员权益';

INSERT INTO `lwf_benefit` (name, val, icon, sort, status, create_by, create_time) VALUES
('多倍积分', '1.00', 'coin', 1, '0', 'admin', sysdate()),
('免费升级', '', 'up', 2, '0', 'admin', sysdate()),
('延时退房', '', 'clock', 3, '0', 'admin', sysdate()),
('忠诚计划', '', 'heart', 4, '0', 'admin', sysdate()),
('网络设施', '', 'wifi', 5, '0', 'admin', sysdate()),
('停车服务', '', 'car', 6, '0', 'admin', sysdate()),
('WIFI服务', '', 'wifi', 7, '0', 'admin', sysdate()),
('其他权益', '', 'gift', 8, '0', 'admin', sysdate()),
('升级礼遇与权益声明', '', 'doc', 9, '0', 'admin', sysdate());

-- ========================= 7. 会员 lwf_member =========================
DROP TABLE IF EXISTS `lwf_member`;
CREATE TABLE `lwf_member` (
  `member_id`   bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '会员ID',
  `phone`       varchar(20)   NOT NULL COMMENT '手机号(登录账号)',
  `password`    varchar(100)  DEFAULT '' COMMENT '密码(BCrypt)',
  `name`        varchar(50)   DEFAULT '' COMMENT '姓名/昵称',
  `avatar`      varchar(255)  DEFAULT '' COMMENT '头像URL',
  `level`       varchar(50)   DEFAULT '银卡会员' COMMENT '会员等级',
  `points`      int(11)       DEFAULT 0 COMMENT '可用积分',
  `balance`     decimal(10,2) DEFAULT 0.00 COMMENT '储值余额',
  `status`      char(1)       DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `openid`      varchar(64)   DEFAULT NULL COMMENT '微信openid',
  `address`     varchar(255)  DEFAULT '' COMMENT '联系/收货地址',
  `create_by`   varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime      DEFAULT NULL COMMENT '注册时间',
  `update_by`   varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime      DEFAULT NULL COMMENT '更新时间',
  `remark`      varchar(500)  DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`member_id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='会员';

-- 演示会员：彭程，手机号 13700009163，密码 123456
INSERT INTO `lwf_member` (member_id, phone, password, name, avatar, level, points, balance, status, create_by, create_time) VALUES
(1, '13700009163', '$2b$10$5PxfeJWg2zRVWlRLqlmCre/vi0OzledsiDlDgAg/49.jeHAk0u6lW', '彭程', '', '储值卡专享', 12860, 5320.00, '0', 'admin', sysdate());

-- ========================= 8. 订单 lwf_order =========================
DROP TABLE IF EXISTS `lwf_order`;
CREATE TABLE `lwf_order` (
  `order_id`    bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no`    varchar(32)   NOT NULL COMMENT '订单号',
  `member_id`   bigint(20)    DEFAULT NULL COMMENT '会员ID',
  `member_name` varchar(50)   DEFAULT '' COMMENT '会员姓名',
  `shop`        varchar(100)  DEFAULT '平云山居' COMMENT '门店',
  `kind`        varchar(16)   DEFAULT '' COMMENT '业务类型(room/dish/promo/recharge/coupon/mall)',
  `scene`       varchar(16)   DEFAULT '' COMMENT '配图场景',
  `status`      varchar(16)   DEFAULT 'pay' COMMENT '状态(pay/confirm/use/done/cancel/refund/expired)',
  `tag_text`    varchar(32)   DEFAULT '' COMMENT '角标文案',
  `title`       varchar(200)  NOT NULL COMMENT '商品标题',
  `unit_price`  decimal(10,2) DEFAULT 0.00 COMMENT '单价',
  `qty`         int(11)       DEFAULT 1 COMMENT '数量',
  `amount`      decimal(10,2) DEFAULT 0.00 COMMENT '商品金额',
  `pay_amount`  decimal(10,2) DEFAULT 0.00 COMMENT '实付金额',
  `coupon_mc_id` bigint(20)   DEFAULT NULL COMMENT '下单所用会员券ID(支付成功时核销)',
  `used_points` int(11)       DEFAULT 0 COMMENT '下单抵扣积分(支付成功时扣减)',
  `ref_id`      bigint(20)    DEFAULT NULL COMMENT '关联业务ID(储值=套餐ID,客房=房型ID)',
  `check_in`    date          DEFAULT NULL COMMENT '入住日期(客房订单)',
  `check_out`   date          DEFAULT NULL COMMENT '离店日期(客房订单)',
  `reviewed`    char(1)       DEFAULT '0' COMMENT '是否已评价(0否 1是)',
  `create_by`   varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime      DEFAULT NULL COMMENT '下单时间',
  `update_by`   varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime      DEFAULT NULL COMMENT '更新时间',
  `remark`      varchar(500)  DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='订单';

INSERT INTO `lwf_order` (order_no, member_id, member_name, shop, kind, scene, status, tag_text, title, unit_price, qty, amount, pay_amount, create_time) VALUES
('79884484073', 1, '彭程', '平云山居', 'coupon', 'coupon', 'done', '礼券', '关注送礼丨房型升级券', 0.00, 1, 0.00, 0.00, '2026-06-08 10:00:00'),
('79884484074', 1, '彭程', '平云山居', 'room', 'room', 'use', '', '高级双床房 1间夜 · 含早', 338.00, 1, 338.00, 338.00, '2026-06-09 11:00:00'),
('79884484075', 1, '彭程', '平云山居', 'dish', 'dine', 'pay', '', '平云山居6-8人超值套餐', 468.00, 1, 468.00, 468.00, '2026-06-09 12:00:00'),
('79884484076', 1, '彭程', '平云山居', 'mall', 'mooncake', 'refund', '', '【测试中秋佳礼】套餐一，购买无效', 10.00, 1, 10.00, 10.00, '2026-05-30 09:00:00'),
('79884484077', 1, '彭程', '平云山居', 'dish', 'dine', 'cancel', '', '平云山居4人超值套餐', 228.00, 1, 228.00, 228.00, '2026-05-28 18:00:00'),
('79884484078', 1, '彭程', '平云山居', 'room', 'room', 'confirm', '', '豪华江景大床房 1间夜 · 含双早', 588.00, 1, 588.00, 588.00, '2026-06-09 13:00:00'),
('79884484079', 1, '彭程', '平云山居', 'dish', 'dine', 'expired', '', '平云山居双人下午茶套餐', 158.00, 1, 158.00, 158.00, '2026-04-12 15:00:00');

-- ========================= 9. 优惠券模板 lwf_coupon =========================
DROP TABLE IF EXISTS `lwf_coupon`;
CREATE TABLE `lwf_coupon` (
  `coupon_id`   bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `title`       varchar(200)  NOT NULL COMMENT '券名称',
  `cat`         int(11)       DEFAULT 0 COMMENT '分类(0优惠券 1权益券 2礼遇券 3其他券)',
  `amount`      decimal(10,2) NOT NULL COMMENT '面额',
  `cond_amount` decimal(10,2) DEFAULT 0.00 COMMENT '使用门槛(满X可用)',
  `descr`       varchar(255)  DEFAULT '' COMMENT '说明',
  `valid_time`  datetime      DEFAULT NULL COMMENT '有效期至',
  `total_qty`   int(11)       DEFAULT -1 COMMENT '发放总量(-1不限)',
  `status`      char(1)       DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`   varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime      DEFAULT NULL COMMENT '创建时间',
  `update_by`   varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime      DEFAULT NULL COMMENT '更新时间',
  `remark`      varchar(500)  DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`coupon_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板';

INSERT INTO `lwf_coupon` (title, cat, amount, cond_amount, descr, valid_time, total_qty, status, create_by, create_time) VALUES
('庆中秋，共团圆丨平云山居通用券', 0, 100.00, 101.00, '每间夜可用一个 | 限部分房型', '2026-12-31 23:59:00', -1, '0', 'admin', sysdate()),
('平云山居餐饮满减券', 0, 50.00, 200.00, '堂食消费满200元可用', '2026-09-30 23:59:00', -1, '0', 'admin', sysdate()),
('储值专享 · 客房升级券', 1, 200.00, 800.00, '储值卡会员专享', '2026-12-31 23:59:00', -1, '0', 'admin', sysdate());

-- ========================= 10. 会员优惠券 lwf_member_coupon =========================
DROP TABLE IF EXISTS `lwf_member_coupon`;
CREATE TABLE `lwf_member_coupon` (
  `mc_id`       bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id`   bigint(20)    NOT NULL COMMENT '会员ID',
  `coupon_id`   bigint(20)    DEFAULT NULL COMMENT '来源券模板ID',
  `title`       varchar(200)  NOT NULL COMMENT '券名称',
  `cat`         int(11)       DEFAULT 0 COMMENT '分类(0优惠券 1权益券 2礼遇券 3其他券)',
  `amount`      decimal(10,2) NOT NULL COMMENT '面额',
  `cond_amount` decimal(10,2) DEFAULT 0.00 COMMENT '使用门槛',
  `descr`       varchar(255)  DEFAULT '' COMMENT '说明',
  `valid_time`  datetime      DEFAULT NULL COMMENT '有效期至',
  `status`      char(1)       DEFAULT '0' COMMENT '状态(0待使用 1已使用 2已失效)',
  `reason`      varchar(100)  DEFAULT '' COMMENT '失效原因',
  `used_time`   datetime      DEFAULT NULL COMMENT '使用时间',
  `create_by`   varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime      DEFAULT NULL COMMENT '领取时间',
  `update_by`   varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime      DEFAULT NULL COMMENT '更新时间',
  `remark`      varchar(500)  DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`mc_id`),
  KEY `idx_member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='会员持有优惠券';

INSERT INTO `lwf_member_coupon` (member_id, coupon_id, title, cat, amount, cond_amount, descr, valid_time, status, reason, used_time, create_time) VALUES
(1, 1, '庆中秋，共团圆丨平云山居通用券', 0, 100.00, 101.00, '每间夜可用一个 | 限部分房型', '2026-12-31 23:59:00', '0', '', NULL, sysdate()),
(1, 2, '平云山居餐饮满减券', 0, 50.00, 200.00, '堂食消费满200元可用', '2026-09-30 23:59:00', '0', '', NULL, sysdate()),
(1, 3, '储值专享 · 客房升级券', 1, 200.00, 800.00, '储值卡会员专享', '2026-12-31 23:59:00', '0', '', NULL, sysdate()),
(1, NULL, '开业庆典丨客房代金券', 0, 100.00, 101.00, '每间夜可用一个', '2026-05-12 23:59:00', '1', '', '2026-05-12 12:00:00', '2026-05-01 10:00:00'),
(1, NULL, '庆中秋，共团圆丨平云山居', 0, 100.00, 101.00, '每间夜可用一个 | 限部分房型', '2022-11-14 23:59:00', '2', '等级不符', NULL, '2022-10-01 10:00:00'),
(1, NULL, '庆中秋，共团圆丨100元券', 0, 100.00, 200.00, '每张预售券可用一个 | 限部分', '2022-09-15 23:59:00', '2', '等级不符', NULL, '2022-08-01 10:00:00');

-- ========================= 11. 积分明细 lwf_point_log =========================
DROP TABLE IF EXISTS `lwf_point_log`;
CREATE TABLE `lwf_point_log` (
  `log_id`      bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id`   bigint(20)    NOT NULL COMMENT '会员ID',
  `type`        varchar(8)    DEFAULT 'in' COMMENT '类型(in收入 out支出)',
  `title`       varchar(200)  NOT NULL COMMENT '说明',
  `points`      int(11)       NOT NULL COMMENT '积分变动值',
  `create_by`   varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime      DEFAULT NULL COMMENT '时间',
  `update_by`   varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime      DEFAULT NULL COMMENT '更新时间',
  `remark`      varchar(500)  DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`log_id`),
  KEY `idx_member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='积分明细';

INSERT INTO `lwf_point_log` (member_id, type, title, points, create_time) VALUES
(1, 'in', '客房消费 · 高级双床房', 338, '2026-06-08 14:22:00'),
(1, 'out', '积分兑换 · 日历房代金券100元', 1888, '2026-06-05 10:10:00'),
(1, 'in', '餐饮消费 · 平云山居6-8人套餐', 468, '2026-06-01 19:45:00'),
(1, 'in', '储值赠送积分', 5000, '2026-05-20 09:30:00'),
(1, 'out', '积分抵扣 · 停车费', 200, '2026-05-18 21:02:00'),
(1, 'in', '会员注册礼', 500, '2026-05-10 11:00:00');

-- ========================= 12. 订单评价 lwf_review =========================
DROP TABLE IF EXISTS `lwf_review`;
CREATE TABLE `lwf_review` (
  `review_id`   bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id`    bigint(20)    NOT NULL COMMENT '订单ID',
  `member_id`   bigint(20)    NOT NULL COMMENT '会员ID',
  `member_name` varchar(50)   DEFAULT '' COMMENT '会员姓名',
  `order_title` varchar(200)  DEFAULT '' COMMENT '订单标题',
  `rating`      int(11)       DEFAULT 5 COMMENT '评分(1-5)',
  `content`     varchar(500)  DEFAULT '' COMMENT '评价内容',
  `create_by`   varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime      DEFAULT NULL COMMENT '评价时间',
  `update_by`   varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime      DEFAULT NULL COMMENT '更新时间',
  `remark`      varchar(500)  DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`review_id`),
  KEY `idx_member` (`member_id`),
  KEY `idx_order` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='订单评价';

-- ========================= 字典 sys_dict_type / sys_dict_data =========================
DELETE FROM sys_dict_data WHERE dict_type IN ('lwf_product_type','lwf_order_status','lwf_point_type','lwf_coupon_status','lwf_coupon_cat','lwf_mall_cat');
DELETE FROM sys_dict_type WHERE dict_type IN ('lwf_product_type','lwf_order_status','lwf_point_type','lwf_coupon_status','lwf_coupon_cat','lwf_mall_cat');

INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) VALUES
(200, '平云山居-商品类型', 'lwf_product_type', '0', 'admin', sysdate(), '餐饮套餐/周年庆'),
(201, '平云山居-订单状态', 'lwf_order_status', '0', 'admin', sysdate(), '订单状态机'),
(202, '平云山居-积分类型', 'lwf_point_type', '0', 'admin', sysdate(), '积分收支'),
(203, '平云山居-券状态', 'lwf_coupon_status', '0', 'admin', sysdate(), '会员券状态'),
(204, '平云山居-券分类', 'lwf_coupon_cat', '0', 'admin', sysdate(), '优惠券分类'),
(205, '平云山居-积分商城分类', 'lwf_mall_cat', '0', 'admin', sysdate(), '积分商城分类');

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) VALUES
(2000, 1, '餐饮套餐', 'dish', 'lwf_product_type', '', 'primary', 'Y', '0', 'admin', sysdate(), ''),
(2001, 2, '周年庆', 'promo', 'lwf_product_type', '', 'danger', 'N', '0', 'admin', sysdate(), ''),
(2010, 1, '待支付', 'pay', 'lwf_order_status', '', 'warning', 'N', '0', 'admin', sysdate(), ''),
(2011, 2, '待确认', 'confirm', 'lwf_order_status', '', 'info', 'N', '0', 'admin', sysdate(), ''),
(2012, 3, '待使用', 'use', 'lwf_order_status', '', 'primary', 'N', '0', 'admin', sysdate(), ''),
(2013, 4, '已完成', 'done', 'lwf_order_status', '', 'success', 'N', '0', 'admin', sysdate(), ''),
(2014, 5, '已取消', 'cancel', 'lwf_order_status', '', 'info', 'N', '0', 'admin', sysdate(), ''),
(2015, 6, '已退款', 'refund', 'lwf_order_status', '', 'danger', 'N', '0', 'admin', sysdate(), ''),
(2016, 7, '已过期', 'expired', 'lwf_order_status', '', 'info', 'N', '0', 'admin', sysdate(), ''),
(2020, 1, '收入', 'in', 'lwf_point_type', '', 'success', 'N', '0', 'admin', sysdate(), ''),
(2021, 2, '支出', 'out', 'lwf_point_type', '', 'danger', 'N', '0', 'admin', sysdate(), ''),
(2030, 1, '待使用', '0', 'lwf_coupon_status', '', 'primary', 'N', '0', 'admin', sysdate(), ''),
(2031, 2, '已使用', '1', 'lwf_coupon_status', '', 'info', 'N', '0', 'admin', sysdate(), ''),
(2032, 3, '已失效', '2', 'lwf_coupon_status', '', 'danger', 'N', '0', 'admin', sysdate(), ''),
(2040, 1, '优惠券', '0', 'lwf_coupon_cat', '', 'primary', 'N', '0', 'admin', sysdate(), ''),
(2041, 2, '权益券', '1', 'lwf_coupon_cat', '', 'success', 'N', '0', 'admin', sysdate(), ''),
(2042, 3, '礼遇券', '2', 'lwf_coupon_cat', '', 'warning', 'N', '0', 'admin', sysdate(), ''),
(2043, 4, '其他券', '3', 'lwf_coupon_cat', '', 'info', 'N', '0', 'admin', sysdate(), ''),
(2050, 1, '房券', '0', 'lwf_mall_cat', '', 'primary', 'N', '0', 'admin', sysdate(), ''),
(2051, 2, '餐饮其他', '1', 'lwf_mall_cat', '', 'success', 'N', '0', 'admin', sysdate(), ''),
(2052, 3, '月饼', '2', 'lwf_mall_cat', '', 'warning', 'N', '0', 'admin', sysdate(), ''),
(2053, 4, '棋牌', '3', 'lwf_mall_cat', '', 'info', 'N', '0', 'admin', sysdate(), ''),
(2054, 5, '自助餐', '4', 'lwf_mall_cat', '', 'danger', 'N', '0', 'admin', sysdate(), '');

-- ========================= 后台菜单 sys_menu =========================
DELETE FROM sys_menu WHERE menu_id BETWEEN 3000 AND 3200;

-- 一级目录
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(3000, '平云山居运营', 0, 5, 'hotel', NULL, '', '', 1, 0, 'M', '0', '0', '', 'shopping', 'admin', sysdate(), '平云山居业务管理');

-- 二级菜单（C）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(3010, '轮播管理', 3000, 1, 'banner',   'hotel/banner/index',   '', '', 1, 0, 'C', '0', '0', 'hotel:banner:list',   'star',          'admin', sysdate(), ''),
(3020, '房型管理', 3000, 2, 'room',     'hotel/room/index',     '', '', 1, 0, 'C', '0', '0', 'hotel:room:list',     'guide',         'admin', sysdate(), ''),
(3030, '餐饮促销', 3000, 3, 'product',  'hotel/product/index',  '', '', 1, 0, 'C', '0', '0', 'hotel:product:list',  'shopping',      'admin', sysdate(), ''),
(3040, '储值套餐', 3000, 4, 'recharge', 'hotel/recharge/index', '', '', 1, 0, 'C', '0', '0', 'hotel:recharge:list', 'money',         'admin', sysdate(), ''),
(3050, '积分商城', 3000, 5, 'mall',     'hotel/mall/index',     '', '', 1, 0, 'C', '0', '0', 'hotel:mall:list',     'gift',          'admin', sysdate(), ''),
(3060, '会员权益', 3000, 6, 'benefit',  'hotel/benefit/index',  '', '', 1, 0, 'C', '0', '0', 'hotel:benefit:list',  'star',          'admin', sysdate(), ''),
(3070, '优惠券',   3000, 7, 'coupon',   'hotel/coupon/index',   '', '', 1, 0, 'C', '0', '0', 'hotel:coupon:list',   'form',          'admin', sysdate(), ''),
(3080, '会员管理', 3000, 8, 'member',   'hotel/member/index',   '', '', 1, 0, 'C', '0', '0', 'hotel:member:list',   'peoples',       'admin', sysdate(), ''),
(3090, '订单管理', 3000, 9, 'order',    'hotel/order/index',    '', '', 1, 0, 'C', '0', '0', 'hotel:order:list',    'list',          'admin', sysdate(), ''),
(3100, '积分明细', 3000, 10,'pointlog', 'hotel/pointlog/index', '', '', 1, 0, 'C', '0', '0', 'hotel:pointlog:list', 'documentation', 'admin', sysdate(), '');

-- 三级按钮（F）：每个菜单 list/query/add/edit/remove/export
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(3011,'轮播查询',3010,1,'','','','',1,0,'F','0','0','hotel:banner:query','#','admin',sysdate(),''),
(3012,'轮播新增',3010,2,'','','','',1,0,'F','0','0','hotel:banner:add','#','admin',sysdate(),''),
(3013,'轮播修改',3010,3,'','','','',1,0,'F','0','0','hotel:banner:edit','#','admin',sysdate(),''),
(3014,'轮播删除',3010,4,'','','','',1,0,'F','0','0','hotel:banner:remove','#','admin',sysdate(),''),
(3015,'轮播导出',3010,5,'','','','',1,0,'F','0','0','hotel:banner:export','#','admin',sysdate(),''),
(3021,'房型查询',3020,1,'','','','',1,0,'F','0','0','hotel:room:query','#','admin',sysdate(),''),
(3022,'房型新增',3020,2,'','','','',1,0,'F','0','0','hotel:room:add','#','admin',sysdate(),''),
(3023,'房型修改',3020,3,'','','','',1,0,'F','0','0','hotel:room:edit','#','admin',sysdate(),''),
(3024,'房型删除',3020,4,'','','','',1,0,'F','0','0','hotel:room:remove','#','admin',sysdate(),''),
(3025,'房型导出',3020,5,'','','','',1,0,'F','0','0','hotel:room:export','#','admin',sysdate(),''),
(3031,'商品查询',3030,1,'','','','',1,0,'F','0','0','hotel:product:query','#','admin',sysdate(),''),
(3032,'商品新增',3030,2,'','','','',1,0,'F','0','0','hotel:product:add','#','admin',sysdate(),''),
(3033,'商品修改',3030,3,'','','','',1,0,'F','0','0','hotel:product:edit','#','admin',sysdate(),''),
(3034,'商品删除',3030,4,'','','','',1,0,'F','0','0','hotel:product:remove','#','admin',sysdate(),''),
(3035,'商品导出',3030,5,'','','','',1,0,'F','0','0','hotel:product:export','#','admin',sysdate(),''),
(3041,'套餐查询',3040,1,'','','','',1,0,'F','0','0','hotel:recharge:query','#','admin',sysdate(),''),
(3042,'套餐新增',3040,2,'','','','',1,0,'F','0','0','hotel:recharge:add','#','admin',sysdate(),''),
(3043,'套餐修改',3040,3,'','','','',1,0,'F','0','0','hotel:recharge:edit','#','admin',sysdate(),''),
(3044,'套餐删除',3040,4,'','','','',1,0,'F','0','0','hotel:recharge:remove','#','admin',sysdate(),''),
(3045,'套餐导出',3040,5,'','','','',1,0,'F','0','0','hotel:recharge:export','#','admin',sysdate(),''),
(3051,'商城查询',3050,1,'','','','',1,0,'F','0','0','hotel:mall:query','#','admin',sysdate(),''),
(3052,'商城新增',3050,2,'','','','',1,0,'F','0','0','hotel:mall:add','#','admin',sysdate(),''),
(3053,'商城修改',3050,3,'','','','',1,0,'F','0','0','hotel:mall:edit','#','admin',sysdate(),''),
(3054,'商城删除',3050,4,'','','','',1,0,'F','0','0','hotel:mall:remove','#','admin',sysdate(),''),
(3055,'商城导出',3050,5,'','','','',1,0,'F','0','0','hotel:mall:export','#','admin',sysdate(),''),
(3061,'权益查询',3060,1,'','','','',1,0,'F','0','0','hotel:benefit:query','#','admin',sysdate(),''),
(3062,'权益新增',3060,2,'','','','',1,0,'F','0','0','hotel:benefit:add','#','admin',sysdate(),''),
(3063,'权益修改',3060,3,'','','','',1,0,'F','0','0','hotel:benefit:edit','#','admin',sysdate(),''),
(3064,'权益删除',3060,4,'','','','',1,0,'F','0','0','hotel:benefit:remove','#','admin',sysdate(),''),
(3065,'权益导出',3060,5,'','','','',1,0,'F','0','0','hotel:benefit:export','#','admin',sysdate(),''),
(3071,'券查询',3070,1,'','','','',1,0,'F','0','0','hotel:coupon:query','#','admin',sysdate(),''),
(3072,'券新增',3070,2,'','','','',1,0,'F','0','0','hotel:coupon:add','#','admin',sysdate(),''),
(3073,'券修改',3070,3,'','','','',1,0,'F','0','0','hotel:coupon:edit','#','admin',sysdate(),''),
(3074,'券删除',3070,4,'','','','',1,0,'F','0','0','hotel:coupon:remove','#','admin',sysdate(),''),
(3075,'券发放',3070,5,'','','','',1,0,'F','0','0','hotel:coupon:grant','#','admin',sysdate(),''),
(3081,'会员查询',3080,1,'','','','',1,0,'F','0','0','hotel:member:query','#','admin',sysdate(),''),
(3082,'会员新增',3080,2,'','','','',1,0,'F','0','0','hotel:member:add','#','admin',sysdate(),''),
(3083,'会员修改',3080,3,'','','','',1,0,'F','0','0','hotel:member:edit','#','admin',sysdate(),''),
(3084,'会员删除',3080,4,'','','','',1,0,'F','0','0','hotel:member:remove','#','admin',sysdate(),''),
(3085,'会员导出',3080,5,'','','','',1,0,'F','0','0','hotel:member:export','#','admin',sysdate(),''),
(3091,'订单查询',3090,1,'','','','',1,0,'F','0','0','hotel:order:query','#','admin',sysdate(),''),
(3092,'订单修改',3090,2,'','','','',1,0,'F','0','0','hotel:order:edit','#','admin',sysdate(),''),
(3093,'订单删除',3090,3,'','','','',1,0,'F','0','0','hotel:order:remove','#','admin',sysdate(),''),
(3094,'订单导出',3090,4,'','','','',1,0,'F','0','0','hotel:order:export','#','admin',sysdate(),''),
(3101,'积分查询',3100,1,'','','','',1,0,'F','0','0','hotel:pointlog:query','#','admin',sysdate(),''),
(3102,'积分导出',3100,2,'','','','',1,0,'F','0','0','hotel:pointlog:export','#','admin',sysdate(),'');

-- 订单评价 菜单 + 按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(3110, '订单评价', 3000, 11, 'review', 'hotel/review/index', '', '', 1, 0, 'C', '0', '0', 'hotel:review:list', 'message', 'admin', sysdate(), ''),
(3111, '评价查询', 3110, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'hotel:review:query',  '#', 'admin', sysdate(), ''),
(3112, '评价删除', 3110, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'hotel:review:remove', '#', 'admin', sysdate(), ''),
(3113, '评价导出', 3110, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'hotel:review:export', '#', 'admin', sysdate(), '');

SET FOREIGN_KEY_CHECKS = 1;
-- ----------------------------------------------------------------------------
-- 完成。后台超级管理员(admin)自动可见以上菜单；C端演示账号：13700009163 / 123456
-- ----------------------------------------------------------------------------
