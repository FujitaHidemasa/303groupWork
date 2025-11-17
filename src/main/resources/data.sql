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
-- 商品テーブル（3件）
-- ===============================
-- INSERT INTO item (name, price, overview,is_download, thumbs_image_name)
-- VALUES 
-- ('ダミー', 0, 'ダミー用',false, 'dummy.jpg');

-- お問い合わせダミーデータ
INSERT INTO contact (
  user_id,
  name,
  email,
  subject,
  message,
  status
)
VALUES
-- 1件目：未対応
(NULL,
 '田中 太郎',
 'taro@example.com',
 '商品について質問があります',
 'こちらの商品はPS5版とPC版の両方で遊べますか？',
 'NEW'),

-- 2件目：対応中
(NULL,
 '佐藤 花子',
 'hanako@example.com',
 '配送日時の変更について',
 '注文済みの商品について、配送日時を変更したいのですが可能でしょうか？',
 'IN_PROGRESS'),

-- 3件目：対応済
(NULL,
 '鈴木 次郎',
 'jiro@example.com',
 '不具合報告',
 'ゲーム起動時にエラーが表示されてしまいます。対処方法を教えてください。',
 'RESOLVED');


-- ===============================
-- 新着情報テーブル（4件）
-- ===============================
INSERT INTO news (news_date, content) VALUES
('2025-10-17', 'ストア公開に向けた準備スタート'),
('2025-11-07', '新商品をラインナップに追加'),
('2025-11-17', 'VOIDR OFFICIAL STORE プレオープン'),
('2025-11-20', 'VOIDR OFFICIAL STORE ローンチ予定');

-- ===============================
-- カートリスト（3件）
-- ===============================
-- ★修正：idはDBに任せる
INSERT INTO cart_list (user_id, created_at)
VALUES
(1, CURRENT_TIMESTAMP),
(2, CURRENT_TIMESTAMP),
(3, CURRENT_TIMESTAMP);

-- ===============================
-- カート（3件）
-- ===============================
