-- ===============================
--  既存テーブル削除（依存関係ごと）
-- ===============================
DROP TABLE IF EXISTS cart CASCADE;
DROP TABLE IF EXISTS cart_list CASCADE;
DROP TABLE IF EXISTS "order" CASCADE;
DROP TABLE IF EXISTS order_list CASCADE;
DROP TABLE IF EXISTS item CASCADE;
DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS item_category CASCADE;
DROP TABLE IF EXISTS authentications CASCADE;
DROP TABLE IF EXISTS item_image CASCADE;
DROP TYPE IF EXISTS role CASCADE;

-- ===============================
--  テーブル作成
-- ===============================

-- -------------------------------
-- ユーザーテーブル
-- -------------------------------
CREATE TABLE "user" (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    address TEXT,
    phone_number VARCHAR(20),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- -------------------------------
-- 商品テーブル
-- -------------------------------
CREATE TABLE item (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    overview TEXT,
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
    category text NOT NULL
);

-- -------------------------------
-- 商品画像テーブル
-- -------------------------------
CREATE TABLE item_image (
    id SERIAL PRIMARY KEY,
    item_id BIGINT REFERENCES item(id) ON DELETE CASCADE,
    image_name text NOT NULL
);

-- -------------------------------
-- 購入履歴リスト
-- -------------------------------
CREATE TABLE order_list (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES "user"(id) ON DELETE CASCADE,
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
    user_id INTEGER NOT NULL,
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

-- 権限用のENUM型
CREATE TYPE role AS ENUM ('ADMIN', 'USER');

-- 認証情報を格納するテーブル
CREATE TABLE authentications (
	id SERIAL PRIMARY KEY,
	-- ユーザー名：主キー
	username VARCHAR(50) NOT NULL Unique,
	-- パスワード
	password VARCHAR(255) NOT NULL Unique,
	-- 権限
	authority role NOT NULL,
	-- 表示名
	displayname VARCHAR(50) NOT NULL
);
