-- ===============================
-- ユーザーテーブル（3件）
-- ===============================
INSERT INTO "user" (name, password, email, address, phone_number, created_at, updated_at, is_active)
VALUES 
('田中 太郎', 'hashed_pw_001', 'taro@example.com', '東京都新宿区1-1-1', '09011112222', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
('佐藤 花子', 'hashed_pw_002', 'hanako@example.com', '大阪府大阪市2-2-2', '08033334444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
('鈴木 次郎', 'hashed_pw_003', 'jiro@example.com', '福岡県福岡市3-3-3', '07055556666', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);

-- ===============================
-- 商品テーブル（5件）
-- ===============================


-- ===============================
-- 購入履歴リスト（2件）
-- ===============================
INSERT INTO order_list (user_id, created_at, updated_at)
VALUES
(1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ===============================
-- 購入履歴（4件）
-- ===============================


-- ===============================
-- カートリスト（3件）
-- ===============================
INSERT INTO cart_list (user_id,is_login_user, created_at, updated_at)
VALUES
(1,true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2,true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3,true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ===============================
-- カート（6件）
-- ===============================
