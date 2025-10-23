-- ===============================
-- ユーザーテーブル（3件）
-- ===============================
INSERT INTO login_user (
  username, password, authority, display_name,
  email, address, phone_number
)
VALUES
('taro', '$2a$10$8uLHLtY/rwBO/TWDf1tXcupVouLyY.ky5hRj/XCwsnLhwO8OLPB9O', 'USER', '田中 太郎', 'taro@example.com', '東京都新宿区1-1-1', '09011112222'),
('hanako', '$2a$10$Y0f8Ny9TKJ4gsJ9sZFtv0enYuS/Cx1trl8Hbef9kboeKoQ590UWWO', 'USER', '佐藤 花子', 'hanako@example.com', '大阪府大阪市2-2-2', '08033334444'),
('jiro', '$2a$10$gmNQ/1zwukD2OddxS2lAD.8iaCdhbu3GjxdNGew8wXFaAbtBCSpVm', 'ADMIN', '鈴木 次郎', 'jiro@example.com', '福岡県福岡市3-3-3', '07055556666');

-- ===============================
-- 商品テーブル（5件）
-- ===============================


-- ===============================
-- 購入履歴リスト（2件）
-- ===============================
--INSERT INTO order_list (user_id, created_at, updated_at)
--VALUES
--(1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--(2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ===============================
-- 購入履歴（4件）
-- ===============================


-- ===============================
-- カートリスト（3件）
-- ===============================

-- ===============================
-- カート（6件）
-- ===============================
