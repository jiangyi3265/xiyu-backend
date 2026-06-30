-- ----------------------------------------------------------------------------
-- Scene dictionary and refund workflow settings.
-- Safe to run more than once.
-- ----------------------------------------------------------------------------
SET NAMES utf8mb4;

INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '平云山居场景', 'lwf_scene', '0', 'admin', sysdate(), '后台业务配图/轮播场景'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'lwf_scene');

DELETE FROM sys_dict_data WHERE dict_type = 'lwf_scene';
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) VALUES
(1, '客房蓝紫', '0', 'lwf_scene', '', 'primary', 'Y', '0', 'admin', sysdate(), '房型默认配图'),
(2, '暖棕客房', '1', 'lwf_scene', '', 'primary', 'N', '0', 'admin', sysdate(), '房型配图'),
(3, '金色礼遇', '2', 'lwf_scene', '', 'warning', 'N', '0', 'admin', sysdate(), '房券/礼遇配图'),
(4, '夜色高级', '3', 'lwf_scene', '', 'info', 'N', '0', 'admin', sysdate(), '房型配图'),
(5, '餐饮', 'dine', 'lwf_scene', '', 'success', 'N', '0', 'admin', sysdate(), '餐饮轮播/产品'),
(6, '客房', 'room', 'lwf_scene', '', 'primary', 'N', '0', 'admin', sysdate(), '客房轮播'),
(7, '储值夜景', 'night', 'lwf_scene', '', 'danger', 'N', '0', 'admin', sysdate(), '储值轮播/退款'),
(8, '月饼', 'mooncake', 'lwf_scene', '', 'warning', 'N', '0', 'admin', sysdate(), '积分商城月饼'),
(9, '优惠券', 'coupon', 'lwf_scene', '', 'danger', 'N', '0', 'admin', sysdate(), '优惠券配图');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 7, '退款审核中', 'refund_apply', 'lwf_order_status', '', 'warning', 'N', '0', 'admin', sysdate(), ''
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'lwf_order_status' AND dict_value = 'refund_apply');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 8, '退款驳回', 'refund_reject', 'lwf_order_status', '', 'info', 'N', '0', 'admin', sysdate(), ''
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'lwf_order_status' AND dict_value = 'refund_reject');

UPDATE sys_dict_data
SET dict_label = '已退款', list_class = 'danger'
WHERE dict_type = 'lwf_order_status' AND dict_value = 'refund';

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT '储值退款规则', 'lwf.recharge.refundPolicy', '储值退款规则：仅退剩余可退实付本金，赠送金额、赠券、赠送权益不可提现。', 'N', 'admin', sysdate(), '展示在退款申请和后台备注中'
WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'lwf.recharge.refundPolicy');
