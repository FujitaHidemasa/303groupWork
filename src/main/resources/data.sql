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
INSERT INTO item (name, price, overview, is_download, created_at, updated_at)
VALUES
('デジタルアートパック', 1800, '高画質の背景素材セット', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ゲーミングマウス', 4800, '高精度センサーとRGBライティング搭載', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ワイヤレスキーボード', 3500, '静音・スリムデザイン', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('音楽BGMセット', 2200, '商用利用可能なフリーBGM素材', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ポスター（A2サイズ）', 1200, 'VOIDR公式アートワークポスター', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

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
INSERT INTO "order" (orderlist_id, item_id, state, created_at)
VALUES
(1, 1, '配送完了', CURRENT_TIMESTAMP),
(1, 2, '配送中', CURRENT_TIMESTAMP),
(2, 3, '決済完了', CURRENT_TIMESTAMP),
(2, 4, 'ダウンロード済', CURRENT_TIMESTAMP);

-- ===============================
-- カートリスト（3件）
-- ===============================
INSERT INTO cart_list (user_id, created_at, updated_at)
VALUES
(1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ===============================
-- カート（6件）
-- ===============================
INSERT INTO cart (cartlist_id, item_id, item_count, is_hold, updated_at)
VALUES
(1, 2, 1, FALSE, CURRENT_TIMESTAMP),
(1, 3, 2, TRUE, CURRENT_TIMESTAMP),
(2, 1, 1, FALSE, CURRENT_TIMESTAMP),
(2, 5, 1, FALSE, CURRENT_TIMESTAMP),
(3, 4, 3, TRUE, CURRENT_TIMESTAMP),
(3, 2, 1, FALSE, CURRENT_TIMESTAMP);
