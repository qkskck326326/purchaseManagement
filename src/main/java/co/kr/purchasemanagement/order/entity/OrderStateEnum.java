package co.kr.purchasemanagement.order.entity;

public enum OrderStateEnum {
    Order_Completed,
    Delivering,
    Delivery_Completed,
    Order_Cancellation,
    Refunding,
    Refund_Completed,
}

// SET GLOBAL event_scheduler = ON;

/*
10분마다 실행하고
product_order 테이블의
order_state 컬럼의 값이
Order_Completed 이고,
order_at 컬럼의 값으로 비교해서 1일이 지난 행의
order_state를 Delivering으로 바꾸기
* */

/*
CREATE EVENT update_order_state_event
ON SCHEDULE EVERY 10 MINUTE
DO
UPDATE product_order
SET order_state = 'Delivering'
WHERE order_state = 'Order_Completed'
  AND order_at < NOW() - INTERVAL 1 DAY;
*/

/*
10분마다 실행하고
product_order 테이블의
order_state 컬럼의 값이
Delivering 이고,
order_at 컬럼의 값으로 비교해서 2일이 지난 행의
order_state를 Delivery_completed으로 바꾸기
* */

/*
CREATE EVENT update_delivery_status_event
ON SCHEDULE EVERY 10 MINUTE
DO
UPDATE product_order
SET order_state = 'Delivery_Completed'
WHERE order_state = 'Delivering'
  AND order_at < NOW() - INTERVAL 2 DAY;
*/


