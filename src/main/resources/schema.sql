-- ===============================
--  既存テーブル削除（依存関係ごと）
-- ===============================
DROP TABLE IF EXISTS cart CASCADE;
DROP TABLE IF EXISTS cart_list CASCADE;
DROP TABLE IF EXISTS order_item CASCADE;
DROP TABLE IF EXISTS "order" CASCADE;
DROP TABLE IF EXISTS order_list CASCADE;
DROP TABLE IF EXISTS item CASCADE;
DROP TABLE IF EXISTS item_category CASCADE;
DROP TABLE IF EXISTS delivery_address CASCADE; --テスト用
DROP TABLE IF EXISTS login_user CASCADE;
DROP TABLE IF EXISTS item_image CASCADE;

-- マッパーが参照する単数形を優先して削除
DROP TABLE IF EXISTS favorite CASCADE;

-- 既存の複数形を使っていた場合の掃除
DROP TABLE IF EXISTS favorites CASCADE;

-- 新着情報テーブルの削除 テスト用
DROP TABLE IF EXISTS news CASCADE;

DROP TYPE IF EXISTS role CASCADE;


-- ===============================
--  ENUM型定義（権限）
-- ===============================
CREATE TYPE role AS ENUM ('ADMIN', 'USER');

-- ===============================
--  ユーザーテーブル
-- ===============================
CREATE TABLE login_user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,   -- ログインID
    password VARCHAR(255) NOT NULL,         -- パスワードハッシュ
    authority role NOT NULL DEFAULT 'USER', -- 権限
    display_name VARCHAR(50) NOT NULL,      -- 表示名
    email VARCHAR(100) NOT NULL,            -- メールアドレス
    address VARCHAR(255),                   -- 住所
    phone_number VARCHAR(20),               -- 電話番号
    enabled BOOLEAN NOT NULL DEFAULT TRUE,	-- 退会フラグ
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
--	配送テーブル	テスト用
-- ===============================
CREATE TABLE delivery_address (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES login_user(id) ON DELETE CASCADE,
    recipient_name VARCHAR(100) NOT NULL,
    postal_code VARCHAR(10),
    address TEXT NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
--  商品テーブル
-- ===============================
CREATE TABLE item (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    overview TEXT,
    is_download BOOLEAN NOT NULL,
    thumbs_image_name TEXT,

	-- ★追加：ソフトデリート用フラグ（TRUEなら削除扱い）
	is_deleted   BOOLEAN NOT NULL DEFAULT FALSE,
    
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
--  商品カテゴリーテーブル
-- ===============================
CREATE TABLE item_category (
    id SERIAL PRIMARY KEY,
    item_id BIGINT REFERENCES item(id) ON DELETE CASCADE,
    category TEXT NOT NULL
);

-- ===============================
--  商品画像テーブル
-- ===============================
CREATE TABLE item_image (
    id SERIAL PRIMARY KEY,
    item_id BIGINT REFERENCES item(id) ON DELETE CASCADE,
    image_name TEXT NOT NULL
);

-- ===============================
--  購入履歴リスト（注文全体の単位）
-- ===============================
CREATE TABLE order_list (
	id SERIAL PRIMARY KEY,
	user_id INTEGER REFERENCES login_user(id) ON DELETE CASCADE,
	
	-- 購入画面でユーザーが入力した情報
	payment_method VARCHAR(50),  -- 支払い方法
	address        TEXT,         -- 配送先住所
	delivery_date  DATE,         -- 配達希望日
	delivery_time  VARCHAR(20),  -- 配達希望時間帯
	
	-- 注文ステータス
	status VARCHAR(20) NOT NULL DEFAULT 'NEW',
	
	-- 金額情報（確定値として保存）
	shipping_fee INTEGER NOT NULL DEFAULT 0,
	final_total  INTEGER NOT NULL DEFAULT 0,

	created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
--  購入履歴（個々の注文レコード）
-- ===============================
CREATE TABLE "order" (
    id SERIAL PRIMARY KEY,
    order_list_id INTEGER REFERENCES order_list(id) ON DELETE CASCADE,
    item_id INTEGER REFERENCES item(id) ON DELETE CASCADE,
    is_hold BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
--  購入履歴詳細（注文ごとの商品情報）
-- ===============================
CREATE TABLE order_item (
    id SERIAL PRIMARY KEY,
    order_id INTEGER REFERENCES "order"(id) ON DELETE CASCADE,
    item_id INTEGER REFERENCES item(id) ON DELETE CASCADE,
    price INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
--  カートリスト（ユーザーごとのカート）
-- ===============================
CREATE TABLE cart_list (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES login_user(id) ON DELETE CASCADE,
    is_login_user BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
--  カート（カート内の商品）
-- ===============================
CREATE TABLE cart (
    id SERIAL PRIMARY KEY,
    cartlist_id INTEGER REFERENCES cart_list(id) ON DELETE CASCADE,
    item_id INTEGER REFERENCES item(id) ON DELETE CASCADE,
    quantity INTEGER DEFAULT 1,
    is_hold BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


-- -------------------------------
-- お気に入りテーブル
-- -------------------------------
-- ★修正：Mapper が参照する単数形 "favorite" を作成
CREATE TABLE IF NOT EXISTS favorite (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES login_user(id) ON DELETE CASCADE,
    item_id INTEGER NOT NULL REFERENCES item(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    -- ★追加：重複登録防止（user_id, item_id の一意制約）
    CONSTRAINT uq_favorite_user_item UNIQUE (user_id, item_id)
);

-- ★追加：検索高速化
CREATE INDEX IF NOT EXISTS idx_favorite_user ON favorite(user_id);
CREATE INDEX IF NOT EXISTS idx_favorite_item ON favorite(item_id);

-- -------------------------------
-- 新着情報テーブル
-- -------------------------------
CREATE TABLE news (
	id SERIAL PRIMARY KEY,
	news_date DATE NOT NULL,
	content TEXT NOT NULL
);


-- cart: 同じカートに同じ商品を重複追加できないようにする（何度実行しても安全）
CREATE UNIQUE INDEX IF NOT EXISTS uq_cart_cartlist_item
  ON cart (cartlist_id, item_id);
  
-- ===============================
--		11/13追加（谷口）
--	パスワード再設定 PIN テーブル
-- ===============================
CREATE TABLE IF NOT EXISTS password_reset_pin (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(100) NOT NULL,
  pin CHAR(6) NOT NULL,
  expire_at TIMESTAMP NOT NULL,
  consumed BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
--		11/13追加（谷口）
--		インデックス
-- ===============================

CREATE INDEX IF NOT EXISTS idx_password_reset_pin_email ON password_reset_pin(email);
CREATE INDEX IF NOT EXISTS idx_password_reset_pin_expire ON password_reset_pin(expire_at);

