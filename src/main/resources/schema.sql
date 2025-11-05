-- ===============================
--  既存テーブル削除（依存関係ごと）
-- ===============================
DROP TABLE IF EXISTS cart CASCADE;
DROP TABLE IF EXISTS cart_list CASCADE;
DROP TABLE IF EXISTS "order" CASCADE;
DROP TABLE IF EXISTS order_list CASCADE;
DROP TABLE IF EXISTS item CASCADE;
DROP TABLE IF EXISTS item_category CASCADE;
DROP TABLE IF EXISTS login_user CASCADE;
DROP TABLE IF EXISTS item_image CASCADE;
DROP TYPE IF EXISTS role CASCADE;

-- ===============================
--  テーブル作成
-- ===============================

-- 権限用のENUM型
CREATE TYPE role AS ENUM ('ADMIN', 'USER');

CREATE TABLE login_user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,   -- ログインID
    password VARCHAR(255) NOT NULL,         -- パスワードハッシュ
    authority role NOT NULL DEFAULT 'USER', -- 権限
    display_name VARCHAR(50) NOT NULL,      -- 表示名
    email VARCHAR(100) NOT NULL,            -- メールアドレス
    address VARCHAR(255),                   -- 住所
    phone_number VARCHAR(20),               -- 電話番号
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------
-- 商品テーブル
-- -------------------------------
CREATE TABLE item (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    overview TEXT,
    is_download BOOLEAN NOT NULL,
    thumbs_image_name TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------
-- 商品カテゴリーテーブル
-- -------------------------------
CREATE TABLE item_category (
    id SERIAL PRIMARY KEY,
    item_id BIGINT REFERENCES item(id) ON DELETE CASCADE,
    category TEXT NOT NULL
);

-- -------------------------------
-- 商品画像テーブル
-- -------------------------------
CREATE TABLE item_image (
    id SERIAL PRIMARY KEY,
    item_id BIGINT REFERENCES item(id) ON DELETE CASCADE,
    image_name TEXT NOT NULL
);

-- -------------------------------
-- 購入履歴リスト
-- -------------------------------
CREATE TABLE order_list (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES login_user(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------
-- 購入履歴
-- -------------------------------
CREATE TABLE "order" (
    id SERIAL PRIMARY KEY,
    orderlist_id INTEGER REFERENCES order_list(id) ON DELETE CASCADE,
    item_id INTEGER REFERENCES item(id) ON DELETE CASCADE,
    state VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------
-- カートリスト
-- -------------------------------
CREATE TABLE cart_list (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES login_user(id) ON DELETE CASCADE,
    is_login_user BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------
-- カート
-- -------------------------------
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
CREATE TABLE IF NOT EXISTS favorites (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES login_user(id) ON DELETE CASCADE,
    item_id INTEGER REFERENCES item(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
--  追加制約（存在チェック付き）
-- ===============================

-- cart: 同じカートに同じ商品を重複追加できないようにする（何度実行しても安全）
CREATE UNIQUE INDEX IF NOT EXISTS uq_cart_cartlist_item
  ON cart (cartlist_id, item_id);

-- favorites: 同じ商品の重複お気に入りを防止（任意）
CREATE UNIQUE INDEX IF NOT EXISTS uq_favorites_user_item
  ON favorites (user_id, item_id);
